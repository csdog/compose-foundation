@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuCheckboxItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemIndicator
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
import com.toutiaoyan.compose.ui.components.dropdownmenu.LocalDropdownMenuItemChecked
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuLabel
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuRadioGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuRadioGroupScope
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuRadioItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuScope
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuGroup
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSeparator
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuShortcut
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSub
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSubContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSubScope
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSubTrigger
import com.toutiaoyan.compose.ui.components.overlay.PointPositionProvider
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.rememberMenuPanelStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

@Immutable
internal class ContextMenuController(
    val open: Boolean,
    val lastPoint: IntOffset,
    val onOpenChange: (Boolean) -> Unit,
    val onPoint: (IntOffset) -> Unit,
    val modal: Boolean,
)

private val LocalContextMenu = staticCompositionLocalOf<ContextMenuController?> { null }

@Composable
private fun contextMenuController(): ContextMenuController =
    checkNotNull(LocalContextMenu.current) {
        "ContextMenuTrigger / ContextMenuContent must be used inside ContextMenu { … }"
    }

/** Reads the nearest [ContextMenu] scope. Matches Radix `useContextMenuContext`. */
@Composable
fun contextMenuState(): ContextMenuScope = ContextMenuScope(contextMenuController())

/** Slot receiver for [ContextMenuTrigger] / [ContextMenuContent]. */
class ContextMenuScope internal constructor(
    private val controller: ContextMenuController,
) {
    val open: Boolean get() = controller.open
}

/**
 * Right-click-triggered menu at the cursor. Matches real shadcn/ui's
 * `context-menu.tsx`:
 *
 * ```
 * ContextMenu
 * ├── ContextMenuTrigger
 * └── ContextMenuContent
 *     ├── ContextMenuItem
 *     ├── ContextMenuSeparator
 *     └── ContextMenuLabel
 * ```
 *
 * Row composables share [DropdownMenuScope] visuals (same as real shadcn sharing
 * dropdown-menu styles). Pass [open] + [onOpenChange] for controlled state.
 *
 * Usage:
 * ```
 * ContextMenu {
 *     ContextMenuTrigger("Right-click me")
 *     ContextMenuContent {
 *         ContextMenuItem("Copy", onClick = {})
 *         ContextMenuSeparator()
 *         ContextMenuItem("Inspect", onClick = {})
 *     }
 * }
 * ```
 */
@Composable
fun ContextMenu(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable ContextMenuScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    var lastPoint by remember { mutableStateOf(IntOffset.Zero) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val controller =
        ContextMenuController(
            open = isOpen,
            lastPoint = lastPoint,
            onOpenChange = setOpen,
            onPoint = { lastPoint = it },
            modal = modal,
        )
    CompositionLocalProvider(LocalContextMenu provides controller) {
        Box(modifier = modifier) {
            ContextMenuScope(controller).content()
        }
    }
}

/** Right-click target for the parent [ContextMenu]. Matches shadcn's `ContextMenuTrigger`. */
@Composable
fun ContextMenuScope.ContextMenuTrigger(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val controller = contextMenuController()
    val onOpenChange = controller.onOpenChange
    val onPoint = controller.onPoint
    Box(
        modifier =
            modifier
                .semantics {
                    stateDescription = if (controller.open) "Expanded" else "Collapsed"
                }
                .then(
                    if (disabled) {
                        Modifier
                    } else {
                        Modifier.pointerInput(onOpenChange, onPoint) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                                        val position = event.changes.first().position
                                        onPoint(IntOffset(position.x.toInt(), position.y.toInt()))
                                        onOpenChange(true)
                                    }
                                }
                            }
                        }
                    },
                ),
    ) {
        content()
    }
}

/** String-label overload of [ContextMenuTrigger]. */
@Composable
fun ContextMenuScope.ContextMenuTrigger(
    text: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
) {
    ContextMenuTrigger(modifier = modifier, disabled = disabled) {
        Text { text }
    }
}

