@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.spinner

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberSpinnerStyle(): Style =
    rememberTheme {
        Style {
            width(16.dp)
            height(16.dp)
        }
    }

