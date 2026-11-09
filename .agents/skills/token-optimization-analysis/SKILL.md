---
name: token-optimization-analysis
description: Analyze and reduce token usage for long coding-agent tasks, including Codex/DSH usage, fixed context overhead, tool-surface and progressive-loading optimization, duplicate file reads, verbose tool output, fresh-session handoffs, and cheap subagent routing. Use when the user asks why tokens are high, how to optimize an agent workflow, including dsh-ops-style tool compaction, or how to compare token efficiency without losing task quality.
---

# Token Optimization Analysis

Optimize **tokens per completed task**, not cache percentage or tokens per request. Preserve
the user's requested outcome and verification scope.

## Boundaries

- An analysis-only request is read-only. Do not edit agent configuration or install tools
  unless the user asks for an implementation.
- Do not optimize by truncating evidence needed to finish the task. Full logs and large tool
  results belong on disk; retain a compact reference plus the relevant tail.
- Do not treat high cache-hit rate as low cost. Report raw, uncached input, output, cache
  reads, model calls, and the quality outcome separately.
- Do not create a fresh session merely because a threshold exists. Preserve the handoff
  needed to continue first.

## Workflow

1. **Establish current state.**
   Run a read-only audit before recommending changes. Inspect the actual session and current
   workspace rather than relying on an old transcript.

   ```bash
   python3 .agents/skills/token-optimization-analysis/scripts/token_audit.py audit \
     --root . --codex-session latest --dsh-project . --json
   ```

   Omit `--codex-session` or `--dsh-project` when that source does not apply.

2. **Audit fixed context overhead.**
   Separate:
   - actual first-prompt/context size from the session;
   - repo-visible instruction files and Skill bodies;
   - tools, MCP schemas, plugins, and model system prompt, which may not be reconstructible
     from the repo.

   Classify tool overhead into always-on schema tokens, on-demand discovery tokens, and
   per-call observation tokens. Disable unused MCP/plugins before a task. Keep always-loaded
   instructions lean and move conditional detail into progressively loaded references.

3. **Audit repeated and oversized observations.**
   Review possible duplicate reads and the largest tool outputs reported by the script.
   A repeated read is suspicious only when the file is unchanged or the first result is still
   relevant. Use `rg`/LSP to locate, then read only the required line ranges.

4. **Route the next work by risk and context.**

   ```bash
   python3 .agents/skills/token-optimization-analysis/scripts/token_audit.py route \
     --task "Locate the failing tests and summarize the first error"
   ```

   Use the routing guidance in [references/routing.md](references/routing.md). Cheap
   subagents are for read-only exploration and deterministic command execution, not final
   architecture, security, or ambiguous product decisions.

5. **Apply optimizations in this order.**
   - right-size the tool surface, then progressively load rare or heavy capabilities;
   - mask completed tool output and store the full artifact;
   - remove unused tools/plugins and irrelevant instructions;
   - narrow retrieval and avoid full-file rereads;
   - route heavy read/test work to a bounded cheaper subagent;
   - structure/compact remaining state;
   - start a fresh session at a natural task boundary.

   Keep the model, tool set, system instructions, and stable prefix unchanged within a run.

6. **Use proportional context thresholds.**
   - Proactively prepare a handoff when utilization reaches 60-70% or prompt size reaches
     250k for cost-sensitive work.
   - Stop and restart at 80% utilization or when a configured 300k prompt check returns exit
     code 2.
   - For DSH, use the existing project script:

     ```bash
     python3 scripts/token_efficiency.py check-session \
       --session "$DSH_SESSION_ID" --max-prompt 300000
     ```

7. **Write a durable handoff.**
   Preserve the goal, acceptance checks, changed files, decisions, failed signatures,
   evidence paths, remaining work, and open risks. See
   [references/audit-playbook.md](references/audit-playbook.md).

8. **Verify with the same outcome.**
   Compare before/after on tests, browser/runtime evidence, model calls, raw tokens,
   efficient tokens (`uncached input + output`), prompt median/p95, tool-output share, and
   cache reads. If quality differs, report an estimated optimization, not a measured win.

## Tool-Surface Optimization

Use the dsh-ops pattern when cost is dominated by file inspection, document reads, large tool
schemas, Windows shell error loops, or PTC code generation. The principles generalize beyond
the plugin:

1. **Inventory tools before changing them.** Record each tool's schema size, discovery cost,
   typical output size, call frequency, and whether it is needed for the current task.
2. **Hide unused stock tools.** A smaller always-on tool surface saves schema tokens on every
   request. Keep compatibility tools available only when the workload actually needs them.
3. **Load heavy capabilities on demand.** PDF, DOCX, CSV/TSV, bulk-edit, and other rare tools
   should enter the tool catalog only after the agent requests that capability.
4. **Use compact, bounded observations.** Prefer path, line number, range, count, error code,
   and a targeted excerpt over full files, full tables, or unfiltered command output.
5. **Batch independent operations.** Process multiple known files or records per call when
   the tool contract supports it, but keep per-item failures inspectable.
6. **Return result references.** Large results should be stored and referenced instead of
   repeatedly copying the same payload into later model requests.
7. **Prefer typed structured calls over generated glue code.** A PTC or typed SDK should
   expose the operation directly, reducing generated TypeScript/Python, type errors, and
   retry turns.
8. **Improve error payloads.** Return enough context for the next action, but not the whole
   log. Better errors are useful only when they reduce retry turns.
9. **Select a task-specific mode.** Use the smallest native tool surface for ordinary work,
   structured APIs for batch/document operations, and compatibility mode only when required.
10. **Pin and validate the implementation.** Verify host compatibility, cache cleanup, disk
    growth, permissions, cancellation, and upgrade behavior before relying on a plugin.

Measure this optimization separately with:

- always-on tool/schema tokens per request;
- observation tokens per tool call;
- tool calls and retry loops per completed task;
- generated-code tokens for PTC-heavy work;
- repeated result bytes avoided by references;
- final efficient tokens (`uncached input + output`) with the same quality outcome.

This optimization is workload-specific. If most cost comes from accumulated conversation
history, reviewer duplication, or repeated builds, tool compaction will move the number very
little. `dsh-ops` and similar plugins are not session orchestrators, subagent role systems, or
build daemons. Do not attribute their schema/output savings to those layers, and do not accept
published savings claims without a same-outcome measurement.

## Existing Project Workflows

- For long Compose parity batches, continue with
  `token-efficient-compose-workflow`: one component per fresh session and Scope → Baseline →
  Implement → Verify → Handoff cards.
- For DSH historical usage and handoff validation, reuse `scripts/token_efficiency.py`; do
  not fork its accounting logic.
- For DSH tool-surface reduction, evaluate `dsh-ops` through the measurements above. Its
  compact native tools, hidden stock tools, `tools_addition` progressive loading, batched
  document operations, result references, and PTC SDK target different token pools; report
  them separately from fresh-session and subagent savings.
- For generic context theory, the installed `context-optimization` and
  `context-compression` Skills remain supporting references.

## Acceptance

An optimization is accepted only when:

- the requested task still passes its original checks;
- the full outcome is preserved, not replaced by a smaller proxy task;
- all reported savings use explicit token definitions;
- any mismatch between the compared runs is stated;
- the next session can continue from the handoff without rereading the old transcript.
