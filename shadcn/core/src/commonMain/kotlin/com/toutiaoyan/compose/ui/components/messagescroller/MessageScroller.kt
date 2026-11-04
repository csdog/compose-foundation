@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.messagescroller
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.hideFromAccessibility
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlinx.coroutines.coroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.icons.ArrowDown
import com.toutiaoyan.compose.ui.icons.MessageScrollerArrow
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.styles.scrollFade
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.launch
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * How close to the bottom (in px) the user has to already be, right before new content
 * arrives, for [MessageScroller] to auto-scroll them along with it -- and how close
 * they have to currently be for [MessageScrollerButton] to hide itself.
 */
internal const val DEFAULT_AUTO_SCROLL_THRESHOLD_PX = 64

/** Which edge of the scroller a [MessageScrollerButton] jumps to, matching real shadcn's `direction` prop. */
enum class MessageScrollerDirection { Start, End }

// A self-generated ImageVector that rotates 180deg for [MessageScrollerDirection.Start]
// -- not a third-party icon-set dependency (this library still takes none -- see README),
// just a plain text glyph doesn't render on WasmJS (Skia has no browser emoji-font
// fallback). Shared between [MessageScroller]'s own `icon` parameter and
// [MessageScrollerButton]'s so overriding it once, at whichever level a caller is
// already customizing, doesn't require redeclaring shadcn's own default glyph.
private val DefaultMessageScrollerIcon: @Composable (rotationDegrees: Float) -> Unit = { rotationDegrees ->
    val nova = currentTheme.activePreset == StylePreset.Nova
    Icon(
        if (nova) MessageScrollerArrow else ArrowDown,
        tint = currentTheme.colors.foreground,
        modifier = (if (nova) Modifier.size(16.dp) else Modifier).graphicsLayer(rotationZ = rotationDegrees),
    )
}

private fun defaultMessageScrollerButton(
    icon: @Composable (rotationDegrees: Float) -> Unit,
): @Composable BoxScope.(isVisible: Boolean, onClick: () -> Unit) -> Unit =
    { isVisible, onClick ->
        MessageScrollerButton(
            visible = isVisible,
            onClick = { onClick() },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = twSpacing.md),
            icon = icon,
        )
    }

/**
 * Whether the scroll position is within [thresholdPx] of the bottom of the content, as a
 * plain function with no Compose dependency -- drives [MessageScrollerButton]'s
 * visibility (shown only once scrolled meaningfully away from the bottom).
 */
internal fun isMessageScrollerNearBottom(
    value: Int,
    maxValue: Int,
    thresholdPx: Int,
): Boolean = (maxValue - value) <= thresholdPx

/**
 * Whether new content arriving should pull the scroll position along with it, given the
 * reader is currently [following] the live edge. Real shadcn's `message-scroller.tsx`
 * auto-scrolls to follow new messages *only* while the reader hasn't released the view --
 * if they've scrolled up into history, new messages must not yank them back down, even if
 * [scrollValue] happens to already be near [previousMaxValue] again.
 */
internal fun shouldAutoScrollToBottom(
    following: Boolean,
    previousMaxValue: Int,
    newMaxValue: Int,
): Boolean {
    if (!following) return false
    return newMaxValue > previousMaxValue
}

/** Legacy distance predicate. Provider intent/anchor/follow behavior is implemented by MessageScrollerState. */
internal fun shouldReleaseFollowing(
    value: Int,
    maxValue: Int,
    thresholdPx: Int,
): Boolean = !isMessageScrollerNearBottom(value, maxValue, thresholdPx)

/**
 * Transcript frame. Inside a Provider, compose Viewport/Content/Item and an explicit
 * Button; the viewport owns scrolling. The standalone legacy form keeps a scrolling
 * column and an automatic end button, and opts into auto-follow for compatibility.
 */
