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

## Current Stage30 audit (2026-11-09)

The historical results above remain historical. Current evidence is indexed by
[Input audit](../../../.agent-runs/input/verify-30d-current/audit.json), with
[freshness](../../../.agent-runs/input/verify-30d-current/freshness.json): all 1,460
test/lint inputs, 1,466 build inputs and 46 distribution artifacts match. Archived XML
proves 41/41 focused tests; owning lint, production Wasm and browser matrix 68/68 pass.
The recovered [18 pinned source bodies and hashes](../../../.agent-runs/input/verify-30d-current/sources-check.json)
match the original manifest exactly. Four prepared current demo crops show distinct
light/dark rendering at desktop/phone; these do not prove every visual state.

The original residual list is reconciled as follows:

- **File capability resolved:** Web now creates an actual hidden HTML `input[type=file]`
  through `wasmJsMain/.../docs/FilePicker.kt`. Current browser evidence proves chooser
  invocation, selection and filename semantics. The visible field is Compose; injected
  JVM tests prove cancellation retention, unsupported-platform disabling and label/button
  focus. Forced browser click does not prove pointer actionability, keyboard access or
  OS-dialog cancellation; these remain coverage gaps, not accepted interaction defects.
- **Label focus resolved:** explicit `FocusRequester` wiring and named semantics are the
  Canvas equivalent of `htmlFor`; current Default/Grid/Form/RTL tests verify focus and
  Grid Tab traversal. Literal DOM label association remains a specific platform difference.
- **Explicit widths:** Inline 244dp and responsive Button Group arithmetic substitute for
  CSS shrink-basis. This is an accepted exported-code/API difference, supported by focused
  layout checks; it does not exempt unexpected visual overflow.
- **Info icon:** Lucide is catalog-only; core stays icon-set independent. This is an
  accepted dependency/API difference.
- **No API table:** pinned official Input MDX has no API Reference section. The local
  omission aligns with that source and is not a missing requirement.
- **Platform validation limits:** browser autofill/IME and external screen-reader behavior
  remain unverified; Canvas semantics and JVM editing/focus tests are the recorded local
  equivalents. No blanket layout/color/interaction exemption is granted.

Acceptance remains open: current evidence lacks the pinned Nova `transition-colors`
mid-frame check (I7), an Input live-shape switch check (I8), and browser proof of ordinary
file-button activation. The existing independent review covered only the recovery harness;
the current bounded audit review is recorded separately. Production/test repairs belong
to an implementation/fixer session. Preserve passed units whose input hashes still match.
