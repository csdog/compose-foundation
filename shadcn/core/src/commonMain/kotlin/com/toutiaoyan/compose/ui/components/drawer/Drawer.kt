package com.toutiaoyan.compose.ui.components.drawer

import com.toutiaoyan.compose.ui.components.overlay.OverlayFocusOwner
import com.toutiaoyan.compose.ui.components.overlay.overlayTriggerFocus
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitiveScope
import com.toutiaoyan.compose.ui.components.sheet.SheetSide
import com.toutiaoyan.compose.ui.gesture.SwipeGesture

/** @see SwipeGesture.shouldDismiss */
internal fun shouldDismissDrawer(
    dragOffsetPx: Float,
    contentExtentPx: Float,
    thresholdFraction: Float,
): Boolean =
    SwipeGesture.shouldDismiss(
        dragOffsetPx = dragOffsetPx,
        contentExtentPx = contentExtentPx,
        thresholdFraction = thresholdFraction,
    )

internal fun DrawerSwipeDirection.toSheetSide(): SheetSide =
    when (this) {
        DrawerSwipeDirection.Up -> SheetSide.Top
        DrawerSwipeDirection.Down -> SheetSide.Bottom
        DrawerSwipeDirection.Left -> SheetSide.Start
        DrawerSwipeDirection.Right -> SheetSide.End
    }

internal fun DrawerSwipeDirection.contentAlignment(): Alignment =
    when (this) {
        DrawerSwipeDirection.Up -> Alignment.TopCenter
        DrawerSwipeDirection.Down -> Alignment.BottomCenter
        DrawerSwipeDirection.Left -> AbsoluteAlignment.CenterLeft
        DrawerSwipeDirection.Right -> AbsoluteAlignment.CenterRight
    }

internal fun DrawerSwipeDirection.dragOrientation(): Orientation =
    when (this) {
        DrawerSwipeDirection.Up, DrawerSwipeDirection.Down -> Orientation.Vertical
        DrawerSwipeDirection.Left, DrawerSwipeDirection.Right -> Orientation.Horizontal
    }

/** Positive result = dragging toward the closing edge. */
internal fun DrawerSwipeDirection.closingSign(): Float =
    when (this) {
        DrawerSwipeDirection.Up, DrawerSwipeDirection.Left -> -1f
        DrawerSwipeDirection.Down, DrawerSwipeDirection.Right -> 1f
    }

internal fun DrawerSwipeDirection.isVertical(): Boolean =
    this == DrawerSwipeDirection.Up || this == DrawerSwipeDirection.Down

/**
 * Counts open nested drawers so a parent can apply Base-style stack peek/scale
 * (`--nested-drawers` / `data-nested-drawer-open`).
 */
internal class DrawerNestHost(
    private val parent: DrawerNestHost? = null,
    private val direction: DrawerSwipeDirection = DrawerSwipeDirection.Down,
) {
    var swipeProgress by mutableStateOf<() -> Float>({ 0f })
        private set
    private val swipeDepth: Int = (parent?.swipeDepth ?: 0) + 1
    private val childSwipes = mutableStateListOf<Pair<DrawerNestHost, () -> Float>>()
    val nestedSwipeProgress: Float
        get() = childSwipes.asReversed().maxByOrNull { it.first.swipeDepth }?.second?.invoke() ?: 0f

    fun attachSwipeProgress(progress: () -> Float) {
        swipeProgress = progress
        parent?.registerChildSwipe(this, direction, progress)
    }

    fun detachSwipeProgress() {
        swipeProgress = { 0f }
        parent?.unregisterChildSwipe(this)
    }

    private fun registerChildSwipe(
        child: DrawerNestHost,
        childDirection: DrawerSwipeDirection,
        progress: () -> Float,
    ) {
        if (childDirection != direction) return
        childSwipes.removeAll { it.first === child }
        childSwipes.add(child to progress)
        parent?.registerChildSwipe(child, childDirection, progress)
    }

    private fun unregisterChildSwipe(child: DrawerNestHost) {
        childSwipes.removeAll { it.first === child }
        parent?.unregisterChildSwipe(child)
    }

    var naturalHeightPx by mutableFloatStateOf(0f)
        private set
    var frontmostHeightPx by mutableFloatStateOf(0f)
        private set

    fun onNaturalHeight(height: Float) {
        naturalHeightPx = height
        if (openChildCount == 0) reportFrontmostHeight(height, direction)
    }

    private fun reportFrontmostHeight(
        height: Float,
        sourceDirection: DrawerSwipeDirection,
    ) {
        if (sourceDirection != direction) return
        frontmostHeightPx = height
        parent?.reportFrontmostHeight(height, direction)
    }

    var openChildCount by mutableIntStateOf(0)
        private set

    fun onChildOpenChange(open: Boolean) {
        openChildCount = (openChildCount + if (open) 1 else -1).coerceAtLeast(0)
        parent?.onChildOpenChange(open)
        if (!open && openChildCount == 0 && naturalHeightPx > 0f) {
            reportFrontmostHeight(naturalHeightPx, direction)
        }
    }
}

