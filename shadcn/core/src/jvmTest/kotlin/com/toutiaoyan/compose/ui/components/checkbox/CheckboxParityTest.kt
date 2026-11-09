package com.toutiaoyan.compose.ui.components.checkbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

    @Test fun mouseHitAreaExtendsBeyondTheControlWithoutChangingLayout() {
        var changes = 0
        var disabled by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.testTag("hit-area").padding(24.dp)) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = { changes++ },
                        disabled = disabled,
                        label = "Hit target",
                        modifier = Modifier.testTag("control"),
                    )
                }
            }
        }
        val bounds = rule.onNodeWithTag("control").fetchSemanticsNode().boundsInRoot
        assertEquals(16f, bounds.width, .5f)
        assertEquals(16f, bounds.height, .5f)
        // Root coordinates: the visible box is [24,40] on each axis.
        val points = listOf(Offset(13f, 32f), Offset(51f, 32f), Offset(32f, 17f), Offset(32f, 47f))
        points.forEach { point -> rule.onNodeWithTag("hit-area").performMouseInput { click(point) } }
        rule.runOnIdle { assertEquals(4, changes) }
        rule.onNodeWithTag("hit-area").performMouseInput { click(Offset(11f, 32f)) }
        rule.runOnIdle {
            assertEquals(4, changes)
            disabled = true
        }
        points.forEach { point -> rule.onNodeWithTag("hit-area").performMouseInput { click(point) } }
        rule.runOnIdle { assertEquals(4, changes) }
    }

    @Test fun themeMinimumKeepsItsExistingLayoutAndPointerTarget() {
        var changes = 0
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = 48.dp) {
                Box(Modifier.testTag("hit-area").padding(24.dp)) {
                    Checkbox(checked = false, onCheckedChange = { changes++ }, modifier = Modifier.testTag("control"))
                }
            }
        }
        val bounds = rule.onNodeWithTag("control").fetchSemanticsNode().boundsInRoot
        assertEquals(48f, bounds.width, .5f)
        assertEquals(48f, bounds.height, .5f)
        rule.onNodeWithTag("hit-area").performMouseInput { click(Offset(25f, 25f)) }
        rule.runOnIdle { assertEquals(1, changes) }
    }

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
