@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.sheet

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.icons.X
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.overlay.Content
import com.toutiaoyan.compose.ui.components.overlay.Overlay
import com.toutiaoyan.compose.ui.components.overlay.OverlayTransitions
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitive
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitiveScope
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Which edge a [Sheet] slides in from. */
enum class SheetSide { Top, Bottom, Start, End }

object SheetDefaults {
    /** Official Content `showCloseButton` default. */
    const val ShowCloseButton = true
}

/** Sheet / drawer slide easing — web `cubic-bezier(0.32, 0.72, 0, 1)`. */
private val SheetSlideEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

private fun sheetSlideTween() = tween<IntOffset>(500, easing = SheetSlideEasing)

/** Edge-slide panel motion for [Sheet] (and drawer via [SheetSide]). */
object SheetTransitions {
    fun enter(side: SheetSide): EnterTransition =
        when (side) {
            SheetSide.Top -> slideInVertically(sheetSlideTween()) { -it }
            SheetSide.Bottom -> slideInVertically(sheetSlideTween()) { it }
            SheetSide.Start -> slideInHorizontally(sheetSlideTween()) { -it }
            SheetSide.End -> slideInHorizontally(sheetSlideTween()) { it }
        }

    fun exit(side: SheetSide): ExitTransition =
        when (side) {
            SheetSide.Top -> slideOutVertically(sheetSlideTween()) { -it }
            SheetSide.Bottom -> slideOutVertically(sheetSlideTween()) { it }
            SheetSide.Start -> slideOutHorizontally(sheetSlideTween()) { -it }
            SheetSide.End -> slideOutHorizontally(sheetSlideTween()) { it }
        }
}

@Immutable
internal class SheetController(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val modal: Boolean,
) {
    fun toggle() = onOpenChange(!open)
}

private val LocalSheet = staticCompositionLocalOf<SheetController?> { null }

internal val LocalSheetTeleportScope =
    staticCompositionLocalOf<TeleportPrimitiveScope?> { null }

@Composable
private fun sheetController(): SheetController =
    checkNotNull(LocalSheet.current) {
        "SheetTrigger / SheetContent / SheetClose / SheetPortal must be used inside Sheet { … }"
    }

/** Reads the nearest [Sheet] scope. Matches Radix `useDialogContext` used by shadcn Sheet. */
@Composable
fun sheetState(): SheetScope = SheetScope(sheetController())

/**
 * Slot receiver for [SheetTrigger] / [SheetContent].
 *
 * [open] / [toggle] match the state Radix exposes on the trigger
 * (`data-state="open"`).
 */
class SheetScope internal constructor(
    private val controller: SheetController,
) {
    val open: Boolean get() = controller.open

    val toggle: () -> Unit get() = controller::toggle
}

/**
 * A modal panel that slides in from a screen edge. Matches real shadcn/ui's
 * `sheet.tsx`:
 *
 * ```
 * Sheet
 * ├── SheetTrigger
 * └── SheetContent   (Portal + Overlay + panel)
 *     ├── SheetClose
 *     ├── SheetHeader
 *     │   ├── SheetTitle
 *     │   └── SheetDescription
 *     └── SheetFooter
 * ```
 *
 * Full-height + fixed-width for [SheetSide.Start]/[SheetSide.End]
 * (`w-3/4 sm:max-w-sm`), full-width + auto-height for [SheetSide.Top]/
 * [SheetSide.Bottom]. For the Base UI drawer with optional swipe handle
 * *with* swipe-to-dismiss, see
 * [com.toutiaoyan.compose.ui.components.drawer.Drawer] instead —
 * that's now a separate component with real drag physics, not an approximation via this one.
 *
 * Pass [open] + [onOpenChange] for controlled state; omit both (or pass
 * [defaultOpen]) for uncontrolled. Panel chrome is Style-driven — override
 * with [SheetContent]'s `style`.
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 * Sheet(open = open, onOpenChange = { open = it }) {
 *     SheetTrigger("Open")
 *     SheetContent {
 *         SheetHeader("Edit profile", description = "Make changes to your profile here.")
 *     }
 * }
 * ```
 */
@Composable
fun Sheet(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable SheetScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val controller = SheetController(open = isOpen, onOpenChange = setOpen, modal = modal)
    CompositionLocalProvider(LocalSheet provides controller) {
        Box(modifier = modifier) {
            SheetScope(controller).content()
        }
    }
}

/**
 * Toggles the parent [Sheet]. Matches real shadcn's `SheetTrigger`
 * (unstyled button primitive). Put any chrome inside — typically a
 * [com.toutiaoyan.compose.ui.components.button.Button].
 */
@Composable
fun SheetScope.SheetTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = sheetController()
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
                .keyboardInputMode()
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

/** String-label overload of [SheetTrigger], rendering a [Button] wired to [toggle]. */
@Composable
fun SheetScope.SheetTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    SheetTrigger(modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
        Button(text, onClick = toggle, enabled = enabled, disabled = disabled, variant = variant, size = size)
    }
}

/**
 * The sliding panel of a [Sheet]. [side] is Radix `side` (`top` / `bottom` /
 * `left`/`start` / `right`/`end`); default [SheetSide.End] (= official `"right"`).
 * Enter/exit default to sliding from that edge; override [enter]/[exit]/[scrimEnter]/
 * [scrimExit] like Compose Unstyled. Official [showCloseButton] defaults to
 * [SheetDefaults.ShowCloseButton] (`true`). Composes internal Portal + Overlay.
 */
