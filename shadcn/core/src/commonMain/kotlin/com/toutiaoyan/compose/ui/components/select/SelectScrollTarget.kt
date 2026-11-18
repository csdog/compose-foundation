package com.toutiaoyan.compose.ui.components.select

internal data class SelectScrollItemBounds(val top: Float, val height: Float)

/** Base UI 1.6 SelectScrollArrow: reveal the next item beyond the 24px overlay. */
internal fun selectScrollTarget(
    items: List<SelectScrollItemBounds>,
    up: Boolean,
    scroll: Float,
    viewport: Float,
    maxScroll: Float,
    scale: Float = 1f,
): Float {
    if (items.isEmpty()) return scroll
    val overlay = 24f * scale
    if (up) {
        val first = items.indexOfFirst { it.top >= scroll + overlay - scale }.coerceAtLeast(0)
        val target = (first - 1).coerceAtLeast(0)
        return if (target < first) (items[target].top - overlay).coerceIn(0f, maxScroll) else 0f
    }
    val firstClipped = items.indexOfFirst { it.top + it.height > scroll + viewport - overlay + scale }
    val last = if (firstClipped < 0) items.lastIndex else (firstClipped - 1).coerceAtLeast(0)
    val target = (last + 1).coerceAtMost(items.lastIndex)
    return if (target > last) {
        (items[target].top + items[target].height - viewport + overlay).coerceIn(0f, maxScroll)
    } else {
        maxScroll
    }
}
