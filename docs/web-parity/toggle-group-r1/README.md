# Toggle Group — Web parity r1 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/toggle-group](https://ui.shadcn.com/docs/components/base/toggle-group),
Base UI + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`).

## Official page reading

- Demo: outline group with Bold/Italic/Underline icon items (`multiple`).
- Sections: Installation, Usage, Composition, Outline (All/Missed), Size (sm + default rows),
  Spacing (sm, `spacing={2}`), Vertical (multiple, spacing 1), Disabled, Custom (Font Weight field
  with four `Aa` cards), RTL (List/Grid/Cards), API Reference, Changelog (2026-05-17 default spacing
  changed from 0 to 2).
- Connected styling comes from `spacing={0}`; default spacing is 2 CSS units.

## Findings and changes

The local page was an 18-example parameter matrix with no official recipe titles and no Custom or
RTL previews. `ToggleGroupDoc` was rebuilt to the official eight recipes with official titles,
descriptions and content:

- Default (B/I/U icons), Outline (All/Missed with literal "all" active), Size (sm + default rows),
  Spacing, Vertical, Disabled, Custom (Font Weight, 64dp cards with Aa), RTL (language toolbar +
  List/Grid/Cards).
- Added the official Composition block, the API table, and two Kotlin extensions:
  `Single select` (single-select deselect behaviour) and `Connected` (`spacing = 0`).
- New `ToggleGroupExamplesTest`: recipe order/copy, Outline literal selection, single-select
  deselect, vertical stacking and disabled non-toggling.

## Verification

- `ToggleGroupExamplesTest` 5/5 pass (`tests/`).
- Wasm Web build `wasm=97fa0dc37ba6ef519ac835a9de585e4f3cf0c3f35ce14a80d232502370a46931`;
  four quadrants captured after the final build, official background pixels verified.
- Browser: clicking Italic selects it (`browser/desktop-light-italic-selected.png`), ArrowRight
  moves the focus ring (`browser/desktop-light-arrow-focus.png`), Tab leaves the group
  (`browser/desktop-light-tab-exit.png`); vertical section screenshot captured.

## Remaining differences (open)

- The Changelog section (historical spacing change) is omitted; the Kotlin default spacing is 8dp
  (2 Tailwind units) and the default is documented in the API table.
- The official `className` per-item 64px custom card styling is reproduced with `Modifier.size`
  (no CSS classes), and the complete arrow-key/Tab matrix for every orientation is covered by
  component tests rather than page recipes.
- Toggle Group stays unticked until the vertical/keyboard matrix and custom-style surface are
  closed at component level.
