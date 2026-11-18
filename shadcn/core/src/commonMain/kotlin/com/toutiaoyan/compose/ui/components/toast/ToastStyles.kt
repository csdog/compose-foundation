@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.TwShadow
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.styles.focusVisible

@Composable
fun rememberToastCardStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            maxWidth(384.dp)
            shape(RoundedCornerShape(16.dp))
            background(colors.popover)
            contentColor(colors.popoverForeground)
            dropShadow(Shadow(radius = TwShadow.lg, color = Color.Black.copy(alpha = .15f)))
            border(colors.border)
            contentPadding(spacing.md)
        }
    }

@Composable
internal fun rememberNovaToastCardStyle(): Style =
    rememberTheme {
        val shadow = Shadow(radius = TwShadow.lg, color = Color.Black.copy(alpha = .15f))
        Style {
            width(1f)
            maxWidth(384.dp)
            shape(RoundedCornerShape(18.dp))
            background(colors.popover)
            contentColor(colors.popoverForeground)
            dropShadow(shadow)
            border(colors.border)
            contentPadding(16.dp)
            if (theme.ring.enabled) {
                focusVisible {
                    borderColor(colors.ring)
                    dropShadow(focusRingShadow(), shadow)
                }
            }
        }
    }
