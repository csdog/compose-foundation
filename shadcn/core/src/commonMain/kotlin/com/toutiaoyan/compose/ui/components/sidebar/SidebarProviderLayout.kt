@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

internal enum class SidebarProviderChild { Rail, Inset, Other }

private class SidebarProviderChildData(val kind: SidebarProviderChild) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@SidebarProviderChildData
}

internal fun Modifier.sidebarProviderChild(kind: SidebarProviderChild): Modifier = then(SidebarProviderChildData(kind))

/**
 * Provider row matching CSS `flex`: a [Sidebar] keeps its fixed (animated) width while
 * [SidebarInset] (`flex-1`) takes the remaining space, independent of composition order —
 * needed for the right-hand sidebar, where the inset is composed before the rail.
 */
@Composable
internal fun SidebarProviderLayout(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { children, constraints ->
        val bounded = constraints.hasBoundedWidth
        val maxW = if (bounded) constraints.maxWidth else Constraints.Infinity
        val maxH =
            if (constraints.hasBoundedHeight) {
                constraints.maxHeight
            } else {
                Constraints.Infinity
            }
        val kinds = children.map { (it.parentData as? SidebarProviderChildData)?.kind }
        val placeables = arrayOfNulls<androidx.compose.ui.layout.Placeable>(children.size)
        val railConstraints =
            if (bounded) Constraints(maxWidth = maxW, maxHeight = maxH) else Constraints()
        var railWidth = 0
        children.indices.filter { kinds[it] == SidebarProviderChild.Rail }.forEach { i ->
            val rail = children[i].measure(railConstraints)
            placeables[i] = rail
            railWidth += rail.width
        }
        var remaining = if (bounded) (maxW - railWidth).coerceAtLeast(0) else Constraints.Infinity
        // Like unweighted Row children, ordinary siblings reserve space before flex items.
        children.indices.filter {
            kinds[it] != SidebarProviderChild.Rail && kinds[it] != SidebarProviderChild.Inset
        }.forEach { i ->
            val item = children[i].measure(Constraints(maxWidth = remaining, maxHeight = maxH))
            placeables[i] = item
            if (bounded) remaining = (remaining - item.width).coerceAtLeast(0)
        }
        val insets = children.indices.filter { kinds[it] == SidebarProviderChild.Inset }
        insets.forEachIndexed { index, i ->
            placeables[i] =
                if (!bounded) {
                    children[i].measure(Constraints(maxHeight = maxH))
                } else {
                    // Equal flex shares; distribute rounding pixels without losing width.
                    val share = remaining / (insets.size - index)
                    remaining -= share
                    children[i].measure(Constraints(minWidth = share, maxWidth = share, maxHeight = maxH))
                }
        }
        val items = placeables.map { checkNotNull(it) }
        val width = constraints.constrainWidth(items.sumOf { it.width })
        val height = constraints.constrainHeight(items.maxOfOrNull { it.height } ?: 0)
        layout(width, height) {
            var x = 0
            items.forEach { item ->
                item.placeRelative(x, 0)
                x += item.width
            }
        }
    }
}
