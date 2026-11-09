#!/usr/bin/env python3
"""Audit agent token usage, fixed context, duplicate reads, and routing."""

from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
from collections import Counter
from pathlib import Path
from typing import Any, Iterator
from urllib.parse import unquote, urlparse


READ_EXTENSIONS = (
    ".kt",
    ".kts",
    ".java",
    ".md",
    ".json",
    ".jsonl",
    ".toml",
    ".yaml",
    ".yml",
    ".xml",
    ".py",
    ".sh",
    ".js",
    ".ts",
    ".tsx",
    ".properties",
    ".gradle",
)
READ_COMMAND = re.compile(
    r"(?:^|[\s|;&])(cat|sed|head|tail|nl|rg|jq|wc|stat|file)\s"
)
PATH_PATTERN = re.compile(
    r"(?<![\w./-])([~]?[\w./-]+(?:"
    + "|".join(re.escape(ext) for ext in READ_EXTENSIONS)
    + r"))(?![\w./-])"
)
INSTRUCTION_FILES = (
    "AGENTS.md",
    "CLAUDE.md",
    ".claude/AGENTS.md",
    ".claude/settings.json",
    ".codex/config.toml",
    ".mcp.json",
)
SKILL_GLOBS = (
    ".claude/skills/*/SKILL.md",
    ".agents/skills/*/SKILL.md",
    ".codex/skills/*/SKILL.md",
)


def iter_json_lines(path: Path) -> Iterator[dict[str, Any]]:
    if path.suffix == ".zstd":
        executable = shutil.which("zstdcat") or shutil.which("zstd")
        if not executable:
            raise RuntimeError("zstdcat or zstd is required")
        command = [executable] if executable.endswith("zstdcat") else [executable, "-dc"]
        process = subprocess.Popen(
            [*command, str(path)],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
        )
        assert process.stdout is not None
        for line in process.stdout:
            if line.strip():
                yield json.loads(line)
        stderr = process.stderr.read() if process.stderr else ""
        if process.wait():
            raise RuntimeError(stderr.strip())
        return

    with path.open(encoding="utf-8") as handle:
        for line in handle:
            if line.strip():
                yield json.loads(line)


def latest_codex_session() -> Path:
    codex_home = Path(os.environ.get("CODEX_HOME", Path.home() / ".codex"))
    sessions = codex_home / "sessions"
    candidates = sorted(
        sessions.rglob("*.jsonl"),
        key=lambda path: path.stat().st_mtime_ns,
        reverse=True,
    )
    if not candidates:
        raise FileNotFoundError(f"No Codex sessions found under {sessions}")
    return candidates[0]


def approximate_tokens(byte_count: int) -> int:
    return round(byte_count / 4)


def text_size(value: Any) -> int:
    if isinstance(value, str):
        return len(value.encode("utf-8"))
    if isinstance(value, list):
        return sum(text_size(item) for item in value)
    if not isinstance(value, dict):
        return 0
    if value.get("type") == "image":
        return 0
    if isinstance(value.get("text"), str):
        return text_size(value["text"])
    return sum(text_size(item) for item in value.values())


def command_string(command: Any) -> str:
    if isinstance(command, list):
        return " ".join(str(part) for part in command)
    return str(command or "")


def command_cwd(item: dict[str, Any], fallback: Path) -> Path:
    raw = str(item.get("cwd") or "")
    if raw.startswith("file://"):
        return Path(unquote(urlparse(raw).path))
    return Path(raw) if raw else fallback


def normalize_path(raw: str, cwd: Path, root: Path) -> str:
    path = Path(raw).expanduser()
    if not path.is_absolute():
        path = cwd / path
    try:
        return str(path.resolve().relative_to(root.resolve()))
    except ValueError:
        return str(path.resolve())


