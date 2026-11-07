# Avatar r4 — group sizing, rings and current-build verification

## Source pin

The current official page was pinned at
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. All 10 examples, the Base UI
wrapper, MDX and Nova stylesheet have the same SHA-256 values as r3; see
[sources.json](sources.json).

## Changes

- Every Avatar inside an AvatarGroup now receives the official
  `ring-2 ring-background` treatment, including non-overlapping groups.
- AvatarGroup tracks member sizes and resolves the default AvatarGroupCount
  size with the official CSS precedence:
  any `sm` -> `sm`, otherwise any `lg` -> `lg`, otherwise `default`.
- `AvatarGroupCount.size` is nullable internally in the group-aware path so an
  omitted value follows the group while an explicit value remains an override.
- The Dropdown regression now covers pointer opening, ArrowUp to the last item,
  Home/End boundaries, wrapped ArrowDown and Escape restoring trigger focus.
- Added light/dark screenshot coverage for spaced groups and a mixed
  Sm + Default + Lg group.

The nullable `AvatarGroupCount.size` parameter is intentionally aligned with
the unreleased pre-release API. Existing explicit AvatarSize call sites remain
source-compatible.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Catalog tests | 5/5 | [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.AvatarExamplesTest.xml) |
| Core parity tests | 7/7 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.avatar.AvatarParityTest.xml) |
| Core screenshot tests | 10/10 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.avatar.AvatarScreenshotTest.xml) |
| Avatar goldens | PASS | [record log](logs/record-avatar-goldens.log), [verify log](logs/verify-avatar-goldens.log) |
| Wasm distribution | PASS | [build log](logs/wasm-build.log), main Wasm SHA-256 `ba10ad5d301a47aa6014fb71d4813c9478f6b04e79ae343052ffd6c36c3f4a72` |
| Changed-file ktlint | 0 findings | No Avatar file appears in the owning reports; see [ktlint log](logs/ktlint.log) |
| `git diff --check` | PASS | Current worktree |
| Independent review | No findings after repair | [review.md](review.md) |

The owning shared JVM-test ktlint task reports four unrelated pre-existing
line-length violations. Core common/test and shared common checks complete;
none names an Avatar file.

## Browser evidence

The current application loaded `658820f72b4617efb380.wasm` and
`af3f6ef212c2d51d83e4.wasm`; viewport/theme metadata and screenshot hashes are
in [browser/meta.json](browser/meta.json).

- Official/local quadrants at 1440×1000 and 390×844, light and dark:
  [browser](browser/).
- Nine official example sections at all four viewport/theme combinations:
  72 captures indexed by [sections/sections.json](sections/sections.json).
- Dropdown menu pointer-open, ArrowUp, Home, End, Escape restoration and dark
  open state; RTL Arabic/English/Hebrew and selector popup states:
  [interactions.json](browser/interactions/interactions.json).

The new mixed-size golden shows a 24dp Count beside Sm/Default/Lg members,
matching the official CSS `sm`-wins cascade.

## Accepted differences

- Shared documentation chrome, layout and Geist rasterization remain covered
  by the site-level parity scope.
- The bundled photos replace the official `next/image` loader without changing
  the official image assets used by the recipes.
- Canvas accessibility mapping differs from the browser's native HTML image and
  div semantics; visible labels and existing Compose descriptions remain.
