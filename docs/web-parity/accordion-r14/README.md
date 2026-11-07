# Accordion r14 - current-page parity checkpoint

## Source pin

The official page was re-read from
`ui.shadcn.com/docs/components/base/accordion` at revision
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX, Base UI wrapper and
seven examples match the nine archived r13 source hashes; the r14 manifest
additionally pins Nova CSS. Exact URLs and SHA-256 hashes are in
[sources.json](sources.json).

The official order is hero preview, Installation, Usage, Composition, Basic,
Multiple, Disabled, Borders, Card, RTL, then API Reference. The local page
retains that order and puts the 31 additional recipes after the official six
under `Kotlin extension:` headings.

## Change

No Accordion component or page renderer change was required. This checkpoint:

- adds a regression assertion for the complete official section order;
- closes the missing quadrant-hash gap and reruns the browser capture;
- adds deterministic validation for every source and captured artifact;
- generates four six-row official/local contact sheets for visual comparison.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Catalog Accordion suites | 63/63 | [log](logs/verify-catalog.log), [XML](tests/) |
| Accordion core suites | 21/21 after forced execution | [log](logs/verify-core.log), [XML](tests/) |
| Artifact validation | 10 sources, 8 quadrants, 48 sections, 17 interactions | [validation.json](validation.json) |
| Wasm distribution | PASS | [log](logs/wasm-build.log); application Wasm SHA-256 `1355c1d598d1b749897e839716f96cd223eb6fae8184765a268246ef86f999cc` |
| Owning ktlint audit | No Accordion finding | [log](logs/ktlint.log); the aggregate catalog test source set retains four unrelated long-line violations |

The focused catalog suites are AccordionExamplesTest 3,
AccordionExtensionTest 32, AccordionPreviewFrameTest 24 and
AccordionRtlPreviewTest 4. The core suites are AccordionNovaTest 15 and
AccordionScreenshotTest 6.

Run the evidence check with:

```bash
node docs/web-parity/accordion-r14/validate.mjs
```

## Browser evidence

The current build loaded `658820f72b4617efb380.wasm` and
`df4c7195f77eef6695dc.wasm`. Four 1440x1000 / 390x844 and light/dark
official/local pairs are indexed in [browser/meta.json](browser/meta.json).
All eight SHA-256 hashes match their PNG files.

Six official sections were captured in all four viewport/theme states,
producing 48 images in [sections/sections.json](sections/sections.json).
Each contact sheet has six rows in Basic, Multiple, Disabled, Borders, Card,
RTL order, with official on the left and local on the right:

- [desktop light](sections/contact-desktop-light.png)
- [desktop dark](sections/contact-desktop-dark.png)
- [phone light](sections/contact-phone-light.png)
- [phone dark](sections/contact-phone-dark.png)

The local interaction sequence records Basic switching from the first to the
second item and back, Multiple opening all three items, Disabled remaining
unchanged after a click, RTL switching Arabic -> English -> Hebrew, horizontal
pointer activation, phone activation and dark mode. The 17 hashes and state
assertions are in
[browser/interactions/interactions.json](browser/interactions/interactions.json).

## Accepted differences

- Shared documentation chrome remains different: the top navigation, Copy Page
  affordance, previous/next controls and TOC treatment are site-level work.
- The local code panel uses its established Compose documentation layout; it is
  not a React source mirror.
- Geist and Arabic/Hebrew fallback-font rasterization still produces small
  glyph and line-wrap differences.
- Compose Canvas does not expose the same native HTML accordion button,
  heading and region roles as Base UI. Keyboard traversal, focus restoration,
  RTL logical arrows and disabled-item behavior are covered by the core tests.
- The full Kotlin extension API/Style matrix, screen-reader output and a real
  touch-device matrix remain open and are not claimed as official parity.

## Review

Independent review and verification are recorded in [review.md](review.md).
