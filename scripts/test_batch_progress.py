import fcntl
import importlib.util
import json
import tempfile
import unittest
from unittest.mock import patch
from pathlib import Path

spec = importlib.util.spec_from_file_location('batch_progress', Path(__file__).with_name('batch_progress.py'))
progress = importlib.util.module_from_spec(spec)
spec.loader.exec_module(progress)


class ProgressTests(unittest.TestCase):
    def test_driver_stop_reason_is_not_replaced_by_worker_success_summary(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / '.agent-runs/chart/orchestrator'
            run.mkdir(parents=True)
            message = run / 'last.md'
            message.write_text('Added 98 passing checks.\nCONTEXT_HANDOFF 30-verify\n')
            reason = 'No work-unit advancement after two handoffs'
            (run / 'runs.json').write_text(json.dumps({'runs': [
                {'stage': '30-verify', 'status': 'blocked', 'reason': reason, 'message_log': str(message)}]}))
            (run.parent.parent / 'batch-state.json').write_text(json.dumps({'results': [
                {'slug': 'chart', 'status': 'blocked', 'reason': 'driver stopped', 'started_at': 'now'}]}))
            self.assertEqual(reason, progress.snapshot(root)['errors'][0]['reason'])

    def test_verification_session_cap_is_pending_not_an_error(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            directory = root / '.agent-runs'
            directory.mkdir()
            (directory / 'batch-state.json').write_text(json.dumps({'results': [
                {'slug': 'chart', 'status': 'verification_pending', 'reason': 'Continue saved coverage', 'started_at': 'now'}]}))
            state = progress.snapshot(root)
            self.assertEqual({'verification_pending': 1}, state['counts'])
            self.assertEqual([], state['errors'])
            self.assertIn('CHECKPOINT_PENDING chart', progress.render(state))

    def test_skipped_committed_failure_is_still_reported_as_blocked(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / '.agent-runs/fake/orchestrator'
            run.mkdir(parents=True)
            (run.parent.parent / 'batch-state.json').write_text(json.dumps({'results': [
                {'slug': 'fake', 'status': 'skipped', 'reason': 'blocked by committed FAIL', 'started_at': 'now'}]}))
            (run / 'runs.json').write_text(json.dumps({'runs': [
                {'stage': '20-implement', 'status': 'blocked', 'reason': 'real assertion failure'}]}))
            state = progress.snapshot(root)
            self.assertEqual({'blocked': 1}, state['counts'])
            self.assertEqual('real assertion failure', state['errors'][0]['reason'])

    def test_live_stage_and_lock_and_stale_state(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / '.agent-runs/fake/orchestrator'
            run.mkdir(parents=True)
            (run.parent.parent/'batch-state.json').write_text(json.dumps({'updated_at':'2026-11-09T10:00:00+08:00', 'results':[
                {'slug':'fake', 'status':'running', 'started_at':'2026-11-09T10:00:00+08:00'}]}))
            (run/'live.json').write_text(json.dumps({'stage':'30-verify', 'actions':12, 'updated_at':'2026-11-09T10:01:00+08:00'}))
            with (run.parent.parent/'batch.lock').open('a') as handle:
                fcntl.flock(handle, fcntl.LOCK_EX)
                state = progress.snapshot(root)
                self.assertTrue(state['active'])
                self.assertIn('30-verify', progress.render(state))
                self.assertIn('actions=12', progress.render(state))
            self.assertIn('stale', progress.render(progress.snapshot(root)))
            (run/'live.json').write_text(json.dumps({'stage':'old', 'updated_at':'2026-11-08T10:00:00+08:00'}))
            self.assertNotIn('old', progress.render(progress.snapshot(root)))

    def test_mirror_appends_changes_and_stops_with_batch(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            log = root / "batch.log"
            log.write_text("STARTED: fake\n")
            states = [{"active": active, "updated_at": "now", "counts": {"running": 1} if active else {"completed": 1},
                       "current": [], "errors": []} for active in (True, True, False)]
            with patch.object(progress, "snapshot", side_effect=states), patch.object(progress.time, "sleep"):
                self.assertEqual(0, progress.mirror(root, log))
            output = log.read_text()
            self.assertTrue(output.startswith("STARTED: fake"))
            self.assertEqual(2, output.count("[PROGRESS]"))
            self.assertIn("STOPPED", output)

    def test_mirror_does_not_repeat_unchanged_errors_on_every_action(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            states = [{"active": active, "updated_at": str(n), "counts": {"blocked": 1}, "current": [],
                       "errors": [{"component": "fake", "reason": "shared gate failed", "event_log": "failure.jsonl"}]}
                      for n, active in enumerate((True, True, False))]
            with patch.object(progress, "snapshot", side_effect=states), patch.object(progress.time, "sleep"):
                progress.mirror(root, root / "batch.log")
            self.assertEqual(1, (root / "batch.log").read_text().count("ERROR fake:"))

    def test_reopened_blocker_is_pending_not_an_active_error(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / '.agent-runs/fake/orchestrator'
            run.mkdir(parents=True)
            (run.parent.parent / 'batch-state.json').write_text(json.dumps({'results': [
                {'slug': 'fake', 'status': 'blocked', 'started_at': 'now'}]}))
            (run / 'runs.json').write_text(json.dumps({'runs': [
                {'stage': '30-verify', 'status': 'blocked', 'reason': 'old failure'}]}))
            mark = run.parent / 'progress.log'
            mark.write_text('30-verify FAIL\n30-verify rotate\n')
            state = progress.snapshot(root)
            self.assertEqual({'recovery_pending': 1}, state['counts'])
            self.assertEqual([], state['errors'])
            self.assertIn('RECOVERY_PENDING fake', progress.render(state))
            self.assertEqual('old failure', state['recoveries'][0]['previous_error']['reason'])
            # A new failure after recovery remains visible.
            mark.write_text('30-verify FAIL\n30-verify rotate\n30-verify FAIL\n')
            self.assertEqual(1, len(progress.snapshot(root)['errors']))

    def test_empty_watch_exits_without_model_calls(self):
        with tempfile.TemporaryDirectory() as tmp:
            self.assertEqual(0, progress.watch(Path(tmp), .1))



class CumulativeProgressTests(unittest.TestCase):
    def test_budget_stop_and_unknown_coverage_are_visible(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            run = root / '.agent-runs/chart'
            (run / 'orchestrator').mkdir(parents=True)
            (run.parent / 'batch-state.json').write_text(json.dumps({'results': [
                {'slug': 'chart', 'status': 'budget_exhausted', 'reason': 'component budget exhausted', 'started_at': 'now'}]}))
            (run / 'orchestrator/runs.json').write_text(json.dumps({'runs': [
                {'stage': '30-verify', 'status': 'context_handoff', 'reason': 'agent requested continuation'}]}))
            (run / 'execution-budget.json').write_text(json.dumps({'sessions': 24, 'input_tokens': 20000000,
                'seconds': 7000, 'limits': {'sessions': 24, 'input_tokens': 20000000, 'seconds': 7200},
                'stop_reason': 'component cumulative input_tokens budget exhausted'}))
            (run / 'continuation-state.json').write_text(json.dumps({'last_advanced_at': '2026-11-11T04:00:00+08:00'}))
            state = progress.snapshot(root)
            output = progress.render(state)
            self.assertEqual('component budget exhausted', state['errors'][0]['reason'])
            self.assertIn('sessions=24/24', output)
            self.assertIn('input=20000000/20000000', output)
            self.assertIn('required remaining=unknown', output)
            self.assertIn('last advancement=2026-11-11T04:00:00+08:00', output)


if __name__ == "__main__":
    unittest.main()
