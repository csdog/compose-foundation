@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberMenubarStyle(): Style =
    rememberTheme {
        val nova = theme.activePreset == StylePreset.Nova
        Style {
            if (nova) height(32.dp)
            shape(RoundedCornerShape(if (nova) radius.lg else radius.md))
            background(colors.background)
            border(colors.border)
            contentPadding(if (nova) 3.dp else spacing.xs2)
        }
    }

@Composable
fun rememberMenubarTriggerStyle(open: Boolean): Style =
    rememberTheme(open) {
        val nova = theme.activePreset == StylePreset.Nova
        Style {
            shape(RoundedCornerShape(radius.sm))
            background(
                if (open) {
                    if (nova) colors.muted else colors.secondary
                } else {
                    colors.background
                },
            )
            if (nova) hovered { background(colors.muted) }
            contentPadding(horizontal = if (nova) 6.dp else spacing.sm, vertical = if (nova) 2.dp else spacing.xs)
        }
    }

@Composable
internal fun rememberMenubarItemStyle(): Style =
    rememberTheme {
        if (theme.activePreset == StylePreset.Nova) Style { shape(RoundedCornerShape(radius.md)) } else Style
    }
