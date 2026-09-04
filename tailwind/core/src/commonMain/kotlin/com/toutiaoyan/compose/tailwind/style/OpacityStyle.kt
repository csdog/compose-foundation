package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope

/** Tailwind's `opacity-*` utilities via [androidx.compose.foundation.style.AlphaScope.alpha]. */

fun StyleScope.opacity(value: Float) {
    alpha(value)
}

fun StyleScope.opacity0() {
    opacity(0.00f)
}

fun StyleScope.opacity50() {
    opacity(0.50f)
}

fun StyleScope.opacity100() {
    opacity(1.00f)
}
