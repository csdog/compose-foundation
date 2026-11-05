@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tabs

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.overlay.OverlayAnimMs
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import kotlin.math.roundToInt
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Immutable
private data class TabsController(
    val selected: String,
    val onSelectedChange: (String) -> Unit,
    val orientation: TabsOrientation,
    val activationMode: TabsActivationMode,
    val loop: Boolean,
    val controlled: Boolean,
)

enum class TabsOrientation { Horizontal, Vertical }

/** Radix `activationMode`: arrow keys select (Automatic) or only move focus (Manual). */
enum class TabsActivationMode { Automatic, Manual }

/** Official `TabsList` `variant`. Default keeps the muted track + sliding pill. */
enum class TabsListVariant { Default, Line }

private val LocalTabsListVariant = staticCompositionLocalOf { TabsListVariant.Default }

private class TabsListState {
    var listCoordinates: LayoutCoordinates? = null
    var focusedTab by mutableStateOf<String?>(null)
    var hadRegisteredTabs = false
    val tabBounds = mutableStateMapOf<String, Rect>()
    val tabOrder = mutableStateListOf<String>()
    val tabEnabled = mutableStateMapOf<String, Boolean>()
    private val focusRequesters = mutableMapOf<String, FocusRequester>()

    fun requester(value: String): FocusRequester = focusRequesters.getOrPut(value) { FocusRequester() }

    fun unregister(value: String) {
        tabOrder.remove(value)
        tabEnabled.remove(value)
        tabBounds.remove(value)
        focusRequesters.remove(value)
    }

    fun updateTabBounds(
        value: String,
        tabCoordinates: LayoutCoordinates,
    ) {
        val list = listCoordinates ?: return
        if (!list.isAttached || !tabCoordinates.isAttached) return
        val topLeft = list.localPositionOf(tabCoordinates, Offset.Zero)
        val size = tabCoordinates.size
        tabBounds[value] =
            Rect(
                left = topLeft.x,
                top = topLeft.y,
                right = topLeft.x + size.width,
                bottom = topLeft.y + size.height,
            )
    }
}

private val LocalTabs = staticCompositionLocalOf<TabsController?> { null }
private val LocalTabsListState = compositionLocalOf<TabsListState?> { null }

/** Selected tab of the nearest [Tabs]. Matches Radix tabs context. */
class TabsState(
    val selected: String,
    val onSelectedChange: (String) -> Unit,
    val orientation: TabsOrientation,
    val activationMode: TabsActivationMode,
)

/** Reads the nearest [Tabs] state. */
@Composable
fun tabsState(): TabsState {
    val ctrl =
        checkNotNull(LocalTabs.current) {
            "tabsState() must be used inside Tabs { … }"
        }
    return TabsState(
        selected = ctrl.selected,
        onSelectedChange = ctrl.onSelectedChange,
        orientation = ctrl.orientation,
        activationMode = ctrl.activationMode,
    )
}

class TabsScope internal constructor()

class TabsListScope internal constructor()

/**
 * Root tabs state, matching real shadcn/ui's `Tabs` + Compose Unstyled's `TabGroup`.
 * Compose [TabsList] / [TabsContent] inside.
 *
 * Usage:
 * ```
 * var tab by remember { mutableStateOf("account") }
 * Tabs(selected = tab, onSelectedChange = { tab = it }) {
 *     TabsList {
 *         TabsTrigger(value = "account", label = "Account")
 *         TabsTrigger(value = "password", label = "Password")
 *     }
 *     TabsContent("account", "Account settings")
 * }
 *
 * Tabs(..., orientation = TabsOrientation.Vertical) { … }
 * Tabs(defaultValue = "account") { … }
 * Tabs(..., activationMode = TabsActivationMode.Manual) { … }
 * ```
 *
 * [activationMode] defaults to [TabsActivationMode.Automatic], matching Base UI
 * and Radix (`Tabs.Root` has no explicit default, so arrow keys both move
 * focus and select the panel). Pass [TabsActivationMode.Manual] to keep focus
 * movement separate from activation.
 */
