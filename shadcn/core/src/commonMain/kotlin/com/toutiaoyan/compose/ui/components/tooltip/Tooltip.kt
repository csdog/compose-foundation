@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tooltip

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.foundation.layout.widthIn
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.TimeSource
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionResult
import com.toutiaoyan.compose.ui.components.overlay.PositionedAnchoredPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.delay

@Immutable
internal class TooltipController(
    val open: Boolean,
    val onTriggerFocusChange: (Boolean) -> Unit,
    val onContentHoverChange: (Boolean) -> Unit,
    val onTriggerDisabledChange: (Boolean) -> Unit,
    val disableHoverableContent: Boolean? = null,
    val onDismissRequest: () -> Unit,
)

internal val LocalInsideTooltipContent = staticCompositionLocalOf { false }

private val LocalTooltip = staticCompositionLocalOf<TooltipController?> { null }

private const val TOOLTIP_DEFAULT_HOVER_DELAY_MILLIS = 600
private const val TOOLTIP_ARROW_ROTATION_DEGREES = 45f

internal class TooltipProviderController(
    val delayMillis: Int,
    val skipDelayMillis: Int,
    val disableHoverableContent: Boolean,
) {
    var lastCloseMark: TimeSource.Monotonic.ValueTimeMark? = null
    var activeTooltip by mutableStateOf<Any?>(null)
    var closeActiveTooltip: (() -> Unit)? = null
}

private val LocalTooltipProvider = staticCompositionLocalOf<TooltipProviderController?> { null }

object TooltipDefaults {
    /** Base UI / base-nova Content always paints `Arrow`. */
    const val ShowArrow = true

    /** Base UI Content `sideOffset` default (`4`). */
    val SideOffset = 4.dp

    /** Official `size-2.5`. */
    val ArrowSize = 10.dp

    /** Official `rounded-[2px]`. */
    val ArrowRadius = 2.dp

    /** Half the diamond, so it sits on the chip edge like `translate-y-[calc(-50%_-_2px)]`. */
    val ArrowOverlap = 5.dp
}

/**
 * Shared hover timing for nested [Tooltip]s. The Kotlin delayDuration and
 * skipDelayDuration aliases map to Base UI delay and timeout respectively.
 * Provider hover delay defaults to zero, matching shadcn. [skipDelayMillis] defaults to Base UI `400` (skip the open delay when
 * moving between tooltips). [disableHoverableContent] defaults `false`. Focus opens immediately; provider
 * delay applies only to hover. Safe-polygon pointer transfer remains pending.
 */
@Composable
fun TooltipProvider(
    delayMillis: Int = 0,
    skipDelayMillis: Int = 400,
    disableHoverableContent: Boolean = false,
    delayDuration: Int? = null,
    skipDelayDuration: Int? = null,
    content: @Composable () -> Unit,
) {
    val resolvedDelay = delayDuration ?: delayMillis
    val resolvedSkip = skipDelayDuration ?: skipDelayMillis
    val provider =
        remember(resolvedDelay, resolvedSkip, disableHoverableContent) {
            TooltipProviderController(resolvedDelay, resolvedSkip, disableHoverableContent)
        }
    CompositionLocalProvider(
        LocalTooltipProvider provides provider,
    ) {
        content()
    }
}

@Composable
private fun tooltipController(): TooltipController =
    checkNotNull(LocalTooltip.current) {
        "TooltipTrigger / TooltipContent must be used inside Tooltip { … }"
    }

/** Reads the nearest [Tooltip] scope. Matches Radix `useTooltipContext`. */
@Composable
fun tooltipState(): TooltipScope = TooltipScope(tooltipController())

/** Slot receiver for [TooltipTrigger] / [TooltipContent]. */
class TooltipScope internal constructor(
    private val controller: TooltipController,
) {
    val open: Boolean get() = controller.open
}

/**
 * A small hover- or focus-triggered label. Matches real shadcn/ui's `tooltip.tsx`:
 *
 * ```
 * Tooltip
 * ├── TooltipTrigger
 * └── TooltipContent
 * ```
 *
 * Inverse `bg-foreground`/`text-background` chip. Base UI Content always
 * paints `Arrow` (`size-2.5 rotate-45`); [TooltipContent] `showArrow`
 * defaults to [TooltipDefaults.ShowArrow]. Base `sideOffset` is `4`
 * ([TooltipDefaults.SideOffset]). Portal / Arrow are not public exports
 * (composed inside Content).
 *
 * Real shadcn's [TooltipProvider] defaults `delayDuration` to 0 (instant); this
 * uses that when a parent [TooltipProvider] is installed; without one, hover uses Base UI’s 600ms default.
 *
 * Usage:
 * ```
 * Tooltip {
 *     TooltipTrigger("Hover me")
 *     TooltipContent("Add to library")
 * }
 * ```
 */
