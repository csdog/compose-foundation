@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlinx.coroutines.delay
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

@Immutable
internal class HoverCardController(
    val open: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val onContentHoverChange: (Boolean) -> Unit,
    val onTriggerDisabledChange: (Boolean) -> Unit,
    val onTriggerHoverChange: (Boolean) -> Unit,
    val onTriggerFocusChange: (Boolean) -> Unit,
    val onTriggerDelayChange: (Int?, Int?) -> Unit,
)

private val LocalHoverCard = staticCompositionLocalOf<HoverCardController?> { null }

private val LocalHoverCardDelayMillis = staticCompositionLocalOf { 600 }

private val LocalHoverCardCloseDelayMillis = staticCompositionLocalOf { 300 }

/**
 * Provides default timing for nested cards. Base UI 1.6.0 defaults to opening
 * after 600ms and closing after 300ms. Trigger delay / closeDelay override these
 * defaults, followed by the root aliases. Official demos explicitly use 10/100ms.
 */
@Composable
fun HoverCardProvider(
    delayMillis: Int = 600,
    closeDelayMillis: Int = 300,
    openDelay: Int? = null,
    closeDelay: Int? = null,
    content: @Composable () -> Unit,
) {
    val resolvedOpen = openDelay ?: delayMillis
    val resolvedClose = closeDelay ?: closeDelayMillis
    CompositionLocalProvider(
        LocalHoverCardDelayMillis provides resolvedOpen,
        LocalHoverCardCloseDelayMillis provides resolvedClose,
    ) {
        content()
    }
}

@Composable
private fun hoverCardController(): HoverCardController =
    checkNotNull(LocalHoverCard.current) {
        "HoverCardTrigger / HoverCardContent must be used inside HoverCard { … }"
    }

/** Reads the nearest [HoverCard] scope. Matches Radix `useHoverCardContext`. */
@Composable
fun hoverCardState(): HoverCardScope = HoverCardScope(hoverCardController())

/** Slot receiver for [HoverCardTrigger] / [HoverCardContent]. */
class HoverCardScope internal constructor(
    private val controller: HoverCardController,
) {
    val open: Boolean get() = controller.open
}

/**
 * A hover-triggered panel for richer preview content (unlike Tooltip, which is
 * text-only). Matches real shadcn/ui's `hover-card.tsx`:
 *
 * ```
 * HoverCard
 * ├── HoverCardTrigger
 * └── HoverCardContent
 * ```
 *
 * Stays open while the pointer is over the trigger **or** the portaled content
 * (Radix `closeDelay`), so the preview is reachable — unlike [com.toutiaoyan.compose.ui.components.tooltip.Tooltip].
 *
 * Panel chrome is Style-driven — override with [HoverCardContent]'s `style`
 * (`w-64 rounded-md border bg-popover p-4 shadow-md`).
 *
 * Usage:
 * ```
 * HoverCard {
 *     HoverCardTrigger("@shadcn")
 *     HoverCardContent("The React Framework – created and maintained by @vercel.")
 * }
 * HoverCard(open = open, onOpenChange = { open = it }) { … }
 * ```
 */
