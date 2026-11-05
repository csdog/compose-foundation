# Item r3 — current official page copy and page-order verification

Official reference: <https://ui.shadcn.com/docs/components/base/item>,
upstream revision `8a3536678a528cc012b9e62484290e79d0be410f`, style `base-nova`.
Fetched 2026-11-05; file SHA-256s in `sources.json`. This round builds on r2
(titles/wrapping, menu row context, RTL body); r2 evidence remains under
`docs/web-parity/item-r2/`.

## Findings

The page order, the 11 previews and the 10 API sections already matched the current
official page, but several recipe descriptions and the `Item vs Field` guide were
paraphrased instead of using the official copy, and the paragraph that follows the
top demo preview was missing.

## Changes

- `ItemDoc.kt`: replaced every recipe `descriptionMarkdown` with the official text
  (`variant`, `size`, icon/avatar/image media, group, header, `render`→Kotlin
  `href` wording for Link, RTL guide sentence), restored the official two
  `Item vs Field` paragraphs, and added the official intro paragraph
  ("The `Item` component is a straightforward flex container…") on the top demo.
  API section titles/order remain the official 10; Kotlin-specific property
  descriptions are kept because they document actual parameters.
- No component changes this round.

## Verification

- `ItemExamplesTest` 9 tests, including a new copy test asserting every official
  description, the intro paragraph, the `Item vs Field` paragraphs and the API
  order; link/dropdown/RTL/typography tests from r2 still pass.
- Core: `ItemParityTest` 12 + `ItemScreenshotTest` 4 green.
- Final Wasm build `ace00496e82de7b39bf64c205bd547398e1962626cf568e9cc4b8e9890ff2aeb`.
  Quadrants + sheets in `browser/`; `browser/states/` includes the dropdown menu
  open and the RTL preview.
- Four quadrants (1440/390 × light/dark) reviewed against the official page: title,
  description, TOC (Installation…API Reference + 10 API entries), preview and code
  panel skeleton match.

## Remaining differences (explained)

- The intro paragraph is rendered above the top preview (catalog's first-example
  description slot) whereas the official page places it after the preview/code block.
- Header recipe at 390px: the official three-card row hits CSS flex min-content
  overflow (titles ~66/58/61px in a 45px content box); Compose wraps instead. This
  is a browser flex sizing difference, not a font fix; not masked with hardcoded
  padding or truncation (same residual as r2).
- Link: official `href="/dashboard"` is same-page navigation; local `href` goes
  through `UriHandler` (browser opens a new tab), and HTML target/rel/modifier-key
  behaviour plus same-page anchoring remain open.
- Dropdown: pointer-open initial highlight differs from Base UI (official does not
  highlight the first item until ArrowDown); shared `DropdownMenu` behaviour, tracked
  for the Dropdown Menu round.
- Shared typography/anti-aliasing wiring, `text-pretty` wrapping, image cropping,
  arbitrary Style overrides and the full dynamic/focus/touch/a11y matrix remain open.
