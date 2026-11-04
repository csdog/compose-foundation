@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.select

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.input.key.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.error
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.icons.NovaCheck
import com.toutiaoyan.compose.ui.icons.NovaChevronRight
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.styles.rememberMenuLabelStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * Shared select state for [SelectTrigger] / [SelectValue] / [SelectContent] / [SelectItem].
 * Type-erased at the Local boundary; [select] / [isSelected] / [labelOf] close over the
 * root's `T` safely.
 */
@Immutable
class SelectController internal constructor(
    val displayText: String?,
    val placeholder: String,
    val open: Boolean,
    val setOpen: (Boolean) -> Unit,
    val invalid: Boolean,
    val required: Boolean,
    val disabled: Boolean,
    val modal: Boolean,
    private val onSelect: (Any?) -> Unit,
    private val selectedEquals: (Any?) -> Boolean,
    private val format: (Any?) -> String,
) {
    fun select(value: Any?) {
        onSelect(value)
        setOpen(false)
    }

    fun isSelected(value: Any?): Boolean = selectedEquals(value)

    fun labelOf(value: Any?): String = format(value)
}

private data class SelectFocusItem(val value: Any?, val requester: FocusRequester, val enabled: Boolean)

private class SelectKeyboard {
    val trigger = FocusRequester()
    var width by mutableStateOf(144.dp)
    var height by mutableStateOf(32.dp)
    var panelTop by mutableStateOf(0f)
    val centers = mutableStateMapOf<Any?, Float>()
    val items = mutableStateListOf<SelectFocusItem>()
    var active: Any? = null
    val pick = mutableMapOf<Any?, () -> Unit>()
    var fromEnd = false
    private var suppressActivationUp = false

    fun consumeRelease(event: KeyEvent): Boolean {
        if (event.type == KeyEventType.KeyUp && event.key in listOf(Key.Enter, Key.Spacebar) && suppressActivationUp) {
            suppressActivationUp = false
            return true
        }
        return false
    }

    fun move(
        key: Key,
        controller: SelectController,
    ): Boolean {
        val options = items.filter { it.enabled }
        if (options.isEmpty()) return false
        val index = options.indexOfFirst { it.value == active }
        val target =
            when (key) {
                Key.DirectionDown -> (index + 1).coerceAtMost(options.lastIndex)
                Key.DirectionUp -> (if (index < 0) options.lastIndex else index - 1).coerceAtLeast(0)
                Key.MoveHome -> 0
                Key.MoveEnd -> options.lastIndex
                Key.Enter, Key.Spacebar -> {
                    suppressActivationUp = true
                    options.getOrNull(index)?.let { pick[it.value]?.invoke() }
                    return index >= 0
                }
                Key.Escape -> {
                    controller.setOpen(false)
                    return true
                }
                else -> return false
            }
        options[target].requester.requestFocus()
        return true
    }
}

private val LocalSelectKeyboard = staticCompositionLocalOf<SelectKeyboard?> { null }
private val LocalSelect = staticCompositionLocalOf<SelectController?> { null }

private val LocalSelectScroll = staticCompositionLocalOf<ScrollState?> { null }

private val LocalSelectItemSelected = staticCompositionLocalOf<Boolean?> { null }

@Composable
private fun selectController(): SelectController =
    checkNotNull(LocalSelect.current) {
        "Select parts must be used inside Select { … }"
    }

/** Reads the nearest [Select] controller. Matches Radix `useSelectContext`. */
@Composable
fun selectState(): SelectController = selectController()

class SelectScope internal constructor()

class SelectContentScope internal constructor()

/**
 * Compound select root matching real shadcn/ui's `Select` composition
 * (`SelectTrigger` / `SelectValue` / `SelectContent` / `SelectItem`).
 *
 * Usage:
 * ```
 * var theme by remember { mutableStateOf<String?>(null) }
 * Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
 *     SelectTrigger()
 *     SelectContent {
 *         SelectItem("Light")
 *         SelectItem("Dark")
 *         SelectItem("System")
 *     }
 * }
 * ```
 */
