@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.table

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberTableRowStyle(
    selected: Boolean = false,
    footer: Boolean = false,
): Style =
    rememberTheme(selected, footer) {
        Style {
            width(1f)
            background(
                when {
                    selected || footer -> colors.muted
                    else -> colors.background
                },
            )
            contentPadding(vertical = spacing.sm, horizontal = 0.dp)
        }
    }
