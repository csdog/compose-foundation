@file:OptIn(
    androidx.compose.ui.ExperimentalComposeUiApi::class,
    ExperimentalFoundationStyleApi::class,
    ExperimentalTypeInference::class,
)

package com.toutiaoyan.compose.ui.components.command

import com.toutiaoyan.compose.ui.components.input.LocalInputCompositionObserver
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.style.hovered
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import com.toutiaoyan.compose.ui.icons.NovaCommandKey
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.contentPadding
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.overlay.overlayTypography
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.icons.NovaSearch
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuSeparator
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputVariant
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.styles.rememberMenuItemStyle
import com.toutiaoyan.compose.ui.styles.rememberMenuLabelStyle
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

internal class CommandNavItem(
    val id: Any,
    val value: String,
    val enabled: Boolean,
    val onSelect: () -> Unit,
)

internal class CommandController(
    initialQuery: String = "",
) {
    var query by mutableStateOf(initialQuery)
    var visibleCount by mutableIntStateOf(0)
    var shouldFilter: Boolean = true
    var filter: (query: String, value: String) -> Boolean = DefaultCommandFilter
    var loop: Boolean = true
    var label: String = "Command Menu"
    var highlightedId by mutableStateOf<Any?>(null)
    var currentValue by mutableStateOf("")
    var navigationRevision by mutableIntStateOf(0)
    var isComposing: Boolean = false
    var controlled: Boolean = false
    var onValueChange: ((String) -> Unit)? = null
    val items = mutableStateListOf<CommandNavItem>()

    // Keep mounted rows in composition order while filtering temporarily removes navigation entries.
    val itemOrder = mutableListOf<Any>()

    fun matches(
        value: String,
        keywords: List<String> = emptyList(),
    ): Boolean {
        if (!shouldFilter || query.isBlank()) return true
        if (filter(query, value)) return true
        return keywords.any { filter(query, it) }
    }

    fun stepHighlight(delta: Int) {
        val enabled = items.filter { it.enabled }
        if (enabled.isEmpty()) return
        val idx = enabled.indexOfFirst { it.id == highlightedId }
        val nextIdx =
            when {
                idx < 0 -> if (delta > 0) 0 else enabled.lastIndex
                else -> {
                    val raw = idx + delta
                    when {
                        loop -> (raw % enabled.size + enabled.size) % enabled.size
                        raw in enabled.indices -> raw
                        else -> return
                    }
                }
            }
        highlight(enabled[nextIdx])
    }

    fun highlight(item: CommandNavItem) {
        navigationRevision++
        if (!controlled) highlightedId = item.id
        if (currentValue != item.value) {
            if (!controlled) currentValue = item.value
            onValueChange?.invoke(item.value)
        }
    }

    fun selectHighlighted(): Boolean {
        val item = items.firstOrNull { it.id == highlightedId } ?: return false
        if (!item.enabled) return false
        item.onSelect()
        return true
    }
}

private fun handleCommandKey(
    ctrl: CommandController,
    event: KeyEvent,
): Boolean {
    if (event.type != KeyEventType.KeyDown || ctrl.isComposing) return false
    return when (event.key) {
        Key.DirectionDown -> {
            ctrl.stepHighlight(1)
            true
        }
        Key.DirectionUp -> {
            ctrl.stepHighlight(-1)
            true
        }
        Key.MoveHome, Key.MoveEnd -> {
            val item =
                (
                    if (event.key == Key.MoveHome) {
                        ctrl.items
                    } else {
                        ctrl.items.asReversed()
                    }
                ).firstOrNull { it.enabled }
            if (item != null) ctrl.highlight(item)
            item != null
        }
        Key.Enter, Key.NumPadEnter -> ctrl.selectHighlighted()
        else -> false
    }
}

internal val DefaultCommandFilter: (String, String) -> Boolean =
    { query, value -> value.contains(query, ignoreCase = true) }

private val LocalCommandGroup =
    staticCompositionLocalOf<androidx.compose.runtime.snapshots.SnapshotStateMap<Any, Boolean>?> { null }

private val LocalCommand = staticCompositionLocalOf<CommandController?> { null }

