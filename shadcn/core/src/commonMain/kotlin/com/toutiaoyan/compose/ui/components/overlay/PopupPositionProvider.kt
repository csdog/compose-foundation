package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

/** Preferred side of the anchor an anchored popup should open on. */
enum class PopupPlacement { Top, Bottom, Start, End }

internal data class PopupPositionResult(
    val offset: IntOffset,
    val bounds: IntRect,
    val physicalSide: PopupPlacement,
    val transformOrigin: TransformOrigin,
)

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
    ): IntOffset = calculatePositionResult(anchorBounds, windowSize, layoutDirection, popupContentSize).offset

    internal fun calculatePositionResult(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): PopupPositionResult {
        var x: Int
        var y: Int
        var resolvedSide = placement

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
                    if (flipped >= 0) {
                        y = flipped
                        resolvedSide = PopupPlacement.Top
                    }
                } else if (placement == PopupPlacement.Top && overflowsTop) {
                    val flipped = anchorBounds.bottom + offsetPx
                    if (flipped + popupContentSize.height <= windowSize.height) {
                        y = flipped
                        resolvedSide = PopupPlacement.Bottom
                    }
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
                    if (flipped >= 0) {
                        x = flipped
                        resolvedSide = PopupPlacement.Start
                    }
                } else if (placement == PopupPlacement.Start && overflowsStart) {
                    val flipped = anchorBounds.right + offsetPx
                    if (flipped + popupContentSize.width <= windowSize.width) {
                        x = flipped
                        resolvedSide = PopupPlacement.End
                    }
                }
            }
        }

        // Always clamp within the window on the cross axis, regardless of flip outcome --
        // real shadcn's Radix positioning does the same "collision padding" clamp.
        x = x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
        y = y.coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0))
        val origin =
            when (resolvedSide) {
                PopupPlacement.Top -> Offset(anchorBounds.center.x.toFloat() - x, anchorBounds.top.toFloat() - y)
                PopupPlacement.Bottom -> Offset(anchorBounds.center.x.toFloat() - x, anchorBounds.bottom.toFloat() - y)
                PopupPlacement.Start -> Offset(anchorBounds.left.toFloat() - x, anchorBounds.center.y.toFloat() - y)
                PopupPlacement.End -> Offset(anchorBounds.right.toFloat() - x, anchorBounds.center.y.toFloat() - y)
            }
        return PopupPositionResult(
            offset = IntOffset(x, y),
            bounds = IntRect(IntOffset(x, y), popupContentSize),
            physicalSide = resolvedSide,
            transformOrigin =
                TransformOrigin(
                    if (popupContentSize.width == 0) 0.5f else origin.x / popupContentSize.width,
                    if (popupContentSize.height == 0) 0.5f else origin.y / popupContentSize.height,
                ),
        )
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

/** Context-menu cursor position is absolute: a trigger may be nested or offset inside its root. */
internal class WindowPointPositionProvider(private val point: IntOffset) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = if (point.x + popupContentSize.width <= windowSize.width) point.x else point.x - popupContentSize.width
        val y =
            if (point.y +
                popupContentSize.height <= windowSize.height
            ) {
                point.y
            } else {
                point.y - popupContentSize.height
            }
        return IntOffset(
            x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0)),
            y.coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0)),
        )
    }
}
