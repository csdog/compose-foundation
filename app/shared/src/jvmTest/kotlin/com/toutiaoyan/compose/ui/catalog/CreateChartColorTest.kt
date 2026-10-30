package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.Oklch
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.toColor
import com.toutiaoyan.compose.ui.catalog.create.CreateContributionHistoryCard
import com.toutiaoyan.compose.ui.catalog.create.CreateAnalyticsCard
import com.toutiaoyan.compose.ui.catalog.create.CreateDividendIncomeCard
import com.toutiaoyan.compose.ui.catalog.create.LocalPreviewChartColors
import com.toutiaoyan.compose.ui.catalog.create.resolveChartColors
import com.toutiaoyan.compose.ui.catalog.create.chartColorSwatch
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateChartColorTest {
    @get:Rule val rule = createComposeRule()

    @Test fun allChartChoicesMatchIndependentOfficialRoles() {
        val rows =
            checkNotNull(javaClass.getResourceAsStream("/themes/create-chart-colors.tsv"))
                .bufferedReader().use { it.readLines().drop(2) }
        assertEquals(264, rows.size)
        for (line in rows) {
            val (name, mode, index, css) = line.split('\t')
            val (l, c, h) = Regex("""oklch\(([\d.]+) ([\d.]+) ([\d.]+)\)""").matchEntire(css)!!.destructured
            val expected = Oklch(l.toFloat(), c.toFloat(), h.toFloat()).toColor()
            assertEquals(
                expected,
                if (mode == "swatch") {
                    chartColorSwatch(
                        name,
                    )
                } else {
                    resolveChartColors(name, mode == "dark")[index.toInt()]
                },
                "$name/$mode/$index",
            )
        }
    }

    @Test fun chartSelectionIsIndependentAndSurvivesResponsiveBranchAndModeChanges() {
        var width by mutableStateOf(1000.dp)
        var dark by mutableStateOf(false)
        var base by mutableStateOf(BaseColor.Neutral)
        var accent by mutableStateOf(Accent.Blue)
        rule.setContent {
            Theme(preset = StylePreset.Nova, baseColor = base, accent = accent, isDark = dark, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(width, 760.dp)) {
                        CreatePage(StylePreset.Nova, {}, base, { base = it }, accent, { accent = it })
                    }
                }
            }
        }
        rule.onNodeWithText("Get Started").performClick()
        assertBar(TwColors.neutral500)
        rule.onNodeWithText("Chart Color").performClick()
        rule.onNodeWithText("Amber").performClick()
        assertBar(TwColors.amber500)
        rule.runOnIdle { accent = Accent.Lime }
        assertBar(TwColors.amber500)
        rule.runOnIdle { width = 390.dp }
        assertBar(TwColors.amber500)
        rule.runOnIdle { dark = true }
        assertBar(TwColors.amber500)
        rule.runOnIdle { width = 1000.dp }
        rule.onNodeWithText("Base Color").performClick()
        rule.onNode(hasText("Mauve") and !hasText("Base Color")).performClick()
        assertBar(TwColors.amber500)
        rule.onNodeWithText("Chart Color").performClick()
        rule.onAllNodesWithText("Neutral").assertCountEquals(0)
        rule.onNode(hasText("Mauve") and !hasText("Base Color")).performClick()
        assertBar(TwColors.mauve500)
        rule.onNodeWithText("Base Color").performClick()
        rule.onNodeWithText("Olive").performClick()
        assertBar(TwColors.olive500)
        rule.onNode(hasText("Menu") and !hasText("Default / Solid")).performClick()
        rule.onNode(hasText("Reset") and hasText("⇧R")).performClick()
        assertBar(TwColors.neutral500)
        rule.runOnIdle {
            assertEquals(BaseColor.Neutral, base)
            assertEquals(Accent.Base, accent)
        }
    }

    @Test fun contributionChartUsesSixOfficialValuesAndProportionalGeometry() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalPreviewChartColors provides resolveChartColors("Blue", false)) {
                    CreateContributionHistoryCard(Modifier.width(320.dp))
                }
            }
        }
        val values = listOf("Dec" to 800f, "Jan" to 1100f, "Feb" to 900f, "Mar" to 1300f, "Apr" to 750f, "May" to 1400f)
        val bounds =
            values.map { (label, amount) ->
                rule.onNodeWithText(label).assertIsDisplayed()
                rule.onNodeWithContentDescription("$label: $amount").fetchSemanticsNode().boundsInRoot
            }
        val maximum = bounds.last().height
        assertEquals(162f, maximum, 1f, "200 chart - 8 top margin - 30 XAxis")
        values.forEachIndexed { i, (_, amount) ->
            assertEquals(amount / 1400f, bounds[i].height / maximum, .012f)
            assertEquals(bounds.last().bottom, bounds[i].bottom, 1f)
            assertTrue(bounds[i].width <= 40.1f)
        }
        assertBar(TwColors.blue500)
    }

    @Test fun analyticsAndDividendMiniChartsAlsoUseTheirSelectedChartRoles() {
        var analytics by mutableStateOf(true)
        var selection by mutableStateOf("Blue")
        rule.setContent {
            Theme(preset = StylePreset.Nova, accent = Accent.Lime, minTouchTarget = null) {
                CompositionLocalProvider(LocalPreviewChartColors provides resolveChartColors(selection, false)) {
                    if (analytics) {
                        CreateAnalyticsCard(Modifier.width(320.dp).testTag("chart card"))
                    } else {
                        CreateDividendIncomeCard(Modifier.width(320.dp).testTag("chart card"))
                    }
                }
            }
        }
        for (isAnalytics in listOf(true, false)) for (name in listOf("Blue", "Amber")) {
            rule.runOnIdle {
                analytics = isAnalytics
                selection = name
            }
            val expected =
                if (isAnalytics) {
                    if (name == "Blue") TwColors.blue300 else TwColors.amber300
                } else {
                    if (name == "Blue") TwColors.blue500 else TwColors.amber500
                }
            val pixels = rule.onNodeWithTag("chart card").captureToImage().toPixelMap()
            var matching = 0
            for (x in 0 until pixels.width) for (y in 0 until pixels.height) {
                val actual = pixels[x, y]
                if (abs(expected.red - actual.red) < .012f &&
                    abs(expected.green - actual.green) < .012f &&
                    abs(expected.blue - actual.blue) < .012f
                ) {
                    matching++
                }
            }
            assertTrue(
                matching > 8,
                "Selected $name chart color must reach the actual " +
                    "${if (isAnalytics) "analytics" else "mini bar"} drawing",
            )
        }
    }

    private fun assertBar(expected: Color) {
        val pixels = rule.onNodeWithContentDescription("Dec: 800.0").captureToImage().toPixelMap()
        val actual = pixels[pixels.width / 2, pixels.height / 2]
        assertTrue(
            abs(expected.red - actual.red) < .012f &&
                abs(expected.green - actual.green) < .012f &&
                abs(expected.blue - actual.blue) < .012f,
            "Expected chart fill $expected, actual $actual",
        )
    }
}
