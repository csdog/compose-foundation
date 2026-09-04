package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.TwShadow

/**
 * Tailwind's `shadow-*` utilities. [androidx.compose.foundation.style.ShadowScope] has no
 * "elevation" concept the way [com.toutiaoyan.compose.tailwind.modifiers.shadow] does (Android's
 * ambient/spot shadow model, a single [Dp] driving both blur and offset together) -- it only
 * offers `dropShadow`/`innerShadow`, CSS box-shadow's own model (independent radius/spread/
 * offset/color). [radius] here is used as the blur radius, not elevation -- a different but
 * comparably-weighted visual analogue, not a unit conversion of [TwShadow]'s existing
 * elevation values.
 */

fun StyleScope.shadow(
    radius: Dp,
    color: Color = Color.Black.copy(alpha = 0.25f),
) {
    dropShadow(Shadow(radius = radius, color = color))
}

fun StyleScope.shadowXs() {
    shadow(TwShadow.xs)
}

fun StyleScope.shadowMd() {
    shadow(TwShadow.md)
}

fun StyleScope.shadowLg() {
    shadow(TwShadow.lg)
}

fun StyleScope.shadowXl() {
    shadow(TwShadow.xl)
}
