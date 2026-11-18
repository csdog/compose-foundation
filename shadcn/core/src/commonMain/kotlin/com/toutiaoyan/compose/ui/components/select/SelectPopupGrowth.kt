package com.toutiaoyan.compose.ui.components.select

import kotlin.math.ceil
import kotlin.math.min

internal data class SelectPopupGrowth(val geometry: SelectPopupGeometry, val scroll: Float)

/** Base UI 1.6 SelectPopup.handleScroll consumes scroll into the anchored viewport. */
internal fun selectPopupGrowth(
    geometry: SelectPopupGeometry,
    windowHeight: Float,
    scroll: Float,
    maxScroll: Float,
    maxHeight: Float = Float.POSITIVE_INFINITY,
    scale: Float = 1f,
): SelectPopupGrowth? {
    val availableHeight = min(selectAlignedViewportHeight(windowHeight, scale), maxHeight)
    if (!geometry.itemAligned || geometry.growthAnchor == SelectPopupAnchor.Fixed ||
        geometry.height >= availableHeight || maxScroll <= 0f
    ) {
        return null
    }
    val topAnchored = geometry.growthAnchor == SelectPopupAnchor.Top
    val diff = if (topAnchored) maxScroll - scroll else scroll
    if (diff <= 0f) return null
    val nextHeight = min(geometry.height + diff, availableHeight)
    val height =
        if (diff <= scale) nextHeight else (ceil(nextHeight / scale) * scale).coerceAtMost(availableHeight)
    val growth = height - geometry.height
    val nextMax = (maxScroll - growth).coerceAtLeast(0f)
    return SelectPopupGrowth(
        geometry.copy(
            top = if (topAnchored) geometry.top else geometry.top - growth,
            height = height,
        ),
        (if (topAnchored) scroll else scroll - growth).coerceIn(0f, nextMax),
    )
}
