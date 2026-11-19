package com.toutiaoyan.compose.ui.components.message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/** Registers descendant parts after successful composition; no writes during composition/measurement. */
internal class MessageParts {
    var footerCount by mutableIntStateOf(0)
    var ghostCount by mutableIntStateOf(0)
}

internal val LocalMessageParts = compositionLocalOf<MessageParts?> { null }

@Composable
internal fun TrackMessagePart(ghost: Boolean = false) {
    val parts = LocalMessageParts.current
    DisposableEffect(parts, ghost) {
        if (parts != null) {
            if (ghost) parts.ghostCount++ else parts.footerCount++
        }
        onDispose {
            if (parts != null) {
                if (ghost) parts.ghostCount-- else parts.footerCount--
            }
        }
    }
}
