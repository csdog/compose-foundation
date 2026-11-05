# Progress r3 — current page, four quadrants and interaction state

Date: 2026-11-05. Local page: `http://127.0.0.1:8775/progress`.
Official page: <https://ui.shadcn.com/docs/components/base/progress>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (all fetched files and sha256 in [sources.json](sources.json)).

## Findings

The current official page still has four Base UI previews (demo, Label, Controlled, RTL) in that
order, plus Usage, a nested "With label and value" part inside Composition, the standard RTL guide
line and a single Base UI API Reference link. The page uses `styleName="base-nova"` — the same
style context as this catalog (no Rhea difference for this component). The previous round (r2)
already had the four recipes; this round re-read the current sources, corrected the remaining
copy/structure details and captured the current browser matrix.

## Changes

- Composition now renders the official composition tree (`Progress → ProgressLabel/ProgressValue →
  ProgressTrack → ProgressIndicator`), and the nested h3 "With label and value" holds the official
  paragraph plus the Kotlin label/value snippet.
- Label paragraph restored with its inline code (`Use \`ProgressLabel\` and \`ProgressValue\` …`).
- RTL description replaced with the official RTL guide link (the language toolbar is preview UI);
  the Arabic/Hebrew number formatting and mirrored fill were already implemented.
- First API section now points at the Base UI Progress documentation before the Kotlin tables.
- No component or theme changes were needed for Progress this round.

## Verification

- **Tests: 28/28 pass** — `ProgressExamplesTest` 16 (4 viewports × 4 behaviours: 13→66 after
  500 ms, label layout/value, controlled slider, three-language RTL) and `ProgressPageLayoutTest`
  12 (4 viewports × 3: official-only recipes + Kotlin notes, label/track width, 288 dp RTL body and
  label rows). XML under [tests](tests/).
- **Build:** Wasm snapshot wasm
  `b222e1e1bac5a6d926fad48202243c14b66b01225fd873d73e325a795cf2c650` (slot e / 8775).
- **Four quadrants** at 1440×1000 / 390×844 light+dark, captured after the final build and
  reviewed: [desktop light](browser/sheet-desktop-light.png),
  [desktop dark](browser/sheet-desktop-dark.png), [phone light](browser/sheet-phone-light.png),
  [phone dark](browser/sheet-phone-dark.png). Official theme was re-asserted by capture.py and the
  background pixel was checked for every official quadrant.
- **States** ([browser/interactions](browser/interactions/)): controlled recipe starts at 50%;
  keyboard End fills to 100% and Home empties it (`controlled-end-100`, `controlled-home-0`);
  clicking the Progress track leaves the value unchanged (`controlled-track-before/after` are
  byte-identical — the official track is also inert, as diagnosed in r2); RTL toolbar menu opens,
  English switches label/value to LTR (`rtl-menu`, `rtl-english`). Focus ring on the slider and
  hover/pressed styling were exercised in earlier r2 evidence and are covered by tests.

## Remaining differences / open items

- The shared page cannot render prose between the Composition code block and later sections, so the
  official h3 content order (paragraph → snippet → tree) renders locally as tree → paragraph →
  snippet. Composition content is complete, order differs.
- API Reference keeps the project's Kotlin API tables (which the official page does not have) and
  adds the official Base UI link sentence; "Kotlin conventions and accessibility" is a documented
  project extension section, not part of the official page.
- The Progress track is not a slider in either implementation; official quarter-click diagnosis
  (r2) found no trusted click effect, and the local track is likewise inert. Full pointer
  drag/keyboard matrix on arbitrary browser engines, native HTML progressbar naming,
  min/null/format/render props and screen-reader behaviour remain open.
- Legacy Vega screenshot goldens still differ (inherited from r2; not refreshed). Shared
  initial-load/resize rendering and title/description wrapping differences remain shared-page
  items. Mobile capture is 390 px mouse emulation, not a real touch device.
