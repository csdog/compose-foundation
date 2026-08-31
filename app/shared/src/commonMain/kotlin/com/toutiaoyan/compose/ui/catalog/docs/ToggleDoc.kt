@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.toggle.Toggle
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.text.Text

val toggleDoc =
    ComponentDoc(
        id = "toggle",
        title = "Toggle",
        description = "A two-state button, commonly used for toolbar toggles like bold or italic.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.toggle.Toggle

            var bold by remember { mutableStateOf(false) }
            Toggle("B", pressed = bold, onPressedChange = { bold = it })
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var bold by remember { mutableStateOf(false) }
                        Toggle("Bold", pressed = bold, onPressedChange = { bold = it })
                        """.trimIndent(),
                    preview = {
                        var bold by remember { mutableStateOf(false) }
                        Toggle("Bold", pressed = bold, onPressedChange = { bold = it })
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        var bold by remember { mutableStateOf(false) }
                        Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, variant = ToggleVariant.Default)
                        """.trimIndent(),
                    preview = {
                        var bold by remember { mutableStateOf(false) }
                        Toggle(
                            "Bold",
                            pressed = bold,
                            onPressedChange = { bold = it },
                            variant = ToggleVariant.Default,
                        )
                    },
                ),
                ComponentExample(
                    title = "Outline",
                    code =
                        """
                        var italic by remember { mutableStateOf(false) }
                        Toggle("Italic", pressed = italic, onPressedChange = { italic = it }, variant = ToggleVariant.Outline)
                        """.trimIndent(),
                    preview = {
                        var italic by remember { mutableStateOf(false) }
                        Toggle(
                            "Italic",
                            pressed = italic,
                            onPressedChange = { italic = it },
                            variant = ToggleVariant.Outline,
                        )
                    },
                ),
                ComponentExample(
                    title = "Sizes",
                    code =
                        """
                        Toggle("Sm", pressed = true, onPressedChange = {}, size = ToggleSize.Sm)
                        Toggle("Md", pressed = true, onPressedChange = {})
                        Toggle("Lg", pressed = true, onPressedChange = {}, size = ToggleSize.Lg)
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Toggle("Sm", pressed = true, onPressedChange = {}, size = ToggleSize.Sm)
                            Toggle("Md", pressed = true, onPressedChange = {})
                            Toggle("Lg", pressed = true, onPressedChange = {}, size = ToggleSize.Lg)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default size",
                    code =
                        """
                        var bold by remember { mutableStateOf(false) }
                        Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, size = ToggleSize.Md)
                        """.trimIndent(),
                    preview = {
                        var bold by remember { mutableStateOf(false) }
                        Toggle(
                            "Bold",
                            pressed = bold,
                            onPressedChange = { bold = it },
                            size = ToggleSize.Md,
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = """Toggle("Bold", pressed = true, onPressedChange = null, disabled = true)""",
                    preview = { Toggle("Bold", pressed = true, onPressedChange = null, disabled = true) },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code = """Toggle("Bold", pressed = false, onPressedChange = {}, disabled = false)""",
                    preview = { Toggle("Bold", pressed = false, onPressedChange = {}, disabled = false) },
                ),
                ComponentExample(
                    title = "With text",
                    code =
                        """
                        var italic by remember { mutableStateOf(false) }
                        Toggle(pressed = italic, onPressedChange = { italic = it }, variant = ToggleVariant.Outline) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                EmojiText("𝐼")
                                Text { "Italic" }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var italic by remember { mutableStateOf(false) }
                        Toggle(
                            pressed = italic,
                            onPressedChange = { italic = it },
                            variant = ToggleVariant.Outline,
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                EmojiText("𝐼")
                                Text { "Italic" }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Toggle("Bold", defaultPressed = true)
                        """.trimIndent(),
                    preview = { Toggle("Bold", defaultPressed = true) },
                ),
                ComponentExample(
                    title = "Default pressed",
                    code =
                        """
                        Toggle("Bold", defaultPressed = false)
                        """.trimIndent(),
                    preview = { Toggle("Bold", defaultPressed = false) },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        var bold by remember { mutableStateOf(false) }
                        Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, label = "Bold")
                        """.trimIndent(),
                    preview = {
                        var bold by remember { mutableStateOf(false) }
                        Toggle("Bold", pressed = bold, onPressedChange = { bold = it }, label = "Bold")
                    },
                ),
                ComponentExample(
                    title = "Default label",
                    code = """Toggle("Bold", defaultPressed = false, label = "Bold")""",
                    preview = { Toggle("Bold", defaultPressed = false, label = "Bold") },
                ),
            ),
    )
