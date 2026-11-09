#!/usr/bin/env python3
"""Measure DSH token usage and manage token-efficient Compose work runs."""

from __future__ import annotations

import argparse
import json
import math
import shutil
import subprocess
import sys
from dataclasses import dataclass, field
from datetime import datetime, timedelta, timezone
from pathlib import Path
from typing import Iterable, Iterator, Sequence


SHANGHAI = timezone(timedelta(hours=8))
REQUIRED_PROGRESS_SECTIONS = (
    "## Goal",
    "## Done",
    "## Current state",
    "## Decisions",
    "## Artifacts",
    "## Verification",
    "## Next",
    "## Open questions",
)


@dataclass
class UsageCall:
    time_ms: int
    input_tokens: int
    cache_read_tokens: int
    cache_write_tokens: int
    output_tokens: int

    @property
    def prompt_tokens(self) -> int:
        return self.input_tokens + self.cache_read_tokens + self.cache_write_tokens

    @property
    def total_tokens(self) -> int:
        return self.prompt_tokens + self.output_tokens


@dataclass
class SessionMetrics:
    session_id: str
    title: str
    created_ms: int
    inherited_events: int
    calls: int = 0
    input_tokens: int = 0
    cache_read_tokens: int = 0
    cache_write_tokens: int = 0
    output_tokens: int = 0
    prune_events: int = 0
    usage_calls: list[UsageCall] = field(default_factory=list)
    prompt_sizes: list[int] = field(default_factory=list)
    context_surface_tokens: int = 0
    context_window: int = 0
    tool_context_tokens: int = 0

    @property
    def raw_tokens(self) -> int:
        return (
            self.input_tokens
            + self.cache_read_tokens
            + self.cache_write_tokens
            + self.output_tokens
        )

    @property
    def efficient_tokens(self) -> int:
        return (
            self.input_tokens + self.cache_write_tokens + self.output_tokens
        )

    @property
    def median_prompt(self) -> int:
        return percentile(self.prompt_sizes, 0.50)

    @property
    def p95_prompt(self) -> int:
        return percentile(self.prompt_sizes, 0.95)

    @property
    def utilization(self) -> float:
        if not self.context_window:
            return 0.0
        return self.context_surface_tokens / self.context_window

    @property
    def tool_share(self) -> float:
        if not self.context_surface_tokens:
            return 0.0
        return self.tool_context_tokens / self.context_surface_tokens


def percentile(values: Sequence[int], point: float) -> int:
    if not values:
        return 0
    ordered = sorted(values)
    index = max(0, min(len(ordered) - 1, math.ceil(point * len(ordered)) - 1))
    return ordered[index]


def local_date(time_ms: int) -> str:
    return datetime.fromtimestamp(time_ms / 1000, SHANGHAI).date().isoformat()


def local_time(time_ms: int) -> str:
    return datetime.fromtimestamp(time_ms / 1000, SHANGHAI).isoformat(timespec="seconds")


def iter_json_lines(path: Path) -> Iterator[dict]:
    if path.suffix == ".zstd":
        zstdcat = shutil.which("zstdcat")
        if zstdcat:
            process = subprocess.Popen(
                [zstdcat, str(path)],
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )
        else:
            zstd = shutil.which("zstd")
            if not zstd:
                raise RuntimeError("zstdcat or zstd is required to read DSH sessions")
            process = subprocess.Popen(
                [zstd, "-dc", str(path)],
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )
        assert process.stdout is not None
        for line in process.stdout:
            if line.strip():
                yield json.loads(line)
        stderr = process.stderr.read() if process.stderr is not None else ""
        status = process.wait()
        if status:
            raise RuntimeError(f"zstd failed for {path}: {stderr.strip()}")
        return

    with path.open(encoding="utf-8") as handle:
        for line in handle:
            if line.strip():
                yield json.loads(line)


