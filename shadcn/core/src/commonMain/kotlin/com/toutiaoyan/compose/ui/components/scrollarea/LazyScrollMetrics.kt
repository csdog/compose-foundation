package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.lazy.LazyListLayoutInfo

/** Estimated list geometry in the logical coordinates used to draw the scrollbar. */
internal data class LazyScrollMetrics(
    val value: Int,
    val maxValue: Int,
    val viewportSize: Int,
)

internal fun lazyListScrollMetrics(
    layout: LazyListLayoutInfo,
    firstVisibleIndex: Int,
    firstVisibleOffset: Int,
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
): LazyScrollMetrics? {
    val viewportSize = layout.viewportEndOffset - layout.viewportStartOffset
    val visible = layout.visibleItemsInfo
    val scrollable = canScrollBackward || canScrollForward
    val hasLayout = visible.isNotEmpty() && viewportSize > 0 && layout.totalItemsCount > 0
    if (!scrollable || !hasLayout) return null

    // Offscreen item sizes are unknown. Keep intermediate arithmetic wide for large lists.
    val averageItemSize = visible.sumOf { it.size.toLong() }.toDouble() / visible.size
    val itemStride = (averageItemSize + layout.mainAxisItemSpacing).coerceAtLeast(0.0)
    val estimatedContentSize =
        itemStride * (layout.totalItemsCount - 1) + averageItemSize +
            layout.beforeContentPadding + layout.afterContentPadding
    val maxValue = (estimatedContentSize - viewportSize).toInt().coerceAtLeast(1)
    val value =
        when {
            !canScrollBackward -> 0
            !canScrollForward -> maxValue
            else -> (firstVisibleIndex * itemStride + firstVisibleOffset).toInt().coerceIn(0, maxValue)
        }
    return LazyScrollMetrics(
        value = if (layout.reverseLayout) maxValue - value else value,
        maxValue = maxValue,
        viewportSize = viewportSize,
    )
}
