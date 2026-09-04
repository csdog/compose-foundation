@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.carousel

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberCarouselDotStyle(active: Boolean): Style =
    rememberTheme(active) {
        val size = if (active) 8.dp else 6.dp
        Style {
            width(size)
            height(size)
            shape(CircleShape)
            background(if (active) colors.primary else colors.border)
        }
    }
