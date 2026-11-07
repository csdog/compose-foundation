# Bubble r3 — keyboard states, complete ghost content and current-build verification

## Source pin

The current official page is pinned at
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX and nine official examples
match r2 byte-for-byte; the current Base wrapper and Nova stylesheet are
archived in [sources.json](sources.json).

## Changes

- Added a regression that scrolls all three Variants ghost paragraphs into a
  600dp test viewport and verifies the final paragraph has non-zero bounds.
- BubbleContent keyboard coverage now includes disabled, pointer, Enter and
  Space activation.
- Tooltip coverage includes focus, Enter, Escape dismissal and trigger focus
  restoration.
- Popover coverage opens through both Enter and Space, closes with Escape and
  restores trigger focus.
- Interactive reaction coverage includes pointer and keyboard activation.
- Added light/dark focused interactive BubbleContent goldens.

The Bubble component itself did not require a production-code change; the
closure is evidence and regression coverage for behavior already present.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Catalog tests | 9/9 | [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.BubbleExamplesTest.xml) |
| Core parity tests | 7/7 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.bubble.BubbleParityTest.xml) |
| Core screenshot tests | 14/14 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.bubble.BubbleScreenshotTest.xml) |
| Bubble goldens | PASS | [record log](logs/record-goldens.log), [verify log](logs/verify-goldens.log) |
| Current Wasm | PASS | `b6f0ddb1a51efe5194b3.wasm`, SHA-256 `7a06664d…f394` shared with the site checkpoint |
| Changed-file ktlint | 0 findings | No Bubble file appears in owning reports; see [ktlint log](logs/ktlint.log) |
| `git diff --check` | PASS | Current worktree |
| Independent review | No blocking findings | [review.md](review.md) |

Owning ktlint tasks still report unrelated pre-existing line-length violations
in other catalog files. Core common/test and Bubble files are clean.

## Browser evidence

The current application loaded `658820f72b4617efb380.wasm` and
`b6f0ddb1a51efe5194b3.wasm`; metadata and screenshot hashes are in
[browser/meta.json](browser/meta.json).

- Official/local quadrants at 1440×1000 and 390×844, light and dark:
  [browser](browser/).
- Eight official example sections at all four viewport/theme combinations:
  64 captures indexed by [sections/sections.json](sections/sections.json).
- Full Variants ghost paragraph capture, link/toast, interactive reaction,
  Collapsible open/closed, Popover open/closed/Escape and dark Popover:
  [interactions.json](browser/interactions/interactions.json).

## Tooltip browser limitation

Ego's synthetic pointer hover did not open Bubble's Canvas Tooltip. The Tooltip
behavior is therefore verified by the JVM focus/Enter/Escape/focus-restoration
test rather than browser screenshot. This is an automation limitation; no
browser open Tooltip state is claimed.

Shared page chrome and font rasterization remain site-level differences.
