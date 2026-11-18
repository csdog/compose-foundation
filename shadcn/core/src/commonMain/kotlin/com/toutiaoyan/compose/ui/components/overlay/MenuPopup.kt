package com.toutiaoyan.compose.ui.components.overlay

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Distinct defaults let an explicitly supplied shared transition retain priority.
internal val MenuDefaultEnter = fadeIn(tween(0))
internal val MenuDefaultExit = fadeOut(tween(0))
private const val MENU_MOTION_MILLIS = 100

/** CSS `zoom-in-95` / menu content hidden scale (visual parity with shadcn). */
private const val MENU_HIDDEN_SCALE = 0.95f
private const val MENU_SCALE_RANGE = 0.05f
private val MenuSlideDistance = 8.dp

// CSS `ease` cubic-bezier(0.25, 0.1, 0.25, 1) — keep tuple for motion parity.
private val MenuCssEase = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

private fun menuContentScale(visible: Float): Float = MENU_HIDDEN_SCALE + MENU_SCALE_RANGE * visible

@Composable
internal fun MenuPopup(
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
    owningNova: Boolean,
    ready: Boolean = true,
    animateDefaults: Boolean = true,
    freezePositionOnExit: Boolean = false,
    horizontalPosition: PopupHorizontalPosition? = null,
    content: @Composable () -> Unit,
) {
    val defaultEnter = enter === MenuDefaultEnter
    val defaultExit = exit === MenuDefaultExit
    if (!owningNova) {
        PositionedAnchoredPopup(
            open, onDismissRequest, placement, offset, align, alignOffset,
            dismissOnClickOutside, focusable,
            if (defaultEnter) {
                if (animateDefaults) PopupTransitions.Enter else EnterTransition.None
            } else {
                enter
            },
            if (defaultExit) {
                if (animateDefaults) PopupTransitions.Exit else ExitTransition.None
            } else {
                exit
            },
            onPositioned = null,
            freezePosition = freezePositionOnExit && !open,
            horizontalPosition = horizontalPosition,
        ) { content() }
        return
    }
    if (defaultEnter && defaultExit) {
        NovaMenuPopup(
            open, onDismissRequest, placement, offset, align, alignOffset,
            dismissOnClickOutside, focusable, ready, animateDefaults, freezePositionOnExit, horizontalPosition, content,
        )
        return
    }
    val entryProgress = remember(open, animateDefaults) { Animatable(0f) }
    var entryPresented by remember { mutableStateOf(false) }
    LaunchedEffect(open, ready, defaultEnter, entryProgress) {
        if (open && ready && defaultEnter) {
            entryPresented = true
            if (animateDefaults) {
                entryProgress.animateTo(1f, tween(MENU_MOTION_MILLIS, easing = MenuCssEase))
            } else {
                entryProgress.snapTo(1f)
            }
        }
    }
    val geometry = remember { mutableStateOf<PopupPositionResult?>(null) }
    val positioned: (PopupPositionResult) -> Unit = remember { { geometry.value = it } }
    val slidePx = with(LocalDensity.current) { MenuSlideDistance.toPx() }
    PositionedAnchoredPopup(
        open, onDismissRequest, placement, offset, align, alignOffset,
        dismissOnClickOutside, focusable,
        if (defaultEnter) EnterTransition.None else enter,
        if (defaultExit) ExitTransition.None else exit,
        onPositioned = positioned,
        retainInterruptedEnter = true,
        freezePosition = freezePositionOnExit && !open,
        resetExitOnOpen = defaultEnter,
        horizontalPosition = horizontalPosition,
    ) {
        DisposableEffect(Unit) { onDispose { entryPresented = false } }
        val entering = transition.targetState == EnterExitState.Visible
        val ownDirection = animateDefaults && if (entering) defaultEnter else defaultExit
        // Default opacity must leave composition too: a built-in fade retains an interrupted spring.
        val opacity =
            if (ownDirection && !entering) {
                transition.animateFloat(
                    transitionSpec = { tween(MENU_MOTION_MILLIS, easing = MenuCssEase) },
                    label = "Menu opacity",
                ) { state -> if (state == EnterExitState.Visible) 1f else 0f }
            } else {
                null
            }
        val scale =
            if (ownDirection && !entering) {
                transition.animateFloat(
                    transitionSpec = { tween(MENU_MOTION_MILLIS, easing = MenuCssEase) },
                    label = "Menu scale",
                ) { state -> if (state == EnterExitState.Visible) 1f else MENU_HIDDEN_SCALE }
            } else {
                null
            }
        val visibility = transition
        Box(
            Modifier.graphicsLayer {
                val position = geometry.value
                if (position != null) transformOrigin = position.transformOrigin
                val ownActiveDirection =
                    animateDefaults &&
                        if (visibility.targetState == EnterExitState.Visible) defaultEnter else defaultExit
                val ownEntry = visibility.targetState == EnterExitState.Visible && defaultEnter
                val visible = if (ownEntry) entryProgress.value else opacity?.value ?: 1f
                alpha =
                    if (defaultEnter && !entryPresented) {
                        0f
                    } else if (ownActiveDirection) {
                        visible
                    } else {
                        1f
                    }
                scaleX =
                    if (ownEntry && animateDefaults) {
                        menuContentScale(visible)
                    } else if (ownActiveDirection) {
                        scale?.value ?: 1f
                    } else {
                        1f
                    }
                scaleY = scaleX
                val amount =
                    if (visibility.targetState == EnterExitState.Visible && defaultEnter && animateDefaults) {
                        (1f - entryProgress.value) * slidePx
                    } else {
                        0f
                    }
                translationX =
                    when (position?.physicalSide ?: placement) {
                        PopupPlacement.Start -> amount
                        PopupPlacement.End -> -amount
                        else -> 0f
                    }
                translationY =
                    when (position?.physicalSide ?: placement) {
                        PopupPlacement.Top -> amount
                        PopupPlacement.Bottom -> -amount
                        else -> 0f
                    }
            },
        ) { content() }
    }
}

