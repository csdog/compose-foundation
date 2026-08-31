@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * One labeled, colored data series in a chart. Mirrors real shadcn/ui's `chart.tsx`
 * `ChartConfig` (a `Record<dataKey, { label, color }>`) -- since there is no Compose
 * Multiplatform equivalent of Recharts (the library `chart.tsx` itself wraps), the
 * actual bars/lines here are drawn directly via [Canvas] rather than delegated to a
 * charting library, but the config-driven series-to-color/label mapping is the same
 * shape as the real API.
 */
data class ChartSeries(val label: String, val color: Color)

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
 * Pass [initialDimension] ([ChartContainerDefaults.InitialDimension]) for Recharts'
 * 320×200 first-paint size instead of 16:9.
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
    val frame =
        if (initialDimension != null) {
            Modifier.size(initialDimension.width, initialDimension.height)
        } else {
            Modifier.fillMaxWidth().aspectRatio(aspectRatio)
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
            ChartLegendVerticalAlign.Top -> Modifier.padding(bottom = twSpacing.md)
            ChartLegendVerticalAlign.Bottom -> Modifier.padding(top = twSpacing.md)
        }
    Row(
        modifier = modifier.fillMaxWidth().then(alignPadding).styleable(styleState, style),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.md)) {
            resolved.values.forEach { series ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!hideIcon) {
                        Box(
                            modifier =
                                Modifier.styleable(
                                    remember { MutableStyleState(MutableInteractionSource()) },
                                    rememberChartLegendSwatchStyle(series.color),
                                ),
                        )
                    }
                    Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { series.label }
                }
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

/** A grouped vertical bar chart -- one bar per series, per x-axis category. */
@Composable
fun BarChart(
    data: List<ChartPoint>,
    config: ChartConfig? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val resolved = resolveChartConfig(config)
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val seriesKeys = resolved.keys.toList()
    val maxValue = data.flatMap { it.values.values }.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    val gridColor = currentTheme.colors.border

    Column(modifier = modifier.styleable(styleState, style)) {
        Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
            drawGridlines(gridColor)
            if (data.isEmpty() || seriesKeys.isEmpty()) return@Canvas

            val groupWidth = size.width / data.size
            val barGap = groupWidth * 0.15f
            val barWidth = (groupWidth - barGap * 2) / seriesKeys.size

            data.forEachIndexed { groupIndex, point ->
                val groupStart = groupIndex * groupWidth + barGap
                seriesKeys.forEachIndexed { seriesIndex, key ->
                    val value = point.values[key] ?: 0f
                    val barHeight = (value / maxValue) * size.height
                    val left = groupStart + seriesIndex * barWidth
                    drawRect(
                        color = resolved.getValue(key).color,
                        topLeft = Offset(left, size.height - barHeight),
                        size = Size(barWidth * 0.85f, barHeight),
                    )
                }
            }
        }
        ChartXAxisLabels(data.map { it.label })
    }
}

/** A multi-series line chart, one polyline per series key. */
@Composable
fun LineChart(
    data: List<ChartPoint>,
    config: ChartConfig? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val resolved = resolveChartConfig(config)
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val seriesKeys = resolved.keys.toList()
    val maxValue = data.flatMap { it.values.values }.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    val gridColor = currentTheme.colors.border

    Column(modifier = modifier.styleable(styleState, style)) {
        Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
            drawGridlines(gridColor)
            if (data.size < 2 || seriesKeys.isEmpty()) return@Canvas

            val stepX = size.width / (data.size - 1)
            seriesKeys.forEach { key ->
                val points =
                    data.mapIndexed { index, point ->
                        val value = point.values[key] ?: 0f
                        Offset(index * stepX, size.height - (value / maxValue) * size.height)
                    }
                for (i in 0 until points.lastIndex) {
                    drawLine(
                        color = resolved.getValue(key).color,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
                points.forEach { drawCircle(color = resolved.getValue(key).color, radius = 3.dp.toPx(), center = it) }
            }
        }
        ChartXAxisLabels(data.map { it.label })
    }
}

/**
 * Hover overlay host for a chart. Matches shadcn's `ChartTooltip`.
 * Not wired into [BarChart] / [LineChart] by default.
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
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs2),
    ) {
        if (!hideLabel) {
            Text(variant = TextVariant.LabelSmall) { label }
        }
        values.forEach { (key, value) ->
            val series = config[key] ?: return@forEach
            Row(
                horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!hideIndicator) {
                    Box(
                        modifier =
                            Modifier.styleable(
                                remember { MutableStyleState(MutableInteractionSource()) },
                                rememberChartIndicatorStyle(series.color, indicator),
                            ),
                    )
                }
                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) {
                    "${series.label}: $value"
                }
            }
        }
    }
}

private fun DrawScope.drawGridlines(color: Color) {
    val lineCount = 4
    for (i in 0..lineCount) {
        val y = size.height * i / lineCount
        drawLine(
            color = color.copy(alpha = 0.5f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Butt,
        )
    }
}

@Composable
private fun ChartXAxisLabels(labels: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = twSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        labels.forEach { label ->
            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { label }
        }
    }
}
