@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.command

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberCommandPanelStyle(): Style =
    rememberTheme {
        Style {
            width(280.dp)
            shape(RoundedCornerShape(radius.md))
            background(colors.popover)
            border(colors.border)
            contentPadding(spacing.sm)
        }
    }

/** Dialog chrome for [CommandDialog]: same panel, no inner padding (Command fills it). */
@Composable
fun rememberCommandDialogPanelStyle(): Style =
    rememberTheme {
        Style {
            contentPadding(0.dp)
        }
    }
