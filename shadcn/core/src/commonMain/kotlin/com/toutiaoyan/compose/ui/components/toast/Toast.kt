@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.icons.X
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.twSpacing
import kotlinx.coroutines.delay
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

enum class ToastVariant { Default, Success, Error, Warning, Info }

/** Viewport edge for [Toaster], matching sonner `position`. Default is bottom-end. */
enum class ToastPosition {
    TopStart,
    TopCenter,
    TopEnd,
    BottomStart,
    BottomCenter,
    BottomEnd,
}

/** sonner `theme`. [System] follows the current Compose theme (default, pixels unchanged). */
enum class ToastTheme { System, Light, Dark }

data class ToastMessage(
    val id: Long,
    val title: String,
    val description: String? = null,
    val variant: ToastVariant = ToastVariant.Default,
    val durationMillis: Long = 4000L,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val cancelLabel: String? = null,
    val onCancel: (() -> Unit)? = null,
)

/**
 * Holds the queue of currently-visible toasts. Real shadcn/ui's `sonner.tsx` is a thin
 * wrapper around the separate `sonner` npm toast library, which owns the actual queue,
 * stacking, and swipe-to-dismiss; since there is no such library for Compose
 * Multiplatform, this class + [Toaster] together reimplement that queue directly.
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
    var durationMillis: Long = 4000L

    fun show(
        title: String,
        description: String? = null,
        variant: ToastVariant = ToastVariant.Default,
        durationMillis: Long? = null,
        duration: Long? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null,
        cancelLabel: String? = null,
        onCancel: (() -> Unit)? = null,
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

    /** Dismiss one toast by [id], or every toast when [id] is omitted. Matches sonner `toast.dismiss()`. */
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

/** Renders [state]'s current toast queue as a bottom-aligned stack. Place once, near the root of your app. */
@Composable
fun Toaster(
    state: ToastState,
    modifier: Modifier = Modifier,
    style: Style = Style,
    cardStyle: Style = Style,
    position: ToastPosition = ToastPosition.BottomEnd,
    closeButton: Boolean = false,
    durationMillis: Long = 4000L,
    duration: Long? = null,
    visibleToasts: Int = Int.MAX_VALUE,
    richColors: Boolean = false,
    offset: Dp = 0.dp,
    gap: Dp = twSpacing.sm,
    invert: Boolean = false,
    theme: ToastTheme = ToastTheme.System,
    enter: EnterTransition = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
    exit: ExitTransition = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
    containerAriaLabel: String = "Notifications",
    closeAriaLabel: String = "Close",
) {
    SideEffect { state.durationMillis = duration ?: durationMillis }
    val isDark = Theme.isDark
    val resolvedInvert =
        when (theme) {
            ToastTheme.System -> invert
            ToastTheme.Light -> invert xor isDark
            ToastTheme.Dark -> invert xor !isDark
        }
    ToastViewport(
        modifier =
            modifier.then(if (offset > 0.dp) Modifier.padding(offset) else Modifier),
        style = style,
        position = position,
        gap = gap,
        containerAriaLabel = containerAriaLabel,
    ) {
        state.toasts.takeLast(visibleToasts).forEach { toast ->
            key(toast.id) {
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
                )
            }
        }
    }
}

/** Stack host for toast cards. Matches shadcn / sonner's `ToastViewport`. */
@Composable
fun ToastViewport(
    modifier: Modifier = Modifier,
    style: Style = Style,
    position: ToastPosition = ToastPosition.BottomEnd,
    gap: Dp = twSpacing.sm,
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
                .semantics { contentDescription = containerAriaLabel }
                .styleable(rootStyleState, style),
        verticalArrangement = Arrangement.spacedBy(gap),
        horizontalAlignment = horizontal,
        content = { content() },
    )
}

/** One toast card. Matches shadcn / sonner's `Toast`. */
@Composable
fun Toast(
    toast: ToastMessage,
    onDismiss: () -> Unit,
    style: Style,
    enter: EnterTransition,
    exit: ExitTransition,
    closeButton: Boolean = false,
    richColors: Boolean = false,
    invert: Boolean = false,
    closeAriaLabel: String = "Close",
) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(toast.id) {
        delay(toast.durationMillis)
        visible = false
        delay(200)
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = enter,
        exit = exit,
    ) {
        val theme = Theme.LocalTheme.current
        val accentColor =
            when (toast.variant) {
                ToastVariant.Success -> TwColors.green600
                ToastVariant.Error -> theme.colors.destructive
                ToastVariant.Warning -> TwColors.amber600
                ToastVariant.Info -> theme.colors.ring
                ToastVariant.Default -> theme.colors.border
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
        val styleState = remember { MutableStyleState(MutableInteractionSource()) }
        Row(
            modifier =
                Modifier
                    .semantics { liveRegion = LiveRegionMode.Polite }
                    .styleable(
                        styleState,
                        rememberToastCardStyle() then richStyle then invertStyle then style,
                    ),
            horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(top = 4.dp)
                        .size(8.dp)
                        .background(accentColor, CircleShape),
            )
            Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs2)) {
                ToastTitle(toast.title)
                if (toast.description != null) {
                    ToastDescription(toast.description)
                }
            }
            val actionLabel = toast.actionLabel
            val onAction = toast.onAction
            if (actionLabel != null && onAction != null) {
                ToastAction(actionLabel, onClick = {
                    onAction()
                    visible = false
                    onDismiss()
                })
            }
            val cancelLabel = toast.cancelLabel
            val onCancel = toast.onCancel
            if (cancelLabel != null && onCancel != null) {
                ToastAction(cancelLabel, onClick = {
                    onCancel()
                    visible = false
                    onDismiss()
                })
            }
            if (closeButton) {
                ToastClose(
                    onClick = {
                        visible = false
                        onDismiss()
                    },
                    ariaLabel = closeAriaLabel,
                )
            }
        }
    }
}

/** Title line of a toast. Matches shadcn / sonner's title slot. */
@Composable
fun ToastTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(modifier = modifier, variant = TextVariant.LabelLarge) { text }
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
    Text(modifier = modifier, variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { text }
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

/** Optional dismiss control. Matches shadcn / sonner's `ToastClose`. Not shown by default. */
@Composable
fun ToastClose(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ariaLabel: String = "Close",
    icon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription = ariaLabel
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

/** Optional action button on a toast. Matches shadcn / sonner's `ToastAction`. Not shown by default. */
@Composable
fun ToastAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    altText: String? = null,
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        variant = ButtonVariant.Outline,
        size = ButtonSize.Sm,
        label = altText ?: "",
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
