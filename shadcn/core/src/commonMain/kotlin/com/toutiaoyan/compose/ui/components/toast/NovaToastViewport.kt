@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private const val TOAST_MOTION_MILLIS = 500

// CSS toast enter/exit easing — keep cubic-bezier tuple for visual parity with Sonner/shadcn.
private val ToastEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

private class NovaToastEntry(var message: ToastMessage) {
    val offset = Animatable(1f)
    val focusRequester = FocusRequester()
    val interactions = MutableInteractionSource()
    var measuredHeight by mutableIntStateOf(0)
    val gesture = ToastSwipeState()
    var swipe: SwipeAxis? = null
    var lastDepth = 0
    var wasVisible = false
    var lastDistance = 0f
}

internal class ToastSwipeState {
    var drag by mutableStateOf(Offset.Zero)
    var dragging by mutableStateOf(false)

    fun dismissedAxis(
        axis: SwipeAxis?,
        threshold: Float,
    ): SwipeAxis? = axis.takeIf { maxOf(drag.x, abs(drag.y)) > threshold }

    fun finish(committed: Boolean) {
        if (!committed) drag = Offset.Zero
        dragging = false
    }
}

internal enum class SwipeAxis {
    Right,
    Down,
}

@Composable
internal fun NovaToastViewport(
    state: ToastState,
    modifier: Modifier,
    style: Style,
    position: ToastPosition,
    gap: Dp,
    visibleToasts: Int,
    containerAriaLabel: String,
    cardStyle: Style,
    closeButton: Boolean,
    richColors: Boolean,
    invert: Boolean,
    closeAriaLabel: String,
) {
    val stored = remember(state) { mutableStateListOf<NovaToastEntry>() }
    val active = state.toasts.toList().asReversed()
    val activeById = active.associateBy(ToastMessage::id)
    val storedById = stored.associateBy { it.message.id }
    val activeEntries = active.map { storedById[it.id] ?: NovaToastEntry(it) }
    val leavingEntries = stored.filterNot { it.message.id in activeById }
    val entries = activeEntries + leavingEntries
    val limit = visibleToasts.coerceAtLeast(0)
    SideEffect {
        entries.forEachIndexed { index, entry ->
            activeById[entry.message.id]?.let {
                entry.message = it
                entry.wasVisible = index < limit
            }
        }
        if (stored.size != entries.size || stored.zip(entries).any { (a, b) -> a !== b }) {
            stored.clear()
            stored.addAll(entries)
        }
    }

    val interactions = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactions) }
    val hovered by interactions.collectIsHoveredAsState()
    val inputModeManager = LocalInputModeManager.current
    val focusManager = LocalFocusManager.current
    var hasFocus by remember(state) { mutableStateOf(false) }
    var touchHeld by remember(state) { mutableStateOf(false) }
    var swipeTouch by remember(state) { mutableStateOf<ToastOutsideTouch?>(null) }
    var nextFocusId by remember(state) { mutableStateOf<Long?>(null) }
    var viewportBounds by remember(state) { mutableStateOf<Rect?>(null) }
    val hasKeyboardFocus = hasFocus && inputModeManager.inputMode == InputMode.Keyboard
    val expanded = hovered || hasKeyboardFocus || swipeTouch != null
    val paused =
        rememberUpdatedState(
            expanded || touchHeld || nextFocusId != null || entries.any { it.gesture.dragging },
        )
    val expansion by animateFloatAsState(
        if (expanded) 1f else 0f,
        tween(TOAST_MOTION_MILLIS, easing = ToastEasing),
        label = "Toast stack expansion",
    )
    val top =
        position == ToastPosition.TopStart || position == ToastPosition.TopCenter ||
            position == ToastPosition.TopEnd
    val visible = activeEntries.take(limit)
    if (visible.isEmpty() && swipeTouch != null) {
        SideEffect { swipeTouch = null }
    }
    val touch = swipeTouch
    if (touch != null) {
        DisposableEffect(touch) {
            val remove =
                touch.observe(bounds = { viewportBounds }) {
                    swipeTouch = null
                }
            onDispose { remove() }
        }
    }
    LaunchedEffect(state, nextFocusId) {
        val target = nextFocusId ?: return@LaunchedEffect
        val requester = entries.firstOrNull { it.message.id == target }?.focusRequester
        requester?.requestFocus()
        nextFocusId = null
    }

    fun adjacentToast(id: Long): Long? {
        val current = state.toasts.asReversed()
        val index = current.indexOfFirst { it.id == id }
        if (index !in 0 until limit) return null
        return (current.getOrNull(index + 1) ?: current.getOrNull(index - 1))?.id
    }

    fun restoreToastFocus(adjacentId: Long?) {
        val remaining = state.toasts.asReversed().take(limit)
        nextFocusId = remaining.firstOrNull { it.id == adjacentId }?.id ?: remaining.firstOrNull()?.id
        if (nextFocusId == null && hasFocus) focusManager.clearFocus(force = true)
    }

    fun dismissWithFocus(id: Long) {
        val adjacentId = adjacentToast(id)
        state.dismiss(id)
        restoreToastFocus(adjacentId)
    }

    entries.forEach { entry ->
        key(state, entry.message.id) {
            val message = activeById[entry.message.id]
            LaunchedEffect(message != null) {
                entry.offset.animateTo(
                    if (message != null) 0f else 1f,
                    tween(TOAST_MOTION_MILLIS, easing = ToastEasing),
                )
                if (message == null) stored.remove(entry)
            }
            if (message != null) {
                ToastTimeout(message, paused) { state.dismiss(message.id) }
            }
        }
    }

    // Keep leaving cards painted long enough to finish their motion, with no interactive descendants.
    val shown = visible + leavingEntries.filter { it.wasVisible }
    val gapPx = with(LocalDensity.current) { gap.toPx() }
    var expandedDistance = 0f
    val depths =
        shown.mapIndexed { index, entry ->
            key(state, entry.message.id) {
                val depth = if (entry.message.id in activeById) index else entry.lastDepth
                animateFloatAsState(
                    depth.toFloat(),
                    tween(TOAST_MOTION_MILLIS, easing = ToastEasing),
                    label = "Toast stack depth",
                )
            }
        }
    val distances =
        shown.map { entry ->
            val activeEntry = entry.message.id in activeById
            val distance = if (activeEntry) expandedDistance else entry.lastDistance
            if (activeEntry) expandedDistance += entry.measuredHeight + gapPx
            key(state, entry.message.id) {
                animateFloatAsState(
                    distance,
                    tween(TOAST_MOTION_MILLIS, easing = ToastEasing),
                    label = "Toast stack position",
                )
            }
        }
    val swipeOffsets =
        shown.map { entry ->
            key(state, entry.message.id) {
                animateOffsetAsState(
                    entry.gesture.drag,
                    if (entry.gesture.dragging || entry.swipe != null) {
                        tween(0)
                    } else {
                        tween(TOAST_MOTION_MILLIS, easing = ToastEasing)
                    },
                    label = "Toast swipe movement",
                )
            }
        }
    Layout(
        contents =
            listOf<@Composable () -> Unit>(
                {
                    shown.forEachIndexed { index, entry ->
                        key(state, entry.message.id) {
                            val message = activeById[entry.message.id] ?: entry.message
                            val interactive =
                                entry.message.id in activeById &&
                                    (index == 0 || expanded || entry.message.id == nextFocusId)
                            val cardModifier =
                                if (interactive) {
                                    Modifier
                                        .keyboardInputMode()
                                        .onPreviewKeyEvent { event ->
                                            if (
                                                event.type == KeyEventType.KeyDown && event.key == Key.Escape &&
                                                state.toasts.any { it.id == message.id }
                                            ) {
                                                dismissWithFocus(message.id)
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                        .focusRequester(entry.focusRequester)
                                        .focusable(interactionSource = entry.interactions)
                                        .toastSwipe(entry.gesture, top) { axis, outsideTouch ->
                                            entry.swipe = axis
                                            if (outsideTouch != null && state.toasts.size > 1) {
                                                swipeTouch = outsideTouch
                                            }
                                            if (hasFocus && inputModeManager.inputMode == InputMode.Keyboard) {
                                                dismissWithFocus(message.id)
                                            } else {
                                                state.dismiss(message.id)
                                            }
                                        }
                                } else {
                                    Modifier.clearAndSetSemantics { hideFromAccessibility() }
                                }
                            ToastCard(
                                message,
                                onDismiss = {
                                    if (hasFocus && inputModeManager.inputMode == InputMode.Keyboard) {
                                        dismissWithFocus(message.id)
                                    } else {
                                        state.dismiss(message.id)
                                    }
                                },
                                onAction = {
                                    val restoreFocus = hasFocus && inputModeManager.inputMode == InputMode.Keyboard
                                    val adjacentId = if (restoreFocus) adjacentToast(message.id) else null
                                    message.onAction?.invoke()
                                    if (restoreFocus && state.toasts.none { it.id == message.id }) {
                                        restoreToastFocus(adjacentId)
                                    }
                                },
                                style = cardStyle,
                                closeButton = closeButton,
                                richColors = richColors,
                                invert = invert,
                                closeAriaLabel = closeAriaLabel,
                                modifier = cardModifier.onSizeChanged { entry.measuredHeight = it.height },
                                nova = true,
                                interactive = interactive,
                                isActive = { state.toasts.any { it.id == message.id } },
                                interactionSource = entry.interactions,
                            )
                        }
                    }
                },
                {
                    shown.forEach { entry ->
                        key(state, entry.message.id) {
                            ToastCard(
                                activeById[entry.message.id] ?: entry.message,
                                onDismiss = {},
                                style = cardStyle,
                                closeButton = false,
                                richColors = richColors,
                                invert = invert,
                                closeAriaLabel = closeAriaLabel,
                                nova = true,
                                showContent = false,
                            )
                        }
                    }
                },
            ),
        modifier =
            modifier
                .widthIn(max = 384.dp)
                .fillMaxWidth()
                .onGloballyPositioned { viewportBounds = it.boundsInRoot() }
                .semantics { contentDescription = containerAriaLabel }
                .styleable(styleState, style)
                .hoverable(interactions)
                .onFocusChanged { hasFocus = it.hasFocus }
                .holdToastOnTouch(state, inputModeManager) { touchHeld = it }
                .focusGroup()
                .toastViewportShortcut(visible.isNotEmpty()),
    ) { (contents, surfaces), constraints ->
        val width = constraints.maxWidth
        val cardConstraints = constraints.copy(minHeight = 0)
        val cards = contents.map { it.measure(cardConstraints) }
        val frontHeight = cards.firstOrNull()?.height ?: 0
        val growth = expansion
        var stackHeight = 0f
        val placements =
            shown.mapIndexed { index, entry ->
                val card = cards[index]
                val leaving = entry.message.id !in activeById
                if (!leaving) {
                    entry.lastDepth = index
                    entry.lastDistance = distances[index].value
                }
                val depth = depths[index].value
                val collapsedScale = (1f - depth * 0.1f).coerceAtLeast(0f)
                val scale = lerp(collapsedScale, 1f, growth)
                // Scale about the bottom edge while keeping the visible top edge exactly one peek higher.
                val collapsedDistance = depth * gapPx + frontHeight * (1f - collapsedScale)
                val distance = lerp(collapsedDistance, distances[index].value, growth)
                val height = lerp(frontHeight.toFloat(), card.height.toFloat(), growth)
                val contentAlpha = lerp(1f, growth, depth.coerceIn(0f, 1f))
                val surface =
                    if (contentAlpha < 1f) {
                        val sized = height.roundToInt()
                        surfaces[index].measure(cardConstraints.copy(minHeight = sized, maxHeight = sized))
                    } else {
                        null
                    }
                if (!leaving) stackHeight = maxOf(stackHeight, distance + height * scale)
                StackPlacement(entry, card, surface, scale, distance, contentAlpha)
            }
        val height = stackHeight.roundToInt().coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            placements.forEachIndexed { index, placement ->
                val (entry, card, surface, scale, distance, contentAlpha) = placement
                val motion = entry.offset.value
                val swipe = swipeOffsets[index].value
                val leaving = entry.message.id !in activeById
                val sign = if (top) -1f else 1f
                val movingX =
                    swipe.x + if (leaving && entry.swipe == SwipeAxis.Right) motion * card.width * 1.5f else 0f
                val movingY =
                    swipe.y + if (entry.swipe != SwipeAxis.Right) sign * motion * card.height * 1.5f else 0f
                val z = -entry.lastDepth.toFloat() + if (leaving) 0.5f else 0f

                fun place(
                    layer: Placeable,
                    opacity: Float,
                ) {
                    val y = if (top) distance else height - layer.height - distance
                    layer.placeWithLayer((width - layer.width) / 2, y.roundToInt(), z) {
                        transformOrigin = TransformOrigin(0.5f, if (top) 0f else 1f)
                        scaleX = scale
                        scaleY = scale
                        translationX = movingX
                        translationY = movingY
                        alpha = opacity
                    }
                }
                surface?.let { place(it, 1f) }
                if (contentAlpha > 0f) place(card, contentAlpha)
            }
        }
    }
}

