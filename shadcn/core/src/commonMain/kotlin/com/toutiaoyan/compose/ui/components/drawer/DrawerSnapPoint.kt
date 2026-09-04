package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Base UI / base-nova drawer snap point.
 *
 * - [Fraction]: `0f..1f` of the viewport (along the swipe axis).
 * - [Absolute]: fixed size in [Dp] (Base treats numbers `> 1` as CSS pixels).
 */
@Immutable
sealed class DrawerSnapPoint {
    data class Fraction(val value: Float) : DrawerSnapPoint() {
        init {
            require(value in 0f..1f) { "Fraction snap points must be in 0f..1f, got $value" }
        }
    }

    data class Absolute(val size: Dp) : DrawerSnapPoint() {
        init {
            require(size >= 0.dp) { "Absolute snap points must be >= 0.dp, got $size" }
        }
    }

    companion object {
        /** Base UI number rule: `<= 1` → fraction of viewport; `> 1` → CSS px. */
        fun fromNumber(value: Float): DrawerSnapPoint = if (value <= 1f) Fraction(value) else Absolute(value.dp)
    }
}

/** Resolves this snap to pixels against [viewportPx] (height for vertical drawers). */
fun DrawerSnapPoint.resolvePx(
    viewportPx: Float,
    density: Density,
): Float =
    when (this) {
        is DrawerSnapPoint.Fraction -> viewportPx * value
        is DrawerSnapPoint.Absolute -> with(density) { size.toPx() }
    }

/**
 * Index into [snapPoints], or `snapPoints.size` for the dismissed (closed) anchor.
 * Offset along the closing axis increases toward dismiss.
 */
internal fun drawerSnapClosingOffsets(
    snapHeightsPx: List<Float>,
    maxHeightPx: Float,
): List<Float> {
    if (snapHeightsPx.isEmpty() || maxHeightPx <= 0f) return emptyList()
    val openOffsets =
        snapHeightsPx.map { height ->
            (maxHeightPx - height).coerceAtLeast(0f)
        }
    return openOffsets + maxHeightPx
}

/** Pick the nearest snap / closed target from a closing [offsetPx]. */
internal fun nearestDrawerSnapIndex(
    offsetPx: Float,
    closingOffsets: List<Float>,
    snapToSequentialPoints: Boolean,
    fromIndex: Int,
    velocityClosing: Float,
): Int {
    if (closingOffsets.isEmpty()) return 0
    if (closingOffsets.size == 1) return 0
    if (snapToSequentialPoints) {
        // Distance-only: move at most one step toward the drag direction.
        val next =
            when {
                velocityClosing > 0f || offsetPx > closingOffsets[fromIndex.coerceIn(0, closingOffsets.lastIndex)] ->
                    (fromIndex + 1).coerceAtMost(closingOffsets.lastIndex)
                velocityClosing < 0f || offsetPx < closingOffsets[fromIndex.coerceIn(0, closingOffsets.lastIndex)] ->
                    (fromIndex - 1).coerceAtLeast(0)
                else -> fromIndex.coerceIn(0, closingOffsets.lastIndex)
            }
        // Still choose by proximity between from and next when mid-drag settle has no velocity.
        if (velocityClosing == 0f) {
            val a = fromIndex.coerceIn(0, closingOffsets.lastIndex)
            val b = next
            return listOf(a, b).minBy { kotlin.math.abs(closingOffsets[it] - offsetPx) }
        }
        return next
    }
    return closingOffsets.indices.minBy { kotlin.math.abs(closingOffsets[it] - offsetPx) }
}
