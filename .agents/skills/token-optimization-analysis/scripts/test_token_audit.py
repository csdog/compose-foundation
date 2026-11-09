#!/usr/bin/env python3

from __future__ import annotations

import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path


AUDIT = Path(__file__).with_name("token_audit.py")


class TokenAuditTest(unittest.TestCase):
    def test_audit_extracts_usage_reads_outputs_and_fixed_context(self) -> None:
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            (root / "src").mkdir()
            (root / "src/App.kt").write_text("class App\n", encoding="utf-8")
            (root / "AGENTS.md").write_text(
                "Keep evidence paths.\n", encoding="utf-8"
            )
            skill = root / ".claude/skills/demo/SKILL.md"
            skill.parent.mkdir(parents=True)
            skill.write_text("---\nname: demo\n---\nDemo.\n", encoding="utf-8")
            session = root / "session.jsonl"
            events = [
                {
                    "type": "event_msg",
                    "payload": {
                        "type": "token_count",
                        "info": {
                            "model_context_window": 1000,
                            "total_token_usage": {
                                "input_tokens": 100,
                                "cached_input_tokens": 80,
                                "output_tokens": 10,
                            },
                            "last_token_usage": {
                                "input_tokens": 100,
                                "cached_input_tokens": 80,
                                "output_tokens": 10,
                            },
                        },
                    },
                },
                {
                    "type": "event_msg",
                    "payload": {
                        "type": "item_completed",
                        "item": {
                            "type": "CommandExecution",
                            "cwd": root.as_uri(),
                            "command": ["/bin/zsh", "-lc", "cat src/App.kt"],
                            "aggregated_output": "class App\n",
                        },
                    },
                },
                {
                    "type": "event_msg",
                    "payload": {
                        "type": "item_completed",
                        "item": {
                            "type": "CommandExecution",
                            "cwd": root.as_uri(),
                            "command": [
                                "/bin/zsh",
                                "-lc",
                                "sed -n '1,5p' src/App.kt",
                            ],
                            "aggregated_output": "class App\n",
                        },
                    },
                },
                {
                    "type": "event_msg",
                    "payload": {
                        "type": "token_count",
                        "info": {
                            "model_context_window": 1000,
                            "total_token_usage": {
                                "input_tokens": 300,
                                "cached_input_tokens": 250,
                                "output_tokens": 20,
                            },
                            "last_token_usage": {
                                "input_tokens": 200,
                                "cached_input_tokens": 170,
                                "output_tokens": 10,
                            },
                        },
                    },
                },
            ]
            session.write_text(
                "\n".join(json.dumps(event) for event in events) + "\n",
                encoding="utf-8",
            )
            process = subprocess.run(
                [
                    sys.executable,
                    str(AUDIT),
                    "audit",
                    "--root",
                    str(root),
                    "--codex-session",
                    str(session),
                    "--json",
                ],
                text=True,
                capture_output=True,
                check=False,
            )
            self.assertEqual(0, process.returncode, process.stderr)
            payload = json.loads(process.stdout)
            usage = payload["codex"]["usage"]
            self.assertEqual(2, usage["calls"])
            self.assertEqual(320, usage["raw_tokens"])
            self.assertEqual(70, usage["efficient_tokens"])
            self.assertEqual(83.3, round(usage["cache_hit_pct"], 1))
            self.assertEqual(100, usage["first_prompt_tokens"])
            self.assertEqual(200, usage["max_prompt_tokens"])
            self.assertEqual(
                "src/App.kt", payload["codex"]["duplicate_reads"][0]["path"]
            )
            self.assertEqual(2, payload["codex"]["duplicate_reads"][0]["reads"])
            self.assertGreaterEqual(
                payload["fixed_context"]["repo_visible_files"], 2
            )

    def test_route_separates_scout_runner_and_main(self) -> None:
        process = subprocess.run(
            [
                sys.executable,
                str(AUDIT),
                "route",
                "--task",
                "Search the codebase, run the failing tests, then implement the fix.",
                "--json",
            ],
            text=True,
            capture_output=True,
            check=False,
        )
        self.assertEqual(0, process.returncode, process.stderr)
        routes = [row["route"] for row in json.loads(process.stdout)["routes"]]
        self.assertEqual(["scout", "runner", "main"], routes)

    def test_route_uses_word_boundaries(self) -> None:
        process = subprocess.run(
            [
                sys.executable,
                str(AUDIT),
                "route",
                "--task",
                "Summarize the latest progress.",
                "--json",
            ],
            text=True,
            capture_output=True,
            check=False,
        )
        self.assertEqual(0, process.returncode, process.stderr)
        routes = [row["route"] for row in json.loads(process.stdout)["routes"]]
        self.assertEqual(["scout"], routes)


if __name__ == "__main__":
    unittest.main()
