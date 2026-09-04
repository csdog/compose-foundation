package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope

/**
 * Tailwind's `animate-*` utilities as [StyleScope] extensions, mirroring
 * [com.toutiaoyan.compose.tailwind.modifiers.animateSpin] et al.
 *
 * Motion values must be remembered in composition and passed in: [StyleScope] is not a
 * `@Composable` receiver, so [com.toutiaoyan.compose.tailwind.core.TwAnimate] drivers stay
 * at the call site. Reading those values *inside* the Style block still only invalidates
 * layout/draw.
 *
 * `animate-bounce` is intentionally omitted: Style's `translationY` is a fixed [Float]
 * with no access to measured `size.height`, so it can't express Modifier's
 * `fraction * size.height` bounce without the caller supplying [height].
 */

fun StyleScope.animateSpin(degrees: Float) {
    rotationZ(degrees)
}

fun StyleScope.animatePulse(pulseAlpha: Float) {
    alpha(pulseAlpha)
}

fun StyleScope.animatePing(
    scale: Float,
    alpha: Float,
) {
    scaleX(scale)
    scaleY(scale)
    alpha(alpha)
}

fun StyleScope.animateBounce(
    fraction: Float,
    height: Float,
) {
    translationY(fraction * height)
}
