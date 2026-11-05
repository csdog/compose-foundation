# Combobox r1 — current official page structure, editable/typed states, panel widths

Official reference: <https://ui.shadcn.com/docs/components/base/combobox>, upstream revision
`8a3536678a528cc012b9e62484290e79d0be410f`, style `base-nova`. Fetched 2026-11-05; file
SHA-256s are in `sources.json`.

## Findings

The local page was still a Base UI API dump: 48 recipes named like "Default open",
"Default query", "Custom filter", "Default should filter", "Default empty text",
"Default input icon", "Required", "Modal", "Accessibility label" and "Side offset".
The current official page has 12 previews and a different order:

demo → Installation → Usage → Composition (`Simple`, `With chips`, `With groups and
collection`) → Custom Items (guide + code) → Multiple Selection (guide + code) →
Basic, Multiple, Clear Button, Groups, Custom Items, Invalid, Disabled,
Auto Highlight, Popup, Input Group, RTL → API Reference.

Component differences found while translating the official examples:

- The inline popup was `anchor width + 28dp`; Base UI sizes it from the *input*
  width (measured official Basic popup 215px against a 215px input group), so the
  local panel was 28dp too wide.
- `ComboboxChips` did not publish its width, so the multiple popup fell back to the
  200dp default instead of the official 320px chips width.
- `ComboboxChipsInput` defaulted to the "Search…" placeholder and did not open the
  panel on click; the official multiple recipe shows no placeholder and opens on
  press.
- Caller-composed `ComboboxList` content never filtered or hid `ComboboxItem`s and
  `ComboboxEmpty` always rendered; official `Combobox.Empty` appears only when the
  collection is empty.
- The editable input did not open on pointer press (only while typing) and had no
  leading `InputGroupAddon` slot for the official Input Group recipe.
- Single-select clear needs a Kotlin `onClear` callback (the value is nullable); Base
  UI derives the reset from `null`.

## Changes

- `ComboboxDoc.kt` rewritten to the official 12 recipes, official order/titles/copy,
  official Usage/composition trees, the two guide sections and a Base UI API
  Reference pointer plus a Kotlin API table. Removed the 48 API-dump recipes.
- `components/combobox/Combobox.kt`:
  inline popup width follows the anchored input/chips width (new `panelWidth`
  override used by Popup/Input Group); chips width tracking; chips-input press opens
  the popup and its default placeholder is empty; items are filtered/hidden against
  the live query and `ComboboxEmpty` hides itself when the collection is non-empty;
  editable input opens on press and accepts a leading `InputGroupAddon` slot
  (`leading`); controller exposes `shouldFilter`.
- Exported `code` is kept in sync with every preview; the guides' code is the Kotlin
  translation of the official MDX snippets.

## Verification

- Focused JVM suites: `ComboboxExamplesTest` 12 tests (3 methods × 1440/390 ×
  light/dark) — official order/copy/composition assertions, every official preview
  rendered, editable open → type "nu" → filter → ArrowDown/Enter pick.
- `:app:shared:jvmTest --tests ...ComboboxExamplesTest` green; XML archived in
  `tests/`.
- Final Wasm build SHA `89721bca784cc2dd66346c26a0de1fa9a445ecf2a0d7ea0d13e04fc658b8129d`; browser evidence: `browser/` (official/local 1440/390 ×
  light/dark + `sheet-*.png` + `meta.json`) and `browser/states/` (closed, open,
  filtered, selected, multi-select open/add, custom items, invalid, disabled,
  auto-highlight, popup, input-group, RTL).
- Visual review of the four quadrants and the states above at 1440/390 in light/dark.

## Remaining differences (explained)

- Composition: the catalog `ComponentDoc` model has a single `compositionCode`
  block; the official three `###` trees (`Simple`, `With chips`, `With groups and
  collection`) are rendered as labelled trees inside one code block, so the ToC has
  one "Composition" entry instead of three.
- The editable input is a fixed 216dp-wide `InputGroup` (official Basic 215px); the
  Input Group recipe's field is therefore 216dp instead of the official ~229px
  content-sized field, while its popup honours the official 240px `w-60` override.
- Kotlin clear for single-select needs `onClear` (nullable value); official derives
  it from Base UI's reset.
- The RTL preview adds the catalog's language toolbar (shared `RtlPreview` chrome);
  the official page has its own docs-level language selector. Hebrew toolbar label
  uses the shared catalog font stack (glyphs may fall back), while the recipe text
  itself uses the Arabic/Hebrew translations from the official MDX.
- `ComboboxCollection` is a name-parity passthrough because Compose callers already
  iterate options explicitly; the API table says so.
- Page API Reference is a Kotlin API table plus the Base UI link (official page only
  links out).