private data class StackPlacement(
    val entry: NovaToastEntry,
    val card: Placeable,
    val surface: Placeable?,
    val scale: Float,
    val distance: Float,
    val contentAlpha: Float,
)

@Composable
internal fun ToastTimeout(
    message: ToastMessage,
    paused: State<Boolean>,
    onTimeout: () -> Unit,
) {
    val latestTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(message.id, message.revision, message.durationMillis) {
        if (message.durationMillis <= 0) return@LaunchedEffect
        var remaining = message.durationMillis
        var resumedAt: Long? = null
        snapshotFlow { paused.value }.collectLatest { isPaused ->
            val time = withFrameMillis { it }
            resumedAt?.let { remaining = (remaining - (time - it)).coerceAtLeast(0L) }
            resumedAt = if (isPaused) null else time
            if (!isPaused) {
                delay(remaining)
                latestTimeout()
            }
        }
    }
}

internal fun Modifier.holdToastOnTouch(
    key: Any,
    inputModeManager: InputModeManager,
    onHold: (Boolean) -> Unit,
): Modifier =
    pointerInput(key, inputModeManager) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (down.type == PointerType.Touch) {
                if (inputModeManager.inputMode != InputMode.Touch) {
                    inputModeManager.requestInputMode(InputMode.Touch)
                }
                onHold(true)
                try {
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                    } while (event.changes.any { it.type == PointerType.Touch && it.pressed })
                } finally {
                    onHold(false)
                }
            }
        }
    }

