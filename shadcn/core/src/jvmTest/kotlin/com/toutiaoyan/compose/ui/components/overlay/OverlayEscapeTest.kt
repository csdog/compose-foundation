package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.sheet.Sheet
import com.toutiaoyan.compose.ui.components.sheet.SheetContent
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertFalse

class OverlayEscapeTest {
    @get:Rule val rule = createComposeRule()

    @Test fun dialogClosesFromFocusedInput() = checkEscape(sheet = false)

    @Test fun sheetClosesFromFocusedInput() = checkEscape(sheet = true)

    @Test
    fun positionalOpenStillFocusesFirstInput() {
        rule.setContent {
            Theme {
                Dialog(true) {
                    DialogContent {
                        Input(value = "", modifier = Modifier.testTag("search"))
                    }
                }
            }
        }
        rule.onNodeWithTag("search").assertIsFocused()
    }

    @Test
    fun dialogCanKeepInitialFocusOnPanelUntilTab() {
        rule.setContent {
            Theme {
                Dialog(focusPanelOnOpen = true, open = true) {
                    DialogContent {
                        Input(value = "", modifier = Modifier.testTag("search"))
                    }
                }
            }
        }
        val search = rule.onNodeWithTag("search").assertIsNotFocused()
        search.performKeyInput { pressKey(Key.Tab) }
        search.assertIsFocused()
    }

    private fun checkEscape(sheet: Boolean) {
        var open by mutableStateOf(true)
        rule.setContent {
            Theme {
                if (sheet) {
                    Sheet(open = open, onOpenChange = { open = it }) {
                        SheetContent { Input(value = "", autoFocus = true, modifier = Modifier.testTag("input")) }
                    }
                } else {
                    Dialog(open = open, onOpenChange = { open = it }) {
                        DialogContent { Input(value = "", autoFocus = true, modifier = Modifier.testTag("input")) }
                    }
                }
            }
        }
        rule.onNodeWithTag("input").performKeyInput { pressKey(Key.Escape) }
        rule.waitForIdle()
        assertFalse(open)
        rule.onNodeWithTag("input").assertDoesNotExist()
    }
}
