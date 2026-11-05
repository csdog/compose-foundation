# Avatar r2 — official page structure and Dropdown grouping

## Findings

The current official Avatar page contains ten recipes:
Default, Basic, Badge, Badge with Icon, Avatar Group, Avatar Group Count, Avatar Group with
Icon, Sizes, Dropdown, and RTL.

The local page already had those ten recipes in order, followed by a local Fallback recipe.
Two structural differences remained:

- The Dropdown recipe omitted the official `DropdownMenuGroup` around Profile, Billing,
  and Settings.
- The extra Fallback recipe appeared as an ordinary page recipe even though the live page
  has no separate Fallback section.

The Sizes guidance also omitted the official backticked `size` prop name.

## Changes

- Wrapped the three related Dropdown rows in `DropdownMenuGroup` in both copied Kotlin and
  the live preview.
- Renamed the extra recipe to `Kotlin extension: Fallback`.
- Matched the Sizes guidance to “Use the `size` prop to change the size of the avatar.”
- Added regression coverage for the ten official titles, extension title, Dropdown group,
  and Sizes copy.

## Verification

The focused suite passed 25 tests:

| Suite | Tests |
|---|---:|
| AvatarExamplesTest | 4 |
| ComponentDocumentationTest | 8 |
| AvatarParityTest | 5 |
| AvatarScreenshotTest | 8 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 17.073s with the
existing one Webpack warning. App Wasm SHA-256:
`d137d06a129661e4a1136ab767ca9253e539ea610159a5516d0d8aab6ab68707`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed.

## Remaining differences

- Re-run all ten official recipes at 1440/390 × light/dark against the current build.
- Close mixed-size group counting, mixed-image border treatment, complete menu focus/pixel
  coverage, and native assistive semantics already recorded in avatar-r1.
