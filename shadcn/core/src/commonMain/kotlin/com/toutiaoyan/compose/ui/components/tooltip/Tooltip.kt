@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tooltip

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.TimeSource
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
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
)

private val LocalTooltip = staticCompositionLocalOf<TooltipController?> { null }

private val LocalTooltipDelayMillis = staticCompositionLocalOf { 0 }

internal class TooltipProviderController(
    val delayMillis: Int,
    val skipDelayMillis: Int,
    val disableHoverableContent: Boolean,
) {
    var lastCloseMark: TimeSource.Monotonic.ValueTimeMark? = null
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
 * Provides hover delay for nested [Tooltip]s. Matches real shadcn's
 * `TooltipProvider` (`delayDuration` / `skipDelayDuration` /
 * `disableHoverableContent`); default `delayDuration` `0` is instant, same as
 * shadcn. [skipDelayMillis] defaults to Radix `300` (skip the open delay when
 * moving between tooltips). [disableHoverableContent] defaults `true` so the
 * portaled panel does not steal hover — Compose popups sit outside the trigger.
 */
@Composable
fun TooltipProvider(
    delayMillis: Int = 0,
    skipDelayMillis: Int = 300,
    disableHoverableContent: Boolean = true,
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
        LocalTooltipDelayMillis provides resolvedDelay,
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
 * matches that unless a parent [TooltipProvider] sets a delay.
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
    content: @Composable TooltipScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var triggerFocused by remember { mutableStateOf(false) }
    var contentHovered by remember { mutableStateOf(false) }
    var triggerDisabled by remember { mutableStateOf(false) }
    var lastCloseMark by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }
    val isActive = !disabled && !triggerDisabled && (isHovered || triggerFocused || contentHovered)
    val resolvedDelay = delayDuration ?: delayMillis ?: LocalTooltipDelayMillis.current
    val provider = LocalTooltipProvider.current
    val resolvedSkip = skipDelayDuration ?: skipDelayMillis ?: provider?.skipDelayMillis ?: 0
    val skipDelay =
        resolvedSkip > 0 &&
            (provider?.lastCloseMark ?: lastCloseMark)?.elapsedNow()?.inWholeMilliseconds?.let { it < resolvedSkip } == true
    val effectiveDelay = if (skipDelay) 0 else resolvedDelay
    var delayedOpen by remember { mutableStateOf(false) }
    if (effectiveDelay > 0) {
        LaunchedEffect(isActive, effectiveDelay) {
            if (isActive) {
                delay(effectiveDelay.toLong())
                delayedOpen = true
            } else {
                delayedOpen = false
            }
        }
    }
    LaunchedEffect(isActive) {
        if (!isActive) {
            val mark = TimeSource.Monotonic.markNow()
            lastCloseMark = mark
            provider?.lastCloseMark = mark
        }
    }
    val hoverOpen = if (effectiveDelay > 0) delayedOpen else isActive
    val resolvedOpen = open ?: hoverOpen
    LaunchedEffect(hoverOpen) {
        onOpenChange?.invoke(hoverOpen)
    }
    val controller =
        TooltipController(
            open = resolvedOpen,
            onTriggerFocusChange = { triggerFocused = it },
            onContentHoverChange = { contentHovered = it },
            onTriggerDisabledChange = { triggerDisabled = it },
            disableHoverableContent = disableHoverableContent,
        )
    CompositionLocalProvider(LocalTooltip provides controller) {
        Box(modifier = modifier.hoverable(interactionSource)) {
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
            textStyle = TextStyle(color = currentTheme.colors.background),
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
    LaunchedEffect(hoverableContent, contentHovered) {
        controller.onContentHoverChange(hoverableContent && contentHovered)
    }
    val resolvedPlacement = side ?: placement
    TooltipPortal {
        AnchoredPopup(
            open = controller.open,
            onDismissRequest = {},
            placement = resolvedPlacement,
            offset = sideOffset,
            align = align,
            alignOffset = alignOffset,
            dismissOnClickOutside = false,
            focusable = false,
            enter = enter,
            exit = exit,
        ) {
            val chipModifier =
                modifier
                    .then(if (hoverableContent) Modifier.hoverable(contentHoverSource) else Modifier)
                    .styleable(
                        styleState,
                        rememberTooltipContentStyle() then style,
                    )
            TooltipBubble(
                placement = resolvedPlacement,
                showArrow = showArrow,
                chipModifier = chipModifier,
                content = content,
            )
        }
    }
}

@Composable
private fun TooltipBubble(
    placement: PopupPlacement,
    showArrow: Boolean,
    chipModifier: Modifier,
    content: @Composable () -> Unit,
) {
    val chip = @Composable { Box(modifier = chipModifier) { content() } }
    if (!showArrow) {
        chip()
        return
    }
    when (placement) {
        PopupPlacement.Top, PopupPlacement.Bottom ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (placement == PopupPlacement.Bottom) {
                    TooltipArrow(modifier = Modifier.offset(y = TooltipDefaults.ArrowOverlap))
                }
                chip()
                if (placement == PopupPlacement.Top) {
                    TooltipArrow(modifier = Modifier.offset(y = -TooltipDefaults.ArrowOverlap))
                }
            }
        PopupPlacement.Start, PopupPlacement.End ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (placement == PopupPlacement.End) {
                    TooltipArrow(modifier = Modifier.offset(x = TooltipDefaults.ArrowOverlap))
                }
                chip()
                if (placement == PopupPlacement.Start) {
                    TooltipArrow(modifier = Modifier.offset(x = -TooltipDefaults.ArrowOverlap))
                }
            }
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
                .graphicsLayer { rotationZ = 45f }
                .clip(RoundedCornerShape(TooltipDefaults.ArrowRadius))
                .background(currentTheme.colors.foreground),
    )
}

/** Teleport host for [TooltipContent]. Base UI does not export TooltipPortal. */
@Composable
internal fun TooltipPortal(content: @Composable () -> Unit) {
    content()
}
