package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.TraversableNode.Companion.TraverseDescendantsAction
import androidx.compose.ui.node.requireLayoutCoordinates
import androidx.compose.ui.node.traverseDescendants
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs

/** Read actual Row children when a gesture settles; no snapshot writes during layout. */
internal class AttachmentGroupSnapInfo(
    private val scroll: ScrollState,
    private val direction: LayoutDirection,
    private val scrollPaddingPx: Float,
    private val keyboardStepPx: Float,
) : SnapLayoutInfoProvider {
    var node: AttachmentGroupSnapNode? = null
    var wheelInput: Boolean = false
    var viewportWidth: Int = Constraints.Infinity
        private set

    fun updateViewport(width: Int) {
        if (viewportWidth == width) return
        viewportWidth = width
        // Scroll content keeps unbounded constraints on resize. Invalidate the
        // real cards before measuring it, so cached preferred widths update too.
        node?.takeIf { it.isAttached }?.traverseDescendants(AttachmentSnapKey) {
            if (it is AttachmentGroupItemNode) it.invalidateMeasurement()
            TraverseDescendantsAction.SkipSubtreeAndContinueTraversal
        }
    }

    private fun ranges(): List<ClosedFloatingPointRange<Float>> {
        val maximum = scroll.maxValue.toFloat()
        val owner = node?.takeIf { it.isAttached && maximum > 0f } ?: return emptyList()
        val row = owner.requireLayoutCoordinates()
        val viewport = row.size.width - maximum
        val children = mutableListOf<AttachmentGroupItemNode>()
        owner.traverseDescendants(AttachmentSnapKey) { child ->
            if (child is AttachmentGroupItemNode) children += child
            // A card's nested attachments and a nested group own their own boundaries.
            TraverseDescendantsAction.SkipSubtreeAndContinueTraversal
        }
        return children.map { it.requireLayoutCoordinates() }.filter { it.size.width > 0 }.map { child ->
            val physical = row.localPositionOf(child, Offset.Zero).x
            val start = if (direction == LayoutDirection.Rtl) row.size.width - physical - child.size.width else physical
            val leading = (start - scrollPaddingPx).coerceIn(0f, maximum)
            // CSS mandatory snapping permits scrolling inside a card larger than
            // the snapport, rather than making its hidden text/actions unreachable.
            val trailing =
                (start + child.size.width - viewport + scrollPaddingPx)
                    .coerceIn(leading, maximum)
            leading..trailing
        }
    }

    override fun calculateSnapOffset(velocity: Float): Float {
        val current = scroll.value.toFloat()
        val target =
            ranges().map { current.coerceIn(it.start, it.endInclusive) }
                .minWithOrNull(compareBy<Float> { abs(it - current) }.thenBy { if (velocity >= 0) -it else it })
                ?: current
        return target - current
    }

    fun keyboardTarget(forward: Boolean): Int {
        val current = scroll.value.toFloat()
        val areas = ranges()
        val step = (current + if (forward) keyboardStepPx else -keyboardStepPx).coerceIn(0f, scroll.maxValue.toFloat())
        if (areas.any { current in it && step in it }) return step.toInt()
        val stops = areas.flatMap { listOf(it.start, it.endInclusive) }.distinct().sorted()
        return (
            if (forward) {
                stops.firstOrNull { it > current + 1f } ?: scroll.maxValue.toFloat()
            } else {
                stops.lastOrNull { it < current - 1f } ?: 0f
            }
        ).toInt()
    }
}

private object AttachmentSnapKey

internal class AttachmentGroupItemNode(var minimumWidth: Dp) : Modifier.Node(), TraversableNode, LayoutModifierNode {
    override val traverseKey: Any = AttachmentSnapKey

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val group = findNearestAncestor(AttachmentSnapKey) as? AttachmentGroupSnapNode
        val viewport = group?.info?.viewportWidth ?: Constraints.Infinity
        if (viewport == Constraints.Infinity) {
            val child = measurable.measure(constraints)
            return layout(child.width, child.height) { child.placeRelative(0, 0) }
        }
        // Nova's horizontal min-w-40 wins over max-w-full below 160dp.
        val limit = minOf(constraints.maxWidth, maxOf(viewport, minimumWidth.roundToPx()))
        val child =
            measurable.measure(
                constraints.copy(
                    minWidth = constraints.minWidth.coerceAtMost(limit),
                    maxWidth = limit,
                ),
            )
        // requiredWidth is the caller's explicit overflow choice. Account for
        // its uncoerced extent so that the entire card is still scrollable.
        return layout(child.measuredWidth, child.height) {
            child.placeRelative((child.measuredWidth - child.width) / 2, 0)
        }
    }
}

internal data class AttachmentGroupItemElement(val minimumWidth: Dp) : ModifierNodeElement<AttachmentGroupItemNode>() {
    override fun create() = AttachmentGroupItemNode(minimumWidth)

    override fun update(node: AttachmentGroupItemNode) {
        node.minimumWidth = minimumWidth
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "attachmentSnapItem"
    }
}

internal class AttachmentGroupSnapNode(var info: AttachmentGroupSnapInfo) : Modifier.Node(), TraversableNode {
    override val traverseKey: Any = AttachmentSnapKey

    override fun onAttach() {
        info.node = this
    }

    override fun onDetach() {
        if (info.node === this) info.node = null
    }

    fun update(next: AttachmentGroupSnapInfo) {
        if (info.node === this) info.node = null
        info = next
        if (isAttached) info.node = this
    }
}

internal data class AttachmentGroupSnapElement(
    val info: AttachmentGroupSnapInfo,
) : ModifierNodeElement<AttachmentGroupSnapNode>() {
    override fun create() = AttachmentGroupSnapNode(info)

    override fun update(node: AttachmentGroupSnapNode) {
        node.update(info)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "attachmentSnapGroup"
    }
}
