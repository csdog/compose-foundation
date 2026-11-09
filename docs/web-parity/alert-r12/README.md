# Alert r12 — current-source completion

- Date: 2026-11-07
- Official revision: `42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9`
- Status: complete

## Findings

The current Alert MDX, `alert.tsx`, six examples, and Nova CSS are byte-identical to
r11's `8a353667…` set. The current `alert.tsx` still exposes `role="alert"`, the two
variants, and the four composition parts. The Nova `.cn-alert*` rules still specify the
same 8dp radius, 10dp/8dp padding, 2dp rows, 16dp icon column, 72dp action reserve, title
weight, destructive description tint, and top-end action placement implemented locally.

## Verification

- Alert component tests: **14/14 passed**.
- Alert catalog tests: **6/6 passed**.
- Current browser capture: official/local at 1440×1000 and 390×844, light/dark.
- Evidence: `browser/`, [`../token-three-components.json`](../token-three-components.json).

## Accepted platform/API differences

- Compose has no native HTML `role` equivalent; the component publishes an assertive live
  region and heading semantics.
- The title/description convenience API accepts text. Rich HTML child content and browser
  link styling are represented through the caller's content where the Compose API permits.
- Shared documentation chrome and font rasterization remain outside component parity.