@Composable
private fun commandController(): CommandController =
    checkNotNull(LocalCommand.current) {
        "CommandInput / CommandList / CommandItem must be used inside Command { … }"
    }

/** Filter state of the nearest [Command]. Matches cmdk / shadcn command context. */
class CommandState(
    val query: String,
    val visibleCount: Int,
    private val match: (String) -> Boolean,
) {
    fun matches(label: String): Boolean = match(label)
}

/** Reads the nearest [Command] state. */
@Composable
fun commandState(): CommandState {
    val ctrl = commandController()
    return CommandState(query = ctrl.query, visibleCount = ctrl.visibleCount, match = ctrl::matches)
}

/** Slot receiver for [CommandInput] / [CommandList]. */
class CommandScope internal constructor()

/**
 * A searchable/filterable action list matching real shadcn/ui's `command.tsx`:
 *
 * ```
 * Command
 * ├── CommandInput
 * └── CommandList
 *     ├── CommandEmpty
 *     ├── CommandGroup
 *     │   └── CommandItem
 *     └── CommandSeparator
 *
 * Wrap in [CommandDialog] for a ⌘K palette.
 * ```
 *
 * Filters [CommandItem]s by case-insensitive substring match as the user types.
 *
 * Usage:
 * ```
 * Command {
 *     CommandInput(placeholder = "Type a command or search...")
 *     CommandList {
 *         CommandEmpty()
 *         CommandGroup(heading = "Suggestions") {
 *             CommandItem("Calendar", onSelect = {})
 *         }
 *     }
 * }
 * Command(defaultQuery = "cal") { … }
 * Command(filter = { query, value -> value.startsWith(query, ignoreCase = true) }) { … }
 * Command(shouldFilter = false) { … }
 * Command(loop = false) { … }
 * Command(label = "Search commands") { … }
 * ```
 */
@Composable
fun Command(
    modifier: Modifier = Modifier,
    style: Style = Style,
    defaultQuery: String = "",
    shouldFilter: Boolean = true,
    filter: (query: String, value: String) -> Boolean = DefaultCommandFilter,
    loop: Boolean = true,
    label: String = "Command Menu",
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    defaultValue: String = "",
    content: @Composable CommandScope.() -> Unit,
) {
    CommandScaffold(
        modifier = modifier,
        style = rememberCommandPanelStyle() then style,
        defaultQuery = defaultQuery,
        shouldFilter = shouldFilter,
        filter = filter,
        loop = loop,
        label = label,
        value = value,
        onValueChange = onValueChange,
        defaultValue = defaultValue,
        content = content,
    )
}

@Composable
private fun CommandScaffold(
    modifier: Modifier,
    style: Style,
    content: @Composable CommandScope.() -> Unit,
    defaultQuery: String = "",
    shouldFilter: Boolean = true,
    filter: (query: String, value: String) -> Boolean = DefaultCommandFilter,
    loop: Boolean = true,
    label: String = "Command Menu",
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    defaultValue: String = "",
) {
    var internalValue by remember { mutableStateOf(defaultValue) }
    val controller = remember { CommandController(defaultQuery) }
    controller.shouldFilter = shouldFilter
    controller.filter = filter
    controller.loop = loop
    controller.label = label
    controller.onValueChange = { next ->
        if (value == null) internalValue = next
        onValueChange?.invoke(next)
    }
    controller.controlled = value != null
    controller.currentValue = value ?: internalValue
    val nova = currentTheme.activePreset == StylePreset.Nova
    LaunchedEffect(nova, controller.query, controller.items.toList(), value) {
        if (nova &&
            (
                controller.controlled ||
                    controller.items.none {
                        it.id == controller.highlightedId &&
                            it.enabled
                    }
            )
        ) {
            controller.highlightedId =
                (
                    controller.items.firstOrNull {
                        it.enabled && it.value == controller.currentValue
                    } ?: controller.items.firstOrNull { it.enabled }
                )?.id
        }
    }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(LocalCommand provides controller) {
        Column(
            modifier =
                modifier
                    .onPreviewKeyEvent { handleCommandKey(controller, it) }
                    .styleable(styleState, style),
        ) {
            CommandScope().content()
        }
    }
}

