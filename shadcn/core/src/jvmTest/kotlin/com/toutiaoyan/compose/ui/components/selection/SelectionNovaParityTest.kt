package com.toutiaoyan.compose.ui.components.selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.toPixelMap
import com.toutiaoyan.compose.ui.tokens.Ring
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.radiobutton.*
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.components.switch.SwitchSize
import com.toutiaoyan.compose.ui.components.toggle.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class SelectionNovaParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("selection_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Checkbox(checked = false, onCheckedChange = {})
                    Checkbox(checked = true, onCheckedChange = {})
                    Checkbox(indeterminate = true)
                    Checkbox(invalid = true)
                    Checkbox(checked = true, onCheckedChange = {}, invalid = true)
                    Checkbox(disabled = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RadioButton(selected = false, onClick = {})
                    RadioButton(selected = true, onClick = {})
                    RadioButton(selected = false, onClick = {}, invalid = true)
                    RadioButton(selected = true, onClick = {}, invalid = true)
                    RadioButton(selected = false, onClick = {}, disabled = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Switch(checked = false, onCheckedChange = {})
                    Switch(checked = true, onCheckedChange = {})
                    Switch(checked = false, onCheckedChange = {}, size = SwitchSize.Sm)
                    Switch(checked = true, onCheckedChange = {}, size = SwitchSize.Sm)
                    Switch(invalid = true)
                    Switch(disabled = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Toggle("Bold")
                    Toggle("Bold", defaultPressed = true)
                    Toggle("Bold", variant = ToggleVariant.Outline)
                    Toggle("Bold", size = ToggleSize.Sm)
                    Toggle("Bold", size = ToggleSize.Lg)
                    Toggle(invalid = true) { Text { "Invalid" } }
                }
            }
        }

    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    private fun focus(
        kind: String,
        dark: Boolean,
    ) = snapshotFocused("selection_${kind}_focus", "target", darkTheme = dark, stylePreset = StylePreset.Nova) {
        when (kind) {
            "checkbox" -> Checkbox(modifier = Modifier.testTag("target"))
            "radio" -> RadioButton(selected = false, onClick = {}, modifier = Modifier.testTag("target"))
            "switch" -> Switch(modifier = Modifier.testTag("target"))
            else -> Toggle("Bold", modifier = Modifier.testTag("target"))
        }
    }

    @Test fun checkboxFocusLight() = focus("checkbox", false)

    @Test fun checkboxFocusDark() = focus("checkbox", true)

    @Test fun radioFocusLight() = focus("radio", false)

    @Test fun radioFocusDark() = focus("radio", true)

    @Test fun switchFocusLight() = focus("switch", false)

    @Test fun switchFocusDark() = focus("switch", true)

    @Test fun toggleFocusLight() = focus("toggle", false)

    @Test fun toggleFocusDark() = focus("toggle", true)

    @Test fun visibleControlsUseNovaDimensionsAndInlineTypography() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Column {
                    Checkbox(modifier = Modifier.testTag("checkbox"))
                    RadioButton(selected = false, onClick = {}, modifier = Modifier.testTag("radio"))
                    Switch(modifier = Modifier.testTag("switch"))
                    Switch(size = SwitchSize.Sm, modifier = Modifier.testTag("small-switch"))
                    Toggle(modifier = Modifier.testTag("toggle")) {
                        Icon(Check, Modifier.testTag("icon"))
                        Text(modifier = Modifier.testTag("label")) { "Bold" }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("checkbox").assertWidthIsEqualTo(16.dp).assertHeightIsEqualTo(16.dp)
        composeRule.onNodeWithTag("radio").assertWidthIsEqualTo(16.dp).assertHeightIsEqualTo(16.dp)
        composeRule.onNodeWithTag("switch").assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(18.4.dp)
        composeRule.onNodeWithTag("small-switch").assertWidthIsEqualTo(24.dp).assertHeightIsEqualTo(14.dp)
        composeRule.onNodeWithTag("toggle").assertHeightIsEqualTo(32.dp)
        composeRule.onNodeWithTag("icon", useUnmergedTree = true).assertWidthIsEqualTo(16.dp)
        val icon = composeRule.onNodeWithTag("icon", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val label = composeRule.onNodeWithTag("label", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(4f, label.left - icon.right)
        val text = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithTag(
            "label",
            useUnmergedTree = true,
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(text)
        }
        assertEquals(14.sp, text.single().layoutInput.style.fontSize)
        assertEquals(FontWeight.Medium, text.single().layoutInput.style.fontWeight)
    }

    @Test fun checkboxAndSwitchLabelsToggleUncontrolledState() {
        composeRule.setContent {
            Theme {
                Column {
                    Checkbox("Accept")
                    Switch("Airplane Mode")
                }
            }
        }
        composeRule.onNodeWithText("Accept").performClick()
        composeRule.onNodeWithContentDescription("Accept").assertIsOn().assertIsFocused()
        composeRule.onNodeWithText("Airplane Mode").performClick()
        composeRule.onNodeWithContentDescription("Airplane Mode").assertIsOn().assertIsFocused()
    }

    @Test fun checkboxSpaceCyclesFromIndeterminateToChecked() {
        var selected: Boolean? = null
        composeRule.setContent {
            Theme {
                Checkbox(
                    indeterminate = true,
                    onCheckedChange = { selected = it },
                    modifier = Modifier.testTag("check"),
                )
            }
        }
        composeRule.onNodeWithTag("check").requestFocus().performKeyInput { pressKey(Key.Spacebar) }
        assertEquals(true, selected)
    }

    @Test fun disabledFieldSetBlocksEverySelectionCallback() {
        var calls = 0
        composeRule.setContent {
            Theme {
                FieldSet(disabled = true) {
                    Checkbox(onCheckedChange = { calls++ }, modifier = Modifier.testTag("c"))
                    Switch(onCheckedChange = { calls++ }, modifier = Modifier.testTag("s"))
                    Toggle(onPressedChange = { calls++ }, modifier = Modifier.testTag("t")) { Text { "Toggle" } }
                    RadioGroup(onValueChange = { calls++ }) { RadioGroupItem("a", modifier = Modifier.testTag("r")) }
                }
            }
        }
        listOf("c", "s", "t", "r").forEach { composeRule.onNodeWithTag(it).assertIsNotEnabled().performClick() }
        assertEquals(0, calls)
    }

    @Test fun radioArrowsSkipDisabledAndWrap() {
        composeRule.setContent {
            Theme {
                RadioGroup(defaultValue = "a") {
                    RadioGroupItem("a", modifier = Modifier.testTag("a"))
                    RadioGroupItem("b", disabled = true, modifier = Modifier.testTag("b"))
                    RadioGroupItem("c", modifier = Modifier.testTag("c"))
                }
            }
        }
        composeRule.onNodeWithTag("a").requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithTag(
            "c",
        ).assertIsFocused().assertIsSelected().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("a").assertIsFocused().assertIsSelected()
    }

    @Test fun controlledRadioOnlyRequestsAChange() {
        var requested: String? = null
        composeRule.setContent {
            Theme {
                RadioGroup(value = "a", onValueChange = { requested = it }) {
                    RadioGroupItem("a", "A")
                    RadioGroupItem("b", "B")
                }
            }
        }
        composeRule.onNodeWithText("B").performClick()
        assertEquals("b", requested)
        composeRule.onNodeWithContentDescription("A").assertIsSelected()
        composeRule.onNodeWithContentDescription("B").assertIsNotSelected().assertIsFocused()
    }

    @Test fun novaFocusRingCanBeExplicitlyDisabledAndUpdatesLive() {
        var ring by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, ring = Ring(enabled = ring), minTouchTarget = null) {
                Box(Modifier.testTag("surface").background(currentTheme.colors.background).padding(8.dp)) {
                    Toggle("Bold", modifier = Modifier.testTag("toggle"))
                }
            }
        }
        composeRule.onNodeWithTag("toggle").requestFocus().assertIsFocused()

        fun pixel() = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap().let { it[6, it.height / 2] }
        assertTrue(pixel().red > 0.99f)
        composeRule.runOnIdle { ring = true }
        assertTrue(pixel().red < 0.99f)
    }

    @Test fun originalPositionalToggleBooleanStillMeansPressed() {
        composeRule.setContent { Theme { Toggle(true, modifier = Modifier.testTag("toggle")) { Text { "Bold" } } } }
        composeRule.onNodeWithTag("toggle").assertIsOn()
    }

    @Test fun rtlRadioArrowsAndHomeEndFollowLogicalOrder() {
        composeRule.setContent {
            Theme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    RadioGroup(defaultValue = "a") {
                        RadioGroupItem("a", modifier = Modifier.testTag("a"))
                        RadioGroupItem("b", modifier = Modifier.testTag("b"))
                        RadioGroupItem("c", modifier = Modifier.testTag("c"))
                    }
                }
            }
        }
        composeRule.onNodeWithTag("a").requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.onNodeWithTag("c").assertIsSelected().performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithTag("a").assertIsSelected().performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithTag("c").assertIsSelected()
    }
}
