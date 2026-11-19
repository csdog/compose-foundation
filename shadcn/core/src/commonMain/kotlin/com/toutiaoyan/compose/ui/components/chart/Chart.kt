@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text

/**
 * One labeled, colored data series in a chart. Mirrors real shadcn/ui's `chart.tsx`
 * `ChartConfig` (a `Record<dataKey, { label, color }>`) -- this Kotlin implementation uses Canvas instead of the web Recharts renderer; the
 * actual bars/lines here are drawn directly via [Canvas] rather than delegated to a
 * charting library, but the config-driven series-to-color/label mapping is the same
 * shape as the real API.
 */
data class ChartSeries(
    val label: String,
    val color: Color,
    val icon: (@Composable () -> Unit)? = null,
    val theme: ChartThemeColors? = null,
)

/** Light/dark series colors; theme colors take precedence over the fallback color. */
data class ChartThemeColors(val light: Color, val dark: Color)

@Composable
internal fun ChartSeries.resolvedColor(): Color = theme?.let { if (Theme.isDark) it.dark else it.light } ?: color

/** dataKey -> series config, matching real shadcn's `ChartConfig`. */
typealias ChartConfig = Map<String, ChartSeries>

/** One x-axis category with one value per series key present in the owning [ChartConfig]. */
data class ChartPoint(val label: String, val values: Map<String, Float>)

private val LocalChartConfig = staticCompositionLocalOf<ChartConfig?> { null }

@Composable
private fun resolveChartConfig(config: ChartConfig?): ChartConfig =
    config
        ?: checkNotNull(LocalChartConfig.current) {
            "Pass config= or wrap in ChartContainer(config) / ChartStyle"
        }

/** Reads the nearest [ChartContainer] / [ChartStyle] config. Matches shadcn's `useChart()`. */
@Composable
fun chartConfig(): ChartConfig = resolveChartConfig(null)

/**
 * Publishes [config] for [chartConfig] / child charts. Matches shadcn's `ChartStyle`
 * (web injects CSS color variables; here colors already live on [ChartSeries]).
 * Optional — [ChartContainer] already publishes [config] when given one.
 */
@Composable
fun ChartStyle(
    config: ChartConfig,
    content: @Composable () -> Unit = {},
) {
    CompositionLocalProvider(LocalChartConfig provides config, content = content)
}

object ChartContainerDefaults {
    /** Official Recharts `ResponsiveContainer` `initialDimension` `{ width: 320, height: 200 }`. */
    val InitialDimension = DpSize(320.dp, 200.dp)
}

/**
 * Fixed aspect-ratio frame for chart content, matching real shadcn's `ChartContainer`
 * (`aspect-video` by default). [accessibilityLayer] adds a generic "Chart" label when
 * [ariaLabel] is omitted (default false).
 * [initialDimension] is an unmeasured-width fallback, not a permanent size override.
 * A measured parent width/height always takes precedence, matching ResponsiveContainer.
 */
@Composable
fun ChartContainer(
    modifier: Modifier = Modifier,
    style: Style = Style,
    aspectRatio: Float = 16f / 9f,
    initialDimension: DpSize? = null,
    config: ChartConfig = emptyMap(),
    ariaLabel: String? = null,
    accessibilityLayer: Boolean = false,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val resolvedLabel = ariaLabel ?: if (accessibilityLayer) "Chart" else null
    require(aspectRatio.isFinite() && aspectRatio > 0f) { "aspectRatio must be positive and finite" }
    val frame =
        Modifier.layout { measurable, constraints ->
            val width =
                if (constraints.hasBoundedWidth) {
                    constraints.maxWidth
                } else {
                    (
                        initialDimension?.width?.roundToPx()
                            ?: constraints.minWidth
                    ).coerceIn(
                        constraints.minWidth,
                        constraints.maxWidth,
                    )
                }
            val naturalHeight =
                if (!constraints.hasBoundedWidth &&
                    initialDimension != null
                ) {
                    initialDimension.height.roundToPx()
                } else {
                    (width / aspectRatio).toInt()
                }
            val height = naturalHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
            val fitted =
                Constraints.fitPrioritizingWidth(
                    minWidth = width,
                    maxWidth = width,
                    minHeight = height,
                    maxHeight = height,
                )
            val child = measurable.measure(Constraints.fixed(fitted.minWidth, fitted.minHeight))
            layout(child.width, child.height) { child.place(0, 0) }
        }
    ChartStyle(config = config) {
        Box(
            modifier =
                modifier
                    .then(frame)
                    .then(
                        if (resolvedLabel != null) {
                            Modifier.semantics { contentDescription = resolvedLabel }
                        } else {
                            Modifier
                        },
                    )
                    .styleable(styleState, style),
        ) {
            content()
        }
    }
}

