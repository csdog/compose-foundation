@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.resizable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.text.Text

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
    if (containerExtentPx <= 0f) return currentFraction
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
 * Testing this: don't simulate a live drag gesture in Compose UI Test --
 * `performMouseInput`/`performTouchInput` drag simulations against a raw
 * `Modifier.draggable` have previously hung this project's JVM test worker outright
 * (see git history). Instead this is two separately-testable halves:
 * 1. [resizablePanelFraction] is the entire drag *math*, extracted as a plain pure
 *    function with no Compose dependency at all -- unit test it directly
 *    (`ResizablePanelGroupTest`) the same way [scrollDragDeltaToContentDelta]
 *    is tested for [ScrollArea].
 * 2. The *rendering* at a given split -- does `weight(fraction)` actually produce the
 *    right pixel proportions -- is covered by screenshot-testing fixed `initialFraction`
 *    values (e.g. 0.3, 0.5, 0.7) rather than dragging to reach them.
 */
@Composable
fun ResizablePanelGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: Orientation = Orientation.Horizontal,
    initialFraction: Float = 0.5f,
    minFraction: Float = 0.15f,
    maxFraction: Float = 0.85f,
    direction: Orientation? = null,
    content: @Composable (first: Modifier, second: Modifier, onHandleDrag: (Float) -> Unit) -> Unit,
) {
    val resolvedOrientation = direction ?: orientation
    var fraction by remember { mutableFloatStateOf(initialFraction) }
    var containerExtentPx by remember { mutableFloatStateOf(0f) }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }

    val onHandleDrag: (Float) -> Unit = { delta ->
        fraction = resizablePanelFraction(fraction, delta, containerExtentPx, minFraction, maxFraction)
    }
    val state =
        ResizableState(
            fraction = fraction,
            orientation = resolvedOrientation,
            onHandleDrag = onHandleDrag,
        )

    CompositionLocalProvider(LocalResizable provides state) {
        if (resolvedOrientation == Orientation.Horizontal) {
            Row(
                modifier =
                    modifier
                        .fillMaxSize()
                        .onSizeChanged { containerExtentPx = it.width.toFloat() }
                        .styleable(styleState, style),
            ) {
                content(
                    Modifier.fillMaxHeight().weight(fraction),
                    Modifier.fillMaxHeight().weight(1f - fraction),
                    onHandleDrag,
                )
            }
        } else {
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .onSizeChanged { containerExtentPx = it.height.toFloat() }
                        .styleable(styleState, style),
            ) {
                content(
                    Modifier.fillMaxWidth().weight(fraction),
                    Modifier.fillMaxWidth().weight(1f - fraction),
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
    Box(modifier = modifier) {
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
    val dragState = rememberDraggableState(resolvedDrag)
    val horizontal = resolvedOrientation == Orientation.Horizontal
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val gripStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .semantics {
                    contentDescription =
                        if (horizontal) "Resize panels horizontally" else "Resize panels vertically"
                    if (disabled) disabled()
                }
                .styleable(
                    styleState,
                    rememberResizableHandleStyle(horizontal) then style,
                )
                .then(
                    if (disabled) {
                        Modifier
                    } else {
                        Modifier.draggable(state = dragState, orientation = resolvedOrientation)
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (showGrip && withHandle) {
            Box(
                modifier =
                    Modifier.styleable(
                        gripStyleState,
                        rememberResizableGripStyle(horizontal),
                    ),
            )
        }
    }
}
