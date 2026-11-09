@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.inputgroup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class InputGroupAddonStyleTest : ScreenshotTest() {
    private fun show(
        style: Style = Style,
        rtl: Boolean = false,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                InputGroup(Modifier.width(200.dp)) {
                    Box(Modifier.height(40.dp))
                    InputGroupAddon(InputGroupAddonAlign.BlockEnd, Modifier.testTag("addon"), style) {
                        Box(Modifier.size(20.dp).testTag("child"))
                    }
                }
            }
        }
    }

    @Test fun defaultBlockEndInsetsStaySixAndEight() {
        show()
        val addon = composeRule.onNodeWithTag("addon").fetchSemanticsNode().boundsInRoot
        val child = composeRule.onNodeWithTag("child", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(6f, child.top - addon.top, 0.1f)
        assertEquals(8f, addon.bottom - child.bottom, 0.1f)
        assertEquals(10f, child.left - addon.left, 0.1f)
    }

    @Test fun topStyleOverridesInsteadOfAddingToDefaultAndPreservesOtherInsets() {
        show(Style { contentPaddingTop(4.dp) })
        val addon = composeRule.onNodeWithTag("addon").fetchSemanticsNode().boundsInRoot
        val child = composeRule.onNodeWithTag("child", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(4f, child.top - addon.top, 0.1f)
        assertEquals(8f, addon.bottom - child.bottom, 0.1f)
        assertEquals(10f, child.left - addon.left, 0.1f)
        composeRule.onNodeWithTag("addon").assertHeightIsEqualTo(32.dp)
    }

    @Test fun defaultStartPaddingRemainsLogicalInRtl() {
        show(rtl = true)
        val addon = composeRule.onNodeWithTag("addon").fetchSemanticsNode().boundsInRoot
        val child = composeRule.onNodeWithTag("child", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(10f, addon.right - child.right, 0.1f)
    }

    @Test fun textOverloadForwardsCallerStyle() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            InputGroup(Modifier.width(200.dp)) {
                Box(Modifier.height(40.dp))
                InputGroupAddon(
                    "Units",
                    InputGroupAddonAlign.BlockEnd,
                    Modifier.testTag("addon"),
                    style = Style { height(48.dp) },
                )
            }
        }
        composeRule.onNodeWithTag("addon").assertHeightIsEqualTo(48.dp)
    }

    @Test fun clickingAddonPaddingFocusesTheRealEditableControl() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            InputGroup(Modifier.width(200.dp)) {
                Input(value = "", onValueChange = {}, modifier = Modifier.testTag("field"))
                InputGroupAddon(InputGroupAddonAlign.InlineStart, Modifier.testTag("addon")) {
                    Box(Modifier.size(20.dp))
                }
            }
        }
        composeRule.onNodeWithTag("addon").performTouchInput { click(Offset(1f, height / 2f)) }
        composeRule.onNodeWithTag("field").assertIsFocused()
    }

    @Test fun addonButtonFocusDoesNotSetControlFocusState() {
        assertOnlyEditableControlOwnsFocus(textarea = false)
    }

    @Test fun textareaFocusReturnsAfterLeavingAnAddonButton() {
        assertOnlyEditableControlOwnsFocus(textarea = true)
    }

    private fun assertOnlyEditableControlOwnsFocus(textarea: Boolean) {
        var groupFocused = false
        var showAddon by mutableStateOf(true)
        setThemedContent(stylePreset = StylePreset.Nova) {
            InputGroup(Modifier.width(240.dp)) {
                val focused = inputGroupState().focused
                SideEffect { groupFocused = focused }
                if (textarea) {
                    InputGroupTextarea("", {}, Modifier.testTag("control"))
                } else {
                    // Direct Input remains supported, alongside the name-parity wrappers.
                    Input("", {}, Modifier.testTag("control"))
                }
                if (showAddon) {
                    InputGroupAddon(
                        if (textarea) InputGroupAddonAlign.BlockEnd else InputGroupAddonAlign.InlineEnd,
                    ) {
                        InputGroupButton("Action", {}, Modifier.testTag("action"))
                    }
                }
            }
        }
        composeRule.onNodeWithTag("control").requestFocus()
        composeRule.runOnIdle { assertTrue(groupFocused) }
        composeRule.onNodeWithTag("action").requestFocus()
        composeRule.onNodeWithTag("action").assertIsFocused()
        composeRule.runOnIdle { assertFalse(groupFocused, "Addon focus must not activate the control focus ring") }
        composeRule.onNodeWithTag("control").requestFocus()
        composeRule.runOnIdle { assertTrue(groupFocused) }
        composeRule.onNodeWithTag("action").requestFocus()
        composeRule.runOnIdle { showAddon = false }
        composeRule.onNodeWithTag("control").requestFocus()
        composeRule.runOnIdle { assertTrue(groupFocused, "Removing a focused addon must clear its focus registration") }
    }
}