@Composable
fun Tooltip(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    delayMillis: Int? = null,
    delayDuration: Int? = null,
    skipDelayMillis: Int? = null,
    skipDelayDuration: Int? = null,
    disableHoverableContent: Boolean? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable TooltipScope.() -> Unit,
) {
    val defaultHoverSource = remember { MutableInteractionSource() }
    val hoverSource = interactionSource ?: defaultHoverSource
    val isHovered by hoverSource.collectIsHoveredAsState()
    var triggerFocused by remember { mutableStateOf(false) }
    var contentHovered by remember { mutableStateOf(false) }
    var triggerDisabled by remember { mutableStateOf(false) }
    var dismissed by remember { mutableStateOf(false) }
    var internalOpen by remember { mutableStateOf(false) }
    var wasInteracting by remember { mutableStateOf(false) }
    var lastCloseMark by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }
    val provider = LocalTooltipProvider.current
    val token = remember { Any() }
    val resolvedOpen = open ?: internalOpen
    val latestOpen by rememberUpdatedState(resolvedOpen)
    val latestOnOpenChange by rememberUpdatedState(onOpenChange)
    val resolvedDelay =
        (delayDuration ?: delayMillis ?: provider?.delayMillis ?: TOOLTIP_DEFAULT_HOVER_DELAY_MILLIS).coerceAtLeast(0)
    val resolvedSkip = (skipDelayDuration ?: skipDelayMillis ?: provider?.skipDelayMillis ?: 0).coerceAtLeast(0)
    val hasInteraction = isHovered || triggerFocused || contentHovered
    val isActive = !disabled && !triggerDisabled && hasInteraction && !dismissed

    fun requestOpen(value: Boolean) {
        if (latestOpen != value) {
            internalOpen = value
            latestOnOpenChange?.invoke(value)
        }
    }

    fun dismiss() {
        dismissed = true
        requestOpen(false)
    }
    LaunchedEffect(hasInteraction) {
        if (!hasInteraction) dismissed = false
    }
    LaunchedEffect(isActive, triggerFocused, resolvedDelay, resolvedSkip, provider) {
        val previousInteraction = wasInteracting
        wasInteracting = isActive
        if (isActive) {
            val recentClose =
                (provider?.lastCloseMark ?: lastCloseMark)
                    ?.elapsedNow()?.inWholeMilliseconds?.let { it < resolvedSkip } == true
            val instant = triggerFocused || provider?.activeTooltip != null || (resolvedSkip > 0 && recentClose)
            if (!instant) delay(resolvedDelay.toLong())
            requestOpen(true)
        } else if (previousInteraction && !dismissed) {
            requestOpen(false)
        }
    }
    DisposableEffect(resolvedOpen, provider) {
        if (resolvedOpen && provider != null) {
            val closePrevious = if (provider.activeTooltip !== token) provider.closeActiveTooltip else null
            provider.activeTooltip = token
            provider.closeActiveTooltip = ::dismiss
            closePrevious?.invoke()
        }
        onDispose {
            if (resolvedOpen) {
                val mark = TimeSource.Monotonic.markNow()
                lastCloseMark = mark
                provider?.lastCloseMark = mark
                if (provider?.activeTooltip === token) {
                    provider.activeTooltip = null
                    provider.closeActiveTooltip = null
                }
            }
        }
    }
    val controller =
        TooltipController(
            open = resolvedOpen,
            onTriggerFocusChange = { triggerFocused = it },
            onContentHoverChange = { contentHovered = it },
            onTriggerDisabledChange = { triggerDisabled = it },
            disableHoverableContent = disableHoverableContent,
            onDismissRequest = ::dismiss,
        )
    CompositionLocalProvider(LocalTooltip provides controller) {
        Box(
            modifier =
                modifier.hoverable(hoverSource).onPreviewKeyEvent { event ->
                    if (resolvedOpen && event.key == Key.Escape && event.type == KeyEventType.KeyDown) {
                        dismiss()
                        true
                    } else {
                        false
                    }
                },
        ) {
            TooltipScope(controller).content()
        }
    }
}

/** Anchor for the parent [Tooltip]. Matches real shadcn's `TooltipTrigger`. */
@Composable
fun TooltipScope.TooltipTrigger(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val controller = tooltipController()
    DisposableEffect(disabled) {
        controller.onTriggerDisabledChange(disabled)
        onDispose { controller.onTriggerDisabledChange(false) }
    }
    Box(
        modifier =
            modifier.onFocusChanged { state ->
                if (!disabled) controller.onTriggerFocusChange(state.hasFocus)
            },
    ) {
        content()
    }
}

/** String-label overload of [TooltipTrigger], rendering a [Button]. */
@Composable
fun TooltipScope.TooltipTrigger(
    text: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
) {
    TooltipTrigger(modifier = modifier, disabled = disabled) {
        Button(text, variant = variant, size = size, disabled = disabled)
    }
}

/**
 * The label chip of a [Tooltip]. [placement] is Radix `side` (default Top).
 * A focusable popup fights the trigger's hoverable() and flickers — this stays
 * `focusable = false`. [showArrow] defaults to the official Radix diamond.
 */
