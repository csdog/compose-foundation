# Breadcrumb r4 — focus visibility, current-page semantics and current-build verification

## Source pin

The current official page is pinned at
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX and seven examples match
r3 byte-for-byte; the current Base wrapper and Nova stylesheet are archived in
[sources.json](sources.json).

## Changes

- BreadcrumbLink now draws a theme-colored focus ring through the transparent
  surface knockout path.
- Added regression coverage for Tab order, Enter/Space activation and disabled
  links.
- The current page is rendered with non-selectable BasicText and exposed as
  `"<text>, current page"` because Compose has no direct `aria-current`
  property. It remains non-clickable.
- Added light/dark focused-link screenshot states.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Catalog tests | 5/5 | [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.BreadcrumbExamplesTest.xml) |
| Core parity tests | 6/6 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbParityTest.xml) |
| Core screenshot tests | 7/7 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbScreenshotTest.xml) |
| Breadcrumb goldens | PASS | [record log](logs/record-breadcrumb-goldens.log), [verify log](logs/verify-breadcrumb-goldens.log) |
| Wasm distribution | PASS | [build log](logs/wasm-build.log), main Wasm SHA-256 `7a06664db4f567efcb5a8b6c17663b03903d6d377f080473180b6d216dc9f394` |
| Changed-file ktlint | 0 findings | No Breadcrumb file appears in the owning reports; see [ktlint log](logs/ktlint.log) |
| `git diff --check` | PASS | Current worktree |
| Independent review | Focus difference documented | [review.md](review.md) |

The shared JVM-test ktlint task reports four unrelated existing line-length
violations. Core common/test and shared common checks complete; none names a
Breadcrumb file.

## Browser evidence

The current application loaded `658820f72b4617efb380.wasm` and
`b6f0ddb1a51efe5194b3.wasm`; metadata and screenshot hashes are in
[browser/meta.json](browser/meta.json).

- Official/local quadrants at 1440×1000 and 390×844, light and dark:
  [browser](browser/).
- Six official example sections at all four viewport/theme combinations:
  48 captures indexed by [sections/sections.json](sections/sections.json).
- Link hover, Dropdown open/closed/dark and RTL Arabic/English/Hebrew states:
  [interactions.json](browser/interactions/interactions.json).

Keyboard focus and Enter/Space activation are covered by the JVM suite and the
light/dark focus goldens. The browser interaction set does not synthesize a
Canvas keyboard-focus state.

## Known Compose focus difference

After the last interactive link, the desktop test semantics still mark the
non-clickable current-page BasicText node as focused, although it has no click,
selection, or explicit focus target. The test therefore verifies that the last
link loses focus and that the current page has no click action; it does not
claim that this Compose desktop semantics state is eliminated.

This is recorded as a Compose focus/semantics difference, analogous to the
official DOM's `span[aria-current=page]`. Shared page chrome and font
rasterization remain site-level differences.
