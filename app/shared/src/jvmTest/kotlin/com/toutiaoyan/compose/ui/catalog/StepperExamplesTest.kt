package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.stepperDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StepperExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun pageIsLabeledAsKotlinExtension() {
        assertEquals("Stepper", stepperDoc.title)
        assertTrue(stepperDoc.isKotlinExtension)
        val guide = stepperDoc.beforeExamples.single { it.title == "Kotlin extension" }
        val paragraph = assertIs<ComponentGuideBlock.Paragraph>(guide.blocks.single())
        assertTrue(paragraph.markdown.contains("not an official"))
        assertEquals("import com.toutiaoyan.compose.ui.components.stepper.*", stepperDoc.usageImportsCode)
    }

    @Test fun apiReferenceDocumentsStepperAndStepperItem() {
        assertEquals(listOf("Stepper", "StepperItem"), stepperDoc.apiSections.map { it.title })
        assertTrue(stepperDoc.apiSections.first().properties.any { it.name == "currentStep" })
        assertTrue(stepperDoc.apiSections.last().properties.any { it.name == "description" })
    }

    @Test fun completeStepActionAdvancesTheIndicator() {
        val example = stepperDoc.examples.single { it.title == "Mid-flow action" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        val before = rule.onNodeWithText("3").captureToImage().toPixelMap()[0, 0]
        rule.onNodeWithText("Complete Step").performClick()
        val after = rule.onNodeWithText("3").captureToImage().toPixelMap()[0, 0]
        assertNotEquals(before, after, "Completing the current step must reach the next indicator")
        assertTrue(example.code.contains("step = (step + 1).coerceAtMost(2)"))
    }

    @Test fun formStepActionAcknowledgesTheEnteredInformation() {
        val example = stepperDoc.examples.single { it.title == "Form step" }
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        rule.onNodeWithText("Next").performClick()
        rule.onNodeWithText("Personal information complete").assertIsDisplayed()
        assertTrue(example.code.contains("submitted = true"))
    }

    @Test fun defaultPreviewRendersStepsAndAdvances() {
        val example = stepperDoc.examples.first()
        assertEquals("Default", example.title)
        rule.setContent { Theme(preset = StylePreset.Nova, minTouchTarget = null) { example.RenderPreview() } }
        rule.onNodeWithText("Details").assertIsDisplayed()
        rule.onNodeWithText("Review").assertIsDisplayed()
        rule.onNodeWithText("Done").assertIsDisplayed()
        rule.onNodeWithText("Next").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Review").assertIsDisplayed()
    }
}
