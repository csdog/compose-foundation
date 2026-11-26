package com.toutiaoyan.compose.ui.components.inputgroup

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

internal val InputGroupMinHeight = 32.dp
internal val InputGroupBorderWidth = 1.dp

internal data class InputGroupAddonParentData(val align: InputGroupAddonAlign)

internal fun MeasureScope.measureInputGroup(
    measurables: List<Measurable>,
    constraints: Constraints,
    nova: Boolean,
): MeasureResult {
    val lanes = measurables.groupBy { (it.parentData as? InputGroupAddonParentData)?.align }
    val maxWidth = constraints.maxWidth
    val blockConstraints = if (constraints.hasBoundedWidth) Constraints.fixedWidth(maxWidth) else Constraints()
    val blockStart = lanes[InputGroupAddonAlign.BlockStart].orEmpty().map { it.measure(blockConstraints) }
    val blockEnd = lanes[InputGroupAddonAlign.BlockEnd].orEmpty().map { it.measure(blockConstraints) }
    val row = measureInputGroupRow(lanes, maxWidth)
    val compactHeight = InputGroupMinHeight.roundToPx()
    val canUseCompactRow = nova && blockStart.isEmpty() && blockEnd.isEmpty()
    val rowHeight =
        if (canUseCompactRow && row.controls.isNotEmpty() && row.height <= compactHeight) {
            (InputGroupMinHeight - InputGroupBorderWidth * 2).roundToPx()
        } else {
            row.height
        }
    val desiredWidth = maxOf(blockStart.widest(), blockEnd.widest(), row.width)
    val width = if (constraints.hasBoundedWidth) maxWidth else maxOf(constraints.minWidth, desiredWidth)
    val height = blockStart.sumOf { it.height } + rowHeight + blockEnd.sumOf { it.height }

    return layout(width, height.coerceAtLeast(constraints.minHeight)) {
        val rowY = placeBlockAddons(blockStart, 0)
        placeInputGroupRow(row, width, rowY, rowHeight)
        placeBlockAddons(blockEnd, rowY + rowHeight)
    }
}

private fun measureInputGroupRow(
    lanes: Map<InputGroupAddonAlign?, List<Measurable>>,
    maxWidth: Int,
): InputGroupRow {
    val leading = lanes[InputGroupAddonAlign.InlineStart].orEmpty().map { it.measure(Constraints()) }
    val trailing = lanes[InputGroupAddonAlign.InlineEnd].orEmpty().map { it.measure(Constraints()) }
    val addonWidth = leading.sumOf { it.width } + trailing.sumOf { it.width }
    val controlWidth = if (maxWidth == Constraints.Infinity) maxWidth else (maxWidth - addonWidth).coerceAtLeast(0)
    val controls = lanes[null].orEmpty()
    val controlConstraints =
        if (controls.size == 1 && controlWidth != Constraints.Infinity) {
            Constraints.fixedWidth(controlWidth)
        } else {
            Constraints(maxWidth = controlWidth)
        }
    return InputGroupRow(leading, controls.map { it.measure(controlConstraints) }, trailing)
}

private class InputGroupRow(
    val leading: List<Placeable>,
    val controls: List<Placeable>,
    val trailing: List<Placeable>,
) {
    val width: Int = leading.sumOf { it.width } + controls.sumOf { it.width } + trailing.sumOf { it.width }
    val height: Int = maxOf(leading.tallest(), controls.tallest(), trailing.tallest())
}

private fun List<Placeable>.widest(): Int = maxOfOrNull { it.width } ?: 0

private fun List<Placeable>.tallest(): Int = maxOfOrNull { it.height } ?: 0

private fun Placeable.PlacementScope.placeBlockAddons(
    addons: List<Placeable>,
    startY: Int,
): Int {
    var y = startY
    addons.forEach { addon ->
        addon.placeRelative(0, y)
        y += addon.height
    }
    return y
}

private fun Placeable.PlacementScope.placeInputGroupRow(
    row: InputGroupRow,
    width: Int,
    y: Int,
    height: Int,
) {
    fun Placeable.rowY(): Int = y + (height - this.height) / 2

    var start = 0
    row.leading.forEach { addon ->
        addon.placeRelative(start, addon.rowY())
        start += addon.width
    }
    row.controls.forEach { control ->
        control.placeRelative(start, control.rowY())
        start += control.width
    }
    var end = width
    row.trailing.asReversed().forEach { addon ->
        end -= addon.width
        addon.placeRelative(end, addon.rowY())
    }
}
