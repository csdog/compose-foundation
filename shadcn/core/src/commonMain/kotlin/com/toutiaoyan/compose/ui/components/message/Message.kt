@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant

/** Which side of a [Message] row its avatar sits on -- the sender's own messages are usually [End]. */
enum class MessageAlign { Start, End }

/** Alignment inherited by the content column, bubbles, and footer. */
internal val LocalMessageAlign = compositionLocalOf { MessageAlign.Start }

/** Registers descendant parts after successful composition; no writes during composition/measurement. */
internal class MessageParts {
    var footerCount by mutableIntStateOf(0)
    var ghostCount by mutableIntStateOf(0)
}

internal val LocalMessageParts = compositionLocalOf<MessageParts?> { null }

@Composable
internal fun TrackMessagePart(ghost: Boolean = false) {
    val parts = LocalMessageParts.current
    DisposableEffect(parts, ghost) {
        if (parts != null) {
            if (ghost) parts.ghostCount++ else parts.footerCount++
        }
        onDispose {
            if (parts != null) {
                if (ghost) parts.ghostCount-- else parts.footerCount--
            }
        }
    }
}

class MessageState(val align: MessageAlign)

/** Reads the nearest [Message] alignment. Matches shadcn `group-data-[align]`. */
@Composable
fun messageState(): MessageState = MessageState(LocalMessageAlign.current)

/**
 * One chat-transcript message row: avatar + content side by side, part of shadcn's "AI
 * Elements" family. Matches real shadcn/ui's `message.tsx` -- [MessageAlign.End]
 * puts the avatar on the right, matching the classic "my messages on the right" chat
 * layout.
 *
 * Takes [avatar]/[content] as two separate slots (rather than one freeform `RowScope`
 * lambda the caller populates in a fixed order) specifically so [Message] itself
 * can deterministically flip their visual order for [MessageAlign.End] -- Compose
 * has no `flex-direction: row-reverse` equivalent that reorders an opaque composable
 * lambda's already-emitted children after the fact.
 *
 * Usage:
 * ```
 * MessageGroup {
 *     Message(
 *         align = MessageAlign.Start,
 *         avatar = { MessageAvatar("AI") },
 *         text = "Hello!",
 *     )
 * }
 * ```
 */
@Composable
fun Message(
    modifier: Modifier = Modifier,
    style: Style = Style,
    align: MessageAlign = MessageAlign.Start,
    avatar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val parts = remember { MessageParts() }
    Row(
        modifier =
            modifier.then(if (nova) Modifier else Modifier.fillMaxWidth())
                .styleable(styleState, rememberMessageRootStyle() then style),
        horizontalArrangement = Arrangement.spacedBy(if (nova) 8.dp else twSpacing.sm),
    ) {
        CompositionLocalProvider(
            LocalMessageAlign provides align,
            LocalMessageParts provides if (nova) parts else null,
            LocalDefaultTextStyle provides
                if (nova) {
                    TextStyle(
                        fontFamily = currentTheme.fontFamily, fontSize = 14.sp, lineHeight = 20.sp,
                        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    )
                } else {
                    LocalDefaultTextStyle.current
                },
        ) {
            val avatarSlot = @Composable {
                if (avatar != null) {
                    Box(
                        modifier =
                            Modifier.align(Alignment.Bottom).offset {
                                IntOffset(0, if (nova && parts.footerCount > 0) -32.dp.roundToPx() else 0)
                            },
                    ) { avatar() }
                }
            }
            if (align == MessageAlign.Start) {
                avatarSlot()
                MessageContentBody(Modifier.weight(1f), Style, content)
            } else {
                MessageContentBody(Modifier.weight(1f), Style, content)
                avatarSlot()
            }
        }
    }
}

/** Convenience: string body instead of a [content] slot. */
@Composable
fun Message(
    avatar: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    align: MessageAlign = MessageAlign.Start,
) {
    Message(modifier = modifier, style = style, align = align, avatar = avatar) {
        Text { text }
    }
}

