package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/** Hit testing uses the actual plot width; logical data order stays independent of RTL layout. */
internal fun chartIndexAt(
    x: Float,
    width: Float,
    count: Int,
    reversed: Boolean,
    line: Boolean,
): Int? {
    if (count <= 0 || width <= 0 || !x.isFinite() || x < 0 || x >= width) return null
    val physical = if (line && count > 1) ((x / width) * (count - 1)).roundToInt() else (x / width * count).toInt()
    val bounded = physical.coerceIn(0, count - 1)
    return if (reversed) count - 1 - bounded else bounded
}

@Composable
internal fun CartesianChart(
    data: List<ChartPoint>,
    config: ChartConfig,
    modifier: Modifier,
    line: Boolean,
    showGrid: Boolean,
    showXAxis: Boolean,
    reversed: Boolean,
    barRadius: Dp,
    defaultIndex: Int?,
    accessibilityLayer: Boolean,
    xAxisLabelFormatter: (String) -> String,
    maxAxisLabels: Int,
    minTickGap: Dp,
    plotPadding: PaddingValues,
    tooltip: (@Composable (ChartPoint) -> Unit)?,
) {
    var activeIndex by remember { mutableStateOf(defaultIndex) }
    var focused by remember { mutableStateOf(false) }
    var hovered by remember { mutableStateOf(false) }
    val pointer = remember { mutableStateOf<Offset?>(null) }
    val selected = activeIndex?.takeIf { it in data.indices }
    val direction = LocalLayoutDirection.current
    val density = LocalDensity.current
    val leftPadding = plotPadding.calculateLeftPadding(direction)
    val rightPadding = plotPadding.calculateRightPadding(direction)
    val leftPx = with(density) { leftPadding.toPx() }
    val rightPx = with(density) { rightPadding.toPx() }
    val colors = config.mapValues { it.value.resolvedColor() }
    val gridColor = currentTheme.colors.border
    val cursorColor = currentTheme.colors.muted
    val description =
        selected?.let { i ->
            data[i].label + ": " +
                config.entries.joinToString(", ") { (key, series) ->
                    series.label + " " + formatChartNumber(data[i].values[key] ?: 0f)
                }
        } ?: ""
    Column(modifier) {
        Layout(
            modifier =
                Modifier.fillMaxWidth().weight(1f)
                    .semantics {
                        contentDescription = "Chart"
                        stateDescription = description
                    }
                    .onFocusChanged {
                        val lostFocus = focused && !it.isFocused
                        focused = it.isFocused
                        if (it.isFocused && selected == null && data.isNotEmpty()) activeIndex = 0
                        if (lostFocus && !hovered) activeIndex = null
                    }
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown || data.isEmpty()) {
                            false
                        } else {
                            val current = selected ?: 0
                            val next =
                                when (event.key) {
                                    Key.DirectionRight -> current + if (reversed) -1 else 1
                                    Key.DirectionLeft -> current + if (reversed) 1 else -1
                                    Key.MoveHome -> 0
                                    Key.MoveEnd -> data.lastIndex
                                    Key.Escape -> null
                                    else -> return@onPreviewKeyEvent false
                                }
                            pointer.value = null
                            activeIndex = next?.coerceIn(data.indices)
                            true
                        }
                    }
                    .focusable(accessibilityLayer)
                    .pointerInput(data.size, reversed, line, leftPx, rightPx) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                when (event.type) {
                                    PointerEventType.Exit -> {
                                        hovered = false
                                        pointer.value = null
                                        if (!focused) activeIndex = null
                                    }
                                    PointerEventType.Enter, PointerEventType.Move, PointerEventType.Press -> {
                                        val point = event.changes.firstOrNull()?.position ?: continue
                                        hovered = true
                                        pointer.value = point
                                        activeIndex =
                                            chartIndexAt(
                                                point.x - leftPx,
                                                size.width.toFloat() - leftPx - rightPx,
                                                data.size,
                                                reversed,
                                                line,
                                            )
                                    }
                                }
                            }
                        }
                    },
            content = {
                Canvas(Modifier.fillMaxSize().padding(plotPadding)) {
                    if (showGrid) drawChartGrid(gridColor)
                    if (data.isEmpty() || config.isEmpty()) return@Canvas
                    val keys = config.keys.toList()
                    val values = data.flatMap { point -> keys.mapNotNull { point.values[it]?.takeIf(Float::isFinite) } }
                    val domain = chartNiceDomain(values)
                    val span = domain.maximum - domain.minimum

                    fun y(value: Float) =
                        size.height - ((value.toDouble() - domain.minimum) / span * size.height).toFloat()

                    fun physical(index: Int) = if (reversed) data.lastIndex - index else index
                    val band = size.width / data.size
                    if (tooltip != null && selected != null) {
                        drawRect(cursorColor, Offset(physical(selected) * band, 0f), Size(band, size.height))
                    }
                    if (line) {
                        keys.forEach { key ->
                            val points =
                                data.mapIndexed { index, point ->
                                    val x =
                                        if (data.size == 1) {
                                            size.width / 2
                                        } else {
                                            physical(index) * size.width / (data.size - 1)
                                        }
                                    Offset(x, y(point.values[key]?.takeIf(Float::isFinite) ?: 0f))
                                }
                            points.zipWithNext().forEach { (start, end) ->
                                drawLine(colors.getValue(key), start, end, 2.dp.toPx(), StrokeCap.Round)
                            }
                            points.forEach { drawCircle(colors.getValue(key), 3.dp.toPx(), it) }
                        }
                    } else {
                        val gap = 4.dp.toPx()
                        val barWidth = ((band * .8f - gap * (keys.size - 1)) / keys.size).coerceAtLeast(0f)
                        data.forEachIndexed { index, point ->
                            keys.forEachIndexed { seriesIndex, key ->
                                val value = point.values[key]?.takeIf(Float::isFinite) ?: 0f
                                val top = min(y(value), y(0f))
                                val height = abs(y(value) - y(0f))
                                val left = physical(index) * band + band * .1f + seriesIndex * (barWidth + gap)
                                drawRoundRect(
                                    colors.getValue(key),
                                    Offset(left, top),
                                    Size(barWidth, height),
                                    androidx.compose.ui.geometry.CornerRadius(
                                        barRadius.toPx().coerceAtMost(
                                            min(
                                                barWidth,
                                                height,
                                            ) / 2,
                                        ),
                                    ),
                                )
                            }
                        }
                    }
                }
                if (selected != null && tooltip != null) Box { tooltip(data[selected]) }
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val canvas = measurables[0].measure(Constraints.fixed(width, height))
            val tip = measurables.getOrNull(1)?.measure(Constraints(maxWidth = width, maxHeight = height))
            layout(width, height) {
                canvas.place(0, 0)
                if (tip != null && selected != null) {
                    val physical = if (reversed) data.lastIndex - selected else selected
                    val target =
                        pointer.value
                            ?: Offset(
                                leftPx +
                                    (
                                        physical +
                                            .5f
                                    ) * (width - leftPx - rightPx) / data.size,
                                height / 2f,
                            )
                    val right = target.x.roundToInt() + 10
                    val left = if (right + tip.width <= width) right else target.x.roundToInt() - tip.width - 10
                    tip.place(
                        left.coerceIn(0, (width - tip.width).coerceAtLeast(0)),
                        (target.y - tip.height / 2).roundToInt().coerceIn(0, (height - tip.height).coerceAtLeast(0)),
                    )
                }
            }
        }
        if (showXAxis && data.isNotEmpty()) {
            // Canvas coordinates remain physical; do not reverse a second time via Row's RTL policy.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                val labels = (if (reversed) data.asReversed() else data).map { xAxisLabelFormatter(it.label) }
                ChartXAxis(labels, minTickGap, maxAxisLabels, leftPadding, rightPadding)
            }
        }
    }
}

private fun DrawScope.drawChartGrid(color: Color) {
    for (i in 0..4) {
        val y = size.height * i / 4
        drawLine(color.copy(alpha = color.alpha * .5f), Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
    }
}
