@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.shadow.DropShadowPainter
import androidx.compose.ui.graphics.shadow.Shadow

/**
 * CSS outer shadows exclude the element's interior, even with a translucent fill.
 * Style owns state and the final shape; the native painter owns spread/blur geometry.
 * Plain draw data is captured during Style resolution and consumed after drawContent,
 * without a snapshot write or a second interaction/focus state.
 */
@Composable
internal fun Modifier.cssStyleable(
    styleState: StyleState?,
    vararg styles: Style,
): Modifier {
    val shadow = remember { CssOuterShadow() }
    val wrapped =
        remember(*styles) {
            Style {
                val target = this
                var resolvedShape: Shape = RectangleShape
                var resolvedShadows: List<Shadow> = emptyList()
                val receiver =
                    object : StyleScope by target {
                        override fun shape(value: Shape) {
                            resolvedShape = value
                            target.shape(value)
                        }

                        override fun dropShadow(value: Shadow) {
                            resolvedShadows = listOf(value)
                            // Keep the native style property/invalidations, but paint the visible
                            // shadow with its interior clipped out after the style resolves.
                            target.dropShadow(value.transparent())
                        }

                        override fun dropShadow(vararg value: Shadow) {
                            resolvedShadows = value.toList()
                            target.dropShadow(*value.map { it.transparent() }.toTypedArray())
                        }
                    }
                styles.forEach { style -> with(style) { receiver.applyStyle() } }
                shadow.update(resolvedShape, resolvedShadows)
            }
        }
    return then(CssShadowElement(shadow)).styleable(styleState, wrapped)
}

private fun Shadow.transparent() =
    Shadow(
        radius = radius,
        color = Color.Transparent,
        spread = spread,
        offset = offset,
        alpha = 0f,
    )

private class CssOuterShadow {
    private var shape: Shape = RectangleShape
    private var values: List<Shadow> = emptyList()
    var invalidate: (() -> Unit)? = null

    fun update(
        shape: Shape,
        values: List<Shadow>,
    ) {
        if (this.shape == shape && this.values == values) return
        this.shape = shape
        this.values = values
        invalidate?.invoke()
    }

    private var painterShape: Shape? = null
    private var painterValues: List<Shadow> = emptyList()
    private var painters: List<DropShadowPainter> = emptyList()

    fun draw(scope: ContentDrawScope): Unit =
        with(scope) {
            if (values.isEmpty()) return
            if (painterShape != shape || painterValues != values) {
                painterShape = shape
                painterValues = values
                painters = values.map { DropShadowPainter(shape, it) }
            }
            val interior = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@with)) }
            clipPath(interior, ClipOp.Difference) {
                painters.forEach { painter -> with(painter) { draw(size) } }
            }
        }
}

private class CssShadowElement(private val shadow: CssOuterShadow) : ModifierNodeElement<CssShadowNode>() {
    override fun create() = CssShadowNode(shadow)

    override fun update(node: CssShadowNode) {
        node.update(shadow)
    }

    override fun equals(other: Any?) = other is CssShadowElement && other.shadow === shadow

    override fun hashCode() = shadow.hashCode()

    override fun InspectorInfo.inspectableProperties() {
        name = "cssOuterShadow"
    }
}

private class CssShadowNode(private var shadow: CssOuterShadow) : Modifier.Node(), DrawModifierNode {
    private val invalidateShadow: () -> Unit = { invalidateDraw() }

    override fun onAttach() {
        shadow.invalidate = invalidateShadow
    }

    override fun onDetach() {
        shadow.invalidate = null
    }

    fun update(value: CssOuterShadow) {
        if (shadow === value) return
        shadow.invalidate = null
        shadow = value
        if (isAttached) shadow.invalidate = invalidateShadow
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        shadow.draw(this)
    }
}
