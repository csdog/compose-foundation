package com.toutiaoyan.compose.ui.components.command

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandKeyboardTest : ScreenshotTest() {
    @Test fun controlledLegacyHighlightCanBeSelectedBeforeAnyArrowPress() {
        val selected = mutableListOf<String>()
        setThemedContent(stylePreset = StylePreset.Vega) {
            Command(value = "Beta") {
                CommandInput(placeholder = "Search commands")
                CommandList {
                    CommandItem("Alpha", onSelect = { selected += "Alpha" })
                    CommandItem("Beta", onSelect = { selected += "Beta" })
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).performClick().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals(listOf("Beta"), selected) }
    }

    @Test fun keyedReorderMovesHighlightsInVisualOrder() {
        var rows by mutableStateOf(listOf("Alpha", "Beta", "Gamma"))
        val selected = mutableListOf<String>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Command(defaultValue = "Beta") {
                CommandInput()
                CommandList {
                    rows.forEach { row -> key(row) { CommandItem(row, onSelect = { selected += row }) } }
                }
            }
        }
        val input = composeRule.onNode(hasSetTextAction()).performClick()
        composeRule.runOnIdle { rows = listOf("Alpha", "Gamma", "Beta") }
        input.performKeyInput { pressKey(Key.DirectionUp) }
        input.performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals(listOf("Gamma"), selected) }
    }
}
