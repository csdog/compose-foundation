#!/usr/bin/env python3
"""Sequentially run unfinished Compose parity components through the TAO driver.

The batch runner is intentionally thin: it discovers unchecked components, skips
components with an existing DONE marker or a committed FAIL, initializes missing
runs, and invokes ``codex_stage_orchestrator.py`` for one component at a time.
All task memory remains in each component's progress.md and TAO contracts.
"""

from __future__ import annotations

import argparse
import fcntl
import json
import math
import re
import shlex
import subprocess
import sys
from dataclasses import asdict, dataclass
from datetime import datetime
from pathlib import Path
from typing import Sequence


UNCHECKED_RE = re.compile(r"^- \[ \] (.+?) \(`([^`]+)`\)")
BATCH_STATE = ".agent-runs/batch-state.json"
BATCH_LOG = ".agent-runs/batch.log"


@dataclass(frozen=True)
class Component:
    name: str
    slug: str


@dataclass
class BatchResult:
    name: str
    slug: str
    status: str
    started_at: str
    completed_at: str | None = None
    returncode: int | None = None
    reason: str | None = None


def parse_unchecked(progress_path: Path) -> list[Component]:
    components: list[Component] = []
    seen: set[str] = set()
    for line in progress_path.read_text(encoding="utf-8").splitlines():
        match = UNCHECKED_RE.match(line)
        if not match:
            continue
        name, slug = match.groups()
        if slug not in seen:
            seen.add(slug)
            components.append(Component(name=name.strip(), slug=slug.strip()))
    return components


def latest_tao_status(run_dir: Path) -> str | None:
    try:
        lines = (run_dir / "progress.log").read_text(encoding="utf-8").splitlines()
    except OSError:
        return None
    for line in reversed(lines):
        fields = line.split()
        if len(fields) >= 2 and fields[1] in {"ok", "FAIL", "rotate"}:
            return fields[1]
    return None


def skip_reason(run_dir: Path) -> str | None:
    # This queue contains only unchecked components. A legacy DONE marker is
    # merely a finished handoff and cannot override the canonical checklist.
    if latest_tao_status(run_dir) == "FAIL":
        return "blocked by committed FAIL"
    return None


def classify_process_result(returncode: int) -> tuple[str, str | None]:
    if returncode == 0:
        return "completed", None
    if returncode == 4:
        return "acceptance_pending", "Handoff finished; canonical component acceptance remains open"
    if returncode == 5:
        return "verification_pending", "Session cap reached with verified progress; resume the saved verification checkpoint"
    if returncode == 3:
        return (
            "infrastructure_blocked",
            "Codex provider unavailable; stop the batch and retry after it recovers",
        )
    return "blocked", "TAO driver stopped; inspect component handoff"


def initialize_component(project_root: Path, component: Component) -> int:
    run_dir = project_root / ".agent-runs" / component.slug
    if (run_dir / "progress.md").is_file():
        return 0
    goal = (
        f"Complete and verify {component.name} web parity against pinned official "
        "sources and the current checklist residuals, with focused tests, "
        "current-worktree browser evidence, explicit residuals, and handoff validation."
    )
    command = [
        sys.executable,
        "scripts/token_efficiency.py",
        "init",
        "--run-dir",
        str(run_dir),
        "--component",
        component.slug,
        "--goal",
        goal,
    ]
    print(f"init: {component.slug}")
    return subprocess.run(command, cwd=project_root).returncode


def orchestrator_command(
    component: Component, args: argparse.Namespace
) -> list[str]:
    command = [
        sys.executable,
        "scripts/codex_stage_orchestrator.py",
        "--component",
        component.slug,
        "--component-name",
        component.name,
        "--execute",
        "--approve-for-me",
        "--max-attempts",
        str(args.max_attempts),
        "--max-seconds",
        str(args.max_seconds),
        "--codex",
        args.codex,
    ]
    if args.max_actions is not None:
        command.extend(["--max-actions", str(args.max_actions)])
    if args.model:
        command.extend(["--model", args.model])
    if args.profile:
        command.extend(["--profile", args.profile])
    if args.complete_components:
        command.append("--require-acceptance-receipt")
    return command


