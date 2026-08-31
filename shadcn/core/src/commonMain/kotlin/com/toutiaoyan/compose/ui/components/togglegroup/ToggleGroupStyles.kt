@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberToggleGroupOutlineStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.md))
            border(colors.border)
        }
    }