/**
 * The menu panel of a [ContextMenu], opened at the last right-click point.
 * Base Positioner defaults: `side` right, `align` start, `sideOffset` 0, `alignOffset` **4**
 * (applied as pixel nudge from the cursor). Compose [ContextMenuItem] /
 * [ContextMenuLabel] / [ContextMenuSeparator] inside.
 */
@Composable
fun ContextMenuScope.ContextMenuContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    // Base ContextMenuContent Positioner defaults.
    sideOffset: Dp = 0.dp,
    alignOffset: Dp = 4.dp,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    val controller = contextMenuController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = controller.open
    val scope =
        remember(controller.onOpenChange) {
            DropdownMenuScope(onOpenChange = { if (!it) controller.onOpenChange(false) })
        }
    val density = LocalDensity.current
    val point =
        remember(controller.lastPoint, sideOffset, alignOffset, density) {
            val sidePx = with(density) { sideOffset.roundToPx() }
            val alignPx = with(density) { alignOffset.roundToPx() }
            // Base default side=right → main axis X; alignOffset on cross axis (Y).
            IntOffset(controller.lastPoint.x + sidePx, controller.lastPoint.y + alignPx)
        }

    if (visibleState.currentState || visibleState.targetState) {
        ContextMenuPortal {
            Popup(
                popupPositionProvider = PointPositionProvider(point),
                onDismissRequest = { controller.onOpenChange(false) },
                properties =
                    PopupProperties(
                        focusable = controller.modal,
                        dismissOnBackPress = controller.modal,
                        dismissOnClickOutside = dismissOnClickOutside && visibleState.targetState,
                    ),
            ) {
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = enter,
                    exit = exit,
                ) {
                    Column(
                        modifier =
                            modifier.styleable(
                                styleState,
                                rememberMenuPanelStyle() then style,
                            ),
                    ) {
                        scope.content()
                    }
                }
            }
        }
    }
}

/** Teleport host for [ContextMenuContent]. Matches shadcn's `ContextMenuPortal`. */
@Composable
fun ContextMenuPortal(content: @Composable () -> Unit) {
    content()
}

/** Alias for [DropdownMenuItem] matching shadcn's `ContextMenuItem`. */
@Composable
fun DropdownMenuScope.ContextMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    destructive: Boolean = false,
    variant: DropdownMenuItemVariant = DropdownMenuItemVariant.Default,
    shortcut: String? = null,
    inset: Boolean = false,
    onSelect: (() -> Boolean)? = null,
    textValue: String? = null,
    style: Style = Style,
) {
    DropdownMenuItem(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        destructive = destructive,
        variant = variant,
        shortcut = shortcut,
        inset = inset,
        onSelect = onSelect,
        textValue = textValue,
        style = style,
    )
}

/** Alias for [DropdownMenuLabel] matching shadcn's `ContextMenuLabel`. */
@Composable
fun DropdownMenuScope.ContextMenuLabel(
    text: String,
    modifier: Modifier = Modifier,
    inset: Boolean = false,
    style: Style = Style,
) {
    DropdownMenuLabel(text = text, modifier = modifier, inset = inset, style = style)
}

@Composable
@JvmName("ContextMenuLabelLambda")
@OverloadResolutionByLambdaReturnType
fun DropdownMenuScope.ContextMenuLabel(
    modifier: Modifier = Modifier,
    inset: Boolean = false,
    style: Style = Style,
    text: () -> String,
) {
    ContextMenuLabel(text = text(), modifier = modifier, inset = inset, style = style)
}

/** Alias for [DropdownMenuSeparator] matching shadcn's `ContextMenuSeparator`. */
@Composable
fun ContextMenuSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    DropdownMenuSeparator(modifier = modifier, style = style)
}

/** Alias for [DropdownMenuGroup] matching shadcn's `ContextMenuGroup`. */
@Composable
fun ContextMenuGroup(
    modifier: Modifier = Modifier,
    heading: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenuGroup(modifier = modifier, heading = heading, content = content)
}

