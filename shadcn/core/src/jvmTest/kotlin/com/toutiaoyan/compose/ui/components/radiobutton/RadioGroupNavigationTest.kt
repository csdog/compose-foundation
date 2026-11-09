@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.radiobutton

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.Rule

class RadioGroupNavigationTest {
    @get:Rule val rule = createComposeRule()

    private fun show(
        value: String? = null,
        disabled: Boolean = false,
        rtl: Boolean = false,
        request: (String) -> Unit = {},
    ) {
        rule.setContent {
            Theme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    Column {
                        Button(onClick = {}, modifier = Modifier.testTag("before")) { Text { "Before" } }
                        FieldSet(disabled = disabled) {
                            RadioGroup(value = value, defaultValue = "b", onValueChange = request) {
                                RadioGroupItem("a", "A", modifier = Modifier.testTag("a"))
                                RadioGroupItem("b", "B", modifier = Modifier.testTag("b"))
                                RadioGroupItem("disabled", "Disabled", disabled = true)
                                RadioGroupItem("c", "C", modifier = Modifier.testTag("c"))
                            }
                        }
                        Button(onClick = {}, modifier = Modifier.testTag("after")) { Text { "After" } }
                    }
                }
            }
        }
    }

    private fun key(
        tag: String,
        key: Key,
    ) = rule.onNodeWithTag(tag).performKeyInput { pressKey(key) }

    @Test fun tabEntersSelectedAndExitsWithoutVisitingOtherItemsOrLabels() {
        show()
        rule.onNodeWithTag("before").requestFocus()
        key("before", Key.Tab)
        rule.onNodeWithContentDescription("B").assertIsFocused().assertIsSelected()
        key("b", Key.Tab)
        rule.onNodeWithTag("after").assertIsFocused()
        rule.onNodeWithTag("after").performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        rule.onNodeWithContentDescription("B").assertIsFocused()
    }

    @Test fun arrowsRoveSkipDisabledAndTabLeavesTheNewSelection() {
        show()
        rule.onNodeWithContentDescription("B").requestFocus()
        key("b", Key.DirectionRight)
        rule.onNodeWithContentDescription("C").assertIsSelected().assertIsFocused()
        key("c", Key.DirectionRight)
        rule.onNodeWithContentDescription("A").assertIsSelected().assertIsFocused()
        key("a", Key.Tab)
        rule.onNodeWithTag("after").assertIsFocused()
    }

    @Test fun controlledRefusalKeepsSelectionButAllowsArrowAndLabelFocus() {
        var requested = ""
        show(value = "b", request = { requested = it })
        rule.onNodeWithContentDescription("B").requestFocus()
        key("b", Key.DirectionRight)
        assertEquals("c", requested)
        rule.onNodeWithContentDescription("C").assertIsFocused().assertIsNotSelected()
        rule.onNodeWithContentDescription("B").assertIsSelected()
        key("c", Key.Tab)
        rule.onNodeWithTag("after").assertIsFocused()
        rule.onNodeWithText("A").performClick()
        assertEquals("a", requested)
        rule.onNodeWithContentDescription("A").assertIsFocused().assertIsNotSelected()
    }

    @Test fun unmatchedControlledValueEntersFirstEnabledWithoutSelectingIt() {
        show(value = "missing")
        rule.onNodeWithTag("before").requestFocus()
        key("before", Key.Tab)
        rule.onNodeWithContentDescription("A").assertIsFocused().assertIsNotSelected()
        key("a", Key.Tab)
        rule.onNodeWithTag("after").assertIsFocused()
    }

    @Test fun disabledParentFieldsetIsSkippedByTab() {
        show(disabled = true)
        rule.onNodeWithTag("before").requestFocus()
        key("before", Key.Tab)
        rule.onNodeWithTag("after").assertIsFocused()
        rule.onNodeWithContentDescription("B").assertIsNotEnabled()
    }

    @Test fun rtlArrowsReverseHorizontalDirectionAndKeepOneTabStop() {
        show(rtl = true)
        rule.onNodeWithContentDescription("B").requestFocus()
        key("b", Key.DirectionLeft)
        rule.onNodeWithContentDescription("C").assertIsSelected().assertIsFocused()
        key("c", Key.Tab)
        rule.onNodeWithTag("after").assertIsFocused()
    }

    @Test fun keyedReorderingUpdatesArrowNavigationOrder() {
        var values by mutableStateOf(listOf("a", "b", "disabled", "c"))
        var rtl by mutableStateOf(false)
        rule.setContent {
            Theme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    RadioGroup(
                        defaultValue = "b",
                        orientation = if (rtl) RadioGroupOrientation.Horizontal else RadioGroupOrientation.Vertical,
                    ) {
                        values.forEach { value ->
                            key(value) {
                                RadioGroupItem(
                                    value,
                                    disabled = value == "disabled",
                                    modifier = Modifier.testTag(value),
                                )
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithTag("b").requestFocus()
        rule.runOnIdle { values = listOf("c", "disabled", "b", "a") }
        key("b", Key.DirectionDown)
        rule.onNodeWithTag("a").assertIsSelected().assertIsFocused()
        key("a", Key.DirectionDown)
        rule.onNodeWithTag("c").assertIsSelected().assertIsFocused()
        key("c", Key.DirectionDown)
        rule.onNodeWithTag("b").assertIsSelected().assertIsFocused()

        rule.runOnIdle {
            rtl = true
            values = listOf("a", "b", "disabled", "c")
        }
        rule.onNodeWithTag("b").performClick()
        rule.runOnIdle { values = listOf("c", "disabled", "b", "a") }
        key("b", Key.DirectionLeft)
        rule.onNodeWithTag("a").assertIsSelected().assertIsFocused()
        key("a", Key.DirectionLeft)
        rule.onNodeWithTag("c").assertIsSelected().assertIsFocused()
        key("c", Key.DirectionLeft)
        rule.onNodeWithTag("b").assertIsSelected().assertIsFocused()
    }
}
