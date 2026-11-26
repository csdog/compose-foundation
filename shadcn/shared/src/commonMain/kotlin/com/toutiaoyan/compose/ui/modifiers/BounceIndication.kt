package com.toutiaoyan.compose.ui.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DelegatableNode

/**
 * Press bounce: nudges content down by a fraction of its height, with a low-bouncy
 * spring so press and release overshoot. Draw-only — layout and hit bounds stay put.
 *
 * Distinct from [MoveDownIndication] (tiny fixed dp nudge) and [ScaleIndication]
 * (uniform scale-down).
 */
private val BounceSpring =
    spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )

/** Downward travel while fully pressed, as a fraction of the node's height. */
private const val BounceHeightFraction = 0.08f

data object BounceIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        BounceIndicationNode(
            interactionSource,
            heightFraction = BounceHeightFraction,
            pressSpec = BounceSpring,
            releaseSpec = BounceSpring,
        )
}

fun Modifier.bounceIndication(interactionSource: InteractionSource): Modifier =
    this.indication(interactionSource, BounceIndication)

internal class BounceIndicationNode(
    interactionSource: InteractionSource,
    private val heightFraction: Float,
    pressSpec: AnimationSpec<Float> = DefaultPressInSpec,
    releaseSpec: AnimationSpec<Float> = DefaultPressOutSpec,
) : DrawIndicationNode(interactionSource, pressSpec, releaseSpec) {
    override fun ContentDrawScope.draw() {
        val top = heightFraction * progress.value * size.height
        translate(top = top) { this@draw.drawContent() }
    }
}
