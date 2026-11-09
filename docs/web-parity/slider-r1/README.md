# Slider r1 — official seven recipes and Nova thumb/fill states

Date: 2026-11-05. Local page: `http://127.0.0.1:8775/slider`.
Official page: <https://ui.shadcn.com/docs/components/base/slider>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (11 fetched sources/hashes in [sources.json](sources.json)).

## Findings

The page was a 19-item parameter matrix (Default/Disabled/Horizontal/Vertical/Inverted/Step/
Min-steps/Min-max/Commit/Value-label/default variants). The current official page has seven
previews — demo (no heading), Range, Multiple Thumbs, Vertical, Controlled, Disabled, RTL — plus
Usage and a link-only API Reference. All previews use `base-nova`; the Nova CSS defines a 4 dp
`bg-muted` track, a `bg-primary` range fill and a 12 dp white thumb with `border-ring` and 3 px
hover/focus/active rings (16 dp hit area via `after:-inset-2`).

## Changes

- Rebuilt the page in official order/title/copy: Default (75, 0–100, step 1), Range (25–50,
  step 5), Multiple Thumbs (10/20/70, step 10), Vertical (two 160 dp sliders), Controlled
  (0.3–0.7 temperature range with label and readout), Disabled (50, inert), RTL (translated
  `RtlPreview` toolbar with the slider mirrored by `LocalLayoutDirection`).
- Usage replaced with the official `Slider` default snippet mapped to the Kotlin API
  (`value`/`onValueChange`, `valueRange`, `step`).
- API Reference converted to a Kotlin API section that also cites the Base UI Slider docs
  (project convention for link-only official API pages).
- Kotlin-only extras kept after the official recipes as `Kotlin extension:` (Step, Inverted,
  Commit, Min steps between thumbs); the duplicated "default" parameter variants were removed.
- RTL preview now wraps its slider in `CompositionLocalProvider(LocalLayoutDirection)` so the
  Arabic/Hebrew fill mirrors from the right (this was missing in the first capture pass; fixed and
  recaptured).

## Verification

- **Tests: 7/7 pass** (`SliderPageTest`): recipe order/titles/descriptions + Kotlin-extension
  labels, Usage/Base UI API copy, default starts at 75 and accepts semantic value changes,
  range/multiple thumb range semantics (25 / 10), controlled readout (0.3, 0.7) + disabled state,
  keyboard Home/End/step (100 → 0 → 1) through the slider semantics, commit extension readout.
  XML under [tests](tests/).
- **Build:** Wasm snapshot wasm
  `02d85afc1cd47cd566e3c5d532c5ff93b15a82698e079e7405a8d7e21735fa49` (slot e / 8775).
- **Four quadrants** 1440×1000 / 390×844 light+dark after the final build, official theme
  re-asserted and checked: [desktop light](browser/sheet-desktop-light.png),
  [desktop dark](browser/sheet-desktop-dark.png), [phone light](browser/sheet-phone-light.png),
  [phone dark](browser/sheet-phone-dark.png). The 75 % demo matches the official track length,
  4 dp fill and 12 dp white thumb in both themes.
- **States** ([browser/interactions](browser/interactions/)): default at 75 (`default-75`), range
  with two thumbs and the 0.3–0.7 fill (`range`), three thumbs (`multiple-thumbs`), vertical pair
  (`vertical`), controlled readout `0.3, 0.7` (`controlled-03-07`), disabled 50 (`disabled-before`),
  RTL Arabic mirrored fill with the language menu open (`rtl-arabic`, `rtl-menu`).

## Remaining differences / open items

- API Reference keeps the project's Kotlin API table plus the official Base UI link instead of the
  official single link sentence.
- Browser pointer/keyboard interaction with the Compose slider could not be exercised through ego's
  synthetic pointer events: clicking the thumb does not move Compose focus in this Canvas app, so
  End/Home/arrow key presses scroll the page instead of the slider (the same Canvas focus limitation
  seen on other focusable previews). Keyboard Home/End/step is verified by the JVM semantics test;
  real drag, touch and pointer-focus matrices remain open.
- Full assistive-technology mapping (role, value text, label association), dynamic focus between
  multiple thumbs, and per-platform gesture/step behaviours remain open; mobile capture is 390 px
  mouse emulation, not a real touch device.
- Shared title/description wrapping, preview centring and font rasterization remain shared-page
  items; the official demo's `max-w-xs` width maps to the local 320 dp cap.
