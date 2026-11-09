@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.focusable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.withFrameNanos
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.icons.NovaChevronRight
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

enum class NavigationMenuOrientation { Horizontal, Vertical }

@Immutable
internal class NavigationMenuController(
    val openValue: String?,
    val onOpenChange: (String?) -> Unit,
    val orientation: NavigationMenuOrientation,
    val delayDuration: Int,
    val skipDelayDuration: Int,
    val closeDelayDuration: Int,
    private val lastCloseMark: () -> TimeSource.Monotonic.ValueTimeMark?,
    private val onClosed: () -> Unit,
) {
    fun shouldSkipDelay(): Boolean =
        openValue != null ||
            (
                skipDelayDuration > 0 &&
                    lastCloseMark()?.elapsedNow()?.inWholeMilliseconds?.let { it < skipDelayDuration } == true
            )

    fun markClosed() = onClosed()
}

private val LocalNavigationMenu = staticCompositionLocalOf<NavigationMenuController?> { null }

@Composable
private fun navigationMenuController(): NavigationMenuController =
    checkNotNull(LocalNavigationMenu.current) {
        "NavigationMenuList / NavigationMenuItem must be used inside NavigationMenu { … }"
    }

/** Open item id of the nearest [NavigationMenu]. Matches Radix navigation-menu context. */
class NavigationMenuState(
    val openValue: String?,
    val onOpenChange: (String?) -> Unit,
    val orientation: NavigationMenuOrientation,
) {
    val value: String? get() = openValue
}

/** Reads the nearest [NavigationMenu] state. */
@Composable
fun navigationMenuState(): NavigationMenuState {
    val ctrl = navigationMenuController()
    return NavigationMenuState(
        openValue = ctrl.openValue,
        onOpenChange = ctrl.onOpenChange,
        orientation = ctrl.orientation,
    )
}

class NavigationMenuScope internal constructor()

class NavigationMenuListScope internal constructor()

class NavigationMenuItemScope internal constructor(
    val value: String,
) {
    internal var keyboardOpened by mutableStateOf(false)
    internal val triggerFocus = FocusRequester()
    internal var triggerMounted = false
}

private val LocalNavigationMenuItem = staticCompositionLocalOf<NavigationMenuItemScope?> { null }
private val LocalNavigationMenuPanel = staticCompositionLocalOf { false }
private val LocalNavigationMenuPanelLinks = staticCompositionLocalOf<MutableList<FocusRequester>?> { null }

/** Reads the nearest [NavigationMenuItem] scope. */
@Composable
fun navigationMenuItemState(): NavigationMenuItemScope =
    checkNotNull(LocalNavigationMenuItem.current) {
        "navigationMenuItemState() must be used inside NavigationMenuItem { … }"
    }

/**
 * Horizontal top-level site nav matching real shadcn/ui's `navigation-menu.tsx`:
 *
 * ```
 * NavigationMenu
 * └── NavigationMenuList
 *     ├── NavigationMenuItem
 *     │   ├── NavigationMenuTrigger
 *     │   ├── NavigationMenuContent
 *     │   └── NavigationMenuIndicator
 *     └── NavigationMenuItem
 *         └── NavigationMenuLink
 * ```
 *
 * Deliberate approximation: real shadcn animates a shared viewport that morphs
 * between panels; this opens each item's own [AnchoredPopup].
 *
 * Panels open on hover (Radix navigation menu); click still toggles when there
 * is no hover, so touch can open them.
 *
 * Usage:
 * ```
 * NavigationMenu {
 *     NavigationMenuList {
 *         NavigationMenuItem(value = "home") {
 *             NavigationMenuLink("Home", onClick = {})
 *         }
 *         NavigationMenuItem(value = "start") {
 *             NavigationMenuTrigger("Getting started")
 *             NavigationMenuContent("Docs links here")
 *         }
 *     }
 * }
 * NavigationMenu(orientation = NavigationMenuOrientation.Vertical) { … }
 * ```
 */
