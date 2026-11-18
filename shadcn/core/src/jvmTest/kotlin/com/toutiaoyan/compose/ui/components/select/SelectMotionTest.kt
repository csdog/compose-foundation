@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class SelectMotionTest : ScreenshotTest() {
    private var open by mutableStateOf(true)

    private fun show(
        aligned: Boolean = false,
        explicit: Boolean = false,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select<String>("Alpha", {}, open = open, onOpenChange = { open = it }) {
                    SelectTrigger()
                    val body: @Composable SelectContentScope.() -> Unit = {
                        SelectGroup {
                            SelectItem("Alpha")
                            SelectItem("Beta")
                        }
                    }
                    if (explicit) {
                        SelectContent(
                            Modifier.testTag("panel"),
                            alignItemWithTrigger = aligned,
                            style = Style { width(200.dp) },
                            enter = EnterTransition.None,
                            exit = PopupTransitions.Exit,
                            content = body,
                        )
                    } else {
                        SelectContent(
                            Modifier.testTag("panel"),
                            alignItemWithTrigger = aligned,
                            style = Style { width(200.dp) },
                            content = body,
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("panel").assertExists()
        composeRule.mainClock.autoAdvance = false
    }

    private fun close() {
        composeRule.runOnIdle { open = false }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
    }

    @Test fun normalDefaultExitRetainsPanelForOneHundredMilliseconds() {
        show()
        close()
        composeRule.mainClock.advanceTimeBy(48)
        composeRule.onNodeWithTag("panel").assertExists()
        composeRule.mainClock.advanceTimeBy(100)
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
    }

    @Test fun explicitSharedExitKeepsItsLongerDuration() {
        show(explicit = true)
        close()
        composeRule.mainClock.advanceTimeBy(160)
        composeRule.onNodeWithTag("panel").assertExists()
        composeRule.mainClock.advanceTimeBy(160)
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
    }

    @Test fun reversingExitKeepsTheSamePanel() {
        show()
        val identity = composeRule.onNodeWithTag("panel").fetchSemanticsNode().id
        close()
        composeRule.mainClock.advanceTimeBy(32)
        composeRule.onNodeWithTag("panel").assertExists()
        composeRule.runOnIdle { open = true }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(160)
        assertEquals(identity, composeRule.onNodeWithTag("panel").fetchSemanticsNode().id)
        close()
        composeRule.mainClock.advanceTimeBy(160)
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
    }

    @Test fun choosingAnotherValueDoesNotRepositionTheRetainedExit() {
        var selected by mutableStateOf("Option 0")
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 200.dp)) {
                Select<String>(selected, { selected = it }, open = open, onOpenChange = { open = it }) {
                    SelectTrigger()
                    SelectContent(
                        Modifier.testTag("panel"),
                        enter = EnterTransition.None,
                        exit = PopupTransitions.Exit,
                    ) {
                        SelectGroup { repeat(30) { SelectItem("Option $it") } }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNode(hasText("Option 0") and hasAnyAncestor(hasTestTag("panel")))
            .performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithText("Option 29").assertIsFocused().assertIsDisplayed()
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithText("Option 29").performKeyInput { pressKey(Key.Enter) }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(48)
        assertEquals("Option 29", selected)
        assertEquals(before, composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow)
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
    }

    @Test fun normalPanelAboveTriggerKeepsItsGapWithCallerHeight() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Box(Modifier.padding(top = 600.dp)) {
                Select<String>("Option 29", {}, defaultOpen = true) {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent(
                        Modifier.testTag("panel"),
                        alignItemWithTrigger = false,
                        style = Style { height(100.dp) },
                        enter = EnterTransition.None,
                        exit = ExitTransition.None,
                    ) {
                        SelectGroup { repeat(30) { SelectItem("Option $it") } }
                    }
                }
            }
        }
        val trigger = composeRule.onNodeWithTag("trigger").fetchSemanticsNode().boundsInWindow
        val panel = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(100f, panel.height, .5f)
        assertEquals(trigger.top - 4f, panel.bottom, .5f)
        composeRule.onAllNodesWithText("Option 29").onLast().assertIsDisplayed()
    }

    @Test fun alignedDefaultExitIsImmediate() {
        show(aligned = true)
        close()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
    }

    @Test fun alignedExplicitExitStillHonorsCallerTransition() {
        show(aligned = true, explicit = true)
        close()
        composeRule.mainClock.advanceTimeBy(48)
        composeRule.onNodeWithTag("panel").assertExists()
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.onNodeWithTag("panel").assertDoesNotExist()
    }
}
