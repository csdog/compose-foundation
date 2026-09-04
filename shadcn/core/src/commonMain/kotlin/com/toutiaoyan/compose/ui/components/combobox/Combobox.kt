@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.combobox

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.button.rememberStyle
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputVariant
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.icons.X
import com.toutiaoyan.compose.ui.styles.rememberMenuItemStyle
import com.toutiaoyan.compose.ui.styles.rememberMenuLabelStyle
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * Shared combobox state. Type-erased at the Local boundary; [select] / [isSelected] /
 * [labelOf] close over the root's `T` safely. Real shadcn's combobox is a
 * Popover + Command recipe; this is that recipe as a compound component.
 */
@Immutable
class ComboboxController internal constructor(
    val displayText: String?,
    val placeholder: String,
    val searchPlaceholder: String,
    val open: Boolean,
    val setOpen: (Boolean) -> Unit,
    val query: String,
    val setQuery: (String) -> Unit,
    val emptyText: String,
    val filtered: List<Any?>,
    val invalid: Boolean,
    val required: Boolean,
    val disabled: Boolean,
    val modal: Boolean,
    val multiple: Boolean,
    val canClear: Boolean,
    private val highlighted: Any?,
    private val onSelect: (Any?) -> Unit,
    private val onClearSelection: () -> Unit,
    private val selectedEquals: (Any?) -> Boolean,
    private val format: (Any?) -> String,
) {
    fun toggle() = setOpen(!open)

    fun select(value: Any?) {
        onSelect(value)
        setQuery("")
        if (!multiple) setOpen(false)
    }

    fun clear() {
        if (!canClear) return
        onClearSelection()
        setQuery("")
    }

    fun isSelected(value: Any?): Boolean = selectedEquals(value)

    fun isHighlighted(value: Any?): Boolean = highlighted != null && highlighted == value

    fun labelOf(value: Any?): String = format(value)
}

private val LocalCombobox = staticCompositionLocalOf<ComboboxController?> { null }

private val LocalComboboxItemSelected = staticCompositionLocalOf<Boolean?> { null }

@Composable
private fun comboboxController(): ComboboxController =
    checkNotNull(LocalCombobox.current) {
        "ComboboxTrigger / ComboboxContent must be used inside Combobox { … }"
    }

/** Reads the nearest [Combobox] scope. Matches the combobox recipe's popover context. */
@Composable
fun comboboxState(): ComboboxScope = ComboboxScope(comboboxController())

/** Slot receiver for [ComboboxTrigger] / [ComboboxContent]. */
class ComboboxScope internal constructor(
    private val controller: ComboboxController,
) {
    val open: Boolean get() = controller.open

    val toggle: () -> Unit get() = controller::toggle
}

internal val DefaultComboboxFilter: (String, String) -> Boolean =
    { query, value -> value.contains(query, ignoreCase = true) }

/**
 * A searchable select. Matches real shadcn/ui's combobox recipe (Popover trigger
 * + Command search list) as a compound API:
 *
 * ```
 * Combobox
 * ├── ComboboxTrigger
 * └── ComboboxContent
 *     ├── ComboboxInput
 *     ├── ComboboxEmpty
 *     └── ComboboxItem
 *
 * Multiple (official `multiple` + chips):
 * Combobox(values, onValuesChange)
 * ├── ComboboxChips
 * │   ├── ComboboxChip
 * │   └── ComboboxChipsInput
 * └── ComboboxContent
 * ```
 *
 * Unlike plain Select, the option list is filterable by typing.
 *
 * Usage:
 * ```
 * var framework by remember { mutableStateOf<String?>(null) }
 * Combobox(
 *     value = framework,
 *     options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
 *     onValueChange = { framework = it },
 * ) {
 *     ComboboxTrigger()
 *     ComboboxContent()
 * }
 *
 * Combobox(..., defaultQuery = "re") { … }
 * Combobox(..., invalid = true) { … }
 * Combobox(..., shouldFilter = false) { … }
 * Combobox(..., filter = { query, value -> value.startsWith(query, ignoreCase = true) }) { … }
 * Combobox(..., itemToStringValue = { it.label }) { … }
 * Combobox(..., autoHighlight = true) { … }
 * ```
 */
