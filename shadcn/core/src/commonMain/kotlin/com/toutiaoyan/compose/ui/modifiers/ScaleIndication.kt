package com.toutiaoyan.compose.ui.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode

/**
 * RikkaUi Scale: subtle press scale (0.97) with a medium-bouncy, medium-low
 * spring. Draw-only — layout and hit bounds stay put.
 */
private val ScaleSpring =
    spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

data object ScaleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ScaleIndicationNode(
            interactionSource,
            scale = 0.97f,
            pressSpec = ScaleSpring,
            releaseSpec = ScaleSpring,
        )
}

fun Modifier.scaleIndication(interactionSource: InteractionSource): Modifier =
    this.indication(interactionSource, ScaleIndication)


internal class ScaleIndicationNode(
    interactionSource: InteractionSource,
    private val scale: Float,
    pressSpec: AnimationSpec<Float> = DefaultPressInSpec,
    releaseSpec: AnimationSpec<Float> = DefaultPressOutSpec,
) : DrawIndicationNode(interactionSource, pressSpec, releaseSpec) {
    override fun ContentDrawScope.draw() {
        val s = 1f - (1f - scale) * progress.value
        scale(s) { this@draw.drawContent() }
    }
}
