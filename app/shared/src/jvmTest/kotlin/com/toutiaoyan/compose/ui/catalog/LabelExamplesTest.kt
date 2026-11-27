package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
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
                        labelDoc.examples.single { it.title == title }.RenderPreview()
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
        val guide = labelDoc.beforeExampleSections.getValue("Label in Field").single()
        assertTrue((guide.blocks.first() as ComponentGuideBlock.Paragraph).markdown.contains("FieldLabel"))
        val code = (guide.blocks.last() as ComponentGuideBlock.Code).code
        assertTrue(code.contains("LabeledFieldControl"))
        val page = componentPageMarkdown(labelDoc)
        assertEquals(1, page.lineSequence().count { it == "## Label in Field" })
        assertTrue(page.indexOf("## Label in Field") < page.indexOf(code))
        assertTrue(page.indexOf(code) < page.indexOf("fun LabelInFieldDemo"))
        assertTrue(
            labelDoc.examples.single { it.title == "RTL" }
                .descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support"),
        )
    }

    @Test fun clickingTheDemoLabelTogglesItsCheckbox() {
        show("Default")
        val checkbox = rule.onNode(isToggleable() and hasContentDescription("Accept terms and conditions"))
        checkbox.assertIsOff()
        rule.onNodeWithText("Accept terms and conditions").performTouchInput { click() }
        checkbox.assertIsOn().assertIsFocused()
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
        val page = componentPageMarkdown(labelDoc)
        val guidance = "built-in label, description, and error handling"
        assertTrue(labelDoc.introMarkdown.orEmpty().contains(guidance))
        assertTrue(page.indexOf(guidance) < page.indexOf("## Installation"))
    }

    @Test fun paymentGuidanceAndBillingLabelActivationWork() {
        show("Label in Field")
        rule.onNodeWithText("Enter your 16-digit card number").assertExists()
        val checkbox = rule.onNode(isToggleable() and hasContentDescription("Same as shipping address"))
        checkbox.assertIsOn()
        rule.onNodeWithText("Same as shipping address").performTouchInput { click() }
        checkbox.assertIsOff().assertIsFocused()
    }

    @Test fun paymentLabelsFocusTheirNamedControls() {
        show("Label in Field")
        listOf("Name on Card", "Card Number", "CVV", "Comments").forEach { label ->
            val control = rule.onNode(hasSetTextAction() and hasContentDescription(label, substring = true))
            rule.onNodeWithText(label).performTouchInput { click() }
            control.assertIsFocused()
        }
        listOf(Triple("Month", "MM", "01"), Triple("Year", "YYYY", "2024")).forEach { (label, placeholder, option) ->
            rule.onNodeWithText(label).performTouchInput { click() }
            val item = rule.onNodeWithText(option).assertIsDisplayed()
            item.performKeyInput { pressKey(Key.Escape) }
            item.assertDoesNotExist()
            rule.onNodeWithContentDescription("$label, $placeholder").assertIsFocused()

            rule.onNodeWithText(label).performTouchInput { click() }
            item.performClick()
            rule.onNodeWithContentDescription("$label, $option").assertIsFocused()
        }
    }

    @Test fun rtlRecipeChangesLanguageAndReversesTheControlRow() {
        show("RTL")

        fun verifyCenteredLabel(
            text: String,
            rtl: Boolean,
        ) {
            val checkbox = rule.onNode(isToggleable()).fetchSemanticsNode().boundsInRoot
            val label = rule.onNodeWithText(text).fetchSemanticsNode().boundsInRoot
            assertEquals(rtl, checkbox.left > label.right)
            val groupCenter = (minOf(checkbox.left, label.left) + maxOf(checkbox.right, label.right)) / 2
            assertEquals(360f, groupCenter, 1f)
        }
        verifyCenteredLabel("قبول الشروط والأحكام", rtl = true)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        verifyCenteredLabel("Accept terms and conditions", rtl = false)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        verifyCenteredLabel("קבל תנאים והגבלות", rtl = true)
        rule.onNodeWithText("קבל תנאים והגבלות").performTouchInput { click() }
        rule.onNode(isToggleable() and hasContentDescription("קבל תנאים והגבלות")).assertIsOn().assertIsFocused()
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