@Composable
fun TooltipScope.TooltipContent(
    text: String,
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Top,
    side: PopupPlacement? = null,
    style: Style = Style,
    sideOffset: Dp = TooltipDefaults.SideOffset,
    align: PopupAlign = PopupAlign.Center,
    alignOffset: Dp = 0.dp,
    showArrow: Boolean = TooltipDefaults.ShowArrow,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
) {
    TooltipContent(
        modifier = modifier,
        placement = placement,
        side = side,
        style = style,
        sideOffset = sideOffset,
        align = align,
        alignOffset = alignOffset,
        showArrow = showArrow,
        enter = enter,
        exit = exit,
    ) {
        Text(
            variant = TextVariant.LabelSmall,
            textStyle = tooltipTextStyle(),
        ) { text }
    }
}

/** Slot form of [TooltipContent], matching shadcn children. */
@Composable
fun TooltipScope.TooltipContent(
    modifier: Modifier = Modifier,
    placement: PopupPlacement = PopupPlacement.Top,
    side: PopupPlacement? = null,
    style: Style = Style,
    sideOffset: Dp = TooltipDefaults.SideOffset,
    align: PopupAlign = PopupAlign.Center,
    alignOffset: Dp = 0.dp,
    showArrow: Boolean = TooltipDefaults.ShowArrow,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
    content: @Composable () -> Unit,
) {
    val controller = tooltipController()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val hoverableContent =
        when (val override = controller.disableHoverableContent) {
            null -> LocalTooltipProvider.current?.disableHoverableContent != true
            else -> !override
        }
    val contentHoverSource = remember { MutableInteractionSource() }
    val contentHovered by contentHoverSource.collectIsHoveredAsState()
    val onContentHoverChange by rememberUpdatedState(controller.onContentHoverChange)
    LaunchedEffect(hoverableContent, contentHovered) {
        onContentHoverChange(hoverableContent && contentHovered)
    }
    DisposableEffect(Unit) {
        onDispose { onContentHoverChange(false) }
    }
    var physicalSide by remember { mutableStateOf<PopupPlacement?>(null) }
    val updateSide: (PopupPositionResult) -> Unit = remember { { physicalSide = it.physicalSide } }
    PositionedAnchoredPopup(
        open = controller.open,
        onDismissRequest = controller.onDismissRequest,
        placement = side ?: placement,
        offset = sideOffset,
        align = align,
        alignOffset = alignOffset,
        dismissOnClickOutside = false,
        focusable = false,
        enter = enter,
        exit = exit,
        onPositioned = if (showArrow) updateSide else null,
    ) {
        val chipModifier =
            modifier
                .widthIn(max = 320.dp)
                .then(if (hoverableContent) Modifier.hoverable(contentHoverSource) else Modifier)
                .styleable(
                    styleState,
                    rememberTooltipContentStyle() then style,
                )
        TooltipBubble(
            physicalSide = physicalSide.takeIf { showArrow },
            chipModifier = chipModifier,
            content = {
                CompositionLocalProvider(
                    LocalInsideTooltipContent provides true,
                    LocalDefaultTextStyle provides tooltipTextStyle(),
                ) { content() }
            },
        )
    }
}

@Composable
private fun TooltipBubble(
    physicalSide: PopupPlacement?,
    chipModifier: Modifier,
    content: @Composable () -> Unit,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box {
        // Arrow overlays the chip; it must not alter popup size or the side offset.
        if (physicalSide != null) {
            val alignment =
                when (physicalSide) {
                    PopupPlacement.Top -> Alignment.BottomCenter
                    PopupPlacement.Bottom -> Alignment.TopCenter
                    PopupPlacement.Start -> if (rtl) Alignment.CenterStart else Alignment.CenterEnd
                    PopupPlacement.End -> if (rtl) Alignment.CenterEnd else Alignment.CenterStart
                }
            val arrowOffset =
                when (physicalSide) {
                    PopupPlacement.Top -> Modifier.offset(y = 3.dp)
                    PopupPlacement.Bottom -> Modifier.offset(y = (-3).dp)
                    PopupPlacement.Start -> Modifier.offset(x = if (rtl) (-4).dp else 4.dp)
                    PopupPlacement.End -> Modifier.offset(x = if (rtl) 4.dp else (-4).dp)
                }
            TooltipArrow(Modifier.align(alignment).then(arrowOffset))
        }
        Box(modifier = chipModifier) { content() }
    }
}

/**
 * Base UI tooltip arrow (not exported; always composed inside Content):
 * `size-2.5 rotate-45 rounded-[2px] bg-foreground`.
 */
@Composable
internal fun TooltipArrow(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(TooltipDefaults.ArrowSize)
                .graphicsLayer { rotationZ = TOOLTIP_ARROW_ROTATION_DEGREES }
                .clip(RoundedCornerShape(TooltipDefaults.ArrowRadius))
                .background(currentTheme.colors.foreground),
    )
}

@Composable
private fun tooltipTextStyle() =
    TextStyle(
        color = currentTheme.colors.background,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )
