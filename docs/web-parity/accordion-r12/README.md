# Accordion r12 — current document geometry and source copy

## Findings

- The official 390 CSS-pixel viewport has a 375 CSS-pixel client width because the classic
  scrollbar consumes 15 pixels. The Accordion document column is therefore 327 CSS pixels,
  starting at x=24, as recorded in [page-layout-r1](../page-layout-r1/official-layout.json).
- The Accordion r7–r11 native verification fixtures still rendered the narrow frame at
  342dp and the RTL/extension content at 260dp. That measured a document width which no
  longer exists after the shared scrollbar geometry fix.
- The Multiple section described `AccordionType.Multiple` although its copied recipe and the
  official MDX use the `multiple` prop.
- The official page ends with an API Reference paragraph linking to Base UI. The local page
  omitted that paragraph and placed Kotlin-only extension recipes directly after the seven
  official examples with no separating heading.

## Changes

- Narrow Accordion frame verification now uses 327dp; RTL and extension scenes use the
  resulting 245dp inner content width.
- Added explicit frame-width assertions so stale 342dp fixtures fail immediately.
- Changed the Multiple explanation to `multiple = true`.
- Added a “Kotlin extensions” heading for the extra Kotlin-only recipes.
- Added the official API Reference paragraph and Base UI link.
- Added a source-copy regression assertion for the Multiple explanation, extension heading,
  copied `multiple` argument, and API link.

## Verification

The combined focused run passed 99 tests:

| Suite | Tests |
|---|---:|
| AccordionPreviewFrameTest | 24 |
| AccordionExtensionTest | 32 |
| AccordionRtlPreviewTest | 4 |
| AccordionExamplesTest | 3 |
| CatalogDocumentLayoutTest | 14 |
| AccordionNovaTest | 15 |
| ComponentDocumentationTest | 7 |

The frame suite verifies all six official examples across 1440/390 and light/dark while
checking that the frame does not grow when an item toggles. The document suite verifies all
seven official viewport widths. The extension suite covers horizontal/vertical keyboard
navigation, RTL arrows, looping, Home/End, and non-collapsible behavior.

The refreshed captures in [native](native/) use the current 327dp narrow frame. Test XML is
archived in [tests](tests/).

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` also completed successfully in
37.315s with the existing Webpack warning; the generated app Wasm SHA-256 is
`1f8719c6bbbcf5ba4813ff8456ca16b2265afdb3db9779b611915353ee53f59b`.

## Browser status

Ego currently exposes no owned compose-foundation TaskSpace. The only available spaces are
the user's main space and unrelated delegated Product Hunt spaces. Per the browser workflow,
no replacement space was created and no user tab was claimed. Therefore this revision does
not claim a new light/dark desktop/mobile browser pass for Accordion.

## Remaining differences

- Re-run the six official examples and Multiple-all-open states in a real owned browser
  space for 1440/390 × light/dark against the current build.
- Exact Geist rasterization, line wrapping, dark glyph weight, and code-panel typography
  still differ.
- Native Hebrew toolbar label ellipsis and the full assistive-technology/touch matrix remain
  open.
- Extra Kotlin extension recipes are intentionally retained and now separated from the seven
  official examples, but their complete Style/dynamic-slot matrix is not an official-page
  equivalence claim.
