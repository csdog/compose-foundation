package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.sliderDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SliderPageTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(560.dp).verticalScroll(rememberScrollState())) {
                    sliderDoc.examples.single { it.title == title }.RenderPreview()
                }
            }
        }
        rule.waitForIdle()
    }

    private fun sliderNode(label: String) = rule.onNodeWithContentDescription(label, substring = true)

    private fun currentValue(label: String): Float =
        sliderNode(label).fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo].current

    @Test fun extensionExamplesUseShortHeadingsAndFragments() {
        val section = docSections(sliderDoc).single { it.title == "Min steps between thumbs" }
        assertEquals("min-steps-between-thumbs", section.fragment)
        assertEquals("kotlin-extension-min-steps-between-thumbs", section.legacyFragment)
        assertTrue(componentPageMarkdown(sliderDoc).contains("\n## Min steps between thumbs\n"))
    }

    @Test fun pageListsOfficialRecipesInOrder() {
        assertEquals(
            listOf(
                "Default",
                "Range",
                "Multiple Thumbs",
                "Vertical",
                "Controlled",
                "Disabled",
                "RTL",
                "Kotlin extension: Step",
                "Kotlin extension: Inverted",
                "Kotlin extension: Commit",
                "Kotlin extension: Min steps between thumbs",
            ),
            sliderDoc.examples.map { it.title },
        )
        assertEquals(
            "An input where the user selects a value from within a given range.",
            sliderDoc.description,
        )
        assertNull(sliderDoc.examplesHeading)
        assertFalse(sliderDoc.examples.first().showHeading)
        assertEquals(
            "Use a `ClosedFloatingPointRange` value for a range slider.",
            sliderDoc.examples.single { it.title == "Range" }.descriptionMarkdown,
        )
        assertEquals(
            "Use a list of values for multiple thumbs.",
            sliderDoc.examples.single { it.title == "Multiple Thumbs" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `orientation = SliderOrientation.Vertical` for a vertical slider.",
            sliderDoc.examples.single { it.title == "Vertical" }.descriptionMarkdown,
        )
        assertEquals(
            "Use the `disabled` prop to disable the slider.",
            sliderDoc.examples.single { it.title == "Disabled" }.descriptionMarkdown,
        )
    }

    @Test fun pageKeepsOfficialUsageAndBaseUiApiReference() {
        assertTrue(sliderDoc.usageCode.contains("Slider("))
        assertTrue(sliderDoc.usageCode.contains("mutableStateOf(33f)"))
        assertEquals("import com.toutiaoyan.compose.ui.components.slider.Slider", sliderDoc.usageImportsCode)
        val api = sliderDoc.apiSections.single()
        assertEquals("Slider", api.title)
        assertTrue(api.descriptionMarkdown.contains("base-ui.com/react/components/slider"))
        assertTrue(api.properties.any { it.name == "value / values" })
        assertTrue(api.properties.any { it.name == "orientation" })
    }

    @Test fun copiedExamplesIncludeTheirStateLayoutAndImports() {
        val clips = sliderDoc.examples.associate { it.title to it.code }
        (clips.values + sliderDoc.usageCode).forEach { code ->
            assertTrue("import com.toutiaoyan.compose.ui.components.slider.Slider\n" in code)
            assertTrue("@Composable\ninternal fun Slider" in code)
            assertTrue("by remember" in code)
        }
        assertEquals(
            2,
            "orientation = SliderOrientation.Vertical".toRegex().findAll(clips.getValue("Vertical")).count(),
        )
        assertTrue("Label(\"Temperature\")" in clips.getValue("Controlled"))
        assertTrue("values.joinToString" in clips.getValue("Controlled"))
        assertTrue("disabled = true" in clips.getValue("Disabled"))
        assertTrue("LocalLayoutDirection provides direction" in clips.getValue("RTL"))
        assertTrue("language == \"ar\" || language == \"he\"" in clips.getValue("RTL"))
        assertFalse("RtlPreview(" in clips.getValue("RTL").substringAfter("internal fun SliderRtlPreview("))
    }

    @Test fun defaultStartsAtSeventyFiveAndAcceptsSemanticValueChanges() {
        show("Default")
        assertEquals(75f, currentValue("Volume"), 0.01f)
        sliderNode("Volume").performSemanticsAction(SemanticsActions.SetProgress) { it(100f) }
        rule.waitForIdle()
        assertEquals(100f, currentValue("Volume"), 0.01f)
    }

    @Test fun rangeAndMultipleThumbsExposeRangeSemantics() {
        show("Range")
        // A range slider reports its start thumb as the current value.
        assertEquals(25f, currentValue("Range"), 0.01f)
        show("Multiple Thumbs")
        assertEquals(10f, currentValue("Multiple thumbs"), 0.01f)
        rule.onNodeWithTag("slider-multiple").assertExists()
    }

    @Test fun keyboardEndHomeAndStepFollowTheRange() {
        show("Default")
        val node = sliderNode("Volume")
        node.performSemanticsAction(SemanticsActions.RequestFocus)
        node.performKeyInput { pressKey(androidx.compose.ui.input.key.Key.MoveEnd) }
        rule.waitForIdle()
        assertEquals(100f, currentValue("Volume"), 0.01f)
        node.performKeyInput { pressKey(androidx.compose.ui.input.key.Key.MoveHome) }
        rule.waitForIdle()
        assertEquals(0f, currentValue("Volume"), 0.01f)
        node.performKeyInput { pressKey(androidx.compose.ui.input.key.Key.DirectionRight) }
        rule.waitForIdle()
        assertEquals(1f, currentValue("Volume"), 0.01f)
    }

    @Test fun controlledShowsItsReadoutAndDisabledIsInert() {
        show("Controlled")
        rule.onNodeWithText("Temperature").assertIsDisplayed()
        rule.onNodeWithText("0.3, 0.7").assertIsDisplayed()
        show("Disabled")
        sliderNode("Disabled").assertIsNotEnabled()
    }

    @Test fun kotlinExtensionsAreLabelledAndRender() {
        show("Kotlin extension: Commit")
        rule.onNodeWithText("Committed: not yet").assertIsDisplayed()
        assertTrue(sliderDoc.examples.drop(7).all { it.title.startsWith("Kotlin extension: ") })
    }
}
