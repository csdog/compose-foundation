# Independent review

## Findings resolved

1. Popover keyboard opening was expanded to both Enter and Space, each followed
   by Escape and trigger focus restoration.
2. Ghost coverage now uses a scrollable 600dp viewport, scrolls the final
   paragraph into view and asserts non-zero bounds. Browser capture also shows
   all three paragraphs.
3. The missing Tooltip browser state is explicitly documented as a synthetic
   Ego hover limitation with JVM behavior coverage.

## Final review

Reviewer result: **No findings** after the evidence fixes.

The unchanged Bubble component behavior, Space activation, focus goldens,
Reactions pointer/Enter, Popover keyboard states, ghost content and ktlint
attribution are consistent.
