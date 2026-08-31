@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import kotlinx.coroutines.delay
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.styles.rememberMenuItemStyle
import com.toutiaoyan.compose.ui.styles.rememberMenuLabelStyle
import com.toutiaoyan.compose.ui.styles.rememberMenuPanelStyle
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twSpacing
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Official shadcn `DropdownMenuItem` `variant`. [Destructive] is the same as [destructive] `true`. */
enum class DropdownMenuItemVariant { Default, Destructive }

@Immutable
internal class DropdownMenuController(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val modal: Boolean,
) {
    fun toggle() = onOpenChange(!open)

    fun dismiss() = onOpenChange(false)
}

private val LocalDropdownMenu = staticCompositionLocalOf<DropdownMenuController?> { null }

internal val LocalDropdownMenuItemChecked = staticCompositionLocalOf<Boolean?> { null }

internal val LocalDropdownMenuItemIndeterminate = staticCompositionLocalOf { false }

@Composable
private fun dropdownMenuController(): DropdownMenuController =
    checkNotNull(LocalDropdownMenu.current) {
        "DropdownMenuTrigger / DropdownMenuContent must be used inside DropdownMenu { … }"
    }

/** Reads the nearest [DropdownMenu] scope. Matches Radix `useDropdownMenuContext`. */
@Composable
fun dropdownMenuState(): DropdownMenuScope {
    val ctrl = dropdownMenuController()
    return DropdownMenuScope(onOpenChange = ctrl.onOpenChange, open = ctrl.open)
}

/** Extra start padding so a plain row lines up with [DropdownMenuCheckboxItem] / RadioItem labels. Matches shadcn `inset`. */
@Composable
private fun Modifier.dropdownMenuInset(inset: Boolean): Modifier {
    val extra = currentTheme.icons.smallSize + twSpacing.sm
    return if (inset) padding(start = extra) else this
}

/**
 * Slot receiver for [DropdownMenuTrigger] / [DropdownMenuContent] and the
 * row composables ([DropdownMenuItem], [DropdownMenuLabel], …).
 *
 * [ContextMenu] constructs this directly so it can reuse the same row API.
 */
class DropdownMenuScope internal constructor(
    internal val onOpenChange: (Boolean) -> Unit,
    val open: Boolean = true,
) {
    val toggle: () -> Unit get() = { onOpenChange(!open) }

    internal fun dismiss() = onOpenChange(false)
}

/**
 * An anchored list of actions. Matches real shadcn/ui's `dropdown-menu.tsx`:
 *
 * ```
 * DropdownMenu
 * ├── DropdownMenuTrigger
 * └── DropdownMenuContent
 *     ├── DropdownMenuLabel
 *     ├── DropdownMenuSeparator
 *     ├── DropdownMenuItem
 *     ├── DropdownMenuCheckboxItem
 *     ├── DropdownMenuRadioGroup
 *     │   └── DropdownMenuRadioItem
 *     └── DropdownMenuSub
 *         ├── DropdownMenuSubTrigger
 *         └── DropdownMenuSubContent
 * ```
 *
 * Pass [open] + [onOpenChange] for controlled state; omit both (or pass
 * [defaultOpen]) for uncontrolled. Item clicks and outside/Escape close via
 * [onOpenChange]`(false)`.
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 * DropdownMenu(open = open, onOpenChange = { open = it }) {
 *     DropdownMenuTrigger("Open")
 *     DropdownMenuContent {
 *         DropdownMenuLabel("My Account")
 *         DropdownMenuSeparator()
 *         DropdownMenuItem("Profile", onClick = {})
 *         DropdownMenuItem("Log out", onClick = {}, destructive = true)
 *     }
 * }
 * ```
 */
@Composable
fun DropdownMenu(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val controller = DropdownMenuController(open = isOpen, onOpenChange = setOpen, modal = modal)
    CompositionLocalProvider(LocalDropdownMenu provides controller) {
        Box(modifier = modifier) {
            DropdownMenuScope(onOpenChange = setOpen, open = isOpen).content()
        }
    }
}

