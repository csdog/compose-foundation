@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.overlay.Content
import com.toutiaoyan.compose.ui.components.sheet.SheetTransitions
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

    /** `sm:[--drawer-content-width:24rem]` for side drawers. */
    val SideMaxWidth = 384.dp

    /** `75%` viewport width for side drawers. */
    const val SideWidthFraction = 0.75f

    /** Base `--stack-step: 0.05` when a nested drawer is open. */
    const val NestedScaleStep = 0.05f

    /** Base `--peek: 1rem`. */
    val NestedPeek = 16.dp
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
        SheetTransitions.enter(LocalDrawerState.current.swipeDirection.toSheetSide()),
    exit: ExitTransition =
        SheetTransitions.exit(LocalDrawerState.current.swipeDirection.toSheetSide()),
    content: @Composable ColumnScope.() -> Unit,
) {
    val modal =
        LocalDrawerTeleportScope.current
            ?: error("DrawerContentImpl must be used within DrawerPortal { }")
    val drawer = LocalDrawerState.current
    val swipeDirection = drawer.swipeDirection
    val useSnap =
        drawer.hasSnapPoints && swipeDirection.isVertical()

    modal.Content(
        contentAlignment = swipeDirection.contentAlignment(),
        enter = enter,
        exit = exit,
    ) {
        // Do NOT fillMaxSize here: TeleportPrimitive.Content wraps children in a
        // click-absorbing Box. A full-size child would steal Overlay outside-clicks
        // (Sheet puts the panel directly in Content for the same reason). Incoming
        // max constraints still come from Content's fillMaxSize parent.
        BoxWithConstraints {
            val nestModifier =
                if (drawer.nestedOpenCount > 0) {
                    val density = LocalDensity.current
                    val peekPx = with(density) { DrawerDefaults.NestedPeek.toPx() }
                    val scale =
                        (1f - DrawerDefaults.NestedScaleStep * drawer.nestedOpenCount)
                            .coerceAtLeast(0.85f)
                    Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationY =
                            when (swipeDirection) {
                                DrawerSwipeDirection.Down -> -peekPx * drawer.nestedOpenCount
                                DrawerSwipeDirection.Up -> peekPx * drawer.nestedOpenCount
                                else -> 0f
                            }
                        translationX =
                            when (swipeDirection) {
                                DrawerSwipeDirection.Right -> -peekPx * drawer.nestedOpenCount
                                DrawerSwipeDirection.Left -> peekPx * drawer.nestedOpenCount
                                else -> 0f
                            }
                    }
                } else {
                    Modifier
                }

            if (useSnap) {
                DrawerSnapPanel(
                    modifier = modifier.then(nestModifier),
                    style = style,
                    viewportHeightPx = constraints.maxHeight.toFloat(),
                    dismissThresholdFraction = dismissThresholdFraction,
                    dismissAnimationSpec = dismissAnimationSpec,
                    content = content,
                )
            } else {
                DrawerDismissPanel(
                    modifier = modifier.then(nestModifier),
                    style = style,
                    maxHeight = maxHeight,
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

    val sizeModifier =
        when (swipeDirection) {
            DrawerSwipeDirection.Up, DrawerSwipeDirection.Down ->
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = (maxHeight - DrawerDefaults.VerticalMaxHeightInset).coerceAtLeast(0.dp))
            DrawerSwipeDirection.Left, DrawerSwipeDirection.Right ->
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(DrawerDefaults.SideWidthFraction)
                    .widthIn(max = DrawerDefaults.SideMaxWidth)
        }

    val panelModifier =
        modifier
            .then(sizeModifier)
            .anchoredSwipe2D(
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
            .clip(drawerPanelShape(swipeDirection, twRadius.xl))
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
    val maxHeightPx = heightsPx.maxOrNull() ?: viewportHeightPx
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
        remember(points, density, dismissThresholdFraction, dismissAnimationSpec) {
            decayAnchoredDraggableState(
                initialValue = initialIndex,
                density = density,
                positionalThresholdFraction = dismissThresholdFraction,
                snapAnimationSpec = dismissAnimationSpec,
            )
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

    val reverseDirection = swipeDirection == DrawerSwipeDirection.Up
    val maxHeightDp = with(density) { maxHeightPx.toDp() }

    val panelModifier =
        modifier
            .fillMaxWidth()
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
                        flingBehavior = null,
                    )
                } else {
                    Modifier
                },
            )
            .clip(drawerPanelShape(swipeDirection, twRadius.xl))
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
    when (swipeDirection) {
        DrawerSwipeDirection.Left, DrawerSwipeDirection.Right -> {
            Row(modifier = panelModifier) {
                if (showHandle && swipeDirection == DrawerSwipeDirection.Right) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterVertically))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    content = content,
                )
                if (showHandle && swipeDirection == DrawerSwipeDirection.Left) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
        }
        DrawerSwipeDirection.Up, DrawerSwipeDirection.Down -> {
            Column(modifier = panelModifier) {
                if (showHandle && swipeDirection == DrawerSwipeDirection.Down) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                content()
                if (showHandle && swipeDirection == DrawerSwipeDirection.Up) {
                    DrawerSwipeHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
