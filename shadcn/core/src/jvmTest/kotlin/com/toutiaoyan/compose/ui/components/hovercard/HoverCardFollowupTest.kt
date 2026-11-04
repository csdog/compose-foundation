@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class HoverCardFollowupTest : ScreenshotTest() {
    private fun advance(ms: Long) {
        composeRule.mainClock.advanceTimeBy(ms)
        composeRule.waitForIdle()
    }

    @Test fun keyboardBlurClosesWithoutWaitingForHoverCloseDelay() {
        lateinit var mode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            mode = LocalInputModeManager.current
            Column {
                HoverCard {
                    HoverCardTrigger(delay = 0, closeDelay = 1000) { Button("Trigger") }
                    HoverCardContent(enter = EnterTransition.None, exit = ExitTransition.None) { Text { "Preview" } }
                }
                Button("After")
            }
        }
        composeRule.runOnIdle { mode.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithText("Trigger").requestFocus()
        composeRule.onNodeWithText("Preview").assertIsDisplayed()
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithText("After").requestFocus()
        advance(48)
        composeRule.onNodeWithText("Preview").assertDoesNotExist()
        composeRule.onNodeWithText("After").assertIsFocused()
    }

    @Test fun focusingContentDoesNotDismissItAndLeavingContentClosesImmediately() {
        lateinit var mode: InputModeManager
        setThemedContent(stylePreset = StylePreset.Nova) {
            mode = LocalInputModeManager.current
            Column {
                HoverCard {
                    HoverCardTrigger(delay = 0, closeDelay = 1000) { Button("Trigger") }
                    HoverCardContent(enter = EnterTransition.None, exit = ExitTransition.None) { Button("Inside") }
                }
                Button("After")
            }
        }
        composeRule.runOnIdle { mode.requestInputMode(InputMode.Keyboard) }
        composeRule.onNodeWithText("Trigger").requestFocus()
        composeRule.onNodeWithText("Inside").requestFocus()
        composeRule.mainClock.autoAdvance = false
        advance(80)
        composeRule.onNodeWithText("Inside").assertIsDisplayed().assertIsFocused()
        composeRule.onNodeWithText("After").requestFocus()
        advance(48)
        composeRule.onNodeWithText("Inside").assertDoesNotExist()
    }

    @Test fun additionalRootContentDoesNotMoveThePopupAnchor() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            HoverCard(defaultOpen = true) {
                Column(Modifier.width(240.dp).height(240.dp)) {
                    HoverCardTrigger { Button("Trigger") }
                    Spacer(Modifier.height(150.dp))
                    Text { "Other root content" }
                }
                HoverCardContent(Modifier.testTag("panel"), enter = EnterTransition.None, exit = ExitTransition.None) {
                    Text { "Preview" }
                }
            }
        }
        val trigger = composeRule.onNodeWithText("Trigger").fetchSemanticsNode().boundsInWindow
        val popup = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow
        assertEquals(trigger.bottom + 4f, popup.top, 1f)
    }

    @Test fun movingTriggerInsideFixedRootRepositionsOpenPopup() {
        var top by mutableStateOf(16.dp)
        setThemedContent(stylePreset = StylePreset.Nova) {
            HoverCard(defaultOpen = true) {
                Column(Modifier.width(240.dp).height(240.dp)) {
                    Spacer(Modifier.height(top))
                    HoverCardTrigger { Button("Trigger") }
                }
                HoverCardContent(Modifier.testTag("panel"), enter = EnterTransition.None, exit = ExitTransition.None) {
                    Text { "Preview" }
                }
            }
        }
        val before = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.top
        composeRule.runOnIdle { top = 112.dp }
        val after = composeRule.onNodeWithTag("panel").fetchSemanticsNode().boundsInWindow.top
        assertEquals(96f, after - before, 1f)
    }
}
