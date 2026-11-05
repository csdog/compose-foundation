package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import kotlin.test.Test
import kotlin.test.assertEquals

class ContextMenuPositionTest {
    private fun provider(
        point: IntOffset,
        side: ContextMenuSide = ContextMenuSide.Right,
        align: PopupAlign = PopupAlign.Start,
        sideOffset: Int = 0,
        alignOffset: Int = 4,
        absolute: Boolean = true,
    ) = ContextMenuPointPositionProvider(
        point = point,
        side = side,
        align = align,
        sideOffsetPx = sideOffset,
        alignOffsetPx = alignOffset,
        absolute = absolute,
    )

    private fun ContextMenuPointPositionProvider.position(
        window: IntSize = IntSize(1440, 1000),
        popup: IntSize = IntSize(144, 200),
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        anchor: IntRect = IntRect.Zero,
    ) = calculatePosition(anchor, window, layoutDirection, popup)

    @Test fun defaultRightSidePlacesPanelBelowTheCursorAndKeepsAlignOffset() {
        assertEquals(IntOffset(100, 104), provider(IntOffset(100, 100)).position())
    }

    @Test fun rightSideFlipsToLeftWhenItWouldOverflowTheWindow() {
        assertEquals(IntOffset(1300 - 144, 104), provider(IntOffset(1300, 100)).position())
    }

    @Test fun leftAndTopSidesPlaceThePanelBeforeTheCursorAndFlipAtTheEdge() {
        assertEquals(
            IntOffset(500 - 144, 104),
            provider(IntOffset(500, 100), side = ContextMenuSide.Left).position(),
        )
        assertEquals(
            IntOffset(504, 500 - 200),
            provider(IntOffset(500, 500), side = ContextMenuSide.Top).position(),
        )
        assertEquals(
            IntOffset(504, 10 + 0),
            provider(IntOffset(500, 10), side = ContextMenuSide.Top).position(),
        )
    }

    @Test fun bottomSideFlipsToTopWhenItWouldOverflowTheWindow() {
        assertEquals(
            IntOffset(504, 900 - 200),
            provider(IntOffset(500, 900), side = ContextMenuSide.Bottom).position(),
        )
    }

    @Test fun inlineSidesFollowLayoutDirection() {
        assertEquals(
            IntOffset(356, 104),
            provider(IntOffset(500, 100), side = ContextMenuSide.InlineStart).position(layoutDirection = LayoutDirection.Ltr),
        )
        assertEquals(
            IntOffset(500, 104),
            provider(IntOffset(500, 100), side = ContextMenuSide.InlineStart).position(layoutDirection = LayoutDirection.Rtl),
        )
        assertEquals(
            IntOffset(500, 104),
            provider(IntOffset(500, 100), side = ContextMenuSide.InlineEnd).position(layoutDirection = LayoutDirection.Ltr),
        )
        assertEquals(
            IntOffset(356, 104),
            provider(IntOffset(500, 100), side = ContextMenuSide.InlineEnd).position(layoutDirection = LayoutDirection.Rtl),
        )
    }

    @Test fun alignEndPinsThePanelEndToTheAnchor() {
        assertEquals(
            IntOffset(100, 300 - 200 - 4),
            provider(IntOffset(100, 300), align = PopupAlign.End).position(),
        )
    }

    @Test fun sideOffsetMovesThePanelAwayFromTheCursor() {
        assertEquals(IntOffset(106, 104), provider(IntOffset(100, 100), sideOffset = 6).position())
    }

    @Test fun nonAbsolutePointsAreRelativeToTheAnchorBounds() {
        assertEquals(
            IntOffset(20 + 100, 30 + 104),
            provider(IntOffset(100, 100), absolute = false).position(anchor = IntRect(20, 30, 400, 300)),
        )
    }
}
