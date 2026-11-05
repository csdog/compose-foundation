# Command — Web parity r11 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/command](https://ui.shadcn.com/docs/components/base/command),
Base UI + `base-nova`, revision `8a3536678a528cc012b9e62484290e79d0be410f`
(`sources.json`).

## Official page reading

- First preview `command-demo`: a `max-w-sm rounded-lg border` command panel with
  "Type a command or search…", Suggestions (Calendar, Search Emoji, Calculator disabled),
  separator, Settings (Profile ⌘P, Billing ⌘B, Settings ⌘S).
- Sections: About, Installation, Usage, Composition, Basic, Shortcuts, Groups, Scrollable, RTL,
  API Reference (6 examples total).
- Basic: outline "Open Menu" button opening a CommandDialog with a Suggestions group.
- Shortcuts: dialog with Settings icons + shortcuts. Groups: dialog with icons and a separator.
- Scrollable: dialog with Navigation / Actions / View / Account / Tools groups.
- RTL: the demo content in Arabic with `dir=rtl` and a language selector (official preview).

## Findings

The previous tick was inaccurate for the page: the local page still rendered a pre-8a35
parameter matrix of 54 examples (Default, Long list, Dialog/Dialog title/…, Disabled, Loading,
Keywords, Empty, Custom filter, Loop, Controlled, Close button, Non-modal, Undismissable, …)
and had no RTL recipe, no About section and no Composition block. The component behaviour items
recorded earlier (Home/End list scrolling, controlled value rejection, IME composition) are
implemented and covered by the new tests.

## Changes

- `CommandDoc.kt` rebuilt to the official page: description/copy, official Composition tree,
  official example order (demo, Basic, Shortcuts, Groups, Scrollable, RTL) with official titles and
  descriptions, an About section, and 4 curated Kotlin extras
  (`Kotlin extension: Controlled value`, `Custom filter`, `Long list keyboard navigation`,
  `Keywords`). The 50 redundant parameter-matrix examples were dropped (Lead-approved); the page
  keeps a compact API table.
- `CommandOfficialRecipes.kt` (new): data-driven previews/code for the five official recipes so the
  exported Kotlin code stays in sync with the preview; the Scrollable recipe is generated from the
  same item lists.
- New `CommandExamplesTest`: recipe order/titles/copy/About/Composition, official demo content,
  Basic dialog open, Scrollable reaching its last group, RTL Arabic headings, and Home/End
  scrolling to the first/last item in the long list.

## Verification

- `:app:shared:jvmTest --tests *CommandExamplesTest` — 6/6 pass (`tests/`).
- Wasm Web build `wasm=b3b5e53c3bf756e7afc8256cfd83b076bdc22a01c820a1ee85d864411072647a`;
  four quadrants captured after the final build (`local-*.png`, `sheet-*.png`,
  `browser/meta.json`), plus browser states:
  `browser/desktop-light-basic-dialog-open.png` (search field + Suggestions rows),
  `browser/desktop-light-basic-dialog-closed.png` (Escape),
  `browser/desktop-light-shortcuts-dialog-open.png`.
- Official DOM headings/tabs/examples count recorded in `browser/meta.json` (6 previews).

## Remaining differences (explained)

- The About section renders after Composition (local page renderer order) instead of before
  Installation; the text is the official cmdk attribution.
- Local CommandDialog is centred and uses the library Dialog width (official uses `top-1/3` and
  `p-0`); the content, copy and rows match. Documented as a Dialog-chrome difference.
- API Reference is a Kotlin API table (official links the cmdk docs).
- No React Aria/Radix tab set and no ad/Vercel card — platform scope.