@Composable
fun Tabs(
    selected: String? = null,
    onSelectedChange: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultValue: String = "",
    orientation: TabsOrientation = TabsOrientation.Horizontal,
    activationMode: TabsActivationMode = TabsActivationMode.Automatic,
    loop: Boolean = true,
    style: Style = Style,
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    content: @Composable TabsScope.() -> Unit,
) {
    var internalSelected by remember { mutableStateOf(defaultValue) }
    val resolvedSelected = value ?: selected
    val resolvedOnChange = onValueChange ?: onSelectedChange
    val current = resolvedSelected ?: internalSelected
    val controlled = rememberUpdatedState(resolvedSelected != null)
    val latestSelected = rememberUpdatedState(current)
    val latestChange = rememberUpdatedState(resolvedOnChange)
    val setSelected: (String) -> Unit =
        remember {
            { next ->
                val previous = if (controlled.value) latestSelected.value else internalSelected
                if (next != previous) {
                    if (!controlled.value) internalSelected = next
                    latestChange.value?.invoke(next)
                }
            }
        }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(
        LocalTabs provides
            TabsController(
                current,
                setSelected,
                orientation,
                activationMode,
                loop,
                resolvedSelected != null,
            ),
    ) {
        TabsLayout(modifier.styleable(styleState, style), orientation) { TabsScope().content() }
    }
}

private object HiddenTabPanel : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = HiddenTabPanel
}

/** Hidden kept panels do not consume CSS gap or participate in placement/focus. */
@Composable
private fun TabsLayout(
    modifier: Modifier,
    orientation: TabsOrientation,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { children, constraints ->
        val column = orientation == TabsOrientation.Horizontal
        val gap = 8.dp.roundToPx()
        val visible = children.filter { it.parentData !== HiddenTabPanel }
        var used = 0
        val placeables =
            visible.mapIndexed { index, child ->
                if (index > 0) used += gap
                child.measure(
                    if (column) {
                        constraints.copy(minWidth = 0, minHeight = 0, maxHeight = Constraints.Infinity)
                    } else {
                        constraints.copy(
                            minWidth = 0,
                            minHeight = 0,
                            maxWidth =
                                if (constraints.hasBoundedWidth) {
                                    (constraints.maxWidth - used).coerceAtLeast(0)
                                } else {
                                    Constraints.Infinity
                                },
                        )
                    },
                ).also { used += if (column) it.height else it.width }
            }
        val cross = placeables.maxOfOrNull { if (column) it.width else it.height } ?: 0
        val width = constraints.constrainWidth(if (column) cross else used)
        val height = constraints.constrainHeight(if (column) used else cross)
        layout(width, height) {
            var offset = 0
            placeables.forEachIndexed { index, child ->
                if (index > 0) offset += gap
                child.placeRelative(if (column) 0 else offset, if (column) offset else 0)
                offset += if (column) child.height else child.width
            }
        }
    }
}

/**
 * The muted track holding tab triggers; the selected trigger paints its own chrome.
 * [variant] `Line` uses the source's external 2dp underline.
 * [indicator] is an optional project decoration slot and is empty by default.
 */