def execute_component(component: Component, args: argparse.Namespace) -> tuple[int, str, str | None]:
    from codex_stage_orchestrator import (acceptance_problem, checklist_accepted,
                                         latest_progress_mark, pending_resume_stage,
                                         reopen_checkpoint, update_component_checkbox)
    root = args.project_root
    run = root / '.agent-runs' / component.slug
    passes = args.max_component_passes if args.complete_components else 1
    if (args.repair_blocked or args.complete_components) and latest_tao_status(run) == 'FAIL':
        mark = latest_progress_mark(run)
        stage = '20-implement' if mark and mark[0] in {'10-baseline', '20-implement', '30-verify', '40-handoff'} else (mark[0] if mark else '00-scope')
        reopen_checkpoint(run, stage, 'User authorized actual component/shared-dependency fixes and full acceptance. Read the recorded failure, diagnose and repair production or verifier causes, then reverify; do not repeat an unchanged failed verification.')
    for attempt in range(1, passes + 1):
        print(f'COMPONENT_PASS {component.slug} {attempt}/{passes}', flush=True)
        with (run / 'batch-driver.log').open('a', encoding='utf-8') as output:
            process = subprocess.run(orchestrator_command(component, args), cwd=root,
                                     stdout=output, stderr=subprocess.STDOUT)
        returncode = process.returncode
        status, reason = classify_process_result(returncode)
        if status == 'completed':
            problem = acceptance_problem(root, run) if args.complete_components else None
            if problem or not checklist_accepted(root, component.slug):
                if problem:
                    update_component_checkbox(root, component.slug, False)
                    path = run / 'acceptance.json'
                    try:
                        receipt = json.loads(path.read_text())
                        if not isinstance(receipt, dict): receipt = {}
                    except (OSError, ValueError):
                        receipt = {}
                    receipt.update(status='pending', resume_stage='30-verify', reason=problem)
                    path.write_text(json.dumps(receipt, ensure_ascii=False, indent=2) + '\n')
                returncode = 4
                status, reason = classify_process_result(returncode)
        if status in {'completed', 'infrastructure_blocked'} or not args.complete_components:
            return returncode, status, reason
        if attempt == passes:
            return returncode, status, reason
        mark = latest_progress_mark(run)
        if returncode == 5:
            # A valid progress checkpoint is already rotating; preserve its unit.
            continue
        if returncode == 4:
            stage = pending_resume_stage(run)
        elif returncode == 2 and mark and mark[1] == 'FAIL':
            stage = '20-implement' if mark[0] in {'10-baseline', '20-implement', '30-verify', '40-handoff'} else mark[0]
        else:
            return returncode, status, reason
        reopen_checkpoint(run, stage, 'Bounded completion loop: repair the recorded component/shared production or test-harness defect, preserve current passing evidence, and return through verification/audit to actual acceptance. Component scope explicitly permits the shared dependency repair necessary for this component. Do not modify scheduling scripts or lower acceptance rules.')
    raise AssertionError('component pass loop did not return')


