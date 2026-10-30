@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.alertdialog

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.overlay.overlayTypography
import com.toutiaoyan.compose.ui.components.text.TextColors
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Dp
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
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.overlay.OverlayTransitions
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

private enum class AlertDialogHeaderPart { Media }

private val LocalAlertDialogStretchActions = staticCompositionLocalOf { false }
private val LocalAlertDialogSize = staticCompositionLocalOf { DialogSize.Default }

@Composable
private fun centeredAlertHeader(): Boolean =
    LocalAlertDialogSize.current == DialogSize.Sm ||
        LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 640.dp.roundToPx() }

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
    val mobile = LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 640.dp.roundToPx() }
    val ringColor = currentTheme.colors.foreground.copy(alpha = 0.1f)
    val alertChrome =
        if (currentTheme.activePreset == StylePreset.Nova) {
            Style {
                // Nova uses an outer ring, which must not consume the 16dp padding.
                borderWidth(Dp.Unspecified)
                dropShadow(Shadow(radius = 0.dp, spread = 1.dp, color = ringColor))
                if (mobile && size == DialogSize.Default) width(320.dp)
            }
        } else {
            Style
        }
    DialogContent(
        modifier = modifier,
        showCloseButton = showCloseButton,
        closeAriaLabel = closeAriaLabel,
        dismissOnClickOutside = dismissOnClickOutside,
        size = size,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
        style = alertChrome then style,
        enter = enter,
        exit = exit,
        scrimEnter = scrimEnter,
        scrimExit = scrimExit,
        content = {
            CompositionLocalProvider(LocalAlertDialogSize provides size) { content() }
        },
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

/** Title/description group: Nova uses a 6dp gap and centers compact headers. */
@Composable
fun AlertDialogHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (currentTheme.activePreset != StylePreset.Nova) {
        DialogHeader(modifier = modifier, style = style, content = content)
        return
    }
    val state = remember { MutableStyleState(MutableInteractionSource()) }
    val centered = centeredAlertHeader()
    Column(modifier = modifier.fillMaxWidth().styleable(state, style)) {
        Layout(content = { content() }) { measurables, constraints ->
            val loose = constraints.copy(minWidth = 0, minHeight = 0)
            val mediaNode = measurables.firstOrNull { it.layoutId == AlertDialogHeaderPart.Media }
            val media = mediaNode?.measure(loose)
            val gap = 6.dp.roundToPx()
            val mediaBottomMargin = 8.dp.roundToPx()
            val mediaColumn = if (media != null && !centered) media.width + 16.dp.roundToPx() else 0
            val bodyWidth =
                if (constraints.hasBoundedWidth) {
                    (constraints.maxWidth - mediaColumn).coerceAtLeast(
                        0,
                    )
                } else {
                    constraints.maxWidth
                }
            val body = measurables.filter { it !== mediaNode }.map { it.measure(loose.copy(maxWidth = bodyWidth)) }
            val width =
                constraints.constrainWidth(
                    if (constraints.hasBoundedWidth) {
                        constraints.maxWidth
                    } else {
                        maxOf((body.maxOfOrNull { it.width } ?: 0) + mediaColumn, media?.width ?: 0)
                    },
                )
            val bodyHeight = body.sumOf { it.height } + gap * (body.size - 1).coerceAtLeast(0)
            val mediaHeight = if (media == null) 0 else media.height + mediaBottomMargin
            val height =
                if (centered && media != null) {
                    mediaHeight + (if (body.isNotEmpty()) gap else 0) + bodyHeight
                } else {
                    maxOf(bodyHeight, mediaHeight)
                }
            layout(width, constraints.constrainHeight(height)) {
                media?.placeRelative(if (centered) (width - media.width) / 2 else 0, 0)
                var y = if (centered && media != null) mediaHeight + (if (body.isNotEmpty()) gap else 0) else 0
                body.forEach { child ->
                    child.placeRelative(if (centered) (width - child.width) / 2 else mediaColumn, y)
                    y += child.height + gap
                }
            }
        }
    }
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
    val nova = currentTheme.activePreset == StylePreset.Nova
    val muted = currentTheme.colors.muted
    val chrome =
        Style {
            width(40.dp)
            height(40.dp)
            background(muted)
            shape(RoundedCornerShape(6.dp))
        }
    Box(
        modifier = modifier.layoutId(AlertDialogHeaderPart.Media).styleable(styleState, chrome, style),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalInlineIconSize provides if (nova) 24.dp else LocalInlineIconSize.current) {
            content()
        }
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

/** Alert heading; Nova uses a 16sp font with a 24sp line height. */
@Composable
fun AlertDialogTitle(text: String) {
    AlertDialogTitle { text }
}

@Composable
@JvmName("AlertDialogTitleLambda")
@OverloadResolutionByLambdaReturnType
fun AlertDialogTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    if (currentTheme.activePreset != StylePreset.Nova) {
        DialogTitle(modifier = modifier, style = style, text = text)
        return
    }
    Text(
        modifier = modifier.semantics { heading() },
        textStyle =
            overlayTypography(16, 24, medium = true).copy(
                textAlign = if (centeredAlertHeader()) TextAlign.Center else TextAlign.Start,
            ),
        style = style,
        text = text,
    )
}

