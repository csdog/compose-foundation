@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberToastCardStyle(): Style =
    rememberTheme {
        Style {
            minWidth(240.dp)
            maxWidth(360.dp)
            shape(RoundedCornerShape(radius.md))
            background(colors.background)
            border(colors.border)
            contentPadding(spacing.md)
        }
    }
