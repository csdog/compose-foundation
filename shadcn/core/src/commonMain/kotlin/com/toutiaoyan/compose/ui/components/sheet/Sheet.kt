@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.sheet

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.components.overlay.OverlayFocusOwner
import com.toutiaoyan.compose.ui.components.overlay.overlayTriggerFocus
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.icons.X
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.components.overlay.ContentWithInitialFocus
import com.toutiaoyan.compose.ui.components.overlay.captureTouchCanvasFocus
import com.toutiaoyan.compose.ui.components.overlay.Overlay
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitive
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitiveScope
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Physical screen edges, plus logical [Start] and [End] extensions that follow layout direction. */
enum class SheetSide { Top, Bottom, Start, End, Left, Right }

object SheetDefaults {
    /** Official Content `showCloseButton` default. */
    const val ShowCloseButton = true
}

internal fun SheetSide.physicalSide(direction: LayoutDirection): SheetSide =
    when (this) {
        SheetSide.Start -> if (direction == LayoutDirection.Ltr) SheetSide.Left else SheetSide.Right
        SheetSide.End -> if (direction == LayoutDirection.Ltr) SheetSide.Right else SheetSide.Left
        else -> this
    }

/** Tailwind w-3/4, with sm:max-w-sm applying from the 640px breakpoint. */
internal fun sheetPanelWidth(viewportWidth: Dp): Dp =
    if (viewportWidth < 640.dp) viewportWidth * .75f else minOf(viewportWidth * .75f, 384.dp)

private val SheetSlideEasing = CubicBezierEasing(.42f, 0f, .58f, 1f)

private fun sheetSlideTween() = tween<IntOffset>(200, easing = SheetSlideEasing)

/** The Base UI popup fades over 200ms while moving 2.5rem from its resting edge. */
object SheetTransitions {
    fun enter(side: SheetSide): EnterTransition = enter(side, 40, LayoutDirection.Ltr)

    fun exit(side: SheetSide): ExitTransition = exit(side, 40, LayoutDirection.Ltr)

    internal fun enter(
        side: SheetSide,
        distance: Int,
        direction: LayoutDirection,
    ): EnterTransition =
        fadeIn(tween(200, easing = SheetSlideEasing)) +
            when (side.physicalSide(direction)) {
                SheetSide.Top -> slideInVertically(sheetSlideTween()) { -distance }
                SheetSide.Bottom -> slideInVertically(sheetSlideTween()) { distance }
                SheetSide.Left -> slideInHorizontally(sheetSlideTween()) { -distance }
                else -> slideInHorizontally(sheetSlideTween()) { distance }
            }

    internal fun exit(
        side: SheetSide,
        distance: Int,
        direction: LayoutDirection,
    ): ExitTransition =
        fadeOut(tween(200, easing = SheetSlideEasing)) +
            when (side.physicalSide(direction)) {
                SheetSide.Top -> slideOutVertically(sheetSlideTween()) { -distance }
                SheetSide.Bottom -> slideOutVertically(sheetSlideTween()) { distance }
                SheetSide.Left -> slideOutHorizontally(sheetSlideTween()) { -distance }
                else -> slideOutHorizontally(sheetSlideTween()) { distance }
            }

    val ScrimEnter: EnterTransition = fadeIn(tween(150))
    val ScrimExit: ExitTransition = fadeOut(tween(150))
}

@Immutable
internal class SheetController(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val modal: Boolean,
    val triggerInteraction: SheetTriggerInteraction,
    val touchActivation: SheetTouchActivation?,
) {
    fun toggle() = onOpenChange(!open)
}

internal class SheetTriggerInteraction {
    var touchRelease: SheetTouchActivation? = null
}

internal class SheetTouchActivation(val restoreCanvasFocus: (() -> Unit)?)

