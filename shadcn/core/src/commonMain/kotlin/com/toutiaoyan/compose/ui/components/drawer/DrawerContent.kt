@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.animation.core.animate
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.overlay.Content
import com.toutiaoyan.compose.ui.gesture.AnchoredSwipeValue
import com.toutiaoyan.compose.ui.gesture.SwipeDismissAxis
import com.toutiaoyan.compose.ui.gesture.SwipeGesture
import com.toutiaoyan.compose.ui.gesture.anchoredSwipe2D
import com.toutiaoyan.compose.ui.gesture.decayAnchoredDraggableState
import com.toutiaoyan.compose.ui.gesture.rememberDecayAnchoredDraggableState
import kotlin.math.roundToInt

/** base-nova sizing defaults. */
object DrawerDefaults {
    /** `calc(100dvh - 6rem)` cap for vertical drawers. */
    val VerticalMaxHeightInset = 96.dp

    /** Base Nova window inset for attached panels (`--drawer-inset: 0px`). */
    val WindowInset = 0.dp
    val CornerRadius = 14.dp
    val SideInset = WindowInset
    val SideCornerRadius = CornerRadius

    /** `sm:[--drawer-content-width:24rem]` for side drawers. */
    val SideMaxWidth = 384.dp

    /** `75%` viewport width for side drawers. */
    const val SideWidthFraction = 0.75f

    /** Base `--stack-step: 0.05` when a nested drawer is open. */
    const val NestedScaleStep = 0.05f

    /** Base `--peek: 1rem`. */
    val NestedPeek = 16.dp
}

private val NovaRightWindowInset = 8.dp

private val DrawerSwipeDirection.novaWindowInset: Dp
    get() = if (this == DrawerSwipeDirection.Right) NovaRightWindowInset else DrawerDefaults.WindowInset

internal val DrawerMotionEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
internal const val DrawerMotionMillis = 450

private fun drawerEnter(direction: DrawerSwipeDirection): EnterTransition =
    when (direction) {
        DrawerSwipeDirection.Down -> slideInVertically(tween(DrawerMotionMillis, easing = DrawerMotionEasing)) { it }
        DrawerSwipeDirection.Up -> slideInVertically(tween(DrawerMotionMillis, easing = DrawerMotionEasing)) { -it }
        DrawerSwipeDirection.Left -> slideInHorizontally(tween(DrawerMotionMillis, easing = DrawerMotionEasing)) { -it }
        DrawerSwipeDirection.Right -> slideInHorizontally(tween(DrawerMotionMillis, easing = DrawerMotionEasing)) { it }
    }

private fun drawerExit(direction: DrawerSwipeDirection): ExitTransition =
    when (direction) {
        DrawerSwipeDirection.Down -> slideOutVertically(tween(DrawerMotionMillis, easing = DrawerMotionEasing)) { it }
        DrawerSwipeDirection.Up -> slideOutVertically(tween(DrawerMotionMillis, easing = DrawerMotionEasing)) { -it }
        DrawerSwipeDirection.Left ->
            slideOutHorizontally(
                tween(DrawerMotionMillis, easing = DrawerMotionEasing),
            ) { -it }
        DrawerSwipeDirection.Right ->
            slideOutHorizontally(
                tween(DrawerMotionMillis, easing = DrawerMotionEasing),
            ) { it }
    }

/**
 * Styled drawer content — shadcn base-nova / Base UI `DrawerContent` wrapper:
 * Portal + Overlay (when modal) + Viewport/Popup panel. Paints [DrawerSwipeHandle]
 * when [Drawer] `showSwipeHandle` is true (base-nova default is false).
 */
@Composable
fun DrawerScope.DrawerContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    DrawerPortal(
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
    ) {
        if (LocalDrawerState.current.modal) {
            DrawerOverlay()
        }
        DrawerContentImpl(
            modifier = modifier,
            style = style,
            content = content,
        )
    }
}

/**
 * Convenience: [title] + optional [description] as [DrawerHeader], then [content].
 */
@Composable
fun DrawerScope.DrawerContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    DrawerContent(
        modifier = modifier,
        style = style,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
    ) {
        DrawerHeader(title, description = description)
        content()
    }
}

