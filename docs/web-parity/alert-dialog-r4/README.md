# Alert Dialog r4 — current-page browser re-verification

## Sources

Current official page re-read at upstream revision
`8a3536678a528cc012b9e62484290e79d0be410f` (MDX, seven examples,
`alert-dialog.tsx`; checksums in [sources.json](sources.json)). Official order: hero
preview, Basic, Small, Media, Small with Media, Destructive, RTL, then API Reference
(`size` table plus Base UI link). The local page keeps that order, already aligned in r3.

## Changes

- The RTL recipe description now uses the official wording “To enable RTL support in
  shadcn/ui, see the RTL configuration guide.” and drops the extra local toolbar note.
- `AlertDialogExamplesTest` now asserts the RTL description alongside the three
  paragraphs pinned in r3.
- The Dropdown-interaction test in `AvatarExamplesTest` was updated to the shared
  DropdownMenu pointer-open behaviour introduced in commit `000feffb` (pointer opens keep
  every row unhighlighted; the first ArrowDown reaches row 1). This is the only cross-page
  test change made here.

## Verification

Final build Wasm SHA-256
`af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`.

Focused tests on final sources: `AlertDialogExamplesTest` 5 (with the RTL copy
assertion) and `ComponentDocumentationTest` 8 in the shared focused run;
`AlertDialogNovaTest` 4, `AlertDialogMediaTest` 3, `AlertDialogScreenshotTest` 4 and
`AlertDialogMediaScreenshotTest` 4 in `:shadcn:core:jvmTest`. XML in [tests](tests/).

Browser evidence after the final build: [browser](browser/) quadrants (pixel-verified
light/dark), six official section pairs in [sections](sections/), and
[browser/interactions](browser/interactions/):

- `local-basic-default`, `local-basic-open`, `local-basic-open-again` — modal opens with
  the overlay, title, description and Cancel/Continue footer.
- `local-basic-escaped` — Escape dismisses the dialog.
- `local-basic-continue-closed` — the Continue action closes it.
- `local-basic-outside-click-still-open` — an outside click does not dismiss the alert
  dialog.
- `local-small-open`, `local-media-open`, `local-destructive-open` — size=sm, media icon
  and destructive variants open correctly.
- `local-basic-open-dark` — dark-theme dialog.
- `local-rtl-default`, `local-rtl-open` — Arabic RTL trigger opens an RTL dialog with
  mirrored footer order.

## Remaining differences

- Body wrapping/font weight, Arabic fallback typography and assistive-technology mapping
  are still open (carried from r1–r3); four old Vega baseline mismatches recorded in r1/r2
  also remain.
- Initial dialog focus is the dialog content, but Tab traversal leaves the dialog to page
  chrome in both the official capture (lands on the nav Home link) and the local capture
  (lands on the trigger); a complete focus-trap/keyboard matrix is therefore not claimed.
- Shared page chrome and Geist rasterization differences are unchanged.
