# Kbd — Web parity r3 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/kbd](https://ui.shadcn.com/docs/components/base/kbd),
Base UI + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`).

## Official page reading

- Unnamed demo preview: two `KbdGroup`s (⌘ ⇧ ⌥ ⌃ / Ctrl + B with a plain "+").
- Sections: Installation, Usage, Composition, Group, Button, Tooltip, Input Group, RTL,
  API Reference (Kbd / KbdGroup).
- Group/Button/Tooltip/Input Group/RTL descriptions were copied verbatim into the page.
- All six official recipes already existed locally in the official order; the local RTL toolbar
  and the 288dp body follow the earlier r2 fix and tests.

## Changes

- `KbdDoc.kt`: Button, Tooltip, Input Group and RTL descriptions rewritten to the current official
  copy (Tooltip/Input Group now say "inside a `Tooltip`/`InputGroupAddon` **component**", RTL now
  points at the shadcn RTL guide). No recipe/order/API changes were needed.
- Verified the Button-tail keycap (⏎, 2dp visual offset), TooltipProvider delay state, the
  three-language RTL picker and the Input Group keycaps with the existing focused suites.

## Verification

- `KbdExamplesTest` 7/7, `KbdViewportTest` 4/4, `:shadcn:core` `KbdParityTest` 11/11 and
  `KbdScreenshotTest` 2/2 pass (`tests/` holds the catalog XML).
- Wasm Web build `wasm=60af0664e81eab666f2dc4c04083fd58a62eb43a82e1813a9c67e8360c202288`;
  four quadrants captured after the final build (including the new shared 80%-max-width description
  layout) and reviewed: `local-*.png`, `sheet-*.png`, `browser/meta.json`.
- Browser states (dark): Tooltip hover on Save / Print shows the keycap hint
  (`browser/desktop-dark-tooltip-save.png`, `browser/desktop-dark-tooltip-print.png`);
  the Group/Button sections with the corrected copy are in
  `browser/desktop-dark-group-button-copy.png`.
- Official DOM headings/API names recorded in `browser/meta.json`.

## Remaining differences (open, from r2)

- `InputGroupAddon`'s `radius - 5px` / negative-margin rule for direct `kbd` children is not
  modelled; the input-group keycaps keep the ordinary radius.
- Tooltip nested in ButtonGroup still differs on merged inner corners/padding, and the two old
  Tooltip goldens remain mismatched (kept, not re-recorded).
- Unicode keycap glyph fallback, native `<kbd>` DOM/assistive-tech mapping, touch and the full
  dynamic theme/shape matrix remain open. Kbd stays unticked.
