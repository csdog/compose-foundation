package com.toutiaoyan.compose.tailwind.modifiers

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius

/**
 * Tailwind's `border-*` width utilities. Compose has no separate border-color
 * primitive, so each function takes the color directly (unlike CSS, which
 * composes `border-width` and `border-{color}` independently).
 *
 * [shape] defaults to [RectangleShape], matching Tailwind's sharp corners by default.
 * When pairing a border with a `rounded*()` clip, a preceding `clip()` in the same
 * modifier chain does visually round the border too — Compose's `clip()` clips
 * everything in that layer, not just modifiers after it — but that only holds as
 * long as no intervening modifier (e.g. `graphicsLayer`) starts a new layer. Passing
 * the same [Shape] explicitly here is the robust way to guarantee the border always
 * matches, independent of chain order or layer boundaries:
 *
 * ```
 * val shape = RoundedCornerShape(LocalTwRadius.current.lg)
 * Modifier.clip(shape).border4(color, shape)
 * ```
 */
fun Modifier.border0(
    color: Color,
    shape: Shape = RectangleShape,
): Modifier = this.border(0.dp, color, shape)

fun Modifier.border(
    color: Color,
    shape: Shape = RectangleShape,
): Modifier = this.border(1.dp, color, shape)

fun Modifier.border2(
    color: Color,
    shape: Shape = RectangleShape,
): Modifier = this.border(2.dp, color, shape)

fun Modifier.border4(
    color: Color,
    shape: Shape = RectangleShape,
): Modifier = this.border(4.dp, color, shape)

fun Modifier.border8(
    color: Color,
    shape: Shape = RectangleShape,
): Modifier = this.border(8.dp, color, shape)

/** Reads [LocalTwRadius] so a Theme can override Tailwind's default `rounded-*` scale. */
private fun Modifier.roundedToken(select: (com.toutiaoyan.compose.tailwind.core.TwRadiusScale) -> Dp): Modifier =
    composed {
        clip(RoundedCornerShape(select(LocalTwRadius.current)))
    }

/** Tailwind's `rounded-*` corner-radius utilities. */
fun Modifier.roundedNone(): Modifier = roundedToken { it.none }

/** v4's `rounded-xs`. */
fun Modifier.roundedXs(): Modifier = roundedToken { it.xs }

/** v4's `rounded-sm`. */
fun Modifier.roundedSm(): Modifier = roundedToken { it.sm }

/** v4's bare/DEFAULT `rounded` — same step as [roundedSm]. */
fun Modifier.rounded(): Modifier = roundedToken { it.sm }

fun Modifier.roundedMd(): Modifier = roundedToken { it.md }

fun Modifier.roundedLg(): Modifier = roundedToken { it.lg }

fun Modifier.roundedXl(): Modifier = roundedToken { it.xl }

fun Modifier.roundedXl2(): Modifier = roundedToken { it.xl2 }

fun Modifier.roundedXl3(): Modifier = roundedToken { it.xl3 }

/** v4's `rounded-4xl` -- new in v4, no v3 equivalent. */
fun Modifier.roundedXl4(): Modifier = roundedToken { it.xl4 }

fun Modifier.roundedFull(): Modifier = roundedToken { it.full }
