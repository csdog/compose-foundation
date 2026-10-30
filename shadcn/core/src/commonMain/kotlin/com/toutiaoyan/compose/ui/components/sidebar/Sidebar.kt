@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.sidebar

import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.style.then
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.icons.Menu
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.tooltip.Tooltip
import com.toutiaoyan.compose.ui.components.tooltip.TooltipContent
import com.toutiaoyan.compose.ui.components.tooltip.TooltipTrigger
import com.toutiaoyan.compose.ui.components.sheet.Sheet
import com.toutiaoyan.compose.ui.components.sheet.SheetContent
import com.toutiaoyan.compose.ui.components.sheet.SheetSide
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Whether a [Sidebar] is open (full width) or closed, plus how to toggle it. */
class SidebarState(
    val open: Boolean,
    val toggle: () -> Unit,
    val collapsible: SidebarCollapsible = SidebarCollapsible.Offcanvas,
    val mobile: Boolean = false,
) {
    /** Icon-collapse rail: closed but still a narrow strip (`collapsible="icon"`). */
    val iconCollapsed: Boolean
        get() = !mobile && collapsible == SidebarCollapsible.Icon && !open
}

private val LocalSidebarState =
    compositionLocalOf<SidebarState> {
        error("Sidebar/SidebarTrigger must be used within a SidebarProvider")
    }

/** Published by [Sidebar] so sibling [SidebarInset] / [SidebarProvider] can match `variant`. */
internal class SidebarLayout {
    var variant by mutableStateOf(SidebarVariant.Sidebar)
    var side by mutableStateOf(SidebarSide.Start)
}

private val LocalSidebarLayout =
    compositionLocalOf<SidebarLayout> {
        error("Sidebar/SidebarInset must be used within a SidebarProvider")
    }

/** Reads the nearest [SidebarProvider] state. Matches shadcn's `useSidebar()`. */
@Composable
fun sidebarState(): SidebarState = LocalSidebarState.current

/**
 * Hoists open/collapsed state for a [Sidebar] + main content pair, matching real
 * shadcn/ui's `SidebarProvider`/`useSidebar()`. [SidebarCollapsible.Icon] keeps a
 * narrow rail (`3rem` / 48dp) instead of hiding the sidebar. Cmd/Ctrl+[shortcutKey]
 * (official `b`) toggles when focus is inside this provider — Compose has no window-
 * level key hook, so it is not app-wide. [SidebarVariant.Floating] / [SidebarVariant.Inset]
 * match official `variant`. Pass [mobile] `true` to render the rail as a [Sheet]
 * (official `useIsMobile`); default `false` so existing desktop goldens stay. Optional
 * [onPersistOpen] is the cookie hook (`SidebarDefaults.CookieName`, 7-day max-age) —
 * Compose has no `document.cookie`, so persistence is the caller's job.
 *
 * Usage:
 * ```
 * SidebarProvider { // uncontrolled, defaultOpen = true
 *     Sidebar { SidebarMenu { SidebarMenuItem("Home", onClick = {}, isActive = true) } }
 *     SidebarInset { SidebarTrigger(); Text { "Main content" } }
 * }
 * ```
 */
@Composable
fun SidebarProvider(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    collapsible: SidebarCollapsible = SidebarCollapsible.Offcanvas,
    defaultOpen: Boolean = true,
    shortcutEnabled: Boolean = true,
    shortcutKey: Key = Key.B,
    mobile: Boolean = false,
    onPersistOpen: ((Boolean) -> Unit)? = null,
    /**
     * When true (default), the provider row expands to max constraints — correct for a
     * page shell wrapping [Sidebar] + [SidebarInset]. Set false when embedding only
     * Sidebar primitives inside an outer layout that already sizes the rail (e.g. a
     * fixed-width catalog nav), otherwise `fillMaxSize` steals sibling space and can
     * produce invalid Constraints (`maxWidth < minWidth`).
     */
    fillMaxSize: Boolean = true,
    content: @Composable () -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen =
        if (collapsible == SidebarCollapsible.None) {
            true
        } else {
            open ?: internalOpen
        }
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
        onPersistOpen?.invoke(next)
    }
    val state =
        remember(isOpen, collapsible, mobile) {
            SidebarState(
                open = isOpen,
                toggle = {
                    if (collapsible != SidebarCollapsible.None) setOpen(!isOpen)
                },
                collapsible = collapsible,
                mobile = mobile,
            )
        }
    val layout = remember { SidebarLayout() }
    val shortcutModifier =
        Modifier.onPreviewKeyEvent { event ->
            if (!shortcutEnabled) return@onPreviewKeyEvent false
            if (collapsible == SidebarCollapsible.None) return@onPreviewKeyEvent false
            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
            if (event.key != shortcutKey) return@onPreviewKeyEvent false
            if (!(event.isCtrlPressed || event.isMetaPressed)) {
                return@onPreviewKeyEvent false
            }
            state.toggle()
            true
        }
    val sized =
        if (fillMaxSize) {
            modifier.fillMaxSize()
        } else {
            modifier
        }
    val rowModifier =
        if (layout.variant == SidebarVariant.Inset) {
            sized
                .background(currentTheme.colors.sidebar)
                .then(shortcutModifier)
        } else {
            sized.then(shortcutModifier)
        }
    CompositionLocalProvider(
        LocalSidebarState provides state,
        LocalSidebarLayout provides layout,
    ) {
        Row(modifier = rowModifier) { content() }
    }
}

