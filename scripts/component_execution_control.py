"""Persistent component limits and input-bound continuation telemetry. No model calls."""
from __future__ import annotations

import hashlib
import json
import os
import time
from pathlib import Path

DEFAULT_SESSIONS = 24
DEFAULT_INPUT_TOKENS = 20_000_000
DEFAULT_SECONDS = 7200
UNITS = ('30a-tests', '30b-build', '30c-browser', '30d-audit')


def read_json(path, default):
    try:
        return json.loads(Path(path).read_text())
    except FileNotFoundError:
        return default


def write_json(path, value):
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True)
    temporary = path.with_suffix(path.suffix + '.tmp')
    temporary.write_text(json.dumps(value, indent=2) + '\n')
    temporary.replace(path)


def source_digest(root):
    """Include untracked implementation/harness inputs; omit generated and run evidence."""
    root = Path(root).resolve()
    digest = hashlib.sha256()
    excluded = {'.git', '.agent-runs', 'build', '.gradle', '.kotlin', 'node_modules', '__pycache__', 'vendor'}
    for directory, dirs, files in os.walk(root):
        dirs[:] = sorted(d for d in dirs if d not in excluded)
        for name in sorted(files):
            path = Path(directory) / name
            if path.suffix not in {'.kt', '.kts', '.py', '.tsx', '.ts', '.js', '.mjs', '.json', '.toml', '.properties', '.html', '.css'}:
                continue
            if path.is_symlink():
                continue
            digest.update(str(path.relative_to(root)).encode())
            digest.update(b'\0')
            digest.update(path.read_bytes())
    for relative in ('AGENTS.md', 'docs/shadcn-web-plan.md'):
        path = root / relative
        if path.exists():
            digest.update(relative.encode())
            digest.update(path.read_bytes())
    return digest.hexdigest()


def load_budget(run):
    run = Path(run)
    path = run / 'execution-budget.json'
    state = read_json(path, None)
    if state is None:
        records = read_json(run / 'orchestrator/runs.json', {}).get('runs', [])
        state = {'sessions': len(records), 'input_tokens': sum(r.get('usage', {}).get('input_tokens', 0) for r in records),
                 'seconds': sum(r.get('elapsed_seconds', 0) for r in records), 'imported_history': len(records)}
        state['seconds'] = sum(r.get('elapsed_seconds', 600) for r in records)
        state['historical_time_estimated'] = any('elapsed_seconds' not in r for r in records)
    state.setdefault('observed_input_tokens', state['input_tokens'] - state.get('estimated_input_tokens', 0))
    state.setdefault('estimated_input_tokens', 0)
    if state.get('active'):
        # The owning run lock prevents parallel sessions. An unfinished reservation is
        # charged in full; crashes must not refund a launch or its reserved allowance.
        active = state.pop('active')
        state['seconds'] += active['seconds']
        state['input_tokens'] += active['input_tokens']
        state['estimated_input_tokens'] += active['input_tokens']
    return state


def reserve(run, max_sessions, max_input_tokens, max_seconds, session_seconds):
    state = load_budget(run)
    limits = {'sessions': max_sessions, 'input_tokens': max_input_tokens, 'seconds': max_seconds}
    current_round = state.get('round')
    if current_round:
        limits = {key: min(limit, current_round['baseline'][key] + current_round['limits'][key]) for key, limit in limits.items()}
    max_sessions, max_input_tokens, max_seconds = (limits[key] for key in ('sessions', 'input_tokens', 'seconds'))
    state['limits'] = limits
    exhausted = next((key for key, limit in limits.items() if state[key] >= limit), None)
    if exhausted:
        state['stop_reason'] = f'component cumulative {exhausted} budget exhausted'
        write_json(Path(run) / 'execution-budget.json', state)
        return None
    allowance = {'seconds': min(session_seconds, max_seconds - state['seconds']),
                 'input_tokens': max_input_tokens - state['input_tokens']}
    state['sessions'] += 1
    state['active'] = {**allowance, 'started_at': time.time()}
    state.pop('stop_reason', None)
    write_json(Path(run) / 'execution-budget.json', state)
    return allowance


def finish(run, elapsed, input_tokens, observed_input_tokens=None):
    path = Path(run) / 'execution-budget.json'
    state = read_json(path, {})
    state.pop('active', None)
    state['seconds'] += elapsed
    state['input_tokens'] += input_tokens
    observed = input_tokens if observed_input_tokens is None else observed_input_tokens
    state['observed_input_tokens'] += observed
    state['estimated_input_tokens'] += max(0, input_tokens - observed)
    write_json(path, state)


def failure_digest(root, run):
    root, run = Path(root).resolve(), Path(run)
    frozen = read_json(run / 'required-verification.lock.json', {})
    paths = frozen.get('inputs', [])
    if isinstance(paths, dict):
        paths = sorted({path for values in paths.values() for path in values})
    digest = hashlib.sha256()
    if not paths:
        digest.update(source_digest(root).encode())
    for relative in sorted(paths):
        path = (root / relative).resolve()
        if not path.is_relative_to(root):
            raise ValueError('failure input outside repository')
        digest.update(relative.encode())
        digest.update(path.read_bytes() if path.exists() else b'<missing>')
    # Operators may list real environment inputs (e.g. a cache directory or a
    # toolchain version receipt). Hash observations, not report timestamps.
    environment = read_json(run / 'environment-inputs.json', [])
    for name in sorted(environment):
        path = Path(name).expanduser().resolve()
        digest.update(str(path).encode())
        if path.exists():
            stat = path.stat()
            digest.update(str((stat.st_mode, stat.st_uid, stat.st_gid)).encode())
            if path.is_file():
                digest.update(path.read_bytes())
        else:
            digest.update(b'<missing>')
    return digest.hexdigest()


