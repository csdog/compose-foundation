# Aspect Ratio r5 — required ratio and current-build verification

## Source pin

The official page was re-read at
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX, Base UI wrapper,
four examples and Nova stylesheet have the same SHA-256 values as r4. Exact
URLs and hashes are in [sources.json](sources.json).

## Changes

- `AspectRatio(ratio:)` is now required, matching the Base UI wrapper.
- The Kotlin component rejects zero, negative, NaN and infinite ratios before
  layout.
- The catalog API section now reports `ratio` as `Required` and documents the
  finite/positive contract.
- The obsolete "square default is retained" assertion was replaced by the
  required-API and invalid-ratio regression coverage.

The signature change is intentional for this pre-release library line. A
repository search found no caller that relied on the removed `1f` default.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Catalog focused tests | 6/6 | [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.AspectRatioExamplesTest.xml) |
| Core layout tests | 3/3 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.aspectratio.AspectRatioLayoutTest.xml) |
| Core screenshot tests | 2/2 | [XML](tests/TEST-com.toutiaoyan.compose.ui.components.aspectratio.AspectRatioScreenshotTest.xml) |
| Wasm distribution build | PASS | [log](logs/wasm-build.log), main Wasm SHA-256 `dfb4b1c16f90934810ef2018a76e8728256402902ff39e2ec8dbb47a77ae3ad2` |
| Changed-file ktlint | 0 findings | No Aspect Ratio file appears in the owning reports; see [ktlint log](logs/ktlint.log) |
| `git diff --check` | PASS | Current worktree |
| Independent review | No findings | [review.md](review.md) |

The four owning ktlint invocations include two passing tasks and two source-set
reports dominated by unrelated pre-existing test-format violations. The report
contains no entry for any of this stage's four modified Kotlin files.

## Browser evidence

Current Wasm resources loaded were `658820f72b4617efb380.wasm` and
`05b244441e7ff1ea3da8.wasm`; metadata, image hashes and viewport state are in
[browser/meta.json](browser/meta.json).

- Four official/local quadrants at 1440×1000 and 390×844, light and dark:
  [browser](browser/).
- Square, Portrait and RTL sections at all four viewport/theme combinations:
  24 captures in [sections/sections.json](sections/sections.json).
- RTL language selector exercised from Arabic to English to Hebrew, including
  both popup states: [browser/interactions.json](browser/interactions.json).

The preview image keeps the expected 1:1, 9:16 and 16:9 frames across desktop
and phone viewports. The RTL caption changes without changing the image frame.

## Accepted differences

- Shared documentation chrome/layout and font rasterization remain covered by
  the site-level parity scope.
- The bundled demo PNG plus Compose image filter stands in for the official
  `next/image` loader.
- Arabic and Hebrew font fallback rasterization cannot be byte-identical to the
  browser's platform fonts.
