# Chip r1 — Kotlin-extension page consistency, labeling and API accuracy

Chip is **not an official shadcn/ui component**. shadcn ships a static `Badge`; the
Base UI docs tree has no `chip.mdx` at the pinned revision
`8a3536678a528cc012b9e62484290e79d0be410f` (probe returned HTTP 404, recorded in
`sources.json`). This page therefore claims no official comparison and is marked as
a Kotlin extension.

## Findings

- The page was not labeled as an extension anywhere and had no API reference.
- Its recipe list was an API dump: `Default`, `Default variant`, `Selectable`,
  `Selected`, `Default selected`, `Outline`, `Disabled`, `Default disabled`, `Icon`,
  `Default leading icon`. Four of those merely restate parameter defaults.
- The tile description, usage and variant list were accurate, but `enabled`/`disabled`,
  `animation` and the selected-aware default variant were undocumented.

## Changes

- `ChipDoc.kt`: added the `Kotlin extension` guide paragraph (states that Chip is a
  Kotlin extension, that shadcn only ships static `Badge`, and that there is no
  official page/recipe list to mirror), set `isKotlinExtension = true`, replaced the
  API-dump recipes with seven meaningful ones (`Default`, `Selectable`, `Selected`,
  `Outline`, `Disabled`, `Icon`, `Row`), and added accurate `Chip` / `ChipVariant`
  API tables from the component source (`label`, `onClick`, `selected`, `variant`,
  `enabled`/`disabled`, `leadingIcon`, `animation`, `modifier`/`style`; three
  variants with their content colors).
- No component behavior change was needed.

## Verification

- `ChipExamplesTest` 16 tests (4 methods × 1440/390 × light/dark): the extension
  label/guide, recipe list, API table accuracy, every preview rendered, selectable
  toggle (`selected` semantics) and disabled semantics/icon slot.
- `:app:shared:jvmTest --tests ...ChipExamplesTest` green; XML archived in `tests/`.
- Final Wasm build SHA `89721bca784cc2dd66346c26a0de1fa9a445ecf2a0d7ea0d13e04fc658b8129d`; browser quadrants (1440/390 × light/dark) captured in `browser/`; the official
  screenshots are the expected 404 page because no official page exists.

## Remaining differences (explained)

- There is no official reference for this page by definition; "parity" here is
  internal consistency + accurate Kotlin API documentation, not a visual match.
- The shared `ComponentDoc` model renders "Base UI" for official pages; Chip uses the
  Lead-provided `isKotlinExtension` flag so the header reads "Kotlin extension".
- Pseudo-class matrix beyond selected/hover/focus/disabled (pressed mid-frame,
  long-press, RTL text with the shared language toolbar) is not part of this
  extension page's verification; the component's existing screenshot tests still
  cover its light/dark states.
