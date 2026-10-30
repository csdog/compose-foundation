package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

@Composable
internal fun overlayTypography(
    size: Int = 14,
    lineHeight: Int = 20,
    medium: Boolean = false,
    muted: Boolean = false,
    fallback: TextStyle = TextStyle.Default,
): TextStyle {
    val theme = currentTheme
    return if (theme.activePreset == StylePreset.Nova) {
        theme.typography.bodyMedium.copy(
            fontFamily = theme.fontFamily,
            fontSize = size.sp,
            lineHeight = lineHeight.sp,
            fontWeight = if (medium) FontWeight.Medium else FontWeight.Normal,
            color = if (muted) theme.colors.mutedForeground else theme.colors.popoverForeground,
        )
    } else {
        fallback
    }
}
