package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.ui.geometry.Rect

internal fun interface ToastOutsideTouch {
    fun observe(
        bounds: () -> Rect?,
        onOutside: () -> Unit,
    ): () -> Unit
}
