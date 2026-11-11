"""Disk-only batch progress display; never invokes a model."""
from __future__ import annotations
import fcntl
import json
import time
from collections import Counter
from pathlib import Path
import component_execution_control as control


def read_json(path: Path, default=None):
    try:
        return json.loads(path.read_text(encoding='utf-8'))
    except (OSError, ValueError):
        return default


def snapshot(root: Path) -> dict:
    state = read_json(root / '.agent-runs/batch-state.json', {})
    results = state.get('results', [])
    active = False
    lock_path = root / '.agent-runs/batch.lock'
    if lock_path.exists():
        with lock_path.open('a') as handle:
            try:
                fcntl.flock(handle, fcntl.LOCK_EX | fcntl.LOCK_NB)
                fcntl.flock(handle, fcntl.LOCK_UN)
            except BlockingIOError:
                active = True
    current = []
    pending = []
    errors = []
    recoveries = []
    components = []
    counts = Counter()
    for row in results:
        status = row['status']
        if status == 'skipped' and row.get('reason') == 'blocked by committed FAIL':
            status = 'blocked'
        counts[status] += 1
        if row['status'] in ('acceptance_pending', 'verification_pending'):
            pending.append({'component': row['slug'], 'reason': row.get('reason'), 'status': row['status']})
        run = root / '.agent-runs' / row['slug'] / 'orchestrator'
        try:
            budget = control.read_json(run.parent / 'execution-budget.json', {})
            _, coverage = control.coverage(run.parent)
            components.append({'component': row['slug'], 'budget': budget, 'coverage': coverage,
                               'last_advanced_at': control.read_json(run.parent / 'continuation-state.json', {}).get('last_advanced_at')})
        except (OSError, ValueError, TypeError):
            components.append({'component': row['slug'], 'telemetry_error': 'invalid execution telemetry'})
        if row['status'] == 'running':
            live = read_json(run / 'live.json', {})
            # Only show telemetry from this invocation, not a previous attempt.
            if live.get('updated_at', '') < row['started_at']:
                live = {}
            current.append({'component': row['slug'], 'started_at': row['started_at'], **live})
        if status in ('blocked', 'infrastructure_blocked', 'init_failed', 'budget_exhausted'):
            records = read_json(run / 'runs.json', {}).get('runs', [])
            last = records[-1] if records else {}
            reason = row.get('reason') if status == 'budget_exhausted' else last.get('reason') or row.get('reason')
            if status != 'budget_exhausted' and last.get('status') == 'blocked' and last.get('message_log'):
                try:
                    from codex_stage_orchestrator import blocker_reason, last_nonempty_line
                    message = Path(last['message_log']).read_text()
                    if last_nonempty_line(message).startswith('STAGE_BLOCKED '):
                        reason = blocker_reason(message)
                except OSError:
                    pass
            event_path = Path(last.get('event_log') or '/nonexistent')
            try:
                with event_path.open('rb') as handle:
                    handle.seek(0, 2)
                    handle.seek(max(0, handle.tell() - 4096))
                    lines = handle.read().decode('utf-8', errors='replace').splitlines()
                reason = next((line[:300] for line in reversed(lines) if line.startswith('Error:')), reason)
            except OSError:
                pass
            error = {'component': row['slug'], 'reason': reason, 'event_log': last.get('event_log')}
            try:
                marks = (run.parent / 'progress.log').read_text().splitlines()
                latest_mark = next((line.split() for line in reversed(marks) if line.strip()), [])
            except OSError:
                latest_mark = []
            if (last.get('status') == 'blocked' and
                    latest_mark == [last.get('stage'), 'rotate']):
                counts[status] -= 1
                counts['recovery_pending'] += 1
                recoveries.append({'component': row['slug'], 'stage': last['stage'],
                                   'reason': 'Operator reopened the checkpoint; waiting for the current component to finish.',
                                   'previous_error': error})
            else:
                errors.append(error)
    return {'active': active, 'updated_at': state.get('updated_at'),
            'counts': {key: value for key, value in counts.items() if value},
            'recoveries': recoveries,
            'acceptance_pending': pending,
            'running': [row['component'] for row in current], 'current': current,
            'errors': errors, 'components': components, 'state': str(root / '.agent-runs/batch-state.json')}


