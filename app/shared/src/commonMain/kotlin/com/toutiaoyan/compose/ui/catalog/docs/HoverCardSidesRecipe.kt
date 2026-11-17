@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.hovercard.*
import com.toutiaoyan.compose.ui.components.text.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalDensity

/** Keep the Nova/Geist flex breakpoint independent of Compose's integer button measurements. */
@Composable
internal fun HoverCardSidesRecipe(modifier: Modifier = Modifier) {
    val sides = listOf(HoverCardSide.Left, HoverCardSide.Top, HoverCardSide.Bottom, HoverCardSide.Right)
    val density = LocalDensity.current
    val chrome = with(density) { 22.dp.toPx() }
    val fontScale = with(density) { 14.sp.toPx() / 14f }
    // Inline text advances at Geist Medium 14px, from the Base UI Sides recipe's
    // measured border boxes minus its 20px padding and 2px border.
    val advances = listOf(26.3125f, 23.4375f, 47.921875f, 34.78125f)
    val ideal = advances.map { chrome + it * fontScale }
    val gap = with(density) { 8.dp.roundToPx() }
    Layout(
        modifier = modifier.fillMaxWidth(),
        content = {
            sides.forEach { side ->
                HoverCard {
                    HoverCardTrigger(
                        delay = 100,
                        closeDelay = 100,
                    ) { Button(side.name, onClick = {}, variant = ButtonVariant.Outline) }
                    HoverCardContent(side = side) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(textStyle = TextStyle(fontWeight = FontWeight.Medium)) { "Hover Card" }
                            Text { "This hover card appears on the " + side.name.lowercase() + " side of the trigger." }
                        }
                    }
                }
            }
        },
    ) { children, constraints ->
        val items = children.map { it.measure(Constraints()) }
        val rows = mutableListOf<MutableList<Int>>()
        var used = 0f
        items.indices.forEach { i ->
            val extra = ideal[i] + if (rows.lastOrNull()?.isNotEmpty() == true) gap else 0
            if (rows.isEmpty() || used + extra > constraints.maxWidth) {
                rows.add(mutableListOf())
                used = 0f
            }
            if (rows.last().isNotEmpty()) used += gap
            rows.last().add(i)
            used += ideal[i]
        }
        val rowWidths = rows.map { row -> row.sumOf { items[it].width } + gap * (row.size - 1) }
        val rowHeights = rows.map { row -> row.maxOf { items[it].height } }
        val width = constraints.constrainWidth(rowWidths.maxOrNull() ?: 0)
        val height = constraints.constrainHeight(rowHeights.sum() + gap * (rows.size - 1).coerceAtLeast(0))
        layout(width, height) {
            var y = 0
            rows.forEachIndexed { line, row ->
                // Fit integer widths inside a row that fits under the CSS measurements.
                val spaces = row.lastIndex
                val overflow = (rowWidths[line] - width).coerceIn(0, gap * spaces)
                var x = (width - rowWidths[line] + overflow) / 2
                row.forEachIndexed { index, i ->
                    items[i].placeRelative(x, y)
                    if (index < spaces) {
                        val reduction = overflow / spaces + if (index < overflow % spaces) 1 else 0
                        x += items[i].width + gap - reduction
                    }
                }
                y += rowHeights[line] + gap
            }
        }
    }
}
