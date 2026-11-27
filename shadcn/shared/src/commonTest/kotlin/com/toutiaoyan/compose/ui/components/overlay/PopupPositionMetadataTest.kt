package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.*
import kotlin.test.*

class PopupPositionMetadataTest {
    private val window = IntSize(800, 600)

    @Test fun flippedPhysicalSidesAndOriginsUseTheFinalBounds() {
        val top =
            PopupPositionProvider(PopupPlacement.Bottom, 4).calculatePositionResult(
                IntRect(100, 550, 180, 580),
                window,
                LayoutDirection.Ltr,
                IntSize(120, 100),
            )
        assertEquals(IntOffset(100, 446), top.offset)
        assertEquals(IntRect(100, 446, 220, 546), top.bounds)
        assertEquals(PopupPlacement.Top, top.physicalSide)
        assertEquals(40f / 120f, top.transformOrigin.pivotFractionX, absoluteTolerance = 1e-6f)
        assertEquals(104f / 100f, top.transformOrigin.pivotFractionY, absoluteTolerance = 1e-6f)

        val left =
            PopupPositionProvider(PopupPlacement.End, 4).calculatePositionResult(
                IntRect(700, 100, 780, 130),
                window,
                LayoutDirection.Rtl,
                IntSize(150, 40),
            )
        assertEquals(IntOffset(546, 100), left.offset)
        assertEquals(PopupPlacement.Start, left.physicalSide)
        assertEquals(154f / 150f, left.transformOrigin.pivotFractionX, absoluteTolerance = 1e-6f)
        assertEquals(15f / 40f, left.transformOrigin.pivotFractionY, absoluteTolerance = 1e-6f)
    }

    @Test fun oddCenterClampZeroAndBothOverflowRetainTheOldPositionArithmetic() {
        data class Case(
            val anchor: IntRect,
            val size: IntSize,
            val align: PopupAlign,
            val expected: IntOffset,
        )
        val cases =
            listOf(
                Case(IntRect(100, 100, 181, 131), IntSize(40, 60), PopupAlign.Center, IntOffset(124, 135)),
                Case(IntRect(-50, -50, -10, -20), IntSize(120, 60), PopupAlign.Start, IntOffset(0, 0)),
                Case(IntRect(750, 100, 790, 130), IntSize(120, 60), PopupAlign.Start, IntOffset(680, 134)),
                Case(IntRect(100, 250, 180, 280), IntSize(900, 700), PopupAlign.Center, IntOffset(0, 0)),
                Case(IntRect(100, 100, 180, 130), IntSize.Zero, PopupAlign.Start, IntOffset(104, 134)),
            )
        for (case in cases) for (direction in LayoutDirection.entries) {
            val provider = PopupPositionProvider(PopupPlacement.Bottom, 4, case.align, 4)
            val result = provider.calculatePositionResult(case.anchor, window, direction, case.size)
            assertEquals(case.expected, result.offset, "$case $direction")
            assertEquals(case.expected, provider.calculatePosition(case.anchor, window, direction, case.size))
            assertEquals(IntRect(case.expected, case.size), result.bounds)
            assertEquals(PopupPlacement.Bottom, result.physicalSide)
            if (case.size == IntSize.Zero) assertEquals(TransformOrigin.Center, result.transformOrigin)
        }
    }
}
