@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.resizable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.DisposableEffect
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import com.toutiaoyan.compose.ui.styles.cssStyleable
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

private const val MIN_PANEL_WEIGHT = 0.000001f
private const val RESIZE_KEYBOARD_FRACTION_STEP = 0.05f
private const val PERCENT_PER_FRACTION = 100

/**
 * The entire drag-to-resize calculation, as a plain function with no Compose dependency:
 * given the current split and how far the handle just moved (in px), returns the new
 * split, clamped to `[minFraction, maxFraction]`. `containerExtentPx <= 0` (not measured
 * yet) is a no-op, returning [currentFraction] unchanged rather than dividing by zero.
 */
internal fun resizablePanelFraction(
    currentFraction: Float,
    dragDeltaPx: Float,
    containerExtentPx: Float,
    minFraction: Float,
    maxFraction: Float,
): Float {
    if (!containerExtentPx.isFinite() || containerExtentPx <= 0f || !dragDeltaPx.isFinite()) return currentFraction
    return (currentFraction + dragDeltaPx / containerExtentPx).coerceIn(minFraction, maxFraction)
}

object ResizableDefaults {
    /** Official [ResizableHandle] omits `withHandle` (hairline only). */
    const val WithHandle = false
}

@Immutable
class ResizableState(
    val fraction: Float,
    val orientation: Orientation,
    val onHandleDrag: (Float) -> Unit,
)

private val LocalResizable = staticCompositionLocalOf<ResizableState?> { null }

private class ResizableBounds(
    val pointerRegion: ResizablePointerRegion,
    val min: Float,
    val max: Float,
    val rtl: Boolean,
    val disabled: Boolean,
    val currentFraction: () -> Float,
    val availableExtent: () -> Float,
    val onHandleExtent: (Float) -> Unit,
    val onPointerStart: () -> Unit,
    val onPointerDrag: (Float) -> Unit,
)

private val LocalResizableBounds = staticCompositionLocalOf<ResizableBounds?> { null }

/** Split of the nearest [ResizablePanelGroup]. Matches react-resizable-panels context. */
@Composable
fun resizableState(): ResizableState =
    checkNotNull(LocalResizable.current) {
        "resizableState() must be used inside ResizablePanelGroup { … }"
    }

/**
 * Two panes divided by a draggable handle, matching real shadcn/ui's `resizable.tsx`
 * (a thin wrapper over `react-resizable-panels`). Since there is no Compose Multiplatform
 * equivalent library, the split is driven directly here as a fraction (0f..1f) of the
 * container's own measured extent -- simpler than a full multi-panel-group API, but
 * covers the documented two-pane use case. [content] receives each pane's already-sized
 * `Modifier` (via `RowScope`/`ColumnScope` weight) plus the current split fraction so the
 * caller can wire [ResizableHandle]'s `onDrag` back into it.
 *
 * Usage:
 * ```
 * ResizablePanelGroup { first, second, onHandleDrag ->
 *     ResizablePanel(first, "One")
 *     ResizableHandle()
 *     ResizablePanel(second, "Two")
 * }
 * ```
 *
 * Pointer capture, keyboard input and fixed layouts are tested separately. The handle
 * uses a non-suspending pointer node so real mouse and touch gestures can be verified.
 */
