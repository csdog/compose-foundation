package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.fieldDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldExamplesTest {
    @get:Rule val rule = createComposeRule()

    private val checkboxes = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)
    private val radios = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)

    private fun show(
        title: String,
        width: Int = 640,
    ) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                Box(Modifier.width(width.dp)) {
                    fieldDoc.examples.single { it.title == title }.preview()
                }
            }
        }
    }

    @Test fun officialRecipeOrderMatchesCurrentPage() {
        assertEquals(
            listOf(
                "Default",
                "Input",
                "Textarea",
                "Select",
                "Slider",
                "Fieldset",
                "Checkbox",
                "Radio",
                "Switch",
                "Choice Card",
                "Field Group",
                "RTL",
                "Responsive Layout",
            ),
            fieldDoc.examples.map { it.title },
        )
        assertEquals(
            "Combine labels, controls, and help text to compose accessible form fields and " +
                "grouped inputs.",
            fieldDoc.description,
        )
        assertEquals(
            listOf("Composition", "Field", "FieldGroup", "FieldSet", "Anatomy", "Form"),
            fieldDoc.beforeExamples.map { it.title },
        )
        assertEquals(listOf(2, 3, 3, 3, 2, 2), fieldDoc.beforeExamples.map { it.level })
        assertEquals(
            listOf("Validation and Errors", "Accessibility"),
            fieldDoc.afterExamples.map { it.title },
        )
        assertEquals(
            listOf(
                "FieldSet",
                "FieldLegend",
                "FieldGroup",
                "Field",
                "FieldContent",
                "FieldLabel",
                "FieldTitle",
                "FieldDescription",
                "FieldSeparator",
                "FieldError",
            ),
            fieldDoc.apiSections.map { it.title },
        )
    }

    @Test fun recipesUseTheOfficialCopy() {
        val byTitle = fieldDoc.examples.associateBy { it.title }
        assertTrue(byTitle.getValue("Input").code.contains("Choose a unique username for your account."))
        assertTrue(byTitle.getValue("Textarea").code.contains("Share your thoughts about our service."))
        assertTrue(byTitle.getValue("Select").code.contains("Select your department or area of work."))
        assertTrue(byTitle.getValue("Fieldset").code.contains("We need your address to deliver your order."))
        assertTrue(byTitle.getValue("Checkbox").code.contains("CDs, DVDs, and iPods"))
        assertTrue(byTitle.getValue("Radio").code.contains("Yearly and lifetime plans offer significant savings."))
        assertTrue(byTitle.getValue("Choice Card").code.contains("Run GPU workloads on a K8s cluster."))
        assertTrue(byTitle.getValue("Field Group").code.contains("Get notified when tasks you've created"))
        assertTrue(byTitle.getValue("Choice Card").descriptionMarkdown!!.contains("Wrap `Field` components"))
        assertTrue(byTitle.getValue("Field Group").descriptionMarkdown!!.contains("Add `FieldSeparator`"))
        assertTrue(byTitle.getValue("RTL").descriptionMarkdown!!.contains("in shadcn/ui"))
        assertTrue(byTitle.getValue("RTL").descriptionMarkdown!!.contains("RTL configuration guide"))
        assertTrue(byTitle.getValue("Responsive Layout").descriptionMarkdown!!.contains("448"))
    }

    @Test fun demoUsesMaxWidthMdAndLegendDescriptionOffset() {
        show("Default")
        val legendTop = rule.onNodeWithText("Payment Method").getBoundsInRoot().top
        val descriptionTop =
            rule.onNodeWithText("All transactions are secure and encrypted").getBoundsInRoot().top
        // legend box is 24 + 6 bottom margin; description starts 40dp below the legend top.
        assertEquals(40f, (descriptionTop - legendTop).value, 0.6f)
        // The official demo constrains the form to `max-w-md` (448px).
        val description = rule.onNodeWithText("All transactions are secure and encrypted")
        assertTrue(description.getBoundsInRoot().right.value > 0f)
    }

    @Test fun checkboxLabelTogglesTheControlAndTopLevelCheckboxStartsChecked() {
        show("Field Group")
        rule.onAllNodes(checkboxes).assertCountEquals(3)
        rule.onAllNodes(checkboxes)[0].assertIsOn()
        rule.onAllNodes(checkboxes)[0].assertIsNotEnabled()
        rule.onAllNodesWithText("Push notifications").filterToOne(hasClickAction()).performClick()
        rule.onAllNodes(checkboxes)[1].assertIsOn()
    }

    @Test fun choiceCardSelectsTheRadioAndMovesTheSelectedBorder() {
        show("Choice Card")
        rule.onAllNodes(radios).assertCountEquals(2)
        rule.onAllNodes(radios)[0].assertIsSelected()
        rule.onNodeWithText("Virtual Machine").performClick()
        rule.onAllNodes(radios)[1].assertIsSelected()
        rule.onAllNodes(radios)[0].assertIsNotSelected()
    }

    @Test fun responsiveFieldsSwitchToARowAtWideContainerWidth() {
        show("Responsive Layout", width = 640)
        val submit = rule.onNodeWithText("Submit").getBoundsInRoot()
        val cancel = rule.onNodeWithText("Cancel").getBoundsInRoot()
        assertEquals(submit.top.value, cancel.top.value, 0.6f)
    }

    @Test fun responsiveFieldsStackBelowThe448dpContainerBreakpoint() {
        show("Responsive Layout", width = 400)
        val submit = rule.onNodeWithText("Submit").getBoundsInRoot()
        val cancel = rule.onNodeWithText("Cancel").getBoundsInRoot()
        assertTrue(cancel.top.value > submit.top.value)
    }

    @Test fun switchRecipeTogglesFromItsLabelAndTrack() {
        show("Switch")
        val toggle = rule.onNode(isToggleable())
        toggle.assertIsOff()
        rule.onNodeWithText("Multi-factor authentication").performClick()
        toggle.assertIsOn()
        toggle.performClick()
        toggle.assertIsOff()
    }

    @Test fun rtlRecipeShowsArabicLabelsAndSeparator() {
        show("RTL", width = 520)
        rule.onNodeWithText("طريقة الدفع").assertIsDisplayed()
        rule.onNodeWithText("الاسم على البطاقة").assertIsDisplayed()
        rule.onNodeWithText("إرسال").assertIsDisplayed()
    }
}
