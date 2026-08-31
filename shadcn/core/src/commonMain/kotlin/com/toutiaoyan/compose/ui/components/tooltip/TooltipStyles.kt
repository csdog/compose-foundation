@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.tooltip

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberTooltipContentStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.md))
            background(colors.foreground)
            contentColor(colors.background)
            contentPadding(horizontal = spacing.md, vertical = spacing.xs)
        }
    }

