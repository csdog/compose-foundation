# Input r1 — current official page rebuild and state matrix

Date: 2026-11-05. Local page: `http://127.0.0.1:8774/input`.
Official page: <https://ui.shadcn.com/docs/components/base/input>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (18 fetched sources and hashes in
[sources.json](sources.json)).

## Findings

The current official page has 15 previews: demo (API Key), Basic, Field, Field Group,
Disabled, Invalid, File, Inline, Grid, Required, Badge, Input Group, Button Group, Form
(`h-[32rem]`), RTL. There is no API Reference section; the page headings are exactly those
recipes plus Installation/Usage. Every non-demoded preview uses the `h-72` (288dp) preview
box with 40dp padding (`Grid` uses 24dp, `Form` is 512dp high) and a `*:max-w-xs` wrapper
(320dp) — the form uses `max-w-sm` (384dp).

The local page still listed 26 ad-hoc parameter demos (Filled, Ghost, Password, Search,
Telephone, Read only, Max length, Autofocus, …) under an `Examples` heading, with no Field
Group/Inline/Grid/Badge/Input Group/Button Group/Form/RTL recipes and a stale API table.

## Changes

- Rebuilt `InputDoc.kt` to the official 15-preview order/titles/copy with
  `examplesHeading = null`; added the official explanatory paragraphs for Field, Field
  Group, Disabled, Invalid, File, Inline, Grid, Required, Badge, Input Group, Button Group,
  Form and RTL. Removed the old API Reference table (the official page has none) and the
  outdated parameter demos.
- Recipes reproduce the official content: API Key password field, plain Basic input,
  Username field, Name/Email + Reset/Submit group, disabled field, invalid field with
  destructive ring, file-input approximation, horizontal Search input + button, 2-column
  grid, Required Field with destructive asterisk, Webhook URL + secondary Beta badge,
  Website URL InputGroup (`https://` addon, info icon), search Input + ButtonGroup, full
  form with Name/Email/Phone/Country select/Address and the RTL API-key field with the
  shared language toolbar.
- RTL copy uses the official wording “To enable RTL support in shadcn/ui, see the RTL
  configuration guide.” (Lead request).
- Component/layout enablement: `FieldHorizontalLayout` now measures each non-content child
  once against the row width (CSS `*:w-auto`/shrink) and gives `FieldContent` the remainder;
  the Inline recipe passes the HTML-equivalent 244dp input width because Compose has no
  `w-full min-w-0` shrink basis. `FieldLabel` stays 14/19.25 medium, Input keeps its 32dp
  Nova box and 10dp radius; the input switches to 16/24 under a 768dp window, matching the
  official `text-base md:text-sm`.

## Verification

- **26 focused JVM tests passed** (XML in [tests](tests)): 8 `InputExamplesTest` (recipe
  order/copy, 320dp field width, disabled field/input, inline widths, form default country,
  destructive asterisk, Arabic RTL copy), 14 `FormNovaParityTest`, 4 `FieldScreenshotTest`.
- Wasm development distribution rebuilt; the four quadrants below were captured from the
  final shared build `421a6c71…` (see [meta.json](meta.json)).
- Four quadrants at 1440×1000 and 390×844 in light and dark:
  [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
  Official background pixels were re-checked for each quadrant after the shared-browser
  theme advisory (input-r1 dark was recaptured).
- Per-recipe official/local comparisons were reviewed for Basic, Field, Field Group,
  Disabled, Invalid, File, Inline, Grid, Required, Badge, Input Group, Button Group, Form and
  RTL; specific states in [browser](browser): typed text + focus ring, disabled, invalid,
  form select open with United States checked, after choosing Canada, RTL language menu and
  Hebrew switch, phone form.

## Remaining differences (page stays unchecked)

- **File**: Compose/browser has no native `type="file"` control; the preview approximates it
  with an outline “Choose File” button and “No file chosen” text. A real file picker needs a
  platform capability that does not exist here.
- Plain field labels have no `htmlFor`; clicking “Username” does not focus the input
  (checkbox/radio/switch labels are wired, and inputs are reachable with Tab).
- Inline/Button Group widths are explicit because Compose has no `flex-basis:100%` shrink
  model for an input; the visual widths (244/252dp) match, but the exported code carries a
  width modifier the official code does not need.
- The Input Group info icon is rendered from Lucide through the catalog preview; the
  exported code uses `Icon(Lucide.Info)` (the library itself has no icon-set dependency).
- Official page has no API Reference; this page therefore documents no Kotlin parameter
  table (component API is covered by the core tests and component docs).
- React Aria/Radix UI tabs, browser package-manager tabs, Kotlin code panels and font
  rasterization remain platform-scope differences; native file inputs and browser autofill
  UI cannot be reproduced.
