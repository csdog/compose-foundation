# Stage 40: Hand off or close

1. Update only this component's canonical checkbox row, evidence link and summary counts.
   Long narratives belong in the component handoff/evidence, not above the compact checklist.
2. Replace `progress.md` with the final structured handoff; keep it under 150 lines.
3. Record HEAD, uncommitted state, Stage actions/rotations, cumulative input warning, and
   the configured compaction threshold. Claim compaction only when supported by evidence.
4. Validate it with `python3 scripts/token_efficiency.py validate --run-dir "$RUN_DIR"`.
5. Do not edit `progress.log`, `loop_log.jsonl`, or `DONE.marker`; the driver owns them.
6. Do not commit, push, publish, reset, or cleanup unless explicitly authorized. If
   incomplete, state the exact failed gate and next command.
7. Do not rerun Stage 30 or start another component in this session.
8. Reconcile the canonical component checkbox against every required gate. Check it
   only when all applicable acceptance requirements have current evidence and any
   remaining platform differences are explicitly classified. If it remains unchecked,
   record the exact open gate and resume stage under `Next`; this is a handoff only,
   not component completion. Do not describe the component as complete or say `Next: None`.
9. When acceptance is pending, write `$RUN_DIR/acceptance.json` with
   `{"status":"pending","resume_stage":"30-verify","reason":"exact open gate"}`.
   Choose `20-implement` for unresolved implementation or `30-verify` for missing
   current evidence; an earlier stage is allowed only if its inputs are missing.
   The driver uses this checkpoint on the next batch and never replays completed
   units whose evidence is still current. When accepted, set `status` to `accepted`.
10. For acceptance, `acceptance.json` must contain `status: accepted`, a nonempty
    `source_hashes` map of project-relative current relevant Kotlin/source/config paths
    to SHA256, and a `gates` object with tests/lint/build/browser/audit/review entries.
    Every gate must have `status: passed`, `evidence` (a real project-relative file),
    and `sha256` matching that file. The audit and independent review must address
    the full applicable acceptance matrix; local passing assertions alone do not suffice.
    Read-only validation: `python3 -c 'import sys;sys.path.insert(0,"scripts");from pathlib import Path;from codex_stage_orchestrator import acceptance_problem;error=acceptance_problem(Path.cwd(),Path(".agent-runs/COMPONENT"));print(error or "ACCEPTANCE_RECEIPT_VALID");sys.exit(bool(error))'`
    Replace COMPONENT with the assigned slug. Validate before checking the row;
    preserve an exact reproducible failure rather than lowering an acceptance requirement.
11. Bind each gate to its execution inputs. The gate evidence must be a JSON receipt
    with `status: passed`, `inputs` (project-relative manifest file), `inputs_sha256`,
    and nonempty `artifacts` mapping retained report/verdict/log paths to SHA256.
    The input manifest may be a flat path/hash object or `{ "inputs": { ... } }`.
    Its paths/hashes must cover the acceptance `source_hashes`. That source map must
    include all Kotlin under app/shared/src/commonMain, shadcn/core/src/commonMain,
    app/webApp/src, and existing root/module Gradle files, .editorconfig and
    gradle/libs.versions.toml, as well as the component's relevant tests/config.
    Reuse an existing gate only if its recorded execution manifest still matches;
    do not attach freshly computed source hashes to old unbound output. Rerun the
    narrow affected gate when correspondence cannot be established. Review verdicts
    are retained artifacts of the fresh independent reviewer, not self-approval.

Print exactly `STAGE_DONE 40-handoff` and stop.
