@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.resizable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberResizableHandleStyle(horizontal: Boolean): Style =
    rememberTheme(horizontal) {
        Style {
            if (horizontal) {
                height(1f)
                width(1.dp)
            } else {
                width(1f)
                height(1.dp)
            }
            background(colors.border)
        }
    }

@Composable
fun rememberResizableGripStyle(horizontal: Boolean): Style =
    rememberTheme(horizontal) {
        Style {
            if (horizontal) {
                width(12.dp)
                height(16.dp)
            } else {
                width(16.dp)
                height(12.dp)
            }
            shape(RoundedCornerShape(radius.xs))
            background(colors.border)
        }
    }

