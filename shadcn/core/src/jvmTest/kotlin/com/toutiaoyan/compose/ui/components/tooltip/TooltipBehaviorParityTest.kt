package com.toutiaoyan.compose.ui.components.tooltip

import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TooltipBehaviorParityTest : ScreenshotTest() {
    @Test fun touchModeClearsTheTooltipWhileTheTriggerRetainsFocus() {
        var mode by mutableStateOf(InputMode.Keyboard)
        val manager =
            object : InputModeManager {
                override val inputMode: InputMode get() = mode

                override fun requestInputMode(inputMode: InputMode): Boolean {
                    mode = inputMode
                    return true
                }
            }
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalInputModeManager provides manager) {
                Tooltip {
                    TooltipTrigger("Hover")
                    TooltipContent("Details")
                }
            }
        }
        composeRule.onNodeWithText("Hover").requestFocus()
        composeRule.onNodeWithText("Details").assertIsDisplayed()
        composeRule.runOnIdle { mode = InputMode.Touch }
        composeRule.onNodeWithText("Details").assertDoesNotExist()
        composeRule.runOnIdle { mode = InputMode.Keyboard }
        composeRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test fun removingHoveredContentClearsItsActivity() {
        val source = MutableInteractionSource()
        val hover = HoverInteraction.Enter()
        var showContent by mutableStateOf(true)
        val changes = mutableListOf<Boolean>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            TooltipProvider {
                Tooltip(interactionSource = source, onOpenChange = { changes.add(it) }) {
                    TooltipTrigger("Hover")
                    if (showContent) TooltipContent("Details")
                }
            }
        }
        composeRule.runOnIdle { source.tryEmit(hover) }
        composeRule.onNodeWithText("Details").performMouseInput { enter(center) }
        composeRule.runOnIdle { source.tryEmit(HoverInteraction.Exit(hover)) }
        composeRule.onNodeWithText("Details").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(listOf(true), changes)
            showContent = false
        }
        composeRule.runOnIdle { assertEquals(listOf(true, false), changes) }
    }

    @Test fun mountingDoesNotReportAnOpenChange() {
        val changes = mutableListOf<Boolean>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Tooltip(onOpenChange = { changes.add(it) }) {
                TooltipTrigger("Hover")
                TooltipContent("Details")
            }
        }
        composeRule.runOnIdle { assertTrue(changes.isEmpty(), "Mounting is not an interaction: $changes") }
    }

    @Test fun keyboardFocusOpensWithoutTheHoverDelay() {
        composeRule.mainClock.autoAdvance = false
        lateinit var inputMode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            inputMode = LocalInputModeManager.current
            TooltipProvider(delayMillis = 1000, skipDelayMillis = 0) {
                Tooltip {
                    TooltipTrigger("Hover")
                    TooltipContent("Details")
                }
            }
        }
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithText("Hover").requestFocus()
        composeRule.mainClock.advanceTimeBy(320)
        composeRule.onNodeWithText("Details").assertIsDisplayed()
        composeRule.onNodeWithText("Hover").assertIsFocused()
    }

    @Test fun escapeClosesWithoutMovingFocusOrReopening() {
        lateinit var inputMode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            inputMode = LocalInputModeManager.current
            Tooltip {
                TooltipTrigger("Hover")
                TooltipContent("Details")
            }
        }
        composeRule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithText("Hover").requestFocus()
        composeRule.onNodeWithText("Details").assertIsDisplayed()
        composeRule.onNodeWithText("Hover").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Details").assertDoesNotExist()
        composeRule.onNodeWithText("Hover").assertIsFocused()
    }

    @Test fun controlledTooltipRequestsCloseOnlyAfterEscape() {
        val changes = mutableListOf<Boolean>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.size(300.dp)) {
                Tooltip(open = true, onOpenChange = { changes.add(it) }) {
                    TooltipTrigger("Hover")
                    TooltipContent("Details")
                }
            }
        }
        composeRule.onNodeWithText("Hover").requestFocus()
        composeRule.runOnIdle { changes.clear() }
        composeRule.onNodeWithText("Hover").performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle { assertEquals(listOf(false), changes) }
        composeRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test fun firstHoverWaitsForTheConfiguredDelay() {
        composeRule.mainClock.autoAdvance = false
        val source = MutableInteractionSource()
        setThemedContent(stylePreset = StylePreset.Nova) {
            TooltipProvider(delayMillis = 1000) {
                Tooltip(interactionSource = source) {
                    TooltipTrigger("Hover")
                    TooltipContent("Details")
                }
            }
        }
        composeRule.runOnIdle { source.tryEmit(HoverInteraction.Enter()) }
        composeRule.mainClock.advanceTimeBy(400)
        composeRule.onNodeWithText("Details").assertDoesNotExist()
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test fun escapeSuppressesTheCurrentHoverUntilItLeaves() {
        val source = MutableInteractionSource()
        val enter = HoverInteraction.Enter()
        setThemedContent(stylePreset = StylePreset.Nova) {
            TooltipProvider {
                Tooltip(interactionSource = source) {
                    TooltipTrigger("Hover")
                    TooltipContent("Details")
                }
                com.toutiaoyan.compose.ui.components.button.Button("Next")
            }
        }
        composeRule.runOnIdle { source.tryEmit(enter) }
        composeRule.onNodeWithText("Details").assertIsDisplayed()
        composeRule.onNodeWithText("Hover").requestFocus()
        composeRule.onNodeWithText("Hover").performKeyInput { pressKey(Key.Escape) }
        composeRule.onNodeWithText("Details").assertDoesNotExist()
        // The focus interaction ends before a fresh mouse hover can reopen.
        composeRule.onNodeWithText("Hover").performKeyInput { pressKey(Key.Tab) }
        composeRule.runOnIdle { source.tryEmit(HoverInteraction.Exit(enter)) }
        composeRule.waitForIdle()
        composeRule.runOnIdle { source.tryEmit(HoverInteraction.Enter()) }
        composeRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test fun newlyOpenedSiblingClosesThePreviousHover() {
        val source = MutableInteractionSource()
        setThemedContent(stylePreset = StylePreset.Nova) {
            TooltipProvider {
                androidx.compose.foundation.layout.Column {
                    Tooltip(interactionSource = source) {
                        TooltipTrigger("First")
                        TooltipContent("First details")
                    }
                    Tooltip {
                        TooltipTrigger("Second")
                        TooltipContent("Second details")
                    }
                }
            }
        }
        composeRule.runOnIdle { source.tryEmit(HoverInteraction.Enter()) }
        composeRule.onNodeWithText("First details").assertIsDisplayed()
        composeRule.onNodeWithText("Second").requestFocus()
        composeRule.onNodeWithText("Second details").assertIsDisplayed()
        composeRule.onNodeWithText("First details").assertDoesNotExist()
    }

    @Test fun newlyHoveredSiblingClosesTheFocusedTooltipWithoutStealingFocus() {
        val source = MutableInteractionSource()
        setThemedContent(stylePreset = StylePreset.Nova) {
            TooltipProvider {
                androidx.compose.foundation.layout.Column {
                    Tooltip {
                        TooltipTrigger("First")
                        TooltipContent("First details")
                    }
                    Tooltip(interactionSource = source) {
                        TooltipTrigger("Second")
                        TooltipContent("Second details")
                    }
                }
            }
        }
        composeRule.onNodeWithText("First").requestFocus()
        composeRule.onNodeWithText("First details").assertIsDisplayed()
        composeRule.runOnIdle { source.tryEmit(HoverInteraction.Enter()) }
        composeRule.onNodeWithText("Second details").assertIsDisplayed()
        composeRule.onNodeWithText("First details").assertDoesNotExist()
        composeRule.onNodeWithText("First").assertIsFocused()
    }
}
