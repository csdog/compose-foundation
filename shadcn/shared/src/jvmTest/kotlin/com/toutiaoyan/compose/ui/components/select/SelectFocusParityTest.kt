@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Behavior established with the published Base UI 1.6 keyboard/pointer fixture. */
@RunWith(Parameterized::class)
class SelectFocusParityTest(private val modal: Boolean) : ScreenshotTest() {
    private var open = false
    private var picked: String? = null
    private var popupBackground = Color.Unspecified

    private fun show(
        pointer: Boolean = false,
        selected: String? = "Alpha",
        firstDisabled: Boolean = false,
        initialFocus: String = "alpha",
        acceptClose: () -> Boolean = { true },
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            popupBackground = Theme.current.colors.popover
            var expanded by remember { mutableStateOf(false) }
            Column {
                Button(onClick = {}, modifier = Modifier.testTag("before")) { Text { "Before" } }
                Select(
                    selected,
                    { picked = it },
                    open = expanded,
                    onOpenChange = {
                        if (it || acceptClose()) {
                            expanded = it
                            open = it
                        }
                    },
                    modal = modal,
                ) {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent {
                        SelectItem("Alpha", Modifier.testTag("alpha"), disabled = firstDisabled)
                        SelectItem("Beta", Modifier.testTag("beta"), disabled = true)
                        SelectItem("Gamma", Modifier.testTag("gamma"))
                    }
                }
                Button(onClick = {}, modifier = Modifier.testTag("after")) { Text { "After" } }
            }
        }
        if (pointer) {
            composeRule.onNodeWithTag("trigger").performMouseInput { click() }
        } else {
            composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        }
        composeRule.onNodeWithTag(initialFocus).assertIsFocused()
    }

    @Test fun selectedDisabledOptionGetsInitialFocusAndCannotBeActivated() {
        show(selected = "Beta", initialFocus = "beta")
        composeRule.onNodeWithTag("beta").assertIsNotEnabled().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle {
            assertTrue(open)
            assertEquals(null, picked)
        }
    }

    @Test fun alignedSelectWithoutAValueInitiallyFocusesTheFirstDisabledOption() {
        show(pointer = true, selected = null, firstDisabled = true)
        val trigger = composeRule.onNodeWithTag("trigger").fetchSemanticsNode().boundsInWindow
        val first = composeRule.onNodeWithTag("alpha").assertIsNotEnabled()
        assertEquals(trigger.center.y, first.fetchSemanticsNode().boundsInWindow.center.y, 1f)
        first.performKeyInput { pressKey(Key.Spacebar) }
        composeRule.runOnIdle {
            assertTrue(open)
            assertEquals(null, picked)
        }
    }

    @Test fun arrowsFocusDisabledItemsButActivationIsInert() {
        show()
        composeRule.onNodeWithTag("alpha").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("beta").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle {
            assertTrue(open)
            assertEquals(null, picked)
        }
        composeRule.onNodeWithTag("beta").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("gamma").assertIsFocused()
    }

    @Test fun tabClosesAndContinuesAfterTheTrigger() {
        show()
        composeRule.onNodeWithTag("alpha").performKeyInput { pressKey(Key.Tab) }
        composeRule.runOnIdle { assertFalse(open) }
        composeRule.onNodeWithTag("after").assertIsFocused()
    }

    @Test fun rejectedTabDoesNotChangeWhereAnAcceptedEscapeReturnsFocus() {
        var closeRequests = 0
        show(acceptClose = { ++closeRequests > 1 })
        composeRule.onNodeWithTag("alpha").performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithTag("alpha").assertIsFocused()
        composeRule.runOnIdle {
            assertTrue(open)
            assertEquals(1, closeRequests)
        }
        composeRule.onNodeWithTag("alpha").performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle {
            assertFalse(open)
            assertEquals(2, closeRequests)
        }
        composeRule.onNodeWithTag("trigger").assertIsFocused()
    }

    @Test fun shiftTabClosesAndReturnsToTheTrigger() {
        show()
        composeRule.onNodeWithTag("alpha").performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        composeRule.runOnIdle { assertFalse(open) }
        composeRule.onNodeWithTag("trigger").assertIsFocused()
    }

    @Test fun pointerMovementFocusesTheItemAndKeyboardCanMoveAway() {
        show()
        movePointerAndReturnWithKeyboard()
    }

    @Test fun pointerOpeningStillAllowsActualMouseFocus() {
        show(pointer = true)
        movePointerAndReturnWithKeyboard()
    }

    private fun movePointerAndReturnWithKeyboard() {
        val gamma = composeRule.onNodeWithTag("gamma").fetchSemanticsNode().boundsInWindow.center
        composeRule.onAllNodes(isRoot()).onFirst().performMouseInput {
            enter(gamma - Offset(2f, 0f))
            moveTo(gamma)
        }
        composeRule.onNodeWithTag("gamma").assertIsFocused().performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithTag("alpha").assertIsFocused()
        val pixels = composeRule.onNodeWithTag("gamma").captureToImage().toPixelMap()
        assertEquals(popupBackground, pixels[pixels.width - 4, pixels.height / 2])
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "modal={0}")
        fun cases() = listOf(arrayOf(true), arrayOf(false))
    }
}
