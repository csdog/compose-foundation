@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleState
import androidx.compose.foundation.style.then
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import com.toutiaoyan.compose.ui.styles.cssStyleable

/**
 * Apply Button chrome to another composable without assigning Button semantics or click behavior.
 * The caller supplies content, link/focus behavior and optional interaction Style state.
 */
@Composable
fun Modifier.buttonStyle(
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
    styleState: StyleState? = null,
): Modifier =
    buttonCssStyleable(
        styleState,
        variant.rememberStyle() then size.rememberStyle() then style,
        nova = currentTheme.activePreset == StylePreset.Nova,
    )

/** Paint the Nova background inside its border, within Style's opacity/transform layer. */
@Composable
internal fun Modifier.buttonCssStyleable(
    state: StyleState?,
    style: Style,
    nova: Boolean,
): Modifier {
    if (!nova) return cssStyleable(state, style)
    val background = remember { ButtonPaddingBackground() }
    val wrapped =
        remember(style) {
            Style {
                val target = this
                var shape: Shape = RectangleShape
                var brush: Brush? = null
                var border = Dp.Unspecified
                var left = 0.dp
                var top = 0.dp
                var right = 0.dp
                var bottom = 0.dp
                val receiver =
                    object : StyleScope by target {
                        override fun shape(value: Shape) {
                            shape = value
                            target.shape(value)
                        }

                        override fun background(color: Color) {
                            brush = SolidColor(color)
                            target.background(Color.Transparent)
                        }

                        override fun background(value: Brush) {
                            brush = value
                            target.background(Color.Transparent)
                        }

                        override fun borderWidth(value: Dp) {
                            border = value
                            target.borderWidth(value)
                        }

                        override fun contentPaddingStart(value: Dp) {
                            left = value
                            target.contentPaddingStart(value)
                        }

                        override fun contentPaddingTop(value: Dp) {
                            top = value
                            target.contentPaddingTop(value)
                        }

                        override fun contentPaddingEnd(value: Dp) {
                            right = value
                            target.contentPaddingEnd(value)
                        }

                        override fun contentPaddingBottom(value: Dp) {
                            bottom = value
                            target.contentPaddingBottom(value)
                        }
                    }
                with(style) { receiver.applyStyle() }
                background.update(ButtonBackgroundData(shape, brush, border, left, top, right, bottom))
            }
        }
    // This draw node is inside Style's content padding. Offset back to the chrome;
    // Style still owns all real measurement, alpha, transforms, borders and shadows.
    return cssStyleable(state, wrapped).then(ButtonBackgroundElement(background))
}

private data class ButtonBackgroundData(
    val shape: Shape,
    val brush: Brush?,
    val border: Dp,
    val left: Dp,
    val top: Dp,
    val right: Dp,
    val bottom: Dp,
)

private class ButtonPaddingBackground {
    private var data: ButtonBackgroundData? = null
    var invalidate: (() -> Unit)? = null
    private val layerPaint = Paint()

    fun update(value: ButtonBackgroundData) {
        if (data == value) return
        data = value
        invalidate?.invoke()
    }

    fun draw(scope: ContentDrawScope): Unit =
        with(scope) {
            val value = data ?: return
            val brush = value.brush ?: return
            // Match Foundation ResolvedStyle: padding rounds, borders ceil (Hairline is one pixel).
            val border =
                when (value.border) {
                    Dp.Unspecified -> 0f
                    Dp.Hairline -> 1f
                    else -> ceil(value.border.toPx())
                }
            val left = value.left.roundToPx().toFloat()
            val top = value.top.roundToPx().toFloat()
            val width = size.width + left + value.right.roundToPx() + border * 2
            val height = size.height + top + value.bottom.roundToPx() + border * 2
            if (width <= 0f || height <= 0f) return
            val outline = value.shape.createOutline(Size(width, height), layoutDirection, this@with)
            val path = Path().apply { addOutline(outline) }
            translate(-left - border, -top - border) {
                val canvas = drawContext.canvas
                canvas.saveLayer(Rect(0f, 0f, width, height), layerPaint)
                try {
                    drawPath(path, brush)
                    if (border > 0f) {
                        // Clear only this background layer, never the parent's pixels.
                        drawPath(
                            path,
                            Color.Transparent,
                            style = Stroke(width = border * 2),
                            blendMode = BlendMode.Clear,
                        )
                    }
                } finally {
                    canvas.restore()
                }
            }
        }
}

private class ButtonBackgroundElement(
    private val value: ButtonPaddingBackground,
) : ModifierNodeElement<ButtonBackgroundNode>() {
    override fun create() = ButtonBackgroundNode(value)

    override fun update(node: ButtonBackgroundNode) {
        node.update(value)
    }

    override fun equals(other: Any?) = other is ButtonBackgroundElement && value === other.value

    override fun hashCode() = value.hashCode()

    override fun InspectorInfo.inspectableProperties() {
        name = "buttonPaddingBackground"
    }
}

private class ButtonBackgroundNode(
    private var value: ButtonPaddingBackground,
) : Modifier.Node(), DrawModifierNode {
    private val invalidateBackground: () -> Unit = { invalidateDraw() }

    override fun onAttach() {
        value.invalidate = invalidateBackground
    }

    override fun onDetach() {
        value.invalidate = null
    }

    fun update(next: ButtonPaddingBackground) {
        if (value === next) return
        value.invalidate = null
        value = next
        if (isAttached) value.invalidate = invalidateBackground
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        value.draw(this)
        drawContent()
    }
}