@Composable
fun NavigationMenu(
    modifier: Modifier = Modifier,
    orientation: NavigationMenuOrientation = NavigationMenuOrientation.Horizontal,
    delayDuration: Int = 50,
    skipDelayDuration: Int = 300,
    value: String? = null,
    onValueChange: ((String?) -> Unit)? = null,
    defaultValue: String? = null,
    ariaLabel: String? = null,
    closeDelayDuration: Int = 50,
    content: @Composable NavigationMenuScope.() -> Unit,
) {
    var lastCloseMark by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }
    var internalOpen by remember { mutableStateOf(defaultValue) }
    val controlled = onValueChange != null
    val openValue = if (controlled) value else internalOpen
    val setOpen: (String?) -> Unit = { next ->
        if (next == null && openValue != null) {
            lastCloseMark = TimeSource.Monotonic.markNow()
        }
        if (!controlled) internalOpen = next
        onValueChange?.invoke(next)
    }
    val controller =
        NavigationMenuController(
            openValue = openValue,
            onOpenChange = setOpen,
            orientation = orientation,
            delayDuration = delayDuration.coerceAtLeast(0),
            skipDelayDuration = skipDelayDuration.coerceAtLeast(0),
            closeDelayDuration = closeDelayDuration.coerceAtLeast(0),
            lastCloseMark = { lastCloseMark },
            onClosed = { lastCloseMark = TimeSource.Monotonic.markNow() },
        )
    CompositionLocalProvider(LocalNavigationMenu provides controller, LocalNavigationMenuPanel provides false) {
        Box(
            modifier =
                if (ariaLabel != null) {
                    modifier.semantics { contentDescription = ariaLabel }
                } else {
                    modifier
                },
        ) {
            NavigationMenuScope().content()
        }
    }
}

/** Row of [NavigationMenuItem]s. Matches shadcn's `NavigationMenuList`. */
@Composable
fun NavigationMenuScope.NavigationMenuList(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable NavigationMenuListScope.() -> Unit,
) {
    val controller = navigationMenuController()
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val listModifier = modifier.styleable(rootStyleState, style)
    val gap = if (currentTheme.activePreset == StylePreset.Nova) 0.dp else twSpacing.xs
    if (controller.orientation == NavigationMenuOrientation.Vertical) {
        Column(
            modifier = listModifier,
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            NavigationMenuListScope().content()
        }
    } else {
        Row(
            modifier = listModifier,
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            NavigationMenuListScope().content()
        }
    }
}

private val LocalNavigationMenuItemBox = staticCompositionLocalOf<BoxScope?> { null }

@Immutable
private class NavigationMenuItemHoverController(
    val onTriggerHoverChange: (Boolean) -> Unit,
    val onContentHoverChange: (Boolean) -> Unit,
)

private val LocalNavigationMenuItemHover =
    staticCompositionLocalOf<NavigationMenuItemHoverController?> { null }

/** One item; [value] is the exclusive-open id when it has a panel. */
@Composable
fun NavigationMenuListScope.NavigationMenuItem(
    value: String,
    content: @Composable NavigationMenuItemScope.() -> Unit,
) {
    val controller = navigationMenuController()
    val scope = remember(value) { NavigationMenuItemScope(value) }
    var triggerHovered by remember { mutableStateOf(false) }
    var contentHovered by remember { mutableStateOf(false) }
    var hoverArmed by remember { mutableStateOf(false) }
    val hovered = triggerHovered || contentHovered
    val latestController by rememberUpdatedState(controller)
    LaunchedEffect(
        hovered,
        value,
        controller.delayDuration,
        controller.skipDelayDuration,
        controller.closeDelayDuration,
    ) {
        if (hovered) {
            val skip = controller.shouldSkipDelay()
            if (!skip && controller.delayDuration > 0) {
                delay(controller.delayDuration.toLong())
            }
            hoverArmed = true
            if (latestController.openValue != value) scope.keyboardOpened = false
            latestController.onOpenChange(value)
        } else if (hoverArmed) {
            delay(controller.closeDelayDuration.toLong())
            if (latestController.openValue == value) latestController.onOpenChange(null)
            hoverArmed = false
        }
    }
    val hover =
        remember {
            NavigationMenuItemHoverController(
                onTriggerHoverChange = { triggerHovered = it },
                onContentHoverChange = { contentHovered = it },
            )
        }
    Box {
        CompositionLocalProvider(
            LocalNavigationMenuItemBox provides this,
            LocalNavigationMenuItem provides scope,
            LocalNavigationMenuItemHover provides hover,
        ) {
            scope.content()
        }
    }
}

