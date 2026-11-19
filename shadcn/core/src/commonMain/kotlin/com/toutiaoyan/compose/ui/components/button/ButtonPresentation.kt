@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.theme.ThemeData
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.rememberTheme

internal fun buttonLabelStyle(
    theme: ThemeData,
    variant: ButtonVariant,
    size: ButtonSize,
): TextStyle {
    val isNova = theme.activePreset == StylePreset.Nova
    val isRhea = theme.activePreset == StylePreset.Rhea
    val isRheaOutline = isRhea && variant == ButtonVariant.Outline
    val isRheaSmall = isRhea && size == ButtonSize.Sm
    return if (isNova || isRheaOutline || isRheaSmall) {
        theme.typography.labelLarge.copy(
            fontFamily = theme.fontFamily,
            fontSize =
                when (size) {
                    ButtonSize.Xs -> 12.sp
                    ButtonSize.Sm -> if (isRhea) 14.sp else 12.8.sp
                    else -> 14.sp
                },
            lineHeight = if (size == ButtonSize.Xs) 16.sp else 20.sp,
            lineHeightStyle =
                LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None,
                    mode = LineHeightStyle.Mode.Tight,
                ),
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
        )
    } else {
        TextStyle.Default
    }
}

@Composable
internal fun rememberButtonIconPadding(
    size: ButtonSize,
    iconPlacement: ButtonIconPlacement?,
): Style {
    val preset = currentTheme.activePreset
    val isNova = preset == StylePreset.Nova
    val isRhea = preset == StylePreset.Rhea
    val supportsIconSpacing = supportsButtonIconSpacing(preset, size)
    val usesCompactIconSpacing = isNova && size in CompactButtonSizes
    val direction = LocalLayoutDirection.current
    return rememberTheme(size, iconPlacement, direction) {
        val normal =
            when (size) {
                ButtonSize.Xs -> spacing.scale2
                ButtonSize.Sm -> if (isRhea) spacing.scale3 else spacing.scale2p5
                ButtonSize.Md, ButtonSize.Lg -> spacing.scale2p5
                else -> null
            }
        if (!supportsIconSpacing || iconPlacement == null || normal == null) {
            Style
        } else {
            val icon = if (usesCompactIconSpacing) spacing.scale1p5 else spacing.scale2
            val start = if (iconPlacement != ButtonIconPlacement.End) icon else normal
            val end = if (iconPlacement != ButtonIconPlacement.Start) icon else normal
            buttonIconPaddingStyle(start, end, direction)
        }
    }
}

private fun buttonIconPaddingStyle(
    start: Dp,
    end: Dp,
    direction: LayoutDirection,
): Style {
    // Foundation scalar padding uses physical edges.
    val (left, right) = if (direction == LayoutDirection.Ltr) start to end else end to start
    return Style { contentPadding(start = left, top = 0.dp, end = right, bottom = 0.dp) }
}

private val CompactButtonSizes = setOf(ButtonSize.Xs, ButtonSize.Sm)

private fun supportsButtonIconSpacing(
    preset: StylePreset,
    size: ButtonSize,
): Boolean = preset == StylePreset.Nova || (preset == StylePreset.Rhea && size == ButtonSize.Sm)

@Composable
internal fun rememberButtonInvalidStyle(invalid: Boolean): Style {
    return rememberTheme(invalid) {
        if (!invalid) {
            Style
        } else {
            Style {
                border(colors.destructive.copy(alpha = if (isDark) 0.5f else 1f))
                if (ring.enabled) {
                    dropShadow(
                        Shadow(
                            radius = 0.dp,
                            spread = ring.width + ring.offset,
                            color = colors.destructive.copy(alpha = if (isDark) 0.4f else 0.2f),
                        ),
                    )
                }
            }
        }
    }
}
