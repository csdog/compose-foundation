@file:OptIn(
    androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class,
    ExperimentalTypeInference::class,
)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.clickable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.dashedBorder
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.ui.styles.scrollFade
import com.toutiaoyan.compose.ui.styles.ScrollFadeOrientation
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlinx.coroutines.launch

/** Part of shadcn's "AI Elements" family: an [Attachment]'s lifecycle. */
enum class AttachmentState { Idle, Uploading, Processing, Error, Done }

enum class AttachmentSize { Default, Sm, Xs }

enum class AttachmentOrientation { Horizontal, Vertical }

enum class AttachmentMediaVariant { Icon, Image }

private val LocalAttachmentState = compositionLocalOf { AttachmentState.Done }

@Composable
private fun AttachmentState.rememberStyle(
    size: AttachmentSize,
    interactive: Boolean,
    focusWithin: Boolean,
): Style {
    val theme = currentTheme
    val colors = theme.colors
    val shapes = twRadius

    // Error: `border-destructive/30`. Idle: dashed via [dashedBorder] (Base
    // `data-[state=idle]:border-dashed`) — Style has no dash stroke. Others: solid border.
    val stroke =
        when (this) {
            AttachmentState.Error -> colors.destructive.copy(alpha = 0.3f)
            AttachmentState.Idle -> null
            else -> colors.border
        }

    val radius = if (theme.activePreset == StylePreset.Nova && size == AttachmentSize.Xs) shapes.lg else shapes.xl
    val nova = theme.activePreset == StylePreset.Nova
    return remember(this, colors, radius, nova, interactive, focusWithin) {
        Style {
            background(colors.card)
            shape(RoundedCornerShape(radius))
            borderWidth(1.dp)
            borderColor(stroke ?: androidx.compose.ui.graphics.Color.Transparent)
            if (nova) {
                if (interactive) {
                    animate(
                        toSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        fromSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                    ) {
                        hovered { background(colors.muted.copy(alpha = 0.5f)) }
                    }
                }
                // CSS focus-within includes pointer focus and every descendant,
                // independent of the action button's own focus-visible ring.
                if (focusWithin) {
                    dropShadow(
                        Shadow(radius = 0.dp, spread = 1.dp, color = colors.ring.copy(alpha = 0.5f)),
                    )
                }
            }
        }
    }
}

