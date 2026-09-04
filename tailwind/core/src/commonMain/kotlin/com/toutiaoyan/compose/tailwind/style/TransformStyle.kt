package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * Tailwind's `scale-*` uniform-scale utilities via [androidx.compose.foundation.style.StyleScope.scale].
 * Percentage-based, matching [com.toutiaoyan.compose.tailwind.modifiers.scale100] et al.'s convention
 * (`1.5f` == `scale-150`) rather than the 0..1 fraction [androidx.compose.foundation.style.AlphaScope.alpha] uses.
 */

fun StyleScope.scale50() {
    scale(0.50f)
}

fun StyleScope.scale100() {
    scale(1.00f)
}

fun StyleScope.scale150() {
    scale(1.50f)
}

/**
 * Tailwind's `rotate-x-*`/`rotate-y-*`/`rotate-z-*` 3D rotation utilities via
 * [androidx.compose.foundation.style.RotationScope]. Representative 45/90/180-degree presets per
 * axis, matching [com.toutiaoyan.compose.tailwind.modifiers.rotateX45] et al.'s "representative
 * subset, not the full ~54-combination scale" precedent.
 */

fun StyleScope.rotateX(degrees: Float) {
    rotationX(degrees)
}

fun StyleScope.rotateY(degrees: Float) {
    rotationY(degrees)
}

fun StyleScope.rotateZ(degrees: Float) {
    rotationZ(degrees)
}

/**
 * Tailwind's `translate-x-*`/`translate-y-*` utilities via
 * [androidx.compose.foundation.style.TranslationScope] -- backed by the same [twSpacing] scale as
 * padding/margin/gap, matching [com.toutiaoyan.compose.tailwind.modifiers.translateX1] et al.'s
 * precedent (Tailwind's own `translate-*` resolves against the `--spacing` theme scale). Takes a
 * [Dp] rather than a raw pixel [Float] like the underlying `TranslationScope` does -- translated
 * to px at the call site via [Dp.value] is a common enough mismatch to hide here.
 */

fun StyleScope.translateX(value: Dp) {
    translationX(value.value)
}

fun StyleScope.translateY(value: Dp) {
    translationY(value.value)
}

fun StyleScope.translateX4() {
    translationX(twSpacing.scale4.value)
}

fun StyleScope.translateY4() {
    translationY(twSpacing.scale4.value)
}