@Composable
fun <T> Select(
    value: T?,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    label: (T) -> String = { it.toString() },
    placeholder: String = "Select an option",
    invalid: Boolean = false,
    required: Boolean = false,
    disabled: Boolean = false,
    defaultOpen: Boolean = false,
    modal: Boolean = true,
    content: @Composable SelectScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (open == null) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val keyboard = remember { SelectKeyboard() }
    var wasOpen by remember { mutableStateOf(isOpen) }
    LaunchedEffect(isOpen) {
        if (!isOpen && wasOpen) keyboard.trigger.requestFocus()
        wasOpen = isOpen
    }
    val resolvedDisabled = disabled || LocalFieldSetDisabled.current
    val controller =
        remember(
            value,
            onValueChange,
            isOpen,
            setOpen,
            placeholder,
            label,
            invalid,
            required,
            resolvedDisabled,
            modal,
        ) {
            SelectController(
                displayText = value?.let(label),
                placeholder = placeholder,
                open = isOpen,
                setOpen = setOpen,
                invalid = invalid,
                required = required,
                disabled = resolvedDisabled,
                modal = modal,
                onSelect = { raw ->
                    @Suppress("UNCHECKED_CAST")
                    onValueChange(raw as T)
                },
                selectedEquals = { it == value },
                format = { raw ->
                    @Suppress("UNCHECKED_CAST")
                    label(raw as T)
                },
            )
        }
    CompositionLocalProvider(LocalSelect provides controller, LocalSelectKeyboard provides keyboard) {
        Box(modifier = modifier) {
            SelectScope().content()
        }
    }
}

@Composable
fun SelectScope.SelectTrigger(
    modifier: Modifier = Modifier,
    variant: SelectVariant = SelectVariant.Default,
    size: SelectSize = SelectSize.Default,
    style: Style = Style,
    animation: ButtonAnimation = ButtonAnimation.Scale,
    accessibilityLabel: String = "",
    invalid: Boolean = false,
    content: @Composable RowScope.() -> Unit = { SelectValue() },
) {
    val ctrl = selectController()
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = !ctrl.disabled
        }
    val pressStyle = rememberPressMotionStyle(animation)
    val description = accessibilityLabel.ifEmpty { ctrl.displayText ?: ctrl.placeholder }
    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    val density = LocalDensity.current
    val errorColor = theme.colors.destructive
    val resolvedInvalid = invalid || ctrl.invalid
    val invalidStyle =
        if (resolvedInvalid) {
            Style {
                borderColor(if (nova && theme.isDark) errorColor.copy(alpha = errorColor.alpha * 0.5f) else errorColor)
                if (nova) {
                    dropShadow(
                        Shadow(
                            radius = 0.dp,
                            spread = 3.dp,
                            color = errorColor.copy(alpha = if (theme.isDark) 0.4f else 0.2f),
                        ),
                    )
                }
            }
        } else {
            Style
        }

    Row(
        modifier =
            modifier
                .then(if (nova) Modifier.width(IntrinsicSize.Max) else Modifier)
                .onGloballyPositioned {
                    keyboard.width = with(density) { it.size.width.toDp() }
                    keyboard.height = with(density) { it.size.height.toDp() }
                }
                .widthIn(min = if (nova) 0.dp else 140.dp)
                .focusRequester(keyboard.trigger)
                .keyboardInputMode()
                .onPreviewKeyEvent { event ->
                    if (keyboard.consumeRelease(event)) return@onPreviewKeyEvent true
                    if (event.type == KeyEventType.KeyDown &&
                        !ctrl.disabled &&
                        event.key in
                        listOf(
                            Key.DirectionDown,
                            Key.DirectionUp,
                        )
                    ) {
                        keyboard.fromEnd = event.key == Key.DirectionUp
                        ctrl.setOpen(true)
                        true
                    } else {
                        false
                    }
                }
                .semantics(mergeDescendants = true) {
                    role = Role.DropdownList
                    contentDescription = description
                    stateDescription = if (ctrl.open) "Expanded" else "Collapsed"
                    if (ctrl.disabled) disabled()
                    if (resolvedInvalid) error("Invalid selection")
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !ctrl.disabled,
                    role = Role.Button,
                ) { ctrl.setOpen(!ctrl.open) }
                .cssStyleable(styleState, pressStyle then variant.rememberStyle(size) then invalidStyle then style),
        horizontalArrangement = Arrangement.spacedBy(if (nova) 6.dp else twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
        // Official always appends SelectPrimitive.Icon (chevron).
        SelectIcon()
    }
}

