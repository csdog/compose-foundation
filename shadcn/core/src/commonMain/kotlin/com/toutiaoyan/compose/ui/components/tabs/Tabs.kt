@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.overlay.OverlayAnimMs
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import kotlin.math.roundToInt
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twSpacing
import androidx.compose.ui.text.TextStyle

@Immutable
private data class TabsController(
    val selected: String,
    val onSelectedChange: (String) -> Unit,
    val orientation: TabsOrientation,
    val activationMode: TabsActivationMode,
    val loop: Boolean,
)

enum class TabsOrientation { Horizontal, Vertical }

/** Radix `activationMode`: arrow keys select (Automatic) or only move focus (Manual). */
enum class TabsActivationMode { Automatic, Manual }

/** Official `TabsList` `variant`. Default keeps the muted track + sliding pill. */
enum class TabsListVariant { Default, Line }

private val LocalTabsListVariant = staticCompositionLocalOf { TabsListVariant.Default }

private class TabsListState {
    var listCoordinates: LayoutCoordinates? = null
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
    val setSelected: (String) -> Unit = { next ->
        if (resolvedSelected == null) internalSelected = next
        resolvedOnChange?.invoke(next)
    }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(
        LocalTabs provides TabsController(current, setSelected, orientation, activationMode, loop),
    ) {
        if (orientation == TabsOrientation.Vertical) {
            Row(
                modifier = modifier.styleable(styleState, style),
                horizontalArrangement = Arrangement.spacedBy(twSpacing.md),
            ) {
                TabsScope().content()
            }
        } else {
            Column(
                modifier = modifier.styleable(styleState, style),
                verticalArrangement = Arrangement.spacedBy(twSpacing.md),
            ) {
                TabsScope().content()
            }
        }
    }
}

/**
 * The muted track that holds triggers + a sliding [TabsIndicator] pill.
 * [variant] `Line` is official `variant="line"`: transparent list + 2.dp underline.
 * [indicator] defaults to [TabsIndicator]; pass `{}` to disable.
 */
@Composable
fun TabsScope.TabsList(
    modifier: Modifier = Modifier,
    variant: TabsListVariant = TabsListVariant.Default,
    style: Style = Style,
    contentDescription: String? = null,
    indicator: @Composable BoxScope.() -> Unit = { TabsIndicator() },
    content: @Composable TabsListScope.() -> Unit,
) {
    val listStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val listState = remember { TabsListState() }
    val vertical = LocalTabs.current?.orientation == TabsOrientation.Vertical
    val itemGap = if (variant == TabsListVariant.Line) twSpacing.xs else twSpacing.xs2
    val listDescription = contentDescription

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
        ) {
            indicator()
            if (vertical) {
                Column(verticalArrangement = Arrangement.spacedBy(itemGap)) {
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
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
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
                .focusRequester(requester)
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
                    onClick = { tabs.onSelectedChange(value) },
                )
                .onPreviewKeyEvent { handleTabsKey(tabs, listState, value, it) }
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .onGloballyPositioned { listState.updateTabBounds(value, it) }
                    .styleable(
                        styleState,
                        rememberTabItemStyle(isSelected) then style,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            content()
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
    val tabs = LocalTabs.current
    val isSelected = tabs?.selected == value
    TabsTrigger(value = value, modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
        Text(
            variant = TextVariant.LabelLarge,
            textStyle = if (!isSelected) TextColors.Muted else TextStyle.Default,
        ) { label }
    }
}

/**
 * Panel shown when [value] matches the selected tab. Defaults to a light fade;
 * override [enter]/[exit] like other shadcn overlay panels.
 */
@Composable
fun TabsScope.TabsContent(
    value: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition = fadeIn(tween(OverlayAnimMs)),
    exit: ExitTransition = fadeOut(tween(OverlayAnimMs)),
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val tabs = checkNotNull(LocalTabs.current) { "TabsContent must be used inside Tabs" }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val selected = tabs.selected == value
    val styled = modifier.styleable(styleState, style)
    if (keepMounted ?: forceMount) {
        Box(modifier = if (selected) styled else Modifier.size(0.dp)) {
            content()
        }
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
    enter: EnterTransition = fadeIn(tween(OverlayAnimMs)),
    exit: ExitTransition = fadeOut(tween(OverlayAnimMs)),
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
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    val enabled = listState.tabOrder.filter { listState.tabEnabled[it] != false }
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
                    stepTab(enabled, idx, +1, last, tabs.loop) ?: return false
                }
            Key.DirectionLeft ->
                if (vertical) {
                    return false
                } else {
                    stepTab(enabled, idx, -1, last, tabs.loop) ?: return false
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
    listState.requester(next).requestFocus()
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
