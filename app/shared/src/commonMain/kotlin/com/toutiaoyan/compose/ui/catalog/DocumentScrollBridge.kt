package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.ScrollState
import kotlin.math.abs
import kotlin.math.roundToInt

private const val HALF_COMPOSE_PIXEL = 0.5

/** Position policy for one bound browser document entry; the binding owns its lifetime. */
class DocumentScrollBridge(
    private val state: ScrollState,
    private val pixelsPerCssPixel: Float,
    restorePosition: Double?,
) {
    private var pendingRestore = restorePosition?.let { (it * pixelsPerCssPixel).roundToInt() }
    private var expectedPosition = state.value
    private var publishedComposePosition = state.value
    private var publishedNativePosition: Double? = null
    private var pendingNativePosition: Double? = null
    private var nativeObservedComposePosition = state.value
    private val tolerance = HALF_COMPOSE_PIXEL / pixelsPerCssPixel

    /** Retry an owned restore as the extent grows, without retaining a scroll mutation. */
    suspend fun updateCompose() {
        pendingRestore?.let { target ->
            if (state.value != expectedPosition || state.isScrollInProgress) {
                pendingRestore = null
            } else {
                state.scrollTo(target)
                expectedPosition = state.value
                if (state.value >= target) pendingRestore = null
            }
        }
    }

    /** A bridge echo is not user input; native input cancels restoration before async feedback. */
    fun nativeScroll(position: Double): Int? {
        val matchesPublication = publishedNativePosition?.let { abs(position - it) <= tolerance } == true
        if (pendingNativePosition == null && matchesPublication) return null
        pendingRestore = null
        pendingNativePosition = position
        nativeObservedComposePosition = state.value
        return (position * pixelsPerCssPixel).roundToInt()
    }

    fun canPublishCompose(): Boolean = pendingNativePosition == null || state.value != nativeObservedComposePosition

    fun publishedNative(position: Double) {
        publishedComposePosition = state.value
        publishedNativePosition = position
        pendingNativePosition = null
    }

    private fun ownedRestorePosition(): Double? {
        if (state.value != expectedPosition || state.isScrollInProgress) pendingRestore = null
        return pendingRestore?.let { it / pixelsPerCssPixel.toDouble() }
    }

    /** No browser read: popstate has already switched the native entry and its scroll position. */
    fun positionForDeparture(): Double =
        pendingNativePosition?.takeIf { state.value == nativeObservedComposePosition }
            ?: ownedRestorePosition()
            ?: (state.value / pixelsPerCssPixel.toDouble())

    /** Call only while the browser still owns this binding's entry, never for a destination. */
    fun positionForCapture(nativePosition: Double): Double {
        val ownedPosition = positionForDeparture()
        val published = publishedNativePosition
        val isNativeEcho = published != null && abs(nativePosition - published) <= tolerance
        return when {
            pendingNativePosition != null && state.value == nativeObservedComposePosition -> nativePosition
            // An unpublished Compose advance is newer than its native echo or queued native feedback.
            state.value != publishedComposePosition || published == null -> ownedPosition
            isNativeEcho -> ownedPosition
            else -> {
                pendingRestore = null
                nativePosition
            }
        }
    }
}
