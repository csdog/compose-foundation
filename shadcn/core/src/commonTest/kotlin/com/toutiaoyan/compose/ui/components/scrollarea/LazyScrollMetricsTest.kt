package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LazyScrollMetricsTest {
    @Test
    fun metrics_null_when_list_is_empty() {
        assertNull(
            metrics(
                visibleItems = emptyList(),
                totalItemsCount = 0,
                viewportStartOffset = 0,
                viewportEndOffset = 200,
                scrollable = false,
            ),
        )
    }

    @Test
    fun metrics_null_when_content_fits_in_viewport() {
        // 2 items of 50px each = 100px content in a 200px viewport -> nothing to scroll.
        val items =
            listOf(
                fakeItem(index = 0, offset = 0, size = 50),
                fakeItem(index = 1, offset = 50, size = 50),
            )
        assertNull(
            metrics(
                visibleItems = items,
                totalItemsCount = 2,
                viewportStartOffset = 0,
                viewportEndOffset = 200,
                scrollable = false,
            ),
        )
    }

    @Test
    fun metrics_estimate_content_from_average_visible_item_size() {
        // 10 items × 40px avg = 400px content; 100px viewport -> maxValue 300.
        // First visible item index 2 at offset 0 -> value = 2 * 40 = 80.
        val items =
            listOf(
                fakeItem(index = 2, offset = 0, size = 40),
                fakeItem(index = 3, offset = 40, size = 40),
                fakeItem(index = 4, offset = 80, size = 40),
            )
        val metrics =
            metrics(
                visibleItems = items,
                totalItemsCount = 10,
                viewportStartOffset = 0,
                viewportEndOffset = 100,
            )!!
        assertEquals(80, metrics.value)
        assertEquals(300, metrics.maxValue)
        assertEquals(100, metrics.viewportSize)
    }

    @Test
    fun metrics_account_for_partial_first_item_offset() {
        // Item 0 scrolled up by 20px (offset = -20) -> value 20.
        val items =
            listOf(
                fakeItem(index = 0, offset = -20, size = 50),
                fakeItem(index = 1, offset = 30, size = 50),
            )
        val metrics =
            metrics(
                visibleItems = items,
                totalItemsCount = 20,
                viewportStartOffset = 0,
                viewportEndOffset = 100,
            )!!
        assertEquals(20, metrics.value)
        // 20 items × 50 = 1000; viewport 100 -> maxValue 900
        assertEquals(900, metrics.maxValue)
    }

    private fun metrics(
        visibleItems: List<LazyListItemInfo>,
        totalItemsCount: Int,
        viewportStartOffset: Int,
        viewportEndOffset: Int,
        scrollable: Boolean = true,
    ): LazyScrollMetrics? =
        lazyListScrollMetrics(
            layout =
                object : LazyListLayoutInfo {
                    override val visibleItemsInfo = visibleItems
                    override val viewportStartOffset = viewportStartOffset
                    override val viewportEndOffset = viewportEndOffset
                    override val totalItemsCount = totalItemsCount
                },
            firstVisibleIndex = visibleItems.firstOrNull()?.index ?: 0,
            firstVisibleOffset = -(visibleItems.firstOrNull()?.offset ?: 0),
            canScrollBackward = scrollable,
            canScrollForward = scrollable,
        )

    private fun fakeItem(
        index: Int,
        offset: Int,
        size: Int,
    ): LazyListItemInfo =
        object : LazyListItemInfo {
            override val index: Int = index
            override val key: Any = index
            override val offset: Int = offset
            override val size: Int = size
            override val contentType: Any? = null
        }
}