enum class SidebarSide {
    Start,
    End,
}

/** Collapse behavior matching shadcn `collapsible`. */
enum class SidebarCollapsible {
    Offcanvas,
    Icon,
    None,
}

/**
 * Official shadcn sidebar metrics.
 */
object SidebarDefaults {
    /** Official `--sidebar-width` (`16rem`). */
    val Width = 256.dp

    /** Official `--sidebar-width-icon` (`3rem`). */
    val IconWidth = 48.dp

    /** Official `--sidebar-width-mobile` (`18rem`). */
    val MobileWidth = 288.dp

    const val CookieName = "sidebar_state"
    const val CookieMaxAgeSeconds = 60 * 60 * 24 * 7
}

/** The collapsible side rail itself -- must be a direct child of [SidebarProvider]. */
@Composable
fun Sidebar(
    modifier: Modifier = Modifier,
    width: Dp = SidebarDefaults.Width,
    iconWidth: Dp = 48.dp,
    side: SidebarSide = SidebarSide.Start,
    variant: SidebarVariant = SidebarVariant.Sidebar,
    ariaLabel: String? = null,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val state = LocalSidebarState.current
    val layout = LocalSidebarLayout.current
    layout.variant = variant
    layout.side = side
    val expanded = state.collapsible == SidebarCollapsible.None || state.open
    val padded = variant != SidebarVariant.Sidebar
    val collapsedWidth =
        when (state.collapsible) {
            SidebarCollapsible.Icon ->
                if (padded) iconWidth + twSpacing.lg else iconWidth
            SidebarCollapsible.Offcanvas, SidebarCollapsible.None -> 0.dp
        }
    val animatedWidth by animateDpAsState(if (expanded) width else collapsedWidth)
    val sidebarBorderColor = currentTheme.colors.sidebarBorder
    val railStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val contentStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val clipModifier =
        if (state.collapsible == SidebarCollapsible.Icon || padded) {
            Modifier.clipToBounds()
        } else {
            Modifier
        }
    val ariaModifier =
        if (ariaLabel != null) {
            Modifier.semantics { contentDescription = ariaLabel }
        } else {
            Modifier
        }
    if (state.mobile && state.collapsible != SidebarCollapsible.None) {
        val sheetSide = if (side == SidebarSide.End) SheetSide.End else SheetSide.Start
        Sheet(
            open = state.open,
            onOpenChange = { next -> if (next != state.open) state.toggle() },
            modifier = modifier.then(ariaModifier),
        ) {
            SheetContent(
                modifier = Modifier.width(SidebarDefaults.MobileWidth),
                side = sheetSide,
                showCloseButton = false,
                style = rememberSidebarContentStyle() then style,
            ) {
                content()
            }
        }
        return
    }
    if (!padded) {
        Box(
            modifier =
                modifier
                    .width(animatedWidth)
                    .then(clipModifier)
                    .then(ariaModifier)
                    .drawBehind {
                        val x = if (side == SidebarSide.End) 0f else size.width
                        drawLine(
                            color = sidebarBorderColor,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                    .styleable(railStyleState, rememberSidebarRailStyle() then style),
        ) {
            if (expanded || state.collapsible == SidebarCollapsible.Icon) {
                Column(
                    modifier =
                        Modifier
                            .width(width)
                            .styleable(contentStyleState, rememberSidebarContentStyle()),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
                    content = content,
                )
            }
        }
    } else {
        Box(
            modifier =
                modifier
                    .width(animatedWidth)
                    .then(clipModifier)
                    .then(ariaModifier)
                    .padding(twSpacing.sm),
        ) {
            if (expanded || state.collapsible == SidebarCollapsible.Icon) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .styleable(
                                contentStyleState,
                                rememberSidebarContentStyle() then
                                    rememberSidebarInnerStyle(variant) then
                                    style,
                            ),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
                    content = content,
                )
            }
        }
    }
}

