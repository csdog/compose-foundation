# Stage 10: Establish the baseline

1. Run one focused component test command through `scripts/agent_run.sh`.
2. Do not start a Web dev server, run browser automation, build a Wasm distribution, or
   perform screenshots in this Stage. Those belong to `30-verify`.
3. Do not run a whole module/repository test suite when class filters can isolate the
   component. If filters bleed into unrelated pre-existing failures, record them as
   baseline environment noise rather than repairing other components here.
4. Record pass/fail counts, the first actionable component failure, and artifact paths in
   `progress.md`.
5. Stop after the focused test result. Do not broaden unless the focused target itself
   cannot compile.

Failure rule: preserve the saved full log and record the exact error signature. Do not
re-read the whole log into context.

Print exactly `STAGE_DONE 10-baseline` and stop.
