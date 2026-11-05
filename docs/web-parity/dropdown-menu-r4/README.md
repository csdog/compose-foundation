# Dropdown Menu — Web parity r4 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/dropdown-menu](https://ui.shadcn.com/docs/components/base/dropdown-menu),
Base UI + `base-nova`, source revision `8a3536678a528cc012b9e62484290e79d0be410f`.
Sources and SHA-256: `sources.json`.

## Official page reading

- First preview is the unnamed `dropdown-menu-demo` (no section heading): My Account / Profile,
  Billing, Settings with shortcuts, a separator, Team / Invite users submenu / New Team, GitHub,
  Support, disabled API, Log out.
- Sections, in order: Installation, Usage, Composition, Basic, Submenu, Shortcuts, Icons,
  Checkboxes, Checkboxes Icons, Radio Group, Radio Icons, Destructive, Avatar, Complex, RTL,
  API Reference (15 H2s; the demo preview sits before Installation).
- `DropdownMenuContent` defaults: `w-(--anchor-width)`, `min-w-32` (128px), `max-h-(--available-height)`,
  `rounded-lg p-1 shadow-md`, `ring-1 ring-foreground/10` (a `box-shadow`, not a `border`).
  Recipes override width with `w-40` (160, demo/checkboxes), `w-48` (192, checkboxes icons),
  `min-w-56` (224, radio icons), `w-44` (176, complex), `w-36` (144, RTL).
- `DropdownMenuSubContent` is `w-auto min-w-[96px]` with `shadow-lg` and the same 1px ring.
- Pointer open does **not** highlight a row; ArrowDown/ArrowUp/Enter/Space open with the first/last
  row highlighted.
- Rows are 28px tall (`text-sm`, `px-1.5 py-1`), labels `px-1.5 py-1 text-xs`.

## Findings against the local page/component

1. All 13 official recipes were present in official order with matching content and explicit widths
   (verified against the sources above; the page-side widths needed no change).
2. Eight Kotlin-only parameter demos were still titled like official recipes (`Inset`, `Disabled`,
   `Non-modal`, …). They are now prefixed `Kotlin extension: …` and stay after the official set.
3. Core panel chrome used a fixed `width(192.dp)` and an inner `border`
   (`foreground/10`). CSS's `ring-1` is a box-shadow: the inner Compose `border` consumes 1dp on
   each side, so every row was 2dp narrower than official and submenu defaults were 192dp instead
   of the official 96dp minimum.
4. `DropdownMenuContent` unconditionally focused the first row on every open, including pointer
   opens. Official only highlights a row for keyboard opens.
5. Collision handling (flip + clamp, available-height cap) already existed in
   `PopupPositionProvider`/`dropdownViewport` and matched the official behaviour in the checked
   states; no change was needed.

## Changes

- `shadcn/core/.../components/dropdownmenu/DropdownMenu.kt`
  - New open intent `DropdownMenuInitialFocus { None, First, Last }`: the trigger records
    ArrowDown/ArrowUp/Enter/Space keyboard opens; a pointer click leaves it `None`.
  - Pointer opens now focus the panel itself (so ArrowDown still reaches the menu) and leave every
    row unhighlighted; keyboard opens focus the first (`ArrowDown`/`Enter`/`Space`) or last
    (`ArrowUp`) enabled row.
  - Nova root content width defaults to `max(anchor width, 128dp)` instead of a fixed 192dp.
  - Nova submenu content uses the official `min-w-[96px]` default (callers still pass explicit
    widths for the recipes that need them).
  - Nova panel draws the official outer `ring-1 foreground/10` via `dropShadow(ringShadow(...))`
    instead of an inner border; legacy (Vega) rendering is unchanged.
- `shadcn/core/.../styles/MenuStyles.kt` — additive, default-preserving parameters
  `rememberMenuPanelStyle(novaWidth, novaMinWidth, novaRing)` (approved by Lead; ContextMenu/Menubar
  pass no arguments and keep the old behaviour).
- `app/shared/.../DropdownMenuDoc.kt` — Kotlin-only recipes renamed to `Kotlin extension: …`.
- Tests: `DropdownMenuExamplesTest` grew from 12 to 18 tests (pointer-open no highlight,
  ArrowDown/ArrowUp/Enter keyboard opens focus first/last, default content width follows the trigger
  with the 128dp minimum, Kotlin-extension titles).
- Goldens: re-recorded `dropdown_menu_nova_{light,dark}` because the pointer-open state intentionally
  no longer highlights the first row. The Vega `dropdown_menu_states_*` goldens did not change
  (legacy rows never had a focused background).

## Verification

- `:app:shared:jvmTest --tests *DropdownMenuExamplesTest` — 18/18 pass
  (`tests/TEST-com.toutiaoyan.compose.ui.catalog.DropdownMenuExamplesTest.xml`).
- `:shadcn:core:recordRoborazziJvm --tests *DropdownMenuScreenshotTest*` then
  `:shadcn:core:verifyRoborazziJvm --tests *DropdownMenuScreenshotTest*` — pass; only the two Nova
  goldens changed.
- Wasm Web build `wasm=b3b5e53c3bf756e7afc8256cfd83b076bdc22a01c820a1ee85d864411072647a`; four
  quadrants captured after the final build (`local-*.png`, `sheet-*.png`, `browser/meta.json`).
- Browser states exercised (1440×1000 light unless noted): pointer open (no focused row), ArrowDown
  after pointer open (Profile highlighted), Escape close, 390×844 pointer open, date-picker-style
  scroll not required. See `browser/desktop-light-pointer-open.png`,
  `browser/desktop-light-arrowdown-first-row.png`, `browser/desktop-light-after-escape.png`,
  `browser/phone-light-pointer-open.png`.
- Compared against the official open menu measured in the same browser (width 160/128/192/224 etc.,
  rows 28px, ring+shadow, pointer open with no highlighted row).

## Remaining differences (explained)

- Compose has no `--anchor-width` CSS length: a caller that passes an explicit width **narrower than a
  wide trigger** still gets the panel grown to the anchor width. All documented recipes pass widths
  equal to or wider than their trigger, so their rendering matches official.
- Submenu panels default to `min-w-[96px]` but do not auto-grow to intrinsic content width for
  callers that pass no width; every official recipe sets its own width.
- Accessibility tree: the Compose Web canvas exposes rows as `Role.Button` / `Role.Checkbox` /
  `Role.RadioButton` semantics (aria-checked/selected state is present) rather than the DOM
  `role=menu` / `role=menuitem` tree the React version renders; Canvas semantics cannot be shaped
  into ARIA menus. Pointer-open focus owner is the menu panel (official keeps DOM focus on the
  trigger) — the visual state (no highlighted row) is the same.
- Platform-scope: no ad/Vercel card, no React Aria/Radix tabs, Kotlin API/recipes instead of TSX.
