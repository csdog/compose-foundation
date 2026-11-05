# Radio Group r1 — official seven recipes and Nova field composition

Date: 2026-11-05. Local page: `http://127.0.0.1:8775/radio-group`.
Official page: <https://ui.shadcn.com/docs/components/base/radio-group>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (fetched files and hashes in [sources.json](sources.json)).

## Findings

The page previously listed a 15-item parameter matrix (Default/Disabled/Horizontal/Vertical/
Uncontrolled/Required/Item and default variants) rather than the official page. The current
official page has seven Base UI previews — demo (no heading), Description, Choice Card, Fieldset,
Disabled, Invalid, RTL — plus Usage, the two-part Composition tree and a link-only API Reference.
All previews use `styleName="base-nova"`, the catalog default. The registry `radio-group.tsx` is a
thin Base UI wrapper; the visual rules are the `cn-radio-group*` / `cn-field*` Nova CSS, where the
group is a `grid gap-2` and items are 16 px circles.

## Changes

- Rebuilt the page in official order/titles/copy: demo, Description, Choice Card, Fieldset,
  Disabled, Invalid, RTL. Added the official Usage (imports + option list snippet) and the exact
  `RadioGroup → RadioGroupItem ×2` composition tree.
- Recipes compose the local `Field` parts: horizontal `Field` rows with `FieldContent` for
  descriptions, `FieldLabel` click-to-select (the local equivalent of the official `htmlFor`
  association), `FieldSet` + `FieldLegend(Label)` for Fieldset/Invalid, and the `FieldLabel`
  choice-card wrapper for the clickable Plus/Pro/Enterprise cards.
- RTL recipe reproduces the official Arabic default plus English/Hebrew toolbar; rows mirror while
  keyboard arrow direction follows `LocalLayoutDirection`.
- Two Kotlin-only demos stay after the official recipes as `Kotlin extension:` (Horizontal
  orientation, Required) — the official page only shows the vertical group.
- API Reference keeps the project's Kotlin API sections (RadioGroup / RadioGroupItem / convenience
  row) and cites the Base UI Radio Group documentation.
- No component or theme changes were required; the existing `radiobutton` primitives already
  provide the Nova circle, arrow wrap/skip-disabled/Home/End behaviour and single Tab stop.

## Verification

- **Tests: 12/12 pass** (`RadioGroupExamplesTest`): recipe order/titles/description/Disabled+Invalid
  copy, Usage/Composition/API copy, default selection + label click, Description fields, Choice Card
  selection, Fieldset copy + selection, disabled item inert and skipped/wrapped by arrows, Home/End,
  invalid rows, RTL Arabic rows (radio right of label) → English LTR, horizontal extension one-row
  layout, required extension single selection. XML under [tests](tests/).
- **Build:** Wasm snapshot wasm
  `b222e1e1bac5a6d926fad48202243c14b66b01225fd873d73e325a795cf2c650` (slot e / 8775).
- **Four quadrants** at 1440×1000 / 390×844 light+dark after the final build, official theme
  re-asserted and pixel-checked (one first pass had a dark phone-light official shot and was
  recaptured): [desktop light](browser/sheet-desktop-light.png),
  [desktop dark](browser/sheet-desktop-dark.png), [phone light](browser/sheet-phone-light.png),
  [phone dark](browser/sheet-phone-dark.png).
- **States** ([browser/interactions](browser/interactions/)): Choice Card Plus → Enterprise click
  moves the selected card (`choice-card-plus/enterprise`); clicking the disabled row leaves the
  selection unchanged (`disabled-before/after-click` byte-identical); Invalid section renders all
  three invalid rows (`invalid`); RTL Arabic default, language menu, English LTR
  (`rtl-arabic`, `rtl-menu`, `rtl-english`).

## Remaining differences / open items

- API Reference uses the project's Kotlin tables instead of the official single Base UI link
  sentence (same convention as other components in this catalog); its h3 subsections add extra
  entries to the On-This-Page list.
- The official Disabled copy says "Use the `disabled` prop on `RadioGroup` to disable all items"
  while its own preview disables one item; the official sentence is kept verbatim and the preview
  matches the official one-item example.
- `FieldLabel`-based rows in Fieldset/Disabled/Invalid render the label at the shared Label weight;
  the official source adds `font-normal` for those rows and the shared Label API has no
  font-weight override. Click-to-select is a Compose convenience on the label, not a native HTML
  `htmlFor` form association.
- Complete assistive-technology matrix (role/label association, screen-reader row reading), dynamic
  reordering with focus, and RTL keyboard navigation in a real browser remain open (JVM tests cover
  arrow wrap/skip/Home/End and single group Tab entry by construction).
- Mobile capture is a 390 px mouse emulation, not a real touch device; native scroll/focus styling
  differences are shared-page items.