def render(state: dict) -> str:
    lines = [f"Batch: {'RUNNING' if state['active'] else 'STOPPED'} | updated: {state['updated_at']}",
             ' | '.join(f'{key}: {value}' for key, value in state['counts'].items()) or 'No batch state']
    for row in state['current']:
        lines.append(f"{row['component']} | {row.get('stage', 'starting')} | actions={row.get('actions', 0)} | "
                     f"last event={row.get('updated_at', row['started_at'])} | {row.get('event_type', '')}")
    if not state['active'] and state['current']:
        lines.append('Interrupted: saved running state is stale; no batch lock is held.')
    for row in state.get('components', []):
        budget = row.get('budget', {})
        limits = budget.get('limits', {})
        coverage = row.get('coverage', {})
        current_round = budget.get('round')
        if current_round:
            spent = {key: max(0, budget.get(key, 0) - current_round['baseline'][key]) for key in ('sessions', 'input_tokens', 'seconds')}
            lines.append(f"{row['component']} | round={current_round['id']} | "
                         f"sessions={spent['sessions']}/{current_round['limits']['sessions']} | "
                         f"input={spent['input_tokens']}/{current_round['limits']['input_tokens']} | "
                         f"seconds={spent['seconds']:.1f}/{current_round['limits']['seconds']}")
        lines.append(f"{row['component']} | cumulative sessions={budget.get('sessions', 'unknown')}/{limits.get('sessions', 'unknown')} | "
                     f"input={budget.get('input_tokens', 'unknown')}/{limits.get('input_tokens', 'unknown')} | "
                     f"observed input={budget.get('observed_input_tokens', 'unknown')} | estimated charge={budget.get('estimated_input_tokens', 'unknown')} | "
                     f"seconds={budget.get('seconds', 'unknown')}/{limits.get('seconds', 'unknown')} | "
                     f"required remaining={coverage.get('remaining') if coverage.get('remaining') is not None else 'unknown'} | "
                     f"last advancement={row.get('last_advanced_at') or 'unknown'} | "
                     f"{budget.get('stop_reason') or coverage.get('reason') or 'input-bound coverage'}")
    for row in state.get('recoveries', []):
        lines.append(f"RECOVERY_PENDING {row['component']} | {row['stage']} | {row['reason']}")
    for row in state.get('acceptance_pending', []):
        label = 'CHECKPOINT_PENDING' if row.get('status') == 'verification_pending' else 'ACCEPTANCE_PENDING'
        lines.append(f"{label} {row['component']} | {row['reason']}")
    for error in state['errors'][-3:]:
        lines.append(f"ERROR {error['component']}: {error['reason']} | {error['event_log'] or ''}")
    return '\n'.join(lines)


def watch(root: Path, interval: float) -> int:
    previous = None
    try:
        while True:
            state = snapshot(root)
            output = render(state)
            if output != previous:
                print(output, flush=True)
                previous = output
            if not state['active']:
                return 0
            time.sleep(interval)
    except KeyboardInterrupt:
        return 0  # stopping the display leaves the batch running


def mirror(root: Path, log: Path, interval: float = 2) -> int:
    """Append changing progress to a batch log, including an already running batch."""
    lock_path = log.with_suffix(log.suffix + '.progress.lock')
    with lock_path.open('a') as lock:
        try:
            fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError:
            raise RuntimeError('a progress mirror is already writing this log')
        previous = None
        seen_errors = set()
        with log.open('a', encoding='utf-8', buffering=1) as output:
            while True:
                state = snapshot(root)
                # Keep counts visible, but append unchanged blocker details only once.
                fresh_errors = []
                for error in state['errors']:
                    signature = json.dumps(error, sort_keys=True)
                    if signature not in seen_errors:
                        fresh_errors.append(error)
                        seen_errors.add(signature)
                message = render({**state, 'errors': []})
                if fresh_errors:
                    message += '\n' + '\n'.join(
                        f"ERROR {error['component']}: {error['reason']} | {error['event_log'] or ''}"
                        for error in fresh_errors)
                if message != previous:
                    output.write('[PROGRESS]\n' + message + '\n')
                    previous = message
                if not state['active']:
                    return 0
                time.sleep(interval)


if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser(description='Mirror disk progress into a batch log without model calls.')
    parser.add_argument('--project-root', type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument('--log', required=True, type=Path)
    args = parser.parse_args()
    try:
        raise SystemExit(mirror(args.project_root.resolve(), args.log.resolve()))
    except RuntimeError as error:
        parser.exit(1, str(error) + '\n')
