package com.toutiaoyan.compose.ui.components.messagescroller

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.channels.Channel
import kotlin.math.roundToInt

/** Initial opening is applied once, on the first measured non-empty transcript. */
enum class MessageScrollerPosition { Start, End, LastAnchor }

enum class MessageScrollerScrollAlign { Start, Center, End, Nearest }

enum class MessageScrollerScrollBehavior { Auto, Smooth }

data class MessageScrollerScrollable(val start: Boolean, val end: Boolean)

data class MessageScrollerVisibility(val currentAnchorId: String?, val visibleMessageIds: List<String>)

internal data class MessageScrollerOptions(
    val autoScroll: Boolean,
    val defaultPosition: MessageScrollerPosition,
    val edgeThreshold: Int,
    val margin: Int,
    val previousPeek: Int,
    val density: Float,
)

internal data class MessageScrollerRow(
    val token: Any,
    val messageId: String?,
    val anchor: Boolean,
    val top: Int,
    val height: Int,
) {
    val bottom: Int get() = top + height
    val identity: Any get() = messageId ?: token
}

internal fun messageScrollerTarget(
    top: Int,
    height: Int,
    viewport: Int,
    position: Int,
    margin: Int,
    align: MessageScrollerScrollAlign,
    paddingStart: Int = 0,
    paddingEnd: Int = 0,
): Int =
    when (align) {
        MessageScrollerScrollAlign.Start -> top - paddingStart - margin
        MessageScrollerScrollAlign.Center ->
            top - paddingStart - ((viewport - paddingStart - paddingEnd).coerceAtLeast(0) - height) / 2 - margin
        MessageScrollerScrollAlign.End -> top + height - viewport + paddingEnd + margin
        MessageScrollerScrollAlign.Nearest ->
            when {
                top >= position + paddingStart && top + height <= position + viewport - paddingEnd -> position
                top < position + paddingStart -> top - paddingStart - margin
                else -> top + height - viewport + paddingEnd + margin
            }
    }.coerceAtLeast(0)

/**
 * Headless transcript state. Commands are suspending: the caller owns their scope,
 * cancellation and failure. No coroutine is launched by constructing this object.
 */
