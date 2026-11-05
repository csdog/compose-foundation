# Aspect Ratio r3 — completed-status re-review

## Findings

The current official Aspect Ratio page contains four recipes: Default, Square, Portrait,
and RTL. The local page order and text still match those four sections.

The component retains one deliberate Kotlin deviation: the web wrapper requires a ratio,
while the existing Kotlin API defaults to `1f`. This is documented in the API section and
was not changed here because changing the public signature would be a breaking API change.

The checklist item was still marked complete despite these open items already recorded in
the ledger:

- RTL caption color/glyph rendering and integer-measurement adaptation.
- Shared document/page differences.
- No fresh current-build browser matrix after the horizontal layout and viewport fixes.

## Changes

- Pinned the four current official recipe titles and their order.
- Added exact assertions for Square/Portrait copy, the unset examples heading, and the
  documented Kotlin `ratio = 1f` default deviation.
- Returned Aspect Ratio to unchecked until the outstanding visual and browser verification
  is complete.

## Verification

The focused suite passed 11 tests:

| Suite | Tests |
|---|---:|
| AspectRatioExamplesTest | 6 |
| AspectRatioLayoutTest | 3 |
| AspectRatioScreenshotTest | 2 |

The development Wasm distribution is up to date after the source audit; current app Wasm
SHA-256 is
`0a444bf1c94e5c1343b0a105d4c379b4582596e4d07a9ae7131a1a30d5387a46`.
Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed.

## Remaining differences

- Re-run Default/Square/Portrait/RTL at 1440/390 × light/dark against the current build.
- Close the recorded caption color/glyph, integer-measurement, and shared-page differences.
- Decide separately whether making `ratio` mandatory is acceptable as a breaking public API
  change; it is not silently changed in this review.