internal val LocalDrawerNestHost =
    staticCompositionLocalOf<DrawerNestHost?> { null }

@Immutable
internal class DrawerState(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val swipeDirection: DrawerSwipeDirection,
    val showSwipeHandle: Boolean,
    val dismissOnClickOutside: Boolean,
    /**
     * When false, swipe-to-dismiss is disabled (Compose convenience; Base maps
     * Vaul `dismissible={false}` mainly to [disablePointerDismissal]).
     */
    val dismissible: Boolean,
    val disablePointerDismissal: Boolean,
    val modal: Boolean,
    val snapPoints: List<DrawerSnapPoint>?,
    val snapPoint: DrawerSnapPoint?,
    val onSnapPointChange: ((DrawerSnapPoint?) -> Unit)?,
    val snapToSequentialPoints: Boolean,
    /** Open nested drawers above this one (for stack styling). */
    val nestedOpenCount: Int,
    val indentGesture: DrawerIndentGesture? = null,
) {
    fun toggle() = onOpenChange(!open)

    fun close() = onOpenChange(false)

    val hasSnapPoints: Boolean get() = !snapPoints.isNullOrEmpty()
}

internal val LocalDrawerState =
    staticCompositionLocalOf<DrawerState> {
        error("DrawerTrigger / DrawerPortal / DrawerContent / DrawerClose must be used within Drawer { }")
    }

internal val LocalDrawerTeleportScope =
    staticCompositionLocalOf<TeleportPrimitiveScope?> { null }

/**
 * Slot receiver for [DrawerTrigger] / [DrawerContent].
 *
 * [open] / [toggle] / [close] match Base UI drawer trigger/close state.
 * [snapPoint] mirrors controlled Base `snapPoint` when snap points are active.
 */
class DrawerScope internal constructor(
    private val state: DrawerState,
) {
    val open: Boolean get() = state.open

    val toggle: () -> Unit get() = state::toggle

    val close: () -> Unit get() = state::close

    val snapPoint: DrawerSnapPoint? get() = state.snapPoint

    val nestedOpenCount: Int get() = state.nestedOpenCount
}

/** Reads the nearest [Drawer] scope. Matches Base UI / shadcn `useDrawer`. */
@Composable
fun drawerState(): DrawerScope = DrawerScope(LocalDrawerState.current)

/**
 * Drawer root matching shadcn **base-nova** / Base UI `Drawer`:
 *
 * ```
 * Drawer
 * ├── DrawerTrigger
 * └── DrawerContent
 *     ├── DrawerSwipeHandle   // when showSwipeHandle
 *     ├── DrawerClose
 *     ├── DrawerHeader
 *     │   ├── DrawerTitle
 *     │   └── DrawerDescription
 *     └── DrawerFooter
 * ```
 *
 * Compose uses **children** slots (not Base `render={...}`):
 * ```
 * Drawer(open = open, onOpenChange = { open = it }) {
 *     DrawerTrigger {
 *         Button("Open drawer", onClick = toggle, variant = ButtonVariant.Outline)
 *     }
 *     DrawerContent {
 *         DrawerHeader("Edit profile", description = "…")
 *         DrawerFooter {
 *             Button("Save", onClick = close)
 *             DrawerClose {
 *                 Button("Cancel", onClick = close, variant = ButtonVariant.Outline)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * [swipeDirection] · [showSwipeHandle] · [snapPoints] / [snapPoint] /
 * [onSnapPointChange] / [snapToSequentialPoints] · [disablePointerDismissal] ·
 * [modal] match base-nova / Base UI Root props.
 */
