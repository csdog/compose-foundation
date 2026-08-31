@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.card
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.utils.rememberTheme

sealed interface CardVariant {
    data object Default : CardVariant

    data object Elevated : CardVariant

    data object Filled : CardVariant
}

/** Same color each variant passes to Style `contentColor(...)`, for [LocalContentColor]. */
fun CardVariant.contentColor(colors: Colors): Color = colors.cardForeground

@Composable
fun CardVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        val foreground = receiver.contentColor(colors)
        when (receiver) {
            CardVariant.Default ->
                Style {
                    background(colors.card)
                    contentColor(foreground)
                    border(colors.border)
                    shape(RoundedCornerShape(radius.xl))
                    contentPadding(spacing.lg)
                }

            CardVariant.Elevated ->
                Style {
                    background(colors.card)
                    contentColor(foreground)
                    shape(RoundedCornerShape(radius.xl))
                    contentPadding(spacing.lg)
                }

            CardVariant.Filled ->
                Style {
                    background(colors.muted)
                    contentColor(foreground)
                    shape(RoundedCornerShape(radius.xl))
                    contentPadding(spacing.lg)
                }
        }
    }
}

sealed interface CardSize {
    data object Default : CardSize

    data object Sm : CardSize
}

/**
 * Official `--card-spacing`. Used for both section gap and inset:
 * `--spacing(4)` (16dp) by default, `--spacing(3)` (12dp) for `size="sm"`.
 */
object CardDefaults {
    /** Official default `--card-spacing: --spacing(4)`. */
    val Spacing = 16.dp

    /** Official `size="sm"` `--card-spacing: --spacing(3)`. */
    val SmSpacing = 12.dp
}

@Composable
fun CardSize.headerSpacing(): Dp =
    when (this) {
        CardSize.Default -> CardDefaults.Spacing
        CardSize.Sm -> CardDefaults.SmSpacing
    }

/** Overlay root inset to official `--card-spacing`. */
@Composable
fun CardSize.rememberSpacingStyle(spacing: Dp): Style =
    rememberTheme(this, spacing) {
        Style { contentPadding(spacing) }
    }