@Composable
fun HoverCard(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    disabled: Boolean = false,
    delayMillis: Int? = null,
    closeDelayMillis: Int? = null,
    openDelay: Int? = null,
    closeDelay: Int? = null,
    disableHoverableContent: Boolean = false,
    content: @Composable HoverCardScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    var triggerHovered by remember { mutableStateOf(false) }
    var triggerFocused by remember { mutableStateOf(false) }
    var contentHovered by remember { mutableStateOf(false) }
    var triggerDisabled by remember { mutableStateOf(false) }
    var triggerOpenDelay by remember { mutableStateOf<Int?>(null) }
    var triggerCloseDelay by remember { mutableStateOf<Int?>(null) }
    val keyboard = LocalInputModeManager.current.inputMode == InputMode.Keyboard
    val isActive =
        !disabled && !triggerDisabled &&
            (triggerHovered || (triggerFocused && keyboard) || (contentHovered && !disableHoverableContent))
    val resolvedDelay = triggerOpenDelay ?: openDelay ?: delayMillis ?: LocalHoverCardDelayMillis.current
    val resolvedClose = triggerCloseDelay ?: closeDelay ?: closeDelayMillis ?: LocalHoverCardCloseDelayMillis.current
    val latestSetOpen by rememberUpdatedState(setOpen)
    val latestOpen by rememberUpdatedState(isOpen)
    var hasBeenActive by remember { mutableStateOf(false) }
    LaunchedEffect(isActive, resolvedDelay, resolvedClose) {
        if (isActive) {
            hasBeenActive = true
            if (resolvedDelay > 0) delay(resolvedDelay.toLong())
            if (!latestOpen) latestSetOpen(true)
        } else if (hasBeenActive) {
            if (resolvedClose > 0) delay(resolvedClose.toLong())
            if (latestOpen) latestSetOpen(false)
        }
    }
    val controller =
        HoverCardController(
            open = isOpen,
            onOpenChange = setOpen,
            onContentHoverChange = { contentHovered = it },
            onTriggerDisabledChange = { triggerDisabled = it },
            onTriggerHoverChange = { triggerHovered = it },
            onTriggerFocusChange = { triggerFocused = it },
            onTriggerDelayChange = { opening, closing ->
                triggerOpenDelay = opening
                triggerCloseDelay = closing
            },
        )
    CompositionLocalProvider(LocalHoverCard provides controller) {
        Box(
            modifier =
                modifier.onPreviewKeyEvent {
                    if (it.key == Key.Escape && it.type == KeyEventType.KeyDown && isOpen) {
                        setOpen(false)
                        true
                    } else {
                        false
                    }
                },
        ) {
            HoverCardScope(controller).content()
        }
    }
}

/** Anchor for the parent [HoverCard]. Matches real shadcn's `HoverCardTrigger`. */
@Composable
fun HoverCardScope.HoverCardTrigger(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    delay: Int? = null,
    closeDelay: Int? = null,
    content: @Composable () -> Unit,
) {
    val controller = hoverCardController()
    val interactionSource = remember { MutableInteractionSource() }
    val hovered = interactionSource.collectIsHoveredAsState().value
    SideEffect {
        controller.onTriggerDisabledChange(disabled)
        controller.onTriggerDelayChange(delay, closeDelay)
        controller.onTriggerHoverChange(hovered)
    }
    DisposableEffect(Unit) {
        onDispose {
            controller.onTriggerDisabledChange(false)
            controller.onTriggerDelayChange(null, null)
            controller.onTriggerHoverChange(false)
            controller.onTriggerFocusChange(false)
        }
    }
    Box(
        modifier =
            modifier
                .hoverable(interactionSource, enabled = !disabled)
                .onFocusChanged { controller.onTriggerFocusChange(it.hasFocus) }
                .semantics { stateDescription = if (controller.open) "Expanded" else "Collapsed" },
    ) { content() }
}

/** String-label trigger. For keyboard focus, use a focusable control in the content slot. */
@Composable
fun HoverCardScope.HoverCardTrigger(
    text: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    delay: Int? = null,
    closeDelay: Int? = null,
) {
    HoverCardTrigger(modifier, disabled, delay, closeDelay) { Text { text } }
}

/**
 * The preview panel of a [HoverCard]. [placement] is Base/Radix `side`.
 * Base Content defaults: `sideOffset` 4, `align` center, `alignOffset` 4.
 * A focusable popup on a hover-triggered overlay flickers open/closed, so this
 * stays `focusable = false` (see [AnchoredPopup]). [dismissOnClickOutside] is
 * pointer-outside dismiss (default true, as in Base UI).
 */