def retry_allowed(run, stage, digest):
    run = Path(run)
    records = read_json(run / 'orchestrator/runs.json', {}).get('runs', [])
    failures = read_json(Path(run) / 'failure-inputs.json', {})
    if stage not in failures and records and records[-1].get('stage') == stage and records[-1].get('status') == 'blocked':
        # Legacy failures have no source signature: bind now and stop, rather than
        # interpreting absence of a signature as evidence of repair.
        record_failure(run, stage, digest)
        return False
    return failures.get(stage) != digest


def record_failure(run, stage, digest):
    path = Path(run) / 'failure-inputs.json'
    failures = read_json(path, {})
    failures[stage] = digest
    write_json(path, failures)


def coverage(run):
    """Only frozen required IDs, bound to current source files, advance continuation."""
    run = Path(run).resolve()
    matrix = read_json(run / 'required-verification.json', {})
    if not isinstance(matrix, dict):
        return frozenset(), {'remaining': None, 'reason': 'required coverage matrix invalid'}
    required = matrix.get('required', [])
    expected_inputs = matrix.get('inputs', [])
    if not isinstance(required, list):
        return frozenset(), {'remaining': None, 'reason': 'required coverage matrix invalid'}
    if not required or not all(isinstance(x, str) and x for x in required) or len(required) != len(set(required)):
        return frozenset(), {'remaining': None, 'reason': 'required coverage matrix missing or invalid'}
    canonical = matrix_digest(matrix)
    if matrix.get('sha256') != canonical:
        return frozenset(), {'remaining': None, 'reason': 'required coverage matrix hash mismatch'}
    # Freeze persisted separately from the worker-writable matrix.
    frozen = read_json(run / 'required-verification.lock.json', {})
    if not isinstance(frozen, dict) or frozen.get('sha256') != canonical or frozen.get('required') != required or frozen.get('inputs') != expected_inputs:
        return frozenset(), {'remaining': None, 'reason': 'required coverage matrix is not frozen by driver'}
    if not valid_inputs(required, expected_inputs):
        return frozenset(), {'remaining': None, 'reason': 'required inputs invalid'}
    ledger = read_json(run / 'verification-progress.json', {})
    if not isinstance(ledger, dict) or not isinstance(ledger.get('completed', []), list):
        return frozenset(), {'remaining': None, 'reason': 'coverage ledger invalid'}
    passed = set()
    hash_cache = {}
    def file_hash(path):
        if path not in hash_cache:
            hash_cache[path] = hashlib.sha256(path.read_bytes()).hexdigest()
        return hash_cache[path]
    for entry in ledger.get('completed', []):
        if not isinstance(entry, dict) or entry.get('status') != 'passed' or entry.get('id') not in required:
            continue
        inputs = entry.get('inputs', {})
        check_inputs = expected_inputs.get(entry['id'], []) if isinstance(expected_inputs, dict) else expected_inputs
        if not isinstance(inputs, dict) or not inputs or set(inputs) != set(check_inputs):
            continue
        try:
            path = (run / entry['evidence']).resolve()
            if not path.is_relative_to(run) or path.stat().st_size > 32 * 1024 * 1024:
                continue
            digest = file_hash(path)
            root = run.parent.parent
            if digest != entry['sha256']:
                continue
            valid = True
            for relative, expected in inputs.items():
                source = (root / relative).resolve()
                run_source = source.is_relative_to(run / 'scope') or (source.is_relative_to(run / 'verify/browser') and source.suffix in {'.py', '.mjs', '.js', '.ts', '.tsx', '.css', '.html'})
                if not source.is_relative_to(root) or (source.is_relative_to(root / '.agent-runs') and not run_source) or file_hash(source) != expected:
                    valid = False
                    break
            if valid:
                passed.add(entry['id'])
        except (OSError, KeyError, TypeError):
            continue
    return frozenset(passed), {'required': len(required), 'passed': len(passed), 'remaining': len(set(required) - passed),
                               'matrix_sha256': canonical}


def valid_inputs(required, inputs):
    groups = list(inputs.values()) if isinstance(inputs, dict) else [inputs]
    if isinstance(inputs, dict) and set(inputs) != set(required):
        return False
    return bool(groups) and all(isinstance(group, list) and group and
        all(isinstance(path, str) and path and not Path(path).is_absolute() and '..' not in Path(path).parts for path in group) and len(group) == len(set(group)) for group in groups)


def matrix_digest(matrix):
    return hashlib.sha256(json.dumps({'required': matrix['required'], 'inputs': matrix.get('inputs', [])}, sort_keys=True).encode()).hexdigest()


def freeze_matrix(run):
    run = Path(run)
    matrix = read_json(run / 'required-verification.json', {})
    if not isinstance(matrix, dict):
        return
    required = matrix.get('required')
    if not isinstance(required, list) or not required or not all(isinstance(x, str) and x for x in required) or len(required) != len(set(required)):
        return
    inputs = matrix.get('inputs')
    if not valid_inputs(required, inputs):
        return
    digest = matrix_digest(matrix)
    if matrix.get('sha256') == digest and not (run / 'required-verification.lock.json').exists():
        write_json(run / 'required-verification.lock.json', {'sha256': digest, 'required': required, 'inputs': inputs})


def advanced(before, after):
    old_unit, old_ids = before
    new_unit, new_ids = after
    regressed = old_unit in UNITS and new_unit in UNITS and UNITS.index(new_unit) < UNITS.index(old_unit)
    # A stage-name change alone proves nothing. Required current coverage must grow.
    return not regressed and old_ids <= new_ids and bool(new_ids - old_ids)
