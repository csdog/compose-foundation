package com.toutiaoyan.compose.ui.modifiers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.launch

internal val DefaultPressInSpec: AnimationSpec<Float> =
    tween(durationMillis = 90, easing = FastOutSlowInEasing)

internal val DefaultPressOutSpec: AnimationSpec<Float> =
    tween(durationMillis = 160, easing = FastOutSlowInEasing)

/** Shared press 0→1 driver for draw-only press indications. */
internal abstract class DrawIndicationNode(
    private val interactionSource: InteractionSource,
    private val pressSpec: AnimationSpec<Float> = DefaultPressInSpec,
    private val releaseSpec: AnimationSpec<Float> = DefaultPressOutSpec,
) : Modifier.Node(),
    DrawModifierNode {
    protected val progress = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press ->
                        launch {
                            progress.animateTo(1f, pressSpec)
                        }

                    is PressInteraction.Release,
                    is PressInteraction.Cancel,
                    ->
                        launch {
                            progress.animateTo(0f, releaseSpec)
                        }
                }
            }
        }
        coroutineScope.launch {
            snapshotFlow { progress.value }.collect { invalidateDraw() }
        }
    }
}
