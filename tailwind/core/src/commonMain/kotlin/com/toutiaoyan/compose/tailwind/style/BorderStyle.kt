package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius

/**
 * Tailwind's `border-*` width utilities via [androidx.compose.foundation.style.BorderScope]
 * (`borderWidth`/`borderColor`) -- unlike [com.toutiaoyan.compose.tailwind.modifiers.border]'s
 * `shape` parameter, `BorderScope`'s border is always clipped to whatever
 * [androidx.compose.foundation.style.ShapeScope.shape] the same [Style] sets (see [rounded]
 * below), so there's no separate shape argument to thread through here.
 *
 * [width] is required (no default) so this does not clash with shadcn-compose's
 * one-arg `StyleScope.border(color)` (fixed 1.dp).
 */

fun StyleScope.border(
    color: Color,
    width: Dp,
) {
    borderWidth(width)
    borderColor(color)
}

fun StyleScope.border0(color: Color) {
    border(color, 0.dp)
}

fun StyleScope.border2(color: Color) {
    border(color, 2.dp)
}

fun StyleScope.border4(color: Color) {
    border(color, 4.dp)
}

fun StyleScope.border8(color: Color) {
    border(color, 8.dp)
}

/**
 * Tailwind's `rounded-*` corner-radius utilities via
 * [androidx.compose.foundation.style.ShapeScope.shape] -- this shape also clips
 * [androidx.compose.foundation.style.BackgroundScope.background] and [border] in the same
 * [Style], per `ShapeScope.shape`'s own KDoc, so setting it once here covers all three.
 *
 * Reads [LocalTwRadius] at consume time (same pattern as Theme tokens in Style blocks).
 */

fun StyleScope.rounded(radius: Dp) {
    shape(RoundedCornerShape(radius))
}

fun StyleScope.roundedNone() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.none))
}

fun StyleScope.roundedXs() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.xs))
}

fun StyleScope.roundedSm() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.sm))
}

fun StyleScope.roundedMd() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.md))
}

fun StyleScope.roundedLg() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.lg))
}

fun StyleScope.roundedXl() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.xl))
}

fun StyleScope.roundedXl2() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.xl2))
}

fun StyleScope.roundedXl3() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.xl3))
}

fun StyleScope.roundedXl4() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.xl4))
}

fun StyleScope.roundedFull() {
    shape(RoundedCornerShape(LocalTwRadius.currentValue.full))
}
