package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.inputGroupDoc
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupInput
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class InputGroupExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun officialPageOrderAndCopyMatchCurrentOfficialPage() {
        assertEquals(
            listOf(
                "Default",
                "inline-start",
                "inline-end",
                "block-start",
                "block-end",
                "Icon",
                "Text",
                "Button",
                "Kbd",
                "Dropdown",
                "Spinner",
                "Textarea",
                "Custom Input",
                "RTL",
            ),
            inputGroupDoc.examples.map { it.title },
        )
        assertEquals("Add addons, buttons, and helper content to inputs.", inputGroupDoc.description)
        assertEquals(null, inputGroupDoc.examplesHeading)
        assertEquals(listOf("Align"), inputGroupDoc.beforeExamples.map { it.title })
        val align = inputGroupDoc.beforeExamples.single().blocks
        assertTrue(assertIs<ComponentGuideBlock.Paragraph>(align.first()).markdown.contains("`align`"))
        assertTrue(
            assertIs<ComponentGuideBlock.Paragraph>(align.last()).markdown.contains("after `InputGroupInput`"),
        )
        assertEquals(
            "Use `align=\"inline-start\"` to position the addon at the start of the input. This is the default.",
            inputGroupDoc.examples.single { it.title == "inline-start" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `align=\"inline-end\"` to position the addon at the end of the input.",
            inputGroupDoc.examples.single { it.title == "inline-end" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `align=\"block-start\"` to position the addon above the input.",
            inputGroupDoc.examples.single { it.title == "block-start" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `align=\"block-end\"` to position the addon below the input.",
            inputGroupDoc.examples.single { it.title == "block-end" }.descriptionMarkdown,
        )
        assertEquals(
            listOf("InputGroup", "InputGroupAddon", "InputGroupButton", "InputGroupInput / InputGroupTextarea", "InputGroupText"),
            inputGroupDoc.apiSections.map { it.title },
        )
        assertEquals(
            listOf("inline-start", "inline-end", "block-start", "block-end"),
            inputGroupDoc.examples.filter { it.headingLevel == 3 }.map { it.title },
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
                        key(index) { inputGroupDoc.examples[index].preview() }
                    }
                }
            }
        }
        for (i in inputGroupDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
        assertEquals(14, inputGroupDoc.examples.size)
    }

    @Test fun demoAddonContentAndBlockEndFooterRender() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "Default" }.preview()
            }
        }
        rule.onNodeWithText("Search...").assertExists()
        rule.onNodeWithText("12 results").assertExists()
        rule.onNodeWithContentDescription("Search").assertExists()
    }

    @Test fun blockEndTextareaShowsCountAndPostAction() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "block-end" }.preview()
            }
        }
        rule.onNodeWithText("USD").assertExists()
        rule.onNodeWithText("0/280").assertExists()
        rule.onNodeWithText("Post").assertExists()
        rule.onNodeWithText("Write a comment...").assertExists()
    }

    @Test fun spinnerRecipeExposesAllFourLoadingIndicators() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "Spinner" }.preview()
            }
        }
        rule.onAllNodesWithContentDescription("Loading").assertCountEquals(4)
    }

    @Test fun kbdRecipeRendersTheShortcut() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "Kbd" }.preview()
            }
        }
        rule.onNodeWithText("⌘K").assertExists()
    }

    @Test fun disabledAndInvalidGroupStatesKeepInputSemantics() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                androidx.compose.foundation.layout.Column {
                    InputGroup(disabled = true) {
                        InputGroupInput(value = "20", onValueChange = {})
                    }
                    InputGroup(invalid = true) {
                        InputGroupInput(value = "20", onValueChange = {})
                    }
                }
            }
        }
        // A disabled group drops the editable text action; the invalid group stays editable.
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(1)
        rule.onAllNodes(hasSetTextAction())[0].assertIsEnabled()
    }

    @Test fun rtlPreviewSwitchesLanguages() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "RTL" }.preview()
            }
        }
        rule.onNodeWithText("١٢ نتيجة").assertExists()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("12 results").assertExists()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("12 תוצאות").assertExists()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