/** The main content area beside the sidebar, matching real shadcn's `SidebarInset`. */
@Composable
fun SidebarInset(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val layout = LocalSidebarLayout.current
    val state = LocalSidebarState.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    if (layout.variant == SidebarVariant.Inset) {
        val edge = twSpacing.sm
        val collapsed = !state.open && state.collapsible != SidebarCollapsible.None
        val start = if (layout.side == SidebarSide.Start && !collapsed) 0.dp else edge
        val end = if (layout.side == SidebarSide.End && !collapsed) 0.dp else edge
        Column(
            modifier =
                modifier
                    .padding(start = start, top = edge, end = end, bottom = edge)
                    .styleable(
                        styleState,
                        rememberSidebarInsetCardStyle() then style,
                    ),
            content = content,
        )
    } else {
        Column(
            modifier =
                modifier.styleable(
                    styleState,
                    rememberSidebarInsetStyle() then style,
                ),
            content = content,
        )
    }
}

/** A hamburger-style button that toggles the nearest [SidebarProvider]'s open state. */
@Composable
fun SidebarTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    // A self-generated ImageVector, not a third-party icon-set dependency (this library
    // still takes none -- see README) -- text glyphs don't render on WasmJS (Skia has no
    // browser emoji-font fallback). Override with any icon set (e.g. this repo's own demo
    // app passes a real Lucide Bars3 here -- see SidebarDoc.kt).
    icon: @Composable () -> Unit = { Icon(Menu, tint = currentTheme.colors.foreground) },
) {
    val state = LocalSidebarState.current
    val resolvedEnabled = enabled && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription = if (state.open) "Collapse sidebar" else "Expand sidebar"
                    if (!resolvedEnabled) disabled()
                }
                .keyboardInputMode()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = state.toggle,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.styleable(styleState, rememberSidebarTriggerStyle(), style),
        ) {
            icon()
        }
    }
}

/**
 * Edge toggle for the nearest [SidebarProvider]. Matches shadcn's `SidebarRail`.
 * Not shown by the default [Sidebar] layout.
 */
@Composable
fun SidebarRail(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val state = LocalSidebarState.current
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription = if (state.open) "Collapse sidebar" else "Expand sidebar"
                }
                .keyboardInputMode()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = state.toggle,
                )
                .styleable(styleState, touchTargetStyle then pressStyle then rememberSidebarTriggerStyle() then style),
        contentAlignment = Alignment.Center,
    ) {}
}

@Composable
fun SidebarHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(modifier = modifier.fillMaxWidth().styleable(styleState, style), content = content)
}

@Composable
fun SidebarFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(modifier = modifier.fillMaxWidth().styleable(styleState, style), content = content)
}

/** A labeled section within the sidebar body, matching real shadcn's `SidebarGroup`/`SidebarGroupLabel`. */
@Composable
fun SidebarGroup(
    modifier: Modifier = Modifier,
    label: String? = null,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.fillMaxWidth().animateContentSize().styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs2),
    ) {
        if (label != null) {
            SidebarGroupLabel(label)
        }
        content()
    }
}

/** Section title inside a [SidebarGroup]. Matches shadcn's `SidebarGroupLabel`. */
@Composable
fun SidebarGroupLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    if (sidebarState().iconCollapsed) return
    Text(
        variant = TextVariant.LabelSmall,
        textStyle = TextColors.Muted,
        modifier = modifier.padding(horizontal = twSpacing.xs, vertical = twSpacing.xs),
    ) { text }
}

@Composable
@JvmName("SidebarGroupLabelLambda")
@OverloadResolutionByLambdaReturnType
fun SidebarGroupLabel(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    SidebarGroupLabel(text = text(), modifier = modifier)
}

