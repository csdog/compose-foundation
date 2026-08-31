package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.TwShadow

/**
 * Style-API counterpart of [com.toutiaoyan.compose.tailwind.modifiers.twCard] --
 * `bg-white rounded-lg shadow-sm` composed in one call so callers don't have to
 * remember that Compose's shadow/clip/background order is load-bearing.
 *
 * Uses [androidx.compose.foundation.style.ShadowScope.dropShadow] (CSS box-shadow
 * model) with [shadowRadius] as blur radius -- the same approximation
 * [com.toutiaoyan.compose.tailwind.style.shadow] documents -- rather than Android
 * elevation. [shape] clips background and shadows together per ShapeScope's contract.
 *
 * When [shape] is omitted, uses [LocalTwRadius]`.lg` at consume time.
 */

fun StyleScope.twCard(
    shape: Shape? = null,
    color: Color = TwColors.white,
    shadowRadius: Dp = TwShadow.xs,
) {
    val resolved = shape ?: RoundedCornerShape(LocalTwRadius.currentValue.lg)
    dropShadow(Shadow(radius = shadowRadius, color = Color.Black.copy(alpha = 0.25f)))
    shape(resolved)
    background(color)
}
