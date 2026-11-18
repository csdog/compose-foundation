package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEvent

internal fun interface ToastOutsideTouch {
    fun observe(
        bounds: () -> Rect?,
        onOutside: () -> Unit,
    ): () -> Unit
}

internal expect fun captureToastOutsideTouch(touch: PointerEvent): ToastOutsideTouch?

@Composable
internal expect fun Modifier.toastViewportShortcut(enabled: Boolean): Modifier
