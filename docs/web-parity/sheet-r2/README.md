# Sheet, round 2

Status: in progress. Browser acceptance remains open. This follows the [round1 behavior checkpoint](../sheet-r1/README.md).

## Reference and implementation

The [nine archived sources](../sheet-r1/sources.json) use shadcn revision `96da9cc7f1aa10580a3741664e2a77c721206c00`. Besides the component, Nova CSS and four recipes, this round archives the official RTL transformer and registry builder. The builder first expands the style classes, then `buildRtlStyles` applies `transformDirection`; its `right-` → `end-` mapping means the close button follows logical end. Reading only Nova's original `right-3` declaration had missed this generated behavior; the implementation and RTL test were corrected after reviewing the native capture.

- Panel: popover background, inward-facing1dp border, CSS shadow-lg pair, no default root padding. Caller width and Style overrides remain supported.
- Header:16dp padding,2dp gap; title16sp/24sp medium with foreground color, description14sp/20sp muted, full line boxes.
- Footer:16dp padding,8dp gap, column actions stretched by default, automatic top margin. Its slot now has `ColumnScope` instead of `RowScope`; callers using Row-specific alignment must migrate. Repository callers are compiled as part of validation.
- SheetColumn: weights are allocated to body content first. Only unused space becomes Footer's auto margin, rather than splitting remaining space with an extra weighted spacer. `fill=false` can shrink a short body; vertical panels retain natural height. Alignment and alignBy modifiers are preserved through parent data.
- Close: ghost IconSm28dp, top/end12dp, localized label override remains available, source default accessible label is Close. The end position mirrors in RTL independently of physical panel sides.
- Four official recipes are prepended to the documentation. Existing Kotlin demonstrations remain available, with colliding titles renamed. Copied code is generated from the compiled recipe and shared helpers.
- Default: exact source copy and initial values, clickable labels focus inputs, Save changes leaves the panel open, Close dismisses. Form state resets after unmount like source uncontrolled inputs.
- Side: four physical directions, ten full paragraphs at14sp/22.75sp with8dp bottom margins, scrollable body, top/bottom capped at50% viewport height, Save changes and Cancel actions.
- No Close Button: exact source copy and outside dismissal.
- RTL: Arabic/English/Hebrew, FieldGroup and Field structure, source username peduarte without@, physical Left for RTL and Right for English.

## Verification scope and remaining gaps

Native popup tests run in an actual1024×768 JVM scene. Overridden `LocalWindowInfo` values check390/1440 responsive widths and recipe height rules; they do not establish mobile browser position, mobile safe areas or viewport-unit behavior. The four core layout captures and recipe captures are native evidence, not browser screenshots or approved goldens.

Still open: browser desktop/mobile × light/dark including interaction states; touch-specific initial focus and virtual keyboard behavior; trigger-less restoration, nested/non-modal sheets, rapid interrupted transitions; all-direction/density animation and blur/shadow matrices; arbitrary fonts, scaling, custom Style and intrinsic/unbounded/very short constraints; full ColumnScope alignBy and dynamic child/weight changes; RTL language toolbar interaction and assistive technology. No old Sheet screenshot baseline should be updated without reviewing intentional source-backed changes and running independent verification.

Original Ego TaskSpace18 is lost. The existing replacement authorization question remains unanswered; no replacement space was created. Editor stays excluded and Attachment's recorded gaps stay deferred.

## Final checkpoint evidence

[Summary](verification/summary.json):55 independent tests pass (15 behavior +12 layout/pixel +28 recipe), and core/shared ktlint pass. JS exits0 in38.178s with known os/path diagnostics; Wasm exits0 in19.397s with one webpack warning. All13 source/configuration/generator hashes and40 reviewed native capture hashes matched at archiving. [Review sheets](review-sheets/layout.json) preserve36 recipe images without pixel resampling. Two sources were added to the manifest to verify the actual generated RTL close position.

The frozen-clock scroll test initially aborted without a complete suite result; only6 completed XML records existed, not28 passed tests. After replacing its loop with one ScrollBy action and explicit clock advancement, the four-mode Side subset and full28 recipe suite passed. See [bounded diagnostic notes](verification/diagnostic-notes.json). The dark translucent border oracle explicitly composites over the background and allows one8-bit rounding step. These fixture corrections do not alter production scroll behavior or infer a cause for the test-process exit.

Version0.3.26. No full-repository commit gate or screenshot baseline record/verify was run. Existing native heading and content-description tests do not prove HTML dialog title/description linkage or assistive-technology equivalence; that remains part of the open accessibility matrix.
