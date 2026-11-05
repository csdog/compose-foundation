# Marker — Web parity r3 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/marker](https://ui.shadcn.com/docs/components/base/marker),
revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`). Official previews use the
`base-rhea` style; this library renders the same recipes with its Nova preset (no rhea preset).

## Official page reading

- Demo: four markers (branch switch, `role=status` spinner + shimmer "Thinking...", separator
  "Conversation compacted", search icon "Explored 4 files").
- Sections: Installation, Usage, Composition, Features, Variants, Status, Shimmer, Separator,
  Border, With Icon, Links and Buttons, Accessibility, API Reference (Marker/MarkerIcon/
  MarkerContent) — 8 examples, all already present locally in the official order.

## Changes

- `MarkerDoc.kt` descriptions aligned with the official copy for Variants, Status, Shimmer,
  Separator, Border, With Icon and Links and Buttons (Status now states the `role="status"` +
  `Spinner` intent; Links and Buttons now says "…with the `render` prop on `Marker`").
- Confirmed the earlier r2 fixes still hold: natural preview height (`h-auto`), in-page link
  (navigates to the Links and Buttons section), and text/icon hover colours.

## Verification

- `MarkerExamplesTest` 5/5, `MarkerInteractionTest` 12/12 and `:shadcn:core` `MarkerParityTest` 8/8
  + `MarkerScreenshotTest` 5/5 pass (catalog XML in `tests/`).
- Wasm Web build `wasm=60af0664e81eab666f2dc4c04083fd58a62eb43a82e1813a9c67e8360c202288`;
  four quadrants captured after the final build and reviewed: `local-*.png`, `sheet-*.png`,
  `browser/meta.json`.
- Browser: link hover state (`browser/desktop-dark-link-hover.png`), revert-button toast
  (`browser/desktop-dark-revert-toast.png`), Status section copy/rows and the Accessibility +
  API reference sections (`browser/desktop-dark-accessibility-api.png`).

## Remaining differences (open, from r2)

- The shared Toaster still draws the action feedback inside the preview (official Sonner shows it
  at the window top); Toast width/stacking/lifetime remain a Toast-batch item.
- Native anchor/button abilities (real DOM focus outline, target/rel, modifier clicks) are modelled
  with a text URL annotation + navigation; full native/assistive-tech behaviour is open.
- The official intro paragraph after the demo and the `base-rhea` preview theme are not reproduced
  (local page skeleton / no rhea preset); Variants table and Accessibility rhythm differ from the
  official MDX. The old dark separator pixel difference is still kept, not re-recorded.
  Marker stays unticked.