def usage_from_events(events: Iterable[dict], inherited_events: int) -> tuple[list[UsageCall], int]:
    calls: list[UsageCall] = []
    prune_events = 0
    for event in events:
        event_type = event.get("type", "")
        seq = int(event.get("seq") or 0)
        if event_type.startswith("compaction/"):
            if seq > inherited_events:
                prune_events += 1
            continue
        if event_type != "assistant/message" or seq <= inherited_events:
            continue
        usage = (event.get("data") or {}).get("usage") or {}
        calls.append(
            UsageCall(
                time_ms=int(event.get("time") or 0),
                input_tokens=int(usage.get("inputTokens") or 0),
                cache_read_tokens=int(usage.get("cacheReadTokens") or 0),
                cache_write_tokens=int(usage.get("cacheWriteTokens") or 0),
                output_tokens=int(usage.get("outputTokens") or 0),
            )
        )
    return calls, prune_events


def find_session_log(dsh_home: Path, session_id: str) -> Path | None:
    candidates = list(
        dsh_home.glob(f"sessions/*/{session_id}/session.v4.jsonl.zstd")
    )
    return candidates[0] if candidates else None


def load_project_sessions(dsh_home: Path, project_cwd: Path) -> list[SessionMetrics]:
    project_cwd = project_cwd.resolve()
    projection_dir = dsh_home / "storages/session_projcache/sessions"
    sessions: list[SessionMetrics] = []

    for projection_path in sorted(projection_dir.glob("*.json")):
        with projection_path.open(encoding="utf-8") as handle:
            record = json.load(handle).get("record") or {}
        identity = record.get("identity") or {}
        cwd = identity.get("cwd")
        if not cwd or Path(cwd).resolve() != project_cwd:
            continue

        rows = record.get("rows") or {}
        session_id = projection_path.stem
        inherited_events = int(identity.get("inheritedEventCount") or 0)
        metrics = SessionMetrics(
            session_id=session_id,
            title=((rows.get("title") or {}).get("val") or ""),
            created_ms=int(identity.get("createdAt") or 0),
            inherited_events=inherited_events,
        )

        context_pressure = ((rows.get("contextPressure") or {}).get("val") or {})
        metrics.context_surface_tokens = int(
            context_pressure.get("surfaceTokens") or 0
        )
        metrics.context_window = int(context_pressure.get("contextWindow") or 0)
        surface = (
            ((rows.get("contextTimeline") or {}).get("val") or {}).get("surface")
            or []
        )
        metrics.tool_context_tokens = sum(
            int(item.get("tokens") or 0)
            for item in surface
            if item.get("cat") == "tool"
        )

        session_log = find_session_log(dsh_home, session_id)
        if session_log:
            calls, prune_events = usage_from_events(
                iter_json_lines(session_log), inherited_events
            )
            metrics.calls = len(calls)
            metrics.prune_events = prune_events
            metrics.usage_calls = calls
            metrics.prompt_sizes = [call.prompt_tokens for call in calls]
            metrics.input_tokens = sum(call.input_tokens for call in calls)
            metrics.cache_read_tokens = sum(
                call.cache_read_tokens for call in calls
            )
            metrics.cache_write_tokens = sum(
                call.cache_write_tokens for call in calls
            )
            metrics.output_tokens = sum(call.output_tokens for call in calls)
        sessions.append(metrics)

    return sessions


def aggregate_dates(sessions: Sequence[SessionMetrics]) -> list[dict]:
    daily: dict[str, dict[str, int | str]] = {}
    for session in sessions:
        for call in session.usage_calls:
            date = local_date(call.time_ms)
            row = daily.setdefault(
                date,
                {
                    "date": date,
                    "calls": 0,
                    "input": 0,
                    "cache_read": 0,
                    "cache_write": 0,
                    "output": 0,
                    "raw": 0,
                },
            )
            row["calls"] = int(row["calls"]) + 1
            row["input"] = int(row["input"]) + call.input_tokens
            row["cache_read"] = int(row["cache_read"]) + call.cache_read_tokens
            row["cache_write"] = int(row["cache_write"]) + call.cache_write_tokens
            row["output"] = int(row["output"]) + call.output_tokens
            row["raw"] = int(row["raw"]) + call.total_tokens
    return [daily[key] for key in sorted(daily)]


