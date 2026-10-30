@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Check
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.badge.Badge
import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
import com.toutiaoyan.compose.ui.components.text.Text

val badgeDoc =
    ComponentDoc(
        id = "badge",
        title = "Badge",
        description = "Displays a badge or a component that looks like a badge.",
        apiProperties =
            listOf(
                ApiProperty("modifier", "Modifier", "Modifier"),
                ApiProperty("variant", "BadgeVariant", "Default"),
                ApiProperty("onClick", "(() -> Unit)?", "null"),
                ApiProperty("style", "Style", "Style"),
                ApiProperty("content", "@Composable () -> Unit", "required"),
            ),
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.badge.Badge

            Badge("New")
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge("Badge")
                            Badge("Secondary", variant = BadgeVariant.Secondary)
                            Badge("Destructive", variant = BadgeVariant.Destructive)
                            Badge("Outline", variant = BadgeVariant.Outline)
                        }
                        """.trimIndent(),
                    preview = {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Badge("Badge")
                            Badge("Secondary", variant = BadgeVariant.Secondary)
                            Badge("Destructive", variant = BadgeVariant.Destructive)
                            Badge("Outline", variant = BadgeVariant.Outline)
                        }
                    },
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
                    title = "With Icon",
                    code =
                        """
                        Badge {
                            Icon(Lucide.Check)
                            Text { "Verified" }
                        }
                        """.trimIndent(),
                    preview = {
                        Badge {
                            Icon(Lucide.Check)
                            Text { "Verified" }
                        }
                    },
                ),
                ComponentExample(
                    title = "With Spinner",
                    code = """Badge(variant = BadgeVariant.Secondary) { Spinner(); Text { "Loading" } }""",
                    preview = {
                        Badge(variant = BadgeVariant.Secondary) {
                            Spinner()
                            Text { "Loading" }
                        }
                    },
                ),
            ),
    )