/**
 * Trailing action on a [SidebarGroup] header. Matches shadcn's `SidebarGroupAction`.
 * Not shown by the default group layout.
 */
@Composable
fun SidebarGroupAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "More",
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    if (sidebarState().iconCollapsed) return
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .semantics {
                    role = Role.Button
                    contentDescription = label
                }
                .keyboardInputMode()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .styleable(styleState, touchTargetStyle then pressStyle then style),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** Body slot of a [SidebarGroup]. Matches shadcn's `SidebarGroupContent`. */
@Composable
fun ColumnScope.SidebarGroupContent(content: @Composable ColumnScope.() -> Unit) {
    content()
}

/** Scrollable body of a [Sidebar]. Matches shadcn's `SidebarContent`. */
@Composable
fun ColumnScope.SidebarContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .weight(1f)
                .styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
        content = content,
    )
}

/** Divider inside a [Sidebar]. Matches shadcn's `SidebarSeparator`. */
@Composable
fun SidebarSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    Separator(modifier = modifier.fillMaxWidth(), style = style)
}

/** Slot receiver for [SidebarMenuItem] / [SidebarMenuSub] children of a [SidebarMenu]. */
class SidebarMenuScope internal constructor()

/** Slot receiver for [SidebarMenuButton] / [SidebarMenuAction] / [SidebarMenuBadge] / [SidebarMenuSub] inside a [SidebarMenuItem]. */
class SidebarMenuItemScope internal constructor()

@Immutable
internal class SidebarMenuController(
    val activeId: String?,
    val onItemClick: (String) -> Unit,
)

private val LocalSidebarMenu = staticCompositionLocalOf<SidebarMenuController?> { null }

/** Active destination of the nearest selection-convenience [SidebarMenu]. */
class SidebarMenuState(
    val activeId: String?,
    val onItemClick: (String) -> Unit,
)

/** Reads the nearest selection-convenience [SidebarMenu]. */
@Composable
fun sidebarMenuState(): SidebarMenuState {
    val menu =
        checkNotNull(LocalSidebarMenu.current) {
            "sidebarMenuState() must be used inside SidebarMenu(activeId, onItemClick) { … }"
        }
    return SidebarMenuState(activeId = menu.activeId, onItemClick = menu.onItemClick)
}

private class SidebarMenuItemOverlayParentData

private class SidebarMenuItemOverlayModifier : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = SidebarMenuItemOverlayParentData()
}

private fun Modifier.sidebarMenuItemOverlay(): Modifier = this.then(SidebarMenuItemOverlayModifier())

@Composable
private fun SidebarMenuColumn(
    modifier: Modifier,
    style: Style,
    content: @Composable SidebarMenuScope.() -> Unit,
) {
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .widthIn(min = 0.dp)
                .styleable(rootStyleState, style),
    ) {
        SidebarMenuScope().content()
    }
}

/**
 * A vertical list matching real shadcn's `SidebarMenu` (`ul`: `flex w-full min-w-0 flex-col`,
 * nova `gap-0`):
 *
 * ```
 * SidebarMenu
 * └── SidebarMenuItem
 *     ├── SidebarMenuButton
 *     ├── SidebarMenuAction
 *     ├── SidebarMenuBadge
 *     └── SidebarMenuSub
 * ```
 *
 * Selection is per-button via [SidebarMenuButton] `isActive`, not on this list.
 */
@Composable
fun SidebarMenu(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable SidebarMenuScope.() -> Unit,
) {
    SidebarMenuColumn(modifier = modifier, style = style, content = content)
}

/**
 * Convenience [SidebarMenu] that publishes [activeId] / [onItemClick] for
 * [SidebarMenuItem] `(value, label)` rows. Prefer [SidebarMenu] + per-item `isActive`.
 */
@Composable
fun SidebarMenu(
    activeId: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable SidebarMenuScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalSidebarMenu provides SidebarMenuController(activeId = activeId, onItemClick = onItemClick),
    ) {
        SidebarMenuColumn(modifier = modifier, style = style, content = content)
    }
}

/**
 * Relative row container matching shadcn's `SidebarMenuItem` (`li.group/menu-item.relative`).
 * Compose [SidebarMenuButton], [SidebarMenuAction], [SidebarMenuBadge], and [SidebarMenuSub]
 * as siblings; action/badge overlay the trailing edge (`absolute right-1`).
 */
