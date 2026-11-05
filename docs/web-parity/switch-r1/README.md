# Switch r1 — current official page rebuild

Date: 2026-11-05. Official page: <https://ui.shadcn.com/docs/components/base/switch>
(revision `8a3536678a528cc012b9e62484290e79d0be410f`). Local page: `http://127.0.0.1:8765/switch`.

## Findings

The local page was a parameter matrix (Default/Sizes/Default size/Default disabled/With label/
With description/Default invalid/Uncontrolled/Default checked/Required/Thumb…). The current
official page has seven recipes — demo, Description, Choice Card, Disabled, Invalid, Size, RTL —
and the official description "A control that allows the user to toggle between checked and not
checked."

## Changes

- Rebuilt the page to the official order and copy: Switch + clickable label; horizontal
  Field with FieldContent/FieldLabel/FieldDescription; Choice Card rows where the whole card is
  the label and the checked card uses the muted surface; disabled switch + label; invalid Field
  and switch ring with destructive copy; Sm/Default size rows; three-language RTL form.
- Removed the parameter matrix and kept the Kotlin-specific API surface in the root API table
  (`checked`, `defaultChecked`, `onCheckedChange`, size, invalid, required, animation, style).
- Added `SwitchExamplesTest` (8 tests) for recipe order/description, label click, disabled
  semantics, choice-card click, invalid copy, Arabic RTL, and Copy Page order.

## Verification

- `SwitchExamplesTest` 8/8 passed (XML under [tests](tests/)).
- Wasm development distribution rebuilt; app Wasm `71d6df0120f39e0a1946b7ab958b52d472afae1d13417a864d61244d312175ff`.
- Four quadrants: [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
- Section comparisons for Description / Choice Card / Disabled / Invalid / Size / RTL under
  [sections](sections/).

## Remaining differences

- No native HTML `<button role=switch>`/label-for; Compose semantics approximate toggle state.
- Choice Card border/background are rendered by the catalog recipe (official uses FieldLabel
  around Field); visual result matches at the checked sizes.
- Shared page chrome and font rasterization follow the documented project scope.
