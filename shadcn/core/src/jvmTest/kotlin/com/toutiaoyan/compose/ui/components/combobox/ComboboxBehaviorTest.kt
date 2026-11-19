@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.combobox

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ComboboxBehaviorTest : ScreenshotTest() {
    @Test fun chipsKeepInputFocusAndSelectWithKeyboard() {
        var selected = emptyList<String>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            var values by remember { mutableStateOf(emptyList<String>()) }
            Combobox(
                values = values,
                options = listOf("Alpha", "Beta"),
                onValuesChange = {
                    values = it
                    selected = it
                },
            ) {
                ComboboxChips {
                    ComboboxChipsInput(Modifier.testTag("query"), placeholder = "Add")
                }
                ComboboxContent {
                    ComboboxList {
                        ComboboxItem("Alpha", "Alpha")
                        ComboboxItem("Beta", "Beta")
                    }
                }
            }
        }
        composeRule.onNodeWithTag("query").performClick()
        composeRule.onNodeWithTag("query").assertIsFocused()
        composeRule.onNodeWithTag("query").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("query").performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals(listOf("Alpha"), selected) }
        composeRule.onNodeWithTag("query").assertIsFocused()
        composeRule.onNodeWithTag("query").performTextInput("Be")
        composeRule.onNodeWithTag("query").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("query").performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals(listOf("Alpha", "Beta"), selected) }
        composeRule.onNodeWithTag("query").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Beta").assertDoesNotExist()
    }

    @Test
    fun anAlreadyOpenEditableInputReceivesFocusWhenEnabled() {
        var inputDisabled by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Combobox<String>(
                value = null,
                options = listOf("Alpha"),
                onValueChange = {},
                open = true,
            ) {
                ComboboxInput(editable = true, disabled = inputDisabled, showTrigger = false)
            }
        }
        composeRule.runOnIdle { inputDisabled = false }
        composeRule.onNode(hasSetTextAction()).assertIsFocused()
    }

    @Test
    fun anAlreadyOpenChipsInputReceivesFocusWhenEnabled() {
        var inputDisabled by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            Combobox(
                values = emptyList<String>(),
                options = listOf("Alpha"),
                onValuesChange = {},
                open = true,
            ) {
                ComboboxChips {
                    ComboboxChipsInput(Modifier.testTag("chips-query"), disabled = inputDisabled)
                }
            }
        }
        composeRule.runOnIdle { inputDisabled = false }
        composeRule.onNodeWithTag("chips-query").assertIsFocused()
    }

    @Test
    fun disabledEditableInputDoesNotOpenOnPointerPress() {
        val requested = mutableListOf<Boolean>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Combobox<String>(
                value = null,
                options = listOf("Alpha"),
                onValueChange = {},
                open = false,
                onOpenChange = { requested += it },
            ) {
                ComboboxInput(Modifier.testTag("disabled-input"), editable = true, disabled = true, showTrigger = false)
            }
        }
        composeRule.onNodeWithTag("disabled-input").performMouseInput { click(center) }
        composeRule.runOnIdle { assertTrue(requested.isEmpty()) }
    }

    @Test
    fun editableInputPressUsesReplacedOpenCallback() {
        var useFirstCallback by mutableStateOf(true)
        val requests = mutableListOf<String>()
        val first: (Boolean) -> Unit = { if (it) requests += "first" }
        val second: (Boolean) -> Unit = { if (it) requests += "second" }
        setThemedContent(stylePreset = StylePreset.Nova) {
            Combobox<String>(
                value = null,
                options = listOf("Alpha"),
                onValueChange = {},
                open = false,
                onOpenChange = if (useFirstCallback) first else second,
            ) {
                ComboboxInput(Modifier.testTag("editable-input"), editable = true, showTrigger = false)
            }
        }
        val input = composeRule.onNodeWithTag("editable-input")
        input.performMouseInput { moveTo(center) }
        composeRule.runOnIdle { useFirstCallback = false }
        input.performMouseInput { click(center) }
        composeRule.runOnIdle { assertEquals(listOf("second"), requests) }
    }

    @Test fun customIconSearchInheritsRootDisabled() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Combobox<String>(null, listOf("Alpha"), {}, disabled = true) {
                ComboboxInput(icon = { Text { "Search" } })
            }
        }
        composeRule.onNodeWithText("Search...").assertIsNotEnabled()
    }

    @Test fun customIconQueryChangeClearsPreviousKeyboardHighlight() {
        var picked: String? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Combobox<String>(null, listOf("Alpha", "Beta"), { picked = it }, defaultOpen = true) {
                ComboboxContent {
                    ComboboxInput(icon = { Text { "Search" } })
                    ComboboxList {
                        ComboboxItem("Alpha", "Alpha")
                        ComboboxItem("Beta", "Beta")
                    }
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).performClick()
        composeRule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNode(hasSetTextAction()).performTextInput("a")
        composeRule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals("Alpha", picked) }
    }

    @Test fun enterRevalidatesDisabledHighlightedOption() {
        var disable by mutableStateOf(false)
        var picked: String? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Combobox<String>(null, listOf("Alpha", "Beta"), { picked = it }, defaultOpen = true) {
                ComboboxInput(editable = true)
                ComboboxContent {
                    ComboboxItem("Alpha", "Alpha", disabled = disable)
                    ComboboxItem("Beta", "Beta")
                }
            }
        }
        composeRule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.runOnIdle { disable = true }
        composeRule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertNull(picked) }
    }
}
