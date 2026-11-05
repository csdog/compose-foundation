# Alert r11 — current-page browser re-verification

## Sources

Current official page re-read at upstream revision
`8a3536678a528cc012b9e62484290e79d0be410f` (MDX, six examples, `alert.tsx`;
checksums in [sources.json](sources.json)). Official order is the hero preview followed by
Basic, Destructive, Action, Custom Colors, RTL and API Reference (Alert/AlertTitle/
AlertDescription/AlertAction tables). The local page has the same six official recipes in
order, then the four `Kotlin extension:` recipes.

## Changes

- Basic description now matches the official copy:
  “A basic alert with an icon, title and description.” (was “An alert with an icon, title,
  and description.”).
- Action description now matches the official wording: “Use `AlertAction` to add a button
  or other action element to the alert.” (was “another action element”).
- Regression assertions for both strings added to `AlertExamplesTest`.

## Verification

Final build Wasm SHA-256
`af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`.

Focused tests on final sources: `AlertExamplesTest` 6 (including the two new copy
assertions), `ComponentDocumentationTest` 8 in the shared focused run, and
`AlertNovaTest` 8 / `AlertScreenshotTest` 4 / `AlertNovaActionScreenshotTest` 2 in
`:shadcn:core:jvmTest`. XML in [tests](tests/).

Browser evidence after the final build (pixel-verified light/dark quadrants):
[browser](browser/) plus five official section pairs in [sections](sections/).
Interactions ([browser/interactions](browser/interactions/)):

- `local-action-default`, `local-action-hover`, `local-action-clicked` (and `-dark`) —
  the `AlertAction` Enable button hover/click states; the click does not navigate.
- `local-rtl-arabic`, `local-rtl-language-popup`, `local-rtl-english`,
  `local-rtl-hebrew` — RTL toolbar switches the two alerts between Arabic, English and
  Hebrew.

## Remaining differences

- Complete grammar/copy of long descriptions, very tall action content, arbitrary complex
  slots and window-reflow behaviour remain unverified (carried from r10).
- The complete focus/pressed/disabled matrix and assistive-technology semantics are still
  open; only the action button hover/click and the RTL language switch were exercised here.
- Shared page chrome (shorter local top bar, no React Aria/Radix tabs, TOC shape) and
  Geist/font rasterization differences are unchanged and explained in the page-layout
  record.
