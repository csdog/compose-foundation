package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import com.toutiaoyan.compose.ui.theme.Theme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChartPresentationParityTest : ScreenshotTest() {
    private val config = mapOf("desktop" to ChartSeries("Desktop", Color.Blue))

    @Test fun legendUsesTwelvePixelTopGapAndNormalTwelveSixteenType() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartLegend(config, Modifier.width(320.dp).testTag("legend"))
        }
        val legend = composeRule.onNodeWithTag("legend").fetchSemanticsNode().boundsInRoot
        val text = composeRule.onNodeWithText("Desktop")
        assertEquals(12f, text.fetchSemanticsNode().boundsInRoot.top - legend.top, .1f)
        val layouts = mutableListOf<TextLayoutResult>()
        text.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(12.sp, layouts.single().layoutInput.style.fontSize)
        assertEquals(16.sp, layouts.single().layoutInput.style.lineHeight)
        assertEquals(FontWeight.Normal, layouts.single().layoutInput.style.fontWeight)
    }

    @Test fun hideIconKeepsTheFallbackColorSwatch() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartLegend(config, Modifier.width(320.dp).testTag("legend"), hideIcon = true)
        }
        val pixels = composeRule.onNodeWithTag("legend").captureToImage().toPixelMap()
        var blue = 0
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
            val c = pixels[x, y]
            if (c.blue > .9f && c.red < .1f && c.green < .1f) blue++
        }
        assertTrue(blue >= 40, "hideIcon hides a custom icon, not the fallback color swatch: $blue")
    }

    @Test fun emptyTooltipPayloadRendersNothing() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartTooltipContent("January", config, emptyMap())
        }
        composeRule.onNodeWithText("January").assertDoesNotExist()
    }

    @Test fun tooltipSeparatesNameFromFormattedNumber() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartTooltipContent("January", config, mapOf("desktop" to 1286f))
        }
        composeRule.onNodeWithText("January").assertIsDisplayed()
        composeRule.onNodeWithText("Desktop").assertIsDisplayed()
        composeRule.onNodeWithText("1,286").assertIsDisplayed()
        composeRule.onNodeWithText("Desktop: 1286.0").assertDoesNotExist()
    }

    @Test fun themeColorsFollowLiveThemeChanges() {
        var dark by mutableStateOf(false)
        val themed =
            mapOf(
                "desktop" to
                    ChartSeries(
                        "Desktop",
                        Color.Blue,
                        theme =
                            ChartThemeColors(
                                Color.Red,
                                Color.Green,
                            ),
                    ),
            )
        composeRule.setContent {
            Theme(
                preset =
                    StylePreset.Nova,
                isDark =
                dark,
            ) {
                ChartLegend(
                    themed,
                    Modifier.width(320.dp).testTag("legend"),
                )
            }
        }

        fun pixels(color: Color): Int {
            val image = composeRule.onNodeWithTag("legend").captureToImage().toPixelMap()
            var count = 0
            for (y in 0 until image.height) for (x in 0 until image.width) if (image[x, y] == color) count++
            return count
        }
        assertTrue(pixels(Color.Red) >= 40)
        composeRule.runOnIdle { dark = true }
        assertTrue(pixels(Color.Green) >= 40)
        assertEquals(0, pixels(Color.Red))
    }

    @Test fun hideIconReplacesTheCustomIconWithTheSeriesSwatch() {
        var hidden by mutableStateOf(false)
        val withIcon =
            mapOf(
                "desktop" to
                    ChartSeries("Desktop", Color.Blue, icon = {
                        Box(Modifier.size(12.dp).background(Color.Red))
                    }),
            )
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartLegend(withIcon, Modifier.width(320.dp).testTag("legend"), hideIcon = hidden)
        }

        fun redPixels(): Int {
            val image = composeRule.onNodeWithTag("legend").captureToImage().toPixelMap()
            var count = 0
            for (y in 0 until image.height) for (x in 0 until image.width) if (image[x, y] == Color.Red) count++
            return count
        }
        assertTrue(redPixels() >= 100)
        composeRule.runOnIdle { hidden = true }
        assertEquals(0, redPixels())
        composeRule.onNodeWithText("Desktop").assertIsDisplayed()
    }

    @Test fun tooltipSupportsLabelValueAndWholeRowFormatters() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartTooltipContent(
                "January",
                config,
                mapOf("desktop" to 12f),
                labelFormatter = { "Month: $it" },
                formatter =
                    {
                            key,
                            value,
                        ->
                        com.toutiaoyan.compose.ui.components.text.Text { "$key=${value.toInt()}" }
                    },
            )
        }
        composeRule.onNodeWithText("Month: January").assertIsDisplayed()
        composeRule.onNodeWithText("desktop=12").assertIsDisplayed()
        composeRule.onNodeWithText("Desktop").assertDoesNotExist()
    }

    @Test fun inactiveTooltipDoesNotRenderItsPayload() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartTooltipContent("January", config, mapOf("desktop" to 12f), active = false)
        }
        composeRule.onNodeWithText("January").assertDoesNotExist()
        composeRule.onNodeWithText("Desktop").assertDoesNotExist()
    }

    @Test fun valueFormatterCanOverrideNumericPresentation() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            ChartTooltipContent("January", config, mapOf("desktop" to 12f), valueFormatter = { "USD ${it.toInt()}" })
        }
        composeRule.onNodeWithText("USD 12").assertIsDisplayed()
    }
}