/**
 * A file-attachment chip (e.g. for a chat composer's upload tray), part of shadcn's "AI
 * Elements" family. Matches real shadcn/ui's `attachment.tsx` — state-driven border
 * ([AttachmentState.Idle] dashed, [AttachmentState.Error] destructive/30, else solid)
 * and orientation (horizontal row vs. vertical card).
 *
 * [onClick] is the Compose stand-in for Base [AttachmentTrigger] (full-bleed hit
 * target). Prefer it for the common case; use [BoxScope.AttachmentTrigger] only in
 * custom [Box] overlays.
 *
 * [actions] and [content] use [AttachmentScope] to pass size/orientation to parts,
 * without exposing an internal `RowScope` -- real
 * shadcn's `AttachmentActions` is a CSS sibling of the content that's laid out inline for
 * [AttachmentOrientation.Horizontal] but absolutely-positioned over the top-right
 * corner for [AttachmentOrientation.Vertical] (a "remove" button floating on a
 * thumbnail). [Attachment] itself builds whichever container each case needs, so
 * [actions] never needs to know which scope it's rendered in.
 *
 * Usage:
 * ```
 * AttachmentGroup {
 *     Attachment(
 *         state = AttachmentState.Done,
 *         actions = {
 *             AttachmentActions {
 *                 AttachmentAction(onClick = {}) { /* icon */ }
 *             }
 *         },
 *     ) {
 *         AttachmentMedia { EmojiText("📄") }
 *         AttachmentContent {
 *             AttachmentTitle("report.pdf")
 *             AttachmentDescription("2.4 MB")
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun Attachment(
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = AttachmentState.Done,
    size: AttachmentSize = AttachmentSize.Default,
    orientation: AttachmentOrientation = AttachmentOrientation.Horizontal,
    onClick: (() -> Unit)? = null,
    actions: (@Composable AttachmentScope.() -> Unit)? = null,
    content: @Composable AttachmentScope.() -> Unit,
) {
    val parts = remember(size, orientation) { AttachmentScope(size, orientation) }
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    var focusWithin by remember { mutableStateOf(false) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val padding =
        when (size) {
            AttachmentSize.Default -> twSpacing.sm
            AttachmentSize.Sm -> twSpacing.xs
            AttachmentSize.Xs -> twSpacing.xs2
        }
    val corner =
        if (currentTheme.activePreset == StylePreset.Nova &&
            size == AttachmentSize.Xs
        ) {
            twRadius.lg
        } else {
            twRadius.xl
        }
    val idleDash =
        if (state == AttachmentState.Idle) {
            Modifier.dashedBorder(color = currentTheme.colors.border, corner = corner)
        } else {
            Modifier
        }

    val nova = currentTheme.activePreset == StylePreset.Nova
    val textStyle =
        if (nova) {
            LocalDefaultTextStyle.current.merge(
                TextStyle(
                    fontSize = if (size == AttachmentSize.Default) 14.sp else 12.sp,
                    lineHeight = if (size == AttachmentSize.Default) 20.sp else 16.sp,
                ),
            )
        } else {
            LocalDefaultTextStyle.current
        }
    CompositionLocalProvider(
        LocalDefaultTextStyle provides textStyle,
        LocalInlineIconSize provides
            if (nova) {
                when {
                    orientation == AttachmentOrientation.Vertical -> 24.dp
                    size == AttachmentSize.Xs -> 14.dp
                    else -> 16.dp
                }
            } else {
                LocalInlineIconSize.current
            },
        LocalAttachmentState provides state,
        LocalContentColor provides currentTheme.colors.cardForeground,
    ) {
        Box(
            // This constraint boundary precedes preferred caller sizing, like
            // max-w-full; requiredWidth can explicitly request overflow.
            modifier =
                Modifier.then(
                    AttachmentGroupItemElement(
                        if (nova && orientation == AttachmentOrientation.Horizontal) 160.dp else 0.dp,
                    ),
                ).then(modifier)
                    .onFocusEvent { focusWithin = it.hasFocus }
                    .cssStyleable(
                        styleState,
                        state.rememberStyle(size, onClick != null, focusWithin) then
                            (if (onClick != null) touchTargetStyle then pressStyle else Style) then style,
                    )
                    .then(idleDash)
                    .then(
                        if (onClick != null) {
                            Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                role = Role.Button,
                                onClick = onClick,
                            )
                        } else {
                            Modifier
                        },
                    ),
            propagateMinConstraints = true,
        ) {
            if (currentTheme.activePreset == StylePreset.Nova) {
                AttachmentLayout(size, orientation) {
                    parts.content()
                    if (actions != null) Box(Modifier.layoutId(AttachmentPart.Actions)) { parts.actions() }
                }
            } else {
                if (orientation == AttachmentOrientation.Horizontal) {
                    Row(
                        modifier = Modifier.padding(padding),
                        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        parts.content()
                        if (actions != null) parts.actions()
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(padding).width(96.dp),
                        verticalArrangement = Arrangement.spacedBy(twSpacing.xs),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) { parts.content() }
                    if (actions != null) {
                        Box(
                            Modifier.align(Alignment.TopEnd).padding(twSpacing.xs).zIndex(1f),
                        ) { parts.actions() }
                    }
                }
            }
        }
    }
}

/**
 * Variant-aware parts of an attachment. Extract reusable parts as
 * `@Composable fun AttachmentScope.MyMedia()` to retain size/orientation.
 */
@androidx.compose.runtime.Immutable
class AttachmentScope internal constructor(
    val size: AttachmentSize,
    val orientation: AttachmentOrientation,
) {
    @Composable
    fun AttachmentMedia(
        modifier: Modifier = Modifier,
        style: Style = Style,
        content: @Composable () -> Unit,
    ) = AttachmentMediaImpl(AttachmentMediaVariant.Icon, modifier, style, size, content)

    @Composable
    fun AttachmentMedia(
        variant: AttachmentMediaVariant,
        modifier: Modifier = Modifier,
        style: Style = Style,
        content: @Composable () -> Unit,
    ) = AttachmentMediaImpl(variant, modifier, style, size, content)

    @Composable
    fun AttachmentActions(
        modifier: Modifier = Modifier,
        style: Style = Style,
        content: @Composable () -> Unit,
    ) = AttachmentActionsImpl(orientation, modifier, style, content)
}

/** Binary bridge for pre-0.3 callers; new source uses variant-aware slots. */
@Deprecated(
    "Use AttachmentScope slots; wrap stored unscoped lambdas in { oldContent() }.",
    level = DeprecationLevel.HIDDEN,
)
@Composable
fun Attachment(
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = AttachmentState.Done,
    size: AttachmentSize = AttachmentSize.Default,
    orientation: AttachmentOrientation = AttachmentOrientation.Horizontal,
    onClick: (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val scopedActions: (@Composable AttachmentScope.() -> Unit)? = actions?.let { legacy -> { legacy() } }
    Attachment(modifier, style, state, size, orientation, onClick, scopedActions) { content() }
}

/** A thumbnail/icon slot in an [Attachment]. */
@Composable
fun AttachmentMedia(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) = AttachmentMediaImpl(AttachmentMediaVariant.Icon, modifier, style, AttachmentSize.Default, content)

/** Image media dims to 60% while uploading, processing or in error. Supply a cropped image. */
@Composable
fun AttachmentMedia(
    variant: AttachmentMediaVariant,
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) = AttachmentMediaImpl(variant, modifier, style, AttachmentSize.Default, content)

@Composable
private fun AttachmentMediaImpl(
    variant: AttachmentMediaVariant,
    modifier: Modifier,
    style: Style,
    size: AttachmentSize,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val colors = currentTheme.colors
    val state = LocalAttachmentState.current
    val error = state == AttachmentState.Error
    val opacity =
        if (variant == AttachmentMediaVariant.Image &&
            state != AttachmentState.Idle &&
            state != AttachmentState.Done
        ) {
            .6f
        } else {
            1f
        }
    val errorStyle =
        remember(colors, error) {
            if (error) Style { background(colors.destructive.copy(alpha = 0.1f)) } else Style
        }
    CompositionLocalProvider(LocalContentColor provides if (error) colors.destructive else colors.foreground) {
        Box(
            modifier =
                modifier.layoutId(AttachmentPart.Media).alpha(opacity)
                    .styleable(styleState, rememberAttachmentMediaStyle(size) then errorStyle then style),
            contentAlignment = Alignment.Center,
        ) { content() }
    }
}

/** The title/description column in a [Attachment]. */
@Composable
fun AttachmentContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val inherited = LocalDefaultTextStyle.current
    val fontSize = inherited.fontSize.takeIf { it.isSp } ?: 14.sp
    val textStyle =
        if (currentTheme.activePreset == StylePreset.Nova) {
            inherited.copy(
                lineHeight = fontSize * 1.25f,
            )
        } else {
            inherited
        }
    CompositionLocalProvider(LocalDefaultTextStyle provides textStyle) {
        Column(
            modifier = modifier.layoutId(AttachmentPart.Content).styleable(styleState, style),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            content = content,
        )
    }
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun AttachmentContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = LocalAttachmentState.current,
) {
    AttachmentContent(modifier = modifier, style = style) {
        AttachmentTitle(title, state = state)
        if (description != null) {
            AttachmentDescription(description)
        }
    }
}

/**
 * The attachment's filename. Truncates with an ellipsis past one line, matching real
 * shadcn's CSS `truncate`. Shimmers while [state] is [AttachmentState.Uploading] or
 * [AttachmentState.Processing], matching real shadcn's CSS `shimmer` class --
 * using the shared moving text-gradient utility. The default state follows the
 * nearest [Attachment]; an explicit [state] overrides that inherited state.
 */
@Composable
fun AttachmentTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = LocalAttachmentState.current,
) {
    val shimmering = state == AttachmentState.Uploading || state == AttachmentState.Processing
    val nova = currentTheme.activePreset == StylePreset.Nova
    val typography = if (nova) TextStyle(fontWeight = FontWeight.Medium) else TextStyle.Default
    Text(
        variant = if (nova) TextVariant.BodyMedium else TextVariant.LabelLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.shimmer(enabled = shimmering),
        textStyle = (if (shimmering) TextColors.Muted else TextStyle.Default).merge(typography),
        style = style,
    ) { text }
}

@Composable
@JvmName("AttachmentTitleLambda")
@OverloadResolutionByLambdaReturnType
fun AttachmentTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = LocalAttachmentState.current,
    text: () -> String,
) {
    AttachmentTitle(text = text(), modifier = modifier, style = style, state = state)
}

