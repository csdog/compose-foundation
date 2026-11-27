package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.spinnerDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SpinnerExamplesTest {
    @get:Rule val rule =
        createComposeRule(
            effectContext =
                object : MotionDurationScale {
                    override val scaleFactor = 0f
                },
        )

    private fun show(name: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                Box(Modifier.width(480.dp)) { spinnerDoc.examples.single { it.title == name }.RenderPreview() }
            }
        }
    }

    @Test fun actualOfficialRecipesReplaceDefaultValueDuplicates() {
        assertEquals(
            listOf(
                "Default",
                "Customization",
                "Size",
                "Button",
                "Badge",
                "Input Group",
                "Empty",
                "RTL",
            ),
            spinnerDoc.examples.map {
                it.title
            },
        )
    }

    @Test fun sizesAreTwelveSixteenTwentyFourAndThirtyTwo() {
        show("Size")
        val nodes = rule.onAllNodesWithContentDescription("Loading").fetchSemanticsNodes()
        assertEquals(listOf(12f, 16f, 24f, 32f), nodes.map { it.boundsInRoot.width })
    }

    @Test fun loadingButtonsAreDisabled() {
        show("Button")
        listOf("Loading...", "Please wait", "Processing").forEach { rule.onNodeWithText(it).assertIsNotEnabled() }
        rule.onAllNodesWithContentDescription("Loading").assertCountEquals(3)
    }

    @Test fun disabledInputsDoNotDisableTheSendAction() {
        show("Input Group")
        val editable = SemanticsMatcher.keyIsDefined(androidx.compose.ui.semantics.SemanticsProperties.EditableText)
        rule.onAllNodes(editable).assertCountEquals(2)
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(0)
        rule.onAllNodes(editable).fetchSemanticsNodes().forEach {
            rule.onNode(SemanticsMatcher("input ${it.id}") { node -> node.id == it.id }).assertIsNotEnabled()
        }
        rule.onNodeWithContentDescription("Send").assertIsEnabled().performClick()
        rule.onNodeWithText("Validating...").assertExists()
        rule.onAllNodesWithContentDescription("Loading").assertCountEquals(2)
    }

    @Test fun cancelIsEnabledAndKeepsTheOfficialUnboundLoadingDemo() {
        show("Empty")
        rule.onNodeWithText("Cancel").assertIsEnabled().performClick()
        rule.onNodeWithText("Processing your request").assertExists()
        rule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test fun defaultIsTheOfficialPaymentItem() {
        show("Default")
        rule.onNodeWithText("Processing payment...").assertExists()
        rule.onNodeWithText("$100.00").assertExists()
        rule.onNodeWithContentDescription("Loading", useUnmergedTree = true).assertWidthIsEqualTo(16.dp)
    }

    @Test fun customizationDrawsVisibleRaysWithoutAnInlineParentColor() {
        show("Customization")
        val pixels = rule.onNodeWithContentDescription("Loading").captureToImage().toPixelMap()
        var ink = 0
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
            if (pixels[x, y].alpha > 0.1f && pixels[x, y].red < 0.5f) ink++
        }
        kotlin.test.assertTrue(ink > 12, "The custom loader must paint visible rays")
    }
}
