# Alert r10 — official recipe boundary and Markdown export

## Findings

The current official Alert page contains six recipes in this order:

1. Default
2. Basic
3. Destructive
4. Action
5. Custom Colors
6. RTL

The local page already matched those recipes, but appended four Kotlin-only recipes
(`Slots`, `Title`, `Description`, and `Multiple actions`) without identifying them as local
extensions.

While adding a regression for that boundary, the shared Copy Page exporter was found to omit
`ComponentDoc.examplesHeading` even though the visual page renders it. This affected any
component which configured an explicit examples heading.

## Changes

- Pinned the six official Alert recipe titles and their order.
- Renamed the four extra recipes to:
  - `Kotlin extension: Slots`
  - `Kotlin extension: Title`
  - `Kotlin extension: Description`
  - `Kotlin extension: Multiple actions`
- Kept the shared Alert examples heading unset so official Basic through RTL are not
  incorrectly grouped as Kotlin extensions.
- Fixed `componentPageMarkdown` to include a configured `examplesHeading`.
- Corrected the preceding Accordion checkpoint in the same way: official examples retain
  their original titles, while only Kotlin-only recipes carry the `Kotlin extension:` prefix.

## Verification

The focused Alert suite passed 28 tests:

| Suite | Tests |
|---|---:|
| AlertExamplesTest | 6 |
| ComponentDocumentationTest | 8 |
| AlertNovaTest | 8 |
| AlertScreenshotTest | 4 |
| AlertNovaActionScreenshotTest | 2 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 16.822s with the
existing one Webpack warning. The app Wasm SHA-256 is
`f376c1fa7a5b1773039fa42dbae5aba7dd90d1359d2225a7bd633dcbee4dc54d`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed. A fresh Alert desktop/mobile light/dark browser pass is therefore not
claimed.

## Remaining differences

- Re-run the six official Alert recipes in a real owned browser space across 1440/390 ×
  light/dark.
- Verify final-line wrapping, arbitrary complex slots, very tall actions, window resize,
  assistive semantics, and the complete focus/pressed/disabled matrix.
