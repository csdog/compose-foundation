@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.badge.Badge
import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.text.Text

val badgeDoc =
    ComponentDoc(
        id = "badge",
        title = "Badge",
        description = "A small label used to highlight status, counts, or metadata.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.badge.Badge

            Badge("New")
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = """Badge("New")""",
                    preview = { Badge("New") },
                ),
                ComponentExample(
                    title = "Default variant",
                    code = """Badge("New", variant = BadgeVariant.Default)""",
                    preview = { Badge("New", variant = BadgeVariant.Default) },
                ),
                ComponentExample(
                    title = "Secondary",
                    code = """Badge("Beta", variant = BadgeVariant.Secondary)""",
                    preview = { Badge("Beta", variant = BadgeVariant.Secondary) },
                ),
                ComponentExample(
                    title = "Destructive",
                    code = """Badge("Error", variant = BadgeVariant.Destructive)""",
                    preview = { Badge("Error", variant = BadgeVariant.Destructive) },
                ),
                ComponentExample(
                    title = "Outline",
                    code = """Badge("Draft", variant = BadgeVariant.Outline)""",
                    preview = { Badge("Draft", variant = BadgeVariant.Outline) },
                ),
                ComponentExample(
                    title = "As link",
                    code = """Badge("Docs", onClick = { /* navigate */ })""",
                    preview = { Badge("Docs", onClick = {}) },
                ),
                ComponentExample(
                    title = "Ghost",
                    code = """Badge("Ghost", variant = BadgeVariant.Ghost)""",
                    preview = { Badge("Ghost", variant = BadgeVariant.Ghost) },
                ),
                ComponentExample(
                    title = "Link",
                    code = """Badge("Docs", variant = BadgeVariant.Link, onClick = {})""",
                    preview = { Badge("Docs", variant = BadgeVariant.Link, onClick = {}) },
                ),
                ComponentExample(
                    title = "With icon",
                    code =
                        """
                        Badge {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                EmojiText("✨")
                                Text { "New" }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Badge {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                EmojiText("✨")
                                Text { "New" }
                            }
                        }
                    },
                ),
            ),
    )
