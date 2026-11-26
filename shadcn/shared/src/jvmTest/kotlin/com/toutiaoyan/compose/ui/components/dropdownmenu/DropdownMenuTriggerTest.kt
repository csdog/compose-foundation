@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test

class DropdownMenuTriggerTest {
    @get:Rule val rule = createComposeRule()

    @Test fun stringTriggerHasOneTabStopAndKeepsLabelAndModifier() {
        content {
            Column {
                Button("Before")
                DropdownMenu {
                    DropdownMenuTrigger("Open", modifier = Modifier.testTag("trigger"))
                }
                Button("After")
            }
        }
        rule.onAllNodes(hasClickAction(), useUnmergedTree = true).assertCountEquals(3)
        rule.onNodeWithContentDescription("Before").requestFocus().performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithTag("trigger").assertIsFocused().assertContentDescriptionEquals("Open")
        rule.onNodeWithTag("trigger").performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithContentDescription("After").assertIsFocused()
    }

    @Test fun keyboardOpenIntentAndEscapeRestoreTheButton() {
        content {
            DropdownMenu {
                DropdownMenuTrigger("Open", variant = ButtonVariant.Outline)
                DropdownMenuContent {
                    DropdownMenuItem("First", {})
                    DropdownMenuItem("Last", {})
                }
            }
        }
        for (key in listOf(Key.Enter, Key.Spacebar, Key.DirectionDown, Key.DirectionUp)) {
            rule.onNodeWithContentDescription("Open").requestFocus().performKeyInput { pressKey(key) }
            rule.onNodeWithContentDescription("Open")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
            rule.onNodeWithText(if (key == Key.DirectionUp) "Last" else "First")
                .assertIsFocused().performKeyInput { pressKey(Key.Escape) }
            rule.onNodeWithText("First").assertDoesNotExist()
            rule.onNodeWithContentDescription("Open").assertIsFocused()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        }
    }

    @Test fun escapeClosesAnAlreadyOpenRefocusedNonModalTriggerOnce() {
        val triggerFocus = FocusRequester()
        val openChanges = mutableListOf<Boolean>()
        content {
            DropdownMenu(modal = false, onOpenChange = { openChanges += it }) {
                DropdownMenuTrigger("Open", modifier = Modifier.focusRequester(triggerFocus))
                DropdownMenuContent { DropdownMenuItem("First", {}) }
            }
        }
        rule.onNodeWithContentDescription("Open").requestFocus()
            .performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("First").assertIsFocused()
        rule.runOnIdle { triggerFocus.requestFocus() }
        rule.onNodeWithContentDescription("Open").assertIsFocused()
            .performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("First").assertDoesNotExist()
        rule.onNodeWithContentDescription("Open").assertIsFocused()
        rule.runOnIdle { org.junit.Assert.assertEquals(listOf(true, false), openChanges) }
    }

