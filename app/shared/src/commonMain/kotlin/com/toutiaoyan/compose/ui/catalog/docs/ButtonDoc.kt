@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ArrowUp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text

val buttonDoc =
    ComponentDoc(
        id = "button",
        title = "Button",
        description = "Displays a button or a component that looks like a button.",
        apiProperties =
            listOf(
                ApiProperty("onClick", "() -> Unit", "{}"),
                ApiProperty("modifier", "Modifier", "Modifier"),
                ApiProperty("enabled", "Boolean", "true"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("variant", "ButtonVariant", "Default"),
                ApiProperty("size", "ButtonSize", "Md"),
                ApiProperty("indication", "Indication?", "null"),
                ApiProperty("label", "String?", "null"),
                ApiProperty("style", "Style", "Style"),
                ApiProperty("content", "@Composable () -> Unit", "required"),
            ),
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.button.Button
            import com.toutiaoyan.compose.ui.components.text.Text

            Button("Continue", onClick = { /* ... */ })
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button("Button", variant = ButtonVariant.Outline)
                            Button(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon, label = "Submit") {
                                Icon(Lucide.ArrowUp)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button("Button", variant = ButtonVariant.Outline)
                            Button(
                                onClick = {},
                                variant = ButtonVariant.Outline,
                                size = ButtonSize.Icon,
                                label = "Submit",
                            ) {
                                Icon(Lucide.ArrowUp)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Button("Default", variant = ButtonVariant.Default)
                        """.trimIndent(),
                    preview = { Button("Default", variant = ButtonVariant.Default) },
                ),
                ComponentExample(
                    title = "Outline",
                    code =
                        """
                        Button("Outline", variant = ButtonVariant.Outline)
                        """.trimIndent(),
                    preview = { Button("Outline", variant = ButtonVariant.Outline) },
                ),
                ComponentExample(
                    title = "Secondary",
                    code =
                        """
                        Button("Secondary", variant = ButtonVariant.Secondary)
                        """.trimIndent(),
                    preview = {
                        Button("Secondary", variant = ButtonVariant.Secondary)
                    },
                ),
                ComponentExample(
                    title = "Ghost",
                    code =
                        """
                        Button("Ghost", variant = ButtonVariant.Ghost)
                        """.trimIndent(),
                    preview = { Button("Ghost", variant = ButtonVariant.Ghost) },
                ),
                ComponentExample(
                    title = "Destructive",
                    code =
                        """
                        Button("Destructive", variant = ButtonVariant.Destructive)
                        """.trimIndent(),
                    preview = {
                        Button("Destructive", variant = ButtonVariant.Destructive)
                    },
                ),
                ComponentExample(
                    title = "Link",
                    code =
                        """
                        Button("Link", variant = ButtonVariant.Link)
                        """.trimIndent(),
                    preview = { Button("Link", variant = ButtonVariant.Link) },
                ),
                ComponentExample(
                    title = "With icon",
                    code =
                        """
                        // Icon needs no `tint` here -- it reads the same ambient content
                        // color Button gives its text, so it matches automatically across
                        // variants and light/dark themes.
                        Button(onClick = {}) {
                            Icon(Lucide.Check)
                            Text { "Confirm" }
                        }
                        """.trimIndent(),
                    preview = {
                        Button(onClick = {}) {
                            Icon(Lucide.Check)
                            Text { "Confirm" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icon sizes",
                    code =
                        """
                        import com.toutiaoyan.compose.ui.components.button.ButtonSize

                        Button(onClick = {}, size = ButtonSize.IconXs) { Icon(Lucide.Check) }
                        Button(onClick = {}, size = ButtonSize.IconSm) { Icon(Lucide.Check) }
                        Button(onClick = {}, size = ButtonSize.Icon) { Icon(Lucide.Check) }
                        Button(onClick = {}, size = ButtonSize.IconLg) { Icon(Lucide.Check) }
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {}, size = ButtonSize.IconXs) { Icon(Lucide.Check) }
                            Button(onClick = {}, size = ButtonSize.IconSm) { Icon(Lucide.Check) }
                            Button(onClick = {}, size = ButtonSize.Icon) { Icon(Lucide.Check) }
                            Button(onClick = {}, size = ButtonSize.IconLg) { Icon(Lucide.Check) }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sizes",
                    code =
                        """
                        Button("Xs", size = ButtonSize.Xs)
                        Button("Sm", size = ButtonSize.Sm)
                        Button("Default", size = ButtonSize.Md)
                        Button("Lg", size = ButtonSize.Lg)
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button("Xs", size = ButtonSize.Xs)
                            Button("Sm", size = ButtonSize.Sm)
                            Button("Default", size = ButtonSize.Md)
                            Button("Lg", size = ButtonSize.Lg)
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Button("Disabled", disabled = true)
                        """.trimIndent(),
                    preview = { Button("Disabled", disabled = true) },
                ),
                ComponentExample(
                    title = "Spinner",
                    code =
                        """
                        Button(onClick = {}, disabled = true) {
                            Spinner()
                            Text { "Please wait" }
                        }
                        """.trimIndent(),
                    preview = {
                        Button(onClick = {}, disabled = true) {
                            Spinner()
                            Text { "Please wait" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        Button("Save", onClick = {}, label = "Save")
                        """.trimIndent(),
                    preview = { Button("Save", onClick = {}, label = "Save") },
                ),
            ),
    )
