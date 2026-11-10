#!/usr/bin/env python3
"""Run Compose parity stages in fresh Codex sessions.

This is the external session lifecycle that the agent cannot perform on itself:
it starts one ``codex exec`` process per attempt, verifies the Stage marker, and
starts the next Stage in a new thread. The durable task memory is still
``progress.md``; this script only owns process/session lifecycle and telemetry.

Examples:

  # Inspect the commands and prompt without spending tokens
  python3 scripts/codex_stage_orchestrator.py \
    --component button-group --start-stage 10-baseline

  # Execute from 10-baseline through 40-handoff, using fresh sessions
  python3 scripts/codex_stage_orchestrator.py \
    --component button-group --start-stage 10-baseline --execute \
    --approve-for-me

The agent must finish a successful Stage with::

  STAGE_DONE <stage-id>

When it must rotate before completing the Stage, it writes the handoff and ends
with::

  CONTEXT_HANDOFF <stage-id>

The orchestrator then starts the same Stage in a new thread. A 413 or other
explicit context overflow is treated as another reason to start the same Stage
again, up to ``--max-attempts``.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import shlex
import signal
import subprocess
import sys
import threading
import time
import component_execution_control as control
from dataclasses import asdict, dataclass
from datetime import datetime
from pathlib import Path
from typing import Iterable, Iterator, Sequence


DEFAULT_STAGES = (
    "00-scope",
    "10-baseline",
    "20-implement",
    "30-verify",
    "40-handoff",
)
PAYLOAD_LIMIT_PATTERNS = (
    "payload too large",
    "request entity too large",
    "http 413",
    "status 413",
)
INFRASTRUCTURE_PATTERNS = (
    "you’ve hit your usage limit",
    "you've hit your usage limit",
    "usage limit",
    "insufficient balance",
    "payment required",
    "not logged in",
    "authentication required",
    "failed to initialize in-process app-server client",
    "unable to open database file",
)
ANSI_RE = re.compile(r"\x1b\[[0-9;]*[A-Za-z]")
ACTION_ITEM_TYPES = {"command_execution", "file_change", "mcp_tool_call"}
PROGRESS_LOG = "progress.log"
LOOP_LOG = "loop_log.jsonl"
DONE_MARKER = "DONE.marker"
STATE_AFTER_STAGE = {
    "00-scope": "baseline",
    "10-baseline": "implement",
    "20-implement": "verify",
    "30-verify": "handoff",
    "40-handoff": "complete",
}
STAGE_ACTION_BUDGETS = {
    "00-scope": 30,
    "10-baseline": 30,
    "20-implement": 50,
    "30-verify": 60,
    "40-handoff": 40,
}


@dataclass(frozen=True)
class TokenUsage:
    input_tokens: int = 0
    cached_input_tokens: int = 0
    output_tokens: int = 0
    total_tokens: int = 0

    @property
    def prompt_tokens(self) -> int:
        return self.input_tokens


@dataclass(frozen=True)
class RunResult:
    returncode: int
    thread_id: str | None
    usage: TokenUsage
    action_count: int
    budget_terminated: bool
    timeout_terminated: bool
    final_message: str
    event_log: Path
    message_log: Path
    usage_source: str = "cli_event"
    usage_complete: bool = True


@dataclass(frozen=True)
class StageDecision:
    status: str
    reason: str


def parse_stages(value: str) -> tuple[str, ...]:
    stages = tuple(item.strip() for item in value.split(",") if item.strip())
    if not stages:
        raise ValueError("at least one Stage is required")
    unknown = [stage for stage in stages if stage not in DEFAULT_STAGES]
    if unknown:
        raise ValueError(f"unknown Stage(s): {', '.join(unknown)}")
    indexes = [DEFAULT_STAGES.index(stage) for stage in stages]
    if indexes != sorted(indexes) or len(set(indexes)) != len(indexes):
        raise ValueError("Stages must be unique and in execution order")
    return stages


def stages_from(start_stage: str, stages: Sequence[str]) -> tuple[str, ...]:
    if start_stage not in stages:
        raise ValueError(f"start Stage {start_stage} is not in the selected run")
    return tuple(stages[stages.index(start_stage) :])


def next_stage(stage: str) -> str | None:
    index = DEFAULT_STAGES.index(stage)
    return DEFAULT_STAGES[index + 1] if index + 1 < len(DEFAULT_STAGES) else None


def _integer(value: object) -> int:
    try:
        return int(value)  # type: ignore[arg-type]
    except (TypeError, ValueError):
        return 0


def _first_value(mapping: dict[str, object], *keys: str) -> object | None:
    for key in keys:
        if key in mapping:
            return mapping[key]
    return None


def extract_usage(value: object) -> TokenUsage | None:
    """Extract the first token-usage object from a Codex JSON event."""

    if isinstance(value, dict):
        if any(
            key in value
            for key in (
                "input_tokens",
                "inputTokens",
                "output_tokens",
                "outputTokens",
            )
        ):
            input_tokens = _integer(
                _first_value(value, "input_tokens", "inputTokens")
            )
            cached_tokens = _integer(
                _first_value(
                    value,
                    "cached_input_tokens",
                    "cachedInputTokens",
                )
            )
            output_tokens = _integer(
                _first_value(value, "output_tokens", "outputTokens")
            )
            total_tokens = _integer(
                _first_value(value, "total_tokens", "totalTokens")
            )
            if not total_tokens:
                total_tokens = input_tokens + output_tokens
            return TokenUsage(
                input_tokens=input_tokens,
                cached_input_tokens=cached_tokens,
                output_tokens=output_tokens,
                total_tokens=total_tokens,
            )
        for child in value.values():
            usage = extract_usage(child)
            if usage:
                return usage
    elif isinstance(value, list):
        for child in value:
            usage = extract_usage(child)
            if usage:
                return usage
    return None


def recover_session_usage(thread_id: str | None, sessions_root: Path | None = None) -> TokenUsage | None:
    """Read cumulative counters from this fresh session, never add snapshots."""
    if not thread_id or not re.fullmatch(r"[a-fA-F0-9-]{36}", thread_id):
        return None
    root = sessions_root or Path(os.environ.get("CODEX_HOME", str(Path.home() / ".codex"))) / "sessions"
    best = None
    for path in root.glob(f"*/*/*/*{thread_id}.jsonl"):
        try:
            with path.open(encoding="utf-8", errors="replace") as handle:
                for line in handle:
                    try:
                        event = json.loads(line)
                    except json.JSONDecodeError:
                        continue  # a killed writer may leave a partial final line
                    payload = event.get("payload", {})
                    counters = None
                    if event.get("type") == "token_usage_record":
                        counters = payload.get("thread_token_usage")
                    elif event.get("type") == "event_msg" and payload.get("type") == "token_count":
                        counters = (payload.get("info") or {}).get("total_token_usage")
                    found = extract_usage(counters)
                    if found and (best is None or found.total_tokens >= best.total_tokens):
                        best = found
        except OSError:
            continue
    return best


def repair_run_usage(project_root: Path) -> dict[str, int]:
    """Backfill interrupted run counters without launching a model."""
    scanned = repaired = missing = 0
    for path in (project_root / ".agent-runs").glob("*/orchestrator/runs.json"):
        data = json.loads(path.read_text(encoding="utf-8"))
        changes = {}
        for record in data.get("runs", []):
            scanned += 1
            usage = record.get("usage", {})
            if usage.get("input_tokens", 0) and record.get("usage_complete", True):
                continue
            recovered = recover_session_usage(record.get("thread_id"))
            if recovered and asdict(recovered) != usage:
                record["usage"] = asdict(recovered)
                record["usage_source"] = "session_log"
                record["usage_complete"] = False  # interrupted final request may be unreported
                changes[record.get("event_log")] = record
                repaired += 1
            elif not recovered and record.get("actions", 0):
                missing += 1
        if changes:
            temporary = path.with_suffix(".json.tmp")
            temporary.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
            temporary.replace(path)
            loop = path.parent.parent / LOOP_LOG
            if loop.exists():
                records = [json.loads(line) for line in loop.read_text().splitlines() if line.strip()]
                for record in records:
                    replacement = changes.get(record.get("event_log"))
                    if replacement:
                        for key in ("usage", "usage_source", "usage_complete"):
                            record[key] = replacement[key]
                temporary = loop.with_suffix(".jsonl.tmp")
                temporary.write_text("".join(json.dumps(record) + "\n" for record in records))
                temporary.replace(loop)
    return {"scanned": scanned, "recovered": repaired, "missing": missing}


def extract_thread_id(value: object) -> str | None:
    if isinstance(value, dict):
        for key in ("thread_id", "threadId"):
            candidate = value.get(key)
            if isinstance(candidate, str) and candidate:
                return candidate
        thread = value.get("thread")
        if isinstance(thread, dict):
            candidate = thread.get("id")
            if isinstance(candidate, str) and candidate:
                return candidate
        for child in value.values():
            candidate = extract_thread_id(child)
            if candidate:
                return candidate
    elif isinstance(value, list):
        for child in value:
            candidate = extract_thread_id(child)
            if candidate:
                return candidate
    return None


def _strip_ansi(value: str) -> str:
    return ANSI_RE.sub("", value)


def last_nonempty_line(value: str) -> str:
    lines = [_strip_ansi(line).strip() for line in value.splitlines()]
    return next((line for line in reversed(lines) if line), "")


def blocker_reason(final_message: str) -> str:
    lines = [line.strip() for line in final_message.splitlines() if line.strip() and not line.strip().startswith("STAGE_BLOCKED ")]
    explicit = next((line for line in lines if re.match(r"^(?:[-*] )?(?:Blocker\s*:|阻塞\s*[:：])", line, re.I)), None)
    if explicit:
        return explicit[:500]
    # Prefer the actual failure over a later validator/whitespace success statement.
    failure = next((line for line in lines if re.search(r"\b(blocked|cannot|could not|failed|failure|unavailable)\b|阻塞|阻断|失败|无法", line, re.I)), None)
    fallback = next((line for line in lines if not re.search(r"\b(pass|passed|successful)\b|通过", line, re.I)), None)
    return (failure or fallback or "Blocked without an explicit reason; inspect progress.md and final message")[:500]


def classify_outcome(
    stage: str,
    returncode: int,
    final_message: str,
    event_tail: str,
    *,
    budget_terminated: bool = False,
    timeout_terminated: bool = False,
) -> StageDecision:
    if budget_terminated:
        return StageDecision(
            "action_budget", "per-session tool-action budget was exhausted"
        )
    if timeout_terminated:
        return StageDecision(
            "session_timeout", "per-session wall-clock budget was exhausted"
        )
    last_line = last_nonempty_line(final_message)
    if returncode == 0 and last_line == f"STAGE_BLOCKED {stage}":
        return StageDecision("blocked", blocker_reason(final_message))
    if returncode == 0 and last_line == f"STAGE_DONE {stage}":
        return StageDecision("stage_done", "required Stage marker found")
    if returncode == 0 and last_line == f"CONTEXT_HANDOFF {stage}":
        return StageDecision(
            "context_handoff", "agent requested a fresh continuation session"
        )

    haystack = f"{event_tail}\n{final_message}".lower()
    if any(pattern in haystack for pattern in PAYLOAD_LIMIT_PATTERNS):
        return StageDecision(
            "context_overflow", "request body or 413 payload limit was reached"
        )
    if any(pattern in haystack for pattern in INFRASTRUCTURE_PATTERNS):
        return StageDecision(
            "infrastructure_blocked",
            "Codex runtime/provider unavailable: " + next(
                (line.strip()[:300] for line in (event_tail + "\n" + final_message).splitlines()
                 if any(pattern in line.lower() for pattern in INFRASTRUCTURE_PATTERNS)),
                "inspect event log"),
        )
    if returncode != 0:
        return StageDecision(
            "process_failed", f"codex exited with status {returncode}"
        )
    if not last_line:
        return StageDecision("protocol_error", "agent produced no final message")
    return StageDecision(
        "protocol_error",
        f"expected final line 'STAGE_DONE {stage}', got {last_line!r}",
    )


def build_stage_prompt(
    *,
    project_root: Path,
    plan_path: Path,
    skill_path: Path,
    card_path: Path,
    progress_path: Path,
    component: str,
    component_name: str,
    stage: str,
    context_target_tokens: int,
    max_actions: int,
    max_seconds: int,
    attempt: int,
    progress_tail: str,
    commands_tail: str,
) -> str:
    root = project_root.resolve()
    return f"""Execute exactly one Compose Foundation web-parity Stage.

