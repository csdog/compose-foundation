package com.toutiaoyan.compose.tailwind.style

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import com.toutiaoyan.compose.tailwind.core.TwDuration
import com.toutiaoyan.compose.tailwind.core.TwEasing

/**
 * Tailwind's `transition-* duration-* ease-*` combo via
 * [androidx.compose.foundation.style.AnimateStyleScope.animate] -- a genuinely different
 * mechanism from [com.toutiaoyan.compose.tailwind.modifiers.transitionAllDuration150]'s
 * `animateContentSize`, which only animates *size* changes. `AnimateStyleScope.animate` wraps any
 * [StyleScope] property writes inside [block] so they animate automatically whenever they change
 * as a result of [androidx.compose.foundation.style.StyleState] (hover/press/focus) driving a
 * different branch of the style -- there's nothing to animate from a single static value the way
 * the other `xxx()` functions in this module apply one. Reuses
 * [com.toutiaoyan.compose.tailwind.core.TwDuration]/[TwEasing] for the same
 * Tailwind-matching timing tokens the Modifier-based version uses.
 */

fun StyleScope.transition(
    durationMs: Int = TwDuration.D150,
    easing: Easing = TwEasing.easeInOut,
    block: StyleScope.() -> Unit,
) {
    // The 2-arg `animate(spec, block)` extension delegates to this same-spec-twice
    // call internally -- called directly here since Kotlin's member-over-extension
    // overload resolution otherwise prefers this class's 3-arg animate(toSpec,
    // fromSpec, block) member and misassigns the trailing lambda, leaving fromSpec
    // unfilled.
    val spec = tween<Float>(durationMs, easing = easing)
    animate(spec, spec) { block() }
}