/** Chevron inside [SelectTrigger]. Official does not export SelectIcon. */
@Composable
internal fun SelectIcon(modifier: Modifier = Modifier) {
    Icon(
        if (currentTheme.activePreset == StylePreset.Nova) NovaChevronRight else ChevronRight,
        tint = currentTheme.colors.mutedForeground,
        modifier = modifier.rotate(90f),
        variant = if (currentTheme.activePreset == StylePreset.Nova) IconVariant.Default else IconVariant.Small,
    )
}

@Composable
fun SelectScope.SelectValue(
    modifier: Modifier = Modifier,
    placeholder: String? = null,
) {
    val ctrl = selectController()
    val placeholderText = placeholder ?: ctrl.placeholder
    val theme = currentTheme
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        maxLines = if (theme.activePreset == StylePreset.Nova) 1 else Int.MAX_VALUE,
        overflow = if (theme.activePreset == StylePreset.Nova) TextOverflow.Ellipsis else TextOverflow.Clip,
        textStyle =
            if (theme.activePreset == StylePreset.Nova) {
                theme.typography.bodyMedium.copy(
                    fontFamily = theme.fontFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (ctrl.displayText == null) theme.colors.mutedForeground else theme.colors.foreground,
                )
            } else if (ctrl.displayText == null) {
                TextColors.Muted
            } else {
                TextStyle.Default
            },
    ) { ctrl.displayText ?: placeholderText }
}

/**
 * Base UI `SelectContent` `alignItemWithTrigger` (legacy Radix called this
 * `position="item-aligned"`). Default is [AlignItemWithTrigger].
 */
enum class SelectContentPosition {
    /** Popper placement with enter/exit motion (`alignItemWithTrigger = false`). */
    Popper,

    /**
     * Base default `alignItemWithTrigger = true`: no enter/exit fade
     * (`data-[align-trigger=true]:animate-none`). Exact “selected row overlaps
     * trigger” geometry is still approximate.
     */
    AlignItemWithTrigger,

    /** @deprecated Use [AlignItemWithTrigger]. */
    @Deprecated(
        "Renamed for Base UI; use AlignItemWithTrigger",
        ReplaceWith("AlignItemWithTrigger"),
    )
    ItemAligned,
}

object SelectContentDefaults {
    /** Base UI `alignItemWithTrigger = true`. */
    val Position = SelectContentPosition.AlignItemWithTrigger

    /** Base UI `align="center"`. */
    val Align = PopupAlign.Center

    /** Base UI `alignItemWithTrigger` default. */
    const val AlignItemWithTrigger = true
}

