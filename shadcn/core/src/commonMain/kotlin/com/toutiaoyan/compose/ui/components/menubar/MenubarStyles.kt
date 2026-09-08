@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberMenubarStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.md))
            background(colors.background)
            border(colors.border)
            contentPadding(spacing.xs2)
        }
    }

@Composable
fun rememberMenubarTriggerStyle(open: Boolean): Style =
    rememberTheme(open) {
        Style {
            shape(RoundedCornerShape(radius.sm))
            background(if (open) colors.secondary else colors.background)
            contentPadding(horizontal = spacing.sm, vertical = spacing.xs)
        }
    }
