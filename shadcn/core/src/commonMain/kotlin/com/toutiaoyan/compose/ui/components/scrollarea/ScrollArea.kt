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
 * `scroll-area.tsx` -- which is itself a custom Radix thumb, not the browser-native
 * scrollbar, so a hand-drawn thumb here is actually the correct parity match rather
 * than a simplification. The thumb is both a scroll indicator and directly draggable,
 * matching Radix's `ScrollAreaThumb`.
 *
 * [type] mirrors Radix `type` / `scrollHideDelay`: [ScrollType.Scroll] and
 * [ScrollType.Hover] carry their own hide delay; default is [ScrollType.Hover] (600ms).
 * [dir] mirrors Radix `dir`: `null` inherits [androidx.compose.ui.platform.LocalLayoutDirection].
 *
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
    type: ScrollType = ScrollType.Hover(),
    dir: ScrollDir? = null,
    scrollHideDelay: Int? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    ProvideScrollDir(dir) {
        ScrollAreaContent(
            modifier = modifier,
            style = style,
            orientation = orientation,
            type = type.withHideDelay(scrollHideDelay),
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
    content: @Composable BoxScope.() -> Unit,
) {
    val verticalState = rememberScrollState()
    val horizontalState = rememberScrollState()
    val scrollsVertically = orientation != ScrollAreaOrientation.Horizontal
    val scrollsHorizontally = orientation != ScrollAreaOrientation.Vertical
    val areaState = ScrollAreaState(vertical = verticalState, horizontal = horizontalState)

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

    CompositionLocalProvider(LocalScrollArea provides areaState) {
        Box(
            modifier =
                modifier
                    .hoverable(hoverInteraction)
                    .styleable(styleState, style),
        ) {
            ScrollAreaViewport(
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
                    modifier = Modifier.align(Alignment.CenterEnd),
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
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

/** Scrollable child of [ScrollArea]. Base UI does not export Viewport (used inside Root). */
@Composable
internal fun ScrollAreaViewport(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .onFocusChanged { focused = it.hasFocus }
                .styleable(styleState, rememberScrollAreaViewportStyle(focused)),
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
