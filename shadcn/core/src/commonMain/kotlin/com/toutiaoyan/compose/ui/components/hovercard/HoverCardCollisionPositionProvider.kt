package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.ui.unit.*
import androidx.compose.ui.window.PopupPositionProvider
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import kotlin.math.roundToInt

/** Base UI PreviewCard defaults: 5px collision inset and fallback to the end of the perpendicular axis. */
internal class HoverCardCollisionPositionProvider(
    private val placement: PopupPlacement,
    private val offset: Int,
    private val align: PopupAlign,
    private val alignOffset: Int,
    private val padding: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val a = anchorBounds
        val s = popupContentSize

        fun horizontal(side: PopupPlacement) = side == PopupPlacement.Start || side == PopupPlacement.End

        fun opposite(side: PopupPlacement) =
            when (side) {
                PopupPlacement.Start -> PopupPlacement.End
                PopupPlacement.End -> PopupPlacement.Start
                PopupPlacement.Top -> PopupPlacement.Bottom
                PopupPlacement.Bottom -> PopupPlacement.Top
            }

        fun main(side: PopupPlacement) =
            when (side) {
                PopupPlacement.Start -> a.left - s.width - offset
                PopupPlacement.End -> a.right + offset
                PopupPlacement.Top -> a.top - s.height - offset
                PopupPlacement.Bottom -> a.bottom + offset
            }

        fun overflow(side: PopupPlacement): Int {
            val start = main(side)
            val extent = if (horizontal(side)) s.width else s.height
            val limit = if (horizontal(side)) windowSize.width else windowSize.height
            return (padding - start).coerceAtLeast(0) + (start + extent + padding - limit).coerceAtLeast(0)
        }
        val perpendicular =
            if (horizontal(placement)) {
                listOf(PopupPlacement.Bottom, PopupPlacement.Top)
            } else {
                if (layoutDirection == LayoutDirection.Rtl) {
                    listOf(
                        PopupPlacement.Start,
                        PopupPlacement.End,
                    )
                } else {
                    listOf(PopupPlacement.End, PopupPlacement.Start)
                }
            }
        val candidates = listOf(placement, opposite(placement)) + perpendicular
        val side = candidates.firstOrNull { overflow(it) == 0 } ?: candidates.minBy { overflow(it) }
        val vertical = !horizontal(side)
        val rtl = vertical && layoutDirection == LayoutDirection.Rtl
        val start = if (vertical) a.left else a.top
        val end = if (vertical) a.right else a.bottom
        val size = if (vertical) s.width else s.height
        val cross =
            when (align) {
                PopupAlign.Start -> if (rtl) end - size - alignOffset else start + alignOffset
                PopupAlign.End -> if (rtl) start + alignOffset else end - size - alignOffset
                PopupAlign.Center -> ((start + end - size) / 2f + if (rtl) -alignOffset else alignOffset).roundToInt()
            }
        val point = if (vertical) IntOffset(cross, main(side)) else IntOffset(main(side), cross)
        return IntOffset(
            point.x.coerceIn(padding, (windowSize.width - s.width - padding).coerceAtLeast(padding)),
            point.y.coerceIn(padding, (windowSize.height - s.height - padding).coerceAtLeast(padding)),
        )
    }
}
