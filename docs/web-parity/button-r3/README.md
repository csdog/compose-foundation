# Button r3 — API Reference deduplication

## Findings

The current official Button page still uses the same fifteen local recipe slots and the
same As Link warning against assigning button semantics to links.

The local ComponentDoc configured the Button property list twice:

- once as the root `apiProperties` table;
- again inside the named `Button` API section.

The visual page and Copy Page Markdown therefore duplicated every root Button property.

## Changes

- Removed the redundant root property table while retaining the detailed named Button table.
- Added a Markdown export regression that requires exactly one `onClick` row and preserves
  the separate `buttonStyle` section.

## Verification

The focused Button suite passed 63 tests:

| Suite | Tests |
|---|---:|
| ButtonExamplesTest | 12 |
| ButtonFollowupRecipesTest | 12 |
| ComponentDocumentationTest | 8 |
| ButtonCallerSizeTest | 4 |
| ButtonIconPlacementTest | 2 |
| ButtonInvalidStateTest | 4 |
| ButtonLabelNoWrapTest | 2 |
| ButtonNovaParityTest | 7 |
| ButtonScreenshotTest | 12 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 14.878s with the
existing Webpack warning. App Wasm SHA-256:
`bd8e2002fe4052bccd452e5f1d00a53181d852e328ef7d5dd1e2f10b8330b64a`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed.

## Remaining differences

- Re-run all fifteen recipes at 1440/390 × light/dark against the current build.
- Close menu natural-width/collision and pointer-focus differences, expanded/pressed states,
  icon-set/raster differences, complete dynamic-theme/Style coverage, and native assistive
  technology/touch behavior.
