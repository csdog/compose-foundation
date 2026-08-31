package com.toutiaoyan.compose.ui.modifiers

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.unit.dp

/** Nudge down on press. Draw-only — layout and hit bounds stay put. */
data object MoveDownIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        MoveDownIndicationNode(interactionSource)
}

fun Modifier.moveDownIndication(interactionSource: InteractionSource): Modifier =
    this.indication(interactionSource, MoveDownIndication)

private class MoveDownIndicationNode(
    interactionSource: InteractionSource,
) : DrawIndicationNode(interactionSource) {
    override fun ContentDrawScope.draw() {
        translate(top = 0.6.dp.toPx() * progress.value) { this@draw.drawContent() }
    }
}