/**
 * Panel primitive (drag, chrome, slide). Must be composed under [DrawerPortal].
 * Prefer [DrawerContent] for the usual Portal+Overlay+panel bundle.
 */
@Composable
internal fun DrawerContentImpl(
    modifier: Modifier = Modifier,
    style: Style = Style,
    dismissThresholdFraction: Float = SwipeGesture.DefaultThresholdFraction,
    animationSpec: AnimationSpec<Float> = SwipeGesture.SettleSpring,
    dismissAnimationSpec: AnimationSpec<Float> = animationSpec,
    enter: EnterTransition =
        drawerEnter(LocalDrawerState.current.swipeDirection),
    exit: ExitTransition =
        drawerExit(LocalDrawerState.current.swipeDirection),
    content: @Composable ColumnScope.() -> Unit,
) {
    val modal =
        LocalDrawerTeleportScope.current
            ?: error("DrawerContentImpl must be used within DrawerPortal { }")
    val drawer = LocalDrawerState.current
    val swipeDirection = drawer.swipeDirection
    val useSnap =
        drawer.hasSnapPoints && swipeDirection.isVertical()

    val floating = currentTheme.activePreset == StylePreset.Nova
    val insetPx = with(LocalDensity.current) { swipeDirection.novaWindowInset.roundToPx() }
    val panelAlignment =
        if (floating) {
            Alignment { size, space, _ ->
                IntOffset(
                    when (swipeDirection) {
                        DrawerSwipeDirection.Left -> insetPx
                        DrawerSwipeDirection.Right -> space.width - size.width - insetPx
                        else -> (space.width - size.width) / 2
                    },
                    when (swipeDirection) {
                        DrawerSwipeDirection.Up -> insetPx
                        DrawerSwipeDirection.Down -> space.height - size.height - insetPx
                        else -> (space.height - size.height) / 2
                    },
                )
            }
        } else {
            swipeDirection.contentAlignment()
        }

    modal.Content(
        contentAlignment = panelAlignment,
        enter = enter,
        exit = exit,
    ) {
        // Do NOT fillMaxSize here: TeleportPrimitive.Content wraps children in a
        // click-absorbing Box. A full-size child would steal Overlay outside-clicks
        // (Sheet puts the panel directly in Content for the same reason). Incoming
        // max constraints still come from Content's fillMaxSize parent.
        BoxWithConstraints {
            val stackDepth =
                animateFloatAsState(
                    targetValue = drawer.nestedOpenCount.toFloat(),
                    animationSpec = tween(DrawerMotionMillis, easing = DrawerMotionEasing),
                    label = "Drawer stack depth",
                )
            val peekPx = with(LocalDensity.current) { DrawerDefaults.NestedPeek.toPx() }
            // Keep the same layer while returning to depth zero so close/reversal
            // continues from the currently rendered transform instead of jumping.
            val nestHost = LocalDrawerNestHost.current
            val nestedSwipe = { nestHost?.nestedSwipeProgress ?: 0f }
            val nestModifier =
                Modifier.graphicsLayer {
                    val depth =
                        drawerStackDepth(
                            stackDepth.value,
                            drawer.nestedOpenCount,
                            nestedSwipe(),
                        )
                    val scale = (1f - DrawerDefaults.NestedScaleStep * depth).coerceIn(0f, 1f)
                    scaleX = scale
                    scaleY = scale
                    transformOrigin =
                        when (swipeDirection) {
                            DrawerSwipeDirection.Down -> TransformOrigin(0.5f, 1f)
                            DrawerSwipeDirection.Up -> TransformOrigin(0.5f, 0f)
                            DrawerSwipeDirection.Right -> TransformOrigin(1f, 0.5f)
                            DrawerSwipeDirection.Left -> TransformOrigin(0f, 0.5f)
                        }
                    val x = peekPx * depth + (1f - scale) * size.width
                    val y = peekPx * depth + (1f - scale) * size.height
                    translationX =
                        when (swipeDirection) {
                            DrawerSwipeDirection.Right -> -x
                            DrawerSwipeDirection.Left -> x
                            else -> 0f
                        }
                    translationY =
                        when (swipeDirection) {
                            DrawerSwipeDirection.Down -> -y
                            DrawerSwipeDirection.Up -> y
                            else -> 0f
                        }
                }

            val stackHeight =
                animateFloatAsState(
                    if (drawer.nestedOpenCount > 0 &&
                        nestHost != null
                    ) {
                        nestHost.frontmostHeightPx
                    } else {
                        nestHost?.naturalHeightPx
                            ?: 0f
                    },
                    tween(DrawerMotionMillis, easing = DrawerMotionEasing),
                    label = "Drawer frontmost height",
                )
            val heightModifier =
                if (swipeDirection.isVertical()) {
                    Modifier.layout { measurable, constraints ->
                        val height = stackHeight.value.roundToInt()
                        val adjusted =
                            if ((drawer.nestedOpenCount > 0 || stackDepth.value > 0f) && height > 0) {
                                val bounded = height.coerceIn(constraints.minHeight, constraints.maxHeight)
                                constraints.copy(minHeight = bounded, maxHeight = bounded)
                            } else {
                                constraints
                            }
                        val placeable = measurable.measure(adjusted)
                        layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                    }
                } else {
                    Modifier
                }
            val measuredModifier =
                modifier.then(heightModifier).then(nestModifier).onSizeChanged {
                    // Never feed a temporary stacked height back as the natural height.
                    if (drawer.nestedOpenCount == 0 && stackDepth.value == 0f) {
                        nestHost?.onNaturalHeight(
                            it.height.toFloat(),
                        )
                    }
                }

            if (useSnap) {
                DrawerSnapPanel(
                    modifier = measuredModifier,
                    style = style,
                    viewportHeightPx = constraints.maxHeight.toFloat(),
                    viewportWidth = maxWidth,
                    dismissThresholdFraction = dismissThresholdFraction,
                    dismissAnimationSpec = dismissAnimationSpec,
                    content = content,
                )
            } else {
                DrawerDismissPanel(
                    modifier = measuredModifier,
                    style = style,
                    maxHeight = maxHeight,
                    maxWidth = maxWidth,
                    dismissThresholdFraction = dismissThresholdFraction,
                    dismissAnimationSpec = dismissAnimationSpec,
                    content = content,
                )
            }
        }
    }
}

