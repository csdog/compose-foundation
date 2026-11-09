# Stage 40: Hand off or close

1. Update the canonical parity progress record with a newest-first entry.
2. Replace `progress.md` with the final structured handoff; keep it under 150 lines.
3. Record HEAD, uncommitted state, Stage actions/rotations, cumulative input warning, and
   the configured compaction threshold. Claim compaction only when supported by evidence.
4. Validate it with `python3 scripts/token_efficiency.py validate --run-dir "$RUN_DIR"`.
5. Do not edit `progress.log`, `loop_log.jsonl`, or `DONE.marker`; the driver owns them.
6. Do not commit, push, publish, reset, or cleanup unless explicitly authorized. If
   incomplete, state the exact failed gate and next command.
7. Do not rerun Stage 30 or start another component in this session.

Print exactly `STAGE_DONE 40-handoff` and stop.