@Composable
fun SidebarMenuScope.SidebarMenuItem(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable SidebarMenuItemScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val overlayEnd = twSpacing.scale1
    Layout(
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        content = { SidebarMenuItemScope().content() },
    ) { measurables, constraints ->
        val overlayMeasurables =
            measurables.filter { it.parentData is SidebarMenuItemOverlayParentData }
        val flowMeasurables =
            measurables.filter { it.parentData !is SidebarMenuItemOverlayParentData }
        val hasOverlay = overlayMeasurables.isNotEmpty()
        val actionReserve = if (hasOverlay) 32.dp.roundToPx() else 0
        val maxW = if (constraints.hasBoundedWidth) constraints.maxWidth else Constraints.Infinity
        val flowPlaceables =
            flowMeasurables.mapIndexed { index, measurable ->
                val childMax =
                    when {
                        maxW == Constraints.Infinity -> Constraints.Infinity
                        index == 0 && hasOverlay -> (maxW - actionReserve).coerceAtLeast(0)
                        else -> maxW
                    }
                measurable.measure(
                    Constraints(
                        minWidth = 0,
                        maxWidth = childMax,
                        minHeight = 0,
                        maxHeight = constraints.maxHeight,
                    ),
                )
            }
        val overlayPlaceables =
            overlayMeasurables.map { measurable ->
                measurable.measure(Constraints())
            }
        var maxFlowW = 0
        flowPlaceables.forEachIndexed { index, placeable ->
            val extra = if (index == 0 && hasOverlay) actionReserve else 0
            maxFlowW = maxOf(maxFlowW, placeable.width + extra)
        }
        val width =
            when {
                constraints.hasBoundedWidth && constraints.minWidth == constraints.maxWidth ->
                    constraints.maxWidth
                constraints.hasBoundedWidth ->
                    maxOf(maxFlowW, constraints.minWidth).coerceAtMost(constraints.maxWidth)
                else -> maxFlowW
            }
        val flowHeight = flowPlaceables.sumOf { it.height }
        val overlayHeight = overlayPlaceables.maxOfOrNull { it.height } ?: 0
        val height = maxOf(flowHeight, overlayHeight)
        layout(width, height) {
            var top = 0
            flowPlaceables.forEach { placeable ->
                placeable.placeRelative(0, top)
                top += placeable.height
            }
            val firstH = flowPlaceables.firstOrNull()?.height ?: 0
            val overlayTop =
                when {
                    firstH <= 28.dp.roundToPx() -> 4.dp.roundToPx()
                    firstH >= 48.dp.roundToPx() -> 10.dp.roundToPx()
                    else -> 6.dp.roundToPx()
                }
            overlayPlaceables.forEach { placeable ->
                placeable.placeRelative(
                    x = (width - placeable.width - overlayEnd.roundToPx()).coerceAtLeast(0),
                    y = overlayTop,
                )
            }
        }
    }
}

/**
 * Convenience row: [value] + [label] wired to the nearest selection-convenience [SidebarMenu],
 * or to [onClick] / [isActive] when that list is omitted. [badge] is a trailing [SidebarMenuBadge].
 */
@Composable
fun SidebarMenuScope.SidebarMenuItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    isActive: Boolean? = null,
    tooltip: String? = null,
    variant: SidebarMenuButtonVariant = SidebarMenuButtonVariant.Default,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    style: Style = Style,
    onClick: (() -> Unit)? = null,
) {
    val menu = LocalSidebarMenu.current
    val resolvedActive = isActive ?: (menu != null && value == menu.activeId)
    val click = onClick
    SidebarMenuItem(modifier = modifier) {
        SidebarMenuButton(
            onClick = {
                if (click != null) {
                    click()
                } else {
                    menu?.onItemClick?.invoke(value)
                }
            },
            active = resolvedActive,
            enabled = enabled,
            disabled = disabled,
            label = label,
            tooltip = tooltip,
            variant = variant,
            size = size,
            style = style,
        ) {
            SidebarMenuButtonLabel(text = label, active = resolvedActive, size = size)
        }
        badge?.let { SidebarMenuBadge(it) }
    }
}