@Composable
private fun DrawerDismissPanel(
    modifier: Modifier,
    style: Style,
    maxHeight: androidx.compose.ui.unit.Dp,
    maxWidth: androidx.compose.ui.unit.Dp,
    dismissThresholdFraction: Float,
    dismissAnimationSpec: AnimationSpec<Float>,
    content: @Composable ColumnScope.() -> Unit,
) {
    val drawer = LocalDrawerState.current
    val swipeDirection = drawer.swipeDirection
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val showHandle = drawer.showSwipeHandle

    val enableHorizontalRight = swipeDirection == DrawerSwipeDirection.Right
    val enableHorizontalLeft = swipeDirection == DrawerSwipeDirection.Left
    val enableVerticalDown = swipeDirection == DrawerSwipeDirection.Down
    val enableVerticalUp = swipeDirection == DrawerSwipeDirection.Up

    val horizontalState =
        rememberDecayAnchoredDraggableState(
            initialValue = AnchoredSwipeValue.Open,
            positionalThresholdFraction = dismissThresholdFraction,
            snapAnimationSpec = dismissAnimationSpec,
        )
    val verticalState =
        rememberDecayAnchoredDraggableState(
            initialValue = AnchoredSwipeValue.Open,
            positionalThresholdFraction = dismissThresholdFraction,
            snapAnimationSpec = dismissAnimationSpec,
        )

    val useHorizontal = drawer.dismissible && (enableHorizontalRight || enableHorizontalLeft)
    val useVertical = drawer.dismissible && (enableVerticalDown || enableVerticalUp)
    val horizontal =
        if (useHorizontal) {
            SwipeDismissAxis(
                state = horizontalState,
                reverseDirection = enableHorizontalLeft,
            )
        } else {
            null
        }
    val vertical =
        if (useVertical) {
            SwipeDismissAxis(
                state = verticalState,
                reverseDirection = enableVerticalUp,
            )
        } else {
            null
        }

    LaunchedEffect(drawer.open) {
        if (drawer.open) {
            horizontalState.snapTo(AnchoredSwipeValue.Open)
            verticalState.snapTo(AnchoredSwipeValue.Open)
        }
    }

    LaunchedEffect(horizontalState.settledValue, verticalState.settledValue) {
        val horizontalClosed =
            useHorizontal && horizontalState.settledValue == AnchoredSwipeValue.Closed
        val verticalClosed =
            useVertical && verticalState.settledValue == AnchoredSwipeValue.Closed
        if (horizontalClosed || verticalClosed) {
            drawer.onOpenChange(false)
        }
    }

    val indentAxis = if (swipeDirection.isVertical()) verticalState else horizontalState
    val nestHost = LocalDrawerNestHost.current
    DisposableEffect(nestHost, indentAxis) {
        nestHost?.attachSwipeProgress {
            drawerSwipeProgress(indentAxis.offset, indentAxis.anchors.positionOf(AnchoredSwipeValue.Closed))
        }
        onDispose { nestHost?.detachSwipeProgress() }
    }
    DisposableEffect(drawer.indentGesture, indentAxis) {
        drawer.indentGesture?.progress = {
            val extent = indentAxis.anchors.positionOf(AnchoredSwipeValue.Closed)
            val offset = indentAxis.offset
            if (!extent.isFinite() || extent <= 0f || !offset.isFinite()) {
                null
            } else {
                (1f - offset / extent).coerceIn(0f, 1f)
            }
        }
        onDispose { drawer.indentGesture?.progress = { null } }
    }

    val sizeModifier =
        when (swipeDirection) {
            DrawerSwipeDirection.Up, DrawerSwipeDirection.Down ->
                (
                    if (currentTheme.activePreset == StylePreset.Nova) {
                        Modifier.width((maxWidth - DrawerDefaults.WindowInset * 2).coerceAtLeast(0.dp))
                    } else {
                        Modifier.fillMaxWidth()
                    }
                )
                    .heightIn(max = (maxHeight - DrawerDefaults.VerticalMaxHeightInset).coerceAtLeast(0.dp))
            DrawerSwipeDirection.Left, DrawerSwipeDirection.Right ->
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Modifier.height(
                        (maxHeight - swipeDirection.novaWindowInset * 2).coerceAtLeast(0.dp),
                    ).width(
                        if (maxWidth >= 640.dp) {
                            DrawerDefaults.SideMaxWidth
                        } else {
                            maxWidth * DrawerDefaults.SideWidthFraction
                        },
                    )
                } else {
                    Modifier.fillMaxHeight().fillMaxWidth(
                        DrawerDefaults.SideWidthFraction,
                    ).widthIn(max = DrawerDefaults.SideMaxWidth)
                }
        }

    val panelShape =
        drawerPhysicalPanelShape(swipeDirection, twRadius.xl, LocalLayoutDirection.current)
    val panelModifier =
        modifier
            .then(sizeModifier)
            .then(
                if (horizontal != null || vertical != null) {
                    Modifier.anchoredSwipe2D(
                        horizontal = horizontal,
                        vertical = vertical,
                        closingOffset = { hPx, vPx ->
                            val x =
                                when {
                                    enableHorizontalRight -> hPx
                                    enableHorizontalLeft -> -hPx
                                    else -> 0
                                }
                            val y =
                                when {
                                    enableVerticalDown -> vPx
                                    enableVerticalUp -> -vPx
                                    else -> 0
                                }
                            IntOffset(x, y)
                        },
                    )
                } else {
                    Modifier
                },
            )
            .clip(panelShape)
            .styleable(
                styleState,
                swipeDirection.rememberPanelStyle() then style,
            )

    DrawerPanelChrome(
        swipeDirection = swipeDirection,
        showHandle = showHandle,
        panelModifier = panelModifier,
        content = content,
    )
}

