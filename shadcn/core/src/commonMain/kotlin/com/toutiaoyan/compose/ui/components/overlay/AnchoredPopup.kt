package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
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
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = open

    if (!visibleState.currentState && !visibleState.targetState) return

    val density = LocalDensity.current
    val offsetPx = with(density) { offset.roundToPx() }
    val alignOffsetPx = with(density) { alignOffset.roundToPx() }
    val positionProvider =
        remember(placement, offsetPx, align, alignOffsetPx) {
            PopupPositionProvider(placement, offsetPx, align, alignOffsetPx)
        }

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties =
            PopupProperties(
                focusable = focusable,
                dismissOnBackPress = focusable,
                // Only honor outside dismiss while open — ignore mid-exit clicks.
                dismissOnClickOutside = dismissOnClickOutside && visibleState.targetState,
            ),
        content = {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = enter,
                exit = exit,
            ) {
                content()
            }
        },
    )
}
