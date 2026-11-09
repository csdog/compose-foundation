# Badge / Alert / Separator token 效率最终报告

- Date: 2026-11-07
- Scope: Badge, Alert, Separator parity completion
- Status: all three component acceptance checks pass
- Definition: raw = uncached input + cache-read input + output; efficient = uncached
  input + output

## Component result

| Component | Core tests | Catalog and page tests | Total | Browser evidence |
| --- | ---: | ---: | ---: | --- |
| Badge | 17/17 | 8/8 | 25/25 | 1440/390 × light/dark |
| Alert | 14/14 | 6/6 | 20/20 | 1440/390 × light/dark |
| Separator | 14/14 | 34/34 | 48/48 | 1440/390 × light/dark |

The final combined focused run passed all 93 checks. The production source sets for both
`shadcn:core` and `app:shared` pass ktlint. The subsequent lint cleanup also makes the full
`:shadcn:core:ktlintCheck` pass, including the previously failing `TraceParityTest.kt`,
`ContextMenuPositionTest.kt`, and `commonMain` source set.

## Token accounting

| Run | Calls | Raw tokens | Efficient tokens | Cache hit | Outcome |
| --- | ---: | ---: | ---: | ---: | --- |
| Historical Compose parity | 5,121 | 1,768,786,909 | 9,315,677 | 99.632% | historical multi-component corpus |
| Badge DSH pilot | 157 | 9,353,674 | 268,874 | 97.694% | complete with Codex follow-up |
| Alert DSH quota failures | 17 | 595,815 | 86,503 | 86.039% | Scope did not complete |
| DSH total on 2026-11-07 | 174 | 9,949,489 | 355,377 | 96.996% | includes failed Alert scopes |
| Current Codex thread snapshot | n/a | 225,990,476 | 1,791,692 | 99.306% | whole thread, lower bound |

The historical average was about **64,956,115 raw tokens/component** and **276,649
efficient tokens/component**. Badge used 85.6% fewer raw tokens, but only 2.8% fewer
efficient tokens. Its prompt median fell from 339,824 to 56,847 and p95 from 633,252 to
94,419; tool-context share fell from 46.483% to 37.902%.

The raw reduction is real for this workflow: fresh stage sessions no longer resend one
growing conversation, so almost all prompt context is served from cache. It is not an 85.6%
cost reduction. Uncached input plus output barely moved because verification and tool calls
still require new work. The Badge pilot also needed Codex follow-up before it reached the
final acceptance state.

## Execution limitations

The Badge stages ran as fresh DSH sessions. The DeepSeek account then hit quota; the
Volcano economy provider returned `429 AccountQuotaExceeded` with the weekly quota resetting
on 2026-11-09 00:00 +0800. The two Alert scope attempts therefore stopped before completion.
Alert and Separator were completed in Codex with fresh source pinning, focused tests, and
current browser quadrants.

The current Codex thread snapshot covers all of its earlier analysis and the final
three-component closure. It is useful as a lower-bound observation, not as a per-component
comparison. Its very high raw total illustrates why a long-lived thread is unsuitable for
repeated parity work even when the cache hit rate exceeds 99%.

## Evidence

- [Badge r4](badge-r4/README.md)
- [Alert r12](alert-r12/README.md)
- [Separator r3](separator-r3/README.md)
- [Combined browser and token manifest](token-three-components.json)
- [Badge handoff](../../.agent-runs/badge-token-pilot/progress.md)
- [Alert handoff](../../.agent-runs/alert-token-pilot/progress.md)
- [Separator handoff](../../.agent-runs/separator-token-pilot/progress.md)
