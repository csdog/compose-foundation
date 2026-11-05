# Chart page hierarchy r4 (2026-11-05)

Rechecked the current [Base UI Chart page](https://ui.shadcn.com/docs/components/base/chart).
Current shadcn/ui `main` is `8a3536678a528cc012b9e62484290e79d0be410f`; its Chart MDX,
component, examples, Nova CSS, and scale references are byte-identical to the existing
`chart-r1` archive. The eight official page previews still match the local sequence.

## Changes

- Added a reusable `headingLevel` field to component examples so documentation can
  represent nested steps in both the rendered page and Copy Page Markdown.
- Nested Add a Grid, Add an Axis, Add Tooltip, and Add Legend under Your First Chart.
- Added Compose-specific instructions for data/config setup, grid, axis, tooltip, and
  legend. These describe the Kotlin implementation instead of claiming Recharts is
  used underneath.
- Kept the first-chart previews at the already verified 320px height and 48px desktop
  inset.

## Verification

The final focused run passes **96 tests**:

- app documentation and interaction: 56
- core Chart contracts and screenshots: 40

Repository-wide ktlint and detekt pass. Wasm and JavaScript development distributions
exit successfully; JavaScript retains the existing unresolved `os`/`path`
diagnostics. The Wasm artifact SHA-256 is recorded in `summary.json`.

## Browser limitation

No fresh browser matrix is claimed. Ego has no owned `compose-foundation` TaskSpace;
user and unrelated delegated spaces were not opened or claimed, and no replacement
space was created.

## Remaining differences

The prior open items remain: exact Recharts chart geometry and animation, rich
config/payload aliases, locale rounding, advanced responsive date-tick selection,
full browser pointer/touch/keyboard combinations, and complete assistive-technology
parity. The shared page header and code-panel details are also not pixel-identical.
Chart stays unchecked.
