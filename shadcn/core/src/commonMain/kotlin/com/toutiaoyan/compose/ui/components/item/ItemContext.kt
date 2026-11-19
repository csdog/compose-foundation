package com.toutiaoyan.compose.ui.components.item

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy

@Stable
internal class ItemContext(val size: ItemSize) {
    val descriptions = mutableStateMapOf<Any, Unit>()
}

@Stable
internal class ItemGroupContext {
    val items = mutableStateMapOf<Any, ItemSize>()
    private val registrationOrder = mutableStateListOf<Any>()
    private val placedTops = mutableStateMapOf<Any, Float>()
    private val rowIndices by derivedStateOf(structuralEqualityPolicy()) {
        val ordered =
            if (registrationOrder.all { it in placedTops }) {
                registrationOrder.sortedBy { placedTops.getValue(it) }
            } else {
                registrationOrder
            }
        ordered.withIndex().associate { it.value to it.index }
    }

    fun register(
        token: Any,
        size: ItemSize,
    ) {
        if (token !in items) registrationOrder.add(token)
        items[token] = size
    }

    fun remove(token: Any) {
        items.remove(token)
        registrationOrder.remove(token)
        placedTops.remove(token)
    }

    fun place(
        token: Any,
        top: Float,
    ) {
        if (token in items) placedTops[token] = top
    }

    fun rowIndex(token: Any): Int? = rowIndices[token]
}

internal val LocalItemContext = staticCompositionLocalOf<ItemContext?> { null }
internal val LocalItemGroupContext = staticCompositionLocalOf<ItemGroupContext?> { null }
