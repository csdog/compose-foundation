@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.separator.SeparatorOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors

val separatorDoc =
    ComponentDoc(
        id = "separator",
        title = "Separator",
        description = "Visually or semantically separates content.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.separator.Separator

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text { "Content above" }
                Separator()
                Text { "Content below" }
            }
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Compose layout and semantics",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "For vertical separators in content-sized rows, use " +
                                    "`Modifier.height(IntrinsicSize.Min)` on the Row. "
                            ) +
                                (
                                    "Separators are semantic by default; pass `decorative = true` " +
                                        "for a visual-only line. "
                                ) +
                                (
                                    "Native Compose exposes a description and orientation, but " +
                                        "does not currently provide the HTML separator role."
                                ),
                        ),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Horizontal, with spacing",
                    code =
                        """
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(variant = TextVariant.LabelLarge) { "Radix Primitives" }
                                Text(textStyle = TextColors.Muted) { "An open-source UI component library." }
                            }
                            Separator()
                            Text { "Unstyled, accessible components for building high-quality design systems." }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(variant = TextVariant.LabelLarge) { "Radix Primitives" }
                                Text(textStyle = TextColors.Muted) { "An open-source UI component library." }
                            }
                            Separator()
                            Text { "Unstyled, accessible components for building high-quality design systems." }
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code = """Separator(orientation = SeparatorOrientation.Horizontal)""",
                    preview = { Separator(orientation = SeparatorOrientation.Horizontal) },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code = """Separator(orientation = SeparatorOrientation.Horizontal)""",
                    preview = { Separator(orientation = SeparatorOrientation.Horizontal) },
                ),
                ComponentExample(
                    title = "Vertical (Kotlin)",
                    code = """Separator(orientation = SeparatorOrientation.Vertical)""",
                    preview = {
                        Row(modifier = Modifier.height(20.dp)) {
                            Separator(orientation = SeparatorOrientation.Vertical)
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical, with spacing",
                    code =
                        """
                        Row(
                            modifier = Modifier.height(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text { "Blog" }
                            Separator(orientation = SeparatorOrientation.Vertical)
                            Text { "Docs" }
                            Separator(orientation = SeparatorOrientation.Vertical)
                            Text { "Source" }
                        }
                        """.trimIndent(),
                    preview = {
                        Row(
                            modifier = Modifier.height(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text { "Blog" }
                            Separator(orientation = SeparatorOrientation.Vertical)
                            Text { "Docs" }
                            Separator(orientation = SeparatorOrientation.Vertical)
                            Text { "Source" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Bare (no spacing)",
                    code = "Separator()",
                    preview = { Separator() },
                ),
                ComponentExample(
                    title = "Decorative",
                    code = """Separator(decorative = true)""",
                    preview = { Separator(decorative = true) },
                ),
                ComponentExample(
                    title = "Default semantics (Kotlin)",
                    code = """Separator()""",
                    preview = { Separator() },
                ),
                ComponentExample(
                    title = "With text",
                    code =
                        """
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Separator(modifier = Modifier.weight(1f))
                            Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "or" }
                            Separator(modifier = Modifier.weight(1f))
                        }
                        """.trimIndent(),
                    preview = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Separator(modifier = Modifier.weight(1f))
                            Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "or" }
                            Separator(modifier = Modifier.weight(1f))
                        }
                    },
                ),
                ComponentExample(
                    title = "Semantic",
                    code = """Separator(decorative = false)""",
                    preview = { Separator(decorative = false) },
                ),
            ),
    )
        .let { native -> native.copy(examples = separatorOfficialExamples + native.examples) }
