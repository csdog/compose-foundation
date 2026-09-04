@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.accordion

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.focusRing

@Composable
fun rememberAccordionTriggerStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            background(colors.background)
            focusRing(RoundedCornerShape(radius.md))
            contentPadding(vertical = spacing.md, horizontal = 0.dp)
        }
    }
