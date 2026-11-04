# Shimmer, round 2: currentColor, CSS colors and nine recipes

Status: in progress. This is a source/native checkpoint, not completed browser or full utility acceptance. [Round1](../shimmer-r1/README.md) records the preceding paint/observation fix.

## Sources and changes

The [eleven pinned shadcn sources](../shimmer-r1/sources.json) remain at `96da9cc7f1aa10580a3741664e2a77c721206c00`. Interpolation rules were checked against [CSS Color4 alpha interpolation](https://www.w3.org/TR/2026/CRD-css-color-4-20261029/#interpolation-alpha), [shorter hue](https://www.w3.org/TR/2026/CRD-css-color-4-20261029/#interpolation-hue), and the [CSS Images4 default gradient space](https://www.w3.org/TR/2025/WD-css-images-4-20250930/). These support premultiplying color coordinates but not polar hue, and using Oklab for modern gradient interpolation. The native implementation's gamut/raster limitations below still apply.

Four RED pixel tests proved the old implementation's visible differences: wrong default base instead of actual red text, light black peak around0.478 rather than translucent black over white, dark peak around0.455 instead of relative OKLCH brightening, and an opaque purple result from half-transparent blue. All four passed after implementation.

- Text resolves currentColor during drawing. Supported inputs include explicit TextStyle, LocalContentColor, ancestor Style color, own Style.shimmer, raw `Style { contentColor(...) } then rememberShimmerStyle()` on Text, and changes while the animation clock is paused. The existing explicit baseColor parameter remains an override.
- An internal TextColorBrush protocol carries draw-time color to the brush. A Style wrapper preserves a preceding own color when Foundation's contentBrush setter clears it; a nearer explicit text color still wins. No per-frame snapshot write back to composition is introduced.
- Light default peak retains the actual base RGB and multiplies its alpha by0.2. Dark applies max(0.8,L+0.4), capped at1, retains chroma/hue, and raises alpha by0.4 capped at1. Lightness endpoints map to black/white. An explicit highlight retains its alpha rather than being composited into an opaque base.
- Intermediate stops use premultiplied OKLCH mixing, shorter hue and powerless-gray hue borrowing. The Oklab gradient is approximated by257 evenly spaced sRGB samples, with a bounded cache for four base colors to support shared Styles without repeated conversions each animation frame.
- The Modifier SrcAtop overlay remains a separate extension for drawn content. The primary page/usage now uses the CSS text-fill Style path. No Attachment change was made; its deferred gaps stay deferred.

## Page and recipe migration

Default, With Marker, Color, Duration, Spread, Angle, Play Once/Replay, Disabling the Shimmer and RTL are prepended to the page. Existing extension demonstrations remain, with the old modifier default named Overlay (Kotlin). The generated snippets include imports and shared helpers from the compiled preview source.

Text uses14/20 line boxes; labels use12/16 monospace. Comparison groups are capped at512dp, gap24, switching from one column to two at640dp. The gradient fills the comparison paragraph's whole width rather than only the glyph width. Spread examples use16dp and96dp. Color examples use the existing Tailwind blue500 at60% alpha and #378ADD. Once uses1100ms and a keyed Replay action. None disables the effect from768dp. Marker has two polite status regions and a decorative spinner, and tests measure each label's pixel motion independently of spinner motion. RTL preserves the source English/Arabic copy and isolated reading directions.

## Validation scope and remaining differences

Core38 tests plus catalog40 tests pass. Core and shared ktlint pass. Four core color captures and40 catalog captures were reviewed, including all viewport1440/390 and light/dark combinations. The four review sheets preserve the40 opaque native images' RGB pixels without scaling. The catalog fixtures override WindowInfo and use native Compose rendering; they do not prove phone-browser rendering, browser preferences or real UI acceptance.

Still open:

- Automatic reduced-motion preference handling. Explicit enabled=false and responsive None are tested; neither proves automatic platform/browser preference support.
- Actual-font `3ch+40px` default spread.64dp remains a documented approximation, and the Dp API does not yet model percentage spread or arbitrary CSS length units.
- Browser desktop/mobile light/dark; exact interpolation/gamut mapping and DisplayP3; saturated out-of-sRGB colors; near-achromatic thresholds and alpha quantization. Compose converts through Oklab/sRGB and uses sampled shader stops. No browser-equivalence or global numerical-error bound is claimed for that approximation.
- Full timing/angle/reverse/RTL matrices, exact640/768 boundaries and dynamic window resizing; custom fonts/density/scaling; rich-text transparent spans, decoration, inline content, emoji and variant-conditioned Style scope capture.
- Raw Foundation.styleable on an ancestor box can still clear a same-node preceding color unless a preserving Style.shimmer wrapper is used. The raw then-chain test covers Text's own Style; it is not a claim about arbitrary external Foundation nodes.
- Existing overlay-based component demos/consumers retain their extension behavior and need the corresponding component's follow-up audit. No full benchmark or assistive-technology equivalence is claimed.

Legacy Shimmer/Text screenshot verification remains4PASS/2CHANGED. The two actual PNGs match round1's exact pre-pass isolation byte for byte, so these remain preexisting differences this round. No screenshot baseline was recorded. The original Ego TaskSpace18 recovery authorization remains unanswered; no replacement was created. Editor remains excluded.

## Final checkpoint

[Summary](verification/summary.json):78 independent tests and both owning-module ktlint checks pass. JS exits0 in35.755s with existing os/path diagnostics; Wasm exits0 in18.911s with one webpack warning. All20 source/configuration/generator/test hashes and44 reviewed native image hashes matched at parent archiving. The12-run workflow was finished and raw logs deleted. Version0.3.28. No full-repository commit gate or screenshot baseline update was performed.
