@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class SelectBehaviorTest : ScreenshotTest() {
    @Test fun typeaheadUsesLatestTextValueSkipsDisabledAndCyclesMatches() {
        var disabled by mutableStateOf(false)
        var label by mutableStateOf("Zebra")
        var picked: String? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, { picked = it }, defaultOpen = true) {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    SelectItem("Alpha")
                    SelectItem("first", textValue = label, disabled = disabled) { Text { "Custom label" } }
                    SelectItem("Zulu")
                    SelectItem("Beta", disabled = true)
                    SelectItem("Blueberry")
                }
            }
        }
        composeRule.onNodeWithText("Alpha").assertIsFocused().performKeyInput { pressKey(Key.Z) }
        composeRule.onNodeWithText("Custom label").assertIsFocused().performKeyInput { pressKey(Key.Z) }
        composeRule.onNodeWithText("Zulu").assertIsFocused()
        composeRule.runOnIdle {
            disabled = true
            label = "Other"
        }
        composeRule.onNodeWithText("Zulu").performKeyInput { pressKey(Key.Z) }
        composeRule.onNodeWithText("Zulu").assertIsFocused()
        Thread.sleep(800)
        composeRule.onNodeWithText("Zulu").performKeyInput { pressKey(Key.B) }
        composeRule.onNodeWithText("Blueberry").assertIsFocused().performKeyInput {
            pressKey(Key.L)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals("Blueberry", picked) }
        composeRule.onNodeWithText("Custom label").assertDoesNotExist()
    }

    @Test fun typeaheadResetsAcrossOpenAndEnterUsesLatestControlledCallback() {
        var open by mutableStateOf(true)
        var suffix by mutableStateOf("old")
        var picked: String? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, { picked = it + suffix }, open = open, onOpenChange = { open = it }) {
                SelectTrigger(Modifier.testTag("trigger"))
                SelectContent {
                    SelectItem("Alpha")
                    SelectItem("Blueberry")
                }
            }
        }
        composeRule.onNodeWithText("Alpha").performKeyInput {
            pressKey(Key.B)
            pressKey(Key.Escape)
        }
        composeRule.runOnIdle { suffix = "new" }
        composeRule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Alpha").assertIsFocused().performKeyInput {
            pressKey(Key.A)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals("Alphanew", picked) }
    }

    @Test fun keyboardFocusRecolorsActualTextAndUpdatesOnLiveThemeChange() {
        var dark by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            val theme = Theme.current
            CompositionLocalProvider(
                Theme.LocalTheme provides
                    theme.copy(
                        isDark = dark,
                        colors =
                            theme.colors.copy(
                                popoverForeground = Color.Green,
                                accentForeground = if (dark) Color.Blue else Color.Red,
                            ),
                    ),
            ) {
                Select<String>(null, {}, defaultOpen = true) {
                    SelectTrigger(Modifier.width(192.dp))
                    SelectContent {
                        SelectItem("Alpha", Modifier.testTag("row")) {
                            SelectionContainer { Text { "Alpha" } }
                        }
                        SelectItem("Beta")
                    }
                }
            }
        }
        composeRule.onNodeWithTag("row").assertIsFocused()

        fun coloredGlyphs(red: Boolean): Int {
            val pixels = composeRule.onNodeWithTag("row").captureToImage().toPixelMap()
            return (0 until pixels.width).sumOf { x ->
                (0 until pixels.height).count { y ->
                    val c = pixels[x, y]
                    if (red) {
                        c.red > .8f && c.green < .2f && c.blue < .2f
                    } else {
                        c.blue > .8f && c.green < .2f && c.red < .2f
                    }
                }
            }
        }
        assertTrue(coloredGlyphs(true) > 10, "focused label must paint accent foreground")
        composeRule.runOnIdle { dark = true }
        assertTrue(coloredGlyphs(false) > 10, "focused label must update after the theme changes")
    }

    @Test fun keyedReorderedOptionsNavigateInVisibleOrder() {
        var reverse by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Select<String>(null, {}, defaultOpen = true) {
                SelectTrigger()
                SelectContent {
                    val values = if (reverse) listOf("Gamma", "Beta", "Alpha") else listOf("Alpha", "Beta", "Gamma")
                    values.forEach { value -> key(value) { SelectItem(value) } }
                }
            }
        }
        composeRule.onNodeWithText("Alpha").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Beta").assertIsFocused()
        composeRule.runOnIdle { reverse = true }
        composeRule.onNodeWithText("Beta").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Alpha").assertIsFocused()
    }
}
