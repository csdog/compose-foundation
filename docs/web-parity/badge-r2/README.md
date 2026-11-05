# Badge r2 — copy-ready Usage and Link guidance

## Findings

The current official Badge page still has seven recipes:
Default, Variants, With Icon, With Spinner, Link, Custom Colors, and RTL.

The component behavior and custom palette values already matched. Two local documentation
issues remained:

- The copied Usage code referenced `BadgeVariant` but imported only `Badge`.
- The Link guidance said “navigate as a badge” rather than matching the official
  “render a link as a badge” wording.

## Changes

- Added the missing `BadgeVariant` import to the Usage code.
- Aligned the Link explanation while retaining the local `href`/`onClick` navigation note.
- Added regression assertions for the import and exact Link guidance.

## Verification

The focused suite passed 30 tests:

| Suite | Tests |
|---|---:|
| BadgeExamplesTest | 8 |
| ComponentDocumentationTest | 8 |
| BadgeScreenshotTest | 4 |
| BadgeExtendedApiTest | 4 |
| BadgeNovaParityTest | 6 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 13.721s with the
existing Webpack warning. App Wasm SHA-256:
`2f9ef10ba8fc012ada37c6893cc0d61dbc1ca4eff46c811e9253de37f5caa00f`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed.

## Remaining differences

- Re-run all seven recipes at 1440/390 × light/dark against the current build.
- Native link semantics, complete interaction/assistive-technology coverage, and shared page
  differences remain open.
