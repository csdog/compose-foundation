package com.toutiaoyan.compose.ui.components.select

import androidx.compose.ui.geometry.Rect
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val ALIGNED_WINDOW_MARGIN_DP = 10f
private const val ALIGNED_TRIGGER_INSET_DP = 20f
private const val MINIMUM_ALIGNED_CONTENT_HEIGHT_DP = 100f
private const val FLOATING_COLLISION_PADDING_DP = 5f
private const val FLOATING_PREFERRED_SIDE_BIAS_DP = 1f

internal fun selectAlignedViewportHeight(
    windowHeight: Float,
    density: Float,
): Float = (windowHeight - 2f * (ALIGNED_WINDOW_MARGIN_DP * density)).coerceAtLeast(0f)

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
    val margin = ALIGNED_WINDOW_MARGIN_DP * scale
    val viewportHeight = selectAlignedViewportHeight(windowHeight, scale)
    val triggerInset = ALIGNED_TRIGGER_INSET_DP * scale
    if (alignItem && !touch && selectedCenter != null) {
        val idealHeight = windowHeight - margin - trigger.center.y + selectedCenter
        var height = min(viewportHeight, idealHeight)
        val scrollTop = idealHeight - height
        val maxScrollTop = (contentHeight - height).coerceAtLeast(0f)
        val topPositioned = scrollTop >= maxScrollTop - scale
        if (topPositioned) height = min(viewportHeight, contentHeight) - (scrollTop - maxScrollTop)
        val fallback =
            trigger.top < triggerInset || trigger.bottom > viewportHeight - triggerInset ||
                ceil(height / scale) * scale + scale < min(contentHeight, MINIMUM_ALIGNED_CONTENT_HEIGHT_DP * scale)
        if (!fallback) {
            val topOffset = max(0f, viewportHeight - idealHeight)
            val topAnchored = topPositioned && (contentHeight >= viewportHeight - 2f * margin || topOffset == 0f)
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
    val padding = FLOATING_COLLISION_PADDING_DP * scale
    val below = (windowHeight - padding - trigger.bottom - sideOffset).coerceAtLeast(0f)
    val preferredSidePadding = (FLOATING_COLLISION_PADDING_DP + FLOATING_PREFERRED_SIDE_BIAS_DP) * scale
    val above = (trigger.top - preferredSidePadding - sideOffset).coerceAtLeast(0f)
    val bottom = contentHeight <= below || below >= above
    val height = min(contentHeight, if (bottom) below else above)
    val top = if (bottom) trigger.bottom + sideOffset else trigger.top - sideOffset - height
    return SelectPopupGeometry(top.coerceAtLeast(padding), height, 0f, false)
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
    val padding = FLOATING_COLLISION_PADDING_DP * scale
    return left.coerceAtMost(windowWidth - padding - popupWidth).coerceAtLeast(padding)
}
