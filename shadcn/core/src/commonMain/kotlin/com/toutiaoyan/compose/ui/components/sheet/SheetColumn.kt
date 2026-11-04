package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private data class SheetChild(
    val weight: Float = 0f,
    val fill: Boolean = true,
    val alignment: Alignment.Horizontal? = null,
    val alignBy: ((Measured) -> Int)? = null,
    val autoMargin: Boolean = false,
)

private class SheetChildModifier(val update: (SheetChild) -> SheetChild) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = update(parentData as? SheetChild ?: SheetChild())
}

internal fun Modifier.sheetAutoMargin(): Modifier = then(SheetChildModifier { it.copy(autoMargin = true) })

private object SheetColumnScope : ColumnScope {
    override fun Modifier.weight(
        weight: Float,
        fill: Boolean,
    ): Modifier {
        require(weight > 0f && weight.isFinite()) { "Sheet weight must be finite and greater than zero" }
        return then(SheetChildModifier { it.copy(weight = weight, fill = fill) })
    }

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier =
        then(SheetChildModifier { it.copy(alignment = alignment, alignBy = null) })

    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier = alignBy { it[alignmentLine] }

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier =
        then(SheetChildModifier { it.copy(alignBy = alignmentLineBlock, alignment = null) })
}

/** CSS column layout: stretch by default, normal ColumnScope weights, and Footer's mt-auto.
 * Auto margins consume only the space left after weighted children, never a second weighted spacer.
 */
@Composable
internal fun SheetColumn(
    modifier: Modifier = Modifier,
    gap: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Layout(modifier = modifier, content = { SheetColumnScope.content() }) { measurables, constraints ->
        val data = measurables.map { it.parentData as? SheetChild ?: SheetChild() }
        val gapPx = gap.roundToPx()
        val gaps = gapPx * (measurables.size - 1).coerceAtLeast(0)
        val bounded = constraints.hasBoundedHeight
        val available = if (bounded) (constraints.maxHeight - gaps).coerceAtLeast(0) else Constraints.Infinity
        val children = arrayOfNulls<Placeable>(measurables.size)
        var used = 0
        var weightSum = 0f

        fun childConstraints(
            index: Int,
            maxHeight: Int,
            minHeight: Int = 0,
        ): Constraints {
            val stretch = constraints.hasBoundedWidth && data[index].alignment == null && data[index].alignBy == null
            return Constraints(
                minWidth = if (stretch) constraints.maxWidth else 0,
                maxWidth = constraints.maxWidth,
                minHeight = minHeight,
                maxHeight = maxHeight,
            )
        }
        measurables.forEachIndexed { index, measurable ->
            if (data[index].weight > 0f && bounded) {
                weightSum += data[index].weight
            } else {
                val limit = if (bounded) (available - used).coerceAtLeast(0) else Constraints.Infinity
                children[index] = measurable.measure(childConstraints(index, limit))
                used += children[index]!!.height
            }
        }
        var remaining = if (bounded) (available - used).coerceAtLeast(0) else 0
        data.forEachIndexed { index, child ->
            if (children[index] == null) {
                val allocation = (remaining * (child.weight / weightSum)).roundToInt().coerceIn(0, remaining)
                children[index] =
                    measurables[index].measure(
                        childConstraints(index, allocation, if (child.fill) allocation else 0),
                    )
                remaining -= allocation
                weightSum -= child.weight
            }
        }
        val measured = children.map { checkNotNull(it) }
        val lines =
            measured.mapIndexed {
                    index,
                    child,
                ->
                data[index].alignBy?.invoke(child)
                    ?: AlignmentLine.Unspecified
            }
        val leading =
            measured.mapIndexed { index, child ->
                if (lines[index] == AlignmentLine.Unspecified) {
                    0
                } else if (layoutDirection == LayoutDirection.Ltr) {
                    lines[index]
                } else {
                    child.width - lines[index]
                }
            }
        val before = leading.maxOrNull() ?: 0
        val after =
            measured.indices.maxOfOrNull { index ->
                if (lines[index] == AlignmentLine.Unspecified) 0 else measured[index].width - leading[index]
            } ?: 0
        val width = constraints.constrainWidth(maxOf(measured.maxOfOrNull { it.width } ?: 0, before + after))
        val naturalHeight = measured.sumOf { it.height } + gaps
        val height = constraints.constrainHeight(naturalHeight)
        val autoCount = data.count { it.autoMargin }
        var autoSpace = (height - naturalHeight).coerceAtLeast(0)
        var autoRemaining = autoCount
        layout(width, height) {
            var y = 0
            measured.forEachIndexed { index, child ->
                if (data[index].autoMargin && autoRemaining > 0) {
                    val margin = autoSpace / autoRemaining
                    y += margin
                    autoSpace -= margin
                    autoRemaining--
                }
                val x =
                    if (lines[index] != AlignmentLine.Unspecified) {
                        val offset = before - leading[index]
                        if (layoutDirection == LayoutDirection.Ltr) offset else width - child.width - offset
                    } else {
                        (data[index].alignment ?: Alignment.Start).align(child.width, width, layoutDirection)
                    }
                child.place(x, y)
                y += child.height + gapPx
            }
        }
    }
}