/**
 * Toggles the parent [DropdownMenu]. Matches real shadcn's `DropdownMenuTrigger`.
 */
@Composable
fun DropdownMenuScope.DropdownMenuTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = dropdownMenuController()
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

/** String-label overload of [DropdownMenuTrigger], rendering a [Button] wired to [toggle]. */
@Composable
fun DropdownMenuScope.DropdownMenuTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    DropdownMenuTrigger(modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
        Button(text, onClick = toggle, enabled = enabled, disabled = disabled, variant = variant, size = size)
    }
}

/**
 * The anchored panel of a [DropdownMenu]
 * (`min-w-56 rounded-md border bg-popover p-1 shadow-md`).
 */
@Composable
fun DropdownMenuScope.DropdownMenuContent(
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    side: PopupPlacement? = null,
    style: Style = Style,
    dismissOnClickOutside: Boolean = true,
    sideOffset: Dp = 4.dp,
    align: PopupAlign = PopupAlign.Start,
    alignOffset: Dp = 0.dp,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    val controller = dropdownMenuController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val scope = remember(controller.onOpenChange, controller.open) {
        DropdownMenuScope(onOpenChange = controller.onOpenChange, open = controller.open)
    }
    DropdownMenuPortal {
        AnchoredPopup(
            open = controller.open,
            onDismissRequest = { controller.dismiss() },
            placement = side ?: placement,
            offset = sideOffset,
            align = align,
            alignOffset = alignOffset,
            dismissOnClickOutside = dismissOnClickOutside,
            focusable = controller.modal,
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

/** Teleport host for [DropdownMenuContent]. Matches shadcn's `DropdownMenuPortal`. */
@Composable
fun DropdownMenuPortal(content: @Composable () -> Unit) {
    content()
}

/** One clickable row in a [DropdownMenu]/[com.toutiaoyan.compose.ui.components.contextmenu.ContextMenu]/[com.toutiaoyan.compose.ui.components.menubar.Menubar]. [onSelect] is Radix `onSelect` (return true to prevent the menu from closing). [textValue] is Radix `textValue` (accessible name; omit to use [label]). */
@Composable
fun DropdownMenuScope.DropdownMenuItem(
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
    content: @Composable () -> Unit = {
        val labelText = @Composable {
            Text(
                variant = TextVariant.BodySmall,
                textStyle =
                    when {
                        destructive || variant == DropdownMenuItemVariant.Destructive ->
                            TextStyle(color = currentTheme.colors.destructive)
                        !(enabled && !disabled) -> TextColors.Muted
                        else -> TextStyle(color = currentTheme.colors.popoverForeground)
                    },
            ) { label }
        }
        if (shortcut == null) {
            labelText()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                labelText()
                DropdownMenuShortcut(shortcut)
            }
        }
    },
) {
    val resolvedEnabled = enabled && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription = textValue ?: label
                    if (!resolvedEnabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = {
                        onClick()
                        // Radix `onSelect` preventDefault: true keeps the menu open.
                        if (onSelect?.invoke() != true) dismiss()
                    },
                )
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberMenuItemStyle() then style,
                )
                .dropdownMenuInset(inset),
    ) {
        content()
    }
}

/** Muted trailing shortcut (e.g. ⌘S). Matches shadcn's `DropdownMenuShortcut`. */
@Composable
fun DropdownMenuShortcut(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        variant = TextVariant.LabelSmall,
        textStyle = TextColors.Muted,
        modifier = modifier,
    ) { text }
}

@Composable
@JvmName("DropdownMenuShortcutLambda")
@OverloadResolutionByLambdaReturnType
fun DropdownMenuShortcut(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    DropdownMenuShortcut(text = text(), modifier = modifier)
}

/**
 * A checkable row that does not dismiss the menu. Matches shadcn's
 * `DropdownMenuCheckboxItem`. [onSelect] is Radix `onSelect` (return true to
 * prevent checking/unchecking).
 */
