# Questionnaire r2 — official fourteen previews and guide copy

Date: 2026-11-05. Local page: `http://127.0.0.1:8775/questionnaire`.
Official page: <https://ui.shadcn.com/docs/components/base/questionnaire>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (fetched files and hashes in [sources.json](sources.json)).

## Findings

The previous page mixed the official Default flow with nineteen arbitrary Kotlin demos. The current
official page has **14 previews**: demo (headingless, three-question flow) plus Multiple Selection,
Freeform Answer, Explicit Skip, Shortcuts, Custom Validation, Controlled, Resume, Conditional
Items, Navigation State, Custom Progress, Animated Items, Card and Dialog. It also carries Usage
(imports, items data, composition JSX, submit handler), the Composition tree, Server Rendering,
Accessibility (three paragraphs), Unstyled and API Reference (two paragraphs). All previews use
`styleName="base-nova"`.

## Changes

- Rebuilt the page in official order/title/copy: official Default recipe (existing generated
  source recipe) + the 13 official recipes above, each with the official explanatory sentence and
  content taken from the corresponding example TSX (question titles, choice labels, descriptions,
  progress wording, submit labels).
- Added the official guide sections: Server Rendering (with the headless-Questionnaire link),
  Accessibility (three paragraphs), Unstyled, and API Reference (two paragraphs, second one the
  Kotlin API summary).
- Custom Validation is a real host-validated flow: controlled active item, `invalid` +
  `QuestionnaireError(text=...)`, returning to the invalid item when the host rejects the answer
  (verified in browser: error text and invalid borders appear on item 1 after validating item 2).
- Resume uses `defaultItem` + `defaultChecked` saved answers and the local `QuestionnaireReset`
  action; Controlled drives `item`/`onItemChange` with the host checkpoint label; Card/Dialog use
  the local Card/CardAction and Dialog parts (progress now sits in `CardAction`, matching the
  official header layout).
- Old non-official demos stay after the official recipes only where they add Kotlin surface:
  `Kotlin extension: Disabled`, `Kotlin extension: Items collection`,
  `Kotlin extension: Choice parts`, `Kotlin extension: Reset`. The redundant old "Item status" and
  "Without actions" demos were removed (their behaviour is covered by Navigation State and the
  default actions).
- Preview min-heights follow the official `min-h-*` values (420/480/520/560/320).

## Verification

- **Tests: 22/22 pass** — `QuestionnairePageTest` 6 (official recipe order/titles/description,
  Usage/Composition/guide copy, controlled item tracking, validation returning to the invalid item,
  resume defaults + reset, dialog open/advance) and `QuestionnaireExamplesTest` 16 (existing
  Default-flow behaviour across 4 viewports: three questions, only visible actions, freeform/mixed
  answer replacement, validation + multi-select submit/toast, input shortcut + explicit skip). XML
  under [tests](tests/).
- **Build:** Wasm snapshot wasm
  `b222e1e1bac5a6d926fad48202243c14b66b01225fd873d73e325a795cf2c650` (slot e / 8775).
- **Four quadrants** at 1440×1000 / 390×844 light+dark after the final build, with the official
  theme re-asserted and pixel-checked: [desktop light](browser/sheet-desktop-light.png),
  [desktop dark](browser/sheet-desktop-dark.png), [phone light](browser/sheet-phone-light.png),
  [phone dark](browser/sheet-phone-dark.png).
- **States** ([browser/interactions](browser/interactions/)): Multiple Selection empty → two
  checked answers; Custom Validation first item → audience → public answer selected → validation
  error returns to item 1 with the message and invalid borders; Resume starts on the saved
  verification step with two checked answers → manual check added → Reset restores the saved
  answers; Dialog closed → open with Clarification header, choices and Next.

## Remaining differences / open items

- Custom Progress uses the local `text` override for the progress label; the official `render` prop
  lets the consumer replace the whole indicator (segmented bar). No custom-render layout API exists
  locally — the official wording and checkpoint text are kept.
- Shortcuts uses the catalog `Select` instead of the official NativeSelect; behaviour (letters /
  numbers / none) matches.
- Resume has no `onReset` callback; the local `QuestionnaireReset` action performs the same reset
  (a host cannot observe the reset event).
- Card and Dialog are composed directly with the local Card/Dialog parts instead of the official
  `render`-prop slot composition; visual structure and the question/description semantics are kept.
- The Composition ownership paragraphs (page/card/dialog owns close and cancellation, etc.) cannot
  render after the shared Composition code block and are therefore not on the page; the same
  shared-page limitation as Code Block. Server Rendering is kept as its own section.
- The official `align="end"` previews pin content near the bottom; the shared preview box centres
  vertically (its `previewVerticalAlignment` only applies to fixed-height previews), so tall
  questionnaire previews are centred rather than bottom-aligned.
- Toast feedback (sonner) was not exercised in the browser matrix; the local Toaster path is
  covered by existing Default-flow tests. Full keyboard/AT matrix (shortcuts in a real browser,
  conditional disabled transitions, animation timing) and real touch devices remain open.
