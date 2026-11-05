# Popover — Web parity r3 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/popover](https://ui.shadcn.com/docs/components/base/popover),
Base UI + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`).

## Official page reading

- Unnamed demo: outline "Open popover" trigger opening a `w-80` "Dimensions" form with Width /
  Max. width / Height / Max. height inputs.
- Sections: Installation, Usage, Composition, Basic, Align, With Form, RTL, API Reference.
- Basic uses PopoverHeader/Title/Description with `align="start"`; Align shows Start/Center/End
  small triggers with `w-40` panels; With Form uses a `w-64` panel with two horizontal fields;
  RTL opens six physical/logical side popovers in Arabic/Hebrew.

## Findings and changes

The five official recipes were already implemented and covered by 24 catalog + 9 core tests
(including the focus suite and the Nova panel spacing/radius/shadow/line-height fixes from r2).
This round aligned the page:

- Added the official descriptions for Basic, Align, With Form and RTL.
- Added the official Composition block and hid the generic "Examples" heading, so the page order
  now matches the official TOC (Installation, Usage, Composition, Basic, Align, With Form, RTL,
  API Reference).
- Curated the Kotlin-only extras to `Kotlin extension: Non-modal`, `Kotlin extension: Placement`,
  `Kotlin extension: Uncontrolled`; the rest of the 20-example matrix is no longer rendered.
- Test extended to assert the official descriptions and curated extension list.

## Verification

- `PopoverExamplesTest` 24/24 pass across 1440/390 × light/dark (`tests/`), covering the four-input
  Dimensions demo, Basic header/start alignment, three independent Align panels, the two-field form
  with editable values, the six-trigger RTL matrix, and the non-modal/placement extensions.
  The previous core `PopoverFocusTest` (2), `PopoverNovaLayoutTest` (5) and
  `PopoverNovaScreenshotTest` (2) remain the focus/layout coverage.
- Wasm Web build `wasm=c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`;
  four quadrants captured after the final build and reviewed (official theme re-asserted before
  capture).
- Browser states (dark): default popover open with the four Dimensions inputs
  (`browser/desktop-dark-default-open.png`), outside click closes
  (`browser/desktop-dark-outside-closed.png`), Escape closes
  (`browser/desktop-dark-escape-closed.png`).

## Remaining differences (open, from r2)

- The Kotlin API keeps `modal = true` by default (Base UI/Radix differ here); the `Kotlin
  extension: Non-modal` recipe documents `modal = false`, but the complete focus/animation matrix
  (initial focus target, focus restore details, enter/exit timing) is still not fully closed.
- The official `side`/`align` physical-property matrix beyond the recipe's align demo is documented
  through the Kotlin extensions rather than a 1:1 page recipe.
- No React Aria/Radix tab set and no ad card. Popover stays unticked.
