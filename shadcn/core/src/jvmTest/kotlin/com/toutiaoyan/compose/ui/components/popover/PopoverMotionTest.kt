package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PopoverMotionTest : ScreenshotTest() {
    @Test fun novaDefaultExitIsPresentMidAnimationAndUnmountedAfterHundredMilliseconds() {
        val open = mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Popover(open = open.value) {
                PopoverTrigger("Open")
                PopoverContent("Body")
            }
        }
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { open.value = false }
        composeRule.mainClock.advanceTimeBy(48)
        composeRule.onNodeWithText("Body").assertExists()
        composeRule.mainClock.advanceTimeBy(112)
        composeRule.onNodeWithText("Body").assertDoesNotExist()
    }

    @Test fun explicitExitRetainsTheCallersLongerDuration() {
        val open = mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Popover(open = open.value) {
                PopoverTrigger("Open")
                PopoverContent("Body", exit = fadeOut(tween(300)))
            }
        }
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { open.value = false }
        composeRule.mainClock.advanceTimeBy(128)
        composeRule.onNodeWithText("Body").assertExists()
        composeRule.mainClock.advanceTimeBy(208)
        composeRule.onNodeWithText("Body").assertDoesNotExist()
    }

    @Test fun openPanelUpdatesItsFillAndCornerAfterThemeChanges() {
        var dark by mutableStateOf(false)
        var radius by mutableStateOf(Radius(10.dp))
        var preset by mutableStateOf(StylePreset.Nova)
        composeRule.setContent {
            Theme(preset = preset, isDark = dark, baseRadius = radius) {
                Popover(defaultOpen = true) {
                    PopoverTrigger("Open")
                    PopoverContent(modifier = Modifier.testTag("panel")) {
                        var count by remember { mutableStateOf(0) }
                        Button("Count $count", onClick = { count++ })
                    }
                }
            }
        }

        fun pixel(corner: Boolean): Int {
            val image = composeRule.onNodeWithTag("panel").captureToImage().toPixelMap()
            return (if (corner) image[2, 2] else image[image.width - 20, image.height / 2]).toArgb()
        }

        val light = pixel(corner = false)
        composeRule.runOnIdle { dark = true }
        val darkFill = pixel(corner = false)
        assertNotEquals(light, darkFill)
        assertNotEquals(darkFill, pixel(corner = true))
        composeRule.runOnIdle { radius = Radius(0.dp) }
        assertEquals(darkFill, pixel(corner = true))
        composeRule.onNodeWithText("Count 0").performClick()
        composeRule.runOnIdle { preset = StylePreset.Vega }
        composeRule.onNodeWithText("Count 1").assertExists()
    }
}