@Composable
fun MessageScroller(
    modifier: Modifier = Modifier,
    style: Style = Style,
    autoScrollThresholdPx: Int = DEFAULT_AUTO_SCROLL_THRESHOLD_PX,
    // Forwarded to the default [button]'s own `icon` slot -- lets a caller swap just the
    // glyph (e.g. a real Lucide ArrowDown) without reimplementing the button's
    // positioning/animation/chrome via a full [button] override. Ignored if [button] is
    // itself overridden, since that override owns its own icon.
    icon: @Composable (rotationDegrees: Float) -> Unit = DefaultMessageScrollerIcon,
    button: (@Composable BoxScope.(isVisible: Boolean, onClick: () -> Unit) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val inherited = LocalMessageScrollerState.current
    val density = LocalDensity.current
    val state =
        inherited ?: rememberMessageScrollerState(
            autoScroll = true,
            scrollEdgeThreshold = with(density) { autoScrollThresholdPx.toDp() },
        )
    val scope = rememberCoroutineScope()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val owner = remember { Any() }
    LaunchedEffect(state, owner) {
        state.attach(owner)
        try {
            coroutineScope {
                launch {
                    snapshotFlow { state.scrollState.value to state.scrollState.maxValue }.collect {
                        state.scrollChanged()
                        state.invalidate()
                    }
                }
                for (event in state.layouts) {
                    withFrameNanos { }
                    // User-input mutation cancels this correction, not the mounted controller.
                    launch { state.reconcileLayout() }.join()
                }
            }
        } finally {
            state.detach(owner)
        }
    }
    CompositionLocalProvider(
        LocalMessageScrollerState provides
            state,
        LocalLegacyMessageScroller provides
            (inherited == null),
    ) {
        Box(modifier = modifier.styleable(styleState, style).clipToBounds()) {
            val root = this
            if (inherited == null) {
                ScrollerViewport(Modifier.fillMaxSize(), state) { content(root) }
            } else {
                content()
            }
            val renderButton = button ?: if (inherited == null) defaultMessageScrollerButton(icon) else null
            renderButton?.invoke(
                this,
                state.scrollable.end,
                { scope.launch { state.scrollToEnd(MessageScrollerScrollBehavior.Smooth) } },
            )
        }
    }
}

private val LocalLegacyMessageScroller = staticCompositionLocalOf { false }

@Composable
private fun ScrollerViewport(
    modifier: Modifier,
    state: MessageScrollerState,
    content: @Composable ColumnScope.() ->
    Unit,
) {
    val owner = remember { Any() }
    DisposableEffect(state, owner) {
        state.viewportAttached(owner)
        onDispose { state.viewportDetached(owner) }
    }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val connection =
        remember(state) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (source == NestedScrollSource.UserInput && available.y != 0f) state.releaseFollowing()
                    return Offset.Zero
                }
            }
        }
    Column(
        modifier =
            modifier.onGloballyPositioned(state::viewportMeasured)
                .nestedScroll(connection)
                .pointerInput(state) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.type == PointerEventType.Scroll ||
                                event.changes.any { it.changedToDownIgnoreConsumed() }
                            ) {
                                state.releaseFollowing()
                            }
                        }
                    }
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) state.releaseFollowing()
                    false
                }
                .onKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) {
                        false
                    } else {
                        when (event.key) {
                            Key.DirectionUp, Key.DirectionDown, Key.PageUp, Key.PageDown, Key.MoveHome, Key.MoveEnd -> {
                                scope.launch {
                                    when (event.key) {
                                        Key.MoveHome -> state.scrollToStart()
                                        Key.MoveEnd -> state.scrollToEnd()
                                        else -> {
                                            val step =
                                                if (event.key == Key.PageUp ||
                                                    event.key == Key.PageDown
                                                ) {
                                                    state.scrollState.viewportSize
                                                } else {
                                                    with(density) { 40.dp.roundToPx() }
                                                }
                                            val delta =
                                                if (event.key == Key.DirectionUp ||
                                                    event.key == Key.PageUp
                                                ) {
                                                    -step
                                                } else {
                                                    step
                                                }
                                            state.scrollState.scrollTo(
                                                (state.scrollState.value + delta).coerceAtLeast(0),
                                            )
                                        }
                                    }
                                }
                                true
                            }
                            else -> false
                        }
                    }
                }
                .scrollFade(state.scrollState)
                .verticalScroll(state.scrollState)
                .graphicsLayer { alpha = if (state.pendingScroll) 0f else 1f }
                .focusable(),
    ) {
        content()
        Spacer(Modifier.layout { _, constraints -> layout(constraints.maxWidth, state.spacerHeight) { } })
    }
}

