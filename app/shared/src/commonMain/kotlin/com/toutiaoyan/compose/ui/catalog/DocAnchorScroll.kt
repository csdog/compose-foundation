package com.toutiaoyan.compose.ui.catalog

import androidx.compose.animation.core.animate
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

/** An explicit anchor click replaces an active wheel seek, but later user input can interrupt it. */
internal suspend fun ScrollState.seekDocumentAnchor(
    target: Int,
    animated: Boolean,
) {
    scroll(MutatePriority.UserInput) {
        val distance = (target - value).toFloat()
        if (animated) {
            var consumed = 0f
            animate(0f, distance) { current, _ ->
                consumed += scrollBy(current - consumed)
            }
        } else {
            scrollBy(distance)
        }
    }
}

/** Follow reflow only while this navigation still owns its last automatic position. */
internal suspend fun ScrollState.followDocumentAnchor(
    anchors: DocAnchors,
    id: String,
    animated: Boolean,
) {
    val initialPosition = value
    val target =
        if (animated) {
            anchors.awaitOffsetOf(id)
        } else {
            coroutineScope {
                val anchor = async { anchors.awaitOffsetOf(id) }
                val userMove =
                    launch(start = CoroutineStart.UNDISPATCHED) {
                        snapshotFlow { value to isScrollInProgress }
                            .first { (position, scrolling) -> position != initialPosition || scrolling }
                        anchor.cancel()
                    }
                try {
                    anchor.await()
                } finally {
                    userMove.cancel()
                }
            }
        }
    if (animated) {
        seekDocumentAnchor(target, animated = true)
    } else {
        if (value != initialPosition || isScrollInProgress) return
        scroll { scrollBy((target - value).toFloat()) }
    }
    var expectedPosition = value
    merge(anchors.changes, snapshotFlow { value to isScrollInProgress }.map { Unit })
        .takeWhile { value == expectedPosition && !isScrollInProgress }
        .collect {
            anchors.offsetOf(id)?.let { target ->
                // Default priority never displaces active user input or native scrolling.
                scroll {
                    if (value == expectedPosition) {
                        scrollBy((target - value).toFloat())
                        expectedPosition = value
                    }
                }
            }
        }
}
