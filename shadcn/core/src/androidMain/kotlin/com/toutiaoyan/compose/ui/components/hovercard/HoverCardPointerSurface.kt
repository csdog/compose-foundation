package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal actual class HoverCardPointerSurface actual constructor() {
    actual fun observeTrigger(modifier: Modifier): Modifier = modifier

    @Composable
    actual fun ObserveTransit(
        anchor: HoverCardAnchor,
        triggerHovered: Boolean,
        contentHovered: Boolean,
        onTransit: (Boolean) -> Unit,
    ) = Unit
}
