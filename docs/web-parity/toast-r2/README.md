# Toast — Web parity r2 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/toast](https://ui.shadcn.com/docs/components/base/toast),
Base UI toast + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f`
(`sources.json`).

## Official page reading

- Demo: "Show Toast" trigger adding `toast.add({ title: "Event created", description: "Sunday,
  December 3 at 9:00 AM" })`.
- Sections: Installation (including adding a single app-level `Toaster`), Usage, Types (status icons
  for default/success/info/warning/error/loading), Action, Promise, API Reference.
- Three previews total: demo, Types, Promise. The toaster is viewport-fixed in the app, not inside
  the preview card.

## Findings and changes

- The three official recipes already existed and the catalog renders the shared toaster at the app
  root (`CatalogToastHost`, bottom-end with 16dp inset), i.e. **viewport-fixed** rather than inside
  the preview. This round verified that in the browser (toast appears at the window bottom-right and
  dismisses after five seconds) — the previous "toaster inside the preview" evidence is superseded.
- Page alignment: added the official Types and Promise descriptions, hid the generic "Examples"
  heading, and curated the Kotlin-only extras down to `Position`, `Duration` and `Close button`
  (the remaining 20+ parameter demos are no longer rendered).
- The official Action prose section is not reproduced as a separate page section; the action is
  visible in the Default recipe ("Undo" closes its own toast). Recorded as a residual.

## Verification

- `ToastExamplesTest` 24/24 pass across 1440/390 × light/dark (`tests/`), including the source
  message/Undo id, five type announcements, promise transitions and positioning extensions.
- Wasm Web build `wasm=97fa0dc37ba6ef519ac835a9de585e4f3cf0c3f35ce14a80d232502370a46931`;
  four quadrants captured after the final build, official background pixels verified
  (light 255 / dark 10).
- Browser: Default toast visible bottom-right of the viewport
  (`browser/desktop-light-default-toast.png`), auto-dismissed
  (`browser/desktop-light-default-dismissed.png`), Types section with five type buttons
  (`browser/...` captured during the probe; see evidence note).

## Remaining differences (open)

- Action prose section omitted (see above); the Kotlin `onAction` callback replaces the React
  `actionProps`.
- Base UI toast's full manager options (stacking depth/swipe direction/timing details) and exact
  enter/exit motion are covered by component tests only, not a 1:1 page recipe.
- No React Aria/Radix tab set and no ad card. Toast stays unticked.
