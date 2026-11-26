package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class ChartNumericLayoutParityTest : ScreenshotTest() {
    private fun assertNumberFits(text: String) {
        val layouts = mutableListOf<TextLayoutResult>()
        val node = composeRule.onNodeWithText(text)
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val actual = layouts.single()
        val bounds = node.fetchSemanticsNode().boundsInRoot
        assertEquals(1, actual.lineCount, "numeric values must not split across lines")
        assertTrue(actual.getLineRight(0) <= bounds.width + .01f, "all numeric glyphs must fit")
        assertEquals(text.length, actual.getLineEnd(0, visibleEnd = true))
        assertFalse(actual.isLineEllipsized(0))
    }

    @Test fun fixedWidthBrowserValueKeepsItsLastDigit() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartTooltipContent(
                "Browser",
                mapOf(
                    "chrome" to
                        ChartSeries(
                            "Chrome",
                            Color.Blue,
                        ),
                ),
                mapOf("chrome" to 1286f),
                Modifier.width(128.dp),
                hideLabel = true,
            )
        }
        assertNumberFits("1,286")
    }

    @Test fun nestedLineLabelDoesNotWrapTheSixDigitValue() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartTooltipContent(
                "Page Views",
                mapOf(
                    "desktop" to
                        ChartSeries(
                            "Desktop",
                            Color.Blue,
                        ),
                ),
                mapOf("desktop" to 12486f),
                Modifier.width(144.dp),
                indicator = ChartIndicator.Line,
            )
        }
        assertNumberFits("12,486")
    }

    @Test fun arabicNameDoesNotTruncateTheNumericPayload() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                ChartTooltipContent(
                    "فبراير",
                    mapOf(
                        "desktop" to
                            ChartSeries(
                                "سطح المكتب",
                                Color.Blue,
                            ),
                    ),
                    mapOf("desktop" to 305f),
                    Modifier.width(128.dp),
                )
            }
        }
        assertNumberFits("305")
    }
}