@Composable
fun HoverCardScope.HoverCardContent(
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    side: PopupPlacement? = null,
    align: PopupAlign = PopupAlign.Center,
    style: Style = Style,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 4.dp,
    dismissOnClickOutside: Boolean = true,
    enter: EnterTransition = rememberHoverCardEnter(side ?: placement),
    exit: ExitTransition = rememberHoverCardExit(side ?: placement),
    content: @Composable () -> Unit,
) {
    val controller = hoverCardController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val hoverInteraction = remember { MutableInteractionSource() }
    val contentHovered by hoverInteraction.collectIsHoveredAsState()
    LaunchedEffect(contentHovered) {
        controller.onContentHoverChange(contentHovered)
    }
    DisposableEffect(Unit) {
        onDispose { controller.onContentHoverChange(false) }
    }
    HoverCardPortal {
        AnchoredPopup(
            open = controller.open,
            onDismissRequest = {
                if (dismissOnClickOutside) controller.onOpenChange(false)
            },
            placement = side ?: placement,
            offset = sideOffset,
            align = align,
            alignOffset = alignOffset,
            dismissOnClickOutside = dismissOnClickOutside,
            focusable = false,
            enter = enter,
            exit = exit,
        ) {
            Box(
                modifier =
                    modifier
                        .hoverable(hoverInteraction)
                        .cssStyleable(
                            styleState,
                            rememberHoverCardContentStyle(),
                            style,
                        ),
            ) {
                val nova = currentTheme.activePreset == StylePreset.Nova
                CompositionLocalProvider(
                    LocalContentColor provides currentTheme.colors.popoverForeground,
                    LocalDefaultTextStyle provides
                        if (nova) {
                            TextStyle(
                                fontSize = 14.sp, lineHeight = 20.sp,
                                lineHeightStyle =
                                    LineHeightStyle(
                                        LineHeightStyle.Alignment.Center,
                                        LineHeightStyle.Trim.None,
                                    ),
                            )
                        } else {
                            LocalDefaultTextStyle.current
                        },
                ) { content() }
            }
        }
    }
}

/** Base UI physical sides plus direction-aware inline sides. */
enum class HoverCardSide { Left, Top, Bottom, Right, InlineStart, InlineEnd }

internal fun HoverCardSide.placement(direction: LayoutDirection): PopupPlacement =
    when (this) {
        HoverCardSide.Left -> PopupPlacement.Start
        HoverCardSide.Right -> PopupPlacement.End
        HoverCardSide.Top -> PopupPlacement.Top
        HoverCardSide.Bottom -> PopupPlacement.Bottom
        HoverCardSide.InlineStart -> if (direction == LayoutDirection.Rtl) PopupPlacement.End else PopupPlacement.Start
        HoverCardSide.InlineEnd -> if (direction == LayoutDirection.Rtl) PopupPlacement.Start else PopupPlacement.End
    }

/** Direction-aware side API. The PopupPlacement overload remains available for existing callers. */
@Composable
fun HoverCardScope.HoverCardContent(
    side: HoverCardSide,
    modifier: Modifier = Modifier,
    align: PopupAlign = PopupAlign.Center,
    style: Style = Style,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 4.dp,
    dismissOnClickOutside: Boolean = true,
    enter: EnterTransition = rememberHoverCardEnter(side.placement(LocalLayoutDirection.current)),
    exit: ExitTransition = rememberHoverCardExit(side.placement(LocalLayoutDirection.current)),
    content: @Composable () -> Unit,
) {
    HoverCardContent(
        modifier = modifier,
        side = side.placement(LocalLayoutDirection.current),
        align = align,
        style = style,
        sideOffset = sideOffset,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        enter = enter,
        exit = exit,
        content = content,
    )
}

