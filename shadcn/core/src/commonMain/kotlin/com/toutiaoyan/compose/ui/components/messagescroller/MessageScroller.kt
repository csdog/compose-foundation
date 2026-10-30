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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.styles.scrollFade
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.launch
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.tailwind.core.twRadius

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
    Icon(
        ArrowDown,
        tint = currentTheme.colors.foreground,
        modifier = Modifier.graphicsLayer(rotationZ = rotationDegrees),
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

/**
 * Whether a user-driven scroll (not one this component just commanded itself via
 * `animateScrollTo`) should release the "follow new messages" state. Matches real
 * shadcn's documented semantics: "Scrolling away from the live edge releases the view,
 * whether by wheel, touch, keyboard scroll keys, or dragging the scrollbar" -- and,
 * critically, release is **sticky**: it does not silently re-engage just because the
 * reader happens to scroll back within [thresholdPx] of the bottom. Re-engaging requires
 * an explicit action ([MessageScrollerButton] or `scrollToEnd()`), which is why this
 * function only ever returns `true` (a one-way transition) and callers should not use its
 * negation to resume following.
 */
internal fun shouldReleaseFollowing(
    value: Int,
    maxValue: Int,
    thresholdPx: Int,
): Boolean = !isMessageScrollerNearBottom(value, maxValue, thresholdPx)

/**
 * A chat-transcript scroll container that follows new messages to the bottom -- but only
 * while the reader hasn't scrolled away to read history -- with a floating "jump to
 * bottom" button when they have. Part of shadcn's "AI Elements" family, matching real
 * shadcn/ui's `message-scroller.tsx` (a wrapper over the separate `@shadcn/react/
 * message-scroller` npm package there; reimplemented directly here on Compose's own
 * [ScrollState][androidx.compose.foundation.ScrollState], since no equivalent package
 * exists for Compose Multiplatform).
 *
 * The auto-scroll/button-visibility *decisions* ([shouldAutoScrollToBottom],
 * [isMessageScrollerNearBottom], [shouldReleaseFollowing]) are all plain functions with
 * no Compose dependency, unit tested directly (`MessageScrollerTest`) -- this
 * component has no drag gesture to avoid simulating live, but keeps the same "extract the
 * decision logic" discipline as the components that do.
 *
 * Usage:
 * ```
 * MessageScroller(modifier = Modifier.fillMaxSize()) {
 *     messages.forEach { Message(...) { ... } }
 * }
 * MessageScrollerProvider {
 *     MessageScroller {
 *         MessageScrollerViewport(contentDescription = MessageScrollerDefaults.ViewportLabel) {
 *             MessageScrollerContent { … }
 *         }
 *     }
 * }
 * ```
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
    button: @Composable BoxScope.(isVisible: Boolean, onClick: () -> Unit) -> Unit = defaultMessageScrollerButton(icon),
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var previousMaxValue by remember { mutableIntStateOf(0) }
    // Sticky: only ever set back to true by an explicit jump-to-bottom, never by the
    // reader passively scrolling back within range -- see shouldReleaseFollowing's doc.
    var isFollowing by remember { mutableStateOf(true) }
    // Distinguishes our own animateScrollTo from a real user gesture, so the release
    // check below doesn't mistake the programmatic scroll it just triggered for the
    // reader manually scrolling away.
    var isProgrammaticScroll by remember { mutableStateOf(false) }

    val styleState = remember { MutableStyleState(MutableInteractionSource()) }

    fun jumpToEnd() {
        coroutineScope.launch {
            isProgrammaticScroll = true
            isFollowing = true
            scrollState.animateScrollTo(scrollState.maxValue)
            isProgrammaticScroll = false
        }
    }

    LaunchedEffect(scrollState.maxValue) {
        val newMaxValue = scrollState.maxValue
        if (shouldAutoScrollToBottom(isFollowing, previousMaxValue, newMaxValue)) {
            isProgrammaticScroll = true
            scrollState.animateScrollTo(newMaxValue)
            isProgrammaticScroll = false
        }
        previousMaxValue = newMaxValue
    }

    LaunchedEffect(scrollState.value) {
        val released =
            !isProgrammaticScroll &&
                shouldReleaseFollowing(scrollState.value, scrollState.maxValue, autoScrollThresholdPx)
        if (released) {
            isFollowing = false
        }
    }

    Box(modifier = modifier.styleable(styleState, style)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).scrollFade(scrollState),
            content = content,
        )
        val nearBottom = isMessageScrollerNearBottom(scrollState.value, scrollState.maxValue, autoScrollThresholdPx)
        button(this, scrollState.maxValue > 0 && !nearBottom, ::jumpToEnd)
    }
}

object MessageScrollerDefaults {
    /** Official MessageScrollerContent `gap-8`. */
    val ContentSpacing = 32.dp

    /** Official Viewport `aria-label`. */
    val ViewportLabel = "Messages"

    /** Official Viewport `preserveScrollOnPrepend`. Prepend-stability is not ported. */
    val PreserveScrollOnPrepend = true
}