def read_paths(command: Any, cwd: Path, root: Path) -> list[str]:
    text = command_string(command)
    if "apply_patch" in text or not READ_COMMAND.search(text):
        return []
    paths: list[str] = []
    for match in PATH_PATTERN.finditer(text):
        raw = match.group(1).strip("'\"")
        if raw.startswith(("http://", "https://")):
            continue
        normalized = normalize_path(raw, cwd, root)
        if normalized not in paths:
            paths.append(normalized)
    return paths


def codex_audit(path: Path, root: Path) -> dict[str, Any]:
    token_events: list[dict[str, Any]] = []
    outputs: list[dict[str, Any]] = []
    reads: Counter[str] = Counter()
    read_evidence: dict[str, list[int]] = {}
    call_index = 0

    for event in iter_json_lines(path):
        if event.get("type") != "event_msg":
            continue
        payload = event.get("payload") or {}
        if payload.get("type") == "token_count":
            token_events.append(payload.get("info") or {})
            continue
        if payload.get("type") != "item_completed":
            continue
        item = payload.get("item") or {}
        item_type = item.get("type")
        output_bytes = 0
        label = ""
        if item_type == "CommandExecution":
            call_index += 1
            output_bytes = text_size(item.get("aggregated_output"))
            command = item.get("command")
            label = command_string(command)[:180]
            cwd = command_cwd(item, root)
            for candidate in read_paths(command, cwd, root):
                reads[candidate] += 1
                read_evidence.setdefault(candidate, []).append(call_index)
        elif item_type == "McpToolCall":
            call_index += 1
            result = item.get("result") or {}
            output_bytes = text_size(result.get("content") or result)
            label = f"{item.get('server', '')}:{item.get('tool', '')}"[:180]
        else:
            continue
        if output_bytes:
            outputs.append(
                {
                    "index": call_index,
                    "type": item_type,
                    "label": label,
                    "bytes": output_bytes,
                    "approx_tokens": approximate_tokens(output_bytes),
                }
            )

    last_usage = (token_events[-1].get("total_token_usage") if token_events else {}) or {}
    first_last = (token_events[0].get("last_token_usage") if token_events else {}) or {}
    prompt_sizes = [
        int((event.get("last_token_usage") or {}).get("input_tokens") or 0)
        for event in token_events
    ]
    input_tokens = int(last_usage.get("input_tokens") or 0)
    cached = int(last_usage.get("cached_input_tokens") or 0)
    output = int(last_usage.get("output_tokens") or 0)
    context_window = int(
        (token_events[-1] if token_events else {}).get("model_context_window") or 0
    )
    duplicates = [
        {
            "path": candidate,
            "reads": count,
            "calls": read_evidence[candidate],
        }
        for candidate, count in reads.most_common()
        if count > 1
    ]
    return {
        "session": str(path),
        "usage": {
            "calls": len(token_events),
            "input_tokens": input_tokens,
            "cached_input_tokens": cached,
            "uncached_input_tokens": max(0, input_tokens - cached),
            "output_tokens": output,
            "raw_tokens": input_tokens + output,
            "efficient_tokens": max(0, input_tokens - cached) + output,
            "cache_hit_pct": round(cached / input_tokens * 100, 3)
            if input_tokens
            else 0.0,
            "first_prompt_tokens": int(first_last.get("input_tokens") or 0),
            "max_prompt_tokens": max(prompt_sizes, default=0),
            "context_window": context_window,
            "max_utilization_pct": round(
                max(prompt_sizes, default=0) / context_window * 100, 3
            )
            if context_window
            else 0.0,
        },
        "tool_output": {
            "calls": len(outputs),
            "bytes": sum(item["bytes"] for item in outputs),
            "approx_tokens": sum(item["approx_tokens"] for item in outputs),
            "largest": sorted(
                outputs, key=lambda item: item["bytes"], reverse=True
            )[:12],
        },
        "duplicate_reads": duplicates[:20],
    }


def visible_context_files(root: Path) -> list[Path]:
    files: list[Path] = []
    for relative in INSTRUCTION_FILES:
        candidate = root / relative
        if candidate.is_file():
            files.append(candidate)
    for pattern in SKILL_GLOBS:
        files.extend(path for path in root.glob(pattern) if path.is_file())
    unique: dict[str, Path] = {}
    for path in files:
        resolved = path.resolve()
        unique[str(resolved)] = resolved
    return sorted(unique.values())


