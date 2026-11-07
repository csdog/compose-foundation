# Alert Dialog r5 - modal focus and current-page parity

## Source pin

The official page was re-read from
`ui.shadcn.com/docs/components/base/alert-dialog` at revision
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX, Base UI wrapper,
seven examples and Nova CSS match the archived r4 files byte-for-byte. Exact
URLs and SHA-256 hashes are in [sources.json](sources.json).

The official order is hero preview, Installation, Usage, Composition, Basic,
Small, Media, Small with Media, Destructive, RTL, then API Reference. The local
page already follows that order and uses the official descriptions.

## Change

Opening a modal now focuses the modal's focus-group anchor before advancing to
the first action. The temporary anchor becomes non-focusable after
initialization, so Tab moves through the dialog actions instead of escaping to
the page trigger beneath the modal.

An Alert Dialog regression verifies Cancel -> Continue -> Cancel, Escape
dismissal, and focus restoration to the trigger. The existing generic Dialog
tab-cycle test remains in the cross-overlay suite.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Catalog tests | 5/5, including full section order | [log](logs/verify-catalog.log), [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.AlertDialogExamplesTest.xml) |
| Alert Dialog plus modal focus | 17/17 | [log](logs/verify-core.log), [XML](tests/) |
| Dialog, Sheet, Drawer and surfaces regression | 150/150 | [log](logs/cross-overlay-tests.log) |
| Wasm distribution | PASS | [log](logs/wasm-build.log), main Wasm SHA-256 `1355c1d598d1b749897e839716f96cd223eb6fae8184765a268246ef86f999cc` |
| Owning ktlint audit | No Alert Dialog or TeleportPrimitive finding | [log](logs/ktlint.log); aggregate reports retain unrelated pre-existing violations |

## Browser evidence

The current build loaded `658820f72b4617efb380.wasm` and
`df4c7195f77eef6695dc.wasm`.

- Four official/local viewport-theme quadrants:
  [browser/meta.json](browser/meta.json).
- Six official sections at 1440x1000 and 390x844, light and dark:
  48 captures in [sections/sections.json](sections/sections.json).
- Modal and action states (22 captures):
  [browser/interactions/interactions.json](browser/interactions/interactions.json).

The interaction captures verify Basic open, the Cancel -> Continue -> Cancel
Tab cycle, Escape closure with trigger focus restoration, outside-click
non-dismissal, Continue closure, dark mode, Small, Media, Small with Media,
Destructive, phone layout, and an Arabic RTL dialog. Official desktop and phone
open states are captured beside the local states.

## Accepted differences

- Shared documentation chrome and Geist/Arabic font rasterization remain
  site-level parity work.
- Compose Canvas does not expose the same native HTML alert-dialog role and
  modal accessibility attributes as Base UI.
- The local API section documents the Kotlin component surface in addition to
  the official `size` row.

## Review

Independent review and verification are recorded in [review.md](review.md).
