package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import kotlin.test.*

class HoverCardPositioningTest {
    private fun position(
        side: PopupPlacement,
        anchor: IntRect,
        window: IntSize = IntSize(375, 1000),
        align: PopupAlign = PopupAlign.Center,
        direction: LayoutDirection = LayoutDirection.Ltr,
    ): IntOffset =
        HoverCardCollisionPositionProvider(
            side,
            4,
            align,
            4,
            5,
        ).calculatePosition(anchor, window, direction, IntSize(256, 84))

    @Test fun horizontalBothSidesUnavailableFallsBackBelow() {
        assertEquals(IntOffset(5, 336), position(PopupPlacement.Start, IntRect(65, 300, 113, 332)))
    }

    @Test fun animationOriginFollowsTheAnchorAfterHorizontalFallbackAndClamping() {
        val result =
            HoverCardCollisionPositionProvider(PopupPlacement.Start, 4, PopupAlign.Center, 4, 5)
                .calculatePositionResult(
                    IntRect(65, 300, 113, 332),
                    IntSize(375, 1000),
                    LayoutDirection.Ltr,
                    IntSize(256, 84),
                )
        assertEquals(PopupPlacement.Bottom, result.physicalSide)
        assertEquals(IntRect(5, 336, 261, 420), result.bounds)
        assertEquals(TransformOrigin(84f / 256f, -4f / 84f), result.transformOrigin)
    }

    @Test fun horizontalAnimationOriginKeepsTheOffsetAndCrossAxisAlignment() {
        val result =
            HoverCardCollisionPositionProvider(PopupPlacement.Start, 4, PopupAlign.Center, 4, 5)
                .calculatePositionResult(
                    IntRect(590, 516, 638, 548),
                    IntSize(1425, 1000),
                    LayoutDirection.Ltr,
                    IntSize(256, 84),
                )
        assertEquals(IntRect(330, 494, 586, 578), result.bounds)
        assertEquals(TransformOrigin(260f / 256f, 38f / 84f), result.transformOrigin)
    }

    @Test fun animationOriginTracksTheTriggerAcrossAVerticalFlip() {
        val result =
            HoverCardCollisionPositionProvider(PopupPlacement.Bottom, 4, PopupAlign.Center, 4, 5)
                .calculatePositionResult(
                    IntRect(100, 150, 148, 182),
                    IntSize(375, 200),
                    LayoutDirection.Ltr,
                    IntSize(256, 84),
                )
        assertEquals(PopupPlacement.Top, result.physicalSide)
        assertEquals(TransformOrigin(119f / 256f, 88f / 84f), result.transformOrigin)
    }

    @Test fun fullWidthAnimationOriginUsesTheDrawablePopupBounds() {
        val result =
            HoverCardCollisionPositionProvider(PopupPlacement.Bottom, 4, PopupAlign.Center, 4, 5)
                .calculatePositionResult(
                    IntRect(100, 150, 148, 182),
                    IntSize(256, 500),
                    LayoutDirection.Ltr,
                    IntSize(256, 84),
                )
        assertEquals(IntRect(0, 186, 256, 270), result.bounds)
        assertEquals(TransformOrigin(124f / 256f, -4f / 84f), result.transformOrigin)
    }

    @Test fun crossAxisRespectsFivePixelViewportInset() {
        assertEquals(IntOffset(114, 336), position(PopupPlacement.Bottom, IntRect(300, 300, 350, 332)))
    }

    @Test fun horizontalPlacementIsRetainedWhenThereIsRoom() {
        assertEquals(
            IntOffset(140, 278),
            position(PopupPlacement.Start, IntRect(400, 300, 448, 332), IntSize(800, 1000)),
        )
    }

    @Test fun topCollisionFlipsBelow() {
        assertEquals(IntOffset(5, 46), position(PopupPlacement.Top, IntRect(100, 10, 148, 42)))
    }

    @Test fun bottomCollisionFlipsAbove() {
        assertEquals(IntOffset(5, 62), position(PopupPlacement.Bottom, IntRect(100, 150, 148, 182), IntSize(375, 200)))
    }

    @Test fun rtlStartEndAndCenterOffsetsFollowTheLogicalCrossAxis() {
        val anchor = IntRect(300, 300, 350, 332)
        val window = IntSize(800, 1000)
        assertEquals(
            IntOffset(90, 336),
            position(PopupPlacement.Bottom, anchor, window, PopupAlign.Start, LayoutDirection.Rtl),
        )
        assertEquals(
            IntOffset(304, 336),
            position(PopupPlacement.Bottom, anchor, window, PopupAlign.End, LayoutDirection.Rtl),
        )
        assertEquals(
            IntOffset(193, 336),
            position(PopupPlacement.Bottom, anchor, window, PopupAlign.Center, LayoutDirection.Rtl),
        )
    }

    @Test fun explicitTriggerAnchorRetainsPlatformInsetNormalization() {
        val anchor =
            HoverCardAnchor().apply {
                root = IntRect(20, 24, 260, 264)
                trigger = IntRect(70, 40, 100, 72)
            }
        val provider =
            HoverCardPositionProvider(
                anchor,
                HoverCardCollisionPositionProvider(PopupPlacement.Bottom, 4, PopupAlign.Center, 4, 5),
            )
        assertEquals(
            IntOffset(19, 52),
            provider.calculatePosition(
                IntRect(0, 0, 240, 240),
                IntSize(400, 600),
                LayoutDirection.Ltr,
                IntSize(100, 40),
            ),
        )
    }
}
