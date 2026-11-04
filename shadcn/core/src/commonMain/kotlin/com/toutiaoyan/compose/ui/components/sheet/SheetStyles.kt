@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

@Composable
fun rememberSheetPanelStyle(): Style =
    rememberTheme {
        Style {
            background(colors.popover)
            dropShadow(
                Shadow(
                    radius =
                        15.dp,
                    spread =
                        (-3).dp,
                    offset =
                        DpOffset(
                            0.dp,
                            10.dp,
                        ),
                    color =
                        Color.Black.copy(
                            alpha =
                            .1f,
                        ),
                ),
                Shadow(
                    radius =
                        6.dp,
                    spread =
                        (-4).dp,
                    offset =
                        DpOffset(
                            0.dp,
                            4.dp,
                        ),
                    color =
                        Color.Black.copy(
                            alpha =
                            .1f,
                        ),
                ),
            )
            contentPadding(0.dp)
        }
    }

@Composable
internal fun rememberSheetRegionStyle(): Style = rememberTheme { Style { contentPadding(16.dp) } }
