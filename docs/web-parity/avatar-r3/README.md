# Avatar r3 — current-page browser re-verification

## Sources

Current official page re-read at upstream revision
`8a3536678a528cc012b9e62484290e79d0be410f` (MDX, ten examples, `avatar.tsx`; checksums
in [sources.json](sources.json)). Official order: hero preview, Basic, Badge, Badge with
Icon, Avatar Group, Avatar Group Count, Avatar Group with Icon, Sizes, Dropdown, RTL, then
the API Reference tables. The local page keeps those ten in order plus
`Kotlin extension: Fallback`.

## Changes

- Badge, Avatar Group, Avatar Group Count and Dropdown descriptions now use the official
  copy (previously “A group of avatars with overlapping styling.”, “Add a count to the
  group.”, “Use the Avatar as a trigger …” and a reworded Badge paragraph).
- The Badge example now keeps the real photo in colour (`grayscale = false`), matching the
  official `avatar-badge.tsx`; the other examples keep the official grayscale treatment
  (demo first avatar + groups + sizes + Badge with Icon, dropdown in colour).
- The RTL description now uses the official “To enable RTL support in shadcn/ui, see the
  RTL configuration guide.” sentence.
- New pixel test `badgeExampleKeepsThePhotoInColorLikeTheOfficialExample` guards the
  colour photo; copy assertions for the four descriptions and the RTL text were added to
  `AvatarExamplesTest`. The Dropdown interaction test now follows the shared pointer-open
  behaviour from commit `000feffb` (no row highlighted on pointer open, first ArrowDown
  focuses row 1, second ArrowDown focuses row 2, Escape closes).

## Verification

Final build Wasm SHA-256
`af0897ba8dcd1ca0ba6ea8ace92d531d0a7a68b6aa02253393db7e8c3dc60285`.

Focused tests on final sources: `AvatarExamplesTest` 5 (one new pixel test) and
`ComponentDocumentationTest` 8 in the shared run; `AvatarParityTest` 5 and
`AvatarScreenshotTest` 8 in `:shadcn:core:jvmTest`. XML in [tests](tests/).

Browser evidence after the final build: [browser](browser/) quadrants
(pixel-verified light/dark), nine official section pairs in [sections](sections/), and
[browser/interactions](browser/interactions/):

- `local-dropdown-default`, `local-dropdown-open`, `local-dropdown-first-focus`,
  `local-dropdown-second-focus`, `local-dropdown-escape-closed`, `local-dropdown-open-dark`
  — the avatar trigger opens the grouped menu (Profile/Billing/Settings + Log out), pointer
  open leaves rows unhighlighted, ArrowDown walks the rows and Escape closes.
- `local-rtl-arabic`, `local-rtl-language-popup`, `local-rtl-english`,
  `local-rtl-hebrew` — the RTL toolbar switches Arabic/English/Hebrew; the count becomes
  `+٣` in Arabic and the badge/group order mirrors.

## Remaining differences

- Mixed-size group counting, mixed-image border treatment and the complete menu
  focus/pixel matrix remain open from r1/r2.
- Native assistive semantics/AT mapping for the avatar image and badge are not verified.
- Hero/group spacing follows the local preview padding; fine pixel geometry vs the
  official `gap-6 md:gap-12` row is within the documented preview-padding difference.
- Shared page chrome and Geist rasterization differences are unchanged.