/** Source composition part: reads the provider and floats above the viewport. */
@Composable
fun BoxScope.MessageScrollerButton(
    modifier: Modifier = Modifier,
    direction: MessageScrollerDirection = MessageScrollerDirection.End,
    style: Style = Style,
    behavior: MessageScrollerScrollBehavior = MessageScrollerScrollBehavior.Smooth,
    icon: @Composable (rotationDegrees: Float) -> Unit = DefaultMessageScrollerIcon,
) {
    val state = messageScrollerState()
    val scope = rememberCoroutineScope()
    MessageScrollerButton(
        visible = if (direction == MessageScrollerDirection.End) state.scrollable.end else state.scrollable.start,
        onClick = {
            scope.launch {
                if (direction == MessageScrollerDirection.End) {
                    state.scrollToEnd(behavior)
                } else {
                    state.scrollToStart(behavior)
                }
            }
        },
        modifier =
            modifier.align(
                if (direction == MessageScrollerDirection.End) {
                    Alignment.BottomCenter
                } else {
                    Alignment.TopCenter
                },
            )
                .padding(vertical = 16.dp),
        direction = direction,
        style = style,
        icon = icon,
    )
}

object MessageScrollerDefaults {
    /** Nova content gap-6; the official Rhea page uses gap-8 (32dp). */
    val ContentSpacing = 24.dp

    /** Official Viewport `aria-label`. */
    val ViewportLabel = "Messages"

    /** Official Viewport default; stable row ids preserve the reading position. */
    val PreserveScrollOnPrepend = true
}

/** Headless state owner, matching upstream defaults. Legacy bare Root still opts into following. */
@Composable
fun MessageScrollerProvider(
    autoScroll: Boolean = false,
    defaultScrollPosition: MessageScrollerPosition = MessageScrollerPosition.End,
    scrollEdgeThreshold: Dp = 8.dp,
    scrollMargin: Dp = 0.dp,
    scrollPreviousItemPeek: Dp = 64.dp,
    content: @Composable () -> Unit,
) {
    val state =
        rememberMessageScrollerState(
            autoScroll,
            defaultScrollPosition,
            scrollEdgeThreshold,
            scrollMargin,
            scrollPreviousItemPeek,
        )
    MessageScrollerProvider(state, content)
}

/** Hoisted state variant; configure the state at its rememberMessageScrollerState call. */
@Composable
fun MessageScrollerProvider(
    state: MessageScrollerState,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalMessageScrollerState provides state, content = content)
}

/**
 * The canonical scroll viewport, with native input and prepend preservation policy.
 */
@Composable
fun MessageScrollerViewport(
    modifier: Modifier = Modifier,
    preserveScrollOnPrepend: Boolean = true,
    contentDescription: String = MessageScrollerDefaults.ViewportLabel,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val state = messageScrollerState()
    SideEffect { state.preserveOnPrepend = preserveScrollOnPrepend }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val labeled =
        if (contentDescription.isNotEmpty()) {
            modifier.fillMaxWidth().semantics { this.contentDescription = contentDescription }
        } else {
            modifier.fillMaxWidth()
        }
    if (LocalLegacyMessageScroller.current) {
        Column(modifier = labeled.styleable(styleState, style), content = content)
    } else {
        ScrollerViewport(labeled.styleable(styleState, style).fillMaxSize(), state, content)
    }
}

/**
 * Transcript content (Nova gap-6, with explicit spacing override). [spacing] defaults to
 * [MessageScrollerDefaults.ContentSpacing].
 */
