@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.buttongroup

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border

@Composable
fun rememberButtonGroupOutlineStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(if (theme.activePreset == StylePreset.Nova) radius.lg else radius.md))
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
            if (theme.activePreset == StylePreset.Nova) {
                border(colors.border)
                contentPadding(horizontal = 10.dp, vertical = 0.dp)
                minHeight(32.dp)
            } else {
                contentPadding(horizontal = spacing.lg, vertical = spacing.sm)
            }
        }
    }
