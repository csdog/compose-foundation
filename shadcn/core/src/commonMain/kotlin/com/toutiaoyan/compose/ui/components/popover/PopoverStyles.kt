@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.style.shadowMd
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberPopoverContentStyle(): Style =
    rememberTheme {
        Style {
            width(288.dp)
            shape(RoundedCornerShape(radius.md))
            background(colors.popover)
            border(colors.border)
            contentPadding(spacing.lg)
            shadowMd()
        }
    }
