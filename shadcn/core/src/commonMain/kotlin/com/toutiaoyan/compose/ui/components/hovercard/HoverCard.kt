@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
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
)

private val LocalHoverCard = staticCompositionLocalOf<HoverCardController?> { null }

private val LocalHoverCardDelayMillis = staticCompositionLocalOf { 0 }

private val LocalHoverCardCloseDelayMillis = staticCompositionLocalOf { 100 }

/**
 * Provides hover delay for nested [HoverCard]s. Matches shadcn/Radix hover-card
 * `openDelay` / `closeDelay`; default open `0` is instant, close `100` lets the
 * pointer travel from [HoverCardTrigger] to the portaled [HoverCardContent].
 */
@Composable
fun HoverCardProvider(
    delayMillis: Int = 0,
    closeDelayMillis: Int = 100,
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
    val interactionSource = remember { MutableInteractionSource() }
    val triggerHovered by interactionSource.collectIsHoveredAsState()
    var contentHovered by remember { mutableStateOf(false) }
    var triggerDisabled by remember { mutableStateOf(false) }
    val isActive =
        !disabled &&
            !triggerDisabled &&
            (triggerHovered || (contentHovered && !disableHoverableContent))
    val resolvedDelay = openDelay ?: delayMillis ?: LocalHoverCardDelayMillis.current
    val resolvedClose = closeDelay ?: closeDelayMillis ?: LocalHoverCardCloseDelayMillis.current
    LaunchedEffect(isActive, resolvedDelay, resolvedClose) {
        if (isActive) {
            if (resolvedDelay > 0) delay(resolvedDelay.toLong())
            setOpen(true)
        } else if (isOpen) {
            if (resolvedClose > 0) delay(resolvedClose.toLong())
            setOpen(false)
        }
    }
    val controller =
        HoverCardController(
            open = isOpen,
            onOpenChange = setOpen,
            onContentHoverChange = { contentHovered = it },
            onTriggerDisabledChange = { triggerDisabled = it },
        )
    CompositionLocalProvider(LocalHoverCard provides controller) {
        Box(modifier = modifier.hoverable(interactionSource)) {
            HoverCardScope(controller).content()
        }
    }
}

/** Anchor for the parent [HoverCard]. Matches real shadcn's `HoverCardTrigger`. */
@Composable
fun HoverCardScope.HoverCardTrigger(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val controller = hoverCardController()
    DisposableEffect(disabled) {
        controller.onTriggerDisabledChange(disabled)
        onDispose { controller.onTriggerDisabledChange(false) }
    }
    Box(
        modifier =
            modifier.semantics {
                stateDescription = if (controller.open) "Expanded" else "Collapsed"
            },
    ) {
        content()
    }
}

/** String-label overload of [HoverCardTrigger]. */
@Composable
fun HoverCardScope.HoverCardTrigger(
    text: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
) {
    HoverCardTrigger(modifier = modifier, disabled = disabled) {
        Text { text }
    }
}

/**
 * The preview panel of a [HoverCard]. [placement] is Base/Radix `side`.
 * Base Content defaults: `sideOffset` 4, `align` center, `alignOffset` 4.
 * A focusable popup on a hover-triggered overlay flickers open/closed, so this
 * stays `focusable = false` (see [AnchoredPopup]). [dismissOnClickOutside] is
 * pointer-outside dismiss (default false; hover-leave still closes).
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
    dismissOnClickOutside: Boolean = false,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
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
                        .styleable(
                            styleState,
                            rememberHoverCardContentStyle() then style,
                        ),
            ) {
                content()
            }
        }
    }
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
    dismissOnClickOutside: Boolean = false,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
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
    dismissOnClickOutside: Boolean = false,
    enter: EnterTransition = PopupTransitions.Enter,
    exit: ExitTransition = PopupTransitions.Exit,
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
