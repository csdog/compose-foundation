"""Regression tests for cross-launch limits and real acceptance progress."""
import hashlib
import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

import component_execution_control as control
import codex_stage_orchestrator as orchestrator


class ExecutionControlTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name)
        self.run = self.root / '.agent-runs/example'
        self.run.mkdir(parents=True)
        (self.run / 'orchestrator').mkdir()
        self.source = self.root / 'Example.kt'
        self.source.write_text('class Example')

    def reserve(self, sessions=2, tokens=100, seconds=60):
        return control.reserve(self.run, sessions, tokens, seconds, 30)

    def test_session_budget_survives_restarts_and_changed_source(self):
        self.assertIsNotNone(self.reserve())
        control.finish(self.run, 2, 10)
        self.assertIsNotNone(self.reserve())
        control.finish(self.run, 2, 10)
        self.source.write_text('class Repaired')
        self.assertIsNone(self.reserve())
        state = control.load_budget(self.run)
        self.assertEqual((2, 20, 4), (state['sessions'], state['input_tokens'], state['seconds']))

    def test_input_and_time_limits_stop_next_launch(self):
        self.reserve()
        control.finish(self.run, 3, 100)
        self.assertIsNone(self.reserve())
        self.assertIsNotNone(self.reserve(tokens=200))  # explicit increase, no reset
        control.finish(self.run, 57, 1)
        self.assertIsNone(self.reserve(sessions=5, tokens=200))

    def test_crash_reservation_is_not_refunded(self):
        allowance = self.reserve()
        self.assertEqual({'seconds': 30, 'input_tokens': 100}, allowance)
        self.assertIsNone(self.reserve())
        state = control.load_budget(self.run)
        self.assertEqual(1, state['sessions'])
        self.assertEqual(100, state['input_tokens'])

    def test_existing_history_is_charged_once(self):
        reports = self.run / 'orchestrator'
        control.write_json(reports / 'runs.json', {'runs': [{'usage': {'input_tokens': 80}, 'elapsed_seconds': 1}]})
        self.assertIsNotNone(self.reserve())
        control.finish(self.run, 1, 20)
        self.assertIsNone(self.reserve(sessions=4))
        self.assertEqual(100, control.load_budget(self.run)['input_tokens'])

    def test_failure_is_not_retried_by_rewording_handoff_or_restarting(self):
        digest = control.source_digest(self.root)
        control.record_failure(self.run, '30-verify', digest)
        (self.run / 'progress.md').write_text('New timestamp and new wording')
        self.assertFalse(control.retry_allowed(self.run, '30-verify', control.source_digest(self.root)))
        self.assertTrue(control.retry_allowed(self.run, '20-implement', digest))
        self.source.write_text('class Fixed')
        self.assertTrue(control.retry_allowed(self.run, '30-verify', control.source_digest(self.root)))

    def test_legacy_failure_without_signature_stops_and_binds(self):
        control.write_json(self.run / 'orchestrator/runs.json', {'runs': [{'stage': '30-verify', 'status': 'blocked'}]})
        self.assertFalse(control.retry_allowed(self.run, '30-verify', 'source'))
        self.assertFalse(control.retry_allowed(self.run, '30-verify', 'source'))
        self.assertTrue(control.retry_allowed(self.run, '30-verify', 'fixed-source'))

    def matrix(self, required):
        matrix = {'required': required, 'inputs': ['Example.kt']}
        matrix['sha256'] = control.matrix_digest(matrix)
        control.write_json(self.run / 'required-verification.json', matrix)
        control.freeze_matrix(self.run)

    def entry(self, identifier='keyboard'):
        path = self.run / 'evidence.json'
        path.write_text('{"passed":true}')
        return {'id': identifier, 'status': 'passed', 'evidence': 'evidence.json',
                'sha256': hashlib.sha256(path.read_bytes()).hexdigest(),
                'inputs': {'Example.kt': hashlib.sha256(self.source.read_bytes()).hexdigest()}}

    def test_extra_diagnostic_with_same_evidence_does_not_advance(self):
        self.matrix(['keyboard', 'rtl'])
        entry = self.entry()
        control.write_json(self.run / 'verification-progress.json', {'completed': [entry]})
        before = control.coverage(self.run)[0]
        control.write_json(self.run / 'verification-progress.json', {'completed': [entry, {**entry, 'id': 'new-diagnostic'}]})
        self.assertEqual(before, control.coverage(self.run)[0])
        self.assertEqual(1, control.coverage(self.run)[1]['remaining'])

    def test_source_change_invalidates_evidence_without_changing_artifact(self):
        self.matrix(['keyboard'])
        control.write_json(self.run / 'verification-progress.json', {'completed': [self.entry()]})
        self.assertEqual(frozenset({'keyboard'}), control.coverage(self.run)[0])
        self.source.write_text('class Changed')
        self.assertEqual(frozenset(), control.coverage(self.run)[0])

    def test_matrix_cannot_grow_or_shrink_after_freezing(self):
        self.matrix(['keyboard', 'rtl'])
        self.matrix(['keyboard'])
        self.assertIsNone(control.coverage(self.run)[1]['remaining'])
        self.assertIn('frozen', control.coverage(self.run)[1]['reason'])

    def test_missing_inputs_or_outside_paths_never_advance(self):
        self.matrix(['keyboard'])
        entry = self.entry()
        for inputs in ({}, {'../outside.kt': 'abc'}, {'.agent-runs/example/evidence.json': entry['sha256']}):
            control.write_json(self.run / 'verification-progress.json', {'completed': [{**entry, 'inputs': inputs}]})
            self.assertEqual(frozenset(), control.coverage(self.run)[0])

    def test_regression_and_lost_coverage_never_count_as_forward_progress(self):
        ids = frozenset({'keyboard'})
        self.assertFalse(control.advanced(('30d-audit', ids), ('30c-browser', ids)))
        self.assertFalse(control.advanced(('30c-browser', ids), ('30d-audit', frozenset())))
        self.assertFalse(control.advanced(('30b-build', ids), ('30c-browser', ids)))
        self.assertTrue(control.advanced(('30c-browser', ids), ('30c-browser', ids | {'rtl'})))

    def stage(self, **kwargs):
        card = self.root / '.agents/skills/token-efficient-compose-workflow/cards/30-verify.md'
        card.parent.mkdir(parents=True, exist_ok=True)
        card.write_text('verify')
        progress = self.run / 'progress.md'
        progress.write_text('## Next\n30c-browser\n')
        args = orchestrator.parse_args(['--component', 'example', '--project-root', str(self.root), '--max-attempts', '1'])
        for key, value in kwargs.items():
            setattr(args, key, value)
        return dict(args=args, project_root=self.root, plan_path=self.root / 'plan', skill_path=self.root / 'skill',
                    progress_path=progress, stage='30-verify', state_path=self.run / 'orchestrator/runs.json')

    def test_exhausted_budget_launches_no_worker(self):
        self.reserve(sessions=1)
        control.finish(self.run, 1, 1)
        with patch.object(orchestrator, 'run_codex') as worker:
            self.assertEqual(6, orchestrator.execute_stage(**self.stage(max_component_sessions=1)))
            worker.assert_not_called()

    def test_stall_counter_survives_orchestrator_restart(self):
        args = self.stage()
        result = orchestrator.RunResult(0, None, orchestrator.TokenUsage(1, 0, 1, 2), 1, False, False,
                                       'CONTEXT_HANDOFF 30-verify', self.run / 'event', self.run / 'last')
        result.event_log.write_text('')
        with patch.object(orchestrator, 'run_codex', return_value=result) as worker, patch.object(orchestrator, 'verify_handoff_or_fail'):
            self.assertEqual(2, orchestrator.execute_stage(**args))
            self.assertEqual(2, orchestrator.execute_stage(**args))
            self.assertEqual(2, control.read_json(self.run / 'continuation-state.json', {})['unchanged_handoffs'])
            self.assertEqual(2, worker.call_count)
            self.assertEqual(2, orchestrator.execute_stage(**args))
            self.assertEqual(2, worker.call_count)

    def test_component_lock_prevents_concurrent_direct_launch(self):
        import fcntl
        args = self.stage()
        with (self.run / 'execution.lock').open('a') as lock, patch.object(orchestrator, 'run_codex') as worker:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
            with self.assertRaisesRegex(RuntimeError, 'another component'):
                orchestrator.execute_stage(**args)
            worker.assert_not_called()

    def test_malformed_budget_fails_closed(self):
        (self.run / 'execution-budget.json').write_text('broken')
        with self.assertRaises(ValueError):
            self.reserve()

    def test_frozen_failure_inputs_ignore_unrelated_edits(self):
        self.matrix(['keyboard'])
        digest = control.failure_digest(self.root, self.run)
        control.record_failure(self.run, '30-verify', digest)
        (self.root / 'Unrelated.kt').write_text('unrelated edit')
        self.assertFalse(control.retry_allowed(self.run, '30-verify', control.failure_digest(self.root, self.run)))
        self.source.write_text('class Repaired')
        self.assertTrue(control.retry_allowed(self.run, '30-verify', control.failure_digest(self.root, self.run)))

    def test_permission_change_in_declared_environment_allows_retry(self):
        cache = self.root / 'cache'
        cache.mkdir()
        control.write_json(self.run / 'environment-inputs.json', [str(cache)])
        digest = control.failure_digest(self.root, self.run)
        control.record_failure(self.run, '30-verify', digest)
        cache.chmod(0o700 if cache.stat().st_mode & 0o777 != 0o700 else 0o755)
        self.assertTrue(control.retry_allowed(self.run, '30-verify', control.failure_digest(self.root, self.run)))

    def test_cli_usage_event_stops_process_at_input_allowance(self):
        import sys
        event = self.run / 'events.jsonl'
        message = self.run / 'last.md'
        program = 'import json,time; print(json.dumps({"type":"turn.completed","usage":{"input_tokens":100,"output_tokens":1}}),flush=True); time.sleep(10)'
        result = orchestrator.run_codex([sys.executable, '-u', '-c', program], project_root=self.root,
                                       event_log=event, message_log=message, max_actions=50, max_seconds=3, max_input_tokens=100)
        self.assertTrue(result.budget_terminated)
        self.assertFalse(result.usage_complete)
        self.assertEqual(100, result.usage.input_tokens)

    def test_token_allowance_reaching_handoff_stops_before_another_worker(self):
        args = self.stage(max_component_input_tokens=100)
        event = self.run / 'events'
        event.write_text('')
        result = orchestrator.RunResult(0, None, orchestrator.TokenUsage(100, 80, 1, 101), 1, False, False,
                                       'CONTEXT_HANDOFF 30-verify', event, self.run / 'last')
        with patch.object(orchestrator, 'run_codex', return_value=result) as worker:
            self.assertEqual(6, orchestrator.execute_stage(**args))
            self.assertEqual(6, orchestrator.execute_stage(**args))
            self.assertEqual(1, worker.call_count)

    def test_unaffected_check_keeps_its_own_input_bound_pass(self):
        other = self.root / 'BrowserFixture.ts'
        other.write_text('fixture')
        matrix = {'required': ['keyboard', 'browser'], 'inputs': {'keyboard': ['Example.kt'], 'browser': ['BrowserFixture.ts']}}
        matrix['sha256'] = control.matrix_digest(matrix)
        control.write_json(self.run / 'required-verification.json', matrix)
        control.freeze_matrix(self.run)
        first = self.entry()
        second = {**first, 'id': 'browser', 'inputs': {'BrowserFixture.ts': hashlib.sha256(other.read_bytes()).hexdigest()}}
        control.write_json(self.run / 'verification-progress.json', {'completed': [first, second]})
        self.assertEqual({'keyboard', 'browser'}, control.coverage(self.run)[0])
        self.source.write_text('class Changed')
        self.assertEqual({'browser'}, control.coverage(self.run)[0])

    def test_session_log_sampler_enforces_allowance_without_cli_usage_event(self):
        import sys
        program = 'import json,time; print(json.dumps({"type":"thread.started","thread_id":"11111111-1111-1111-1111-111111111111"}),flush=True); time.sleep(12)'
        with patch.object(orchestrator, 'recover_session_usage', return_value=orchestrator.TokenUsage(100, 50, 1, 101)):
            result = orchestrator.run_codex([sys.executable, '-u', '-c', program], project_root=self.root,
                event_log=self.run / 'events.jsonl', message_log=self.run / 'last.md',
                max_actions=50, max_seconds=10, max_input_tokens=100)
        self.assertTrue(result.budget_terminated)
        self.assertFalse(result.timeout_terminated)
        self.assertEqual(100, result.usage.input_tokens)

    def test_handoff_emits_input_warning_too(self):
        import contextlib
        import io
        args = self.stage(warn_stage_input_tokens=1)
        event = self.run / 'events'
        event.write_text('')
        result = orchestrator.RunResult(0, None, orchestrator.TokenUsage(2, 0, 1, 3), 1, False, False,
                                       'CONTEXT_HANDOFF 30-verify', event, self.run / 'last')
        output = io.StringIO()
        with contextlib.redirect_stdout(output), patch.object(orchestrator, 'run_codex', return_value=result):
            orchestrator.execute_stage(**args)
        self.assertIn('warning: session input exceeded watch target: 2', output.getvalue())

    def test_process_failure_cannot_repeat_with_identical_inputs(self):
        args = self.stage()
        event = self.run / 'events'
        event.write_text('Error: permissions denied')
        result = orchestrator.RunResult(1, None, orchestrator.TokenUsage(2, 0, 1, 3), 1, False, False,
                                       '', event, self.run / 'last')
        with patch.object(orchestrator, 'run_codex', return_value=result) as worker:
            self.assertEqual(2, orchestrator.execute_stage(**args))
            self.assertEqual(2, orchestrator.execute_stage(**args))
            self.assertEqual(1, worker.call_count)

    def test_conservative_charge_does_not_masquerade_as_measured_usage(self):
        self.reserve()
        control.finish(self.run, 1, 100, observed_input_tokens=7)
        state = control.load_budget(self.run)
        self.assertEqual(100, state['input_tokens'])
        self.assertEqual(7, state['observed_input_tokens'])
        self.assertEqual(93, state['estimated_input_tokens'])