def write_batch_state(project_root: Path, results: Sequence[BatchResult]) -> None:
    path = project_root / BATCH_STATE
    path.parent.mkdir(parents=True, exist_ok=True)
    payload = {
        "version": 1,
        "updated_at": datetime.now().astimezone().isoformat(timespec="seconds"),
        "results": [asdict(result) for result in results],
    }
    temporary = path.with_suffix(".json.tmp")
    temporary.write_text(
        json.dumps(payload, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    temporary.replace(path)


def append_batch_log(project_root: Path, result: BatchResult) -> None:
    path = project_root / BATCH_LOG
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("a", encoding="utf-8") as handle:
        handle.write(
            json.dumps(asdict(result), ensure_ascii=False) + "\n"
        )


def parse_args(argv: Sequence[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Run all unchecked Compose parity components sequentially."
    )
    parser.add_argument(
        "--project-root",
        type=Path,
        default=Path(__file__).resolve().parents[1],
    )
    parser.add_argument(
        "--limit",
        type=int,
        default=0,
        help="maximum components to consider; 0 means all",
    )
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--background", action="store_true", help="detach execution and return a log receipt")
    mode.add_argument("--watch", action="store_true", help="display progress without model calls; Ctrl-C stops display only")
    parser.add_argument("--interval", type=float, default=2, help="progress refresh seconds")
    mode.add_argument("--status", action="store_true", help="read compact persisted status")
    mode.add_argument("--repair-usage", action="store_true", help="recover historical session usage without model calls")
    parser.add_argument("--start-after")
    parser.add_argument("--reverse", action="store_true", help="run unchecked components in reverse checklist order")
    parser.add_argument("--repair-blocked", action="store_true", help="resume recorded component failures in an implementation/fixer session")
    parser.add_argument("--complete-components", action="store_true", help="repair and reverify each component before advancing; require acceptance receipts")
    parser.add_argument("--max-component-passes", type=int, default=6, help="bounded repair/verification passes for one component")
    parser.add_argument(
        "--exclude",
        default="",
        help="comma-separated component slugs to skip",
    )
    parser.add_argument("--max-attempts", type=int, default=6)
    parser.add_argument("--max-seconds", type=int, default=600)
    parser.add_argument("--max-actions", type=int)
    parser.add_argument("--codex", default="codex")
    parser.add_argument("--model")
    parser.add_argument("--profile")
    parser.add_argument(
        "--execute",
        action="store_true",
        help="start Codex sessions; without it only print the queue",
    )
    args = parser.parse_args(argv)
    args.project_root = args.project_root.resolve()
    args.exclude = {
        slug.strip() for slug in args.exclude.split(",") if slug.strip()
    }
    if args.interval < 0.1 or not math.isfinite(args.interval):
        parser.error("--interval must be finite and at least 0.1 seconds")
    if args.limit < 0:
        parser.error("--limit cannot be negative")
    if args.max_attempts < 1:
        parser.error("--max-attempts must be positive")
    if args.max_component_passes < 1:
        parser.error("--max-component-passes must be positive")
    if args.max_seconds < 1:
        parser.error("--max-seconds must be positive")
    if args.max_actions is not None and args.max_actions < 1:
        parser.error("--max-actions must be positive")
    if args.background and not args.execute:
        parser.error("--background requires --execute")
    return args


def start_background(args: argparse.Namespace, argv: Sequence[str]) -> int:
    directory = args.project_root / ".agent-runs" / "batches"
    directory.mkdir(parents=True, exist_ok=True)
    stamp = datetime.now().strftime("%Y%m%dT%H%M%S%f")
    log = directory / f"{stamp}.log"
    command = [sys.executable, "-u", str(Path(__file__).resolve()),
               *[value for value in argv if value != "--background"]]
    with log.open("a", encoding="utf-8") as output:
        process = subprocess.Popen(command, cwd=args.project_root, stdin=subprocess.DEVNULL,
                                   stdout=output, stderr=subprocess.STDOUT,
                                   start_new_session=True)
    try:
        exitcode = process.wait(timeout=0.5)
    except subprocess.TimeoutExpired:
        exitcode = None
    mirror_pid = None
    if exitcode is None:
        with log.open("a", encoding="utf-8") as output:
            monitor = subprocess.Popen(
                [sys.executable, "-u", str(Path(__file__).with_name("batch_progress.py")),
                 "--project-root", str(args.project_root), "--log", str(log)],
                cwd=args.project_root, stdin=subprocess.DEVNULL, stdout=output,
                stderr=subprocess.STDOUT, start_new_session=True,
            )
        mirror_pid = monitor.pid
    receipt = {"pid": process.pid, "log": str(log), "progress_pid": mirror_pid,
               "launch_status": "process_started" if exitcode is None else "exited",
               "returncode": exitcode,
               "note": "process_started does not mean the model runtime is ready"}
    (directory / "latest.json").write_text(json.dumps(receipt) + "\n")
    print(json.dumps(receipt))
    return exitcode or 0


def run_main(argv: Sequence[str] | None = None) -> int:
    args = parse_args(argv)
    root = args.project_root
    if args.status or args.watch:
        from batch_progress import snapshot, watch
        if args.watch:
            return watch(root, args.interval)
        print(json.dumps(snapshot(root), ensure_ascii=False))
        return 0
    if args.repair_usage:
        from codex_stage_orchestrator import repair_run_usage
        print(json.dumps(repair_run_usage(root)))
        return 0
    if args.background:
        return start_background(args, list(sys.argv[1:] if argv is None else argv))
    progress = root / "docs/shadcn-web-progress.md"
    components = parse_unchecked(progress)
    if args.reverse:
        components.reverse()
    if args.start_after:
        slugs = [component.slug for component in components]
        if args.start_after not in slugs:
            print(
                f"ERROR: --start-after slug not in unchecked list: "
                f"{args.start_after}",
                file=sys.stderr,
            )
            return 1
        components = components[slugs.index(args.start_after) + 1 :]

    queued: list[Component] = []
    skipped: list[tuple[Component, str]] = []
    for component in components:
        run_dir = root / ".agent-runs" / component.slug
        reason = skip_reason(run_dir)
        if args.repair_blocked or args.complete_components:
            reason = None
        if component.slug in args.exclude:
            reason = "excluded"
        if reason:
            skipped.append((component, reason))
        else:
            queued.append(component)
        if args.limit and len(queued) >= args.limit:
            break

    print(f"Unchecked: {len(components)}; queued: {len(queued)}")
    for component, reason in skipped:
        print(f"SKIP {component.slug}: {reason}")
    for index, component in enumerate(queued, 1):
        print(
            f"{index:02d}. {component.name} ({component.slug}): "
            f"{shlex.join(orchestrator_command(component, args))}"
        )
    if not args.execute:
        return 0

    results: list[BatchResult] = [
        BatchResult(
            name=component.name,
            slug=component.slug,
            status="skipped",
            started_at=datetime.now().astimezone().isoformat(timespec="seconds"),
            completed_at=datetime.now().astimezone().isoformat(timespec="seconds"),
            reason=reason,
        )
        for component, reason in skipped
    ]
    infrastructure_blocked = False
    for component in queued:
        if infrastructure_blocked:
            result = BatchResult(
                name=component.name,
                slug=component.slug,
                status="not_started",
                started_at=datetime.now().astimezone().isoformat(
                    timespec="seconds"
                ),
                completed_at=datetime.now().astimezone().isoformat(
                    timespec="seconds"
                ),
                reason="batch stopped after provider became unavailable",
            )
            results.append(result)
            append_batch_log(root, result)
            write_batch_state(root, results)
            continue
        started = datetime.now().astimezone().isoformat(timespec="seconds")
        result = BatchResult(
            name=component.name,
            slug=component.slug,
            status="running",
            started_at=started,
        )
        results.append(result)
        write_batch_state(root, results)
        print(f"STARTED: {component.slug}", flush=True)
        init_status = initialize_component(root, component)
        if init_status != 0:
            result.status = "init_failed"
            result.returncode = init_status
            result.reason = "token_efficiency init failed"
        else:
            result.returncode, result.status, result.reason = execute_component(component, args)
            infrastructure_blocked = result.status == "infrastructure_blocked"
        result.completed_at = datetime.now().astimezone().isoformat(
            timespec="seconds"
        )
        append_batch_log(root, result)
        write_batch_state(root, results)
        print(f"{result.status.upper()}: {component.slug}")
        if args.complete_components and result.status != 'completed':
            print('Completion mode stopped at unresolved component; checkpoint preserved, no scan-and-skip.', flush=True)
            break

    completed = sum(result.status == "completed" for result in results)
    blocked = sum(result.status == "blocked" for result in results)
    pending = sum(result.status in {"acceptance_pending", "verification_pending"} for result in results)
    infrastructure = sum(
        result.status == "infrastructure_blocked" for result in results
    )
    print(
        f"Batch complete: {completed} accepted, {pending} acceptance-pending, {blocked} blocked, "
        f"{infrastructure} infrastructure-blocked, "
        f"{len(results) - completed - pending - blocked - infrastructure} skipped/other"
    )
    if infrastructure:
        return 3
    return 2 if blocked else (4 if pending else 0)


def main(argv: Sequence[str] | None = None) -> int:
    args = parse_args(argv)
    if (args.execute and not (args.background or args.status or args.watch)) or args.repair_usage:
        directory = args.project_root / ".agent-runs"
        directory.mkdir(parents=True, exist_ok=True)
        with (directory / "batch.lock").open("a") as lock:
            try:
                fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
            except BlockingIOError:
                print("ERROR: another batch or usage repair is active", file=sys.stderr)
                return 1
            return run_main(argv)
    return run_main(argv)


if __name__ == "__main__":
    raise SystemExit(main())
