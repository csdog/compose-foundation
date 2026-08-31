@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.components.chart.BarChart
import com.toutiaoyan.compose.ui.components.chart.ChartContainer
import com.toutiaoyan.compose.ui.components.chart.ChartContainerDefaults
import com.toutiaoyan.compose.ui.components.chart.ChartIndicator
import com.toutiaoyan.compose.ui.components.chart.ChartPoint
import com.toutiaoyan.compose.ui.components.chart.ChartSeries
import com.toutiaoyan.compose.ui.components.chart.ChartLegend
import com.toutiaoyan.compose.ui.components.chart.ChartLegendContent
import com.toutiaoyan.compose.ui.components.chart.ChartLegendDefaults
import com.toutiaoyan.compose.ui.components.chart.ChartLegendVerticalAlign
import com.toutiaoyan.compose.ui.components.chart.ChartStyle
import com.toutiaoyan.compose.ui.components.chart.ChartTooltip
import com.toutiaoyan.compose.ui.components.chart.ChartTooltipContent
import com.toutiaoyan.compose.ui.components.chart.LineChart
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twSpacing

private val chartConfig =
    mapOf(
        "desktop" to ChartSeries("Desktop", Color(0xFF2563EB)),
        "mobile" to ChartSeries("Mobile", Color(0xFF60A5FA)),
    )

private val chartData =
    listOf(
        ChartPoint("Jan", mapOf("desktop" to 186f, "mobile" to 80f)),
        ChartPoint("Feb", mapOf("desktop" to 305f, "mobile" to 200f)),
        ChartPoint("Mar", mapOf("desktop" to 237f, "mobile" to 120f)),
        ChartPoint("Apr", mapOf("desktop" to 73f, "mobile" to 190f)),
        ChartPoint("May", mapOf("desktop" to 209f, "mobile" to 130f)),
    )

