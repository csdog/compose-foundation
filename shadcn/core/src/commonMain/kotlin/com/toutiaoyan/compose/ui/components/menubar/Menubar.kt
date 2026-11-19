@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.menubar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.LayoutCoordinates
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.*
import com.toutiaoyan.compose.ui.components.contextmenu.LocalContextMenuNavigation
import com.toutiaoyan.compose.ui.components.contextmenu.LocalContextMenuNova
import com.toutiaoyan.compose.ui.components.contextmenu.LocalContextMenuLeadingSelectionIndicator
import com.toutiaoyan.compose.ui.components.dropdownmenu.dropdownMenuInset
import com.toutiaoyan.compose.ui.components.overlay.overlayTypography
import com.toutiaoyan.compose.ui.styles.rememberMenuLabelStyle
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.dropdownMenuState
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
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

internal class MenubarNavigation {
    val triggers = linkedMapOf<String, FocusRequester>()
    val disabled = mutableSetOf<String>()
    val triggerCoordinates = mutableMapOf<String, LayoutCoordinates>()
    var restoreFocus by mutableStateOf<String?>(null)
    var focusLastFor: String? = null
    var focusFirstFor: String? = null

    fun enabledTriggers(direction: LayoutDirection): List<String> {
        val enabled = triggers.keys.filter { it !in disabled }
        if (!enabled.all { triggerCoordinates[it]?.isAttached == true }) return enabled
        return enabled.sortedBy { value ->
            val x = triggerCoordinates.getValue(value).positionInRoot().x
            if (direction == LayoutDirection.Rtl) -x else x
        }
    }

    fun move(
        from: String,
        delta: Int,
        direction: LayoutDirection,
        open: Boolean,
        onOpenChange: (String?) -> Unit,
    ) {
        val values = enabledTriggers(direction)
        if (values.isEmpty()) return
        val next = values[(values.indexOf(from) + delta).mod(values.size)]
        triggers[next]?.requestFocus()
        if (open) {
            focusFirstFor = next
            onOpenChange(next)
        }
    }
}

