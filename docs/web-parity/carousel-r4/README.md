# Carousel r4 - interleaved official section order

## Source pin

The official page was re-read from
`ui.shadcn.com/docs/components/base/carousel` at revision
`42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`. The MDX, seven examples,
Base UI wrapper and Nova CSS are archived with SHA-256 hashes in
[sources.json](sources.json).

## Change

The shared documentation model now supports:

- introduction sections immediately after the hero preview and before
  Installation;
- guide sections immediately before a named example.

Carousel uses these slots to reproduce the official order: About before
Installation, Options before API, and Events before Plugins. The Kotlin-only
keyboard section is explicitly prefixed as an extension.

## Verification

| Check | Result | Evidence |
| --- | --- | --- |
| Carousel catalog suite | 8/8 | [log](logs/verify-catalog.log), [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.CarouselExamplesTest.xml) |
| Shared documentation regression | 8/8 | same log, [XML](tests/TEST-com.toutiaoyan.compose.ui.catalog.ComponentDocumentationTest.xml) |
| Carousel core suites | 17/17 | [log](logs/verify-core.log), [XML](tests/) |
| Wasm distribution | PASS | [log](logs/wasm-build.log); application Wasm SHA-256 `9f3c798e7efa3c253701849ba080311d44ea15e3a8c44224a50a4b93ca3a9458` |
| Artifact validation | 10 sources, 8 quadrants, 88 sections, 7 interactions | [validation.json](validation.json) |
| Owning ktlint audit | No Carousel finding | [log](logs/ktlint.log); four unrelated existing long lines remain in the aggregate catalog test source set |

Run the deterministic evidence check with:

```bash
node docs/web-parity/carousel-r4/validate.mjs
```

## Browser evidence

The final `a0632a7824f765b591ee.wasm` build produced the four
official/local viewport-theme pairs in [browser/meta.json](browser/meta.json).
The 88 section captures cover all seven recipes plus About, Options, Events
and API Reference in every viewport/theme state.

Seven-row recipe contact sheets, official left and local right:

- [desktop light](browser/contact-desktop-light.png)
- [desktop dark](browser/contact-desktop-dark.png)
- [phone light](browser/contact-phone-light.png)
- [phone dark](browser/contact-phone-dark.png)

Interactions in
[interactions.json](browser/interactions/interactions.json) verify
1/2/3 -> 2/3/4 navigation, drag advancement, the two-second autoplay tick,
and Arabic direction moving from ١ to ٢ through the logical next control.

## Accepted differences

- The Kotlin implementation uses Compose Pager instead of Embla. Arbitrary
  Embla option/plugin objects and inertia tuning are not exposed by this API;
  the page states that boundary directly.
- Shared documentation chrome and Geist/Arabic glyph rasterization remain
  site-level work.
- Compose Canvas does not expose the same DOM roles or aria-roledescription as
  the React component. Slide descriptions, keyboard navigation and control
  boundaries are covered by JVM tests.
- The pagination-dot recipe and the keyboard/accessibility guide are
  Kotlin-only extensions and are visibly labeled as such.

## Review

Independent review and verification are recorded in review.md.
