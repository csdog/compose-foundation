# Token Audit Playbook

Read this reference when performing a full token investigation or comparing two workflows.

## Definitions

- `raw tokens`: all prompt input plus output, including cache reads.
- `uncached input`: prompt input served without cache.
- `efficient tokens`: uncached input plus output.
- `cache hit`: cache reads divided by total prompt input. It is a discount signal, not a
  task-efficiency metric.
- `tokens/task`: total model usage from task start through accepted completion, including
  retries and handoff overhead.

## Evidence Order

1. Real session usage records.
2. Test/build/runtime outcome for the same task.
3. Repo-visible fixed-context files.
4. Tool-call output sizes and repeated reads.
5. Counterfactual context-cap estimates, clearly labeled as estimates.

## Fixed Context Audit

Measure the first prompt from the actual session when available. Report separately:

- system prompt and tool schemas;
- MCP and plugin tool surface;
- memory/instruction files (`AGENTS.md`, `CLAUDE.md`, settings);
- enabled Skill descriptions and loaded Skill bodies;
- user task and current-turn payload.

Repo files cannot prove the complete provider-side system prompt or active tool schemas.
Do not fabricate a breakdown that was not observable.

Optimize large volatile prefixes before stable content. Avoid changing tools, models, or
instructions mid-session because that can invalidate prompt-cache reuse.

## Duplicate Read Audit

A repeated path is a candidate, not automatically waste.

Treat it as likely waste when:

- the file did not change between reads;
- the same line range is read again;
- the read is only to recover information already captured in the handoff;
- a broad search output is reread instead of referencing the saved artifact path.

Do not mask a changed file, an active error, security evidence, or a new line range.

## Measurement Protocol

For an A/B comparison, record:

| Field | Why |
| --- | --- |
| task and acceptance check | proves same outcome |
| calls | exposes retry/loop cost |
| raw tokens | total repeated context |
| uncached input | new model work |
| output tokens | generated work |
| cache reads | repeated cached context |
| median/p95 prompt | context growth |
| tool-output share | observation bloat |
| wall time | latency tradeoff |
| quality result | prevents token-only optimization |

Report a range when runs differ in scope or when only one run is measured. Never call a
raw-token reduction a cost reduction when quality or completion differs.

## Optimization Order

1. Stop irrelevant retrieval.
2. Disable unused tool/plugin surfaces.
3. Mask completed observations.
4. Deduplicate unchanged reads.
5. Route read-only exploration and deterministic test running.
6. Compact or hand off unresolved state.
7. Change the execution architecture only when the above are insufficient.

## Handoff Template

```markdown
## Goal
## Acceptance
## Verified done
## Current state
## Decisions
## Artifacts and evidence paths
## Failed signatures and risks
## Next actions
## Open questions
```

Keep exact paths, commands, error codes, and identifiers. Do not copy raw logs.
