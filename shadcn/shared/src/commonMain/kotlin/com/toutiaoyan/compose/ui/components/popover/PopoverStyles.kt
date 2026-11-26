@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import com.toutiaoyan.compose.tailwind.style.ringShadow
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.tailwind.style.shadowMd
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberPopoverContentStyle(): Style =
    rememberTheme {
        val nova = theme.activePreset == StylePreset.Nova
        Style {
            width(288.dp)
            shape(RoundedCornerShape(if (nova) radius.lg else radius.md))
            background(colors.popover)
            if (nova) {
                dropShadow(
                    Shadow(
                        radius = 4.dp,
                        spread = (-2).dp,
                        offset = DpOffset(0.dp, 2.dp),
                        color = Color.Black.copy(alpha = 0.1f),
                    ),
                    Shadow(
                        radius = 6.dp,
                        spread = (-1).dp,
                        offset = DpOffset(0.dp, 4.dp),
                        color = Color.Black.copy(alpha = 0.1f),
                    ),
                    ringShadow(colors.foreground.copy(alpha = 0.1f)),
                )
            } else {
                border(colors.border)
                shadowMd()
            }
            contentPadding(if (nova) 10.dp else spacing.lg)
        }
    }
