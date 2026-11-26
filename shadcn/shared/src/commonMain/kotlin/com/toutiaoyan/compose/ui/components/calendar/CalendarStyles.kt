@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberCalendarRootStyle(): Style =
    rememberTheme {
        Style {
            contentPadding(if (theme.activePreset == StylePreset.Nova) 8.dp else spacing.md)
        }
    }

@Composable
fun rememberCalendarCaptionDropdownStyle(): Style =
    rememberTheme {
        Style {
            height(if (theme.activePreset == StylePreset.Nova) 24.dp else 32.dp)
            contentPadding(horizontal = spacing.sm, vertical = spacing.xs)
            shape(RoundedCornerShape(radius.md))
            border(colors.input)
        }
    }

@Composable
fun rememberCalendarNavButtonStyle(cellSize: Dp = 28.dp): Style =
    rememberTheme(cellSize) {
        Style {
            width(cellSize)
            height(cellSize)
            shape(RoundedCornerShape(radius.md))
            if (theme.activePreset != StylePreset.Nova) border(colors.border)
            hovered { background(colors.muted.copy(alpha = if (isDark) 0.5f else 1f)) }
        }
    }

@Composable
fun rememberCalendarDayCellChromeStyle(cellSize: Dp? = null): Style =
    rememberTheme(cellSize) {
        Style {
            width(cellSize ?: if (theme.activePreset == StylePreset.Nova) 28.dp else 36.dp)
            height(cellSize ?: if (theme.activePreset == StylePreset.Nova) 28.dp else 36.dp)
            contentPadding(if (theme.activePreset == StylePreset.Nova) 0.dp else 2.dp)
            shape(RoundedCornerShape(radius.md))
        }
    }

@Composable
internal fun rememberCalendarPressMotionStyle(interactionSource: InteractionSource): Style {
    val legacy = rememberPressMotionStyle()
    if (currentTheme.activePreset != StylePreset.Nova) return legacy
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset =
        animateFloatAsState(
            targetValue = if (isPressed) 1f else 0f,
            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
            label = "Calendar press",
        )
    return remember(offset) { Style { translationY(offset.value.dp.toPx()) } }
}