/** shadcn ChartLegend `verticalAlign`: `top` adds `pb-3`, `bottom` adds `pt-3`. */
enum class ChartLegendVerticalAlign { Top, Bottom }

object ChartLegendDefaults {
    /** Official Recharts Legend `verticalAlign="bottom"` (`pt-3`). */
    val VerticalAlign = ChartLegendVerticalAlign.Bottom
}

/**
 * A color-swatch + label row for every series in [config], matching `ChartLegendContent`.
 * [verticalAlign] is shadcn `verticalAlign` (default [ChartLegendDefaults.VerticalAlign]).
 */
@Composable
fun ChartLegend(
    config: ChartConfig? = null,
    modifier: Modifier = Modifier,
    hideIcon: Boolean = false,
    verticalAlign: ChartLegendVerticalAlign = ChartLegendDefaults.VerticalAlign,
    style: Style = Style,
) {
    val resolved = resolveChartConfig(config)
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val alignPadding =
        when (verticalAlign) {
            ChartLegendVerticalAlign.Top -> Modifier.padding(bottom = 12.dp)
            ChartLegendVerticalAlign.Bottom -> Modifier.padding(top = 12.dp)
        }
    Row(
        modifier = modifier.fillMaxWidth().then(alignPadding).styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        resolved.values.forEach { series ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (series.icon != null && !hideIcon) {
                    Box(Modifier.size(12.dp)) {
                        CompositionLocalProvider(
                            LocalContentColor provides
                                currentTheme.colors.mutedForeground,
                        ) { series.icon.invoke() }
                    }
                } else {
                    Box(
                        Modifier.styleable(
                            remember { MutableStyleState(MutableInteractionSource()) },
                            rememberChartLegendSwatchStyle(series.resolvedColor()),
                        ),
                    )
                }
                Text(textStyle = chartTypography()) { series.label }
            }
        }
    }
}

/** Alias for [ChartLegend] matching shadcn's `ChartLegendContent`. */
@Composable
fun ChartLegendContent(
    config: ChartConfig? = null,
    modifier: Modifier = Modifier,
    hideIcon: Boolean = false,
    verticalAlign: ChartLegendVerticalAlign = ChartLegendDefaults.VerticalAlign,
    style: Style = Style,
) {
    ChartLegend(
        config = config,
        modifier = modifier,
        hideIcon = hideIcon,
        verticalAlign = verticalAlign,
        style = style,
    )
}

/** Grouped bars with pointer selection and keyboard tooltip navigation. */
@Composable
fun BarChart(
    data: List<ChartPoint>,
    config: ChartConfig? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    showGrid: Boolean = true,
    showXAxis: Boolean = true,
    reversed: Boolean = false,
    barRadius: androidx.compose.ui.unit.Dp = 4.dp,
    defaultIndex: Int? = null,
    accessibilityLayer: Boolean = true,
    xAxisLabelFormatter: (String) -> String = { it },
    maxAxisLabels: Int = Int.MAX_VALUE,
    minTickGap: androidx.compose.ui.unit.Dp = 5.dp,
    plotPadding: PaddingValues = PaddingValues(5.dp),
    tooltip: (@Composable (ChartPoint) -> Unit)? = null,
) {
    val state = remember { MutableStyleState(MutableInteractionSource()) }
    CartesianChart(
        data, resolveChartConfig(config), modifier.styleable(state, style), false,
        showGrid, showXAxis, reversed, barRadius, defaultIndex, accessibilityLayer,
        xAxisLabelFormatter, maxAxisLabels, minTickGap, plotPadding, tooltip,
    )
}

