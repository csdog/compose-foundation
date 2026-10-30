package com.toutiaoyan.compose.ui.components.trace

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class TraceParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("trace_parity", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Trace(Modifier.width(384.dp)) {
                TraceLine {
                    TraceIcon { Icon(Check) }
                    TraceTitle("Searched the web")
                    TraceDescription("2.4s")
                }
                TraceLine(status = TraceStatus.Pending) { TraceTitle("Waiting for results") }
                TraceLine(status = TraceStatus.Error) { TraceTitle("Unable to read page") }
                TraceCollapsible(defaultOpen = true) {
                    TraceCollapsibleTrigger { TraceLine { TraceTitle("Read 3 pages") } }
                    TraceCollapsibleContent { Text { "Reviewed documentation and examples." } }
                }
            }
        }

    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    @Test fun uncontrolledTriggerExpandsAndCollapsesDetails() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithText("Details").assertDoesNotExist()
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.onNodeWithText("Details").assertIsDisplayed()
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.onNodeWithText("Details").assertDoesNotExist()
    }

    @Test fun controlledTriggerOnlyRequestsStateChange() {
        var requested: Boolean? = null
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible(open = false, onOpenChange = { requested = it }) {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").performClick()
        assertEquals(true, requested)
        composeRule.onNodeWithText("Details").assertDoesNotExist()
    }

    @Test fun keyboardEnterExpandsDetails() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test fun disabledTriggerCannotExpand() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Trace {
                    TraceCollapsible(enabled = false) {
                        TraceCollapsibleTrigger(Modifier.testTag("trigger")) { TraceLine { TraceTitle("Read pages") } }
                        TraceCollapsibleContent { Text { "Details" } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").assertIsNotEnabled().performClick()
        composeRule.onNodeWithText("Details").assertDoesNotExist()
    }
}