@Stable
class MessageScrollerState internal constructor(
    val scrollState: ScrollState,
    options: MessageScrollerOptions,
) {
    internal var options by mutableStateOf(options)
        private set
    internal val layouts = Channel<Unit>(Channel.CONFLATED)
    private val measuredRows = linkedMapOf<Any, MessageScrollerRow>()
    private var rows by mutableStateOf<List<MessageScrollerRow>>(emptyList())
    private var viewportHeight by mutableIntStateOf(0)
    private var contentBottom by mutableIntStateOf(0)
    private var contentBottomMeasured = 0
    private var contentTopMeasured = 0
    private var paddingStart = 0
    private var paddingEnd = 0
    private var viewportOrigin = Offset.Zero
    private var measuredViewportHeight = 0
    private var rootOwner: Any? = null
    private var viewportOwner: Any? = null
    private var viewportPresent by mutableStateOf(false)
    private val viewportReady: Boolean get() = mounted && viewportPresent && viewportHeight > 0
    private var initialized = false
    private var previousBottom = 0
    private var lastScrollValue = 0
    private var commands by mutableIntStateOf(0)
    private var activeCommand: Job? = null
    private var commandRevision = 0L
    private var anchoredToken: Any? = null
    private val handledAnchors = mutableSetOf<Any>()
    internal var spacerHeight by mutableIntStateOf(0)
        private set
    internal var mounted by mutableStateOf(false)
        private set
    internal var preserveOnPrepend = true
    var pendingScroll by mutableStateOf(options.defaultPosition != MessageScrollerPosition.Start)
        private set
    var isFollowing by mutableStateOf(false)
        private set
    val isAutoscrolling: Boolean get() = commands > 0

    val scrollable: MessageScrollerScrollable by derivedStateOf {
        MessageScrollerScrollable(
            start = viewportReady && scrollState.value > this.options.edgeThreshold,
            end = viewportReady && contentBottom - scrollState.value - viewportHeight > this.options.edgeThreshold,
        )
    }
    val visibility: MessageScrollerVisibility by derivedStateOf {
        if (!viewportReady) return@derivedStateOf MessageScrollerVisibility(null, emptyList())
        val line = scrollState.value + this.options.margin + this.options.previousPeek
        MessageScrollerVisibility(
            rows.lastOrNull { !it.messageId.isNullOrEmpty() && it.anchor && it.top <= line + 1 }?.messageId,
            rows.filter {
                it.bottom > line &&
                    it.top < scrollState.value +
                    viewportHeight
            }.mapNotNull { it.messageId?.takeIf(String::isNotEmpty) },
        )
    }

    internal fun configure(value: MessageScrollerOptions) {
        if (options == value) return
        val enable = value.autoScroll && !options.autoScroll
        options = value
        if (!value.autoScroll) {
            isFollowing = false
        } else if (enable && mounted && !scrollable.end) {
            isFollowing = true
        }
        invalidate()
    }

    internal fun attach(owner: Any) {
        rootOwner = owner
        mounted = true
        initialized = false
        pendingScroll = options.defaultPosition != MessageScrollerPosition.Start
        isFollowing = false
        anchoredToken = null
        spacerHeight = 0
        previousBottom = 0
        handledAnchors.clear()
        invalidate()
    }

    internal fun detach(owner: Any) {
        if (rootOwner != owner) return
        releaseFollowing()
        rootOwner = null
        mounted = false
        measuredRows.clear()
        rows = emptyList()
        contentBottomMeasured = 0
    }

    internal fun viewportAttached(owner: Any) {
        viewportOwner = owner
        viewportPresent = true
        invalidate()
    }

    internal fun viewportDetached(owner: Any) {
        if (viewportOwner != owner) return
        viewportOwner = null
        viewportPresent = false
        measuredViewportHeight = 0
        contentBottomMeasured = 0
        invalidate()
    }

    internal fun itemAttached() {
        if (!initialized && options.defaultPosition != MessageScrollerPosition.Start) pendingScroll = true
        invalidate()
    }

    internal fun invalidate() {
        layouts.trySend(Unit)
    }

    internal fun viewportMeasured(coordinates: LayoutCoordinates) {
        val origin = coordinates.positionInWindow()
        val height = coordinates.size.height
        if (origin != viewportOrigin || height != measuredViewportHeight) {
            viewportOrigin = origin
            measuredViewportHeight = height
            invalidate()
        }
    }

    internal fun contentMeasured(coordinates: LayoutCoordinates) {
        val top = (coordinates.positionInWindow().y - viewportOrigin.y + scrollState.value).roundToInt()
        val bottom = top + coordinates.size.height
        if (bottom != contentBottomMeasured || top != contentTopMeasured) {
            contentTopMeasured = top
            contentBottomMeasured = bottom
            invalidate()
        }
    }

    internal fun itemMeasured(
        token: Any,
        id: String?,
        anchor: Boolean,
        coordinates: LayoutCoordinates,
    ) {
        val top = (coordinates.positionInWindow().y - viewportOrigin.y + scrollState.value).roundToInt()
        val row = MessageScrollerRow(token, id, anchor, top, coordinates.size.height)
        if (measuredRows[token] != row) {
            measuredRows[token] = row
            invalidate()
        }
    }

    internal fun removeItem(token: Any) {
        measuredRows.remove(token)
        invalidate()
    }

    /** Called by native user-input hooks, never inferred from content growth. */
    fun releaseFollowing() {
        commandRevision++
        val command = activeCommand
        activeCommand = null
        command?.cancel()
        isFollowing = false
        anchoredToken = null
    }

    internal fun scrollChanged() {
        val value = scrollState.value
        if (value != lastScrollValue && commands == 0 && initialized) {
            if (value < lastScrollValue && scrollable.end) releaseFollowing()
            if (options.autoScroll && !scrollable.end && anchoredToken == null && spacerHeight == 0) isFollowing = true
        }
        lastScrollValue = value
    }

    /** A command owns only its nested job, never the caller's whole coroutine. */
    private suspend fun performScroll(block: suspend () -> Boolean): Boolean =
        coroutineScope {
            val revision = ++commandRevision
            val previous = activeCommand
            activeCommand = currentCoroutineContext()[Job]
            previous?.cancel()
            commands++
            try {
                block()
            } catch (cancelled: CancellationException) {
                if (commandRevision == revision) {
                    isFollowing = false
                    anchoredToken = null
                }
                throw cancelled
            } finally {
                commands--
                if (commandRevision == revision) {
                    activeCommand = null
                    lastScrollValue = scrollState.value
                }
            }
        }

    private suspend fun scroll(
        target: Int,
        behavior: MessageScrollerScrollBehavior,
    ): Boolean {
        if (!viewportReady) return false
        if (behavior == MessageScrollerScrollBehavior.Smooth) {
            scrollState.animateScrollTo(target)
        } else {
            scrollState.scrollTo(target)
        }
        lastScrollValue = scrollState.value
        return true
    }

    private suspend fun move(
        target: Int,
        behavior: MessageScrollerScrollBehavior,
    ): Boolean = performScroll { scroll(target, behavior) }

    suspend fun scrollToStart(behavior: MessageScrollerScrollBehavior = MessageScrollerScrollBehavior.Auto): Boolean {
        if (!viewportReady) return false
        releaseFollowing()
        spacerHeight = 0
        return move(0, behavior)
    }

    suspend fun scrollToEnd(behavior: MessageScrollerScrollBehavior = MessageScrollerScrollBehavior.Auto): Boolean {
        if (!viewportReady) return false
        return performScroll {
            anchoredToken = null
            spacerHeight = 0
            isFollowing = options.autoScroll
            scroll((contentBottom - viewportHeight).coerceAtLeast(0), behavior)
        }
    }

    suspend fun scrollToMessage(
        messageId: String,
        align: MessageScrollerScrollAlign = MessageScrollerScrollAlign.Start,
        behavior: MessageScrollerScrollBehavior = MessageScrollerScrollBehavior.Auto,
        scrollMargin: Dp? = null,
    ): Boolean {
        if (!viewportReady) return false
        val row = rows.firstOrNull { it.messageId == messageId } ?: return false
        releaseFollowing()
        return targetRow(
            row,
            align,
            behavior,
            scrollMargin?.let { (it.value * options.density).roundToInt() }
                ?: options.margin,
            anchor =
            false,
        )
    }

    private suspend fun targetRow(
        row: MessageScrollerRow,
        align: MessageScrollerScrollAlign,
        behavior: MessageScrollerScrollBehavior,
        margin: Int,
        anchor: Boolean,
    ): Boolean =
        performScroll {
            val target =
                messageScrollerTarget(
                    row.top,
                    row.height,
                    viewportHeight,
                    scrollState.value,
                    margin,
                    align,
                    paddingStart,
                    paddingEnd,
                )
            spacerHeight = (target + viewportHeight - contentBottom).coerceAtLeast(0)
            anchoredToken = if (anchor) row.identity else null
            if (anchor) handledAnchors.add(row.identity)
            isFollowing = false
            if (target > scrollState.maxValue) withFrameNanos { }
            scroll(target, behavior)
        }

    /** Coalesces layout notifications after the frame; measuring only updates plain caches. */
    internal suspend fun reconcileLayout() {
        if (!mounted || !viewportPresent || scrollState.maxValue == Int.MAX_VALUE) return
        val previous = rows
        val next = measuredRows.values.sortedBy { it.top }
        val previousReading = previous.firstOrNull { it.bottom > scrollState.value }
        val oldBottom = previousBottom
        val oldViewportHeight = viewportHeight
        val wasAtEnd = viewportReady && contentBottom - scrollState.value - viewportHeight <= options.edgeThreshold
        rows = next
        viewportHeight = measuredViewportHeight
        contentBottom =
            if (contentBottomMeasured > 0) {
                contentBottomMeasured
            } else {
                (scrollState.maxValue + viewportHeight - spacerHeight).coerceAtLeast(0)
            }
        paddingStart = next.firstOrNull()?.let { (it.top - contentTopMeasured).coerceAtLeast(0) } ?: 0
        paddingEnd = next.lastOrNull()?.let { (contentBottom - it.bottom).coerceAtLeast(0) } ?: 0
        previousBottom = contentBottom
        if (viewportHeight == 0) return
        if (!initialized && (next.isNotEmpty() || contentBottom > viewportHeight)) {
            initialized = true
            handledAnchors.addAll(next.filter { it.anchor }.map { it.identity })
            try {
                when (options.defaultPosition) {
                    MessageScrollerPosition.Start -> move(0, MessageScrollerScrollBehavior.Auto)
                    MessageScrollerPosition.End -> scrollToEnd()
                    MessageScrollerPosition.LastAnchor -> {
                        val anchor = next.lastOrNull { it.anchor }
                        if (anchor == null || contentBottom - anchor.top <= viewportHeight) {
                            scrollToEnd()
                        } else {
                            targetRow(
                                anchor,
                                MessageScrollerScrollAlign.Start,
                                MessageScrollerScrollBehavior.Auto,
                                options.margin + options.previousPeek,
                                anchor = true,
                            )
                        }
                    }
                }
            } finally {
                // Interrupted positioning must reveal the reader's current position too.
                pendingScroll = false
            }
            return
        }
        if (next.isEmpty() && contentBottom <= viewportHeight) pendingScroll = false
        if (!initialized) return
        // A footer/keyboard can shrink the viewport without changing transcript content.
        // Keep an existing end position, without enabling follow-output for future appends.
        // Otherwise a wrapping status footer can alternate its own height indefinitely.
        if (viewportHeight < oldViewportHeight && wasAtEnd && contentBottom == oldBottom &&
            anchoredToken == null && spacerHeight == 0
        ) {
            move((contentBottom - viewportHeight).coerceAtLeast(0), MessageScrollerScrollBehavior.Auto)
            return
        }
        val oldFirstIndex =
            previous.firstOrNull()?.let { first ->
                next.indexOfFirst { it.identity == first.identity }
            }
                ?: -1
        if (preserveOnPrepend && previousReading != null &&
            (oldFirstIndex > 0 || (!isFollowing && anchoredToken == null))
        ) {
            val moved = next.firstOrNull { it.identity == previousReading.identity }
            if (moved != null && moved.top != previousReading.top) {
                move(
                    (
                        scrollState.value +
                            moved.top - previousReading.top
                    ).coerceAtLeast(0),
                    MessageScrollerScrollBehavior.Auto,
                )
                return
            }
        }
        handledAnchors.retainAll(next.map { it.identity }.toSet())
        val additions = next.filter { it.anchor && it.identity !in handledAnchors }
        if (previous.isNotEmpty() && additions.isNotEmpty()) {
            if (isFollowing && options.autoScroll && additions.size > 1) {
                scrollToEnd()
            } else {
                targetRow(
                    additions.first(),
                    MessageScrollerScrollAlign.Start,
                    MessageScrollerScrollBehavior.Auto,
                    options.margin + options.previousPeek,
                    anchor = true,
                )
            }
            return
        }
        val anchor = next.firstOrNull { it.identity == anchoredToken }
        if (anchor != null) {
            val previousSpacer = spacerHeight
            targetRow(
                anchor,
                MessageScrollerScrollAlign.Start,
                MessageScrollerScrollBehavior.Auto,
                options.margin + options.previousPeek,
                anchor = true,
            )
            if (options.autoScroll && previousSpacer > 0 && spacerHeight == 0) scrollToEnd()
            return
        }
        if (isFollowing && options.autoScroll && contentBottom != oldBottom) scrollToEnd()
    }
}