object CommandDefaults {
    /**
     * Official [com.toutiaoyan.compose.ui.components.dialog.DialogContent] `showCloseButton`.
     * [CommandDialog] stays `false` so existing palettes have no X.
     */
    val ShowCloseButton = true
}

/**
 * [Command] inside a [Dialog]. Matches shadcn's `CommandDialog` (⌘K palette).
 *
 * Pass [open] + [onOpenChange] for controlled state; omit both (or pass
 * [defaultOpen]) for uncontrolled. [onPointerDownOutside] is Radix Dialog
 * `onPointerDownOutside` (return true to prevent dismiss). [onInteractOutside]
 * is Radix `onInteractOutside` (return true to prevent dismiss).
 * Pass [CommandDefaults.ShowCloseButton] for Dialog's official X; default stays hidden.
 */
@Composable
fun CommandDialog(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    defaultQuery: String = "",
    shouldFilter: Boolean = true,
    filter: (query: String, value: String) -> Boolean = DefaultCommandFilter,
    loop: Boolean = true,
    label: String = "Command Menu",
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    defaultValue: String = "",
    modal: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    showCloseButton: Boolean = false,
    closeAriaLabel: String = "Close",
    title: String = "Command Palette",
    description: String = "Search for a command to run...",
    content: @Composable CommandScope.() -> Unit,
) {
    Dialog(
        open = open,
        onOpenChange = onOpenChange,
        defaultOpen = defaultOpen,
        modal = modal,
    ) {
        DialogContent(
            modifier =
                modifier.semantics {
                    contentDescription = "$title. $description"
                },
            showCloseButton = showCloseButton,
            closeAriaLabel = closeAriaLabel,
            dismissOnClickOutside = dismissOnClickOutside,
            onPointerDownOutside = onPointerDownOutside,
            onInteractOutside = onInteractOutside,
            style = rememberCommandDialogPanelStyle(),
        ) {
            CommandScaffold(
                modifier = Modifier.fillMaxWidth(),
                style = Style,
                defaultQuery = defaultQuery,
                shouldFilter = shouldFilter,
                filter = filter,
                loop = loop,
                label = label,
                value = value,
                onValueChange = onValueChange,
                defaultValue = defaultValue,
                content = content,
            )
        }
    }
}

/** Search field of a [Command]. Matches shadcn's `CommandInput`. [icon] is optional and not shown by default. */
@Composable
fun CommandScope.CommandInput(
    modifier: Modifier = Modifier,
    placeholder: String = "Type a command or search...",
    disabled: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
) {
    val ctrl = commandController()
    val observer = remember(ctrl) { { active: Boolean -> ctrl.isComposing = active } }
    CompositionLocalProvider(LocalInputCompositionObserver provides observer) {
        if (currentTheme.activePreset == StylePreset.Nova) {
            val colors = currentTheme.colors
            InputGroup(
                modifier = modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 4.dp),
                style =
                    Style {
                        background(colors.input.copy(alpha = 0.3f))
                        borderColor(colors.input.copy(alpha = 0.3f))
                    },
            ) {
                InputGroupAddon { if (icon != null) icon() else Icon(NovaSearch, tint = colors.mutedForeground) }
                Input(
                    value = ctrl.query,
                    onValueChange = { ctrl.query = it },
                    placeholder = placeholder,
                    disabled = disabled,
                )
            }
        } else if (icon == null) {
            Input(
                value = ctrl.query,
                onValueChange = { ctrl.query = it },
                placeholder = placeholder,
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
                    onValueChange = { ctrl.query = it },
                    placeholder = placeholder,
                    variant = InputVariant.Ghost,
                    disabled = disabled,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Scroll/list slot of a [Command]. Matches shadcn's `CommandList`. */
@Composable
fun CommandScope.CommandList(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val ctrl = commandController()
    Column(
        modifier =
            modifier.fillMaxWidth().then(
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Modifier.heightIn(
                        max = 288.dp,
                    ).verticalScroll(rememberScrollState())
                } else {
                    Modifier
                },
            ).semantics {
                contentDescription = ctrl.label
            },
        content = content,
    )
}

/**
 * Shown when the query is non-empty and no [CommandItem] matches.
 * Matches shadcn's `CommandEmpty`.
 */
@Composable
fun CommandEmpty(
    modifier: Modifier = Modifier,
    text: String = "No results found.",
) {
    val ctrl = commandController()
    if (ctrl.query.isNotBlank() && ctrl.visibleCount == 0) {
        val emptyStyleState = remember { MutableStyleState(MutableInteractionSource()) }
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .then(
                        if (currentTheme.activePreset == StylePreset.Nova) {
                            Modifier.padding(
                                vertical = 24.dp,
                            )
                        } else {
                            Modifier
                        },
                    )
                    .styleable(
                        emptyStyleState,
                        if (currentTheme.activePreset == StylePreset.Nova) Style else rememberMenuLabelStyle(),
                    ),
            contentAlignment =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Alignment.Center
                } else {
                    Alignment.TopStart
                },
        ) {
            Text(variant = TextVariant.BodySmall, textStyle = overlayTypography(fallback = TextColors.Muted)) { text }
        }
    }
}

