# Button Group r2 — current official recipe set

## Findings

The current official Button Group page has moved beyond the legacy local parameter gallery.
Its sections are:

Default, Orientation, Size, Nested, Separator, Split, Input, Input Group, Dropdown Menu,
Select, Popover, and RTL.

The local page still showed thirteen older examples such as Outline, With label, Spacing,
Disabled, and Item. The shared `ButtonGroupItem` also had no `size` parameter even though
individual Button sizes are part of the current Size recipe.

## Changes

- Added an additive `size: ButtonSize = ButtonSize.Md` parameter to both
  `ButtonGroupItem` overloads.
- Rebuilt the page around the twelve current official recipes.
- Added functional Kotlin previews for Orientation, Size, Nested, Separator, Split, Input,
  Input Group, Dropdown Menu, Select, Popover, and RTL.
- Added page-order, size-height, Dropdown/Popover opening, and RTL language tests.

## Verification

The focused suite passed 42 tests:

| Suite | Tests |
|---|---:|
| ButtonGroupExamplesTest | 16 |
| ButtonFollowupRecipesTest | 12 |
| ComponentDocumentationTest | 8 |
| ButtonGroupScreenshotTest | 6 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 23.937s with the
existing Webpack warning. App Wasm SHA-256:
`89f199d0691f9915804ed3b1a1e394c19944fa1e65a9074785094bd7d09bf556`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed.

## Remaining differences

- Run all twelve recipes at 1440/390 × light/dark against the current build.
- The local ButtonGroup primitive does not expose Compose's platform equivalent of the web
  `role="group"`; verify its current semantics and assistive behavior.
- Shared menu natural width/collision, mixed-child corner inheritance, and multi-control
  focus behavior still require dedicated visual and interaction verification.
