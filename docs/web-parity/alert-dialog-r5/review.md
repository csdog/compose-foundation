# Alert Dialog r5 review

## Independent verification

The independent verifier confirmed:

- catalog tests 5/5;
- Alert Dialog plus modal-focus tests 17/17;
- Dialog, Alert Dialog, Sheet, Drawer and surfaces regressions 150/150;
- the browser Tab cycle and Escape focus restoration;
- the final Wasm asset and all source, quadrant, section and interaction
  hashes;
- no Alert Dialog or `TeleportPrimitive` ktlint finding;
- `git diff --check`.

## Independent review

The shared focus change is intentionally limited to the modal content entry.
It replaces a zero-size focus sentinel with the panel's focus-group anchor,
then disables that temporary anchor after focus advances to the first action.
This preserves the existing focus cycle while giving the first browser Tab a
stable in-dialog origin.

The focused Alert Dialog test verifies the complete user-facing contract:
Cancel -> Continue -> Cancel, Escape dismissal, and restoration to the trigger.
The 150-test cross-overlay run covers the shared behavior for Dialog, Sheet,
Drawer and surfaces.

The first review pass found two low-severity record gaps: the interaction count
was stale and the catalog test did not assert the full section order. The count
is corrected to 22 and `AlertDialogExamplesTest` now asserts the complete
official page and local API order.

No remaining source, behavior or evidence findings.

The only residuals are the shared documentation shell/font rasterization and
the absence of native HTML alert-dialog accessibility attributes on Compose
Canvas; both are recorded in [README.md](README.md).