Project root: {root}
Component: {component_name} ({component})
Stage: {stage}
Attempt: {attempt}

Recovery checkpoint: {read_text_tail(root / '.agent-runs' / component / 'recovery.json', lines=20, max_chars=2000) or '<none>'}
The assigned stage and recovery checkpoint override obsolete stop/next instructions
in earlier handoffs. Close the canonical checklist residuals; do not merely repeat
an earlier handoff. Preserve passing evidence whose relevant input hashes still match.
STAGE_DONE 40-handoff is only a handoff. Component acceptance additionally requires
the canonical docs/shadcn-web-progress.md component checkbox to be checked after
all required gates pass. Never check it while required current evidence is missing.
The user explicitly authorizes fixing this component's production defects and the
shared dependency defects blocking it. Stage20 is the fixer context; implement real
repairs and regression checks before returning to verification. Do not edit scheduler
scripts or weaken the plan/coverage/checkbox criteria. Browser harness repairs belong
to the component's verification work. Read the assigned canonical row's historical
scope link for the full acceptance residuals after the documentation reorganization.
Existing paths relocated by docs cleanup are indexed by
docs/archive/2026-11-09/relocation-map.json; locate preserved evidence rather than
declaring it unavailable or discarding current passing work.
Stage 20 must pass owning lint for its modified Kotlin production/test source sets
before STAGE_DONE; fix its own formatting in the implementation session, not in a
later read-only verifier. Attach each Gradle test task's --tests filters immediately
after that task so other tasks do not run unrelated suites accidentally.
In this checkout, Gradle verification commands must add `--no-daemon -x :installGitHooks`:
that unrelated setup task writes read-only .git/hooks. Retain every required
lint/test/build task. Use a fresh single-use daemon so an older Gradle daemon cannot
retain a narrower sandbox/cache policy. Workers receive precise writable grants for
Gradle, Skiko and Kotlin daemon caches; do not grant the whole home directory.
On macOS, native JVM tests may also abort with exit 134/SIGABRT in AWT/AppKit
application registration before assertions, even when cache writes succeed.
Use the bounded diagnostic/crash signature to distinguish this from an assertion
failure; retry the identical --no-daemon verification command once through approved
escalation before declaring an environment blocker. Do not disable the worker sandbox.
Shared lint repair evidence is indexed by .agent-runs/shared-lint-repair/latest.json
when present; the older acceptance-recovery-summary.json remains historical.
Reuse only the tasks covered by a matching current input manifest; never infer an
unlisted task passed. Otherwise rerun the owning task.

Initial reads:
1. {plan_path}: locate rules cited by the current residual; read only those sections
2. {skill_path}: consult only instructions needed for the assigned work unit
3. {card_path}
4. {progress_path}