@Composable
fun DropdownMenuScope.DropdownMenuCheckboxItem(
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
    val resolvedEnabled = enabled && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Row(
        modifier =
            modifier
                .semantics {
                    role = Role.Checkbox
                    contentDescription = textValue ?: label
                    stateDescription =
                        when {
                            indeterminate -> "Indeterminate"
                            checked -> "Checked"
                            else -> "Unchecked"
                        }
                    if (!resolvedEnabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Checkbox,
                    onClick = {
                        // Radix `onSelect` preventDefault: true skips checking/unchecking.
                        if (onSelect?.invoke() == true) return@clickable
                        if (indeterminate) onCheckedChange(true) else onCheckedChange(!checked)
                    },
                )
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberMenuItemStyle() then style,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        CompositionLocalProvider(
            LocalDropdownMenuItemChecked provides checked,
            LocalDropdownMenuItemIndeterminate provides indeterminate,
        ) {
            DropdownMenuItemIndicator()
            Text(
                variant = TextVariant.BodySmall,
                textStyle =
                    if (!enabled) {
                        TextColors.Muted
                    } else {
                        TextStyle(color = currentTheme.colors.popoverForeground)
                    },
            ) { label }
        }
    }
}

/** Check-mark slot for a [DropdownMenuCheckboxItem]. Inlined by official; not exported. */
@Composable
internal fun DropdownMenuItemIndicator(
    checked: Boolean = checkNotNull(LocalDropdownMenuItemChecked.current) {
        "DropdownMenuItemIndicator must be used inside DropdownMenuCheckboxItem, or pass checked="
    },
    modifier: Modifier = Modifier,
    indeterminate: Boolean = LocalDropdownMenuItemIndeterminate.current,
) {
    DropdownMenuItemIndicator(modifier = modifier) {
        when {
            indeterminate ->
                Box(
                    modifier =
                        Modifier
                            .size(width = 10.dp, height = 2.dp)
                            .background(currentTheme.colors.popoverForeground),
                )
            checked ->
                Icon(
                    Check,
                    tint = currentTheme.colors.popoverForeground,
                    variant = IconVariant.Small,
                )
        }
    }
}

/** Slot form of [DropdownMenuItemIndicator] for custom marks (e.g. radio dots). */
@Composable
internal fun DropdownMenuItemIndicator(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.size(currentTheme.icons.smallSize),
        contentAlignment = contentAlignment,
        content = content,
    )
}

@Immutable
private class DropdownMenuRadioController(
    val value: String,
    val onValueChange: (String) -> Unit,
)

private val LocalDropdownMenuRadio = staticCompositionLocalOf<DropdownMenuRadioController?> { null }

/** Selected value of the nearest [DropdownMenuRadioGroup]. */
class DropdownMenuRadioGroupState(
    val value: String,
    val onValueChange: (String) -> Unit,
)

/** Reads the nearest [DropdownMenuRadioGroup]. Matches Radix radio-group context. */
@Composable
fun dropdownMenuRadioGroupState(): DropdownMenuRadioGroupState {
    val radio =
        checkNotNull(LocalDropdownMenuRadio.current) {
            "dropdownMenuRadioGroupState() must be used inside DropdownMenuRadioGroup { … }"
        }
    return DropdownMenuRadioGroupState(value = radio.value, onValueChange = radio.onValueChange)
}

/** Slot receiver for [DropdownMenuRadioItem]. */
class DropdownMenuRadioGroupScope internal constructor()

/**
 * Exclusive radio group. Matches shadcn's `DropdownMenuRadioGroup`.
 * Items stay open after a click (same as [DropdownMenuCheckboxItem]).
 */
@Composable
fun DropdownMenuScope.DropdownMenuRadioGroup(
    value: String,
    onValueChange: (String) -> Unit,
    content: @Composable DropdownMenuRadioGroupScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalDropdownMenuRadio provides DropdownMenuRadioController(value, onValueChange),
    ) {
        DropdownMenuRadioGroupScope().content()
    }
}

/**
 * One radio row. Must sit inside [DropdownMenuRadioGroup]. Matches shadcn's
 * `DropdownMenuRadioItem`.
 */
