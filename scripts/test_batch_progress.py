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

    def test_empty_watch_exits_without_model_calls(self):
        with tempfile.TemporaryDirectory() as tmp:
            self.assertEqual(0, progress.watch(Path(tmp), .1))


if __name__ == '__main__':
    unittest.main()
