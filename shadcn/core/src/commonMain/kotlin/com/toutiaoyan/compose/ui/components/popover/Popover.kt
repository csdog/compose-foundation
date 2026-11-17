@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlinx.coroutines.launch

internal class PopoverController(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val modal: Boolean,
    val focus: PopoverFocusState,
) {
    fun toggle() = onOpenChange(!open)

    fun dismissWithEscape() {
        focus.restoreOnDismissal = true
        onOpenChange(false)
    }

    fun dismissAfterTab(direction: FocusDirection) {
        focus.restoreOnDismissal = true
        focus.exitDirection = direction
        onOpenChange(false)
    }

    fun dismissFromOutside() {
        focus.restoreOnDismissal = false
        onOpenChange(false)
    }
}

internal class PopoverFocusState {
    var presented by mutableStateOf(false)
    var openedByTouch = false
    var restoreOnDismissal = false
    var focusAtTrigger by mutableStateOf(false)
    var exitDirection: FocusDirection? by mutableStateOf(null)
    var triggerPresses = 0
    var returnFocus: (() -> Boolean)? = null
    var restoreTouchHost: (() -> Unit)? = null
}

private val LocalPopover = staticCompositionLocalOf<PopoverController?> { null }

@Composable
private fun popoverController(): PopoverController =
    checkNotNull(LocalPopover.current) {
        "PopoverTrigger / PopoverContent must be used inside Popover { … }"
    }

/** Reads the nearest [Popover] scope. Matches Radix `usePopoverContext`. */
@Composable
fun popoverState(): PopoverScope = PopoverScope(popoverController())

/**
 * Slot receiver for [PopoverTrigger] / [PopoverContent].
 *
 * [open] / [toggle] match the state Radix exposes on the trigger
 * (`data-state="open"`).
 */
class PopoverScope internal constructor(
    private val controller: PopoverController,
) {
    val open: Boolean get() = controller.open

    val toggle: () -> Unit get() = controller::toggle
}

/**
 * A click-triggered anchored panel matching real shadcn/ui's `popover.tsx`:
 *
 * ```
 * Popover
 * ├── PopoverTrigger
 * └── PopoverContent
 *     ├── PopoverHeader
 *     │   ├── PopoverTitle
 *     │   └── PopoverDescription
 *     └── …
 * ```
 *
 * Pass [open] + [onOpenChange] for controlled state; omit both (or pass
 * [defaultOpen]) for uncontrolled, matching Radix `open` / `defaultOpen` /
 * `onOpenChange`. Outside click and Escape close via [onOpenChange]`(false)`.
 *
 * [modal] defaults to `true` for compatibility. Pass `false` to let outside pointer
 * interactions reach the owner. On Web, Desktop and iOS the panel can take keyboard
 * focus in both modes. This flag does not lock document scrolling.
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 * Popover(open = open, onOpenChange = { open = it }) {
 *     PopoverTrigger("Open popover")
 *     PopoverContent("Place content for the popover here.")
 * }
 * ```
 */
@Composable
fun Popover(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable PopoverScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val focus = remember { PopoverFocusState() }
    val ownerFocus = LocalFocusManager.current
    var wasPresented by remember { mutableStateOf(false) }
    LaunchedEffect(isOpen, focus.presented) {
        if (focus.presented) wasPresented = true
        if (!isOpen && !focus.presented && wasPresented) {
            withFrameNanos { }
            if (modal || focus.restoreOnDismissal) {
                focus.restoreTouchHost?.invoke()
                val restored = focus.returnFocus?.invoke() == true
                val direction = focus.exitDirection
                if (restored && direction != null) ownerFocus.moveFocus(direction)
            }
            focus.exitDirection = null
            focus.focusAtTrigger = false
            focus.restoreTouchHost = null
            focus.restoreOnDismissal = false
            wasPresented = false
        }
    }
    LaunchedEffect(isOpen, focus.focusAtTrigger) {
        if (isOpen && focus.focusAtTrigger) {
            withFrameNanos { }
            focus.restoreTouchHost?.invoke()
            focus.returnFocus?.invoke()
        }
    }
    val controller = PopoverController(open = isOpen, onOpenChange = setOpen, modal = modal, focus = focus)
    CompositionLocalProvider(LocalPopover provides controller) {
        Box(
            modifier =
                modifier.onKeyEvent { event ->
                    // Escape can reach the owner when a nested layer leaves it focused.
                    if (!modal && isOpen && event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                        controller.dismissWithEscape()
                        true
                    } else {
                        false
                    }
                },
        ) {
            PopoverScope(controller).content()
        }
    }
}

