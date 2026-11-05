@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.dropdownmenu

import com.toutiaoyan.compose.ui.components.contextmenu.LocalContextMenuNova
import com.toutiaoyan.compose.ui.components.contextmenu.LocalContextMenuLeadingSelectionIndicator
import com.toutiaoyan.compose.ui.components.contextmenu.LocalContextMenuNavigation
import com.toutiaoyan.compose.ui.components.contextmenu.ContextMenuNavigation
import com.toutiaoyan.compose.ui.components.contextmenu.contextMenuNavigation
import com.toutiaoyan.compose.ui.components.contextmenu.contextMenuTypography
import com.toutiaoyan.compose.ui.icons.NovaCheck
import com.toutiaoyan.compose.ui.icons.NovaChevronRight
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import com.toutiaoyan.compose.ui.components.contextmenu.MenuEscapeLayers
import com.toutiaoyan.compose.ui.components.contextmenu.LocalMenuEscapeLayers
import com.toutiaoyan.compose.ui.components.contextmenu.LocalMenuEscapeDepth
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
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
    val anchorBounds: () -> Rect,
    /**
     * Which row (if any) the menu should focus once it appears. Base UI only moves focus
     * into the menu for keyboard opens; a pointer open leaves every row unhighlighted
     * until the first ArrowDown/ArrowUp.
     */
    val initialFocus: () -> DropdownMenuInitialFocus,
    val consumeInitialFocus: () -> Unit,
    val setInitialFocus: (DropdownMenuInitialFocus) -> Unit,
) {
    fun toggle() = onOpenChange(!open)

    fun dismiss() = onOpenChange(false)

    fun onOpenChangeWithFocus(focus: DropdownMenuInitialFocus) {
        setInitialFocus(focus)
        onOpenChange(true)
    }
}

/** Base UI menu open intent: none (pointer), first row (Enter/Space/ArrowDown), last row (ArrowUp). */
internal enum class DropdownMenuInitialFocus { None, First, Last }

private val LocalDropdownMenu = staticCompositionLocalOf<DropdownMenuController?> { null }

