package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.textareaDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextareaExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun officialRecipeOrderAndDescriptionMatchCurrentPage() {
        assertEquals("Displays a form textarea or a component that looks like a textarea.", textareaDoc.description)
        assertEquals(
            listOf(
                "Default",
                "Field",
                "Disabled",
                "Invalid",
                "Button",
                "RTL",
                "Kotlin extension: Rows",
                "Kotlin extension: Min and max lines",
                "Kotlin extension: Max length",
                "Kotlin extension: Autofocus",
                "Kotlin extension: Read only",
            ),
            textareaDoc.examples.map { it.title },
        )
        assertFalse(textareaDoc.examples.first().showHeading)
        assertTrue(textareaDoc.examples.drop(6).all { it.title.startsWith("Kotlin extension: ") })
    }

    @Test fun fieldRecipeRendersLabelDescriptionAndTextarea() {
        val example = textareaDoc.examples.single { it.title == "Field" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        rule.onNodeWithText("Message").assertIsDisplayed()
        rule.onNodeWithText("Enter your message below.").assertIsDisplayed()
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(1)
    }

    @Test fun disabledRecipeDisablesTheTextarea() {
        val example = textareaDoc.examples.single { it.title == "Disabled" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(0)
        rule.onNodeWithText("Type your message here.").assertExists()
    }

    @Test fun invalidRecipeShowsTheErrorDescription() {
        val example = textareaDoc.examples.single { it.title == "Invalid" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        rule.onNodeWithText("Please enter a valid message.").assertIsDisplayed()
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(1)
    }

    @Test fun buttonRecipePairsTextareaWithSubmit() {
        val example = textareaDoc.examples.single { it.title == "Button" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        rule.onNodeWithText("Send message").assertIsDisplayed()
    }

    @Test fun rtlRecipeStartsWithArabicCopy() {
        val example = textareaDoc.examples.single { it.title == "RTL" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        rule.onNodeWithText("التعليقات").assertExists()
        rule.onNodeWithText("شاركنا أفكارك حول خدمتنا.").assertExists()
    }

    @Test fun copyPageMarkdownKeepsOfficialOrderAndExtensions() {
        val markdown = componentPageMarkdown(textareaDoc)
        val headings = listOf("## Field", "## Disabled", "## Invalid", "## Button", "## RTL")
        val positions =
            headings.map { heading ->
                markdown.indexOf(heading).also { assertTrue(it >= 0, "missing $heading") }
            }
        assertEquals(positions.sorted(), positions)
        assertFalse(markdown.contains("## Examples"))
        assertTrue(markdown.contains("## Rows"))
    }
}
