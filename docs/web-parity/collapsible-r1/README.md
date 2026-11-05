# Collapsible r1 — current official page and browser recheck

Date: 2026-11-05. Local page: `http://127.0.0.1:8765/collapsible`.
Official page: <https://ui.shadcn.com/docs/components/base/collapsible>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (sources and hashes in [sources.json](sources.json)).

## Findings

The 2026-10-31 Web-behavior round left the page with a parameter-matrix of Kotlin API demos
(Always-visible siblings, Default open, Disabled, Keep mounted, Force mount, Uncontrolled,
Trigger, Content). The current official page has five previews — Default (top aligned),
Basic (top aligned), Settings Panel, File Tree (`h-[36rem]`), RTL (top aligned, `dir=rtl`) —
plus Usage, Composition, a Controlled State guide and the Base UI API Reference paragraph.
`collapsible.tsx` is a thin Base UI wrapper with no component-level styling.

## Changes

- Rebuilt the page to the official order: Default → Basic → Settings Panel → File Tree → RTL.
  Default/Basic reproduce the 350dp order card, muted open state, Learn More button; Settings
  Panel uses Card + Field + Input + icon trigger; File Tree is a 256dp Card with Explorer/Outline
  Tabs and recursive nested Collapsibles; RTL reuses the official language toolbar and order copy
  in Arabic/English/Hebrew.
- `examplesHeading = null` (official has no `Examples` heading) and the four remaining Kotlin-only
  demos are kept after the official recipes as `Kotlin extension: …` (Disabled, Default open,
  Keep mounted, Force mount).
- Added `Controlled State` paragraph + code and the Base UI `API Reference` paragraph; Usage and
  Composition now match the official source.
- Preview geometry follows the official `h-72` (288dp) preview bodies with 40dp padding; Default,
  Basic and RTL are top aligned and File Tree is fixed at 576dp, matching `h-[36rem]`.
- Added `CollapsibleExamplesTest` (8 tests) locking recipe order/titles, controlled-state copy,
  Copy Page heading order, Default/Basic/Settings/File Tree behavior, and the Arabic RTL copy.

## Verification

- 8/8 focused JVM tests passed (XML archived under [tests](tests/)).
- Wasm development distribution rebuilt for this page; served app Wasm `ee99559a…` (see
  [browser/meta.json](browser/meta.json)).
- Browser four quadrants at 1440×1000 and 390×844, light and dark: [sheet-desktop-light](sheet-desktop-light.png),
  [sheet-desktop-dark](sheet-desktop-dark.png), [sheet-phone-light](sheet-phone-light.png),
  [sheet-phone-dark](sheet-phone-dark.png). Section-level comparisons for Basic / Settings Panel /
  File Tree / RTL are under [sections](sections/).
- Interaction evidence: Default opens/collapses order details, Basic reveals the panel, Settings
  expands the hidden radius fields, File Tree expands `components` → `ui` → file rows, and the RTL
  toolbar switches Arabic → English (LTR) in
  [browser/interactions](browser/interactions/).

## Remaining differences

- The official preview tabs include React Aria and Radix UI variants; this catalog only implements
  the Base UI variant (documented project-wide platform scope).
- Kotlin-only API demos are intentionally kept as `Kotlin extension:` recipes.
- Settings Panel inputs have no visible labels in either implementation; the official sr-only
  `FieldLabel`s have no local equivalent, so the screen-reader label remains a native-a11y gap.
- Geist/Arabic font rasterization and single-pixel anti-aliasing differ from the browser; no
  layout, color or content difference remains at the checked sizes.