/**
 * Official `MessageScrollerProvider`. Identity wrapper so catalog trees match the docs
 * (`Provider > Root > Viewport > Content > Item`). Scroll still lives on [MessageScroller].
 */
@Composable
fun MessageScrollerProvider(content: @Composable () -> Unit) {
    content()
}

/**
 * Official `MessageScrollerViewport`. Nested inside [MessageScroller]'s scrolling
 * column (Root still owns scroll). [contentDescription] defaults to
 * [MessageScrollerDefaults.ViewportLabel]. [preserveScrollOnPrepend] is accepted, not ported.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun ColumnScope.MessageScrollerViewport(
    modifier: Modifier = Modifier,
    preserveScrollOnPrepend: Boolean = true,
    contentDescription: String = MessageScrollerDefaults.ViewportLabel,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val labeled =
        if (contentDescription.isNotEmpty()) {
            modifier.fillMaxWidth().semantics { this.contentDescription = contentDescription }
        } else {
            modifier.fillMaxWidth()
        }
    Column(
        modifier = labeled.styleable(styleState, style),
        content = content,
    )
}

/**
 * Official `MessageScrollerContent` (`gap-8`). [spacing] defaults to
 * [MessageScrollerDefaults.ContentSpacing].
 */
@Composable
fun ColumnScope.MessageScrollerContent(
    modifier: Modifier = Modifier,
    spacing: Dp = MessageScrollerDefaults.ContentSpacing,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

/**
 * Official `MessageScrollerItem`. [scrollAnchor] is accepted to match the docs prop;
 * turn anchoring is not ported yet (default `false` is a no-op).
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun ColumnScope.MessageScrollerItem(
    modifier: Modifier = Modifier,
    scrollAnchor: Boolean = false,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) { content() }
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
        val styleState = remember { MutableStyleState(interactionSource) }
        val pressStyle = rememberPressMotionStyle()
        val touchTargetStyle = rememberMinTouchTargetStyle()
        val buttonStyle =
            remember(isHovered, theme) {
                Style {
                    background(if (isHovered) theme.colors.muted else theme.colors.background)
                    borderWidth(1.dp)
                    borderColor(theme.colors.border)
                    focusRing(RoundedCornerShape(twRadius.md))
                }
            }
        Box(
            modifier =
                Modifier
                    .semantics {
                        role = Role.Button
                        contentDescription =
                            if (direction == MessageScrollerDirection.Start) {
                                "Scroll to start"
                            } else {
                                "Scroll to end"
                            }
                    }
                    .hoverable(interactionSource)
                    .keyboardInputMode()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                    .styleable(styleState, touchTargetStyle then pressStyle),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .styleable(styleState, buttonStyle then style),
                contentAlignment = Alignment.Center,
            ) {
                val rotation = if (direction == MessageScrollerDirection.Start) 180f else 0f
                icon(rotation)
            }
        }
    }
}
