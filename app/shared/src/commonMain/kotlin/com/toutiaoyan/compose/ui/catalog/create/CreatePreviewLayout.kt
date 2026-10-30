package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.tokens.StylePreset

/** The web reference runs in an iframe; responsive rules use the preview's width. */
internal val LocalCreatePreviewWidth = staticCompositionLocalOf<Dp?> { null }

@Immutable
internal data class FinancePreviewMetrics(val gap: Dp, val columnWidth: Dp)

internal fun financePreviewMetrics(
    width: Dp,
    preset: StylePreset,
): FinancePreviewMetrics {
    val compact = width < 768.dp
    val dense = preset == StylePreset.Lyra || preset == StylePreset.Mira
    val gap =
        when {
            compact -> 16.dp
            dense -> 24.dp
            width >= 1600.dp -> 48.dp
            else -> 40.dp
        }
    val gridWidth =
        when {
            compact -> 2400.dp
            dense -> 2600.dp
            else -> 3000.dp
        }
    return FinancePreviewMetrics(gap, (gridWidth - gap * 8) / 7)
}

@Composable
internal fun isCompactCreatePreview(): Boolean {
    val width =
        LocalCreatePreviewWidth.current
            ?: with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    return width < 768.dp
}
