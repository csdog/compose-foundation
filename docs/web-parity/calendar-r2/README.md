# Calendar r2 — official-first recipe labeling

## Findings

The current official Calendar page has ten recipes:
Default, Basic, Range Calendar, Month and Year Selector, Presets, Date and Time Picker,
Booked dates, Custom Cell Size, Week Numbers, and RTL.

The local page had those ten in order, followed by five Kotlin-only variants:
Multiple, Disabled, Week starts on Monday, Hide outside days, and Month bounds. Those five
were presented as ordinary official recipes.

The documented Persian/Jalali calendar gap and time-zone guidance already match the current
page's support sections.

## Changes

- Preserved the five additional implementations but labeled them
  `Kotlin extension: …`.
- Kept their previews and generated code bound to the original base recipe names, so behavior
  is unchanged.
- Added exact official-first ordering and extension-label regression coverage.

## Verification

The focused suite passed 35 tests:

| Suite | Tests |
|---|---:|
| CalendarExamplesTest | 6 |
| ComponentDocumentationTest | 8 |
| CalendarParityTest | 9 |
| CalendarScreenshotTest | 10 |
| DatePickerScreenshotTest | 2 |

`:app:webApp:wasmJsBrowserDevelopmentExecutableDistribution` passed in 16.701s with the
existing Webpack warning. App Wasm SHA-256:
`bf634f4203db8a417d4d2c97ebd49d1cce4f6a420a4bbfb8f77a40096e812c4d`.

Repository-wide ktlint and detekt passed. Test XML is archived in [tests](tests/).

## Browser status

No owned compose-foundation Ego TaskSpace is available. The only listed spaces belong to the
user or unrelated delegated Product Hunt work, so no replacement space was created and no
user tab was claimed.

## Remaining differences

- Run the ten official recipes at 1440/390 × light/dark against the current build.
- Persian/Jalali calendar conversion remains unsupported and explicitly documented.
- Native time-input behavior, caption details, and the complete assistive-technology and
  touch interaction matrix remain open.
