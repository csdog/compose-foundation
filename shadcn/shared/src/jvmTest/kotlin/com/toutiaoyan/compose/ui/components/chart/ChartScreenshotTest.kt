@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class ChartScreenshotTest : ScreenshotTest() {
    private val config =
        mapOf(
            "desktop" to ChartSeries("Desktop", Color(0xFF2563EB)),
            "mobile" to ChartSeries("Mobile", Color(0xFF60A5FA)),
        )
    private val data =
        listOf(
            ChartPoint("Jan", mapOf("desktop" to 186f, "mobile" to 80f)),
            ChartPoint("Feb", mapOf("desktop" to 305f, "mobile" to 200f)),
            ChartPoint("Mar", mapOf("desktop" to 237f, "mobile" to 120f)),
        )

    private fun barStates(darkTheme: Boolean) {
        snapshot("chart_bar_states", darkTheme = darkTheme) {
            Column {
                ChartContainer { BarChart(data = data, config = config) }
                ChartLegend(config = config)
            }
        }
    }

    private fun lineStates(darkTheme: Boolean) {
        snapshot("chart_line_states", darkTheme = darkTheme) {
            Column {
                ChartContainer { LineChart(data = data, config = config) }
                ChartLegend(config = config)
            }
        }
    }

    @Test
    fun bar_light() = barStates(darkTheme = false)

    @Test
    fun bar_dark() = barStates(darkTheme = true)

    @Test
    fun line_light() = lineStates(darkTheme = false)

    @Test
    fun line_dark() = lineStates(darkTheme = true)

    private fun legendBottom(darkTheme: Boolean) {
        snapshot("chart_legend_bottom", darkTheme = darkTheme) {
            Column {
                ChartContainer { BarChart(data = data, config = config) }
                ChartLegend(
                    config = config,
                    verticalAlign = ChartLegendDefaults.VerticalAlign,
                )
            }
        }
    }

    @Test
    fun legend_bottom_light() = legendBottom(darkTheme = false)

    @Test
    fun legend_bottom_dark() = legendBottom(darkTheme = true)

    private fun officialSize(darkTheme: Boolean) {
        snapshot("chart_initial_dimension", darkTheme = darkTheme) {
            ChartContainer(initialDimension = ChartContainerDefaults.InitialDimension) {
                BarChart(data = data, config = config)
            }
        }
    }

    @Test
    fun initial_dimension_light() = officialSize(darkTheme = false)

    @Test
    fun initial_dimension_dark() = officialSize(darkTheme = true)
}
