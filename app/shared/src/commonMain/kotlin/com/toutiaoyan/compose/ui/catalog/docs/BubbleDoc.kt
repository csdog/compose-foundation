@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleContent
import com.toutiaoyan.compose.ui.components.bubble.BubbleGroup
import com.toutiaoyan.compose.ui.components.bubble.BubbleReactions
import com.toutiaoyan.compose.ui.components.bubble.BubbleReactionsSide
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors

val bubbleDoc =
    ComponentDoc(
        id = "bubble",
        title = "Bubble",
        description = "A single chat message bubble, self-aligned to the left or right of the transcript.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            BubbleGroup {
                Bubble("Hey, how's it going?", align = MessageAlign.End)
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Variants",
                    code =
                        """
                        BubbleGroup {
                            Bubble("Default")
                            Bubble("Secondary", variant = BubbleVariant.Secondary)
                            Bubble("Muted", variant = BubbleVariant.Muted)
                            Bubble("Outline", variant = BubbleVariant.Outline)
                            Bubble("Ghost", variant = BubbleVariant.Ghost)
                            Bubble("Destructive", variant = BubbleVariant.Destructive)
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble("Default")
                            Bubble("Secondary", variant = BubbleVariant.Secondary)
                            Bubble("Muted", variant = BubbleVariant.Muted)
                            Bubble("Outline", variant = BubbleVariant.Outline)
                            Bubble("Ghost", variant = BubbleVariant.Ghost)
                            Bubble("Destructive", variant = BubbleVariant.Destructive)
                        }
                    },
                ),
                ComponentExample(
                    title = "Tinted",
                    code =
                        """
                        BubbleGroup {
                            Bubble("Tinted", variant = BubbleVariant.Tinted)
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble("Tinted", variant = BubbleVariant.Tinted)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        BubbleGroup {
                            Bubble("Hello", variant = BubbleVariant.Default)
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble("Hello", variant = BubbleVariant.Default)
                        }
                    },
                ),
                ComponentExample(
                    title = "Conversation alignment",
                    code =
                        """
                        BubbleGroup {
                            Bubble("Hi, how can I help?", variant = BubbleVariant.Muted, align = MessageAlign.Start)
                            Bubble("Summarize this for me.", align = MessageAlign.End)
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble("Hi, how can I help?", variant = BubbleVariant.Muted, align = MessageAlign.Start)
                            Bubble("Summarize this for me.", align = MessageAlign.End)
                        }
                    },
                ),
                ComponentExample(
                    title = "Conversation with reactions",
                    code =
                        """
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hey! Want to see chat bubbles?", variant = BubbleVariant.Muted)
                            }
                            Bubble {
                                BubbleContent(
                                    "I can group messages, switch sides, and keep the whole thread easy to scan.",
                                    variant = BubbleVariant.Muted,
                                )
                                BubbleReactions { EmojiText("👍", typography = TextTypography.LabelSmall) }
                            }
                            Bubble("Sure. Hit me with your best demo.", align = MessageAlign.End)
                            Bubble {
                                BubbleContent(
                                    "Yes. You are reading a demo that is demoing itself. Very meta. Very on-brand.",
                                    variant = BubbleVariant.Muted,
                                )
                                BubbleReactions {
                                    EmojiText("👍", typography = TextTypography.LabelSmall)
                                    EmojiText("🔥", typography = TextTypography.LabelSmall)
                                    Text(typography = TextTypography.LabelSmall, textStyle = TextStyles.Muted) { "+2" }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hey! Want to see chat bubbles?", variant = BubbleVariant.Muted)
                            }
                            Bubble {
                                BubbleContent(
                                    "I can group messages, switch sides, and keep the whole thread easy to scan.",
                                    variant = BubbleVariant.Muted,
                                )
                                BubbleReactions { EmojiText("👍", typography = TextVariant.LabelSmall) }
                            }
                            Bubble("Sure. Hit me with your best demo.", align = MessageAlign.End)
                            Bubble {
                                BubbleContent(
                                    "Yes. You are reading a demo that is demoing itself. Very meta. Very on-brand.",
                                    variant = BubbleVariant.Muted,
                                )
                                BubbleReactions {
                                    EmojiText("👍", typography = TextVariant.LabelSmall)
                                    EmojiText("🔥", typography = TextVariant.LabelSmall)
                                    Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "+2" }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hello")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hello")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        BubbleGroup {
                            Bubble("Hello")
                            Bubble("Hi there", align = MessageAlign.End)
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble("Hello")
                            Bubble("Hi there", align = MessageAlign.End)
                        }
                    },
                ),
                ComponentExample(
                    title = "Reactions",
                    code =
                        """
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hello")
                                BubbleReactions { EmojiText("👍", typography = TextVariant.LabelSmall) }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hello")
                                BubbleReactions { EmojiText("👍", typography = TextVariant.LabelSmall) }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Reactions top",
                    code =
                        """
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hello")
                                BubbleReactions(side = BubbleReactionsSide.Top) {
                                    EmojiText("👍", typography = TextVariant.LabelSmall)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        BubbleGroup {
                            Bubble {
                                BubbleContent("Hello")
                                BubbleReactions(side = BubbleReactionsSide.Top) {
                                    EmojiText("👍", typography = TextVariant.LabelSmall)
                                }
                            }
                        }
                    },
                ),
            ),
    )
