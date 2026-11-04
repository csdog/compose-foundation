@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.card

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Dp

/** Declaration tracking only: no snapshot writes from Style resolution or measurement. */
internal class CardInsetOverrides {
    var top = false
        private set
    var bottom = false
        private set
    var invalidate: (() -> Unit)? = null

    fun update(
        top: Boolean,
        bottom: Boolean,
    ) {
        if (this.top == top && this.bottom == bottom) return
        this.top = top
        this.bottom = bottom
        invalidate?.invoke()
    }
}

@Composable
internal fun rememberCardCallerStyle(
    style: Style,
    insets: CardInsetOverrides,
): Style =
    remember(style, insets) {
        Style {
            val target = this
            var top = false
            var bottom = false
            val scope =
                object : StyleScope by target {
                    override fun contentPaddingTop(value: Dp) {
                        top = true
                        target.contentPaddingTop(value)
                    }

                    override fun contentPaddingBottom(value: Dp) {
                        bottom = true
                        target.contentPaddingBottom(value)
                    }
                }
            with(style) { scope.applyStyle() }
            insets.update(top, bottom)
        }
    }

internal fun Modifier.cardPartLine(line: HorizontalAlignmentLine?): Modifier =
    if (line == null) {
        this
    } else {
        layout { measurable, constraints ->
            val child = measurable.measure(constraints)
            layout(child.width, child.height, alignmentLines = mapOf(line to 0)) { child.placeRelative(0, 0) }
        }
    }

internal data class CardLayoutElement(
    val spacing: Dp,
    val footerLine: HorizontalAlignmentLine,
    val imageLine: HorizontalAlignmentLine,
    val insets: CardInsetOverrides,
) : ModifierNodeElement<CardLayoutNode>() {
    override fun create() = CardLayoutNode(this)

    override fun update(node: CardLayoutNode) {
        node.update(this)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "cardInsets"
    }
}

internal class CardLayoutNode(private var value: CardLayoutElement) : Modifier.Node(), LayoutModifierNode {
    private val invalidateInsets: () -> Unit = { invalidateMeasurement() }

    override fun onAttach() {
        value.insets.invalidate = invalidateInsets
    }

    override fun onDetach() {
        value.insets.invalidate = null
    }

    fun update(next: CardLayoutElement) {
        value.insets.invalidate = null
        value = next
        if (isAttached) value.insets.invalidate = invalidateInsets
        invalidateMeasurement()
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val child = measurable.measure(constraints)
        val top = if (!value.insets.top && child[value.imageLine] == 0) value.spacing.roundToPx() else 0
        val bottom =
            if (!value.insets.bottom && child[value.footerLine] != AlignmentLine.Unspecified) {
                value.spacing.roundToPx()
            } else {
                0
            }
        // Style adds the default vertical padding outside this node. Only reclaim the
        // appropriate inset here; each part retains its full measured and semantic bounds.
        return layout(child.width, constraints.constrainHeight((child.height - top - bottom).coerceAtLeast(0))) {
            child.placeRelative(0, -top)
        }
    }
}
