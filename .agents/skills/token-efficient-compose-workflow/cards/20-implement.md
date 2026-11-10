# Stage 20: Implement only verified differences

1. Read only the artifacts named by the scope card.
2. Apply the smallest source change that addresses an observed difference.
3. Run one focused compile or test command after the change through the output wrapper.
   For multiple Gradle test tasks, put each task's `--tests` options directly after
   that task; never leave another task running its full unrelated suite.
   If this run already records the current macOS AWT/AppKit registration abort
   before assertions and an identical approved retry passed, execute subsequent
   focused native JVM wrapper commands through approved escalation from the outset.
   Keep the worker sandbox, exact task/filter scope, and cache grants. Do not reproduce
   the same known startup denial in every fresh session or treat assertion failures
   as permission failures. New or changed failure signatures require diagnosis.
4. Before marking implementation done, run owning ktlint tasks for every changed
   Kotlin source set. Fix formatting in this component's modified production/test
   files here; do not defer it to a verifier that cannot edit source. Preserve exact
   rendered strings when wrapping prose. Record tasks, result, relevant input hashes
   and compact evidence paths. Reuse a passing result only when its inputs match.
   An unresolved owning lint gate is STAGE_BLOCKED 20-implement, not completion.
5. Update `Done`, `Decisions`, `Verification`, and `Next` in `progress.md`.
6. Do not run broad visual or repository sweeps in this stage.

Failure rule: after the same failure twice, preserve the error signature and start a fresh
session instead of continuing speculative edits.

Print exactly `STAGE_DONE 20-implement` and stop.