@Composable
fun TabsScope.TabsList(
    modifier: Modifier = Modifier,
    variant: TabsListVariant = TabsListVariant.Default,
    style: Style = Style,
    contentDescription: String? = null,
    indicator: @Composable BoxScope.() -> Unit = {},
    content: @Composable TabsListScope.() -> Unit,
) {
    val listStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val listState = remember { TabsListState() }
    val vertical = LocalTabs.current?.orientation == TabsOrientation.Vertical
    val itemGap = if (variant == TabsListVariant.Line) 4.dp else 0.dp
    val listDescription = contentDescription
    val tabs = checkNotNull(LocalTabs.current)
    val registrations = listState.tabOrder.map { it to listState.tabEnabled[it] }
    LaunchedEffect(registrations, tabs.selected, tabs.controlled) {
        if (!tabs.controlled) {
            val ready = registrations.isNotEmpty() && registrations.all { it.second != null }
            if (ready || (registrations.isEmpty() && listState.hadRegisteredTabs)) {
                if (ready) listState.hadRegisteredTabs = true
                val enabled = registrations.filter { it.second == true }.map { it.first }
                if (tabs.selected !in enabled) tabs.onSelectedChange(enabled.firstOrNull() ?: "")
            }
        }
    }
    LaunchedEffect(listState.focusedTab) {
        listState.focusedTab?.takeIf { listState.tabEnabled[it] == true }?.let {
            listState.requester(it).requestFocus()
        }
    }

    CompositionLocalProvider(
        LocalTabsListState provides listState,
        LocalTabsListVariant provides variant,
    ) {
        Box(
            modifier =
                modifier
                    .selectableGroup()
                    .then(
                        if (listDescription != null) {
                            Modifier.semantics { this.contentDescription = listDescription }
                        } else {
                            Modifier
                        },
                    )
                    .onGloballyPositioned { listState.listCoordinates = it }
                    .styleable(
                        listStyleState,
                        rememberTabsListStyle(variant, horizontal = !vertical) then style,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            indicator()
            if (vertical) {
                Column(Modifier.width(IntrinsicSize.Max), verticalArrangement = Arrangement.spacedBy(itemGap)) {
                    TabsListScope().content()
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(itemGap)) {
                    TabsListScope().content()
                }
            }
        }
    }
}

/**
 * Sliding selected chrome behind [TabsTrigger]s. Base UI does not export a
 * separate `TabsIndicator` (Default pill / Line underline are CSS). Kept
 * internal; [TabsList] still composes it by default.
 */
@Composable
internal fun BoxScope.TabsIndicator(
    modifier: Modifier = Modifier,
    style: Style = Style,
    animationSpec: AnimationSpec<Float> = tween(OverlayAnimMs),
) {
    val tabs = LocalTabs.current ?: return
    val listState = LocalTabsListState.current ?: return
    val variant = LocalTabsListVariant.current
    val bounds = listState.tabBounds[tabs.selected]
    val density = LocalDensity.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val vertical = tabs.orientation == TabsOrientation.Vertical
    val linePx = with(density) { 2.dp.toPx() }

    val targetLeft: Float
    val targetWidth: Float
    val targetTop: Float
    val targetHeight: Float
    if (bounds == null) {
        targetLeft = 0f
        targetWidth = 0f
        targetTop = 0f
        targetHeight = 0f
    } else if (variant == TabsListVariant.Line) {
        if (vertical) {
            targetLeft = bounds.right - linePx
            targetWidth = linePx
            targetTop = bounds.top
            targetHeight = bounds.height
        } else {
            targetLeft = bounds.left
            targetWidth = bounds.width
            targetTop = bounds.bottom - linePx
            targetHeight = linePx
        }
    } else {
        targetLeft = bounds.left
        targetWidth = bounds.width
        targetTop = bounds.top
        targetHeight = bounds.height
    }

    val left by animateFloatAsState(targetLeft, animationSpec, label = "tabs-indicator-left")
    val width by animateFloatAsState(targetWidth, animationSpec, label = "tabs-indicator-width")
    val top by animateFloatAsState(targetTop, animationSpec, label = "tabs-indicator-top")
    val height by animateFloatAsState(targetHeight, animationSpec, label = "tabs-indicator-height")

    if (bounds == null && targetWidth == 0f) return

    Box(
        modifier =
            modifier
                .align(Alignment.TopStart)
                .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                .width(with(density) { width.toDp().coerceAtLeast(0.dp) })
                .height(with(density) { height.toDp().coerceAtLeast(0.dp) })
                .styleable(styleState, rememberTabsIndicatorStyle(variant) then style),
    )
}

/**
 * One selectable tab label. Registers its bounds so [TabsIndicator] can slide here.
 */
@Composable
fun TabsListScope.TabsTrigger(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val tabs = checkNotNull(LocalTabs.current) { "TabsTrigger must be used inside Tabs" }
    val listState =
        checkNotNull(LocalTabsListState.current) { "TabsTrigger must be used inside TabsList" }
    val resolvedEnabled = enabled && !disabled
    val isSelected = tabs.selected == value
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = resolvedEnabled
        }
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val hovered by interactionSource.collectIsHoveredAsState()
    val direction = LocalLayoutDirection.current
    val colors = currentTheme.colors
    val dark = currentTheme.isDark
    val variant = LocalTabsListVariant.current
    val enabledTabs = listState.tabOrder.filter { listState.tabEnabled[it] == true }
    val focusEntry =
        listState.focusedTab?.takeIf { it in enabledTabs }
            ?: tabs.selected.takeIf { it in enabledTabs } ?: enabledTabs.firstOrNull()
    val novaHorizontal = currentTheme.activePreset == StylePreset.Nova && tabs.orientation == TabsOrientation.Horizontal
    val itemSizeStyle =
        remember(novaHorizontal) {
            // Nova list32 - padding6 - the trigger's explicit calc(100% - 1px).
            if (novaHorizontal) Style { height(25.dp) } else Style
        }
    val requester = listState.requester(value)

    DisposableEffect(value) {
        if (value !in listState.tabOrder) listState.tabOrder.add(value)
        onDispose { listState.unregister(value) }
    }
    SideEffect {
        listState.tabEnabled[value] = resolvedEnabled
    }

    Box(
        modifier =
            modifier
                .then(if (tabs.orientation == TabsOrientation.Vertical) Modifier.fillMaxWidth() else Modifier)
                .onGloballyPositioned { listState.updateTabBounds(value, it) }
                .keyboardInputMode()
                .focusRequester(requester)
                .focusProperties { canFocus = resolvedEnabled && value == focusEntry }
                .onFocusChanged { if (it.isFocused) listState.focusedTab = value }
                .semantics {
                    role = Role.Tab
                    this.selected = isSelected
                    if (!resolvedEnabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Tab,
                    onClick = {
                        listState.focusedTab = value
                        tabs.onSelectedChange(value)
                    },
                )
                .onPreviewKeyEvent { handleTabsKey(tabs, listState, value, it, direction) }
                .drawWithContent {
                    drawContent()
                    if (variant == TabsListVariant.Line && isSelected) {
                        val thickness = 2.dp.toPx()
                        if (tabs.orientation == TabsOrientation.Horizontal) {
                            drawRect(
                                colors.foreground,
                                Offset(0f, size.height + 3.dp.toPx()),
                                androidx.compose.ui.geometry.Size(size.width, thickness),
                            )
                        } else {
                            val x = if (direction == LayoutDirection.Rtl) -4.dp.toPx() else size.width + 2.dp.toPx()
                            drawRect(
                                colors.foreground,
                                Offset(x, 0f),
                                androidx.compose.ui.geometry.Size(thickness, size.height),
                            )
                        }
                    }
                }
                .cssStyleable(
                    styleState,
                    touchTargetStyle then
                        rememberTabItemStyle(isSelected, variant) then itemSizeStyle then style,
                ),
        contentAlignment =
            if (tabs.orientation == TabsOrientation.Vertical) {
                Alignment.CenterStart
            } else {
                Alignment.Center
            },
    ) {
        val color =
            when {
                isSelected || hovered -> colors.foreground
                dark -> colors.mutedForeground
                else -> colors.foreground.copy(alpha = colors.foreground.alpha * .6f)
            }
        CompositionLocalProvider(LocalContentColor provides color) {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp),
                verticalAlignment =
                    Alignment.CenterVertically,
            ) { content() }
        }
    }
}

