# Batch execution and token accounting

Run from the compose-foundation project directory:

```sh
python3 scripts/run_unfinished_components.py --limit 3 --execute --background
python3 scripts/run_unfinished_components.py --status
python3 scripts/run_unfinished_components.py --watch --interval 2
python3 scripts/run_unfinished_components.py --repair-usage
```

Background launch returns a PID and log path immediately. The Python driver waits
and advances components without model calls. Detailed driver output goes to
`.agent-runs/<component>/batch-driver.log`; batch transitions go to the receipt log.
Read status on demand or at a meaningful checkpoint instead of polling with a model.
There is no automatic chat notification transport. Status is the last persisted
snapshot, not proof the process is still alive; consult the receipt log on interruption.
A project lock prevents overlapping batch execution and usage repair.

Interrupted sessions recover cumulative usage from their own local Codex session
log (`CODEX_HOME/sessions`, default `~/.codex/sessions`). Snapshots are never added
together. Records include `usage_source` and `usage_complete`; recovered interrupted
usage remains incomplete because the last in-flight request may never report usage.
Unavailable usage is marked explicitly rather than assumed to be a measured zero.
Historical repair updates both runs.json and loop_log.jsonl and is idempotent.
It does not launch models or rerun verification.

## Agent coordination

Use the Python driver as the sole unattended scheduler. Stage prompts explicitly
prohibit nested agents and progress-only coordination. Do not layer a model polling
loop over the background driver. Interactive review, when needed, has one scheduling
owner and terminal/blocker reports, without a second coordinator.

```sh
python3 scripts/agent_tree_usage.py --thread ROOT_THREAD_ID
```

This read-only report follows explicit parent links across local active and archived
sessions. It excludes unlinked and auxiliary sessions, marks missing usage, and emits
a compact total by default. Add `--details` for per-agent counters. Coordination
attribution is diagnostic, not estimated savings. These are workflow/prompt controls;
they do not disable collaboration tools in the Codex host or alter already running chats.

## Live progress without token usage

Run `--watch` in a terminal to display batch counts, current component, stage,
action count, last event time and the latest errors. It refreshes from disk every
two seconds and prints only changes. Ctrl-C closes the display without stopping
work. `--status` returns the same data as JSON for integrations. No model or agent
is invoked by either display. Open this in a terminal, not a model-driven polling loop.
The batch lock distinguishes active work from a stale saved `running` record.
The event time is the last observed event, not a synthetic heartbeat or an ETA.

Database and in-process app-server initialization failures stop the queue as
infrastructure faults, preserving the component handoff. They do not authorize
bypassing host permissions. Existing startup permission errors still require a
working execution environment before a real batch can resume. A background receipt
reports early exit codes and labels a surviving process as `process_started`, which
is not a guarantee the model runtime is ready.

### Starting from a restricted agent shell

The outer Codex CLI host needs its normal user access to `CODEX_HOME` databases
and the local app-server. Starting the batch inside a restricted tool sandbox
can fail before a worker exists. Launch the outer batch through the tool's
approved `require_escalated` execution path, or from the user's normal terminal.
Keep worker `--approve-for-me` / workspace-write policy intact. Do not change
filesystem permissions, copy authentication files, or disable sandboxing to work
around host initialization errors. Validate a real `thread.started` plus tool
activity in `--watch`; a PID receipt alone does not prove successful startup.

For historical failures, `recover_transient_failure` appends a `rotate` only when
the matching saved event log proves an infrastructure fault. Preserve the old FAIL
for auditing; do not clear product blockers or invent completed-stage marks.

Background launches also attach a lightweight progress mirror to their receipt log.
It appends `[PROGRESS]` blocks on state changes, reads disk only, and exits when the
batch lock is released. An existing batch can attach the same mirror using
`scripts/batch_progress.py --log <receipt-log>`; a per-log lock prevents duplicate
mirrors. A file viewer may need refresh; `tail -f <receipt-log>` follows updates live.

### External blockers versus continuation

Workers must end with `STAGE_BLOCKED <stage>` when a shared gate requires an
external repair. The preceding line is the exact blocker reason. This is terminal
for the current component, rather than a fresh-session retry. For legacy handoffs,
two consecutive `30-verify` continuations without advancing the ordered `## Next`
work unit stop with an explicit no-progress reason. A completed unit may still
rotate normally. Preserve FAIL records; after inspecting and repairing a gate,
an operator may append a rotate mark to resume that stage with evidence.
