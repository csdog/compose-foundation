package com.toutiaoyan.compose.tailwind.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import com.toutiaoyan.compose.tailwind.core.TwAnimate

/**
 * Tailwind's `animate-spin` utility — continuous Z rotation, same as CSS
 * `animation: spin 1s linear infinite`.
 *
 * ```
 * // web: className="animate-spin"
 * Modifier.animateSpin()
 * ```
 */
@Composable
fun Modifier.animateSpin(): Modifier {
    val degrees = TwAnimate.rememberSpinState()
    return this.graphicsLayer { rotationZ = degrees.value }
}

/**
 * Tailwind's `animate-pulse` utility — opacity oscillates `1 ↔ 0.5` on a 2s loop,
 * same as CSS `animation: pulse 2s cubic-bezier(.4, 0, .6, 1) infinite`.
 *
 * ```
 * // web: className="animate-pulse"
 * Modifier.animatePulse()
 * ```
 */
@Composable
fun Modifier.animatePulse(): Modifier {
    val pulseAlpha = TwAnimate.rememberPulse()
    return this.alpha(pulseAlpha)
}

/**
 * Tailwind's `animate-ping` utility — scale `1→2` and fade out over 1s, same as
 * CSS `animation: ping 1s cubic-bezier(0, 0, .2, 1) infinite`.
 *
 * ```
 * // web: className="animate-ping"
 * Modifier.animatePing()
 * ```
 */
@Composable
fun Modifier.animatePing(): Modifier {
    val ping = TwAnimate.rememberPing()
    return this.graphicsLayer {
        scaleX = ping.scale
        scaleY = ping.scale
        alpha = ping.alpha
    }
}

/**
 * Tailwind's `animate-bounce` utility — vertical bounce of 25% height, same as
 * CSS `animation: bounce 1s infinite` (`translateY(-25%)` ↔ `none`).
 *
 * ```
 * // web: className="animate-bounce"
 * Modifier.animateBounce()
 * ```
 */
@Composable
fun Modifier.animateBounce(): Modifier {
    val fraction = TwAnimate.rememberBounce()
    return this.graphicsLayer {
        translationY = fraction * size.height
    }
}
