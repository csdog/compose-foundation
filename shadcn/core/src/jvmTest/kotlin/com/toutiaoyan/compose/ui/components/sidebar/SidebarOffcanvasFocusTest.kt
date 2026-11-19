package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SidebarOffcanvasFocusTest : ScreenshotTest() {
    @Test fun sidebarChildIsSkippedAfterClosingTheOffcanvasRail() {
        var open by mutableStateOf(true)
        setThemedContent {
            Column {
                Button("Before", modifier = Modifier.testTag("before"))
                SidebarProvider(open = open, mobile = false, modifier = Modifier.size(560.dp, 180.dp)) {
                    Sidebar { Button("Rail command", modifier = Modifier.testTag("rail-command")) }
                    SidebarInset { Button("Content command", modifier = Modifier.testTag("content-command")) }
                }
            }
        }
        val before = composeRule.onNodeWithTag("before")
        before.requestFocus().performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithTag("rail-command").assertIsFocused()
        composeRule.runOnIdle { open = false }
        before.requestFocus().performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithTag("content-command").assertIsFocused()
            .performKeyInput { withKeyDown(Key.ShiftLeft) { pressKey(Key.Tab) } }
        before.assertIsFocused()
    }

    @Test fun closingTheSidebarClearsFocusFromItsUnplacedCommands() {
        var open by mutableStateOf(true)
        var commandFocused = false
        setThemedContent {
            SidebarProvider(open = open, mobile = false, modifier = Modifier.size(560.dp, 180.dp)) {
                Sidebar {
                    Button(
                        "Rail command",
                        modifier = Modifier.testTag("rail-command").onFocusChanged { commandFocused = it.isFocused },
                    )
                }
                SidebarInset { Button("Content command") }
            }
        }
        composeRule.onNodeWithTag("rail-command").requestFocus()
        composeRule.runOnIdle {
            assertTrue(commandFocused)
            open = false
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle { assertFalse(commandFocused) }
    }
}
