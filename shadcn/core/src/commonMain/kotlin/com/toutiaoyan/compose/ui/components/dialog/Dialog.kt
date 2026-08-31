@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.dialog

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.overlay.Content
import com.toutiaoyan.compose.ui.components.overlay.Overlay
import com.toutiaoyan.compose.ui.components.overlay.OverlayAnimMs
import com.toutiaoyan.compose.ui.components.overlay.OverlayTransitions
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitive
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitiveScope
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.icons.X
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Panel motion for [Dialog] / [AlertDialog] — web shadcn fade + zoom-95. */
object DialogTransitions {
    val Enter: EnterTransition =
        fadeIn(tween(OverlayAnimMs)) + scaleIn(initialScale = 0.95f, animationSpec = tween(OverlayAnimMs))
    val Exit: ExitTransition =
        fadeOut(tween(OverlayAnimMs)) + scaleOut(targetScale = 0.95f, animationSpec = tween(OverlayAnimMs))
}

@Immutable
internal class DialogController(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val modal: Boolean,
) {
    fun toggle() = onOpenChange(!open)
}

private val LocalDialog = staticCompositionLocalOf<DialogController?> { null }

internal val LocalDialogTeleportScope =
    staticCompositionLocalOf<TeleportPrimitiveScope?> { null }

@Composable
internal fun dialogController(): DialogController =
    checkNotNull(LocalDialog.current) {
        "DialogTrigger / DialogContent / DialogClose / DialogPortal must be used inside Dialog { … }"
    }

/** Reads the nearest [Dialog] scope. Matches Radix `useDialogContext`. */
@Composable
fun dialogState(): DialogScope = DialogScope(dialogController())

/**
 * Slot receiver for [DialogTrigger] / [DialogContent].
 *
 * [open] / [toggle] match the state Radix exposes on the trigger
 * (`data-state="open"`).
 */
class DialogScope internal constructor(
    private val controller: DialogController,
) {
    val open: Boolean get() = controller.open

    val toggle: () -> Unit get() = controller::toggle
}

/**
 * A modal dialog matching real shadcn/ui's `dialog.tsx`:
 *
 * ```
 * Dialog
 * ├── DialogTrigger
 * └── DialogContent   (Portal + Overlay + panel)
 *     ├── DialogClose
 *     ├── DialogHeader
 *     │   ├── DialogTitle
 *     │   └── DialogDescription
 *     └── DialogFooter
 * ```
 *
 * Pass [open] + [onOpenChange] for controlled state; omit both (or pass
 * [defaultOpen]) for uncontrolled, matching Radix `open` / `defaultOpen` /
 * `onOpenChange`. Outside click and Escape close via [onOpenChange]`(false)`.
 *
 * Built on [TeleportPrimitive] with separate Overlay / Content (not
 * [androidx.compose.ui.window.Dialog] — see that host's doc comment for why).
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 * Dialog(open = open, onOpenChange = { open = it }) {
 *     DialogTrigger("Edit profile")
 *     DialogContent("Edit profile", description = "Make changes to your profile here.") {
 *         DialogFooter {
 *             Button("Save changes", onClick = { open = false })
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun Dialog(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable DialogScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val controller = DialogController(open = isOpen, onOpenChange = setOpen, modal = modal)
    CompositionLocalProvider(LocalDialog provides controller) {
        Box(modifier = modifier) {
            DialogScope(controller).content()
        }
    }
}

/**
 * Toggles the parent [Dialog]. Matches real shadcn's `DialogTrigger`
 * (unstyled button primitive). Put any chrome inside — typically a
 * [com.toutiaoyan.compose.ui.components.button.Button].
 *
 * When the child is already clickable (e.g. a Button wired to [DialogScope.toggle]),
 * that inner click wins; this wrapper still covers the rest of the hit target.
 */
@Composable
fun DialogScope.DialogTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = dialogController()
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)
    val resolvedEnabled = enabled && !disabled
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    stateDescription = if (controller.open) "Expanded" else "Collapsed"
                }
                .clickable(
                    interactionSource = interactionSource,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = controller::toggle,
                )
                .styleable(styleState, style),
    ) {
        content()
    }
}

/** String-label overload of [DialogTrigger], rendering a [Button] wired to [toggle]. */
@Composable
fun DialogScope.DialogTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    DialogTrigger(modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
        Button(text, onClick = toggle, enabled = enabled, disabled = disabled, variant = variant, size = size)
    }
}

/**
 * The modal panel of a [Dialog]. Matches base-nova `.cn-dialog-content`
 * (`rounded-xl border bg-popover p-4`, `sm:max-w-sm`).
 *
 * [size] picks the panel width ([DialogSize.Default] is 384dp). Override
 * chrome further with [style]. Motion defaults match web shadcn (fade + zoom-95);
 * override [enter]/[exit]/[scrimEnter]/[scrimExit] like Compose Unstyled's Dialog.
 * [onPointerDownOutside] is Radix `onPointerDownOutside` (return true to prevent dismiss).
 * [onInteractOutside] is Radix `onInteractOutside` (return true to prevent dismiss);
 * overlay click runs pointer then interact — either true prevents dismiss.
 */
