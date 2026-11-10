package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.components.toggle.Toggle
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test

class ToggleGroupKeyboardTest {
    @get:Rule val rule = createComposeRule()

    private fun navigation(
        vertical: Boolean,
        rtl: Boolean,
        loopFocus: Boolean = true,
    ) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    Column {
                        Toggle("Before", pressed = false, onPressedChange = {})
                        ToggleGroup(
                            loopFocus = loopFocus,
                            orientation =
                                if (vertical) {
                                    ToggleGroupOrientation.Vertical
                                } else {
                                    ToggleGroupOrientation.Horizontal
                                },
                        ) {
                            ToggleGroupItem("a", "Alpha")
                            ToggleGroupItem("b", "Beta", disabled = true)
                            ToggleGroupItem("c", "Gamma")
                            ToggleGroupItem("d", "Disabled last", disabled = true)
                        }
                        Toggle("After", pressed = false, onPressedChange = {})
                    }
                }
            }
        }
        val forward =
            if (vertical) {
                Key.DirectionDown
            } else if (rtl) {
                Key.DirectionLeft
            } else {
                Key.DirectionRight
            }
        val backward =
            if (vertical) {
                Key.DirectionUp
            } else if (rtl) {
                Key.DirectionRight
            } else {
                Key.DirectionLeft
            }
        val alpha = rule.onNodeWithText("Alpha")
        val gamma = rule.onNodeWithText("Gamma")
        rule.onNodeWithText("Before").requestFocus().performKeyInput { pressKey(Key.Tab) }
        alpha.assertIsFocused().assertIsOff().performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithText("After").assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        alpha.assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        rule.onNodeWithText("Before").assertIsFocused().performKeyInput { pressKey(Key.Tab) }
        alpha.assertIsFocused().performKeyInput { pressKey(forward) }
        gamma.assertIsFocused().assertIsOff().performKeyInput { pressKey(forward) }
        if (loopFocus) {
            alpha.assertIsFocused().performKeyInput { pressKey(backward) }
        } else {
            gamma.assertIsFocused().performKeyInput { pressKey(Key.MoveHome) }
            alpha.assertIsFocused().performKeyInput { pressKey(backward) }
            alpha.assertIsFocused().performKeyInput { pressKey(Key.MoveEnd) }
        }
        gamma.assertIsFocused().performKeyInput { pressKey(Key.MoveHome) }
        alpha.assertIsFocused().performKeyInput { pressKey(Key.MoveEnd) }
        gamma.assertIsFocused().performKeyInput { pressKey(Key.Spacebar) }
        gamma.assertIsOn().performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithText("After").assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        gamma.assertIsFocused().assertIsOn()
        rule.onNodeWithText("Beta").assertIsNotEnabled()
    }

    @Test fun mouseSelectionOwnsSubsequentKeyboardInput() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                ToggleGroup {
                    ToggleGroupItem("a", "Alpha")
                    ToggleGroupItem("b", "Beta")
                    ToggleGroupItem("c", "Gamma")
                }
            }
        }
        rule.onNodeWithText("Alpha").requestFocus().assertIsFocused()
        val gamma = rule.onNodeWithText("Gamma")
        gamma.performMouseInput { click() }
        gamma.assertIsFocused().assertIsOn().performKeyInput {
            pressKey(Key.DirectionLeft)
            pressKey(Key.Spacebar)
        }
        rule.onNodeWithText("Beta").assertIsFocused().assertIsOn()
    }

    @Test fun horizontalLtrWithoutLoop() = navigation(vertical = false, rtl = false, loopFocus = false)

    @Test fun horizontalRtlWithoutLoop() = navigation(vertical = false, rtl = true, loopFocus = false)

    @Test fun verticalLtrWithoutLoop() = navigation(vertical = true, rtl = false, loopFocus = false)

    @Test fun verticalRtlWithoutLoop() = navigation(vertical = true, rtl = true, loopFocus = false)

    @Test fun horizontalLtr() = navigation(vertical = false, rtl = false)

    @Test fun horizontalRtl() = navigation(vertical = false, rtl = true)

    @Test fun verticalLtr() = navigation(vertical = true, rtl = false)

    @Test fun verticalRtl() = navigation(vertical = true, rtl = true)
}