@Composable
fun SelectScope.SelectContent(
    modifier: Modifier = Modifier,
    variant: SelectVariant = SelectVariant.Default,
    style: Style = Style,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 0.dp,
    position: SelectContentPosition = SelectContentDefaults.Position,
    /**
     * Base UI Positioner `alignItemWithTrigger`. When non-null, overrides
     * [position] (`true` → [SelectContentPosition.AlignItemWithTrigger]).
     */
    alignItemWithTrigger: Boolean? = null,
    align: PopupAlign = SelectContentDefaults.Align,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    dismissOnClickOutside: Boolean = true,
    content: @Composable SelectContentScope.() -> Unit,
) {
    val ctrl = selectController()
    val scrollState = rememberScrollState()
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    val nova = currentTheme.activePreset == StylePreset.Nova

    @Suppress("DEPRECATION")
    val itemAligned =
        alignItemWithTrigger
            ?: (
                position == SelectContentPosition.AlignItemWithTrigger ||
                    position == SelectContentPosition.ItemAligned
            )
    // Base keeps sideOffset=4 even when alignItemWithTrigger is true.
    val resolvedEnter = if (itemAligned) EnterTransition.None else enter
    val resolvedExit = if (itemAligned) ExitTransition.None else exit
    val density = LocalDensity.current
    var alignedCenter by remember(ctrl.open, ctrl.displayText) { mutableStateOf<Float?>(null) }
    val target =
        keyboard.items.firstOrNull {
            it.enabled && ctrl.isSelected(it.value)
        } ?: keyboard.items.firstOrNull { it.enabled }
    val measuredCenter = target?.let { keyboard.centers[it.value]?.minus(keyboard.panelTop) }
    LaunchedEffect(ctrl.open, measuredCenter) {
        if (ctrl.open && alignedCenter == null && measuredCenter != null) alignedCenter = measuredCenter
    }
    val resolvedOffset =
        if (nova && itemAligned && alignedCenter != null) {
            with(density) {
                -(keyboard.height / 2) - alignedCenter!!.toDp()
            }
        } else {
            sideOffset
        }
    SelectPortal {
        AnchoredPopup(
            open = ctrl.open && !ctrl.disabled,
            onDismissRequest = { ctrl.setOpen(false) },
            offset = resolvedOffset,
            align = align,
            alignOffset = alignOffset,
            dismissOnClickOutside = dismissOnClickOutside,
            focusable = ctrl.modal,
            enter = resolvedEnter,
            exit = resolvedExit,
        ) {
            val panelStyleState = remember { MutableStyleState(MutableInteractionSource()) }
            CompositionLocalProvider(LocalSelectScroll provides scrollState) {
                val scope = SelectContentScope()
                LaunchedEffect(keyboard.items.size) {
                    val available = keyboard.items.filter { it.enabled }
                    val target =
                        available.firstOrNull { ctrl.isSelected(it.value) }
                            ?: if (keyboard.fromEnd) available.lastOrNull() else available.firstOrNull()
                    target?.requester?.requestFocus()
                }
                Column(
                    modifier =
                        modifier
                            .onGloballyPositioned { keyboard.panelTop = it.boundsInRoot().top }
                            .widthIn(min = if (nova) 144.dp else 128.dp, max = 280.dp)
                            .onPreviewKeyEvent {
                                    event ->
                                keyboard.consumeRelease(event) ||
                                    (
                                        event.type == KeyEventType.KeyDown &&
                                            keyboard.move(
                                                event.key,
                                                ctrl,
                                            )
                                    )
                            }
                            .styleable(
                                panelStyleState,
                                variant.rememberPanelStyle() then (
                                    if (nova) {
                                        Style {
                                            width(keyboard.width.coerceAtLeast(144.dp))
                                        }
                                    } else {
                                        Style
                                    }
                                ) then style,
                            ),
                ) {
                    // Official Content always composes ScrollUp + Viewport + ScrollDown.
                    scope.SelectScrollUpButton()
                    SelectViewport(
                        modifier =
                            (
                                if (nova) {
                                    Modifier.heightIn(
                                        max = 288.dp,
                                    )
                                } else {
                                    Modifier
                                }
                            ).verticalScroll(scrollState),
                    ) {
                        scope.content()
                    }
                    scope.SelectScrollDownButton()
                }
            }
        }
    }
}

/** Teleport host for [SelectContent]. Official does not export SelectPortal. */
@Composable
internal fun SelectPortal(content: @Composable () -> Unit) {
    content()
}

/** Scrollable panel of [SelectContent]. Official does not export SelectViewport. */
@Composable
internal fun SelectViewport(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier, content = content)
}

/**
 * Chevron above a long [SelectContent] list. Matches shadcn's
 * `SelectScrollUpButton`. Always composed by [SelectContent].
 */
