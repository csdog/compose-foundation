package com.toutiaoyan.compose.ui.styles

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal data class ShimmerLch(val l: Float, val c: Float, val h: Float, val alpha: Float)

internal fun Color.shimmerLch(): ShimmerLch {
    val lab = convert(ColorSpaces.Oklab)
    val chroma = sqrt(lab.green * lab.green + lab.blue * lab.blue)
    val hue = if (chroma < .00001f) 0f else ((atan2(lab.blue, lab.green) * 180f / PI.toFloat()) + 360f) % 360f
    return ShimmerLch(lab.red, if (chroma < .00001f) 0f else chroma, hue, alpha)
}

internal fun ShimmerLch.shimmerColor(): Color {
    // CSS maps the lightness endpoints to black/white regardless of chroma.
    if (l <= 0f) return Color.Black.copy(alpha = alpha.coerceIn(0f, 1f))
    if (l >= 1f) return Color.White.copy(alpha = alpha.coerceIn(0f, 1f))
    val radians = h * PI.toFloat() / 180f
    return Color(l, c * cos(radians), c * sin(radians), alpha.coerceIn(0f, 1f), ColorSpaces.Oklab)
        .convert(ColorSpaces.Srgb)
}

/** The source's relative color expression; retain highlight alpha rather than composite it over base. */
internal fun shimmerHighlight(
    base: Color,
    dark: Boolean,
): Color {
    if (!dark) return base.copy(alpha = base.alpha * .2f)
    val source = base.shimmerLch()
    return source.copy(l = maxOf(.8f, source.l + .4f).coerceAtMost(1f), alpha = (base.alpha + .4f).coerceAtMost(1f))
        .shimmerColor()
}

/** color-mix(in oklch, highlight, base 50%): premultiply L/C/alpha, but not hue. */
internal fun shimmerOklchMidpoint(
    base: Color,
    highlight: Color,
): Color {
    val a = base.shimmerLch()
    val b = highlight.shimmerLch()
    val alpha = (a.alpha + b.alpha) / 2f
    if (alpha == 0f) return Color.Transparent
    val fromHue = if (a.c == 0f) b.h else a.h
    val toHue = if (b.c == 0f) fromHue else b.h
    var delta = toHue - fromHue
    if (delta > 180f) delta -= 360f
    if (delta < -180f) delta += 360f
    return ShimmerLch(
        l = (a.l * a.alpha + b.l * b.alpha) / (2f * alpha),
        c = (a.c * a.alpha + b.c * b.alpha) / (2f * alpha),
        h = (fromHue + delta / 2f + 360f) % 360f,
        alpha = alpha,
    ).shimmerColor()
}

/** CSS modern gradients interpolate in Oklab with premultiplied alpha. */
private fun shimmerOklabMix(
    a: Color,
    b: Color,
    fraction: Float,
): Color {
    if (fraction == 0f) return a
    if (fraction == 1f) return b
    val start = a.convert(ColorSpaces.Oklab)
    val end = b.convert(ColorSpaces.Oklab)
    val alpha = a.alpha * (1f - fraction) + b.alpha * fraction
    if (alpha == 0f) return Color.Transparent

    fun component(
        from: Float,
        to: Float,
    ) = (from * a.alpha * (1f - fraction) + to * b.alpha * fraction) / alpha
    val l = component(start.red, end.red)
    if (l <= 0f) return Color.Black.copy(alpha = alpha)
    if (l >= 1f) return Color.White.copy(alpha = alpha)
    return Color(l, component(start.green, end.green), component(start.blue, end.blue), alpha, ColorSpaces.Oklab)
        .convert(ColorSpaces.Srgb)
}

/** Native shaders interpolate their supplied sRGB stops. Sample the CSS Oklab curve first.
 * Cache by base color outside animation state; no snapshot write or color conversion per frame.
 */
internal class ShimmerColorCache(private val highlight: Color?, private val dark: Boolean) {
    private val cached = linkedMapOf<Color, List<Color>>()

    fun colors(base: Color): List<Color> {
        cached[base]?.let { return it }
        val peak = highlight ?: shimmerHighlight(base, dark)
        val middle = shimmerOklchMidpoint(base, peak)
        val stops = listOf(base, middle, peak, middle, base)
        val colors =
            List(257) { index ->
                val segment = minOf(index / 64, 3)
                shimmerOklabMix(stops[segment], stops[segment + 1], (index - segment * 64) / 64f)
            }
        // One Style can be shared by several differently colored text nodes.
        if (cached.size >= 4) cached.remove(cached.keys.first())
        cached[base] = colors
        return colors
    }
}
