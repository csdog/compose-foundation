@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.button.ButtonSurface
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.tailwind.core.TwColors
import kotlinx.coroutines.delay
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

enum class ToastVariant { Default, Success, Error, Warning, Info, Loading }

enum class ToastPriority { Low, High }

/** Kotlin viewport placement extension. Default is bottom-end. */
enum class ToastPosition {
    TopStart,
    TopCenter,
    TopEnd,
    BottomStart,
    BottomCenter,
    BottomEnd,
}

/** Kotlin theme override. [System] follows the current Compose theme. */
enum class ToastTheme { System, Light, Dark }

data class ToastMessage(
    val id: Long,
    val title: String,
    val description: String? = null,
    val variant: ToastVariant = ToastVariant.Default,
    val durationMillis: Long = 5000L,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val cancelLabel: String? = null,
    val onCancel: (() -> Unit)? = null,
    val revision: Int = 0,
    val priority: ToastPriority = ToastPriority.Low,
)

/**
 * Holds the queue of currently-visible toasts. The current shadcn Base UI Toast uses a
 * manager for updates and async feedback. This Kotlin manager preserves the existing
 * show/dismiss API and offers update/loading/promise operations; the caller owns async work.
 *
 * Usage:
 * ```
 * val toastState = rememberToastState()
 * Button("Save", onClick = { toastState.success("Saved") })
 * Toaster(state = toastState)
 * ```
 */
class ToastState {
    private val _toasts = mutableStateListOf<ToastMessage>()
    val toasts: List<ToastMessage> get() = _toasts
    private var nextId = 0L
    var durationMillis: Long = 5000L

    fun show(
        title: String = "",
        description: String? = null,
        variant: ToastVariant = ToastVariant.Default,
        durationMillis: Long? = null,
        duration: Long? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        cancelLabel: String? = null,
        onCancel: (() -> Unit)? = null,
        priority: ToastPriority = ToastPriority.Low,
    ): Long {
        val id = nextId++
        val resolvedDuration = duration ?: durationMillis ?: this.durationMillis
        _toasts.add(
            ToastMessage(
                id,
                title,
                description,
                variant,
                resolvedDuration,
                actionLabel,
                onAction,
                cancelLabel,
                onCancel,
                priority = priority,
            ),
        )
        return id
    }

    fun success(
        title: String,
        description: String? = null,
        durationMillis: Long? = null,
        duration: Long? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        cancelLabel: String? = null,
        onCancel: (() -> Unit)? = null,
    ): Long =
        show(
            title = title,
            description = description,
            variant = ToastVariant.Success,
            durationMillis = durationMillis,
            duration = duration,
            actionLabel = actionLabel,
            onAction = onAction,
            cancelLabel = cancelLabel,
            onCancel = onCancel,
        )

    fun error(
        title: String,
        description: String? = null,
        durationMillis: Long? = null,
        duration: Long? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        cancelLabel: String? = null,
        onCancel: (() -> Unit)? = null,
    ): Long =
        show(
            title = title,
            description = description,
            variant = ToastVariant.Error,
            durationMillis = durationMillis,
            duration = duration,
            actionLabel = actionLabel,
            onAction = onAction,
            cancelLabel = cancelLabel,
            onCancel = onCancel,
        )

    fun warning(
        title: String,
        description: String? = null,
        durationMillis: Long? = null,
        duration: Long? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        cancelLabel: String? = null,
        onCancel: (() -> Unit)? = null,
    ): Long =
        show(
            title = title,
            description = description,
            variant = ToastVariant.Warning,
            durationMillis = durationMillis,
            duration = duration,
            actionLabel = actionLabel,
            onAction = onAction,
            cancelLabel = cancelLabel,
            onCancel = onCancel,
        )

    fun info(
        title: String,
        description: String? = null,
        durationMillis: Long? = null,
        duration: Long? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        cancelLabel: String? = null,
        onCancel: (() -> Unit)? = null,
    ): Long =
        show(
            title = title,
            description = description,
            variant = ToastVariant.Info,
            durationMillis = durationMillis,
            duration = duration,
            actionLabel = actionLabel,
            onAction = onAction,
            cancelLabel = cancelLabel,
            onCancel = onCancel,
        )

