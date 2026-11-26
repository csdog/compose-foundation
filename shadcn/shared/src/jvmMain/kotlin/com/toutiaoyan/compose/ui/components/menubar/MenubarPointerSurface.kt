package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

internal actual class MenubarPointerSurface actual constructor() {
    actual fun observeRoot(modifier: Modifier): Modifier = modifier

    @Composable
    actual fun ObserveOpen(onMove: (Offset) -> Unit) = Unit
}