@Composable
fun <T> Combobox(
    value: T?,
    options: List<T>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    label: (T) -> String = { it.toString() },
    itemToStringValue: (T) -> String = label,
    placeholder: String = "Select...",
    searchPlaceholder: String = "Search...",
    emptyText: String = "No results found.",
    defaultQuery: String = "",
    invalid: Boolean = false,
    required: Boolean = false,
    disabled: Boolean = false,
    modal: Boolean = true,
    shouldFilter: Boolean = true,
    filter: (query: String, value: String) -> Boolean = DefaultComboboxFilter,
    keywords: (T) -> List<String> = { emptyList() },
    defaultOpen: Boolean = false,
    autoHighlight: Boolean = false,
    onClear: (() -> Unit)? = null,
    content: @Composable ComboboxScope.() -> Unit,
) {
    ComboboxRoot(
        selected = listOfNotNull(value),
        multiple = false,
        onPick = onValueChange,
        canClear = onClear != null,
        onClearSelection = { onClear?.invoke() },
        options = options,
        modifier = modifier,
        open = open,
        onOpenChange = onOpenChange,
        label = itemToStringValue,
        placeholder = placeholder,
        searchPlaceholder = searchPlaceholder,
        emptyText = emptyText,
        defaultQuery = defaultQuery,
        invalid = invalid,
        required = required,
        disabled = disabled,
        modal = modal,
        shouldFilter = shouldFilter,
        filter = filter,
        keywords = keywords,
        autoHighlight = autoHighlight,
        defaultOpen = defaultOpen,
        content = content,
    )
}

/**
 * Official `multiple` combobox. [values] / [onValuesChange] replace single [value].
 * Compose [ComboboxChips] + [ComboboxChip] + [ComboboxChipsInput] as the trigger.
 */
@Composable
fun <T> Combobox(
    values: List<T>,
    options: List<T>,
    onValuesChange: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    label: (T) -> String = { it.toString() },
    itemToStringValue: (T) -> String = label,
    placeholder: String = "Select...",
    searchPlaceholder: String = "Search...",
    emptyText: String = "No results found.",
    defaultQuery: String = "",
    invalid: Boolean = false,
    required: Boolean = false,
    disabled: Boolean = false,
    modal: Boolean = true,
    shouldFilter: Boolean = true,
    filter: (query: String, value: String) -> Boolean = DefaultComboboxFilter,
    keywords: (T) -> List<String> = { emptyList() },
    defaultOpen: Boolean = false,
    autoHighlight: Boolean = false,
    content: @Composable ComboboxScope.() -> Unit,
) {
    ComboboxRoot(
        selected = values,
        multiple = true,
        onPick = { item ->
            onValuesChange(if (item in values) values - item else values + item)
        },
        canClear = true,
        onClearSelection = { onValuesChange(emptyList()) },
        options = options,
        modifier = modifier,
        open = open,
        onOpenChange = onOpenChange,
        label = itemToStringValue,
        placeholder = placeholder,
        searchPlaceholder = searchPlaceholder,
        emptyText = emptyText,
        defaultQuery = defaultQuery,
        invalid = invalid,
        required = required,
        disabled = disabled,
        modal = modal,
        shouldFilter = shouldFilter,
        filter = filter,
        keywords = keywords,
        defaultOpen = defaultOpen,
        autoHighlight = autoHighlight,
        content = content,
    )
}

