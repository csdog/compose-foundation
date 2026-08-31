@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.marker

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.components.separator.Separator

@Composable
fun MarkerVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        when (receiver) {
            MarkerVariant.Default, MarkerVariant.Separator -> Style
            MarkerVariant.Border ->
                Style {
                    contentPaddingBottom(spacing.sm)
                }
        }
    }
}

enum class MarkerVariant { Default, Separator, Border }
