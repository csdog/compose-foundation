# Context Menu r1 — current official page structure, pointer sides, panel chrome

Official reference: <https://ui.shadcn.com/docs/components/base/context-menu>,
upstream revision `8a3536678a528cc012b9e62484290e79d0be410f`, style `base-nova`.
Fetched 2026-11-05; file SHA-256s are in `sources.json`.

## Findings

The local page was still a 39-recipe Base UI API dump ("Checkboxes", "Radio group",
"Disabled", "Default disabled", "Default destructive", "Default variant", "Inset",
"Non-modal", "Modal", "Undismissable", "Controlled", "Portal", "Group heading",
"Trigger", "Item", "Content", "Side offset", …). The current official page is:

demo → Installation → Usage → Composition → Basic, Submenu, Shortcuts, Groups, Icons,
Checkboxes, Radio, Destructive, Sides, RTL → API Reference.

Measured official panel widths (right-click, light theme, 1440px): Basic 144,
Submenu content 144 / sub-content 144, Shortcuts 144, Groups 144, Icons 144,
Checkboxes 187.7, Radio 144, Destructive 144, Sides 144, RTL 192. The demo content is
`w-48` (192) with a `w-44` (176) submenu; submenus elsewhere are auto/min-w-36 (144).

Component differences found:

- The panel was hardcoded to 192dp; official content is `min-w-36` (or the recipe's
  `w-48`/`w-44`), so most recipes rendered 48dp too wide.
- The panel used an inner 1dp border instead of the official `ring-1
  ring-foreground/10` outer ring.
- `ContextMenuContent` exposed only `sideOffset`/`alignOffset`; Base UI also exposes
  `side` (top/right/bottom/left/inline-start/inline-end) and `align` — required by
  the official Sides recipe.
- `ContextMenuItem` had no slot content, so the official Icons/Destructive/RTL
  recipes (icon + label + shortcut) could not be expressed; only flat labels.
- `ContextMenuSubContent` could not override its width (official demo submenu is
  176, other recipes 144).
- Demo/submenu rendering already had window coordinates, edge flipping, RTL, touch
  cancel and two-layer Escape from the previous round and still passes.

## Changes

- `ContextMenuDoc.kt` rewritten to the 11 official recipes plus the official Usage,
  composition tree, descriptions and Base UI API Reference pointer. Removed the
  39 API-dump recipes. All previews use the official dashed `aspect-video
  max-w-xs` trigger (`Right click here`; touch long-press stays available).
- `components/contextmenu/ContextMenuPosition.kt`: new
  `ContextMenuPointPositionProvider` + `ContextMenuSide` — physical
  Top/Right/Bottom/Left + logical InlineStart/InlineEnd, cross-axis `align`
  (Start/Center/End), side/align offsets, edge flip and window clamping.
  Unit-tested directly (`ContextMenuPositionTest`, 8 tests).
- `components/contextmenu/ContextMenu.kt`: `ContextMenuContent` exposes
  `side`/`align` and `panelWidth` (official 144 default; demo 192), forwards the
  shared `rememberMenuPanelStyle(novaWidth, novaRing)` so the ring is an outer
  shadow; `ContextMenuItem` gained a required-slot overload (default label row keeps
  destructive/variant colors); `ContextMenuSubContent` gained a `style` override and
  defaults to the official 144dp width.
- Consumed parity-b's shared `rememberMenuPanelStyle(novaWidth/novaMinWidth/novaRing)`
  change; no edit to shared files from this batch.

## Verification

- `ContextMenuExamplesTest` 16 tests (4 methods × 1440/390 × light/dark): official
  order/copy/composition/API link, all 11 previews, four Sides targets, RTL
  Arabic→English→Hebrew switch.
- `ContextMenuPanelTest` 2 tests (open panel 144dp → 136dp row; 192dp → 184dp row),
  `ContextMenuPositionTest` 8 tests, existing `ContextMenuScreenshotTest` 2 — all
  under `:shadcn:core:jvmTest --tests com.toutiaoyan.compose.ui.components.contextmenu.*`.
  XML archived in `tests/`.
- Final Wasm build SHA `89721bca784cc2dd66346c26a0de1fa9a445ecf2a0d7ea0d13e04fc658b8129d`; quadrants in `browser/`; open/hover/checked states in
  `browser/states/` (default, basic, submenu + nested submenu, double-Escape, groups,
  icons, checkboxes toggled-and-still-open, radio, destructive, sides=top, RTL Arabic
  menu + language menu).

## Remaining differences (explained)

- Panel width is an explicit `panelWidth` (144 default) because Compose menu rows
  fill their parent width; official `min-w-36` grows with content (e.g. Checkboxes
  187.7). Recipes that need it pass the measured official width (192 demo/RTL,
  188 checkboxes, 176 demo submenu).
- The official trigger switches `Right click here` / `Long press here` through CSS
  `pointer-fine`/`pointer-coarse`; the catalog shows one label, while touch devices
  still open through the implemented long-press path.
- `panelWidth` is a Kotlin-only way to express the official `className` width; the
  API Reference only links to Base UI like the official page (no Kotlin table).
- RTL preview language names come from the shared `RtlPreview` toolbar; its Hebrew
  label uses the catalog font fallback, while the context-menu content itself uses
  the official Arabic/Hebrew strings and mirrored submenu arrows.
- The open-panel visuals are covered by the browser evidence and the panel-width
  widget test; the committed Roborazzi goldens still cover only the idle trigger
  (same as before — no open-state golden existed, and this round did not add one).
