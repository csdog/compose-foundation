package com.toutiaoyan.compose.ui.components.slider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
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

    @Test
    fun directThumbHitStoresTheGrabOffset() {
        val hit =
            sliderPointerTarget(
                position = Offset(35f, 24f),
                size = IntSize(112, 48),
                thumbSizePx = 12f,
                vertical = false,
                range = 0f..100f,
                values = listOf(25f, 50f),
                reverse = false,
            )
        assertEquals(SliderPointerTarget(index = 0, grabOffsetPx = 4f), hit)
    }

    @Test
    fun trackMidpointChoosesTheLaterThumb() {
        val hit =
            sliderPointerTarget(
                position = Offset(43.5f, 24f),
                size = IntSize(112, 48),
                thumbSizePx = 12f,
                vertical = false,
                range = 0f..100f,
                values = listOf(25f, 50f),
                reverse = false,
            )
        assertEquals(SliderPointerTarget(index = 1, grabOffsetPx = null), hit)
    }

    @Test
    fun directHitAtOverlappingVerticalMaximumStartsWithTheFirstThumb() {
        val hit =
            sliderPointerTarget(
                position = Offset(24f, 10f),
                size = IntSize(48, 112),
                thumbSizePx = 12f,
                vertical = true,
                range = 0f..100f,
                values = listOf(100f, 100f),
                reverse = false,
            )
        assertEquals(SliderPointerTarget(index = 0, grabOffsetPx = -4f), hit)
    }

    @Test
    fun webCoarseKeysUseAnAbsoluteStepOnlyForSmallDiscreteSteps() {
        assertEquals(22f, sliderKeyboardTarget(12f, 0f..100f, 4f, 1, coarse = true, webBehavior = true))
        assertEquals(25f, sliderKeyboardTarget(35f, 0f..100f, 5f, -1, coarse = true, webBehavior = true))
        assertEquals(75f, sliderKeyboardTarget(25f, 0f..100f, 5f, 1, coarse = true, webBehavior = false))
        assertEquals(600f, sliderKeyboardTarget(100f, 0f..1000f, 50f, 1, coarse = true, webBehavior = true))
        assertEquals(0.35f, sliderKeyboardTarget(0.25f, 0f..1f, 0f, 1, coarse = true, webBehavior = true), 0.0001f)
    }
}
