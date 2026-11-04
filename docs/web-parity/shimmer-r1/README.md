# Shimmer, round 1

Status: historical round1 checkpoint. Later currentColor/CSS color and recipe work is recorded in [round2](../shimmer-r2/README.md); full browser/utility parity remains incomplete.

[Eleven official sources](sources.json) are pinned to shadcn revision `96da9cc7f1aa10580a3741664e2a77c721206c00`: MDX, nine Base recipes, and the package's Tailwind CSS. The CSS comes from the same revision archived during Scroll Fade. Direct web text fetch of the live utilities page failed; this source review and the native captures are not live-browser acceptance.

## Findings and implementation

The existing Modifier and Style forms both purported to implement shimmer. The Style form produced a `contentBrush`, but Text did not actually paint that brush. Five initial RED pixel tests proved absent animation, ignored plain/annotated and ancestor brushes, and incorrect translucent fill behavior.

Two causes were verified in Compose1.12.2 source. Text appended its explicit solid color on the same Foundation Style node, whose `contentColor` setter clears `contentBrush`. In addition, BasicText's existing color bridge did not paint Style brushes. Text now keeps the explicit solid-color fallback on a nearer separate Style node. This preserves its previous color precedence without deleting a brush on the outer node. The draw bridge resolves the nearest brush, isolates the glyph subtree with saveLayer only when needed, and applies the brush with SrcIn. Whitespace, surrounding backgrounds and siblings are not painted. The original solid text color's alpha becomes an opaque glyph mask while the brush is active, so fill alpha is not multiplied twice. Ordinary text takes the direct draw path.

After painting worked, a separate performance test found that the Style caller's composition count rose from4 to64 during the sampled clock interval. The existing Modifier already kept its caller stable; its own composition-phase state read was not evidence of caller recomposition. The animation helper now returns State. Modifier reads it during drawing, and a remembered Style reads it in its own paint observation scope, creating a new shader brush per resolved frame to avoid stale size-cached shaders. Both paths change pixels while their callers stay stable.

The docs now use the official title/description and reference the real TextColors.Muted API in copied code and KDoc. No missing official recipe is claimed as migrated.

## Progress and remaining source differences

| Area | Current status |
| --- | --- |
| Style brush painting | Fixed for tested plain/annotated text, ancestor brush, selection container, transparent fill and dynamic removal |
| Animation reads | Fixed and measured with real pixels and caller composition counters |
| Disabled and once | Native Style disable restores solid color; once animates then retains its final frame |
| Existing text colors | Explicit solid color precedence and existing ancestor-color updates covered |
| Default currentColor highlight | Still different: implementation uses white overlay-derived highlight and muted base, while CSS derives from currentColor; dark uses relative OKLCH lightness/alpha |
| Explicit color and mixing | Source CSS uses alpha and OKLCH color-mix; current resolveShimmerPeak composites to an opaque peak; still pending |
| Default spread |64dp approximation remains; CSS uses3ch+40px from actual font metrics |
| Arbitrary spread | Length/percentage CSS equivalence still pending; native API currently uses Dp |
| Reduced motion | Automatic CSS preference equivalence still pending; explicit enabled=false is not a substitute |
| Geometry and direction | Existing20°/reverse/RTL implementation retained; full angles, percentages, clock boundaries and compiled CSS specificity matrix pending |
| Recipes | Default, With Marker, Color, Duration, Spread, Angle, Once/Replay, None/md and RTL still need full source migration and responsive checks |
| Glyph rendering | Simple annotated and selection cases pass; colored/transparent spans, inlineContent, decoration, emoji, custom brushes, fonts/scaling and clipping need full matrix |
| Browser | Desktop/mobile × light/dark and interaction/reduced-motion states pending |

## Evidence scope

Native tests freeze the clock before composition and compare actual pixels. Six cropped text captures show initial/middle light/dark, disabled red restoration, and settled once. These are native evidence, not approved visual-parity goldens. No screenshot baseline was recorded.

Legacy Shimmer/Text verification reports4PASS and2CHANGED. To attribute changes, the parent temporarily installed exact pre-pass Text and Shimmer sources, then verified only the two changed methods. Both actual PNGs were byte-identical to the current implementation's actual PNGs. Thus these differences predate this round, rather than establishing a regression introduced by this bridge/state-read repair. Text inheritance dark differs mainly in existing background/border colors; Shimmer light differs slightly in glyph colors. Their original historical cause remains unassigned. Current green source bytes were restored and checked; only KDoc changed afterward.

The original Ego TaskSpace18 was lost. The existing replacement authorization question remains unanswered, so no new space was created. The goal stays active while native/source work remains available. Editor stays excluded and Attachment's recorded remaining gaps remain deferred.

## Validation checkpoint

[Compact summary](verification/summary.json):18 independent tests pass (12new Shimmer,1existing TextColor,5existing AttachmentState), core/shared ktlint pass. Final restoration validation reused the same passing test artifacts after exact green source restoration and a KDoc-only edit; it was not a new runtime execution. JS exits0 in45.731s with known os/path diagnostics, and Wasm exits0 in24.917s with one webpack warning. All11 source/config/test hashes and6 reviewed native PNG hashes matched at parent archiving. The workflow was finished and raw logs deleted.

RED/GREEN, the4→64 caller-composition counter failure, legacy verification and exact pre-pass isolation are preserved under verification. No baseline was recorded. Version0.3.27; no full-repository commit gate or browser acceptance.
