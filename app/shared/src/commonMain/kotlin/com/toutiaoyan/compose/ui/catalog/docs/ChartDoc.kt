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
        ChartPoint("Jun", mapOf("desktop" to 214f, "mobile" to 140f)),
    )

val chartDoc =
    ComponentDoc(
        id = "chart",
        title = "Chart",
        description = "Beautiful charts. Built using Recharts. Copy and paste into your apps.",
        usageCode =
            """
            import androidx.compose.ui.graphics.Color
            import com.toutiaoyan.compose.ui.components.chart.*

            val config = mapOf("desktop" to ChartSeries("Desktop", Color(0xFF2563EB)))
            val data = listOf(ChartPoint("Jan", mapOf("desktop" to 186f)))
            ChartContainer {
                BarChart(data = data, config = config)
            }
            ChartLegend(config = config)
            """.trimIndent(),
        examplesHeading = null,
        introductionSections =
            listOf(
                ComponentGuideSection(
                    "Component",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Updated: `chart` follows the Recharts v3 API shape. If you are upgrading " +
                                "existing chart code, see Updating to Recharts v3 below. This Compose port keeps " +
                                "the same config-driven API but draws the chart with Canvas instead of bundling " +
                                "Recharts.",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "Introducing Charts. A collection of chart components that you can copy and paste " +
                                "into your apps.",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "Charts are designed to look great out of the box. They work well with the other " +
                                "components and are fully customizable to fit your project. " +
                                "[Browse the Charts Library](https://ui.shadcn.com/charts).",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "The web implementation uses [Recharts](https://recharts.org/) under the hood. The " +
                                "Compose port mirrors that composition contract: you build a chart from " +
                                "`ChartContainer`, `BarChart`/`LineChart`, `ChartTooltip` and `ChartLegend`, and " +
                                "only bring in the pieces you need.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            val config = mapOf(
                                "desktop" to ChartSeries("Desktop", Color(0xFF2563EB)),
                                "mobile" to ChartSeries("Mobile", Color(0xFF60A5FA)),
                            )
                            val data = listOf(
                                ChartPoint("Jan", mapOf("desktop" to 186f, "mobile" to 80f)),
                            )

                            ChartContainer(config = config, aspectRatio = 16f / 9f) {
                                BarChart(
                                    data = data,
                                    tooltip = {
                                        ChartTooltipContent(
                                            label = it.label,
                                            config = config,
                                            values = it.values,
                                        )
                                    },
                                )
                            }
                            """.trimIndent(),
                        ),
                        ComponentGuideBlock.Paragraph(
                            "The port does not wrap a charting library either: the chart primitives are plain " +
                                "Compose, the config is yours, and there is no abstraction lock-in.",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Updating to Recharts v3",
                    listOf(
                        ComponentGuideBlock.Paragraph("If you are updating older chart code to the v3 shape:"),
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "Reference the chart color tokens directly through `ChartSeries.color` (or " +
                                    "`ChartThemeColors`) instead of wrapping them in a CSS `hsl()` expression.",
                                "Use `defaultIndex` for the initial tooltip state only; keep persistent active " +
                                    "shapes in your own chart state.",
                                "Set the orientation once on the chart root; do not repeat it per bar.",
                                "Give `ChartContainer` a height or aspect ratio so it can measure on first " +
                                    "render; `ChartContainerDefaults.InitialDimension` is only a fallback.",
                            ),
                        ),
                    ),
                ),
            ),
        beforeExampleSections =
            mapOf(
                "Tooltip" to
                    listOf(
                        ComponentGuideSection(
                            "Chart Config",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "The chart config is where you define the labels, icons and colors for a chart.",
                                ),
                                ComponentGuideBlock.Paragraph(
                                    "It is intentionally decoupled from chart data. This allows you to share config " +
                                        "and color tokens between charts. It can also work independently for " +
                                        "cases where " +
                                        "your data or color tokens live remotely or in a different format.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    val chartConfig = mapOf(
                                        "desktop" to ChartSeries(
                                            label = "Desktop",
                                            icon = { Icon(Lucide.Monitor, Modifier.size(12.dp)) },
                                            // A color like Color(0xFF2563EB) or a shared theme token
                                            color = Color(0xFF2563EB),
                                            // OR a light/dark pair used by the chart renderer
                                            theme = ChartThemeColors(
                                                light = Color(0xFF2563EB),
                                                dark = Color(0xFFDC2626),
                                            ),
                                        ),
                                    )
                                    """.trimIndent(),
                                ),
                            ),
                        ),
                        ComponentGuideSection(
                            "Theming",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Charts have built-in support for theming. You can use shared color tokens " +
                                        "(recommended) or color values in any color format, such as hex, hsl or oklch.",
                                ),
                            ),
                        ),
                        ComponentGuideSection(
                            "CSS Variables",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Define your colors once in the theme (the web page lists `--chart-1` through " +
                                        "`--chart-5` for light and dark), then reference the same values from " +
                                        "`ChartSeries`.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    val chartConfig = mapOf(
                                        "desktop" to ChartSeries(label = "Desktop", color = theme.colors.chart1),
                                        "mobile" to ChartSeries(label = "Mobile", color = theme.colors.chart2),
                                    )
                                    """.trimIndent(),
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "hex, hsl or oklch",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "You can also define your colors directly in the chart config. Use the color " +
                                        "format you prefer.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    val chartConfig = mapOf(
                                        "desktop" to ChartSeries("Desktop", Color(0xFF2563EB)),
                                        "mobile" to ChartSeries("Mobile", Oklch(0.5f, 0.2f, 240f).toColor()),
                                        "tablet" to ChartSeries("Tablet", Color(0xFF60A5FA)),
                                    )
                                    """.trimIndent(),
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "Using Colors",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "To use the theme colors in your chart, reference the series from the config. " +
                                        "Bars, lines, the tooltip indicator and the legend all resolve the " +
                                        "same series color.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    Column {
                                        ChartContainer(config = chartConfig) {
                                            BarChart(data = data, config = chartConfig)
                                        }
                                        ChartLegend(config = chartConfig)
                                    }
                                    """.trimIndent(),
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "Components",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Resolve a component's series color through the chart config.",
                                ),
                                ComponentGuideBlock.Code("BarChart(data = data, config = chartConfig)"),
                            ),
                            level = 4,
                        ),
                        ComponentGuideSection(
                            "Chart Data",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Kotlin API difference: ChartPoint stores numeric values rather than a CSS " +
                                        "fill field. " +
                                        "Use matching config keys to assign each browser's color.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    val browserConfig = mapOf(
                                        "chrome" to ChartSeries("Chrome", currentTheme.colors.chart1),
                                        "safari" to ChartSeries("Safari", currentTheme.colors.chart2),
                                    )
                                    val browserData = listOf(
                                        ChartPoint("Chrome", mapOf("chrome" to 275f)),
                                        ChartPoint("Safari", mapOf("safari" to 200f)),
                                    )
                                    BarChart(data = browserData, config = browserConfig)
                                    """.trimIndent(),
                                ),
                            ),
                            level = 4,
                        ),
                        ComponentGuideSection(
                            "Tailwind",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Kotlin API difference: Compose has no CSS fill class or LabelList. " +
                                        "For custom labels, resolve the same config color explicitly.",
                                ),
                                ComponentGuideBlock.Code(
                                    "Text(\"Desktop\", color = chartConfig.getValue(\"desktop\").color)",
                                ),
                            ),
                            level = 4,
                        ),
                    ),
                "RTL" to
                    listOf(
                        ComponentGuideSection(
                            "Tooltip Props",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "A chart tooltip contains a label, name, indicator and value. You can turn " +
                                        "any of these on or off and customize the indicator style.",
                                ),
                                ComponentGuideBlock.Table(
                                    headers = listOf("Prop", "Kotlin type", "Description"),
                                    rows =
                                        listOf(
                                            listOf(
                                                "`label` / `labelFormatter`",
                                                "`String` / `(String) -> String`",
                                                "The tooltip label and its display formatter.",
                                            ),
                                            listOf(
                                                "`values` / `valueFormatter` / `formatter`",
                                                "`Map<String, Float>` / `(Float) -> String` / `(@Composable " +
                                                    "(String, Float) -> Unit)?`",
                                                "Value rows; names come from chart config labels. The nullable " +
                                                    "formatter renders custom composable content.",
                                            ),
                                            listOf(
                                                "`indicator`",
                                                "`ChartIndicator`",
                                                "The indicator style: Dot, Line or Dashed.",
                                            ),
                                            listOf("`hideLabel`", "`Boolean`", "Whether to hide the label."),
                                            listOf("`hideIndicator`", "`Boolean`", "Whether to hide the indicator."),
                                        ),
                                ),
                                ComponentGuideBlock.Paragraph(
                                    "Kotlin API difference: the web `labelKey` and `nameKey` payload lookups " +
                                        "are represented by an explicit `label`, the `values` map and chart config " +
                                        "labels. labelFormatter and valueFormatter return text; formatter " +
                                        "renders custom composable content.",
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "Colors",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Colors are automatically referenced from the chart config.",
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "Custom",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Kotlin API difference: supply the visitors label explicitly and use " +
                                        "browser keys " +
                                        "in values and config instead of web labelKey/nameKey lookups. " +
                                        "This displays Total Visitors with Chrome and Safari names.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    val browserConfig = mapOf(
                                        "chrome" to ChartSeries("Chrome", currentTheme.colors.chart1),
                                        "safari" to ChartSeries("Safari", currentTheme.colors.chart2),
                                    )
                                    ChartTooltipContent(
                                        label = "Total Visitors",
                                        config = browserConfig,
                                        values = mapOf("chrome" to 187f, "safari" to 200f),
                                    )
                                    """.trimIndent(),
                                ),
                                ComponentGuideBlock.Paragraph(
                                    "Colors are automatically referenced from the chart config. " +
                                        "`labelFormatter` formats the tooltip heading; `valueFormatter` " +
                                        "formats numeric values. " +
                                        "Row names come from `ChartSeries.label`. Use the composable " +
                                        "`formatter` for custom rows.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    BarChart(
                                        data = data,
                                        config = chartConfig,
                                        tooltip = {
                                            ChartTooltipContent(
                                                label = it.label,
                                                config = chartConfig,
                                                values = it.values,
                                                indicator = ChartIndicator.Line,
                                                hideLabel = false,
                                            )
                                        },
                                    )
                                    """.trimIndent(),
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "Legend",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "You can use `ChartLegend` / `ChartLegendContent` to add a legend to your chart.",
                                ),
                            ),
                        ),
                        ComponentGuideSection(
                            "Colors",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Colors are automatically referenced from the chart config.",
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "Custom",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "Kotlin API difference: config labels replace the web nameKey=browser lookup. " +
                                        "Using the browserConfig above displays Chrome and Safari for the " +
                                        "legend names.",
                                ),
                                ComponentGuideBlock.Code("ChartLegend(config = browserConfig)"),
                                ComponentGuideBlock.Paragraph(
                                    "Colors are automatically referenced from the chart config. Use `hideIcon` to " +
                                        "render the swatch-only legend. In a `Column`, composition order " +
                                        "places the legend " +
                                        "above or below the chart; `verticalAlign` controls spacing toward the chart.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    Column {
                                        ChartContainer {
                                            BarChart(data = data, config = chartConfig)
                                        }
                                        ChartLegend(
                                            config = chartConfig,
                                            verticalAlign = ChartLegendVerticalAlign.Bottom,
                                        )
                                    }
                                    """.trimIndent(),
                                ),
                            ),
                            level = 3,
                        ),
                        ComponentGuideSection(
                            "Accessibility",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    "You can turn on the `accessibilityLayer` prop to add an accessible layer " +
                                        "to your chart. This prop adds keyboard access and screen reader support " +
                                        "to your charts.",
                                ),
                                ComponentGuideBlock.Paragraph(
                                    "In this port the chart layer is keyboard accessible by default: the plot is " +
                                        "focusable, arrow keys move the active datum, Home/End jump to the ends, " +
                                        "Escape " +
                                        "dismisses the tooltip, and `ariaLabel` names the chart region.",
                                ),
                                ComponentGuideBlock.Code(
                                    """
                                    ChartContainer(ariaLabel = "Monthly desktop and mobile users") {
                                        LineChart(data = data, config = chartConfig, accessibilityLayer = true)
                                    }
                                    """.trimIndent(),
                                ),
                            ),
                        ),
                    ),
            ),
        examples =
            chartOfficialExamples +
                listOf(
                    ComponentExample(
                        title = "Kotlin extension: Bar chart",
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
                        title = "Kotlin extension: Hide legend icon",
                        code =
                            """
                            ChartLegend(config = chartConfig, hideIcon = true)
                            """.trimIndent(),
                        preview = {
                            ChartLegend(config = chartConfig, hideIcon = true)
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default hide legend icon",
                        code =
                            """
                            ChartLegend(config = chartConfig, hideIcon = false)
                            """.trimIndent(),
                        preview = {
                            ChartLegend(config = chartConfig, hideIcon = false)
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Tooltip line indicator",
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
                        title = "Kotlin extension: Dashed indicator",
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
                        title = "Kotlin extension: Hide tooltip label",
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
                        title = "Kotlin extension: Default hide tooltip label",
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
                        title = "Kotlin extension: Hide tooltip indicator",
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
                        title = "Kotlin extension: Default hide tooltip indicator",
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
                        title = "Kotlin extension: Dot indicator",
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
                        title = "Kotlin extension: Default indicator",
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
                        title = "Kotlin extension: Accessible label",
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
                        title = "Kotlin extension: Default aria label",
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
                        title = "Kotlin extension: Accessibility layer",
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
                        title = "Kotlin extension: Default accessibility layer",
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
                        title = "Kotlin extension: Legend top",
                        code =
                            """
                            ChartLegend(config = chartConfig, verticalAlign = ChartLegendVerticalAlign.Top)
                            """.trimIndent(),
                        preview = {
                            ChartLegend(config = chartConfig, verticalAlign = ChartLegendVerticalAlign.Top)
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Legend bottom",
                        code =
                            """
                            ChartLegend(config = chartConfig, verticalAlign = ChartLegendDefaults.VerticalAlign)
                            """.trimIndent(),
                        preview = {
                            ChartLegend(config = chartConfig, verticalAlign = ChartLegendDefaults.VerticalAlign)
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Legend",
                        code =
                            """
                            ChartLegend(config = chartConfig)
                            """.trimIndent(),
                        preview = {
                            ChartLegend(config = chartConfig)
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default vertical align",
                        code =
                            """
                            ChartLegend(config = chartConfig)
                            """.trimIndent(),
                        preview = {
                            ChartLegend(config = chartConfig)
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Tooltip",
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
                        title = "Kotlin extension: Official size",
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
                        title = "Kotlin extension: Container",
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
                        title = "Kotlin extension: Default aspect ratio",
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
                        title = "Kotlin extension: Legend content",
                        code =
                            """
                            ChartLegendContent(config = chartConfig)
                            """.trimIndent(),
                        preview = {
                            ChartLegendContent(config = chartConfig)
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Tooltip content",
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
                        title = "Kotlin extension: Style",
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
                        title = "Kotlin extension: Line chart",
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
