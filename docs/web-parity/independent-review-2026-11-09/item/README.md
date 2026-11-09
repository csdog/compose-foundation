# Independent Item review — 2026-11-09

Verdict: fixed confirmed Header/Footer minimum-gap and documentation-order defects.
Source manifest `sources.json` supersedes malformed root-relative URLs in Item r3.
All 14 pinned files returned HTTP200 and actual TSX/MDX/Nova CSS; reviewed bytes match r2.
Pinned revision: `8a3536678a528cc012b9e62484290e79d0be410f`.

Header/Footer constrained content yielded a measured 0px gap before the fix, against Nova gap-2.
`tests/red-gap/` retains the failure; private Nova arrangement reserves 8dp while preserving
space-between placement. The same measured test covers Header and Footer in LTR and RTL.
`tests/green/` retains 39 passing tests (13 core parity, 4 screenshot methods,
10 catalog examples, 12 page layout cases). Plain JVM execution does not compare golden pixels.

The intro now follows the primary preview, Composition lead-in precedes its tree, usage includes
media, and title docs correctly describe default wrapping. Shared optional lead-in hook defaults
null; the export regression proves existing behavior remains byte-identical when omitted.

`lint/` retains actual source-set check output: core commonMain0/JVMtest0, catalog JVMtest0,
catalog commonMain17 unrelated, zero owned violations. No formatter used.

Final integration build/current browser belongs to root. Pending browser checks: intro/Composition
placement, four quadrants, dropdown selection/keyboard, RTL languages, narrow Header/Footer custom
slots. Existing phone Header min-content overflow, native link DOM adaptation and shared
language-selector tofu/font behavior remain explicit browser gaps. No full parity claim.

Exact artifacts, commands, source comparisons, dirty-diff scope and limitations:
`.agent-runs/review-item/progress.md`.
