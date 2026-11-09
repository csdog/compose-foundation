---
name: token-efficient-compose-workflow
description: "Use for long Compose Foundation/shadcn parity batches when token cost or context growth matters. Forces one component per fresh execution session, compact file handoffs, masked command output, bounded concurrency/reasoning, stable prompt prefixes, and deterministic verification."
license: Apache-2.0
metadata:
  author: angryecho
  version: "1.0.0"
  keywords:
    - token-efficiency
    - context-compression
    - observation-masking
    - compose-foundation
    - shadcn-parity
---

# Token-Efficient Compose Workflow

Optimize tokens per completed component, not requests per second or cache percentage.
For usage investigation and generic optimization audits, use the
`token-optimization-analysis` Skill first; this Skill owns the Compose execution cards.

## Mandatory Rules

1. Execute one component per session. If five components remain, use five short runs,
   not one 600-step conversation.
2. Keep at most two workers active. Partition only independent components. Parallelism
   trades wall-clock time for duplicated instructions and context.
   Serialize live source edits with Gradle validation, not just Gradle processes. While
   a shared source set is compiling, testing, or linting, freeze its live sources for
   every worker. Other workers may review or stage patches under `.agent-runs/`; apply
   a complete coherent patch only after the validation owner releases the source set.
   Grant source editing and Gradle execution explicitly, and archive results before release.
3. Use `high` reasoning only for design judgments or unresolved interaction defects.
   Use `off`/`low` for discovery, formatting, command execution, and routine verification;
   use `medium` for implementation.
4. Keep the stable prefix stable. Do not change model, tools, skill list, system prompt,
   or reasoning level inside a run unless a failed attempt proves it is necessary.
5. Do not paste raw logs, DOM trees, XML, screenshots, or full source files into chat.
   Store them under the run directory or parity evidence directory and report only the
   path, summary, and relevant tail.
6. Restart after a component is verified. Also restart after 250k context tokens for
   cost-sensitive work, after two no-progress turns, or after repeatedly re-reading the
   same file. Quality-first work may use 400k, but a natural component boundary is better.
7. Let scripts make deterministic pass/fail decisions. The model reviews failed tests and
   exceptions; it does not manually inspect everything a script can check.

## Single scheduler for unattended batches

Use `scripts/run_unfinished_components.py --execute --background` for unattended
component queues. Python owns queue progression, waiting, retries and persistence.
Do not assign a second agent to coordinate the queue while the parent also watches it.
Stage workers execute their assigned work directly and return evidence or a blocker;
they do not spawn coordination agents or send progress-only messages. The exception
is one bounded fresh read-only reviewer session when plan R10/R15 requires independent
review; pass file/diff/evidence paths only and persist a real verdict. The reviewer
cannot delegate or schedule work; never retry an unchanged acknowledgement-only transport.

Do not run a model loop calling `wait_agent`, `list_agents`, `--status`, or log tails
when no decision is pending. The background receipt identifies the log and PID;
read compact status on a user request or a meaningful completion/failure signal.
There is no automatic chat notification transport; do not promise automatic wakeups.
For work requiring interactive delegation, use one scheduling owner, bounded workers
and terminal/blocker reports only. Do not create a nested coordinator. Shared source
editing and validation serialization rules still apply.

Measure the entire explicitly linked agent tree, rather than only the parent:

```bash
python3 scripts/agent_tree_usage.py --thread <root-thread-id>
```

Use `--details` only to investigate expensive sessions. The summary reports raw,
cached and uncached input, output, and coordination attribution separately. Attribution
is usage following coordination calls, not a claim that all of it can be eliminated.

## Start a Run

From `compose-foundation/`:

```bash
RUN_DIR=.agent-runs/<component>
COMPONENT=<component>
python3 scripts/token_efficiency.py init \
  --run-dir "$RUN_DIR" \
  --component "$COMPONENT" \
  --goal "Complete and verify <component> parity without unreported differences."
python3 scripts/token_efficiency.py validate --run-dir "$RUN_DIR"
```

In the fresh execution session, start by reading:

1. This skill.
2. `$RUN_DIR/progress.md`.
3. The current stage card under `cards/`.
4. Files explicitly named by the stage card.

Then perform bounded read-only discovery required by that card: owning component
sources, checklist entry, commands.log, manifests, pinned upstream files and evidence.
This starting list does not prohibit inspecting the files the card requires. Keep
searches scoped to the current component and stage. Do not load the previous session transcript.

## Stage Cards

Run the cards as separate sessions:

- `cards/00-scope.md`
- `cards/10-baseline.md`
- `cards/20-implement.md`
- `cards/30-verify.md`
- `cards/40-handoff.md`