/** Trigger that opens [NavigationMenuContent]. Matches shadcn's `NavigationMenuTrigger`. */
@Composable
fun NavigationMenuItemScope.NavigationMenuTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = navigationMenuController()
    val isOpen = controller.openValue == value
    val hover = LocalNavigationMenuItemHover.current
    DisposableEffect(this) {
        triggerMounted = true
        onDispose { triggerMounted = false }
    }
    val resolvedEnabled = enabled && !disabled
    val direction = LocalLayoutDirection.current
    val openKey =
        if (controller.orientation == NavigationMenuOrientation.Horizontal) {
            Key.DirectionDown
        } else if (direction == LayoutDirection.Rtl) {
            Key.DirectionLeft
        } else {
            Key.DirectionRight
        }
    var stickyHoverPress by remember { mutableStateOf(false) }
    var triggerHovered by remember { mutableStateOf(false) }
    LaunchedEffect(isOpen) {
        stickyHoverPress = isOpen && triggerHovered
        if (stickyHoverPress) {
            delay(500)
            stickyHoverPress = false
        }
    }
    NavMenuHitTarget(
        modifier =
            modifier.focusRequester(triggerFocus).keyboardInputMode().onPreviewKeyEvent {
                if (it.type != KeyEventType.KeyDown || !resolvedEnabled) {
                    false
                } else if (it.key == openKey) {
                    keyboardOpened = true
                    controller.onOpenChange(value)
                    true
                } else if (it.key == Key.Escape && isOpen) {
                    controller.onOpenChange(null)
                    true
                } else {
                    false
                }
            },
        open = isOpen,
        expanded = isOpen,
        enabled = resolvedEnabled,
        style = style,
        onClick = { hovered, keyboard, expandedOnPress ->
            keyboardOpened = keyboard
            if (hovered && stickyHoverPress) {
                controller.onOpenChange(value)
            } else {
                controller.onOpenChange(if (expandedOnPress ?: isOpen) null else value)
            }
        },
        onHoverChange = {
            triggerHovered = it
            hover?.onTriggerHoverChange(it)
        },
    ) {
        if (currentTheme.activePreset == StylePreset.Nova) {
            val rotation = animateFloatAsState(if (isOpen) 180f else 0f, tween(300), label = "navigation chevron")
            Row(verticalAlignment = Alignment.CenterVertically) {
                content()
                Icon(
                    NovaChevronRight,
                    modifier =
                        Modifier.padding(start = 4.dp).offset(y = 1.dp).size(12.dp)
                            .graphicsLayer { rotationZ = 90f + rotation.value },
                )
            }
        } else {
            content()
        }
    }
}

/** String-label overload of [NavigationMenuTrigger]. */
@Composable
fun NavigationMenuItemScope.NavigationMenuTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    NavigationMenuTrigger(
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        style = style,
    ) {
        if (currentTheme.activePreset == StylePreset.Nova) {
            Text(maxLines = 1) { text }
        } else {
            Text(variant = TextVariant.BodySmall) { text }
        }
    }
}

