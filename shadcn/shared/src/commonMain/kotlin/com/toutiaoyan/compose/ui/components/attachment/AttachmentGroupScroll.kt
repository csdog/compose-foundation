package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch

@Composable
internal fun rememberAttachmentGroupFling(snap: AttachmentGroupSnapInfo): FlingBehavior {
    val snapping = rememberSnapFlingBehavior(snap)
    return remember(snap, snapping) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float =
                with(snapping) {
                    // Wheel events supply their motion already, including the final snap approach.
                    performFling(if (snap.wheelInput) 0f else initialVelocity)
                }
        }
    }
}

@Composable
internal fun rememberAttachmentScrollContainment(scrollState: ScrollState): NestedScrollConnection =
    remember(scrollState) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset = if (scrollState.maxValue > 0) Offset(available.x, 0f) else Offset.Zero

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity = if (scrollState.maxValue > 0) Velocity(available.x, 0f) else Velocity.Zero
        }
    }

internal fun Modifier.trackAttachmentWheelInput(snap: AttachmentGroupSnapInfo): Modifier =
    pointerInput(snap) {
        awaitPointerEventScope {
            while (true) {
                when (awaitPointerEvent(PointerEventPass.Initial).type) {
                    PointerEventType.Scroll -> snap.wheelInput = true
                    PointerEventType.Press -> snap.wheelInput = false
                }
            }
        }
    }

@Composable
internal fun Modifier.attachmentGroupKeyboardScroll(
    scrollState: ScrollState,
    snap: AttachmentGroupSnapInfo,
    direction: LayoutDirection,
): Modifier {
    val scope = rememberCoroutineScope()
    return onKeyEvent { event ->
        val arrow = event.horizontalArrow()
        if (arrow == null || scrollState.maxValue == 0) {
            false
        } else {
            val forward = (arrow == Key.DirectionRight) == (direction == LayoutDirection.Ltr)
            scope.launch { scrollState.animateScrollTo(snap.keyboardTarget(forward)) }
            true
        }
    }
}

private fun KeyEvent.horizontalArrow(): Key? {
    val modified = isAltPressed || isCtrlPressed || isMetaPressed || isShiftPressed
    if (type != KeyEventType.KeyDown || modified) return null
    return key.takeIf { it == Key.DirectionRight || it == Key.DirectionLeft }
}
