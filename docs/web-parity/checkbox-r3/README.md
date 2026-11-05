# Checkbox r3 — current-page browser recheck

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
