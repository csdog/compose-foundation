# Visual regression testing

`:shadcn:core` has a Roborazzi screenshot-test suite under `shadcn/core/src/jvmTest/`, running
Robolectric-less on the JVM/Desktop target (`roborazzi-compose-desktop`) -- no Android
or Robolectric dependency anywhere in this module.

## Why this exists

Live-browser spot checks (toggling dark mode, tabbing to a button to see its focus
ring) are useful but not reproducible or reviewable -- nothing is committed, so a
regression only surfaces if someone happens to look at the same screen again. This
suite makes every component's visual states a committed, diffable artifact.

## What's covered

- **Per-component state matrices** (`shadcn/core/src/jvmTest/kotlin/.../components/*ScreenshotTest.kt`):
  every component's variants/enabled/disabled/checked-selected states, each captured in
  both light and dark, plus a real keyboard-`requestFocus()` capture for interactive
  leaf controls (proves the focus ring actually renders, not just that the modifier
  compiles).
- **Style preset matrix** (`StylePresetMatrixTest.kt`): all 8 `StylePreset` values
  (Vega/Nova/Maia/Lyra/Mira/Luma/Sera/Rhea) side by side in one composite image
  (`style_matrix_ring_swatch_<light|dark>.png`), plus a real focused `Button` per
  preset (`style_matrix_button_focused_<preset>_<light|dark>.png`) -- this is the
  durable proof that picking a style preset actually changes the ring/shape tokens end
  to end (`StylePreset` -> `Theme.light/dark(ring = ...)` -> `Button`
  -> `shadcnFocusRing`), not just in the two presets that happened to get eyeballed live.

Not covered yet: hover state (no reliable hover-simulation API is used here -- only
prop-driven states and real focus/click are exercised), and per-side border-stripping
inside `ToggleGroup`/`ButtonGroup` (see `docs/shadcn-parity.md`).

**A real blind spot, found the hard way:** every golden here composes once, under one
fixed theme, and never recomposes -- so a `Style { }` block that captures a stale
CompositionLocal snapshot (see `.claude/AGENTS.md`'s notes on this anti-pattern) renders
*correctly* in a static test, because the one theme it was composed under happens to be
the one it's frozen at. This exact bug shipped undetected in `InputGroup`/
`Input`'s error state until a live dark-mode toggle in the running catalog app
revealed it. This suite catches wrong colors; it cannot catch colors that are right once
and then never update. Since a screenshot diff structurally can't catch this, the fix
does not replace it. `currentTheme` inside `Style { }` is a compiler error
(`@Composable` getter). The remaining risk is capturing a theme color in
composition and stuffing it into Style -- that compiles and a static screenshot
will not catch a live theme change.

## Running it

```bash
# Record goldens (first run, or after an intentional visual change)
./gradlew :shadcn:core:recordRoborazziJvm

# Verify against committed goldens (what CI runs, via :shadcn:core:jvmTest)
./gradlew :shadcn:core:verifyRoborazziJvm
```

Goldens live in `shadcn/core/src/jvmTest/snapshots/` and are committed to git. On a
mismatch, Roborazzi writes a `*_compare.png` beside the golden with
expected/actual/diff side by side; CI uploads these as the `screenshot-diffs` artifact
on failure.
