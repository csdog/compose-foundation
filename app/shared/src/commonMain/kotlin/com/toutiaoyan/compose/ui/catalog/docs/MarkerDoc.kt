@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.marker.Marker
import com.toutiaoyan.compose.ui.components.marker.MarkerContent
import com.toutiaoyan.compose.ui.components.marker.MarkerIcon
import com.toutiaoyan.compose.ui.components.marker.MarkerLabel
import com.toutiaoyan.compose.ui.components.marker.MarkerVariant

val markerDoc =
    ComponentDoc(
        id = "marker",
        title = "Marker",
        description = "A labeled divider for a chat transcript, e.g. a date separator or a pinned-messages banner.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            Marker("Today", variant = MarkerVariant.Separator)
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Marker("Today")
                        """.trimIndent(),
                    preview = {
                        Marker("Today")
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        Marker("March 3", variant = MarkerVariant.Separator)
                        """.trimIndent(),
                    preview = {
                        Marker("March 3", variant = MarkerVariant.Separator)
                    },
                ),
                ComponentExample(
                    title = "Border, with icon",
                    code =
                        """
                        Marker(variant = MarkerVariant.Border) {
                            MarkerIcon { EmojiText("📌") }
                            MarkerContent("Pinned messages")
                        }
                        """.trimIndent(),
                    preview = {
                        Marker(variant = MarkerVariant.Border) {
                            MarkerIcon { EmojiText("📌") }
                            MarkerContent("Pinned messages")
                        }
                    },
                ),
                ComponentExample(
                    title = "Icon",
                    code =
                        """
                        Marker(variant = MarkerVariant.Border) {
                            MarkerIcon { EmojiText("📌") }
                            MarkerContent("Pinned messages")
                        }
                        """.trimIndent(),
                    preview = {
                        Marker(variant = MarkerVariant.Border) {
                            MarkerIcon { EmojiText("📌") }
                            MarkerContent("Pinned messages")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Marker {
                            MarkerContent("Today")
                        }
                        """.trimIndent(),
                    preview = {
                        Marker {
                            MarkerContent("Today")
                        }
                    },
                ),
                ComponentExample(
                    title = "Label",
                    code =
                        """
                        Marker {
                            MarkerLabel("Today")
                        }
                        """.trimIndent(),
                    preview = {
                        Marker {
                            MarkerLabel("Today")
                        }
                    },
                ),
            ),
    )