/**
 * Toggles the parent [Popover]. Matches real shadcn's `PopoverTrigger`
 * (unstyled button primitive). Put any chrome inside — typically a
 * [com.toutiaoyan.compose.ui.components.button.Button].
 *
 * When the child is already clickable (e.g. a Button wired to [PopoverScope.toggle]),
 * that inner click wins; this wrapper still covers the rest of the hit target.
 */
@Composable
fun PopoverScope.PopoverTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = popoverController()
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)
    val resolvedEnabled = enabled && !disabled
    val triggerFocus = remember { FocusRequester() }
    val childFocus = remember { FocusRequester() }
    val returnFocus =
        remember(triggerFocus, childFocus) {
            // Prefer a supplied Button's focus target; an unstyled text trigger
            // instead uses the wrapper's own clickable focus target.
            { childFocus.requestFocus() || triggerFocus.requestFocus() }
        }
    DisposableEffect(controller.focus, returnFocus) {
        onDispose { if (controller.focus.returnFocus === returnFocus) controller.focus.returnFocus = null }
    }
    Box(
        modifier =
            modifier
                .focusRequester(triggerFocus)
                .onFocusChanged { if (it.hasFocus) controller.focus.returnFocus = returnFocus }
                .pointerInput(controller.focus, returnFocus) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.type == PointerEventType.Press) {
                                controller.focus.triggerPresses++
                                val touch = event.changes.any { it.type == PointerType.Touch }
                                controller.focus.openedByTouch = touch
                                controller.focus.restoreTouchHost = if (touch) popoverTouchHostFocus(event) else null
                                controller.focus.returnFocus = returnFocus
                            }
                        }
                    }
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && (event.key == Key.Enter || event.key == Key.Spacebar)) {
                        controller.focus.openedByTouch = false
                        controller.focus.restoreTouchHost = null
                    }
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Tab &&
                        controller.open && controller.focus.focusAtTrigger
                    ) {
                        if (event.isShiftPressed) {
                            controller.dismissAfterTab(FocusDirection.Previous)
                        } else {
                            controller.focus.focusAtTrigger = false
                        }
                        true
                    } else {
                        false
                    }
                }
                .keyboardInputMode()
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
                .focusRequester(childFocus)
                .focusGroup()
                .styleable(styleState, style),
    ) {
        content()
    }
}

/** String-label overload of [PopoverTrigger], rendering a [Button] wired to [toggle]. */
@Composable
fun PopoverScope.PopoverTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    PopoverTrigger(modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
        Button(text, onClick = toggle, enabled = enabled, disabled = disabled, variant = variant, size = size)
    }
}

/**
 * The anchored panel of a [Popover]. Matches real shadcn's `PopoverContent`
 * (`w-72 rounded-md border bg-popover p-4 shadow-md`, `align` center, `sideOffset` 4).
 *
 * Panel chrome is Style-driven — override with [style]. [placement] is Radix
 * `side`. [align] defaults to [PopupAlign.Center] like official.
 */