internal val LocalDropdownMenuContent = staticCompositionLocalOf { false }

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
internal fun Modifier.dropdownMenuInset(inset: Boolean): Modifier {
    val extra = if (LocalContextMenuNova.current) 22.dp else currentTheme.icons.smallSize + twSpacing.sm
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
    val anchor = remember { mutableStateOf(Rect.Zero) }
    val readAnchor = remember { { anchor.value } }
    val initialFocus = remember { mutableStateOf(DropdownMenuInitialFocus.None) }
    val controller =
        DropdownMenuController(
            open =
            isOpen,
            onOpenChange =
            setOpen,
            modal =
            modal,
            anchorBounds =
            readAnchor,
            initialFocus =
            { initialFocus.value },
            consumeInitialFocus =
            { initialFocus.value = DropdownMenuInitialFocus.None },
            setInitialFocus =
            { initialFocus.value = it },
        )
    CompositionLocalProvider(LocalDropdownMenu provides controller) {
        Box(modifier = modifier.onGloballyPositioned { anchor.value = it.boundsInWindow() }) {
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
            modifier.contextMenuNavigation(resolvedEnabled, "")
                .semantics {
                    role = Role.Button
                    stateDescription = if (controller.open) "Expanded" else "Collapsed"
                }
                // Base UI opens the menu from the keyboard with Enter / Space / ArrowDown
                // (first row focused) or ArrowUp (last row focused). Record that intent
                // before the click handler toggles the menu; pointer clicks leave it None.
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown || !resolvedEnabled) {
                        false
                    } else {
                        when (event.key) {
                            Key.DirectionDown,
                            Key.DirectionUp,
                            -> {
                                if (controller.open) {
                                    false
                                } else {
                                    val focus =
                                        if (event.key == Key.DirectionUp) {
                                            DropdownMenuInitialFocus.Last
                                        } else {
                                            DropdownMenuInitialFocus.First
                                        }
                                    controller.onOpenChangeWithFocus(focus)
                                    true
                                }
                            }
                            Key.Enter, Key.Spacebar -> {
                                if (!controller.open) controller.onOpenChangeWithFocus(DropdownMenuInitialFocus.First)
                                // Fall through so the row's own click handler performs the toggle.
                                false
                            }
                            else -> false
                        }
                    }
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
    val scope =
        remember(controller.onOpenChange, controller.open) {
            DropdownMenuScope(onOpenChange = controller.onOpenChange, open = controller.open)
        }
    val navigation = remember { ContextMenuNavigation() }
    val escapeLayers = remember { MenuEscapeLayers() }
    val panelFocus = remember { FocusRequester() }
    val nova = currentTheme.activePreset == com.toutiaoyan.compose.ui.tokens.StylePreset.Nova
    // Official base-nova `DropdownMenuContent` is `w-(--anchor-width)` with `min-w-32`;
    // its demos override that with explicit `w-40` / `min-w-56` style widths.
    val anchorWidth =
        with(LocalDensity.current) { controller.anchorBounds().width.toDp() }
    val panelWidth = if (nova) maxOf(anchorWidth, 128.dp) else null
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
            CompositionLocalProvider(
                LocalDropdownMenuContent provides true,
                LocalContextMenuNova provides nova,
                LocalMenuEscapeLayers provides escapeLayers,
                LocalMenuEscapeDepth provides 0,
                LocalContextMenuNavigation provides navigation,
            ) {
                LaunchedEffect(Unit) {
                    val intent = controller.initialFocus()
                    controller.consumeInitialFocus()
                    androidx.compose.runtime.withFrameNanos { }
                    when (intent) {
                        DropdownMenuInitialFocus.First -> navigation.items.firstOrNull()?.requestFocus()
                        DropdownMenuInitialFocus.Last -> navigation.items.lastOrNull()?.requestFocus()
                        // Pointer opens keep every row unhighlighted; focus the panel itself so
                        // ArrowDown/ArrowUp can still reach the menu, matching Base UI.
                        DropdownMenuInitialFocus.None -> panelFocus.requestFocus()
                    }
                }
                Column(
                    modifier =
                        modifier.dropdownViewport(
                            side
                                ?: placement,
                            sideOffset,
                            controller.anchorBounds,
                        ).focusRequester(panelFocus)
                            .focusTarget()
                            .onPreviewKeyEvent {
                                if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                                    if (!escapeLayers.closeDeepest()) controller.dismiss()
                                    true
                                } else {
                                    navigation.key(it)
                                }
                            }.styleable(
                                styleState,
                                rememberMenuPanelStyle(novaWidth = panelWidth, novaRing = true) then
                                    style,
                            ).verticalScroll(rememberScrollState()),
                ) {
                    scope.content()
                }
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
                variant = if (LocalContextMenuNova.current) TextVariant.BodyMedium else TextVariant.BodySmall,
                style =
                    if (LocalContextMenuNova.current) {
                        Style {
                            fontSize(14.sp)
                            lineHeight(20.sp)
                        }
                    } else {
                        Style
                    },
                textStyle =
                    contextMenuTypography().merge(
                        when {
                            destructive || variant == DropdownMenuItemVariant.Destructive ->
                                TextStyle(color = currentTheme.colors.destructive)
                            !(enabled && !disabled) && !LocalContextMenuNova.current -> TextColors.Muted
                            else -> TextStyle(color = currentTheme.colors.popoverForeground)
                        },
                    ),
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
    val destructiveTone = destructive || variant == DropdownMenuItemVariant.Destructive
    val colors = currentTheme.colors
    val destructiveStyle =
        if (LocalContextMenuNova.current && destructiveTone) {
            Style {
                hovered { background(colors.destructive.copy(alpha = 0.1f)) }
                focused { background(colors.destructive.copy(alpha = 0.1f)) }
            }
        } else {
            Style
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = if (LocalContextMenuNova.current) Style else rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier.contextMenuNavigation(resolvedEnabled, label)
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
                    touchTargetStyle then pressStyle then rememberMenuItemStyle() then destructiveStyle then style,
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
    if (LocalContextMenuNova.current) {
        Text(
            variant = TextVariant.LabelSmall,
            modifier = modifier,
            textStyle =
                contextMenuTypography(label = true).copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                    letterSpacing = 0.1.em,
                ),
        ) { text }
        return
    }
    Text(
        variant = TextVariant.LabelSmall,
        textStyle = contextMenuTypography(TextColors.Muted, label = true),
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
 * prevent checking/unchecking). [leadingContent] supplies optional visuals before the label.
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
    leadingContent: (@Composable () -> Unit)? = null,
    style: Style = Style,
    onCheckedChange: (Boolean) -> Unit,
) {
    val leadingIndicator = LocalContextMenuNova.current && LocalContextMenuLeadingSelectionIndicator.current
    val resolvedEnabled = enabled && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = if (LocalContextMenuNova.current) Style else rememberMinTouchTargetStyle()
    Row(
        modifier =
            modifier.contextMenuNavigation(resolvedEnabled, label)
                .semantics {
                    role = Role.Checkbox
                    toggleableState =
                        when {
                            indeterminate -> ToggleableState.Indeterminate
                            checked -> ToggleableState.On
                            else -> ToggleableState.Off
                        }
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
        horizontalArrangement = Arrangement.spacedBy(if (LocalContextMenuNova.current) 6.dp else twSpacing.sm),
    ) {
        CompositionLocalProvider(
            LocalDropdownMenuItemChecked provides checked,
            LocalDropdownMenuItemIndeterminate provides indeterminate,
        ) {
            if (!LocalContextMenuNova.current || leadingIndicator) DropdownMenuItemIndicator()
            leadingContent?.invoke()
            Text(
                modifier = if (LocalContextMenuNova.current) Modifier.weight(1f) else Modifier,
                variant = if (LocalContextMenuNova.current) TextVariant.BodyMedium else TextVariant.BodySmall,
                style =
                    if (LocalContextMenuNova.current) {
                        Style {
                            fontSize(14.sp)
                            lineHeight(20.sp)
                        }
                    } else {
                        Style
                    },
                textStyle =
                    contextMenuTypography(
                        if (!enabled) TextColors.Muted else TextStyle(color = currentTheme.colors.popoverForeground),
                    ),
            ) { label }
            if (LocalContextMenuNova.current && !leadingIndicator) DropdownMenuItemIndicator()
        }
    }
}

/** Check-mark slot for a [DropdownMenuCheckboxItem]. Inlined by official; not exported. */
@Composable
internal fun DropdownMenuItemIndicator(
    checked: Boolean =
        checkNotNull(LocalDropdownMenuItemChecked.current) {
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
                    if (LocalContextMenuNova.current) NovaCheck else Check,
                    tint = currentTheme.colors.popoverForeground,
                    variant = if (LocalContextMenuNova.current) IconVariant.Default else IconVariant.Small,
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
        modifier = modifier.size(if (LocalContextMenuNova.current) 16.dp else currentTheme.icons.smallSize),
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
 * `DropdownMenuRadioItem`. [leadingContent] supplies optional visuals before the label.
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
    leadingContent: (@Composable () -> Unit)? = null,
    style: Style = Style,
) {
    val radio =
        checkNotNull(LocalDropdownMenuRadio.current) {
            "DropdownMenuRadioItem must be used inside DropdownMenuRadioGroup { … }"
        }
    val leadingIndicator = LocalContextMenuNova.current && LocalContextMenuLeadingSelectionIndicator.current
    val resolvedEnabled = enabled && !disabled
    val selected = radio.value == value
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = if (LocalContextMenuNova.current) Style else rememberMinTouchTargetStyle()
    Row(
        modifier =
            modifier.contextMenuNavigation(resolvedEnabled, label)
                .semantics {
                    role = Role.RadioButton
                    this.selected = selected
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
        horizontalArrangement = Arrangement.spacedBy(if (LocalContextMenuNova.current) 6.dp else twSpacing.sm),
    ) {
        if (!LocalContextMenuNova.current || leadingIndicator) {
            DropdownMenuItemIndicator(contentAlignment = Alignment.Center) {
                if (selected && leadingIndicator) {
                    Icon(NovaCheck, variant = IconVariant.Default, tint = currentTheme.colors.popoverForeground)
                } else if (selected) {
                    Box(
                        modifier =
                            Modifier
                                .size(8.dp)
                                .background(currentTheme.colors.popoverForeground, CircleShape),
                    )
                }
            }
        }
        leadingContent?.invoke()
        Text(
            modifier = if (LocalContextMenuNova.current) Modifier.weight(1f) else Modifier,
            variant = if (LocalContextMenuNova.current) TextVariant.BodyMedium else TextVariant.BodySmall,
            style =
                if (LocalContextMenuNova.current) {
                    Style {
                        fontSize(14.sp)
                        lineHeight(20.sp)
                    }
                } else {
                    Style
                },
            textStyle =
                contextMenuTypography(
                    if (!enabled) TextColors.Muted else TextStyle(color = currentTheme.colors.popoverForeground),
                ),
        ) { label }
        if (LocalContextMenuNova.current && !leadingIndicator) {
            Box(Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                if (selected) {
                    Icon(
                        NovaCheck,
                        variant = IconVariant.Default,
                        tint = currentTheme.colors.popoverForeground,
                    )
                }
            }
        }
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
        textStyle = contextMenuTypography(label = true),
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
    val nova = LocalContextMenuNova.current
    val spacing =
        if (nova) {
            Modifier.layout { measurable, constraints ->
                val bleed = 4.dp.roundToPx()
                val child = measurable.measure(constraints.offset(horizontal = bleed * 2))
                layout((child.width - bleed * 2).coerceAtLeast(0), child.height) {
                    child.placeRelative(-bleed, 0)
                }
            }.padding(vertical = 4.dp)
        } else {
            Modifier
        }
    Separator(modifier = modifier.then(spacing), style = style)
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
    val focusContent: Boolean,
    val openFromKeyboard: () -> Unit,
    val closeFromKeyboard: () -> Unit,
    val triggerFocus: FocusRequester,
    val onChildOpenChange: (Any, Boolean) -> Unit,
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
    val parentSub = LocalDropdownMenuSub.current
    val escapeLayers = LocalMenuEscapeLayers.current
    val depth = LocalMenuEscapeDepth.current + 1
    val triggerFocus = remember { FocusRequester() }
    var restoreFocus by remember { mutableStateOf(false) }
    var hoverSuppressed by remember { mutableStateOf(false) }
    LaunchedEffect(isOpen, restoreFocus) {
        if (!isOpen && restoreFocus) {
            // Allow the parent popup to regain keyboard focus before targeting its trigger.
            androidx.compose.runtime.withFrameNanos { }
            androidx.compose.runtime.withFrameNanos { }
            triggerFocus.requestFocus()
            restoreFocus = false
        }
    }
    var triggerHovered by remember { mutableStateOf(false) }
    var contentHovered by remember { mutableStateOf(false) }
    var hoverArmed by remember { mutableStateOf(false) }
    var focusContent by remember { mutableStateOf(defaultOpen || open == true) }
    val openChildren = remember { mutableStateMapOf<Any, Unit>() }
    val childOpenChange =
        remember {
            { token: Any, childOpen: Boolean ->
                if (childOpen) openChildren[token] = Unit else openChildren.remove(token)
                Unit
            }
        }
    val parentChildOpenChange = LocalDropdownMenuSub.current?.onChildOpenChange
    val childToken = remember { Any() }
    DisposableEffect(isOpen, parentChildOpenChange) {
        parentChildOpenChange?.invoke(childToken, isOpen)
        onDispose { parentChildOpenChange?.invoke(childToken, false) }
    }
    val hovered = triggerHovered || contentHovered
    val hasOpenChildren = openChildren.isNotEmpty()
    LaunchedEffect(hovered, hasOpenChildren, focusContent, hoverSuppressed) {
        if (!hovered) hoverSuppressed = false
        if (hovered && !hoverSuppressed) {
            hoverArmed = true
            setOpen(true)
        } else if (hoverArmed && !hasOpenChildren && !focusContent) {
            delay(100)
            setOpen(false)
            hoverArmed = false
        }
    }
    val controller =
        DropdownMenuSubController(
            open = isOpen,
            onOpenChange = { next ->
                if (!next) focusContent = false else hoverSuppressed = false
                setOpen(next)
            },
            parentDismiss = { dismiss() },
            focusContent = focusContent,
            openFromKeyboard = {
                hoverSuppressed = false
                focusContent = true
                setOpen(true)
            },
            closeFromKeyboard = {
                parentSub?.openFromKeyboard?.invoke()
                hoverSuppressed = true
                hoverArmed = false
                focusContent = false
                restoreFocus = true
                setOpen(false)
            },
            triggerFocus = triggerFocus,
            onChildOpenChange = childOpenChange,
            onTriggerHoverChange = { triggerHovered = it },
            onContentHoverChange = { contentHovered = it },
        )
    val closeCurrent by rememberUpdatedState(controller.closeFromKeyboard)
    DisposableEffect(escapeLayers, depth, isOpen) {
        val unregister = if (isOpen) escapeLayers?.register(depth) { closeCurrent() } else null
        onDispose { unregister?.invoke() }
    }
    CompositionLocalProvider(
        LocalDropdownMenuSub provides controller,
        LocalMenuEscapeDepth provides depth,
    ) {
        Box {
            DropdownMenuSubScope(open = isOpen, onOpenChange = controller.onOpenChange).content()
        }
    }
}

/** Row opening [DropdownMenuSubContent], with optional [leadingContent] before the label. */
@Composable
fun DropdownMenuSubScope.DropdownMenuSubTrigger(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    inset: Boolean = false,
    textValue: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    style: Style = Style,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
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
    val touchTargetStyle = if (LocalContextMenuNova.current) Style else rememberMinTouchTargetStyle()
    Row(
        modifier =
            modifier.focusRequester(controller.triggerFocus).contextMenuNavigation(resolvedEnabled, label)
                .fillMaxWidth()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown &&
                        event.key == (
                            if (rtl) {
                                Key.DirectionLeft
                            } else {
                                Key.DirectionRight
                            }
                        ) &&
                        resolvedEnabled
                    ) {
                        controller.openFromKeyboard()
                        true
                    } else {
                        false
                    }
                }
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
        horizontalArrangement = if (leadingContent == null) Arrangement.SpaceBetween else Arrangement.spacedBy(6.dp),
    ) {
        leadingContent?.invoke()
        Text(
            modifier = if (leadingContent == null) Modifier else Modifier.weight(1f),
            variant = if (LocalContextMenuNova.current) TextVariant.BodyMedium else TextVariant.BodySmall,
            style =
                if (LocalContextMenuNova.current) {
                    Style {
                        fontSize(14.sp)
                        lineHeight(20.sp)
                    }
                } else {
                    Style
                },
            textStyle =
                contextMenuTypography(
                    if (!enabled) TextColors.Muted else TextStyle(color = currentTheme.colors.popoverForeground),
                ),
        ) { label }
        Icon(
            if (LocalContextMenuNova.current) NovaChevronRight else ChevronRight,
            modifier = if (rtl) Modifier.rotate(180f) else Modifier,
            tint = currentTheme.colors.mutedForeground,
            variant = if (LocalContextMenuNova.current) IconVariant.Default else IconVariant.Small,
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
    val nav = remember { ContextMenuNavigation() }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val contextNova = LocalContextMenuNova.current
    val escapeLayers = LocalMenuEscapeLayers.current
    AnchoredPopup(
        open = controller.open,
        onDismissRequest = { controller.onOpenChange(false) },
        placement = side ?: placement,
        offset = sideOffset,
        align = align,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        // Hover must not steal focus from the trigger and generate another hover exit.
        // ArrowRight promotes this same panel to keyboard focus when requested.
        focusable = controller.focusContent,
        enter = enter,
        exit = exit,
    ) {
        CompositionLocalProvider(LocalContextMenuNavigation provides if (contextNova) nav else null) {
            LaunchedEffect(controller.focusContent) {
                if (contextNova && controller.focusContent) {
                    androidx.compose.runtime.withFrameNanos { }
                    nav.items.firstOrNull()?.requestFocus()
                }
            }
            Column(
                modifier =
                    modifier.dropdownViewport(side ?: placement, sideOffset)
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.Escape) {
                                if (escapeLayers?.closeDeepest() != true) controller.closeFromKeyboard()
                                true
                            } else if (contextNova &&
                                event.type == KeyEventType.KeyDown &&
                                event.key == (
                                    if (rtl) {
                                        Key.DirectionRight
                                    } else {
                                        Key.DirectionLeft
                                    }
                                )
                            ) {
                                if (escapeLayers?.closeDeepest() != true) controller.closeFromKeyboard()
                                true
                            } else {
                                contextNova && nav.key(event)
                            }
                        }
                        .hoverable(hoverSource)
                        .styleable(
                            styleState,
                            // Official submenu content is `w-auto min-w-[96px]`; callers pass an
                            // explicit `style` width for wider labels.
                            rememberMenuPanelStyle(
                                novaMinWidth = if (contextNova) 96.dp else null,
                                novaRing = contextNova,
                            ) then style,
                        ).verticalScroll(rememberScrollState()),
            ) {
                scope.content()
            }
        }
    }
}

/** Anchor/window state is read during measurement, so scrolling does not recompose the menu. */
@Composable
private fun Modifier.dropdownViewport(
    placement: PopupPlacement,
    sideOffset: Dp,
    anchorBounds: (() -> Rect)? = null,
): Modifier {
    val window = LocalWindowInfo.current
    val density = LocalDensity.current
    val margin = with(density) { 8.dp.roundToPx() }
    val offset = with(density) { sideOffset.roundToPx() }
    return layout { measurable, constraints ->
        val height = window.containerSize.height
        val anchor = anchorBounds?.invoke()
        val available =
            if (anchor != null && (placement == PopupPlacement.Top || placement == PopupPlacement.Bottom)) {
                maxOf(anchor.top.toInt() - offset - margin, height - anchor.bottom.toInt() - offset - margin)
            } else {
                height - margin * 2
            }
        val limit = available.coerceAtLeast(1)
        val child =
            measurable.measure(
                constraints.copy(
                    minHeight =
                        minOf(
                            constraints.minHeight,
                            limit,
                        ),
                    maxHeight =
                        minOf(
                            constraints.maxHeight,
                            limit,
                        ),
                ),
            )
        layout(child.width, child.height) { child.placeRelative(0, 0) }
    }
}
