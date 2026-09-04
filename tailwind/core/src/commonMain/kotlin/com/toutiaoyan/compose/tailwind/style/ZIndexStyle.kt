package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope

/**
 * Tailwind's `z-*` stacking-order utilities via
 * [androidx.compose.foundation.style.ZIndexScope.zIndex] -- no `tailwind-effects` Modifier
 * equivalent exists yet (it was never ported there), so this is the first `z-*` implementation
 * anywhere in tailwind-compose, not a Style-API port of an existing one.
 */

fun StyleScope.zIndex0() {
    zIndex(0f)
}

fun StyleScope.zIndex10() {
    zIndex(10f)
}

fun StyleScope.zIndex50() {
    zIndex(50f)
}