def cap_estimate(
    sessions: Sequence[SessionMetrics], cap: int
) -> dict[str, int | float]:
    raw = 0
    capped = 0
    calls = 0
    for session in sessions:
        for call in session.usage_calls:
            calls += 1
            raw += call.total_tokens
            capped += min(call.prompt_tokens, cap) + call.output_tokens
    reduction = 0.0 if not raw else (raw - capped) / raw * 100
    return {
        "cap": cap,
        "calls": calls,
        "raw": raw,
        "projected_raw": capped,
        "reduction_pct": round(reduction, 3),
    }


def session_payload(session: SessionMetrics) -> dict:
    return {
        "session_id": session.session_id,
        "title": session.title,
        "created": local_time(session.created_ms),
        "inherited_events": session.inherited_events,
        "calls": session.calls,
        "input_tokens": session.input_tokens,
        "cache_read_tokens": session.cache_read_tokens,
        "cache_write_tokens": session.cache_write_tokens,
        "output_tokens": session.output_tokens,
        "raw_tokens": session.raw_tokens,
        "efficient_tokens": session.efficient_tokens,
        "median_prompt": session.median_prompt,
        "p95_prompt": session.p95_prompt,
        "context_surface_tokens": session.context_surface_tokens,
        "context_window": session.context_window,
        "utilization_pct": round(session.utilization * 100, 3),
        "tool_share_pct": round(session.tool_share * 100, 3),
        "prune_events": session.prune_events,
    }


def report(
    dsh_home: Path,
    project_cwd: Path,
    caps: Sequence[int],
    as_json: bool,
) -> int:
    sessions = load_project_sessions(dsh_home, project_cwd)
    active = [session for session in sessions if session.calls]
    raw = sum(session.raw_tokens for session in active)
    input_tokens = sum(session.input_tokens for session in active)
    cache_read = sum(session.cache_read_tokens for session in active)
    cache_write = sum(session.cache_write_tokens for session in active)
    output = sum(session.output_tokens for session in active)
    calls = sum(session.calls for session in active)
    prompt_sizes = [
        prompt for session in active for prompt in session.prompt_sizes
    ]
    surface = sum(session.context_surface_tokens for session in active)
    tool_surface = sum(session.tool_context_tokens for session in active)

    payload = {
        "project": str(project_cwd.resolve()),
        "sessions_found": len(sessions),
        "sessions_with_usage": len(active),
        "calls": calls,
        "input_tokens": input_tokens,
        "cache_read_tokens": cache_read,
        "cache_write_tokens": cache_write,
        "output_tokens": output,
        "raw_tokens": raw,
        "efficient_tokens": input_tokens + cache_write + output,
        "cache_hit_pct": round(
            cache_read / (input_tokens + cache_read + cache_write) * 100, 3
        )
        if input_tokens + cache_read + cache_write
        else 0.0,
        "prompt_median": percentile(prompt_sizes, 0.50),
        "prompt_p95": percentile(prompt_sizes, 0.95),
        "tool_context_share_pct": round(tool_surface / surface * 100, 3)
        if surface
        else 0.0,
        "daily": aggregate_dates(active),
        "sessions": sorted(
            (session_payload(session) for session in active),
            key=lambda row: row["raw_tokens"],
            reverse=True,
        ),
        "caps": [cap_estimate(active, cap) for cap in caps],
    }

    if as_json:
        print(json.dumps(payload, indent=2, sort_keys=True))
        return 0

    print(f"Project: {payload['project']}")
    print(
        f"Sessions: {len(active)} with usage / {len(sessions)} records; "
        f"calls={calls:,}"
    )
    print(
        f"Raw: {raw:,}; efficient (uncached input + output): "
        f"{input_tokens + cache_write + output:,}"
    )
    print(
        f"Input: {input_tokens:,}; cache-read: {cache_read:,}; "
        f"output: {output:,}; cache-hit: {payload['cache_hit_pct']:.3f}%"
    )
    print(
        f"Prompt median: {payload['prompt_median']:,}; "
        f"p95: {payload['prompt_p95']:,}; "
        f"tool context share: {payload['tool_context_share_pct']:.3f}%"
    )
    print("\nDate totals")
    for row in payload["daily"]:
        print(
            f"{row['date']} calls={row['calls']:,} raw={row['raw']:,} "
            f"uncached={row['input']:,} cache={row['cache_read']:,} "
            f"output={row['output']:,}"
        )
    print("\nSessions")
    for row in payload["sessions"]:
        print(
            f"{row['session_id']} calls={row['calls']:,} raw={row['raw_tokens']:,} "
            f"efficient={row['efficient_tokens']:,} "
            f"median_prompt={row['median_prompt']:,} "
            f"tool_share={row['tool_share_pct']:.1f}% "
            f"prunes={row['prune_events']} {row['title']}"
        )
    print("\nContext-cap estimate (illustrative, excludes handoff/compaction cost)")
    for row in payload["caps"]:
        print(
            f"cap={row['cap']:,} projected_raw={row['projected_raw']:,} "
            f"reduction={row['reduction_pct']:.1f}%"
        )
    return 0