/** Alias for [DropdownMenuCheckboxItem] matching shadcn's `ContextMenuCheckboxItem`. */
@Composable
fun DropdownMenuScope.ContextMenuCheckboxItem(
    label: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    indeterminate: Boolean = false,
    onSelect: (() -> Boolean)? = null,
    textValue: String? = null,
    style: Style = Style,
    onCheckedChange: (Boolean) -> Unit,
) {
    DropdownMenuCheckboxItem(
        label = label,
        checked = checked,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        indeterminate = indeterminate,
        onSelect = onSelect,
        textValue = textValue,
        style = style,
        onCheckedChange = onCheckedChange,
    )
}

/** Alias for [DropdownMenuRadioGroup] matching shadcn's `ContextMenuRadioGroup`. */
@Composable
fun DropdownMenuScope.ContextMenuRadioGroup(
    value: String,
    onValueChange: (String) -> Unit,
    content: @Composable DropdownMenuRadioGroupScope.() -> Unit,
) {
    DropdownMenuRadioGroup(value = value, onValueChange = onValueChange, content = content)
}

/** Alias for [DropdownMenuRadioItem] matching shadcn's `ContextMenuRadioItem`. */
@Composable
fun DropdownMenuRadioGroupScope.ContextMenuRadioItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    onSelect: (() -> Boolean)? = null,
    textValue: String? = null,
    style: Style = Style,
) {
    DropdownMenuRadioItem(
        value = value,
        label = label,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        onSelect = onSelect,
        textValue = textValue,
        style = style,
    )
}

/** Alias for [DropdownMenuSub] matching shadcn's `ContextMenuSub`. */
@Composable
fun DropdownMenuScope.ContextMenuSub(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    defaultOpen: Boolean = false,
    content: @Composable DropdownMenuSubScope.() -> Unit,
) {
    DropdownMenuSub(
        open = open,
        onOpenChange = onOpenChange,
        defaultOpen = defaultOpen,
        content = content,
    )
}

/** Alias for [DropdownMenuSubTrigger] matching shadcn's `ContextMenuSubTrigger`. */
@Composable
fun DropdownMenuSubScope.ContextMenuSubTrigger(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    inset: Boolean = false,
    textValue: String? = null,
    style: Style = Style,
) {
    DropdownMenuSubTrigger(
        label = label,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        inset = inset,
        textValue = textValue,
        style = style,
    )
}

/** Alias for [DropdownMenuSubContent] matching shadcn's `ContextMenuSubContent`.
 * Base wraps [ContextMenuContent] (`alignOffset` **4**, `sideOffset` 0) — not Dropdown Sub defaults.
 */
@Composable
fun DropdownMenuSubScope.ContextMenuSubContent(
    modifier: Modifier = Modifier,
    sideOffset: Dp = 0.dp,
    alignOffset: Dp = 4.dp,
    dismissOnClickOutside: Boolean = true,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    DropdownMenuSubContent(
        modifier = modifier,
        sideOffset = sideOffset,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        content = content,
    )
}

/** Alias for [DropdownMenuShortcut] matching shadcn's `ContextMenuShortcut`. */
@Composable
fun ContextMenuShortcut(
    text: String,
    modifier: Modifier = Modifier,
) {
    DropdownMenuShortcut(text = text, modifier = modifier)
}

@Composable
@JvmName("ContextMenuShortcutLambda")
@OverloadResolutionByLambdaReturnType
fun ContextMenuShortcut(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    ContextMenuShortcut(text = text(), modifier = modifier)
}

/** Alias for [DropdownMenuItemIndicator]. Base UI does not export ContextMenuItemIndicator. */
@Composable
internal fun ContextMenuItemIndicator(
    checked: Boolean =
        checkNotNull(LocalDropdownMenuItemChecked.current) {
            "ContextMenuItemIndicator must be used inside ContextMenuCheckboxItem, or pass checked="
        },
    modifier: Modifier = Modifier,
) {
    DropdownMenuItemIndicator(checked = checked, modifier = modifier)
}