@Composable
fun rememberMessageScrollerState(
    autoScroll: Boolean = false,
    defaultScrollPosition: MessageScrollerPosition = MessageScrollerPosition.End,
    scrollEdgeThreshold: Dp = 8.dp,
    scrollMargin: Dp = 0.dp,
    scrollPreviousItemPeek: Dp = 64.dp,
): MessageScrollerState {
    val scroll = rememberScrollState()
    val density = LocalDensity.current
    val options =
        with(density) {
            MessageScrollerOptions(
                autoScroll,
                defaultScrollPosition,
                scrollEdgeThreshold.roundToPx().coerceAtLeast(0),
                scrollMargin.roundToPx(),
                scrollPreviousItemPeek.roundToPx(),
                density.density,
            )
        }
    val state = remember { MessageScrollerState(scroll, options) }
    SideEffect { state.configure(options) }
    return state
}

internal val LocalMessageScrollerState = staticCompositionLocalOf<MessageScrollerState?> { null }

@Composable
fun messageScrollerState(): MessageScrollerState =
    checkNotNull(LocalMessageScrollerState.current) { "MessageScrollerProvider is required" }

@Composable
fun messageScrollerScrollable(): MessageScrollerScrollable = messageScrollerState().scrollable

@Composable
fun messageScrollerVisibility(): MessageScrollerVisibility = messageScrollerState().visibility
