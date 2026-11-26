package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.runtime.Composable

@Composable
internal expect fun HoverCardWindowEscape(
    pointerOverCard: Boolean,
    onEscape: () -> Unit,
)
