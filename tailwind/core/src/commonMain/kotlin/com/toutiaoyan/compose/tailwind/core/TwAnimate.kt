package com.toutiaoyan.compose.tailwind.core

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

/**
 * Tailwind's built-in `--animate-*` tokens: specs + remember drivers.
 *
 * Prefer `Modifier.animateSpin()` / `animatePulse()` / `animatePing()` /
 * `animateBounce()` at call sites (`className="animate-*"` equivalent).
 *
 * CSS theme tokens (Tailwind v4 `theme.css`):
 * - `--animate-spin`: `spin 1s linear infinite`
 * - `--animate-ping`: `ping 1s cubic-bezier(0, 0, .2, 1) infinite`
 * - `--animate-pulse`: `pulse 2s cubic-bezier(.4, 0, .6, 1) infinite`
 * - `--animate-bounce`: `bounce 1s infinite`
 */
@Suppress("MagicNumber")
object TwAnimate {
    /**
     * Tailwind `animate-spin` / `--animate-spin`: `spin 1s linear infinite`.
     * Animate a Float from `0f` to `360f` (degrees).
     */
    val spin: InfiniteRepeatableSpec<Float> =
        infiniteRepeatable(
            animation = tween(durationMillis = TwDuration.D1000, easing = TwEasing.linear),
        )

    /**
     * Tailwind `animate-pulse` / `--animate-pulse`:
     * `pulse 2s cubic-bezier(.4, 0, .6, 1) infinite`.
     *
     * CSS keyframes hit opacity `.5` at 50%; Compose approximates that as a 1s
     * half-cycle with [RepeatMode.Reverse] so the full loop is [TwDuration.D2000].
     * Animate a Float from `1f` to `0.5f`.
     */
    val pulse: InfiniteRepeatableSpec<Float> =
        infiniteRepeatable(
            animation = tween(durationMillis = TwDuration.D1000, easing = TwEasing.pulse),
            repeatMode = RepeatMode.Reverse,
        )

    /**
     * Tailwind `animate-ping` progress `0f → 1f` over 1s ([TwEasing.easeOut]).
     * Hits `1f` at 75% and holds through 100%, matching CSS
     * `transform: scale(2); opacity: 0` at `75%, 100%`.
     *
     * Map with [Ping]: `scale = 1 + t`, `alpha = 1 - t`. Prefer [rememberPing].
     */
    val ping: InfiniteRepeatableSpec<Float> =
        infiniteRepeatable(
            animation =
                keyframes {
                    durationMillis = TwDuration.D1000
                    0f at 0
                    1f at 750 using TwEasing.easeOut
                    1f at TwDuration.D1000
                },
        )

    /**
     * Tailwind `animate-bounce` / `--animate-bounce`: `bounce 1s infinite`.
     * Translation as a fraction of element height: `-0.25f` ↔ `0f`
     * (`translateY(-25%)` ↔ `none`), with the CSS keyframe easings.
     */
    val bounce: InfiniteRepeatableSpec<Float> =
        infiniteRepeatable(
            animation =
                keyframes {
                    durationMillis = TwDuration.D1000
                    (-0.25f) at 0
                    // 0% → 50%: easing from the 0%/100% keyframe
                    0f at 500 using CubicBezierEasing(0.8f, 0f, 1f, 1f)
                    // 50% → 100%: easing from the 50% keyframe (= ease-out)
                    (-0.25f) at TwDuration.D1000 using TwEasing.easeOut
                },
        )

    /** Mapped `animate-ping` values: scale `1→2`, alpha `1→0`. */
    data class Ping(
        val scale: Float,
        val alpha: Float,
    )

    /** Running spin state; defer `.value` reads to layout/draw for frame-rate consumers. */
    @Composable
    fun rememberSpinState(label: String = "animate-spin"): State<Float> {
        val transition = rememberInfiniteTransition(label = label)
        return transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = spin,
            label = label,
        )
    }

    /** Value-form driver retained for callers that render the angle in composition. */
    @Composable
    fun rememberSpin(label: String = "animate-spin"): Float = rememberSpinState(label).value

    /** Running `animate-pulse` value: opacity `1f → 0.5f`. */
    @Composable
    fun rememberPulse(label: String = "animate-pulse"): Float {
        val transition = rememberInfiniteTransition(label = label)
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.5f,
            animationSpec = pulse,
            label = label,
        )
        return alpha
    }

    /** Running `animate-ping` values: [Ping.scale] `1→2`, [Ping.alpha] `1→0`. */
    @Composable
    fun rememberPing(label: String = "animate-ping"): Ping {
        val transition = rememberInfiniteTransition(label = label)
        val t by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = ping,
            label = label,
        )
        return Ping(scale = 1f + t, alpha = 1f - t)
    }

    /**
     * Running `animate-bounce` value: height fraction `-0.25f ↔ 0f`
     * (`translationY = value * height`).
     */
    @Composable
    fun rememberBounce(label: String = "animate-bounce"): Float {
        val transition = rememberInfiniteTransition(label = label)
        val fraction by transition.animateFloat(
            initialValue = -0.25f,
            targetValue = -0.25f,
            animationSpec = bounce,
            label = label,
        )
        return fraction
    }
}