@Composable
private fun <T> ComboboxRoot(
    selected: List<T>,
    multiple: Boolean,
    onPick: (T) -> Unit,
    canClear: Boolean,
    onClearSelection: () -> Unit,
    options: List<T>,
    modifier: Modifier,
    open: Boolean?,
    onOpenChange: ((Boolean) -> Unit)?,
    label: (T) -> String,
    placeholder: String,
    searchPlaceholder: String,
    emptyText: String,
    defaultQuery: String,
    invalid: Boolean,
    required: Boolean,
    disabled: Boolean,
    modal: Boolean,
    shouldFilter: Boolean,
    filter: (query: String, value: String) -> Boolean,
    keywords: (T) -> List<String>,
    defaultOpen: Boolean,
    autoHighlight: Boolean,
    content: @Composable ComboboxScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    var query by remember { mutableStateOf(defaultQuery) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val filtered =
        remember(options, query, label, shouldFilter, filter, keywords) {
            if (!shouldFilter || query.isBlank()) {
                options
            } else {
                options.filter { option ->
                    filter(query, label(option)) || keywords(option).any { filter(query, it) }
                }
            }
        }
    val displayText =
        when {
            selected.isEmpty() -> null
            !multiple -> selected.firstOrNull()?.let(label)
            else -> selected.joinToString { label(it) }
        }
    val controller =
        ComboboxController(
            displayText = displayText,
            placeholder = placeholder,
            searchPlaceholder = searchPlaceholder,
            open = isOpen,
            setOpen = setOpen,
            query = query,
            setQuery = { query = it },
            emptyText = emptyText,
            filtered = filtered,
            invalid = invalid,
            required = required,
            disabled = disabled || LocalFieldSetDisabled.current,
            modal = modal,
            multiple = multiple,
            canClear = canClear,
            highlighted =
                if (autoHighlight && query.isNotBlank() && filtered.isNotEmpty()) {
                    filtered.first()
                } else {
                    null
                },
            onSelect = { raw ->
                @Suppress("UNCHECKED_CAST")
                onPick(raw as T)
            },
            onClearSelection = onClearSelection,
            selectedEquals = { candidate ->
                selected.any { it == candidate }
            },
            format = { raw ->
                @Suppress("UNCHECKED_CAST")
                label(raw as T)
            },
        )
    CompositionLocalProvider(LocalCombobox provides controller) {
        Box(modifier = modifier) {
            ComboboxScope(controller).content()
        }
    }
}

/**
 * Trigger button for the parent [Combobox] (`justify-between` outline button +
 * chevron). [showClear] is official ComboboxInput `showClear` (default false).
 * [showTrigger] is official ComboboxInput `showTrigger` (default true).
 * Override [icon] or [content] for custom chrome.
 */
@Composable
fun ComboboxScope.ComboboxTrigger(
    modifier: Modifier = Modifier,
    style: Style = Style,
    invalid: Boolean = false,
    accessibilityLabel: String = "",
    showClear: Boolean = false,
    showTrigger: Boolean = true,
    icon: @Composable () -> Unit = { ComboboxIcon() },
    content: @Composable RowScope.() -> Unit = { ComboboxValue() },
) {
    val ctrl = comboboxController()
    val triggerInteractionSource = remember { MutableInteractionSource() }
    val triggerStyleState =
        rememberUpdatedStyleState(triggerInteractionSource) { it.isEnabled = !ctrl.disabled }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val triggerDescription = accessibilityLabel.ifEmpty { ctrl.displayText ?: ctrl.placeholder }
    val errorColor = currentTheme.colors.destructive
    val resolvedInvalid = invalid || ctrl.invalid
    val invalidStyle = if (resolvedInvalid) Style { borderColor(errorColor) } else Style

    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription =
                        if (ctrl.required) "$triggerDescription, required" else triggerDescription
                    stateDescription = if (ctrl.open) "Expanded" else "Collapsed"
                    if (ctrl.disabled) disabled()
                }
                .clickable(
                    interactionSource = triggerInteractionSource,
                    indication = null,
                    enabled = !ctrl.disabled,
                    role = Role.Button,
                ) { ctrl.toggle() }
                .styleable(triggerStyleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .styleable(
                        triggerStyleState,
                        rememberComboboxTriggerStyle() then
                            ButtonVariant.Outline.rememberStyle() then
                            ButtonSize.Md.rememberStyle() then
                            invalidStyle then
                            style,
                    ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
            if (showClear || showTrigger) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (showClear) ComboboxClear()
                    if (showTrigger) icon()
                }
            }
        }
    }
}

/** Chevron inside [ComboboxTrigger]. Base UI does not export ComboboxIcon (always in Trigger). */
@Composable
internal fun ComboboxIcon(modifier: Modifier = Modifier) {
    Icon(
        ChevronRight,
        tint = currentTheme.colors.mutedForeground,
        modifier = modifier.rotate(90f),
        variant = IconVariant.Small,
    )
}

/**
 * Clear control for Trigger/Input `showClear`. Base UI defines but does not export
 * `ComboboxClear` (only used inside Input).
 */
