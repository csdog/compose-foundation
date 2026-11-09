package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.text.Text
import kotlin.test.Test
import kotlin.test.assertEquals

class PopoverBehaviorTest : ScreenshotTest() {
    @Test fun nonModalEscapeFromTriggerClosesUncontrolledPopover() {
        setThemedContent {
            Popover(modal = false) {
                PopoverTrigger("Open")
                PopoverContent("Body", enter = EnterTransition.None, exit = ExitTransition.None)
            }
        }
        composeRule.onNodeWithText("Open").performClick().requestFocus()
        composeRule.onNodeWithText("Body").assertIsDisplayed()
        composeRule.onNodeWithText("Open").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Body").assertDoesNotExist()
        composeRule.onNodeWithText("Open").assertIsFocused()
    }

    @Test fun nonModalEscapeUsesLatestCallbackAndRespectsControlledRefusal() {
        var revision by mutableStateOf("old")
        val calls = mutableListOf<String>()
        setThemedContent {
            val current = revision
            Popover(open = true, modal = false, onOpenChange = { calls.add("$current:$it") }) {
                PopoverTrigger("Open")
                PopoverContent("Body", enter = EnterTransition.None, exit = ExitTransition.None)
            }
        }
        composeRule.runOnIdle { revision = "new" }
        composeRule.onNodeWithText("Open").requestFocus().performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle { assertEquals(listOf("new:false"), calls) }
        composeRule.onNodeWithText("Body").assertIsDisplayed()
    }

    @Test fun modalInitialFocusSkipsDisabledChildAndEntersFirstEnabledChild() {
        setThemedContent {
            Popover(defaultOpen = true) {
                PopoverTrigger("Open")
                PopoverContent(enter = EnterTransition.None, exit = ExitTransition.None) {
                    Button("Disabled", enabled = false)
                    Button("First")
                    Button("Second")
                }
            }
        }
        composeRule.onNodeWithText("First").assertIsFocused()
        composeRule.onNodeWithText("Disabled").assertIsNotEnabled()
        composeRule.onNodeWithText("Second").assertIsNotFocused()
    }

    @Test fun modalPanelWithoutFocusableChildrenKeepsFallbackFocus() {
        setThemedContent {
            Popover(defaultOpen = true) {
                PopoverTrigger("Open")
                PopoverContent(
                    modifier = Modifier.testTag("panel"),
                    enter = EnterTransition.None,
                    exit = ExitTransition.None,
                ) { Text { "Body" } }
            }
        }
        composeRule.onNodeWithTag("panel").assertIsFocused()
    }

    @Test fun modalInitialFocusPreservesExplicitAutoFocus() {
        setThemedContent {
            Popover(defaultOpen = true) {
                PopoverTrigger("Open")
                PopoverContent(enter = EnterTransition.None, exit = ExitTransition.None) {
                    Button("First")
                    Input("", modifier = Modifier.testTag("input"), autoFocus = true)
                }
            }
        }
        composeRule.onNodeWithTag("input").assertIsFocused()
        composeRule.onNodeWithText("First").assertIsNotFocused()
    }

    @Test fun touchOpeningFocusesPanelAndKeyboardReopeningFocusesChild() {
        setThemedContent {
            Popover {
                PopoverTrigger("Open")
                PopoverContent(
                    modifier = Modifier.testTag("panel"),
                    enter = EnterTransition.None,
                    exit = ExitTransition.None,
                ) { Button("First") }
            }
        }
        composeRule.onNodeWithText("Open").performTouchInput { click() }
        composeRule.onNodeWithTag("panel").assertIsFocused()
        composeRule.onNodeWithText("First").assertIsNotFocused()
        composeRule.onNodeWithTag("panel").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
        composeRule.onNodeWithText("Open").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithText("First").assertIsFocused()
    }

    @Test fun nestedNonModalEscapeClosesOnlyTheChildThenFallsBackToParent() {
        setThemedContent {
            Popover(defaultOpen = true) {
                PopoverTrigger("Parent trigger")
                PopoverContent(enter = EnterTransition.None, exit = ExitTransition.None) {
                    Text { "Parent body" }
                    Popover(defaultOpen = true, modal = false) {
                        PopoverTrigger("Child trigger")
                        PopoverContent("Child body", enter = EnterTransition.None, exit = ExitTransition.None)
                    }
                }
            }
        }
        composeRule.onNodeWithText("Child trigger").requestFocus().performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Child body").assertDoesNotExist()
        composeRule.onNodeWithText("Parent body").assertIsDisplayed()
        composeRule.onNodeWithText("Child trigger").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Parent body").assertDoesNotExist()
    }
}
