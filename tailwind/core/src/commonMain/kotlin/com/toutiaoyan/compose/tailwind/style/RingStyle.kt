package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailwind's `ring-*` utilities (CSS `box-shadow: 0 0 0 <width> <color>`), built on the real
 * Compose Style API rather than [com.toutiaoyan.compose.tailwind.modifiers.ring]'s
 * hand-rolled `drawWithCache`/negative-inset technique.
 *
 * [androidx.compose.foundation.style.ShadowScope.dropShadow] takes a [Shadow] with a `spread`
 * parameter -- CSS box-shadow's actual spread radius -- which is a more faithful, first-party
 * way to express a ring than expanding and stroking a shape outline by hand: `radius = 0.dp`
 * (no blur), `spread = width` (grows the shadow geometry outward by exactly [width], matching
 * Tailwind's `ring-*` scale), `offset = DpOffset.Zero` (centered, not cast to one side).
 */

fun StyleScope.ring(
    color: Color,
    width: Dp = 1.dp,
) {
    dropShadow(ringShadow(color, width))
}

/**
 * The raw [Shadow] value [ring] wraps, exposed on its own for callers that need the ring
 * *inside* a state block (`focused { dropShadow(ringShadow(color, width)) }`) instead of
 * unconditionally -- [ring] itself always applies, so it can't express "only on focus"
 * without wrapping call sites in their own `Style { focused { ... } }`, which defeats the point
 * of a reusable helper. This is that lower-level building block.
 */

fun ringShadow(
    color: Color,
    width: Dp = 1.dp,
): Shadow = Shadow(radius = 0.dp, spread = width, color = color)

fun StyleScope.ring0(color: Color) {
    ring(color, 0.dp)
}

fun StyleScope.ring2(color: Color) {
    ring(color, 2.dp)
}

fun StyleScope.ring4(color: Color) {
    ring(color, 4.dp)
}

fun StyleScope.ring8(color: Color) {
    ring(color, 8.dp)
}
