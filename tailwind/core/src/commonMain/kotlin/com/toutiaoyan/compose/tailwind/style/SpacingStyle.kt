package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.contentPaddingHorizontal
import androidx.compose.foundation.style.contentPaddingVertical
import androidx.compose.foundation.style.externalPadding
import androidx.compose.foundation.style.externalPaddingHorizontal
import androidx.compose.foundation.style.externalPaddingVertical
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * Tailwind's `p-*` padding utilities via
 * [androidx.compose.foundation.style.StyleScope.contentPadding] -- StyleScope has no
 * `padding()` (see AGENTS.md); content padding is the Style-API analogue of
 * [com.toutiaoyan.compose.tailwind.modifiers.p4] et al.'s `Modifier.padding`.
 *
 * Named steps read [twSpacing] at Style consume time (Theme / LocalTwSpacing).
 */

fun StyleScope.p(value: Dp) {
    contentPadding(value)
}

fun StyleScope.px(value: Dp) {
    contentPaddingHorizontal(value)
}

fun StyleScope.py(value: Dp) {
    contentPaddingVertical(value)
}

fun StyleScope.pt(value: Dp) {
    contentPaddingTop(value)
}

fun StyleScope.pb(value: Dp) {
    contentPaddingBottom(value)
}

fun StyleScope.pl(value: Dp) {
    contentPaddingStart(value)
}

fun StyleScope.pr(value: Dp) {
    contentPaddingEnd(value)
}

fun StyleScope.p2() {
    contentPadding(twSpacing.scale2)
}

fun StyleScope.p4() {
    contentPadding(twSpacing.scale4)
}

fun StyleScope.px2() {
    contentPaddingHorizontal(twSpacing.scale2)
}

fun StyleScope.px4() {
    contentPaddingHorizontal(twSpacing.scale4)
}

fun StyleScope.py2() {
    contentPaddingVertical(twSpacing.scale2)
}

fun StyleScope.py4() {
    contentPaddingVertical(twSpacing.scale4)
}

/**
 * Tailwind's `m-*` margin utilities via
 * [androidx.compose.foundation.style.StyleScope.externalPadding] -- the outer inset
 * analogue of [com.toutiaoyan.compose.tailwind.modifiers.m4] et al.'s `Modifier.padding`
 * used as margin.
 */

fun StyleScope.m(value: Dp) {
    externalPadding(value)
}

fun StyleScope.mx(value: Dp) {
    externalPaddingHorizontal(value)
}

fun StyleScope.my(value: Dp) {
    externalPaddingVertical(value)
}

fun StyleScope.mt(value: Dp) {
    externalPaddingTop(value)
}

fun StyleScope.mb(value: Dp) {
    externalPaddingBottom(value)
}

fun StyleScope.ml(value: Dp) {
    externalPaddingStart(value)
}

fun StyleScope.mr(value: Dp) {
    externalPaddingEnd(value)
}

fun StyleScope.m2() {
    externalPadding(twSpacing.scale2)
}

fun StyleScope.m4() {
    externalPadding(twSpacing.scale4)
}

fun StyleScope.mx2() {
    externalPaddingHorizontal(twSpacing.scale2)
}

fun StyleScope.mx4() {
    externalPaddingHorizontal(twSpacing.scale4)
}

fun StyleScope.my2() {
    externalPaddingVertical(twSpacing.scale2)
}

fun StyleScope.my4() {
    externalPaddingVertical(twSpacing.scale4)
}