private fun Modifier.trackSheetTriggerTouch(interaction: SheetTriggerInteraction): Modifier =
    pointerInput(interaction) {
        awaitEachGesture {
            val press = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val activation =
                if (press.type == PointerType.Touch) {
                    SheetTouchActivation(captureTouchCanvasFocus(currentEvent))
                } else {
                    null
                }
            val release = waitForUpOrCancellation(pass = PointerEventPass.Initial)
            if (release != null && activation != null) {
                // Click handlers run at Main. Scope the modality to this event's dispatch.
                interaction.touchRelease = activation
                try {
                    awaitPointerEvent(PointerEventPass.Final)
                } finally {
                    interaction.touchRelease = null
                }
            }
        }
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

    @Composable
    internal fun triggerModifier(modifier: Modifier): Modifier =
        modifier.overlayTriggerFocus().trackSheetTriggerTouch(controller.triggerInteraction)
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
 * Full-height + responsive width for horizontal sides
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
    SheetHost(open, onOpenChange, defaultOpen, modal) {
        val scope = this
        Box(modifier = modifier) { scope.content() }
    }
}

@Composable
internal fun SheetHost(
    open: Boolean?,
    onOpenChange: ((Boolean) -> Unit)?,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable SheetScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    var touchActivation by remember { mutableStateOf<SheetTouchActivation?>(null) }
    val triggerInteraction = remember { SheetTriggerInteraction() }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (next) touchActivation = triggerInteraction.touchRelease
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    if (!isOpen && touchActivation != null) {
        // Controlled owners can refuse a close request while the panel still has focus.
        SideEffect { touchActivation = null }
    }
    val controller = SheetController(isOpen, setOpen, modal, triggerInteraction, touchActivation)
    OverlayFocusOwner(open = isOpen, modal = modal) {
        CompositionLocalProvider(LocalSheet provides controller) {
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
            triggerModifier(modifier)
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
    Button(
        text,
        modifier =
            triggerModifier(modifier).semantics {
                stateDescription = if (open) "Expanded" else "Collapsed"
            },
        onClick = toggle,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

/**
 * The sliding panel of a [Sheet]. [side] is Radix `side` (`top` / `bottom` /
 * `left`/`start` / `right`/`end`); default [SheetSide.Right] (= official `"right"`).
 * Enter/exit default to sliding from that edge; override [enter]/[exit]/[scrimEnter]/
 * [scrimExit] like Compose Unstyled. Official [showCloseButton] defaults to
 * [SheetDefaults.ShowCloseButton] (`true`). Composes internal Portal + Overlay.
 */
@Composable
fun SheetScope.SheetContent(
    modifier: Modifier = Modifier,
    side: SheetSide = SheetSide.Right,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    showCloseButton: Boolean = SheetDefaults.ShowCloseButton,
    closeAriaLabel: String = "Close",
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    enter: EnterTransition? = null,
    exit: ExitTransition? = null,
    scrimEnter: EnterTransition = SheetTransitions.ScrimEnter,
    scrimExit: ExitTransition = SheetTransitions.ScrimExit,
    closeIcon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.foreground, variant = IconVariant.Small)
    },
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val direction = LocalLayoutDirection.current
    val viewportWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }
    val distance = with(density) { 40.dp.roundToPx() }
    val resolvedEnter = enter ?: SheetTransitions.enter(side, distance, direction)
    val resolvedExit = exit ?: SheetTransitions.exit(side, distance, direction)
    val alignment =
        when (side) {
            SheetSide.Top -> Alignment.TopCenter
            SheetSide.Bottom -> Alignment.BottomCenter
            SheetSide.Start -> Alignment.CenterStart
            SheetSide.End -> Alignment.CenterEnd
            SheetSide.Left -> AbsoluteAlignment.CenterLeft
            SheetSide.Right -> AbsoluteAlignment.CenterRight
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
        modal.ContentWithInitialFocus(
            contentAlignment = alignment,
            enter = resolvedEnter,
            exit = resolvedExit,
            focusPanelOnOpen = controller.touchActivation != null,
            restoreCanvasFocus = controller.touchActivation?.restoreCanvasFocus,
        ) {
            // Defaults live in Style so an explicit caller width (e.g. Sidebar) wins.
            val panelSize =
                Style {
                    when (side) {
                        SheetSide.Top, SheetSide.Bottom -> width(1f)
                        else -> {
                            width(sheetPanelWidth(viewportWidth))
                            height(1f)
                        }
                    }
                }
            val physicalSide = side.physicalSide(direction)
            val border = currentTheme.colors.border
            val panelModifier =
                modifier.drawWithContent {
                    drawContent()
                    val stroke = 1.dp.toPx()
                    when (physicalSide) {
                        SheetSide.Left -> drawRect(border, Offset(size.width - stroke, 0f), Size(stroke, size.height))
                        SheetSide.Right -> drawRect(border, Offset.Zero, Size(stroke, size.height))
                        SheetSide.Top -> drawRect(border, Offset(0f, size.height - stroke), Size(size.width, stroke))
                        else -> drawRect(border, Offset.Zero, Size(size.width, stroke))
                    }
                }.cssStyleable(styleState, rememberSheetPanelStyle() then panelSize then style).absolutePadding(
                    left = if (physicalSide == SheetSide.Right) 1.dp else 0.dp,
                    right = if (physicalSide == SheetSide.Left) 1.dp else 0.dp,
                    top = if (physicalSide == SheetSide.Bottom) 1.dp else 0.dp,
                    bottom = if (physicalSide == SheetSide.Top) 1.dp else 0.dp,
                )
            CompositionLocalProvider(LocalContentColor provides currentTheme.colors.popoverForeground) {
                Box(modifier = panelModifier) {
                    SheetColumn(
                        Modifier.fillMaxWidth().then(
                            if (physicalSide == SheetSide.Left || physicalSide == SheetSide.Right) {
                                Modifier.fillMaxHeight()
                            } else {
                                Modifier
                            },
                        ),
                        content = content,
                    )
                    if (showCloseButton) {
                        SheetClose(
                            modifier = Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = 12.dp),
                            ariaLabel = closeAriaLabel,
                            icon = closeIcon,
                        )
                    }
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
    side: SheetSide = SheetSide.Right,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    showCloseButton: Boolean = SheetDefaults.ShowCloseButton,
    closeAriaLabel: String = "Close",
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    enter: EnterTransition? = null,
    exit: ExitTransition? = null,
    scrimEnter: EnterTransition = SheetTransitions.ScrimEnter,
    scrimExit: ExitTransition = SheetTransitions.ScrimExit,
    closeIcon: @Composable () -> Unit = {
        Icon(X, tint = currentTheme.colors.foreground, variant = IconVariant.Small)
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
    enter: EnterTransition = SheetTransitions.ScrimEnter,
    exit: ExitTransition = SheetTransitions.ScrimExit,
) {
    val modal =
        LocalSheetTeleportScope.current
            ?: error("SheetOverlay must be used within SheetPortal { }")
    modal.Overlay(color = Color.Black.copy(alpha = .1f), enter = enter, exit = exit)
}

/** Structural header slot matching shadcn's `SheetHeader`. */
@Composable
fun SheetHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    SheetColumn(
        modifier = modifier.styleable(styleState, rememberSheetRegionStyle() then style),
        gap = 2.dp,
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
        variant = TextVariant.TitleSmall,
        textStyle =
            TextStyle(
                color = currentTheme.colors.foreground,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
            ),
        style = style,
        text = text,
    )
}

@Composable
fun SheetDescription(text: String) {
    SheetDescription { text }
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
        variant = TextVariant.BodyMedium,
        textStyle =
            TextStyle(
                color = currentTheme.colors.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
            ),
        style = style,
        text = text,
    )
}

/** Stacked actions at the bottom of a [Sheet], with automatic top margin, 16dp padding and 8dp gap. */
@Composable
fun SheetFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    SheetColumn(
        modifier = modifier.sheetAutoMargin().styleable(styleState, rememberSheetRegionStyle() then style),
        gap = 8.dp,
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
        Icon(X, tint = currentTheme.colors.foreground, variant = IconVariant.Small)
    },
) {
    val controller = sheetController()
    Button(
        modifier = modifier.size(28.dp),
        onClick = { controller.onOpenChange(false) },
        disabled = disabled,
        variant = ButtonVariant.Ghost,
        size = ButtonSize.IconSm,
        label = ariaLabel,
    ) {
        icon()
    }
}
