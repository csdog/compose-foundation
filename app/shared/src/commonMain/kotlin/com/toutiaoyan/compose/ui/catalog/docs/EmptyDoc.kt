@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.empty.Empty
import com.toutiaoyan.compose.ui.components.empty.EmptyContent
import com.toutiaoyan.compose.ui.components.empty.EmptyContentDefaults
import com.toutiaoyan.compose.ui.components.empty.EmptyDefaults
import com.toutiaoyan.compose.ui.components.empty.EmptyDescription
import com.toutiaoyan.compose.ui.components.empty.EmptyHeader
import com.toutiaoyan.compose.ui.components.empty.EmptyMedia
import com.toutiaoyan.compose.ui.components.empty.EmptyMediaDefaults
import com.toutiaoyan.compose.ui.components.empty.EmptyMediaVariant
import com.toutiaoyan.compose.ui.components.empty.EmptyTitle
import com.toutiaoyan.compose.ui.components.empty.EmptyTitleDefaults
import com.toutiaoyan.compose.ui.components.empty.EmptyVariant

val emptyDoc =
    ComponentDoc(
        id = "empty",
        title = "Empty",
        description = "A centered placeholder for an empty list, search result, or state.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            Empty("No results", description = "Try a different search term.", media = { EmojiText("📭") })
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Empty {
                            EmptyHeader(
                                "No results found",
                                description = "Try adjusting your search or filters.",
                                media = { EmojiText("📭") },
                            )
                            EmptyContent {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader(
                                "No results found",
                                description = "Try adjusting your search or filters.",
                                media = { EmojiText("📭") },
                            )
                            EmptyContent {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Outline",
                    code =
                        """
                        Empty(
                            "No projects yet",
                            description = "Get started by creating a new project.",
                            media = { EmojiText("📁") },
                            variant = EmptyVariant.Outline,
                        )
                        """.trimIndent(),
                    preview = {
                        Empty(
                            "No projects yet",
                            description = "Get started by creating a new project.",
                            media = { EmojiText("📁") },
                            variant = EmptyVariant.Outline,
                        )
                    },
                ),
                ComponentExample(
                    title = "Root spacing (gap-4)",
                    code =
                        """
                        Empty(spacing = EmptyDefaults.Spacing) {
                            EmptyHeader {
                                EmptyMedia(size = EmptyMediaDefaults.OfficialIconSize) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                            EmptyContent {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty(spacing = EmptyDefaults.Spacing) {
                            EmptyHeader {
                                EmptyMedia(size = EmptyMediaDefaults.OfficialIconSize) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                            EmptyContent {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content spacing",
                    code =
                        """
                        Empty {
                            EmptyHeader("No results found", description = "Try adjusting your search or filters.")
                            EmptyContent(spacing = EmptyContentDefaults.Spacing) {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                                Button("Browse all", onClick = {}, variant = ButtonVariant.Ghost)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader("No results found", description = "Try adjusting your search or filters.")
                            EmptyContent(spacing = EmptyContentDefaults.Spacing) {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                                Button("Browse all", onClick = {}, variant = ButtonVariant.Ghost)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title (text-sm)",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found", variant = EmptyTitleDefaults.Official)
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found", variant = EmptyTitleDefaults.Official)
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Large padding",
                    code =
                        """
                        Empty(padding = EmptyDefaults.MdPadding) {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty(padding = EmptyDefaults.MdPadding) {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Radius (rounded-xl)",
                    code =
                        """
                        Empty(radius = EmptyDefaults.Radius, variant = EmptyVariant.Outline) {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty(radius = EmptyDefaults.Radius, variant = EmptyVariant.Outline) {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Media variant",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaDefaults.Variant) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaDefaults.Variant) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Media icon",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default media variant",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Empty {
                            EmptyHeader("No results found", description = "Try adjusting your search or filters.")
                            EmptyContent {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader("No results found", description = "Try adjusting your search or filters.")
                            EmptyContent {
                                Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Media",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmojiText("📭") }
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Empty {
                            EmptyHeader {
                                EmptyTitle("No results found")
                                EmptyDescription("Try adjusting your search or filters.")
                            }
                        }
                    },
                ),
            ),
    )