@Composable
fun ResizablePanelGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: Orientation = Orientation.Horizontal,
    initialFraction: Float = 0.5f,
    minFraction: Float = 0f,
    maxFraction: Float = 1f,
    direction: Orientation? = null,
    disabled: Boolean = false,
    content: @Composable (first: Modifier, second: Modifier, onHandleDrag: (Float) -> Unit) -> Unit,
) {
    require(
        minFraction.isFinite() &&
            maxFraction.isFinite() &&
            minFraction in 0f..1f &&
            maxFraction in minFraction..1f,
    ) {
        "Panel limits must satisfy 0 <= minFraction <= maxFraction <= 1"
    }
    val resolvedOrientation = direction ?: orientation
    val rtl = resolvedOrientation == Orientation.Horizontal && LocalLayoutDirection.current == LayoutDirection.Rtl
    var fraction by remember {
        mutableFloatStateOf(
            if (initialFraction.isFinite()) {
                initialFraction.coerceIn(
                    minFraction,
                    maxFraction,
                )
            } else {
                0.5f.coerceIn(
                    minFraction,
                    maxFraction,
                )
            },
        )
    }
    val current = fraction.coerceIn(minFraction, maxFraction)
    SideEffect { if (fraction != current) fraction = current }
    var contentExtentPx by remember { mutableFloatStateOf(0f) }
    var handleExtentPx by remember { mutableFloatStateOf(0f) }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val extent = { (contentExtentPx - handleExtentPx).coerceAtLeast(0f) }
    val onHandleDrag: (Float) -> Unit = { delta ->
        if (!disabled) {
            fraction =
                resizablePanelFraction(
                    fraction.coerceIn(minFraction, maxFraction),
                    if (rtl) -delta else delta, extent(), minFraction, maxFraction,
                )
        }
    }
    val state = ResizableState(current, resolvedOrientation, onHandleDrag)
    val region = remember { ResizablePointerRegion() }
    val pointerDrag = remember { ResizableDragAccumulator() }
    // A gesture uses one frozen geometry/constraint frame, like the upstream hit region.
    DisposableEffect(
        region,
        resolvedOrientation,
        minFraction,
        maxFraction,
        rtl,
        disabled,
        contentExtentPx,
        handleExtentPx,
    ) {
        region.cancelGesture?.invoke()
        onDispose { region.cancelGesture?.invoke() }
    }
    val bounds =
        ResizableBounds(
            region,
            minFraction,
            maxFraction,
            rtl,
            disabled,
            { fraction.coerceIn(minFraction, maxFraction) },
            extent,
            { handleExtentPx = it },
            { pointerDrag.start(fraction.coerceIn(minFraction, maxFraction), extent()) },
            { delta ->
                if (!disabled) fraction = pointerDrag.drag(delta, minFraction, maxFraction, rtl)
            },
        )
    CompositionLocalProvider(LocalResizable provides state, LocalResizableBounds provides bounds) {
        if (resolvedOrientation == Orientation.Horizontal) {
            Row(
                modifier =
                    modifier.fillMaxSize().styleable(styleState, style)
                        .onSizeChanged { contentExtentPx = it.width.toFloat() }.resizablePointerRouter(region),
            ) {
                content(
                    Modifier.fillMaxHeight().weight(current.coerceAtLeast(MIN_PANEL_WEIGHT)),
                    Modifier.fillMaxHeight().weight((1f - current).coerceAtLeast(MIN_PANEL_WEIGHT)),
                    onHandleDrag,
                )
            }
        } else {
            Column(
                modifier =
                    modifier.fillMaxSize().styleable(styleState, style)
                        .onSizeChanged { contentExtentPx = it.height.toFloat() }.resizablePointerRouter(region),
            ) {
                content(
                    Modifier.fillMaxWidth().weight(current.coerceAtLeast(MIN_PANEL_WEIGHT)),
                    Modifier.fillMaxWidth().weight((1f - current).coerceAtLeast(MIN_PANEL_WEIGHT)),
                    onHandleDrag,
                )
            }
        }
    }
}

/** One pane inside a [ResizablePanelGroup]. Matches shadcn's `ResizablePanel`. */
@Composable
fun ResizablePanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.clipToBounds()) {
        content()
    }
}

/** String-body overload of [ResizablePanel]. */
@Composable
fun ResizablePanel(
    modifier: Modifier = Modifier,
    text: String,
) {
    ResizablePanel(modifier) {
        Text { text }
    }
}

/** The draggable divider between two panes in a [ResizablePanelGroup].
 * Official `withHandle` is omitted (false). Pass `withHandle = true` for the grip.
 */
