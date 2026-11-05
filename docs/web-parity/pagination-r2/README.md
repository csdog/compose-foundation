# Pagination — Web parity r2 (2026-11-05)

Official: [ui.shadcn.com/docs/components/base/pagination](https://ui.shadcn.com/docs/components/base/pagination),
Base UI + base-nova, revision `8a3536678a528cc012b9e62484290e79d0be410f` (`sources.json`).

## Official page reading

- Unnamed demo: Previous, 1, active 2, 3, ellipsis, Next.
- Sections: Installation, Usage, Composition, Simple, Icons Only, Next.js (prose), RTL,
  Changelog / RTL Support (historical prose).
- Simple: five page numbers with 2 active. Icons Only: Rows-per-page `Field` + `Select` (10/25/50/100)
  with Previous/Next only. RTL: Arabic/Hebrew labels and Arabic numerals, arrows mirror in RTL.

## Findings and changes

The four official recipes were already implemented (Nova 32dp/2dp spacing, responsive Previous/Next
text, RTL arrows, disabled/ellipsis semantics). This round aligned the page:

- Added the official descriptions for Simple, Icons Only and RTL.
- Hid the generic "Examples" heading so the four recipes are direct sections like the official page.
- Curated the Kotlin-only extras down to `Kotlin extension: Controlled`, `Kotlin extension: First
  and last`, `Kotlin extension: Ellipsis` (dropped the "(Kotlin)" suffix and the rest of the
  26-example matrix; kept the API table and the navigation-callback Kotlin note).
- Test extended to assert the official descriptions and the curated extension list.

## Verification

- `PaginationExamplesTest` 20/20 pass across 1440/390 × light/dark (`tests/`), covering the static
  default composition, Simple, Icons Only rows selector with 25 default, RTL Arabic digits and
  language switching.
- Wasm Web build `wasm=c0570519f21493ae932505507aeb4d8130a06b13be91215ebede3b58db1710cc`;
  four quadrants captured after the final build and reviewed (official theme re-asserted before
  capture).
- Browser states: Icons Only rows-per-page select open with 25 checked
  (`browser/desktop-dark-icons-only-select-open.png`), RTL section with Arabic numerals and
  mirrored arrows (`browser/desktop-dark-rtl-section.png`).

## Remaining differences (open, from r1)

- The Next.js section and Changelog/RTL-Support prose are omitted (Next.js `Link` is JS-only and
  the changelog is historical).
- Pagination controls are Compose buttons, not real `<a>` anchors; href/render integration has no
  Kotlin equivalent.
- Two old Vega screenshot mismatches remain (kept, not re-recorded). Pagination stays unticked.