/** Convenience trigger that takes a string label. */
@Composable
fun TabsListScope.TabsTrigger(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    TabsTrigger(value = value, modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
        Text(
            modifier = Modifier.wrapContentWidth(unbounded = true),
            maxLines = 1,
            overflow = TextOverflow.Visible,
            variant = TextVariant.LabelLarge,
            textStyle =
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                ),
        ) { label }
    }
}

/**
 * Panel shown when [value] matches the selected tab, immediately by default.
 * Explicit [enter]/[exit] transitions remain available as project extensions.
 */
@Composable
fun TabsScope.TabsContent(
    value: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition = EnterTransition.None,
    exit: ExitTransition = ExitTransition.None,
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val tabs = checkNotNull(LocalTabs.current) { "TabsContent must be used inside Tabs" }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val selected = tabs.selected == value
    var panelHasFocus by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(selected) {
        if (!selected && panelHasFocus) focusManager.clearFocus(force = true)
    }
    val styled = modifier.styleable(styleState, style)
    if (keepMounted ?: forceMount) {
        Layout(
            modifier =
                (if (selected) styled else Modifier.then(HiddenTabPanel).clearAndSetSemantics {})
                    .onFocusChanged { panelHasFocus = it.hasFocus }
                    .focusProperties { onEnter = { if (!selected) cancelFocusChange() } }.focusGroup(),
            content = { Box(if (selected) Modifier else styled) { content() } },
        ) { children, constraints ->
            if (selected) {
                val child = children.single().measure(constraints)
                layout(child.width, child.height) { child.placeRelative(0, 0) }
            } else {
                layout(0, 0) {}
            }
        }
    } else if (enter == EnterTransition.None && exit == ExitTransition.None) {
        if (selected) Box(styled) { content() }
    } else {
        AnimatedVisibility(
            visible = selected,
            enter = enter,
            exit = exit,
            modifier = styled,
        ) {
            content()
        }
    }
}

