# Stage 20: Implement only verified differences

1. Read only the artifacts named by the scope card.
2. Apply the smallest source change that addresses an observed difference.
3. Run one focused compile or test command after the change through the output wrapper.
4. Update `Done`, `Decisions`, `Verification`, and `Next` in `progress.md`.
5. Do not run broad visual or repository sweeps in this stage.

Failure rule: after the same failure twice, preserve the error signature and start a fresh
session instead of continuing speculative edits.

Print exactly `STAGE_DONE 20-implement` and stop.
