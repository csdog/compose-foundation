# Field r1 — current official page, Nova layout fixes, browser recheck

Date: 2026-11-05. Local page: `http://127.0.0.1:8774/field`.
Official page: <https://ui.shadcn.com/docs/components/base/field>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (all 16 fetched sources and hashes in
[sources.json](sources.json)).

## Findings

The local page still had 26 ad-hoc Kotlin recipes under an `Examples` heading; the current
official page has a 13-preview sequence, then prose sections and a ten-entry API Reference:

- Demo (payment form, `max-w-md` = 448, preview `h-[800px] p-6 md:h-[850px]`), Installation,
  Usage, Composition (H3: Field / FieldGroup / FieldSet), Anatomy, Form,
  Input, Textarea, Select, Slider, Fieldset, Checkbox, Radio, Switch, Choice Card,
  Field Group, RTL, Responsive Layout, Validation and Errors, Accessibility, API Reference.
- Page description is “Combine labels, controls, and help text to compose accessible form
  fields and grouped inputs.”
- Live DOM measurements used for parity: legend `16/24` with `margin-bottom 6px`;
  description directly after a legend-variant legend gets `margin-top -6px`; FieldSet
  `gap 16`; FieldGroup `gap 20` (`data-slot=checkbox-group` groups use `gap 12` and the parent
  FieldSet switches to `gap 12`); separator `height 20` with `-my-2`; input `32px`,
  `padding 4/10`, radius 10; choice-card label radius 10 and inner Field padding 10.

## Changes

- Rebuilt `FieldDoc.kt` to the official order/titles/copy: `examplesHeading = null`,
  thirteen official recipes, Composition + Field/FieldGroup/FieldSet (level 3), Anatomy,
  Form as `beforeExamples`; Validation and Errors + Accessibility as `afterExamples`; ten
  `apiSections`; usage code mirrors the official Profile example in Kotlin.
- Added the official recipes' real content: payment demo with 448 width, select grid,
  separator, billing row, comments and reset; Username/Password fields (description before
  the password input); Feedback textarea `rows = 4`; Department select (nine items, full
  width); Price Range pair slider with weighted values in the description; Address
  Information; desktop checkbox list + sync description; Subscription Plan radios;
  horizontal MFA switch; Kubernetes/VM choice cards; Responses/Tasks group with a disabled
  checkbox and “Manage tasks” link; three-language RTL payment form reusing the shared
  toolbar/Noto fonts; Responsive Layout at the 448dp container breakpoint.
- Component fixes:
  - New `FieldSetLayout` (Nova): direct children are tagged (legend / label-legend /
    description / group); `legend → description` loses 6dp; a `FieldSet` holding a dense
    group uses `gap 12`.
  - `FieldGroup(dense = true)` mirrors `data-slot="checkbox-group"` (12dp gap and the dense
    parent gap); legacy spacing unchanged.
  - `FieldHorizontalLayout` follows CSS `*:w-auto`: controls keep their intrinsic width,
    `FieldContent` (`flex-1`) receives the remaining width; the responsive recipe gives the
    input the HTML default 192dp width.
  - `FieldSeparator` Nova: 20dp box with `-8/-8` layout margins, 1dp line behind an optional
    background-masked `px-2` label.
  - `FieldTitle` dims to 50% in a disabled field; choice-card `FieldLabel` uses the theme
    `rounded-lg` (10dp) radius instead of a hardcoded 8dp.
  - `FormNovaParityTest` line-height expectation corrected to the official
    `leading-normal` 21sp for `FieldDescription` (error text stays 20).

## Verification

- **27 focused JVM tests passed** (XML in [tests](tests)): 9 `FieldExamplesTest`
  (recipe order/copy/API, 40dp legend→description offset, radio/choice selection,
  checkbox-label toggle, 448 responsive breakpoint, switch label/control toggle, Arabic RTL
  copy), 14 `FormNovaParityTest`, 4 `FieldScreenshotTest`.
- Wasm development distribution rebuilt; the four quadrants below were captured from the
  final shared build `421a6c71…` after the `FieldHorizontalLayout` rework (see
  [meta.json](meta.json)).
- Four quadrants at 1440×1000 and 390×844, light and dark: [sheet-desktop-light](sheet-desktop-light.png),
  [sheet-desktop-dark](sheet-desktop-dark.png), [sheet-phone-light](sheet-phone-light.png),
  [sheet-phone-dark](sheet-phone-dark.png); per-recipe official/local comparisons were also
  reviewed while iterating.
- Browser interactions in [browser](browser): Select open (all items incl. the placeholder
  item) and choosing “Design”, Radio plan switch, Choice Card move to Virtual Machine,
  Switch on, RTL Arabic→Hebrew language switch, phone Responsive stacking, dark Select
  panel, focused input with typed text.

## Remaining differences (page stays unchecked)

- Compose has no `htmlFor`: clicking a plain field label does not focus/activate its control.
  Checkbox/radio/switch recipes wire the label explicitly; inputs are reachable with Tab.
- `FieldDescription`'s `nth-last-2:-mt-1` (4dp less when the description is second-to-last in a
  `Field`) is not implemented; the official Input → Password recipe differs by 4dp.
- `FieldSet`'s `has-[>[data-slot=radio-group]]:gap-3` cannot be expressed because `RadioGroup`
  is a separate component without a data-slot marker; Radio and Choice Card legend→description
  spacing stays 16dp instead of 12dp.
- Responsive Layout's official three-item bullet list is rendered as “• ”-prefixed paragraph
  lines without a hanging indent (the example description slot only renders paragraphs).
- Textarea has no browser-native resize grip.
- `FieldError` with several messages renders inline “• ” items instead of `<ul class="list-disc">`
  (no current recipe shows this state); screen-reader/label association and the React Aria /
  Radix UI tab variants remain platform-scope differences, as do Kotlin-only code panels and
  font rasterization.
