package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HoverCardPointerTransitTest {
    private val trigger = IntRect(100, 100, 140, 120)
    private val popup =
        PopupPositionResult(
            IntOffset(60, 144),
            IntRect(60, 144, 240, 195),
            PopupPlacement.Bottom,
            TransformOrigin.Center,
        )

    @Test fun sharedGapHoldsWithoutDirectionalSpeed() {
        assertEquals(
            HoverCardTransitRegion.SharedGap,
            hoverCardTransitRegion(Offset(125f, 130f), null, trigger, popup, 1f),
        )
    }

    @Test fun diagonalApproachRequiresTriggerDepartureAndAimsAtThePanel() {
        val start = Offset(135f, 119f)
        assertEquals(
            HoverCardTransitRegion.Approach,
            hoverCardTransitRegion(Offset(171f, 135f), start, trigger, popup, 1f),
        )
        assertNull(hoverCardTransitRegion(Offset(171f, 135f), null, trigger, popup, 1f))
        assertNull(hoverCardTransitRegion(Offset(235f, 130f), start, trigger, popup, 1f))
        assertNull(hoverCardTransitRegion(Offset(130f, 98f), start, trigger, popup, 1f))
    }

    @Test fun sharedGapRespectsTheCurrentPhysicalSide() {
        val topBounds = IntRect(60, 42, 240, 94)
        val leftBounds = IntRect(22, 90, 94, 130)
        val rightBounds = IntRect(146, 90, 216, 130)
        val placements =
            listOf(
                Triple(PopupPlacement.Top, topBounds, Offset(116f, 97f)),
                Triple(PopupPlacement.Start, leftBounds, Offset(97f, 104f)),
                Triple(PopupPlacement.End, rightBounds, Offset(143f, 104f)),
            )
        placements.forEach { (side, bounds, point) ->
            val positioned = PopupPositionResult(bounds.topLeft, bounds, side, TransformOrigin.Center)
            assertEquals(HoverCardTransitRegion.SharedGap, hoverCardTransitRegion(point, null, trigger, positioned, 1f))
        }
    }

    @Test fun aligningThePanelAwayFromTheTriggerKeepsTheNarrowerTrough() {
        val shifted = popup.copy(bounds = IntRect(125, 144, 265, 195))
        assertEquals(
            HoverCardTransitRegion.SharedGap,
            hoverCardTransitRegion(Offset(107f, 130f), null, trigger, shifted, 1f),
        )
    }

    @Test fun widerTriggerCanApproachTheFarCornerOfANarrowPanel() {
        val wideTrigger = IntRect(0, 50, 200, 100)
        val narrowPanel = popup.copy(bounds = IntRect(0, 120, 80, 200))
        val start = Offset(150f, 100f)
        assertEquals(
            HoverCardTransitRegion.Approach,
            hoverCardTransitRegion(Offset(130f, 110f), start, wideTrigger, narrowPanel, 1f),
        )
        assertNull(hoverCardTransitRegion(Offset(175f, 110f), start, wideTrigger, narrowPanel, 1f))
    }

    @Test fun touchingOrClampedOverlappingBoundsHaveNoGap() {
        val overlap = popup.copy(bounds = IntRect(60, 117, 240, 195))
        assertNull(hoverCardTransitRegion(Offset(125f, 120f), null, trigger, overlap, 1f))
    }
}