@Composable
internal fun ComboboxClear(
    modifier: Modifier = Modifier,
    contentDescription: String = "Clear",
) {
    val ctrl = comboboxController()
    if (!ctrl.canClear || ctrl.displayText == null) return
    Box(
        modifier =
            modifier
                .semantics { this.contentDescription = contentDescription }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !ctrl.disabled,
                    onClick = { ctrl.clear() },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            X,
            tint = currentTheme.colors.mutedForeground,
            variant = IconVariant.Small,
        )
    }
}

/** Selected-value label inside [ComboboxTrigger]. Matches shadcn's value slot. */
@Composable
fun ComboboxValue(
    modifier: Modifier = Modifier,
    placeholder: String? = null,
) {
    val ctrl = comboboxController()
    Text(
        modifier = modifier,
        textStyle = if (ctrl.displayText == null) TextColors.Muted else TextStyle.Default,
    ) { ctrl.displayText ?: (placeholder ?: ctrl.placeholder) }
}

/**
 * Filterable option panel of a [Combobox]. Default slot is search + items;
 * pass [content] to compose [ComboboxInput] / [ComboboxEmpty] / [ComboboxItem]
 * yourself (shadcn Command-inside-Popover recipe).
 */
@Composable
fun ComboboxScope.ComboboxContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    // Base UI / base-nova Content default `sideOffset = 6`, `align = "start"`, `alignOffset = 0`.
    sideOffset: Dp = 6.dp,
    align: PopupAlign = PopupAlign.Start,
    alignOffset: Dp = 0.dp,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    dismissOnClickOutside: Boolean = true,
    content: @Composable ColumnScope.() -> Unit = { ComboboxDefaultList() },
) {
    val ctrl = comboboxController()
    val panelStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    ComboboxPortal {
        AnchoredPopup(
            open = ctrl.open,
            onDismissRequest = { ctrl.setOpen(false) },
            offset = sideOffset,
            align = align,
            alignOffset = alignOffset,
            dismissOnClickOutside = dismissOnClickOutside,
            focusable = ctrl.modal,
            enter = enter,
            exit = exit,
        ) {
            Column(
                modifier =
                    modifier.styleable(
                        panelStyleState,
                        rememberComboboxPanelStyle() then style,
                    ),
                content = content,
            )
        }
    }
}

/** Teleport host for [ComboboxContent]. Base UI does not export ComboboxPortal. */
@Composable
internal fun ComboboxPortal(content: @Composable () -> Unit) {
    content()
}

@Composable
private fun ComboboxDefaultList() {
    val ctrl = comboboxController()
    ComboboxInput()
    ComboboxList {
        if (ctrl.filtered.isEmpty()) {
            ComboboxEmpty()
        } else {
            ctrl.filtered.forEach { option ->
                ComboboxItem(
                    value = option,
                    label = ctrl.labelOf(option),
                )
            }
        }
    }
}

/** Option list inside [ComboboxContent]. Matches shadcn CommandList in the combobox recipe. */
@Composable
fun ComboboxList(content: @Composable () -> Unit) {
    content()
}

/** Search field inside [ComboboxContent]. Matches the Command input in shadcn's combobox recipe. */
@Composable
fun ComboboxInput(
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    disabled: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
) {
    val ctrl = comboboxController()
    val resolvedPlaceholder = placeholder ?: ctrl.searchPlaceholder
    if (icon == null) {
        Input(
            value = ctrl.query,
            onValueChange = ctrl.setQuery,
            placeholder = resolvedPlaceholder,
            variant = InputVariant.Ghost,
            disabled = disabled,
            modifier = modifier.fillMaxWidth(),
        )
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        ) {
            icon()
            Input(
                value = ctrl.query,
                onValueChange = ctrl.setQuery,
                placeholder = resolvedPlaceholder,
                variant = InputVariant.Ghost,
                disabled = disabled,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Empty-state row when no options match the query. */
@Composable
fun ComboboxEmpty(
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    val ctrl = comboboxController()
    Box(modifier = modifier.fillMaxWidth()) {
        Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { text ?: ctrl.emptyText }
    }
}

/** One option row in a [Combobox]. [onSelect] is Radix `onSelect` (return true to prevent selecting). */
@Composable
fun ComboboxItem(
    value: Any?,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    onSelect: (() -> Boolean)? = null,
    textValue: String? = null,
    content: @Composable RowScope.() -> Unit = {
        ComboboxItemIndicator()
        Text(variant = TextVariant.BodySmall) { label }
    },
) {
    val ctrl = comboboxController()
    val resolvedEnabled = enabled && !disabled
    val selected = ctrl.isSelected(value)
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val highlightStyle =
        if (ctrl.isHighlighted(value)) rememberComboboxHighlightedItemStyle() else Style
    Row(
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
                        if (onSelect?.invoke() != true) ctrl.select(value)
                    },
                )
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberMenuItemStyle() then highlightStyle,
                ),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalComboboxItemSelected provides selected) {
            content()
        }
    }
}