@Composable
private fun DrawerSnapPanel(
    modifier: Modifier,
    style: Style,
    viewportHeightPx: Float,
    viewportWidth: androidx.compose.ui.unit.Dp,
    dismissThresholdFraction: Float,
    dismissAnimationSpec: AnimationSpec<Float>,
    content: @Composable ColumnScope.() -> Unit,
) {
    val drawer = LocalDrawerState.current
    val swipeDirection = drawer.swipeDirection
    val density = LocalDensity.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val showHandle = drawer.showSwipeHandle
    val points = drawer.snapPoints.orEmpty()
    val onSnapChange = rememberUpdatedState(drawer.onSnapPointChange)
    val onOpenChange = rememberUpdatedState(drawer.onOpenChange)

    val heightsPx =
        remember(points, viewportHeightPx, density) {
            points.map { it.resolvePx(viewportHeightPx, density) }
        }
    // base-nova keeps a viewport-sized snap panel subject to the same 6rem cap
    // as ordinary vertical drawers. Snap heights are exposed by translating it.
    val maxHeightPx =
        if (currentTheme.activePreset == StylePreset.Nova) {
            (viewportHeightPx - with(density) { DrawerDefaults.VerticalMaxHeightInset.toPx() }).coerceAtLeast(0f)
        } else {
            heightsPx.maxOrNull() ?: viewportHeightPx
        }
    val closingOffsets =
        remember(heightsPx, maxHeightPx) {
            drawerSnapClosingOffsets(heightsPx, maxHeightPx)
        }
    val closedIndex = points.size

    fun indexOfSnap(point: DrawerSnapPoint?): Int {
        if (point == null) return 0
        val idx = points.indexOf(point)
        return if (idx >= 0) idx else 0
    }

    val initialIndex = indexOfSnap(drawer.snapPoint).coerceIn(0, (points.size - 1).coerceAtLeast(0))
    val snapState =
        remember(points, density, dismissThresholdFraction, dismissAnimationSpec, drawer.snapToSequentialPoints) {
            if (drawer.snapToSequentialPoints) {
                AnchoredDraggableState(initialIndex)
            } else {
                decayAnchoredDraggableState(
                    initialValue = initialIndex,
                    density = density,
                    positionalThresholdFraction = dismissThresholdFraction,
                    snapAnimationSpec = dismissAnimationSpec,
                )
            }
        }

    LaunchedEffect(closingOffsets, drawer.open) {
        if (closingOffsets.isEmpty()) return@LaunchedEffect
        snapState.updateAnchors(
            DraggableAnchors {
                closingOffsets.forEachIndexed { index, offset ->
                    index at offset
                }
            },
        )
        if (drawer.open) {
            val target = indexOfSnap(drawer.snapPoint).coerceIn(0, points.lastIndex.coerceAtLeast(0))
            snapState.snapTo(target)
        }
    }

    LaunchedEffect(drawer.snapPoint, points) {
        if (!drawer.open || points.isEmpty()) return@LaunchedEffect
        val target = indexOfSnap(drawer.snapPoint)
        if (snapState.settledValue != target && target in points.indices) {
            snapState.snapTo(target)
        }
    }

    LaunchedEffect(snapState.settledValue) {
        val settled = snapState.settledValue
        if (settled == closedIndex) {
            onOpenChange.value(false)
            onSnapChange.value?.invoke(null)
        } else if (settled in points.indices) {
            onSnapChange.value?.invoke(points[settled])
        }
    }

    val nestHost = LocalDrawerNestHost.current
    DisposableEffect(nestHost, snapState, closingOffsets) {
        nestHost?.attachSwipeProgress {
            drawerSwipeProgress(snapState.offset, closingOffsets.lastOrNull() ?: 0f)
        }
        onDispose { nestHost?.detachSwipeProgress() }
    }

    DisposableEffect(drawer.indentGesture, snapState, closingOffsets) {
        drawer.indentGesture?.progress = {
            val lowest = closingOffsets.dropLast(1).maxOrNull() ?: 0f
            val closed = closingOffsets.lastOrNull() ?: lowest
            val offset = snapState.offset
            // Between snap points the background stays indented. Only the
            // segment below the lowest visible snap reveals the page again.
            if (!offset.isFinite() || closed <= lowest) {
                null
            } else {
                (1f - (offset - lowest) / (closed - lowest)).coerceIn(0f, 1f)
            }
        }
        onDispose { drawer.indentGesture?.progress = { null } }
    }

    val sequentialFling =
        remember(snapState, closingOffsets, dismissAnimationSpec) {
            object : FlingBehavior {
                override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                    if (closingOffsets.isEmpty()) return initialVelocity
                    val index =
                        nearestDrawerSnapIndex(
                            snapState.requireOffset(),
                            closingOffsets,
                            true,
                            snapState.settledValue,
                            initialVelocity,
                        )
                    animate(
                        snapState.requireOffset(),
                        closingOffsets[index],
                        initialVelocity,
                        dismissAnimationSpec,
                    ) { value, _ -> scrollBy(value - snapState.requireOffset()) }
                    return 0f
                }
            }
        }
    val reverseDirection = swipeDirection == DrawerSwipeDirection.Up
    val maxHeightDp = with(density) { maxHeightPx.toDp() }

    val panelModifier =
        modifier
            .then(
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Modifier.width((viewportWidth - DrawerDefaults.WindowInset * 2).coerceAtLeast(0.dp))
                } else {
                    Modifier.fillMaxWidth()
                },
            )
            .height(maxHeightDp)
            .offset {
                val closing = snapState.offset.let { if (it.isNaN()) 0f else it }
                val y = if (reverseDirection) -closing else closing
                IntOffset(0, y.roundToInt())
            }
            .then(
                if (drawer.dismissible) {
                    Modifier.anchoredDraggable(
                        state = snapState,
                        orientation = Orientation.Vertical,
                        reverseDirection = reverseDirection,
                        flingBehavior = if (drawer.snapToSequentialPoints) sequentialFling else null,
                    )
                } else {
                    Modifier
                },
            )
            .clip(
                drawerPhysicalPanelShape(swipeDirection, twRadius.xl, LocalLayoutDirection.current),
            )
            .styleable(
                styleState,
                swipeDirection.rememberPanelStyle() then style,
            )

    DrawerPanelChrome(
        swipeDirection = swipeDirection,
        showHandle = showHandle,
        panelModifier = panelModifier,
        content = content,
    )
}

