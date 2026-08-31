@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.chip.Chip
import com.toutiaoyan.compose.ui.components.chip.ChipVariant

val chipDoc =
    ComponentDoc(
        id = "chip",
        title = "Chip",
        description = "A selectable tag, commonly used for filters or multi-select choices.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.chip.Chip

            var selected by remember { mutableStateOf(false) }
            Chip(label = "Kotlin", selected = selected, onClick = { selected = !selected })
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = """Chip(label = "Kotlin", onClick = {})""",
                    preview = { Chip(label = "Kotlin", onClick = {}) },
                ),
                ComponentExample(
                    title = "Default variant",
                    code = """Chip(label = "Kotlin", variant = ChipVariant.Default, onClick = {})""",
                    preview = { Chip(label = "Kotlin", variant = ChipVariant.Default, onClick = {}) },
                ),
                ComponentExample(
                    title = "Selectable",
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
                    code = """Chip(label = "Kotlin", variant = ChipVariant.Selected, onClick = {})""",
                    preview = { Chip(label = "Kotlin", variant = ChipVariant.Selected, onClick = {}) },
                ),
                ComponentExample(
                    title = "Default selected",
                    code = """Chip(label = "Kotlin", selected = false, onClick = {})""",
                    preview = { Chip(label = "Kotlin", selected = false, onClick = {}) },
                ),
                ComponentExample(
                    title = "Outline",
                    code = """Chip(label = "Outline", variant = ChipVariant.Outline, onClick = {})""",
                    preview = { Chip(label = "Outline", variant = ChipVariant.Outline, onClick = {}) },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = """Chip(label = "Disabled", onClick = {}, disabled = true)""",
                    preview = { Chip(label = "Disabled", onClick = {}, disabled = true) },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code = """Chip(label = "Default", onClick = {}, disabled = false)""",
                    preview = { Chip(label = "Default", onClick = {}, disabled = false) },
                ),
                ComponentExample(
                    title = "Icon",
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
                    title = "Default leading icon",
                    code = """Chip(label = "Kotlin", onClick = {}, leadingIcon = null)""",
                    preview = { Chip(label = "Kotlin", onClick = {}, leadingIcon = null) },
                ),
            ),
    )