@Composable
fun SelectContentScope.SelectScrollUpButton(modifier: Modifier = Modifier) {
    val scrollState = LocalSelectScroll.current
    if (currentTheme.activePreset == StylePreset.Nova && scrollState?.canScrollBackward != true) return
    val scope = rememberCoroutineScope()
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button) {
                    scope.launch { scrollState?.scrollBy(-48f) }
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (currentTheme.activePreset == StylePreset.Nova) NovaChevronRight else ChevronRight,
            tint = currentTheme.colors.mutedForeground,
            modifier = Modifier.rotate(-90f),
            variant = if (currentTheme.activePreset == StylePreset.Nova) IconVariant.Default else IconVariant.Small,
        )
    }
}

/**
 * Chevron below a long [SelectContent] list. Matches shadcn's
 * `SelectScrollDownButton`. Always composed by [SelectContent].
 */
@Composable
fun SelectContentScope.SelectScrollDownButton(modifier: Modifier = Modifier) {
    val scrollState = LocalSelectScroll.current
    if (currentTheme.activePreset == StylePreset.Nova && scrollState?.canScrollForward != true) return
    val scope = rememberCoroutineScope()
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button) {
                    scope.launch { scrollState?.scrollBy(48f) }
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (currentTheme.activePreset == StylePreset.Nova) NovaChevronRight else ChevronRight,
            tint = currentTheme.colors.mutedForeground,
            modifier = Modifier.rotate(90f),
            variant = if (currentTheme.activePreset == StylePreset.Nova) IconVariant.Default else IconVariant.Small,
        )
    }
}

/**
 * One option row in a [Select]. Matches shadcn's `SelectItem`.
 * Always wraps label in ItemText and paints ItemIndicator internally (not exported).
 * [onSelect] is Radix `onSelect` (return true to prevent selecting).
 */
@Composable
fun <T> SelectContentScope.SelectItem(
    value: T,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    textValue: String? = null,
    onSelect: (() -> Boolean)? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    val ctrl = selectController()
    val resolvedEnabled = enabled && !disabled
    val isSelected = ctrl.isSelected(value)
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    val requester = remember { FocusRequester() }
    DisposableEffect(keyboard, value) {
        keyboard.items.add(SelectFocusItem(value, requester, resolvedEnabled))
        onDispose {
            keyboard.items.removeAll { it.requester === requester }
            keyboard.pick.remove(value)
            keyboard.centers.remove(value)
        }
    }
    SideEffect {
        keyboard.pick[value] = { if (onSelect?.invoke() != true) ctrl.select(value) }
        val index = keyboard.items.indexOfFirst { it.requester === requester }
        if (index >= 0 &&
            keyboard.items[index].enabled != resolvedEnabled
        ) {
            keyboard.items[index] =
                SelectFocusItem(
                    value,
                    requester,
                    resolvedEnabled,
                )
        }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val labelText = textValue ?: ctrl.labelOf(value)
    val isHovered by interactionSource.collectIsHoveredAsState()
    val itemContentColor =
        if (!resolvedEnabled) {
            currentTheme.colors.popoverForeground
        } else if (isHovered) {
            currentTheme.colors.accentForeground
        } else {
            currentTheme.colors.popoverForeground
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .focusRequester(requester)
                .onGloballyPositioned { keyboard.centers[value] = it.boundsInRoot().center.y }
                .onFocusChanged { if (it.isFocused) keyboard.active = value }
                .semantics {
                    role = Role.Button
                    this.selected = isSelected
                    if (!resolvedEnabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    // Radix `onSelect` preventDefault: true skips selecting (and closing).
                    onClick = {
                        if (onSelect?.invoke() != true) ctrl.select(value)
                    },
                )
                .styleable(styleState, rememberSelectItemStyle()),
        horizontalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) 6.dp else twSpacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides itemContentColor,
            LocalSelectItemSelected provides isSelected,
            LocalDefaultTextStyle provides
                if (currentTheme.activePreset == StylePreset.Nova) {
                    currentTheme.typography.bodyMedium.copy(
                        fontFamily =
                            currentTheme.fontFamily,
                        fontSize =
                            14.sp,
                        lineHeight =
                            20.sp,
                    )
                } else {
                    LocalDefaultTextStyle.current
                },
        ) {
            if (content != null) {
                SelectItemText {
                    content()
                }
            } else {
                SelectItemText(labelText)
            }
            SelectItemIndicator()
        }
    }
}

