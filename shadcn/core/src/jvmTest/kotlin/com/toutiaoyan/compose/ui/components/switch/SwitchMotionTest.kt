@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.switch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class SwitchMotionTest : ScreenshotTest() {
    @Test fun lightTrackInterpolatesAndReverses() = trackMotion(false)

    @Test fun darkTrackInterpolatesAndReverses() = trackMotion(true)

    private fun trackMotion(initialDark: Boolean) {
        var checked by mutableStateOf(false)
        var dark by mutableStateOf(initialDark)
        val backdrop = Color(0xff808080)
        var unchecked = Color.Unspecified
        var primary = Color.Unspecified
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                val colors = currentTheme.colors
                unchecked =
                    colors.input
                        .copy(alpha = colors.input.alpha * if (dark) .8f else 1f)
                        .compositeOver(backdrop)
                primary = colors.primary
                // Isolate the root background from the thumb and press animation.
                Box(Modifier.background(backdrop)) {
                    Switch(checked, { checked = it }, Modifier.testTag("switch"), content = {})
                }
            }
        }
        val node = composeRule.onNodeWithTag("switch")

        fun sample(): Color = node.captureToImage().toPixelMap().let { it[it.width / 2, it.height / 2] }

        fun assertColor(
            expected: Color,
            actual: Color,
        ) {
            assertTrue(
                listOf(
                    expected.red - actual.red,
                    expected.green - actual.green,
                    expected.blue - actual.blue,
                    expected.alpha - actual.alpha,
                ).all { abs(it) <= 3f / 255f },
                "$expected != $actual",
            )
        }

        fun distance(
            a: Color,
            b: Color,
        ) = abs(a.red - b.red) + abs(a.green - b.green) +
            abs(a.blue - b.blue) + abs(a.alpha - b.alpha)
        assertColor(unchecked, sample())
        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.runOnIdle { checked = true }
            composeRule.mainClock.advanceTimeByFrame()
            sample()
            composeRule.mainClock.advanceTimeBy(48)
            val entering = sample()
            assertTrue(
                distance(entering, unchecked) > .03f && distance(entering, primary) > .03f,
                "48ms must be intermediate: $entering",
            )
            composeRule.runOnIdle { checked = false }
            assertColor(entering, sample())
            composeRule.mainClock.advanceTimeByFrame()
            sample()
            composeRule.mainClock.advanceTimeBy(48)
            val returning = sample()
            assertTrue(
                distance(returning, unchecked) < distance(entering, unchecked),
                "Reversal must return toward unchecked",
            )
            assertTrue(distance(returning, unchecked) > .01f, "Reversal must interpolate")
            composeRule.mainClock.advanceTimeBy(200)
            assertColor(unchecked, sample())
            composeRule.runOnIdle { dark = !dark }
            composeRule.mainClock.advanceTimeByFrame()
            assertColor(unchecked, sample())
            composeRule.runOnIdle { checked = true }
            composeRule.mainClock.advanceTimeBy(240)
            assertColor(primary, sample())
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test fun callerStyleStillOverridesAnimatedTrack() {
        var checked by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Switch(
                    checked,
                    { checked = it },
                    Modifier.testTag("switch"),
                    style = Style { background(Color.Magenta) },
                    content = {},
                )
            }
        }
        composeRule.runOnIdle { checked = true }
        val pixels = composeRule.onNodeWithTag("switch").captureToImage().toPixelMap()
        val color = pixels[pixels.width / 2, pixels.height / 2]
        assertTrue(color.red > .99f && color.blue > .99f && color.green < .01f)
    }

    @Test fun bothThumbSizesMoveInTheLogicalDirectionAndInterpolate() {
        var checked by mutableStateOf(false)
        var size: SwitchSize by mutableStateOf(SwitchSize.Default)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalLayoutDirection provides direction) {
                    Switch(checked, { checked = it }, size = size) {
                        SwitchThumb(Modifier.testTag("thumb"))
                    }
                }
            }
        }

        fun left() =
            composeRule.onNodeWithTag("thumb", useUnmergedTree = true)
                .fetchSemanticsNode().boundsInRoot.left
        for (nextSize in listOf(SwitchSize.Default, SwitchSize.Sm)) {
            for (nextDirection in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
                composeRule.runOnIdle {
                    size = nextSize
                    direction = nextDirection
                    checked = false
                }
                composeRule.waitForIdle()
                val start = left()
                val delta = (nextSize.thumbSize.value - 2f) * if (nextDirection == LayoutDirection.Ltr) 1f else -1f
                composeRule.mainClock.autoAdvance = false
                try {
                    composeRule.runOnIdle { checked = true }
                    composeRule.mainClock.advanceTimeByFrame()
                    left()
                    composeRule.mainClock.advanceTimeBy(48)
                    val middle = left()
                    assertTrue(abs(middle - start) > .1f && abs(middle - start) < abs(delta), "Thumb must interpolate")
                    composeRule.mainClock.advanceTimeBy(200)
                    assertTrue(
                        abs(left() - start - delta) < .1f,
                        "Thumb must translate by its own size minus 2px in $nextDirection",
                    )
                } finally {
                    composeRule.mainClock.autoAdvance = true
                }
            }
        }
    }
}
