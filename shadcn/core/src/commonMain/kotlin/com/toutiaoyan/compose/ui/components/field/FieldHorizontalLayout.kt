package com.toutiaoyan.compose.ui.components.field

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp

internal enum class FieldChild { Content, Checkbox }

private data class FieldChildData(val kind: FieldChild, val previous: Any?)

private class FieldChildModifier(val kind: FieldChild) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = FieldChildData(kind, parentData)
}

internal fun Modifier.fieldChild(kind: FieldChild): Modifier = then(FieldChildModifier(kind))

/** Horizontal fields with FieldContent align at the top; checkboxes receive the CSS mt-px. */
@Composable
internal fun FieldHorizontalLayout(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { children, constraints ->
        val gap = 8.dp.roundToPx()
        val kinds = children.map { (it.parentData as? FieldChildData)?.kind }
        val hasContent = FieldChild.Content in kinds
        val placeables = arrayOfNulls<androidx.compose.ui.layout.Placeable>(children.size)
        val available =
            if (constraints.hasBoundedWidth) {
                (constraints.maxWidth - gap * (children.size - 1)).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
        var remaining = available
        // CSS `*:w-auto`: measure each non-content child once with the row's full width so
        // controls can wrap/shrink, then give `FieldContent` (`flex-1`) the remaining space.
        children.indices.filter { kinds[it] != FieldChild.Content }.forEach { i ->
            val child =
                if (available == Constraints.Infinity) {
                    children[i].measure(Constraints())
                } else {
                    children[i].measure(Constraints(maxWidth = available))
                }
            placeables[i] = child
            if (remaining != Constraints.Infinity) remaining = (remaining - child.width).coerceAtLeast(0)
        }
        children.indices.filter { kinds[it] == FieldChild.Content }.forEach { i ->
            placeables[i] =
                if (remaining == Constraints.Infinity) {
                    children[i].measure(Constraints())
                } else {
                    children[i].measure(Constraints(minWidth = remaining, maxWidth = remaining))
                }
        }
        val inset = 1.dp.roundToPx()
        val items = placeables.map { checkNotNull(it) }
        val width = constraints.constrainWidth(items.sumOf { it.width } + gap * (items.size - 1).coerceAtLeast(0))
        val height =
            constraints.constrainHeight(
                items.indices.maxOfOrNull {
                    items[it].height + if (hasContent && kinds[it] == FieldChild.Checkbox) inset else 0
                } ?: 0,
            )
        layout(width, height) {
            var x = 0
            items.forEachIndexed { i, item ->
                val y =
                    if (hasContent) {
                        if (kinds[i] == FieldChild.Checkbox) inset else 0
                    } else {
                        (height - item.height) / 2
                    }
                item.placeRelative(x, y)
                x += item.width + gap
            }
        }
    }
}
