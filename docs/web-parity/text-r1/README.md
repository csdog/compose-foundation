# Text r1 — Kotlin extension consistency check

Date: 2026-11-05. Local page: `http://127.0.0.1:8765/text`. No official page:
shadcn/ui styles text with Tailwind classes and has no `Text` component.

## Findings

The page described itself with `TextStyles.Muted` / `TextTypography.*` in Usage and example
`code` strings, but those names do not exist in the current Kotlin API (`TextVariant` /
`TextColors` / `TextStyle`). The code panel therefore documented a removed API, and the page
still looked like an official component (Base UI tab).

## Changes

- Usage and every example now use `TextVariant` / `TextColors.Muted`; imports point at
  `com.toutiaoyan.compose.ui.components.text.*`.
- Added a `Kotlin extension` guide paragraph, a `Variants` recipe that renders every
  `TextVariant`, and a `Text` API table (variant/maxLines/overflow/textStyle/style).
- `ComponentDoc.isKotlinExtension` was added; the shared document header now renders a plain
  `Kotlin extension` label instead of the Base UI tab for extension-only pages (Text, Chip,
  Stepper). Official component pages are unchanged.
- Added `TextExamplesTest` (5 tests) covering the API names, recipe order, variant rendering,
  muted copy and Copy Page markdown.

## Verification

- `TextExamplesTest` 5/5 and `StepperExamplesTest` 3/3 passed in the same run.
- Wasm development distribution rebuilt; app Wasm `4c78d6c1478e32c5f52a.wasm` (local-meta.json).
- Browser quadrants at 1440×1000 and 390×844, light/dark: [browser](browser/). The header now
  shows `Kotlin extension`; TOC lists Variants / Muted / Kotlin extension: Style font-size /
  API Reference.

## Remaining differences

- No official reference exists (project extension), so this is an internal-consistency review.
- Shared catalog chrome (single preview tab, Kotlin code panel) and font rasterization differ
  from the shadcn docs site by the documented project scope.
