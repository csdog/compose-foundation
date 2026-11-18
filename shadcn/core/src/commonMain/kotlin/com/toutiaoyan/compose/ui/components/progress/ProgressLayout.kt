package com.toutiaoyan.compose.ui.components.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.constrainHeight

internal const val PROGRESS_VALUE_LAYOUT_ID = "progress-value"

/** Flex-wrap with gap-3, a logical auto margin on Value, and an always-full-width final track. */
@Composable
internal fun ProgressRootLayout(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
        val loose = Constraints(maxWidth = width, maxHeight = constraints.maxHeight)
        val gap = 12.dp.roundToPx()
        val rows = mutableListOf<MutableList<Pair<Placeable, Boolean>>>()
        var used = 0
        measurables.dropLast(1).forEach { measurable ->
            val placeable = measurable.measure(loose)
            if (rows.isEmpty() || (rows.last().isNotEmpty() && used + gap + placeable.width > width)) {
                rows.add(mutableListOf())
                used = 0
            }
            if (rows.last().isNotEmpty()) used += gap
            rows.last().add(placeable to (measurable.layoutId == PROGRESS_VALUE_LAYOUT_ID))
            used += placeable.width
        }
        val track = measurables.last().measure(loose.copy(minWidth = width))
        val heights = rows.map { row -> row.maxOf { it.first.height } }
        val naturalHeight = heights.sum() + gap * rows.size + track.height
        layout(width, constraints.constrainHeight(naturalHeight)) {
            var y = 0
            rows.forEachIndexed { index, row ->
                val remaining = (width - row.sumOf { it.first.width } - gap * (row.size - 1)).coerceAtLeast(0)
                var x = 0
                var marginApplied = false
                row.forEachIndexed { itemIndex, (placeable, autoMargin) ->
                    if (itemIndex > 0) x += gap
                    if (autoMargin && !marginApplied) {
                        x += remaining
                        marginApplied = true
                    }
                    placeable.placeRelative(x, y)
                    x += placeable.width
                }
                y += heights[index] + gap
            }
            track.placeRelative(0, y)
        }
    }
}
