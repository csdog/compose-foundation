package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal expect class HoverCardPointerSurface() {
    fun observeTrigger(modifier: Modifier): Modifier

    @Composable
    fun ObserveTransit(
        anchor: HoverCardAnchor,
        triggerHovered: Boolean,
        contentHovered: Boolean,
        onTransit: (Boolean) -> Unit,
    )
}
