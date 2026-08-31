@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.alertdialog

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogDescription
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogOverlay
import com.toutiaoyan.compose.ui.components.dialog.DialogPortal
import com.toutiaoyan.compose.ui.components.dialog.DialogScope
import com.toutiaoyan.compose.ui.components.dialog.DialogTitle
import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.components.dialog.DialogTransitions
import com.toutiaoyan.compose.ui.components.dialog.dialogController
import com.toutiaoyan.compose.ui.components.dialog.dialogState
import com.toutiaoyan.compose.ui.components.overlay.OverlayTransitions
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

object AlertDialogDefaults {
    /**
     * Official [DialogContent] `showCloseButton` default (`true`).
     * [AlertDialogContent] stays `false` so confirm dialogs keep no X.
     */
    val ShowCloseButton = true
}

/**
 * A [Dialog] that can't be dismissed by clicking outside or a close button —
 * matches real shadcn/ui's `alert-dialog.tsx`:
 *
 * ```
 * AlertDialog
 * ├── AlertDialogTrigger
 * └── AlertDialogContent
 *     ├── AlertDialogHeader
 *     │   ├── AlertDialogTitle
 *     │   └── AlertDialogDescription
 *     └── AlertDialogFooter
 *         ├── AlertDialogCancel
 *         └── AlertDialogAction
 * ```
 *
 * Distinct from [Dialog] in real Radix: used when the user must make an
 * explicit choice, e.g. "Are you absolutely sure?". [AlertDialogAction] and
 * [AlertDialogCancel] are styled [com.toutiaoyan.compose.ui.components.button.Button]s
 * that also dismiss, matching web shadcn.
 *
 * Pass [open] + [onOpenChange] for controlled state; omit both (or pass
 * [defaultOpen]) for uncontrolled.
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 * AlertDialog(open = open, onOpenChange = { open = it }) {
 *     AlertDialogTrigger("Show Dialog")
 *     AlertDialogContent(
 *         "Are you absolutely sure?",
 *         description = "This action cannot be undone.",
 *     ) {
 *         AlertDialogFooter {
 *             AlertDialogCancel("Cancel")
 *             AlertDialogAction("Continue", variant = ButtonVariant.Destructive)
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun AlertDialog(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable DialogScope.() -> Unit,
) {
    Dialog(
        open = open,
        onOpenChange = onOpenChange,
        modifier = modifier,
        defaultOpen = defaultOpen,
        modal = modal,
        content = content,
    )
}

/** Reads the nearest [AlertDialog] scope. Same as [dialogState] (AlertDialog is a Dialog). */
@Composable
fun alertDialogState(): DialogScope = dialogState()

/** Toggles the parent [AlertDialog]. Matches real shadcn's `AlertDialogTrigger`. */
@Composable
fun DialogScope.AlertDialogTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    DialogTrigger(
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        style = style,
        content = content,
    )
}

/** String-label overload of [AlertDialogTrigger], rendering a [Button] wired to [toggle]. */
@Composable
fun DialogScope.AlertDialogTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    DialogTrigger(
        text = text,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

/**
 * The modal panel of an [AlertDialog]. Same chrome as [DialogContent], but
 * no close button and no outside-click dismiss by default.
 * Pass [AlertDialogDefaults.ShowCloseButton] for Dialog's official X.
 */
@Composable
fun DialogScope.AlertDialogContent(
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = false,
    closeAriaLabel: String = "Close",
    dismissOnClickOutside: Boolean = false,
    size: DialogSize = DialogSize.Default,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    style: Style = Style,
    enter: EnterTransition = DialogTransitions.Enter,
    exit: ExitTransition = DialogTransitions.Exit,
    scrimEnter: EnterTransition = OverlayTransitions.ScrimEnter,
    scrimExit: ExitTransition = OverlayTransitions.ScrimExit,
    content: @Composable ColumnScope.() -> Unit,
) {
    DialogContent(
        modifier = modifier,
        showCloseButton = showCloseButton,
        closeAriaLabel = closeAriaLabel,
        dismissOnClickOutside = dismissOnClickOutside,
        size = size,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
        style = style,
        enter = enter,
        exit = exit,
        scrimEnter = scrimEnter,
        scrimExit = scrimExit,
        content = content,
    )
}

/**
 * Convenience: [title] + optional [description] as [AlertDialogHeader], then [content].
 */
@Composable
fun DialogScope.AlertDialogContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = false,
    closeAriaLabel: String = "Close",
    dismissOnClickOutside: Boolean = false,
    size: DialogSize = DialogSize.Default,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    style: Style = Style,
    enter: EnterTransition = DialogTransitions.Enter,
    exit: ExitTransition = DialogTransitions.Exit,
    scrimEnter: EnterTransition = OverlayTransitions.ScrimEnter,
    scrimExit: ExitTransition = OverlayTransitions.ScrimExit,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialogContent(
        modifier = modifier,
        showCloseButton = showCloseButton,
        closeAriaLabel = closeAriaLabel,
        dismissOnClickOutside = dismissOnClickOutside,
        size = size,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
        style = style,
        enter = enter,
        exit = exit,
        scrimEnter = scrimEnter,
        scrimExit = scrimExit,
    ) {
        AlertDialogHeader(title, description = description)
        content()
    }
}

