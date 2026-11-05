# Code Block r2 — current official page, full recipe rebuild

Date: 2026-11-05. Local page: `http://127.0.0.1:8775/code-block`.
Official page: <https://ui.shadcn.com/docs/components/base/code-block>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (all 31 fetched sources and their sha256 are in
[sources.json](sources.json)).

## Findings

The previous round left ten recipes (Default, Basic, Line Numbers, Start Line, Diff, Wrap,
Height, Show More, Destructive, Trace) plus a `maxHeight` cap in `CodeBlockContent`. The current
official page defines **25 preview sections** and a much larger page body:

- Intro preview (demo, no heading) plus Basic, Header, Actions, Floating Actions, Line Numbers,
  Start Line, Wrap, Highlighted Lines, Diff, Height, Show More → Max Height (h3), Size,
  Destructive, Trace, Tabs, Message, Typeset, RTL, Syntax Highlighting → Languages, Terminal
  Output, Server Rendering, Streaming, Markdown (h3).
- Usage (imports + JSX), Composition tree, six Features bullets, eight Accessibility bullets,
  and nine API Reference subsections (`CodeBlock`, `CodeBlockHeader`, `CodeBlockTitle`,
  `CodeBlockActions`, `CodeBlockCopyButton`, `CodeBlockContent`, `CodeBlockLine`,
  `CodeBlockFooter`, `useHighlightedCode`).
- Every preview uses `styleName="base-rhea"` except RTL, which uses `base-nova`. The catalog keeps
  its **Nova** default (per the parity decision) — Rhea is not faked.
- Syntax highlighting is an explicit out-of-scope boundary: Shiki (`useHighlightedCode`), ANSI
  terminal colors, server rendering, streaming and Markdown/Streamdown integrations are not
  ported. `CodeBlockContent` renders plain text.
- The official `code-block.tsx` is a thin slot wrapper; visual rules live in
  `style-rhea.css`/`style-nova.css` (`cn-code-block*`). Both stylesheets were fetched for
  geometry comparison.

## Changes

Page (`app/shared/.../catalog/docs/CodeBlockDoc.kt`, rebuilt from the in-flight work):

- Official order and titles restored; recipes are sections (no `Examples` heading), Max Height is
  an h3 under Show More, Languages and Terminal Output are h3 under Syntax Highlighting.
- Demo, Basic, Header, Actions, Floating Actions, Highlighted Lines, Tabs, Message, Typeset and
  the syntax-highlighting recipes added with the official copy fragments (description, Actions
  note, Floating Actions note, Start Line gutter note, Wrap note, Height scroll-fade note,
  Show More footer note, Size note, Trace note, RTL guide link).
- Features (6 bullets, highlighter bullet reworded for plain text) and Accessibility (8 bullets)
  guide sections added; Usage imports/snippet and Composition tree match the official source.
- Nine API Reference subsections added; `useHighlightedCode` is documented as not provided.
- RTL recipe implements the official language toolbar (Arabic default, English, Hebrew), header
  following the page direction, code content forced LTR, and Arabic/Hebrew catalog fonts.
- Typeset is approximated with catalog heading/body text because the project has no `typeset`
  utility; the recipe description says so.
- Download buttons in the demo/Actions header are drawn (official icon/label) but Canvas cannot
  start a file download; documented below.

Component (`shadcn/core/.../codeblock/CodeBlockContent.kt`):

- `maxHeight` + `scrollable` cap (official `max-h-*` + `overflow-y-hidden`): scrollable caps use
  `scrollFade` + `verticalScroll`; non-scrollable caps clip and keep the static bottom
  `scroll-fade-y` mask.
- Horizontal `scroll-fade-x` while a non-wrapped block scrolls.
- Code content is always LTR (`dir="ltr"` parity); the header still follows the page direction.
- Line numbers are `hideFromAccessibility()` (official "Line numbers are hidden from screen
  readers").
- Keyboard scrolling retained (arrows, PageUp/Down, Home/End) and typed as a stable modifier.

## Verification

- **Focused tests: 26/26 passed** — `CodeBlockParityTest` 11 (core) and
  `CodeBlockExamplesTest` 15 (page). XML archived under [tests](tests/). New coverage: capped
  scroll by keyboard, non-scrollable clip, RTL content LTR + header direction, recipe order/copy,
  wrap toggle height change, Show More/Max Height heights and toggles, Tabs switching, Trace,
  Message, Typeset, plain-text syntax recipes.
- **Build:** Wasm development distribution rebuilt for this page; served snapshot wasm
  `9862c51aa2d4c5b26086265e89618996d8c315d4c7346a0173caabf79a5a7107`
  (`slot=e`, port 8775; log `/tmp/cf-parity/build-e.log`).
- **Four browser quadrants** at 1440×1000 and 390×844, light/dark, reviewed after the final build:
  [desktop light](browser/sheet-desktop-light.png), [desktop dark](browser/sheet-desktop-dark.png),
  [phone light](browser/sheet-phone-light.png), [phone dark](browser/sheet-phone-dark.png).
  A page-level scroll review of the whole page is in
  [local-page-contact.png](browser/local-page-contact.png); official section references are under
  [official-sections](browser/official-sections/).
- **States exercised in the browser** (evidence in [browser/interactions](browser/interactions/)):
  copy → check confirmation, wrap toggle on/off, floating actions revealed on hover, capped block
  scrolled with keyboard End, Show More collapsed → expanded → collapsed, Tabs route.ts → chat.tsx,
  RTL Arabic header + code LTR, RTL language menu open, English (LTR) switch.
- Full-page first screen matches the official skeleton: title, description, Base UI mark, demo
  preview/code, Installation, Usage, Composition, Features, all recipe headings in order,
  Accessibility, API Reference.

## Remaining differences

- **Style context:** official previews are style `base-rhea` (2xl radius, `bg-muted` surface,
  `px-4 py-4`, 14px code) while the catalog renders its Nova default (lg radius, bordered
  `bg-background`, `px-3 py-3`). Documented and intentional; no per-preview style switch was faked.
  RTL preview is Nova on both sides.
- **Syntax highlighting:** Shiki tokenizer, ANSI color interpretation, server rendering, streaming
  and Markdown/Streamdown recipes are out of scope; `useHighlightedCode` does not exist locally.
  `Syntax Highlighting`, `Languages` and `Terminal Output` previews show the same content as plain
  text (ANSI escape codes are not rendered), and their descriptions say so.
- **Typeset:** no `typeset` utility exists in the project; the preview reproduces heading/body
  rhythm with catalog text styles, and the description labels the approximation.
- **Download:** the demo/Actions download icon is inert — Canvas cannot open a browser file
  download without a platform API; copy is the working action.
- **Scrollbars:** official `scrollbar-thin` draws a visible track on horizontal/vertical scroll;
  Compose Web does not render native scrollbars for these scroll containers.
- **Page skeleton gaps (shared):** the official intro paragraph after the demo (before
  Installation) and the "CodeBlockContent takes a string or a list of CodeBlockLine" paragraph
  after Composition have no slot in the shared `ComponentDetailScreen`; their content is carried
  by the Features bullets/recipe copy. Only the Base UI variant is implemented (React Aria/Radix
  tabs are project-wide out of scope).
- **Focus semantics:** the local content shows its ring on pointer focus as well as keyboard focus
  (Compose has no `:focus-visible`); official only rings on keyboard focus.
- Font rasterization of Geist/Arabic/Hebrew differs from the browser; no layout/color/content
  difference remains at the checked sizes beyond the items above.
