# Checkbox r3 — current-page browser recheck

## Final acceptance — 2026-11-12

Stage30 independent audit **PASS** and `STAGE_DONE 30-verify` are recorded in the
[current audit](../../../.agent-runs/checkbox/verify-30/until-pass-20261112/acceptance/audit.md)
and [handoff](../../../.agent-runs/checkbox/progress.md). Stage40 closes Checkbox only;
Chart is skipped. The November5 record below remains historical, not current-build proof.

- [Current browser evidence](../../../.agent-runs/checkbox/verify-30/until-pass-20261112/acceptance/browser/README.md)
  and [results](../../../.agent-runs/checkbox/verify-30/until-pass-20261112/acceptance/browser/results.json):
  separate JS/WasmJS catalog runs,1440×1000/390×844 light/dark, DPR1/zoom1,
  seven interaction checks per target, both tween midframes, live theme and three-language RTL.
- [Cache-carry results](../../../.agent-runs/checkbox/verify-30/until-pass-20261112/cache-carry/results.json),
  [final three-task build](../../../.agent-runs/checkbox/verify-30/until-pass-20261112/cache-carry/02-eof-final-build-summary.txt)
  and [final input/artifact binding](../../../.agent-runs/checkbox/verify-30/until-pass-20261112/cache-carry/actual-build-binding-final.json):
  default configuration-cache JS/Wasm distributions and Desktop compile exit0.
  JS48/Wasm44 path sets, lengths and hashes equal30c HTTP/disk content; browser evidence
  is reused on identical assets, not claimed rerun after cache repair.
  [EOF resolution](../../../.agent-runs/checkbox/verify-30/until-pass-20261112/cache-carry/eof-resolution.json)
  closes the historical1199byte/open_findings entry; final build script is1198bytes.
- Retained30a Checkbox14/14, expanded18/18 (four other cases), separate FocusVisible5/5,
  and four formal Nova/Neutral golden/actual pairs with0 differing pixels are hash-bound
  reuse, not a new JVM/lint/native full run. Exact XML/hash paths are in the final audit.
- [Source and acceptance index](../../../.agent-runs/checkbox/evidence-20/sources.json)
  preserves current wrapper/CSS pin8ab27715521cbd4f87ae6886cd989cbb678f6de7 and links the
  [historical MDX/eight-recipe source set](../checkbox-r1/sources.json) at96da9cc7….
  The historical r3 pin below is not relabeled; live official deployment SHA is unknown.

### Resolution of historical remaining differences

- The old dark-golden blocker is superseded by the scoped four-pair Nova/Neutral
  comparison, not a claim that every historical Vega/Field snapshot now passes.
- Expanded hit-area/label activation, both color-tween directions, live theme and JVM
  live shape are covered by retained30a and current30c. F1 stationary mouse→keyboard→
  first touch repeats pass on both targets. No physical-phone result is claimed.
- Canvas has no native HTML input/form serialization, constraint UI or label-for.
  Caller-hoisted state/validation, triStateToggleable, real focus/Space and actual
  Checkbox DOM/AX role/name/checked/disabled/invalid provide the scoped equivalents.
  Supplementary same-runtime fixture covers mixed and dynamic invalid/disabled removal,
  latest callbacks and focus; it is not the complete catalog graph. CDP bridge activation
  is not external screen-reader testing, and the full external AT matrix is not claimed.
- Shared catalog chrome, Table layout/API and font rasterization remain documented
  platform/API boundaries, not exact official-page pixel equality. The final audit found
  no scoped control/clipping regression. Radio's independent upstream AX checked gap
  remains open for Radio, not repaired, waived or counted as Checkbox success.

## Historical r3 record

Date: 2026-11-05. Official page: <https://ui.shadcn.com/docs/components/base/checkbox>
(revision `8a3536678a528cc012b9e62484290e79d0be410f`). Local page: `http://127.0.0.1:8765/checkbox`.

## Findings

The eight official recipes and their order were already aligned in r2. This round found one copy
mismatch: the RTL description said "To enable RTL support in your app, see the RTL configuration
guide." while the official page says "in shadcn/ui". The page otherwise matched at 1440/390 in
both themes.

## Changes

- Aligned the RTL description with the official wording.
- No component/theme change was needed; r2's official 150ms color transition is retained.

## Verification

- `CheckboxExamplesTest` 3/3 and `CheckboxViewportTest` 12/12 passed (XML under [tests](tests/)).
- Wasm development distribution rebuilt; served app Wasm `0a293101b04b3764ed6593df56ab2fe30ec34ffd3214dcc18bb6e72f40e3aa82`.
- Four quadrants: [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
  Section captures for Invalid State / Basic / Description / Disabled / Group / Table / RTL under
  [sections](sections/).
- Browser interactions: Basic checkbox checked/unchecked and a Table row selection
  ([interactions](browser/interactions/)).

## Remaining differences

- Two historical dark Roborazzi goldens still differ; their actual output has been byte-identical
  to HEAD since the first round, and they were not refreshed.
- Canvas has no native HTML `<input type=checkbox>` / label-for association; CSS hit-area,
  dynamic theme/shape and the full assistive-technology matrix remain open.
- Shared catalog chrome and font rasterization follow the documented project scope.