@Composable
fun DropdownMenuRadioGroupScope.DropdownMenuRadioItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    onSelect: (() -> Boolean)? = null,
    textValue: String? = null,
    style: Style = Style,
) {
    val radio =
        checkNotNull(LocalDropdownMenuRadio.current) {
            "DropdownMenuRadioItem must be used inside DropdownMenuRadioGroup { … }"
        }
    val resolvedEnabled = enabled && !disabled
    val selected = radio.value == value
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Row(
        modifier =
            modifier
                .semantics {
                    role = Role.RadioButton
                    contentDescription = textValue ?: label
                    stateDescription = if (selected) "Selected" else "Not selected"
                    if (!resolvedEnabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.RadioButton,
                    onClick = {
                        if (onSelect?.invoke() != true) radio.onValueChange(value)
                    },
                )
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberMenuItemStyle() then style,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        DropdownMenuItemIndicator(contentAlignment = Alignment.Center) {
            if (selected) {
                Box(
                    modifier =
                        Modifier
                            .size(8.dp)
                            .background(currentTheme.colors.popoverForeground, CircleShape),
                )
            }
        }
        Text(
            variant = TextVariant.BodySmall,
            textStyle =
                if (!enabled) {
                    TextColors.Muted
                } else {
                    TextStyle(color = currentTheme.colors.popoverForeground)
                },
        ) { label }
    }
}

/** A non-interactive small heading row, matching real shadcn's `DropdownMenuLabel` (e.g. "My Account"). */
@Composable
fun DropdownMenuScope.DropdownMenuLabel(
    text: String,
    modifier: Modifier = Modifier,
    inset: Boolean = false,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.LabelSmall,
        modifier =
            modifier
                .styleable(
                    styleState,
                    rememberMenuLabelStyle() then style,
                )
                .dropdownMenuInset(inset),
    ) { text }
}

@Composable
@JvmName("DropdownMenuLabelLambda")
@OverloadResolutionByLambdaReturnType
fun DropdownMenuScope.DropdownMenuLabel(
    modifier: Modifier = Modifier,
    inset: Boolean = false,
    style: Style = Style,
    text: () -> String,
) {
    DropdownMenuLabel(text = text(), modifier = modifier, inset = inset, style = style)
}

/** A thin divider between rows in a [DropdownMenu]. */
@Composable
fun DropdownMenuSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    Separator(modifier = modifier, style = style)
}

/** Groups related rows. Matches shadcn's `DropdownMenuGroup`. */
@Composable
fun DropdownMenuGroup(
    modifier: Modifier = Modifier,
    heading: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (heading != null) {
            val styleState = remember { MutableStyleState(MutableInteractionSource()) }
            Text(
                variant = TextVariant.LabelSmall,
                modifier = Modifier.styleable(styleState, rememberMenuLabelStyle()),
            ) { heading }
        }
        content()
    }
}

@Immutable
private class DropdownMenuSubController(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val parentDismiss: () -> Unit,
    val onTriggerHoverChange: (Boolean) -> Unit,
    val onContentHoverChange: (Boolean) -> Unit,
) {
    fun toggle() = onOpenChange(!open)
}

private val LocalDropdownMenuSub = staticCompositionLocalOf<DropdownMenuSubController?> { null }

@Composable
private fun dropdownMenuSubController(): DropdownMenuSubController =
    checkNotNull(LocalDropdownMenuSub.current) {
        "DropdownMenuSubTrigger / DropdownMenuSubContent must be used inside DropdownMenuSub { … }"
    }

/** Reads the nearest [DropdownMenuSub] scope. Matches Radix submenu context. */
@Composable
fun dropdownMenuSubState(): DropdownMenuSubScope {
    val controller = dropdownMenuSubController()
    return DropdownMenuSubScope(open = controller.open, onOpenChange = controller.onOpenChange)
}

/** Slot receiver for [DropdownMenuSubTrigger] / [DropdownMenuSubContent]. */
class DropdownMenuSubScope internal constructor(
    val open: Boolean,
    internal val onOpenChange: (Boolean) -> Unit,
) {
    val toggle: () -> Unit get() = { onOpenChange(!open) }
}

