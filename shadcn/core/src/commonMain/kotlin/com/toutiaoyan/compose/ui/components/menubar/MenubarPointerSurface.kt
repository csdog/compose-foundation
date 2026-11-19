package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

internal expect class MenubarPointerSurface() {
    fun observeRoot(modifier: Modifier): Modifier

    @Composable
    fun ObserveOpen(onMove: (Offset) -> Unit)
}