/** Labeled section of [CommandItem]s. Matches shadcn's `CommandGroup`. */
@Composable
fun CommandGroup(
    modifier: Modifier = Modifier,
    heading: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val matches = remember { androidx.compose.runtime.mutableStateMapOf<Any, Boolean>() }
    val nova = currentTheme.activePreset == StylePreset.Nova
    CompositionLocalProvider(LocalCommandGroup provides matches) {
        Column(
            modifier =
                modifier.fillMaxWidth().then(
                    if (nova &&
                        matches.values.any {
                            it
                        }
                    ) {
                        Modifier.padding(4.dp)
                    } else {
                        Modifier
                    },
                ),
        ) {
            if (heading != null && (!nova || matches.values.any { it })) CommandGroupHeading(text = heading)
            content()
        }
    }
}

/** Section heading inside [CommandGroup]. Matches shadcn's group heading slot. */
@Composable
fun CommandGroupHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    val headingStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.LabelSmall,
        textStyle = overlayTypography(12, 16, medium = true, muted = true, fallback = TextColors.Muted),
        modifier =
            modifier
                .fillMaxWidth()
                .styleable(headingStyleState, rememberMenuLabelStyle()),
    ) { text }
}

@Composable
@JvmName("CommandGroupHeadingLambda")
@OverloadResolutionByLambdaReturnType
fun CommandGroupHeading(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    CommandGroupHeading(text = text(), modifier = modifier)
}

