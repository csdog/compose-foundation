@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberAttachmentMediaStyle(): Style =
    rememberTheme {
        Style {
            width(40.dp)
            height(40.dp)
            shape(RoundedCornerShape(radius.md))
            background(colors.muted)
        }
    }
