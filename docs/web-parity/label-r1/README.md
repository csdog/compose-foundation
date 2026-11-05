# Label — Web parity r1 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/label](https://ui.shadcn.com/docs/components/base/label),
Base UI + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`).

## Official page reading

- Unnamed demo: checkbox + "Accept terms and conditions" label.
- A callout right after the demo points form fields at `Field`/`FieldLabel`.
- Sections: Installation, Usage, Label in Field (embeds the shared `field-demo` payment form), RTL,
  API Reference.
- The React Label is `<label htmlFor>`: clicking it toggles/focuses the associated control.

## Findings and changes

The old local page was a parameter matrix (`Required`, `Default required`, `Disabled`,
`Default disabled`, `With a checkbox`) with a non-official description and no Label-in-Field or RTL
recipe. Rebuilt `LabelDoc.kt` to the official page:

- Official description "Renders an accessible label associated with controls." and official Usage.
- Recipes in official order: `Default` (checkbox + label, clicking the label toggles the checkbox),
  `Label in Field` (payment form with `FieldLabel`/`FieldDescription`/`FieldSet`/`FieldGroup`/
  `FieldSeparator`, matching the official `field-demo` content and the Field guidance as its
  description), `RTL` (Arabic "قبول الشروط والأحكام" with the catalog's language toolbar and RTL
  layout), then two Kotlin-only extras: `Kotlin extension: Required` and
  `Kotlin extension: Disabled` (the `required`/`disabled` props are Kotlin-only).
- New `LabelExamplesTest`: official order/description, label-click toggles the demo checkbox,
  the payment-form fields render, RTL Arabic label, required marker and disabled text.

## Verification

- `LabelExamplesTest` 6/6 pass (`tests/`).
- Wasm Web build `wasm=60af0664e81eab666f2dc4c04083fd58a62eb43a82e1813a9c67e8360c202288`;
  four quadrants captured after the final build and reviewed (new shared description layout
  included): `local-*.png`, `sheet-*.png`, `browser/meta.json`.
- Browser: clicking the demo label checks its checkbox
  (`browser/desktop-dark-label-click-checked.png`).

## Remaining differences (open)

- Compose has no `htmlFor`-style implicit label/control association: the demo wires the label click
  manually, and `FieldLabel`s in the payment form do not move focus into their inputs when clicked.
  Callers must pass a click/focus modifier (or use `Checkbox(label = ...)`) for that behaviour.
- The official post-demo callout is not rendered in the local page skeleton; its Field guidance is
  the description of the `Label in Field` recipe instead.
- Arabic line boxes were checked visually (single-line label at 14/14), but arbitrary fonts, emoji
  and multi-line Arabic labels and the full assistive-tech matrix remain open. Label stays unticked.
