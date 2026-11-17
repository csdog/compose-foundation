package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.semantics.SemanticsActions
import com.toutiaoyan.compose.ui.catalog.docs.inputOTPDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.components.inputotp.InputOTP
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPGroup
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPSlot
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class InputOTPExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun officialPageOrderAndCopyMatchCurrentOfficialPage() {
        assertEquals(
            listOf(
                "Default",
                "Pattern",
                "Separator",
                "Disabled",
                "Controlled",
                "Invalid",
                "Four Digits",
                "Alphanumeric",
                "Form",
                "RTL",
            ),
            inputOTPDoc.examples.map { it.title },
        )
        assertEquals(
            "Accessible one-time password component with copy-paste functionality.",
            inputOTPDoc.description,
        )
        assertEquals(null, inputOTPDoc.examplesHeading)
        assertEquals(listOf("About"), inputOTPDoc.introductionSections.map { it.title })
        val markdown = componentPageMarkdown(inputOTPDoc)
        assertTrue(markdown.indexOf("## About") < markdown.indexOf("## Installation"))
        assertTrue(markdown.contains("Use the following composition to build an `InputOTP`:"))
        assertEquals(
            "Use the `pattern` prop to define a custom pattern for the OTP input.",
            inputOTPDoc.examples.single { it.title == "Pattern" }.descriptionMarkdown,
        )
        assertEquals(
            "Use the `InputOTPSeparator` component to add a separator between input groups.",
            inputOTPDoc.examples.single { it.title == "Separator" }.descriptionMarkdown,
        )
        assertEquals(
            "Use the `disabled` prop to disable the input.",
            inputOTPDoc.examples.single { it.title == "Disabled" }.descriptionMarkdown,
        )
        assertEquals(
            "Use the `value` and `onValueChange` props to control the input value.",
            inputOTPDoc.examples.single { it.title == "Controlled" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `invalid` on the slots to show an error state.",
            inputOTPDoc.examples.single { it.title == "Invalid" }.descriptionMarkdown,
        )
        assertTrue(
            inputOTPDoc.examples
                .single { it.title == "RTL" }
                .descriptionMarkdown!!
                .contains("RTL configuration guide"),
        )
        val api = inputOTPDoc.afterExamples.single { it.title == "API Reference" }
        assertTrue(
            api.blocks.filterIsInstance<com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Paragraph>()
                .single()
                .markdown
                .contains("https://input-otp.rodz.dev"),
        )
    }

    @Test fun everyOfficialPreviewRendersAtBothWidths() {
        var index by mutableIntStateOf(0)
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    Box(
                        Modifier.requiredSize((if (width < 768) width - 65 else 638).dp, 420.dp)
                            .padding(40.dp)
                            .testTag("preview"),
                        contentAlignment = Alignment.Center,
                    ) {
                        key(index) { inputOTPDoc.examples[index].preview() }
                    }
                }
            }
        }
        for (i in inputOTPDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
        assertEquals(10, inputOTPDoc.examples.size)
    }

    @Test fun officialFormUsesLargeSlotTypography() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                Box(Modifier.requiredSize(560.dp, 600.dp)) {
                    inputOTPDoc.examples.single { it.title == "Form" }.preview()
                }
            }
        }
        rule.onNode(hasSetTextAction()).performTextInput("123456")
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("1").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(20.sp, layouts.single().layoutInput.style.fontSize)
        assertEquals(28.sp, layouts.single().layoutInput.style.lineHeight)
    }

    @Test fun formShowsAllSixDigitsWhenTheCardNarrows() {
        var availableWidth by mutableIntStateOf(560)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                Box(Modifier.requiredSize(availableWidth.dp, 620.dp)) {
                    inputOTPDoc.examples.single { it.title == "Form" }.preview()
                }
            }
        }
        rule.onNodeWithText("Verification code").performClick()
        rule.onNode(hasSetTextAction()).assertIsFocused().performTextInput("123456")
        for (width in listOf(255, 325, 560)) {
            rule.runOnIdle { availableWidth = width }
            val field = rule.onNode(hasSetTextAction()).fetchSemanticsNode().boundsInRoot
            val digits = (1..6).map { rule.onNodeWithText(it.toString()).fetchSemanticsNode().boundsInRoot }
            assertEquals(48f, field.height, absoluteTolerance = 1f)
            if (width >= 325) {
                assertEquals(304f, field.width, absoluteTolerance = 1f)
            }
            assertTrue(digits.all { it.width > 0 && it.left >= field.left && it.right <= field.right })
            assertTrue(digits.zipWithNext().all { (a, b) -> a.right <= b.left })
        }
    }

    @Test fun controlledPreviewReflectsTypedDigits() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                var value by remember { mutableStateOf("") }
                Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    InputOTP(value = value, onValueChange = { value = it }, length = 6) {
                        InputOTPGroup {
                            InputOTPSlot(0)
                            InputOTPSlot(1)
                            InputOTPSlot(2)
                            InputOTPSlot(3)
                            InputOTPSlot(4)
                            InputOTPSlot(5)
                        }
                    }
                    Text { if (value.isEmpty()) "empty" else "value:$value" }
                }
            }
        }
        rule.onNodeWithText("empty").assertExists()
        rule.onNode(hasSetTextAction()).performTextInput("12")
        rule.waitForIdle()
        rule.onNodeWithText("value:12").assertExists()
    }

    @Test fun invalidAndDisabledSlotsExposeTheirSemantics() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                Column {
                    InputOTP(
                        value = "000000",
                        onValueChange = {},
                        length = 6,
                        invalid = true,
                        contentDescription = "Invalid code",
                    ) {
                        InputOTPGroup {
                            InputOTPSlot(0, invalid = true)
                            InputOTPSlot(1, invalid = true)
                        }
                    }
                    InputOTP(
                        value = "123456",
                        onValueChange = {},
                        length = 6,
                        disabled = true,
                        contentDescription = "Disabled code",
                    ) {
                        InputOTPGroup {
                            InputOTPSlot(0)
                            InputOTPSlot(1)
                        }
                    }
                }
            }
        }
        rule.onNodeWithContentDescription("Invalid code").assertExists()
        rule.onNodeWithContentDescription("Invalid code").assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Error),
        )
        rule.onNodeWithContentDescription("Disabled code").assertIsNotEnabled()
    }

    @Test fun digitsPatternFiltersTypedLetters() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                var value by remember { mutableStateOf("") }
                Column {
                    InputOTP(
                        value = value,
                        onValueChange = { value = it },
                        length = 4,
                        pattern = Regex("[0-9]"),
                    ) {
                        InputOTPGroup {
                            InputOTPSlot(0)
                            InputOTPSlot(1)
                            InputOTPSlot(2)
                            InputOTPSlot(3)
                        }
                    }
                    Text { "digits:$value" }
                }
            }
        }
        rule.onNode(hasSetTextAction()).performTextInput("a1b2")
        rule.waitForIdle()
        rule.onNodeWithText("digits:12").assertExists()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
