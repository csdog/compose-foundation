@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberSheetPanelStyle(): Style =
    rememberTheme {
        Style {
            background(colors.background)
            border(colors.border)
            contentPadding(spacing.xl2)
        }
    }

