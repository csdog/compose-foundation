# Checkbox motion and current-source review (2026-11-05)

Rechecked the current [Base UI Checkbox page](https://ui.shadcn.com/docs/components/base/checkbox).
Current shadcn/ui `main` is `8a3536678a528cc012b9e62484290e79d0be410f`; the MDX,
component, Field source, Nova CSS, and all eight examples are byte-identical to the
existing `checkbox-r1` archive.

## Changes

- Base Checkbox uses `transition-colors` without a transform. The local default
  previously reused Button's scale-down press animation.
- Added `CheckboxDefaults.Animation = None` and a 150ms color-only Style transition.
- Callers can still explicitly request Scale, Bounce, or MoveDown through the existing
  `animation` parameter.
- Documented the color-only default and recorded the affected settled goldens.

## Verification

The final functional suites pass **25 tests**: six core parity checks, four Checkbox
screenshots, three page tests, and twelve viewport tests. Repository-wide ktlint and
detekt pass. Wasm and JavaScript development distributions exit successfully; the
JavaScript build retains the existing `os`/`path` diagnostics.

The light settled screenshot goldens pass pixel verification. The two dark failures
reproduce byte-for-byte from the earlier before/HEAD isolation, so no stale golden was
refreshed; hashes and images are in `legacy-pixel-isolation.json` and
`pixel-isolation/`.

## Browser limitation

No fresh browser matrix is claimed. Ego has no owned `compose-foundation` TaskSpace;
user and unrelated delegated spaces were not opened or claimed, and no replacement
space was created.

## Remaining differences

The existing dark-golden mismatch, exact CSS hit-area pseudo-element, HTML form
`name`/`value`/`required` behavior, native label association, touch matrices, dynamic
theme/shape transitions, and complete assistive-technology mapping remain open.
Checkbox stays unchecked.