/**
 * Nested menu. Matches shadcn's `DropdownMenuSub`. Trigger and Content must
 * share this [Box] so [AnchoredPopup] anchors to the row.
 *
 * Opens on hover (Radix `openOnHover`); click still toggles when there is no
 * hover, so touch can open the submenu.
 */
@Composable
fun DropdownMenuScope.DropdownMenuSub(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    defaultOpen: Boolean = false,
    content: @Composable DropdownMenuSubScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    var triggerHovered by remember { mutableStateOf(false) }
    var contentHovered by remember { mutableStateOf(false) }
    var hoverArmed by remember { mutableStateOf(false) }
    val hovered = triggerHovered || contentHovered
    LaunchedEffect(hovered) {
        if (hovered) {
            hoverArmed = true
            setOpen(true)
        } else if (hoverArmed) {
            delay(100)
            setOpen(false)
            hoverArmed = false
        }
    }
    val controller =
        DropdownMenuSubController(
            open = isOpen,
            onOpenChange = setOpen,
            parentDismiss = { dismiss() },
            onTriggerHoverChange = { triggerHovered = it },
            onContentHoverChange = { contentHovered = it },
        )
    CompositionLocalProvider(LocalDropdownMenuSub provides controller) {
        Box {
            DropdownMenuSubScope(open = isOpen, onOpenChange = setOpen).content()
        }
    }
}

/** Row that opens [DropdownMenuSubContent]. Matches shadcn's `DropdownMenuSubTrigger`. */
@Composable
fun DropdownMenuSubScope.DropdownMenuSubTrigger(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    inset: Boolean = false,
    textValue: String? = null,
    style: Style = Style,
) {
    val controller = dropdownMenuSubController()
    val resolvedEnabled = enabled && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    LaunchedEffect(isHovered) {
        controller.onTriggerHoverChange(isHovered)
    }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .semantics {
                    role = Role.Button
                    contentDescription = textValue ?: label
                    stateDescription = if (controller.open) "Expanded" else "Collapsed"
                    if (!resolvedEnabled) disabled()
                }
                .hoverable(interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = {
                        if (isHovered) {
                            controller.onOpenChange(true)
                        } else {
                            controller.toggle()
                        }
                    },
                )
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberMenuItemStyle() then style,
                )
                .dropdownMenuInset(inset),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            variant = TextVariant.BodySmall,
            textStyle =
                if (!enabled) {
                    TextColors.Muted
                } else {
                    TextStyle(color = currentTheme.colors.popoverForeground)
                },
        ) { label }
        Icon(
            ChevronRight,
            tint = currentTheme.colors.mutedForeground,
            variant = IconVariant.Small,
        )
    }
}

/**
 * Nested panel, opened to the end (right in LTR) of [DropdownMenuSubTrigger].
 * Item clicks dismiss the root menu.
 */
@Composable
fun DropdownMenuSubScope.DropdownMenuSubContent(
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.End,
    side: PopupPlacement? = null,
    style: Style = Style,
    // Base UI / base-nova SubContent default `sideOffset = 0`, `alignOffset = -3`.
    sideOffset: Dp = 0.dp,
    align: PopupAlign = PopupAlign.Start,
    alignOffset: Dp = (-3).dp,
    dismissOnClickOutside: Boolean = true,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    val controller = dropdownMenuSubController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val hoverSource = remember { MutableInteractionSource() }
    val contentHovered by hoverSource.collectIsHoveredAsState()
    LaunchedEffect(contentHovered) {
        controller.onContentHoverChange(contentHovered)
    }
    val scope =
        remember(controller.parentDismiss) {
            DropdownMenuScope(onOpenChange = { if (!it) controller.parentDismiss() })
        }
    AnchoredPopup(
        open = controller.open,
        onDismissRequest = { controller.onOpenChange(false) },
        placement = side ?: placement,
        offset = sideOffset,
        align = align,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        enter = enter,
        exit = exit,
    ) {
        Column(
            modifier =
                modifier
                    .hoverable(hoverSource)
                    .styleable(
                        styleState,
                        rememberMenuPanelStyle() then style,
                    ),
        ) {
            scope.content()
        }
    }
}