@Composable
fun ResizableHandle(
    onDrag: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier,
    orientation: Orientation? = null,
    showGrip: Boolean = true,
    withHandle: Boolean = ResizableDefaults.WithHandle,
    disabled: Boolean = false,
    style: Style = Style,
) {
    val state = LocalResizable.current
    val resolvedDrag =
        onDrag
            ?: checkNotNull(state) {
                "ResizableHandle needs onDrag= or a parent ResizablePanelGroup"
            }.onHandleDrag
    val resolvedOrientation = orientation ?: state?.orientation ?: Orientation.Horizontal
    val horizontal = resolvedOrientation == Orientation.Horizontal
    val bounds = LocalResizableBounds.current
    val resolvedDisabled = disabled || bounds?.disabled == true
    val focus = remember { FocusRequester() }
    val inputMode = LocalInputModeManager.current
    val config = LocalViewConfiguration.current
    val hitConfig =
        remember(config) {
            object : ViewConfiguration by config {
                override val minimumTouchTargetSize = DpSize(20.dp, 20.dp)
            }
        }

    val region = bounds?.pointerRegion ?: remember { ResizablePointerRegion() }
    SideEffect {
        if (region.horizontal != horizontal || resolvedDisabled) region.cancelGesture?.invoke()
        region.horizontal = horizontal
        region.enabled = !resolvedDisabled
        region.onStart = {
            bounds?.onPointerStart?.invoke()
            inputMode.requestInputMode(InputMode.Touch)
            if (state != null) focus.requestFocus()
        }
        region.onDelta = if (onDrag == null && bounds != null) bounds.onPointerDrag else resolvedDrag
    }
    DisposableEffect(region) {
        onDispose {
            region.enabled = false
            region.handle = null
            region.cancelGesture?.invoke()
        }
    }

    fun setFraction(target: Float): Boolean {
        if (resolvedDisabled || state == null || bounds == null || !target.isFinite()) return false
        val delta = (target.coerceIn(bounds.min, bounds.max) - bounds.currentFraction()) * bounds.availableExtent()
        resolvedDrag(if (bounds.rtl) -delta else delta)
        return true
    }
    val decreaseKey = if (horizontal) Key.DirectionLeft else Key.DirectionUp
    val increaseKey = if (horizontal) Key.DirectionRight else Key.DirectionDown
    val interactions = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactions) }
    val gripStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(LocalViewConfiguration provides hitConfig) {
        Box(
            modifier =
                modifier.zIndex(1f)
                    .focusRequester(focus)
                    .keyboardInputMode()
                    .onPreviewKeyEvent { event ->
                        if (resolvedDisabled || state == null || bounds == null || event.type != KeyEventType.KeyDown) {
                            false
                        } else {
                            when (event.key) {
                                decreaseKey ->
                                    setFraction(bounds.currentFraction() - RESIZE_KEYBOARD_FRACTION_STEP)
                                increaseKey ->
                                    setFraction(bounds.currentFraction() + RESIZE_KEYBOARD_FRACTION_STEP)
                                Key.MoveHome -> setFraction(bounds.min)
                                Key.MoveEnd -> setFraction(bounds.max)
                                Key.Enter, Key.NumPadEnter, Key.F6 -> true
                                else -> false
                            }
                        }
                    }
                    .semantics {
                        contentDescription =
                            if (horizontal) {
                                "Resize panels horizontally"
                            } else {
                                "Resize panels vertically"
                            }
                        if (resolvedDisabled) disabled()
                        if (state != null && bounds != null) {
                            progressBarRangeInfo = ProgressBarRangeInfo(state.fraction, bounds.min..bounds.max, 0)
                            stateDescription = "${(state.fraction * PERCENT_PER_FRACTION).roundToInt()}%"
                            if (!resolvedDisabled) {
                                // A semantic press focuses the divider and exposes its minimum touch bounds.
                                onClick { focus.requestFocus() }
                                setProgress { setFraction(it) }
                            }
                        }
                    }
                    .cssStyleable(styleState, rememberResizableHandleStyle(horizontal) then style)
                    .onSizeChanged {
                        bounds?.onHandleExtent?.invoke(
                            if (horizontal) {
                                it.width.toFloat()
                            } else {
                                it.height.toFloat()
                            },
                        )
                    }
                    .focusable(enabled = !resolvedDisabled && state != null, interactionSource = interactions)
                    .onGloballyPositioned { region.handle = it }
                    .then(if (bounds == null) Modifier.resizablePointerRouter(region) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (showGrip && withHandle) {
                val nova = currentTheme.activePreset == StylePreset.Nova
                val width =
                    if (nova) {
                        if (horizontal) 4.dp else 24.dp
                    } else {
                        if (horizontal) 12.dp else 16.dp
                    }
                val height =
                    if (nova) {
                        if (horizontal) 24.dp else 4.dp
                    } else {
                        if (horizontal) 16.dp else 12.dp
                    }
                Box(
                    modifier =
                        Modifier.requiredSize(
                            width,
                            height,
                        ).styleable(
                            gripStyleState,
                            rememberResizableGripStyle(horizontal),
                        ),
                )
            }
        }
    }
}
