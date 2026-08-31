@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.buttongroup

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberButtonGroupOutlineStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.md))
            border(colors.border)
        }
    }

@Composable
fun rememberButtonGroupTextStyle(
    topStart: Dp,
    topEnd: Dp,
    bottomEnd: Dp,
    bottomStart: Dp,
): Style =
    rememberTheme(topStart, topEnd, bottomEnd, bottomStart) {
        Style {
            shape(RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart))
            background(colors.muted)
            contentPadding(horizontal = spacing.lg, vertical = spacing.sm)
        }
    }
