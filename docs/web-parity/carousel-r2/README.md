# Carousel current-page review (2026-11-05)

Compared the current [Base UI Carousel page](https://ui.shadcn.com/docs/components/base/carousel)
with the pinned [Carousel first review](../carousel-r1/sources.json). Current shadcn/ui
`main` is `8a3536678a528cc012b9e62484290e79d0be410f`; the component, MDX, and all seven
official examples are byte-identical to the first review's sources.

The official page has Default, Sizes, Spacing, Orientation, API, Plugins, and RTL.
The local catalog keeps those seven official recipes first and labels pagination dots
as a Kotlin extension.

## Changes

- Fixed the plugin recipe so autoplay pauses while hovered or dragged and resumes
  when both states end, matching the official hover stop/reset lifecycle.
- Routed every generated and rendered page slot through the public `CarouselItem`
  component instead of an unrelated `Box`.
- Documented that Compose has no direct `aria-roledescription`; callers should supply
  a slide-specific content description on `CarouselItem`.
- Labeled `With dot indicators` as `Kotlin extension: With dot indicators`.

## Verification

The focused suites pass **24 tests**: seven page recipes and boundaries, five core
behavior tests, and twelve screenshot fixtures. Repository-wide ktlint and detekt
pass. Wasm and JavaScript development distributions exit successfully; the JavaScript
build retains its existing unresolved `os`/`path` diagnostics.

## Browser limitation

No fresh browser matrix is claimed. Ego has no owned `compose-foundation` TaskSpace;
the user space and unrelated delegated spaces were not claimed, and no replacement
space was created.

## Remaining differences

Embla's full option object, arbitrary plugin stacks, and loop mode do not map directly
to Compose Pager. The Kotlin API exposes native equivalents such as `PageSize`,
`pageSpacing`, `userScrollEnabled`, and `CarouselAutoplay`, while unsupported Embla
configuration remains explicit. Browser drag/hover behavior, dynamic theme/font
changes, arbitrary host sizes, full focus/assistive-technology mapping, and all state
combinations remain open. Carousel stays unchecked.
