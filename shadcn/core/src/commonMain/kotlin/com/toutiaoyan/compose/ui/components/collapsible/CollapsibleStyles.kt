@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.collapsible

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberCollapsibleContentStyle(): Style =
    rememberTheme {
        Style {
            contentPaddingTop(spacing.sm)
        }
    }
