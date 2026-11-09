# Stage 30: Verify the component

Execute at most one unchecked work unit in this session. Record the unit status and exact
evidence paths under `Verification` in `progress.md`. Never rerun a unit already recorded
as passed unless the current worktree changed or new evidence contradicts it.

Work units:

1. `30a-tests`: run the focused JVM test suites and owning ktlint tasks through
   `scripts/agent_run.sh`. Use one command for tests and one for lint; do not enumerate
   individual tests when the focused task already covers them.
2. `30b-build`: run the current-worktree build required by the component, including the
   Wasm distribution when browser evidence is required. Reuse a recorded successful build
   if the relevant source hash is unchanged.
3. `30c-browser`: prefer one bounded verification script that performs all required
   desktop/phone, light/dark, DOM/dimension, and interaction checks; it must write
   screenshots and a compact summary to disk. Do not issue one tool call per assertion.
   Reuse an existing current-worktree verifier when available instead of writing another.
   If the existing script is stale, patch it and rerun once.
   Spend at most 5 actions detecting the available browser runtime. If no reusable
   verifier exists, create one bounded script, run it once, and inspect only its compact
   summary plus failed assertions. Do not alternate between server startup, ad-hoc browser
   probes, and screenshot inspection when the batch script can cover them.
   If GUI Ego cannot start because the Mac is locked or Chromium Mach services are denied,
   do not relaunch it. Switch once to an installed headless Playwright runtime and request
   escalation for the single bounded verifier command. A sandbox-only permission failure
   is not a product blocker until that escalated batch retry fails.
4. `30d-audit`: consume the test XML, logs, build hash, browser summary, and source hashes.
   Compare deterministic evidence before visual judgment. Do not repeat any passed unit.

After completing `30a-tests`, `30b-build`, or `30c-browser`:

1. Update `progress.md` with pass/fail, counts, and evidence paths.
2. Put the next unchecked unit first under `Next`.
3. Run the handoff validator and `git diff --check`.
4. End with exactly `CONTEXT_HANDOFF 30-verify`.

Keep `progress.md` under 140 lines at every checkpoint so the next unit has room for its
evidence. Summarize repeated baseline/implementation results instead of copying them.

Complete `30d-audit` only when all required evidence is present. If a difference is
unexplained, record the assertion plus artifact path and keep the component incomplete.
Never write `progress.log`, `loop_log.jsonl`, or `DONE.marker`; the driver owns them.

After `30d-audit` passes, print exactly `STAGE_DONE 30-verify` and stop.

## Blocked gates

If a required shared gate fails outside this component's authorized scope, record
its exact failure and evidence, then end with `STAGE_BLOCKED 30-verify`.
Put a one-line blocker reason immediately before that marker. Do not use
CONTEXT_HANDOFF for an unchanged external blocker. A fresh context cannot repair
an out-of-scope failure. The scheduler must repair the shared gate before resuming.
