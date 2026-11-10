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

Worker sessions using workspace-write/automatic review explicitly receive
`--add-dir "$GRADLE_USER_HOME"` (default `~/.gradle`). This permits Gradle wrapper
and dependency cache writes without making the rest of the home directory writable.
Read-only sessions do not receive this grant. Other permission failures must retry
the identical required command once through automatic approval before being called
an environment blocker. Blank lines before STAGE_BLOCKED are ignored when extracting
the displayed reason; existing records can fall back to the saved final message.

For blocked markers, the parser prefers an explicit `Blocker:` line and then the
actual failure statement, ignoring successful validator footers. The progress log
emits unchanged blocker details once per mirror process, while keeping blocked
counts visible. Initial prompt reads are not an authorization boundary: bounded
read-only discovery required by the current card remains allowed. Before treating
archived failures as current, compare owning inputs or rerun the narrow gate once;
preserve superseded results as historical evidence.

A blocked run whose matching TAO checkpoint has been explicitly reopened with a
`rotate` mark is displayed as `RECOVERY_PENDING`, not an active ERROR. The old
failure remains in the JSON recovery record and original logs. A later FAIL becomes
an active error again. This label means queued recovery, never successful completion.
Batch stdout and progress mirrors both use append mode to preserve interleaved lines.


## Component acceptance and checkpoint recovery

`40-handoff` completion is separate from component acceptance. The driver writes
`DONE.marker` only when the canonical component checkbox is checked; otherwise it
writes `HANDOFF.marker`, returns exit 4, and displays `ACCEPTANCE_PENDING`. Batch
completion/exit 0 also requires canonical acceptance, including for older child
processes that return 0. Unchecked components with legacy DONE markers stay queued.
The handoff card records the next gate in `acceptance.json` (`status: pending`,
`resume_stage`, `reason`); the next batch uses that stage. Missing legacy checkpoints
reopen implementation to reconcile checklist residuals rather than invent acceptance.

An operator may call `reopen_checkpoint(run_dir, stage, reason)` after inspecting
and repairing the recorded blocker, while holding `.agent-runs/batch.lock` with no
component worker active. It preserves old logs, moves legacy DONE to HANDOFF, writes
`recovery.json`, and appends an audited rotate mark. Auto-routing respects the latest
rotate stage even if a later-numbered stage had completed historically. Product FAILs
are never blanket-cleared by normal batch startup. Mandatory plan R10/R15 review may
use one fresh bounded read-only reviewer; it must return findings/verdict, not an
acknowledgement or a new coordination loop.


### Native JVM cache grants

Workspace-write workers also receive precise writable grants for `~/.skiko` and
`~/Library/Application Support/kotlin/daemon`, alongside `GRADLE_USER_HOME`. Native
Skiko extraction locks and Kotlin daemon marker writes happen outside Gradle's own
cache; an escalation retry must not be mistaken for a verified cache-policy change.
Use `--no-daemon` on verification commands to avoid reusing a Gradle daemon started
with a narrower sandbox policy. These grants do not expose the full home directory
and are omitted for read-only reviewers. A bounded worker smoke probe creates and
deletes only its own temporary files in the three granted cache directories.

Blocker extraction recognizes `阻塞:` / `阻塞：` and Chinese failure statements as
well as English markers; successful reviewer/validator summaries are not blockers.

A successful cache write probe is not a passing native JVM test. macOS AWT/AppKit
application registration can abort with exit 134/SIGABRT before assertions under
the worker sandbox. Inspect its compact native signature and perform the one
approved exact-command retry with `--no-daemon` before declaring an environment
blocker. Never infer test counts from stale XML after an abnormal executor exit.


### Verification coverage feedback

A read-only audit that finds unperformed applicable checks routes `Next` back to
30a/30b/30c and returns CONTEXT_HANDOFF. Missing evidence is assigned verification
work; it is not an external blocker. Confirmed production failures and actual
external/shared gate failures remain terminal blockers.

Browser coverage can span sessions. `verification-progress.json` stores stable
completed check IDs, passed status, run-relative evidence paths and SHA256 values.
The driver counts only new completed IDs whose files exist under the run directory
and still match the recorded hashes; this is progress telemetry, never acceptance.
Two handoffs with neither unit advancement nor new verified coverage still stop.
If the session cap is reached with verified progress, exit 5 retains a rotate
checkpoint and displays CHECKPOINT_PENDING rather than committing a false FAIL.
The batch can continue other components and the next batch resumes that checkpoint.


