@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A [ScrollArea]-styled overlay for [androidx.compose.foundation.lazy.LazyColumn] /
 * [androidx.compose.foundation.lazy.LazyRow]: the lazy list owns scrolling (and keeps
 * laziness), while this composable paints a draggable custom thumb driven by [state].
 *
 * Do **not** wrap the lazy list in [ScrollArea] -- nested same-axis scrollables fight
 * over gestures and `verticalScroll` forces unbounded measure, which collapses
 * laziness. Pass the same [LazyListState] to both this wrapper and the list.
 *
 * [ScrollAreaOrientation.Both] is treated as vertical: a single lazy list only scrolls
 * on one axis. [type] / [dir] match [ScrollArea] (Radix `type` / `dir`).
 *
 * Usage:
 * ```
 * val state = rememberLazyListState()
 * LazyScrollArea(state = state, modifier = Modifier.fillMaxSize()) {
 *     LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
 *         items(100) { Text { "Item $it" } }
 *     }
 * }
 * ```
 */
@Composable
fun LazyScrollArea(
    state: LazyListState,
    modifier: Modifier = Modifier,
    style: Style = Style,
    orientation: ScrollAreaOrientation = ScrollAreaOrientation.Vertical,
    type: ScrollType = ScrollType.Hover(),
    dir: ScrollDir? = null,
    scrollHideDelay: Int? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    ProvideScrollDir(dir) {
        LazyScrollAreaContent(
            state = state,
            modifier = modifier,
            style = style,
            orientation = orientation,
            type = type.withHideDelay(scrollHideDelay),
            content = content,
        )
    }
}

@Composable
private fun LazyScrollAreaContent(
    state: LazyListState,
    modifier: Modifier,
    style: Style,
    orientation: ScrollAreaOrientation,
    type: ScrollType,
    content: @Composable BoxScope.() -> Unit,
) {
    // Observe scroll position so the thumb recomposes while the list moves.
    @Suppress("UNUSED_VARIABLE")
    val firstVisibleItemIndex = state.firstVisibleItemIndex
    @Suppress("UNUSED_VARIABLE")
    val firstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset

    val vertical = orientation != ScrollAreaOrientation.Horizontal
    val metrics = lazyListScrollMetrics(state.layoutInfo)

    val hoverInteraction = remember { MutableInteractionSource() }
    val isHovered by hoverInteraction.collectIsHoveredAsState()
    val styleState = remember { MutableStyleState(hoverInteraction) }
    val thumbInteraction = remember { MutableInteractionSource() }
    val thumbDragging by thumbInteraction.collectIsDraggedAsState()

    val overflowing = metrics != null && metrics.maxValue > 0
    val scrolling = state.isScrollInProgress || thumbDragging
    val visible =
        rememberScrollbarVisible(
            type = type,
            overflowing = overflowing,
            scrolling = scrolling,
            hovering = isHovered,
        )
    val forceShowWhenEmpty = type is ScrollType.Always

    Box(
        modifier =
            modifier
                .hoverable(hoverInteraction)
                .styleable(styleState, style),
    ) {
        content()
        if (scrollbarMayShow(type, overflowing)) {
            val value = metrics?.value ?: 0
            val maxValue = metrics?.maxValue ?: 0
            val viewportSize = metrics?.viewportSize ?: 1
            ScrollBar(
                value = value,
                maxValue = maxValue,
                viewportSize = viewportSize,
                onDragDelta = { delta -> state.dispatchRawDelta(delta) },
                vertical = vertical,
                visible = visible,
                forceShowWhenEmpty = forceShowWhenEmpty,
                interactionSource = thumbInteraction,
                modifier =
                    Modifier.align(
                        if (vertical) Alignment.CenterEnd else Alignment.BottomCenter,
                    ),
            )
        }
    }
}
