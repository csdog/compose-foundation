# Input Group r1 — current official page, Align group and addon variants

Official reference: <https://ui.shadcn.com/docs/components/base/input-group>,
upstream revision `8a3536678a528cc012b9e62484290e79d0be410f`, style `base-nova`.
Fetched 2026-11-05; file SHA-256s in `sources.json`.

## Findings

Local page was still an API dump: Default, inline-end, "block-end (chat composer)",
Disabled, Default disabled, Invalid, Default invalid, Addon, Default align, Button,
Default button variant, Default button size, Text, Input, Textarea. The current
official page has 14 previews: demo, then `Align` (h2) with h3 `inline-start` /
`inline-end` / `block-start` / `block-end`, then Icon, Text, Button, Kbd, Dropdown,
Spinner, Textarea, Custom Input, RTL, and an API Reference with five sections
(InputGroup, InputGroupAddon, InputGroupButton, InputGroupInput, InputGroupTextarea).

Component gaps found while translating:

- `InputGroupInput` did not forward `readOnly`/`type`; `InputGroupTextarea` did not
  forward `readOnly`, `rows`, `minLines` or `maxLines` (needed by the Button and
  Custom Input recipes).

## Changes

- `InputGroupDoc.kt` rewritten to the official 14 recipes and order, with the
  official `Align` callout modelled as an h2 `beforeExamples` guide and the four
  align variants as nested h3 examples (TOC matches the official page), official
  Usage/Composition, and the five official API sections.
- `InputGroup.kt`: pass-through parameters above; no visual style changes.
- Exported code matches every preview. The Custom Input recipe documents that
  Compose has no third-party `TextareaAutosize` equivalent and uses the built-in
  `InputGroupTextarea` with explicit rows.

## Verification

- `InputGroupExamplesTest` 32 tests (8 methods × 1440/390 × light/dark): official
  order/copy/Align grouping, five API sections, all 14 previews, addon texts,
  block-end footer, spinner labels, kbd, disabled/invalid input semantics, RTL
  Arabic→English→Hebrew.
- Core: `InputGroupAddonStyleTest` 5 + `InputGroupScreenshotTest` 12 green.
- Final Wasm build `ace00496e82de7b39bf64c205bd547398e1962626cf568e9cc4b8e9890ff2aeb`.
  Quadrants + `sheet-*` + `meta.json` in `browser/`; interaction/state shots in
  `browser/states/` (inline-start, block-end, button, dropdown menu open,
  RTL Arabic + language switch).

## Remaining differences (explained)

- The `Align` heading is modelled through the catalog's `beforeExamples` guide
  (same visual h2 + callout, four h3 recipes nested); the ToC renders it identically.
- Custom Input: no Compose `TextareaAutosize`; the recipe uses `InputGroupTextarea`
  with rows and explicit height instead of DOM auto-sizing.
- Button recipe: the copy action toggles the check icon locally (no clipboard
  dependency in the catalog), and the info popover uses plain text content.
- The Dropdown recipe relies on the shared `DropdownMenu`; menu pointer-open initial
  highlight behaviour is tracked in the Item/Dropdown Menu residuals.
- RTL preview adds the shared catalog language toolbar; its Hebrew label uses the
  shared font fallback while the recipe body uses the official translations.
- Native `htmlFor`/input ids and arbitrary className overrides are Kotlin
  equivalents (semantics/Modifier) rather than DOM attributes.
