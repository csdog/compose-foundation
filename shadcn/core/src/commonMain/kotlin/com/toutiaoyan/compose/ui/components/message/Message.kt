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

/**
 * The enclosing [Message]'s [MessageAlign], read by [MessageHeader] and
 * [MessageFooter] so they self-align to match -- matches real shadcn's CSS
 * `group-data-[align=end]/message:justify-end` (a parent-state selector with no direct
 * Compose equivalent, so this is threaded down via [CompositionLocalProvider] instead).
 * Defaults to [MessageAlign.Start] so Header/Footer degrade gracefully if ever used
 * outside a [Message].
 */
internal val LocalMessageAlign = compositionLocalOf { MessageAlign.Start }

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
    avatar: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        // Real shadcn's `.message` is `w-full` -- without this, the row sizes to its
        // own content (avatar + however wide the bubble happens to be) and the parent
        // Column's default Alignment.Start then places that whole undersized row flush
        // left, regardless of [align]. Bubble's own end-alignment only resolves
        // *within* this row, so a narrower-than-full row left an End-aligned bubble
        // looking stranded short of the real right edge, with dead space after it.
        modifier = modifier.fillMaxWidth().styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        CompositionLocalProvider(LocalMessageAlign provides align) {
            if (align == MessageAlign.Start) {
                Box(modifier = Modifier.align(Alignment.Bottom)) { avatar() }
                MessageContent(content = content)
            } else {
                MessageContent(content = content)
                Box(modifier = Modifier.align(Alignment.Bottom)) { avatar() }
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
    content: @Composable () -> Unit,
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
        Text { text }
    }
}

/** The flex column beside [MessageAvatar]. Matches shadcn's `MessageContent`. */
@Composable
fun RowScope.MessageContent(content: @Composable ColumnScope.() -> Unit) {
    // fill = true (the default): real shadcn's MessageContent is `w-full`, occupying
    // its full flex-1 share -- with fill = false a shrunk column (e.g. a short bubble)
    // would be positioned right after the previous sibling instead of at its slot's
    // true edge, leaving the avatar stranded short of the row's real end for
    // MessageAlign.End, even with the outer Row now correctly filling its width.
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs),
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
        verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
        content = content,
    )
}

/** A metadata row (e.g. sender name) above a [Message]'s content, self-aligned to match [LocalMessageAlign]. */
@Composable
fun ColumnScope.MessageHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val align = LocalMessageAlign.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier
                .align(if (align == MessageAlign.End) Alignment.End else Alignment.Start)
                .styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        content = content,
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
        Text(variant = TextVariant.LabelLarge) { text }
    }
}

/** A metadata row (e.g. timestamp) below a [Message]'s content, self-aligned to match [LocalMessageAlign]. */
@Composable
fun ColumnScope.MessageFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val align = LocalMessageAlign.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier
                .align(if (align == MessageAlign.End) Alignment.End else Alignment.Start)
                .styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        content = content,
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
        Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { text }
    }
}