def fixed_context_audit(root: Path) -> dict[str, Any]:
    rows = []
    for path in visible_context_files(root):
        data = path.read_bytes()
        rows.append(
            {
                "path": str(path.resolve().relative_to(root.resolve())),
                "bytes": len(data),
                "lines": data.count(b"\n")
                + (1 if data and not data.endswith(b"\n") else 0),
                "approx_tokens": approximate_tokens(len(data)),
            }
        )
    rows.sort(key=lambda row: row["bytes"], reverse=True)
    return {
        "repo_visible_files": len(rows),
        "bytes": sum(row["bytes"] for row in rows),
        "approx_tokens": sum(row["approx_tokens"] for row in rows),
        "top_files": rows[:20],
    }


def dsh_audit(root: Path) -> dict[str, Any]:
    project_candidates = [root]
    if root.name == "compose-foundation" or (root / "compose-foundation").is_dir():
        project_candidates.append(root.parent if root.name == "compose-foundation" else root)
    reports: list[tuple[Path, dict[str, Any]]] = []
    missing: list[str] = []
    for project in project_candidates:
        helper = project / "scripts/token_efficiency.py"
        if not helper.is_file():
            nested = project / "compose-foundation/scripts/token_efficiency.py"
            helper = nested if nested.is_file() else helper
        if not helper.is_file():
            missing.append(str(helper))
            continue
        report = run_dsh_report(helper, project)
        if report.get("available"):
            reports.append((project, report))
    if reports:
        return sorted(
            reports,
            key=lambda row: int(row[1].get("calls") or 0),
            reverse=True,
        )[0][1]
    reason = "; ".join(sorted(set(missing))) or "no DSH report available"
    return {"available": False, "reason": reason}


def run_dsh_report(helper: Path, project: Path) -> dict[str, Any]:
    process = subprocess.run(
        [
            sys.executable,
            str(helper),
            "report",
            "--cwd",
            str(project),
            "--json",
        ],
        cwd=project,
        text=True,
        capture_output=True,
        check=False,
    )
    if process.returncode:
        return {
            "available": False,
            "reason": process.stderr.strip() or f"exit {process.returncode}",
        }
    report = json.loads(process.stdout)
    keys = (
        "calls",
        "raw_tokens",
        "efficient_tokens",
        "cache_hit_pct",
        "prompt_median",
        "prompt_p95",
        "tool_context_share_pct",
        "caps",
    )
    return {"available": True, **{key: report.get(key) for key in keys}}


def route_task(task: str) -> dict[str, Any]:
    text = task.lower()
    def contains(term: str) -> bool:
        return re.search(rf"(?<!\w){re.escape(term)}(?!\w)", text) is not None

    evidence_terms = (
        "find",
        "search",
        "explore",
        "investigate",
        "locate",
        "inventory",
        "map",
        "research",
        "summarize",
        "read",
        "reads",
        "reading",
    )
    execution_terms = (
        "test",
        "tests",
        "testing",
        "tested",
        "build",
        "lint",
        "typecheck",
        "verify",
        "reproduce",
        "failing",
        "ci",
        "log",
    )
    decision_terms = (
        "design",
        "architecture",
        "security",
        "public api",
        "ambiguous",
        "decide",
        "implement",
        "refactor",
    )
    routes = []
    if any(contains(term) for term in evidence_terms):
        routes.append(
            {
                "route": "scout",
                "reason": "read-only discovery can be isolated",
                "return_limit": "30 lines with path:line evidence",
            }
        )
    if any(contains(term) for term in execution_terms):
        routes.append(
            {
                "route": "runner",
                "reason": "deterministic command execution can be isolated",
                "return_limit": "40 lines with exit codes and log path",
            }
        )
    if any(contains(term) for term in decision_terms) or not routes:
        routes.append(
            {
                "route": "main",
                "reason": "requires judgment or owns the final outcome",
                "return_limit": "full acceptance evidence",
            }
        )
    return {
        "task": task,
        "routes": routes,
        "warning": "Measure total subagent startup plus returned tokens; "
        "one-file reads do not need delegation.",
    }


