package com.toutiaoyan.compose.tailwind.core

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailwind v4 `shadow-*` scale, approximated as Compose elevation (a single Dp
 * driving Android's ambient/spot shadow model). CSS `box-shadow` supports
 * arbitrary offset/blur/spread/color per layer, which Compose's
 * [androidx.compose.ui.draw.shadow] has no equivalent for — this is a visual
 * approximation, not a faithful reproduction. `shadow-inner` has no Compose
 * primitive and is intentionally omitted.
 *
 * Names match v4: `shadow-2xs` → [xs2], `shadow-xs` → [xs], `shadow-sm` → [sm],
 * `shadow-2xl` → [xl2]. There is no bare/DEFAULT token; v3's unnamed `shadow`
 * is v4's [sm].
 */
object TwShadow {
    val none: Dp = 0.dp

    /** v4 `shadow-2xs`. */
    val xs2: Dp = 0.5.dp

    /** v4 `shadow-xs`. */
    val xs: Dp = 1.dp

    /** v4 `shadow-sm`. */
    val sm: Dp = 3.dp

    val md: Dp = 6.dp
    val lg: Dp = 10.dp
    val xl: Dp = 15.dp

    /** v4 `shadow-2xl`. */
    val xl2: Dp = 25.dp
}