## Reverse completion mode

`python3 scripts/run_unfinished_components.py --reverse --repair-blocked --complete-components --execute --background`

Reverse applies before limits/start-after. Completion mode owns one component until
canonical acceptance passes, and routes a verifier's recorded product/shared/harness
blocker to a fresh implementation/fixer session. It preserves valid verification
checkpoints and current evidence. Missing evidence returns to verification. Component
passes are bounded (default 6); unresolved work stops the queue for attention instead
of scanning every component and accumulating skipped failures. No scheduler edits or
acceptance-rule reductions are authorized for stage workers.

The strict handoff requires acceptance.json with accepted status, tests/lint/build/
browser/audit/review passed gate receipts (project-relative evidence plus SHA256), and
current relevant source_hashes including component Kotlin. Missing/changed proof
restores the checkbox to unchecked and retains a verification checkpoint. The
canonical checkbox and summary count are synchronized only after receipt validation;
existing historical checkmarks are not blanket reaccepted.


### Verification-only failure recovery

A fixer reporting a terminal blocker is not blindly rerun by completion mode.
After inspecting a verification-only blocker, the operator may write
`failure-routing.json` with `kind: verification_only`, `resume_stage: 30-verify`,
`failed_stage`, and SHA256 bindings named `progress_sha256`, `message_sha256`,
`record_sha256`. These bind the complete progress.log, blocked message bytes and
latest runs.json record (JSON serialized with sorted keys), respectively.
Stage workers must not infer or grant this operator route from free-form wording.
A new FAIL, changed message or new record invalidates the route. Missing or stale
routes retain repair behavior. Historical FAIL/evidence are preserved; routing is
not gate acceptance. Operators can alternatively reopen an inspected checkpoint
under the batch lock using the existing recovery API.


## Persistent component limits (2026-11-11)

Both batch and direct orchestrator execution enforce the same defaults: 24 sessions,
20,000,000 cumulative input tokens (cached input included), and 7,200 active seconds
per component. `--max-component-sessions`, `--max-component-input-tokens` and
`--max-component-seconds` explicitly set the limits; changing a limit never clears
spent usage. `execution-budget.json` reserves a launch before starting a worker.
A crashed reservation is conservatively charged in full. Existing runs are imported
once; missing historical durations are conservatively estimated at 600 seconds per
session, marked `historical_time_estimated`. This can stop old over-budget components
immediately. Never delete the ledger to recover a checkpoint.

Exit 6 means cumulative budget exhaustion, not success or ordinary blocked repair.
The batch stops at that component. The current session receives only the remaining
time/input allowance; observed CLI/session counters are sampled without model calls
(every five seconds for session logs). Input limits act at observed request boundaries:
an in-flight request can overshoot before its usage is reported. Missing/incomplete
usage is conservatively charged against the reserved allowance. `input_tokens` in the
budget file is the charged budget, with `observed_input_tokens` and
`estimated_input_tokens` displayed separately; original runs.json usage remains the
measured usage source and is never overwritten by these estimates. All handoffs emit
input warnings; warnings are separate from the persistent hard stop. Direct execution
holds a component lock, preventing simultaneous launches against its state.

Continuation requires a frozen finite mandatory coverage matrix and current relevant
input hashes (see the Stage 30 card). Legacy unbound ledger IDs cannot extend a run.
A higher stage name alone, regression, or added optional diagnostic is not progress.
Two stalled handoffs persist across restarts. Status displays settled cumulative usage,
limits, remaining mandatory checks and the last effective advancement; unknown coverage
is printed as unknown, never zero. Completed evidence remains reusable per check when
its relevant inputs match.

`failure-inputs.json` remembers blocked-stage input fingerprints across process restarts.
Rewording progress does not permit a retry. Frozen relevant inputs are used where present;
otherwise the source/config tree is conservatively fingerprinted. Legacy failures without
a signature stop and bind first, rather than assume remediation. A failed verification
may enter its owning implementation stage once; a blocked fixer cannot repeat unchanged.
For diagnosed environment blockers, `environment-inputs.json` may list actual toolchain
receipt files/cache directories whose contents or permission/ownership changes are relevant.
Use measured environment inputs, not a timestamp-only retry note. These files do not waive
any acceptance gate. No queue restart or operator budget increase is automatic.
