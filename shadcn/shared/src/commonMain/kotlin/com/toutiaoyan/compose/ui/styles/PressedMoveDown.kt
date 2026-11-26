package com.toutiaoyan.compose.ui.styles

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.pressed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A tactile "push" affordance: nudges the component down slightly while held down,
 * springing back to its resting position on release -- reads as the component being
 * physically pressed into the surface, not just dimmed. Real shadcn/ui's `button.tsx` has
 * no active-state transform (verified directly against the source CSS -- there's no
 * `active:translate-*` class anywhere in it), so this is a deliberate enhancement over the
 * literal port, not a translated class -- the same kind of tactile feedback native
 * platforms give for free (iOS `UIButton` highlight, Material's press ripple) and that a
 * flat color-only hover/press state lacks.
 *
 * Press-in is a short FastOutSlowIn tween; release is a slightly longer ease so the
 * bounce-back reads softer than the downstroke.
 *
 * [pushDown] is a raw pixel offset via [TranslationScope.translationY][
 * androidx.compose.foundation.style.TranslationScope.translationY], not a layout inset --
 * it moves the drawn component without affecting siblings or its own layout bounds.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
fun StyleScope.pressedMoveDown(pushDown: Dp = 0.6.dp) {
    animate(
        toSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        fromSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
    ) {
        pressed {
            translationY(pushDown.toPx())
        }
    }
}
