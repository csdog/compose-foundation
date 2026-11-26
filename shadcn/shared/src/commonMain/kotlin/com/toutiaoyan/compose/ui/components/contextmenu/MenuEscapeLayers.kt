package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.runtime.staticCompositionLocalOf

/** Logical open layers, independent of popup exit animations or native window focus. */
internal class MenuEscapeLayers {
    private data class Entry(val depth: Int, val close: () -> Unit)

    private val entries = mutableListOf<Entry>()

    fun register(
        depth: Int,
        close: () -> Unit,
    ): () -> Unit {
        val entry = Entry(depth, close)
        entries += entry
        return { entries.remove(entry) }
    }

    fun closeDeepest(): Boolean {
        val entry =
            entries.withIndex()
                .maxWithOrNull(compareBy({ it.value.depth }, { it.index }))?.value ?: return false
        entry.close()
        return true
    }
}

internal val LocalMenuEscapeLayers = staticCompositionLocalOf<MenuEscapeLayers?> { null }
internal val LocalMenuEscapeDepth = staticCompositionLocalOf { 0 }
