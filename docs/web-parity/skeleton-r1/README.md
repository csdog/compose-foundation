# Skeleton r1 — official recipes and pulse observation

Ten pinned upstream files (Base component, Nova CSS, MDX and seven recipes) are archived in `sources.json`, revision `96da9cc7f1aa10580a3741664e2a77c721206c00`. All source hashes were verified. The official page was read on 2026-10-31: https://ui.shadcn.com/docs/components/base/skeleton . Source palette/geometry targets here are Nova rather than an inferred shared website preview theme.

## Findings and fixes

Default previously showed only two 20dp lines; it now has the 48dp circle, 16dp row gap, 250×16/200×16 lines and 8dp line gap. Avatar previously showed a square and disagreed with copied code; it now has a 40dp circle and 150×16/100×16 lines. Card, Text, Form, Table and RTL are migrated in full from the archived recipes.

Card is capped at 320dp, with two-thirds/half lines and 16:9 image placeholder. Text is capped at 320dp with a three-quarter last line. Form has two 60dp groups, 28dp group gaps and a 96×32dp button. Table is capped at 384dp with five rows; the first column consumes remaining width and the others are 96dp/80dp with 16dp gaps. RTL has real English/Arabic/Hebrew selection and logical mirroring.

All seven official previews and copied standalone snippets now derive from the compiled `SkeletonRecipes.kt` regions via `scripts/generate_skeleton_recipe_code.py`. Generator idempotence was checked after formatting. Shimmer remains a separately listed project extension: its earlier single-row copied code now matches the three-row preview using the same generator. Android Studio preview helpers also reuse the actual recipes.

Default chrome was already Nova bg-muted/rounded-md and remains unchanged. The original pulse rebuilt its consumer 49 times during the controlled frame test; the opacity State is now read inside Style's own observation scope. The 2-second 1→0.5→1 animation is retained and the consumer no longer recomposes on each frame. Existing Kotlin style policy is retained: explicit non-null Style supplies the entire class set; use `rememberSkeletonStyle() then ...` for overrides or `rememberSkeletonChrome()` to omit pulse.

## Verification

- Initial original-runtime RED: eight cases, six pass/two fail. Both failures measured 49 consumer compositions instead of one. Timing/chrome/circle tests already passed before the fix.
- Final core: ten passing tests for light/dark chrome, circle overrides, explicit replacement styles, samples at 0/500/1000/1500/2000ms and stable consumer composition counts.
- Final app: forty passing cases (ten recipe/language/extension states × viewport 1440/390 × light/dark), including actual language selector interaction, exact dimensions, gaps, aspect ratio and responsive table width.
- Final combined run: **50 tests, zero failures/errors/skips**, core/shared ktlint pass. Shared test compilation validates the catalog callers.
- Legacy owning `verifyRoborazziJvm` task: **two light/dark Skeleton screenshots pass without changes**; no baseline recorded.
- Forty native PNGs were reviewed through eight contact sheets using original pixels with no crop/resizing. Their final hashes and the eleven source/test/generator/config hashes were rechecked after validation; all match. Contact layout/order is in `review-sheets/layout.json`.
- JS exits 0 in 25.487s but retains existing unresolved os/path diagnostics; it is not a clean web build. Wasm exits 0 in 18.557s with one existing warning. AGP experimental options, Text invisible-reference suppression and Gradle10 warnings remain.

These are native JVM captures with 640dp/390dp containers, 24dp preview insets and simulated WindowInfo 1440/390 inside the actual desktop test scene. Table therefore measures 384dp/342dp. The RTL toolbar owns a separate inset. Selected-language captures occur later in the live pulse timeline than the initial Arabic capture; they are not frozen references at a common opacity phase. The native fixtures do not prove a phone-browser render or website pixel equality.

## Remaining acceptance

Actual browser light/dark × desktop/phone comparison and complete browser interaction/animation checks remain open. The original Ego TaskSpace18 is missing and the earlier recovery approval question is unanswered; no replacement space was created. Native/source evidence does not close this requirement. Smaller-than-390dp containers and platform-specific rendering are outside this tested viewport matrix.

Skeleton remains unchecked. Full repository commit gate has not been established; no commit or publication is claimed. The next priority page is Table. Editor remains excluded and Attachment residuals remain deferred.