@Composable
fun SheetScope.SheetContent(
    modifier: Modifier = Modifier,
    side: SheetSide = SheetSide.End,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    showCloseButton: Boolean = SheetDefaults.ShowCloseButton,
    closeAriaLabel: String = "Close",
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    enter: EnterTransition? = null,
    exit: ExitTransition? = null,
    scrimEnter: EnterTransition = OverlayTransitions.ScrimEnter,
    scrimExit: ExitTransition = OverlayTransitions.ScrimExit,
    closeIcon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
    content: @Composable ColumnScope.() -> Unit,
) {
    val resolvedEnter = enter ?: SheetTransitions.enter(side)
    val resolvedExit = exit ?: SheetTransitions.exit(side)
    val alignment =
        when (side) {
            SheetSide.Top -> Alignment.TopCenter
            SheetSide.Bottom -> Alignment.BottomCenter
            SheetSide.Start -> Alignment.CenterStart
            SheetSide.End -> Alignment.CenterEnd
        }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val controller = sheetController()

    SheetPortal(
        dismissOnClickOutside = dismissOnClickOutside,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
    ) {
        if (controller.modal) {
            SheetOverlay(enter = scrimEnter, exit = scrimExit)
        }
        val modal =
            checkNotNull(LocalSheetTeleportScope.current) {
                "SheetContent panel must be used within SheetPortal { }"
            }
        modal.Content(contentAlignment = alignment, enter = resolvedEnter, exit = resolvedExit) {
            // Official Start/End: w-3/4 sm:max-w-sm ≈ 320.dp.
            val sizeModifier =
                when (side) {
                    SheetSide.Top, SheetSide.Bottom -> Modifier.fillMaxWidth()
                    SheetSide.Start, SheetSide.End -> Modifier.fillMaxHeight().width(320.dp)
                }
            val panelModifier =
                sizeModifier
                    .then(modifier)
                    .styleable(
                        styleState,
                        rememberSheetPanelStyle() then style,
                    )
            if (showCloseButton) {
                Box(modifier = panelModifier) {
                    Column(verticalArrangement = Arrangement.spacedBy(twSpacing.lg)) {
                        content()
                    }
                    SheetClose(
                        modifier = Modifier.align(Alignment.TopEnd),
                        ariaLabel = closeAriaLabel,
                        icon = closeIcon,
                    )
                }
            } else {
                Column(
                    modifier = panelModifier,
                    verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Convenience: [title] + optional [description] as [SheetHeader], then [content].
 */
@Composable
fun SheetScope.SheetContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    side: SheetSide = SheetSide.End,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    showCloseButton: Boolean = SheetDefaults.ShowCloseButton,
    closeAriaLabel: String = "Close",
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    enter: EnterTransition? = null,
    exit: ExitTransition? = null,
    scrimEnter: EnterTransition = OverlayTransitions.ScrimEnter,
    scrimExit: ExitTransition = OverlayTransitions.ScrimExit,
    closeIcon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
    content: @Composable ColumnScope.() -> Unit,
) {
    SheetContent(
        modifier = modifier,
        side = side,
        style = style,
        dismissOnClickOutside = dismissOnClickOutside,
        showCloseButton = showCloseButton,
        closeAriaLabel = closeAriaLabel,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
        enter = enter,
        exit = exit,
        scrimEnter = scrimEnter,
        scrimExit = scrimExit,
        closeIcon = closeIcon,
    ) {
        SheetHeader(title, description = description)
        content()
    }
}

/**
 * Full-window portal ([TeleportPrimitive]) — web `SheetPortal` (not exported by
 * official sheet.tsx; kept internal for [SheetContent]).
 */
@Composable
internal fun SheetPortal(
    dismissOnClickOutside: Boolean = true,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    val controller = sheetController()
    TeleportPrimitive(
        visible = controller.open,
        onDismissRequest = { controller.onOpenChange(false) },
        dismissOnClickOutside = dismissOnClickOutside,
        onPointerDownOutside = onPointerDownOutside,
        onInteractOutside = onInteractOutside,
    ) {
        CompositionLocalProvider(LocalSheetTeleportScope provides this) {
            content()
        }
    }
}

/** Scrim — web `SheetOverlay` (not exported; composed by [SheetContent]). */
@Composable
internal fun SheetOverlay(
    enter: EnterTransition = OverlayTransitions.ScrimEnter,
    exit: ExitTransition = OverlayTransitions.ScrimExit,
) {
    val modal =
        LocalSheetTeleportScope.current
            ?: error("SheetOverlay must be used within SheetPortal { }")
    modal.Overlay(enter = enter, exit = exit)
}

/** Structural header slot matching shadcn's `SheetHeader`. */
@Composable
fun SheetHeader(
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
fun SheetHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    SheetHeader(modifier = modifier, style = style) {
        SheetTitle(title)
        if (description != null) {
            SheetDescription(description)
        }
    }
}

@Composable
fun SheetTitle(text: String) {
    SheetTitle { text }
}

@Composable
@JvmName("SheetTitleLambda")
@OverloadResolutionByLambdaReturnType
fun SheetTitle(
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
fun SheetDescription(text: String) {
    Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { text }
}

@Composable
@JvmName("SheetDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun SheetDescription(
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

/** The action-button row at the bottom of a [Sheet], right-aligned. */
@Composable
fun SheetFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm, Alignment.End),
    ) {
        content()
    }
}

/** Dismisses the parent [Sheet]. Matches shadcn's `SheetClose`. [ariaLabel] is the accessible name (web sr-only “Close”). */
@Composable
fun SheetClose(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    ariaLabel: String = "Close",
    icon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.mutedForeground, variant = IconVariant.Small)
    },
) {
    val controller = sheetController()
    val resolvedEnabled = !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val closeButtonStyle =
        remember {
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
                .keyboardInputMode()
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
