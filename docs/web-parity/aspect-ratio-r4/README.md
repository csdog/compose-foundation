# Aspect Ratio r4 — current-page browser re-verification

## Sources

Current official page re-read at upstream revision
`8a3536678a528cc012b9e62484290e79d0be410f` (MDX, four examples,
`aspect-ratio.tsx`; checksums in [sources.json](sources.json)). Official order: hero
preview, Square, Portrait, RTL and the AspectRatio API table (ratio required). The local
page has the same four recipes in order; the documented Kotlin deviation (`ratio` defaults
to `1f` instead of being required) is unchanged.

## Changes

- The RTL recipe description now uses the official wording “To enable RTL support in
  shadcn/ui, see the RTL configuration guide.” (was “in your app”).
- `AspectRatioExamplesTest` asserts the corrected RTL copy.

## Verification

Final build Wasm SHA-256
`af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`.

Focused tests on final sources: `AspectRatioExamplesTest` 6 in the shared run and
`AspectRatioLayoutTest` 3 / `AspectRatioScreenshotTest` 2 in `:shadcn:core:jvmTest`.
XML in [tests](tests/).

Browser evidence after the final build: [browser](browser/) quadrants
(pixel-verified light/dark), three official section pairs in [sections](sections/), and
[browser/interactions](browser/interactions/):

- `local-square-desktop`, `local-square-phone` — the square example keeps its 1:1 frame
  across viewport changes.
- `local-portrait-desktop` — the 9:16 portrait frame.
- `local-rtl-arabic`, `local-rtl-language-popup`, `local-rtl-english`,
  `local-rtl-hebrew` — the language toolbar switches the caption while the image and ratio
  stay fixed.

## Remaining differences

- RTL caption colour/glyph rendering and the integer-measurement adaptation remain open
  from r3.
- The bundled upstream demo PNG (grayscale + dark brightness via `ColorFilter`) stands in
  for the official `next/image`; the photo itself is the same asset, but loader-related
  differences are out of scope.
- The Kotlin `ratio = 1f` default vs the web wrapper's required `ratio` is a deliberate
  public-API deviation; making it mandatory would be a breaking change and is not decided
  here.
- Shared page chrome and font rasterization differences are unchanged.