def progress_template(component: str, goal: str) -> str:
    return f"""# Agent handoff: {component}

## Goal

{goal}

Acceptance: the component's focused tests pass, its documented residuals are explicit, and
the parity progress record contains the evidence paths.

## Done

- None yet.

## Current state

- Stage: scope
- Component: `{component}`
- Status: active

## Decisions

- Keep one component per execution session.

## Artifacts

- Run directory: `.`
- Command log: `commands.log`

## Verification

- No commands run yet.

## Next

1. Run `python3 scripts/token_efficiency.py validate --run-dir "$PWD"`.
2. Select the exact source, docs page, and focused test command.

## Open questions

- None.
"""


def init_run(run_dir: Path, component: str, goal: str, force: bool) -> int:
    if run_dir.exists() and any(run_dir.iterdir()) and not force:
        raise SystemExit(f"{run_dir} is not empty; use --force to reinitialize")
    run_dir.mkdir(parents=True, exist_ok=True)
    state = {
        "version": 1,
        "component": component,
        "goal": goal,
        "stage": "scope",
        "status": "active",
        "created_at": datetime.now(SHANGHAI).isoformat(timespec="seconds"),
    }
    (run_dir / "state.json").write_text(
        json.dumps(state, indent=2) + "\n", encoding="utf-8"
    )
    (run_dir / "progress.md").write_text(
        progress_template(component, goal), encoding="utf-8"
    )
    (run_dir / "commands.log").touch()
    print(run_dir.resolve())
    return 0