@Composable
fun DialogScope.DialogContent(
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = true,
    closeAriaLabel: String = "Close",
    dismissOnClickOutside: Boolean = true,
    size: DialogSize = DialogSize.Default,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    style: Style = Style,
    enter: EnterTransition = DialogTransitions.Enter,
    exit: ExitTransition = DialogTransitions.Exit,
    scrimEnter: EnterTransition = OverlayTransitions.ScrimEnter,
    scrimExit: ExitTransition = OverlayTransitions.ScrimExit,
    // A self-generated ImageVector, not a third-party icon-set dependency (this library
    // still takes none -- see README) -- text glyphs don't render on WasmJS (Skia has no
    // browser emoji-font fallback). Override with any icon set (e.g. this repo's own demo
    // app passes a real Lucide XMark here -- see DialogDoc.kt).
    closeIcon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val controller = dialogController()
    DialogPortal(
        dismissOnClickOutside = dismissOnClickOutside,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
    ) {
        if (controller.modal) {
            DialogOverlay(enter = scrimEnter, exit = scrimExit)
        }
        val modal =
            checkNotNull(LocalDialogTeleportScope.current) {
                "DialogContent panel must be used within DialogPortal { }"
            }
        modal.Content(enter = enter, exit = exit) {
            Box(
                modifier =
                    modifier.styleable(
                        styleState,
                        rememberDialogPanelStyle(size) then style,
                    ),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.lg)) {
                    content()
                }
                if (showCloseButton) {
                    DialogClose(
                        modifier = Modifier.align(Alignment.TopEnd),
                        ariaLabel = closeAriaLabel,
                        icon = closeIcon,
                    )
                }
            }
        }
    }
}

/**
 * Convenience: [title] + optional [description] as [DialogHeader], then [content].
 */
@Composable
fun DialogScope.DialogContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = true,
    closeAriaLabel: String = "Close",
    dismissOnClickOutside: Boolean = true,
    size: DialogSize = DialogSize.Default,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    style: Style = Style,
    enter: EnterTransition = DialogTransitions.Enter,
    exit: ExitTransition = DialogTransitions.Exit,
    scrimEnter: EnterTransition = OverlayTransitions.ScrimEnter,
    scrimExit: ExitTransition = OverlayTransitions.ScrimExit,
    closeIcon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
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
        closeIcon = closeIcon,
    ) {
        DialogHeader(title, description = description)
        content()
    }
}

/**
 * Full-window portal ([TeleportPrimitive]) — web `DialogPortal`.
 * Prefer [DialogContent] unless you need to customize Overlay / panel separately.
 */
@Composable
fun DialogPortal(
    dismissOnClickOutside: Boolean = true,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    val controller = dialogController()
    TeleportPrimitive(
        visible = controller.open,
        onDismissRequest = { controller.onOpenChange(false) },
        dismissOnClickOutside = dismissOnClickOutside,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
    ) {
        CompositionLocalProvider(LocalDialogTeleportScope provides this) {
            content()
        }
    }
}

/** Scrim — web `DialogOverlay`. Must be composed under [DialogPortal]. */
@Composable
fun DialogOverlay(
    enter: EnterTransition = OverlayTransitions.ScrimEnter,
    exit: ExitTransition = OverlayTransitions.ScrimExit,
) {
    val modal =
        LocalDialogTeleportScope.current
            ?: error("DialogOverlay must be used within DialogPortal { }")
    modal.Overlay(enter = enter, exit = exit)
}

/**
 * Dismisses the parent [Dialog]. Matches shadcn's `DialogClose`.
 * [DialogContent] already places one when [DialogContent] `showCloseButton` is true.
 * [ariaLabel] is the accessible name (web sr-only “Close”).
 */
@Composable
fun DialogClose(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    ariaLabel: String = "Close",
    icon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
) {
    val controller = dialogController()
    val resolvedEnabled = !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val theme = currentTheme
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val closeButtonStyle =
        remember(theme) {
            Style {
                focusRing(RoundedCornerShape(twRadius.sm))
            }
        }
    Box(
        modifier =
            modifier
                .then(if (resolvedEnabled) Modifier else Modifier.alpha(0.5f))
                .semantics {
                    role = Role.Button
                    contentDescription = ariaLabel
                    if (!resolvedEnabled) disabled()
                }
                .styleable(styleState, touchTargetStyle then closeButtonStyle then pressStyle)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = { controller.onOpenChange(false) },
                ),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

/** The title + description block at the top of a [Dialog]. */
@Composable
fun DialogHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs),
    ) {
        content()
    }
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun DialogHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    DialogHeader(modifier = modifier, style = style) {
        DialogTitle(title)
        if (description != null) {
            DialogDescription(description)
        }
    }
}

@Composable
fun DialogTitle(text: String) {
    DialogTitle { text }
}

@Composable
@JvmName("DialogTitleLambda")
@OverloadResolutionByLambdaReturnType
fun DialogTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier.semantics { heading() },
        variant = TextVariant.TitleMedium,
        style = style,
        text = text,
    )
}

@Composable
fun DialogDescription(text: String) {
    Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { text }
}

@Composable
@JvmName("DialogDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun DialogDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle = TextColors.Muted,
        style = style,
        text = text,
    )
}

/** The action-button row at the bottom of a [Dialog], right-aligned.
 * [showCloseButton] matches current shadcn `DialogFooter` (default `false`).
 */
@Composable
fun DialogFooter(
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = false,
    closeAriaLabel: String = "Close",
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val controller = dialogController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm, Alignment.End),
    ) {
        content()
        if (showCloseButton) {
            Button(
                text = closeAriaLabel,
                onClick = { controller.onOpenChange(false) },
                variant = ButtonVariant.Outline,
            )
        }
    }
}