@Composable
private fun DrawerPanelChrome(
    swipeDirection: DrawerSwipeDirection,
    showHandle: Boolean,
    panelModifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val nestHost = LocalDrawerNestHost.current
    val nestedCount = LocalDrawerState.current.nestedOpenCount
    val nestedSwipe = { nestHost?.nestedSwipeProgress ?: 0f }
    val brightness =
        animateFloatAsState(
            if (nestedCount > 0) 0.05f else 0f,
            tween(DrawerMotionMillis, easing = DrawerMotionEasing),
            label = "Drawer stack brightness",
        )
    val chrome =
        panelModifier.drawWithContent {
            drawContent()
            drawRect(Color.Black.copy(alpha = brightness.value))
        }
    val contentAlpha =
        animateFloatAsState(
            if (nestedCount > 0 && nestedSwipe() == 0f) 0f else 1f,
            tween(300, easing = DrawerMotionEasing),
            label = "Drawer covered content",
        )
    when (swipeDirection) {
        DrawerSwipeDirection.Left, DrawerSwipeDirection.Right -> {
            Row(modifier = chrome, horizontalArrangement = Arrangement.Absolute.Left) {
                if (showHandle && swipeDirection == DrawerSwipeDirection.Right) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterVertically))
                }
                Column(
                    modifier = Modifier.weight(1f).graphicsLayer { alpha = contentAlpha.value },
                    content = content,
                )
                if (showHandle && swipeDirection == DrawerSwipeDirection.Left) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
        }
        DrawerSwipeDirection.Up, DrawerSwipeDirection.Down -> {
            Column(modifier = chrome) {
                if (showHandle && swipeDirection == DrawerSwipeDirection.Down) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                Column(
                    Modifier.weight(1f, fill = false).graphicsLayer { alpha = contentAlpha.value },
                    content = content,
                )
                if (showHandle && swipeDirection == DrawerSwipeDirection.Up) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
