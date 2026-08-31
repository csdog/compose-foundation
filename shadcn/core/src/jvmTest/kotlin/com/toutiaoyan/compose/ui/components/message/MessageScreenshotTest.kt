@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleContent
import com.toutiaoyan.compose.ui.components.bubble.BubbleReactions
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors

class MessageScreenshotTest : ScreenshotTest() {
    private fun startAligned(darkTheme: Boolean) {
        snapshot("message_start_aligned", darkTheme = darkTheme) {
            Message(
                modifier = Modifier.width(280.dp),
                align = MessageAlign.Start,
                avatar = { MessageAvatar("AI") },
            ) {
                MessageHeader("Assistant")
                Text { "Hello! How can I help you today?" }
            }
        }
    }

    private fun endAligned(darkTheme: Boolean) {
        snapshot("message_end_aligned", darkTheme = darkTheme) {
            Message(
                modifier = Modifier.width(280.dp),
                align = MessageAlign.End,
                avatar = { MessageAvatar("Me") },
                text = "Can you summarize this document?",
            )
        }
    }

    private fun group(darkTheme: Boolean) {
        snapshot("message_group", darkTheme = darkTheme) {
            MessageGroup(modifier = Modifier.width(280.dp)) {
                Message(avatar = { MessageAvatar("AI") }, text = "Sure, one moment.")
                Message(
                    align = MessageAlign.End,
                    avatar = { MessageAvatar("Me") },
                ) {
                    Text { "Thanks!" }
                    MessageFooter("2:04 PM")
                }
            }
        }
    }

    private fun conversationWithBubbles(darkTheme: Boolean) {
        snapshot("message_conversation_with_bubbles", darkTheme = darkTheme) {
            Column(modifier = Modifier.width(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MessageGroup {
                    Message(
                        align = MessageAlign.End,
                        avatar = { MessageAvatar { EmojiText("🙂") } },
                    ) {
                        Bubble("Deploying to prod real quick.", align = MessageAlign.End)
                    }
                    Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                        Bubble("It's 4:55 PM. On a Friday.", variant = BubbleVariant.Muted)
                    }
                    Message(
                        align = MessageAlign.End,
                        avatar = { MessageAvatar { EmojiText("🙂") } },
                    ) {
                        Bubble("It's a one-line change.", align = MessageAlign.End)
                        MessageFooter("Delivered")
                    }
                    Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                        Bubble {
                            BubbleContent(variant = BubbleVariant.Muted) {
                                EmojiText("It's always a one-line change 😭.")
                            }
                        }
                    }
                    Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                        Bubble {
                            BubbleContent("Alright, let me take a look.", variant = BubbleVariant.Muted)
                            BubbleReactions { EmojiText("👍", typography = TextVariant.LabelSmall) }
                        }
                    }
                }
                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "Oliver is typing…" }
            }
        }
    }

    @Test
    fun start_aligned_light() = startAligned(darkTheme = false)

    @Test
    fun start_aligned_dark() = startAligned(darkTheme = true)

    @Test
    fun end_aligned_light() = endAligned(darkTheme = false)

    @Test
    fun end_aligned_dark() = endAligned(darkTheme = true)

    @Test
    fun group_light() = group(darkTheme = false)

    @Test
    fun conversation_with_bubbles_light() = conversationWithBubbles(darkTheme = false)

    @Test
    fun conversation_with_bubbles_dark() = conversationWithBubbles(darkTheme = true)
}