/** A circular avatar slot for [Message]'s `avatar` parameter. */
@Composable
fun MessageAvatar(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit = {},
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier.styleable(
                styleState,
                rememberMessageAvatarStyle() then style,
            ),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/** String-label overload of [MessageAvatar]. */
@Composable
fun MessageAvatar(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    MessageAvatar(modifier = modifier, style = style) {
        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { Text { text } }
    }
}

/** The flex column beside [MessageAvatar]. Matches shadcn's `MessageContent`. */
@Composable
fun RowScope.MessageContent(content: @Composable ColumnScope.() -> Unit) {
    MessageContentBody(Modifier.weight(1f), Style, content)
}

/** Standalone content slot; root modifier and style are caller-controlled. */
@Composable
fun MessageContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    MessageContentBody(modifier, style, content)
}

@Composable
private fun MessageContentBody(
    modifier: Modifier,
    style: Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val nova = currentTheme.activePreset == StylePreset.Nova
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, rememberMessageRootStyle() then style),
        horizontalAlignment =
            if (nova &&
                LocalMessageAlign.current == MessageAlign.End
            ) {
                Alignment.End
            } else {
                Alignment.Start
            },
        verticalArrangement = Arrangement.spacedBy(if (nova) 10.dp else twSpacing.xs),
        content = content,
    )
}

/** Vertically stacks a transcript of [Message]s. */
@Composable
fun MessageGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, style),
        verticalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) {
                    8.dp
                } else {
                    twSpacing.sm
                },
            ),
        content = content,
    )
}

/**
 * A metadata row (e.g. sender name) above a [Message]'s content, aligned to start in Nova and to the message side in legacy presets.
 */
@Composable
fun ColumnScope.MessageHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val align = LocalMessageAlign.current
    val nova = currentTheme.activePreset == StylePreset.Nova
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier
                .align(if (!nova && align == MessageAlign.End) Alignment.End else Alignment.Start)
                .styleable(styleState, rememberMessageMetadataStyle() then style),
        horizontalArrangement = Arrangement.spacedBy(if (nova) 0.dp else twSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            CompositionLocalProvider(
                LocalContentColor provides if (nova) currentTheme.colors.mutedForeground else LocalContentColor.current,
                LocalDefaultTextStyle provides
                    if (nova) {
                        TextStyle(
                            fontFamily = currentTheme.fontFamily, fontSize = 12.sp, lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
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
        },
    )
}

/** String-label overload of [MessageHeader]. */
@Composable
fun ColumnScope.MessageHeader(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    MessageHeader(modifier = modifier, style = style) {
        Text(
            variant =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    TextVariant.BodyMedium
                } else {
                    TextVariant.LabelLarge
                },
        ) { text }
    }
}

/**
 * A metadata row (e.g. timestamp) below a [Message]'s content, aligned to start in Nova and to the message side in legacy presets.
 */
@Composable
fun ColumnScope.MessageFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val align = LocalMessageAlign.current
    val nova = currentTheme.activePreset == StylePreset.Nova
    TrackMessagePart()
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier
                .align(if (align == MessageAlign.End) Alignment.End else Alignment.Start)
                .styleable(styleState, rememberMessageMetadataStyle() then style),
        horizontalArrangement = Arrangement.spacedBy(if (nova) 0.dp else twSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            CompositionLocalProvider(
                LocalContentColor provides if (nova) currentTheme.colors.mutedForeground else LocalContentColor.current,
                LocalDefaultTextStyle provides
                    if (nova) {
                        TextStyle(
                            fontFamily = currentTheme.fontFamily, fontSize = 12.sp, lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
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
        },
    )
}

/** String-label overload of [MessageFooter]. */
@Composable
fun ColumnScope.MessageFooter(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    MessageFooter(modifier = modifier, style = style) {
        if (currentTheme.activePreset == StylePreset.Nova) {
            Text { text }
        } else {
            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { text }
        }
    }
}
