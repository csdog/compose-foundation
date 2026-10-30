package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.Colors

/** Only chart roles are scoped; buttons and other controls retain the selected Theme. */
internal val LocalPreviewChartColors = staticCompositionLocalOf<List<Color>?> { null }

internal fun resolveChartColors(
    selection: String,
    dark: Boolean,
): List<Color> {
    val base = BaseColor.entries.firstOrNull { it.label == selection }
    val palette =
        if (base != null) {
            if (dark) base.dark else base.light
        } else {
            Accent.valueOf(selection).applyTo(if (dark) BaseColor.Neutral.dark else BaseColor.Neutral.light, dark)
        }
    return palette.chartColors()
}

internal fun chartColorSwatch(selection: String): Color =
    BaseColor.entries.firstOrNull { it.label == selection }?.dark?.mutedForeground
        ?: Accent.valueOf(selection).applyTo(BaseColor.Neutral.dark, dark = true).primary

@Composable
internal fun previewChartColors(): List<Color> = LocalPreviewChartColors.current ?: currentTheme.colors.chartColors()

private fun Colors.chartColors() = listOf(chart1, chart2, chart3, chart4, chart5)
