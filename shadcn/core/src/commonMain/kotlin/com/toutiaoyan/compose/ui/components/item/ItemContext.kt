package com.toutiaoyan.compose.ui.components.item

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.staticCompositionLocalOf

@Stable
internal class ItemContext(val size: ItemSize) {
    val descriptions = mutableStateMapOf<Any, Unit>()
}

@Stable
internal class ItemGroupContext {
    val items = mutableStateMapOf<Any, ItemSize>()
    val order = mutableStateListOf<Any>()
}

internal val LocalItemContext = staticCompositionLocalOf<ItemContext?> { null }
internal val LocalItemGroupContext = staticCompositionLocalOf<ItemGroupContext?> { null }
