package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput

/** Makes touch metadata available to the click handler during the release's Main pass. */
internal class TouchRelease<T : Any>(private val capture: (PointerEvent) -> T?) {
    var value: T? = null
        private set

    fun track(modifier: Modifier): Modifier =
        modifier.pointerInput(this) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                if (down.type != PointerType.Touch) return@awaitEachGesture
                val release = capture(currentEvent) ?: return@awaitEachGesture
                if (waitForUpOrCancellation(pass = PointerEventPass.Initial) == null) return@awaitEachGesture
                value = release
                try {
                    awaitPointerEvent(PointerEventPass.Final)
                } finally {
                    value = null
                }
            }
        }
}