/** Alias for [DialogHeader] matching shadcn's `AlertDialogHeader`. */
@Composable
fun AlertDialogHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    DialogHeader(modifier = modifier, style = style, content = content)
}

/**
 * Optional leading media slot (icon / illustration). Matches Base UI
 * `AlertDialogMedia` — nova: `bg-muted mb-2 size-10 rounded-md`.
 */
@Composable
fun AlertDialogMedia(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .size(40.dp)
                .background(currentTheme.colors.muted, RoundedCornerShape(6.dp))
                .styleable(styleState, style),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun AlertDialogHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    AlertDialogHeader(modifier = modifier, style = style) {
        AlertDialogTitle(title)
        if (description != null) {
            AlertDialogDescription(description)
        }
    }
}

/** Alias for [DialogTitle] matching shadcn's `AlertDialogTitle`. */
@Composable
fun AlertDialogTitle(text: String) {
    DialogTitle(text)
}

@Composable
@JvmName("AlertDialogTitleLambda")
@OverloadResolutionByLambdaReturnType
fun AlertDialogTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    DialogTitle(modifier = modifier, style = style, text = text)
}

/** Alias for [DialogDescription] matching shadcn's `AlertDialogDescription`. */
@Composable
fun AlertDialogDescription(text: String) {
    DialogDescription(text)
}

@Composable
@JvmName("AlertDialogDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun AlertDialogDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    DialogDescription(modifier = modifier, style = style, text = text)
}

/** Alias for [DialogFooter] matching shadcn's `AlertDialogFooter`. */
@Composable
fun AlertDialogFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    DialogFooter(modifier = modifier, style = style, content = content)
}

/** Alias for [DialogPortal] matching shadcn's `AlertDialogPortal`. */
@Composable
fun AlertDialogPortal(
    dismissOnClickOutside: Boolean = false,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    DialogPortal(
        dismissOnClickOutside = dismissOnClickOutside,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
        content = content,
    )
}

/** Alias for [DialogOverlay] matching shadcn's `AlertDialogOverlay`. */
@Composable
fun AlertDialogOverlay(
    enter: EnterTransition = OverlayTransitions.ScrimEnter,
    exit: ExitTransition = OverlayTransitions.ScrimExit,
) {
    DialogOverlay(enter = enter, exit = exit)
}

/**
 * Confirm action — web `AlertDialogAction`. Default [Button] that also dismisses.
 */
@Composable
fun AlertDialogAction(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = dialogController()
    Button(
        onClick = {
            onClick()
            controller.onOpenChange(false)
        },
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
        content = content,
    )
}

/** String-label overload of [AlertDialogAction]. Matches shadcn `<AlertDialogAction>Continue</AlertDialogAction>`. */
@Composable
fun AlertDialogAction(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    AlertDialogAction(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    ) {
        Text { text }
    }
}

/**
 * Cancel action — web `AlertDialogCancel`. Outline [Button] that also dismisses.
 */
@Composable
fun AlertDialogCancel(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = dialogController()
    Button(
        onClick = {
            onClick()
            controller.onOpenChange(false)
        },
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
        content = content,
    )
}

/** String-label overload of [AlertDialogCancel]. Matches shadcn `<AlertDialogCancel>Cancel</AlertDialogCancel>`. */
@Composable
fun AlertDialogCancel(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    AlertDialogCancel(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    ) {
        Text { text }
    }
}
