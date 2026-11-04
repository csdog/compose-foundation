@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberMessageAvatarStyle(): Style =
    rememberTheme {
        Style {
            if (theme.activePreset == StylePreset.Nova) {
                minWidth(32.dp)
                clip()
            } else {
                width(32.dp)
                height(32.dp)
            }
            shape(CircleShape)
            background(colors.muted)
        }
    }

@Composable
internal fun rememberMessageMetadataStyle(): Style {
    val ghost = (LocalMessageParts.current?.ghostCount ?: 0) > 0
    return rememberTheme(ghost) {
        if (theme.activePreset == StylePreset.Nova) {
            Style { contentPadding(horizontal = if (ghost) 0.dp else 12.dp, vertical = 0.dp) }
        } else {
            Style
        }
    }
}

@Composable
internal fun rememberMessageRootStyle(): Style =
    rememberTheme {
        if (theme.activePreset == StylePreset.Nova) Style { width(1f) } else Style
    }
