# Card current-source review (2026-11-05)

Compared the current [Base UI Card page](https://ui.shadcn.com/docs/components/base/card)
with the archived [first Card review](../card-r1/README.md). Current shadcn/ui `main`
is `8a3536678a528cc012b9e62484290e79d0be410f`; the only commit since the first review
changed `apps/v4/registry/directory.json`. All nine archived upstream files and the
event image are therefore byte-identical to the already archived and hashed `card-r1`
files.

The page still has Default, Size, Spacing (including Edge to Edge), Image, and RTL.
The API Reference still contains `Card`, `CardHeader`, `CardTitle`, `CardDescription`,
`CardAction`, `CardContent`, and `CardFooter`; there is no official `CardImage` API.

## Changes

- Aligned the local `CardHeader`, `CardDescription`, `CardAction`, and `CardContent`
  descriptions with the current upstream wording where it is useful in Compose.
- Explicitly labeled the local `CardImage` API section as a Kotlin extension and
  described it as Kotlin-only.
- Added page-export assertions for the CardAction semantics and the `CardImage`
  extension boundary.

No component behavior changed. This review confirms the current upstream snapshot
does not introduce a new Card source or recipe, so the existing spacing, image,
Edge-to-Edge, RTL, and interaction implementation remains applicable.

## Verification

The focused page and core suites pass **37 tests**: `CardExamplesTest` 20,
`CardNovaParityTest` 5, `CardSpacingContractTest` 5, and `CardScreenshotTest` 7.
The exact XML files are in `tests/`; `summary.json` records the source revision,
test totals, repository-wide checks, and the Wasm build.

## Browser limitation

No fresh browser matrix was claimed. Ego still has no owned `compose-foundation`
TaskSpace; existing user and unrelated delegated spaces were not claimed, and no
replacement space was created.

## Remaining differences

The open items from `card-r1` remain: native HTML scrollbar appearance and the 1px
border layout effect, font/weight and line-wrap sampling, shared document/code-preview
geometry, browser keyboard bridging for Select, and the complete link, focus,
invalid, disabled, touch, and assistive-technology matrices. Card stays unchecked.
