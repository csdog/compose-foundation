# Input Group independent review — 2026-11-09

Pinned official revision: `8a3536678a528cc012b9e62484290e79d0be410f`.
17 durable reviewed sources: 14 Base examples, MDX, wrapper, and matching Nova CSS.
See `sources.json` for exact full paths, URLs, successful curl status and hashes.
`source/examples-fetch.json` additionally records HTTP 200 for all examples.

## Final source-gap followup

All previously listed practical callsite differences are now fixed explicitly in catalog
previews and their copied recipes, with existing public variant/Style/textStyle arguments:

| Pinned source | Previous local | Final native callsite |
| --- | --- | --- |
| Button Info `variant="secondary"`; group `--radius:9999px` | Ghost; child icon-xs radius7 | Secondary Info, CircleShape on both Info and Favorite |
| Inline button addon outer margin -0.3rem; Kbd -0.15rem, padding8 | Unadjusted padding8 | Equivalent outer insets3.2 (8-4.8) and5.6 (8-2.4) |
| URL prefix addon `pl-1.5` | Padding8 | Explicit start6 |
| Nested Kbd radius `var(--radius)-5px` | Generic Kbd radius | Captured baseRadius.value-5, explicit RoundedCornerShape |
| Code header `font-mono font-medium`, muted addon text | Default proportional text | Explicit Monospace/Medium/muted text style in both code-header recipes |

No generic child-inspection/context API was introduced. These inset equivalents preserve
the known callsite's content positions and measured contribution without negative-size
layout tricks. Other arbitrary user children continue using explicit native Style args.

Followup RED: `red-final-callsites/`68 cases/16 failures (Copy gap9 vs4.2;
Info transparent vssecondary; header Default vsMonospace; initial Kbd merged-node test).
Corrected unmerged actual Kbd text bounds have their own valid RED4/4 in `red-kbd-exact/`:
13 versus expected10.6 (border1 + inset5.6 + key's internal end padding4).

Final `final-callsites/` XML68/68, no failures/errors/skips; timestamp
`2026-11-08T23:53:51.702Z`. Prior core19 tests are unchanged and green:87 focused total.
Final log `.agent-runs/review-input-group/logs/20261109T075339-green-final-callsites-r2-87474.log`.
Owning JVM-test lint fully passes; common-main has0 InputGroup findings and15 foreign.
Final source fingerprint manifest updated; no source edit after this verification.
`git diff --check` passes. The earlier71-case checkpoint below is preserved as history.

## Source provenance corrections

The historical `input-group-r1/sources.json` shortens MDX/wrapper/example paths to
repository-root `input-group.mdx`, `components/…`, and `examples/…`; these are not
real pinned paths. Preserve that historical manifest. Correct paths are under
`apps/v4/content/docs/components/base/`, `apps/v4/registry/bases/base/ui/`, and
`apps/v4/examples/base/`. The Nova CSS path was correct.
One review fetch of `apps/v4/registry/bases/base/examples/input-group-button.tsx`
returned HTTP 404 and was rejected. The pinned git tree identified the correct
`apps/v4/examples/base/` location. Sandbox DNS failed; exact read-only retry with
escalation succeeded. Error bodies were not accepted as source evidence.

## Catalog fixes and regression evidence

- Button Copy now writes the official URL through existing `rememberCopyToClipboard`.
- Secure URL prefix is an addon, so it consumes addon width before editable input
  measurement rather than adding a second control that overflows its trailing button.
- Code textarea restores the source's 200 dp minimum outer height (184 dp editable
  semantics height after 8 dp top/bottom padding).
- RTL restores the omitted Saving row/translations and uses Arabic font index 0,
  English inherited index 1, and Hebrew index 2.
- Custom Input now describes the supported growing Compose textarea and minimum lines;
  removed the false instruction to add a DOM `input-group-control` slot.
- Usage imports are flush after `trimIndent`; Button snippet includes copy state.
- API docs split Input/Textarea and no longer promise unsupported arbitrary passthrough.

`red/TEST-com.toutiaoyan.compose.ui.catalog.InputGroupExamplesTest.xml`: 44 cases,
12 initial failures. Copy and minimum-height regressions were valid RED; the first
URL test expected three editable controls and was corrected to two (read-only URL
has no set-text action). A later comparison against the unrelated Search input
was corrected to compare directly against the favorite button. Final 44/44 green
is archived under `green/`. Failed attempts are retained in run logs.

Catalog green timestamp `2026-11-08T23:40:03.189Z`; 2.9 test seconds.
The coordinator reported another agent had started a queued run without the grant;
this class XML survived and was copied immediately. A stable combined rerun is owned
by the coordinator/OTP agent. Source hashes at this checkpoint are recorded separately.

## Earlier focused verification checkpoint

Final archived XML in `final/`: InputGroupExamplesTest 52/52, InputGroupAddonStyleTest
7/7, InputGroupScreenshotTest 12/12; 71 total, no failures/errors/skips.
Catalog timestamp `2026-11-08T23:48:18.538Z`. Relevant final logs:
- `.agent-runs/review-input-group/logs/20261109T074621-green-focus-styles-final-82741.log`
  contains core 19/19 and core owning common-main/JVM-test lint success.
- `.agent-runs/review-input-group/logs/20261109T074751-green-catalog-pixels-final-83543.log`
  contains catalog 52/52; aggregate command fails on owning lint, not these tests.
- Catalog common-main owned lint 0, 14 foreign diagnostics. JVM-test had one owned
  blank-before-local-function diagnostic and one foreign; the owned blank line was
  manually fixed after the 52-green run. Coordinator/next Gradle owner will confirm it.

Core fix: focused addon subtrees are excluded from the group control-focus border/ring
and `InputGroupState.focused`. Cleanup removes registration on addon disposal. Direct
Input and wrapped Textarea retain working control focus, tested through button focus,
return to control, and focused-addon unmount. No public signature or Input.kt change.

Additional pinned-source catalog styling restored: secure URL pill shape, selected
favorite blue-600, 24 dp button recipe gap, 1 dp code header/footer dividers and 8 dp
padding on their bordered edges. Copied recipes match these changes. `red-focus-styles/`
preserves focus/color RED. `red-divider-pixels/` preserves actual 4/4 pixel RED after
addressing the existing nodes; expected border versus transparent was the failure.
The dark pixel comparison composites the translucent group background and permits one
8-bit raster alpha rounding unit (0.005 channel tolerance); missing borders still fail.

`final-source-hashes.json` fingerprints all five implementation/test files. The
catalog final test hash differs only by the last blank-line lint correction.
No formatter, init filter, build or browser run was performed by this review.

## Browser and platform limits

Historical browser `input-group-r2/browser/summary.json`: 50/50 at 2026-11-08T11:53Z;
it predates these fixes. Current build/browser is the root integration owner's gate.
Reuse `scripts/verify_component_browser.mjs` and `scripts/component_browser_helpers.mjs`.
Current browser must cover Copy/feedback, selected favorite/prefix bounds at 390/1440,
pill shape, 200-height textarea/dividers, RTL saving and actual Arabic/Hebrew glyphs,
live themes and overlays, and control versus addon keyboard focus ring.

Native focus events retain the library's existing input-mode policy; no claim of full
browser `:focus-visible` heuristics. Arbitrary HTML prop/DOM slot injection has no
native public equivalent and is now documented honestly. The known Info/radius/offset/header-font callsites are resolved by the followup above.
Icon geometry and native subpixel rounding remain platform differences. Current visual
verification still belongs to root integration; no full-parity claim.
