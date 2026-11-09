#!/usr/bin/env python3
"""Focused tests for the Codex Stage orchestrator."""

from __future__ import annotations

import importlib.util
import json
import sys
import tempfile
import unittest
from unittest.mock import patch
from pathlib import Path


SCRIPT = Path(__file__).with_name("codex_stage_orchestrator.py")
SPEC = importlib.util.spec_from_file_location(
    "codex_stage_orchestrator", SCRIPT
)
assert SPEC and SPEC.loader
orchestrator = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = orchestrator
SPEC.loader.exec_module(orchestrator)


class StageSelectionTests(unittest.TestCase):
    def test_default_action_budget_allows_one_browser_unit(self) -> None:
        args = orchestrator.parse_args(["--component", "skeleton"])
        self.assertIsNone(args.max_actions)
        self.assertEqual(6, args.max_attempts)
        self.assertEqual(600, args.max_seconds)
        self.assertEqual(30, orchestrator.STAGE_ACTION_BUDGETS["10-baseline"])
        self.assertEqual(60, orchestrator.STAGE_ACTION_BUDGETS["30-verify"])

    def test_stage_list_and_start_stage(self) -> None:
        stages = orchestrator.parse_stages(
            "00-scope,10-baseline,20-implement"
        )
        self.assertEqual(
            ("10-baseline", "20-implement"),
            orchestrator.stages_from("10-baseline", stages),
        )

    def test_stage_order_is_enforced(self) -> None:
        with self.assertRaises(ValueError):
            orchestrator.parse_stages("20-implement,10-baseline")

    def test_next_stage(self) -> None:
        self.assertEqual("30-verify", orchestrator.next_stage("20-implement"))
        self.assertIsNone(orchestrator.next_stage("40-handoff"))


