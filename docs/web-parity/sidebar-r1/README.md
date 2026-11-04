# Sidebar r1 — provider state and mobile shell

Upstream: Base Sidebar + Nova CSS, revision `96da9cc7f1aa10580a3741664e2a77c721206c00`. The 19 distinct files and hashes are in `sources.json`; app/registry use-mobile hooks have separate names. All hashes were checked before archiving.

## Changes

Desktop `open` and mobile `openMobile` are independent. Mobile defaults closed, auto-selects below 768dp, and accepts an explicit override. Remembered callbacks use current owners/mode; two same-frame uncontrolled mobile toggles cancel. Desktop persistence does not run for mobile changes. None keeps the panel visible without forcing provider state.

Offcanvas retains its composed children/input state and slides over 200ms linear. Mobile Sheet defaults to 288dp, Sidebar background and no padding or close button; caller width override remains supported. Default inward border is wholly inside the panel and mirrors logical Start in RTL; None has no default border. Catalog's static sidebar explicitly opts out of responsive switching/shortcut handling. Mobile example uses the new mobile state.

## Evidence

`verification/summary.json` records 26 independent passing JVM tests (6 state, 8 responsive/callback, 8 mobile geometry, 4 border) and core/shared ktlint. The exact final source restoration reused matching cached XML after an earlier actual 26-test runtime pass. Shared JVM caller compilation passed. JS exits 0 but retains os/path module errors; Wasm exits 0 with one warning. These are not clean web builds.

Initial original-source RED had 5 failures and one weak pass. A strengthened isolated motion RED measured 47dp after 96ms; the corrected linear motion passes its midpoint assertion. Test source/config hashes match the archived version 0.3.29 worktree at archival time.

Legacy verification: 17 cases, current 1 pass/16 changed, prepass 2 pass/15 changed, no baselines recorded. Exact source isolation shows five identical artifacts and ten desktop border-only differences between current and prepass. Two mobile cases change with panel padding/background. Large desktop palette and whole-scene mobile scrim changes already existed before this round. Do not attribute all legacy differences to this round or treat the old goldens as accepted.

Eight native captures are blank geometry fixtures (actual JVM popup 1024×768, simulated WindowInfo 390/1440); they do not prove phone/browser/menu appearance. Parent visual review remains pending after image output truncation.

## Open work

- Content scrolling and Header/Footer/Group spacing were subsequently fixed in [r2](../sidebar-r2/README.md); the evidence in this r1 directory predates those edits.
- Group/menu/label/action/badge styling and full official examples, floating/inset layouts, physical right placement and RTL.
- Trigger PanelLeft/28dp and rail focus/hit behavior; shortcut scope, focus restoration, persistence integration.
- Child composition writes to shared layout state and width-animation composition reads need review.
- Browser light/dark × desktop/phone and complete state matrix. Read-only revalidation of original Ego TaskSpace 18 on 2026-10-31 still returned `task space not found: 18`. No replacement space was created; prior approval question remains unanswered.
- Full repository commit gate has not passed; no new commit is claimed.

Sidebar remains unchecked. Editor is excluded; Attachment residuals remain deferred.
