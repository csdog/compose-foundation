@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.bubble

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors

class BubbleScreenshotTest : ScreenshotTest() {
    private fun variants(darkTheme: Boolean) {
        snapshot("bubble_variants", darkTheme = darkTheme) {
            BubbleGroup(modifier = Modifier.width(280.dp)) {
                Bubble { BubbleContent(variant = BubbleVariant.Default) { Text { "Default" } } }
                Bubble {
                    BubbleContent(
                        variant = BubbleVariant.Secondary,
                    ) { Text { "Secondary" } }
                }
                Bubble { BubbleContent(variant = BubbleVariant.Muted) { Text { "Muted" } } }
                Bubble { BubbleContent(variant = BubbleVariant.Outline) { Text { "Outline" } } }
                Bubble { BubbleContent(variant = BubbleVariant.Ghost) { Text { "Ghost" } } }
                Bubble {
                    BubbleContent(
                        variant = BubbleVariant.Destructive,
                    ) { Text { "Destructive" } }
                }
            }
        }
    }

    private fun alignment(darkTheme: Boolean) {
        snapshot("bubble_alignment", darkTheme = darkTheme) {
            BubbleGroup(modifier = Modifier.width(280.dp)) {
                Bubble(align = MessageAlign.Start) {
                    BubbleContent(variant = BubbleVariant.Muted) { Text { "Hi, how can I help?" } }
                }
                Bubble(align = MessageAlign.End) {
                    BubbleContent(variant = BubbleVariant.Default) { Text { "Summarize this for me." } }
                }
            }
        }
    }

    private fun conversationWithReactions(darkTheme: Boolean) {
        snapshot("bubble_conversation_with_reactions", darkTheme = darkTheme) {
            BubbleGroup(modifier = Modifier.width(280.dp)) {
                Bubble {
                    BubbleContent(variant = BubbleVariant.Muted) {
                        Text { "Hey! Want to see chat bubbles?" }
                    }
                }
                Bubble {
                    BubbleContent(variant = BubbleVariant.Muted) {
                        Text { "I can group messages, switch sides, and keep the whole thread easy to scan." }
                    }
                    BubbleReactions { EmojiText("👍", typography = TextVariant.LabelSmall) }
                }
                Bubble(align = MessageAlign.End) {
                    BubbleContent { Text { "Sure. Hit me with your best demo." } }
                }
                Bubble {
                    BubbleContent(variant = BubbleVariant.Muted) {
                        Text { "Yes. You are reading a demo that is demoing itself. Very meta. Very on-brand." }
                    }
                    BubbleReactions {
                        EmojiText("👍", typography = TextVariant.LabelSmall)
                        EmojiText("🔥", typography = TextVariant.LabelSmall)
                        Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "+2" }
                    }
                }
            }
        }
    }

    @Test
    fun variants_light() = variants(darkTheme = false)

    @Test
    fun variants_dark() = variants(darkTheme = true)

    @Test
    fun alignment_light() = alignment(darkTheme = false)

    @Test
    fun alignment_dark() = alignment(darkTheme = true)

    @Test
    fun conversation_with_reactions_light() = conversationWithReactions(darkTheme = false)

    @Test
    fun conversation_with_reactions_dark() = conversationWithReactions(darkTheme = true)

    private fun tinted(darkTheme: Boolean) {
        snapshot("bubble_tinted", darkTheme = darkTheme) {
            BubbleGroup(modifier = Modifier.width(280.dp)) {
                Bubble { BubbleContent(variant = BubbleVariant.Tinted) { Text { "Tinted" } } }
            }
        }
    }

    @Test
    fun tinted_light() = tinted(darkTheme = false)

    @Test
    fun tinted_dark() = tinted(darkTheme = true)

    private fun reactionsTop(darkTheme: Boolean) {
        snapshot("bubble_reactions_top", darkTheme = darkTheme) {
            BubbleGroup(modifier = Modifier.width(280.dp)) {
                Bubble {
                    BubbleContent { Text { "Hello" } }
                    BubbleReactions(side = BubbleReactionsSide.Top) {
                        EmojiText("👍", typography = TextVariant.LabelSmall)
                    }
                }
            }
        }
    }

    @Test
    fun reactions_top_light() = reactionsTop(darkTheme = false)

    @Test
    fun reactions_top_dark() = reactionsTop(darkTheme = true)
}
