package com.toutiaoyan.compose.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalInputModeManager

/** Observe before click/navigation handlers consume a key; leave its behavior intact. */
@Composable
internal fun Modifier.keyboardInputMode(): Modifier {
    val manager = LocalInputModeManager.current
    return onPreviewKeyEvent { event ->
        if (event.type == KeyEventType.KeyDown) manager.requestInputMode(InputMode.Keyboard)
        false
    }
}
