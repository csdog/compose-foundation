@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.select

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.focusable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
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
import com.toutiaoyan.compose.ui.components.overlay.MenuDefaultEnter
import com.toutiaoyan.compose.ui.components.overlay.MenuDefaultExit
import com.toutiaoyan.compose.ui.components.overlay.PopupHorizontalPosition
import com.toutiaoyan.compose.ui.components.overlay.MenuPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
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
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.semantics.hideFromAccessibility
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
    internal val popupOpen: Boolean get() = open && !disabled

    fun select(value: Any?) {
        if (disabled) return
        onSelect(value)
        if (open) setOpen(false)
    }

    fun isSelected(value: Any?): Boolean = selectedEquals(value)

    fun labelOf(value: Any?): String = format(value)
}

private data class SelectFocusItem(val value: Any?, val requester: FocusRequester, val enabled: Boolean)

private data class SelectSearchItem(
    val value: Any?,
    val label: String,
    val enabled: Boolean,
    val activate: () -> Unit,
)

private val LocalSelectRegistrationOnly = staticCompositionLocalOf { false }

private class SelectGrowthObservation {
    var scroll: Int? = null
    var viewport: Int? = null
    var pendingViewport: Int? = null
}

private class SelectPanelSizing {
    var height: Dp = Dp.Unspecified
    var maxHeight: Dp = Dp.Unspecified
}

private data class SelectPanelChrome(val top: Float, val bottom: Float) {
    val height get() = top + bottom
}

private class SelectKeyboard {
    val trigger = FocusRequester()
    var width by mutableStateOf(144.dp)
    var height by mutableStateOf(32.dp)
    var panelTop by mutableStateOf(0f)
    var anchorBounds by mutableStateOf(Rect.Zero)
    var touchOpen by mutableStateOf(false)
    var contentCoordinates: LayoutCoordinates? = null
    var panelCoordinates: LayoutCoordinates? = null
    var previousMousePosition: Offset? = null
    var valueBounds by mutableStateOf<Rect?>(null)
    val itemTextEdges = mutableStateMapOf<Any?, SelectTextEdges>()
    val naturalCenters = mutableStateMapOf<Any?, Float>()
    val naturalHeights = mutableMapOf<Any?, Float>()
    var scrollUpHovered by mutableStateOf(false)
    var scrollDownHovered by mutableStateOf(false)
    val scrollHovered get() = scrollUpHovered || scrollDownHovered
    val centers = mutableStateMapOf<Any?, Float>()
    val items = mutableStateListOf<SelectFocusItem>()
    var active: Any? = null
    var hasActive = false
    var reveal: ((Any?) -> Unit)? = null
    val pick = mutableMapOf<Any?, () -> Unit>()
    val closedPick = mutableMapOf<Any?, () -> Unit>()
    var fromEnd = false
    private var suppressActivationUp = false
    val labels = mutableMapOf<Any?, String>()
    var closedItems: List<SelectSearchItem> = emptyList()
    var popupMounted = false
    var tabForward by mutableStateOf(false)
    private var search = ""
    private var lastType = kotlin.time.TimeSource.Monotonic.markNow()

    private fun positionOf(value: Any?) = naturalCenters[value] ?: centers[value] ?: Float.MAX_VALUE

    fun resetSearch() {
        search = ""
    }

    fun typeAhead(
        event: KeyEvent,
        closedController: SelectController? = null,
    ): Boolean {
        if (event.isCtrlPressed || event.isMetaPressed || event.isAltPressed) return false
        val char = event.utf16CodePoint.toChar()
        if (lastType.elapsedNow().inWholeMilliseconds > 750) search = ""
        if (char.isISOControl() || (char.isWhitespace() && search.isEmpty())) return false
        val newSession = search.isEmpty()
        lastType = kotlin.time.TimeSource.Monotonic.markNow()
        search += char
        val prefix = if (search.all { it.equals(char, ignoreCase = true) }) char.toString() else search
        val options =
            if (closedController != null && !popupMounted) {
                closedItems.filter { it.enabled }
            } else {
                items.filter { it.enabled }.sortedBy { positionOf(it.value) }.map { item ->
                    SelectSearchItem(item.value, labels[item.value].orEmpty(), true) {
                        if (closedController != null) {
                            closedPick[item.value]?.invoke()
                        } else if (item.requester.requestFocus()) {
                            active = item.value
                            hasActive = true
                            reveal?.invoke(item.value)
                        }
                    }
                }
            }
        val index =
            if (newSession && closedController != null) {
                options.indexOfFirst { closedController.isSelected(it.value) }
            } else {
                if (hasActive) options.indexOfFirst { it.value == active } else -1
            }
        val start = if (prefix.length == 1) index + 1 else index.coerceAtLeast(0)
        val candidates = options.drop(start) + options.take(start)
        val match = candidates.firstOrNull { it.label.startsWith(prefix, ignoreCase = true) }
        if (match != null) {
            if (closedController != null) {
                active = match.value
                hasActive = true
            }
            match.activate()
        } else if (!char.isWhitespace()) {
            search = ""
        }
        return true
    }

