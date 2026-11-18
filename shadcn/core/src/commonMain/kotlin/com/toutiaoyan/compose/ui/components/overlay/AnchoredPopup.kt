package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/**
 * Default fade for anchored panels (Dropdown / Popover / Tooltip / …), matching
 * Compose Unstyled docs' typical `fadeIn()` / `fadeOut()` on menu & tooltip panels.
 */
object PopupTransitions {
    val Enter: EnterTransition = fadeIn(tween(OverlayAnimMs))
    val Exit: ExitTransition = fadeOut(tween(OverlayAnimMs))
}

/**
 * The shared building block for every anchor-positioned overlay (Popover, DropdownMenu,
 * Tooltip, ContextMenu, HoverCard, Combobox/Select dropdowns, Menubar, NavigationMenu):
 * a [Popup] placed relative to the composable it's called from (its "anchor"), flipped
 * to stay on-screen via [PopupPositionProvider], dismissed on outside click or
 * Escape by default -- matching real shadcn/ui's shared Radix `Popper` primitive.
 *
 * Keeps the [Popup] mounted until [exit] finishes ([MutableTransitionState]), so
 * dismiss is not an instant cut — same pattern as [TeleportPrimitive] / Unstyled's
 * DropdownMenuPanel.
 *
 * Call this from inside the anchor's own composition scope (e.g. right after the
 * trigger `Row`/`Box` in the same parent) so the [Popup] measures relative to it.
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 * Box {
 *     Button("Open", onClick = { open = true })
 *     AnchoredPopup(open = open, onDismissRequest = { open = false }) {
 *         Text { "Popover content" }
 *     }
 * }
 * ```
 */
@Composable
fun AnchoredPopup(
    open: Boolean,
    onDismissRequest: () -> Unit,
    placement: PopupPlacement = PopupPlacement.Bottom,
    offset: Dp = 4.dp,
    align: PopupAlign = PopupAlign.Start,
    alignOffset: Dp = 0.dp,
    dismissOnClickOutside: Boolean = true,
    // Tooltip/HoverCard pass false: a hover-triggered popup has nothing worth tabbing
    // into and must never take keyboard focus away from whatever the user was actually
    // interacting with. Worse, a focusable popup competes with the trigger's own
    // hoverable() for pointer/focus ownership the instant it opens -- on desktop this
    // observably flickers the popup open/closed in a loop, because taking focus can
    // itself perturb the hover state that decided to open the popup in the first
    // place. Popover/DropdownMenu/Dialog/etc. still default to true: those are
    // click-triggered and their content (menu items, form fields) genuinely needs
    // keyboard focus and Escape-to-dismiss.
    focusable: Boolean = true,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable () -> Unit,
) {
    PositionedAnchoredPopup(
        open, onDismissRequest, placement, offset, align, alignOffset,
        dismissOnClickOutside, focusable, enter, exit,
        onPositioned = null,
    ) { content() }
}

@Composable
internal fun PositionedAnchoredPopup(
    open: Boolean,
    onDismissRequest: () -> Unit,
    placement: PopupPlacement,
    offset: Dp,
    align: PopupAlign,
    alignOffset: Dp,
    dismissOnClickOutside: Boolean,
    focusable: Boolean,
    enter: EnterTransition,
    exit: ExitTransition,
    onPositioned: ((PopupPositionResult) -> Unit)?,
    retainInterruptedEnter: Boolean = false,
    freezePosition: Boolean = false,
    resetExitOnOpen: Boolean = false,
    horizontalPosition: PopupHorizontalPosition? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val visibility = remember { PopupVisibilityMemory() }
    if (resetExitOnOpen && open && !visibility.wasOpen) {
        // Entry is owned externally: an interrupted caller exit must not leak into it.
        visibility.state = MutableTransitionState(true)
    }
    visibility.wasOpen = open
    val visibleState = visibility.state
    visibleState.targetState = open

    if (!visibleState.currentState && !visibleState.targetState &&
        (!retainInterruptedEnter || visibleState.isIdle)
    ) {
        return
    }

    RawAnchoredPopup(
        onDismissRequest,
        placement,
        offset,
        align,
        alignOffset,
        dismissOnClickOutside && visibleState.targetState,
        focusable,
        onPositioned,
        freezePosition = freezePosition,
        horizontalPosition = horizontalPosition,
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = enter,
            exit = exit,
        ) {
            content()
        }
    }
}

/** Receives popup width, window width, and the physical layout direction. */
internal typealias PopupHorizontalPosition = (Int, Int, LayoutDirection) -> Int

private class PopupVisibilityMemory {
    var state = MutableTransitionState(false)
    var wasOpen = false
}

private class PopupPositionMemory {
    var last: PopupPositionResult? = null
}

/** Placement only: the caller owns mounting, motion, and input lifetime. */
@Composable
internal fun RawAnchoredPopup(
    onDismissRequest: () -> Unit,
    placement: PopupPlacement,
    offset: Dp,
    align: PopupAlign,
    alignOffset: Dp,
    dismissOnClickOutside: Boolean,
    focusable: Boolean,
    onPositioned: ((PopupPositionResult) -> Unit)?,
    freezePosition: Boolean = false,
    horizontalPosition: PopupHorizontalPosition? = null,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val offsetPx = with(density) { offset.roundToPx() }
    val alignOffsetPx = with(density) { alignOffset.roundToPx() }
    val positionMemory = remember { PopupPositionMemory() }
    val positionProvider =
        remember(placement, offsetPx, align, alignOffsetPx, onPositioned, freezePosition, horizontalPosition) {
            val delegate = PopupPositionProvider(placement, offsetPx, align, alignOffsetPx)
            object : androidx.compose.ui.window.PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: androidx.compose.ui.unit.IntRect,
                    windowSize: androidx.compose.ui.unit.IntSize,
                    layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                    popupContentSize: androidx.compose.ui.unit.IntSize,
                ): androidx.compose.ui.unit.IntOffset {
                    val result =
                        positionMemory.last?.takeIf { freezePosition }
                            ?: delegate.calculatePositionResult(
                                anchorBounds,
                                windowSize,
                                layoutDirection,
                                popupContentSize,
                            ).let { positioned ->
                                val left =
                                    horizontalPosition?.invoke(
                                        popupContentSize.width,
                                        windowSize.width,
                                        layoutDirection,
                                    )
                                if (left == null) {
                                    positioned
                                } else {
                                    val offset = IntOffset(left, positioned.offset.y)
                                    positioned.copy(
                                        offset = offset,
                                        bounds = IntRect(offset, popupContentSize),
                                        transformOrigin =
                                            TransformOrigin(
                                                positioned.transformOrigin.pivotFractionX +
                                                    (positioned.offset.x - left).toFloat() /
                                                    popupContentSize.width.coerceAtLeast(1),
                                                positioned.transformOrigin.pivotFractionY,
                                            ),
                                    )
                                }
                            }.also { positionMemory.last = it }
                    onPositioned?.invoke(result)
                    return result.offset
                }
            }
        }

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties =
            PopupProperties(
                focusable = focusable,
                dismissOnBackPress = focusable,
                dismissOnClickOutside = dismissOnClickOutside,
            ),
        content = content,
    )
}
