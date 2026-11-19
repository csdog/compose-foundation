@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
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
import kotlin.test.assertTrue

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

    @Test fun novaPanelAndOuterShadowFadeInTogether() {
        assertShadowDuringEntry(StylePreset.Nova)
    }

    @Test fun vegaPanelAndOuterShadowFadeInTogether() {
        assertShadowDuringEntry(StylePreset.Vega)
    }

    private fun assertShadowDuringEntry(preset: StylePreset) {
        var open by mutableStateOf(false)
        setThemedContent(stylePreset = preset) {
            Box(Modifier.size(400.dp, 240.dp).background(Color.White).testTag("backing")) {
                Popover(open = open, modifier = Modifier.offset(x = 120.dp, y = 40.dp)) {
                    PopoverTrigger("Open")
                    PopoverContent(
                        modifier = Modifier.testTag("panel"),
                        style =
                            Style {
                                width(100.dp)
                                background(Color.Black)
                                dropShadow(Shadow(radius = 0.dp, spread = 12.dp, color = Color.Red))
                            },
                    ) {
                        Box(Modifier.size(48.dp))
                    }
                }
            }
        }
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { open = true }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(48)
        val backing = composeRule.onNodeWithTag("backing")
        val backingOrigin = backing.fetchSemanticsNode().boundsInWindow.topLeft
        val bounds = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.translate(-backingOrigin)
        val pixels = backing.captureToImage().toPixelMap()
        val y = bounds.center.y.toInt()
        val panel = pixels[bounds.center.x.toInt(), y]
        val shadow = pixels[bounds.right.toInt() + 3, y]
        assertTrue(panel.red in 0.02f..0.98f, "Expected a partially visible popup, observed $panel")
        assertEquals(panel.red, panel.green, 0.01f, "The fading shadow must not tint the panel interior")
        assertTrue(shadow.green < 0.98f, "The panel appeared without its outer shadow, observed $shadow")
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