Each card ends with `STAGE_DONE <card-id>`. A stage session stops after writing its facts
and evidence; it does not continue into the next card.

## Command Output Masking

Use the wrapper for every command that can produce more than 80 lines:

```bash
scripts/agent_run.sh "$RUN_DIR" focused-tests -- \
  ./gradlew :app:shared:jvmTest --tests '*Chip*'
```

The full log is written to `$RUN_DIR/logs/`. Only the final 80 lines enter model context.
For a failure, inspect targeted lines from the saved log instead of opening the whole file.

## Handoff Contract

`progress.md` is the only durable working memory. Keep it under 150 lines and preserve:

- exact goal and acceptance check
- verified completed work
- current stage and blocker
- decisions and rejected approaches
- every modified artifact with a precise path
- exact verification commands and results
- ordered next actions
- open questions

Before starting the next session, run:

```bash
python3 scripts/token_efficiency.py validate --run-dir "$RUN_DIR"
```

While a DSH session is active, check its current prompt and tool-output share:

```bash
python3 scripts/token_efficiency.py check-session \
  --session "$DSH_SESSION_ID" --max-prompt 300000
```

Exit code `2` means compact or restart after saving the handoff.

## Stop Conditions

- Component acceptance passes and evidence paths are recorded: run the handoff card.
- Context pressure reaches 70%: mask resolved tool output, update the handoff, start fresh.
- The same failure occurs twice: record the exact signature and stop editing the same code.
- Three consecutive turns produce no new evidence: stop and request direction.

## Measurement

Report `tokens/component`, model calls, median/p95 prompt size, cache hit rate, wall time,
and one quality outcome. The optimization is successful only when the component still
passes with less total work.

Analyze prior DSH runs with:

```bash
python3 scripts/token_efficiency.py report \
  --cwd /Users/cscs/IdeaProjects/angryecho
```

Context caps in that report are counterfactual estimates, not guarantees. A cap that loses
artifact paths or failure details is invalid because it causes re-fetching.

## Codex Driver

Codex uses the TAO stage-card contract through:

```bash
python3 scripts/codex_stage_orchestrator.py \
  --component <component-slug> --component-name "<Component Name>"
```

The command is a dry run unless `--execute` is supplied. For headless unattended runs,
add `--execute --approve-for-me`.

The driver:

- routes from the last `<stage> ok` line in `.agent-runs/<slug>/progress.log`;
- starts one fresh `codex exec --json` session per Stage attempt;
- injects only `progress.md`, the recent `progress.log` tail, and the recent
  `commands.log` tail;
- writes attempt telemetry to `.agent-runs/<slug>/loop_log.jsonl`;
- appends `<stage> rotate` when a new session is required for the same Stage;
- appends `<stage> FAIL` on a committed failure and never auto-retries that failure;
- writes `.agent-runs/<slug>/DONE.marker` only after `40-handoff` passes and the
  canonical component checkbox is checked; otherwise records `HANDOFF.marker` and
  `acceptance_pending`, and a later batch resumes open implementation/verification;
- defaults to Stage-specific hard budgets: scope 30, baseline 30, implement 50, verify
  60, handoff 40; `--max-actions` overrides all Stages;
- defaults to a 600-second wall-clock watchdog per session; `--max-seconds 0` disables it;
- defaults to a 250k per-prompt target and a 2M cumulative Stage-input warning.
- passes Codex CLI `model_auto_compact_token_limit=250000` and
  `model_auto_compact_token_limit_scope="body_after_prefix"` to every headless
  session. Use `--compact-threshold 0` to retain the host/model default.

`30-verify` is split into recoverable work units: `tests`, `build`, `browser`, and
`audit`. A session executes at most one unchecked unit, records its evidence, and rotates
with `CONTEXT_HANDOFF 30-verify`. The 60-action budget applies to one unit, not the whole
verification Stage.

For repeated mechanical checks, execute one bounded batch script or command sequence that
writes detailed logs and screenshots to disk and prints a compact summary. Do not issue one
tool call per assertion. PTC is a host tool-presentation mode, not a prompt mode: do not
request or simulate `run_code`/PTC unless the active runtime explicitly exposes that
transport and its generated SDK.

`turn.completed.usage.input_tokens` from Codex is cumulative Stage input, not the
current prompt size. Do not compare it directly with the 250k context target. Use it only
as the cumulative Stage warning/metric.

After a committed `FAIL`, the default `--start-stage auto` halts. An operator may resume
only after inspecting `progress.md` and the saved event log, by passing the failed Stage
explicitly and, when justified, a larger `--max-actions` budget.
