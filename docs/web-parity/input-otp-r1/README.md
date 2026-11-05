# Input OTP r1 — current official page, invalid API and a11y semantics

Official reference: <https://ui.shadcn.com/docs/components/base/input-otp>,
upstream revision `8a3536678a528cc012b9e62484290e79d0be410f`, style `base-nova`.
Fetched 2026-11-05; file SHA-256s in `sources.json`.

## Findings

Local page was an API dump: Default, With separator, Disabled, Default disabled,
Pattern, Default pattern, Masked, Default masked, Text type, Default type, Complete,
Read only, Default read only, Max length, Default max length, Slot, Group, Separator.
The current official page has 10 previews: demo, Pattern, Separator, Disabled,
Controlled, Invalid, Four Digits, Alphanumeric, Form, RTL (plus About, Usage,
Composition and an input-otp API link).

Component gaps found:

- No `invalid` state existed (official applies `aria-invalid` per slot).
- The root had no accessibility semantics at all (no name, disabled or error state).

## Changes

- `InputOTPDoc.kt` rewritten to the official 10 recipes/order, official About and
  API link, with Kotlin API tables (`length/maxLength`, `pattern/accept`,
  `masked/type`, `invalid`, `onComplete`).
- `InputOTP.kt`: added `invalid` on the root and on `InputOTPSlot` (resolved as
  slot invalid OR root invalid) with the destructive border/ring, plus root
  `contentDescription` with `disabled()`/`error()` semantics. Existing slot, group,
  separator and paste behaviour unchanged.

## Verification

- `InputOTPExamplesTest` 20 tests (5 methods × 1440/390 × light/dark): official
  order/copy/About/API link, all 10 previews, controlled typing updates, digits
  pattern filters letters, invalid error semantics and disabled semantics.
- Core: `InputOTPScreenshotTest` 7 green.
- Final Wasm build `ace00496e82de7b39bf64c205bd547398e1962626cf568e9cc4b8e9890ff2aeb`.
  Quadrants in `browser/`; states in `browser/states/` (controlled empty/typed,
  disabled, invalid, Four Digits, form card, RTL Arabic).

## Remaining differences (explained)

- `About` is placed by the catalog after Composition instead of directly after the
  demo/Installation slot because the page model has no pre-Installation guide hook.
- The official form recipe styles slots with `h-12 w-11 text-xl`; slots use the
  shared 32dp size (no per-slot size override in the Kotlin API), so the form card
  has default-size slots.
- Invalid uses `invalid = true` (root or per slot) instead of `aria-invalid`; the
  rendered error ring matches the official slot treatment.
- Accessibility now exposes name/disabled/error semantics on the root; per-slot
  announcements, IME composition and platform autofill/one-time-code hints remain
  outside this round's matrix.
- RTL preview uses the shared catalog language toolbar (Hebrew label font fallback);
  the RTL body mirrors the digits and label like the official page.
