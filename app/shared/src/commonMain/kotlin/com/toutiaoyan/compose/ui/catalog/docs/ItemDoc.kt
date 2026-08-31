@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.item.Item
import com.toutiaoyan.compose.ui.components.item.ItemActions
import com.toutiaoyan.compose.ui.components.item.ItemContent
import com.toutiaoyan.compose.ui.components.item.ItemDescription
import com.toutiaoyan.compose.ui.components.item.ItemFooter
import com.toutiaoyan.compose.ui.components.item.ItemGroup
import com.toutiaoyan.compose.ui.components.item.ItemHeader
import com.toutiaoyan.compose.ui.components.item.ItemMedia
import com.toutiaoyan.compose.ui.components.item.ItemMediaVariant
import com.toutiaoyan.compose.ui.components.item.ItemSeparator
import com.toutiaoyan.compose.ui.components.item.ItemSize
import com.toutiaoyan.compose.ui.components.item.ItemTitle
import com.toutiaoyan.compose.ui.components.item.ItemVariant

val itemDoc =
    ComponentDoc(
        id = "item",
        title = "Item",
        description = "A single row in a list of interactive/informational rows.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            Item(variant = ItemVariant.Outline) {
                ItemContent("Jane Doe", description = "jane@example.com")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        ItemGroup {
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia(variant = ItemMediaVariant.Icon) { EmojiText("📦") }
                                ItemContent("Order #1234", description = "Shipped on March 12")
                                ItemActions("Track")
                            }
                            ItemSeparator()
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia(variant = ItemMediaVariant.Icon) { EmojiText("📦") }
                                ItemContent("Order #1235", description = "Processing")
                                ItemActions("Track")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ItemGroup {
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia(variant = ItemMediaVariant.Icon) { EmojiText("📦") }
                                ItemContent("Order #1234", description = "Shipped on March 12")
                                ItemActions("Track")
                            }
                            ItemSeparator()
                            Item(variant = ItemVariant.Outline) {
                                ItemMedia(variant = ItemMediaVariant.Icon) { EmojiText("📦") }
                                ItemContent("Order #1235", description = "Processing")
                                ItemActions("Track")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Muted",
                    code =
                        """
                        Item(variant = ItemVariant.Muted) {
                            ItemContent("Muted item", description = "Lower emphasis row")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Muted) {
                            ItemContent("Muted item", description = "Lower emphasis row")
                        }
                    },
                ),
                ComponentExample(
                    title = "Size",
                    code =
                        """
                        Item(variant = ItemVariant.Outline, size = ItemSize.Sm) {
                            ItemContent("Small item", description = "Compact padding")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline, size = ItemSize.Sm) {
                            ItemContent("Small item", description = "Compact padding")
                        }
                    },
                ),
                ComponentExample(
                    title = "Extra small",
                    code =
                        """
                        Item(variant = ItemVariant.Outline, size = ItemSize.Xs) {
                            ItemContent("Extra small item", description = "Official size=xs.")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline, size = ItemSize.Xs) {
                            ItemContent("Extra small item", description = "Official size=xs.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default size",
                    code =
                        """
                        Item(variant = ItemVariant.Outline, size = ItemSize.Default) {
                            ItemContent("Default item", description = "Standard padding")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline, size = ItemSize.Default) {
                            ItemContent("Default item", description = "Standard padding")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        Item(variant = ItemVariant.Default) {
                            ItemContent("Default item", description = "Standard padding")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Default) {
                            ItemContent("Default item", description = "Standard padding")
                        }
                    },
                ),
                ComponentExample(
                    title = "As button",
                    code =
                        """
                        Item(
                            title = "Open settings",
                            description = "Click the row",
                            variant = ItemVariant.Outline,
                            onClick = {},
                        )
                        """.trimIndent(),
                    preview = {
                        Item(
                            title = "Open settings",
                            description = "Click the row",
                            variant = ItemVariant.Outline,
                            onClick = {},
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Item(
                            title = "Archived order",
                            description = "No longer available",
                            variant = ItemVariant.Outline,
                            disabled = true,
                        )
                        """.trimIndent(),
                    preview = {
                        Item(
                            title = "Archived order",
                            description = "No longer available",
                            variant = ItemVariant.Outline,
                            disabled = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Item(
                            title = "Archived order",
                            description = "No longer available",
                            variant = ItemVariant.Outline,
                            disabled = false,
                        )
                        """.trimIndent(),
                    preview = {
                        Item(
                            title = "Archived order",
                            description = "No longer available",
                            variant = ItemVariant.Outline,
                            disabled = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Media image",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Image) { EmojiText("🖼️") }
                            ItemContent("Cover photo", description = "40dp rounded thumbnail")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Image) { EmojiText("🖼️") }
                            ItemContent("Cover photo", description = "40dp rounded thumbnail")
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemHeader("Details")
                            ItemContent("Jane Doe", description = "jane@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemHeader("Details")
                            ItemContent("Jane Doe", description = "jane@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemContent("Jane Doe", description = "jane@example.com")
                            ItemFooter("Updated 2 hours ago")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemContent("Jane Doe", description = "jane@example.com")
                            ItemFooter("Updated 2 hours ago")
                        }
                    },
                ),
                ComponentExample(
                    title = "Actions",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemContent("Order #1234", description = "Shipped on March 12")
                            ItemActions("Track")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemContent("Order #1234", description = "Shipped on March 12")
                            ItemActions("Track")
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        ItemGroup {
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1234", description = "Shipped on March 12")
                            }
                            ItemSeparator()
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1235", description = "Processing")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ItemGroup {
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1234", description = "Shipped on March 12")
                            }
                            ItemSeparator()
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1235", description = "Processing")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemContent("Jane Doe", description = "jane@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemContent("Jane Doe", description = "jane@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemContent {
                                ItemTitle("Jane Doe")
                                ItemDescription("jane@example.com")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemContent {
                                ItemTitle("Jane Doe")
                                ItemDescription("jane@example.com")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemContent {
                                ItemTitle("Order #1234")
                                ItemDescription("Shipped on March 12")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemContent {
                                ItemTitle("Order #1234")
                                ItemDescription("Shipped on March 12")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Media",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Icon) { EmojiText("📦") }
                            ItemContent("Order #1234", description = "Shipped on March 12")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Icon) { EmojiText("📦") }
                            ItemContent("Order #1234", description = "Shipped on March 12")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default media variant",
                    code =
                        """
                        Item(variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Default) { EmojiText("📦") }
                            ItemContent("Order #1234", description = "Shipped on March 12")
                        }
                        """.trimIndent(),
                    preview = {
                        Item(variant = ItemVariant.Outline) {
                            ItemMedia(variant = ItemMediaVariant.Default) { EmojiText("📦") }
                            ItemContent("Order #1234", description = "Shipped on March 12")
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        ItemGroup {
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1234", description = "Shipped on March 12")
                            }
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1235", description = "Processing")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ItemGroup {
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1234", description = "Shipped on March 12")
                            }
                            Item(variant = ItemVariant.Outline) {
                                ItemContent("Order #1235", description = "Processing")
                            }
                        }
                    },
                ),
            ),
    )
