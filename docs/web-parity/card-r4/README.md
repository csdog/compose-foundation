# Card r4 - current-page parity checkpoint

## Source pin

The official page was re-read from
`ui.shadcn.com/docs/components/base/card` at revision
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX, six examples,
Base UI wrapper and Nova CSS are archived with SHA-256 hashes in
[sources.json](sources.json).

The official content order is hero preview, Installation, Usage, Composition,
Size, Spacing with an embedded Edge to Edge preview, Image, RTL, API Reference
and Changelog. The local page keeps that order; the official Edge to Edge
recipe is not mislabeled as an extension. Kotlin-only `CardImage`, `Filled`
and `Elevated` surfaces remain explicitly identified as extensions.

## Change

- Corrected the embedded official recipe title from `Kotlin extension: Edge to
  Edge` to `Edge to Edge` while keeping it visually nested under Spacing.
- Removed the local-only Edge to Edge explanation and retained the official
  negative-offset guidance in the Spacing copy.
- Added a 390dp regression for the Password/Forgot row's measured 2px gap.
- Expanded the page regression to reject the old extension title and verify
  the official Spacing sentence.
- Added the optional post-API guide slot to the shared page model and a
  Kotlin-equivalent Card Changelog / Spacing Variable after API Reference.
  Migration items use ordered steps, so the TOC retains the official
  `Changelog -> Spacing Variable` hierarchy.

No Card layout/component production code required a change.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Card catalog suite | 20/20 | [log](logs/verify-catalog.log), [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.CardExamplesTest.xml) |
| Shared documentation regression | 8/8 | same log, [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.ComponentDocumentationTest.xml) |
| Card core suites | 17/17 | [log](logs/verify-core.log), [XML](tests/) |
| Wasm distribution | PASS | [log](logs/wasm-build.log); application Wasm SHA-256 `ac441aca38fd247b6842973ad06696e5b26a9249a9d462e6207b309fb68d4aab` |
| Artifact validation | 9 sources, 8 quadrants, 64 sections, 6 interactions | [validation.json](validation.json) |
| Owning ktlint audit | No Card finding | [log](logs/ktlint.log); four unrelated pre-existing long lines remain in the aggregate catalog test source set |

Run the deterministic evidence check with:

```bash
node docs/web-parity/card-r4/validate.mjs
```

## Browser evidence

The final `c575ab2dc6c8aee1ddd9.wasm` build produced four official/local
viewport-theme pairs, indexed in [browser/meta.json](browser/meta.json), plus
six recipe pairs and API/Changelog pairs at all four states:

- [desktop light](browser/contact-desktop-light.png)
- [desktop dark](browser/contact-desktop-dark.png)
- [phone light](browser/contact-phone-light.png)
- [phone dark](browser/contact-phone-dark.png)

Each contact sheet has six rows: Default, Size, Spacing, Edge to Edge, Image
and RTL, with official on the left and local on the right.

The API and Changelog pairs are available under
`browser/sections/{official,local}-{api-reference,changelog}-{state}.png`.

The interaction set records 16px -> 32px spacing selection and the RTL
language menu switching Arabic -> English -> Hebrew:
[interactions.json](browser/interactions/interactions.json).

## Accepted differences

- Shared documentation chrome, copy/previous/next controls and code-panel
  presentation are site-level work.
- Geist and Arabic/Hebrew fallback-font rasterization changes natural wrapping
  and glyph positions, while the measured Card widths and spacing match.
- Compose uses its native scrollbar and Canvas ring rasterization for the
  overflow card instead of browser DOM painting.
- The image recipe uses the archived upstream sample PNG with a local color
  filter instead of `next/image`; its 16:9 geometry and content match.
- Compose Canvas does not expose the same HTML container/label associations
  as the React implementation. Card layout, input behavior, focus and RTL
  behavior are covered by JVM tests.

## Review

Independent review and verification are recorded in review.md.
