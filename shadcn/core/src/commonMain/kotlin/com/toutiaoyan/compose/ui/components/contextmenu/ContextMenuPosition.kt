@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign

/**
 * Base UI `side` values for [ContextMenuScope.ContextMenuContent]. The menu is anchored to the
 * right-click point; `InlineStart` / `InlineEnd` follow [LayoutDirection] like Base UI's
 * `inline-start` / `inline-end` (`right` remains physical, matching the official `side` prop).
 */
enum class ContextMenuSide {
    Top,
    Right,
    Bottom,
    Left,
    InlineStart,
    InlineEnd,
}

/**
 * Positions a context-menu panel around the pointer. Mirrors Base UI's Positioner defaults:
 * `side = right`, `align = start`, flip to the opposite side when the preferred side does not
 * fit, then clamp inside the window. `alignOffset` nudges along the cross axis (the previous
 * implementation hardcoded a +4dp Y nudge for the default right side).
 */
internal class ContextMenuPointPositionProvider(
    private val point: IntOffset,
    private val side: ContextMenuSide,
    private val align: PopupAlign,
    private val sideOffsetPx: Int,
    private val alignOffsetPx: Int,
    /** True when [point] is already in window coordinates (cursor points are). */
    private val absolute: Boolean,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val anchorX = if (absolute) point.x else anchorBounds.left + point.x
        val anchorY = if (absolute) point.y else anchorBounds.top + point.y
        val width = popupContentSize.width
        val height = popupContentSize.height
        val resolvedSide =
            when (side) {
                ContextMenuSide.InlineStart ->
                    if (layoutDirection == LayoutDirection.Rtl) ContextMenuSide.Right else ContextMenuSide.Left
                ContextMenuSide.InlineEnd ->
                    if (layoutDirection == LayoutDirection.Rtl) ContextMenuSide.Left else ContextMenuSide.Right
                else -> side
            }

        fun cross(
            anchor: Int,
            size: Int,
        ): Int =
            when (align) {
                PopupAlign.Start -> anchor + alignOffsetPx
                PopupAlign.Center -> anchor - size / 2 + alignOffsetPx
                PopupAlign.End -> anchor - size - alignOffsetPx
            }
        var x: Int
        var y: Int
        when (resolvedSide) {
            ContextMenuSide.Right, ContextMenuSide.InlineEnd -> {
                x = anchorX + sideOffsetPx
                if (x + width > windowSize.width) x = anchorX - sideOffsetPx - width
                y = cross(anchorY, height)
            }
            ContextMenuSide.Left, ContextMenuSide.InlineStart -> {
                x = anchorX - sideOffsetPx - width
                if (x < 0) x = anchorX + sideOffsetPx
                y = cross(anchorY, height)
            }
            ContextMenuSide.Bottom -> {
                y = anchorY + sideOffsetPx
                if (y + height > windowSize.height) y = anchorY - sideOffsetPx - height
                x = cross(anchorX, width)
            }
            ContextMenuSide.Top -> {
                y = anchorY - sideOffsetPx - height
                if (y < 0) y = anchorY + sideOffsetPx
                x = cross(anchorX, width)
            }
        }
        return IntOffset(
            x.coerceIn(0, (windowSize.width - width).coerceAtLeast(0)),
            y.coerceIn(0, (windowSize.height - height).coerceAtLeast(0)),
        )
    }
}