    /**
     * Updates an existing message while preserving its ID.
     * Returns false if the message is missing or [transform] dismisses or updates it.
     */
    fun update(
        id: Long,
        transform: (ToastMessage) -> ToastMessage,
    ): Boolean {
        val previous = _toasts.firstOrNull { it.id == id } ?: return false
        val updated = transform(previous).copy(id = id, revision = previous.revision + 1)
        val index = _toasts.indexOfFirst { it === previous }
        if (index < 0) return false
        _toasts[index] = updated
        return true
    }

    fun loading(
        title: String,
        description: String? = null,
    ): Long = show(title, description, variant = ToastVariant.Loading, duration = 0)

    /** Caller owns the coroutine. Cancellation removes pending feedback and is rethrown. */
    suspend fun <T> promise(
        loading: String,
        success: (T) -> String,
        error: (Throwable) -> String,
        block: suspend () -> T,
    ): Result<T> {
        val id = this.loading(loading)
        return try {
            val result = block()
            update(id) {
                it.copy(
                    title =
                        success(result),
                    variant =
                        ToastVariant.Success,
                    durationMillis =
                    durationMillis,
                )
            }
            Result.success(result)
        } catch (cancel: kotlinx.coroutines.CancellationException) {
            dismiss(id)
            throw cancel
        } catch (failure: Exception) {
            update(id) {
                it.copy(
                    title =
                        error(failure),
                    variant =
                        ToastVariant.Error,
                    durationMillis =
                    durationMillis,
                )
            }
            Result.failure(failure)
        }
    }

    /** Base UI naming alias; dismissal still uses the existing Kotlin immediate-removal policy. */
    fun close(id: Long? = null) = dismiss(id)

    /** Immediately remove one toast by [id], or every toast when [id] is omitted. */
    fun dismiss(id: Long? = null) {
        if (id == null) {
            _toasts.clear()
        } else {
            _toasts.removeAll { it.id == id }
        }
    }
}

@Composable
fun rememberToastState(): ToastState = remember { ToastState() }

/** Alias of [rememberToastState]. Matches the `*State()` naming of overlay hooks. */
@Composable
fun toastState(): ToastState = rememberToastState()

private val DefaultToastEnter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
private val DefaultToastExit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })

/** Renders [state]'s current toast queue. Place once, near the root of your app. */
@Composable
fun Toaster(
    state: ToastState,
    modifier: Modifier = Modifier,
    style: Style = Style,
    cardStyle: Style = Style,
    position: ToastPosition = ToastPosition.BottomEnd,
    closeButton: Boolean = true,
    durationMillis: Long = 5000L,
    duration: Long? = null,
    visibleToasts: Int = 3,
    richColors: Boolean = false,
    offset: Dp = 0.dp,
    gap: Dp = 12.dp,
    invert: Boolean = false,
    theme: ToastTheme = ToastTheme.System,
    enter: EnterTransition = DefaultToastEnter,
    exit: ExitTransition = DefaultToastExit,
    containerAriaLabel: String = "Notifications",
    closeAriaLabel: String = "Close toast",
) {
    SideEffect { state.durationMillis = duration ?: durationMillis }
    val isDark = Theme.isDark
    val resolvedInvert =
        when (theme) {
            ToastTheme.System -> invert
            ToastTheme.Light -> invert xor isDark
            ToastTheme.Dark -> invert xor !isDark
        }
    val viewportModifier = modifier.then(if (offset > 0.dp) Modifier.padding(offset) else Modifier)
    if (currentTheme.activePreset == StylePreset.Nova && enter === DefaultToastEnter && exit === DefaultToastExit) {
        NovaToastViewport(
            state = state,
            modifier = viewportModifier,
            style = style,
            position = position,
            gap = gap,
            visibleToasts = visibleToasts,
            containerAriaLabel = containerAriaLabel,
            cardStyle = cardStyle,
            closeButton = closeButton,
            richColors = richColors,
            invert = resolvedInvert,
            closeAriaLabel = closeAriaLabel,
        )
        return
    }
    ToastViewport(
        modifier = viewportModifier,
        style = style,
        position = position,
        gap = gap,
        containerAriaLabel = containerAriaLabel,
    ) {
        state.toasts.forEachIndexed { index, toast ->
            key(state, toast.id) {
                Toast(
                    toast = toast,
                    onDismiss = { state.dismiss(toast.id) },
                    style = cardStyle,
                    enter = enter,
                    exit = exit,
                    closeButton = closeButton,
                    richColors = richColors,
                    invert = resolvedInvert,
                    closeAriaLabel = closeAriaLabel,
                    limited = index < state.toasts.size - visibleToasts.coerceAtLeast(0),
                )
            }
        }
    }
}