@Composable
fun NavigationMenuItemScope.NavigationMenuContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    side: PopupPlacement? = null,
    // Base UI NavigationMenuPositioner default `sideOffset = 8`, `alignOffset = 0`.
    sideOffset: Dp = 8.dp,
    alignOffset: Dp = 0.dp,
    dismissOnClickOutside: Boolean = true,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable () -> Unit,
) {
    val foreground = currentTheme.colors.popoverForeground
    val controller = navigationMenuController()
    val isOpen = controller.openValue == value
    val panelFocus = remember { FocusRequester() }
    val panelLinks = remember { mutableListOf<FocusRequester>() }
    LaunchedEffect(isOpen) {
        if (!isOpen && keyboardOpened) {
            if (controller.openValue == null && triggerMounted) triggerFocus.requestFocus()
            keyboardOpened = false
        }
    }
    val panelStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val hover = LocalNavigationMenuItemHover.current
    val hoverSource = remember { MutableInteractionSource() }
    val contentHovered by hoverSource.collectIsHoveredAsState()
    val latestHover by rememberUpdatedState(hover)
    LaunchedEffect(contentHovered) { latestHover?.onContentHoverChange(contentHovered) }
    DisposableEffect(Unit) { onDispose { latestHover?.onContentHoverChange(false) } }
    val placement =
        side
            ?: if (controller.orientation == NavigationMenuOrientation.Vertical) {
                PopupPlacement.End
            } else {
                PopupPlacement.Bottom
            }
    AnchoredPopup(
        open = isOpen,
        onDismissRequest = { controller.onOpenChange(null) },
        placement = placement,
        offset = sideOffset,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        focusable = keyboardOpened && isOpen,
        enter = enter,
        exit = exit,
    ) {
        LaunchedEffect(isOpen, keyboardOpened) {
            if (isOpen && keyboardOpened) {
                withFrameNanos { }
                if (panelLinks.firstOrNull()?.requestFocus() != true) panelFocus.requestFocus()
            }
        }
        NavigationMenuPositioner {
            Box(
                modifier =
                    modifier
                        .onPreviewKeyEvent {
                            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape && isOpen) {
                                controller.onOpenChange(null)
                                true
                            } else {
                                false
                            }
                        }
                        .focusRequester(panelFocus)
                        .focusable(enabled = keyboardOpened && isOpen)
                        .hoverable(hoverSource)
                        .styleable(
                            panelStyleState,
                            rememberNavMenuPanelStyle() then style,
                        ),
            ) {
                CompositionLocalProvider(
                    LocalNavigationMenuPanel provides true,
                    LocalNavigationMenuPanelLinks provides panelLinks,
                    LocalContentColor provides foreground,
                ) { content() }
            }
        }
    }
}

/** String-body overload of [NavigationMenuContent]. */
@Composable
fun NavigationMenuItemScope.NavigationMenuContent(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    sideOffset: Dp = 8.dp,
    alignOffset: Dp = 0.dp,
    dismissOnClickOutside: Boolean = true,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
) {
    NavigationMenuContent(
        modifier = modifier,
        style = style,
        sideOffset = sideOffset,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        enter = enter,
        exit = exit,
    ) {
        Text { text }
    }
}

/**
 * Positioning host for [NavigationMenuContent]. Matches Base UI
 * `NavigationMenuPositioner` (Portal + Positioner + Popup + Viewport).
 * Shared-viewport morph between items is not ported — layout passthrough.
 */
@Composable
fun NavigationMenuPositioner(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (modifier === Modifier) {
        content()
    } else {
        Box(modifier) { content() }
    }
}

/** @deprecated Base UI exports [NavigationMenuPositioner]; use that name. */
@Deprecated(
    "Renamed for Base UI; use NavigationMenuPositioner",
    ReplaceWith("NavigationMenuPositioner(modifier, content)"),
)
@Composable
fun NavigationMenuViewport(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    NavigationMenuPositioner(modifier = modifier, content = content)
}

/** Plain nav link (no panel). Matches shadcn's `NavigationMenuLink`. */
@Composable
fun NavigationMenuItemScope.NavigationMenuLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    active: Boolean = false,
    style: Style = Style,
    closeOnClick: Boolean = true,
    content: @Composable () -> Unit,
) {
    val controller = navigationMenuController()
    val inPanel = LocalNavigationMenuPanel.current
    val panelLinks = LocalNavigationMenuPanelLinks.current
    val linkFocus = remember { FocusRequester() }
    val resolvedEnabled = enabled && !disabled
    DisposableEffect(panelLinks, resolvedEnabled) {
        if (resolvedEnabled) panelLinks?.add(linkFocus)
        onDispose { panelLinks?.remove(linkFocus) }
    }
    NavMenuHitTarget(
        modifier = modifier.focusRequester(linkFocus),
        open = active,
        active = active,
        enabled = resolvedEnabled,
        style = style,
        onClick = { _, _, _ ->
            onClick()
            if (closeOnClick) controller.onOpenChange(null)
        },
        onHoverChange = { hovered ->
            if (hovered && !inPanel && controller.openValue != null) {
                controller.onOpenChange(null)
            }
        },
        content = content,
    )
}

