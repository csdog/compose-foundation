@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.empty

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.dp

/** Style resolves once and supplies the final caller shape to Empty's dashed paint. */
@Composable
internal fun Modifier.emptyStyleable(
    state: StyleState,
    base: Style,
    style: Style,
    dashedBorderColor: Color?,
    nova: Boolean,
): Modifier {
    if (dashedBorderColor == null) return styleable(state, base, style)
    val border = remember { EmptyDashedOutline() }
    val wrapped =
        remember(base, style, dashedBorderColor) {
            Style {
                val target = this
                var shape: Shape = RectangleShape
                val receiver =
                    object : StyleScope by target {
                        override fun shape(value: Shape) {
                            shape = value
                            target.shape(value)
                        }
                    }
                with(base) { receiver.applyStyle() }
                with(style) { receiver.applyStyle() }
                border.update(shape, dashedBorderColor)
            }
        }
    val paint = EmptyBorderElement(border)
    return if (nova) then(paint).styleable(state, wrapped) else styleable(state, wrapped).then(paint)
}

private class EmptyDashedOutline {
    private var shape: Shape = RectangleShape
    private var color: Color = Color.Transparent
    var invalidate: (() -> Unit)? = null

    fun update(
        shape: Shape,
        color: Color,
    ) {
        if (this.shape == shape && this.color == color) return
        this.shape = shape
        this.color = color
        invalidate?.invoke()
    }

    fun draw(scope: ContentDrawScope): Unit =
        with(scope) {
            val stroke =
                Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), phase = 0f),
                )
            drawOutline(shape.createOutline(size, layoutDirection, this), color, style = stroke)
        }
}

private class EmptyBorderElement(private val border: EmptyDashedOutline) : ModifierNodeElement<EmptyBorderNode>() {
    override fun create() = EmptyBorderNode(border)

    override fun update(node: EmptyBorderNode) {
        node.update(border)
    }

    override fun equals(other: Any?) = other is EmptyBorderElement && other.border === border

    override fun hashCode() = border.hashCode()

    override fun InspectorInfo.inspectableProperties() {
        name = "emptyDashedOutline"
    }
}

private class EmptyBorderNode(private var border: EmptyDashedOutline) : Modifier.Node(), DrawModifierNode {
    private val invalidateBorder: () -> Unit = { invalidateDraw() }

    override fun onAttach() {
        border.invalidate = invalidateBorder
    }

    override fun onDetach() {
        border.invalidate = null
    }

    fun update(value: EmptyDashedOutline) {
        if (border === value) return
        border.invalidate = null
        border = value
        if (isAttached) border.invalidate = invalidateBorder
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        border.draw(this)
    }
}
