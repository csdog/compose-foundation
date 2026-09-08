@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.kbd
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberKbdStyle(): Style =
    rememberTheme {
        Style {
            height(20.dp)
            minWidth(20.dp)
            shape(RoundedCornerShape(radius.sm))
            background(colors.muted)
            contentPadding(horizontal = spacing.xs2, vertical = 0.dp)
        }
    }
