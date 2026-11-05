# Spinner r2 — current-page browser recheck

Date: 2026-11-05. Official page: <https://ui.shadcn.com/docs/components/base/spinner>
(revision `8a3536678a528cc012b9e62484290e79d0be410f`). Local page: `http://127.0.0.1:8765/spinner`.

## Findings

The page already had all eight official recipes in order (demo, Customization, Size, Button,
Badge, Input Group, Empty, RTL), the official description, and the correct API table. This round
re-verified them against the current page in the browser; no page or component change was needed.

## Verification

- `SpinnerExamplesTest` 7/7 and `SpinnerViewportTest` 8/8 passed (XML under [tests](tests/)).
- Wasm development distribution rebuilt; app Wasm `71d6df0120f39e0a1946b7ab958b52d472afae1d13417a864d61244d312175ff`.
- Four quadrants: [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
- Section comparisons for Customization / Size / Button / Badge / Input Group / Empty / RTL under
  [sections](sections/). The Badge, Input Group and Empty compositions match the official
  geometry and three-spinner rows.

## Remaining differences

- Compose `Spinner` has no DOM `role=progressbar`/accessible name beyond the `label`; native
  assistive-technology matrix remains a platform-scope difference.
- Shared catalog chrome and font rasterization follow the documented project scope.
