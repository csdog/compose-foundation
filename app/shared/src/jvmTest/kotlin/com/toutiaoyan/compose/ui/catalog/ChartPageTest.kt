package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.catalog.docs.chartDoc
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChartPageTest {
    @get:Rule val rule = createComposeRule()

    private fun markdown() = componentPageMarkdown(chartDoc)

    @Test fun pageUsesOfficialDescriptionAndRecipeOrder() {
        assertEquals(
            "Beautiful charts. Built using Recharts. Copy and paste into your apps.",
            chartDoc.description,
        )
        assertEquals(
            listOf(
                "Default",
                "Your First Chart",
                "Add a Grid",
                "Add an Axis",
                "Add Tooltip",
                "Add Legend",
                "Tooltip",
                "RTL",
            ),
            chartDoc.examples.take(8).map { it.title },
        )
        assertTrue(chartDoc.examples.drop(8).all { it.title.startsWith("Kotlin extension: ") })
    }

    @Test fun pageAddsOfficialGuideSectionsInOrder() {
        assertEquals(
            listOf("Component", "Updating to Recharts v3"),
            chartDoc.introductionSections.map { it.title },
        )
        assertEquals(
            listOf(3, 3, 3),
            chartDoc.beforeExampleSections.getValue("Tooltip").takeLast(3).map { it.level },
        )
        assertEquals(
            setOf("Tooltip", "RTL"),
            chartDoc.beforeExampleSections.keys,
        )
        assertEquals(
            listOf("Chart Config", "Theming", "CSS Variables", "hex, hsl or oklch", "Using Colors"),
            chartDoc.beforeExampleSections.getValue("Tooltip").map { it.title },
        )
        assertEquals(
            listOf("Tooltip Props", "Legend", "Accessibility"),
            chartDoc.beforeExampleSections.getValue("RTL").map { it.title },
        )
        assertTrue(chartDoc.beforeExamples.isEmpty())
        assertTrue(chartDoc.afterExamples.isEmpty())
        val guide = markdown()
        val order =
            listOf(
                "## Component",
                "## Updating to Recharts v3",
                "## Installation",
                "## Your First Chart",
                "### Add a Grid",
                "### Add an Axis",
                "### Add Tooltip",
                "### Add Legend",
                "## Chart Config",
                "## Theming",
                "### CSS Variables",
                "### hex, hsl or oklch",
                "### Using Colors",
                "## Tooltip",
                "## Tooltip Props",
                "## Legend",
                "## Accessibility",
                "## RTL",
            )
        var last = -1
        order.forEach { heading ->
            val idx = guide.indexOf(heading)
            assertTrue(idx > last, "$heading missing or out of order")
            last = idx
        }
    }

    @Test fun componentSectionCarriesIntroAndCalloutCopy() {
        val component = chartDoc.introductionSections.first()
        val paragraphs =
            component.blocks.filterIsInstance<ComponentGuideBlock.Paragraph>()
                .map { it.markdown }
        assertTrue(paragraphs.any { it.contains("Updated: `chart` follows the Recharts v3 API shape") })
        assertTrue(paragraphs.any { it.contains("Introducing Charts.") })
        assertTrue(paragraphs.any { it.contains("Browse the Charts Library") })
        assertTrue(paragraphs.any { it.contains("`ChartContainer`") })
        assertTrue(
            component.blocks.any {
                it is ComponentGuideBlock.Code &&
                    it.code.contains("BarChart(")
            },
        )
    }

    @Test fun chartConfigAndThemingKeepOfficialCopy() {
        val config = chartDoc.beforeExampleSections.getValue("Tooltip").single { it.title == "Chart Config" }
        assertTrue(
            config.blocks.filterIsInstance<ComponentGuideBlock.Paragraph>()
                .any { it.markdown.contains("intentionally decoupled from chart data") },
        )
        val code =
            config.blocks.filterIsInstance<ComponentGuideBlock.Code>()
                .single()
        assertTrue(code.code.contains("ChartThemeColors"))
        val theming = chartDoc.beforeExampleSections.getValue("Tooltip").single { it.title == "Theming" }
        assertTrue(
            theming.blocks.filterIsInstance<ComponentGuideBlock.Paragraph>()
                .any { it.markdown.contains("built-in support for theming") },
        )
    }

    @Test fun tooltipPropsAndAccessibilitySectionsCarryOfficialText() {
        val tooltip = chartDoc.beforeExampleSections.getValue("RTL").single { it.title == "Tooltip Props" }
        assertEquals(
            "A chart tooltip contains a label, name, indicator and value. You can turn any of these on or " +
                "off and customize the indicator style.",
            (
                tooltip.blocks.first() as
                    ComponentGuideBlock.Paragraph
            ).markdown,
        )
        val props =
            tooltip.blocks.filterIsInstance<ComponentGuideBlock.Bullets>()
                .single()
                .items
        assertEquals(5, props.size)
        listOf("label", "values", "indicator", "hideLabel", "hideIndicator").forEach { prop ->
            assertTrue(props.any { it.contains("`$prop") }, prop)
        }
        val accessibility = chartDoc.beforeExampleSections.getValue("RTL").single { it.title == "Accessibility" }
        assertTrue(
            accessibility.blocks.filterIsInstance<ComponentGuideBlock.Paragraph>()
                .any { it.markdown.contains("accessibilityLayer") },
        )
    }
}
