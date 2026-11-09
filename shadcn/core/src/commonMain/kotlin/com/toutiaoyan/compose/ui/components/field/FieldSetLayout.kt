@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.roundToInt

/**
 * Direct-child kinds inside a [FieldSet], mirroring real shadcn's
 * `data-slot` selectors: `.cn-field-legend`, `.cn-field-description`,
 * `.cn-field-group` and the `data-[slot=checkbox-group]` marker.
 */
internal enum class FieldSetChildKind {
    Legend,
    LegendLabel,
    Description,
    Group,
    Other,
}

internal data class FieldSetAlignment(
    val horizontal: Alignment.Horizontal? = null,
    val line: ((Measured) -> Int)? = null,
)

internal data class FieldSetChildData(
    val kind: FieldSetChildKind = FieldSetChildKind.Other,
    val dense: Boolean = false,
    val weight: Float? = null,
    val fill: Boolean = true,
    val align: FieldSetAlignment? = null,
    val previous: Any? = null,
)

private class FieldSetChildModifier(
    private val kind: FieldSetChildKind? = null,
    private val dense: Boolean? = null,
    private val weight: Float? = null,
    private val fill: Boolean? = null,
    private val align: FieldSetAlignment? = null,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any {
        val previous = parentData as? FieldSetChildData
        return FieldSetChildData(
            kind = kind ?: previous?.kind ?: FieldSetChildKind.Other,
            dense = dense ?: previous?.dense ?: false,
            weight = weight ?: previous?.weight,
            fill = fill ?: previous?.fill ?: true,
            align = align ?: previous?.align,
            previous = parentData,
        )
    }
}

internal fun Modifier.fieldSetChild(
    kind: FieldSetChildKind? = null,
    dense: Boolean? = null,
): Modifier = then(FieldSetChildModifier(kind = kind, dense = dense))

private fun Modifier.fieldSetWeight(
    weight: Float,
    fill: Boolean,
): Modifier = then(FieldSetChildModifier(weight = weight, fill = fill))

private fun Modifier.fieldSetAlign(alignment: Alignment.Horizontal): Modifier =
    then(FieldSetChildModifier(align = FieldSetAlignment(horizontal = alignment)))

/**
 * Scope adapter so [FieldSet] keeps its `ColumnScope` content contract while its
 * vertical layout applies the CSS `gap-4` plus the legend/description negative margin.
 * `weight`/`align` keep their Column meaning for direct children.
 */
private class FieldSetContentScope : ColumnScope {
    override fun Modifier.weight(
        weight: Float,
        fill: Boolean,
    ): Modifier {
        require(weight > 0f) { "invalid weight; must be greater than zero" }
        return this.fieldSetWeight(weight.coerceAtMost(Float.MAX_VALUE), fill)
    }

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier = this.fieldSetAlign(alignment)

    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier =
        then(FieldSetChildModifier(align = FieldSetAlignment(line = { it[alignmentLine] })))

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier =
        then(FieldSetChildModifier(align = FieldSetAlignment(line = alignmentLineBlock)))
}

private val fieldSetContentScope = FieldSetContentScope()

/**
 * Vertical stack matching `.cn-field-set`:
 * - gap 16 (Nova `gap-4`), or 12 when a direct child is a `checkbox-group`
 *   (`has-[>[data-slot=checkbox-group]]:gap-3`);
 * - `FieldDescription` immediately after a `legend`-variant `FieldLegend` loses 6dp
 *   (`[[data-variant=legend]+&]:-mt-1.5`), while the legend keeps its 6dp bottom margin.
 */
@Composable
internal fun FieldSetLayout(
    modifier: Modifier,
    spacing: Dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Layout(modifier = modifier, content = { fieldSetContentScope.content() }) { children, constraints ->
        val gap = spacing.roundToPx()
        val data = children.map { it.parentData as? FieldSetChildData }
        val denseGap = (spacing - 4.dp).coerceAtLeast(0.dp).roundToPx()
        val effectiveGap = if (data.any { it?.dense == true }) denseGap else gap
        val legendToDescription = (effectiveGap - 6.dp.roundToPx()).coerceAtLeast(0)

        fun gapBefore(index: Int): Int {
            if (index == 0) return 0
            val previous = data[index - 1]
            val current = data[index]
            val base = effectiveGap
            return if (previous?.kind == FieldSetChildKind.Legend &&
                current?.kind == FieldSetChildKind.Description
            ) {
                legendToDescription
            } else {
                base
            }
        }

        val totalGap = children.indices.sumOf { gapBefore(it) }
        val totalWeight = data.sumOf { (it?.weight ?: 0f).toDouble() }
        val placeables = arrayOfNulls<androidx.compose.ui.layout.Placeable>(children.size)
        var fixedHeight = 0
        children.forEachIndexed { index, child ->
            if (data[index]?.weight != null) return@forEachIndexed
            val maxHeight =
                if (constraints.hasBoundedHeight) {
                    (constraints.maxHeight - fixedHeight - totalGap).coerceAtLeast(0)
                } else {
                    Constraints.Infinity
                }
            val item = child.measure(Constraints(maxWidth = constraints.maxWidth, maxHeight = maxHeight))
            placeables[index] = item
            fixedHeight += item.height
        }
        // Like Column, an unbounded parent distributes only its minimum height.
        val targetHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else constraints.minHeight
        val remaining = (targetHeight - fixedHeight - totalGap).coerceAtLeast(0)
        val allocations =
            IntArray(children.size) { index ->
                if (totalWeight > 0.0) {
                    (remaining * ((data[index]?.weight ?: 0f).toDouble() / totalWeight)).roundToInt()
                } else {
                    0
                }
            }
        var remainder = remaining - allocations.sum()
        children.forEachIndexed { index, child ->
            val itemData = data[index] ?: return@forEachIndexed
            if (itemData.weight == null) return@forEachIndexed
            val adjustment = remainder.compareTo(0)
            remainder -= adjustment
            val height = (allocations[index] + adjustment).coerceAtLeast(0)
            placeables[index] =
                child.measure(
                    Constraints(
                        minHeight = if (itemData.fill) height else 0,
                        maxHeight = height,
                        maxWidth = constraints.maxWidth,
                    ),
                )
        }
        val items = placeables.map { checkNotNull(it) }
        val lines = items.mapIndexed { index, item -> data[index]?.align?.line?.invoke(item) }
        val beforeLine =
            (lines.filterNotNull().filter { it != AlignmentLine.Unspecified }.maxOrNull() ?: 0).coerceAtLeast(0)
        val afterLine =
            items.indices.maxOfOrNull { index ->
                val line = lines[index]
                if (line == null || line == AlignmentLine.Unspecified) 0 else items[index].width - line
            } ?: 0
        val width = constraints.constrainWidth(maxOf(items.maxOfOrNull { it.width } ?: 0, beforeLine + afterLine))
        val height = constraints.constrainHeight(items.sumOf { it.height } + totalGap)
        layout(width, height) {
            var y = 0
            items.forEachIndexed { index, item ->
                y += gapBefore(index)
                val alignment = data[index]?.align
                val line = lines[index]
                val x =
                    if (line == AlignmentLine.Unspecified) {
                        0
                    } else if (line != null) {
                        val offset = beforeLine - line
                        if (layoutDirection == LayoutDirection.Rtl) width - item.width - offset else offset
                    } else {
                        (alignment?.horizontal ?: Alignment.Start).align(item.width, width, layoutDirection)
                    }
                item.place(x, y)
                y += item.height
            }
        }
    }
}
