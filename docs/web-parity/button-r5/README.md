# Button r5 - current page and interaction parity

## Source pin

The official page was re-read from
`ui.shadcn.com/docs/components/base/button` at revision
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX, Base UI wrapper,
15 examples and Nova CSS are archived with URLs and SHA-256 hashes in
[sources.json](sources.json).

## Changes

- Removed the non-official Composition section from the Button page.
- Renamed the Kotlin-only `buttonStyle` API entry to
  `Kotlin extension: buttonStyle` and identified its Kotlin-only behavior.
- Added page-order and API-copy assertions covering all official headings.
- Fixed the Button Group `Label As...` submenu to the official 96dp panel
  width. Radio rows use the matching compact horizontal inset so Personal,
  Work and Other remain on one line.
- Added a focused 96dp submenu-width assertion and kept the selected radio
  value after closing and reopening the menu.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Catalog focused tests | 24/24 | [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.ButtonExamplesTest.xml), [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.ButtonFollowupRecipesTest.xml) |
| Exact Button core suites | 42/42 | [log](logs/verify-core.log), [XML](tests/) |
| Wasm distribution | PASS | [log](logs/wasm-build.log), main Wasm SHA-256 `a4730c39243ffaeb06a76e3be511b7a5adfd563b7515b523c29ff83007f5d481` |
| Owning ktlint audit | No Button finding | [log](logs/ktlint.log); aggregate reports still contain unrelated pre-existing violations |
| `git diff --check` | PASS | Current worktree |

The exact core run covers the six `components.button` suites, Button Group
screenshots and shared button focus-visible behavior. Catalog coverage runs
at 390 and 1440 widths in light and dark modes.

## Browser evidence

The final build loaded `658820f72b4617efb380.wasm` and
`f6f852c7632388536e67.wasm`. Capture metadata and image hashes are recorded
under the evidence JSON files below.

- Four official/local viewport-theme quadrants:
  [browser/meta.json](browser/meta.json).
- All 15 Button sections at 1440x1000 and 390x844, light and dark:
  120 captures in [sections/sections.json](sections/sections.json).
- Button Group and As Link interactions:
  [browser/interactions/interactions.json](browser/interactions/interactions.json).

The interaction set includes official and local desktop/phone menu and
submenu states. The local Label As submenu is 96dp wide on desktop and flips
left on phone, matching the official page. Work remains selected after the
menu is closed and reopened. The styled Login link exposes hover and pointer
focus states; pointer activation changes the hash from `#example-12` to the
empty fragment. Loading buttons remain disabled, and the RTL selector was
exercised through Arabic, English and Hebrew.

## Accepted differences

- Shared documentation shell, sidebar coverage, typography rasterization and
  the local installation/code tabs remain site-level parity work.
- Browser keyboard-only focus traversal is represented by the JVM regression
  that verifies one Tab stop and Enter activation; browser evidence covers
  pointer focus and activation plus the same link's URL annotation.
- Compose Canvas does not expose the same native HTML link semantics as the
  official `<a>` element. The local recipe uses `LinkAnnotation.Url` and avoids
  assigning `Role.Button`.

## Review

Independent reviewer and verifier results are recorded in [review.md](review.md).
