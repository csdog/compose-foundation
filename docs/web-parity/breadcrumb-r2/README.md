# Breadcrumb r2 — grouped menu paths and extension label

## Findings

The current official Breadcrumb page contains seven recipes:
Default, Basic, Custom separator, Dropdown, Collapsed, Link component, and RTL.

The local page had those seven in order, followed by a local Disabled recipe. Two
structural differences remained:

- The Default, Dropdown, and RTL recipes composed Documentation/Themes/GitHub directly
  under `DropdownMenuContent`; the official source wraps them in `DropdownMenuGroup`.
- The extra Disabled recipe looked like an ordinary official page recipe.

## Changes

- Added `DropdownMenuGroup` around the three related menu paths in copied code and all three
  live menu previews.
- Renamed the extra recipe to `Kotlin extension: Disabled`.
- Added regression coverage for the seven official titles, the extension title, and the
  grouped menu source.

## Verification

The focused suite passed 23 tests:

| Suite | Tests |
|---|---:|
| BreadcrumbExamplesTest | 5 |
| ComponentDocumentationTest | 8 |
| BreadcrumbParityTest | 5 |
| BreadcrumbScreenshotTest | 5 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 19.626s with the
existing Webpack warning. App Wasm SHA-256:
`a604c8c70d2c74d0be3eaa47435a290ca711d505da3b02ad53a5975cb86abe5f`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed.

## Remaining differences

- Re-run all seven official recipes at 1440/390 × light/dark against the current build.
- Close the recorded narrow-screen wrapping, native link/navigation semantics, and focus
  differences.
