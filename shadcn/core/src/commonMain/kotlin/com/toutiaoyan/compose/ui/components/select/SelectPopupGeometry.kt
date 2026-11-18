package com.toutiaoyan.compose.ui.components.select

import androidx.compose.ui.geometry.Rect
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

internal enum class SelectPopupAnchor {
    Top,
    Bottom,
    Fixed,
}

internal data class SelectPopupGeometry(
    val top: Float,
    val height: Float,
    val initialScroll: Float,
    val itemAligned: Boolean,
    val growthAnchor: SelectPopupAnchor = SelectPopupAnchor.Fixed,
)

/** Base UI 1.6 SelectPopup's aligned viewport calculation, in window pixels. */
internal fun selectPopupGeometry(
    trigger: Rect,
    windowHeight: Float,
    contentHeight: Float,
    selectedCenter: Float?,
    alignItem: Boolean,
    touch: Boolean,
    sideOffset: Float,
    scale: Float = 1f,
): SelectPopupGeometry {
    val margin = 10f * scale
    val viewportHeight = (windowHeight - 2f * margin).coerceAtLeast(0f)
    if (alignItem && !touch && selectedCenter != null) {
        val idealHeight = windowHeight - margin - trigger.center.y + selectedCenter
        var height = min(viewportHeight, idealHeight)
        val scrollTop = idealHeight - height
        val maxScrollTop = (contentHeight - height).coerceAtLeast(0f)
        val topPositioned = scrollTop >= maxScrollTop - scale
        if (topPositioned) height = min(viewportHeight, contentHeight) - (scrollTop - maxScrollTop)
        val fallback =
            trigger.top < 20f * scale || trigger.bottom > viewportHeight - 20f * scale ||
                ceil(height / scale) * scale + scale < min(contentHeight, 100f * scale)
        if (!fallback) {
            val topOffset = max(0f, viewportHeight - idealHeight)
            val topAnchored = topPositioned && (contentHeight >= viewportHeight - 20f * scale || topOffset == 0f)
            val top =
                if (topPositioned) {
                    if (topAnchored) margin else topOffset + margin
                } else {
                    windowHeight - margin - height
                }
            val scroll = if (topPositioned) (contentHeight - height).coerceAtLeast(0f) else scrollTop
            val anchor =
                when {
                    !topPositioned -> SelectPopupAnchor.Bottom
                    topAnchored -> SelectPopupAnchor.Top
                    else -> SelectPopupAnchor.Fixed
                }
            return SelectPopupGeometry(top, height.coerceAtLeast(0f), scroll, true, anchor)
        }
    }
    // Floating UI's default 5px collision padding, with a 1px preferred-side bias.
    val below = (windowHeight - 5f * scale - trigger.bottom - sideOffset).coerceAtLeast(0f)
    val above = (trigger.top - 6f * scale - sideOffset).coerceAtLeast(0f)
    val bottom = contentHeight <= below || below >= above
    val height = min(contentHeight, if (bottom) below else above)
    val top = if (bottom) trigger.bottom + sideOffset else trigger.top - sideOffset - height
    return SelectPopupGeometry(top.coerceAtLeast(5f * scale), height, 0f, false)
}

internal data class SelectTextEdges(val left: Float, val right: Float)

/** Base UI's Value/ItemText alignment ignores align and alignOffset in aligned mode. */
internal fun selectPopupLeft(
    trigger: Rect,
    popupWidth: Float,
    windowWidth: Float,
    value: Rect?,
    text: SelectTextEdges?,
    rtl: Boolean,
    scale: Float = 1f,
): Float {
    val left =
        if (value != null && text != null) {
            if (rtl) value.right - text.right else value.left - text.left
        } else {
            if (rtl) trigger.right - popupWidth else trigger.left
        }
    val padding = 5f * scale
    return left.coerceAtMost(windowWidth - padding - popupWidth).coerceAtLeast(padding)
}
