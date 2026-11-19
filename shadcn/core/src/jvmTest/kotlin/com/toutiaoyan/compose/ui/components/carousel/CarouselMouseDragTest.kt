package com.toutiaoyan.compose.ui.components.carousel

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class CarouselMouseDragTest {
    @Test fun mainAxisIgnoresCrossAxisMovement() {
        assertEquals(0f, carouselMainAxis(Offset(0f, 200f), Orientation.Horizontal))
        assertEquals(0f, carouselMainAxis(Offset(200f, 0f), Orientation.Vertical))
        assertEquals(-17f, carouselMainAxis(Offset(-17f, 200f), Orientation.Horizontal))
        assertEquals(23f, carouselMainAxis(Offset(200f, 23f), Orientation.Vertical))
    }

    @Test fun horizontalPointerMovementUsesOppositeLogicalDirectionsInRtl() {
        assertEquals(40f, carouselScrollDelta(-40f, Orientation.Horizontal, LayoutDirection.Ltr))
        assertEquals(-40f, carouselScrollDelta(-40f, Orientation.Horizontal, LayoutDirection.Rtl))
        assertEquals(-40f, carouselScrollDelta(40f, Orientation.Horizontal, LayoutDirection.Ltr))
        assertEquals(40f, carouselScrollDelta(40f, Orientation.Horizontal, LayoutDirection.Rtl))
    }

    @Test fun verticalMovementFollowsThePointerInBothLayoutDirections() {
        for (direction in LayoutDirection.entries) {
            assertEquals(40f, carouselScrollDelta(-40f, Orientation.Vertical, direction))
            assertEquals(-40f, carouselScrollDelta(40f, Orientation.Vertical, direction))
        }
    }

    @Test fun takeoverRequiresExceedingSlopAndOnlyScrollsTheExcess() {
        for (distance in listOf(-18f, -5f, 0f, 5f, 18f)) {
            assertEquals(0f, carouselOverSlop(distance, 18f))
        }
        assertEquals(1f, carouselOverSlop(19f, 18f))
        assertEquals(-1f, carouselOverSlop(-19f, 18f))
        assertEquals(42f, carouselOverSlop(60f, 18f))
    }

    @Test fun slopTracksNetDisplacementRatherThanDistanceTravelled() {
        val moves = listOf(12f, -10f, 12f, -10f)
        var distance = 0f
        for (move in moves) {
            distance += move
            assertEquals(0f, carouselOverSlop(distance, 18f))
        }
    }
}