/** The attachment's byte size/status line. Truncates with an ellipsis, matching real shadcn's CSS `truncate`. */
@Composable
fun AttachmentDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    isError: Boolean = LocalAttachmentState.current == AttachmentState.Error,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val theme = currentTheme
    Text(
        variant = TextVariant.BodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textStyle =
            if (isError) {
                TextStyle(color = theme.colors.destructive.copy(alpha = 0.8f))
            } else {
                TextColors.Muted
            },
        style = style,
        modifier = modifier.styleable(styleState, style),
    ) { text }
}

@Composable
@JvmName("AttachmentDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun AttachmentDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    isError: Boolean = LocalAttachmentState.current == AttachmentState.Error,
    text: () -> String,
) {
    AttachmentDescription(text = text(), modifier = modifier, style = style, isError = isError)
}

/** A group of trailing actions (e.g. a remove button) in a [Attachment]. */
@Composable
fun AttachmentActions(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    AttachmentActionsImpl(AttachmentOrientation.Horizontal, modifier, style, content)
}

@Composable
private fun AttachmentActionsImpl(
    orientation: AttachmentOrientation,
    modifier: Modifier,
    style: Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.layoutId(AttachmentPart.Actions).styleable(styleState, style),
        horizontalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) {
                    if (orientation == AttachmentOrientation.Vertical) twSpacing.xs else 0.dp
                } else {
                    twSpacing.xs2
                },
            ),
        content = { content() },
    )
}

