package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.inputDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InputExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String, width: Int = 640) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                Box(Modifier.width(width.dp)) {
                    inputDoc.examples.single { it.title == title }.preview()
                }
            }
        }
    }

    @Test fun officialRecipeOrderMatchesCurrentPage() {
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Field",
                "Field Group",
                "Disabled",
                "Invalid",
                "File",
                "Inline",
                "Grid",
                "Required",
                "Badge",
                "Input Group",
                "Button Group",
                "Form",
                "RTL",
            ),
            inputDoc.examples.map { it.title },
        )
        assertEquals(
            "A text input component for forms and user data entry with built-in styling and " +
                "accessibility features.",
            inputDoc.description,
        )
        assertTrue(inputDoc.examples[3].descriptionMarkdown!!.contains("multiple `Field` blocks"))
        assertTrue(inputDoc.examples[7].descriptionMarkdown!!.contains("inline input"))
        assertTrue(inputDoc.examples[13].descriptionMarkdown!!.contains("multiple inputs, a select"))
        assertTrue(inputDoc.examples[14].descriptionMarkdown!!.contains("RTL configuration guide"))
    }

    @Test fun officialCopyIsPresentInTheExportedCode() {
        val byTitle = inputDoc.examples.associateBy { it.title }
        assertTrue(byTitle.getValue("Default").code.contains("Your API key is encrypted and stored securely."))
        assertTrue(byTitle.getValue("Field").code.contains("Choose a unique username for your account."))
        assertTrue(byTitle.getValue("Field Group").code.contains("We'll send updates to this address."))
        assertTrue(byTitle.getValue("Disabled").code.contains("This field is currently disabled."))
        assertTrue(byTitle.getValue("Invalid").code.contains("This field contains validation errors."))
        assertTrue(byTitle.getValue("File").code.contains("No file chosen"))
        assertTrue(byTitle.getValue("Grid").code.contains("First Name"))
        assertTrue(byTitle.getValue("Required").code.contains("This field is required"))
        assertTrue(byTitle.getValue("Badge").code.contains("Beta"))
        assertTrue(byTitle.getValue("Input Group").code.contains("https://"))
        assertTrue(byTitle.getValue("Button Group").code.contains("Type to search..."))
        assertTrue(byTitle.getValue("Form").code.contains("john@example.com"))
    }

    @Test fun inputsFillTheConfiguredWidth() {
        show("Basic")
        val input = rule.onNode(hasSetTextAction()).getBoundsInRoot()
        // The 320dp field box minus the 10dp inline padding on each side.
        assertTrue((input.right - input.left).value >= 298f)
    }

    @Test fun disabledRecipeDisablesFieldAndInput() {
        show("Disabled")
        rule.onAllNodesWithText("Email")[1].assertIsNotEnabled()
        rule.onNodeWithText("This field is currently disabled.").assertIsDisplayed()
    }

    @Test fun inlineRecipeLetsTheInputFillTheRow() {
        show("Inline")
        val input = rule.onNode(hasSetTextAction()).getBoundsInRoot()
        val button = rule.onNodeWithText("Search").getBoundsInRoot()
        assertTrue((input.right - input.left).value > 200f)
        assertTrue(input.right.value < button.left.value)
        assertTrue(button.right.value - 16f <= 320f)
    }

    @Test fun formStartsWithTheSelectedCountry() {
        show("Form", width = 480)
        rule.onNodeWithText("United States").assertIsDisplayed()
        rule.onNodeWithText("Submit").assertIsDisplayed()
        rule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test fun requiredLabelShowsTheDestructiveAsterisk() {
        show("Required")
        rule.onNodeWithText("Required Field").assertIsDisplayed()
        rule.onNodeWithText("*", substring = true).assertIsDisplayed()
    }

    @Test fun rtlRecipeShowsArabicCopy() {
        show("RTL")
        rule.onNodeWithText("مفتاح API").assertIsDisplayed()
        rule.onNodeWithText("مفتاح API الخاص بك مشفر ومخزن بأمان.").assertIsDisplayed()
    }
}
