package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.unit.dp

internal const val NOVA_POPUP_MOTION_MILLIS = 100

private const val NOVA_POPUP_HIDDEN_SCALE = 0.95f
private const val NOVA_POPUP_SCALE_RANGE = 0.05f
private val NovaPopupSlideDistance = 8.dp

internal fun GraphicsLayerScope.applyNovaPopupMotion(
    position: PopupPositionResult?,
    fraction: Float,
    slide: Float,
    waitForPosition: Boolean,
) {
    alpha = if (waitForPosition && position == null) 0f else fraction
    scaleX = NOVA_POPUP_HIDDEN_SCALE + NOVA_POPUP_SCALE_RANGE * fraction
    scaleY = scaleX
    if (position != null) transformOrigin = position.transformOrigin
    val displacement = slide * NovaPopupSlideDistance.toPx()
    translationX =
        when (position?.physicalSide) {
            PopupPlacement.Start -> displacement
            PopupPlacement.End -> -displacement
            else -> 0f
        }
    translationY =
        when (position?.physicalSide) {
            PopupPlacement.Top -> displacement
            PopupPlacement.Bottom -> -displacement
            else -> 0f
        }
}
