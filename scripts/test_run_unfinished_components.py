#!/usr/bin/env python3
"""Focused tests for the unfinished-component batch runner."""

from __future__ import annotations

import json
import subprocess
import importlib.util
import sys
import tempfile
import unittest
import contextlib
import io
import hashlib
from types import SimpleNamespace
from unittest.mock import patch
from pathlib import Path


SCRIPT = Path(__file__).with_name("run_unfinished_components.py")
SPEC = importlib.util.spec_from_file_location("run_unfinished_components", SCRIPT)
assert SPEC and SPEC.loader
batch = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = batch
SPEC.loader.exec_module(batch)


def write_verification_route(run):
    last = json.loads((run / 'orchestrator/runs.json').read_text())['runs'][-1]
    (run / 'failure-routing.json').write_text(json.dumps({
        'kind': 'verification_only', 'resume_stage': '30-verify', 'failed_stage': last['stage'],
        'progress_sha256': hashlib.sha256((run / 'progress.log').read_bytes()).hexdigest(),
        'message_sha256': hashlib.sha256(Path(last['message_log']).read_bytes()).hexdigest(),
        'record_sha256': hashlib.sha256(json.dumps(last, sort_keys=True).encode()).hexdigest(),
    }))


class BatchTests(unittest.TestCase):
    def test_reverse_order_precedes_limit_and_includes_blocked_for_repair(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            (root / 'docs').mkdir()
            (root / 'docs/shadcn-web-progress.md').write_text('- [ ] First (`first`)\n- [ ] Last (`last`)\n')
            run = root / '.agent-runs/last'
            run.mkdir(parents=True)
            (run / 'progress.log').write_text('30-verify FAIL\n')
            output = io.StringIO()
            with contextlib.redirect_stdout(output):
                code = batch.run_main(['--project-root', str(root), '--reverse', '--repair-blocked', '--limit', '1'])
            self.assertEqual(0, code)
            self.assertIn('01. Last (last)', output.getvalue())
            self.assertNotIn('01. First', output.getvalue())
            self.assertNotIn('SKIP last', output.getvalue())

    def test_complete_mode_repairs_verifier_failure_before_advancing(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            (root / 'docs').mkdir()
            checklist = root / 'docs/shadcn-web-progress.md'
            checklist.write_text('- [ ] Example (`example`)\n')
            run = root / '.agent-runs/example'
            run.mkdir(parents=True)
            (run / 'progress.log').write_text('20-implement ok\n30-verify FAIL\n')
            (run / 'progress.md').write_text('Recorded defect')
            count = 0
            def driver(*args, **kwargs):
                nonlocal count
                count += 1
                if count == 1:
                    with (run / 'progress.log').open('a') as f: f.write('30-verify FAIL\n')
                    return SimpleNamespace(returncode=2)
                source = root / 'Example.kt'
                source.write_text('class FixedExample')
                inputs = run / 'inputs.json'
                inputs.write_text(json.dumps({'Example.kt': hashlib.sha256(source.read_bytes()).hexdigest()}))
                artifact = run / 'report.json'
                artifact.write_text('{"passed":true}')
                gates = {}
                for name in ['tests', 'lint', 'build', 'browser', 'audit', 'review']:
                    evidence = run / f'{name}.json'
                    evidence.write_text(json.dumps({'gate': name, 'status': 'passed',
                        'inputs': str(inputs.relative_to(root)), 'inputs_sha256': hashlib.sha256(inputs.read_bytes()).hexdigest(),
                        'artifacts': {str(artifact.relative_to(root)): hashlib.sha256(artifact.read_bytes()).hexdigest()}}))
                    gates[name] = {'status': 'passed', 'evidence': str(evidence.relative_to(root)), 'sha256': hashlib.sha256(evidence.read_bytes()).hexdigest()}
                (run / 'acceptance.json').write_text(json.dumps({'status': 'accepted', 'gates': gates, 'source_hashes': {'Example.kt': hashlib.sha256(source.read_bytes()).hexdigest()}}))
                checklist.write_text('- [x] Example (`example`)\n')
                return SimpleNamespace(returncode=0)
            args = batch.parse_args(['--project-root', str(root), '--complete-components', '--max-component-passes', '2'])
            with patch.object(batch.subprocess, 'run', side_effect=driver) as worker:
                result = batch.execute_component(batch.Component('Example', 'example'), args)
            self.assertEqual((0, 'completed', None), result)
            self.assertEqual(2, count)
            self.assertEqual('20-implement', json.loads((run / 'recovery.json').read_text())['stage'])
            self.assertIn('--require-acceptance-receipt', worker.call_args.args[0])

    def test_verification_only_blocker_resumes_verify_on_startup_and_in_loop(self):
        for initial_failure in (True, False):
            with self.subTest(initial_failure=initial_failure), tempfile.TemporaryDirectory() as tmp:
                root = Path(tmp)
                run = root / '.agent-runs/example'
                reports = run / 'orchestrator'
                reports.mkdir(parents=True)
                (run / 'progress.md').write_text('Existing implementation and evidence')
                (run / 'progress.log').write_text(
                    '20-implement FAIL\n' if initial_failure else '30-verify rotate\n')
                message = reports / 'blocked.txt'
                message.write_text('阻塞：尚缺浏览器验证证据；当前没有已确认的生产缺陷可供修复。\nSTAGE_BLOCKED 20-implement\n')
                records = reports / 'runs.json'
                records.write_text(json.dumps({'runs': [{'stage': '20-implement', 'status': 'blocked',
                                                        'message_log': str(message)}]}))
                write_verification_route(run)
                observed = []

                def driver(*args, **kwargs):
                    observed.append((run / 'progress.log').read_text().splitlines()[-1])
                    if not initial_failure and len(observed) == 1:
                        with (run / 'progress.log').open('a') as f:
                            f.write('30-verify FAIL\n')
                        message.write_text('Blocker: Missing browser verification evidence; no confirmed production defect.\nSTAGE_BLOCKED 30-verify\n')
                        records.write_text(json.dumps({'runs': [{'stage': '30-verify', 'status': 'blocked',
                                                                'message_log': str(message)}]}))
                        write_verification_route(run)
                    return SimpleNamespace(returncode=2)

                args = batch.parse_args(['--project-root', str(root), '--complete-components',
                                         '--max-component-passes', '1' if initial_failure else '2'])
                with patch.object(batch.subprocess, 'run', side_effect=driver):
                    batch.execute_component(batch.Component('Example', 'example'), args)
                self.assertEqual(['30-verify rotate'] * len(observed), observed)
                self.assertIn('FAIL', (run / 'progress.log').read_text())
                self.assertTrue(message.exists())

    def test_failure_routing_preserves_product_repairs_and_rejects_stale_reports(self):
        for record_stage, reason in [
            ('20-implement', 'Blocker: browser transition is wrong; production defect confirmed.'),
            ('30-verify', 'Blocker: Missing browser verification; no confirmed production defect.'),
            ('20-implement', 'Blocker: No production edits yet; missing browser verification.'),
        ]:
            with self.subTest(record_stage=record_stage, reason=reason), tempfile.TemporaryDirectory() as tmp:
                run = Path(tmp)
                reports = run / 'orchestrator'
                reports.mkdir()
                message = reports / 'blocked.txt'
                message.write_text(reason + f'\nSTAGE_BLOCKED {record_stage}\n')
                (reports / 'runs.json').write_text(json.dumps({'runs': [
                    {'stage': record_stage, 'status': 'blocked', 'message_log': str(message)}]}))
                self.assertEqual('20-implement', batch.failure_resume_stage(run, '20-implement'))

    def test_same_stage_new_failure_or_changed_message_rejects_old_route(self):
        with tempfile.TemporaryDirectory() as tmp:
            run = Path(tmp)
            reports = run / 'orchestrator'
            reports.mkdir()
            (run / 'progress.log').write_text('20-implement FAIL\n')
            message = reports / 'blocked.txt'
            message.write_text('Missing verification, no confirmed production defect.\nSTAGE_BLOCKED 20-implement\n')
            records = reports / 'runs.json'
            records.write_text(json.dumps({'runs': [{'stage': '20-implement', 'status': 'blocked',
                                                    'message_log': str(message)}]}))
            write_verification_route(run)
            self.assertEqual('30-verify', batch.failure_resume_stage(run, '20-implement'))
            original = message.read_text()
            for reason in [
                'Blocker: Missing browser verification; no production defect fix has been applied; production defect confirmed.',
                'Blocker: Missing verification; no confirmed production defect.\nLater: shared production defect confirmed.',
            ]:
                message.write_text(reason + '\nSTAGE_BLOCKED 20-implement\n')
                self.assertEqual('20-implement', batch.failure_resume_stage(run, '20-implement'))
            message.write_text(original)
            original_records = records.read_text()
            payload = json.loads(original_records)
            payload['runs'][-1]['thread_id'] = 'different-attempt'
            records.write_text(json.dumps(payload))
            self.assertEqual('20-implement', batch.failure_resume_stage(run, '20-implement'))
            records.write_text(original_records)
            with (run / 'progress.log').open('a') as f:
                f.write('20-implement rotate\n20-implement FAIL\n')
            self.assertEqual('20-implement', batch.failure_resume_stage(run, '20-implement'))

    def test_blocked_fixer_is_not_blindly_repeated(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / '.agent-runs/example'
            run.mkdir(parents=True)
            (run / 'progress.log').write_text('20-implement rotate\n')
            (run / 'progress.md').write_text('A real external blocker')
            def driver(*args, **kwargs):
                with (run / 'progress.log').open('a') as f:
                    f.write('20-implement FAIL\n')
                return SimpleNamespace(returncode=2)
            args = batch.parse_args(['--project-root', str(root), '--complete-components', '--max-component-passes', '3'])
            with patch.object(batch.subprocess, 'run', side_effect=driver) as worker:
                result = batch.execute_component(batch.Component('Example', 'example'), args)
            self.assertEqual('blocked', result[1])
            self.assertEqual(1, worker.call_count)

    def test_detached_batch_finishes_without_polling_or_a_model(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            (root / "docs").mkdir()
            (root / "docs/shadcn-web-progress.md").write_text("- [ ] Fake (`fake`)\n")
            (root / "scripts").mkdir()
            run = root / ".agent-runs/fake"
            run.mkdir(parents=True)
            (run / "progress.md").write_text("existing")
            (root / "scripts/codex_stage_orchestrator.py").write_text('print("verbose child output")')
            receipt = subprocess.run([sys.executable, str(SCRIPT), "--project-root", str(root),
                                      "--execute", "--background"], capture_output=True, text=True)
            self.assertIn(receipt.returncode, (0, 4), receipt.stderr)
            log = Path(json.loads(receipt.stdout)["log"])
            # Wait in the test only; production callers receive an immediate receipt.
            import time
            deadline = time.monotonic() + 5
            while time.monotonic() < deadline:
                if log.exists() and "Batch complete:" in log.read_text():
                    break
                time.sleep(.02)
            self.assertIn("Batch complete:", log.read_text())
            self.assertNotIn("verbose child output", log.read_text())
            self.assertIn("verbose child output", (run / "batch-driver.log").read_text())
            status = subprocess.run([sys.executable, str(SCRIPT), "--project-root", str(root), "--status"],
                                    capture_output=True, text=True, check=True)
            self.assertEqual({"acceptance_pending": 1}, json.loads(status.stdout)["counts"])

    def test_classifies_provider_outage_without_component_fail(self) -> None:
        self.assertEqual('verification_pending', batch.classify_process_result(5)[0])
        self.assertEqual(
            (
                "infrastructure_blocked",
                "Codex provider unavailable; stop the batch and retry after it recovers",
            ),
            batch.classify_process_result(3),
        )
        self.assertEqual(("blocked", "TAO driver stopped; inspect component handoff"),
                         batch.classify_process_result(2))

    def test_parses_unchecked_components_in_order(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / "progress.md"
            path.write_text(
                "- [x] Done (`done`)\n"
                "- [ ] Empty (`empty`)\n"
                "- [ ] Progress (`progress`)\n",
                encoding="utf-8",
            )
            components = batch.parse_unchecked(path)
            self.assertEqual(
                [("Empty", "empty"), ("Progress", "progress")],
                [(item.name, item.slug) for item in components],
            )

    def test_failed_and_done_runs_are_skipped(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            done = root / "done"
            done.mkdir()
            (done / "DONE.marker").write_text("complete\n", encoding="utf-8")
            failed = root / "failed"
            failed.mkdir()
            (failed / "progress.log").write_text(
                "00-scope ok\n20-implement FAIL\n",
                encoding="utf-8",
            )
            active = root / "active"
            active.mkdir()
            (active / "progress.log").write_text(
                "00-scope ok\n10-baseline rotate\n",
                encoding="utf-8",
            )
            self.assertIsNone(batch.skip_reason(done))
            self.assertEqual("blocked by committed FAIL", batch.skip_reason(failed))
            self.assertIsNone(batch.skip_reason(active))



class PersistentBudgetBatchTests(unittest.TestCase):
    def test_forwards_limits_and_stops_without_completion_retry(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / '.agent-runs/example'
            run.mkdir(parents=True)
            (run / 'progress.md').write_text('checkpoint')
            args = batch.parse_args(['--project-root', str(root), '--complete-components',
                '--max-component-sessions', '3', '--max-component-input-tokens', '900', '--max-component-seconds', '100'])
            command = batch.orchestrator_command(batch.Component('Example', 'example'), args)
            for flag, value in [('--max-component-sessions', '3'), ('--max-component-input-tokens', '900'), ('--max-component-seconds', '100')]:
                self.assertEqual(value, command[command.index(flag) + 1])
            with patch.object(batch.subprocess, 'run', return_value=SimpleNamespace(returncode=6)) as worker:
                code, status, _ = batch.execute_component(batch.Component('Example', 'example'), args)
                self.assertEqual((6, 'budget_exhausted'), (code, status))
                self.assertEqual(1, worker.call_count)

    def test_batch_exit_code_preserves_budget_stop(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            (root / 'docs').mkdir()
            (root / 'docs/shadcn-web-progress.md').write_text('- [ ] Example (`example`)\n')
            with patch.object(batch, 'initialize_component', return_value=0), patch.object(batch, 'execute_component', return_value=(6, 'budget_exhausted', 'spent')):
                self.assertEqual(6, batch.run_main(['--project-root', str(root), '--execute', '--complete-components']))


if __name__ == "__main__":
    unittest.main()
