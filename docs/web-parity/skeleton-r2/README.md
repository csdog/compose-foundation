# Skeleton r2 — official page in the browser and pulse observation

Date: 2026-11-05. Local page: `http://127.0.0.1:8774/skeleton`.
Official page: <https://ui.shadcn.com/docs/components/base/skeleton>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (10 sources and hashes in
[sources.json](sources.json)).

## Findings

The current official page is: demo, Installation, Usage (a 100×20 rounded-full skeleton),
Avatar, Card (`h-80`), Text, Form, Table, and RTL — seven previews plus the demo, with no
`Examples` heading and no prose besides the RTL guide link. The local page already had the
seven official recipes (built in [r1](../skeleton-r1/README.md) with the 48/40dp circles,
250/200 lines, 250×16/200×16 default lines, card 16:9 image, form groups, table widths and
RTL selector), plus a Kotlin `Shimmer` extension.

## Changes

- `examplesHeading = null` so the recipes are top-level H2 sections like the official page.
- Renamed the project extension to `Kotlin extension: Shimmer`.
- Added the official RTL sentence (“To enable RTL support in shadcn/ui, see the RTL
  configuration guide.”) to the RTL recipe description.
- No component or recipe geometry change was needed; the r1 pulse fix (opacity read inside
  Style's observation scope, no per-frame consumer recomposition) and the Shimmer extension
  code stay as they were.

## Verification

- **40 focused JVM tests passed** (XML in [tests](tests)): ten recipe/language/extension
  states × viewport 1440/390 × light/dark, including order/copy assertions, exact
  dimensions/gaps/aspect ratio, the responsive table width, RTL language switching, and the
  Shimmer extension's three rows and copied code.
- Wasm development distribution rebuilt; the four quadrants below were captured from the
  final shared build (see [meta.json](meta.json)).
- Four quadrants at 1440×1000 and 390×844 in light and dark:
  [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
  Official background pixels were checked for each quadrant.
- The pulse animation is verified by the native screenshot/observation tests rather than
  browser frames; the browser capture shows the settled 1→0.5→1 opacity phase at capture
  time.

## Remaining differences (page stays unchecked)

- The official phone layout lets the 314dp avatar/profile row overflow its 320dp preview
  column and clip at the edges; the Compose page clamps the row to the preview content width
  (measured 268dp in the 390px browser at the docs page's 16dp page padding + 40dp preview
  padding). The native 342dp fixture fits, so this is a browser-phone overflow difference,
  not a recipe geometry difference.
- `Skeleton` still has no browser reduced-motion hook: the pulse always animates unless the
  caller passes `rememberSkeletonChrome()`/a replacement Style.
- The docs page has no API Reference (the official page has none either); component API is
  covered by the core screenshot tests.
- React Aria/Radix UI tabs, Kotlin code panels, font rasterization and native vs Skia pulse
  frame timing are platform-scope differences. Smaller-than-390dp containers were not
  tested.