/** String-label overload of [NavigationMenuLink]. */
@Composable
fun NavigationMenuItemScope.NavigationMenuLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    active: Boolean = false,
    style: Style = Style,
    closeOnClick: Boolean = true,
) {
    NavigationMenuLink(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        active = active,
        style = style,
        closeOnClick = closeOnClick,
    ) {
        if (currentTheme.activePreset == StylePreset.Nova) {
            Text(maxLines = 1) { text }
        } else {
            Text(variant = TextVariant.BodySmall) { text }
        }
    }
}

/**
 * Caret under an open [NavigationMenuTrigger]. Hidden when the item is closed.
 * Matches shadcn's `NavigationMenuIndicator`.
 */
@Composable
fun NavigationMenuItemScope.NavigationMenuIndicator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val controller = navigationMenuController()
    if (controller.openValue != value) return
    val box =
        checkNotNull(LocalNavigationMenuItemBox.current) {
            "NavigationMenuIndicator must be used inside NavigationMenuItem { … }"
        }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    with(box) {
        Box(
            modifier =
                modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .size(8.dp)
                    .rotate(45f)
                    .styleable(styleState, rememberNavMenuIndicatorStyle() then style),
        )
    }
}

@Composable
private fun NavMenuHitTarget(
    modifier: Modifier,
    open: Boolean,
    style: Style,
    onClick: (hovered: Boolean, keyboard: Boolean, expandedOnPress: Boolean?) -> Unit,
    onHoverChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    expanded: Boolean? = null,
    active: Boolean = false,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var lastPointerType by remember { mutableStateOf(PointerType.Unknown) }
    var keyboardActivation by remember { mutableStateOf(false) }
    var expandedOnPress by remember { mutableStateOf<Boolean?>(null) }
    val latestExpanded by rememberUpdatedState(expanded)
    val latestHoverChange by rememberUpdatedState(onHoverChange)
    LaunchedEffect(isHovered, enabled) { latestHoverChange(isHovered && enabled) }
    DisposableEffect(Unit) { onDispose { latestHoverChange(false) } }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = enabled }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val labelStyle =
        if (nova) {
            TextStyle(
                fontFamily = theme.fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = if (expanded != null) FontWeight.Medium else FontWeight.Normal,
                letterSpacing = 0.sp,
            )
        } else {
            TextStyle.Default
        }
    val itemStyle =
        if (expanded != null) {
            rememberNavMenuItemStyle(open)
        } else {
            rememberNavMenuLinkStyle(
                active,
                LocalNavigationMenuPanel.current,
            )
        }
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    if (expanded != null) {
                        stateDescription = if (expanded) "Expanded" else "Collapsed"
                    }
                    if (active) selected = true
                    if (!enabled) disabled()
                }
                .hoverable(interactionSource, enabled = enabled)
                .keyboardInputMode()
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        lastPointerType = PointerType.Unknown
                        expandedOnPress = null
                        if (it.key == Key.Enter || it.key == Key.Spacebar) keyboardActivation = true
                    }
                    false
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            event.changes.firstOrNull { it.pressed && !it.previousPressed }?.let {
                                lastPointerType = it.type
                                expandedOnPress = latestExpanded
                                keyboardActivation = false
                            }
                        }
                    }
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = {
                        val expandedAtDown = expandedOnPress
                        expandedOnPress = null
                        onClick(isHovered && lastPointerType == PointerType.Mouse, keyboardActivation, expandedAtDown)
                    },
                )
                .styleable(
                    styleState,
                    touchTargetStyle then (if (nova) Style else pressStyle) then itemStyle then style,
                ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalDefaultTextStyle provides labelStyle) { content() }
    }
}