/** String-body overload of [TabsContent]. */
@Composable
fun TabsScope.TabsContent(
    value: String,
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition = EnterTransition.None,
    exit: ExitTransition = ExitTransition.None,
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
) {
    TabsContent(
        value = value,
        modifier = modifier,
        style = style,
        enter = enter,
        exit = exit,
        forceMount = forceMount,
        keepMounted = keepMounted,
    ) {
        Text { text }
    }
}

private fun handleTabsKey(
    tabs: TabsController,
    listState: TabsListState,
    current: String,
    event: KeyEvent,
    direction: LayoutDirection,
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    val enabled =
        listState.tabOrder.filter { listState.tabEnabled[it] != false }.sortedBy {
            val bounds = listState.tabBounds[it]
            when {
                tabs.orientation == TabsOrientation.Vertical -> bounds?.top ?: Float.MAX_VALUE
                direction == LayoutDirection.Rtl -> -(bounds?.left ?: -Float.MAX_VALUE)
                else -> bounds?.left ?: Float.MAX_VALUE
            }
        }
    if (enabled.isEmpty()) return false
    val idx = enabled.indexOf(current)
    if (idx < 0) return false
    val vertical = tabs.orientation == TabsOrientation.Vertical
    val last = enabled.lastIndex
    val next =
        when (event.key) {
            Key.DirectionRight ->
                if (vertical) {
                    return false
                } else {
                    stepTab(
                        enabled,
                        idx,
                        if (direction == LayoutDirection.Rtl) {
                            -1
                        } else {
                            1
                        },
                        last,
                        tabs.loop,
                    )
                        ?: return false
                }
            Key.DirectionLeft ->
                if (vertical) {
                    return false
                } else {
                    stepTab(
                        enabled,
                        idx,
                        if (direction == LayoutDirection.Rtl) {
                            1
                        } else {
                            -1
                        },
                        last,
                        tabs.loop,
                    )
                        ?: return false
                }
            Key.DirectionDown ->
                if (!vertical) {
                    return false
                } else {
                    stepTab(enabled, idx, +1, last, tabs.loop) ?: return false
                }
            Key.DirectionUp ->
                if (!vertical) {
                    return false
                } else {
                    stepTab(enabled, idx, -1, last, tabs.loop) ?: return false
                }
            Key.MoveHome -> enabled.first()
            Key.MoveEnd -> enabled[last]
            Key.Enter, Key.NumPadEnter, Key.Spacebar -> {
                tabs.onSelectedChange(current)
                return true
            }
            else -> return false
        }
    listState.focusedTab = next
    if (tabs.activationMode == TabsActivationMode.Automatic) {
        tabs.onSelectedChange(next)
    }
    return true
}

private fun stepTab(
    enabled: List<String>,
    idx: Int,
    delta: Int,
    last: Int,
    loop: Boolean,
): String? {
    val next = idx + delta
    return when {
        next in 0..last -> enabled[next]
        loop && enabled.isNotEmpty() -> enabled[(next + enabled.size) % enabled.size]
        else -> null
    }
}
