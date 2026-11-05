package com.toutiaoyan.compose.ui.components.checkbox

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.field.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class CheckboxParityTest {
    @get:Rule val rule = createComposeRule()

    @Test fun descriptionAlignsTheControlWithTheFirstLine() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Field(Modifier.width(288.dp), orientation = FieldOrientation.Horizontal) {
                    Checkbox(modifier = Modifier.testTag("control"))
                    FieldContent(Modifier.testTag("content")) {
                        FieldLabel("Accept terms and conditions")
                        FieldDescription("By clicking this checkbox, you agree to the terms and conditions.")
                    }
                }
            }
        }
        val control = rule.onNodeWithTag("control").fetchSemanticsNode().boundsInRoot
        val content = rule.onNodeWithTag("content").fetchSemanticsNode().boundsInRoot
        assertEquals(1f, control.top - content.top, .5f)
        assertEquals(16f, control.width, .5f)
    }

    @Test fun defaultInteractionUsesOfficialColorMotionWithoutAScaleTransform() {
        assertEquals(ButtonAnimation.None, CheckboxDefaults.Animation)
        assertEquals(150, CheckboxDefaults.ColorTransitionMillis)
    }

    @Test fun indeterminateSpaceSelectsOnceAndControlledRejectionIsRespected() {
        var changes = 0
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Checkbox(checked = false, indeterminate = true, onCheckedChange = { changes++ }, label = "All")
            }
        }
        rule.onNodeWithContentDescription("All").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithContentDescription("All").performKeyInput { pressKey(Key.Spacebar) }
        rule.runOnIdle { assertEquals(1, changes) }
        rule.onNodeWithContentDescription("All").assertIsToggleable()
    }

    @Test fun disabledFieldPreventsControlAndLabelActivation() {
        var changes = 0
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                FieldSet(disabled = true) {
                    Checkbox("Disabled", checked = false, onCheckedChange = { changes++ })
                }
            }
        }
        rule.onNodeWithText("Disabled").performClick()
        rule.onNodeWithContentDescription("Disabled").assertIsNotEnabled()
        rule.runOnIdle { assertEquals(0, changes) }
    }

    @Test fun choiceCardPaddingAndControlEachToggleOnceAndKeepTheControlFocus() {
        var changes = 0
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var selected by remember { mutableStateOf(false) }
                val focus = remember { androidx.compose.ui.focus.FocusRequester() }
                FieldLabel(modifier = Modifier.testTag("card"), selected = selected, onClick = {
                    selected = !selected
                    changes++
                    focus.requestFocus()
                }) {
                    Field(Modifier.width(240.dp), orientation = FieldOrientation.Horizontal) {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = {
                                selected = it
                                changes++
                            },
                            label = "Notifications",
                            modifier = Modifier.focusRequester(focus),
                        )
                        FieldContent {
                            FieldTitle("Notifications")
                            FieldDescription("Updates at any time.")
                        }
                    }
                }
            }
        }
        rule.onNodeWithTag("card").performTouchInput { click(androidx.compose.ui.geometry.Offset(3f, 3f)) }
        rule.onNodeWithContentDescription("Notifications").assertIsOn().assertIsFocused()
        rule.runOnIdle { assertEquals(1, changes) }
        rule.onNodeWithContentDescription("Notifications").performClick()
        rule.onNodeWithContentDescription("Notifications").assertIsOff()
        rule.runOnIdle { assertEquals(2, changes) }
        rule.onNodeWithContentDescription("Notifications").performKeyInput { pressKey(Key.Spacebar) }
        rule.onNodeWithContentDescription("Notifications").assertIsOn()
        rule.runOnIdle { assertEquals(3, changes) }
    }

    @Test fun plainHorizontalFieldStaysCenteredAndRtlMovesControlToInlineStart() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl,
                ) {
                    Field(Modifier.width(224.dp), orientation = FieldOrientation.Horizontal) {
                        Checkbox(modifier = Modifier.testTag("box"))
                        FieldLabel("Terms", Modifier.testTag("label"))
                    }
                }
            }
        }
        val box = rule.onNodeWithTag("box").fetchSemanticsNode().boundsInRoot
        val label = rule.onNodeWithTag("label").fetchSemanticsNode().boundsInRoot
        kotlin.test.assertTrue(box.left > label.right)
        assertEquals(label.center.y, box.center.y, .5f)
    }
}
