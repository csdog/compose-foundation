package com.toutiaoyan.compose.tailwind.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.toutiaoyan.compose.tailwind.core.TwShadow

/**
 * Tailwind v4 `shadow-*` elevation utilities (see [TwShadow] for the
 * approximation caveat). Like [Modifier.clip]/[Modifier.background], these
 * must precede the modifiers whose bounds they should wrap (e.g.
 * `Modifier.shadowLg().roundedLg().bgWhite()`).
 *
 * Pass the same [Shape] used for the accompanying `rounded*()`/`clip()` call
 * as [shape] -- otherwise the shadow casts a rectangular halo under rounded
 * content (mirrors the `shape` param on [Modifier.border]).
 */
fun Modifier.shadowNone(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.none, shape)

/** v4 `shadow-2xs`. */
fun Modifier.shadowXs2(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.xs2, shape)

/** v4 `shadow-xs`. */
fun Modifier.shadowXs(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.xs, shape)

/** v4 `shadow-sm`. */
fun Modifier.shadowSm(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.sm, shape)

fun Modifier.shadowMd(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.md, shape)

fun Modifier.shadowLg(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.lg, shape)

fun Modifier.shadowXl(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.xl, shape)

/** v4 `shadow-2xl`. */
fun Modifier.shadowXl2(shape: Shape = RectangleShape): Modifier = this.shadow(TwShadow.xl2, shape)
