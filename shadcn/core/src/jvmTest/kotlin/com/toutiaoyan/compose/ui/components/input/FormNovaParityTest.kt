package com.toutiaoyan.compose.ui.components.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import com.toutiaoyan.compose.ui.tokens.BaseColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.field.*
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class FormNovaParityTest : ScreenshotTest() {
    private fun matrix(dark: Boolean) =
        snapshot("form_nova_parity", darkTheme = dark, stylePreset = StylePreset.Nova) {
            FieldGroup(Modifier.width(320.dp)) {
                Field("API Key", description = "Your API key is encrypted and stored securely.") {
                    Input("", {}, placeholder = "sk-...")
                }
                Field("Message") { Textarea("", {}, placeholder = "Type your message here.") }
                Field("Email", invalid = true) {
                    Input("not-an-email", {}, invalid = true)
                    FieldError("Enter a valid email address.")
                }
                FieldSet(disabled = true) { Field("Disabled") { Input("", {}, placeholder = "Email") } }
            }
        }

    @Test fun lightMatrix() = matrix(false)

    @Test fun darkMatrix() = matrix(true)

    private fun focus(dark: Boolean) =
        snapshotFocused("input_nova_focus", "input", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Input("", {}, Modifier.width(320.dp).testTag("input"), placeholder = "Email")
        }

    @Test fun focusLight() = focus(false)

    @Test fun focusDark() = focus(true)

    private fun areaFocus(dark: Boolean) =
        snapshotFocused("textarea_nova_focus", "area", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Textarea("", {}, Modifier.width(320.dp).testTag("area"), placeholder = "Type your message here.")
        }

    @Test fun textareaFocusLight() = areaFocus(false)

    @Test fun textareaFocusDark() = areaFocus(true)

    @Test fun controlsKeepTheirDimensionsAndTextareaPlaceholderStartsAtTheTop() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Column(Modifier.width(320.dp)) {
                    Box(
                        Modifier.testTag("input-box"),
                    ) { Input("", {}, Modifier.testTag("input"), placeholder = "Email") }
                    Box(
                        Modifier.testTag("area-box"),
                    ) { Textarea("", {}, Modifier.testTag("area"), placeholder = "Message") }
                }
            }
        }
        composeRule.onNodeWithTag("input-box").assertHeightIsEqualTo(32.dp)
        composeRule.onNodeWithTag("area-box").assertHeightIsEqualTo(64.dp)
        val area = composeRule.onNodeWithTag("area-box").fetchSemanticsNode().boundsInRoot
        val placeholder =
            composeRule.onNodeWithText(
                "Message",
                useUnmergedTree = true,
            ).fetchSemanticsNode().boundsInRoot
        assertTrue(placeholder.top - area.top in 8f..10f)
        composeRule.onNodeWithTag("input").requestFocus()
        composeRule.onNodeWithTag("input-box").assertHeightIsEqualTo(32.dp)
    }

    @Test fun novaFieldSpacingUsesTwentyEightAndTwo() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldGroup(Modifier.width(320.dp)) {
                    Field(Modifier.testTag("first")) {
                        FieldLabel("Email", Modifier.testTag("label"))
                        Box(Modifier.testTag("input-box")) { Input("", {}, Modifier.testTag("input")) }
                    }
                    Field(Modifier.testTag("second")) {
                        FieldContent {
                            FieldTitle("Title", Modifier.testTag("title"))
                            FieldDescription("Help", Modifier.testTag("help"))
                        }
                    }
                }
            }
        }

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        assertEquals(20f, bounds("second").top - bounds("first").bottom)
        assertEquals(8f, bounds("input-box").top - bounds("label").bottom)
        assertEquals(2f, bounds("help").top - bounds("title").bottom)
    }

    @Test fun labelsAndSupportingCopyUseNovaTypography() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Column {
                    Label("Label")
                    FieldLabel("Field label")
                    FieldDescription("Help")
                    FieldError("Error")
                }
            }
        }
        for ((text, height) in listOf("Label" to 14.sp, "Field label" to 19.25.sp, "Help" to 20.sp, "Error" to 20.sp)) {
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithText(
                text,
                useUnmergedTree = true,
            ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
                it(results)
            }
            assertEquals(14.sp, results.single().layoutInput.style.fontSize)
            assertEquals(height, results.single().layoutInput.style.lineHeight)
            if (text.contains(
                    "label",
                    ignoreCase = true,
                )
            ) {
                assertEquals(FontWeight.Medium, results.single().layoutInput.style.fontWeight)
            }
        }
    }

    @Test fun nestedFieldSetCannotReenableItsDisabledParent() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldSet(disabled = true) { FieldSet { Input("", {}, Modifier.testTag("input")) } }
            }
        }
        composeRule.onNodeWithTag("input").assertIsNotEnabled()
    }

    @Test fun textEditingHonorsMaxLengthAndInvalidSemantics() {
        var value by mutableStateOf("")
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Input(value, { value = it }, Modifier.testTag("input"), maxLength = 4, invalid = true)
            }
        }
        composeRule.onNodeWithTag("input").performTextInput("abcdef")
        assertEquals("abcd", value)
        composeRule.onNodeWithTag(
            "input",
        ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Invalid input"))
    }

    @Test fun inputBackgroundFollowsLiveThemeChanges() {
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark) {
                Box(Modifier.background(currentTheme.colors.background)) {
                    Input("", {}, Modifier.width(320.dp).testTag("input"))
                }
            }
        }

        fun check(darkMode: Boolean) {
            val colors = if (darkMode) BaseColor.Neutral.dark else BaseColor.Neutral.light
            val expected =
                if (darkMode) {
                    colors.input.copy(
                        alpha = colors.input.alpha * 0.3f,
                    ).compositeOver(colors.background)
                } else {
                    colors.background
                }
            val pixels = composeRule.onNodeWithTag("input").captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 2, pixels.height / 2]
            assertTrue(kotlin.math.abs(expected.red - actual.red) < 0.015f)
        }
        check(false)
        composeRule.runOnIdle { dark = true }
        check(true)
    }

    @Test fun responsiveFieldsSwitchAtTheContainerMdBreakpoint() {
        var width by mutableStateOf(450.dp)
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Field(Modifier.width(width), orientation = FieldOrientation.Responsive) {
                    Box(Modifier.size(16.dp).testTag("a"))
                    Box(Modifier.size(16.dp).testTag("b"))
                }
            }
        }

        fun bounds(tag: String) = composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        assertEquals(bounds("a").top, bounds("b").top)
        composeRule.runOnIdle { width = 447.dp }
        assertEquals(8f, bounds("b").top - bounds("a").bottom)
    }

    @Test fun textareaGrowsWithMultilineContent() {
        var value by mutableStateOf("")
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Box(
                    Modifier.width(320.dp).testTag("outer"),
                ) { Textarea(value, { value = it }, Modifier.testTag("area")) }
            }
        }
        composeRule.onNodeWithTag("outer").assertHeightIsEqualTo(64.dp)
        composeRule.onNodeWithTag("area").performTextInput("one\ntwo\nthree\nfour\nfive")
        assertEquals("one\ntwo\nthree\nfour\nfive", value)
        assertTrue(composeRule.onNodeWithTag("outer").fetchSemanticsNode().boundsInRoot.height > 100f)
    }
}
