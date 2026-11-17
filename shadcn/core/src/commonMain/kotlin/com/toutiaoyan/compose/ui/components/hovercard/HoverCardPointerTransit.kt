package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntRect
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionResult
import kotlin.math.max
import kotlin.math.min

internal enum class HoverCardTransitRegion { SharedGap, Approach }

/** Geometry is in Compose window pixels. The main axis grows from the trigger toward the popup. */
internal fun hoverCardTransitRegion(
    point: Offset,
    departure: Offset?,
    trigger: IntRect,
    popup: PopupPositionResult,
    buffer: Float,
): HoverCardTransitRegion? {
    val side = popup.physicalSide
    val vertical = side == PopupPlacement.Top || side == PopupPlacement.Bottom
    val sign = if (side == PopupPlacement.Top || side == PopupPlacement.Start) -1 else 1

    fun main(offset: Offset) = (if (vertical) offset.y else offset.x) * sign

    fun cross(offset: Offset) = if (vertical) offset.x else offset.y

    val panel = popup.bounds
    val triggerEdge =
        if (vertical) max(trigger.top * sign, trigger.bottom * sign) else max(trigger.left * sign, trigger.right * sign)
    val panelEdge =
        if (vertical) min(panel.top * sign, panel.bottom * sign) else min(panel.left * sign, panel.right * sign)
    val primary = main(point)
    if (panelEdge <= triggerEdge || primary < triggerEdge - buffer) return null

    val secondary = cross(point)
    val triggerLow = if (vertical) trigger.left else trigger.top
    val triggerHigh = if (vertical) trigger.right else trigger.bottom
    val panelLow = if (vertical) panel.left else panel.top
    val panelHigh = if (vertical) panel.right else panel.bottom
    val triggerIsNarrower = triggerHigh - triggerLow < panelHigh - panelLow
    val sharedLow = if (triggerIsNarrower) triggerLow else panelLow
    val sharedHigh = if (triggerIsNarrower) triggerHigh else panelHigh
    if (primary <= panelEdge + buffer && secondary in (sharedLow - buffer)..(sharedHigh + buffer)) {
        return HoverCardTransitRegion.SharedGap
    }

    if (departure == null) return null
    val start = cross(departure)
    val towardHigh = start > (panelLow + panelHigh) / 2f
    val startMain = main(departure) - buffer * if (sign < 0) 1.5f else .5f
    val startCenter = if (triggerIsNarrower) start else start + if (towardHigh) buffer * 2f else -buffer * 2f
    val startRadius = if (triggerIsNarrower) buffer / 4f else 0f
    val near = panelEdge + buffer / 2f
    val far = near + (if (vertical) panel.height else panel.width)
    val lowEnd = if (triggerIsNarrower || towardHigh) near else far
    val highEnd = if (triggerIsNarrower || !towardHigh) near else far
    val inside =
        insideApproach(
            Offset(primary, secondary),
            Offset(startMain, startCenter - startRadius),
            Offset(lowEnd, panelLow.toFloat()),
            Offset(highEnd, panelHigh.toFloat()),
            Offset(startMain, startCenter + startRadius),
        )
    return HoverCardTransitRegion.Approach.takeIf { inside }
}

private fun insideApproach(
    point: Offset,
    startLow: Offset,
    endLow: Offset,
    endHigh: Offset,
    startHigh: Offset,
): Boolean {
    fun crosses(
        first: Offset,
        second: Offset,
    ): Boolean =
        (first.y > point.y) != (second.y > point.y) &&
            point.x <= first.x + (point.y - first.y) * (second.x - first.x) / (second.y - first.y)

    return crosses(startLow, endLow) xor crosses(endLow, endHigh) xor
        crosses(endHigh, startHigh) xor crosses(startHigh, startLow)
}
