@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class CalendarPressMotionTest : ScreenshotTest() {
    @Test fun novaTranslationHasIntermediateFramesAndRestoresOnRelease() {
        val source = MutableInteractionSource()
        setThemedContent(stylePreset = StylePreset.Nova) {
            // Enlarge the 1dp motion so raster quantization cannot hide intermediate frames.
            CompositionLocalProvider(LocalDensity provides Density(8f)) {
                Box(
                    Modifier.size(40.dp).background(Color.White).testTag("surface"),
                    contentAlignment = Alignment.Center,
                ) {
                    val state = rememberUpdatedStyleState(source)
                    Box(
                        Modifier.size(12.dp)
                            .styleable(state, rememberCalendarPressMotionStyle(source))
                            .background(Color.Green),
                    )
                }
            }
        }

        fun top(): Int {
            val pixels = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
            return (0 until pixels.height).first { pixels[pixels.width / 2, it].red < .1f }
        }
        val resting = top()
        composeRule.mainClock.autoAdvance = false
        val press = PressInteraction.Press(Offset.Zero)
        composeRule.runOnIdle { assertTrue(source.tryEmit(press)) }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(48)
        val entering = top()
        composeRule.mainClock.advanceTimeBy(200)
        val held = top()
        assertTrue(
            entering > resting && entering < held,
            "Press must paint an intermediate position: $resting/$entering/$held",
        )
        assertEquals(8, held - resting, "Held translation is exactly 1dp at density8")
        composeRule.runOnIdle { assertTrue(source.tryEmit(PressInteraction.Release(press))) }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(48)
        val leaving = top()
        assertTrue(leaving > resting && leaving < held, "Release must also paint an intermediate position")
        composeRule.mainClock.advanceTimeBy(200)
        assertEquals(resting, top())
    }
}
