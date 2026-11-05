# Alert Dialog r3 — current recipe copy

## Findings

The current official Alerts Dialog page still contains seven recipes in this order:
Default, Basic, Small, Media, Small with Media, Destructive, and RTL.

The component behavior and ordering already matched, but three explanatory paragraphs had
drifted from the current official page:

- Basic described only the required response instead of the title, description, Cancel, and
  Continue composition.
- Small with Media described “combining” the APIs instead of the page's explicit size and
  media guidance.
- Destructive attributed the behavior to a button variant instead of
  `AlertDialogAction`.

## Changes

- Aligned Basic, Small with Media, and Destructive descriptions with the current official
  source, using Kotlin API names.
- Expanded the existing page-order test into a copy regression covering all three paragraphs.
- Preserved the current-page Settings placeholder note because the local catalog has no
  fabricated Settings route.

## Verification

The focused suite passed 28 tests:

| Suite | Tests |
|---|---:|
| AlertDialogExamplesTest | 5 |
| ComponentDocumentationTest | 8 |
| AlertDialogNovaTest | 4 |
| AlertDialogMediaTest | 3 |
| AlertDialogScreenshotTest | 4 |
| AlertDialogMediaScreenshotTest | 4 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 15.947s with the
existing Webpack warning. App Wasm SHA-256:
`0a444bf1c94e5c1343b0a105d4c379b4582596e4d07a9ae7131a1a30d5387a46`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed. A new light/dark desktop/mobile browser pass is not claimed.

## Remaining differences

- Run the seven official dialogs in a real browser at 1440/390 × light/dark.
- Recheck body wrapping and font weight, Arabic fallback typography, assistive-technology
  mapping, and the four old Vega baseline mismatches already recorded in r1/r2.