/**
 * Icon/action button inside [AttachmentActions]. Matches Base `AttachmentAction`
 * (`Button` ghost + `icon-xs` by default).
 */
@Composable
fun AttachmentAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Ghost,
    size: ButtonSize = ButtonSize.IconXs,
    label: String? = null,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        label = label,
        style = style,
        content = content,
    )
}

/**
 * Full-bleed hit target over an [Attachment] (Base `AttachmentTrigger`:
 * `absolute inset-0 z-10`). Prefer [Attachment] `onClick` for the common case;
 * use this in custom [Box] overlays.
 */
@Composable
fun BoxScope.AttachmentTrigger(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    label: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val resolvedEnabled = enabled && !disabled
    Box(
        modifier =
            modifier
                .matchParentSize()
                .zIndex(10f)
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    if (!label.isNullOrEmpty()) {
                        contentDescription = label
                    }
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = resolvedEnabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
    )
}

/**
 * A horizontally-scrollable row of [Attachment]s (e.g. a composer's upload tray).
 * Content-sized by default, matching real shadcn's CSS (`flex ... overflow-x-auto`, no
 * forced height) -- does not [fillMaxHeight], since doing so inside an unconstrained
 * (wrap-content) parent expands the row to consume all available vertical space.
 * Cards snap to their logical start with 4dp scroll padding. Oversized cards
 * remain scrollable inside their own snap area. Non-attachment content does not
 * create snap stops. Unhandled arrow keys scroll the group; horizontal motion
 * at its edges is contained while vertical motion can reach an ancestor.
 */
@Composable
fun AttachmentGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val scrollState = rememberScrollState()
    val direction = LocalLayoutDirection.current
    val paddingPx = with(LocalDensity.current) { 4.dp.toPx() }
    val keyboardStepPx = with(LocalDensity.current) { 40.dp.toPx() }
    val snap =
        remember(scrollState, direction, paddingPx, keyboardStepPx) {
            AttachmentGroupSnapInfo(scrollState, direction, paddingPx, keyboardStepPx)
        }
    val snapping = rememberSnapFlingBehavior(snap)
    val fling =
        remember(snap, snapping) {
            object : FlingBehavior {
                override suspend fun ScrollScope.performFling(initialVelocity: Float): Float =
                    with(snapping) {
                        // Wheel events supply their motion already. Reset velocity for
                        // the final snap, including its spring, not just its approach.
                        performFling(if (snap.wheelInput) 0f else initialVelocity)
                    }
            }
        }
    val scope = rememberCoroutineScope()
    val containment =
        remember(scrollState) {
            object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset = if (scrollState.maxValue > 0) Offset(available.x, 0f) else Offset.Zero

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity,
                ): Velocity = if (scrollState.maxValue > 0) Velocity(available.x, 0f) else Velocity.Zero
            }
        }
    Row(
        modifier =
            modifier.styleable(styleState, style)
                .scrollFade(scrollState, ScrollFadeOrientation.Horizontal, 40.dp, .12f, 96.dp)
                .pointerInput(snap) {
                    awaitPointerEventScope {
                        while (true) {
                            when (awaitPointerEvent(PointerEventPass.Initial).type) {
                                PointerEventType.Scroll -> snap.wheelInput = true
                                PointerEventType.Press -> snap.wheelInput = false
                            }
                        }
                    }
                }
                .nestedScroll(containment)
                .onKeyEvent { event ->
                    val arrow = event.key == Key.DirectionRight || event.key == Key.DirectionLeft
                    if (event.type == KeyEventType.KeyDown && arrow && !event.isAltPressed && !event.isCtrlPressed &&
                        !event.isMetaPressed && !event.isShiftPressed && scrollState.maxValue > 0
                    ) {
                        val forward = (event.key == Key.DirectionRight) == (direction == LayoutDirection.Ltr)
                        scope.launch { scrollState.animateScrollTo(snap.keyboardTarget(forward)) }
                        true
                    } else {
                        false
                    }
                }
                .focusable()
                .layout { measurable, constraints ->
                    snap.updateViewport(constraints.maxWidth)
                    val child = measurable.measure(constraints)
                    layout(child.width, child.height) { child.placeRelative(0, 0) }
                }
                .horizontalScroll(scrollState, flingBehavior = fling)
                .then(AttachmentGroupSnapElement(snap))
                .then(
                    if (currentTheme.activePreset == StylePreset.Nova) Modifier.padding(vertical = 4.dp) else Modifier,
                ),
        horizontalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) 12.dp else twSpacing.sm,
            ),
        content = content,
    )
}
