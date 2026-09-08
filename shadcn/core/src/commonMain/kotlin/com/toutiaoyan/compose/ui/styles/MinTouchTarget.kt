package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.minSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Expands the interactive region's layout to at least the theme
 * [com.toutiaoyan.compose.ui.theme.ThemeData.minTouchTarget]
 * so hit testing stays usable while an inner child keeps the visual chrome size.
 *
 * Returns [Style] (no-op) when the theme sets `minTouchTarget = null` or a non-positive size.
 *
 * Prefer applying this on an **outer** interactive wrapper; put background/border/height on
 * an inner child so the painted control does not grow to 48dp.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun rememberMinTouchTargetStyle(size: Dp? = currentTheme.minTouchTarget): Style {
    val resolved = size?.takeIf { it > 0.dp }
    return remember(resolved) {
        if (resolved == null) {
            Style
        } else {
            Style {
                minSize(resolved, resolved)
            }
        }
    }
}
