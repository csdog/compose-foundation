package com.toutiaoyan.compose.ui.modifiers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import kotlin.math.hypot
import kotlin.math.max
import kotlinx.coroutines.launch

private val DefaultRippleRadius = Dp.Unspecified
private const val RippleAlpha = 0.16f
private const val StateLayerAlpha = 0.08f
private const val RadiusInMs = 225
private const val FadeInMs = 75
private const val FadeOutMs = 150

/**
 * Material-style bounded ripple for [androidx.compose.foundation.LocalIndication].
 *
 * Draw-only: expanding circle from the press point. When [bounded], clipped to the
 * layout bounds. The component (or caller's `Modifier.clip(shape)`) clips the ripple to
 * its visual shape. A draw context's graphics-layer outline can belong to an ancestor
 * and must not be treated as this component's outline.
 * Color defaults to [LocalContentColor] (else black).
 */
data class RippleIndication(
    val bounded: Boolean = true,
    val color: Color = Color.Unspecified,
    val radius: Dp = DefaultRippleRadius,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        RippleIndicationNode(interactionSource, bounded, color, radius)
}

/** Default factory used by [com.toutiaoyan.compose.ui.theme.Theme] as [androidx.compose.foundation.LocalIndication]. */
val DefaultRippleIndication: IndicationNodeFactory = RippleIndication()

fun ripple(
    bounded: Boolean = true,
    color: Color = Color.Unspecified,
    radius: Dp = DefaultRippleRadius,
): IndicationNodeFactory = RippleIndication(bounded, color, radius)

fun Modifier.rippleIndication(
    interactionSource: InteractionSource,
    bounded: Boolean = true,
    color: Color = Color.Unspecified,
    radius: Dp = DefaultRippleRadius,
): Modifier = this.indication(interactionSource, ripple(bounded, color, radius))

private class RippleIndicationNode(
    private val interactionSource: InteractionSource,
    private val bounded: Boolean,
    private val color: Color,
    private val radius: Dp,
) : Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {
    private val ripples = mutableMapOf<PressInteraction.Press, RippleWave>()

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> startRipple(interaction)
                    is PressInteraction.Release -> finishRipple(interaction.press)
                    is PressInteraction.Cancel -> finishRipple(interaction.press)
                }
            }
        }
    }

    private fun startRipple(press: PressInteraction.Press) {
        val wave = RippleWave(origin = press.pressPosition)
        ripples[press] = wave
        coroutineScope.launch {
            snapshotFlow { wave.radius.value to wave.alpha.value }.collect { invalidateDraw() }
        }
        coroutineScope.launch {
            wave.alpha.animateTo(1f, tween(FadeInMs, easing = FastOutSlowInEasing))
        }
        coroutineScope.launch {
            wave.radius.animateTo(1f, tween(RadiusInMs, easing = FastOutSlowInEasing))
        }
    }

    private fun finishRipple(press: PressInteraction.Press) {
        val wave = ripples[press] ?: return
        if (wave.finishing) return
        wave.finishing = true
        coroutineScope.launch {
            wave.alpha.animateTo(0f, tween(FadeOutMs, easing = FastOutSlowInEasing))
            ripples.remove(press)
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        val tint =
            color.takeOrElse {
                currentValueOf(LocalContentColor).takeOrElse { Color.Black }
            }
        if (ripples.isEmpty()) return
        if (bounded) {
            clipRect { drawRipples(tint) }
        } else {
            drawRipples(tint)
        }
    }

    private fun DrawScope.drawRipples(tint: Color) {
        val waves = ripples.values
        val layer = waves.maxOf { it.alpha.value } * StateLayerAlpha
        if (layer > 0f) {
            drawRect(tint.copy(alpha = layer))
        }
        val endRadius =
            if (radius != Dp.Unspecified) {
                radius.toPx()
            } else {
                null
            }
        ripples.values.forEach { wave ->
            val origin = wave.origin.coerceIn(size)
            val maxRadius = endRadius ?: origin.distanceToFarthestCorner(size)
            val startRadius = maxRadius * 0.3f
            val r = startRadius + (maxRadius - startRadius) * wave.radius.value
            drawCircle(
                color = tint.copy(alpha = RippleAlpha * wave.alpha.value),
                radius = r,
                center = origin,
            )
        }
    }
}

private class RippleWave(
    val origin: Offset,
) {
    val radius = Animatable(0f)
    val alpha = Animatable(0f)
    var finishing = false
}

private fun Offset.coerceIn(size: Size): Offset {
    if (x in 0f..size.width && y in 0f..size.height) return this
    return Offset(size.width / 2f, size.height / 2f)
}

private fun Offset.distanceToFarthestCorner(size: Size): Float {
    val dx = max(x, size.width - x)
    val dy = max(y, size.height - y)
    return hypot(dx, dy)
}
