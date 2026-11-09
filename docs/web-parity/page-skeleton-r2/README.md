# Shared docs page skeleton — r2 (2026-11-05)

Scope: shared docs-page layout only (no component docs). Official revision
`8a3536678a528cc012b9e62484290e79d0be410f`; measured on the live Button/Accordion/Carousel
pages at 1440×1000 and 390×844.

## Measured baseline (official DOM, `official-dom.json`)

| Metric (1440) | Official |
|---|---|
| main top | y=64 |
| h1 | y=112, h=36, 30/36 semibold, tracking -0.75 |
| description | 16/24, 80% of the 640 column |
| Base UI tab row | y=204, h=30, 16/24, padding 4/0/2 |
| tab label ink / underline | 214–225 / 236–237 |
| preview card | y=266, 640 wide, radius 18 |
| preview inner | y=267, h=288 (Button) / 300 (Accordion) |
| code panel | y=556, h=107.5, 14/24.5, padding 14/0, line box 26.5 |
| first section H2 | y=712.5 (48px below the card) |

Phone (390): h1 y=80 (content top padding 24 below the 56px mobile header), description y≈132,
preview card y=261.9.

## Local before this round

The local page had the title at the same desktop position (ink 121–122 vs official 119), but:

- **Phone title was 34px too low**: content used a fixed 48dp vertical padding at every width, so
  the h1 ink started at 122 instead of 88 (official 390). This is the documented "手机标题仍低于官网"
  residual.
- **The "Base UI" label sat 8dp above its underline** (official tab is 2px above), which pushed the
  underline and the whole first preview card down by 6px: card top 272 vs official 266 for Button and
  Carousel, 296 vs 290 for Accordion (code divider and card bottom also +6).

## Changes (shared only)

`app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/ComponentDetailScreen.kt`:

1. Doc column vertical padding is now viewport-dependent: `24.dp` below 640dp, `48.dp` otherwise
   (measured official content top: 80 at 390 vs 112 at 1440 with the same 56/64 header height).
2. The "Base UI"/"Kotlin extension" label-to-underline column spacing changed `8.dp → 2.dp`
   (official tab uses `padding: 4px 0 2px`, i.e. the underline sits 2px under the label ink).

No other files changed; no component doc or demo was touched.

## Before/after (canvas pixel measurements, `measurements.json`)

| Page | Metric | Official | Before | After |
|---|---|---|---|---|
| Button | desktop card top | 266 | 272 | **266** |
| Button | desktop code divider / card bottom | 555 / 664 | 561 / 670 | **555 / 664** |
| Button | desktop tab underline | 236 | 242 | **236** |
| Button | phone title ink top | 88 | 122 | **98** |
| Accordion | desktop card top | 290 | 296 | **290** |
| Accordion | desktop card bottom | 700 | 706 | **700** |
| Carousel | desktop card top | 266 | 272 | **266** |
| Carousel | phone title ink top | 87 | 121 | **97** |

Four quadrants for Button/Accordion/Carousel were captured before and after the change:
`before/<slug>/` and `after/<slug>/` (official + local at 1440×1000 / 390×844, light/dark, plus
contact sheets and meta.json). Official quadrant background pixels were verified light 255 / dark 10.

## Verification

- Builds: before `wasm=bbcced4b…`, after `wasm=8145312b5309baff4e93e75b87b3fc8e4bfd63796f06abe4f7ce471b0b00f3b5`.
- Tests (`:app:shared:jvmTest` under the global lock): CatalogDocumentLayoutTest 14/14,
  CatalogNavigationTest 6/6, ComponentDocumentationTest 8/8 — 28/28, XML in `tests/`.
- Code panel typography was measured but left unchanged: local line-numbers use 14sp/26.5 line box
  with a 64dp gutter (numbers right-aligned), matching the official pre (14/24.5 with a 26.5px line
  box, numbers at x≈430 and code at x≈455); the collapsed panel is 108px in both. Dark glyph colour
  and syntax colours match the existing doc palette, so no change was justified.

## Remaining differences (documented, not changed)

- Phone content is still ~10px below official (title ink 98 vs 88): the local mobile header is 64px
  versus the official 56px (8px) plus ink rounding. Changing the shared top bar height is a
  chrome-level change outside this skeleton pass; measured here instead.
- Section rhythm after a preview is 32px locally vs official 48px (preview margin-bottom 48). The
  same outer `spacedBy(32)` also drives header→tab grouping, so changing it needs a layout
  restructure rather than a one-constant tweak; documented with the measured values.
- Carousel's preview card is 429px tall locally vs the official 622.5px: that is a per-component
  preview height (`minPreviewHeight`) owned by the Carousel doc, not the shared frame, so it was not
  changed in this batch.
- TOC/Copy Page/prev-next alignment was checked and left as is (no clear offset).
