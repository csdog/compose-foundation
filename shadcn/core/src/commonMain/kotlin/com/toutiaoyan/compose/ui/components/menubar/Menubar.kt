@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuCheckboxItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemIndicator
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItemVariant
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuLabel
import com.toutiaoyan.compose.ui.components.dropdownmenu.LocalDropdownMenuItemChecked
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
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.rememberMenuPanelStyle
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

@Immutable
internal class MenubarController(
    val openValue: String?,
    val onOpenChange: (String?) -> Unit,
    val modal: Boolean,
)

private val LocalMenubar = staticCompositionLocalOf<MenubarController?> { null }

@Composable
private fun menubarController(): MenubarController =
    checkNotNull(LocalMenubar.current) {
        "MenubarMenu must be used inside Menubar { … }"
    }

/** Open menu id of the nearest [Menubar]. Matches Radix menubar context. */
class MenubarState(
    val openValue: String?,
    val onOpenChange: (String?) -> Unit,
)

/** Reads the nearest [Menubar] state. */
@Composable
fun menubarState(): MenubarState {
    val ctrl = menubarController()
    return MenubarState(openValue = ctrl.openValue, onOpenChange = ctrl.onOpenChange)
}

/** Slot receiver for [MenubarMenu]. */
class MenubarScope internal constructor()

/** Slot receiver for [MenubarTrigger] / [MenubarContent]. */
class MenubarMenuScope internal constructor(
    val value: String,
)

private val LocalMenubarMenu = staticCompositionLocalOf<MenubarMenuScope?> { null }

/** Reads the nearest [MenubarMenu] scope. */
@Composable
fun menubarMenuState(): MenubarMenuScope =
    checkNotNull(LocalMenubarMenu.current) {
        "menubarMenuState() must be used inside MenubarMenu { … }"
    }

/**
 * A horizontal row of menu triggers matching real shadcn/ui's `menubar.tsx`:
 *
 * ```
 * Menubar
 * ├── MenubarMenu
 * │   ├── MenubarTrigger
 * │   └── MenubarContent
 * │       ├── MenubarItem
 * │       └── MenubarSeparator
 * └── MenubarMenu
 *     ├── MenubarTrigger
 *     └── MenubarContent
 * ```
 *
 * Only one menu is open at a time. Click a trigger to open; while any menu is
 * open, hovering another trigger switches to it (Radix menubar).
 *
 * Usage:
 * ```
 * Menubar {
 *     MenubarMenu(value = "file") {
 *         MenubarTrigger("File")
 *         MenubarContent {
 *             MenubarItem("New Tab", onClick = {})
 *             MenubarSeparator()
 *             MenubarItem("Close Window", onClick = {}, destructive = true)
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun Menubar(
    modifier: Modifier = Modifier,
    style: Style = Style,
    modal: Boolean = true,
    value: String? = null,
    onValueChange: ((String?) -> Unit)? = null,
    defaultValue: String? = null,
    ariaLabel: String? = null,
    content: @Composable MenubarScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultValue) }
    val controlled = onValueChange != null
    val openValue = if (controlled) value else internalOpen
    val setOpen: (String?) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onValueChange?.invoke(next)
    }
    val controller =
        MenubarController(openValue = openValue, onOpenChange = setOpen, modal = modal)
    val barStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(LocalMenubar provides controller) {
        Row(
            modifier =
                modifier
                    .then(
                        if (ariaLabel != null) {
                            Modifier.semantics { contentDescription = ariaLabel }
                        } else {
                            Modifier
                        },
                    )
                    .styleable(
                        barStyleState,
                        rememberMenubarStyle() then style,
                    ),
        ) {
            MenubarScope().content()
        }
    }
}

/** One top-level menu (e.g. File). [value] is the exclusive-open id. */
@Composable
fun MenubarScope.MenubarMenu(
    value: String,
    content: @Composable MenubarMenuScope.() -> Unit,
) {
    menubarController()
    val scope = MenubarMenuScope(value)
    CompositionLocalProvider(LocalMenubarMenu provides scope) {
        Box { scope.content() }
    }
}

/** Trigger label for the parent [MenubarMenu]. Matches shadcn's `MenubarTrigger`. */
@Composable
fun MenubarMenuScope.MenubarTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = menubarController()
    val resolvedEnabled = enabled && !disabled
    val isOpen = controller.openValue == value
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    LaunchedEffect(isHovered) {
        if (resolvedEnabled && isHovered && controller.openValue != null) {
            controller.onOpenChange(value)
        }
    }
    val styleState = rememberUpdatedStyleState(interactionSource)
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription = value
                    stateDescription = if (isOpen) "Expanded" else "Collapsed"
                    if (!resolvedEnabled) disabled()
                }
                .hoverable(interactionSource, enabled = resolvedEnabled)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = {
                        controller.onOpenChange(if (isOpen) null else value)
                    },
                )
                .styleable(styleState, touchTargetStyle then pressStyle then rememberMenubarTriggerStyle(isOpen) then style),
    ) {
        content()
    }
}

/** String-label overload of [MenubarTrigger]. */
@Composable
fun MenubarMenuScope.MenubarTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    MenubarTrigger(modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
        Text(variant = TextVariant.BodySmall) { text }
    }
}

