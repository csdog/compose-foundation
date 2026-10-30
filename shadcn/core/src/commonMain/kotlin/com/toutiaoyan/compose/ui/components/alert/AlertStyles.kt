package com.toutiaoyan.compose.ui.components.alert

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.rememberTheme

sealed interface AlertVariant {
    data object Default : AlertVariant

    data object Destructive : AlertVariant
}

/** Same color each variant passes to Style `contentColor(...)`, for [LocalContentColor]. */
fun AlertVariant.contentColor(colors: Colors): Color =
    when (this) {
        AlertVariant.Default -> colors.foreground
        AlertVariant.Destructive -> colors.destructive
    }

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AlertVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        val nova = theme.activePreset == StylePreset.Nova
        val foreground =
            if (nova && receiver == AlertVariant.Default) {
                colors.cardForeground
            } else {
                receiver.contentColor(
                    colors,
                )
            }
        when (receiver) {
            AlertVariant.Default ->
                Style {
                    if (nova) width(1f)
                    background(if (nova) colors.card else colors.background)
                    contentColor(foreground)
                    border(colors.border)
                    shape(RoundedCornerShape(if (nova) baseRadius.value else radius.lg))
                }

            AlertVariant.Destructive ->
                Style {
                    if (nova) width(1f)
                    background(if (nova) colors.card else colors.background)
                    contentColor(foreground)
                    border(colors.border)
                    shape(RoundedCornerShape(if (nova) baseRadius.value else radius.lg))
                }
        }
    }
}
