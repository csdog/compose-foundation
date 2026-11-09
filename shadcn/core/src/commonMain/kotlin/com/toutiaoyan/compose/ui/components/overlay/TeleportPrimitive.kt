package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
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

/** Opt-in for nested surfaces whose exiting native window still receives outside clicks. */
internal val LocalOverlayExitOutsideDismiss = staticCompositionLocalOf<(() -> Unit)?> { null }

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
    internal val childHost: OverlayHostState?,
    internal val onExitOutsideDismiss: (() -> Unit)? = null,
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
    if (!visibleState.currentState && !visibleState.targetState && visibleState.isIdle) return

    val modal = LocalOverlayModal.current
    val host = LocalOverlayHost.current
    val focusState = LocalOverlayFocus.current
    val exitOutsideDismiss = LocalOverlayExitOutsideDismiss.current
    val layer = rememberGraphicsLayer()
    val childHost = remember(layer, host) { if (host != null) OverlayHostState(layer) else null }
    val body: @Composable () -> Unit = {
        DisposableEffect(host, childHost, modal) {
            if (!modal && childHost != null) host?.branches?.add(childHost)
            onDispose { host?.branches?.remove(childHost) }
        }
        DisposableEffect(focusState) {
            focusState?.presented = true
            onDispose { focusState?.presented = false }
        }
        val scope =
            TeleportPrimitiveScope(
                visibleState = visibleState,
                onDismissRequest = onDismissRequest,
                // Only dismiss while the overlay is intended to stay open (not mid-exit).
                dismissOnClickOutside = dismissOnClickOutside && visibleState.targetState,
                onPointerDownOutside = onPointerDownOutside,
                onInteractOutside = onInteractOutside,
                childHost = childHost,
                onExitOutsideDismiss = exitOutsideDismiss,
            )
        Box(
            modifier =
                Modifier.fillMaxSize().then(
                    if (modal) Modifier.observeOverlayOutside(childHost) else Modifier,
                ).onPreviewKeyEvent {
                        event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Escape && visibleState.targetState) {
                        onDismissRequest()
                        true
                    } else {
                        false
                    }
                },
        ) {
            Box(
                Modifier.fillMaxSize().drawWithContent {
                    layer.record {
                        if (host != null) drawLayer(host.background)
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(layer)
                },
            ) {
                // Exit handoff belongs only to this portal. Nested surfaces must opt in
                // themselves, so an ordinary Dialog inside a Drawer cannot inherit it.
                androidx.compose.runtime.CompositionLocalProvider(LocalOverlayExitOutsideDismiss provides null) {
                    scope.content()
                }
            }
            childHost?.portals?.forEach { (id, portal) -> androidx.compose.runtime.key(id) { portal() } }
        }
    }
    if (!modal && host != null) {
        HostPortal(host, body)
    } else {
        Popup(
            popupPositionProvider = FullWindowPositionProvider,
            onDismissRequest = onDismissRequest,
            properties =
                PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                ),
            content = body,
        )
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
    Overlay(color = currentTheme.colors.foreground.copy(alpha = 0.5f), enter = enter, exit = exit)
}

/** Configurable scrim, preserving the original two-parameter overload for existing callers. */
@Composable
fun TeleportPrimitiveScope.Overlay(
    color: Color,
    enter: EnterTransition = OverlayTransitions.ScrimEnter,
    exit: ExitTransition = OverlayTransitions.ScrimExit,
) {
    OverlayWithOpacity(color, { 1f }, enter, exit)
}

/** Drawer-only motion hook; opacity covers the recorded backdrop and scrim together. */
@Composable
internal fun TeleportPrimitiveScope.OverlayWithOpacity(
    color: Color,
    opacity: () -> Float,
    enter: EnterTransition,
    exit: ExitTransition,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val host = LocalOverlayHost.current
    val blur = host != null && currentTheme.activePreset == StylePreset.Nova
    AnimatedVisibility(
        visibleState = visibleState,
        enter = enter,
        exit = exit,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = opacity() }
                    .then(
                        if (blur) {
                            Modifier.graphicsLayer {
                                renderEffect = BlurEffect(4.dp.toPx(), 4.dp.toPx(), TileMode.Clamp)
                            }.drawWithContent {
                                drawLayer(checkNotNull(host).background)
                                drawContent()
                            }
                        } else {
                            Modifier
                        },
                    )
                    .background(color)
                    .focusProperties { canFocus = false }
                    .then(
                        if (dismissOnClickOutside || (!visibleState.targetState && onExitOutsideDismiss != null)) {
                            Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    // A closing native Popup still owns this hit target.
                                    // Forward only to the nearest open ancestor's policy.
                                    if (!visibleState.targetState) {
                                        onExitOutsideDismiss?.invoke()
                                    } else if (onPointerDownOutside?.invoke() != true &&
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
    val initialFocus = remember { FocusRequester() }
    val managed = LocalOverlayFocus.current != null
    val focusManager = LocalFocusManager.current
    var initializing by remember { mutableStateOf(true) }
    var hasFocus by remember { mutableStateOf(false) }
    var wasFocused by remember { mutableStateOf(false) }
    val modal = LocalOverlayModal.current
    val host = LocalOverlayHost.current
    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            wasFocused = true
        } else if (managed && !modal && wasFocused && visibleState.targetState && dismissOnClickOutside) {
            if (onInteractOutside?.invoke() != true) onDismissRequest()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = contentAlignment,
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = enter,
            exit = exit,
        ) {
            LaunchedEffect(initialFocus) {
                if (managed) {
                    withFrameNanos { }
                    initialFocus.requestFocus()
                    initializing = !focusManager.moveFocus(FocusDirection.Next)
                }
            }
            Box(
                modifier =
                    Modifier.overlayOutsideDismiss(host, !modal && visibleState.targetState && dismissOnClickOutside) {
                        if (onPointerDownOutside?.invoke() != true &&
                            onInteractOutside?.invoke() != true
                        ) {
                            onDismissRequest()
                        }
                    }.then(
                        if (managed) {
                            Modifier.focusProperties { canFocus = initializing }
                                .focusRequester(initialFocus)
                                .focusTarget()
                        } else {
                            Modifier
                        },
                    ).onFocusChanged { hasFocus = it.hasFocus }
                        .focusGroup()
                        .pointerInput(
                            Unit,
                        ) { detectTapGestures { } },
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    LocalOverlayHost provides (childHost ?: LocalOverlayHost.current),
                ) {
                    content()
                }
            }
        }
    }
}
