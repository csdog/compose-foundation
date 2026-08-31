package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo

/**
 * Estimated scroll metrics for a lazy list, in the same `(value, maxValue, viewportSize)`
 * shape [ScrollThumb] expects from `ScrollState`.
 *
 * Lazy lists do not expose a true content height, so size is estimated from the average
 * size of currently visible items times [LazyListLayoutInfo.totalItemsCount].
 */
internal data class LazyScrollMetrics(
    val value: Int,
    val maxValue: Int,
    val viewportSize: Int,
)

/**
 * Derives [LazyScrollMetrics] from a lazy list's layout snapshot.
 * Returns `null` when there is nothing to scroll (empty list, no visible items, or
 * content fits entirely in the viewport).
 */
internal fun lazyListScrollMetrics(
    visibleItems: List<LazyListItemInfo>,
    totalItemsCount: Int,
    viewportStartOffset: Int,
    viewportEndOffset: Int,
): LazyScrollMetrics? {
    if (visibleItems.isEmpty() || totalItemsCount <= 0) return null

    val viewportSize = viewportEndOffset - viewportStartOffset
    if (viewportSize <= 0) return null

    val averageItemSize =
        visibleItems
            .map { it.size }
            .average()
            .toFloat()
            .coerceAtLeast(1f)
    val estimatedContentSize = averageItemSize * totalItemsCount
    val maxValue = (estimatedContentSize - viewportSize).toInt().coerceAtLeast(0)
    if (maxValue <= 0) return null

    val firstVisible = visibleItems.first()
    val value =
        (firstVisible.index * averageItemSize - firstVisible.offset)
            .toInt()
            .coerceIn(0, maxValue)

    return LazyScrollMetrics(
        value = value,
        maxValue = maxValue,
        viewportSize = viewportSize,
    )
}

internal fun lazyListScrollMetrics(layoutInfo: LazyListLayoutInfo): LazyScrollMetrics? =
    lazyListScrollMetrics(
        visibleItems = layoutInfo.visibleItemsInfo,
        totalItemsCount = layoutInfo.totalItemsCount,
        viewportStartOffset = layoutInfo.viewportStartOffset,
        viewportEndOffset = layoutInfo.viewportEndOffset,
    )
