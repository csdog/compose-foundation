@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberMessageAvatarStyle(): Style =
    rememberTheme {
        Style {
            width(32.dp)
            height(32.dp)
            shape(CircleShape)
            background(colors.muted)
        }
    }
