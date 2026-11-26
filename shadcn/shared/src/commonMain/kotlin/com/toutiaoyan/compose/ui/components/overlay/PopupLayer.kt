package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.offset
import com.toutiaoyan.compose.tailwind.core.TwShadow

// Reserve room for the built-in outer shadows inside the offscreen alpha layer.
private val PopupShadowSpace = TwShadow.xl2

internal fun Modifier.popupGraphicsLayer(block: GraphicsLayerScope.() -> Unit): Modifier =
    layout { measurable, constraints ->
        val space = PopupShadowSpace.roundToPx()
        val extra = space * 2
        val layer = measurable.measure(constraints.offset(extra, extra))
        layout(layer.width - extra, layer.height - extra) {
            layer.place(-space, -space)
        }
    }.graphicsLayer {
        block()
        val space = PopupShadowSpace.roundToPx()
        transformOrigin =
            TransformOrigin(
                expandPivot(transformOrigin.pivotFractionX, size.width, space),
                expandPivot(transformOrigin.pivotFractionY, size.height, space),
            )
    }.padding(PopupShadowSpace)

private fun expandPivot(
    fraction: Float,
    layerSize: Float,
    space: Int,
): Float = (space + fraction * (layerSize - space * 2)) / layerSize

@Composable
internal fun AnimatedVisibilityScope.popupFadeModifier(
    defaultEnter: Boolean,
    defaultExit: Boolean,
): Modifier {
    if (!defaultEnter && !defaultExit) return Modifier

    val opacity =
        transition.animateFloat(
            transitionSpec = {
                val fading =
                    when (targetState) {
                        EnterExitState.PreEnter, EnterExitState.Visible -> defaultEnter
                        EnterExitState.PostExit -> defaultExit
                    }
                tween(if (fading) OverlayAnimMs else 0)
            },
            label = "Popup opacity",
        ) { state ->
            when (state) {
                EnterExitState.PreEnter -> if (defaultEnter) 0f else 1f
                EnterExitState.Visible -> 1f
                EnterExitState.PostExit -> if (defaultExit) 0f else 1f
            }
        }
    return Modifier.popupGraphicsLayer { alpha = opacity.value }
}
