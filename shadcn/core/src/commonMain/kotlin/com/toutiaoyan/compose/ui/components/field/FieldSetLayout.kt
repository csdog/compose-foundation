@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

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

internal data class FieldSetChildData(
    val kind: FieldSetChildKind = FieldSetChildKind.Other,
    val dense: Boolean = false,
    val weight: Float? = null,
    val fill: Boolean = true,
    val align: Alignment.Horizontal? = null,
    val previous: Any? = null,
)

private class FieldSetChildModifier(
    private val kind: FieldSetChildKind? = null,
    private val dense: Boolean? = null,
    private val weight: Float? = null,
    private val fill: Boolean? = null,
    private val align: Alignment.Horizontal? = null,
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

private fun Modifier.fieldSetWeight(weight: Float, fill: Boolean): Modifier =
    then(FieldSetChildModifier(weight = weight, fill = fill))

private fun Modifier.fieldSetAlign(alignment: Alignment.Horizontal): Modifier =
    then(FieldSetChildModifier(align = alignment))

/**
 * Scope adapter so [FieldSet] keeps its `ColumnScope` content contract while its
 * vertical layout applies the CSS `gap-4` plus the legend/description negative margin.
 * `weight`/`align` keep their Column meaning for direct children.
 */
private class FieldSetContentScope : ColumnScope {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier =
        this.fieldSetWeight(weight, fill)

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier =
        this.fieldSetAlign(alignment)

    // Not used by the field recipes; Column's baseline alignment is left to real Columns.
    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier = this

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier = this
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

        val totalWeight = data.sumOf { (it?.weight ?: 0f).toDouble() }.toFloat()
        val placeables = arrayOfNulls<androidx.compose.ui.layout.Placeable>(children.size)
        var fixedHeight = 0
        var maxWidth = 0
        children.forEachIndexed { index, child ->
            val itemData = data[index]
            if (totalWeight > 0f && itemData?.weight != null) return@forEachIndexed
            val childConstraints = if (constraints.hasBoundedHeight) {
                Constraints(maxWidth = constraints.maxWidth, maxHeight = constraints.maxHeight)
            } else {
                Constraints(maxWidth = constraints.maxWidth)
            }
            val placeable = child.measure(childConstraints)
            placeables[index] = placeable
            fixedHeight += placeable.height + gapBefore(index)
            maxWidth = maxOf(maxWidth, placeable.width)
        }
        val remaining =
            if (constraints.hasBoundedHeight) {
                (constraints.maxHeight - fixedHeight).coerceAtLeast(0)
            } else {
                0
            }
        children.forEachIndexed { index, child ->
            val itemData = data[index] ?: return@forEachIndexed
            val weight = itemData.weight ?: return@forEachIndexed
            if (totalWeight <= 0f) return@forEachIndexed
            val height = (remaining * (weight / totalWeight)).toInt()
            val placeable =
                child.measure(
                    Constraints(
                        maxWidth = constraints.maxWidth,
                        maxHeight = height,
                    ),
                )
            placeables[index] = placeable
            maxWidth = maxOf(maxWidth, placeable.width)
        }
        val items = placeables.map { checkNotNull(it) }
        var totalHeight = 0
        items.forEachIndexed { index, item ->
            val itemData = data[index]
            totalHeight +=
                if (itemData?.weight != null && totalWeight > 0f) {
                    itemData.weight?.let { (remaining * (it / totalWeight)).toInt() } ?: item.height
                } else {
                    item.height
                } + gapBefore(index)
        }
        totalHeight = constraints.constrainHeight(totalHeight)
        val width = constraints.constrainWidth(maxWidth)
        layout(width, totalHeight) {
            var y = 0
            items.forEachIndexed { index, item ->
                y += gapBefore(index)
                val itemData = data[index]
                val itemHeight =
                    if (itemData?.weight != null && totalWeight > 0f) {
                        (remaining * ((itemData.weight ?: 0f) / totalWeight)).toInt()
                    } else {
                        item.height
                    }
                val x =
                    when (itemData?.align) {
                        Alignment.Start -> 0
                        Alignment.CenterHorizontally -> (width - item.width) / 2
                        Alignment.End -> width - item.width
                        else -> 0
                    }
                item.placeRelative(x, y)
                y += itemHeight
            }
        }
    }
}
