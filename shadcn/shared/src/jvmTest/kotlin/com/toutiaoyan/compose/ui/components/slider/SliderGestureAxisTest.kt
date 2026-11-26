package com.toutiaoyan.compose.ui.components.slider

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals

class SliderGestureAxisTest {
    @Test
    fun waitsForSlopAndLetsThePerpendicularAxisScroll() {
        assertEquals(SliderGestureAxis.Pending, sliderGestureAxis(Offset(8f, 2f), vertical = false, touchSlop = 8f))
        assertEquals(SliderGestureAxis.CrossAxis, sliderGestureAxis(Offset(9f, 12f), vertical = false, touchSlop = 8f))
        assertEquals(SliderGestureAxis.Slider, sliderGestureAxis(Offset(-12f, 9f), vertical = false, touchSlop = 8f))
        assertEquals(SliderGestureAxis.CrossAxis, sliderGestureAxis(Offset(12f, 12f), vertical = false, touchSlop = 8f))
    }

    @Test
    fun usesTheConfiguredAxisInBothDirections() {
        assertEquals(SliderGestureAxis.Slider, sliderGestureAxis(Offset(3f, -12f), vertical = true, touchSlop = 8f))
        assertEquals(SliderGestureAxis.CrossAxis, sliderGestureAxis(Offset(-12f, 3f), vertical = true, touchSlop = 8f))
    }
}
