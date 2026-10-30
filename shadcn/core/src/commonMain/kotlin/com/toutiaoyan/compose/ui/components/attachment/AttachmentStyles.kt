@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

@Composable
fun rememberAttachmentMediaStyle(): Style = rememberAttachmentMediaStyle(AttachmentSize.Default)

@Composable
internal fun rememberAttachmentMediaStyle(size: AttachmentSize): Style =
    rememberTheme(size) {
        Style {
            val extent =
                if (theme.activePreset == StylePreset.Nova) {
                    when (size) {
                        AttachmentSize.Default -> 40.dp
                        AttachmentSize.Sm -> 32.dp
                        AttachmentSize.Xs -> 28.dp
                    }
                } else {
                    40.dp
                }
            width(extent)
            height(extent)
            shape(
                RoundedCornerShape(
                    if (theme.activePreset == StylePreset.Nova && size != AttachmentSize.Xs) radius.lg else radius.md,
                ),
            )
            clip()
            background(colors.muted)
        }
    }
