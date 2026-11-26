package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.math.roundToInt

internal data class ChartAxisTick(val index: Int, val center: Float)

/** Keep the end tick and walk backwards, reserving actual label width plus minTickGap. */
internal fun chartAxisTicks(
    width: Float,
    centers: List<Float>,
    labelWidths: List<Int>,
    gap: Float,
    maximum: Int,
): List<ChartAxisTick> {
    if (width <= 0 || centers.isEmpty() || maximum <= 0) return emptyList()
    val result = mutableListOf<ChartAxisTick>()
    var right = width
    for (index in centers.indices.reversed()) {
        val half = labelWidths[index] / 2f
        if (half * 2 > width) continue
        val center = if (index == centers.lastIndex) centers[index].coerceIn(half, width - half) else centers[index]
        if (center - half >= 0 && center + half <= right && result.size < maximum) {
            result += ChartAxisTick(index, center)
            right = center - half - gap.coerceAtLeast(0f)
        }
    }
    return result.asReversed()
}

@Composable
internal fun ChartXAxis(
    labels: List<String>,
    minTickGap: Dp,
    maximum: Int,
    leftPadding: Dp,
    rightPadding: Dp,
) {
    val style = chartTypography(color = currentTheme.colors.mutedForeground)
    Layout(
        modifier = Modifier.fillMaxWidth().height(30.dp),
        content = {
            labels.forEachIndexed { index, label ->
                key(index, label) {
                    Text(textStyle = style, maxLines = 1) { label }
                }
            }
        },
    ) { measurables, incoming ->
        // Use the very same shaped text for collision checks and drawing. Separate
        // remembered text measurements can become stale when a fallback font resolves.
        val children =
            measurables.map {
                it.measure(Constraints(maxHeight = incoming.maxHeight))
            }
        val width = incoming.maxWidth.toFloat()
        val left = leftPadding.toPx()
        val right = rightPadding.toPx()
        val band = (width - left - right).coerceAtLeast(0f) / labels.size
        val centers = labels.indices.map { left + (it + .5f) * band }
        val ticks = chartAxisTicks(width, centers, children.map { it.width }, minTickGap.toPx(), maximum)
        layout(incoming.maxWidth, incoming.maxHeight) {
            ticks.forEach { tick ->
                val child = children[tick.index]
                child.place((tick.center - child.width / 2f).roundToInt(), 10.dp.roundToPx())
            }
        }
    }
}
