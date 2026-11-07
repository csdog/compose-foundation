# Carousel r4 review

## Independent review

The final review found no implementation or evidence-integrity issue.

The first review found that About, Options and Events had dropped the official
Embla wording and reference links. The final page retains those links and adds
the Compose Pager mapping alongside them. Plugins also retains the official
`plugins`-prop framing and explains the Kotlin autoplay replacement.

## Independent verification

Verification passed on the final tree:

- `node docs/web-parity/carousel-r4/validate.mjs`: 10 sources, 8 quadrants,
  88 section captures and 7 interactions, with every hash matching.
- Carousel catalog: 8/8.
- Shared documentation regression: 8/8.
- Carousel core suites: 17/17.
- Page order is About -> Installation -> Usage -> Composition -> Sizes ->
  Spacing -> Orientation -> Options -> API -> Events -> Plugins -> RTL ->
  API Reference.
- Final Wasm `a0632a7824f765b591ee.wasm` has SHA-256
  `9f3c798e7efa3c253701849ba080311d44ea15e3a8c44224a50a4b93ca3a9458`
  and is the artifact loaded by the browser manifest.
- Browser interactions verify 1/2/3 -> 2/3/4 navigation, drag advancement,
  the two-second autoplay tick, and Arabic ١ -> ٢ through the logical next
  control.
- `git diff --check` passes.
- Carousel ktlint findings are zero. The aggregate failure remains four
  unrelated pre-existing long lines in other catalog tests.

No unresolved review finding remains.
