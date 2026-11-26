@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp

internal enum class SidebarProviderChild { Rail, Inset, RailToggle }

private class SidebarProviderChildData(
    val kind: SidebarProviderChild,
    val side: SidebarSide,
    val showsInwardBorder: Boolean,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@SidebarProviderChildData
}

internal fun Modifier.sidebarProviderChild(
    kind: SidebarProviderChild,
    side: SidebarSide = SidebarSide.Start,
    showsInwardBorder: Boolean = false,
): Modifier = then(SidebarProviderChildData(kind, side, showsInwardBorder))

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
        val data = children.map { it.parentData as? SidebarProviderChildData }
        val panels = children.indices.filter { data[it]?.kind == SidebarProviderChild.Rail }
        val placeables = arrayOfNulls<Placeable>(children.size)
        val railConstraints =
            if (bounded) Constraints(maxWidth = maxW, maxHeight = maxH) else Constraints()
        var railWidth = 0
        panels.forEach { i ->
            val rail = children[i].measure(railConstraints)
            placeables[i] = rail
            railWidth += rail.width
        }
        var remaining = if (bounded) (maxW - railWidth).coerceAtLeast(0) else Constraints.Infinity
        // Like unweighted Row children, ordinary siblings reserve space before flex items.
        children.indices.filter { index ->
            val kind = data[index]?.kind
            kind == null || (kind == SidebarProviderChild.RailToggle && panels.isEmpty())
        }.forEach { i ->
            val item = children[i].measure(Constraints(maxWidth = remaining, maxHeight = maxH))
            placeables[i] = item
            if (bounded) remaining = (remaining - item.width).coerceAtLeast(0)
        }
        val insets = children.indices.filter { data[it]?.kind == SidebarProviderChild.Inset }
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
        val starts = IntArray(children.size)
        var flowWidth = 0
        placeables.forEachIndexed { index, item ->
            starts[index] = flowWidth
            if (item != null) flowWidth += item.width
        }
        val overlays =
            children.indices.filter {
                data[it]?.kind == SidebarProviderChild.RailToggle && placeables[it] == null
            }.map { index ->
                // A provider-level rail follows its panel, or can precede the first panel.
                val panelIndex = panels.lastOrNull { it < index } ?: panels.first()
                val panel = checkNotNull(placeables[panelIndex])
                val panelData = checkNotNull(data[panelIndex])
                val border = if (panelData.showsInwardBorder && panel.width > 0) 1.dp.roundToPx() else 0
                val edge =
                    starts[panelIndex] +
                        if (panelData.side == SidebarSide.Start) panel.width - border else border
                val strip = 16.dp.roundToPx()
                val toggle = children[index].measure(Constraints.fixed(strip, panel.height))
                (edge - strip / 2) to toggle
            }
        val width = constraints.constrainWidth(flowWidth)
        val height = constraints.constrainHeight(placeables.maxOfOrNull { it?.height ?: 0 } ?: 0)
        layout(width, height) {
            placeables.forEachIndexed { index, item -> item?.placeRelative(starts[index], 0) }
            overlays.forEach { (start, toggle) -> toggle.placeRelative(start, 0, zIndex = 1f) }
        }
    }
}