/** String-label convenience matching `<SidebarMenuItem><SidebarMenuButton>`. */
@Composable
fun SidebarMenuScope.SidebarMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    badge: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    tooltip: String? = null,
    variant: SidebarMenuButtonVariant = SidebarMenuButtonVariant.Default,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    style: Style = Style,
) {
    SidebarMenuItem(
        value = text,
        label = text,
        modifier = modifier,
        badge = badge,
        enabled = enabled,
        disabled = disabled,
        isActive = isActive,
        tooltip = tooltip,
        variant = variant,
        size = size,
        style = style,
        onClick = onClick,
    )
}

/**
 * Clickable chrome of a [SidebarMenuItem]. Matches shadcn's `SidebarMenuButton`
 * (`isActive` / `variant` / `size` / `tooltip`). Prefer composing this inside
 * [SidebarMenuItem]; a tooltip is shown only while the rail is icon-collapsed.
 */
@Composable
fun SidebarMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    isActive: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    label: String = "",
    tooltip: String? = null,
    variant: SidebarMenuButtonVariant = SidebarMenuButtonVariant.Default,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val resolvedActive = active || isActive
    val resolvedEnabled = enabled && !disabled
    val state = sidebarState()
    val tooltipText = tooltip ?: label.takeIf { it.isNotEmpty() }
    val showTooltip = state.iconCollapsed && !state.mobile && tooltipText != null
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colors = currentTheme.colors
    val contentColor =
        when {
            resolvedActive || isHovered -> colors.sidebarAccentForeground
            else -> colors.sidebarForeground
        }
    val button: @Composable () -> Unit = {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                modifier =
                    modifier
                        .clipToBounds()
                        .semantics(mergeDescendants = true) {
                            role = Role.Button
                            contentDescription = label
                            selected = resolvedActive
                            if (!resolvedEnabled) disabled()
                        }
                        .keyboardInputMode()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = resolvedEnabled,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .styleable(
                            styleState,
                            touchTargetStyle then
                                pressStyle then
                                rememberSidebarMenuButtonStyle(
                                    resolvedActive,
                                    variant,
                                    size,
                                    state.iconCollapsed,
                                ) then
                                style,
                        ),
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
    if (showTooltip) {
        Tooltip {
            TooltipTrigger { button() }
            TooltipContent(tooltipText)
        }
    } else {
        button()
    }
}

@Composable
private fun RowScope.SidebarMenuButtonLabel(
    text: String,
    active: Boolean,
    size: SidebarMenuButtonSize,
) {
    val variant =
        if (size == SidebarMenuButtonSize.Sm) {
            TextVariant.BodySmall
        } else {
            TextVariant.BodyMedium
        }
    Text(
        modifier = Modifier.weight(1f, fill = false),
        variant = variant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textStyle = if (active) TextStyle(fontWeight = FontWeight.Medium) else TextStyle.Default,
    ) { text }
}

/** String-label overload of [SidebarMenuButton]. */
@Composable
fun SidebarMenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    isActive: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    tooltip: String? = null,
    variant: SidebarMenuButtonVariant = SidebarMenuButtonVariant.Default,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    style: Style = Style,
) {
    SidebarMenuButton(
        onClick = onClick,
        modifier = modifier,
        active = active,
        isActive = isActive,
        enabled = enabled,
        disabled = disabled,
        label = text,
        tooltip = tooltip,
        variant = variant,
        size = size,
        style = style,
    ) {
        SidebarMenuButtonLabel(text = text, active = active || isActive, size = size)
    }
}

@Composable
fun SidebarInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
) {
    Input(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder,
        enabled = enabled,
        disabled = disabled,
    )
}

/** Trailing count/status chip on a [SidebarMenuItem]. Matches shadcn's `SidebarMenuBadge`. */
@Composable
fun SidebarMenuBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    if (sidebarState().iconCollapsed) return
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .sidebarMenuItemOverlay()
                .styleable(styleState, rememberSidebarMenuBadgeStyle()),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            variant = TextVariant.BodySmall,
            maxLines = 1,
            textStyle = TextStyle(fontWeight = FontWeight.Medium),
        ) { text }
    }
}

@Composable
@JvmName("SidebarMenuBadgeLambda")
@OverloadResolutionByLambdaReturnType
fun SidebarMenuBadge(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    SidebarMenuBadge(text = text(), modifier = modifier)
}

/**
 * Trailing action on a [SidebarMenuItem] (e.g. overflow). Matches shadcn's
 * `SidebarMenuAction` (`absolute` overlay; hidden when the rail is icon-collapsed).
 */
