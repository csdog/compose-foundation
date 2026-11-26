package com.toutiaoyan.compose.ui.components.inputotp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class InputOTPNovaMotionTest : ScreenshotTest() {
    @Test
    fun focusAndBlurAnimateBothBorderAndRing() {
        lateinit var focusManager: FocusManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            focusManager = LocalFocusManager.current
            Box(Modifier.testTag("scene").background(Color.White).padding(12.dp)) {
                InputOTP(value = "", onValueChange = {}, length = 4)
            }
        }
        val scene = composeRule.onNodeWithTag("scene")

        fun sample() = scene.captureToImage().toPixelMap()

        val idle = sample()
        composeRule.mainClock.autoAdvance = false
        composeRule.onNode(hasSetTextAction()).requestFocus().assertIsFocused()
        composeRule.mainClock.advanceTimeBy(64)
        val entering = sample()
        composeRule.mainClock.advanceTimeBy(180)
        val focused = sample()
        composeRule.runOnIdle { focusManager.clearFocus() }
        composeRule.mainClock.advanceTimeBy(64)
        val leaving = sample()
        composeRule.mainClock.advanceTimeBy(180)
        val idleAgain = sample()
        for (y in listOf(12, 11)) {
            val unfocused = idle[28, y].red
            val settled = focused[28, y].red
            assertTrue(settled < unfocused, "focus paints the border/ring pixel at y=$y")
            val focusFrame = entering[28, y].red
            val blurFrame = leaving[28, y].red
            assertTrue(focusFrame > settled && focusFrame < unfocused, "focus interpolates the pixel at y=$y")
            assertTrue(blurFrame > settled && blurFrame < unfocused, "blur interpolates the pixel at y=$y")
            assertEquals(idle[28, y], idleAgain[28, y])
        }
    }

    @Test
    fun caretBlinksInTheEmptySlot() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.testTag("scene").background(Color.White).padding(12.dp)) {
                InputOTP(value = "", onValueChange = {}, length = 4)
            }
        }
        val scene = composeRule.onNodeWithTag("scene")

        fun ink(): Float {
            val pixels = scene.captureToImage().toPixelMap()
            return (27..29).minOf { pixels[it, 28].red }
        }
        val idle = ink()
        composeRule.mainClock.autoAdvance = false
        composeRule.onNode(hasSetTextAction()).requestFocus()
        composeRule.mainClock.advanceTimeBy(48)
        assertTrue(ink() < idle - .4f, "caret starts visible")
        composeRule.mainClock.advanceTimeBy(280)
        assertEquals(idle, ink(), "caret is transparent from 20% through 50%")
        composeRule.mainClock.advanceTimeBy(470)
        assertTrue(ink() < idle - .4f, "caret becomes visible again")
    }

    @Test
    fun focusedSlotUsesTheCurrentRadiusAndThemeAndRetainsItsDigit() {
        var radius by mutableStateOf(0.dp)
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, baseRadius = Radius(radius)) {
                Box(Modifier.testTag("scene").background(Color(0xFF313131)).padding(12.dp)) {
                    InputOTP(value = "7", onValueChange = {}, length = 4)
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).requestFocus().assertIsFocused()
        val scene = composeRule.onNodeWithTag("scene")
        val before = scene.captureToImage().toPixelMap()
        composeRule.runOnIdle { radius = 14.dp }
        val rounded = scene.captureToImage().toPixelMap()
        assertNotEquals(before[12, 12], rounded[12, 12])
        composeRule.runOnIdle { dark = true }
        val darkened = scene.captureToImage().toPixelMap()
        assertNotEquals(rounded[60, 20], darkened[60, 20])
        composeRule.onNode(hasSetTextAction()).assertIsFocused().assertTextContains("7")
    }
}
