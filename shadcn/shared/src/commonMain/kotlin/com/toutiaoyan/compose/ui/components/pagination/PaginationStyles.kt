@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.pagination

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberPaginationButtonStyle(): Style =
    rememberTheme {
        Style {
            width(if (theme.activePreset == StylePreset.Nova) 32.dp else 36.dp)
            height(if (theme.activePreset == StylePreset.Nova) 32.dp else 36.dp)
            shape(RoundedCornerShape(radius.md))
        }
    }

@Composable
fun rememberPaginationLabeledButtonStyle(): Style =
    rememberTheme {
        Style {
            height(if (theme.activePreset == StylePreset.Nova) 32.dp else 36.dp)
            contentPadding(horizontal = spacing.md, vertical = 0.dp)
            shape(RoundedCornerShape(radius.md))
        }
    }

@Composable
internal fun rememberPaginationRootStyle(): Style =
    rememberTheme {
        if (theme.activePreset == StylePreset.Nova) Style { width(1f) } else Style
    }

@Composable
internal fun rememberPaginationControlStyle(previous: Boolean): Style =
    rememberTheme(previous) {
        Style {
            contentPaddingStart(if (previous) 6.dp else 10.dp)
            contentPaddingEnd(if (previous) 10.dp else 6.dp)
        }
    }
