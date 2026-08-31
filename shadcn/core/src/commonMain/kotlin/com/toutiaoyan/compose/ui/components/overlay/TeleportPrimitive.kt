package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.toutiaoyan.compose.ui.theme.currentTheme

/** Always positions its content at the window's origin, letting it measure to full size. */
private object FullWindowPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset = IntOffset.Zero
}

/** Default duration for modal enter/exit, aligned with web shadcn `animate-in` / `animate-out`. */
internal const val OverlayAnimMs = 250

/** Default scrim transitions for [TeleportPrimitiveScope.Overlay]. */
object OverlayTransitions {
    val ScrimEnter: EnterTransition = fadeIn(tween(OverlayAnimMs * 2))
    val ScrimExit: ExitTransition = fadeOut(tween(OverlayAnimMs * 2))
}

/**
 * Scope for [TeleportPrimitive] children — mirrors Vue `<Teleport>` / Radix Portal where
 * `Overlay` and `Content` are separate siblings, not one combined composable.
 */
class TeleportPrimitiveScope internal constructor(
    internal val visibleState: MutableTransitionState<Boolean>,
    internal val onDismissRequest: () -> Unit,
    internal val dismissOnClickOutside: Boolean,
    internal val onPointerDownOutside: (() -> Boolean)?,
    internal val onInteractOutside: (() -> Boolean)?,
)

/**
 * Full-window teleport/portal for modal surfaces (Dialog, AlertDialog, Sheet, Drawer).
 * Built on [Popup] (not [androidx.compose.ui.window.Dialog]) so desktop stays
 * in-page like web shadcn. Keeps the [Popup] mounted until exit transitions
 * finish ([MutableTransitionState]).
 *
 * Compose [Overlay] and [Content] as separate siblings — same split as Radix
 * `Dialog.Overlay` / `Dialog.Content` or Vue Teleport + overlay/content.
 *
 * Usage:
 * ```
 * TeleportPrimitive(visible = open, onDismissRequest = { open = false }) {
 *     Overlay()
 *     Content(enter = DialogTransitions.Enter, exit = DialogTransitions.Exit) {
 *         CardContent("Dialog content")
 *     }
 * }
 * ```
 */
@Composable
fun TeleportPrimitive(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    dismissOnClickOutside: Boolean = true,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    content: @Composable TeleportPrimitiveScope.() -> Unit,
) {
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = visible

    // Stay composed while entering, showing, or exiting — drop only when fully settled closed.
    if (!visibleState.currentState && !visibleState.targetState) return

    Popup(
        popupPositionProvider = FullWindowPositionProvider,
        onDismissRequest = onDismissRequest,
        properties =
            PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                // handled by Overlay so the scrim itself is the hit target
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
    ) {
        val scope =
            TeleportPrimitiveScope(
                visibleState = visibleState,
                onDismissRequest = onDismissRequest,
                // Only dismiss while the overlay is intended to stay open (not mid-exit).
                dismissOnClickOutside = dismissOnClickOutside && visibleState.targetState,
                onPointerDownOutside = onPointerDownOutside,
                onInteractOutside = onInteractOutside,
            )
        Box(modifier = Modifier.fillMaxSize()) {
            scope.content()
        }
    }
}

/**
 * Full-screen scrim (`bg-black/50`). Click dismisses when [TeleportPrimitive.dismissOnClickOutside]
 * is true — Radix `Dialog.Overlay`.
 */
@Composable
fun TeleportPrimitiveScope.Overlay(
    enter: EnterTransition = OverlayTransitions.ScrimEnter,
    exit: ExitTransition = OverlayTransitions.ScrimExit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = enter,
        exit = exit,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(currentTheme.colors.foreground.copy(alpha = 0.5f))
                    .then(
                        if (dismissOnClickOutside) {
                            Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    // Radix: true = preventDefault. Overlay click is
                                    // `onPointerDownOutside` then `onInteractOutside`.
                                    if (onPointerDownOutside?.invoke() != true &&
                                        onInteractOutside?.invoke() != true
                                    ) {
                                        onDismissRequest()
                                    }
                                },
                            )
                        } else {
                            Modifier
                        },
                    ),
        )
    }
}

/**
 * Teleported panel slot. [enter]/[exit] are panel motion only (zoom for Dialog, slide for
 * Sheet/Drawer); scrim motion stays on [Overlay]. Clicks on the panel do not dismiss
 * (non-propagating no-op) — Radix `Dialog.Content`.
 */
@Composable
fun TeleportPrimitiveScope.Content(
    contentAlignment: Alignment = Alignment.Center,
    enter: EnterTransition = EnterTransition.None,
    exit: ExitTransition = ExitTransition.None,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = contentAlignment,
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = enter,
            exit = exit,
        ) {
            Box(
                modifier =
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {},
                    ),
            ) {
                content()
            }
        }
    }
}