/** Multi-series lines share the same data, keyboard and tooltip contract as [BarChart]. */
@Composable
fun LineChart(
    data: List<ChartPoint>,
    config: ChartConfig? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    showGrid: Boolean = true,
    showXAxis: Boolean = true,
    reversed: Boolean = false,
    defaultIndex: Int? = null,
    accessibilityLayer: Boolean = true,
    xAxisLabelFormatter: (String) -> String = { it },
    maxAxisLabels: Int = Int.MAX_VALUE,
    minTickGap: androidx.compose.ui.unit.Dp = 5.dp,
    plotPadding: PaddingValues = PaddingValues(5.dp),
    tooltip: (@Composable (ChartPoint) -> Unit)? = null,
) {
    val state = remember { MutableStyleState(MutableInteractionSource()) }
    CartesianChart(
        data, resolveChartConfig(config), modifier.styleable(state, style), true,
        showGrid, showXAxis, reversed, 0.dp, defaultIndex, accessibilityLayer,
        xAxisLabelFormatter, maxAxisLabels, minTickGap, plotPadding, tooltip,
    )
}

/**
 * Hover overlay host for a chart. Matches shadcn's `ChartTooltip`.
 * Pass a tooltip content slot to [BarChart] / [LineChart] to display it at the active datum.
 */
@Composable
fun ChartTooltip(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) { content() }
}

/**
 * Series swatch + value list for a [ChartTooltip]. Matches shadcn's `ChartTooltipContent`.
 * [indicator] is Dot (default, current 8dp circle), Line, or Dashed.
 */
@Composable
fun ChartTooltipContent(
    label: String,
    config: ChartConfig,
    values: Map<String, Float>,
    modifier: Modifier = Modifier,
    hideLabel: Boolean = false,
    hideIndicator: Boolean = false,
    indicator: ChartIndicator = ChartIndicator.Dot,
    style: Style = Style,
    active: Boolean = true,
    labelFormatter: (String) -> String = { it },
    valueFormatter: (Float) -> String = ::formatChartNumber,
    formatter: (@Composable (key: String, value: Float) -> Unit)? = null,
) {
    if (!active || values.isEmpty()) return
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nestedLabel = values.size == 1 && indicator != ChartIndicator.Dot
    val showLabel = !hideLabel && label.isNotEmpty()
    Column(
        modifier =
            modifier.widthIn(min = 128.dp).width(IntrinsicSize.Max)
                .cssStyleable(styleState, rememberChartTooltipStyle(), style),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (showLabel && !nestedLabel) {
            Text(
                textStyle =
                    chartTypography(
                        weight =
                            FontWeight.Medium,
                    ),
            ) {
                labelFormatter(
                    config[label]?.label
                        ?: label,
                )
            }
        }
        values.forEach { (key, value) ->
            val series = config[key]
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (formatter != null) {
                    formatter(key, value)
                } else {
                    if (series?.icon != null) {
                        Box(Modifier.size(10.dp)) {
                            CompositionLocalProvider(
                                LocalContentColor provides
                                    currentTheme.colors.mutedForeground,
                            ) { series.icon.invoke() }
                        }
                    } else if (!hideIndicator) {
                        ChartTooltipIndicator(series?.resolvedColor() ?: currentTheme.colors.foreground, indicator)
                    }
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = if (nestedLabel) Alignment.Bottom else Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (showLabel && nestedLabel) {
                                Text(
                                    textStyle =
                                        chartTypography(
                                            weight =
                                                FontWeight.Medium,
                                        ),
                                ) {
                                    labelFormatter(
                                        config[label]?.label
                                            ?: label,
                                    )
                                }
                            }
                            Text(textStyle = chartTypography(color = currentTheme.colors.mutedForeground)) {
                                series?.label ?: key
                            }
                        }
                        Text(
                            maxLines = 1,
                            textStyle =
                                chartTypography(
                                    weight = FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                ),
                        ) { valueFormatter(value) }
                    }
                }
            }
        }
    }
}

/** English-style grouping for the default recipe; callers can supply locale-specific formatting. */
fun formatChartNumber(value: Float): String {
    val raw = value.toString().removeSuffix(".0")
    if (!value.isFinite() || 'E' in raw || 'e' in raw) return raw
    val sign = if (raw.startsWith("-")) "-" else ""
    val unsigned = raw.removePrefix("-")
    val integer = unsigned.substringBefore('.')
    val fraction = if ('.' in unsigned) "." + unsigned.substringAfter('.') else ""
    return sign + integer.reversed().chunked(3).joinToString(",").reversed() + fraction
}

@Composable
internal fun chartTypography(
    color: Color = currentTheme.colors.foreground,
    weight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily? = null,
) = TextStyle(
    color = color,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontWeight = weight,
    fontFamily = fontFamily ?: currentTheme.fontFamily,
    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
)
