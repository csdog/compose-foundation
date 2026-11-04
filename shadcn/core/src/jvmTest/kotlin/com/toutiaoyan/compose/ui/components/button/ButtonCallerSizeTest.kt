@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class ButtonCallerSizeTest : ScreenshotTest() {
    private fun assertColor(
        expected: Color,
        actual: Color,
    ) {
        assertEquals(expected.red, actual.red, 0.01f)
        assertEquals(expected.green, actual.green, 0.01f)
        assertEquals(expected.blue, actual.blue, 0.01f)
        assertEquals(expected.alpha, actual.alpha, 0.01f)
    }

    private fun fullWidth(dark: Boolean) {
        var fill = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            fill = currentTheme.colors.secondary
            Box(Modifier.width(200.dp)) {
                Button(
                    "Load History",
                    modifier =
                        Modifier.fillMaxWidth().testTag("button"),
                    variant =
                        ButtonVariant.Secondary,
                )
            }
        }
        val pixels = composeRule.onNodeWithTag("button").captureToImage().toPixelMap()
        assertColor(fill, pixels[12, pixels.height / 2])
        assertColor(fill, pixels[pixels.width - 13, pixels.height / 2])
    }

    @Test fun fillWidthPaintsTheWholeRequestedButtonLight() = fullWidth(false)

    @Test fun fillWidthPaintsTheWholeRequestedButtonDark() = fullWidth(true)

    @Test fun explicitCallerHeightConstrainsThePaintedChrome() {
        var fill = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            fill = currentTheme.colors.secondary
            Button(
                "Load History",
                modifier = Modifier.width(200.dp).height(56.dp).testTag("button"),
                variant = ButtonVariant.Secondary,
            )
        }
        val pixels = composeRule.onNodeWithTag("button").captureToImage().toPixelMap()
        assertColor(fill, pixels[pixels.width / 2, 4])
    }

    @Test fun expandedTouchTargetKeepsTheIconChromeSmall() {
        var fill = Color.Unspecified
        var background = Color.Unspecified
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(preset = StylePreset.Nova, isDark = false, minTouchTarget = 48.dp) {
                fill = currentTheme.colors.secondary
                background = currentTheme.colors.background
                Button(
                    onClick = {},
                    modifier = Modifier.testTag("button"),
                    variant = ButtonVariant.Secondary,
                    size = ButtonSize.IconSm,
                ) { Box(Modifier.size(4.dp)) }
            }
        }
        composeRule.onNodeWithTag("button").assertWidthIsEqualTo(48.dp).assertHeightIsEqualTo(48.dp)
        val pixels = composeRule.onNodeWithTag("button").captureToImage().toPixelMap()
        assertColor(background, pixels[4, pixels.height / 2])
        assertColor(fill, pixels[pixels.width / 2, pixels.height / 2])
    }
}