/** Check mark on a selected [ComboboxItem]. Base UI does not export ItemIndicator. */
@Composable
internal fun ComboboxItemIndicator(
    selected: Boolean =
        checkNotNull(LocalComboboxItemSelected.current) {
            "ComboboxItemIndicator must be used inside ComboboxItem, or pass selected="
        },
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(currentTheme.icons.smallSize)) {
        if (selected) {
            Icon(Check, tint = currentTheme.colors.foreground, variant = IconVariant.Small)
        }
    }
}

/** Optional grouping wrapper. Matches shadcn CommandGroup in the combobox recipe. */
@Composable
fun ComboboxGroup(
    modifier: Modifier = Modifier,
    heading: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (heading != null) {
            ComboboxLabel(text = heading)
        }
        content()
    }
}

/** Section heading inside [ComboboxContent]. */
@Composable
fun ComboboxLabel(
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
@JvmName("ComboboxLabelLambda")
@OverloadResolutionByLambdaReturnType
fun ComboboxLabel(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    ComboboxLabel(text = text(), modifier = modifier, style = style)
}

/** Divider between groups. */
@Composable
fun ComboboxSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    Separator(modifier = modifier, style = style)
}

/**
 * Official `ComboboxCollection` — Base UI maps a collection; Compose callers already
 * `forEach` into [ComboboxItem], so this is a name-parity passthrough.
 */
@Composable
fun ComboboxCollection(content: @Composable () -> Unit) {
    content()
}

/**
 * Official `ComboboxChips` — bordered wrap that hosts [ComboboxChip]s + [ComboboxChipsInput].
 * Click / focus opens the list. Default stays closed so idle goldens are unchanged.
 */
@Composable
fun ComboboxChips(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val ctrl = comboboxController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    var hasFocusWithin by remember { mutableStateOf(false) }
    val chipsStyle = rememberComboboxChipsStyle(focused = hasFocusWithin, invalid = ctrl.invalid)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .onFocusEvent { hasFocusWithin = it.hasFocus }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !ctrl.disabled,
                    onClick = { ctrl.setOpen(true) },
                )
                .styleable(styleState, chipsStyle then style),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/**
 * One selected value inside [ComboboxChips]. [showRemove] is official `showRemove` (default true).
 * Remove toggles the value out of the multiple selection.
 */
@Composable
fun ComboboxChip(
    value: Any?,
    label: String,
    modifier: Modifier = Modifier,
    showRemove: Boolean = true,
    style: Style = Style,
) {
    val ctrl = comboboxController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, rememberComboboxChipStyle() then style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(variant = TextVariant.LabelSmall) { label }
        if (showRemove) {
            Box(
                modifier =
                    Modifier
                        .semantics { contentDescription = "Remove $label" }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = !ctrl.disabled,
                            onClick = { ctrl.select(value) },
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    X,
                    tint = currentTheme.colors.mutedForeground,
                    variant = IconVariant.Small,
                )
            }
        }
    }
}

/** Inline query field inside [ComboboxChips]. Official `ComboboxChipsInput`. */
@Composable
fun RowScope.ComboboxChipsInput(
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    disabled: Boolean = false,
) {
    val ctrl = comboboxController()
    Input(
        value = ctrl.query,
        onValueChange = {
            ctrl.setQuery(it)
            if (!ctrl.open && it.isNotEmpty()) ctrl.setOpen(true)
        },
        placeholder = placeholder ?: ctrl.searchPlaceholder,
        variant = InputVariant.Ghost,
        disabled = disabled || ctrl.disabled,
        modifier = modifier.weight(1f),
    )
}
