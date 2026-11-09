# Badge r4 — token-efficient pilot

- Date: 2026-11-07
- Official revision: `42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`
- Official page: `https://ui.shadcn.com/docs/components/base/badge`
- Source evidence: `sources.json` plus raw copies in `source/`
- Status: complete

## Changes

- Pinned the official Badge MDX, `badge.tsx`, seven official examples, and
  `apps/v4/registry/styles/style-nova.css` at the revision above.
- The MDX and all seven examples are byte-identical to r3's `8a353667…` pin.
  The newly pinned items are `badge.tsx` and Nova CSS.
- Removed the local-only Badge Composition section with
  `BadgeDoc.compositionCode = null`; the official page has no Composition heading.
- Added keyboard tests for `onClick` and `href` badges. Space and Enter both activate;
  a `href` badge is not announced with `Role.Button`.
- Added an optional `interactionSource` parameter and deterministic tests for the
  Default, Secondary, Outline, and Ghost hover colors.
- Badge production changes are limited to the optional controlled `interactionSource`
  parameter; no golden image was edited.

## Verification

- `:shadcn:core:jvmTest --tests '*Badge*'`: 17/17 passed.
- `:app:shared:jvmTest --tests '*Badge*'`: 8/8 passed.
- Final combined Badge/Alert/Separator run: Badge 25/25, Alert 20/20, Separator 48/48.
- `git diff --check`: passed.
- `token_efficiency.py validate`: passed.
- `:shadcn:core:verifyRoborazziJvm`: 12 Badge behavior tests passed; 4 golden
  comparisons failed. A HEAD-only control run reproduced the same four failures.
  The drift affects 331 of 365 checked goldens and changes rendered text sizes, so it
  is a pre-existing renderer/font-environment problem rather than this Badge change.
- Current browser capture: official/local at 1440×1000 and 390×844, light/dark.
- Evidence: `browser/`, [`../token-three-components.json`](../token-three-components.json).
- `:shadcn:core:ktlintJvmMainSourceSetCheck` and
  `:app:shared:ktlintJvmMainSourceSetCheck`: passed.
- `:shadcn:core:ktlintCheck`: passed after formatting the two previously failing test files
  and the remaining `commonMain` violations.

## Open

- Golden pixel verification remains repo-blocked by pre-existing font/renderer drift; the
  HEAD-only control and current browser quadrants separate this repository issue from Badge.
- The caller owns icon size through `LocalInlineIconSize`; non-Nova focus presets and
  CSS `transition-all` are documented Compose platform/API differences.

Run evidence:

- `.agent-runs/badge-token-pilot/progress.md`
- `.agent-runs/badge-token-pilot/commands.log`
- `.agent-runs/badge-token-pilot/logs/`
