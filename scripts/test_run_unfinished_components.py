#!/usr/bin/env python3
"""Focused tests for the unfinished-component batch runner."""

from __future__ import annotations

import json
import subprocess
import importlib.util
import sys
import tempfile
import unittest
from pathlib import Path


SCRIPT = Path(__file__).with_name("run_unfinished_components.py")
SPEC = importlib.util.spec_from_file_location("run_unfinished_components", SCRIPT)
assert SPEC and SPEC.loader
batch = importlib.util.module_from_spec(SPEC)
sys.modules[SPEC.name] = batch
SPEC.loader.exec_module(batch)


class BatchTests(unittest.TestCase):
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
                                      "--execute", "--background"], capture_output=True, text=True, check=True)
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
            self.assertEqual({"completed": 1}, json.loads(status.stdout)["counts"])

    def test_classifies_provider_outage_without_component_fail(self) -> None:
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
            self.assertEqual("completed", batch.skip_reason(done))
            self.assertEqual("blocked by committed FAIL", batch.skip_reason(failed))
            self.assertIsNone(batch.skip_reason(active))


if __name__ == "__main__":
    unittest.main()
