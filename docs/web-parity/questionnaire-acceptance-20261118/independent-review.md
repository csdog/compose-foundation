# Questionnaire independent final review

2026-11-18. Verdict **PASS** for the documented Base/Nova Compose Web scope in `closure-draft.md`, with the platform and API boundaries below retained in the published acceptance record.

## Evidence reviewed

- Independently checked the plan, current checklist, scoped changes, pinned official sources, baseline captures and final acceptance receipts. All 26 official files match their recorded byte counts and SHA-256 values at revision `968d8cb9dc42ae5d52e8fd356cf84596ec832a26`.
- `acceptance-evidence.json` has no receipt hash drift. The final locked build has identical before/after input maps. Current app, core and scripts match that map. JS 49 and Wasm 45 distribution members match the disk hashes used for browser transport verification.
- Archived XML contains 128 passing tests with zero errors, failures or skips. This includes 61 core Questionnaire, 38 catalog Questionnaire and 29 shared preview regressions. The 34 screenshot checks are included in the core total. Fourteen separate recipe snippets compile and owning lint passes.
- All locked browser results pass. Eight matrix cases cover 144 recipe positions and 112 official-recipe screenshots. Four flow cases cover external validation, corrected submission, reset feedback, dialog reopening/dismissal and numeric shortcuts. Four layout cases cover progress semantics, navigation and live theme changes. Four route cases cover anchors, reload/history and clipboard output. Four motion cases record intermediate positions and reduced-motion behavior. All 422 transport receipts match their actual response bodies and build artifacts.
- Arabic, English and Hebrew composition behavior is checked by the passing `arabicAndHebrewMirrorChoicesAndKeepNavigationWorking` JVM test. Browser locale coverage remains bounded as documented.

## Findings resolved

- Initial-composition documentation now attributes server rendering to upstream React and describes the Kotlin collection behavior accurately.
- The explicit external validation error now participates in the active question's accessible name and error state. Final flows show the actual public-audience error instead of the contradictory generic required-answer fallback.
- Progress exposes the named read-only progressbar with the real step range. Final browser evidence confirms the previous slider export has been corrected.
- All 30 refreshed Questionnaire references match both the pre-change baseline actual and the repeat capture byte for byte. Their old hashes match the original HEAD blobs. No additional Questionnaire reference was refreshed. The archived dimension and pixel changes therefore predate this scoped implementation.
- Shared preview additions retain default alignment and fixed-height behavior. Existing non-center Accordion and Collapsible callers use fixed heights. Shared regressions pass. The unrelated ComponentDetailScreen anchor changes remain present and outside this audit's write scope.

## Acceptance boundaries

- Kotlin state, callbacks and composition supply the local equivalents for the upstream native form, render and SSR contracts. Native FormData, cancelable reset events, DOM substitution and hydration remain upstream interfaces. Shortcuts uses the existing Compose Select.
- Browser native focus remains an unnamed Canvas proxy. Named semantics, Compose JVM focus assertions and browser keyboard behavior provide the recorded local evidence. Actual operating-system screen-reader announcements remain unverified.
- Shared Dialog still lacks an exported native dialog role. Visible headings, Cancel/reopen, Escape and outside-touch dismissal have real browser evidence. This acceptance retains that shared semantics limitation explicitly.
- Narrow-card semantic proxy coordinates can lag the rendered wrapped content. The saved DOM contains overlapping stale proxy positions. Screenshot-derived radio borders map one-to-one to the named controls, and trusted touches at the rendered controls pass selection, error correction and submission. The proxy-coordinate defect remains recorded.
- Android/iOS execution and system AT remain unexecuted. Desktop has compile evidence. Dynamic theme changes are covered. The official page supplies no separate shape-switching recipe.

No remaining blocker was found within this documented scope. This review added only this report and performed no implementation edits or commits.