@Composable
fun PopoverScope.PopoverContent(
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    side: PopupPlacement? = null,
    align: PopupAlign = PopupAlign.Center,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 0.dp,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val controller = popoverController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val panelFocus = remember { FocusRequester() }
    var panelHasFocus by remember { mutableStateOf(false) }
    var childHasFocus by remember { mutableStateOf(false) }
    var initialFocusGiven by remember { mutableStateOf(false) }
    val foreground = currentTheme.colors.popoverForeground
    val nova = currentTheme.activePreset == StylePreset.Nova
    val bodyTypography =
        if (nova) {
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                lineHeightStyle =
                    LineHeightStyle(
                        LineHeightStyle.Alignment.Center,
                        LineHeightStyle.Trim.None,
                    ),
            )
        } else {
            LocalDefaultTextStyle.current
        }
    val properties =
        popoverPopupProperties(
            controller.modal,
            controller.open,
            dismissOnClickOutside,
            focusPanel = !controller.focus.focusAtTrigger,
        )
    val focusable = properties.focusable
    val latestController by rememberUpdatedState(controller)
    val dismissalScope = rememberCoroutineScope()
    PopoverPortal {
        PopoverPopup(
            open = controller.open,
            onDismissRequest = {
                if (controller.modal) {
                    controller.dismissFromOutside()
                } else {
                    // Skiko reports outside press before delivering the same press to the trigger.
                    val presses = controller.focus.triggerPresses
                    dismissalScope.launch {
                        withFrameNanos { }
                        if (latestController.open && presses == controller.focus.triggerPresses) {
                            latestController.dismissFromOutside()
                        }
                    }
                }
            },
            onWindowEscape = controller::dismissWithEscape,
            placement = side ?: placement,
            offset = sideOffset,
            align = align,
            alignOffset = alignOffset,
            properties = properties,
            enter = enter,
            exit = exit,
        ) {
            val focusManager = LocalFocusManager.current
            DisposableEffect(controller.focus, focusable) {
                if (focusable) controller.focus.presented = true
                onDispose { if (focusable) controller.focus.presented = false }
            }
            LaunchedEffect(controller.open, focusable) {
                if (controller.open && focusable) {
                    withFrameNanos { }
                    controller.focus.restoreTouchHost?.invoke()
                    if (!panelHasFocus && panelFocus.requestFocus() && !controller.focus.openedByTouch) {
                        // Enter searches descendants and leaves focus on the panel if
                        // none can take it. Touch avoids opening a virtual keyboard.
                        focusManager.moveFocus(FocusDirection.Enter)
                    }
                    initialFocusGiven = true
                } else {
                    initialFocusGiven = false
                }
            }
            LaunchedEffect(controller.open, controller.focus.exitDirection) {
                if (controller.open && controller.focus.exitDirection != null) {
                    // A controlled owner can decline the dismissal request.
                    withFrameNanos { }
                    if (latestController.open) {
                        controller.focus.exitDirection = null
                        panelFocus.requestFocus()
                    }
                }
            }
            val allowTabExit = controller.open && !controller.modal && focusable && initialFocusGiven
            CompositionLocalProvider(
                LocalContentColor provides foreground,
                LocalDefaultTextStyle provides bodyTypography,
            ) {
                Box(
                    modifier =
                        Modifier.keyboardInputMode().onKeyEvent { event ->
                            when {
                                !controller.open || event.type != KeyEventType.KeyDown -> false
                                event.key == Key.Escape -> {
                                    controller.dismissWithEscape()
                                    true
                                }
                                event.key == Key.Tab && event.isShiftPressed &&
                                    allowTabExit && panelHasFocus && !childHasFocus -> {
                                    controller.focus.focusAtTrigger = true
                                    true
                                }
                                else -> false
                            }
                        }.focusRequester(panelFocus)
                            .onFocusChanged {
                                panelHasFocus = it.hasFocus
                                childHasFocus = it.hasFocus && !it.isFocused
                            }.then(modifier).focusable(enabled = focusable).pointerInput(controller.focus) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        if (event.type == PointerEventType.Press) {
                                            controller.focus.focusAtTrigger = false
                                        }
                                    }
                                }
                            },
                    propagateMinConstraints = true,
                ) {
                    PopoverTabBoundary(allowTabExit && childHasFocus) {
                        controller.focus.focusAtTrigger = true
                    }
                    Column(
                        modifier = Modifier.styleable(styleState, rememberPopoverContentStyle() then style),
                        verticalArrangement = Arrangement.spacedBy(if (nova) 10.dp else 0.dp),
                        content = content,
                    )
                    PopoverTabBoundary(allowTabExit) { controller.dismissAfterTab(FocusDirection.Next) }
                }
            }
        }
    }
}