These are starting reads, not a restriction to four files. Read-only discovery
required by the current card is authorized: search the owning component files,
checklist entry, run commands.log, source manifests, pinned upstream sources and
verification evidence. Use bounded rg/file reads. Do not load unrelated components
or the whole transcript. Scope authorizes this discovery and source verification;
missing filenames in progress.md are a reason to locate them, not to stop.

TAO contract snapshots. Reuse recorded commands instead of rediscovering them.

progress.log tail:
{progress_tail or "<empty>"}

commands.log tail:
{commands_tail or "<empty>"}

Do not load a previous transcript. Do not start another Stage or component.
The Python driver is the only scheduler. Execute this work unit directly; do not
create a review coordinator or delegate the component queue. Do not
poll agents or send progress-only messages. Return one final handoff with evidence
paths, or record the exact blocker. The driver owns waiting, retries and routing.
When plan R10/R15 requires independent review, one bounded fresh `codex exec` reviewer
session is permitted. Pass only the component diff/file paths, acceptance rules and
evidence paths; save its verdict to disk. Do not use acknowledgement-only transport,
reuse the implementer's transcript, start a nested coordinator, or retry an unchanged
review transport failure. Wait for this single process without model polling. Reviewer
must be read-only; report a real review verdict/findings, not a project-loaded receipt.
Follow every numbered B/G/C/M/T/H/R/I/V rule in the plan. Preserve unrelated
working-tree changes. Do not commit, push, publish, reset, or use destructive
cleanup. Store screenshots, contact sheets, DOM/XML, and full logs as files;
pass paths and compact summaries instead of injecting large evidence into chat.
Inspect only prepared image tiles/crops and never repeat the same image hash in
this session.

Before starting work, read the current Stage card and progress.md. If this is a
continuation attempt, preserve all existing progress and continue from current
repository state. Run the card's focused checks, update progress.md, run the
handoff validator, and run `git diff --check` when the plan requires it.
Stay strictly inside the current card's responsibilities; do not bring a later
Stage's build, browser, or audit work into this session.

If the card defines work units, execute at most one unchecked unit in this
session. A completed work unit is a valid rotation point: record its evidence,
run the handoff validator and `git diff --check`, then use CONTEXT_HANDOFF until
every required unit is complete. Reuse commands and evidence already recorded
as passed; do not enumerate assertions when one deterministic batch script can
perform the work and print a compact summary.

Do not simulate or request PTC/run_code unless the current runtime explicitly
exposes that transport. Codex CLI sessions must use their advertised tools.
If any card-required command, including Gradle validation, a read-only source
lookup or bounded browser batch, is blocked only by sandbox permission, retry
that exact command once with escalation
before recording an environment blocker.

Continuation coverage must use the driver-frozen required-verification.json IDs.
Each passed ledger entry must include an inputs mapping of repository-relative source
paths to current SHA256 values. Extra diagnostics never extend the component budget.
Do not broaden or replace an existing frozen matrix to obtain further continuations.

Context rule: if the last prompt approaches {context_target_tokens:,} tokens, the
session is compacted, repeated tool output threatens the payload limit, or you
would otherwise need the previous transcript, stop safely. Finish the current
file state, write an accurate compact handoff to progress.md, and end with
exactly:

CONTEXT_HANDOFF {stage}

Before reporting any gate failure, verify that the evidence describes the current
worktree. Historical failures without matching current inputs are not current blockers;
rerun the narrow owning gate once and record source hashes and the new result. Likewise,
never call an old successful build/browser capture current without matching inputs.

If an external/shared gate fails and this card cannot repair it, do not request
another identical continuation. Record the failed gate, evidence and required fix
in progress.md, put a one-line blocker reason before the final line, and end with:

STAGE_BLOCKED {stage}

Use CONTEXT_HANDOFF only when a new session can make progress (a completed unit or
context pressure). Do not append repeated unchanged blocker reports.

For a genuinely complete Stage, end with exactly:

STAGE_DONE {stage}

Update progress.md after every meaningful checkpoint and at least every 10
substantive tool actions. The driver terminates this session after {max_actions}
actions or {max_seconds} seconds if it does not end first; the next fresh session
will resume from disk.

