package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

/** Preferred side of the anchor an anchored popup should open on. */
enum class PopupPlacement { Top, Bottom, Start, End }

/** Cross-axis alignment relative to the anchor. Matches Radix `align`. */
enum class PopupAlign { Start, Center, End }

/**
 * Positions a [androidx.compose.ui.window.Popup]'s content relative to its anchor
 * (the trigger element), matching real shadcn/ui's Radix-based `Popover`/`DropdownMenu`/
 * `Tooltip`/etc: prefer [placement], flip to the opposite side if there isn't room, and
 * always clamp within the window so the popup never renders off-screen.
 *
 * This is the one positioning primitive every anchored overlay component
 * (Popover, DropdownMenu, Tooltip, ContextMenu, HoverCard, Combobox, Menubar,
 * NavigationMenu) shares -- see [AnchoredPopup].
 */
class PopupPositionProvider(
    private val placement: PopupPlacement,
    private val offsetPx: Int,
    private val align: PopupAlign = PopupAlign.Start,
    private val alignOffsetPx: Int = 0,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        var x: Int
        var y: Int

        fun crossAxis(
            anchorStart: Int,
            anchorEnd: Int,
            popupSize: Int,
        ): Int {
            val anchorSize = anchorEnd - anchorStart
            return when (align) {
                PopupAlign.Start -> anchorStart + alignOffsetPx
                PopupAlign.Center -> anchorStart + (anchorSize - popupSize) / 2 + alignOffsetPx
                PopupAlign.End -> anchorEnd - popupSize + alignOffsetPx
            }
        }

        when (placement) {
            PopupPlacement.Bottom, PopupPlacement.Top -> {
                x = crossAxis(anchorBounds.left, anchorBounds.right, popupContentSize.width)
                y =
                    if (placement == PopupPlacement.Bottom) {
                        anchorBounds.bottom + offsetPx
                    } else {
                        anchorBounds.top - popupContentSize.height - offsetPx
                    }
                // Flip vertically if there's no room on the preferred side.
                val overflowsBottom = y + popupContentSize.height > windowSize.height
                val overflowsTop = y < 0
                if (placement == PopupPlacement.Bottom && overflowsBottom) {
                    val flipped = anchorBounds.top - popupContentSize.height - offsetPx
                    if (flipped >= 0) y = flipped
                } else if (placement == PopupPlacement.Top && overflowsTop) {
                    val flipped = anchorBounds.bottom + offsetPx
                    if (flipped + popupContentSize.height <= windowSize.height) y = flipped
                }
            }

            PopupPlacement.Start, PopupPlacement.End -> {
                y = crossAxis(anchorBounds.top, anchorBounds.bottom, popupContentSize.height)
                x =
                    if (placement == PopupPlacement.End) {
                        anchorBounds.right + offsetPx
                    } else {
                        anchorBounds.left - popupContentSize.width - offsetPx
                    }
                val overflowsEnd = x + popupContentSize.width > windowSize.width
                val overflowsStart = x < 0
                if (placement == PopupPlacement.End && overflowsEnd) {
                    val flipped = anchorBounds.left - popupContentSize.width - offsetPx
                    if (flipped >= 0) x = flipped
                } else if (placement == PopupPlacement.Start && overflowsStart) {
                    val flipped = anchorBounds.right + offsetPx
                    if (flipped + popupContentSize.width <= windowSize.width) x = flipped
                }
            }
        }

        // Always clamp within the window on the cross axis, regardless of flip outcome --
        // real shadcn's Radix positioning does the same "collision padding" clamp.
        x = x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
        y = y.coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0))
        return IntOffset(x, y)
    }
}

/**
 * Positions a popup's top-left corner at a fixed point relative to the anchor's own
 * top-left (e.g. wherever a right-click landed) -- used by [ContextMenu], which
 * opens at the cursor rather than snapped to one side of a trigger element. Still
 * clamps within the window so a context menu opened near an edge doesn't render
 * off-screen.
 */
class PointPositionProvider(private val pointInAnchor: IntOffset) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left + pointInAnchor.x
        val y = anchorBounds.top + pointInAnchor.y
        return IntOffset(
            x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0)),
            y.coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0)),
        )
    }
}