/** Selectable row. Hidden when it doesn't match [CommandInput]. */
@Composable
fun CommandItem(
    label: String,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    shortcut: String? = null,
    value: String? = null,
    keywords: List<String> = emptyList(),
    forceMount: Boolean = false,
    textValue: String? = null,
    content: @Composable () -> Unit = {
        if (shortcut == null) {
            Text(variant = TextVariant.BodySmall, textStyle = overlayTypography()) { label }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(variant = TextVariant.BodySmall, textStyle = overlayTypography()) { label }
                CommandShortcut(shortcut)
            }
        }
    },
) {
    val ctrl = commandController()
    val resolvedEnabled = enabled && !disabled
    val resolvedValue = value ?: label
    val matches = forceMount || ctrl.matches(resolvedValue, keywords)
    val itemId = remember { Any() }
    DisposableEffect(ctrl, itemId) {
        ctrl.itemOrder.add(itemId)
        onDispose { ctrl.itemOrder.remove(itemId) }
    }
    val group = LocalCommandGroup.current
    DisposableEffect(group, itemId) { onDispose { group?.remove(itemId) } }
    SideEffect { group?.set(itemId, matches) }
    val latestSelect by rememberUpdatedState(onSelect)
    DisposableEffect(matches, resolvedEnabled, resolvedValue) {
        if (matches) ctrl.visibleCount++
        val nav =
            if (matches) {
                CommandNavItem(itemId, resolvedValue, resolvedEnabled) { latestSelect() }.also { item ->
                    val order = ctrl.itemOrder.indexOf(itemId)
                    val next = ctrl.items.indexOfFirst { ctrl.itemOrder.indexOf(it.id) > order }
                    ctrl.items.add(if (next < 0) ctrl.items.size else next, item)
                }
            } else {
                null
            }
        onDispose {
            if (matches) ctrl.visibleCount--
            if (nav != null) {
                ctrl.items.remove(nav)
                if (ctrl.highlightedId == itemId) ctrl.highlightedId = null
            }
        }
    }
    if (!matches) return
    val highlighted =
        ctrl.highlightedId == itemId ||
            (ctrl.highlightedId == null && ctrl.currentValue.isNotEmpty() && ctrl.currentValue == resolvedValue)
    val bringIntoView = remember { BringIntoViewRequester() }
    val novaItem = currentTheme.activePreset == StylePreset.Nova
    LaunchedEffect(highlighted, novaItem, ctrl.navigationRevision) {
        if (highlighted && novaItem && ctrl.navigationRevision > 0) bringIntoView.bringIntoView()
    }
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
            it.isHovered = highlighted
        }
    val pressStyle = rememberPressMotionStyle()
    val nova = currentTheme.activePreset == StylePreset.Nova
    val colors = currentTheme.colors
    val touchTargetStyle = if (nova) Style else rememberMinTouchTargetStyle()
    val itemStyle =
        if (nova) {
            Style {
                width(1f)
                contentPadding(horizontal = 8.dp, vertical = 6.dp)
                shape(RoundedCornerShape(6.dp))
                if (highlighted) background(colors.muted)
                hovered { background(colors.muted) }
                if (!resolvedEnabled) alpha(0.5f)
            }
        } else {
            rememberMenuItemStyle()
        }
    Box(
        modifier =
            modifier.bringIntoViewRequester(bringIntoView)
                .onPointerEvent(PointerEventType.Enter) {
                    if (resolvedEnabled) ctrl.items.firstOrNull { it.id == itemId }?.let(ctrl::highlight)
                }
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
                        ctrl.items.firstOrNull { it.id == itemId }?.let(ctrl::highlight)
                        onSelect()
                    },
                )
                .styleable(styleState, touchTargetStyle then pressStyle then itemStyle),
    ) {
        CompositionLocalProvider(LocalDefaultTextStyle provides overlayTypography()) { content() }
    }
}

/** Muted trailing shortcut (e.g. ⌘P). Matches shadcn's `CommandShortcut`. */
@Composable
fun CommandShortcut(
    text: String,
    modifier: Modifier = Modifier,
) {
    if (currentTheme.activePreset == StylePreset.Nova && text.startsWith("⌘")) {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(NovaCommandKey, modifier = Modifier.size(12.dp), tint = currentTheme.colors.mutedForeground)
            Text(textStyle = overlayTypography(12, 16, muted = true)) { text.removePrefix("⌘") }
        }
        return
    }
    Text(
        variant = TextVariant.LabelSmall,
        textStyle = overlayTypography(12, 16, muted = true, fallback = TextColors.Muted),
        modifier = modifier,
    ) { text }
}

@Composable
@JvmName("CommandShortcutLambda")
@OverloadResolutionByLambdaReturnType
fun CommandShortcut(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    CommandShortcut(text = text(), modifier = modifier)
}

/** Divider between groups. Matches shadcn's `CommandSeparator`. */
@Composable
fun CommandSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    DropdownMenuSeparator(modifier = modifier, style = style)
}

/** Bottom bar of a [Command] (hints, selection count). Matches shadcn's `CommandFooter`. */
@Composable
fun CommandFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** String-label overload of [CommandFooter]. */
@Composable
fun CommandFooter(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    CommandFooter(modifier = modifier, style = style) {
        Text(variant = TextVariant.BodySmall, textStyle = overlayTypography(fallback = TextColors.Muted)) { text }
    }
}

/** Async-search placeholder. Matches shadcn's `CommandLoading`. Not shown by default. */
@Composable
fun CommandLoading(
    modifier: Modifier = Modifier,
    text: String = "Loading...",
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        Spinner()
        Text(variant = TextVariant.BodySmall, textStyle = overlayTypography(fallback = TextColors.Muted)) { text }
    }
}
