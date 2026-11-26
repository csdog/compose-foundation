package com.toutiaoyan.compose.ui.components.inputgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithTag
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

class InputGroupNovaMotionTest : ScreenshotTest() {
    @Test
    fun borderColorTransitionsInBothDirectionsWhileTheRingUpdatesImmediately() {
        lateinit var focusManager: FocusManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            focusManager = LocalFocusManager.current
            Box(Modifier.testTag("scene").background(Color.White).padding(12.dp)) {
                InputGroup(modifier = Modifier.width(200.dp)) {
                    InputGroupInput(value = "", onValueChange = {})
                    InputGroupAddon(InputGroupAddonAlign.InlineEnd) { InputGroupText("USD") }
                }
            }
        }
        val scene = composeRule.onNodeWithTag("scene")

        fun sample() = scene.captureToImage().toPixelMap()
        val idle = sample()
        composeRule.mainClock.autoAdvance = false
        composeRule.onNode(hasSetTextAction()).requestFocus().assertIsFocused()
        composeRule.mainClock.advanceTimeBy(64)
        val entering = sample()
        composeRule.mainClock.advanceTimeBy(220)
        val focused = sample()

        val borderX = idle.width / 2
        val borderY = 12
        val ringY = 10
        val idleBorder = idle[borderX, borderY].red
        val focusedBorder = focused[borderX, borderY].red
        val enteringBorder = entering[borderX, borderY].red
        assertTrue(enteringBorder > focusedBorder && enteringBorder < idleBorder, "focus interpolates the border")
        assertEquals(focused[borderX, ringY], entering[borderX, ringY], "the outer ring appears immediately")
        assertNotEquals(idle[borderX, ringY], focused[borderX, ringY])

        composeRule.runOnIdle { focusManager.clearFocus() }
        composeRule.mainClock.advanceTimeBy(64)
        val leaving = sample()
        composeRule.mainClock.advanceTimeBy(220)
        val idleAgain = sample()
        val leavingBorder = leaving[borderX, borderY].red
        assertTrue(leavingBorder > focusedBorder && leavingBorder < idleBorder, "blur interpolates the border")
        assertEquals(idle[borderX, ringY], leaving[borderX, ringY], "the outer ring disappears immediately")
        assertEquals(idle[borderX, borderY], idleAgain[borderX, borderY])
    }

    @Test
    fun focusedGroupRepaintsItsShapeAndThemeWithoutReplacingTheInputValue() {
        var radius by mutableStateOf(0.dp)
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, baseRadius = Radius(radius)) {
                Box(Modifier.testTag("scene").background(Color(0xFF313131)).padding(12.dp)) {
                    InputGroup(modifier = Modifier.width(200.dp)) {
                        InputGroupInput(value = "Retained value", onValueChange = {})
                    }
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).requestFocus().assertIsFocused()
        val scene = composeRule.onNodeWithTag("scene")
        val square = scene.captureToImage().toPixelMap()

        composeRule.runOnIdle { radius = 14.dp }
        val rounded = scene.captureToImage().toPixelMap()
        assertNotEquals(square[12, 12], rounded[12, 12], "the focused corner follows the live shape")
        assertEquals(square[112, 10], rounded[112, 10], "the straight portion of the ring retains its color")

        composeRule.runOnIdle { dark = true }
        val themed = scene.captureToImage().toPixelMap()
        assertNotEquals(rounded[112, 10], themed[112, 10], "the focused ring adopts the new theme")
        composeRule.onNode(hasSetTextAction()).assertIsFocused().assertTextContains("Retained value")
    }
}
