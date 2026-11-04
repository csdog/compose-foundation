@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.kbd
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.tooltip.LocalInsideTooltipContent
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberKbdStyle(): Style {
    val inTooltip = LocalInsideTooltipContent.current
    return rememberTheme(inTooltip) {
        val nova = theme.activePreset == StylePreset.Nova
        Style {
            height(20.dp)
            minWidth(20.dp)
            shape(RoundedCornerShape(radius.sm))
            background(
                if (nova &&
                    inTooltip
                ) {
                    colors.background.copy(
                        alpha =
                            if (theme.isDark) {
                                0.1f
                            } else {
                                0.2f
                            },
                    )
                } else {
                    colors.muted
                },
            )
            if (nova) contentColor(if (inTooltip) colors.background else colors.mutedForeground)
            contentPadding(horizontal = if (nova) 4.dp else spacing.xs2, vertical = 0.dp)
        }
    }
}
