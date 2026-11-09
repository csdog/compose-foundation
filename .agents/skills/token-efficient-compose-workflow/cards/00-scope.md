# Stage 00: Scope one component

1. Read `$RUN_DIR/progress.md` and the component checklist entry for `$COMPONENT`.
2. Name the exact implementation files, catalog doc files, official source, and focused test command.
   For each fetched source, require a successful HTTP status and inspect the actual content
   before recording its hash. Use a fetch mode that fails on HTTP errors (for example,
   `curl --fail --location`); reject error bodies such as `404: Not Found` even when their
   hashes match an existing manifest. Verify the path at the pinned revision, including
   nested component directories. Preserve invalid historical evidence and record the
   corrected path, revision, and hash separately.
   For visual/style decisions, read both the component wrapper and its matching pinned
   style CSS; absence of a class in the wrapper does not establish absence of styling.
3. Record those paths under `Artifacts` and replace `Next` with the baseline command.
4. Do not edit production code in this stage.

Failure rule: if the component cannot be identified unambiguously, record the blocker and stop.
If the required read-only official revision lookup fails only because sandbox DNS/network
access is denied, retry that exact lookup once with escalation before declaring the source
unavailable. Never invent a revision.

Print exactly `STAGE_DONE 00-scope` and stop.
