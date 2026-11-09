@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toggle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class ToggleNovaParityTest : ScreenshotTest() {
    @Test
    fun transparentSurfaceAndSelectedColorSurvivePointerDownAndInvalidRing() {
        var dark by mutableStateOf(false)
        var selected by mutableStateOf(false)
        var invalid by mutableStateOf(false)
        var variant by mutableStateOf<ToggleVariant>(ToggleVariant.Default)
        var muted = Color.Unspecified
        val surface = Color(0xfff08050)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                muted = currentTheme.colors.muted
                Box(Modifier.background(surface)) {
                    Toggle(
                        pressed = selected,
                        onPressedChange = {},
                        modifier = Modifier.testTag("toggle"),
                        variant = variant,
                        animation = ButtonAnimation.None,
                        invalid = invalid,
                    ) { Box(Modifier.size(10.dp)) }
                }
            }
        }

        fun assertInterior(expected: Color) {
            val pixels = composeRule.onNodeWithTag("toggle").captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 2, pixels.height / 2]
            assertEquals(expected.red, actual.red, .01f)
            assertEquals(expected.green, actual.green, .01f)
            assertEquals(expected.blue, actual.blue, .01f)
        }
        for (isDark in listOf(false, true)) {
            for (v in listOf(ToggleVariant.Default, ToggleVariant.Outline)) {
                for (isInvalid in listOf(false, true)) {
                    composeRule.runOnIdle {
                        dark = isDark
                        variant = v
                        invalid = isInvalid
                        selected = false
                    }
                    assertInterior(surface)
                    composeRule.onNodeWithTag("toggle").performTouchInput { down(center) }
                    assertInterior(surface)
                    composeRule.onNodeWithTag("toggle").performTouchInput { cancel() }
                    composeRule.runOnIdle { selected = true }
                    assertInterior(muted)
                    composeRule.onNodeWithTag("toggle").performTouchInput { down(center) }
                    assertInterior(muted)
                    composeRule.onNodeWithTag("toggle").performTouchInput { cancel() }
                }
            }
        }
    }

    @Test
    fun originalPositionalContentCallsCompileAndRetainUncontrolledState() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                // This is the public positional signature from before iconPlacement was added.
                Toggle(
                    null, null, Modifier.testTag("toggle"), true, false,
                    ToggleVariant.Default, ToggleSize.Md, ButtonAnimation.None,
                    "Legacy caller", false, Style, { Box(Modifier.size(10.dp)) },
                )
            }
        }
        composeRule.onNodeWithTag("toggle").assertIsOff().performClick().assertIsOn()
    }
}
