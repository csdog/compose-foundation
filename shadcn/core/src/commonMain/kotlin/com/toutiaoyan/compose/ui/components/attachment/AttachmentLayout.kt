package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

/** Parent data identifies real attachment parts without a global slot registry. */
internal enum class AttachmentPart { Media, Content, Actions }

/** Nova's fit-content flex row/column. Fixed media/actions reserve space before filenames. */
@Composable
internal fun AttachmentLayout(
    size: AttachmentSize,
    orientation: AttachmentOrientation,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { nodes, constraints ->
        val vertical = orientation == AttachmentOrientation.Vertical
        val hasMedia = nodes.any { it.layoutId == AttachmentPart.Media }
        val hasContent = nodes.any { it.layoutId == AttachmentPart.Content }
        val paddingY =
            if (!hasMedia && !hasContent) {
                0
            } else {
                when (size) {
                    AttachmentSize.Default -> 8.dp
                    AttachmentSize.Sm -> 6.dp
                    AttachmentSize.Xs -> 4.dp
                }.roundToPx()
            }
        val paddingX = paddingY + if (!hasMedia && hasContent) 2.dp.roundToPx() else 0
        val gap =
            when (size) {
                AttachmentSize.Default -> 8.dp
                AttachmentSize.Sm -> 10.dp
                AttachmentSize.Xs -> 6.dp
            }.roundToPx()
        val border = 1.dp.roundToPx() // Style's border has already reserved this outside the layout.
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val body = if (vertical) nodes.filter { it.layoutId != AttachmentPart.Actions } else nodes
        val positions = mutableListOf<Triple<Placeable, Int, Int>>()
        val width: Int
        val height: Int
        if (vertical) {
            width = constraints.constrainWidth((if (hasContent) 120.dp else 96.dp).roundToPx() - border * 2)
            val innerWidth = (width - paddingX * 2).coerceAtLeast(0)
            var y = paddingY
            body.forEachIndexed { index, node ->
                val inset = if (node.layoutId == AttachmentPart.Content) 4.dp.roundToPx() else 0
                val available = (innerWidth - inset * 2).coerceAtLeast(0)
                val child =
                    node.measure(
                        if (node.layoutId == AttachmentPart.Media) {
                            Constraints.fixed(available, available)
                        } else {
                            loose.copy(minWidth = available, maxWidth = available)
                        },
                    )
                positions += Triple(child, paddingX + inset, y)
                y += child.height + if (index < body.lastIndex) gap else 0
            }
            height = constraints.constrainHeight(y + paddingY)
            nodes.filter { it.layoutId == AttachmentPart.Actions }.forEach { node ->
                val child = node.measure(loose.copy(maxWidth = innerWidth))
                // absolute top-3/right-3: these do not consume the column's space.
                positions += Triple(child, width - child.width - 12.dp.roundToPx(), 12.dp.roundToPx())
            }
        } else {
            val gaps = gap * (body.size - 1).coerceAtLeast(0)
            val maxInnerWidth =
                if (constraints.hasBoundedWidth) {
                    (constraints.maxWidth - paddingX * 2).coerceAtLeast(
                        0,
                    )
                } else {
                    Constraints.Infinity
                }
            val fixed = mutableMapOf<Int, Placeable>()
            var used = 0
            // Actions cannot be pushed outside the card by a long title.
            val fixedIndices =
                body.indices.filter { body[it].layoutId != AttachmentPart.Content }
                    .sortedBy { if (body[it].layoutId == AttachmentPart.Actions) 0 else 1 }
            fixedIndices.forEach { index ->
                val remaining =
                    if (maxInnerWidth == Constraints.Infinity) {
                        Constraints.Infinity
                    } else {
                        (maxInnerWidth - used - gaps).coerceAtLeast(
                            0,
                        )
                    }
                val mediaSize =
                    when (size) {
                        AttachmentSize.Default -> 40.dp
                        AttachmentSize.Sm -> 32.dp
                        AttachmentSize.Xs -> 28.dp
                    }.roundToPx().coerceAtMost(remaining)
                val child =
                    body[index].measure(
                        if (body[index].layoutId == AttachmentPart.Media) {
                            Constraints.fixed(mediaSize, mediaSize)
                        } else {
                            loose.copy(maxWidth = remaining)
                        },
                    )
                fixed[index] = child
                used += child.width
            }
            val flexible = body.indices.filter { body[it].layoutId == AttachmentPart.Content }
            val natural = flexible.sumOf { body[it].maxIntrinsicWidth(Constraints.Infinity).toLong() }
            val naturalWidth =
                (natural + used + gaps + paddingX * 2).coerceAtMost(
                    (Constraints.Infinity - 1).toLong(),
                ).toInt()
            width = constraints.constrainWidth(maxOf(naturalWidth, 160.dp.roundToPx() - border * 2))
            val available = (width - paddingX * 2 - used - gaps).coerceAtLeast(0)
            val children =
                body.indices.map { index ->
                    fixed[index] ?: run {
                        val share =
                            available / flexible.size +
                                if (flexible.indexOf(index) < available % flexible.size) {
                                    1
                                } else {
                                    0
                                }
                        body[index].measure(loose.copy(minWidth = share, maxWidth = share))
                    }
                }
            val bodyHeight = children.maxOfOrNull { it.height } ?: 0
            height = constraints.constrainHeight(bodyHeight + paddingY * 2)
            var x = paddingX
            children.forEach { child ->
                positions += Triple(child, x, paddingY + (bodyHeight - child.height) / 2)
                x += child.width + gap
            }
        }
        layout(width, height) {
            positions.forEach { (child, x, y) ->
                // Vertical actions use physical right-3 in Nova, including RTL.
                if (vertical) child.place(x, y) else child.placeRelative(x, y)
            }
        }
    }
}
