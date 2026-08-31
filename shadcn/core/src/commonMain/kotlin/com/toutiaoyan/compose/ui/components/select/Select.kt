@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ScrollState
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
    val setOpen =
        remember(onOpenChange) {
            onOpenChange ?: { internalOpen = it }
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
            modal
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
    CompositionLocalProvider(LocalSelect provides controller) {
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
    val errorColor = currentTheme.colors.destructive
    val resolvedInvalid = invalid || ctrl.invalid
    val invalidStyle = if (resolvedInvalid) Style { borderColor(errorColor) } else Style

    Row(
        modifier =
            modifier
                .widthIn(min = 140.dp)
                .semantics(mergeDescendants = true) {
                    role = Role.DropdownList
                    contentDescription = description
                    stateDescription = if (ctrl.open) "Expanded" else "Collapsed"
                    if (ctrl.disabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !ctrl.disabled,
                    role = Role.Button,
                ) { ctrl.setOpen(!ctrl.open) }
                .styleable(styleState, pressStyle then variant.rememberStyle(size) then invalidStyle then style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
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
internal fun SelectIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        ChevronRight,
        tint = currentTheme.colors.mutedForeground,
        modifier = modifier.rotate(90f),
        variant = IconVariant.Small,
    )
}

@Composable
fun SelectScope.SelectValue(
    modifier: Modifier = Modifier,
    placeholder: String? = null,
) {
    val ctrl = selectController()
    val placeholderText = placeholder ?: ctrl.placeholder
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle = if (ctrl.displayText == null) TextColors.Muted else TextStyle.Default,
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
    @Suppress("DEPRECATION")
    val itemAligned =
        alignItemWithTrigger
            ?: (position == SelectContentPosition.AlignItemWithTrigger ||
                position == SelectContentPosition.ItemAligned)
    // Base keeps sideOffset=4 even when alignItemWithTrigger is true.
    val resolvedEnter = if (itemAligned) EnterTransition.None else enter
    val resolvedExit = if (itemAligned) ExitTransition.None else exit
    SelectPortal {
        AnchoredPopup(
            open = ctrl.open,
            onDismissRequest = { ctrl.setOpen(false) },
            offset = sideOffset,
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
                Column(
                    modifier =
                        modifier
                            .widthIn(min = 128.dp, max = 280.dp)
                            .styleable(panelStyleState, variant.rememberPanelStyle() then style),
                ) {
                    // Official Content always composes ScrollUp + Viewport + ScrollDown.
                    scope.SelectScrollUpButton()
                    SelectViewport(
                        modifier = Modifier.verticalScroll(scrollState),
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
fun SelectContentScope.SelectScrollUpButton(
    modifier: Modifier = Modifier,
) {
    val scrollState = LocalSelectScroll.current
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
            ChevronRight,
            tint = currentTheme.colors.mutedForeground,
            modifier = Modifier.rotate(-90f),
            variant = IconVariant.Small,
        )
    }
}

/**
 * Chevron below a long [SelectContent] list. Matches shadcn's
 * `SelectScrollDownButton`. Always composed by [SelectContent].
 */
@Composable
fun SelectContentScope.SelectScrollDownButton(
    modifier: Modifier = Modifier,
) {
    val scrollState = LocalSelectScroll.current
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
            ChevronRight,
            tint = currentTheme.colors.mutedForeground,
            modifier = Modifier.rotate(90f),
            variant = IconVariant.Small,
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
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides itemContentColor,
            LocalSelectItemSelected provides isSelected,
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
    Text(
        modifier = modifier.weight(1f),
        variant = TextVariant.BodySmall,
    ) { text }
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
    selected: Boolean = checkNotNull(LocalSelectItemSelected.current) {
        "SelectItemIndicator must be used inside SelectItem, or pass selected="
    },
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Box(modifier = modifier.size(currentTheme.icons.smallSize)) {
        if (selected) {
            Icon(
                Check,
                tint = tint,
                variant = IconVariant.Small,
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
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.LabelSmall,
        modifier = modifier.styleable(styleState, rememberMenuLabelStyle() then style),
        textStyle = TextColors.Muted,
    ) { text }
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
