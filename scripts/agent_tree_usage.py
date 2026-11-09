#!/usr/bin/env python3
"""Read local Codex usage for a root and its explicitly linked descendants.

No model calls. Cached input is part of input, and reasoning is part of output.
Coordination attribution means usage immediately following a coordination tool;
it is diagnostic attribution, not a measurement of avoidable cost.
"""
from __future__ import annotations

import argparse
import json
import os
from collections import Counter
from pathlib import Path

COORDINATION = {"wait_agent", "send_message", "list_agents", "spawn_agent", "followup_task"}
FIELDS = ("input_tokens", "cached_input_tokens", "output_tokens", "total_tokens")


def events(path: Path):
    with path.open(encoding="utf-8", errors="replace") as handle:
        for line in handle:
            try:
                yield json.loads(line)
            except json.JSONDecodeError:
                continue


def session_usage(path: Path) -> dict:
    latest = {}
    attributed = Counter()
    calls = Counter()
    previous = None
    requests = 0
    for event in events(path):
        payload = event.get("payload", {})
        if event.get("type") == "response_item" and payload.get("type") in ("function_call", "custom_tool_call"):
            previous = payload.get("name")
            calls[previous] += 1
        if event.get("type") == "token_usage_record":
            requests += 1
            if previous in COORDINATION:
                attributed.update({key: payload.get("usage", {}).get(key, 0) for key in FIELDS})
            counters = payload.get("thread_token_usage")
            if counters and counters.get("total_tokens", 0) >= latest.get("total_tokens", 0):
                latest = counters
        if event.get("type") == "event_msg" and payload.get("type") == "token_count":
            counters = (payload.get("info") or {}).get("total_token_usage")
            if counters and counters.get("total_tokens", 0) >= latest.get("total_tokens", 0):
                latest = counters
    usage = {key: latest.get(key, 0) for key in FIELDS}
    usage["uncached_input_tokens"] = usage["input_tokens"] - usage["cached_input_tokens"]
    return {"usage": usage, "usage_available": bool(latest), "recorded_requests": requests,
            "coordination_calls": {key: calls[key] for key in sorted(COORDINATION) if calls[key]},
            "input_after_coordination": attributed["input_tokens"]}


def report(root_id: str, roots: list[Path], details: bool = False) -> dict:
    sessions = {}
    for directory in roots:
        for path in directory.rglob("*.jsonl"):
            metadata = next(events(path), {})
            if metadata.get("type") != "session_meta":
                continue
            data = metadata.get("payload", {})
            source = data.get("source", {})
            spawn = source.get("subagent", {}).get("thread_spawn", {}) if isinstance(source, dict) else {}
            if data.get("id"):
                sessions[data["id"]] = (path, spawn.get("parent_thread_id"), spawn.get("agent_path"))
    if root_id not in sessions:
        raise ValueError(f"root session not found: {root_id}")
    selected = {root_id}
    while True:
        children = {key for key, (_, parent, _) in sessions.items() if parent in selected}
        if children <= selected:
            break
        selected.update(children)
    total = Counter()
    rows = []
    for session_id in sorted(selected):
        path, parent, agent = sessions[session_id]
        row = session_usage(path)
        total.update(row["usage"])
        rows.append({"id": session_id, "parent": parent, "agent": agent, **row})
    coordination = sum(row["input_after_coordination"] for row in rows)
    result = {"root": root_id, "sessions": len(rows), "usage": dict(total),
              "missing_usage_sessions": sum(not row["usage_available"] for row in rows),
              "recorded_requests": sum(row["recorded_requests"] for row in rows),
              "input_after_coordination": coordination,
              "coordination_attribution": "request usage after coordination tools; not estimated savings",
              "coverage": "local explicit parent links only; auxiliary and unlinked sessions excluded"}
    if details:
        result["details"] = sorted(rows, key=lambda row: row["usage"]["input_tokens"], reverse=True)
    return result


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--thread", required=True)
    parser.add_argument("--sessions-root", action="append", type=Path)
    parser.add_argument("--details", action="store_true")
    args = parser.parse_args()
    home = Path(os.environ.get("CODEX_HOME", str(Path.home() / ".codex")))
    try:
        result = report(args.thread, args.sessions_root or [home / "sessions", home / "archived_sessions"], args.details)
    except ValueError as error:
        parser.error(str(error))
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