def render_audit(payload: dict[str, Any]) -> None:
    usage = payload.get("codex", {}).get("usage")
    if usage:
        print(
            "Codex: "
            f"calls={usage['calls']:,} raw={usage['raw_tokens']:,} "
            f"efficient={usage['efficient_tokens']:,} "
            f"cache={usage['cache_hit_pct']:.3f}% "
            f"first_prompt={usage['first_prompt_tokens']:,} "
            f"max_prompt={usage['max_prompt_tokens']:,}"
        )
    fixed = payload["fixed_context"]
    print(
        "Repo-visible instructions: "
        f"files={fixed['repo_visible_files']} bytes={fixed['bytes']:,} "
        f"approx_tokens={fixed['approx_tokens']:,}"
    )
    for row in fixed["top_files"][:8]:
        print(f"  {row['approx_tokens']:>7,} ~tokens  {row['path']}")
    codex = payload.get("codex")
    if codex:
        output = codex["tool_output"]
        print(
            "Tool output: "
            f"calls={output['calls']} bytes={output['bytes']:,} "
            f"approx_tokens={output['approx_tokens']:,}"
        )
        for row in output["largest"][:5]:
            print(f"  {row['approx_tokens']:>7,} ~tokens  {row['label']}")
        duplicates = codex["duplicate_reads"]
        print(f"Possible duplicate reads: {len(duplicates)}")
        for row in duplicates[:8]:
            print(f"  reads={row['reads']} calls={row['calls']} {row['path']}")
    dsh = payload.get("dsh")
    if dsh:
        if dsh.get("available"):
            print(
                "DSH: "
                f"calls={dsh['calls']:,} raw={dsh['raw_tokens']:,} "
                f"efficient={dsh['efficient_tokens']:,} "
                f"cache={dsh['cache_hit_pct']:.3f}%"
            )
        else:
            print(f"DSH unavailable: {dsh['reason']}")


def run_audit(args: argparse.Namespace) -> int:
    root = args.root.resolve()
    payload: dict[str, Any] = {"root": str(root)}
    session = latest_codex_session() if args.codex_session == "latest" else args.codex_session
    if session:
        payload["codex"] = codex_audit(Path(session).expanduser().resolve(), root)
    if args.dsh_project:
        payload["dsh"] = dsh_audit(args.dsh_project.resolve())
    payload["fixed_context"] = fixed_context_audit(root)
    if args.json:
        print(json.dumps(payload, indent=2, sort_keys=True))
    else:
        render_audit(payload)
    return 0


def _run_route(args: argparse.Namespace) -> int:
    payload = route_task(args.task)
    if args.json:
        print(json.dumps(payload, indent=2, sort_keys=True))
    else:
        for route in payload["routes"]:
            print(f"{route['route']}: {route['reason']} ({route['return_limit']})")
        print(f"WARN: {payload['warning']}")
    return 0


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)

    audit = commands.add_parser("audit", help="audit usage, context, and duplicate reads")
    audit.add_argument("--root", type=Path, default=Path.cwd())
    audit.add_argument("--codex-session", help="Codex JSONL path or 'latest'")
    audit.add_argument("--dsh-project", type=Path)
    audit.add_argument("--json", action="store_true")
    audit.set_defaults(handler=run_audit)

    route = commands.add_parser("route", help="choose scout, runner, or main-agent routing")
    route.add_argument("--task", required=True)
    route.add_argument("--json", action="store_true")
    route.set_defaults(handler=_run_route)
    return parser


def main() -> int:
    args = build_parser().parse_args()
    return args.handler(args)


if __name__ == "__main__":
    raise SystemExit(main())
