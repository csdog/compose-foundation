package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.toAwtImage
import java.io.File
import javax.imageio.ImageIO
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ChartAxisParityTest : ScreenshotTest() {
    @Test fun dateLabelsUseTheirFullWidthAndPreserveTheLastDay() {
        val config = mapOf("desktop" to ChartSeries("Desktop", Color.Blue))
        val data = (1..30).map { ChartPoint("Apr $it", mapOf("desktop" to it.toFloat())) }
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartContainer(Modifier.width(320.dp).height(200.dp), config = config) {
                BarChart(data, minTickGap = 32.dp)
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("Apr 30").assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        val actual = layouts.single()
        val node = composeRule.onNodeWithText("Apr 30").fetchSemanticsNode().boundsInRoot
        // SimpleText's reconstructed semantic Paragraph can report its full incoming width
        // as overflow even when the actual tight node and painted line both fit.
        assertTrue(node.width + .01f >= actual.getLineRight(0), "glyph line must fit the actual node")
        assertTrue(node.height + .01f >= actual.getLineBottom(0), "full line height must fit")
        assertEquals("Apr 30".length, actual.getLineEnd(0, visibleEnd = true))
        assertFalse(actual.isLineEllipsized(0))
    }

    @Test fun arabicTicksReserveTheirActualShapedWidth() {
        assertRtlTickSpacing(listOf("يون", "ماي", "أبر", "مار", "فبر", "ينا"))
    }

    @Test fun hebrewTicksReserveTheirActualShapedWidth() {
        assertRtlTickSpacing(listOf("יונ", "מאי", "אפר", "מרץ", "פבר", "ינו"))
    }

    @Test fun arabicAxisResizeMatchesAFreshNarrowLayout() {
        assertRtlResize(listOf("يون", "ماي", "أبر", "مار", "فبر", "ينا"), "arabic")
    }

    @Test fun hebrewAxisResizeMatchesAFreshNarrowLayout() {
        assertRtlResize(listOf("יונ", "מאי", "אפר", "מרץ", "פבר", "ינו"), "hebrew")
    }

    private fun assertRtlResize(
        labels: List<String>,
        name: String,
    ) {
        var width by mutableStateOf(592.dp)
        var generation by mutableStateOf(0)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(Modifier.width(620.dp).height(30.dp).testTag("axis-resize-scene")) {
                    key(generation) {
                        Box(Modifier.width(width)) {
                            ChartXAxis(labels, 5.dp, Int.MAX_VALUE, 5.dp, 5.dp)
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle { width = 294.dp }
        val resized = composeRule.onNodeWithTag("axis-resize-scene").captureToImage().toAwtImage()
        composeRule.runOnIdle { generation++ }
        val fresh = composeRule.onNodeWithTag("axis-resize-scene").captureToImage().toAwtImage()
        val dir = File("build/reports/chart-axis-resize").apply { mkdirs() }
        ImageIO.write(resized, "png", File(dir, "$name-resized.png"))
        ImageIO.write(fresh, "png", File(dir, "$name-fresh.png"))
        var different = 0
        for (y in 0 until fresh.height) for (x in 0 until fresh.width) {
            if (resized.getRGB(x, y) != fresh.getRGB(x, y)) different++
        }
        assertEquals(0, different, "Resized axis must paint the same glyph positions as a fresh layout")
    }

    private fun assertRtlTickSpacing(labels: List<String>) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                androidx.compose.foundation.layout.Box(Modifier.width(294.dp)) {
                    ChartXAxis(labels, 5.dp, Int.MAX_VALUE, 5.dp, 5.dp)
                }
            }
        }
        composeRule.onNodeWithText(labels.last()).assertIsDisplayed()
        val visible =
            labels.mapNotNull { label ->
                composeRule.onAllNodesWithText(
                    label,
                ).fetchSemanticsNodes().singleOrNull()?.let { label to it.boundsInRoot }
            }.sortedBy { it.second.left }
        assertTrue(visible.size >= 2)
        visible.zipWithNext().forEach { (a, b) ->
            assertTrue(b.second.left - a.second.right >= 4.5f, "RTL tick gap: $visible")
        }
        visible.forEach { (label, bounds) ->
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithText(
                label,
            ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
            val text = results.single()
            assertEquals(1, text.lineCount)
            assertFalse(text.isLineEllipsized(0))
            assertEquals(label.length, text.getLineEnd(0, visibleEnd = true))
            assertTrue(
                bounds.width >= text.getLineRight(0) - text.getLineLeft(0),
                "RTL glyphs must fit: $label $bounds",
            )
        }
    }

    @Test fun selectedLabelsKeepTheRequiredGap() {
        val centers = (0..29).map { 12 + (it + .5f) * 566 / 30 }
        val widths = (1..30).map { if (it < 10) 30 else 38 }
        val ticks = chartAxisTicks(590f, centers, widths, 32f, Int.MAX_VALUE)
        assertEquals(29, ticks.last().index)
        assertTrue(ticks.size in 4..9)
        ticks.zipWithNext().forEach { (a, b) ->
            assertTrue(b.center - widths[b.index] / 2f - a.center - widths[a.index] / 2f >= 32)
        }
    }

    @Test fun narrowAndEmptyAxesNeverPlaceAnOversizedLabel() {
        assertTrue(chartAxisTicks(20f, listOf(10f), listOf(30), 5f, 10).isEmpty())
        assertTrue(chartAxisTicks(0f, emptyList(), emptyList(), 5f, 10).isEmpty())
    }

    @Test fun explicitMaximumCapsLabelsWithoutDroppingTheEndTick() {
        val ticks = chartAxisTicks(600f, listOf(50f, 150f, 250f, 350f, 450f, 550f), List(6) { 20 }, 5f, 2)
        assertEquals(2, ticks.size)
        assertEquals(5, ticks.last().index)
    }
}