internal fun Modifier.toastSwipe(
    gesture: ToastSwipeState,
    top: Boolean,
    onSwipe: (SwipeAxis, ToastOutsideTouch?) -> Unit,
): Modifier =
    pointerInput(gesture, top) {
        val slop = 10.dp.toPx()
        val threshold = 40.dp.toPx()
        val verticalSign = if (top) -1 else 1
        awaitEachGesture {
            val down = awaitFirstDown()
            val outsideTouch = swipeOutsideTouch(down.type, currentEvent)
            val releaseScroll = preventToastTouchScroll(currentEvent)
            var displacement = Offset.Zero
            var axis: SwipeAxis? = null
            var committed = false
            try {
                while (true) {
                    val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                    if (change.isConsumed) break
                    if (!change.pressed) {
                        val dismissed = gesture.dismissedAxis(axis, threshold)
                        if (dismissed != null) {
                            change.consume()
                            committed = true
                            onSwipe(dismissed, outsideTouch)
                        }
                        break
                    }
                    displacement += change.positionChange()
                    axis = axis ?: displacement.swipeAxis(slop, verticalSign)
                    if (axis != null) {
                        gesture.dragging = true
                        gesture.drag =
                            when (axis) {
                                SwipeAxis.Right -> Offset(displacement.x.coerceAtLeast(0f), 0f)
                                SwipeAxis.Down ->
                                    Offset(0f, (displacement.y * verticalSign).coerceAtLeast(0f) * verticalSign)
                            }
                        change.consume()
                    }
                }
            } finally {
                releaseScroll?.invoke()
                gesture.finish(committed)
            }
        }
    }

private fun swipeOutsideTouch(
    pointerType: PointerType,
    event: PointerEvent,
): ToastOutsideTouch? = if (pointerType == PointerType.Touch) captureToastOutsideTouch(event) else null

private fun Offset.swipeAxis(
    slop: Float,
    verticalSign: Int,
): SwipeAxis? =
    when {
        x > slop && x >= abs(y) -> SwipeAxis.Right
        y * verticalSign > slop && abs(y) > abs(x) -> SwipeAxis.Down
        else -> null
    }
