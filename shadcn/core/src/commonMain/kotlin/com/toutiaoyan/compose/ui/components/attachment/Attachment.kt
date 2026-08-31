@file:OptIn(
    androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class,
    ExperimentalTypeInference::class,
)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.dashedBorder
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Part of shadcn's "AI Elements" family: an [Attachment]'s lifecycle. */
enum class AttachmentState { Idle, Uploading, Processing, Error, Done }

enum class AttachmentSize { Default, Sm, Xs }

enum class AttachmentOrientation { Horizontal, Vertical }

@Composable
private fun AttachmentState.rememberStyle(): Style {
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

    return remember(this, colors, shapes) {
        Style {
            background(colors.background)
            shape(RoundedCornerShape(shapes.xl))
            if (stroke != null) {
                borderWidth(1.dp)
                borderColor(stroke)
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
 * [actions] is a plain `@Composable () -> Unit` slot, not scoped to `RowScope` -- real
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
    actions: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val padding =
        when (size) {
            AttachmentSize.Default -> twSpacing.sm
            AttachmentSize.Sm -> twSpacing.xs
            AttachmentSize.Xs -> twSpacing.xs2
        }
    val corner = twRadius.xl
    val idleDash =
        if (state == AttachmentState.Idle) {
            Modifier.dashedBorder(color = currentTheme.colors.border, corner = corner)
        } else {
            Modifier
        }

    Box(
        modifier =
            modifier.styleable(
                styleState,
                (if (onClick != null) touchTargetStyle then pressStyle else Style) then style,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .styleable(styleState, state.rememberStyle())
                    .then(idleDash),
        ) {
            if (orientation == AttachmentOrientation.Horizontal) {
                Row(
                    modifier = Modifier.padding(padding),
                    horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    content()
                    // When [onClick] paints [AttachmentTrigger], actions are siblings above it.
                    if (actions != null && onClick == null) actions()
                }
            } else {
                Column(
                    modifier = Modifier.padding(padding).width(96.dp),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    content()
                }
            }
            if (onClick != null) {
                AttachmentTrigger(onClick = onClick)
            }
            if (actions != null && (onClick != null || orientation == AttachmentOrientation.Vertical)) {
                Box(
                    modifier =
                        Modifier
                            .align(
                                if (orientation == AttachmentOrientation.Vertical) {
                                    Alignment.TopEnd
                                } else {
                                    Alignment.CenterEnd
                                },
                            )
                            .padding(
                                if (orientation == AttachmentOrientation.Vertical) {
                                    twSpacing.xs
                                } else {
                                    padding
                                },
                            )
                            .zIndex(1f),
                ) {
                    actions()
                }
            }
        }
    }
}

/** A thumbnail/icon slot in a [Attachment]. */
@Composable
fun AttachmentMedia(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier.styleable(
                styleState,
                rememberAttachmentMediaStyle() then style,
            ),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/** The title/description column in a [Attachment]. */
@Composable
fun AttachmentContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        content = content,
    )
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun AttachmentContent(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = AttachmentState.Done,
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
 * approximated the same way [Skeleton] approximates `animate-pulse`, an alpha
 * oscillation rather than a literal moving-gradient sweep (Compose has no CSS
 * `background-position` animation primitive).
 */
@Composable
fun AttachmentTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = AttachmentState.Done,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val shimmering = state == AttachmentState.Uploading || state == AttachmentState.Processing
    val alphaModifier =
        if (shimmering) {
            val transition = rememberInfiniteTransition(label = "attachment-shimmer")
            val alpha by transition.animateFloat(
                initialValue = 1f,
                targetValue = 0.5f,
                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                label = "attachment-shimmer-alpha",
            )
            Modifier.alpha(alpha)
        } else {
            Modifier
        }
    Text(
        variant = TextVariant.LabelLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.then(alphaModifier).styleable(styleState, style)
    ) { text }
}

@Composable
@JvmName("AttachmentTitleLambda")
@OverloadResolutionByLambdaReturnType
fun AttachmentTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: AttachmentState = AttachmentState.Done,
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
    isError: Boolean = false,
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
    isError: Boolean = false,
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
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs2),
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
 */
@Composable
fun AttachmentGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()).styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        content = content,
    )
}
