@file:OptIn(ExperimentalFoundationStyleApi::class)
package com.toutiaoyan.compose.ui.components.stepper

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberStepCircleStyle(reached: Boolean): Style =
    rememberTheme(reached) {
        Style {
            width(32.dp)
            height(32.dp)
            shape(CircleShape)
            background(if (reached) colors.primary else colors.muted)
            contentColor(if (reached) colors.primaryForeground else colors.mutedForeground)
        }
    }

@Composable
fun rememberStepConnectorStyle(active: Boolean, horizontal: Boolean): Style =
    rememberTheme(active, horizontal) {
        Style {
            if (horizontal) {
                height(2.dp)
            } else {
                width(2.dp)
            }
            background(if (active) colors.primary else colors.border)
        }
    }