@Composable
fun MessageScrollerContent(
    modifier: Modifier = Modifier,
    spacing: Dp = MessageScrollerDefaults.ContentSpacing,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val state = messageScrollerState()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier =
            Modifier.onGloballyPositioned(state::contentMeasured)
                .then(modifier).fillMaxWidth().styleable(styleState, style)
                .semantics { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

/** Stable transcript row boundary, used by anchors, message commands and visibility. */
@Composable
fun MessageScrollerItem(
    modifier: Modifier = Modifier,
    scrollAnchor: Boolean = false,
    messageId: String? = null,
    content: @Composable () -> Unit,
) {
    val state = messageScrollerState()
    val token = remember { Any() }
    DisposableEffect(state, token) {
        state.itemAttached()
        onDispose { state.removeItem(token) }
    }
    Box(
        modifier =
            Modifier.onGloballyPositioned {
                state.itemMeasured(token, messageId, scrollAnchor, it)
            }.then(modifier).fillMaxWidth(),
    ) { content() }
}

/**
 * A floating "scroll to bottom"/"scroll to top" affordance. Normally rendered internally
 * by [MessageScroller] for [MessageScrollerDirection.End]; exposed separately
 * in case a caller wants a custom trigger, position, or a second [Start]-direction button
 * of their own (real shadcn's own `MessageScrollerButton` is likewise caller-composed,
 * not auto-rendered by `MessageScroller`, for exactly this reason).
 *
 * Animation matches real shadcn's `message-scroller.tsx` exactly, transcribed from its
 * source CSS (`data-[active=true]:...`/`data-[active=false]:...` on the real component):
 * showing slides+scales+fades in over 200ms with an ease-out curve
 * (`cubic-bezier(0.23,1,0.32,1)`); hiding is twice as slow (400ms) with an accelerating
 * ease-in curve (`cubic-bezier(0.7,0,0.84,0)`) -- not a symmetric fade.
 */
@Composable
fun MessageScrollerButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    direction: MessageScrollerDirection = MessageScrollerDirection.End,
    // See [DefaultMessageScrollerIcon]. Receives the resolved rotation so an override can
    // apply it too (e.g. this repo's own demo app passes a real Lucide
    // ArrowDown here -- see MessageScrollerDoc.kt).
    icon: @Composable (rotationDegrees: Float) -> Unit = DefaultMessageScrollerIcon,
) {
    val showEasing = remember { CubicBezierEasing(0.23f, 1f, 0.32f, 1f) }
    val hideEasing = remember { CubicBezierEasing(0.7f, 0f, 0.84f, 0f) }
    // End slides up from below (offscreen below the viewport); Start slides down from above.
    val offscreenSign = if (direction == MessageScrollerDirection.End) 1 else -1

    AnimatedVisibility(
        visible = visible,
        enter =
            slideInVertically(tween(200, easing = showEasing)) { fullHeight -> offscreenSign * fullHeight } +
                scaleIn(tween(200, easing = showEasing), initialScale = 0.95f) +
                fadeIn(tween(200, easing = showEasing)),
        exit =
            slideOutVertically(tween(400, easing = hideEasing)) { fullHeight -> offscreenSign * fullHeight } +
                scaleOut(tween(400, easing = hideEasing), targetScale = 0.95f) +
                fadeOut(tween(400, easing = hideEasing)),
        modifier = modifier,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        val theme = currentTheme
        val styleState = rememberUpdatedStyleState(interactionSource)
        val pressStyle = rememberPressMotionStyle()
        val nova = theme.activePreset == StylePreset.Nova
        val touchTargetStyle = if (nova) Style else rememberMinTouchTargetStyle()
        val buttonStyle =
            rememberTheme(isHovered) {
                Style {
                    background(if (isHovered) colors.muted else colors.background)
                    borderWidth(1.dp)
                    borderColor(colors.border)
                    focusRing(RoundedCornerShape(radius.md))
                }
            }
        Box(
            modifier =
                Modifier
                    .semantics {
                        role = Role.Button
                        if (!visible) {
                            disabled()
                            hideFromAccessibility()
                        }
                        contentDescription =
                            if (direction == MessageScrollerDirection.Start) {
                                "Scroll to start"
                            } else {
                                "Scroll to end"
                            }
                    }
                    .focusProperties { canFocus = visible }
                    .hoverable(interactionSource, enabled = visible)
                    .keyboardInputMode()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        enabled = visible,
                        onClick = onClick,
                    )
                    .styleable(styleState, touchTargetStyle then pressStyle),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(if (nova) 28.dp else 36.dp)
                        .styleable(styleState, buttonStyle then style),
                contentAlignment = Alignment.Center,
            ) {
                val rotation = if (direction == MessageScrollerDirection.Start) 180f else 0f
                icon(rotation)
            }
        }
    }
}
