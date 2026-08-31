package com.toutiaoyan.compose.ui.styles

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.pressed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Press bounce: nudges the component down then springs back with overshoot
 * ([Spring.DampingRatioLowBouncy]). Draw-only via [translationY] — layout and hit
 * bounds stay put.
 *
 * Distinct from [pressedMoveDown] (tiny fixed nudge, tween) and [pressedScale].
 *
 * @param pushDown Pixel offset while fully pressed (default `4.dp`).
 */
@OptIn(ExperimentalFoundationStyleApi::class)
fun StyleScope.pressedBounce(pushDown: Dp = 4.dp) {
    val bounce =
        spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        )
    animate(
        toSpec = bounce,
        fromSpec = bounce,
    ) {
        pressed {
            translationY(pushDown.toPx())
        }
    }
}
