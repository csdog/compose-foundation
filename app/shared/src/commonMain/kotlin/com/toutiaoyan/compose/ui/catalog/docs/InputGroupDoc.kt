@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowUp
import com.composables.icons.lucide.Plus
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButton
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButtonSize
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupInput
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupText
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupTextarea
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.currentTheme

val inputGroupDoc =
    ComponentDoc(
        id = "input-group",
        title = "Input Group",
        description =
            "Addons, buttons, and helper content around an Input/Textarea — matching " +
                "shadcn input-group.tsx (InputGroup / InputGroupAddon / InputGroupButton / InputGroupText).",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
            import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon

            var amount by remember { mutableStateOf("") }
            InputGroup {
                InputGroupAddon("${'$'}")
                Input(value = amount, onValueChange = { amount = it })
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "inline-start",
                    code =
                        """
                        var amount by remember { mutableStateOf("") }
                        InputGroup(contentDescription = "Amount") {
                            InputGroupAddon("${'$'}")
                            Input(value = amount, onValueChange = { amount = it })
                        }
                        """.trimIndent(),
                    preview = {
                        var amount by remember { mutableStateOf("") }
                        InputGroup(contentDescription = "Amount") {
                            InputGroupAddon("$")
                            Input(value = amount, onValueChange = { amount = it })
                        }
                    },
                ),
                ComponentExample(
                    title = "inline-end",
                    code =
                        """
                        var domain by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = domain, onValueChange = { domain = it })
                            InputGroupAddon(".com", align = InputGroupAddonAlign.InlineEnd)
                        }
                        """.trimIndent(),
                    preview = {
                        var domain by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = domain, onValueChange = { domain = it })
                            InputGroupAddon(".com", align = InputGroupAddonAlign.InlineEnd)
                        }
                    },
                ),
                ComponentExample(
                    title = "block-end (chat composer)",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        InputGroup {
                            Input(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Ask anything...",
                                singleLine = false,
                            )
                            InputGroupAddon(InputGroupAddonAlign.BlockEnd) {
                                InputGroupButton(
                                    onClick = {},
                                    size = InputGroupButtonSize.IconSm,
                                    style = Style { shape(CircleShape) },
                                ) {
                                    DocIcon(Lucide.Plus)
                                }
                                Spacer(Modifier.weight(1f))
                                InputGroupButton(
                                    onClick = {},
                                    variant = ButtonVariant.Default,
                                    size = InputGroupButtonSize.IconSm,
                                    enabled = message.isNotBlank(),
                                    style = Style { shape(CircleShape) },
                                ) {
                                    DocIcon(Lucide.ArrowUp, tint = currentTheme.colors.primaryForeground)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        InputGroup {
                            Input(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Ask anything...",
                                singleLine = false,
                            )
                            InputGroupAddon(InputGroupAddonAlign.BlockEnd) {
                                InputGroupButton(
                                    onClick = {},
                                    size = InputGroupButtonSize.IconSm,
                                    style = Style { shape(CircleShape) },
                                ) {
                                    DocIcon(Lucide.Plus)
                                }
                                Spacer(Modifier.weight(1f))
                                InputGroupButton(
                                    onClick = {},
                                    variant = ButtonVariant.Default,
                                    size = InputGroupButtonSize.IconSm,
                                    enabled = message.isNotBlank(),
                                    style = Style { shape(CircleShape) },
                                ) {
                                    DocIcon(Lucide.ArrowUp, tint = currentTheme.colors.primaryForeground)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        InputGroup(disabled = true) {
                            InputGroupAddon("${'$'}")
                            Input(value = "20", onValueChange = {})
                        }
                        """.trimIndent(),
                    preview = {
                        InputGroup(disabled = true) {
                            InputGroupAddon("$")
                            Input(value = "20", onValueChange = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        InputGroup(disabled = false) {
                            InputGroupAddon("${'$'}")
                            Input(value = "20", onValueChange = {})
                        }
                        """.trimIndent(),
                    preview = {
                        InputGroup(disabled = false) {
                            InputGroupAddon("$")
                            Input(value = "20", onValueChange = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    code =
                        """
                        InputGroup(invalid = true) {
                            InputGroupAddon("${'$'}")
                            Input(value = "20", onValueChange = {})
                        }
                        """.trimIndent(),
                    preview = {
                        InputGroup(invalid = true) {
                            InputGroupAddon("$")
                            Input(value = "20", onValueChange = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        InputGroup(invalid = false) {
                            InputGroupAddon("${'$'}")
                            Input(value = "20", onValueChange = {})
                        }
                        """.trimIndent(),
                    preview = {
                        InputGroup(invalid = false) {
                            InputGroupAddon("$")
                            Input(value = "20", onValueChange = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Addon",
                    code =
                        """
                        var amount by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon("${'$'}")
                            Input(value = amount, onValueChange = { amount = it })
                        }
                        """.trimIndent(),
                    preview = {
                        var amount by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon("$")
                            Input(value = amount, onValueChange = { amount = it })
                        }
                    },
                ),
                ComponentExample(
                    title = "Default align",
                    code =
                        """
                        var amount by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon("${'$'}", align = InputGroupAddonAlign.InlineStart)
                            Input(value = amount, onValueChange = { amount = it })
                        }
                        """.trimIndent(),
                    preview = {
                        var amount by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon("$", align = InputGroupAddonAlign.InlineStart)
                            Input(value = amount, onValueChange = { amount = it })
                        }
                    },
                ),
                ComponentExample(
                    title = "Button",
                    code =
                        """
                        var query by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = query, onValueChange = { query = it })
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                InputGroupButton("Search", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var query by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = query, onValueChange = { query = it })
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                InputGroupButton("Search", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default button variant",
                    code =
                        """
                        var query by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = query, onValueChange = { query = it })
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                InputGroupButton("Search", onClick = {}, variant = ButtonVariant.Ghost)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var query by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = query, onValueChange = { query = it })
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                InputGroupButton("Search", onClick = {}, variant = ButtonVariant.Ghost)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default button size",
                    code =
                        """
                        var query by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = query, onValueChange = { query = it })
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                InputGroupButton("Search", onClick = {}, size = InputGroupButtonSize.Xs)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var query by remember { mutableStateOf("") }
                        InputGroup {
                            Input(value = query, onValueChange = { query = it })
                            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                InputGroupButton("Search", onClick = {}, size = InputGroupButtonSize.Xs)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Text",
                    code =
                        """
                        var url by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon {
                                InputGroupText("https://")
                            }
                            Input(value = url, onValueChange = { url = it })
                        }
                        """.trimIndent(),
                    preview = {
                        var url by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon {
                                InputGroupText("https://")
                            }
                            Input(value = url, onValueChange = { url = it })
                        }
                    },
                ),
                ComponentExample(
                    title = "Input",
                    code =
                        """
                        var amount by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon("${'$'}")
                            InputGroupInput(value = amount, onValueChange = { amount = it })
                        }
                        """.trimIndent(),
                    preview = {
                        var amount by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupAddon("$")
                            InputGroupInput(value = amount, onValueChange = { amount = it })
                        }
                    },
                ),
                ComponentExample(
                    title = "Textarea",
                    code =
                        """
                        var note by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupTextarea(value = note, onValueChange = { note = it }, placeholder = "Write a note...")
                        }
                        """.trimIndent(),
                    preview = {
                        var note by remember { mutableStateOf("") }
                        InputGroup {
                            InputGroupTextarea(
                                value = note,
                                onValueChange = { note = it },
                                placeholder = "Write a note...",
                            )
                        }
                    },
                ),
            ),
    )
