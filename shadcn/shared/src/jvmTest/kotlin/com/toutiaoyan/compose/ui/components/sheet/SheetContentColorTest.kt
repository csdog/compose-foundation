@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertTrue

class SheetContentColorTest : ScreenshotTest() {
    @Test fun bodyAndSelectableTextUsePopoverForegroundAcrossLiveThemeChanges() {
        var dark by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            val theme = Theme.current
            CompositionLocalProvider(
                Theme.LocalTheme provides
                    theme.copy(
                        isDark = dark,
                        colors =
                            theme.colors.copy(
                                foreground = Color.Green,
                                popover = Color.White,
                                popoverForeground = if (dark) Color.Blue else Color.Red,
                            ),
                    ),
                LocalContentColor provides Color.Green,
            ) {
                Sheet(defaultOpen = true) {
                    SheetContent(showCloseButton = false) {
                        Text(Modifier.testTag("body")) { "Sheet body" }
                        SelectionContainer { Text(Modifier.testTag("selectable")) { "Selectable body" } }
                    }
                }
            }
        }
        assertGlyphColor("body", Color.Red)
        assertGlyphColor("selectable", Color.Red)
        composeRule.runOnIdle { dark = true }
        assertGlyphColor("body", Color.Blue)
        assertGlyphColor("selectable", Color.Blue)
    }

    @Test fun callerContentColorStillOverridesThePanelDefault() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Sheet(defaultOpen = true) {
                SheetContent(showCloseButton = false, style = Style { contentColor(Color.Green) }) {
                    Text(Modifier.testTag("body")) { "Custom body" }
                }
            }
        }
        assertGlyphColor("body", Color.Green)
    }

    private fun assertGlyphColor(
        tag: String,
        expected: Color,
    ) {
        val pixels = composeRule.onNodeWithTag(tag).captureToImage().toPixelMap()
        val matching =
            (0 until pixels.width).sumOf { x ->
                (0 until pixels.height).count { y ->
                    val actual = pixels[x, y]
                    kotlin.math.abs(actual.red - expected.red) < .1f &&
                        kotlin.math.abs(actual.green - expected.green) < .1f &&
                        kotlin.math.abs(actual.blue - expected.blue) < .1f
                }
            }
        assertTrue(matching > 20, "$tag must paint $expected; found $matching solid glyph pixels")
    }
}
