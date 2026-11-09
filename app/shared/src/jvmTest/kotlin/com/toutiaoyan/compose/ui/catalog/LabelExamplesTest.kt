package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.labelDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LabelExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(720.dp).height(980.dp), contentAlignment = Alignment.TopCenter) {
                        labelDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesAndKotlinExtensionsAppearInOrder() {
        assertEquals(
            listOf(
                "Default",
                "Label in Field",
                "RTL",
                "Kotlin extension: Required",
                "Kotlin extension: Disabled",
            ),
            labelDoc.examples.map { it.title },
        )
        assertEquals("Renders an accessible label associated with controls.", labelDoc.description)
        assertTrue(
            labelDoc.examples.single { it.title == "Label in Field" }
                .descriptionMarkdown.orEmpty()
                .contains("FieldLabel"),
        )
        assertTrue(
            labelDoc.examples.single { it.title == "RTL" }
                .descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support"),
        )
    }

    @Test fun clickingTheDemoLabelTogglesItsCheckbox() {
        show("Default")
        rule.onNode(isToggleable()).assertIsOff()
        rule.onNodeWithText("Accept terms and conditions").performClick()
        rule.onNode(isToggleable()).assertIsOn()
    }

    @Test fun labelInFieldRendersTheOfficialPaymentForm() {
        show("Label in Field")
        listOf(
            "Payment Method",
            "Name on Card",
            "Card Number",
            "Month",
            "Year",
            "CVV",
            "Billing Address",
            "Comments",
            "Submit",
            "Cancel",
        ).forEach {
            rule.onNodeWithText(it).assertExists()
        }
    }

    @Test fun rtlRecipeShowsTheArabicLabel() {
        show("RTL")
        rule.onNodeWithText("قبول الشروط والأحكام").assertIsDisplayed()
    }

    @Test fun fieldGuidanceAppearsAfterThePrimaryPreview() {
        val page = com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown(labelDoc)
        val guidance = "built-in label, description, and error handling"
        assertTrue(labelDoc.introMarkdown.orEmpty().contains(guidance))
        assertTrue(page.indexOf(guidance) < page.indexOf("## Installation"))
    }

    @Test fun paymentGuidanceAndBillingLabelActivationWork() {
        show("Label in Field")
        rule.onNodeWithText("Enter your 16-digit card number").assertExists()
        rule.onNode(isToggleable()).assertIsOn()
        rule.onNodeWithText("Same as shipping address").performClick()
        rule.onNode(isToggleable()).assertIsOff()
    }

    @Test fun rtlRecipeChangesLanguageAndReversesTheControlRow() {
        show("RTL")

        fun checkboxLeft() = rule.onNode(isToggleable()).fetchSemanticsNode().boundsInRoot.left
        val arabicLeft = checkboxLeft()
        val arabicLabel = rule.onNodeWithText("قبول الشروط والأحكام").fetchSemanticsNode().boundsInRoot
        assertTrue(arabicLeft > arabicLabel.right, "Arabic puts the checkbox at logical start on the right")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        val englishLabel = rule.onNodeWithText("Accept terms and conditions").fetchSemanticsNode().boundsInRoot
        assertTrue(checkboxLeft() < englishLabel.left)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("קבל תנאים והגבלות").assertIsDisplayed()
        assertEquals(arabicLeft, checkboxLeft(), 0.1f)
        rule.onNodeWithText("קבל תנאים והגבלות").performClick()
        rule.onNode(isToggleable()).assertIsOn()
    }

    @Test fun requiredExtensionRendersTheDestructiveMarker() {
        show("Kotlin extension: Required")
        rule.onNodeWithText("Email").assertIsDisplayed()
        rule.onNodeWithText(" *", substring = true).assertIsDisplayed()
    }

    @Test fun disabledExtensionStillRendersItsText() {
        show("Kotlin extension: Disabled")
        rule.onNodeWithText("Email").assertIsDisplayed()
    }
}
