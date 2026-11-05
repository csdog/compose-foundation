# Trace page recipes r2 (2026-11-05)

Rechecked the current [Base UI Trace page](https://ui.shadcn.com/docs/components/base/trace)
at shadcn/ui `8a3536678a528cc012b9e62484290e79d0be410f`. The page defines the same
part composition and fifteen example previews documented in the current MDX.

## Changes

- Replaced the non-official `Expanded` recipe with the official `Collapsible`
  composition and real terminal output in `CodeBlock`.
- Added the official `Description`, `Open State`, and `Group` recipes.
- Covered default-open, controlled-open, and an external Button trigger in the same
  Open State preview.
- Added page tests for recipe order, descriptions, expansion, controlled open state,
  group content, and removal of the `Expanded` alias.

## Verification

The focused suites pass **11 tests**: five page tests and six core Trace behavior and
screenshot tests. Repository-wide ktlint and detekt pass. Wasm and JavaScript
development distributions exit successfully; JavaScript retains the existing
`os`/`path` diagnostics.

## Browser limitation

No fresh browser matrix is claimed. Ego has no owned `compose-foundation` TaskSpace;
the user space and unrelated delegated spaces were not claimed, and no replacement
space was created.

## Remaining differences

The current page also has Link, Dialog, Sheet, Sources, Chain of Thought, Commit,
Whole Turn, Reasoning, and Tool recipes. Those compositions and the full browser
desktop/mobile light/dark, pointer, touch, keyboard, reduced-motion, and
assistive-technology matrices remain open. Trace stays unchecked.
