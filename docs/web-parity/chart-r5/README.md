# Chart r5 — official page structure, callout and guide sections

Date: 2026-11-05. Local page: `http://127.0.0.1:8775/chart`.
Official page: <https://ui.shadcn.com/docs/components/base/chart>, revision
`8a3536678a528cc012b9e62484290e79d0be410f` (12 fetched sources/hashes in [sources.json](sources.json)).

## Findings

The eight official previews were already migrated (r4), but the page still used a custom
description and had none of the official guide content. The current official page has:

- the official description "Beautiful charts. Built using Recharts. Copy and paste into your apps.";
- an "Updated: The chart component now uses Recharts v3" callout above the top preview, then
  intro paragraphs ("Introducing Charts…", "Browse the Charts Library"), a `## Component`
  section, `## Updating to Recharts v3` bullets, and a click-to-install manual flow;
- `## Your First Chart` with nested h3 steps (Add a Grid/Axis/Tooltip/Legend), then
  `## Chart Config`, `## Theming` (### CSS Variables, ### hex, hsl or oklch, ### Using Colors),
  `## Tooltip` (preview + ### Props table / ### Colors / ### Custom), `## Legend` (preview +
  ### Colors / ### Custom), `## Accessibility`, and `## RTL`;
- no `Advanced` section at this revision (the earlier audit wording does not exist on the page).

## Changes

- Description set to the official sentence.
- Added the official guide content as sections around the existing eight recipes:
  `Component` (contains the v3 callout, the "Introducing Charts" copy, the Recharts/composition
  notes and a Kotlin `ChartContainer`/`BarChart` example), `Updating to Recharts v3` (4 bullets
  mapped to the Compose API), `Chart Config`, `Theming`, `CSS Variables`, `hex, hsl or oklch`,
  `Using Colors` before the recipes; `Tooltip Props` (props as bullets), `Legend` and
  `Accessibility` after the recipes.
- Kotlin-appropriate code notes replace the TSX snippets (Canvas renderer, `ChartSeries`,
  `ChartThemeColors`, `ChartTooltipContent`, `ChartLegend`, `accessibilityLayer`), and the callout
  text explicitly notes that this Compose port keeps the API shape without bundling Recharts.
- Kotlin-only demos stay after the official recipes with `Kotlin extension:` titles.

## Verification

- **Tests: 41/41 pass** — `ChartExamplesTest` 36 (nine behaviours × four viewports: first-chart
  steps, grid/axis, keyboard tooltip, legend, interactive series switch, anatomy, RTL three
  languages) and `ChartPageTest` 5 (official description/recipe order, guide section order in page
  markdown, callout/intro copy, Chart Config/Theming copy, Tooltip props + Accessibility copy).
  XML under [tests](tests/).
- **Build:** Wasm snapshot wasm
  `02d85afc1cd47cd566e3c5d532c5ff93b15a82698e079e7405a8d7e21735fa49` (slot e / 8775).
- **Four quadrants** 1440×1000 / 390×844 light+dark after the final build, official theme
  re-asserted and checked: [desktop light](browser/sheet-desktop-light.png),
  [desktop dark](browser/sheet-desktop-dark.png), [phone light](browser/sheet-phone-light.png),
  [phone dark](browser/sheet-phone-dark.png).
- **States** ([browser/interactions](browser/interactions/)): interactive demo Desktop → Mobile
  series switch (`demo-desktop-series`, `demo-mobile-series`), keyboard tooltip on the Add Tooltip
  chart after two arrow keys showing February/Desktop 305/Mobile 200
  (`tooltip-idle`, `tooltip-keyboard-march`), legend with both series (`legend`), RTL Arabic chart
  (`rtl-arabic`). Series/anatomy/RTL language switching is also covered by the JVM suite.

## Historical r5 differences / open items

- **No slot between the top preview and Usage** in the shared doc model: the official callout and
  the two intro paragraphs sit above the demo preview, and `Component`/`Updating to Recharts v3`
  sit before Installation. Locally they are folded into the first guide section after
  Usage/Composition (guide sections render before the recipes), so their position differs from the
  official page while their copy is present. This is the documented shared-shell limitation.
- The official `Chart Config`/`Theming` block appears after the first-chart examples and
  `Tooltip Props`/`Legend`/`Accessibility` between the Tooltip and RTL previews; the catalog can
  only render guide sections before or after the whole recipe list, so the local order is
  Component → config/theming → recipes → tooltip props/legend/accessibility. Relative order inside
  each group is official.
- The official `### Props` table is flattened to bullets (the guide block model has no table
  block); Tooltip/Legend `Colors`/`Custom` subsections are folded into their parent guide sections.
- The local page always renders a Usage section (the official Chart page has none) and the
  project's Kotlin API tables do not apply here (the official page has no API Reference).
- Charts are drawn with Canvas instead of Recharts; upgrading notes and code snippets are therefore
  Kotlin/Compose equivalents (documented in the callout copy). Shared title/description wrapping,
  preview centring and font rasterization remain shared-page items.

## Independent worktree review (2026-11-09)

The historical shell-order limitations above are superseded by the current
`ChartDoc.kt`: `introductionSections` places Component and Updating to Recharts v3
before Installation, and `beforeExampleSections` interleaves Chart Config/Theming
before Tooltip and Tooltip Props/Legend/Accessibility before RTL. `ChartPageTest`
checks this order. The callout/intro is still folded into Component; the Props
table and Colors/Custom subsections remain flattened.

The authoritative archived source pin is `8a3536678a528cc012b9e62484290e79d0be410f`
in sources.json and chart-r6/summary.json. The different pin in
.agent-runs/chart/progress.md is stale. The r6 45/45 report covers source hashes
and browser smoke/capture checks; it does not establish full-page visual parity
or browser interaction correctness. See `.agent-runs/review-chart/progress.md`
for the independent evidence assessment.
