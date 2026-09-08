@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberMenuPanelStyle(): Style =
    rememberTheme {
        Style {
            width(224.dp)
            shape(RoundedCornerShape(radius.md))
            background(colors.popover)
            border(colors.border)
            contentPadding(spacing.xs2)
        }
    }

@Composable
fun rememberMenuItemStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            shape(RoundedCornerShape(radius.sm))
            background(colors.popover)
            contentPadding(horizontal = spacing.sm, vertical = spacing.xs)
            hovered { background(colors.muted) }
            disabledDim()
        }
    }

@Composable
fun rememberMenuLabelStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            contentPadding(horizontal = spacing.sm, vertical = spacing.xs)
        }
    }
