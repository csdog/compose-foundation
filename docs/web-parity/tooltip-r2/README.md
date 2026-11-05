# Tooltip — Web parity r2 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/tooltip](https://ui.shadcn.com/docs/components/base/tooltip),
Base UI + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`).

## Official page reading

- Demo: outline "Hover" trigger with "Add to library" tooltip.
- Sections: Installation (TooltipProvider at the app root), Usage, Composition, Side, With Keyboard
  Shortcut, Disabled Button, RTL, API Reference (5 previews).
- Timing comes from the provider (delay/skip-delay/close-delay), Escape dismisses, arrow sits on the
  content side and content hover keeps the tooltip alive.

## Findings and changes

- All five official recipes already existed with official titles; this round added the missing
  official **Composition** block and official descriptions for Side, Disabled Button and RTL,
  and curated the Kotlin-only extras down to `Arrow`, `Delay` and `Controlled` (the rest of the
  19-example matrix is no longer rendered).
- Verified the existing r1 behaviour items: provider/official timings, Escape dismissal, arrow
  placement, content hover, disabled-button wrapper, dynamic trigger removal and unmount cleanup
  are covered by the catalog + core tooltip suites.

## Verification

- `TooltipExamplesTest` 20/20 pass across 1440/390 × light/dark (`tests/`), including all four
  physical sides, the keyboard-shortcut recipe, disabled wrapper, RTL language switching and
  focus/Escape.
- Wasm Web build `wasm=97fa0dc37ba6ef519ac835a9de585e4f3cf0c3f35ce14a80d232502370a46931`;
  four quadrants captured after the final build, official background pixels verified.
- Browser: hover shows the "Add to library" tooltip with arrow
  (`browser/desktop-light-hover.png`), keyboard tab focus opens it and Escape dismisses
  (`browser/desktop-light-keyboard.png`, `browser/desktop-light-escape.png`).

## Remaining differences (open, from r1)

- Full pointer/keyboard delay matrix across nested providers, arrow offset per physical side and
  native overlay accessibility mapping remain open at component level.
- The eight upstream timing sources are covered by tests but not all shown as page recipes; the
  `Delay`/`Controlled` Kotlin extensions document the Kotlin API.
- No React Aria/Radix tab set. Tooltip stays unticked.
