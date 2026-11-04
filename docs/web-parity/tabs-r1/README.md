# Tabs r1 — source recipes, keyboard state and selected chrome

Nine pinned shadcn files are archived in `sources.json`: Base implementation, MDX, Nova CSS and six recipes at `96da9cc7f1aa10580a3741664e2a77c721206c00`. Sources and all hashes were checked. Official page read on 2026-10-31: https://ui.shadcn.com/docs/components/base/tabs .

Behavior reference: [Base UI Tabs](https://base-ui.com/react/components/tabs). Its list defaults to manual keyboard activation; keepMounted is false by default and hidden kept panels remain mounted. The KMP root enum maps these controls into its existing API. Automatic mode remains explicitly available. Existing String values/callback surface is retained, without Base UI event-details/reason/cancel objects.

## Changes

- Root default activation is Manual. Arrows move focus; Enter/Space activate. Horizontal arrows reverse in RTL. Disabled tabs are skipped, Home/End and non-loop boundaries work, keyed reorder follows the current measured visual order, and Tab exits through one roving entry.
- Uncontrolled roots choose an enabled initial/fallback value when missing/disabled; valid explicit defaults are not transiently cleared before child registration is ready. Stable setters observe the latest owner/callback, while controlled refusal leaves selection unchanged.
- Default panels switch immediately; explicit custom transitions remain available. Kept panels preserve input state but consume no gap, are not placed, clear hidden accessibility semantics, reject descendant focus requests and relinquish owned focus. The root uses a pure layout pass to omit hidden panels from its 8dp spacing.
- Selection chrome lives on the actual trigger rather than an implicitly sliding decoration. Weighted triggers still fill their full bounds (the Message Scroller usage pattern). Default lists have zero internal gap, Line lists 4dp, horizontal tracks 32dp; vertical triggers have uniform width and start alignment. Slots place icons and labels in a row with 6dp spacing. Labels use 14/20 medium typography and source foreground/hover tones.
- Line underline is 2dp and outside the trigger at the source offset. Keyboard focus had been tinting transparent interiors through the native shadow painter; Tabs now uses the existing cssStyleable outer-shadow clipping implementation. No duplicate interaction state was introduced.
- Six official recipes are migrated: Default with all four real cards, Line, Vertical, Disabled, Icons, and translated English/Arabic/Hebrew RTL. Exact translated title/label identity was checked for all twelve pairs. Preview and copied standalone code share the compiled source regions through `scripts/generate_tabs_recipe_code.py`; regeneration is idempotent. Existing Default activation API example was corrected in both code and preview to omit the old explicit Automatic mode, with a separate four-mode keyboard test.

## Evidence

Initial original-runtime eight cases had six failures/two passes. The initial panel assertion lacked a recomposition frame, so it is not used as the fade proof. Exact original-runtime isolation reran the strengthened one-frame panel test: old content still exists after one frame (RED); current immediate switch passes. This verifies removal of the implicit fade without waiting through it.

Kept-panel tests exposed 68dp instead of the source 60dp stack, then a genuine ability to focus the hidden input. Final tests check merged semantics absence, unmerged invisibility, the retained requester's rejection, exact stack height and the preserved `Ada` value after reopening.

Visual review exposed focus-shadow fill, confirmed by two RED pixel tests: light interior0.815686 instead of1.0, dark0.247059 instead of0.039216. Both pass after clipping the shadow outside the final shape. Selected initial-frame full-width fill and outside underline placement are independently tested.

Final **58 independent passing cases**, zero failures/errors/skips: twelve behavior, eight light/dark chrome, two existing Nova layout regressions, and thirty-six app cases. The unchanged core22 ran in the actual54-case run; after the Default activation doc correction, app36 ran fresh. This is evidence across two final owning runs, not a claim of one single58-case combined command. Core/shared ktlint pass.

Forty-four native captures cover Default's four panels, Line, Vertical, Disabled, Icons and three RTL languages in 1440/390 WindowInfo × light/dark. Parent reviewed all via eight contact sheets at original resolution. The focus fix changed exactly eight Line/Vertical captures, all re-reviewed; the other36 stayed byte-identical. The last doc correction did not change any capture. Final thirteen source/test/generator/config hashes and forty-four native hashes match.

Legacy owning verification: seven changed, none missing, no baselines recorded. Exact original-two-file isolation yields four passes (Line2/Nova2) and three pre-existing Vega changes (states light/dark and focused light). Current/source-before images, dimensions, pixel JSON, source/harness hashes and verified-PASS provenance are archived. Pre-existing mismatches are not attributed to this round.

Final JS exits0 in18.088s but retains existing unresolved os/path diagnostics; this is not a clean web build. Wasm exits0 in12.729s with one warning. Existing AGP/Text suppression/Gradle10 diagnostics remain. Full repository commit gate was not run and no commit/publication is claimed.

## Scope and remaining acceptance

Native fixtures use actual desktop scenes, simulated WindowInfo1440/390 and 640dp/390dp preview containers. Default root is400dp desktop/342dp under phone constraints; RTL root is384dp max inside the catalog wrapper. Captures add8dp bottom canvas so the external underline is visible; RTL captures also include the real catalog language toolbar. Keyboard Line/Vertical states include their focus ring. They do not prove browser/phone OS rendering or HTML pixel equality.

Remaining work:

- Actual browser light/dark × desktop/phone, fonts/wrapping, every RTL panel and complete hover/pressed/focus state matrix. Native Hebrew toolbar label ellipsizes at the existing144dp shared control width; catalog toolbar parity remains separate from the Tabs primitive.
- Source background/color and underline-opacity transitions still need their150ms temporal parity pass. Default selection now paints on each trigger instead of sliding, but the full transition curves are not accepted here.
- Panel tab stops and trigger↔panel accessibility relationships, focused-tab removal/disable restoration, empty-list edge cases and all caller wrappers/custom style/orientation combinations. KeepMounted nested caller layouts may need their own spacing policy.
- Existing Kotlin API examples beyond the now-tested Default activation example need the complete state matrix/regrouping; list-level Base UI props and generic render/value/event-detail APIs are not exhaustively mirrored by the Kotlin surface.
- Legacy baseline acceptance and full repository commit gate.

Read-only browser recovery revalidation returned `task space not found:18` again. Ego guidev2.2.0 forbids creating a new TaskSpace as recovery; the earlier approval question is unanswered, and no replacement was created. Browser acceptance remains open. Tabs stays unchecked; next priority unstarted page is Toast. Editor remains excluded; Attachment residuals remain deferred.
