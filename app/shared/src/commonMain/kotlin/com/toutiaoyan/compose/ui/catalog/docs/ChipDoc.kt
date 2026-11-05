@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.components.chip.Chip
import com.toutiaoyan.compose.ui.components.chip.ChipVariant

/**
 * Kotlin extension page. shadcn/ui ships no Chip component -- its `Badge` is a static
 * label -- so there is no official Base UI page to mirror. This page is titled and marked
 * as this catalog's own extension and documents the actual Kotlin API; it does not
 * fabricate an official comparison.
 */
val chipDoc =
    ComponentDoc(
        id = "chip",
        isKotlinExtension = true,
        title = "Chip",
        description = "A selectable tag, commonly used for filters or multi-select choices.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.chip.Chip
            """.trimIndent(),
        usageCode =
            """
            var selected by remember { mutableStateOf(false) }
            Chip(
                label = "Kotlin",
                selected = selected,
                onClick = { selected = !selected },
            )
            """.trimIndent(),
        compositionCode =
            """
            Chip
            ├── leadingIcon (optional slot)
            └── label
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    title = "Kotlin extension",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                (
                                    "Chip is a Kotlin extension of this catalog, not an official " +
                                        "shadcn/ui component. shadcn only ships `Badge`, which is a " +
                                        "static label, so there is no Base UI reference page or " +
                                        "upstream recipe list to mirror. The API below is the current " +
                                        "Kotlin source of truth: a selectable tag built from the same " +
                                        "tokens as Button/Badge."
                                ),
                            ),
                        ),
                ),
            ),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = """Chip(label = "Kotlin", onClick = {})""",
                    preview = { Chip(label = "Kotlin", onClick = {}) },
                ),
                ComponentExample(
                    title = "Selectable",
                    descriptionMarkdown =
                        "Pass `selected` and toggle it from `onClick`; the default variant follows " +
                            "the selection state.",
                    code =
                        """
                        var selected by remember { mutableStateOf(false) }
                        Chip(label = "Kotlin", selected = selected, onClick = { selected = !selected })
                        """.trimIndent(),
                    preview = {
                        var selected by remember { mutableStateOf(false) }
                        Chip(label = "Kotlin", selected = selected, onClick = { selected = !selected })
                    },
                ),
                ComponentExample(
                    title = "Selected",
                    descriptionMarkdown = "Pin the selected look with `ChipVariant.Selected`.",
                    code = """Chip(label = "Kotlin", variant = ChipVariant.Selected, onClick = {})""",
                    preview = { Chip(label = "Kotlin", variant = ChipVariant.Selected, onClick = {}) },
                ),
                ComponentExample(
                    title = "Outline",
                    descriptionMarkdown = "Border-only treatment with `ChipVariant.Outline`.",
                    code = """Chip(label = "Outline", variant = ChipVariant.Outline, onClick = {})""",
                    preview = { Chip(label = "Outline", variant = ChipVariant.Outline, onClick = {}) },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown =
                        "`disabled` (or `enabled = false`) removes the click handler and dims the tag.",
                    code = """Chip(label = "Disabled", onClick = {}, disabled = true)""",
                    preview = { Chip(label = "Disabled", onClick = {}, disabled = true) },
                ),
                ComponentExample(
                    title = "Icon",
                    descriptionMarkdown =
                        "The optional `leadingIcon` slot receives its size modifier and inherits the " +
                            "variant content color.",
                    code =
                        """
                        Chip(
                            label = "Verified",
                            onClick = {},
                            leadingIcon = { modifier -> DocIcon(Lucide.Check, modifier = modifier) },
                        )
                        """.trimIndent(),
                    preview = {
                        Chip(
                            label = "Verified",
                            onClick = {},
                            leadingIcon = { modifier -> DocIcon(Lucide.Check, modifier = modifier) },
                        )
                    },
                ),
                ComponentExample(
                    title = "Row",
                    descriptionMarkdown = "Chips compose in a row for filter groups.",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Chip(label = "Kotlin", selected = true, onClick = {})
                            Chip(label = "Compose", onClick = {})
                            Chip(label = "Multiplatform", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Chip(label = "Kotlin", selected = true, onClick = {})
                            Chip(label = "Compose", onClick = {})
                            Chip(label = "Multiplatform", onClick = {})
                        }
                    },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Chip",
                    (
                        "Selectable tag. Invoke with a label; the default variant is " +
                            "`Selected` when `selected = true`, otherwise `Default`."
                    ),
                    listOf(
                        ApiProperty("label", "String", "Required"),
                        ApiProperty("onClick", "(() -> Unit)?", "null (static tag when omitted)"),
                        ApiProperty("selected", "Boolean", "false"),
                        ApiProperty("variant", "ChipVariant", "Selected when selected, else Default"),
                        ApiProperty("enabled / disabled", "Boolean", "true / false"),
                        ApiProperty("leadingIcon", "(@Composable (Modifier) -> Unit)?", "null"),
                        ApiProperty("animation", "ButtonAnimation", "ButtonAnimation.Scale"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "ChipVariant",
                    "Content colors: Default uses `secondaryForeground`, Selected uses " +
                        "`primaryForeground`, Outline uses `foreground`.",
                    listOf(
                        ApiProperty("Default", "ChipVariant", "Secondary fill with a border"),
                        ApiProperty("Selected", "ChipVariant", "Primary fill"),
                        ApiProperty("Outline", "ChipVariant", "Transparent with a border"),
                    ),
                ),
            ),
    )
