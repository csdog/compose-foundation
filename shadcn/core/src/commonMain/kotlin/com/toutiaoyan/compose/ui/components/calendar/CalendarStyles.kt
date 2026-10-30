@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

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
fun rememberCalendarNavButtonStyle(): Style =
    rememberTheme {
        Style {
            width(28.dp)
            height(28.dp)
            shape(RoundedCornerShape(radius.md))
            if (theme.activePreset != StylePreset.Nova) border(colors.border)
        }
    }

@Composable
fun rememberCalendarDayCellChromeStyle(): Style =
    rememberTheme {
        Style {
            width(if (theme.activePreset == StylePreset.Nova) 28.dp else 36.dp)
            height(if (theme.activePreset == StylePreset.Nova) 28.dp else 36.dp)
            contentPadding(if (theme.activePreset == StylePreset.Nova) 0.dp else 2.dp)
            shape(RoundedCornerShape(radius.md))
        }
    }
