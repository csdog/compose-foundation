# Separator r3 — current-source completion

- Date: 2026-11-07
- Official revision: `42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`
- Status: complete

## Findings

The current Separator MDX, `separator.tsx`, five examples, and Nova CSS are byte-identical
to the r1 `8a353667…` and r2 `96da9cc7…` sets. The component still uses the Base UI
separator primitive with the same one-pixel border-token rules and default horizontal
orientation.

## Verification

- Separator component and page tests: **48/48 passed**, including orientation, semantics,
  explicit Style overrides, intrinsic sizing, 767/768 CSS breakpoints, all five official
  recipes, three RTL languages, and four viewport/theme combinations.
- Current browser capture: official/local at 1440×1000 and 390×844, light/dark.
- Evidence: `browser/`, [`../token-three-components.json`](../token-three-components.json).

## Accepted platform differences

- Compose does not expose an HTML ARIA separator role. The semantic default publishes a
  localized description and orientation; `decorative = true` suppresses it.
- Shared documentation chrome and font rasterization remain outside component parity.