    @Test fun genericNonModalTriggerWithoutHostClosesOnceAndLeavesOtherKeysAlone() {
        val triggerFocus = FocusRequester()
        val openChanges = mutableListOf<Boolean>()
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.size(560.dp, 400.dp), contentAlignment = Alignment.Center) {
                    DropdownMenu(modal = false, onOpenChange = { openChanges += it }) {
                        DropdownMenuTrigger(modifier = Modifier.focusRequester(triggerFocus).testTag("generic")) {
                            com.toutiaoyan.compose.ui.components.text.Text { "Open" }
                        }
                        DropdownMenuContent { DropdownMenuItem("First", {}) }
                    }
                }
            }
        }
        rule.onNodeWithTag("generic").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("First").assertIsFocused()
        rule.runOnIdle { triggerFocus.requestFocus() }
        rule.onNodeWithTag("generic").assertIsFocused().performKeyInput { pressKey(Key.A) }
        rule.onNodeWithText("First").assertExists()
        rule.runOnIdle { org.junit.Assert.assertEquals(listOf(true), openChanges) }
        rule.onNodeWithTag("generic").performKeyInput { keyDown(Key.Escape) }
        rule.onNodeWithText("First").assertDoesNotExist()
        rule.runOnIdle { org.junit.Assert.assertEquals(listOf(true, false), openChanges) }
        rule.onNodeWithTag("generic").performKeyInput {
            keyUp(Key.Escape)
            pressKey(Key.Escape)
        }
        rule.onNodeWithTag("generic").assertIsFocused()
        rule.runOnIdle { org.junit.Assert.assertEquals(listOf(true, false), openChanges) }
    }

    @Test fun refocusedNonModalTriggerClosesDeepestSubmenuBeforeRoot() {
        val triggerFocus = FocusRequester()
        val openChanges = mutableListOf<Boolean>()
        val childChanges = mutableListOf<Boolean>()
        val grandchildChanges = mutableListOf<Boolean>()
        content {
            DropdownMenu(modal = false, onOpenChange = { openChanges += it }) {
                DropdownMenuTrigger("Open", modifier = Modifier.focusRequester(triggerFocus))
                DropdownMenuContent {
                    DropdownMenuSub(onOpenChange = { childChanges += it }) {
                        DropdownMenuSubTrigger("Level one")
                        DropdownMenuSubContent {
                            DropdownMenuSub(onOpenChange = { grandchildChanges += it }) {
                                DropdownMenuSubTrigger("Level two")
                                DropdownMenuSubContent { DropdownMenuItem("Leaf", {}) }
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithContentDescription("Open").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("Level one").performTouchInput { click() }
        rule.onNodeWithText("Level two").performTouchInput { click() }
        rule.onNodeWithText("Leaf").assertExists()
        rule.runOnIdle { triggerFocus.requestFocus() }
        rule.onNodeWithContentDescription("Open").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Leaf").assertDoesNotExist()
        rule.onNodeWithText("Level two").assertExists()
        rule.runOnIdle {
            org.junit.Assert.assertEquals(listOf(true), openChanges)
            org.junit.Assert.assertEquals(0, childChanges.count { !it })
            org.junit.Assert.assertEquals(1, grandchildChanges.count { !it })
            triggerFocus.requestFocus()
        }
        rule.onNodeWithContentDescription("Open").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Level two").assertDoesNotExist()
        rule.onNodeWithText("Level one").assertExists()
        rule.runOnIdle {
            org.junit.Assert.assertEquals(listOf(true), openChanges)
            org.junit.Assert.assertEquals(1, childChanges.count { !it })
            org.junit.Assert.assertEquals(1, grandchildChanges.count { !it })
            triggerFocus.requestFocus()
        }
        rule.onNodeWithContentDescription("Open").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Level one").assertDoesNotExist()
        rule.runOnIdle { org.junit.Assert.assertEquals(listOf(true, false), openChanges) }
    }

    @Test fun enterClosesAnAlreadyOpenRefocusedNonModalTriggerOnce() {
        assertActivationClosesRefocusedTrigger(Key.Enter)
    }

    @Test fun spaceClosesAnAlreadyOpenRefocusedNonModalTriggerOnce() {
        assertActivationClosesRefocusedTrigger(Key.Spacebar)
    }

    private fun assertActivationClosesRefocusedTrigger(key: Key) {
        val triggerFocus = FocusRequester()
        val openChanges = mutableListOf<Boolean>()
        content {
            DropdownMenu(modal = false, onOpenChange = { openChanges += it }) {
                DropdownMenuTrigger("Open", modifier = Modifier.focusRequester(triggerFocus))
                DropdownMenuContent { DropdownMenuItem("First", {}) }
            }
        }
        rule.onNodeWithContentDescription("Open").requestFocus()
            .performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("First").assertIsFocused()
        rule.runOnIdle { triggerFocus.requestFocus() }
        rule.onNodeWithContentDescription("Open").assertIsFocused()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
            .performKeyInput { keyDown(key) }
        rule.onNodeWithContentDescription("Open")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        rule.onNodeWithText("First").assertDoesNotExist()
        rule.runOnIdle { org.junit.Assert.assertEquals(listOf(true, false), openChanges) }
        rule.onNodeWithContentDescription("Open").performKeyInput { keyUp(key) }
        rule.onNodeWithContentDescription("Open").assertIsFocused()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        rule.onNodeWithText("First").assertDoesNotExist()
        rule.runOnIdle { org.junit.Assert.assertEquals(listOf(true, false), openChanges) }
    }

    @Test fun modalPointerReopenDuringExitKeepsContentAndKeyboardNavigation() {
        var mounts = 0
        var disposals = 0
        val changes = mutableListOf<Boolean>()
        rule.mainClock.autoAdvance = false
        content {
            DropdownMenu(onOpenChange = { changes += it }) {
                DropdownMenuTrigger("Open")
                DropdownMenuContent(modifier = Modifier.testTag("retained-menu")) {
                    DisposableEffect(Unit) {
                        mounts++
                        onDispose { disposals++ }
                    }
                    DropdownMenuItem("First", {})
                    DropdownMenuItem("Last", {})
                }
            }
        }
        rule.onNodeWithContentDescription("Open").performMouseInput { click() }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(160)
        rule.onNodeWithTag("retained-menu").performKeyInput { pressKey(Key.DirectionDown) }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithText("First").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.mainClock.advanceTimeByFrame()
        rule.mainClock.advanceTimeBy(16)
        rule.waitForIdle()
        rule.onNodeWithText("First").assertExists()
        rule.onNodeWithContentDescription("Open").assertIsFocused()
            .performMouseInput { click() }
        // With autoAdvance disabled, input requests do not recompose trigger semantics.
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Open")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithTag("retained-menu").performKeyInput { pressKey(Key.DirectionDown) }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithText("First").assertIsFocused()
            .performKeyInput { pressKey(Key.DirectionDown) }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
        rule.onNodeWithText("Last").assertIsFocused()
        rule.mainClock.advanceTimeBy(160)
        rule.runOnIdle {
            org.junit.Assert.assertEquals(listOf(true, false, true), changes)
            org.junit.Assert.assertEquals(1, mounts)
            org.junit.Assert.assertEquals(0, disposals)
        }
    }

    @Test fun pointerOpenDoesNotHighlightARowAndSelectionCloses() {
        var selected = false
        content {
            DropdownMenu {
                DropdownMenuTrigger("Open")
                DropdownMenuContent { DropdownMenuItem("Choose", { selected = true }) }
            }
        }
        rule.onNodeWithContentDescription("Open").performTouchInput { click() }
        rule.onNodeWithText("Choose").assertIsNotFocused().performClick()
        rule.onNodeWithText("Choose").assertDoesNotExist()
        rule.runOnIdle { org.junit.Assert.assertTrue(selected) }
    }

    @Test fun bothDisabledInputsRejectPointerAndKeyboard() {
        content {
            Column {
                DropdownMenu {
                    DropdownMenuTrigger("Disabled", disabled = true)
                    DropdownMenuContent { DropdownMenuItem("Disabled child", {}) }
                }
                DropdownMenu {
                    DropdownMenuTrigger("Not enabled", enabled = false)
                    DropdownMenuContent { DropdownMenuItem("Not enabled child", {}) }
                }
            }
        }
        for (label in listOf("Disabled", "Not enabled")) {
            rule.onNodeWithContentDescription(label).assertIsNotEnabled()
                .performTouchInput { click() }
                .performKeyInput {
                    pressKey(Key.DirectionDown)
                    pressKey(Key.Enter)
                    pressKey(Key.Spacebar)
                }
        }
        rule.onNodeWithText("Disabled child").assertDoesNotExist()
        rule.onNodeWithText("Not enabled child").assertDoesNotExist()
    }

    @Test fun stringTriggerStyleAppliesToButtonChrome() {
        content {
            DropdownMenu {
                DropdownMenuTrigger("Open", style = Style { width(180.dp) })
            }
        }
        rule.onNodeWithContentDescription("Open").assertWidthIsEqualTo(180.dp)
    }

    private fun content(body: @Composable () -> Unit) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(560.dp, 400.dp), contentAlignment = Alignment.Center) { body() }
                }
            }
        }
    }
}