    fun consumeRelease(event: KeyEvent): Boolean {
        if (event.type == KeyEventType.KeyUp && event.key in listOf(Key.Enter, Key.Spacebar) && suppressActivationUp) {
            suppressActivationUp = false
            return true
        }
        return false
    }

    fun handleOpenKey(
        event: KeyEvent,
        controller: SelectController,
        nova: Boolean,
    ): Boolean {
        if (consumeRelease(event)) return true
        if (event.type != KeyEventType.KeyDown) return false
        if (nova && event.key == Key.Tab) {
            controller.setOpen(false)
            tabForward = !event.isShiftPressed
            return true
        }
        return move(event.key, controller, includeDisabled = nova) || typeAhead(event)
    }

    fun move(
        key: Key,
        controller: SelectController,
        includeDisabled: Boolean = false,
    ): Boolean {
        val options = items.filter { includeDisabled || it.enabled }.sortedBy { positionOf(it.value) }
        if (options.isEmpty()) return false
        val index = if (hasActive) options.indexOfFirst { it.value == active } else -1
        val target =
            when (key) {
                Key.DirectionDown -> (index + 1).coerceAtMost(options.lastIndex)
                Key.DirectionUp -> (if (index < 0) options.lastIndex else index - 1).coerceAtLeast(0)
                Key.MoveHome -> 0
                Key.MoveEnd -> options.lastIndex
                Key.Enter, Key.Spacebar -> {
                    if (key == Key.Spacebar &&
                        search.isNotEmpty() &&
                        lastType.elapsedNow().inWholeMilliseconds <= 750
                    ) {
                        return false
                    }
                    suppressActivationUp = true
                    options.getOrNull(index)?.takeIf { it.enabled }?.let { pick[it.value]?.invoke() }
                    return index >= 0
                }
                Key.Escape -> {
                    controller.setOpen(false)
                    return true
                }
                else -> return false
            }
        if (options[target].requester.requestFocus()) {
            active = options[target].value
            hasActive = true
            reveal?.invoke(options[target].value)
        }
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
    val resolvedDisabled = disabled || LocalFieldSetDisabled.current
    val popupOpen = isOpen && !resolvedDisabled
    val keyboard = remember { SelectKeyboard() }
    val setOpen: (Boolean) -> Unit = { next ->
        keyboard.tabForward = false
        if (open == null) internalOpen = next
        onOpenChange?.invoke(next)
    }
    if (isOpen && keyboard.tabForward) {
        // A controlled caller can leave the popup open after the Tab request.
        SideEffect { keyboard.tabForward = false }
    }
    val focusManager = LocalFocusManager.current
    var wasOpen by remember { mutableStateOf(popupOpen) }
    LaunchedEffect(popupOpen) {
        if (!popupOpen && wasOpen) {
            val forward = keyboard.tabForward
            keyboard.tabForward = false
            if (!resolvedDisabled) {
                keyboard.trigger.requestFocus()
                if (forward) focusManager.moveFocus(FocusDirection.Next)
            }
        }
        keyboard.resetSearch()
        wasOpen = popupOpen
    }
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
    animation: ButtonAnimation =
        if (currentTheme.activePreset == StylePreset.Nova) ButtonAnimation.None else ButtonAnimation.Scale,
    accessibilityLabel: String = "",
    invalid: Boolean = false,
    content: @Composable RowScope.() -> Unit = { SelectValue() },
) {
    val ctrl = selectController()
    val canOpen = !ctrl.disabled && !ctrl.popupOpen
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
                    keyboard.anchorBounds = it.boundsInWindow()
                }
                .pointerInput(keyboard, canOpen) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (canOpen && event.type == PointerEventType.Press) {
                                keyboard.touchOpen = event.changes.any { it.type == PointerType.Touch }
                            }
                            if (event.type == PointerEventType.Press || event.type == PointerEventType.Release) {
                                event.changes.firstOrNull { it.type == PointerType.Mouse }?.let { change ->
                                    keyboard.previousMousePosition = keyboard.anchorBounds.topLeft + change.position
                                }
                            }
                        }
                    }
                }
                .widthIn(min = if (nova) 0.dp else 140.dp)
                .focusRequester(keyboard.trigger)
                .keyboardInputMode()
                .onPreviewKeyEvent { event ->
                    if (keyboard.consumeRelease(event)) return@onPreviewKeyEvent true
                    if (ctrl.disabled) return@onPreviewKeyEvent false
                    // A non-focusable popup leaves platform keyboard delivery on its trigger.
                    if (ctrl.popupOpen && !ctrl.modal) {
                        return@onPreviewKeyEvent keyboard.handleOpenKey(event, ctrl, nova)
                    }
                    if (event.type != KeyEventType.KeyDown || ctrl.popupOpen) return@onPreviewKeyEvent false
                    keyboard.touchOpen = false
                    if (event.key == Key.DirectionDown || event.key == Key.DirectionUp) {
                        keyboard.fromEnd = event.key == Key.DirectionUp
                        ctrl.setOpen(true)
                        true
                    } else {
                        keyboard.typeAhead(event, ctrl)
                    }
                }
                .semantics(mergeDescendants = true) {
                    role = Role.DropdownList
                    contentDescription = description
                    stateDescription = if (ctrl.popupOpen) "Expanded" else "Collapsed"
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

private data class SelectTextItem(val value: Any?)

private val LocalSelectTextItem = staticCompositionLocalOf<SelectTextItem?> { null }

@Composable
private fun Modifier.selectItemTextCoordinates(): Modifier {
    val item = LocalSelectTextItem.current ?: return this
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    if (currentTheme.activePreset != StylePreset.Nova) return this
    return onGloballyPositioned { text ->
        keyboard.panelCoordinates?.takeIf { it.isAttached }?.let { panel ->
            val left = panel.localPositionOf(text, Offset.Zero).x
            keyboard.itemTextEdges[item.value] = SelectTextEdges(left, left + text.size.width)
        }
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
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    val nova = theme.activePreset == StylePreset.Nova
    DisposableEffect(keyboard, nova) { onDispose { keyboard.valueBounds = null } }
    Text(
        modifier =
            modifier.then(
                if (nova) Modifier.onGloballyPositioned { keyboard.valueBounds = it.boundsInWindow() } else Modifier,
            ),
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
     * Aligns the selected option with the trigger. Uses normal popup placement for
     * touch opening or when alignment cannot fit. Default motion is disabled while aligned.
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
    enter: EnterTransition = MenuDefaultEnter,
    exit: ExitTransition = MenuDefaultExit,
    dismissOnClickOutside: Boolean = true,
    content: @Composable SelectContentScope.() -> Unit,
) {
    val ctrl = selectController()
    val popupOpen = ctrl.popupOpen
    val scrollState = rememberScrollState()
    val triggerInputMode = LocalInputModeManager.current
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    val nova = currentTheme.activePreset == StylePreset.Nova
    val density = LocalDensity.current
    DisposableEffect(keyboard) {
        onDispose {
            keyboard.closedItems = emptyList()
            keyboard.reveal = null
            keyboard.contentCoordinates = null
            keyboard.panelCoordinates = null
            keyboard.previousMousePosition = null
        }
    }
    var popupRetained by remember { mutableStateOf(false) }
    if (!popupOpen && !popupRetained) {
        SideEffect {
            keyboard.contentCoordinates = null
            keyboard.panelCoordinates = null
            keyboard.previousMousePosition = null
            keyboard.reveal = null
        }
        // Register sanctioned Select parts without measuring, placing, focusing, or
        // exposing an offscreen copy. Measurable order follows keyed declaration order.
        // The registry occupies no space, including when a parent queries intrinsic size.
        SubcomposeLayout(Modifier.size(0.dp)) {
            val registrations =
                subcompose(Unit) {
                    CompositionLocalProvider(LocalSelectRegistrationOnly provides true) {
                        SelectContentScope().content()
                    }
                }
            keyboard.closedItems = registrations.mapNotNull { it.layoutId as? SelectSearchItem }
            layout(0, 0) {}
        }
        return
    }

    val panelSizing = remember { SelectPanelSizing() }
    val growthObservation = remember { SelectGrowthObservation() }
    val callerStyle =
        remember(style) {
            Style {
                panelSizing.height = Dp.Unspecified
                panelSizing.maxHeight = Dp.Unspecified
                val target = this
                val receiver =
                    object : StyleScope by target {
                        override fun height(value: Dp) {
                            panelSizing.height = value
                            target.height(value)
                        }

                        override fun maxHeight(value: Dp) {
                            panelSizing.maxHeight = value
                            target.maxHeight(value)
                        }
                    }
                with(style) { receiver.applyStyle() }
            }
        }
    val touchOpen = keyboard.touchOpen
    val scrollOverlay = if (nova && touchOpen) 0f else with(density) { 24.dp.toPx() }
    val windowMargin = with(density) { 10.dp.toPx() }
    val revealScope = rememberCoroutineScope()
    SideEffect {
        keyboard.reveal = revealItem@{ value ->
            val center = keyboard.naturalCenters[value] ?: return@revealItem
            val height = keyboard.naturalHeights[value] ?: return@revealItem
            revealScope.launch {
                val current = scrollState.value.toFloat()
                val viewport = scrollState.viewportSize.toFloat()
                val top = center - height / 2f
                val bottom = center + height / 2f
                val insetTop = if (scrollState.canScrollBackward) scrollOverlay else 0f
                val insetBottom = if (scrollState.canScrollForward) scrollOverlay else 0f
                val target =
                    when {
                        top < current + insetTop -> top - scrollOverlay
                        bottom > current + viewport - insetBottom -> bottom - viewport + scrollOverlay
                        else -> current
                    }
                scrollState.scrollTo(target.coerceIn(0f, scrollState.maxValue.toFloat()).roundToInt())
            }
        }
    }
    @Suppress("DEPRECATION")
    val itemAligned =
        alignItemWithTrigger
            ?: (
                position == SelectContentPosition.AlignItemWithTrigger ||
                    position == SelectContentPosition.ItemAligned
            )
    var alignmentFallback by remember(nova, itemAligned) { mutableStateOf(false) }
    val windowHeight = LocalWindowInfo.current.containerSize.height.toFloat()
    var contentHeight by remember { mutableStateOf(0) }
    var panelChrome by remember { mutableStateOf<SelectPanelChrome?>(null) }
    var placedGeometry by remember { mutableStateOf<SelectPopupGeometry?>(null) }
    var initialScrollApplied by remember { mutableStateOf(false) }
    var addedHeight by remember { mutableStateOf(0f) }
    val selectedItem = keyboard.items.firstOrNull { ctrl.isSelected(it.value) }
    val target =
        selectedItem
            ?: keyboard.items.minByOrNull { keyboard.naturalCenters[it.value] ?: Float.MAX_VALUE }
    val selectedCenter = target?.let { keyboard.naturalCenters[it.value] }
    val anchorBounds = keyboard.anchorBounds
    val valueBounds = keyboard.valueBounds
    val textRefExpected = selectedItem != null || ctrl.displayText == null
    val textEdges = if (textRefExpected) target?.let { keyboard.itemTextEdges[it.value] } else null
    val geometrySelectedCenter =
        if (valueBounds != null && textEdges != null && selectedCenter != null) {
            selectedCenter + (panelChrome?.top ?: 0f) - (valueBounds.center.y - anchorBounds.center.y)
        } else {
            anchorBounds.height / 2f
        }
    val initialGeometry =
        if (nova && contentHeight > 0 && panelChrome != null && keyboard.anchorBounds.height > 0f) {
            selectPopupGeometry(
                trigger = keyboard.anchorBounds,
                windowHeight = windowHeight,
                contentHeight = contentHeight + checkNotNull(panelChrome).height,
                selectedCenter = geometrySelectedCenter,
                alignItem = itemAligned && !alignmentFallback,
                touch = touchOpen,
                sideOffset = with(density) { sideOffset.toPx() },
                scale = density.density,
            )
        } else {
            null
        }
    SideEffect {
        if (popupOpen && nova && itemAligned && initialGeometry?.itemAligned == false) alignmentFallback = true
    }
    val availablePanelHeight =
        minOf(
            (windowHeight - windowMargin * 2f).coerceAtLeast(0f),
            contentHeight + (panelChrome?.height ?: 0f),
            with(density) { panelSizing.maxHeight.takeIf { it.isSpecified }?.toPx() ?: Float.POSITIVE_INFINITY },
        )
    val computedGeometry =
        initialGeometry?.let { initial ->
            val growth =
                if (initial.growthAnchor == SelectPopupAnchor.Fixed) {
                    0f
                } else {
                    addedHeight.coerceIn(0f, (availablePanelHeight - initial.height).coerceAtLeast(0f))
                }
            initial.copy(
                // Layout rounds height to whole pixels. Derive the bottom-anchored top
                // from that same height so half-pixel text centers cannot move the margin.
                top =
                    if (initial.growthAnchor == SelectPopupAnchor.Bottom) {
                        initial.top + initial.height - (initial.height + growth).roundToInt()
                    } else {
                        initial.top
                    },
                height = initial.height + growth,
            )
        }
    var lastOpenGeometry by remember { mutableStateOf<SelectPopupGeometry?>(null) }
    var lastOpenWidth by remember { mutableStateOf(144.dp) }
    SideEffect {
        if (popupOpen) {
            lastOpenGeometry = computedGeometry
            lastOpenWidth = keyboard.width.coerceAtLeast(144.dp)
        }
    }
    val panelWidth = if (popupOpen) keyboard.width.coerceAtLeast(144.dp) else lastOpenWidth
    val geometry = if (popupOpen) computedGeometry else lastOpenGeometry
    LaunchedEffect(initialGeometry, initialScrollApplied, availablePanelHeight, popupOpen) {
        if (!popupOpen) return@LaunchedEffect
        val initial = initialGeometry ?: return@LaunchedEffect
        if (!initialScrollApplied || initial.growthAnchor == SelectPopupAnchor.Fixed) return@LaunchedEffect
        snapshotFlow { Triple(scrollState.value, scrollState.maxValue, scrollState.viewportSize) }.collect {
                (scroll, maxScroll, viewport) ->
            val previousScroll = growthObservation.scroll
            val previousViewport = growthObservation.viewport
            growthObservation.scroll = scroll
            growthObservation.viewport = viewport
            // Bounds changes are not scroll gestures, including our own pending resize.
            if (previousViewport != null && previousViewport != viewport) {
                growthObservation.pendingViewport = null
                return@collect
            }
            if (viewport == growthObservation.pendingViewport || panelSizing.height.isSpecified) return@collect
            if (scroll == previousScroll ||
                (previousScroll == null && scroll == initial.initialScroll.roundToInt())
            ) {
                return@collect
            }
            // Caller styles can change without changing the measured viewport.
            val currentMaximum =
                minOf(
                    (windowHeight - windowMargin * 2f).coerceAtLeast(0f),
                    contentHeight + (panelChrome?.height ?: 0f),
                    with(density) {
                        panelSizing.maxHeight.takeIf { it.isSpecified }?.toPx() ?: Float.POSITIVE_INFINITY
                    },
                )
            val growth =
                addedHeight.coerceIn(0f, (currentMaximum - initial.height).coerceAtLeast(0f))
            val current =
                initial.copy(
                    top = if (initial.growthAnchor == SelectPopupAnchor.Bottom) initial.top - growth else initial.top,
                    height = initial.height + growth,
                )
            val measuredHeight = keyboard.panelCoordinates?.size?.height ?: return@collect
            // An explicit caller height can constrain the panel below our available height.
            if (measuredHeight + density.density < current.height) return@collect
            val next =
                selectPopupGrowth(
                    geometry = current,
                    windowHeight = windowHeight,
                    scroll = scroll.toFloat(),
                    maxScroll = maxScroll.toFloat(),
                    maxHeight = currentMaximum,
                    scale = density.density,
                ) ?: return@collect
            growthObservation.pendingViewport = viewport
            addedHeight = next.geometry.height - initial.height
            // Compensate in the same frame without cancelling the user's scroll mutation.
            scrollState.dispatchRawDelta(next.scroll - scrollState.value)
            growthObservation.scroll = scrollState.value
        }
    }
    val aligned = geometry?.itemAligned ?: itemAligned
    if (nova && popupOpen && !ctrl.modal && geometry?.itemAligned == true) {
        SelectAlignedScrollLock {
            keyboard.panelCoordinates?.takeIf { it.isAttached }?.boundsInWindow()
        }
    }
    val placement =
        if (geometry?.itemAligned == false && geometry.top < keyboard.anchorBounds.top) {
            PopupPlacement.Top
        } else {
            PopupPlacement.Bottom
        }
    val resolvedOffset =
        geometry?.let {
            with(density) {
                if (placement == PopupPlacement.Top) {
                    (keyboard.anchorBounds.top - it.top - it.height).toDp()
                } else {
                    (it.top - keyboard.anchorBounds.bottom).toDp()
                }
            }
        } ?: sideOffset
    val contentReady =
        !nova ||
            (
                initialScrollApplied && geometry != null && (target == null || selectedCenter != null) &&
                    (!geometry.itemAligned || !textRefExpected || target == null || textEdges != null)
            )
    LaunchedEffect(geometry, placedGeometry, scrollState.maxValue, popupOpen) {
        if (!popupOpen) return@LaunchedEffect
        if (geometry != null && placedGeometry == geometry && !initialScrollApplied && scrollState.viewportSize > 0) {
            val fromEnd = keyboard.fromEnd && keyboard.items.none { ctrl.isSelected(it.value) }
            val selected = keyboard.items.firstOrNull { ctrl.isSelected(it.value) }
            val targetScroll =
                when {
                    fromEnd -> scrollState.maxValue
                    geometry.itemAligned -> geometry.initialScroll.roundToInt()
                    selected != null -> {
                        val center = keyboard.naturalCenters[selected.value] ?: 0f
                        val itemHeight = keyboard.naturalHeights[selected.value] ?: 0f
                        (center + itemHeight / 2f - scrollState.viewportSize + scrollOverlay).roundToInt()
                    }
                    else -> 0
                }
            val initialScroll = targetScroll.coerceIn(0, scrollState.maxValue)
            val measuredHeight = keyboard.panelCoordinates?.size?.height
            if (geometry.itemAligned && measuredHeight != null && measuredHeight + density.density < geometry.height) {
                val center = target?.let { keyboard.naturalCenters[it.value] }
                val itemHeight = target?.let { keyboard.naturalHeights[it.value] }
                if (center != null && itemHeight != null) {
                    val visibleTop = initialScroll + if (initialScroll > 0) scrollOverlay else 0f
                    val visibleBottom =
                        initialScroll + scrollState.viewportSize -
                            if (initialScroll < scrollState.maxValue) scrollOverlay else 0f
                    if (center - itemHeight / 2f < visibleTop || center + itemHeight / 2f > visibleBottom) {
                        alignmentFallback = true
                        return@LaunchedEffect
                    }
                }
            }
            scrollState.scrollTo(initialScroll)
            initialScrollApplied = true
        }
    }
    val horizontalPosition: PopupHorizontalPosition? =
        if (nova && geometry?.itemAligned == true) {
            remember(valueBounds, textEdges, anchorBounds, density) {
                { width, window, direction ->
                    selectPopupLeft(
                        trigger = anchorBounds,
                        popupWidth = width.toFloat(),
                        windowWidth = window.toFloat(),
                        value = valueBounds,
                        text = textEdges,
                        rtl = direction == LayoutDirection.Rtl,
                        scale = density.density,
                    ).roundToInt()
                }
            }
        } else {
            null
        }
    SelectPortal {
        MenuPopup(
            open = popupOpen,
            onDismissRequest = { if (popupOpen) ctrl.setOpen(false) },
            placement = placement,
            offset = resolvedOffset,
            align = align,
            alignOffset = alignOffset,
            dismissOnClickOutside = dismissOnClickOutside,
            focusable = ctrl.modal && popupOpen,
            enter = enter,
            exit = exit,
            owningNova = nova,
            ready = contentReady,
            animateDefaults = !aligned,
            freezePositionOnExit = true,
            horizontalPosition = horizontalPosition,
        ) {
            DisposableEffect(Unit) {
                popupRetained = true
                keyboard.popupMounted = true
                onDispose {
                    keyboard.popupMounted = false
                    popupRetained = false
                }
            }
            val panelStyleState = remember { MutableStyleState(MutableInteractionSource()) }
            CompositionLocalProvider(LocalSelectScroll provides scrollState) {
                val scope = SelectContentScope()
                LaunchedEffect(keyboard.items.size, popupOpen, initialScrollApplied) {
                    if (!popupOpen || (nova && !initialScrollApplied)) return@LaunchedEffect
                    val available = keyboard.items.filter { it.enabled }
                    val selected = (if (nova) keyboard.items else available).firstOrNull { ctrl.isSelected(it.value) }
                    val target =
                        selected ?: when {
                            keyboard.fromEnd -> available.lastOrNull()
                            nova && aligned -> keyboard.items.firstOrNull()
                            else -> available.firstOrNull()
                        }
                    target?.requester?.requestFocus()
                }
                val maxPanelHeight =
                    with(density) { (windowHeight - windowMargin * 2f).coerceAtLeast(0f).toDp() }
                val panelModifier =
                    modifier
                        .then(
                            if (nova && !contentReady) {
                                Modifier.graphicsLayer { alpha = 0f }.clearAndSetSemantics { }
                            } else {
                                Modifier
                            },
                        )
                        .onGloballyPositioned {
                            keyboard.panelTop = it.boundsInRoot().top
                            keyboard.panelCoordinates = it
                            if (!initialScrollApplied) placedGeometry = geometry
                        }
                        .then(
                            if (nova) {
                                Modifier.pointerInput(keyboard) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent(PointerEventPass.Final)
                                            val coordinates = keyboard.panelCoordinates
                                            if (coordinates?.isAttached == true) {
                                                val mouse = event.changes.firstOrNull { it.type == PointerType.Mouse }
                                                if (mouse != null) {
                                                    keyboard.previousMousePosition =
                                                        coordinates.localToWindow(mouse.position)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Modifier
                            },
                        )
                        .then(if (nova) Modifier else Modifier.widthIn(min = 128.dp, max = 280.dp))
                        .then(
                            if (nova) {
                                Modifier.heightIn(
                                    max = maxPanelHeight,
                                )
                            } else {
                                Modifier
                            },
                        )
                        .keyboardInputMode()
                        .onPreviewKeyEvent {
                                event ->
                            if (!popupOpen) return@onPreviewKeyEvent false
                            if (event.type == KeyEventType.KeyDown) {
                                triggerInputMode.requestInputMode(InputMode.Keyboard)
                            }
                            keyboard.handleOpenKey(event, ctrl, nova)
                        }
                        .cssStyleable(
                            panelStyleState,
                            variant.rememberPanelStyle() then (
                                if (nova) {
                                    Style {
                                        minWidth(144.dp)
                                        width(panelWidth)
                                        geometry?.let { height(with(density) { it.height.toDp() }) }
                                    }
                                } else {
                                    Style
                                }
                            ) then callerStyle,
                        )
                if (nova) {
                    Box(panelModifier) {
                        SelectViewport(
                            Modifier.onGloballyPositioned { viewport ->
                                keyboard.panelCoordinates?.takeIf { it.isAttached }?.let { panel ->
                                    val top = panel.localPositionOf(viewport, Offset.Zero).y.coerceAtLeast(0f)
                                    val bottom = (panel.size.height - top - viewport.size.height).coerceAtLeast(0f)
                                    panelChrome = SelectPanelChrome(top, bottom)
                                }
                            }.fillMaxHeight().verticalScroll(scrollState, enabled = popupOpen),
                        ) {
                            Column(
                                Modifier.onSizeChanged { contentHeight = it.height }
                                    .onGloballyPositioned { keyboard.contentCoordinates = it },
                            ) { scope.content() }
                        }
                        if (!touchOpen) {
                            Box(Modifier.align(Alignment.TopCenter)) { scope.SelectScrollUpButton() }
                            Box(Modifier.align(Alignment.BottomCenter)) { scope.SelectScrollDownButton() }
                        }
                    }
                } else {
                    Column(panelModifier) {
                        scope.SelectScrollUpButton()
                        SelectViewport(Modifier.verticalScroll(scrollState, enabled = popupOpen)) { scope.content() }
                        scope.SelectScrollDownButton()
                    }
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
 * `SelectScrollUpButton`. [SelectContent] provides an automatic arrow for non-touch Nova openings.
 */
@Composable
fun SelectContentScope.SelectScrollUpButton(modifier: Modifier = Modifier) {
    SelectScrollButton(modifier, up = true)
}

/** Chevron below a long panel. [SelectContent] provides the automatic arrow for non-touch Nova openings. */
@Composable
fun SelectContentScope.SelectScrollDownButton(modifier: Modifier = Modifier) {
    SelectScrollButton(modifier, up = false)
}

private class SelectScrollPointer {
    var coordinates: LayoutCoordinates? = null
}

@Composable
private fun SelectContentScope.SelectScrollButton(
    modifier: Modifier,
    up: Boolean,
) {
    val popupOpen = selectController().popupOpen
    val nova = currentTheme.activePreset == StylePreset.Nova
    val density = LocalDensity.current
    val clickDistance = with(density) { 48.dp.toPx() }
    val scrollState = LocalSelectScroll.current
    if (nova && (if (up) scrollState?.canScrollBackward else scrollState?.canScrollForward) != true) return
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    val scope = rememberCoroutineScope()
    var autoScroll by remember(popupOpen) { mutableStateOf(false) }
    val pointer = remember { SelectScrollPointer() }
    SideEffect {
        if (up) keyboard.scrollUpHovered = autoScroll else keyboard.scrollDownHovered = autoScroll
    }
    DisposableEffect(keyboard, up) {
        onDispose {
            if (up) keyboard.scrollUpHovered = false else keyboard.scrollDownHovered = false
            pointer.coordinates = null
        }
    }
    LaunchedEffect(nova, autoScroll, scrollState, keyboard, up, density, popupOpen) {
        if (popupOpen && nova && autoScroll && scrollState != null) {
            keyboard.hasActive = false
            while (true) {
                delay(40)
                if (!(if (up) scrollState.canScrollBackward else scrollState.canScrollForward)) break
                keyboard.hasActive = false
                val items =
                    keyboard.naturalCenters.entries.sortedBy { it.value }.mapNotNull { (value, center) ->
                        keyboard.naturalHeights[value]?.let { height ->
                            SelectScrollItemBounds(center - height / 2f, height)
                        }
                    }
                scrollState.scrollTo(
                    selectScrollTarget(
                        items = items,
                        up = up,
                        scroll = scrollState.value.toFloat(),
                        viewport = scrollState.viewportSize.toFloat(),
                        maxScroll = scrollState.maxValue.toFloat(),
                        scale = density.density,
                    ).roundToInt(),
                )
            }
        }
    }
    Box(
        modifier =
            modifier.fillMaxWidth()
                .then(
                    if (nova) {
                        Modifier.background(currentTheme.colors.popover).focusProperties { canFocus = false }
                            .onGloballyPositioned { pointer.coordinates = it }
                            .clearAndSetSemantics { hideFromAccessibility() }
                            .pointerInput(keyboard, up, popupOpen) {
                                if (!popupOpen) return@pointerInput
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        if (event.type == PointerEventType.Exit) autoScroll = false
                                        val moving = event.type == PointerEventType.Move
                                        if (moving || event.type == PointerEventType.Enter) {
                                            val coordinates = pointer.coordinates
                                            val change = event.changes.firstOrNull { it.type == PointerType.Mouse }
                                            if (coordinates?.isAttached == true && change != null) {
                                                val position = coordinates.localToWindow(change.position)
                                                if (position != keyboard.previousMousePosition) autoScroll = true
                                            }
                                        }
                                    }
                                }
                            }
                    } else {
                        Modifier
                    },
                )
                .clickable(enabled = popupOpen, role = Role.Button) {
                    if (popupOpen) scope.launch { scrollState?.scrollBy(if (up) -clickDistance else clickDistance) }
                }
                .then(if (nova) Modifier.padding(vertical = 4.dp) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (nova) NovaChevronRight else ChevronRight,
            tint = currentTheme.colors.mutedForeground,
            modifier = Modifier.rotate(if (up) -90f else 90f),
            variant = if (nova) IconVariant.Default else IconVariant.Small,
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
    val popupOpen = ctrl.popupOpen
    val canActivate = popupOpen && resolvedEnabled
    val isSelected = ctrl.isSelected(value)
    val keyboard = checkNotNull(LocalSelectKeyboard.current)
    val labelText = textValue ?: ctrl.labelOf(value)
    val activate = {
        if (resolvedEnabled && !ctrl.disabled && onSelect?.invoke() != true) ctrl.select(value)
    }
    if (LocalSelectRegistrationOnly.current) {
        val registration = SelectSearchItem(value, labelText, resolvedEnabled, activate)
        Layout(content = {}, modifier = Modifier.layoutId(registration)) { _, _ -> layout(0, 0) {} }
        return
    }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val requester = remember { FocusRequester() }
    var itemCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    DisposableEffect(keyboard, value) {
        keyboard.items.add(SelectFocusItem(value, requester, resolvedEnabled))
        onDispose {
            keyboard.items.removeAll { it.requester === requester }
            keyboard.pick.remove(value)
            keyboard.closedPick.remove(value)
            keyboard.centers.remove(value)
            keyboard.naturalCenters.remove(value)
            keyboard.naturalHeights.remove(value)
            keyboard.itemTextEdges.remove(value)
            keyboard.labels.remove(value)
        }
    }
    SideEffect {
        keyboard.labels[value] = labelText
        keyboard.closedPick[value] = activate
        keyboard.pick[value] = { if (popupOpen) activate() }
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
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val suppressFocus = currentTheme.activePreset == StylePreset.Nova && keyboard.scrollHovered
    val scrollStyle = remember { Style { background(Color.Transparent) } }
    val itemContentColor =
        if (!suppressFocus && (if (nova) isFocused else resolvedEnabled && isHovered)) {
            currentTheme.colors.accentForeground
        } else {
            currentTheme.colors.popoverForeground
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .focusRequester(requester)
                .focusProperties { canFocus = popupOpen }
                .then(
                    if (nova && canActivate) {
                        Modifier.pointerInput(requester) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val mouse = event.changes.firstOrNull { it.type == PointerType.Mouse }
                                    val position = mouse?.let { itemCoordinates?.localToWindow(it.position) }
                                    if (event.type == PointerEventType.Move &&
                                        position != null && position != keyboard.previousMousePosition &&
                                        !keyboard.scrollHovered
                                    ) {
                                        requester.requestFocus()
                                    }
                                }
                            }
                        }
                    } else {
                        Modifier
                    },
                )
                .onGloballyPositioned {
                    itemCoordinates = it
                    if (!nova) keyboard.centers[value] = it.boundsInRoot().center.y
                    keyboard.contentCoordinates?.takeIf { coordinates -> coordinates.isAttached }?.let { content ->
                        keyboard.naturalCenters[value] = content.localPositionOf(it, Offset(0f, it.size.height / 2f)).y
                        keyboard.naturalHeights[value] = it.size.height.toFloat()
                    }
                }
                .onFocusChanged {
                    if (popupOpen && it.isFocused) {
                        keyboard.active = value
                        keyboard.hasActive = true
                    }
                }
                .semantics {
                    role = Role.Button
                    this.selected = isSelected
                    if (!canActivate) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = canActivate,
                    role = Role.Button,
                    // Radix `onSelect` preventDefault: true skips selecting (and closing).
                    onClick = {
                        if (popupOpen) activate()
                    },
                )
                .then(
                    if (nova && !resolvedEnabled) {
                        Modifier.focusable(enabled = popupOpen, interactionSource = interactionSource)
                    } else {
                        Modifier
                    },
                )
                .styleable(styleState, rememberSelectItemStyle() then if (suppressFocus) scrollStyle else Style),
        horizontalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) 6.dp else twSpacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides itemContentColor,
            LocalSelectItemSelected provides isSelected,
            LocalSelectTextItem provides remember(value) { SelectTextItem(value) },
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
        modifier = modifier.selectItemTextCoordinates().weight(1f).height(with(LocalDensity.current) { 20.sp.toDp() }),
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
        modifier = modifier.selectItemTextCoordinates().weight(1f),
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
    if (LocalSelectRegistrationOnly.current) {
        content()
        return
    }
    val nova = currentTheme.activePreset == StylePreset.Nova
    Column(modifier = modifier.then(if (nova) Modifier.padding(4.dp) else Modifier)) {
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
    if (LocalSelectRegistrationOnly.current) return
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
    if (LocalSelectRegistrationOnly.current) return
    if (currentTheme.activePreset == StylePreset.Nova) {
        // CSS my-1 surrounds the line; its -mx-1 overflow is clipped at the
        // popup edges. A full-width line has that same visible horizontal extent.
        Box(modifier = modifier.padding(vertical = 4.dp)) {
            Separator(style = style)
        }
    } else {
        Separator(modifier = modifier, style = style)
    }
}
