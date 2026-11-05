# Date Picker — Web parity r10 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/date-picker](https://ui.shadcn.com/docs/components/base/date-picker),
Base UI + `base-nova`, revision `8a3536678a528cc012b9e62484290e79d0be410f`
(`sources.json`).

## Official page reading

- First preview `date-picker-demo`: outline button (w-212) with "Pick a date" + chevron, popover
  with a single-month Calendar.
- Sections: Installation, Usage, Composition, Basic, Range Picker, Date of Birth, Input,
  Time Picker, Natural Language Picker, RTL (8 examples; the description is
  "A date picker component with range and presets.").
- Basic: Field label "Date" + picker. Range: two-month range with a Field label. Date of Birth:
  dropdown month/year caption, closes on select. Input: InputGroup with a formatted date and a
  calendar button. Time: date popover + `input type="time"`. Natural Language: `chrono-node` parser
  feeding the calendar. RTL: Arabic/Hebrew DayPicker locale in RTL.

## Findings

The previous tick was inaccurate for the page: the local page rendered a 38-example
Calendar-parameter matrix (Localized, Multiple, Disabled, Two months, Week starts Monday,
Hide outside days, Paged navigation, Min/max, From/To month, Grid, Caption, Weekdays, …) and did not
have the official Basic / Range Picker / Date of Birth / Input / Time Picker / Natural Language /
RTL recipes. The behaviour items recorded earlier (timezone, locale/week start, range, multi-month,
month bounds, keyboard focus, popover initial focus) are implemented in `Calendar` and covered by
the existing Calendar suites.

## Changes

- `DatePickerDoc.kt` rebuilt to the official set (demo, Basic, Range Picker, Date of Birth, Input,
  Time Picker, Natural Language Picker, RTL) with official titles/descriptions, the official
  Composition tree, and 4 curated Kotlin extras (`Localized` Chinese calendar, `Multiple dates`,
  `Disabled weekends`, `Min and max date`). The 38-example parameter matrix was dropped
  (Lead-approved); a compact API table remains.
- Previews reuse `DatePickerParityExample` for the demo and a `PopoverScope`-aware trigger/calendar
  helper for the other recipes, so each trigger really toggles its popover.
- New `DatePickerExamplesTest`: recipe order/copy, demo picks a date (selected state), range starts
  from the official Jan 20 → Feb 9 default, Date of Birth opens the dropdown caption, Input keeps
  the official formatted value, Time shows the date/time fields, natural-language parsing, RTL
  Arabic placeholder/label.
- One page-level bug found and fixed while testing: the new shared trigger initially passed an empty
  `onClick`; it now calls `PopoverScope.toggle`, so every recipe opens its calendar.

## Verification

- `:app:shared:jvmTest --tests *DatePickerExamplesTest` — 8/8 pass (`tests/`).
- Wasm Web build `wasm=b3b5e53c3bf756e7afc8256cfd83b076bdc22a01c820a1ee85d864411072647a`;
  four quadrants captured after the final build (`local-*.png`, `sheet-*.png`,
  `browser/meta.json`), and the demo popover was opened in the browser:
  `browser/desktop-light-demo-open.png` (November 2026 calendar, today highlighted) and
  `browser/desktop-light-demo-after-escape.png`.

## Remaining differences (explained)

- Time Picker: Compose has no native `<input type="time">`; the Time field is a plain 24h text field
  with the official label/layout. Documented in the recipe code comment.
- Natural Language Picker: `chrono-node` is JavaScript-only; the Kotlin recipe ships a small parser
  for "In N days", "tomorrow", "yesterday" and "next week". Documented in the code comment.
- RTL: the catalog supplies its own Arabic `CalendarLocale` (months/weekdays/placeholder) instead of
  `date-fns`/DayPicker locale bundles; the calendar is wrapped in `LayoutDirection.Rtl`.
- The local page adds an API-reference table (the official page has none) and the Kotlin extensions
  after the official recipes.
- Platform scope: no React Aria/Radix tabs, no ad/Vercel card.
