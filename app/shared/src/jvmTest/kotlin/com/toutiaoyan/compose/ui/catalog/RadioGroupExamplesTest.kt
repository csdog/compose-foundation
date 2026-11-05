package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.radioGroupDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RadioGroupExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(560.dp).verticalScroll(rememberScrollState())) {
                    radioGroupDoc.examples.single { it.title == title }.preview()
                }
            }
        }
        rule.waitForIdle()
    }

    private fun radios() = rule.onAllNodes(isSelectable())

    private fun selectedRadios() = rule.onAllNodes(isSelected())

    @Test fun pageListsOfficialRecipesInOrder() {
        assertEquals(
            listOf(
                "Default",
                "Description",
                "Choice Card",
                "Fieldset",
                "Disabled",
                "Invalid",
                "RTL",
                "Kotlin extension: Horizontal orientation",
                "Kotlin extension: Required",
            ),
            radioGroupDoc.examples.map { it.title },
        )
        assertEquals(
            "A set of checkable buttons—known as radio buttons—where no more than one of the buttons " +
                "can be checked at a time.",
            radioGroupDoc.description,
        )
        assertNull(radioGroupDoc.examplesHeading)
        assertFalse(radioGroupDoc.examples.first().showHeading)
        assertEquals(
            "Use the `disabled` prop on `RadioGroup` to disable all items.",
            radioGroupDoc.examples.single { it.title == "Disabled" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `invalid` on `RadioGroupItem` and `Field` to show validation errors.",
            radioGroupDoc.examples.single { it.title == "Invalid" }.descriptionMarkdown,
        )
    }

    @Test fun pageKeepsOfficialUsageCompositionAndApiCopy() {
        assertTrue(radioGroupDoc.usageImportsCode!!.contains("radiobutton.*"))
        listOf("RadioGroup", "RadioGroupItem", "Label").forEach {
            assertTrue(radioGroupDoc.usageCode.contains(it), it)
        }
        assertEquals("RadioGroup\n├── RadioGroupItem\n└── RadioGroupItem", radioGroupDoc.compositionCode)
        assertEquals(
            listOf("RadioGroup", "RadioGroupItem", "RadioGroupItem (with text)"),
            radioGroupDoc.apiSections.map { it.title },
        )
        assertTrue(
            radioGroupDoc.apiSections.first().descriptionMarkdown.contains("base-ui.com/react/components/radio-group"),
        )
    }

    @Test fun defaultStartsOnComfortableAndLabelClickSelects() {
        show("Default")
        assertEquals(3, radios().fetchSemanticsNodes().size)
        radios()[1].assertIsSelected()
        rule.onNodeWithText("Compact").performClick()
        rule.waitForIdle()
        radios()[2].assertIsSelected()
        rule.onNodeWithText("Default").performClick()
        rule.waitForIdle()
        radios()[0].assertIsSelected()
        assertEquals(1, selectedRadios().fetchSemanticsNodes().size)
    }

    @Test fun descriptionRecipeRendersFieldsAndSelects() {
        show("Description")
        rule.onNodeWithText("Standard spacing for most use cases.").assertIsDisplayed()
        rule.onNodeWithText("More space between elements.").assertIsDisplayed()
        rule.onNodeWithText("Minimal spacing for dense layouts.").assertIsDisplayed()
        rule.onNodeWithText("Compact").performClick()
        rule.waitForIdle()
        radios()[2].assertIsSelected()
    }

    @Test fun choiceCardClickSelectsThePlan() {
        show("Choice Card")
        listOf("Plus", "Pro", "Enterprise").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        rule.onNodeWithText("Enterprise").performClick()
        rule.waitForIdle()
        val cards = radios().fetchSemanticsNodes()
        assertEquals(3, cards.size)
        // Cards are ordered Plus, Pro, Enterprise.
        rule.onAllNodes(isSelectable())[2].assertIsSelected()
        assertEquals(1, selectedRadios().fetchSemanticsNodes().size)
        rule.onNodeWithTag("radio-card-enterprise").assertExists()
    }

    @Test fun fieldsetShowsLegendAndOptions() {
        show("Fieldset")
        rule.onNodeWithText("Subscription Plan").assertIsDisplayed()
        rule.onNodeWithText("Yearly and lifetime plans offer significant savings.").assertIsDisplayed()
        listOf("Monthly ($9.99/month)", "Yearly ($99.99/year)", "Lifetime ($299.99)").forEach {
            rule.onNodeWithText(it).assertIsDisplayed()
        }
        rule.onNodeWithText("Yearly ($99.99/year)").performClick()
        rule.waitForIdle()
        radios()[1].assertIsSelected()
    }

    @Test fun disabledItemCannotBeSelectedAndIsSkippedByArrows() {
        show("Disabled")
        rule.onNodeWithText("Disabled").performClick()
        rule.waitForIdle()
        // The first item stays disabled; Option 2 remains the only selection.
        radios()[1].assertIsSelected()
        val selectable = radios().fetchSemanticsNodes()
        assertEquals(3, selectable.size)
        rule.onNodeWithText("Option 3").performClick()
        rule.waitForIdle()
        radios()[2].assertIsSelected()
        // Arrow navigation wraps across the enabled items and skips the disabled first one.
        radios()[2].requestFocus()
        radios()[2].performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        radios()[1].assertIsSelected()
        radios()[1].performKeyInput { pressKey(Key.DirectionUp) }
        rule.waitForIdle()
        radios()[2].assertIsSelected()
    }

    @Test fun endAndHomeMoveSelectionToGroupEnds() {
        show("Default")
        radios()[1].requestFocus()
        radios()[1].performKeyInput { pressKey(Key.MoveEnd) }
        rule.waitForIdle()
        radios()[2].assertIsSelected()
        radios()[2].performKeyInput { pressKey(Key.MoveHome) }
        rule.waitForIdle()
        radios()[0].assertIsSelected()
    }

    @Test fun invalidRowsRenderErrorContext() {
        show("Invalid")
        rule.onNodeWithText("Notification Preferences").assertIsDisplayed()
        rule.onNodeWithText("Choose how you want to receive notifications.").assertIsDisplayed()
        listOf("Email only", "SMS only", "Both Email & SMS").forEach {
            rule.onNodeWithText(it).assertIsDisplayed()
        }
        assertEquals(3, radios().fetchSemanticsNodes().size)
        radios()[0].assertIsSelected()
        rule.onNodeWithText("SMS only").performClick()
        rule.waitForIdle()
        radios()[1].assertIsSelected()
    }

    @Test fun rtlHeaderAndRowsFollowThePageDirection() {
        show("RTL")
        rule.onNodeWithText("افتراضي").assertIsDisplayed()
        rule.onNodeWithText("مريح").assertIsDisplayed()
        rule.onNodeWithText("مضغوط").assertIsDisplayed()
        val radioX = radios()[1].fetchSemanticsNode().boundsInRoot.left
        val labelX = rule.onNodeWithText("مريح").fetchSemanticsNode().boundsInRoot.left
        assertTrue(radioX > labelX, "radio $radioX should sit right of its label $labelX in RTL")
        // Language switch flips to LTR English.
        rule.onNodeWithText("Arabic (العربية)").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("English").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Comfortable").assertIsDisplayed()
        val ltrRadioX = radios()[1].fetchSemanticsNode().boundsInRoot.left
        val ltrLabelX = rule.onNodeWithText("Comfortable").fetchSemanticsNode().boundsInRoot.left
        assertTrue(ltrRadioX < ltrLabelX, "radio $ltrRadioX should sit left of its label $ltrLabelX in LTR")
    }

    @Test fun horizontalExtensionUsesASingleRow() {
        show("Kotlin extension: Horizontal orientation")
        val nodes = radios().fetchSemanticsNodes()
        assertEquals(3, nodes.size)
        val tops = nodes.map { it.boundsInRoot.top }
        assertTrue(tops.max() - tops.min() < 2f, "radios should share one row: $tops")
    }

    @Test fun requiredExtensionKeepsSingleSelection() {
        show("Kotlin extension: Required")
        radios()[0].assertIsSelected()
        rule.onNodeWithText("Option B").performClick()
        rule.waitForIdle()
        radios()[1].assertIsSelected()
        assertEquals(1, selectedRadios().fetchSemanticsNodes().size)
    }
}