/** Alert description, centered alongside compact Nova headings. */
@Composable
fun AlertDialogDescription(text: String) {
    AlertDialogDescription { text }
}

@Composable
@JvmName("AlertDialogDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun AlertDialogDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    if (currentTheme.activePreset != StylePreset.Nova) {
        DialogDescription(modifier = modifier, style = style, text = text)
        return
    }
    Text(
        modifier = modifier,
        textStyle =
            overlayTypography(muted = true, fallback = TextColors.Muted).copy(
                textAlign = if (centeredAlertHeader()) TextAlign.Center else TextAlign.Start,
            ),
        style = style,
        text = text,
    )
}

/** Nova: reversed full-width phone actions; equal columns for small panels. */
@Composable
fun AlertDialogFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val small = LocalAlertDialogSize.current == DialogSize.Sm
    if (currentTheme.activePreset != StylePreset.Nova || !centeredAlertHeader()) {
        DialogFooter(modifier = modifier, style = style, content = content)
        return
    }
    val state = remember { MutableStyleState(MutableInteractionSource()) }
    val colors = currentTheme.colors
    val radius = com.toutiaoyan.compose.tailwind.core.twRadius.xl
    val chrome =
        Style {
            background(colors.muted.copy(alpha = 0.5f))
            shape(RoundedCornerShape(bottomStart = radius, bottomEnd = radius))
            contentPadding(16.dp)
        }
    val bleed =
        Modifier.layout { measurable, constraints ->
            val inset = 16.dp.roundToPx()
            val child = measurable.measure(constraints.offset(horizontal = inset * 2))
            layout((child.width - inset * 2).coerceAtLeast(0), (child.height - inset).coerceAtLeast(0)) {
                child.placeRelative(-inset, 0)
            }
        }
    Row(
        bleed.then(modifier).fillMaxWidth().drawWithContent {
            drawContent()
            drawLine(colors.border, Offset(0f, 0.5.dp.toPx()), Offset(size.width, 0.5.dp.toPx()), 1.dp.toPx())
        }.padding(top = 1.dp).styleable(state, chrome, style),
    ) {
        // Keep the public RowScope slot; only compact Nova changes placement.
        Layout(content = {
            CompositionLocalProvider(LocalAlertDialogStretchActions provides true) { content() }
        }) { measurables, constraints ->
            val gap = 8.dp.roundToPx()
            val count = measurables.size
            val width = constraints.maxWidth
            val cellWidth = if (small) ((width - gap) / 2).coerceAtLeast(0) else width
            val children =
                measurables.map {
                    it.measure(constraints.copy(minWidth = cellWidth, maxWidth = cellWidth, minHeight = 0))
                }
            val rows = if (small) children.chunked(2) else emptyList()
            val height =
                if (small) {
                    rows.sumOf { row -> row.maxOf { it.height } } + gap * (rows.size - 1).coerceAtLeast(0)
                } else {
                    children.sumOf { it.height } + gap * (count - 1).coerceAtLeast(0)
                }
            layout(width, constraints.constrainHeight(height)) {
                if (small) {
                    var y = 0
                    rows.forEach { row ->
                        row.forEachIndexed { column, child -> child.placeRelative(column * (cellWidth + gap), y) }
                        y += row.maxOf { it.height } + gap
                    }
                } else {
                    var y = 0
                    children.asReversed().forEach { child ->
                        child.placeRelative(0, y)
                        y += child.height + gap
                    }
                }
            }
        }
    }
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
        style = (if (LocalAlertDialogStretchActions.current) Style { width(1f) } else Style) then style,
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
        style = (if (LocalAlertDialogStretchActions.current) Style { width(1f) } else Style) then style,
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
