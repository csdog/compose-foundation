package com.toutiaoyan.compose.ui.components.item

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
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasRequestFocusAction
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

class ItemNovaMotionTest : ScreenshotTest() {
    @Test
    fun focusAndBlurAnimateTheBorderWhileTheRingUpdatesImmediately() {
        lateinit var focusManager: FocusManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            focusManager = LocalFocusManager.current
            Box(Modifier.testTag("scene").background(Color.White).padding(12.dp)) {
                Item("Profile", Modifier.width(160.dp), variant = ItemVariant.Outline, onClick = {})
            }
        }
        val scene = composeRule.onNodeWithTag("scene")

        fun sample() = scene.captureToImage().toPixelMap()

        val idle = sample()
        composeRule.mainClock.autoAdvance = false
        composeRule.onNode(hasRequestFocusAction()).requestFocus().assertIsFocused()
        composeRule.mainClock.advanceTimeBy(48)
        val entering = sample()
        composeRule.mainClock.advanceTimeBy(152)
        val focused = sample()

        val x = idle.width / 2
        val idleBorder = idle[x, 12].red
        val focusedBorder = focused[x, 12].red
        assertTrue(entering[x, 12].red > focusedBorder && entering[x, 12].red < idleBorder)
        assertNotEquals(idle[x, 10], focused[x, 10])
        assertEquals(focused[x, 10], entering[x, 10])

        composeRule.runOnIdle { focusManager.clearFocus() }
        composeRule.mainClock.advanceTimeBy(48)
        val leaving = sample()
        composeRule.mainClock.advanceTimeBy(152)
        val idleAgain = sample()

        assertTrue(leaving[x, 12].red > focusedBorder && leaving[x, 12].red < idleBorder)
        assertEquals(idle[x, 10], leaving[x, 10])
        assertEquals(idle[x, 12], idleAgain[x, 12])
    }

    @Test
    fun focusedItemRetainsFocusAndUsesTheCurrentThemeAndRadius() {
        var dark by mutableStateOf(false)
        var radius by mutableStateOf(0.dp)
        lateinit var setKeyboardMode: () -> Unit
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, baseRadius = Radius(radius)) {
                val mode = LocalInputModeManager.current
                setKeyboardMode = { mode.requestInputMode(InputMode.Keyboard) }
                Box(Modifier.testTag("scene").background(Color(0xFF313131)).padding(12.dp)) {
                    Item("Profile", Modifier.width(160.dp), variant = ItemVariant.Muted, onClick = {})
                }
            }
        }
        composeRule.runOnIdle { setKeyboardMode() }
        val target = composeRule.onNode(hasRequestFocusAction()).requestFocus().assertIsFocused()
        val scene = composeRule.onNodeWithTag("scene")
        val square = scene.captureToImage().toPixelMap()

        composeRule.runOnIdle { radius = 18.dp }
        val rounded = scene.captureToImage().toPixelMap()
        assertNotEquals(square[12, 12], rounded[12, 12])

        composeRule.runOnIdle { dark = true }
        val themed = scene.captureToImage().toPixelMap()
        assertNotEquals(rounded[80, 10], themed[80, 10])
        assertNotEquals(rounded[80, 25], themed[80, 25])
        target.assertIsFocused()
    }
}
