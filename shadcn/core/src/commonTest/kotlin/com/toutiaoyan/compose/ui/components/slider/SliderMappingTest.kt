package com.toutiaoyan.compose.ui.components.slider

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals

class SliderMappingTest {
    @Test
    fun horizontal_thumb_center_at_start_is_zero() {
        val thumb = 12f
        val width = 112f
        val visual =
            sliderVisualFractionAlongTrack(
                position = Offset(thumb / 2f, 0f),
                widthPx = width,
                heightPx = 48f,
                thumbSizePx = thumb,
                vertical = false,
            )
        assertEquals(0f, visual, 0.001f)
    }

    @Test
    fun horizontal_thumb_center_at_end_is_one() {
        val thumb = 12f
        val width = 112f
        val visual =
            sliderVisualFractionAlongTrack(
                position = Offset(width - thumb / 2f, 0f),
                widthPx = width,
                heightPx = 48f,
                thumbSizePx = thumb,
                vertical = false,
            )
        assertEquals(1f, visual, 0.001f)
    }

    @Test
    fun vertical_thumb_center_at_bottom_is_zero() {
        val thumb = 12f
        val height = 112f
        val visual =
            sliderVisualFractionAlongTrack(
                position = Offset(0f, height - thumb / 2f),
                widthPx = 48f,
                heightPx = height,
                thumbSizePx = thumb,
                vertical = true,
            )
        assertEquals(0f, visual, 0.001f)
    }

    @Test
    fun unmeasured_track_does_not_divide_by_zero() {
        val visual =
            sliderVisualFractionAlongTrack(
                position = Offset(8f, 0f),
                widthPx = 0f,
                heightPx = 48f,
                thumbSizePx = 12f,
                vertical = false,
            )
        assertEquals(0f, visual, 0.001f)
    }
}