val chartDoc =
    ComponentDoc(
        id = "chart",
        title = "Chart",
        description =
            "A config-driven bar/line chart. Real shadcn/ui's chart.tsx wraps Recharts; " +
                "since there is no Compose Multiplatform equivalent, this draws directly via Canvas.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            val config = mapOf("desktop" to ChartSeries("Desktop", Color(0xFF2563EB)))
            val data = listOf(ChartPoint("Jan", mapOf("desktop" to 186f)))
            ChartContainer {
                BarChart(data = data, config = config)
            }
            ChartLegend(config = config)
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Bar chart",
                    code =
                        """
                        ChartContainer {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        ChartLegend(config = chartConfig, modifier = Modifier.padding(top = 8.dp))
                        """.trimIndent(),
                    preview = {
                        Column {
                            ChartContainer {
                                BarChart(data = chartData, config = chartConfig)
                            }
                            ChartLegend(
                                config = chartConfig,
                                modifier = Modifier.padding(top = twSpacing.sm),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Hide legend icon",
                    code =
                        """
                        ChartLegend(config = chartConfig, hideIcon = true)
                        """.trimIndent(),
                    preview = {
                        ChartLegend(config = chartConfig, hideIcon = true)
                    },
                ),
                ComponentExample(
                    title = "Default hide legend icon",
                    code =
                        """
                        ChartLegend(config = chartConfig, hideIcon = false)
                        """.trimIndent(),
                    preview = {
                        ChartLegend(config = chartConfig, hideIcon = false)
                    },
                ),
                ComponentExample(
                    title = "Tooltip line indicator",
                    code =
                        """
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Line,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Line,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Dashed indicator",
                    code =
                        """
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Dashed,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Dashed,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Hide tooltip label",
                    code =
                        """
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideLabel = true,
                        )
                        """.trimIndent(),
                    preview = {
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideLabel = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default hide tooltip label",
                    code =
                        """
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideLabel = false,
                        )
                        """.trimIndent(),
                    preview = {
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideLabel = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Hide tooltip indicator",
                    code =
                        """
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideIndicator = true,
                        )
                        """.trimIndent(),
                    preview = {
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideIndicator = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default hide tooltip indicator",
                    code =
                        """
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideIndicator = false,
                        )
                        """.trimIndent(),
                    preview = {
                        ChartTooltipContent(
                            label = "January",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                            hideIndicator = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Dot indicator",
                    code =
                        """
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Dot,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Dot,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default indicator",
                    code =
                        """
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Dot,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "January",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                                indicator = ChartIndicator.Dot,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        ChartContainer(ariaLabel = "Monthly visitors") {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer(ariaLabel = "Monthly visitors") {
                            BarChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aria label",
                    code =
                        """
                        ChartContainer(ariaLabel = null) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer(ariaLabel = null) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessibility layer",
                    code =
                        """
                        ChartContainer(accessibilityLayer = true) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer(accessibilityLayer = true) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default accessibility layer",
                    code =
                        """
                        ChartContainer(accessibilityLayer = false) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer(accessibilityLayer = false) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
                ComponentExample(
                    title = "Legend top",
                    code =
                        """
                        ChartLegend(config = chartConfig, verticalAlign = ChartLegendVerticalAlign.Top)
                        """.trimIndent(),
                    preview = {
                        ChartLegend(config = chartConfig, verticalAlign = ChartLegendVerticalAlign.Top)
                    },
                ),
                ComponentExample(
                    title = "Legend bottom",
                    code =
                        """
                        ChartLegend(config = chartConfig, verticalAlign = ChartLegendDefaults.VerticalAlign)
                        """.trimIndent(),
                    preview = {
                        ChartLegend(config = chartConfig, verticalAlign = ChartLegendDefaults.VerticalAlign)
                    },
                ),
                ComponentExample(
                    title = "Legend",
                    code =
                        """
                        ChartLegend(config = chartConfig)
                        """.trimIndent(),
                    preview = {
                        ChartLegend(config = chartConfig)
                    },
                ),
                ComponentExample(
                    title = "Default vertical align",
                    code =
                        """
                        ChartLegend(config = chartConfig)
                        """.trimIndent(),
                    preview = {
                        ChartLegend(config = chartConfig)
                    },
                ),
                ComponentExample(
                    title = "Tooltip",
                    code =
                        """
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "Jan",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        ChartTooltip {
                            ChartTooltipContent(
                                label = "Jan",
                                config = chartConfig,
                                values = mapOf("desktop" to 186f, "mobile" to 80f),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Official size",
                    code =
                        """
                        ChartContainer(initialDimension = ChartContainerDefaults.InitialDimension) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer(initialDimension = ChartContainerDefaults.InitialDimension) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
                ComponentExample(
                    title = "Container",
                    code =
                        """
                        ChartContainer {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer {
                            BarChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aspect ratio",
                    code =
                        """
                        ChartContainer(aspectRatio = 16f / 9f) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer(aspectRatio = 16f / 9f) {
                            BarChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
                ComponentExample(
                    title = "Legend content",
                    code =
                        """
                        ChartLegendContent(config = chartConfig)
                        """.trimIndent(),
                    preview = {
                        ChartLegendContent(config = chartConfig)
                    },
                ),
                ComponentExample(
                    title = "Tooltip content",
                    code =
                        """
                        ChartTooltipContent(
                            label = "Jan",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                        )
                        """.trimIndent(),
                    preview = {
                        ChartTooltipContent(
                            label = "Jan",
                            config = chartConfig,
                            values = mapOf("desktop" to 186f, "mobile" to 80f),
                        )
                    },
                ),
                ComponentExample(
                    title = "Style",
                    code =
                        """
                        ChartStyle(config = chartConfig) {
                            BarChart(data = chartData)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartStyle(config = chartConfig) {
                            BarChart(data = chartData)
                        }
                    },
                ),
                ComponentExample(
                    title = "Line chart",
                    code =
                        """
                        ChartContainer {
                            LineChart(data = chartData, config = chartConfig)
                        }
                        """.trimIndent(),
                    preview = {
                        ChartContainer {
                            LineChart(data = chartData, config = chartConfig)
                        }
                    },
                ),
            ),
    )