/** Slot-based vertical viewport for custom toast layouts. */
@Composable
fun ToastViewport(
    modifier: Modifier = Modifier,
    style: Style = Style,
    position: ToastPosition = ToastPosition.BottomEnd,
    gap: Dp = 12.dp,
    containerAriaLabel: String = "Notifications",
    content: @Composable () -> Unit,
) {
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val horizontal =
        when (position) {
            ToastPosition.TopStart, ToastPosition.BottomStart -> Alignment.Start
            ToastPosition.TopCenter, ToastPosition.BottomCenter -> Alignment.CenterHorizontally
            ToastPosition.TopEnd, ToastPosition.BottomEnd -> Alignment.End
        }
    Column(
        modifier =
            modifier
                .widthIn(max = 384.dp).fillMaxWidth()
                .semantics { contentDescription = containerAriaLabel }
                .styleable(rootStyleState, style),
        verticalArrangement = Arrangement.spacedBy(gap),
        horizontalAlignment = horizontal,
        content = { content() },
    )
}

/** One toast card. Matches shadcn / Base UI's `Toast`. */
@Composable
fun Toast(
    toast: ToastMessage,
    onDismiss: () -> Unit,
    style: Style,
    enter: EnterTransition,
    exit: ExitTransition,
    closeButton: Boolean = true,
    richColors: Boolean = false,
    invert: Boolean = false,
    closeAriaLabel: String = "Close toast",
    limited: Boolean = false,
) {
    var visible by remember { mutableStateOf(true) }
    val latestDismiss = rememberUpdatedState(onDismiss)
    LaunchedEffect(toast.id, toast.revision, toast.durationMillis) {
        visible = true
        if (toast.durationMillis <= 0) return@LaunchedEffect
        delay(toast.durationMillis)
        visible = false
        delay(200)
        latestDismiss.value()
    }

    AnimatedVisibility(
        visible = visible && !limited,
        enter = enter,
        exit = exit,
    ) {
        ToastCard(
            toast = toast,
            onDismiss = {
                visible = false
                onDismiss()
            },
            style = style,
            closeButton = closeButton,
            richColors = richColors,
            invert = invert,
            closeAriaLabel = closeAriaLabel,
        )
    }
}

@Composable
internal fun ToastCard(
    toast: ToastMessage,
    onDismiss: () -> Unit,
    style: Style,
    closeButton: Boolean,
    richColors: Boolean,
    invert: Boolean,
    closeAriaLabel: String,
    modifier: Modifier = Modifier,
    nova: Boolean = false,
    showContent: Boolean = true,
    interactive: Boolean = true,
    isActive: () -> Boolean = { true },
    interactionSource: MutableInteractionSource? = null,
    onAction: () -> Unit = { toast.onAction?.invoke() },
) {
    val theme = Theme.LocalTheme.current
    val accentColor =
        when (toast.variant) {
            ToastVariant.Success -> TwColors.green600
            ToastVariant.Error -> theme.colors.destructive
            ToastVariant.Warning -> TwColors.amber600
            ToastVariant.Info -> theme.colors.ring
            ToastVariant.Default, ToastVariant.Loading -> theme.colors.foreground
        }
    val richStyle =
        if (richColors) {
            Style { background(accentColor.copy(alpha = 0.12f)) }
        } else {
            Style
        }
    val invertStyle =
        if (invert) {
            Style {
                background(theme.colors.foreground)
                contentColor(theme.colors.background)
            }
        } else {
            Style
        }
    val interactions = interactionSource ?: remember { MutableInteractionSource() }
    val styleState = remember(interactions) { MutableStyleState(interactions) }
    val baseStyle = if (nova) rememberNovaToastCardStyle() else rememberToastCardStyle()
    Row(
        modifier =
            modifier
                .then(
                    if (showContent && interactive) {
                        Modifier.semantics {
                            liveRegion =
                                if (toast.priority == ToastPriority.High) {
                                    LiveRegionMode.Assertive
                                } else {
                                    LiveRegionMode.Polite
                                }
                        }
                    } else {
                        Modifier
                    },
                ).cssStyleable(styleState, baseStyle, richStyle, invertStyle, style),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showContent) {
            ToastStatusIcon(toast.variant)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (toast.title.isNotEmpty()) ToastTitle(toast.title)
                toast.description?.let { ToastDescription(it) }
            }
            toast.actionLabel?.let { label ->
                ToastActionControl(
                    onClick = { if (isActive()) onAction() },
                    modifier = Modifier,
                    altText = null,
                    interactive = interactive,
                    reserveMinTouchTarget = !nova,
                ) { Text { label } }
            }
            val cancelLabel = toast.cancelLabel
            val onCancel = toast.onCancel
            if (cancelLabel != null && onCancel != null) {
                ToastActionControl(
                    onClick = {
                        if (isActive()) {
                            onCancel()
                            onDismiss()
                        }
                    },
                    modifier = Modifier,
                    altText = null,
                    interactive = interactive,
                    reserveMinTouchTarget = !nova,
                ) { Text { cancelLabel } }
            }
            if (closeButton) {
                ToastCloseControl(
                    onClick = { if (isActive()) onDismiss() },
                    modifier = Modifier,
                    ariaLabel = closeAriaLabel,
                    interactive = interactive,
                    reserveMinTouchTarget = !nova,
                    icon = { ToastCloseIcon() },
                )
            }
        }
    }
}

