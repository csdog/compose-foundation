#!/usr/bin/env python3
"""Focused tests for the token-efficiency workflow helpers."""

from __future__ import annotations

import importlib.util
import json
import sys
import tempfile
import unittest
from pathlib import Path


SCRIPT = Path(__file__).with_name("token_efficiency.py")
SPEC = importlib.util.spec_from_file_location("token_efficiency", SCRIPT)
assert SPEC and SPEC.loader
token_efficiency = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = token_efficiency
SPEC.loader.exec_module(token_efficiency)


class UsageTests(unittest.TestCase):
    def test_usage_filter_skips_inherited_events(self) -> None:
        events = [
            {
                "type": "assistant/message",
                "seq": 5,
                "time": 1,
                "data": {
                    "usage": {
                        "inputTokens": 100,
                        "cacheReadTokens": 1000,
                        "outputTokens": 10,
                    }
                },
            },
            {
                "type": "assistant/message",
                "seq": 9,
                "time": 2,
                "data": {
                    "usage": {
                        "inputTokens": 20,
                        "cacheReadTokens": 300,
                        "outputTokens": 4,
                    }
                },
            },
            {"type": "compaction/prune", "seq": 10, "data": {}},
        ]
        calls, prunes = token_efficiency.usage_from_events(events, 5)
        self.assertEqual(1, len(calls))
        self.assertEqual(324, calls[0].total_tokens)
        self.assertEqual(1, prunes)

    def test_run_validation_requires_handoff_sections(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            run_dir = Path(tmp)
            token_efficiency.init_run(run_dir, "chip", "Finish chip parity", False)
            self.assertEqual([], token_efficiency.validate_progress(run_dir, 150))

            progress = run_dir / "progress.md"
            progress.write_text("# incomplete\n", encoding="utf-8")
            errors = token_efficiency.validate_progress(run_dir, 150)
            self.assertGreaterEqual(len(errors), len(token_efficiency.REQUIRED_PROGRESS_SECTIONS))

    def test_session_payload_includes_derived_metrics(self) -> None:
        session = token_efficiency.SessionMetrics(
            session_id="session-test",
            title="Test",
            created_ms=0,
            inherited_events=0,
        )
        session.calls = 1
        session.input_tokens = 20
        session.cache_read_tokens = 300
        session.output_tokens = 4
        session.prompt_sizes = [320]
        payload = token_efficiency.session_payload(session)
        self.assertEqual(324, payload["raw_tokens"])
        self.assertEqual(24, payload["efficient_tokens"])
        self.assertEqual(320, payload["median_prompt"])

    def test_session_budget_warning(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            dsh_home = Path(tmp)
            projection_dir = (
                dsh_home / "storages/session_projcache/sessions"
            )
            projection_dir.mkdir(parents=True)
            projection = projection_dir / "session-test.json"
            projection.write_text(
                json.dumps(
                    {
                        "record": {
                            "rows": {
                                "contextPressure": {
                                    "val": {
                                        "surfaceTokens": 800000,
                                        "contextWindow": 1000000,
                                    }
                                },
                                "contextTimeline": {
                                    "val": {
                                        "surface": [
                                            {"cat": "tool", "tokens": 600000},
                                            {"cat": "assistant", "tokens": 200000},
                                        ]
                                    }
                                },
                                "tokenUsage": {
                                    "val": {
                                        "last": {
                                            "buckets": {
                                                "uncachedInputTokens": 1000,
                                                "cacheReadTokens": 400000,
                                            }
                                        }
                                    }
                                },
                            }
                        }
                    }
                ),
                encoding="utf-8",
            )
            self.assertEqual(
                2,
                token_efficiency.check_session(
                    dsh_home, "session-test", 300000, 0.50
                ),
            )


if __name__ == "__main__":
    unittest.main()
