# Accordion r13 — current-page browser re-verification

## Sources

The current official page was re-read at upstream revision
`8a3536678a528cc012b9e62484290e79d0be410f` (MDX, seven examples and `accordion.tsx`;
checksums in [sources.json](sources.json)). The page order is the hero preview
(no heading) followed by Basic, Multiple, Disabled, Borders, Card, RTL and the Base UI
API Reference paragraph. All recipe titles, order and descriptions match the local
`AccordionDoc` (no page copy change was needed this round); the 31 Kotlin-only recipes
remain after the official six and keep the `Kotlin extension:` prefix.

## Changes

No page or component change was required. This round only re-verified the page in a
real browser space and refreshed the evidence.

## Verification

Final build: `:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` succeeded;
application Wasm SHA-256 `af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`.

Focused tests (final sources) — 107 tests in `:app:shared:jvmTest`:

| Suite | Tests |
|---|---:|
| AccordionExamplesTest | 3 |
| AccordionExtensionTest | 32 |
| AccordionPreviewFrameTest | 24 |
| AccordionRtlPreviewTest | 4 |
| CatalogDocumentLayoutTest | 14 |
| ComponentDocumentationTest | 8 |
| Alert/AlertDialog/AspectRatio/Avatar suites in the same run | 22 |

and 62 tests in `:shadcn:core:jvmTest` (AccordionNovaTest 15, plus the Alert/AlertDialog/
AspectRatio/Avatar component suites 47). JUnit XML is archived in [tests](tests/).

Browser evidence (1440×1000 and 390×844, light/dark), captured after the final build and
pixel-verified for theme: [browser](browser/) contains the four official and four local
quadrants plus the contact sheets. Six official recipes were also captured section by
section at desktop-light: [sections](sections/).

Interactions ([browser/interactions](browser/interactions/)):

- `local-basic-first-open`, `local-basic-second-open`, `local-basic-first-open-again` —
  a single-open accordion switches the open answer and closes the previous one.
- `local-multiple-default`, `local-multiple-billing-open`, `local-multiple-all-open`
  (and `-dark`) — `multiple = true` keeps three items open together.
- `local-disabled-default`, `local-disabled-after-click` — the disabled item does not
  toggle.
- `local-rtl-arabic`, `local-rtl-language-popup`, `local-rtl-english`,
  `local-rtl-hebrew` — the language toolbar switches Arabic ↔ English ↔ Hebrew with the
  matching direction and font.
- `local-horizontal-default`, `local-horizontal-second-click`,
  `local-horizontal-keyboard` — the Kotlin horizontal-orientation extension reacts to
  pointer and arrow-key activation.

## Remaining differences

- Shared page chrome is unchanged: the local top bar is shorter than the official
  header (title starts ~13px higher), the official React Aria/Radix tabs and the
  copy-page chevron are absent by project scope (Base UI only), and the local TOC lists
  the Kotlin extension recipes (official has six recipes + API Reference).
- Preview contents start ~15px lower than the official equivalent at 1440 and the
  description line breaking differs slightly; this is the shared preview-padding /
  Geist-rasterization difference already recorded in the page-layout and r7–r12 records.
- Geist glyph rasterization, dark glyph weight and code-panel typography still differ.
- The full Kotlin extension API/Style/dynamic-slot, assistive-technology and touch
  matrices remain open; they are not an official-page equivalence claim.
