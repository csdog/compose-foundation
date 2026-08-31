package com.toutiaoyan.compose.ui.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Platform entries offer raw URIs; [com.toutiaoyan.compose.ui.App] collects after
 * the nav stack is attached.
 *
 * Uses a [Channel] so a cold-start system callback that fires before Compose
 * collects is queued and delivered once.
 */
class InboundDeepLinks {
    private val channel =
        Channel<String>(
            capacity = 16,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    val events: Flow<String> = channel.receiveAsFlow()

    fun offer(uri: String) {
        val trimmed = uri.trim()
        if (trimmed.isEmpty()) return
        channel.trySend(trimmed)
    }
}
