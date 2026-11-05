# Hover Card r3 — current page copy/order and fresh open/close evidence

Date: 2026-11-05. Local page: `http://127.0.0.1:8774/hover-card`.
Official page: <https://ui.shadcn.com/docs/components/base/hover-card>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (sources and hashes in [sources.json](sources.json)).

## Findings

The current official page is: demo (`hover-card-demo`, `h-80`), Installation, Usage,
Composition, Trigger Delays, Positioning, Basic, Sides (`h-[22rem]`), RTL, API Reference.
The demo and Basic are the same `hover-card-demo` example; API Reference is only a link to
the Base UI docs.

The local page already had the four official recipes (Default, Basic, Sides, RTL) and the
r2 positioning/focus fixes, but:
- Composition rendered only the tree, without the official sentence
  “Use the following composition to build a `HoverCard`:”.
- Trigger Delays/Positioning used custom prose instead of the official copy.
- The three Kotlin-only demos were still titled Controlled / Provider timing / Disabled,
  not `Kotlin extension: …`.

## Changes

- Moved Composition into `beforeExamples` with the official paragraph + tree (order:
  Composition → Trigger Delays → Positioning → Basic → Sides → RTL).
- Trigger Delays now says “Use `delay` and `closeDelay` on the trigger to control when the
  card opens and closes…”; Positioning now says “Use the `side` and `align` props on
  `HoverCardContent` to control placement…” (with the logical-side note kept for Kotlin).
- Renamed Kotlin-only recipes to `Kotlin extension: Controlled`,
  `Kotlin extension: Provider timing`, `Kotlin extension: Disabled`.
- No component code change was needed this round; the r2 trigger-anchor, immediate
  focus-out close, Nova 5dp window avoidance and RTL line-box fixes remain in place.

## Verification

- **17 focused JVM tests passed** (XML in [tests](tests)): 5 `HoverCardExamplesTest`
  (recipe order/API sections, official Next.js copy, four physical sides, three-language
  RTL via the toolbar, controlled state) and 12 `HoverCardBrowserParityTest`.
- Wasm development distribution rebuilt; the four quadrants below were captured from the final
  shared build `421a6c71…` (see [meta.json](meta.json)).
- Four quadrants at 1440×1000 and 390×844 in light and dark:
  [sheet-desktop-light](sheet-desktop-light.png), [sheet-desktop-dark](sheet-desktop-dark.png),
  [sheet-phone-light](sheet-phone-light.png), [sheet-phone-dark](sheet-phone-dark.png).
- Fresh browser open/close evidence in [browser](browser): desktop light and dark demo
  closed → open → closed, phone light/dark demo closed → open, and all four physical sides
  opened (Left/Top/Bottom/Right) after hovering the Sides triggers. The demo card shows
  @nextjs, “The React Framework – created and maintained by @vercel.” and
  “Joined December 2021”, positioned below the trigger.

## Remaining differences (page stays unchecked)

- Safe polygon across the trigger/content gap and text-selection protection are still not
  implemented.
- Multiple triggers/handles, arbitrary nested content hosts, clip/scroll-container
  ancestors and very large content positioning remain unverified.
- Escape handling when focus is outside the root, and the r2 continuous multi-hover /
  reload-then-hover matrix, remain open.
- The enter/exit animation is still based on the requested side rather than the resolved
  side after collision avoidance; static positions match but “flipping animation origin”
  is not claimed.
- Full screen-reader/interactive-content link, touch devices, dynamic theme/shape
  transitions and the complete three-language web matrix (this round captured the default
  Arabic state; r2 holds the language-switch evidence) remain unverified.
- Page chrome differences are platform scope: no React Aria/Radix UI tabs, Kotlin-only
  code panel and desktop/phone code tabs, and the official API Reference is a single link
  to Base UI while this page documents the Kotlin API surface.
