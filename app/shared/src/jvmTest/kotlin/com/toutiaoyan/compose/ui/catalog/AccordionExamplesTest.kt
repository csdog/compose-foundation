package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.accordionDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AccordionExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun basicRendersActualQuestionsAndSwitchesAnswer() {
        val example = accordionDoc.examples.single { it.title == "Basic" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("How do I reset my password?").assertIsDisplayed()
        rule.onNodeWithText("Can I change my subscription plan?").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Yes, you can upgrade", substring = true).assertIsDisplayed()
        rule.onNodeWithText("{item.trigger}").assertDoesNotExist()
    }

    @Test fun multipleKeepsBothRealAnswersOpen() {
        val example = accordionDoc.examples.single { it.title == "Multiple" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.preview() } }
        rule.onNodeWithText("Manage how you receive", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Privacy & Security").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Manage how you receive", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Control your privacy", substring = true).assertIsDisplayed()
        rule.onNodeWithText("{item.content}").assertDoesNotExist()
    }

    @Test fun officialSectionOrderAndCopyMatchThePageSource() {
        val multiple = accordionDoc.examples.single { it.title == "Multiple" }
        assertEquals(null, accordionDoc.examplesHeading)
        assertEquals(
            listOf("Default", "Basic", "Multiple", "Disabled", "Borders", "Card", "RTL"),
            accordionDoc.examples.take(7).map { it.title },
        )
        assertTrue(accordionDoc.examples.drop(7).all { it.title.startsWith("Kotlin extension: ") })
        assertEquals(
            "Use `multiple = true` to allow multiple items to be open at the same time.",
            multiple.descriptionMarkdown,
        )
        assertTrue(multiple.code.contains("multiple = true"))

        val markdown = componentPageMarkdown(accordionDoc)
        val order =
            listOf(
                "## Installation",
                "## Usage",
                "## Composition",
                "## Basic",
                "## Multiple",
                "## Disabled",
                "## Borders",
                "## Card",
                "## RTL",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing an official section: $order")
        assertEquals(order.sorted(), order)

        val api = accordionDoc.afterExamples.single { it.title == "API Reference" }
        val paragraph = assertIs<ComponentGuideBlock.Paragraph>(api.blocks.single())
        assertTrue(paragraph.markdown.contains("https://base-ui.com/react/components/accordion#api-reference"))
    }
}