/** Label of a [SelectItem]. Official does not export SelectItemText. */
@Composable
internal fun RowScope.SelectItemText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val theme = currentTheme
    if (theme.activePreset != StylePreset.Nova) {
        Text(modifier = modifier.weight(1f), variant = TextVariant.BodySmall) { text }
        return
    }
    // CSS's single-line 20px box is independent of the fallback font's
    // natural ascent/descent. The clickable row owns the merged label semantics.
    Box(
        modifier = modifier.weight(1f).height(with(LocalDensity.current) { 20.sp.toDp() }),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            // Match shrink-0/whitespace-nowrap while preserving tall glyph ink.
            modifier =
                Modifier.wrapContentWidth(Alignment.Start, unbounded = true)
                    .wrapContentHeight(Alignment.CenterVertically, unbounded = true),
            maxLines = 1,
            variant = TextVariant.BodyMedium,
            textStyle =
                theme.typography.bodyMedium.copy(
                    fontFamily = theme.fontFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
        ) { text }
    }
}

/** Slot form used when [SelectItem] has custom children. */
@Composable
internal fun RowScope.SelectItemText(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** Check mark slot of a [SelectItem]. Official does not export SelectItemIndicator. */
@Composable
internal fun SelectItemIndicator(
    selected: Boolean =
        checkNotNull(LocalSelectItemSelected.current) {
            "SelectItemIndicator must be used inside SelectItem, or pass selected="
        },
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Box(
        modifier =
            modifier.size(
                if (currentTheme.activePreset == StylePreset.Nova) 16.dp else currentTheme.icons.smallSize,
            ),
    ) {
        if (selected) {
            Icon(
                if (currentTheme.activePreset == StylePreset.Nova) NovaCheck else Check,
                tint = tint,
                variant = if (currentTheme.activePreset == StylePreset.Nova) IconVariant.Default else IconVariant.Small,
            )
        }
    }
}

/**
 * Optional grouping wrapper for [SelectLabel] + [SelectItem]s. Matches shadcn's
 * `SelectGroup`.
 */
@Composable
fun SelectContentScope.SelectGroup(
    modifier: Modifier = Modifier,
    heading: String? = null,
    content: @Composable SelectContentScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (heading != null) {
            SelectLabel(heading)
        }
        content()
    }
}

@Composable
fun SelectContentScope.SelectLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val labelStyle =
        if (nova) {
            Style {
                minHeight(24.dp)
                contentPadding(PaddingValues(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp))
            }
        } else {
            Style
        }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    if (nova) {
        Box(
            modifier =
                modifier.semantics(mergeDescendants = true) {
                }.styleable(
                    styleState,
                    rememberMenuLabelStyle() then labelStyle then style,
                ),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                textStyle =
                    theme.typography.bodySmall.copy(
                        fontFamily = theme.fontFamily,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = theme.colors.mutedForeground,
                    ),
            ) {
                text
            }
        }
    } else {
        Text(
            variant = TextVariant.LabelSmall,
            modifier = modifier.styleable(styleState, rememberMenuLabelStyle() then style),
            textStyle = TextColors.Muted,
        ) {
            text
        }
    }
}

@Composable
@JvmName("SelectLabelLambda")
@OverloadResolutionByLambdaReturnType
fun SelectContentScope.SelectLabel(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    SelectLabel(text = text(), modifier = modifier, style = style)
}

@Composable
fun SelectSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    Separator(modifier = modifier, style = style)
}
