@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

enum class ScrollAreaOrientation { Vertical, Horizontal, Both }

class ScrollAreaState(
    val vertical: ScrollState,
    val horizontal: ScrollState,
)

private val LocalScrollArea = staticCompositionLocalOf<ScrollAreaState?> { null }

/** Scroll positions of the nearest [ScrollArea]. Matches Radix scroll-area context. */
@Composable
fun scrollAreaState(): ScrollAreaState =
    checkNotNull(LocalScrollArea.current) {
        "scrollAreaState() must be used inside ScrollArea { … }"
    }

/**
 * A scrollable region with a slim custom scrollbar thumb, matching real shadcn/ui's
 * `scroll-area.tsx`. The viewport uses Compose scrolling; the custom scrollbar supports
 * direct dragging and track presses. Radix visibility policies remain optional extensions.
 *
 * [type] mirrors Radix `type` / `scrollHideDelay`: [ScrollType.Scroll] and
 * [ScrollType.Hover] carry their own hide delay; default is [ScrollType.Auto] (visible when overflowing), matching Base UI.
 * [dir] mirrors Radix `dir`: `null` inherits [androidx.compose.ui.platform.LocalLayoutDirection].
 *
 * [viewportStyle] configures the inner viewport (for example matching a custom root shape).
 * For long lazy lists, use [LazyScrollArea] instead -- nesting a `LazyColumn` here
 * disables laziness and fights over the same-axis scroll gesture.
 *
 * Usage:
 * ```
 * ScrollArea(modifier = Modifier.height(200.dp)) {
 *     Column { repeat(50) { Text { "Row $it" } } }
 * }
 * ScrollArea(type = ScrollType.Always) { ... }
 * ScrollArea(type = ScrollType.Scroll(), scrollHideDelay = 1000) { ... }
 * ScrollArea(dir = ScrollDir.Rtl) { ... }
 * ```
 */
@Composable
fun ScrollArea(
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: ScrollAreaOrientation = ScrollAreaOrientation.Vertical,
    type: ScrollType = ScrollType.Auto,
    dir: ScrollDir? = null,
    scrollHideDelay: Int? = null,
    viewportStyle: Style = Style,
    content: @Composable BoxScope.() -> Unit,
) {
    ProvideScrollDir(dir) {
        ScrollAreaContent(
            modifier = modifier,
            style = style,
            orientation = orientation,
            type = type.withHideDelay(scrollHideDelay),
            viewportStyle = viewportStyle,
            content = content,
        )
    }
}