@Composable
fun Drawer(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    swipeDirection: DrawerSwipeDirection = DrawerSwipeDirection.Down,
    showSwipeHandle: Boolean = false,
    snapPoints: List<DrawerSnapPoint>? = null,
    snapPoint: DrawerSnapPoint? = null,
    onSnapPointChange: ((DrawerSnapPoint?) -> Unit)? = null,
    snapToSequentialPoints: Boolean = false,
    /**
     * Base UI: when true, outside presses do not close the drawer.
     * Swipe-to-dismiss is unaffected (use [dismissible] = false to block swipe).
     */
    disablePointerDismissal: Boolean = false,
    dismissOnClickOutside: Boolean = true,
    /**
     * When false, swipe-to-dismiss is disabled. Outside clicks still follow
     * [disablePointerDismissal] / [dismissOnClickOutside].
     */
    dismissible: Boolean = true,
    modal: Boolean = true,
    /** Opt in to the nearest DrawerIndentHost background effect. */
    indentBackground: Boolean = false,
    content: @Composable DrawerScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }

    var internalSnap by remember(snapPoints) {
        mutableStateOf(snapPoint ?: snapPoints?.firstOrNull())
    }
    val snapControlled = snapPoint != null || onSnapPointChange != null
    val activeSnap =
        when {
            snapPoints.isNullOrEmpty() -> null
            snapPoint != null -> snapPoint
            else -> internalSnap
        }
    val setSnap: (DrawerSnapPoint?) -> Unit = { next ->
        if (!snapControlled || onSnapPointChange != null) {
            if (snapPoint == null) internalSnap = next
            onSnapPointChange?.invoke(next)
        }
    }

    val indentHost = LocalDrawerIndentState.current
    val indentKey = remember { Any() }
    val indentGesture = remember { DrawerIndentGesture() }
    DisposableEffect(indentHost, isOpen, indentBackground) {
        if (isOpen && indentBackground) indentHost?.register(indentKey) { indentGesture.progress() }
        onDispose { indentHost?.unregister(indentKey) }
    }

    val parentNest = LocalDrawerNestHost.current
    val selfNest = remember(parentNest, swipeDirection) { DrawerNestHost(parentNest, swipeDirection) }
    DisposableEffect(isOpen, parentNest) {
        if (isOpen) {
            parentNest?.onChildOpenChange(true)
            onDispose { parentNest?.onChildOpenChange(false) }
        } else {
            onDispose { }
        }
    }

    val pointerDismiss =
        dismissOnClickOutside && !disablePointerDismissal
    val state =
        DrawerState(
            open = isOpen,
            onOpenChange = setOpen,
            swipeDirection = swipeDirection,
            showSwipeHandle = showSwipeHandle,
            dismissOnClickOutside = pointerDismiss,
            dismissible = dismissible,
            disablePointerDismissal = disablePointerDismissal,
            modal = modal,
            snapPoints = snapPoints,
            snapPoint = activeSnap,
            onSnapPointChange = setSnap,
            snapToSequentialPoints = snapToSequentialPoints,
            nestedOpenCount = selfNest.openChildCount,
            indentGesture = if (indentBackground) indentGesture else null,
        )
    OverlayFocusOwner(isOpen, modal) {
        CompositionLocalProvider(
            LocalDrawerState provides state,
            LocalDrawerNestHost provides selfNest,
        ) {
            Box(modifier = modifier) {
                DrawerScope(state).content()
            }
        }
    }
}

/**
 * Host slot for the open control. Matches Base UI / shadcn `DrawerTrigger`.
 *
 * Web Base UI merges behavior into a host via `render={<Button />}` so there is
 * no nested button. Compose has no `render` prop — put the host as **children**
 * and wire [toggle] yourself (typically a [Button]):
 * ```
 * DrawerTrigger {
 *     Button("Open", onClick = toggle, variant = ButtonVariant.Outline)
 * }
 * ```
 * This slot does not add its own clickable layer (avoids button-in-button).
 */
@Composable
fun DrawerScope.DrawerTrigger(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.overlayTriggerFocus()) {
        content()
    }
}

/**
 * Convenience: outline-style [Button] labeled [text], wired to [toggle].
 * Prefer the children overload when you need a custom host.
 */
@Composable
fun DrawerScope.DrawerTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
) {
    Button(
        text,
        onClick = toggle,
        modifier = modifier.overlayTriggerFocus(),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
    )
}
