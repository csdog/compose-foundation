@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.kbd.Kbd
import com.toutiaoyan.compose.ui.components.kbd.KbdGroup
import com.toutiaoyan.compose.ui.components.text.Text

val kbdDoc =
    ComponentDoc(
        id = "kbd",
        title = "Kbd",
        description = "A keyboard-shortcut label.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.kbd.Kbd

            Kbd("⌘K")
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        KbdGroup {
                            Kbd("Ctrl")
                            Kbd("K")
                        }
                        """.trimIndent(),
                    preview = {
                        KbdGroup {
                            Kbd("Ctrl")
                            Kbd("K")
                        }
                    },
                ),
                ComponentExample(
                    title = "Single",
                    code = "Kbd(\"⌘K\")",
                    preview = { Kbd("⌘K") },
                ),
                ComponentExample(
                    title = "In Button",
                    code =
                        """
                        Button(variant = ButtonVariant.Outline) {
                            Text { "Search" }
                            Kbd("⌘K")
                        }
                        """.trimIndent(),
                    preview = {
                        Button(variant = ButtonVariant.Outline) {
                            Text { "Search" }
                            Kbd("⌘K")
                        }
                    },
                ),
            ),
    )
