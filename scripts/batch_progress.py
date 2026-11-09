"""Disk-only batch progress display; never invokes a model."""
from __future__ import annotations
import fcntl
import json
import time
from collections import Counter
from pathlib import Path


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
    errors = []
    for row in results:
        run = root / '.agent-runs' / row['slug'] / 'orchestrator'
        if row['status'] == 'running':
            live = read_json(run / 'live.json', {})
            # Only show telemetry from this invocation, not a previous attempt.
            if live.get('updated_at', '') < row['started_at']:
                live = {}
            current.append({'component': row['slug'], 'started_at': row['started_at'], **live})
        if row['status'] in ('blocked', 'infrastructure_blocked', 'init_failed'):
            records = read_json(run / 'runs.json', {}).get('runs', [])
            last = records[-1] if records else {}
            reason = last.get('reason') or row.get('reason')
            if not last.get('reason') and last.get('message_log'):
                try:
                    lines = [line.strip() for line in Path(last['message_log']).read_text().splitlines() if line.strip()]
                    reason = next((line for line in reversed(lines) if not line.startswith('STAGE_')), reason)
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
            errors.append({'component': row['slug'], 'reason': reason,
                           'event_log': last.get('event_log')})
    return {'active': active, 'updated_at': state.get('updated_at'),
            'counts': dict(Counter(row['status'] for row in results)),
            'running': [row['component'] for row in current], 'current': current,
            'errors': errors, 'state': str(root / '.agent-runs/batch-state.json')}


def render(state: dict) -> str:
    lines = [f"Batch: {'RUNNING' if state['active'] else 'STOPPED'} | updated: {state['updated_at']}",
             ' | '.join(f'{key}: {value}' for key, value in state['counts'].items()) or 'No batch state']
    for row in state['current']:
        lines.append(f"{row['component']} | {row.get('stage', 'starting')} | actions={row.get('actions', 0)} | "
                     f"last event={row.get('updated_at', row['started_at'])} | {row.get('event_type', '')}")
    if not state['active'] and state['current']:
        lines.append('Interrupted: saved running state is stale; no batch lock is held.')
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
        with log.open('a', encoding='utf-8', buffering=1) as output:
            while True:
                state = snapshot(root)
                message = render(state)
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