class BlockerProtocolTests(unittest.TestCase):
    def test_external_blocker_is_not_rotation(self):
        decision = orchestrator.classify_outcome("30-verify", 0, "Shared lint needs scheduler repair\n\nSTAGE_BLOCKED 30-verify", "")
        self.assertEqual("blocked", decision.status)
        self.assertEqual("Shared lint needs scheduler repair", decision.reason)

    def test_verification_progress_uses_next_unit_not_appended_notes(self):
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / "progress.md"
            path.write_text("## Next\n1. Resolve `30a-tests` lint gate.\n## Notes\n30b-build later\n")
            self.assertEqual("30a-tests", orchestrator.next_verification_unit(path))
            path.write_text("## Next\n1. Run `30b-build`.\n")
            self.assertEqual("30b-build", orchestrator.next_verification_unit(path))

    def test_repeated_handoffs_stop_before_six_sessions(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / ".agent-runs/chart"
            run.mkdir(parents=True)
            (run / "orchestrator").mkdir()
            card = root / ".agents/skills/token-efficient-compose-workflow/cards/30-verify.md"
            card.parent.mkdir(parents=True)
            card.write_text("Verify")
            progress = run / "progress.md"
            progress.write_text("## Next\n1. Fix 30a-tests shared lint.\n")
            event = root / "events.jsonl"
            event.write_text("")
            result = orchestrator.RunResult(0, None, orchestrator.TokenUsage(), 1, False, False,
                                            "CONTEXT_HANDOFF 30-verify", event, root / "last.md")
            args = orchestrator.parse_args(["--component", "chart", "--project-root", str(root)])
            with patch.object(orchestrator, "run_codex", return_value=result) as execute, patch.object(orchestrator, "verify_handoff_or_fail"):
                code = orchestrator.execute_stage(args=args, project_root=root, plan_path=root / "plan", skill_path=root / "skill", progress_path=progress, stage="30-verify", state_path=run / "orchestrator/runs.json")
            self.assertEqual(2, code)
            self.assertEqual(2, execute.call_count)
            records = json.loads((run / "orchestrator/runs.json").read_text())["runs"]
            self.assertEqual("blocked", records[-1]["status"])


class InfrastructureStartupTests(unittest.TestCase):
    def test_permission_startup_failure_stops_as_infrastructure(self):
        error = "Error: failed to initialize in-process app-server client: Operation not permitted (os error 1)"
        result = orchestrator.classify_outcome("00-scope", 1, "", error)
        self.assertEqual("infrastructure_blocked", result.status)
        self.assertIn("Operation not permitted", result.reason)


class UsageRecoveryTests(unittest.TestCase):
    def test_repair_updates_both_ledgers_and_is_idempotent(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            folder = root / ".agent-runs/fake/orchestrator"
            folder.mkdir(parents=True)
            record = {"thread_id": "fake", "event_log": "attempt.jsonl", "actions": 2,
                      "usage": {"input_tokens": 0}}
            path = folder / "runs.json"
            path.write_text(json.dumps({"runs": [record]}))
            loop = folder.parent / "loop_log.jsonl"
            loop.write_text(json.dumps(record) + "\n")
            with patch.object(orchestrator, "recover_session_usage", return_value=orchestrator.TokenUsage(100, 80, 5, 105)):
                self.assertEqual(1, orchestrator.repair_run_usage(root)["recovered"])
                self.assertEqual(0, orchestrator.repair_run_usage(root)["recovered"])
            saved = json.loads(path.read_text())["runs"][0]
            self.assertFalse(saved["usage_complete"])
            self.assertEqual(saved, json.loads(loop.read_text()))

    def test_cumulative_snapshots_are_not_added_and_partial_line_is_ignored(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            folder = root / "2026/11/08"
            folder.mkdir(parents=True)
            thread = "01a1b882-be6c-7e81-b7c6-28408c500189"
            events = [{"type": "event_msg", "payload": {"type": "token_count", "info": {
                "total_token_usage": {"input_tokens": n, "cached_input_tokens": n-10, "output_tokens": 5},
                "last_token_usage": {"input_tokens": 1}}}} for n in (100, 200, 200)]
            (folder / f"rollout-{thread}.jsonl").write_text(
                "".join(json.dumps(e) + "\n" for e in events) + '{"partial":')
            usage = orchestrator.recover_session_usage(thread, root)
            self.assertEqual(200, usage.input_tokens)
            self.assertEqual(190, usage.cached_input_tokens)
            self.assertEqual(205, usage.total_tokens)
            self.assertIsNone(orchestrator.recover_session_usage("../bad", root))


class EventParsingTests(unittest.TestCase):
    def test_extracts_thread_id_and_usage(self) -> None:
        event = {
            "type": "turn.completed",
            "thread_id": "thread-test",
            "payload": {
                "usage": {
                    "input_tokens": 120_000,
                    "cached_input_tokens": 100_000,
                    "output_tokens": 400,
                }
            },
        }
        self.assertEqual("thread-test", orchestrator.extract_thread_id(event))
        self.assertEqual(
            orchestrator.TokenUsage(
                input_tokens=120_000,
                cached_input_tokens=100_000,
                output_tokens=400,
                total_tokens=120_400,
            ),
            orchestrator.extract_usage(event),
        )

    def test_classifies_stage_done(self) -> None:
        decision = orchestrator.classify_outcome(
            "20-implement",
            0,
            "Completed work.\n\nSTAGE_DONE 20-implement\n",
            "",
        )
        self.assertEqual("stage_done", decision.status)

    def test_classifies_handoff(self) -> None:
        decision = orchestrator.classify_outcome(
            "30-verify",
            0,
            "Handoff written.\nCONTEXT_HANDOFF 30-verify\n",
            "",
        )
        self.assertEqual("context_handoff", decision.status)

    def test_classifies_payload_overflow(self) -> None:
        decision = orchestrator.classify_outcome(
            "30-verify",
            1,
            "",
            "unexpected status 413 Payload Too Large",
        )
        self.assertEqual("context_overflow", decision.status)

    def test_classifies_provider_usage_limit_as_infrastructure(self) -> None:
        decision = orchestrator.classify_outcome(
            "00-scope",
            1,
            "",
            "You’ve hit your usage limit. Try again later.",
        )
        self.assertEqual("infrastructure_blocked", decision.status)

    def test_classifies_provider_balance_as_infrastructure(self) -> None:
        decision = orchestrator.classify_outcome(
            "40-handoff",
            1,
            "",
            "402 Payment Required: Insufficient Balance",
        )
        self.assertEqual("infrastructure_blocked", decision.status)

    def test_classifies_action_budget(self) -> None:
        decision = orchestrator.classify_outcome(
            "30-verify",
            0,
            "",
            "",
            budget_terminated=True,
        )
        self.assertEqual("action_budget", decision.status)

    def test_missing_final_marker_is_protocol_error(self) -> None:
        decision = orchestrator.classify_outcome(
            "20-implement",
            0,
            "I finished the work but omitted the marker.",
            "",
        )
        self.assertEqual("protocol_error", decision.status)


class CommandTests(unittest.TestCase):
    def test_verify_card_requires_browser_reuse_and_handoff_buffer(self) -> None:
        card = (
            SCRIPT.parent.parent
            / ".agents/skills/token-efficient-compose-workflow/cards/30-verify.md"
        ).read_text(encoding="utf-8")
        self.assertIn("Reuse an existing current-worktree verifier", card)
        self.assertIn("under 140 lines", card)

    def test_baseline_card_excludes_browser_and_wasm(self) -> None:
        card = (
            SCRIPT.parent.parent
            / ".agents/skills/token-efficient-compose-workflow/cards/10-baseline.md"
        ).read_text(encoding="utf-8")
        self.assertIn("Do not start a Web dev server", card)
        self.assertIn("Those belong to `30-verify`", card)

    def test_stage_prompt_requires_one_work_unit_and_batch_script(self) -> None:
        prompt = orchestrator.build_stage_prompt(
            project_root=Path("/repo"),
            plan_path=Path("docs/plan.md"),
            skill_path=Path(".agents/skill/SKILL.md"),
            card_path=Path("cards/30-verify.md"),
            progress_path=Path(".agent-runs/test/progress.md"),
            component="test",
            component_name="Test",
            stage="30-verify",
            context_target_tokens=250_000,
            max_actions=40,
            max_seconds=600,
            attempt=1,
            progress_tail="20-implement ok",
            commands_tail="RUN focused tests",
        )
        self.assertIn("execute at most one unchecked unit", prompt)
        self.assertIn("one deterministic batch script", prompt)
        self.assertIn("Do not simulate or request PTC/run_code", prompt)

    def test_builds_headless_json_command(self) -> None:
        command = orchestrator.build_codex_command(
            codex_bin="codex",
            project_root=Path("/repo"),
            message_log=Path("/repo/.agent-runs/x/last.md"),
            sandbox="workspace-write",
            approve_for_me=True,
            model="test-model",
            profile="test-profile",
            compact_threshold=250_000,
            compact_threshold_scope="body_after_prefix",
            prompt="prompt",
        )
        self.assertEqual("exec", command[1])
        self.assertIn("--json", command)
        self.assertIn("--approve-for-me", command)
        self.assertNotIn("--sandbox", command)
        self.assertIn("stage_orchestrator", command)
        self.assertIn("model_auto_compact_token_limit=250000", command)
        self.assertIn(
            'model_auto_compact_token_limit_scope="body_after_prefix"',
            command,
        )
        self.assertEqual("prompt", command[-1])

    def test_explicit_sandbox_without_auto_review(self) -> None:
        command = orchestrator.build_codex_command(
            codex_bin="codex",
            project_root=Path("/repo"),
            message_log=Path("/repo/.agent-runs/x/last.md"),
            sandbox="read-only",
            approve_for_me=False,
            model=None,
            profile=None,
            compact_threshold=0,
            compact_threshold_scope="total",
            prompt="prompt",
        )
        self.assertIn("--sandbox", command)
        self.assertEqual(
            "read-only",
            command[command.index("--sandbox") + 1],
        )
        self.assertNotIn("--approve-for-me", command)
        self.assertFalse(
            any(
                str(value).startswith("model_auto_compact_token_limit=")
                for value in command
            )
        )

    def test_run_codex_parses_fake_json_stream(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            event_log = root / "events.jsonl"
            message_log = root / "last.md"
            worker = """
import json
import pathlib
import sys

print(json.dumps({
    "type": "thread.started",
    "thread_id": "thread-fake",
}))
print(json.dumps({
    "type": "turn.completed",
    "usage": {
        "input_tokens": 12345,
        "cached_input_tokens": 12000,
        "output_tokens": 67,
    },
}))
pathlib.Path(sys.argv[1]).write_text(
    "STAGE_DONE 10-baseline\\n",
    encoding="utf-8",
)
"""
            result = orchestrator.run_codex(
                [sys.executable, "-c", worker, str(message_log)],
                project_root=root,
                event_log=event_log,
                message_log=message_log,
                max_actions=100,
                max_seconds=10,
            )
            self.assertEqual(0, result.returncode)
            self.assertEqual("thread-fake", result.thread_id)
            self.assertEqual(12345, result.usage.prompt_tokens)
            self.assertEqual(67, result.usage.output_tokens)
            self.assertEqual(
                "STAGE_DONE 10-baseline",
                orchestrator.last_nonempty_line(result.final_message),
            )

    def test_action_budget_terminates_fake_session(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            event_log = root / "events.jsonl"
            message_log = root / "last.md"
            worker = """
import json
import time

print(json.dumps({
    "type": "item.started",
    "item": {"id": "item-1", "type": "command_execution"},
}), flush=True)
time.sleep(30)
"""
            result = orchestrator.run_codex(
                [sys.executable, "-c", worker],
                project_root=root,
                event_log=event_log,
                message_log=message_log,
                max_actions=1,
                max_seconds=10,
            )
            self.assertTrue(result.budget_terminated)
            self.assertEqual(1, result.action_count)

    def test_wall_clock_budget_terminates_fake_session(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            result = orchestrator.run_codex(
                [sys.executable, "-c", "import time; time.sleep(30)"],
                project_root=root,
                event_log=root / "events.jsonl",
                message_log=root / "last.md",
                max_actions=100,
                max_seconds=1,
            )
            self.assertTrue(result.timeout_terminated)
            self.assertEqual(
                "session_timeout",
                orchestrator.classify_outcome(
                    "30-verify",
                    result.returncode,
                    result.final_message,
                    "",
                    timeout_terminated=result.timeout_terminated,
                ).status,
            )


class StateTests(unittest.TestCase):
    def test_state_appends_records(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            state_path = Path(tmp) / "runs.json"
            orchestrator.append_run_state(state_path, {"stage": "00-scope"})
            orchestrator.append_run_state(state_path, {"stage": "10-baseline"})
            state = state_path.read_text(encoding="utf-8")
            self.assertIn('"stage": "00-scope"', state)
            self.assertIn('"stage": "10-baseline"', state)
            self.assertIn('"version": 1', state)

    def test_advances_handoff_state(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            run_dir = Path(tmp)
            state_path = run_dir / "state.json"
            state_path.write_text(
                json.dumps(
                    {
                        "version": 1,
                        "component": "button-group",
                        "goal": "test",
                        "stage": "scope",
                        "status": "active",
                    }
                ),
                encoding="utf-8",
            )
            orchestrator.advance_run_state(run_dir, "00-scope")
            first = json.loads(state_path.read_text(encoding="utf-8"))
            self.assertEqual("baseline", first["stage"])
            self.assertEqual("active", first["status"])

            orchestrator.advance_run_state(run_dir, "40-handoff")
            complete = json.loads(state_path.read_text(encoding="utf-8"))
            self.assertEqual("complete", complete["stage"])
            self.assertEqual("complete", complete["status"])

    def test_bootstraps_tao_progress_log(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            run_dir = Path(tmp)
            state_dir = run_dir / "orchestrator"
            state_dir.mkdir()
            (state_dir / "runs.json").write_text(
                json.dumps(
                    {
                        "runs": [
                            {"stage": "00-scope", "status": "stage_done"},
                            {"stage": "10-baseline", "status": "stage_done"},
                        ]
                    }
                ),
                encoding="utf-8",
            )
            orchestrator.bootstrap_tao_contract(run_dir)
            self.assertEqual(
                "00-scope ok\n10-baseline ok\n",
                (run_dir / "progress.log").read_text(encoding="utf-8"),
            )
            self.assertEqual("20-implement", orchestrator.infer_start_stage(run_dir))

    def test_trailing_fail_blocks_auto_routing(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            run_dir = Path(tmp)
            (run_dir / "progress.log").write_text(
                "00-scope ok\n10-baseline FAIL\n",
                encoding="utf-8",
            )
            with self.assertRaises(RuntimeError):
                orchestrator.infer_start_stage(run_dir)

    def test_recovers_only_proven_transient_provider_failure(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            run_dir = Path(tmp)
            event_log = run_dir / "events.jsonl"
            event_log.write_text(
                '{"type":"error","message":"You’ve hit your usage limit."}\n',
                encoding="utf-8",
            )
            state_dir = run_dir / "orchestrator"
            state_dir.mkdir()
            (state_dir / "runs.json").write_text(
                json.dumps(
                    {
                        "runs": [
                            {
                                "stage": "00-scope",
                                "status": "process_failed",
                                "event_log": str(event_log),
                            }
                        ]
                    }
                ),
                encoding="utf-8",
            )
            (run_dir / "progress.log").write_text(
                "00-scope FAIL\n", encoding="utf-8"
            )
            self.assertTrue(orchestrator.recover_transient_failure(run_dir))
            self.assertEqual(
                "00-scope FAIL\n00-scope rotate\n",
                (run_dir / "progress.log").read_text(encoding="utf-8"),
            )

    def test_does_not_recover_component_failure(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            run_dir = Path(tmp)
            event_log = run_dir / "events.jsonl"
            event_log.write_text(
                '{"type":"error","message":"assertion failed"}\n',
                encoding="utf-8",
            )
            state_dir = run_dir / "orchestrator"
            state_dir.mkdir()
            (state_dir / "runs.json").write_text(
                json.dumps(
                    {
                        "runs": [
                            {
                                "stage": "20-implement",
                                "status": "process_failed",
                                "event_log": str(event_log),
                            }
                        ]
                    }
                ),
                encoding="utf-8",
            )
            (run_dir / "progress.log").write_text(
                "20-implement FAIL\n", encoding="utf-8"
            )
            self.assertFalse(orchestrator.recover_transient_failure(run_dir))
            self.assertEqual(
                "20-implement FAIL\n",
                (run_dir / "progress.log").read_text(encoding="utf-8"),
            )

    def test_bootstrap_writes_loop_and_done_contracts(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            run_dir = Path(tmp)
            state_dir = run_dir / "orchestrator"
            state_dir.mkdir()
            runs = [
                {"stage": stage, "status": "stage_done"}
                for stage in orchestrator.DEFAULT_STAGES
            ]
            (state_dir / "runs.json").write_text(
                json.dumps({"runs": runs}),
                encoding="utf-8",
            )
            orchestrator.bootstrap_tao_contract(run_dir)
            self.assertTrue((run_dir / "loop_log.jsonl").is_file())
            self.assertTrue((run_dir / "DONE.marker").is_file())
            self.assertIsNone(orchestrator.infer_start_stage(run_dir))


if __name__ == "__main__":
    unittest.main()