internal class MenubarController(
    val openValue: String?,
    val onOpenChange: (String?) -> Unit,
    val modal: Boolean,
    val navigation: MenubarNavigation,
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
    val navigation = remember { MenubarNavigation() }
    val controller =
        MenubarController(
            openValue =
            openValue,
            onOpenChange =
            setOpen,
            modal =
            modal,
            navigation =
            navigation,
        )
    val nova = currentTheme.activePreset == StylePreset.Nova
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
            horizontalArrangement = Arrangement.spacedBy(if (nova) 2.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
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
    val controller = menubarController()
    val scope = remember(value) { MenubarMenuScope(value) }
    DropdownMenu(
        open = controller.openValue == value,
        onOpenChange = { controller.onOpenChange(if (it) value else null) },
        modal = controller.modal,
    ) {
        CompositionLocalProvider(LocalMenubarMenu provides scope) { scope.content() }
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
    val nova = currentTheme.activePreset == StylePreset.Nova
    val direction = LocalLayoutDirection.current
    val focusRequester = remember { FocusRequester() }
    val navigation = controller.navigation
    val latestController by rememberUpdatedState(controller)
    DisposableEffect(navigation, value) {
        navigation.triggers[value] = focusRequester
        onDispose {
            navigation.triggers.remove(value)
            navigation.disabled.remove(value)
            navigation.triggerCoordinates.remove(value)
        }
    }
    SideEffect { if (resolvedEnabled) navigation.disabled.remove(value) else navigation.disabled.add(value) }
    LaunchedEffect(isOpen, navigation.restoreFocus) {
        if (!isOpen && navigation.restoreFocus == value) {
            withFrameNanos { }
            focusRequester.requestFocus()
            navigation.restoreFocus = null
        }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    LaunchedEffect(isHovered, resolvedEnabled) {
        if (resolvedEnabled && isHovered && latestController.openValue != null) {
            latestController.onOpenChange(value)
        }
    }
    val styleState = rememberUpdatedStyleState(interactionSource)
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = if (nova) Style else rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .onGloballyPositioned { navigation.triggerCoordinates[value] = it }
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown || !resolvedEnabled) {
                        false
                    } else {
                        when (event.key) {
                            Key.DirectionRight, Key.DirectionLeft -> {
                                val physical = if (event.key == Key.DirectionRight) 1 else -1
                                navigation.move(
                                    value,
                                    if (direction == LayoutDirection.Rtl) -physical else physical,
                                    direction,
                                    controller.openValue != null,
                                    controller.onOpenChange,
                                )
                                true
                            }
                            Key.MoveHome, Key.MoveEnd -> {
                                val values = navigation.enabledTriggers(direction)
                                val target =
                                    if (event.key == Key.MoveHome) {
                                        values.firstOrNull()
                                    } else {
                                        values.lastOrNull()
                                    }
                                if (target != null) {
                                    navigation.triggers[target]?.requestFocus()
                                    if (controller.openValue != null) {
                                        navigation.focusFirstFor = target
                                        controller.onOpenChange(target)
                                    }
                                }
                                true
                            }
                            Key.Enter, Key.Spacebar -> {
                                navigation.focusFirstFor = value
                                navigation.focusLastFor = null
                                controller.onOpenChange(value)
                                true
                            }
                            Key.DirectionDown, Key.DirectionUp -> {
                                navigation.focusFirstFor = if (event.key == Key.DirectionDown) value else null
                                navigation.focusLastFor = if (event.key == Key.DirectionUp) value else null
                                controller.onOpenChange(value)
                                true
                            }
                            else -> false
                        }
                    }
                }
                .semantics {
                    role = Role.Button
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
                        navigation.focusLastFor = null
                        navigation.focusFirstFor = null
                        controller.onOpenChange(if (isOpen) null else value)
                    },
                )
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberMenubarTriggerStyle(isOpen) then style,
                ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides currentTheme.colors.foreground,
            LocalDefaultTextStyle provides
                if (nova) {
                    TextStyle(
                        fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    )
                } else {
                    LocalDefaultTextStyle.current
                },
        ) { content() }
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
        Text(
            variant =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    TextVariant.BodyMedium
                } else {
                    TextVariant.BodySmall
                },
        ) { text }
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
    align: PopupAlign = PopupAlign.Start,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    val controller = menubarController()
    val direction = LocalLayoutDirection.current
    val nova = currentTheme.activePreset == StylePreset.Nova
    dropdownMenuState().DropdownMenuContent(
        modifier =
            modifier.onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) {
                    false
                } else {
                    when (event.key) {
                        Key.DirectionRight, Key.DirectionLeft -> {
                            val physical = if (event.key == Key.DirectionRight) 1 else -1
                            controller.navigation.move(
                                value,
                                if (direction == LayoutDirection.Rtl) -physical else physical,
                                direction,
                                true,
                                controller.onOpenChange,
                            )
                            true
                        }
                        else -> false
                    }
                }
            }.onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                    // Let the shared menu handler dismiss the deepest open layer first.
                    controller.navigation.restoreFocus = value
                    false
                } else {
                    false
                }
            },
        // Nova menubars override the shared dropdown minimum (min-w-36).
        style = (if (nova) Style { minWidth(144.dp) } else Style) then style,
        sideOffset = sideOffset,
        align = align,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        content = {
            val navigation = LocalContextMenuNavigation.current
            LaunchedEffect(Unit) {
                val focusLast = controller.navigation.focusLastFor == value
                val focusFirst = controller.navigation.focusFirstFor == value
                if (focusLast || focusFirst) {
                    withFrameNanos { }
                    if (focusLast) {
                        navigation?.items?.lastOrNull()?.requestFocus()
                    } else {
                        navigation?.items?.firstOrNull()?.requestFocus()
                    }
                    controller.navigation.focusLastFor = null
                    controller.navigation.focusFirstFor = null
                }
            }
            content()
        },
    )
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
    content: (@Composable () -> Unit)? = null,
) {
    if (content == null) {
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
            style = rememberMenubarItemStyle() then style,
        )
    } else {
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
            style = rememberMenubarItemStyle() then style,
            content = content,
        )
    }
}

/** Alias for [DropdownMenuLabel] matching shadcn's `MenubarLabel`. */
@Composable
fun DropdownMenuScope.MenubarLabel(
    text: String,
    modifier: Modifier = Modifier,
    inset: Boolean = false,
    style: Style = Style,
) {
    if (LocalContextMenuNova.current) {
        val styleState = remember { MutableStyleState(MutableInteractionSource()) }
        Text(
            modifier = modifier.styleable(styleState, rememberMenuLabelStyle() then style).dropdownMenuInset(inset),
            textStyle =
                overlayTypography(medium = true).copy(
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                ),
        ) { text }
    } else {
        DropdownMenuLabel(text = text, modifier = modifier, inset = inset, style = style)
    }
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
    CompositionLocalProvider(LocalContextMenuLeadingSelectionIndicator provides true) {
        DropdownMenuCheckboxItem(
            label = label,
            checked = checked,
            modifier = modifier,
            enabled = enabled,
            disabled = disabled,
            indeterminate = indeterminate,
            onSelect = onSelect,
            textValue = textValue,
            style = rememberMenubarItemStyle() then style,
            onCheckedChange = onCheckedChange,
        )
    }
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
    CompositionLocalProvider(LocalContextMenuLeadingSelectionIndicator provides true) {
        DropdownMenuRadioItem(
            value = value,
            label = label,
            modifier = modifier,
            enabled = enabled,
            disabled = disabled,
            onSelect = onSelect,
            textValue = textValue,
            style = rememberMenubarItemStyle() then style,
        )
    }
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
        style = rememberMenubarItemStyle() then style,
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
    checked: Boolean =
        checkNotNull(LocalDropdownMenuItemChecked.current) {
            "MenubarItemIndicator must be used inside MenubarCheckboxItem, or pass checked="
        },
    modifier: Modifier = Modifier,
) {
    DropdownMenuItemIndicator(checked = checked, modifier = modifier)
}
