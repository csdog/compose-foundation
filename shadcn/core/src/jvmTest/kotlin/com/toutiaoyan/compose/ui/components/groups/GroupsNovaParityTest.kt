@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.buttongroup.*
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.components.inputgroup.*
import com.toutiaoyan.compose.ui.components.slider.Slider
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.togglegroup.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class GroupsNovaParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("groups_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ToggleGroup(variant = ToggleVariant.Outline, spacing = 0.dp, defaultValue = setOf("b")) {
                    ToggleGroupItem("b", "Bold")
                    ToggleGroupItem("i", "Italic")
                    ToggleGroupItem("u", "Underline", disabled = true)
                }
                ToggleGroup(
                    variant = ToggleVariant.Outline,
                    orientation = ToggleGroupOrientation.Vertical,
                    spacing = 0.dp,
                ) {
                    ToggleGroupItem("b", "Bold")
                    ToggleGroupItem("i", "Italic")
                }
                ButtonGroup {
                    ButtonGroupItem("Archive", onClick = {}, variant = ButtonVariant.Outline)
                    ButtonGroupItem("Report", onClick = {}, variant = ButtonVariant.Outline)
                    ButtonGroupItem("Snooze", onClick = {}, variant = ButtonVariant.Outline)
                }
                Slider(value = 0.75f, onValueChange = {})
                Slider(value = 0.25f..0.75f, onValueChange = {})
                Slider(value = 0.5f, onValueChange = {}, disabled = true)
                InputGroup {
                    InputGroupInput("", placeholder = "Search...")
                    InputGroupAddon("Find")
                    InputGroupAddon("12 results", InputGroupAddonAlign.InlineEnd)
                }
                InputGroup {
                    InputGroupInput("Invalid", invalid = true)
                    InputGroupAddon("$")
                }
                InputGroup(disabled = true) {
                    InputGroupInput("Disabled")
                    InputGroupAddon("$")
                }
                InputGroup {
                    InputGroupTextarea("", {}, placeholder = "Ask anything...")
                    InputGroupAddon(InputGroupAddonAlign.BlockEnd) { InputGroupButton("Send", {}) }
                }
            }
        }

    @Test fun matrixLight() = matrix(false)

    @Test fun matrixDark() = matrix(true)

    private fun focus(
        kind: String,
        dark: Boolean,
    ) = snapshotFocused("groups_${kind}_focus", "target", darkTheme = dark, stylePreset = StylePreset.Nova) {
        when (kind) {
            "slider" -> Slider(0.75f, {}, Modifier.width(200.dp).testTag("target"))
            "input" ->
                InputGroup(Modifier.width(240.dp)) {
                    InputGroupInput("Search", modifier = Modifier.testTag("target"))
                    InputGroupAddon("Find")
                }
            "toggle" ->
                ToggleGroup(variant = ToggleVariant.Outline, spacing = 0.dp) {
                    ToggleGroupItem("b", "Bold")
                    ToggleGroupItem("i", "Italic", modifier = Modifier.testTag("target"))
                    ToggleGroupItem("u", "Underline")
                }
            else ->
                ButtonGroup {
                    ButtonGroupItem("Archive", {}, variant = ButtonVariant.Outline)
                    ButtonGroupItem(
                        "Report",
                        {},
                        modifier = Modifier.testTag("target"),
                        variant = ButtonVariant.Outline,
                    )
                    ButtonGroupItem("Snooze", {}, variant = ButtonVariant.Outline)
                }
        }
    }

    @Test fun sliderFocusLight() = focus("slider", false)

    @Test fun sliderFocusDark() = focus("slider", true)

    @Test fun inputFocusLight() = focus("input", false)

    @Test fun inputFocusDark() = focus("input", true)

    @Test fun toggleFocusLight() = focus("toggle", false)

    @Test fun toggleFocusDark() = focus("toggle", true)

    @Test fun buttonFocusLight() = focus("button", false)

    @Test fun buttonFocusDark() = focus("button", true)

    @Test fun singleToggleCanClearAndArrowsOnlyMoveFocus() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                ToggleGroup(type = ToggleGroupType.Single, defaultValue = setOf("a")) {
                    ToggleGroupItem("a", "Alpha")
                    ToggleGroupItem("b", "Beta", disabled = true)
                    ToggleGroupItem("c", "Gamma")
                }
            }
        }
        composeRule.onNodeWithText("Alpha").performClick().assertIsOff()
        composeRule.onNodeWithText("Alpha").requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("Gamma").assertIsFocused().assertIsOff().performKeyInput { pressKey(Key.Spacebar) }
        composeRule.onNodeWithText("Gamma").assertIsOn().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithText("Alpha").assertIsFocused().assertIsOff()
    }

    @Test fun toggleVerticalHomeEndAndRtlNavigation() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ToggleGroup {
                        ToggleGroupItem("a", "Alpha")
                        ToggleGroupItem("b", "Beta")
                    }
                }
            }
        }
        composeRule.onNodeWithText("Alpha").requestFocus().performKeyInput { pressKey(Key.DirectionLeft) }
        composeRule.onNodeWithText("Beta").assertIsFocused().performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithText("Alpha").assertIsFocused().performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithText("Beta").assertIsFocused()
    }

    @Test fun groupDisabledInheritsFieldSet() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldSet(disabled = true) {
                    ToggleGroup { ToggleGroupItem("a", "Alpha") }
                    InputGroup {
                        InputGroupInput("Text", modifier = Modifier.testTag("input"))
                        InputGroupButton("Send", {})
                    }
                }
            }
        }
        composeRule.onNodeWithText("Alpha").assertIsNotEnabled()
        composeRule.onNodeWithTag("input").assertIsNotEnabled()
        composeRule.onNodeWithText("Send").assertIsNotEnabled()
    }

    @Test fun clickingAddonFocusesInputAndButtonRetainsOwnAction() {
        var sends = 0
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                InputGroup(Modifier.width(320.dp).testTag("group")) {
                    var text by remember { mutableStateOf("") }
                    InputGroupInput(text, { text = it }, modifier = Modifier.testTag("input"))
                    InputGroupAddon("Find")
                    InputGroupAddon(InputGroupAddonAlign.InlineEnd) { InputGroupButton("Send", { sends++ }) }
                }
            }
        }
        composeRule.onNodeWithText("Find").performClick()
        composeRule.onNodeWithTag("input").assertIsFocused().performTextInput("abc")
        composeRule.onNodeWithTag("input").assertTextEquals("abc")
        composeRule.onNodeWithText("Send").performClick()
        assertEquals(1, sends)
    }

    @Test fun inputGroupHas32DpInlineHeight() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                InputGroup(Modifier.width(320.dp).testTag("group")) {
                    InputGroupInput("")
                    InputGroupAddon("Find")
                }
            }
        }
        composeRule.onNodeWithTag("group").assertHeightIsEqualTo(32.dp)
    }

    private fun thumb() =
        composeRule.onNode(
            hasRequestFocusAction() and hasAnyAncestor(hasTestTag("slider")),
            useUnmergedTree = true,
        )

    @Test fun sliderKeyboardStepsAndCommitsRequestedValue() {
        var commit = -1f
        var result = 50f
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var value by remember { mutableStateOf(50f) }
                Slider(
                    value,
                    {
                        value = it
                        result = it
                    },
                    Modifier.width(
                        200.dp,
                    ).testTag("slider"),
                    valueRange = 0f..100f,
                    step = 5f,
                    onValueCommit = { commit = it },
                )
            }
        }
        thumb().requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        assertEquals(55f, result)
        assertEquals(55f, commit)
        thumb().performKeyInput { pressKey(Key.PageUp) }
        assertEquals(100f, result)
        thumb().performKeyInput { pressKey(Key.MoveHome) }
        assertEquals(0f, result)
        thumb().performKeyInput { pressKey(Key.MoveEnd) }
        assertEquals(100f, result)
    }

    @Test fun sliderRangeKeyboardCannotCrossNeighborOrMinGap() {
        var result = 20f..40f
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var value by remember { mutableStateOf(20f..40f) }
                Slider(
                    value,
                    {
                        value = it
                        result = it
                    },
                    Modifier.width(
                        200.dp,
                    ).testTag("slider"),
                    valueRange = 0f..100f,
                    step = 5f,
                    minStepsBetweenThumbs = 2,
                )
            }
        }
        composeRule.onAllNodes(
            hasRequestFocusAction() and hasAnyAncestor(hasTestTag("slider")),
            useUnmergedTree = true,
        )[0].requestFocus().performKeyInput {
            pressKey(Key.MoveEnd)
        }
        assertEquals(30f..40f, result)
    }

    @Test fun sliderRtlRightDecrementsAndInvertedRightIncrements() {
        var result = 50f
        var invert by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Slider(50f, {
                        result = it
                    }, Modifier.width(200.dp).testTag("slider"), valueRange = 0f..100f, step = 5f, inverted = invert)
                }
            }
        }
        thumb().requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        assertEquals(45f, result)
        composeRule.runOnIdle { invert = true }
        thumb().performKeyInput { pressKey(Key.DirectionRight) }
        assertEquals(55f, result)
    }

    @Test fun sliderAccessibleProgressSnaps() {
        var result = 0f
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) { Slider(50f, { result = it }, valueRange = 0f..100f, step = 10f) }
        }
        composeRule.onNode(
            SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsProperties.ProgressBarRangeInfo),
        ).performSemanticsAction(SemanticsActions.SetProgress) {
            it(73f)
        }
        assertEquals(70f, result)
    }

    @Test fun novaSliderRemainsVisibleWithoutTouchTargetMinimum() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Slider(0.5f, {}, Modifier.width(200.dp).testTag("slider"))
            }
        }
        composeRule.onNodeWithTag("slider").assertHeightIsEqualTo(12.dp)
        thumb().assertWidthIsEqualTo(12.dp).assertHeightIsEqualTo(12.dp)
    }

    @Test fun controlledToggleReportsMembershipWithoutChangingCallerState() {
        var requested: Set<String>? = null
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                ToggleGroup(value = setOf("a"), onValueChange = { requested = it }) {
                    ToggleGroupItem("a", "Alpha")
                    ToggleGroupItem("b", "Beta")
                }
            }
        }
        composeRule.onNodeWithText("Beta").performClick().assertIsOff()
        assertEquals(setOf("a", "b"), requested)
    }

    @Test fun removingInvalidControlClearsGroupError() {
        var invalid by mutableStateOf(true)
        var state: InputGroupState? = null
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                InputGroup {
                    if (invalid) InputGroupInput("Invalid", invalid = true) else InputGroupInput("Valid")
                    state = inputGroupState()
                }
            }
        }
        composeRule.runOnIdle {
            assertTrue(state!!.invalid)
            invalid = false
        }
        composeRule.runOnIdle { assertFalse(state!!.invalid) }
    }

    @Test fun nestedIconGroupKeepsButtonHeight() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                ButtonGroup(spacing = 8.dp) {
                    ButtonGroup {
                        ButtonGroupItem(
                            onClick = {},
                            variant = ButtonVariant.Outline,
                            modifier = Modifier.testTag("icon"),
                            style =
                                androidx.compose.foundation.style.Style {
                                    width(32.dp)
                                    contentPadding(0.dp)
                                },
                        ) {
                            com.toutiaoyan.compose.ui.components.icon.Icon(
                                com.toutiaoyan.compose.ui.icons.Check,
                            )
                        }
                    }
                    ButtonGroup { ButtonGroupItem("Archive", {}, variant = ButtonVariant.Outline) }
                }
            }
        }
        composeRule.onNodeWithTag("icon").assertHeightIsEqualTo(32.dp)
    }
}
