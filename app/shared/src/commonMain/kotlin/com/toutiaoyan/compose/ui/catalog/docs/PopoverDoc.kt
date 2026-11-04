@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.popover.Popover
import com.toutiaoyan.compose.ui.components.popover.PopoverContent
import com.toutiaoyan.compose.ui.components.popover.PopoverDescription
import com.toutiaoyan.compose.ui.components.popover.PopoverHeader
import com.toutiaoyan.compose.ui.components.popover.PopoverTitle
import com.toutiaoyan.compose.ui.components.popover.PopoverTrigger

val popoverDoc =
    ComponentDoc(
        id = "popover",
        title = "Popover",
        description = "Displays rich content in a portal, triggered by a button.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.popover.Popover
            import com.toutiaoyan.compose.ui.components.popover.PopoverContent
            import com.toutiaoyan.compose.ui.components.popover.PopoverTrigger
            import com.toutiaoyan.compose.ui.components.popover.PopoverHeader
            import com.toutiaoyan.compose.ui.components.popover.PopoverTitle
            import com.toutiaoyan.compose.ui.components.popover.PopoverDescription
            import com.toutiaoyan.compose.ui.components.button.ButtonVariant

            Popover {
                PopoverTrigger("Open Popover", variant = ButtonVariant.Outline)
                PopoverContent {
                    PopoverHeader {
                        PopoverTitle("Title")
                        PopoverDescription("Description text here.")
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Dimensions form",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var width by remember { mutableStateOf("100%") }
                        var height by remember { mutableStateOf("25px") }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Dimensions", description = "Set the dimensions for the layer.") {
                                FieldGroup {
                                    Field("Width") {
                                        Input(value = width, onValueChange = { width = it })
                                    }
                                    Field("Height") {
                                        Input(value = height, onValueChange = { height = it })
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var width by remember { mutableStateOf("100%") }
                        var height by remember { mutableStateOf("25px") }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Dimensions", description = "Set the dimensions for the layer.") {
                                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                                    FieldGroup {
                                        Field("Width") {
                                            Input(value = width, onValueChange = { width = it })
                                        }
                                        Field("Height") {
                                            Input(value = height, onValueChange = { height = it })
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Popover {
                                PopoverTrigger("Top", variant = ButtonVariant.Outline)
                                PopoverContent("Top", placement = PopupPlacement.Top)
                            }
                            Popover {
                                PopoverTrigger("Right", variant = ButtonVariant.Outline)
                                PopoverContent("Right", placement = PopupPlacement.End)
                            }
                            Popover {
                                PopoverTrigger("Bottom", variant = ButtonVariant.Outline)
                                PopoverContent("Bottom", placement = PopupPlacement.Bottom)
                            }
                            Popover {
                                PopoverTrigger("Left", variant = ButtonVariant.Outline)
                                PopoverContent("Left", placement = PopupPlacement.Start)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Popover {
                                PopoverTrigger("Top", variant = ButtonVariant.Outline)
                                PopoverContent("Top", placement = PopupPlacement.Top)
                            }
                            Popover {
                                PopoverTrigger("Right", variant = ButtonVariant.Outline)
                                PopoverContent("Right", placement = PopupPlacement.End)
                            }
                            Popover {
                                PopoverTrigger("Bottom", variant = ButtonVariant.Outline)
                                PopoverContent("Bottom", placement = PopupPlacement.Bottom)
                            }
                            Popover {
                                PopoverTrigger("Left", variant = ButtonVariant.Outline)
                                PopoverContent("Left", placement = PopupPlacement.Start)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }, modal = false) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Page stays interactive while this is open.")
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }, modal = false) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Page stays interactive while this is open.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }, modal = true) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("The page behind is inert.")
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }, modal = true) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("The page behind is inert.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }, modal = true) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("The page behind is inert.")
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }, modal = true) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("The page behind is inert.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-dismissible",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent(
                                "Clicking outside will not close this popover.",
                                dismissOnClickOutside = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent(
                                "Clicking outside will not close this popover.",
                                dismissOnClickOutside = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent(
                                "Clicking outside will close this popover.",
                                dismissOnClickOutside = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent(
                                "Clicking outside will close this popover.",
                                dismissOnClickOutside = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Placed 12dp from the trigger.", sideOffset = 12.dp)
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Placed 12dp from the trigger.", sideOffset = 12.dp)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Placed 4dp from the trigger.", sideOffset = 4.dp)
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Placed 4dp from the trigger.", sideOffset = 4.dp)
                        }
                    },
                ),
                ComponentExample(
                    title = "Official side prop",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Above the trigger", side = PopupPlacement.Top)
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Above the trigger", side = PopupPlacement.Top)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Below the trigger", side = PopupPlacement.Bottom)
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Below the trigger", side = PopupPlacement.Bottom)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placement",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Below the trigger", placement = PopupPlacement.Bottom)
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Below the trigger", placement = PopupPlacement.Bottom)
                        }
                    },
                ),
                ComponentExample(
                    title = "Placement",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Above the trigger", placement = PopupPlacement.Top)
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Above the trigger", placement = PopupPlacement.Top)
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent(
                                "Notice",
                                description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                            ) {}
                        }
                        """.trimIndent(),
                    preview = {
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent(
                                "Notice",
                                description = "Omit open and onOpenChange. Optional defaultOpen starts closed.",
                            ) {}
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        Popover(defaultOpen = false) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Notice", description = "Starts closed.") {}
                        }
                        """.trimIndent(),
                    preview = {
                        Popover(defaultOpen = false) {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent("Notice", description = "Starts closed.") {}
                        }
                    },
                ),
                ComponentExample(
                    title = "Title and description",
                    code =
                        """
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("Compose Title and Description inside Header.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("PopoverDescription matches shadcn's description slot.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("PopoverDescription matches shadcn's description slot.")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("PopoverContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Popover {
                            PopoverTrigger("Open popover", variant = ButtonVariant.Outline)
                            PopoverContent {
                                PopoverHeader {
                                    PopoverTitle("Notice")
                                    PopoverDescription("PopoverContent hosts Header, Title, and Description.")
                                }
                            }
                        }
                    },
                ),
            ),
    )
        .let { native -> native.copy(examples = popoverOfficialExamples + native.examples) }
