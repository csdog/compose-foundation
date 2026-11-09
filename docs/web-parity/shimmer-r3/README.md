# Shimmer r3 — current utility page copy/order and browser frames

Date: 2026-11-05. Local page: `http://127.0.0.1:8774/shimmer`.
Official page: <https://ui.shadcn.com/docs/utils/shimmer>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (11 sources and hashes in
[sources.json](sources.json)). Note the official page pins `styleName="base-rhea"` for its
previews because utilities are documented with the rhea style; this port renders with Nova
tokens, and the shimmer itself derives its colors from the text `currentColor`.

## Findings

The current official utility page has nine previews — demo, With Marker, Color, Duration,
Spread, Angle, Play Once, Disabling the Shimmer, RTL — with prose-only sections for Reverse,
Fallback and Reduced Motion. The table of classes documents `shimmer`, `shimmer-once`,
`shimmer-reverse`, `shimmer-none`, `shimmer-color-*`, `shimmer-duration-*`,
`shimmer-spread-*` and `shimmer-angle-*`, with defaults of 2000ms, `calc(3ch + 40px)` and
20°.

The local page already had the nine official recipes (prepended before four Kotlin demos),
but it rendered them under an `Examples` H2, had no official section copy, no Reverse
section, no Fallback/Reduced Motion prose, and the Kotlin-only demos were not titled
`Kotlin extension: …`.

## Changes

- `examplesHeading = null`; the nine official recipes are now top-level H2 sections with the
  official paragraphs as descriptions (With Marker, Color, Duration, Spread, Angle, Play
  Once, Disabling the Shimmer, RTL).
- Added a `Reverse` recipe between Angle and Play Once using the official copy and
  `reverse = true`. The official page documents this class with code only; the preview is
  added for the Compose page (documented residual).
- Added `Kotlin extension: CSS class mapping` before the recipes (class → Style API mapping
  plus the currentColor behavior bullets) and `Fallback` + `Reduced Motion` guide sections
  after the recipes; the Compose port does not read a platform reduced-motion preference,
  so callers use `enabled = false` (documented).
- Renamed the four Kotlin demos to `Kotlin extension: Overlay modifier`,
  `Kotlin extension: Style content brush`, `Kotlin extension: Duration, angle, spread` and
  `Kotlin extension: Reverse and once`.
- Added the missing `reverse` parameter to the shared recipe helper and regenerated the
  standalone snippets with `scripts/generate_shimmer_recipe_code.py` (new
  `shimmerReverseRecipeCode`).

## Verification

- **44 focused JVM tests passed** (XML in [tests](tests)): 11 ShimmerExamplesTest cases ×
  viewport 1440/390 × light/dark, including recipe order/copy/guide assertions, pixel
  animation checks, once/replay, responsive none at the 768dp boundary, RTL reading
  direction, and a new reverse-vs-default frame comparison.
- Wasm development distribution rebuilt; served app wasm `97fa0dc3…` (see [meta.json](meta.json)).
- Four quadrants at 1440×1000 and 390×844 in light and dark:
  [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
- Browser frames in [browser](browser): demo, Reverse, Play Once, Disabling at desktop light,
  dark demo, phone light/dark, RTL; each captured at two times ~420ms apart and the frames
  differ, confirming the sweep is running (and the responsive `none` case stays static at
  desktop).

## Remaining differences (page stays unchecked)

- Official utilities are documented with `base-rhea`; this port renders with the Nova
  preset, so the surrounding chrome/palette and the exact highlight alpha differ from the
  rhea previews even though both derive from `currentColor`.
- The exact CSS default spread is `calc(3ch + 40px)`; the Kotlin API uses the documented
  64dp approximation and cannot express the font-relative `3ch` term or percentage spreads.
- Automatic reduced-motion is a browser/CSS feature; the Compose port does not read a
  platform preference and requires `enabled = false`.
- The Reverse preview is added for the Compose page (official section is code-only), and the
  Fallback/Reduced Motion prose sections render after RTL instead of before it because the
  doc model only supports guide sections before or after the recipe list.
- Exact interpolation/gamut mapping (Oklab sampling in sRGB), Display P3, saturated
  out-of-gamut colors, alpha quantization, custom fonts/density and rich-text transparent
  spans remain unverified; native fixtures cannot prove browser pixel equality.
