@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRing

@Composable
fun rememberNavMenuItemStyle(open: Boolean): Style =
    rememberTheme(open) {
        Style {
            shape(RoundedCornerShape(radius.sm))
            background(if (open) colors.secondary else colors.background)
            focusRing(RoundedCornerShape(radius.sm))
            contentPadding(horizontal = spacing.md, vertical = spacing.xs)
        }
    }

@Composable
fun rememberNavMenuPanelStyle(): Style =
    rememberTheme {
        Style {
            width(320.dp)
            shape(RoundedCornerShape(radius.md))
            background(colors.popover)
            border(colors.border)
            contentPadding(spacing.lg)
        }
    }

@Composable
fun rememberNavMenuIndicatorStyle(): Style =
    rememberTheme {
        Style {
            background(colors.border)
            shape(RoundedCornerShape(1.dp))
        }
    }