private fun toastTypography(medium: Boolean = false): TextStyle =
    TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = if (medium) FontWeight.Medium else FontWeight.Normal,
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )

/** Title line of a toast. Matches shadcn / Base UI's title slot. */
@Composable
fun ToastTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(modifier = modifier, textStyle = toastTypography(medium = true)) { text }
}

@Composable
@JvmName("ToastTitleLambda")
@OverloadResolutionByLambdaReturnType
fun ToastTitle(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    ToastTitle(text = text(), modifier = modifier)
}

/** Supporting copy under [ToastTitle]. */
@Composable
fun ToastDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(modifier = modifier, textStyle = toastTypography().merge(TextColors.Muted)) { text }
}

@Composable
@JvmName("ToastDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun ToastDescription(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    ToastDescription(text = text(), modifier = modifier)
}

/** Dismiss control, shown by default by [Toaster]. */
@Composable
fun ToastClose(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ariaLabel: String = "Close toast",
    icon: @Composable () -> Unit = { ToastCloseIcon() },
) {
    ToastCloseControl(onClick, modifier, ariaLabel, interactive = true, icon = icon)
}

@Composable
private fun ToastCloseIcon() {
    Icon(ToastXIcon, modifier = Modifier.size(16.dp), tint = currentTheme.colors.mutedForeground)
}

@Composable
private fun ToastCloseControl(
    onClick: () -> Unit,
    modifier: Modifier,
    ariaLabel: String,
    interactive: Boolean,
    reserveMinTouchTarget: Boolean = true,
    icon: @Composable () -> Unit,
) {
    ButtonSurface(
        onClick = onClick,
        modifier = modifier,
        variant = ButtonVariant.Ghost,
        size = ButtonSize.IconSm,
        label = ariaLabel,
        interactive = interactive,
        reserveMinTouchTarget = reserveMinTouchTarget,
        content = icon,
    )
}

/** Optional action button on a toast. Matches shadcn / Base UI's `ToastAction`. Not shown by default. */
@Composable
fun ToastAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altText: String? = null,
    content: @Composable () -> Unit,
) {
    ToastActionControl(onClick, modifier, altText, interactive = true, content = content)
}

@Composable
private fun ToastActionControl(
    onClick: () -> Unit,
    modifier: Modifier,
    altText: String?,
    interactive: Boolean,
    reserveMinTouchTarget: Boolean = true,
    content: @Composable () -> Unit,
) {
    ButtonSurface(
        onClick = onClick,
        modifier = modifier,
        variant = ButtonVariant.Outline,
        size = ButtonSize.Sm,
        label = altText ?: "",
        interactive = interactive,
        reserveMinTouchTarget = reserveMinTouchTarget,
        content = content,
    )
}

/** String-label overload of [ToastAction]. Matches shadcn `<ToastAction>Undo</ToastAction>`. */
@Composable
fun ToastAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altText: String? = null,
) {
    ToastAction(onClick = onClick, modifier = modifier, altText = altText) {
        Text { text }
    }
}
