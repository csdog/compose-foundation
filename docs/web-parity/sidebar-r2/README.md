# Sidebar r2 — content scrolling and section spacing

Uses the exact Base/Nova source archived and hashed in [r1](../sidebar-r1/sources.json). Upstream SidebarContent is flex-1/min-h-0 with overflow-auto, hidden overflow in icon mode; Nova gives it gap-0, Header/Footer gap-2 p-2, Group p-2. Panel inner layout itself does not add p-2/gap-2.

## Changes

- SidebarContent now owns a remembered vertical scroll state and scroll viewport. Icon collapse clips rather than exposing scroll interaction. The existing public API is retained.
- Header/Footer each get 8dp padding and 8dp spacing. Desktop panel global padding/spacing is removed, including its floating/inset inner body. Group owns its own 8dp padding and has no extra group gap.
- Header/Footer catalog examples wrap menu groups in SidebarContent in both copied code and actual previews, so the footer stays at the bottom.

## Validation

Eight new tests (desktop/mobile × light/dark × geometry/long content) all failed before the fix and pass after it. They check exact section/child positions, no artificial content spacing, real mouse-wheel input and semantic scroll-to-last, with header/footer positions unchanged.

Final combined run: **34 tests, zero failures/errors/skips**, core/shared ktlint and shared JVM compilation pass. Final source/test/config/doc hashes are in `verification/final/source-test-config-doc-hashes.json`. Parent checked all eight native layout captures via two contact sheets at original resolution; final bytes match the checked green captures exactly.

These are actual desktop scene renders: desktop panel 256×400, forced-mobile Sheet 288×768 in the 1024×768 native popup scene. They are not phone-browser captures or complete SidebarMenu visual tests. The extra eight mobile geometry captures remain a separate fixture set; this round does not claim their parent visual review.

JS webpack exits 0 in 26.873s but still reports existing os/path errors. Wasm exits 0 in 18.351s with one warning. Existing AGP/Text suppression/Gradle10 diagnostics remain. No legacy golden was recorded; r1's before/after isolation is historical evidence for r1, not an acceptance result for this layout revision.

## Remaining

Official page recipe migration, menu/group-label/action/badge/trigger/rail styling and behavior; physical right positioning, floating/inset complete states, shared layout composition writes, animation read phases, global shortcut/focus/persistence policy and actual browser light/dark × desktop/phone checks. Original Ego TaskSpace18 remains missing, and no replacement was created. Sidebar remains unchecked; full repository commit gate is not established.