@Composable
private fun ScrollAreaContent(
    modifier: Modifier,
    style: Style,
    orientation: ScrollAreaOrientation,
    type: ScrollType,
    viewportStyle: Style,
    content: @Composable BoxScope.() -> Unit,
) {
    val verticalState = rememberScrollState()
    val horizontalState = rememberScrollState()
    val scrollsVertically = orientation != ScrollAreaOrientation.Horizontal
    val scrollsHorizontally = orientation != ScrollAreaOrientation.Vertical
    val areaState = remember(verticalState, horizontalState) { ScrollAreaState(verticalState, horizontalState) }

    val hoverInteraction = remember { MutableInteractionSource() }
    val isHovered by hoverInteraction.collectIsHoveredAsState()
    val styleState = remember { MutableStyleState(hoverInteraction) }

    val verticalThumbInteraction = remember { MutableInteractionSource() }
    val horizontalThumbInteraction = remember { MutableInteractionSource() }
    val verticalDragging by verticalThumbInteraction.collectIsDraggedAsState()
    val horizontalDragging by horizontalThumbInteraction.collectIsDraggedAsState()

    val verticalScrolling = verticalState.isScrollInProgress || verticalDragging
    val horizontalScrolling = horizontalState.isScrollInProgress || horizontalDragging

    val verticalOverflowing = verticalState.maxValue > 0
    val horizontalOverflowing = horizontalState.maxValue > 0
    val verticalVisible =
        rememberScrollbarVisible(
            type = type,
            overflowing = verticalOverflowing,
            scrolling = verticalScrolling,
            hovering = isHovered,
        )
    val horizontalVisible =
        rememberScrollbarVisible(
            type = type,
            overflowing = horizontalOverflowing,
            scrolling = horizontalScrolling,
            hovering = isHovered,
        )
    val forceShowWhenEmpty = type is ScrollType.Always
    val corner = if (currentTheme.activePreset == StylePreset.Nova) 10.dp else 6.dp
    val dualAxis = scrollsVertically && scrollsHorizontally && verticalOverflowing && horizontalOverflowing

    CompositionLocalProvider(LocalScrollArea provides areaState) {
        Box(
            modifier =
                modifier
                    .hoverable(hoverInteraction)
                    .styleable(styleState, style),
        ) {
            ScrollAreaViewport(
                verticalState = verticalState.takeIf { scrollsVertically },
                horizontalState = horizontalState.takeIf { scrollsHorizontally },
                style = viewportStyle,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .let { if (scrollsVertically) it.verticalScroll(verticalState) else it }
                        .let { if (scrollsHorizontally) it.horizontalScroll(horizontalState) else it },
                content = content,
            )
            if (scrollsVertically && scrollbarMayShow(type, verticalOverflowing)) {
                ScrollBar(
                    value = verticalState.value,
                    maxValue = verticalState.maxValue,
                    viewportSize = verticalState.viewportSize,
                    onDragDelta = { delta -> verticalState.dispatchRawDelta(delta) },
                    vertical = true,
                    visible = verticalVisible,
                    forceShowWhenEmpty = forceShowWhenEmpty,
                    interactionSource = verticalThumbInteraction,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(bottom = if (dualAxis) corner else 0.dp),
                )
            }
            if (scrollsHorizontally && scrollbarMayShow(type, horizontalOverflowing)) {
                ScrollBar(
                    value = horizontalState.value,
                    maxValue = horizontalState.maxValue,
                    viewportSize = horizontalState.viewportSize,
                    onDragDelta = { delta -> horizontalState.dispatchRawDelta(delta) },
                    vertical = false,
                    visible = horizontalVisible,
                    forceShowWhenEmpty = forceShowWhenEmpty,
                    interactionSource = horizontalThumbInteraction,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(end = if (dualAxis) corner else 0.dp),
                )
            }
        }
    }
}

/** Scrollable child of [ScrollArea]. Base UI does not export Viewport (used inside Root). */
@Composable
internal fun ScrollAreaViewport(
    modifier: Modifier = Modifier,
    style: Style = Style,
    verticalState: ScrollState? = null,
    horizontalState: ScrollState? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val inputMode = LocalInputModeManager.current
    val step = with(LocalDensity.current) { 40.dp.toPx() }
    val horizontalSign = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f
    val overflowing = (verticalState?.maxValue ?: 0) > 0 || (horizontalState?.maxValue ?: 0) > 0
    Box(
        modifier =
            modifier
                .testTag("scroll-area-viewport")
                .keyboardInputMode()
                .onFocusChanged { focused = it.isFocused }
                .onKeyEvent { event ->
                    if (!focused || event.type != KeyEventType.KeyDown ||
                        event.isAltPressed || event.isCtrlPressed || event.isMetaPressed
                    ) {
                        return@onKeyEvent false
                    }
                    val state =
                        when (event.key) {
                            Key.DirectionLeft, Key.DirectionRight -> horizontalState
                            else -> verticalState ?: horizontalState
                        } ?: return@onKeyEvent false
                    val delta =
                        when (event.key) {
                            Key.DirectionDown -> if (verticalState != null) step else return@onKeyEvent false
                            Key.DirectionUp -> if (verticalState != null) -step else return@onKeyEvent false
                            Key.DirectionLeft -> -step * horizontalSign
                            Key.DirectionRight -> step * horizontalSign
                            Key.PageDown -> state.viewportSize.toFloat()
                            Key.PageUp -> -state.viewportSize.toFloat()
                            Key.MoveHome -> -state.value.toFloat()
                            Key.MoveEnd -> (state.maxValue - state.value).toFloat()
                            Key.Spacebar -> state.viewportSize * if (event.isShiftPressed) -1f else 1f
                            else -> return@onKeyEvent false
                        }
                    state.dispatchRawDelta(delta)
                    true
                }
                .focusable(enabled = overflowing)
                .cssStyleable(
                    styleState,
                    rememberScrollAreaViewportStyle(
                        focused &&
                            inputMode.inputMode == InputMode.Keyboard,
                    ),
                    style,
                ),
        content = content,
    )
}

/**
 * Intersection of vertical + horizontal thumbs. Base UI paints Corner inside Root
 * but does not export it.
 */
@Composable
internal fun ScrollAreaCorner(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(modifier = modifier.styleable(styleState, style))
}