The final line must contain no Markdown, punctuation, or trailing explanation.
"""


def build_codex_command(
    *,
    codex_bin: str,
    project_root: Path,
    message_log: Path,
    sandbox: str,
    approve_for_me: bool,
    model: str | None,
    profile: str | None,
    compact_threshold: int,
    compact_threshold_scope: str,
    prompt: str,
) -> list[str]:
    command = [
        codex_bin,
        "exec",
        "--json",
        "-C",
        str(project_root.resolve()),
        "--thread-source",
        "stage_orchestrator",
        "-o",
        str(message_log),
    ]
    if approve_for_me:
        command.append("--approve-for-me")
    else:
        command.extend(["--sandbox", sandbox])
    if approve_for_me or sandbox == "workspace-write":
        gradle_home = Path(os.environ.get("GRADLE_USER_HOME", str(Path.home() / ".gradle"))).resolve()
        cache_dirs = [gradle_home, Path.home() / ".skiko",
                      Path.home() / "Library/Application Support/kotlin/daemon"]
        for cache in cache_dirs:
            command.extend(["--add-dir", str(cache)])
    if model:
        command.extend(["--model", model])
    if profile:
        command.extend(["--profile", profile])
    if compact_threshold > 0:
        command.extend(
            [
                "-c",
                f"model_auto_compact_token_limit={compact_threshold}",
                "-c",
                (
                    "model_auto_compact_token_limit_scope="
                    f"{json.dumps(compact_threshold_scope)}"
                ),
            ]
        )
    command.append(prompt)
    return command


def read_tail(path: Path, max_bytes: int = 65536) -> str:
    try:
        with path.open("rb") as handle:
            handle.seek(0, 2)
            size = handle.tell()
            handle.seek(max(0, size - max_bytes))
            return handle.read().decode("utf-8", errors="replace")
    except OSError:
        return ""


def read_text_tail(
    path: Path, *, lines: int, max_chars: int = 4000
) -> str:
    try:
        content = path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return ""
    tail = "\n".join(content.splitlines()[-lines:])
    return tail[-max_chars:]


def append_contract_mark(run_dir: Path, stage: str, status: str) -> None:
    path = run_dir / PROGRESS_LOG
    with path.open("a", encoding="utf-8") as handle:
        handle.write(f"{stage} {status}\n")


def append_loop_record(run_dir: Path, record: dict[str, object]) -> None:
    path = run_dir / LOOP_LOG
    with path.open("a", encoding="utf-8") as handle:
        handle.write(json.dumps(record, ensure_ascii=False) + "\n")


def recover_transient_failure(run_dir: Path) -> bool:
    """Remove only a trailing FAIL caused by a known provider outage.

    The original FAIL remains in progress.log for auditability. Appending a
    ``rotate`` mark after it makes deterministic auto-routing retry the same
    Stage after quota, balance, or authentication has been restored.
    """

    latest = latest_progress_mark(run_dir)
    if not latest or latest[1] != "FAIL":
        return False
    stage = latest[0]
    runs_path = run_dir / "orchestrator" / "runs.json"
    try:
        payload = json.loads(runs_path.read_text(encoding="utf-8"))
        runs = payload.get("runs", [])
    except (OSError, json.JSONDecodeError, AttributeError):
        return False
    for run in reversed(runs):
        if not isinstance(run, dict) or run.get("stage") != stage:
            continue
        if run.get("status") != "process_failed":
            return False
        event_log = run.get("event_log")
        if not isinstance(event_log, str):
            return False
        evidence = read_text_tail(
            Path(event_log), lines=100, max_chars=20_000
        ).lower()
        if any(pattern in evidence for pattern in INFRASTRUCTURE_PATTERNS):
            append_contract_mark(run_dir, stage, "rotate")
            print(
                f"recovered transient {stage} FAIL from provider outage; "
                "the Stage will retry"
            )
            return True
        return False
    return False


def bootstrap_tao_contract(run_dir: Path) -> None:
    """Migrate an existing runs.json into the TAO routing marks."""

    progress_path = run_dir / PROGRESS_LOG
    runs_path = run_dir / "orchestrator" / "runs.json"
    completed: list[str] = []
    existing_runs: list[dict[str, object]] = []
    if runs_path.is_file():
        try:
            runs = json.loads(runs_path.read_text(encoding="utf-8")).get("runs", [])
        except (json.JSONDecodeError, AttributeError):
            runs = []
        for run in runs:
            if not isinstance(run, dict):
                continue
            existing_runs.append(run)
            stage = run.get("stage")
            if run.get("status") == "stage_done" and stage in DEFAULT_STAGES:
                if stage not in completed:
                    completed.append(stage)
    if not progress_path.exists() and completed:
        progress_path.write_text(
            "".join(f"{stage} ok\n" for stage in completed),
            encoding="utf-8",
        )
    loop_path = run_dir / LOOP_LOG
    if not loop_path.exists() and existing_runs:
        with loop_path.open("w", encoding="utf-8") as handle:
            for run in existing_runs:
                handle.write(json.dumps(run, ensure_ascii=False) + "\n")
    if (
        not (run_dir / DONE_MARKER).exists()
        and completed[-len(DEFAULT_STAGES) :] == list(DEFAULT_STAGES)
    ):
        write_done_marker(run_dir)


def latest_progress_mark(run_dir: Path) -> tuple[str, str] | None:
    path = run_dir / PROGRESS_LOG
    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except OSError:
        return None
    for line in reversed(lines):
        fields = line.split()
        if (
            len(fields) >= 2
            and fields[0] in DEFAULT_STAGES
            and fields[1] in {"ok", "FAIL", "rotate"}
        ):
            return fields[0], fields[1]
    return None


def last_completed_stage(run_dir: Path) -> str | None:
    path = run_dir / PROGRESS_LOG
    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except OSError:
        return None
    for line in reversed(lines):
        fields = line.split()
        if len(fields) >= 2 and fields[0] in DEFAULT_STAGES:
            if fields[1] == "ok":
                return fields[0]
    return None


def infer_start_stage(run_dir: Path) -> str | None:
    latest = latest_progress_mark(run_dir)
    if latest and latest[1] == "FAIL":
        raise RuntimeError(
            f"latest TAO mark is {latest[0]} FAIL; no automatic retry"
        )
    if latest and latest[1] == "rotate":
        return latest[0]
    last = last_completed_stage(run_dir)
    if last is None:
        return DEFAULT_STAGES[0]
    return next_stage(last)


def checklist_accepted(project_root: Path, component: str) -> bool:
    """The canonical component checkbox is required for final acceptance."""
    path = project_root / "docs/shadcn-web-progress.md"
    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except OSError:
        return False
    matches = [re.match(r"^- \[([ xX])\] .+? \(`([^`]+)`\)", line) for line in lines]
    statuses = [match[1].lower() == "x" for match in matches if match and match[2] == component]
    return bool(statuses) and all(statuses)


def pending_resume_stage(run_dir: Path) -> str:
    try:
        payload = json.loads((run_dir / "acceptance.json").read_text())
        stage = payload.get("resume_stage")
        if payload.get("status") == "pending" and stage in DEFAULT_STAGES[:-1]:
            return stage
    except (OSError, ValueError, AttributeError):
        pass
    return "20-implement"


def acceptance_problem(project_root: Path, run_dir: Path) -> str | None:
    """Check durable gate evidence and current source correspondence before closure."""
    root = project_root.resolve()
    try:
        receipt = json.loads((run_dir / 'acceptance.json').read_text())
    except (OSError, ValueError):
        return 'Missing or invalid acceptance.json'
    if not isinstance(receipt, dict) or receipt.get('status') != 'accepted':
        return 'Acceptance receipt is not accepted'
    def matches(path, digest):
        if not isinstance(path, str) or not isinstance(digest, str):
            return False
        location = (root / path).resolve()
        try:
            return location.is_relative_to(root) and location.is_file() and hashlib.sha256(location.read_bytes()).hexdigest() == digest
        except OSError:
            return False
    sources = receipt.get('source_hashes')
    if not isinstance(sources, dict) or not sources or not any(str(path).endswith('.kt') for path in sources):
        return 'Acceptance requires current component Kotlin source hashes'
    for directory in ('app/shared/src/commonMain', 'shadcn/core/src/commonMain', 'app/webApp/src'):
        for path in (root / directory).rglob('*.kt'):
            if str(path.relative_to(root)) not in sources:
                return f'Acceptance source manifest omits shared production dependency: {path.relative_to(root)}'
    for name in ('build.gradle.kts', 'settings.gradle.kts', '.editorconfig', 'gradle/libs.versions.toml',
                 'app/shared/build.gradle.kts', 'shadcn/core/build.gradle.kts', 'app/webApp/build.gradle.kts'):
        if (root / name).is_file() and name not in sources:
            return f'Acceptance source manifest omits build/lint input: {name}'
    for path, digest in sources.items():
        if not matches(path, digest):
            return f'Acceptance source changed: {path}'
    gates = receipt.get('gates', {})
    for name in ('tests', 'lint', 'build', 'browser', 'audit', 'review'):
        gate = gates.get(name) if isinstance(gates, dict) else None
        if not isinstance(gate, dict) or gate.get('status') != 'passed' or not matches(gate.get('evidence'), gate.get('sha256')):
            return f'Missing, failed or changed acceptance evidence: {name}'
        try:
            proof = json.loads((root / gate['evidence']).read_text())
            if proof.get('status') != 'passed' or not matches(proof.get('inputs'), proof.get('inputs_sha256')):
                return f'Acceptance gate lacks bound runtime inputs: {name}'
            manifest = json.loads((root / proof['inputs']).read_text())
            bound = manifest.get('inputs', manifest)
            if not isinstance(bound, dict) or any(bound.get(path) != digest for path, digest in sources.items()):
                return f'Acceptance gate inputs differ from accepted source: {name}'
            for path, digest in bound.items():
                if not matches(path, digest):
                    return f'Acceptance runtime input changed: {name}: {path}'
            artifacts = proof.get('artifacts')
            if not isinstance(artifacts, dict) or not artifacts or any(not matches(path, digest) for path, digest in artifacts.items()):
                return f'Acceptance gate lacks matching retained artifacts: {name}'
        except (OSError, ValueError, AttributeError, TypeError):
            return f'Acceptance gate has invalid runtime provenance: {name}'
    return None


def update_component_checkbox(project_root: Path, component: str, checked: bool) -> None:
    path = project_root / 'docs/shadcn-web-progress.md'
    text = path.read_text()
    pattern = r'(?m)^(- )\[[ xX]\]( .+? \(`' + re.escape(component) + r'`\))'
    text, count = re.subn(pattern, lambda m: m[1] + ('[x]' if checked else '[ ]') + m[2], text)
    if count != 1:
        raise RuntimeError(f'Expected one canonical checkbox for {component}, found {count}')
    marks = re.findall(r'^- \[([ xX])\] .+? \(`[^`]+`\)', text, re.M)
    closed = sum(mark.lower() == 'x' for mark in marks)
    text = re.sub(r'\*\*\d+ / \d+ 项已勾选，\d+ 项未完成\*\*',
                  f'**{closed} / {len(marks)} 项已勾选，{len(marks)-closed} 项未完成**', text)
    temporary = path.with_suffix('.md.tmp')
    temporary.write_text(text)
    temporary.replace(path)


def reopen_checkpoint(run_dir: Path, stage: str, reason: str) -> None:
    """Preserve old evidence and reopen only the operator-selected checkpoint."""
    if stage not in DEFAULT_STAGES:
        raise ValueError(f"invalid recovery stage: {stage}")
    marker = run_dir / DONE_MARKER
    if marker.exists():
        marker.replace(run_dir / "HANDOFF.marker")
    record = {"stage": stage, "reason": reason,
              "timestamp": datetime.now().astimezone().isoformat(timespec="seconds")}
    (run_dir / "recovery.json").write_text(json.dumps(record, ensure_ascii=False, indent=2) + "\n")
    append_contract_mark(run_dir, stage, "rotate")
    append_loop_record(run_dir, {"status": "checkpoint_reopened", **record})
    state_path = run_dir / "state.json"
    if state_path.exists():
        state = json.loads(state_path.read_text())
        state.update(stage=stage.split("-", 1)[1], status="active", updated_at=record["timestamp"])
        temporary = state_path.with_suffix(".json.tmp")
        temporary.write_text(json.dumps(state, ensure_ascii=False, indent=2) + "\n")
        temporary.replace(state_path)


def write_done_marker(run_dir: Path) -> bool:
    accepted = checklist_accepted(run_dir.parent.parent, run_dir.name)
    path = run_dir / (DONE_MARKER if accepted else "HANDOFF.marker")
    if not accepted and (run_dir / DONE_MARKER).exists():
        (run_dir / DONE_MARKER).replace(path)
    path.write_text(
        datetime.now().astimezone().isoformat(timespec="seconds") + "\n",
        encoding="utf-8",
    )
    return accepted


def run_codex(
    command: Sequence[str],
    *,
    project_root: Path,
    event_log: Path,
    message_log: Path,
    max_actions: int,
    max_seconds: int,
    max_input_tokens: int = 0,
) -> RunResult:
    event_log.parent.mkdir(parents=True, exist_ok=True)
    message_log.parent.mkdir(parents=True, exist_ok=True)
    if message_log.exists():
        message_log.unlink()

    thread_id: str | None = None
    usage = TokenUsage()
    action_count = 0
    budget_terminated = False
    timeout_terminated = False
    progress_lock = threading.Lock()
    def publish_progress(event_type: str) -> None:
        with progress_lock:
            path = event_log.parent.parent / "live.json"
            temporary = path.with_suffix(".json.tmp")
            temporary.write_text(json.dumps({
                "stage": event_log.parent.name,
                "updated_at": datetime.now().astimezone().isoformat(timespec="seconds"),
                "event_type": event_type, "thread_id": thread_id,
                "actions": action_count, "usage": asdict(usage), "event_log": str(event_log),
            }) + "\n", encoding="utf-8")
            temporary.replace(path)

    publish_progress("starting")
    with event_log.open("w", encoding="utf-8") as raw:
        process = subprocess.Popen(
            list(command),
            cwd=project_root,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="utf-8",
            errors="replace",
            bufsize=1,
            start_new_session=True,
        )
        assert process.stdout is not None

        def terminate_for_timeout() -> None:
            nonlocal timeout_terminated
            if process.poll() is not None:
                return
            timeout_terminated = True
            print(
                f"wall-clock budget reached: {max_seconds}s; rotating session"
            )
            try:
                os.killpg(process.pid, signal.SIGTERM)
            except ProcessLookupError:
                process.terminate()

        monitor_stop = threading.Event()
        def monitor_input_budget() -> None:
            nonlocal budget_terminated, usage
            while not monitor_stop.wait(5):
                if process.poll() is not None:
                    return
                recovered = recover_session_usage(thread_id)
                if recovered and recovered.input_tokens > usage.input_tokens:
                    usage = recovered
                    publish_progress('usage.sample')
                if max_input_tokens and usage.input_tokens >= max_input_tokens:
                    budget_terminated = True
                    try:
                        os.killpg(process.pid, signal.SIGTERM)
                    except ProcessLookupError:
                        pass
                    return
        monitor = threading.Thread(target=monitor_input_budget, daemon=True)
        if max_input_tokens:
            monitor.start()
        watchdog = (
            threading.Timer(max_seconds, terminate_for_timeout)
            if max_seconds > 0
            else None
        )
        if watchdog:
            watchdog.daemon = True
            watchdog.start()
        with process.stdout:
            for line in process.stdout:
                raw.write(line)
                raw.flush()
                try:
                    event = json.loads(line)
                except json.JSONDecodeError:
                    continue
                found_thread = extract_thread_id(event)
                if found_thread:
                    thread_id = found_thread
                    print(f"thread: {thread_id}")
                found_usage = extract_usage(event)
                if found_usage:
                    if found_usage.input_tokens >= usage.input_tokens:
                        usage = found_usage
                    if max_input_tokens and usage.input_tokens >= max_input_tokens:
                        budget_terminated = True
                        try:
                            os.killpg(process.pid, signal.SIGTERM)
                        except ProcessLookupError:
                            pass
                publish_progress(str(event.get("type", "event")))
                item = event.get("item")
                if (
                    event.get("type") == "item.started"
                    and isinstance(item, dict)
                    and item.get("type") in ACTION_ITEM_TYPES
                ):
                    action_count += 1
                    publish_progress("item.started")
                    if max_actions > 0 and action_count >= max_actions:
                        budget_terminated = True
                        print(
                            "action budget reached:"
                            f" {action_count}/{max_actions}; rotating session"
                        )
                        try:
                            os.killpg(process.pid, signal.SIGTERM)
                        except ProcessLookupError:
                            process.terminate()
                        break
        monitor_stop.set()
        if max_input_tokens:
            monitor.join(timeout=6)
        if watchdog:
            watchdog.cancel()
        try:
            returncode = process.wait(timeout=15)
        except subprocess.TimeoutExpired:
            try:
                os.killpg(process.pid, signal.SIGKILL)
            except ProcessLookupError:
                process.kill()
            returncode = process.wait()

    usage_source = "cli_event" if usage.total_tokens else "unavailable"
    usage_complete = bool(usage.total_tokens) and not (budget_terminated or timeout_terminated or returncode != 0)
    if not usage_complete:
        recovered = recover_session_usage(thread_id)
        if recovered and recovered.total_tokens >= usage.total_tokens:
            usage = recovered
            usage_source = "session_log"

    publish_progress(f"exited:{returncode}")
    final_message = (
        message_log.read_text(encoding="utf-8", errors="replace")
        if message_log.exists()
        else ""
    )
    print(
        "usage:"
        f" prompt={usage.prompt_tokens:,}"
        f" cached={usage.cached_input_tokens:,}"
        f" output={usage.output_tokens:,}"
        f" actions={action_count}"
        f" timed_out={str(timeout_terminated).lower()}"
    )
    return RunResult(
        returncode=returncode,
        thread_id=thread_id,
        usage=usage,
        action_count=action_count,
        budget_terminated=budget_terminated,
        timeout_terminated=timeout_terminated,
        final_message=final_message,
        event_log=event_log,
        message_log=message_log,
        usage_source=usage_source,
        usage_complete=usage_complete,
    )


def run_checked(command: Sequence[str], *, cwd: Path) -> None:
    result = subprocess.run(
        list(command),
        cwd=cwd,
        text=True,
        encoding="utf-8",
        errors="replace",
        capture_output=True,
    )
    if result.stdout:
        print(result.stdout.rstrip())
    if result.returncode != 0:
        if result.stderr:
            print(result.stderr.rstrip(), file=sys.stderr)
        raise RuntimeError(f"command failed: {shlex.join(command)}")


def append_run_state(state_path: Path, record: dict[str, object]) -> None:
    state: dict[str, object] = {"version": 1, "runs": []}
    if state_path.exists():
        try:
            loaded = json.loads(state_path.read_text(encoding="utf-8"))
            if isinstance(loaded, dict) and isinstance(loaded.get("runs"), list):
                state = loaded
        except json.JSONDecodeError:
            pass
    runs = state.get("runs")
    assert isinstance(runs, list)
    runs.append(record)
    state["updated_at"] = datetime.now().astimezone().isoformat(timespec="seconds")
    temporary = state_path.with_suffix(".json.tmp")
    temporary.write_text(
        json.dumps(state, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    temporary.replace(state_path)


def advance_run_state(run_dir: Path, completed_stage: str) -> None:
    state_path = run_dir / "state.json"
    state = json.loads(state_path.read_text(encoding="utf-8"))
    if not isinstance(state, dict):
        raise RuntimeError(f"invalid state file: {state_path}")
    next_state = STATE_AFTER_STAGE[completed_stage]
    state["stage"] = next_state
    state["status"] = "complete" if next_state == "complete" else "active"
    state["updated_at"] = datetime.now().astimezone().isoformat(
        timespec="seconds"
    )
    temporary = state_path.with_suffix(".json.tmp")
    temporary.write_text(
        json.dumps(state, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )
    temporary.replace(state_path)


def verify_prerequisites(
    *,
    project_root: Path,
    plan_path: Path,
    skill_path: Path,
    run_dir: Path,
) -> None:
    required = [project_root, plan_path, skill_path, run_dir]
    missing = [path for path in required if not path.exists()]
    if missing:
        raise RuntimeError(
            "missing required path(s): "
            + ", ".join(str(path) for path in missing)
        )


def verify_handoff(project_root: Path, run_dir: Path) -> None:
    run_checked(
        [
            sys.executable,
            "scripts/token_efficiency.py",
            "validate",
            "--run-dir",
            str(run_dir),
        ],
        cwd=project_root,
    )
    run_checked(["git", "diff", "--check"], cwd=project_root)


def verify_handoff_or_fail(
    project_root: Path, run_dir: Path, stage: str
) -> None:
    try:
        verify_handoff(project_root, run_dir)
    except (OSError, RuntimeError):
        append_contract_mark(run_dir, stage, "FAIL")
        raise


def attempt_paths(
    run_dir: Path, stage: str, attempt: int
) -> tuple[Path, Path]:
    stamp = datetime.now().astimezone().strftime("%Y%m%dT%H%M%S%z")
    base = run_dir / "orchestrator" / stage / f"attempt-{attempt}-{stamp}"
    return base.with_suffix(".events.jsonl"), base.with_suffix(".last.md")


def next_verification_unit(progress_path: Path) -> str | None:
    """Use the ordered Next checkpoint, not growing handoff prose, as progress."""
    text = read_text_tail(progress_path, lines=300, max_chars=50000)
    section = re.search(r"^## Next\s*\n(.*?)(?=^## |\Z)", text, re.M | re.S)
    match = re.search(r"30[a-d]-(?:tests|build|browser|audit)", section.group(1)) if section else None
    return match.group(0) if match else None


def verification_checkpoint(progress_path: Path) -> tuple[str | None, frozenset[str]]:
    """Count completed coverage IDs only when their local evidence hashes match.

    This measures continuation progress, never component acceptance. Rewriting
    prose, timestamps or an already-completed check does not advance coverage.
    """
    unit = next_verification_unit(progress_path)
    return unit, control.coverage(progress_path.parent)[0]


def execute_stage(**kwargs) -> int:
    import fcntl
    run = kwargs['args'].run_dir
    run.mkdir(parents=True, exist_ok=True)
    with (run / 'execution.lock').open('a') as lock:
        try:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            raise RuntimeError('another component execution owns this checkpoint')
        return _execute_stage(**kwargs)


def _execute_stage(
    *,
    args: argparse.Namespace,
    project_root: Path,
    plan_path: Path,
    skill_path: Path,
    progress_path: Path,
    stage: str,
    state_path: Path,
) -> int:
    card_path = (
        project_root
        / ".agents/skills/token-efficient-compose-workflow/cards"
        / f"{stage}.md"
    )
    if not card_path.is_file():
        raise RuntimeError(f"missing Stage card: {card_path}")
    action_budget = args.max_actions or STAGE_ACTION_BUDGETS[stage]

    continuation = control.read_json(args.run_dir / 'continuation-state.json', {})
    unchanged_handoffs = continuation.get('unchanged_handoffs', 0)
    control.freeze_matrix(args.run_dir)
    for attempt in range(1, args.max_attempts + 1):
        inputs_digest = control.failure_digest(project_root, args.run_dir)
        if not control.retry_allowed(args.run_dir, stage, inputs_digest):
            print(f'UNCHANGED_FAILURE: {stage}; repair inputs before retrying', file=sys.stderr)
            return 2
        allowance = control.reserve(args.run_dir, args.max_component_sessions,
                                    args.max_component_input_tokens, args.max_component_seconds,
                                    args.max_seconds or args.max_component_seconds)
        if allowance is None:
            print('COMPONENT_BUDGET_EXHAUSTED: checkpoint preserved; no automatic reset', file=sys.stderr)
            return 6
        previous_checkpoint = verification_checkpoint(progress_path) if stage == "30-verify" else (None, frozenset())
        previous_unit = previous_checkpoint[0]
        progress_tail = read_text_tail(
            args.run_dir / PROGRESS_LOG,
            lines=8,
            max_chars=2000,
        )
        commands_tail = read_text_tail(
            args.run_dir / "commands.log",
            lines=15,
            max_chars=3000,
        )
        prompt = build_stage_prompt(
            project_root=project_root,
            plan_path=plan_path.relative_to(project_root),
            skill_path=skill_path.relative_to(project_root),
            card_path=card_path.relative_to(project_root),
            progress_path=progress_path.relative_to(project_root),
            component=args.component,
            component_name=args.component_name,
            stage=stage,
            context_target_tokens=args.context_target_tokens,
            max_actions=action_budget,
            max_seconds=allowance['seconds'],
            attempt=attempt,
            progress_tail=progress_tail,
            commands_tail=commands_tail,
        )
        event_log, message_log = attempt_paths(
            args.run_dir, stage, attempt
        )
        command = build_codex_command(
            codex_bin=args.codex,
            project_root=project_root,
            message_log=message_log,
            sandbox=args.sandbox,
            approve_for_me=args.approve_for_me,
            model=args.model,
            profile=args.profile,
            compact_threshold=args.compact_threshold,
            compact_threshold_scope=args.compact_threshold_scope,
            prompt=prompt,
        )
        print(f"\n[{stage}] attempt {attempt}/{args.max_attempts}")
        print(f"event_log: {event_log}")
        session_started = time.monotonic()
        result = run_codex(
            command,
            project_root=project_root,
            event_log=event_log,
            message_log=message_log,
            max_actions=action_budget,
            max_seconds=allowance['seconds'],
            max_input_tokens=allowance['input_tokens'],
        )
        elapsed = time.monotonic() - session_started
        control.finish(args.run_dir, elapsed, result.usage.input_tokens if result.usage_complete else max(result.usage.input_tokens, allowance['input_tokens']),
                       observed_input_tokens=result.usage.input_tokens)
        if result.usage.input_tokens > args.warn_stage_input_tokens:
            print(f'warning: session input exceeded watch target: {result.usage.input_tokens:,}')
        decision = classify_outcome(
            stage,
            result.returncode,
            result.final_message,
            read_tail(result.event_log),
            budget_terminated=result.budget_terminated,
            timeout_terminated=result.timeout_terminated,
        )
        advanced = False
        if decision.status == "context_handoff":
            control.freeze_matrix(args.run_dir)
            checkpoint = verification_checkpoint(progress_path)
            advanced = (control.advanced(previous_checkpoint, checkpoint) if stage == '30-verify'
                        else control.failure_digest(project_root, args.run_dir) != inputs_digest)
            unchanged_handoffs = 0 if advanced else unchanged_handoffs + 1
            continuation.update(unchanged_handoffs=unchanged_handoffs)
            if advanced:
                continuation['last_advanced_at'] = datetime.now().astimezone().isoformat(timespec='seconds')
            control.write_json(args.run_dir / 'continuation-state.json', continuation)
            if unchanged_handoffs >= 2:
                decision = StageDecision("blocked", f"No work-unit advancement or new hash-bound coverage after two handoffs: {previous_unit}; inspect progress.md blocker")
        record = {
            "stage": stage,
            "attempt": attempt,
            "status": decision.status,
            "reason": decision.reason,
            "timestamp": datetime.now()
            .astimezone()
            .isoformat(timespec="seconds"),
            "thread_id": result.thread_id,
            "returncode": result.returncode,
            "elapsed_seconds": elapsed,
            "inputs_sha256": inputs_digest,
            "usage": asdict(result.usage),
            "usage_source": result.usage_source,
            "usage_complete": result.usage_complete,
            "actions": result.action_count,
            "event_log": str(result.event_log),
            "message_log": str(result.message_log),
        }
        append_run_state(
            state_path,
            record,
        )
        append_loop_record(args.run_dir, record)
        if decision.status == 'blocked':
            control.record_failure(args.run_dir, stage, control.failure_digest(project_root, args.run_dir))

        if decision.status == "stage_done":
            continuation.update(unchanged_handoffs=0, last_advanced_at=datetime.now().astimezone().isoformat(timespec='seconds'))
            control.write_json(args.run_dir / 'continuation-state.json', continuation)
            try:
                verify_handoff_or_fail(project_root, args.run_dir, stage)
            except RuntimeError:
                if stage == '40-handoff' and args.require_acceptance_receipt:
                    update_component_checkbox(project_root, args.component, False)
                raise
            if stage == '40-handoff' and args.require_acceptance_receipt:
                problem = acceptance_problem(project_root, args.run_dir)
                if problem:
                    update_component_checkbox(project_root, args.component, False)
                    receipt_path = args.run_dir / 'acceptance.json'
                    try:
                        receipt = json.loads(receipt_path.read_text())
                        if not isinstance(receipt, dict): receipt = {}
                    except (OSError, ValueError):
                        receipt = {}
                    resume = receipt.get('resume_stage') if receipt.get('status') == 'pending' else None
                    if resume not in DEFAULT_STAGES[:-1]: resume = '30-verify'
                    receipt.update(status='pending', resume_stage=resume, reason=problem)
                    receipt_path.write_text(json.dumps(receipt, ensure_ascii=False, indent=2) + '\n')
                    append_contract_mark(args.run_dir, resume, 'rotate')
                    state_file = args.run_dir / 'state.json'
                    if state_file.exists():
                        state = json.loads(state_file.read_text())
                        state.update(stage=resume.split('-', 1)[1], status='acceptance_pending')
                        state_file.write_text(json.dumps(state, indent=2) + '\n')
                    marker = args.run_dir / DONE_MARKER
                    if marker.exists(): marker.replace(args.run_dir / 'HANDOFF.marker')
                    print(f'ACCEPTANCE_PENDING: {problem}')
                    return 4
                if checklist_accepted(project_root, args.component):
                    update_component_checkbox(project_root, args.component, True)
            append_contract_mark(args.run_dir, stage, "ok")
            advance_run_state(args.run_dir, stage)
            if STATE_AFTER_STAGE[stage] == "complete":
                if not write_done_marker(args.run_dir):
                    state = json.loads((args.run_dir / "state.json").read_text())
                    state["status"] = "acceptance_pending"
                    (args.run_dir / "state.json").write_text(json.dumps(state, indent=2) + "\n")
                    print("ACCEPTANCE_PENDING: canonical component checklist remains unchecked")
                    return 4
            print(f"[{stage}] completed")
            return 0

        if control.load_budget(args.run_dir)['input_tokens'] >= args.max_component_input_tokens:
            print('COMPONENT_BUDGET_EXHAUSTED: input allowance reached; checkpoint preserved', file=sys.stderr)
            return 6

        if decision.status in {
            "context_handoff",
            "context_overflow",
            "action_budget",
            "session_timeout",
        }:
            print(f"[{stage}] rotating: {decision.reason}")
            if attempt < args.max_attempts:
                verify_handoff_or_fail(project_root, args.run_dir, stage)
                append_contract_mark(args.run_dir, stage, "rotate")
                continue
            if stage == "30-verify" and decision.status == "context_handoff" and advanced:
                verify_handoff_or_fail(project_root, args.run_dir, stage)
                append_contract_mark(args.run_dir, stage, "rotate")
                print("CHECKPOINT_PENDING: session cap reached with verified progress; resume next batch")
                return 5
        else:
            print(f"[{stage}] failed: {decision.reason}", file=sys.stderr)
            if decision.status == "infrastructure_blocked":
                return 3
        if decision.status not in {'context_handoff', 'context_overflow', 'action_budget', 'session_timeout'}:
            control.record_failure(args.run_dir, stage, control.failure_digest(project_root, args.run_dir))
        append_contract_mark(args.run_dir, stage, "FAIL")
        return 2

    return 2


def dry_run(
    *,
    args: argparse.Namespace,
    project_root: Path,
    plan_path: Path,
    skill_path: Path,
    progress_path: Path,
) -> int:
    for stage in args.stages:
        card_path = (
            project_root
            / ".agents/skills/token-efficient-compose-workflow/cards"
            / f"{stage}.md"
        )
        prompt = build_stage_prompt(
            project_root=project_root,
            plan_path=plan_path.relative_to(project_root),
            skill_path=skill_path.relative_to(project_root),
            card_path=card_path.relative_to(project_root),
            progress_path=progress_path.relative_to(project_root),
            component=args.component,
            component_name=args.component_name,
            stage=stage,
            context_target_tokens=args.context_target_tokens,
            max_actions=(
                args.max_actions or STAGE_ACTION_BUDGETS[stage]
            ),
            max_seconds=args.max_seconds,
            attempt=1,
            progress_tail=read_text_tail(
                args.run_dir / PROGRESS_LOG,
                lines=8,
                max_chars=2000,
            ),
            commands_tail=read_text_tail(
                args.run_dir / "commands.log",
                lines=15,
                max_chars=3000,
            ),
        )
        event_log, message_log = attempt_paths(args.run_dir, stage, 1)
        command = build_codex_command(
            codex_bin=args.codex,
            project_root=project_root,
            message_log=message_log,
            sandbox=args.sandbox,
            approve_for_me=args.approve_for_me,
            model=args.model,
            profile=args.profile,
            compact_threshold=args.compact_threshold,
            compact_threshold_scope=args.compact_threshold_scope,
            prompt=prompt,
        )
        print(f"\n=== {stage} ===")
        print(shlex.join(command[:-1] + ["<stage-prompt>"]))
        print(f"event_log: {event_log}")
        print("\n--- prompt ---")
        print(prompt.rstrip())
    return 0


def parse_args(argv: Sequence[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Run each Compose parity Stage in a fresh Codex session and "
            "rotate on context pressure. Dry-run unless --execute is supplied."
        )
    )
    parser.add_argument(
        "--project-root",
        type=Path,
        default=Path(__file__).resolve().parents[1],
    )
    parser.add_argument("--component", required=True)
    parser.add_argument("--component-name")
    parser.add_argument(
        "--stages",
        default=",".join(DEFAULT_STAGES),
        help="comma-separated Stage list",
    )
    parser.add_argument(
        "--start-stage",
        default="auto",
        help="Stage ID, or auto to route from the last progress.log ok mark",
    )
    parser.add_argument('--max-component-sessions', type=int, default=control.DEFAULT_SESSIONS)
    parser.add_argument('--max-component-input-tokens', type=int, default=control.DEFAULT_INPUT_TOKENS)
    parser.add_argument('--max-component-seconds', type=int, default=control.DEFAULT_SECONDS)
    parser.add_argument("--max-attempts", type=int, default=6)
    parser.add_argument(
        "--context-target-tokens",
        "--max-prompt-tokens",
        dest="context_target_tokens",
        type=int,
        default=250_000,
        help="per-prompt handoff target included in the Stage card prompt",
    )
    parser.add_argument(
        "--warn-stage-input-tokens",
        type=int,
        default=2_000_000,
        help="warning threshold for one session input; component limits are enforced separately",
    )
    parser.add_argument(
        "--max-actions",
        type=int,
        default=None,
        help=(
            "override the hard tool-action budget for every Stage; default is "
            "scope/baseline 30, implement 50, verify 60, handoff 40"
        ),
    )
    parser.add_argument(
        "--compact-threshold",
        type=int,
        default=250_000,
        help=(
            "Codex model_auto_compact_token_limit; use 0 to leave the "
            "host/model default unchanged"
        ),
    )
    parser.add_argument(
        "--compact-threshold-scope",
        choices=("total", "body_after_prefix"),
        default="body_after_prefix",
        help="scope used by Codex when measuring the compaction threshold",
    )
    parser.add_argument(
        "--max-seconds",
        type=int,
        default=600,
        help="hard wall-clock budget for one Codex session; 0 disables",
    )
    parser.add_argument("--codex", default="codex")
    parser.add_argument(
        "--sandbox",
        choices=("read-only", "workspace-write", "danger-full-access"),
        default="workspace-write",
    )
    parser.add_argument(
        "--approve-for-me",
        action="store_true",
        help="route headless approval requests through Codex automatic review",
    )
    parser.add_argument("--model")
    parser.add_argument("--require-acceptance-receipt", action="store_true", help="require hash-bound test/lint/build/browser/audit/review receipts before component closure")
    parser.add_argument("--profile")
    parser.add_argument(
        "--execute",
        action="store_true",
        help="actually start Codex sessions; otherwise print the plan",
    )
    parser.add_argument(
        "--recover-transient-fail",
        action="store_true",
        help=(
            "append a rotate mark only when the latest FAIL is proven by its "
            "event log to be a provider quota, balance, or auth outage"
        ),
    )
    args = parser.parse_args(argv)
    try:
        args.stages = parse_stages(args.stages)
    except ValueError as error:
        parser.error(str(error))
    if min(args.max_component_sessions, args.max_component_input_tokens, args.max_component_seconds) < 1:
        parser.error('component budgets must be positive')
    if args.max_attempts < 1:
        parser.error("--max-attempts must be at least 1")
    if args.context_target_tokens < 1:
        parser.error("--context-target-tokens must be positive")
    if args.warn_stage_input_tokens < 1:
        parser.error("--warn-stage-input-tokens must be positive")
    if args.max_actions is not None and args.max_actions < 1:
        parser.error("--max-actions must be positive")
    if args.compact_threshold < 0:
        parser.error("--compact-threshold cannot be negative")
    if args.max_seconds < 0:
        parser.error("--max-seconds cannot be negative")
    args.component_name = args.component_name or args.component
    args.project_root = args.project_root.resolve()
    args.run_dir = args.project_root / ".agent-runs" / args.component
    return args


def main(argv: Sequence[str] | None = None) -> int:
    args = parse_args(argv)
    project_root = args.project_root
    plan_path = project_root / "docs/shadcn-web-plan.md"
    skill_path = (
        project_root
        / ".agents/skills/token-efficient-compose-workflow/SKILL.md"
    )
    progress_path = args.run_dir / "progress.md"
    try:
        verify_prerequisites(
            project_root=project_root,
            plan_path=plan_path,
            skill_path=skill_path,
            run_dir=args.run_dir,
        )
    except RuntimeError as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    if not progress_path.is_file():
        print(f"ERROR: missing progress handoff: {progress_path}", file=sys.stderr)
        return 1

    if args.recover_transient_fail:
        if not recover_transient_failure(args.run_dir):
            print(
                "ERROR: latest FAIL is not a proven transient provider outage",
                file=sys.stderr,
            )
            return 1

    bootstrap_tao_contract(args.run_dir)
    selected = tuple(args.stages)
    if args.start_stage == "auto":
        try:
            start_stage = infer_start_stage(args.run_dir)
        except RuntimeError as error:
            print(f"ERROR: {error}", file=sys.stderr)
            return 2
        if start_stage is None:
            if checklist_accepted(project_root, args.component) and (
                    not args.require_acceptance_receipt or acceptance_problem(project_root, args.run_dir) is None):
                print(f"COMPONENT ACCEPTED: {progress_path}")
                return 0
            if args.require_acceptance_receipt and checklist_accepted(project_root, args.component) and args.execute:
                update_component_checkbox(project_root, args.component, False)
            start_stage = pending_resume_stage(args.run_dir)
            if args.execute:
                reopen_checkpoint(args.run_dir, start_stage,
                                  "Handoff finished but canonical checklist is open; close residual implementation and revalidate changed inputs.")
    else:
        start_stage = args.start_stage
    try:
        args.stages = stages_from(start_stage, selected)
    except ValueError as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    if not args.execute:
        return dry_run(
            args=args,
            project_root=project_root,
            plan_path=plan_path,
            skill_path=skill_path,
            progress_path=progress_path,
        )

    state_path = args.run_dir / "orchestrator" / "runs.json"
    for stage in args.stages:
        try:
            stage_status = execute_stage(
                args=args,
                project_root=project_root,
                plan_path=plan_path,
                skill_path=skill_path,
                progress_path=progress_path,
                stage=stage,
                state_path=state_path,
            )
        except (OSError, RuntimeError, ValueError, TypeError, KeyError) as error:
            print(f"ERROR: {error}", file=sys.stderr)
            return 1
        if stage_status == 3:
            print(
                f"INFRASTRUCTURE BLOCKED at {stage}; inspect {state_path}",
                file=sys.stderr,
            )
            return 3
        if stage_status == 4:
            print(f"HANDOFF COMPLETE, ACCEPTANCE PENDING: {progress_path}")
            return 4
        if stage_status == 6:
            print(f'COMPONENT BUDGET EXHAUSTED: {progress_path}')
            return 6
        if stage_status == 5:
            print(f"VERIFICATION CHECKPOINT PENDING: {progress_path}")
            return 5
        if stage_status != 0:
            print(
                f"STOPPED at {stage}; inspect progress.md and {state_path}",
                file=sys.stderr,
            )
            return 2
    print(f"ALL STAGES COMPLETE: {', '.join(args.stages)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
