@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.pagination

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberPaginationButtonStyle(): Style =
    rememberTheme {
        Style {
            width(36.dp)
            height(36.dp)
            shape(RoundedCornerShape(radius.md))
        }
    }

@Composable
fun rememberPaginationLabeledButtonStyle(): Style =
    rememberTheme {
        Style {
            height(36.dp)
            contentPadding(horizontal = spacing.md, vertical = 0.dp)
            shape(RoundedCornerShape(radius.md))
        }
    }
