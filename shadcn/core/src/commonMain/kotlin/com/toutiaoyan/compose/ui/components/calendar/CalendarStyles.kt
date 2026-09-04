@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.calendar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberCalendarRootStyle(): Style =
    rememberTheme {
        Style {
            contentPadding(spacing.md)
        }
    }

@Composable
fun rememberCalendarCaptionDropdownStyle(): Style =
    rememberTheme {
        Style {
            height(32.dp)
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
            border(colors.border)
        }
    }

@Composable
fun rememberCalendarDayCellChromeStyle(): Style =
    rememberTheme {
        Style {
            width(36.dp)
            height(36.dp)
            contentPadding(2.dp)
            shape(RoundedCornerShape(radius.md))
        }
    }
