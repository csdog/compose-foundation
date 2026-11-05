# Stepper r1 — Kotlin extension consistency check

Date: 2026-11-05. Local page: `http://127.0.0.1:8765/stepper`. No official page:
shadcn/ui ships no Stepper component.

## Findings

The page was not labeled as a project extension, had no Usage imports block, no explanation of
the component's origin, and no API Reference; the shared header showed a misleading `Base UI`
tab.

## Changes

- Added the `Kotlin extension` guide paragraph explaining that no Base UI page exists.
- Added `usageImportsCode`, a `Stepper` API table (currentStep/orientation/showLabels/styles/
  content) and a `StepperItem` table (title/description/disabled), plus the extension flag so the
  header reads `Kotlin extension`.
- Kept the 12 existing recipes; their code and previews already match.
- Added `StepperExamplesTest` (3 tests): extension labeling, API tables, and the Default preview
  rendering/advancing steps.

## Verification

- `StepperExamplesTest` 3/3 passed.
- Wasm development distribution rebuilt; app Wasm `4c78d6c1478e32c5f52a.wasm`.
- Browser quadrants at 1440×1000 and 390×844, light/dark: [browser](browser/). TOC lists the
  Kotlin extension guide, 12 recipes and API Reference > Stepper/StepperItem; phone layout fits.

## Remaining differences

- No official reference exists (project extension); internal consistency only.
- Shared catalog chrome and font rasterization differences follow the documented project scope.
