@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroup
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupItem
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupOrientation
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupSeparator
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupText
import com.toutiaoyan.compose.ui.components.buttongroup.rememberButtonGroupOutlineStyle

import androidx.compose.foundation.style.contentPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ChevronDown
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.dropdownmenu.*

val buttonGroupDoc =
    ComponentDoc(
        id = "button-group",
        title = "Button Group",
        description = "A container that groups related buttons together with consistent styling.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroup
            import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupItem

            ButtonGroup(contentDescription = "Button group") {
                ButtonGroupItem("Archive", onClick = {})
                ButtonGroupItem("Report", onClick = {})
                ButtonGroupItem("Snooze", onClick = {})
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("orientation", "ButtonGroupOrientation", "Horizontal"),
                ApiProperty("spacing", "Dp", "0.dp"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("contentDescription", "String?", "null"),
                ApiProperty("ButtonGroupItem.variant", "ButtonVariant", "Ghost"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        ButtonGroup(spacing = 8.dp, contentDescription = "Message actions") {
                            if (
                                LocalWindowInfo.current.containerSize.width >=
                                with(LocalDensity.current) { 640.dp.roundToPx() }
                            ) {
                                ButtonGroup {
                                    ButtonGroupItem(onClick = {}, variant = ButtonVariant.Outline, modifier = Modifier.semantics { contentDescription = "Go Back" }, style = Style { width(32.dp); contentPadding(0.dp) }) { Icon(Lucide.ArrowLeft) }
                                }
                            }
                            ButtonGroup {
                                ButtonGroupItem("Archive", onClick = {}, variant = ButtonVariant.Outline)
                                ButtonGroupItem("Report", onClick = {}, variant = ButtonVariant.Outline)
                            }
                            ButtonGroup {
                                ButtonGroupItem("Snooze", onClick = {}, variant = ButtonVariant.Outline)
                                DropdownMenu {
                                    DropdownMenuTrigger {
                                        ButtonGroupItem(onClick = toggle, variant = ButtonVariant.Outline, modifier = Modifier.semantics { contentDescription = "More Options" }, style = Style { width(32.dp); contentPadding(0.dp) }) { Icon(Lucide.ChevronDown) }
                                    }
                                    DropdownMenuContent {
                                        DropdownMenuItem("Mark as Read", onClick = {})
                                        DropdownMenuItem("Archive", onClick = {})
                                        DropdownMenuSeparator()
                                        DropdownMenuItem("Snooze", onClick = {})
                                        DropdownMenuItem("Add to Calendar", onClick = {})
                                        DropdownMenuItem("Add to List", onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Label As...")
                                            DropdownMenuSubContent {
                                                DropdownMenuItem("Personal", onClick = {})
                                                DropdownMenuItem("Work", onClick = {})
                                            }
                                        }
                                        DropdownMenuSeparator()
                                        DropdownMenuItem("Trash", onClick = {}, variant = DropdownMenuItemVariant.Destructive)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(spacing = 8.dp, contentDescription = "Message actions") {
                            if (
                                LocalWindowInfo.current.containerSize.width >=
                                with(LocalDensity.current) { 640.dp.roundToPx() }
                            ) {
                                ButtonGroup {
                                    ButtonGroupItem(
                                        onClick = {},
                                        variant = ButtonVariant.Outline,
                                        modifier = Modifier.semantics { contentDescription = "Go Back" },
                                        style =
                                            Style {
                                                width(32.dp)
                                                contentPadding(0.dp)
                                            },
                                    ) { Icon(Lucide.ArrowLeft) }
                                }
                            }
                            ButtonGroup {
                                ButtonGroupItem("Archive", onClick = {}, variant = ButtonVariant.Outline)
                                ButtonGroupItem("Report", onClick = {}, variant = ButtonVariant.Outline)
                            }
                            ButtonGroup {
                                ButtonGroupItem("Snooze", onClick = {}, variant = ButtonVariant.Outline)
                                DropdownMenu {
                                    DropdownMenuTrigger {
                                        ButtonGroupItem(
                                            onClick = toggle,
                                            variant = ButtonVariant.Outline,
                                            modifier =
                                                Modifier.semantics {
                                                    contentDescription = "More Options"
                                                },
                                            style =
                                                Style {
                                                    width(32.dp)
                                                    contentPadding(0.dp)
                                                },
                                        ) { Icon(Lucide.ChevronDown) }
                                    }
                                    DropdownMenuContent {
                                        DropdownMenuItem("Mark as Read", onClick = {})
                                        DropdownMenuItem("Archive", onClick = {})
                                        DropdownMenuSeparator()
                                        DropdownMenuItem("Snooze", onClick = {})
                                        DropdownMenuItem("Add to Calendar", onClick = {})
                                        DropdownMenuItem("Add to List", onClick = {})
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Label As...")
                                            DropdownMenuSubContent {
                                                DropdownMenuItem("Personal", onClick = {})
                                                DropdownMenuItem("Work", onClick = {})
                                            }
                                        }
                                        DropdownMenuSeparator()
                                        DropdownMenuItem(
                                            "Trash",
                                            onClick = {},
                                            variant = DropdownMenuItemVariant.Destructive,
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Outline",
                    code =
                        """
                        ButtonGroup {
                            ButtonGroupItem("Copy", variant = ButtonVariant.Outline, onClick = {})
                            ButtonGroupItem("Share", variant = ButtonVariant.Outline, onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup {
                            ButtonGroupItem("Copy", variant = ButtonVariant.Outline, onClick = {})
                            ButtonGroupItem("Share", variant = ButtonVariant.Outline, onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "With label",
                    code =
                        """
                        val rounded = TwRadius.md
                        ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                            ButtonGroupText("https://", topStart = rounded, bottomStart = rounded)
                            ButtonGroupSeparator()
                            Button(
                                "example.com",
                                onClick = {},
                                variant = ButtonVariant.Ghost,
                                style = Style {
                                    shape(RoundedCornerShape(0.dp, rounded, rounded, 0.dp))
                                }
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        val rounded = twRadius.md
                        ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                            ButtonGroupText("https://", topStart = rounded, bottomStart = rounded)
                            ButtonGroupSeparator()
                            Button(
                                "example.com",
                                onClick = {},
                                variant = ButtonVariant.Ghost,
                                style =
                                    Style {
                                        shape(RoundedCornerShape(0.dp, rounded, rounded, 0.dp))
                                    },
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        ButtonGroup(orientation = ButtonGroupOrientation.Vertical) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(orientation = ButtonGroupOrientation.Vertical) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        ButtonGroup(orientation = ButtonGroupOrientation.Horizontal) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(orientation = ButtonGroupOrientation.Horizontal) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        ButtonGroup(orientation = ButtonGroupOrientation.Horizontal) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(orientation = ButtonGroupOrientation.Horizontal) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Spacing",
                    code =
                        """
                        ButtonGroup(spacing = 8.dp) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(spacing = 8.dp) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Default spacing",
                    code =
                        """
                        ButtonGroup(spacing = 0.dp) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(spacing = 0.dp) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        ButtonGroup(disabled = true) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(disabled = true) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        ButtonGroup(disabled = false) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup(disabled = false) {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        ButtonGroup {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupSeparator()
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupSeparator()
                            ButtonGroupItem("Snooze", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Text",
                    code =
                        """
                        ButtonGroup {
                            ButtonGroupText("https://")
                            ButtonGroupItem("Copy", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup {
                            ButtonGroupText("https://")
                            ButtonGroupItem("Copy", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        ButtonGroup {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        ButtonGroup {
                            ButtonGroupItem("Archive", onClick = {})
                            ButtonGroupItem("Report", onClick = {})
                        }
                    },
                ),
            ),
    )
