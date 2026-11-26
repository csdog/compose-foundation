@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.table

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

internal const val TABLE_MUTED_BACKGROUND_ALPHA = 0.5f

@Composable
fun rememberTableRowStyle(
    selected: Boolean = false,
    footer: Boolean = false,
): Style =
    rememberTheme(selected, footer) {
        Style {
            width(1f)
            if (footer) background(colors.muted.copy(alpha = TABLE_MUTED_BACKGROUND_ALPHA))
            hovered { background(colors.muted.copy(alpha = TABLE_MUTED_BACKGROUND_ALPHA)) }
            if (selected) background(colors.muted)
            contentPadding(0.dp)
        }
    }
