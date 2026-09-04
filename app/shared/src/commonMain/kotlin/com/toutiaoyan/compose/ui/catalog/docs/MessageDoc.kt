@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleContent
import com.toutiaoyan.compose.ui.components.bubble.BubbleReactions
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.message.Message
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.message.MessageAvatar
import com.toutiaoyan.compose.ui.components.message.MessageFooter
import com.toutiaoyan.compose.ui.components.message.MessageGroup
import com.toutiaoyan.compose.ui.components.message.MessageHeader
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors

val messageDoc =
    ComponentDoc(
        id = "message",
        title = "Message",
        description =
            "One chat-transcript row: avatar and content laid out side by side, mirrored for the sender's own " +
                "messages.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            Message(
                align = MessageAlign.Start,
                avatar = { MessageAvatar("AI") },
                text = "Hello! How can I help you today?",
            )
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Start aligned",
                    code =
                        """
                        Message(
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default align",
                    code =
                        """
                        Message(
                            align = MessageAlign.Start,
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(
                            align = MessageAlign.Start,
                            avatar = { MessageAvatar("AI") },
                        ) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
                ComponentExample(
                    title = "End aligned",
                    code =
                        """
                        Message(
                            align = MessageAlign.End,
                            avatar = { MessageAvatar("Me") },
                            text = "Can you summarize this document?",
                        )
                        """.trimIndent(),
                    preview = {
                        Message(
                            align = MessageAlign.End,
                            avatar = { MessageAvatar("Me") },
                            text = "Can you summarize this document?",
                        )
                    },
                ),
                ComponentExample(
                    title = "Conversation with bubbles",
                    code =
                        """
                        MessageGroup {
                            Message(
                                align = MessageAlign.End,
                                avatar = { MessageAvatar { EmojiText("🙂") } },
                            ) {
                                    Bubble("Deploying to prod real quick.", align = MessageAlign.End)
                            }
                            Message(avatar = { MessageAvatar { EmojiText("👨") } }) {
                                Bubble {
                                        BubbleContent("It's 4:55 PM. On a Friday.", variant = BubbleVariant.Muted)
                                }
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
                                    BubbleReactions { EmojiText("👍", typography = TextTypography.LabelSmall) }
                                }
                            }
                        }
                        Text(typography = TextTypography.LabelSmall, textStyle = TextStyles.Muted) { "Oliver is typing…" }
                        """.trimIndent(),
                    preview = {
                        Column(
                            modifier = Modifier.width(280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
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
                                        BubbleReactions {
                                            EmojiText("👍", typography = TextVariant.LabelSmall)
                                        }
                                    }
                                }
                            }
                            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "Oliver is typing…" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Avatar",
                    code =
                        """
                        Message(avatar = { MessageAvatar("AI") }, text = "Hello! How can I help you today?")
                        """.trimIndent(),
                    preview = {
                        Message(avatar = { MessageAvatar("AI") }, text = "Hello! How can I help you today?")
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Message(avatar = { MessageAvatar("AI") }) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(avatar = { MessageAvatar("AI") }) {
                            MessageHeader("Assistant")
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                            MessageFooter("Delivered")
                        }
                        """.trimIndent(),
                    preview = {
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                            MessageFooter("Delivered")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                        }
                        """.trimIndent(),
                    preview = {
                        Message(avatar = { MessageAvatar("AI") }) {
                            Text { "Hello! How can I help you today?" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        MessageGroup {
                            Message(avatar = { MessageAvatar("AI") }, text = "Hello!")
                            Message(align = MessageAlign.End, avatar = { MessageAvatar("Me") }, text = "Hi.")
                        }
                        """.trimIndent(),
                    preview = {
                        MessageGroup {
                            Message(avatar = { MessageAvatar("AI") }, text = "Hello!")
                            Message(align = MessageAlign.End, avatar = { MessageAvatar("Me") }, text = "Hi.")
                        }
                    },
                ),
            ),
    )
