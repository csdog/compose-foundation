# Table r1 — structure, cell chrome and official data/interaction recipes

Seven pinned upstream files are archived in `sources.json`: Base table, MDX, Nova CSS and Default/Footer/Actions/RTL recipes at revision `96da9cc7f1aa10580a3741664e2a77c721206c00`. The official page was read on 2026-10-31: https://ui.shadcn.com/docs/components/base/table . MDX explicitly targets base-nova. All hashes were checked.

## Changes

Caption now renders at the bottom regardless of declaration order, centered with a real 16dp margin. A pure layout pass orders captions and removes final body/footer dividers; it does not write observable state during measure/composition. Body and Footer form actual groups. Footer gets its own half-muted background and top rule, with medium text.

Header cells are 40dp with horizontal 8dp padding. Body cells use 14/20 text, four-sided 8dp padding and no wrapping. Row content is vertically centered. Rows are transparent over their parent, hover at muted/50, and selected state stays muted even under hover and is exposed in semantics. Physical Left/Right text alignment is distinct from logical End in RTL.

Table owns a horizontal viewport with intrinsic content width. The old manually nested viewport in ApiReference was removed; the actual documentation caller is tested. The existing string APIs and trailing String lambda still work. Custom cells use an explicit `content = { ... }` slot (required first parameter prevents it from intercepting `TableCell { "text" }`). The new slot was initially caught breaking that overload and was fixed before final validation.

Official data/interaction recipes now appear first: seven-invoice Default with footer/caption; three-invoice Footer retaining the source's literal total; three real product menus with Edit/Duplicate/Delete; and English/Arabic/Hebrew RTL text/selection. Source menu items have no data mutation handlers; local items dismiss without deleting products. Previews and copied code derive from the same compiled regions via `scripts/generate_table_recipe_code.py`; regeneration is idempotent. Existing Kotlin API examples remain after these recipes.

**Column layout is still provisional:** the invoices use consistent explicit 100dp invoice/amount cells and two weighted middle columns, with a weighted footer total covering the leading region. This is not the browser's automatic cross-row table sizing or a general colSpan implementation. The data/interaction migration does not close page pixel parity.

## Verification

- Original-runtime RED: ten tests, all fail. Core fixes pass those same assertions.
- Final core: sixteen passing cases for light/dark caption margin/order, cell metrics, transparency/hover, selected hover/semantics, footer top rule/alpha, string and content slots, physical/logical RTL alignment, and horizontal overflow reaching the last cell.
- Final app: twenty-eight passing cases across 1440/390 WindowInfo and light/dark: six source recipe/language states plus actual ApiReference caller. Language tests check translations and actual Invoice/Amount horizontal positions. Actions open the menu, expose all three items, select Delete, and verify dismissal with products retained. Paused-clock exit verification waits 320ms after applying composition because the existing overlay exit lasts 250ms; the earlier 192ms test was shorter than that actual transition.
- Combined **44 tests, zero failures/errors/skips**, core/shared ktlint pass. After exact source isolation/restoration, core16 reused matching verified XML while strengthened app28 ran again. This is not claimed as a second independent full44 runtime.
- Twenty-four native captures reviewed through four original-pixel contact sheets. Final captures are byte-identical to reviewed files. All twelve final source/test/generator/config/caller hashes and twenty-four native hashes were rechecked. Contact order/layout is in `review-sheets/layout.json`.
- Legacy two screenshots both change, none missing; no baselines recorded. Exact original-two-file isolation yields light pass/dark change. The pre-existing dark difference is exactly 1952 pixels (two 976px row rules, RGB38→34); the screenshot height146→162 is introduced this round. Original files/harness hashes, pixel JSON and provenance are archived. Verified PASS light uses the canonical golden explicitly, not a stale generated render.
- JS exits 0 in25.623s with existing unresolved os/path errors; this is not a clean web build. Wasm exits0 in18.397s with one warning. Existing AGP/Text suppression/Gradle10 diagnostics remain.

Native fixtures use a 640dp/390dp desktop scene container with 24dp preview inset (actual table viewport592/342dp), plus simulated WindowInfo1440/390. They are not phone OS/browser evidence. Phone amount columns are clipped initially by the horizontal viewport and can be reached by scrolling; automatic sizing will affect this extent. Actions capture the post-menu state and may show the row currently under the test pointer. Hebrew native recipe height is381px versus372px for English/Arabic; fallback font metrics still need review rather than assuming identical line boxes.

## Remaining work and plan

1. Implement shared automatic column measurement and general spans, then compare exact column distribution/phone scroll extent with HTML. Current RowScope weights are an explicit Kotlin layout policy.
2. Complete header/group semantics, header/custom checkbox padding and slot typography inheritance; review expanded-menu row tone and the source color transition. TableHeader is still a single-row shorthand rather than a general multi-row thead container.
3. Review Hebrew fallback metrics, all existing Kotlin extension examples and source/page structure. Existing twelve extension examples have not received the complete new state matrix.
4. Browser light/dark × desktop/phone and full state/pixel acceptance. Read-only revalidation of original Ego TaskSpace18 again returned `task space not found: 18`; guidev2.2.0 forbids creating a replacement as recovery. The earlier approval question remains unanswered; no replacement space was created.
5. Audit old golden differences for eventual baseline acceptance, and pass the full repository commit gate before a checkpoint commit. No full gate or commit is claimed here.

Table remains unchecked. Continue the priority list with the next unstarted page (Tabs), keeping these differences open for the follow-up pass. Editor remains excluded; Attachment residuals remain deferred.
