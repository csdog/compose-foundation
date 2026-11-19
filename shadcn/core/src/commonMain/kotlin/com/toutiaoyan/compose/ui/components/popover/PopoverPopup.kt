package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionResult
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.overlay.popupFadeModifier
import com.toutiaoyan.compose.ui.components.overlay.NOVA_POPUP_MOTION_MILLIS
import com.toutiaoyan.compose.ui.components.overlay.applyNovaPopupMotion
import com.toutiaoyan.compose.ui.components.overlay.popupGraphicsLayer
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionProvider as AnchoredPositionProvider
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

private val NovaPopoverCollisionPadding = 5.dp

@Composable
internal fun PopoverPopup(
    open: Boolean,
    onDismissRequest: () -> Unit,
    onWindowEscape: () -> Unit,
    placement: PopupPlacement,
    offset: Dp,
    align: PopupAlign,
    alignOffset: Dp,
    properties: PopupProperties,
    enter: EnterTransition,
    exit: ExitTransition,
    content: @Composable () -> Unit,
) {
    val nova = currentTheme.activePreset == StylePreset.Nova
    val defaultEnter = enter === PopupTransitions.Enter
    val defaultExit = exit === PopupTransitions.Exit
    if (open) PopoverWindowEscape(onWindowEscape)
    val visible = remember { MutableTransitionState(false) }
    visible.targetState = open
    if (!visible.currentState && !visible.targetState && visible.isIdle) return

    val geometry = remember { mutableStateOf<PopupPositionResult?>(null) }
    val offsetPx = with(LocalDensity.current) { offset.roundToPx() }
    val alignOffsetPx = with(LocalDensity.current) { alignOffset.roundToPx() }
    val collisionPadding = with(LocalDensity.current) { if (nova) NovaPopoverCollisionPadding.roundToPx() else 0 }
    val positionProvider =
        remember(placement, offsetPx, align, alignOffsetPx, collisionPadding) {
            val anchor = AnchoredPositionProvider(placement, offsetPx, align, alignOffsetPx)
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize,
                ): IntOffset {
                    val spareWidth = ((windowSize.width - popupContentSize.width) / 2).coerceAtLeast(0)
                    val spareHeight = ((windowSize.height - popupContentSize.height) / 2).coerceAtLeast(0)
                    val inset = IntOffset(minOf(collisionPadding, spareWidth), minOf(collisionPadding, spareHeight))
                    val available = IntSize(windowSize.width - 2 * inset.x, windowSize.height - 2 * inset.y)
                    val position =
                        anchor.calculatePositionResult(
                            anchorBounds.translate(-inset),
                            available,
                            layoutDirection,
                            popupContentSize,
                        )
                    geometry.value = position
                    return position.offset + inset
                }
            }
        }

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = { if (open) onDismissRequest() },
        properties = properties,
    ) {
        AnimatedVisibility(
            visibleState = visible,
            enter = if (defaultEnter) EnterTransition.None else enter,
            exit = if (defaultExit) ExitTransition.None else exit,
        ) {
            val motion =
                if (nova && (defaultEnter || defaultExit)) {
                    val progress =
                        transition.animateFloat(
                            transitionSpec = { tween(NOVA_POPUP_MOTION_MILLIS) },
                            label = "Popover opacity and scale",
                        ) { state ->
                            when (state) {
                                EnterExitState.PreEnter -> if (defaultEnter) 0f else 1f
                                EnterExitState.Visible -> 1f
                                EnterExitState.PostExit -> if (defaultExit) 0f else 1f
                            }
                        }
                    val slide =
                        if (defaultEnter) {
                            transition.animateFloat(
                                transitionSpec = { tween(NOVA_POPUP_MOTION_MILLIS) },
                                label = "Popover entry displacement",
                            ) { state -> if (state == EnterExitState.PreEnter) 1f else 0f }
                        } else {
                            null
                        }
                    Modifier.popupGraphicsLayer {
                        applyNovaPopupMotion(geometry.value, progress.value, slide?.value ?: 0f, defaultEnter)
                    }
                } else {
                    popupFadeModifier(defaultEnter, defaultExit)
                }
            Box(motion, propagateMinConstraints = true) { content() }
        }
    }
}