/**
 * Dropdown panel of a [MenubarMenu]. Compose [MenubarItem] / [MenubarSeparator]
 * / [MenubarLabel] inside (aliases of the DropdownMenu row API).
 */
@Composable
fun MenubarMenuScope.MenubarContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    // Base UI / base-nova MenubarContent default `sideOffset = 8`, `alignOffset = -4`.
    sideOffset: Dp = 8.dp,
    alignOffset: Dp = (-4).dp,
    dismissOnClickOutside: Boolean = true,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    val controller = menubarController()
    val isOpen = controller.openValue == value
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val scope =
        remember(controller) {
            DropdownMenuScope(onOpenChange = { if (!it) controller.onOpenChange(null) })
        }
    // Must share the [MenubarMenu] Box with [MenubarTrigger] so [AnchoredPopup]
    // measures against the trigger, not an empty nested DropdownMenu Box.
    MenubarPortal {
        AnchoredPopup(
            open = isOpen,
            onDismissRequest = { controller.onOpenChange(null) },
            offset = sideOffset,
            alignOffset = alignOffset,
            dismissOnClickOutside = dismissOnClickOutside,
            focusable = controller.modal,
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

/** Teleport host for [MenubarContent]. Matches shadcn's `MenubarPortal`. */
@Composable
fun MenubarPortal(content: @Composable () -> Unit) {
    content()
}

/** Alias for [DropdownMenuItem] matching shadcn's `MenubarItem`. */
@Composable
fun DropdownMenuScope.MenubarItem(
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

/** Alias for [DropdownMenuLabel] matching shadcn's `MenubarLabel`. */
@Composable
fun DropdownMenuScope.MenubarLabel(
    text: String,
    modifier: Modifier = Modifier,
    inset: Boolean = false,
    style: Style = Style,
) {
    DropdownMenuLabel(text = text, modifier = modifier, inset = inset, style = style)
}

@Composable
@JvmName("MenubarLabelLambda")
@OverloadResolutionByLambdaReturnType
fun DropdownMenuScope.MenubarLabel(
    modifier: Modifier = Modifier,
    inset: Boolean = false,
    style: Style = Style,
    text: () -> String,
) {
    MenubarLabel(text = text(), modifier = modifier, inset = inset, style = style)
}

/** Alias for [DropdownMenuSeparator] matching shadcn's `MenubarSeparator`. */
@Composable
fun MenubarSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    DropdownMenuSeparator(modifier = modifier, style = style)
}

/** Alias for [DropdownMenuGroup] matching shadcn's `MenubarGroup`. */
@Composable
fun MenubarGroup(
    modifier: Modifier = Modifier,
    heading: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenuGroup(modifier = modifier, heading = heading, content = content)
}

/** Alias for [DropdownMenuCheckboxItem] matching shadcn's `MenubarCheckboxItem`. */
@Composable
fun DropdownMenuScope.MenubarCheckboxItem(
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

/** Alias for [DropdownMenuRadioGroup] matching shadcn's `MenubarRadioGroup`. */
@Composable
fun DropdownMenuScope.MenubarRadioGroup(
    value: String,
    onValueChange: (String) -> Unit,
    content: @Composable DropdownMenuRadioGroupScope.() -> Unit,
) {
    DropdownMenuRadioGroup(value = value, onValueChange = onValueChange, content = content)
}

/** Alias for [DropdownMenuRadioItem] matching shadcn's `MenubarRadioItem`. */
@Composable
fun DropdownMenuRadioGroupScope.MenubarRadioItem(
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

/** Alias for [DropdownMenuSub] matching shadcn's `MenubarSub`. */
@Composable
fun DropdownMenuScope.MenubarSub(
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

/** Alias for [DropdownMenuSubTrigger] matching shadcn's `MenubarSubTrigger`. */
@Composable
fun DropdownMenuSubScope.MenubarSubTrigger(
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

/** Alias for [DropdownMenuSubContent] matching shadcn's `MenubarSubContent`. */
@Composable
fun DropdownMenuSubScope.MenubarSubContent(
    modifier: Modifier = Modifier,
    // Inherit DropdownMenuSubContent / Base: `sideOffset = 0`, `alignOffset = -3`.
    sideOffset: Dp = 0.dp,
    alignOffset: Dp = (-3).dp,
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

/** Alias for [DropdownMenuShortcut] matching shadcn's `MenubarShortcut`. */
@Composable
fun MenubarShortcut(
    text: String,
    modifier: Modifier = Modifier,
) {
    DropdownMenuShortcut(text = text, modifier = modifier)
}

@Composable
@JvmName("MenubarShortcutLambda")
@OverloadResolutionByLambdaReturnType
fun MenubarShortcut(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    MenubarShortcut(text = text(), modifier = modifier)
}

/** Alias for [DropdownMenuItemIndicator]. Base UI does not export MenubarItemIndicator. */
@Composable
internal fun MenubarItemIndicator(
    checked: Boolean = checkNotNull(LocalDropdownMenuItemChecked.current) {
        "MenubarItemIndicator must be used inside MenubarCheckboxItem, or pass checked="
    },
    modifier: Modifier = Modifier,
) {
    DropdownMenuItemIndicator(checked = checked, modifier = modifier)
}
