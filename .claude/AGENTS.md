# AGENTS.md — compose-foundation

This project uses [kmm-agent-skills](https://github.com/ronjunevaldoz/kmm-agent-skills).
The existing KMM skills remain in `.claude/skills/`. Codex-native project token workflows
are installed in `.agents/skills/`, which Codex scans from the current directory to the
repository root.

## Project overview

A shadcn/ui-inspired Compose Multiplatform component library built on the Compose
Styles API (`@ExperimentalFoundationStyleApi`) -- token-based theming, sealed variant
systems, zero Material dependency. Targets Android, iOS (arm64 + simulator), Desktop
(JVM), and Web (JS + WasmJS).

**Alignment target (since 2026-08-21): Base UI** — primary source of truth is
`apps/v4/registry/bases/base/ui/*.tsx` (docs: `ui.shadcn.com/docs/components/base/*`),
with default style combo **base-nova**. Legacy `new-york-v4` (Radix/Vaul) is historical
diff only; see `vendor/README.md` and `docs/shadcn-parity.md`.

Group ID: `com.toutiaoyan.compose`   Artifacts: `ui`, `tailwind`   Published to: Maven Central (
dry-run wired, not yet released)

## Dependencies

- **`:tailwind-core`** (`com.toutiaoyan.compose:tailwind`, packages
  `com.toutiaoyan.compose.tailwind.{core,modifiers,style}`) — vendored Tailwind-style
  utilities (from upstream `tailwind-compose` 0.1.4: core / effects / style / layout).
  Sibling of `:shadcn:core` under `tailwind/core`. Utility-class/atomic styling (`TwColors`,
  `Oklch.toColor()`, `Modifier.shadowMd`, `ringShadow`, Flex/Grid/Spacing, …) sits
  *underneath*/*alongside* compose-foundation's component-level Style API variants — same
  relationship as Tailwind CSS + shadcn/ui on the web.
    - Prefer named `TwColors` shades when byte-identical to the source oklch; otherwise
      `Oklch(l, c, h).toColor()` for new tokens — see `docs/shadcn-parity.md` §1 / §5.
    - Only reach for a vendored utility where compose-foundation's own `Style`/`StyleScope`
      DSL has **no equivalent property at all** — don't duplicate something the Style
      block already handles. Confirmed example: `StyleScope` has no shadow/elevation, so
      `Card`'s `CardVariant.Elevated` applies `Modifier.shadowMd(shape)` on the outer
      modifier chain *before* `.styleable(...)` — see `Card.kt`.
    - `:shadcn:core` depends via `api(projects.tailwindCore)` so consumers can import
      `com.toutiaoyan.compose.tailwind.core.TwColors` without an extra dependency.
- **icons-lucide-cmp** (`com.composables:icons-lucide-cmp`, `2.2.1`) — Lucide icons as
  Compose `ImageVector`
  via [composablehorizons/compose-icons](https://github.com/composablehorizons/compose-icons).
  Kotlin API is `Lucide.<Name>` under `com.composables.icons.lucide`. **`:app:shared`-only, never
  `:shadcn:core`** — the catalog app's examples (e.g. Date Picker's trigger icon) may use
  it, but every `:shadcn:core` component still uses plain text glyphs for icons (`"☰"`,
  `"✕"`, `"↓"`, ...), matching this library's zero-icon-set-dependency stance. Render an
  `ImageVector` with `androidx.compose.foundation.Image(imageVector = ...,
  colorFilter = ColorFilter.tint(...))` — not `androidx.compose.material.Icon`, which
  would pull in a Material dependency this project deliberately avoids.

## Skill routing

| Topic                             | Skill                                                                                     |
|-----------------------------------|-------------------------------------------------------------------------------------------|
| New design system component       | `kotlin-multiplatform-design-system` / `kotlin-multiplatform-design-system-extended`      |
| Publishing to Maven Central       | `kotlin-multiplatform-library-publishing`                                                 |
| iOS / SPM distribution            | `kotlin-multiplatform-xcframework-spm`                                                    |
| API surface management            | `kotlin-multiplatform-library-publishing` (apiCheck / apiDump — not yet wired, see Notes) |
| Platform-specific implementations | `kotlin-multiplatform-expect-actual`                                                      |
| Catalog app navigation            | `kotlin-multiplatform-navigation`                                                         |
| Unit / integration tests          | `kotlin-multiplatform-unit-testing` (JVM / screenshot tests are wired; see `docs/visual-testing.md`)                            |
| Token usage analysis / optimization | `token-optimization-analysis`                                                            |
| Token-efficient long parity work  | `token-efficient-compose-workflow`                                                        |
| Code quality (detekt, ktlint)     | `kotlin-multiplatform-code-quality`                                                       |
| CI automation                     | `kotlin-multiplatform-ci-github-actions`                                                  |
| Architecture audit                | `kotlin-multiplatform-audit`                                                              |
| Harvest consumer lessons          | `kotlin-multiplatform-audit` (`--harvest` mode via `/kmm-harvest-lessons`)                |

## Token-efficient execution

For any request to analyze token usage, investigate context growth, compare workflows, or
reduce agent cost, use `token-optimization-analysis` first. Its audit covers actual
Codex/DSH usage, repo-visible fixed context, duplicate reads, tool-output volume, and cheap
subagent routing. Do not optimize from cache-hit percentage alone.

For long shadcn parity batches, follow `token-efficient-compose-workflow`. Keep one
component per execution session, at most two concurrent workers, and carry state through
`.agent-runs/<component>/progress.md` instead of the conversation transcript. Route verbose
commands through `scripts/agent_run.sh`; only the relevant tail enters model context while
the full log remains on disk. Use `scripts/token_efficiency.py report` to measure DSH usage
and context composition before changing the workflow.

For unattended queues, use `scripts/run_unfinished_components.py --execute --background`.
The Python driver is the sole scheduler; do not add a nested review coordinator or
model polling loop. Stage workers do not spawn agents. Report only terminal results,
blockers, evidence paths, and decisions requiring action. Read status on demand;
background execution does not itself deliver chat notifications. For token analysis,
include descendants with `scripts/agent_tree_usage.py --thread <root-thread-id>`.

## Module graph

| Module                                                | Purpose                                                                                                                                                                               |
|-------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `:shadcn:core`                                        | Published artifact (`com.toutiaoyan.compose:ui`) -- tokens, `Theme`, every component (including the AI Elements family: Message/Bubble/Attachment/Marker/MessageScroller), styles |
| `:tailwind-core`                                      | Published artifact (`com.toutiaoyan.compose:tailwind`, dir `tailwind/core`) -- `TwColors`/`Oklch`, Modifier + Style utilities                                                         |
| `:core`                                               | Small shared utility module (currently minimal, stock demo code)                                                                                                                      |
| `:app:shared`                                         | Catalog/docs app shared code -- navigation, sidebar, per-component doc pages (not published)                                                                                          |
| `:app:androidApp` / `:app:desktopApp` / `:app:webApp` | Catalog app platform entry points (not published)                                                                                                                                     |
| `:app:iosApp`                                         | Xcode project for the catalog app on iOS (not published)                                                                                                                              |

## Published artifacts

| Artifact                          | Module           |
|-----------------------------------|------------------|
| `com.toutiaoyan.compose:ui`       | `:shadcn:core`   |
| `com.toutiaoyan.compose:tailwind` | `:tailwind-core` |

## API surface rules

- No `binary-compatibility-validator` wired yet -- consider adding it via
  `kotlin-multiplatform-library-publishing` before the first real Maven Central release,
  so accidental public API breaks are caught in CI.
- Until then, treat any signature change to a public `Shadcn*` component or `styles/*Variant`
  sealed interface as a breaking change requiring a version bump.

## Component styling rules

0. **Every `Dp` token in this library uses the same numeral as real shadcn's CSS `px`
   value** (`ring-[3px]` -> `3.dp`, `size-4`/16px -> `16.dp`, etc.) -- a deliberate,
   project-wide convention (see every "Ndp = real Npx ✅" row in `docs/shadcn-parity.md`),
   **not** a physically-accurate unit conversion. CSS `px` (~1/96in) and Android `dp`
   (~1/160in) are different physical units; a literal conversion would multiply every
   value by ~1.667. This project doesn't do that, on purpose: it preserves *relative
   proportion* between controls, spacing, and text within the app (all ported the same
   "same numeral" way), which is what visual parity with real shadcn actually requires --
   not inch-for-inch physical matching against a web page rendered at an unrelated
   viewing distance/zoom level. Standard practice for porting a web design system to a
   native toolkit (Material's own guidance does the same). Don't "fix" an individual
   token to a physically-precise dp value -- that breaks its proportion against every
   sibling token that still uses the numeral convention. If describing this in a comment,
   say "same numeral as real shadcn's Npx," not "matches Npx" -- the latter reads as a
   physical-unit claim that isn't true.
1. **Variants are flat, stateless sealed interfaces** (`data object` per variant, e.g.
   `ButtonVariant`, `ChipVariant`) -- no hardcoded theme values baked into the object.
2. **Variant -> `Style` mapping is a `@Composable fun <Variant>.rememberStyle(): Style`**
   (see `components/button/ButtonStyles.kt` / `components/chip/ChipStyles.kt`).
   Prefer `utils/RememberTheme.kt`'s `rememberTheme(receiver)`: its keys include
   `Theme.LocalTheme`, `twRadius`, `twSpacing`, `unitSpacing`, and caller keys.
   An explicit `remember` must key every theme/token input its factory reads.
   `ChipVariant.rememberStyle()` already uses `rememberTheme`; the old missing-key
   defect is fixed and is not a pending task.
3. Inside a `Style { }` block, use `StyleScope` theme extensions backed by
   `currentValue`, or values from a fully keyed `rememberTheme` factory. Do not call
   the `@Composable currentTheme` getter inside the Style lambda, or capture theme
   values in an unkeyed `remember`. The former is a compiler error; the latter can
   freeze the first theme. The historical InputGroup/Input theme bug is fixed;
   retain live theme-switch coverage because static light/dark snapshots cannot
   detect a value that freezes after the first render.
4. **Nested text needs an explicit current content-color path.** Do not assume
   Style `contentColor()` alone will update nested `Text` / `BasicTextField` on
   a live theme switch. `Chip` already provides
   `LocalContentColor provides variant.contentColor(currentTheme.colors)` around
   its label; this is the current fix, not a missing pass-through. Inputs pass
   their resolved text/cursor colors explicitly. Preserve these paths when refactoring.
5. **The focus ring is configured via the Style API's `dropShadow()`, called directly
   inside each component's `focused { }` block** (`styles/FocusRing.kt`'s
   `ThemeData.focusRingShadow()` builds the `Shadow` value: `radius = 0.dp`,
   `spread = ring.width + ring.offset`, color from `colors.ring` at
   `ring.opacity`) -- **not** a separate `Modifier`. A prior version used a hand-rolled
   `Modifier.drawWithContent` + manual per-corner `RoundRect` reimplementation (~90
   lines), justified by an unverified claim that `dropShadow` "blurs at `radius = 0.dp`."
   That claim was checked directly (a screenshot test of `dropShadow(radius = 0.dp,
   spread = 3.dp)`) and was false: perfectly crisp, only ordinary single-pixel edge
   anti-aliasing, zero effect on measured layout size -- exactly matching real CSS
   `box-shadow`. Removed entirely (2026-07-09); see `docs/shadcn-parity.md` §6 for the
   full writeup.
   `dropShadow` always follows the **final resolved** `shape()` -- including a later
   `style`-parameter override from `ButtonGroup`/`ToggleGroup` (their own
   per-position corner-stripped shape, passed as the `style` param to `Button`/
   `Toggle` and merged in via `.styleable(...)`'s override-not-additive cascade) --
   not just whatever `shape()` the *same* `Style{}` block itself declares. Verified via
   `ButtonGroupScreenshotTest.with_label_focused`: the ring correctly appears only at the
   outer rounded ends, never at the internal flush seam between grouped items.
   **Transparent-surface correction (2026-10-30):** native Style drop shadows fill
   the shape underneath its background; with `bg-transparent` / `bg-input/30`, that
   fill leaks through the interior (confirmed by focused Input/invalid-field pixel
   comparisons). The affected controls use internal `cssStyleable`: Style still
   resolves real interaction state and the final caller-overridden shape, while
   Compose's `DropShadowPainter` renders the same shadow clipped with
   `ClipOp.Difference` against `Shape.createOutline`. This is an outer-shadow
   knockout, not a hand-drawn per-corner ring or a border-width/layout change.
   Preserve transparent-interior and generic-shape coverage in CssStyleableTest.
   `LocalGroupCorners` has been removed from the source; no provider/reader cleanup remains.
   A component whose "active"/"focused" state is a *computed* boolean rather than a real
   per-node focus event (`InputOTP`'s `OtpSlot`, `InputGroup`'s
   `hasFocusWithin`, `StylePresetMatrixTest`'s ring swatch) can't use the `focused { }`
   state predicate at all (there's no real `InteractionSource` driving it) -- call
   `dropShadow(theme.focusRingShadow())` inside a plain `if (isActive) { ... }` in the
   `Style { }` body instead.
6. Context-aware Style blocks may default *derivable, non-per-instance* values (color,
   shape) from theme/`CompositionLocal` state. **Per-instance interaction state
   (`isFocused`, `checked`, `pressed`) must stay driven by a real `InteractionSource`
   passed to `rememberUpdatedStyleState`/`focused { }`** -- there is no single global
   "the currently focused node."
7. Model compound-component spacing/density (icon+label pairs, group item corners,
   leading/trailing addons) as **explicit per-position parameters passed down the
   composition** (see `ButtonGroupCorners` / `ToggleCorners`), not a `CompositionLocal`.
   Real shadcn's `data-slot="..."` is inert HTML markup that exists only for CSS
   attribute selectors (`[data-slot=button-group]:gap-2`) -- it carries no spacing logic
   of its own, and Compose has no CSS-selector equivalent to translate it into. A
   `LocalDataSlots`-style CompositionLocal is a plausible-sounding but non-
   equivalent reimplementation; only introduce one once a *second* real component needs
   the exact same slot values -- don't add it speculatively.
8. `ShadcnStylePreset` bundling `shapes`/`spacing`/`typography`/`ring`/`animations`/
   `icons` per entry is correct. **`ShadcnBaseColor` is a separate axis from style
   presets**, matching real shadcn where `tailwind.baseColor` and the style/registry
   choice are independent `components.json` fields. Real shadcn's `tailwind.baseColor`
   only accepts **`neutral`, `gray`, `zinc`, `stone`, `slate`** -- five values, nothing
   else. `Mauve`/`Olive`/`Mist`/`Taupe` (already in `ShadcnBaseColor.kt`) are **not**
   part of the official spec -- they're this library's own deliberate extension beyond
   it; document them that way rather than as "official," and note `Gray`/`Slate` (both
   real, currently missing here) would be needed for actual spec parity.
9. Color conversion already has one documented, verified methodology
   (`docs/shadcn-parity.md` §5): oklch -> linear sRGB -> gamma-encoded hex, **no
   perceptual gamut mapping applied**. Don't introduce a second "wide-gamut
   approximation" path for the same tokens -- one checked-against-real-source conversion
   method per color, not two competing ones.
   **Vendored `Oklch(lightness, chroma, hue).toColor()`**
   (`com.toutiaoyan.compose.tailwind.core.Oklch`) implements this exact same
   conversion — use it when a value has **no** named `TwColors` shade that is
   byte-identical. Prefer **`TwColors.*`** when it is (e.g. `Colors.kt`'s
   `card`/`onCard`/`popover`/`onPopover`/`sidebar`/`onSidebar` are now
   `TwColors.white` / `zinc950` / `zinc50` / `zinc900`, same oklch triples as zinc.json).
   **Migrated (2026-07-08):** the rest of `Colors.kt`'s default palette and all 7
   `ShadcnBaseColor` families now reference **named `TwColors` shades**
   (`com.toutiaoyan.compose.tailwind.core.TwColors`, e.g. `TwColors.zinc900`) instead of
   hex literals, verified field-by-field via a scratch JVM test comparing every value
   against the full 26-hue/11-shade `TwColors` table before switching (exact match for
   ~90% of fields, 1-9-unit rounding snaps for a handful explicitly documented as
   "~zinc-400"-style approximations already) -- confirmed zero Roborazzi diffs after.
   **Follow-up (2026-08-03):** the remaining container-role `Oklch(...).toColor()`
   literals were also replaced with the matching `TwColors` shades (no ARGB change).
   A few fields (`success`/`warning`/`destructiveHover` in both files) are **intentionally
   still hex literals**: the same scratch test found they're hand-tuned, not exact/near
   matches to any named shade -- snapping them would silently shift the actual color, not
   just rename it. **Accent correction (2026-10-30):** `Accent.kt` now follows the current official
   `apps/v4/registry/themes.ts` rather than the old hand-tuned values. Its colors
   exactly match named `TwColors` shades. Preserve secondary/chart/sidebar overrides,
   Lime/Yellow foreground exceptions, Rose dark sidebar, and the base focus ring.
   `AccentSourceTest` checks all declared and omitted roles against a frozen source fixture.
10. **`Colors` has per-container-role tokens** (`card`/`onCard`, `popover`/
    `onPopover`, `sidebar`/`onSidebar`, `sidebarPrimary`/`onSidebarPrimary`,
    `sidebarAccent`/`onSidebarAccent`, `sidebarBorder`, `sidebarRing`), matching real
    shadcn's `--card`/`--popover`/`--sidebar*` CSS variable families (verified against
    `ui.shadcn.com/r/colors/zinc.json`) -- these are a **separate namespace** from the
    generic `surface`/`background`/`muted` tokens, not aliases of them, even though the
    default light palette's hex values happen to coincide (real shadcn's own light theme
    does too; only dark mode raises card/popover/sidebar to a distinct panel tone).
    **Use `card`/`popover` correctly, don't default back to `surface`:** `Card` →
    `card`/`onCard`; floating anchored panels (`Popover`, `DropdownMenu`,
    `ContextMenu`, `Command`, `Combobox`, `HoverCard`,
    `NavigationMenu`'s panel) → `popover`/`onPopover`; `Sidebar` → `sidebar`/
    `onSidebar` for the rail, `sidebarAccent`/`onSidebarAccent` for the active menu item,
    `sidebarBorder` for its edge divider. **`AlertDialog`/`Dialog`/`Sheet`/`Drawer` use `popover`/`popoverForeground`
    for the pinned Nova and Rhea styles.** At shadcn revision
    `8a3536678a528cc012b9e62484290e79d0be410f`, `style-nova.css` and `style-rhea.css`
    define `bg-popover text-popover-foreground` on `.cn-alert-dialog-content`,
    `.cn-dialog-content`, `.cn-sheet-content`, and `.cn-drawer-popup`. Read the matching
    style CSS as well as the wrapper; the old plain-background guidance came from
    wrapper-only/historical evidence. Durable HTTP-200 bodies and SHA256 records are
    in `.agent-runs/review-sheet/evidence/source-manifest.json` (Nova SHA256
    `fd9803ec398290c306b377a46a769cad0c7ff024bf6a626993c113cacc20f16e`, Rhea SHA256
    `bac4eff75a0e21d9dd90ce40634937f54730471d8f7fa50ca67b317892cf908f`).
    This correction does not redefine historical styles.
    `Tooltip` stays on `onSurface`/`background` (inverted) -- confirmed against
    real tooltip.tsx's `bg-foreground text-background`, a genuinely different pattern
    from the popover family, not a card/popover role at all.
    For `ShadcnBaseColor`'s 7 base families: light-mode card/popover/sidebar are set
    `== background` (matches real shadcn universally); dark-mode values are derived via
    `blend()` (a 50/50 RGB midpoint of `background`/`secondary`) rather than
    hand-verified per-family oklch, since that reproduces Zinc's real verified value
    (`#18181B`) to within one rounding unit -- see `blend()`'s doc comment in
    `ShadcnBaseColor.kt`. `sidebarPrimary`/`onSidebarPrimary` are aliased to
    `primary`/`onPrimary` rather than importing real shadcn's own dark-mode zinc.json
    anomaly (`sidebar-primary` is a hardcoded blue in dark mode only, unrelated to this
    project's `ShadcnStylePreset` accent system, and an artifact of their theme
    generator, not a deliberate two-tier design -- their own light palette keeps
    `sidebar-primary == primary`).

## Component creation checklist

Every new component's doc comment and screenshot-test coverage must account for these
before considering the component done -- most of the bugs found in review passes so far
(ring corner-radius mismatch, Collapsible fade-through overlap, ScrollArea thumb
mispositioning, Tooltip/HoverCard focus-steal flicker, missing Carousel width) were
caused by one of these being silently skipped, not by a typo:

1. **List every visual state** the component can be in (default, hover, focused,
   pressed, selected, disabled, error, loading, empty) and confirm each one actually has
   a screenshot test -- not just the ones that happen to be easy to trigger without a
   gesture. A state with no golden is a state nobody is actually checking.
2. **List every animation/transition** (expand/collapse, fade, slide) and check it at a
   **mid-transition frame**, not just the two settled endpoints. `mainClock.autoAdvance =
   false` + `mainClock.advanceTimeBy(...)` is the safe, gesture-free way to do this (see
   `Collapsible`'s doc comment) -- freezing the animation clock is not a gesture
   simulation and has never caused the hangs `performMouseInput`/`performTouchInput` drag
   simulation has (see point 4). The Collapsible overlap bug only showed up mid-fade;
   both settled states looked completely correct.
3. **Positive scenario:** does the component render/behave correctly with realistic
   content at its intended size? **Negative scenario:** what happens with zero items, an
   empty string, content wider than the container, or a size the caller forgot to
   constrain (see `Carousel`'s doc comment -- an unconstrained `HorizontalPager`
   silently expands to fill the ambient width instead of failing loudly). Prefer a
   component that documents a hard requirement (`modifier` must set a main-axis size) or
   handles the empty case explicitly over one that silently does something ambient/
   unpredictable.
4. **Any pointer-driven interaction** (drag-to-resize, drag-to-scroll, hover-to-open)
   needs its *math* extracted into a plain, non-Composable function and unit tested
   directly (see `resizablePanelFraction` for `ResizablePanelGroup`,
   `scrollDragDeltaToContentDelta` for `ScrollArea`) -- never write the positioning
   math inline inside a `rememberDraggableState { }` lambda where it can only be
   exercised by actually dragging. Simulating a real drag/hover gesture via
   `performMouseInput`/`performTouchInput` has hung this project's JVM test worker
   outright before; the *rendering* half (does the resulting layout look right at a given
   state) is instead covered by screenshot-testing fixed values (a few different
   `initialFraction`s, a few different scroll positions) rather than by reaching those
   values through a live gesture.
5. **Any `Popup`-backed hover-triggered overlay** (`Tooltip`, `HoverCard`) must pass
   `focusable = false` to `AnchoredPopup` -- a focusable popup competes with the
   trigger's own `hoverable()` for focus the instant it opens, which flickers the popup
   open/closed in a loop on desktop. Click-triggered overlays (`Popover`, `DropdownMenu`,
   `Dialog`) keep the `focusable = true` default; their content genuinely needs keyboard
   focus and Escape-to-dismiss.
6. **A component's own doc page must not duplicate itself.** `ComponentDetailScreen`
   shows `examples.first()` once under "Preview & Code"; the "Examples" section must
   only render `examples.drop(1)` (and only render the whole section when that's
   non-empty) -- otherwise every single-example component shows the identical
   preview+code twice on its own page.

## Registry parity status

Every real shadcn/ui **Base** component (`apps/v4/registry/bases/base/ui`,
fetched fresh, not from memory) is implemented **except those below, by deliberate
scope decision**:

- **`questionnaire`** -- **was a real gap**, fixed. Official lives under
  `apps/v4/registry/bases/base/ui/questionnaire.tsx` (also Aria / Radix trees).
  Earlier sweeps that only listed legacy `new-york-v4/ui/` missed it.
  Implemented as `Questionnaire` + Progress / Item / Title / Description /
  Choices / Choice / ChoiceDescription / Input / Error / Actions / Previous /
  Skip / Next / Submit (`questionnaireState()`). Resume: Root `defaultItem`
  (alias of `defaultValue`), Choice `defaultChecked`, Input `defaultValue` —
  each applied once.
- **`native-select`** -- skipped. Real shadcn's version renders an actual HTML
  `<select>`, so on the web it triggers the *browser's own OS-level* native dropdown --
  not custom-drawn JS. Every component in this library, `Select` included, is
  100% pure Compose drawing with zero platform-widget interop. Matching `native-select`'s
  real behavior would mean embedding actual native pickers per platform (`UIPickerView`
  on iOS, Android `Spinner`, no equivalent on Desktop, and Compose-for-Web doesn't render
  to real DOM at all) -- a fundamentally different, much larger scope than every other
  component here. A Compose-drawn "alias" with the same name wouldn't actually be native,
  so it wasn't built as a consolation. User-confirmed scope decision, not an oversight.
- **`direction`** -- not a gap, not implemented, and shouldn't be: Radix's
  `DirectionProvider`/`useDirection` exists because the web has no built-in RTL/LTR
  concept. Compose already has one natively (`LocalLayoutDirection`) -- there's nothing
  to port.
- **`form`** -- consolidated into `Field`. Real shadcn's `form.tsx` is itself a thin
  wrapper of the same `Field` primitives around `react-hook-form`; this library has no
  form-state-management dependency (every component here is already caller-hoisted
  state), so there's no separate `Form`.
- **`input`**/**`sonner`** -- implemented as `Input`/`ShadcnToast` (naming
  choices, not gaps).
- **`data-table`** -- N/A, not a gap: real shadcn's own docs say *"instead of a
  data-table component ... a guide on how to build your own"* on top of `Table` +
  TanStack Table. No Compose Multiplatform equivalent of TanStack Table exists, and
  building one is a much larger scope than this component library's remit.
- **`toast`** (as distinct from `sonner`) -- N/A: real shadcn's own `toast` docs page
  says it's *"deprecated. Use sonner instead"* -- already covered by the `sonner` ->
  `ShadcnToast` mapping above, not a second component to build.
- **`date-picker`** -- N/A as a standalone component in real shadcn either (their docs:
  *"built using a composition of the `<Popover />` and the `<Calendar />`
  components"*) -- added as a catalog doc example (`DatePickerDoc.kt`) composing this
  library's own `Popover` + `Calendar`, not a new `:shadcn:core` component.
- **`select`** -- **was a real gap**, fixed. `Select` previously existed only as
  an internal utility (this catalog app's own theme/base-color/accent pickers in
  `CatalogTopBar`), never exposed as a documented public component, and its trigger
  used `ButtonVariant.Outline` (a filled-button look) rather than real shadcn's actual
  `SelectTrigger` style (`border-input bg-transparent`, a bordered field). Redesigned
  the trigger to match, widened `value` from `T` to `T?` so it can show placeholder
  text (backward-compatible -- `CatalogTopBar`'s existing non-null call sites still
  compile unchanged), fixed its popup panel to use the `popover`/`onPopover` container-
  role tokens instead of generic `surface`/`onSurface` (same class of fix as the
  Popover/DropdownMenu/ContextMenu/Command/Combobox/HoverCard/NavigationMenu pass
  earlier), and gave it a proper `SelectDoc.kt` catalog page.
- **`dropdown-menu`/`command`/`menubar`/`context-menu`** -- **was a real gap**, fixed.
  All four previously took a flat `items: List<DropdownMenuItem>` (or
  `CommandItem`), with no way to reproduce real shadcn's default demos --
  a "My Account" label, a grouped set of items, a separator, then a destructive item.
  `DropdownMenu`/`ContextMenu`/`MenubarMenu` were redesigned from a
  data-list API to a slot-based `content: @Composable DropdownMenuScope.() -> Unit`
  (`DropdownMenuItem`/`DropdownMenuLabel` are now composables in that scope,
  freely mixed with the existing `DropdownMenuSeparator`; `ContextMenu` and
  `MenubarMenu` reuse the same scope since they already shared the row rendering).
  `Command` kept a data model instead (`groups: List<CommandGroup>`, each
  with an optional `heading`) rather than going full slot-based, because its live
  search-filtering needs to inspect every item's label up front -- a freely-composed
  slot API would need extra machinery to hide non-matching children mid-composition.
  This was a deliberate, user-approved API redesign (not additive/backward-compatible --
  nothing is published to Maven Central yet, so no external consumers exist).
- **`accordion`/`aspect-ratio`/`item`/`input-group`/`toggle`** -- checked against real
  shadcn, judged as acceptable documented deviations, not gaps: `accordion`'s doc example
  has 3 items vs real's 1 (arguably more useful documentation, not less correct);
  `aspect-ratio` now bundles the actual upstream demo PNG as a catalog resource
  with grayscale/dark brightness; no network image-loading dependency is added;
  `item`'s doc example is richer than real's minimal single-item default (a superset, not
  a mismatch); `input-group`'s addon pattern differs from real's but is a valid alternate
  composition; `toggle`'s icon-only `aria-label` pattern vs this library's text-glyph
  icons is the same icon exemption documented throughout this file.

**shadcn's "AI Elements" family is implemented**, not out of scope: `Marker`,
`Message`/`MessageGroup`, `Bubble`/`BubbleGroup`,
`Attachment`/`AttachmentGroup`, `MessageScroller` (`attachment.tsx`,
`bubble.tsx`, `marker.tsx`, `message.tsx`, `message-scroller.tsx` in the real registry).
An earlier pass in this project excluded them by guessing their purpose from the
filename alone, without fetching real source -- fetching and reading the actual `.tsx`
confirmed they're general chat/AI-assistant UI primitives, not narrowly AI-chat-specific
in a way that would exclude them from a general-purpose component library, and the user
explicitly asked for them to be added. **Lesson generalized**: never classify a shadcn
component as out-of-scope from its filename -- always fetch
`raw.githubusercontent.com/shadcn-ui/ui/main/apps/v4/registry/bases/base/ui/<name>.tsx`
and read it first.

If a new component lands in the real registry, re-check this list before assuming it's
missing.

**shadcn/ui also ships `docs/utils/*` pages, separate from `docs/components/base/*`** --
these are CSS/Tailwind utilities (modifiers, in Compose terms), not components, so they
don't show up when only checking the components registry. `shimmer` and `scroll-fade`
were missed for exactly this reason (only found once the user pointed at
`ui.shadcn.com/docs/utils/shimmer` and `.../scroll-fade` directly) -- implemented as
`Modifier.shimmer()`/`Modifier.shadcnScrollFade()` in
`library/src/commonMain/kotlin/.../styles/`, cataloged under a new `UTILS`
`CatalogCategory`. `shadcnScrollFade` is wired directly into `MessageScroller`'s
internal scroll container (real shadcn's own message-scroller demo uses it), and
`shadcnShimmer` powers a "Thinking…" placeholder in the message-scroller catalog demo
while a reply is queued but hasn't started streaming yet. **Lesson generalized**: when
auditing registry parity, also check `ui.shadcn.com/docs/utils/*` for utility pages, not
just `docs/components/base/*` for components -- they're a separate nav section on the
real site and easy to miss entirely.

## Notes for future sessions

- **The Compose Styles API does not match what the `kotlin-multiplatform-design-system`
  skill assumes.** The annotation in the currently configured Compose Multiplatform 1.12.2 is
  `androidx.compose.foundation.style.ExperimentalFoundationStyleApi`, not
  `ExperimentalStylesApi`. `padding()` does not exist on `StyleScope` -- use
  `contentPadding(...)`. Verify any new Style API usage against the real jar
  (`~/.gradle/caches/modules-2/files-2.1/org.jetbrains.compose.foundation/`) with a
  compile spike before writing component code, the same way `library/build.gradle.kts`'s
  history did -- don't trust the skill's code samples verbatim for this API surface.
- **Focus rings are drawn with the Style API's own `dropShadow()`, not a hand-rolled
  `Stroke` modifier.** An earlier version of this file claimed the opposite -- that
  `dropShadow` "always rasterizes through an offscreen bitmap and visibly blurs a ring
  this thin," even at `radius = 0.dp` -- and used that to justify a ~90-line custom
  `Modifier.drawWithContent` reimplementation (`Modifier.shadcnFocusRing`, since
  removed). That claim was never actually tested and was false: verified directly
  (2026-07-09) via a screenshot test of `dropShadow(radius = 0.dp, spread = 3.dp)` --
  perfectly crisp, only ordinary single-pixel anti-aliasing, zero layout-size effect,
  exactly matching real CSS `box-shadow`. Every component now calls
  `dropShadow(theme.focusRingShadow())` directly inside its own `focused { }` block (see
  `styles/FocusRing.kt`'s `ThemeData.focusRingShadow()`). Do not resurrect the
  custom modifier or re-add this blur claim without re-testing it first.
- **Colors and hover semantics are checked against real shadcn/ui source**, not memory --
  see `ui.shadcn.com/docs/theming` for token values and
  `github.com/shadcn-ui/ui/blob/main/apps/v4/registry/bases/base/ui/*.tsx` for exact
  per-variant Tailwind classes (hover treatment, border presence, disabled opacity).
  When adding a new component, fetch its real Base source first rather than approximating.
  The same rule applies to `components.json` fields (`tailwind.baseColor`, `style`) --
  see item 8 above for a case where this was skipped and a fabricated-sounding but
  actually-real set of custom base colors got documented as "official" when it isn't.
- Roborazzi screenshot tests are wired (`shadcn/core/src/jvmTest/`, Robolectric-less
  JVM/Desktop capture, see `docs/visual-testing.md`) and compile/pass cleanly (verified
  2026-07-08 via `./gradlew :shadcn:core:verifyRoborazziJvm`). Plain `:shadcn:core:jvmTest` does
  *not* pixel-compare against the committed goldens (`captureRoboImage` is a no-op writer
  without the record/verify system properties the wrapper tasks set) -- after any
  component visual change, run `./gradlew :shadcn:core:recordRoborazziJvm` to update the
  affected goldens, then `./gradlew :shadcn:core:verifyRoborazziJvm` to confirm a clean diff
  before committing. `jvmTest` alone does not enable pixel comparison. The current
  `.github/workflows/ci.yml` still invokes plain `jvmTest`; explicit screenshot
  verification in CI remains a task in `docs/shadcn-align-progress.md`.

## Commands installed

See `.claude/commands/kmm-*.md` for available slash commands.
Key commands:

- `/kmm-run-audit` — architecture audit with per-finding remediation
- `/kmm-harvest-lessons` — collect patterns to upstream to skills
- `/kmm-verify` — full validation pipeline (build + test + apiCheck)
- `/kmm-check-updates` — check for skill updates
