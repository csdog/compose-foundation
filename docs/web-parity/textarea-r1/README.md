# Textarea r1 — current official page rebuild

Date: 2026-11-05. Official page: <https://ui.shadcn.com/docs/components/base/textarea>
(revision `8a3536678a528cc012b9e62484290e79d0be410f`). Local page: `http://127.0.0.1:8765/textarea`.

## Findings

The previous page was a 22-entry Kotlin parameter matrix (Default/Default disabled/Default
invalid/… duplicates) with a custom description. The current official page has six recipes —
demo, Field, Disabled, Invalid, Button, RTL — and the official description "Displays a form
textarea or a component that looks like a textarea."

## Changes

- Rebuilt the page: official demo (320dp, `Type your message here.`), Field with
  label → description → textarea, Disabled Field/Textarea, Invalid Field/Textarea with a
  destructive description, Textarea + full-width `Send message` Button, and the three-language
  RTL feedback form with `rows = 4`.
- Removed the duplicate parameter matrix; the remaining Kotlin-only params are curated as
  `Kotlin extension:` recipes (Rows, Min and max lines, Max length, Autofocus, Read only).
- Usage now shows `Textarea(value, onValueChange, placeholder)`; API table lists the public
  Textarea parameters.
- Added `TextareaExamplesTest` (7 tests) covering recipe order/description, Field assembly,
  disabled semantics, invalid description, Button pairing, Arabic RTL copy and Copy Page order.
  `CatalogPreviewTest`'s autofocus case now targets the renamed extension recipe.

## Verification

- `TextareaExamplesTest` 7/7 passed (XML under [tests](tests/)).
- Wasm development distribution rebuilt; app Wasm `af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`.
- Four quadrants: [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
- Section comparisons for Field / Disabled / Invalid / Button / RTL (desktop + phone) under
  [sections](sections/).

## Remaining differences

- No native HTML textarea resize grip (CSS `resize-y`); Canvas cannot provide one.
- No `htmlFor`-style label→control focus association in the Field composition.
- Official previews use `base-nova` here, matching the catalog; shared page chrome and font
  rasterization differences follow the documented project scope.
