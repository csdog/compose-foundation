package com.toutiaoyan.compose.tailwind.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.TwShadow

/**
 * Tailwind has no single `card` utility class -- a card is just `bg-white rounded-lg shadow-sm`,
 * and CSS applies all three to the same box regardless of declaration order. Compose's
 * `shadow()`/`clip()`/`background()` are order-sensitive instead: get it wrong and the shadow
 * casts a rectangular halo under rounded content, or the background ignores the clip entirely
 * (see `ShowcaseSection.kt`'s fix history). [twCard] exists purely to close that Compose-specific
 * gap -- hence the `tw` prefix shared with [twDark] and
 * [com.toutiaoyan.compose.tailwind.core.twResponsive], the library's other
 * non-utility composition helpers -- by fixing the order and threading one [Shape] through all
 * three calls instead of requiring callers to build it themselves.
 *
 * When [shape] is omitted, uses [LocalTwRadius]`.lg` (theme-overridable `rounded-lg`).
 */
fun Modifier.twCard(
    shape: Shape? = null,
    color: Color = TwColors.white,
    shadowElevation: Dp = TwShadow.xs,
): Modifier =
    composed {
        val resolved = shape ?: RoundedCornerShape(LocalTwRadius.current.lg)
        this
            .shadow(shadowElevation, resolved)
            .clip(resolved)
            .background(color, resolved)
    }