/** String-label overload of [HoverCardContent]. */
@Composable
fun HoverCardScope.HoverCardContent(
    text: String,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    side: PopupPlacement? = null,
    align: PopupAlign = PopupAlign.Center,
    style: Style = Style,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 4.dp,
    dismissOnClickOutside: Boolean = true,
    enter: EnterTransition = rememberHoverCardEnter(side ?: placement),
    exit: ExitTransition = rememberHoverCardExit(side ?: placement),
) {
    HoverCardContent(
        modifier = modifier,
        placement = placement,
        side = side,
        align = align,
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
 * Convenience: [title] + optional [description] as [HoverCardHeader], then [content].
 * Distinguished from the string-body overload by the trailing [content] lambda.
 */
@Composable
fun HoverCardScope.HoverCardContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Bottom,
    side: PopupPlacement? = null,
    align: PopupAlign = PopupAlign.Center,
    style: Style = Style,
    sideOffset: Dp = 4.dp,
    alignOffset: Dp = 4.dp,
    dismissOnClickOutside: Boolean = true,
    enter: EnterTransition = rememberHoverCardEnter(side ?: placement),
    exit: ExitTransition = rememberHoverCardExit(side ?: placement),
    content: @Composable () -> Unit,
) {
    HoverCardContent(
        modifier = modifier,
        placement = placement,
        side = side,
        align = align,
        style = style,
        sideOffset = sideOffset,
        alignOffset = alignOffset,
        dismissOnClickOutside = dismissOnClickOutside,
        enter = enter,
        exit = exit,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
            HoverCardHeader(title, description = description)
            content()
        }
    }
}

/** Teleport host for [HoverCardContent]. Base UI does not export HoverCardPortal. */
@Composable
internal fun HoverCardPortal(content: @Composable () -> Unit) {
    content()
}

/** Structural header slot. Base HoverCard does not export Header — Compose helper. */
@Composable
fun HoverCardHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs),
        content = content,
    )
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun HoverCardHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    HoverCardHeader(modifier = modifier, style = style) {
        HoverCardTitle(title)
        if (description != null) {
            HoverCardDescription(description)
        }
    }
}

/** String overload of [HoverCardTitle]. */
@Composable
fun HoverCardTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    HoverCardTitle(modifier = modifier, style = style) { text }
}

@Composable
@JvmName("HoverCardTitleString")
@OverloadResolutionByLambdaReturnType
fun HoverCardTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier.semantics { heading() },
        variant = TextVariant.TitleSmall,
        style = style,
        text = text,
    )
}

/** String overload of [HoverCardDescription]. */
@Composable
fun HoverCardDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    HoverCardDescription(modifier = modifier, style = style) { text }
}

@Composable
@JvmName("HoverCardDescriptionString")
@OverloadResolutionByLambdaReturnType
fun HoverCardDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle = TextColors.Muted,
        style = style,
        text = text,
    )
}

private fun hoverCardOrigin(side: PopupPlacement): TransformOrigin =
    when (side) {
        PopupPlacement.Bottom -> TransformOrigin(0.5f, 0f)
        PopupPlacement.Top -> TransformOrigin(0.5f, 1f)
        PopupPlacement.Start -> TransformOrigin(1f, 0.5f)
        PopupPlacement.End -> TransformOrigin(0f, 0.5f)
    }

@Composable
private fun rememberHoverCardEnter(side: PopupPlacement): EnterTransition {
    if (currentTheme.activePreset != StylePreset.Nova) return PopupTransitions.Enter
    val distance = with(LocalDensity.current) { 8.dp.roundToPx() }
    return remember(side, distance) {
        val offset =
            when (side) {
                PopupPlacement.Bottom -> IntOffset(0, -distance)
                PopupPlacement.Top -> IntOffset(0, distance)
                PopupPlacement.Start -> IntOffset(distance, 0)
                PopupPlacement.End -> IntOffset(-distance, 0)
            }
        fadeIn(tween(100)) + scaleIn(tween(100), initialScale = 0.95f, transformOrigin = hoverCardOrigin(side)) +
            slideIn(tween(100)) { offset }
    }
}

@Composable
private fun rememberHoverCardExit(side: PopupPlacement): ExitTransition {
    if (currentTheme.activePreset != StylePreset.Nova) return PopupTransitions.Exit
    return remember(side) {
        fadeOut(tween(100)) + scaleOut(tween(100), targetScale = 0.95f, transformOrigin = hoverCardOrigin(side))
    }
}