/** Keeps tab order inside the panel connected to the trigger's owner. */
@Composable
private fun PopoverTabBoundary(
    enabled: Boolean,
    onFocus: () -> Unit,
) {
    Box(
        Modifier.size(0.dp)
            .semantics { hideFromAccessibility() }
            .onFocusChanged { if (enabled && it.isFocused) onFocus() }
            .focusable(enabled),
    )
}

/** String-body overload of [PopoverContent]. */
@Composable
fun PopoverScope.PopoverContent(
    text: String,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    side: PopupPlacement? = null,
    align: PopupAlign = PopupAlign.Center,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 0.dp,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
) {
    PopoverContent(
        modifier = modifier,
        placement = placement,
        side = side,
        align = align,
        style = style,
        dismissOnClickOutside = dismissOnClickOutside,
        sideOffset = sideOffset,
        alignOffset = alignOffset,
        enter = enter,
        exit = exit,
    ) {
        Text { text }
    }
}

/**
 * Convenience: [title] + optional [description] as [PopoverHeader], then [content].
 * Distinguished from the string-body overload by the trailing [content] lambda.
 */
@Composable
fun PopoverScope.PopoverContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    side: PopupPlacement? = null,
    align: PopupAlign = PopupAlign.Center,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 0.dp,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable ColumnScope.() -> Unit,
) {
    PopoverContent(
        modifier = modifier,
        placement = placement,
        side = side,
        align = align,
        style = style,
        dismissOnClickOutside = dismissOnClickOutside,
        sideOffset = sideOffset,
        alignOffset = alignOffset,
        enter = enter,
        exit = exit,
    ) {
        PopoverHeader(title, description = description)
        content()
    }
}

/** Teleport host for [PopoverContent]. Official does not export PopoverPortal. */
@Composable
internal fun PopoverPortal(content: @Composable () -> Unit) {
    content()
}

/**
 * Positioning wrapper distinct from [PopoverTrigger].
 * Base UI / base-nova does **not** export `PopoverAnchor` — kept internal only.
 */
@Composable
internal fun PopoverScope.PopoverAnchor(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) { content() }
}

/** Structural header slot matching shadcn's `PopoverHeader`. */
@Composable
fun PopoverHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        verticalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) {
                    2.dp
                } else {
                    twSpacing.xs
                },
            ),
        content = content,
    )
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun PopoverHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    PopoverHeader(modifier = modifier, style = style) {
        PopoverTitle(title)
        if (description != null) {
            PopoverDescription(description)
        }
    }
}

/** String overload of [PopoverTitle]. */
@Composable
fun PopoverTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    PopoverTitle(modifier = modifier, style = style) { text }
}

@Composable
@JvmName("PopoverTitleString")
@OverloadResolutionByLambdaReturnType
fun PopoverTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier.semantics { heading() },
        variant = TextVariant.TitleSmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                )
            } else {
                TextStyle.Default
            },
        style = style,
        text = text,
    )
}

/** String overload of [PopoverDescription]. */
@Composable
fun PopoverDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    PopoverDescription(modifier = modifier, style = style) { text }
}

@Composable
@JvmName("PopoverDescriptionString")
@OverloadResolutionByLambdaReturnType
fun PopoverDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                TextColors.Muted.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                )
            } else {
                TextColors.Muted
            },
        style = style,
        text = text,
    )
}
