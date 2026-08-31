package com.toutiaoyan.compose.ui.gesture

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SwipeGestureTest {
    @Test
    fun a_small_drag_below_the_threshold_does_not_dismiss() {
        assertFalse(
            SwipeGesture.shouldDismiss(
                dragOffsetPx = 50f,
                contentExtentPx = 400f,
                thresholdFraction = 0.3f,
            ),
        )
    }

    @Test
    fun a_drag_past_the_threshold_dismisses() {
        assertTrue(
            SwipeGesture.shouldDismiss(
                dragOffsetPx = 130f,
                contentExtentPx = 400f,
                thresholdFraction = 0.3f,
            ),
        )
    }

    @Test
    fun zero_extent_never_dismisses() {
        assertFalse(
            SwipeGesture.shouldDismiss(
                dragOffsetPx = 50f,
                contentExtentPx = 0f,
                thresholdFraction = 0.3f,
            ),
        )
    }
}