/** The default Nova route has one lifetime owner, independent of visibility transitions. */
private class NovaMenuPhaseController {
    var retained by mutableStateOf(false)
    var presented by mutableStateOf(false)
}

@Composable
private fun NovaMenuPopup(
    open: Boolean,
    onDismissRequest: () -> Unit,
    placement: PopupPlacement,
    offset: Dp,
    align: PopupAlign,
    alignOffset: Dp,
    dismissOnClickOutside: Boolean,
    focusable: Boolean,
    ready: Boolean,
    animateDefaults: Boolean,
    freezePositionOnExit: Boolean,
    horizontalPosition: PopupHorizontalPosition?,
    content: @Composable () -> Unit,
) {
    val controller = remember { NovaMenuPhaseController() }
    // A fresh value is available during composition, before the direction's first draw.
    // Only progress is keyed: the Popup and its content keep their composition identity.
    val progress = remember(open, animateDefaults) { Animatable(0f) }
    SideEffect {
        if (open) controller.retained = true
    }
    LaunchedEffect(open, progress, open && ready) {
        // Mount for measurement, then start the full entry once the owner has positioned its content.
        if (open && !ready) return@LaunchedEffect
        if (!open && !controller.presented) {
            controller.retained = false
            return@LaunchedEffect
        }
        if (open) controller.presented = true
        if (open || controller.retained) {
            if (animateDefaults) {
                progress.animateTo(1f, tween(MENU_MOTION_MILLIS, easing = MenuCssEase))
            } else {
                progress.snapTo(1f)
            }
            if (!open) {
                controller.retained = false
                controller.presented = false
            }
        }
    }
    if (!open && !controller.retained) return

    val geometry = remember { mutableStateOf<PopupPositionResult?>(null) }
    val positioned: (PopupPositionResult) -> Unit = remember { { geometry.value = it } }
    val slidePx = with(LocalDensity.current) { MenuSlideDistance.toPx() }
    RawAnchoredPopup(
        onDismissRequest = { if (open) onDismissRequest() },
        placement = placement,
        offset = offset,
        align = align,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside && open,
        focusable = focusable && open,
        onPositioned = positioned,
        freezePosition = freezePositionOnExit && !open,
        horizontalPosition = horizontalPosition,
    ) {
        Box(
            Modifier.graphicsLayer {
                val position = geometry.value
                if (position != null) transformOrigin = position.transformOrigin
                val visible =
                    if ((open && !ready) || !controller.presented) {
                        0f
                    } else if (open) {
                        progress.value
                    } else {
                        1f - progress.value
                    }
                alpha = visible
                scaleX = if (animateDefaults) menuContentScale(visible) else 1f
                scaleY = scaleX
                val amount = if (open && animateDefaults) (1f - progress.value) * slidePx else 0f
                translationX =
                    when (position?.physicalSide ?: placement) {
                        PopupPlacement.Start -> amount
                        PopupPlacement.End -> -amount
                        else -> 0f
                    }
                translationY =
                    when (position?.physicalSide ?: placement) {
                        PopupPlacement.Top -> amount
                        PopupPlacement.Bottom -> -amount
                        else -> 0f
                    }
            },
        ) { content() }
    }
}