@Composable
fun SidebarMenuAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "More",
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    if (sidebarState().iconCollapsed) return
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            modifier
                .sidebarMenuItemOverlay()
                .semantics {
                    role = Role.Button
                    contentDescription = label
                }
                .keyboardInputMode()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberSidebarMenuActionStyle() then style,
                ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Nested destination list under a [SidebarMenuItem]. Matches shadcn's `SidebarMenuSub`
 * (`border-l`, `mx-3.5`, `px-2.5`, `gap-1`). Hidden when the rail is icon-collapsed.
 */
@Composable
fun SidebarMenuSub(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable SidebarMenuScope.() -> Unit,
) {
    if (sidebarState().iconCollapsed) return
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val borderColor = currentTheme.colors.sidebarBorder
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = twSpacing.scale3p5,
                    end = twSpacing.scale3p5,
                    top = twSpacing.scale0p5,
                    bottom = twSpacing.scale0p5,
                )
                .drawBehind {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                .padding(horizontal = twSpacing.scale2p5)
                .styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.scale1),
    ) {
        SidebarMenuScope().content()
    }
}

/** One row inside [SidebarMenuSub]. Matches shadcn's `SidebarMenuSubItem` (`li.relative`). */
@Composable
fun SidebarMenuScope.SidebarMenuSubItem(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable SidebarMenuItemScope.() -> Unit,
) {
    SidebarMenuItem(modifier = modifier, style = style, content = content)
}

@Composable
fun SidebarMenuScope.SidebarMenuSubItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    isActive: Boolean? = null,
    tooltip: String? = null,
    variant: SidebarMenuButtonVariant = SidebarMenuButtonVariant.Default,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    style: Style = Style,
    onClick: (() -> Unit)? = null,
) {
    SidebarMenuItem(
        value = value,
        label = label,
        modifier = modifier,
        badge = badge,
        enabled = enabled,
        disabled = disabled,
        isActive = isActive,
        tooltip = tooltip,
        variant = variant,
        size = size,
        style = style,
        onClick = onClick,
    )
}

/**
 * Loading placeholder for a [SidebarMenuItem]. Matches shadcn's `SidebarMenuSkeleton`.
 * Optional — not shown by the default [Sidebar] layout. Width is a fixed 70% (web uses
 * a random 50–90%, which would flake screenshot tests).
 */
@Composable
fun SidebarMenuSkeleton(
    modifier: Modifier = Modifier,
    showIcon: Boolean = false,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(horizontal = twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        if (showIcon) {
            Skeleton(modifier = Modifier.size(16.dp))
        }
        Skeleton(
            modifier =
                Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp),
        )
    }
}

/** Clickable chrome of a [SidebarMenuSubItem]. Matches shadcn's `SidebarMenuSubButton` (`isActive` / `size` sm|md / `disabled`). */
@Composable
fun SidebarMenuSubButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    isActive: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    label: String = "",
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    if (sidebarState().iconCollapsed) return
    val resolvedActive = active || isActive
    val resolvedEnabled = enabled && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colors = currentTheme.colors
    val contentColor =
        when {
            resolvedActive || isHovered -> colors.sidebarAccentForeground
            else -> colors.sidebarForeground
        }
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
            modifier =
                modifier
                    .clipToBounds()
                    .semantics(mergeDescendants = true) {
                        role = Role.Button
                        contentDescription = label
                        selected = resolvedActive
                        if (!resolvedEnabled) disabled()
                    }
                    .keyboardInputMode()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = resolvedEnabled,
                        role = Role.Button,
                        onClick = onClick,
                    )
                    .styleable(
                        styleState,
                        touchTargetStyle then
                            pressStyle then
                            rememberSidebarMenuSubButtonStyle(resolvedActive, size) then
                            style,
                    ),
            horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/** String-label overload of [SidebarMenuSubButton]. */
@Composable
fun SidebarMenuSubButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    isActive: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    size: SidebarMenuButtonSize = SidebarMenuButtonSize.Default,
    style: Style = Style,
) {
    SidebarMenuSubButton(
        onClick = onClick,
        modifier = modifier,
        active = active,
        isActive = isActive,
        enabled = enabled,
        disabled = disabled,
        label = text,
        size = size,
        style = style,
    ) {
        SidebarMenuButtonLabel(text = text, active = active || isActive, size = size)
    }
}
