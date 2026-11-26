@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.accordion

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.minSize
import com.toutiaoyan.compose.ui.styles.border
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.tokens.StylePreset

@Composable
fun rememberAccordionTriggerStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            if (theme.activePreset == StylePreset.Nova) {
                minSize(0.dp, 42.dp)
                border(Color.Transparent)
            }
            background(if (theme.activePreset == StylePreset.Nova) Color.Transparent else colors.background)
            if (theme.activePreset == StylePreset.Nova) {
                shape(RoundedCornerShape(baseRadius.value))
            } else {
                focusRing(RoundedCornerShape(radius.md))
            }
            contentPadding(
                vertical = if (theme.activePreset == StylePreset.Nova) 10.dp else spacing.md,
                horizontal = 0.dp,
            )
        }
    }
