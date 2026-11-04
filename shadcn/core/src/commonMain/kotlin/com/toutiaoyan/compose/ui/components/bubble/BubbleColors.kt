package com.toutiaoyan.compose.ui.components.bubble

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import com.toutiaoyan.compose.tailwind.core.Oklch
import com.toutiaoyan.compose.tailwind.core.toColor
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cbrt
import kotlin.math.sqrt

/** CSS relative OKLCH: replace L, scale C, retain the primary's hue. */
private fun Color.bubbleOklch(): Oklch {
    val rgb = convert(ColorSpaces.LinearSrgb)
    val l = cbrt(0.4122214708 * rgb.red + 0.5363325363 * rgb.green + 0.0514459929 * rgb.blue)
    val m = cbrt(0.2119034982 * rgb.red + 0.6806995451 * rgb.green + 0.1073969566 * rgb.blue)
    val s = cbrt(0.0883024619 * rgb.red + 0.2817188376 * rgb.green + 0.6299787005 * rgb.blue)
    val a = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s
    val b = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s
    val chroma = sqrt(a * a + b * b)
    val hue = if (chroma < 0.00001) 0.0 else (atan2(b, a) * 180 / PI + 360) % 360
    val lightness = 0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s
    return Oklch(lightness.toFloat(), chroma.toFloat(), hue.toFloat())
}

internal fun bubbleTintedColor(
    primary: Color,
    dark: Boolean,
    hovered: Boolean = false,
): Color {
    val source = primary.bubbleOklch()
    val lightness =
        if (dark) {
            if (hovered) 0.35f else 0.3f
        } else {
            if (hovered) 0.88f else 0.93f
        }
    return source.copy(lightness = lightness, chroma = source.chroma * if (hovered) 0.5f else 0.4f).toColor()
}

/** The source's 5% foreground mix uses the OKLCH interpolation space. */
internal fun bubbleHoverMix(
    background: Color,
    foreground: Color,
): Color {
    val a = background.bubbleOklch()
    val b = foreground.bubbleOklch()
    val fromHue = if (a.chroma < 0.00001f) b.hue else a.hue
    val toHue = if (b.chroma < 0.00001f) fromHue else b.hue
    val delta = (toHue - fromHue + 540f) % 360f - 180f
    return Oklch(
        a.lightness * 0.95f + b.lightness * 0.05f,
        a.chroma * 0.95f + b.chroma * 0.05f,
        (fromHue + delta * 0.05f + 360f) % 360f,
    ).toColor()
}