def validate_progress(run_dir: Path, max_lines: int) -> list[str]:
    errors: list[str] = []
    state_path = run_dir / "state.json"
    progress_path = run_dir / "progress.md"
    if not state_path.is_file():
        errors.append(f"missing {state_path}")
    else:
        try:
            state = json.loads(state_path.read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc:
            errors.append(f"invalid state.json: {exc}")
        else:
            if not state.get("component"):
                errors.append("state.json component is empty")
            if not state.get("goal"):
                errors.append("state.json goal is empty")
    if not progress_path.is_file():
        errors.append(f"missing {progress_path}")
    else:
        text = progress_path.read_text(encoding="utf-8")
        for section in REQUIRED_PROGRESS_SECTIONS:
            if section not in text:
                errors.append(f"missing progress section: {section}")
        count = len(text.splitlines())
        if count > max_lines:
            errors.append(f"progress.md is {count} lines; maximum is {max_lines}")
    return errors


def validate_run(run_dir: Path, max_lines: int) -> int:
    errors = validate_progress(run_dir, max_lines)
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 1
    print(f"OK: {run_dir.resolve()} is a valid token-efficient run")
    return 0


def check_session(
    dsh_home: Path,
    session_id: str,
    max_prompt: int,
    max_tool_share: float,
) -> int:
    projection = (
        dsh_home
        / "storages/session_projcache/sessions"
        / f"{session_id}.json"
    )
    if not projection.is_file():
        print(f"ERROR: missing projection {projection}", file=sys.stderr)
        return 1
    with projection.open(encoding="utf-8") as handle:
        rows = (json.load(handle).get("record") or {}).get("rows") or {}

    pressure = (rows.get("contextPressure") or {}).get("val") or {}
    surface_tokens = int(pressure.get("surfaceTokens") or 0)
    context_window = int(pressure.get("contextWindow") or 0)
    surface = (
        ((rows.get("contextTimeline") or {}).get("val") or {}).get("surface")
        or []
    )
    tool_tokens = sum(
        int(item.get("tokens") or 0)
        for item in surface
        if item.get("cat") == "tool"
    )
    last_buckets = (
        ((((rows.get("tokenUsage") or {}).get("val") or {}).get("last") or {}).get("buckets"))
        or {}
    )
    last_prompt = (
        int(last_buckets.get("uncachedInputTokens") or 0)
        + int(last_buckets.get("cacheReadTokens") or 0)
        + int(last_buckets.get("cacheWriteTokens") or 0)
    )
    utilization = surface_tokens / context_window if context_window else 0.0
    tool_share = tool_tokens / surface_tokens if surface_tokens else 0.0

    warning_reasons: list[str] = []
    if max_prompt > 0 and last_prompt > max_prompt:
        warning_reasons.append(
            f"last prompt {last_prompt:,} exceeds {max_prompt:,}"
        )
    if utilization > 0.70:
        warning_reasons.append(f"context utilization {utilization:.1%} exceeds 70%")
    if tool_share > max_tool_share:
        warning_reasons.append(
            f"tool context share {tool_share:.1%} exceeds {max_tool_share:.1%}"
        )

    print(
        f"session={session_id} surface={surface_tokens:,} "
        f"limit={context_window:,} utilization={utilization:.1%} "
        f"last_prompt={last_prompt:,} tool_share={tool_share:.1%}"
    )
    if warning_reasons:
        for reason in warning_reasons:
            print(f"WARN: {reason}")
        print("ACTION: mask resolved tool output, update the handoff, and start fresh")
        return 2
    print("OK: context is within the configured token-efficiency budget")
    return 0


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description=__doc__)
    subparsers = parser.add_subparsers(dest="command", required=True)

    report_parser = subparsers.add_parser(
        "report", help="summarize DSH usage for one project"
    )
    report_parser.add_argument(
        "--dsh-home", type=Path, default=Path.home() / ".dsh"
    )
    report_parser.add_argument(
        "--cwd",
        type=Path,
        default=Path(__file__).resolve().parents[2],
    )
    report_parser.add_argument(
        "--caps",
        default="100000,200000,300000,400000",
        help="comma-separated prompt caps for illustrative savings",
    )
    report_parser.add_argument("--json", action="store_true")

    init_parser = subparsers.add_parser(
        "init", help="create a fresh per-component run handoff"
    )
    init_parser.add_argument("--run-dir", type=Path, required=True)
    init_parser.add_argument("--component", required=True)
    init_parser.add_argument("--goal", required=True)
    init_parser.add_argument("--force", action="store_true")

    validate_parser = subparsers.add_parser(
        "validate", help="validate a handoff before starting a fresh session"
    )
    validate_parser.add_argument("--run-dir", type=Path, required=True)
    validate_parser.add_argument("--max-lines", type=int, default=150)

    check_parser = subparsers.add_parser(
        "check-session", help="warn when an active DSH session exceeds budget"
    )
    check_parser.add_argument(
        "--dsh-home", type=Path, default=Path.home() / ".dsh"
    )
    check_parser.add_argument("--session", required=True)
    check_parser.add_argument("--max-prompt", type=int, default=300000)
    check_parser.add_argument("--max-tool-share", type=float, default=0.50)
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    args = build_parser().parse_args(argv)
    if args.command == "report":
        caps = [
            int(value)
            for value in args.caps.split(",")
            if value.strip() and int(value) > 0
        ]
        return report(args.dsh_home, args.cwd, caps, args.json)
    if args.command == "init":
        return init_run(args.run_dir, args.component, args.goal, args.force)
    if args.command == "validate":
        return validate_run(args.run_dir, args.max_lines)
    if args.command == "check-session":
        return check_session(
            args.dsh_home,
            args.session,
            args.max_prompt,
            args.max_tool_share,
        )
    raise AssertionError(args.command)


if __name__ == "__main__":
    raise SystemExit(main())
