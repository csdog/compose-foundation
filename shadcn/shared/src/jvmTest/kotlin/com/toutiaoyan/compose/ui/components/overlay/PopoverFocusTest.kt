package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.popover.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class PopoverFocusTest {
    @get:Rule val rule = createComposeRule()

    @Test fun pointerOpenedButtonTriggerRegainsFocusAfterEscape() = checkReturn(buttonChild = true)

    @Test fun pointerOpenedTextTriggerRegainsFocusAfterEscape() = checkReturn(buttonChild = false)

    private fun checkReturn(buttonChild: Boolean) {
        lateinit var mode: InputModeManager
        rule.setContent {
            Theme {
                mode = LocalInputModeManager.current
                Column {
                    Button("Previous", {}, Modifier.testTag("previous"))
                    Popover {
                        PopoverTrigger("First", modifier = Modifier.testTag("first"))
                        if (buttonChild) {
                            PopoverTrigger {
                                Button("Second", toggle, Modifier.testTag("second"))
                            }
                        } else {
                            PopoverTrigger(Modifier.testTag("second")) { Text { "Second" } }
                        }
                        PopoverContent { Input("", autoFocus = true, modifier = Modifier.testTag("input")) }
                    }
                }
            }
        }
        rule.onNodeWithTag("previous").requestFocus()
        rule.onNodeWithTag("second").performMouseInput { click() }
        rule.waitForIdle()
        // Popup and owner have separate focus owners. Move the owner's retained
        // focus so an early return request is observable during the exit frame.
        rule.onNodeWithTag("previous").requestFocus()
        rule.mainClock.autoAdvance = false
        rule.onNodeWithTag("input").performKeyInput { pressKey(Key.Escape) }
        rule.mainClock.advanceTimeByFrame()
        rule.onNodeWithTag("input").assertExists()
        rule.onNodeWithTag("second").assertIsNotFocused()
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        rule.onNodeWithTag("input").assertDoesNotExist()
        rule.onNodeWithTag("second").assertIsFocused()
        rule.onNodeWithTag("previous").assertIsNotFocused()
        assertEquals(InputMode.Keyboard, mode.inputMode)
    }
}
