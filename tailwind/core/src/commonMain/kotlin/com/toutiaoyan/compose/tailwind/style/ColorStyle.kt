package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.graphics.Color

/**
 * Tailwind's `text-{color}` utilities via
 * [androidx.compose.foundation.style.ContentColorScope.contentColor] -- "primarily affecting
 * text color" and inherited by child text components per its own KDoc, the direct Style-API
 * analogue of setting a text color, not [androidx.compose.foundation.style.ForegroundScope]
 * (which overlays a color/brush on top of already-drawn content, meant for translucent tints).
 */

fun StyleScope.textColor(color: Color) {
    contentColor(color)
}
