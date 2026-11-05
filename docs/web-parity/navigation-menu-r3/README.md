# Navigation Menu — Web parity r3 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/navigation-menu](https://ui.shadcn.com/docs/components/base/navigation-menu),
Base UI + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`).

## Official page reading

- Unnamed demo preview (`h-96`): Getting started (3 links), Components (6 links, 2-column,
  hidden below `md`), With Icon (Backlog / To Do / Done), Docs link.
- Sections: Installation, Usage, Composition, Link Component (prose + code, no preview), RTL
  (same demo in Arabic/Hebrew), API Reference.
- Composition includes `NavigationMenuIndicator`. 768/1024 media queries: Components appears at
  `md` and the 2-column grid grows from 500 to 600px at `lg`.

## Findings and changes

The official Default/RTL recipes existed already; the page still rendered the 20-example
parameter matrix after them and had no Link Component section, no Indicator in Composition and no
official RTL copy.

- `NavigationMenuRecipes.kt`: official RTL description, `NavigationMenuIndicator` in the exported
  composition, and a phone-preview fix — the official page shrinks its preview padding below `sm`
  so the three visible triggers fit; the Compose preview keeps 40dp, so this round sets a 16dp
  preview padding for the demo and compacts trigger horizontal padding below 640dp (desktop keeps
  the official 10dp trigger padding). The 768/1024 logic itself is unchanged.
- `NavigationMenuDoc.kt`: added the official "Link Component" guide (render prop → Kotlin
  `NavigationLink(onClick = ...)`), hid the generic "Examples" heading, and kept only three
  genuinely Kotlin-specific extras: `Kotlin extension: Vertical`, `Kotlin extension: Delay`,
  `Kotlin extension: Controlled` (the rest of the matrix is no longer rendered).
- `NavigationMenuResponsiveTest` updated to assert the curated extras, RTL copy, Indicator
  composition and the Link Component section.

## Verification

- `NavigationMenuExamplesTest` 16/16 and `NavigationMenuResponsiveTest` 2/2 pass (`tests/`), including
  the 768/1024 media-query grid test and the three-language RTL recipe.
- Wasm Web build `wasm=c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`;
  four quadrants captured after the final build and reviewed (official quadrants re-asserted right
  before capture because agents share one browser profile).
- Browser states: hover/click opens the Components panel with the six links in a 2-column grid
  (`browser/desktop-dark-components-panel.png`), Escape closes
  (`browser/desktop-dark-after-escape.png`), hovering Getting started
  (`browser/desktop-dark-hover-getting-started.png`), 1024 viewport
  (`browser/tablet1024-dark-components-panel.png`), and the 390px preview now fits all three
  visible triggers (`sheet-phone-light.png`).

## Remaining differences (open, from r2)

- Shared NavigationMenu viewport / Root `align` handling is still open (r2 item); the popup here
  paints outside the preview card instead of being clipped to an in-page viewport like the official
  `h-96` preview.
- The official `render` prop (Next.js Link / native anchor) has no Kotlin equivalent: links are
  `onClick` actions with button semantics, not real anchors.
- Full pointer/keyboard delay matrix, native focus outline and assistive-tech mapping remain open.
  Navigation Menu stays unticked.
