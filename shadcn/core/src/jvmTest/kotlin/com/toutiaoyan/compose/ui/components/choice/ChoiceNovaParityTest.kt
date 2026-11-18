@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.choice

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.inputotp.*
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.combobox.*
import com.toutiaoyan.compose.ui.components.inputgroup.*
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ChoiceNovaParityTest : ScreenshotTest() {
    private val frameworks = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro")

    @Composable private fun SampleSelect(
        invalid: Boolean = false,
        disabled: Boolean = false,
        open: Boolean = false,
    ) {
        Select<String>(
            null,
            {},
            placeholder = "Select a fruit",
            invalid = invalid,
            disabled = disabled,
            defaultOpen = open,
        ) {
            SelectTrigger(Modifier.width(192.dp))
            SelectContent {
                SelectGroup(heading = "Fruits") {
                    SelectItem("Apple")
                    SelectItem("Banana")
                    SelectItem("Blueberry")
                }
            }
        }
    }

    @Composable private fun SampleCombo(
        invalid: Boolean = false,
        disabled: Boolean = false,
        open: Boolean = false,
    ) {
        Combobox<String>(null, frameworks, {
        }, placeholder = "Select a framework", invalid = invalid, disabled = disabled, defaultOpen = open) {
            ComboboxInput(editable = true)
            ComboboxContent(enter = EnterTransition.None, exit = ExitTransition.None)
        }
    }

    private fun matrix(dark: Boolean) =
        snapshot("choice_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InputOTP("123456", {}, 6)
                InputOTP("", {}, 6)
                InputOTP("123456", {}, 6, disabled = true)
                InputOTP("123456", {}, 6) {
                    InputOTPGroup {
                        InputOTPSlot(0)
                        InputOTPSlot(1)
                        InputOTPSlot(2)
                    }
                    InputOTPSeparator()
                    InputOTPGroup {
                        InputOTPSlot(3)
                        InputOTPSlot(4)
                        InputOTPSlot(5)
                    }
                }
                SampleSelect()
                SampleSelect(invalid = true)
                SampleSelect(disabled = true)
                SampleCombo()
                SampleCombo(invalid = true)
                SampleCombo(disabled = true)
            }
        }

    @Test fun matrixLight() = matrix(false)

    @Test fun matrixDark() = matrix(true)

    private fun focus(
        kind: String,
        dark: Boolean,
    ) {
        if (kind == "combo") {
            setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
                Combobox<String>(null, frameworks, {
                }, placeholder = "Select a framework") {
                    ComboboxInput(
                        Modifier.testTag("target"),
                        editable = true,
                    )
                }
            }
            field("target").requestFocus()
            captureNamed("choice_combo_focus", dark)
            return
        }
        snapshotFocused("choice_${kind}_focus", "target", darkTheme = dark, stylePreset = StylePreset.Nova) {
            when (kind) {
                "otp" -> InputOTP("12", {}, 6, Modifier.testTag("target"))
                "select" ->
                    Select<String>(null, {
                    }, placeholder = "Select a fruit") { SelectTrigger(Modifier.width(192.dp).testTag("target")) }
                else ->
                    Combobox<String>(null, frameworks, {
                    }, placeholder = "Select a framework") {
                        ComboboxInput(
                            Modifier.testTag("target"),
                            editable = true,
                        )
                    }
            }
        }
    }

    @Test fun otpFocusLight() = focus("otp", false)

    @Test fun otpFocusDark() = focus("otp", true)

    @Test fun selectFocusLight() = focus("select", false)

    @Test fun selectFocusDark() = focus("select", true)

    @Test fun comboFocusLight() = focus("combo", false)

    @Test fun comboFocusDark() = focus("combo", true)

    @Test fun selectOpenLight() =
        snapshot("choice_select_open", stylePreset = StylePreset.Nova) {
            SampleSelect(open = true)
        }

    @Test fun selectOpenDark() =
        snapshot("choice_select_open", darkTheme = true, stylePreset = StylePreset.Nova) {
            SampleSelect(open = true)
        }

    @Test fun comboOpenLight() =
        snapshot(
            "choice_combo_open",
            stylePreset = StylePreset.Nova,
        ) { SampleCombo(open = true) }

    @Test fun comboOpenDark() =
        snapshot("choice_combo_open", darkTheme = true, stylePreset = StylePreset.Nova) {
            SampleCombo(open = true)
        }

    private fun field(tag: String = "otp") =
        composeRule.onNode(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText) and
                (hasTestTag(tag) or hasAnyAncestor(hasTestTag(tag))),
            useUnmergedTree = true,
        )

    @Test fun otpSixSlotsAre192By32AndTwoGroupsDoNotOverlap() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Column {
                    InputOTP("123456", {}, 6, Modifier.testTag("flat"))
                    InputOTP("123456", {}, 6, Modifier.testTag("split")) {
                        InputOTPGroup { repeat(3) { InputOTPSlot(it) } }
                        InputOTPSeparator()
                        InputOTPGroup { repeat(3) { InputOTPSlot(it + 3) } }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("flat").assertWidthIsEqualTo(192.dp).assertHeightIsEqualTo(32.dp)
        composeRule.onNodeWithTag("split").assertWidthIsEqualTo(224.dp).assertHeightIsEqualTo(32.dp)
    }

    @Test fun otpPasteFiltersAndClipsThenBackspaceEdits() {
        var complete = ""
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var value by remember { mutableStateOf("") }
                InputOTP(value, { value = it }, 6, Modifier.testTag("otp"), onComplete = { complete = it })
            }
        }
        field().performTextInput("12-ab345678")
        field().assertTextEquals("123456")
        composeRule.runOnIdle { assertEquals("123456", complete) }
        field().requestFocus().performKeyInput { pressKey(Key.Backspace) }
        field().assertTextEquals("12345")
    }

    @Test fun otpDisabledInheritsFieldSet() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldSet(disabled = true) {
                    InputOTP("123456", {}, 6, Modifier.testTag("otp"))
                }
            }
        }
        field().assertIsNotEnabled()
    }

    @Test fun selectArrowsFocusDisabledWithoutActivationAndCommitRestoresTrigger() {
        var picked: String? = null
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var value by remember { mutableStateOf<String?>(null) }
                Select(value, {
                    value = it
                    picked = it
                }, placeholder = "Pick") {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent {
                        SelectItem("Apple")
                        SelectItem("Banana", disabled = true)
                        SelectItem("Cherry")
                    }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Apple").assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Banana").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals(null, picked) }
        composeRule.onNodeWithText("Banana").assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithText("Cherry").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals("Cherry", picked) }
        composeRule.onNodeWithTag("trigger").assertIsFocused()
    }

    @Test fun selectEscapeAndUncontrolledOpenNotification() {
        var notified = false
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Select<String>(null, {}, placeholder = "Pick", onOpenChange = { notified = it }) {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent { SelectItem("Apple") }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").performClick()
        composeRule.onNodeWithText("Apple").assertIsDisplayed().performKeyInput { pressKey(Key.Escape) }
        composeRule.runOnIdle { assertFalse(notified) }
        composeRule.onNodeWithTag("trigger").assertIsFocused()
    }

    @Test fun selectKeyboardHonorsPreventSelection() {
        var picked = false
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Select<String>(null, { picked = true }, defaultOpen = true) {
                    SelectTrigger()
                    SelectContent { SelectItem("Apple", onSelect = { true }) }
                }
            }
        }
        composeRule.onNodeWithText("Apple").performKeyInput { pressKey(Key.Enter) }
        assertFalse(picked)
        composeRule.onNodeWithText("Apple").assertIsDisplayed()
    }

    @Test fun editableComboFiltersKeepsFocusAndSelectsByKeyboard() {
        var picked: String? = null
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var value by remember { mutableStateOf<String?>(null) }
                Combobox(value, frameworks, {
                    value = it
                    picked = it
                }, placeholder = "Framework") {
                    ComboboxInput(Modifier.testTag("combo"), editable = true)
                    ComboboxContent(enter = EnterTransition.None, exit = ExitTransition.None)
                }
            }
        }
        field("combo").performTextInput("sv")
        field("combo").assertIsFocused()
        composeRule.onNodeWithText("SvelteKit").assertIsDisplayed()
        composeRule.onNodeWithText("Next.js").assertDoesNotExist()
        field("combo").performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle { assertEquals("SvelteKit", picked) }
        field("combo").assertTextEquals("SvelteKit").assertIsFocused()
    }

    @Test fun editableComboEmptyEscapeAndClear() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var value by remember { mutableStateOf<String?>("Next.js") }
                Combobox(value, frameworks, { value = it }, onClear = { value = null }) {
                    ComboboxInput(Modifier.testTag("combo"), editable = true, showClear = true)
                    ComboboxContent(enter = EnterTransition.None, exit = ExitTransition.None)
                }
            }
        }
        field("combo").performTextReplacement("zzzz")
        composeRule.onNodeWithText("No results found.").assertIsDisplayed()
        field("combo").requestFocus().performKeyInput { pressKey(Key.Escape) }
        field("combo").assertTextEquals("Next.js")
        composeRule.onNodeWithContentDescription("Clear").performClick()
        field("combo").assertTextEquals("")
    }

    @Test fun comboboxAndSelectInheritFieldSetDisabled() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldSet(disabled = true) {
                    Select<String>(null, {}) { SelectTrigger(Modifier.testTag("select")) }
                    Combobox<String>(null, frameworks, {}) { ComboboxInput(Modifier.testTag("combo"), editable = true) }
                }
            }
        }
        composeRule.onNodeWithTag("select").assertIsNotEnabled()
        field("combo").assertIsNotEnabled()
    }

    @Test fun editableComboAndInlineButtonGroupStay32High() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Column {
                    SampleCombo()
                    InputGroup(Modifier.width(216.dp).testTag("group")) {
                        InputGroupInput("")
                        InputGroupAddon(InputGroupAddonAlign.InlineEnd) { InputGroupButton("Go", {}) }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("group").assertHeightIsEqualTo(32.dp)
    }

    @Test fun openSelectItemsHave28DpRowsAnd24DpLabel() {
        composeRule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { SampleSelect(open = true) } }
        composeRule.onNodeWithText("Apple").assertHeightIsEqualTo(28.dp)
        composeRule.onNodeWithText("Fruits").assertHeightIsEqualTo(24.dp)
    }

    @Test fun openComboboxItemsHave28DpRows() {
        composeRule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { SampleCombo(open = true) } }
        composeRule.onNodeWithText("Next.js").assertHeightIsEqualTo(28.dp)
    }

    @Test fun enterReleaseAfterCommitDoesNotReopenSelect() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                var value by remember { mutableStateOf<String?>(null) }
                Select(value, { value = it }, placeholder = "Pick") {
                    SelectTrigger(Modifier.testTag("trigger"))
                    SelectContent { SelectItem("Apple", Modifier.testTag("option")) }
                }
            }
        }
        composeRule.onNodeWithTag("trigger").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("option").performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithTag("trigger").assertIsFocused()
        composeRule.onNodeWithTag("option").assertDoesNotExist()
    }

    @Test fun maskedOtpExposesPasswordSemantics() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) { InputOTP("123456", {}, 6, Modifier.testTag("otp"), masked = true) }
        }
        field().assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
    }

    @Test fun autoHighlightedComboboxCommitsWithEnter() {
        var picked: String? = null
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Combobox<String>(null, frameworks, { picked = it }, autoHighlight = true) {
                    ComboboxInput(Modifier.testTag("combo"), editable = true)
                    ComboboxContent(enter = EnterTransition.None, exit = ExitTransition.None)
                }
            }
        }
        field("combo").performTextInput("sv")
        field("combo").performKeyInput { pressKey(Key.Enter) }
        composeRule.runOnIdle { assertEquals("SvelteKit", picked) }
    }
}
