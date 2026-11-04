# Sheet, round 1

Status: historical round1 checkpoint. Subsequent chrome, layout and recipe work is recorded in [round2](../sheet-r2/README.md). Neither checkpoint represents completed browser acceptance.

Reference: [Base UI Sheet](https://ui.shadcn.com/docs/components/base/sheet), checked 2026-10-31. The seven archived files in `sources.json` are pinned to shadcn revision `96da9cc7f1aa10580a3741664e2a77c721206c00`; all seven hashes were rechecked before editing. Source recipes: Default, Side, No Close Button and RTL. Nova CSS is the visual target; the live MDX's Rhea presentation is not interchangeable with Nova styling.

## Implementation plan and progress

| Area | Source contract | Current round |
| --- | --- | --- |
| Side width | 75% viewport; 384px cap from 640px | Implemented, including caller width override |
| Direction | Physical top/right/bottom/left; right default | Added Left/Right; retained logical Start/End extensions, mirrored in RTL |
| Motion | 40px offset + opacity; 200ms ease-in-out | Implemented; SheetContent converts 40dp through density |
| Scrim | Black at 10%, 150ms opacity; supported backdrop blur | Black/opacity/timing implemented; existing Nova overlay-host blur reused; browser effect acceptance pending |
| Focus | First tabbable; return to trigger | Shared overlay focus owner connected; direct string Button avoids nested clickable trigger; keyboard tests added |
| Panel chrome | Popover background, inward border only, shadow-lg, no root padding | Still pending; legacy all-edge border/background/padding remain |
| Header/title/description | Padding16/gap2; title16/24 medium; description14/20 | Still pending |
| Footer | Column, gap8, padding16, auto top margin | Still pending; existing Row API requires migration with weighted body handling |
| Close button | Ghost icon-sm28, physical top/right12 | Still pending; legacy sizing/position remain |
| Default recipe | Edit profile form, body flex1, Save/Close footer | Still pending migration |
| Side recipe | Four physical directions; long body; vertical panels max50vh | Still pending migration |
| No Close Button recipe | Exact source copy, outside dismissal | Component behavior tested; recipe migration pending |
| RTL recipe | Arabic/English/Hebrew copy, physical side chosen by recipe | Still pending migration |
| Browser | Desktop/mobile × light/dark, interaction states | Pending; original Ego TaskSpace18 lost and replacement approval unanswered |
| Native evidence | Width, direction, motion, focus and dismissal | See verification artifacts; native Popup scene is1024×768, not a phone browser |

## Scope of evidence

The 390/639/640/1440 width checks override `LocalWindowInfo` inside a real1024×768 JVM test scene. They establish responsive width calculation and native layout, not mobile viewport position, safe areas, browser zoom, viewport units or browser rendering. Four native captures show only the panel and its existing unfinished chrome. They are not approved screenshot goldens or a claim of visual parity.

The animation check advances a real Compose animation clock, checks an intermediate displacement and faded pixel, and checks removal after200ms. Its direct transition fixture runs at density1. Logical RTL animation direction is resolved in SheetContent; density/font-scale and all four directions still need a complete motion matrix. Keyboard tests cover controlled dismissal refusal and focus handoff/return; touch-specific initial focus, trigger-less previous-focus restoration, assistive technology, nested sheets, non-modal interaction and dynamic content remain open.

Existing Sheet screenshot goldens must not be blanket-recorded to hide changes. Source-backed intentional differences need parent review and a separate verify pass before any baseline update. Other component changes and Attachment's deferred gaps remain outside this round.

## Current validation checkpoint

[Compact summary](verification/summary.json): 15 native tests and core ktlint pass; shared JVM caller compilation passes. JS webpack exits0 with existing unresolved os/path diagnostics, and Wasm exits0 with one webpack warning. Six source/configuration hashes and four reviewed native capture hashes matched at archiving. Workflow finished and temporary raw logs were deleted. No full-repository commit gate was run and no screenshot goldens were updated.
