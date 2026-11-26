@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.empty

import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText

class EmptyScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("empty_states", darkTheme = darkTheme) {
            Empty {
                EmptyHeader {
                    EmptyMedia { EmojiText("📭") }
                    EmptyTitle("No results found")
                    EmptyDescription("Try adjusting your search or filters.")
                }
                EmptyContent {
                    Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun outline(darkTheme: Boolean) {
        snapshot("empty_outline", darkTheme = darkTheme) {
            Empty(
                "No projects yet",
                description = "Get started by creating a new project.",
                media = { EmojiText("📁") },
                variant = EmptyVariant.Outline,
            )
        }
    }

    @Test
    fun outline_light() = outline(darkTheme = false)

    @Test
    fun outline_dark() = outline(darkTheme = true)

    private fun spacing(darkTheme: Boolean) {
        snapshot("empty_spacing", darkTheme = darkTheme) {
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
        }
    }

    @Test
    fun spacing_light() = spacing(darkTheme = false)

    @Test
    fun spacing_dark() = spacing(darkTheme = true)

    private fun contentSpacing(darkTheme: Boolean) {
        snapshot("empty_content_spacing", darkTheme = darkTheme) {
            Empty {
                EmptyHeader("No results found", description = "Try adjusting your search or filters.")
                EmptyContent(spacing = EmptyContentDefaults.Spacing) {
                    Button("Clear filters", onClick = {}, variant = ButtonVariant.Outline)
                    Button("Browse all", onClick = {}, variant = ButtonVariant.Ghost)
                }
            }
        }
    }

    @Test
    fun content_spacing_light() = contentSpacing(darkTheme = false)

    @Test
    fun content_spacing_dark() = contentSpacing(darkTheme = true)

    private fun officialTitle(darkTheme: Boolean) {
        snapshot("empty_title_sm", darkTheme = darkTheme) {
            Empty {
                EmptyHeader {
                    EmptyTitle("No results found", variant = EmptyTitleDefaults.Official)
                    EmptyDescription("Try adjusting your search or filters.")
                }
            }
        }
    }

    @Test
    fun title_sm_light() = officialTitle(darkTheme = false)

    @Test
    fun title_sm_dark() = officialTitle(darkTheme = true)

    private fun mdPadding(darkTheme: Boolean) {
        snapshot("empty_md_padding", darkTheme = darkTheme) {
            Empty(padding = EmptyDefaults.MdPadding) {
                EmptyHeader {
                    EmptyTitle("No results found")
                    EmptyDescription("Try adjusting your search or filters.")
                }
            }
        }
    }

    @Test
    fun md_padding_light() = mdPadding(darkTheme = false)

    @Test
    fun md_padding_dark() = mdPadding(darkTheme = true)

    private fun officialRadius(darkTheme: Boolean) {
        snapshot("empty_rounded_xl", darkTheme = darkTheme) {
            Empty(radius = EmptyDefaults.Radius, variant = EmptyVariant.Outline) {
                EmptyHeader {
                    EmptyTitle("No results found")
                    EmptyDescription("Try adjusting your search or filters.")
                }
            }
        }
    }

    @Test
    fun rounded_xl_light() = officialRadius(darkTheme = false)

    @Test
    fun rounded_xl_dark() = officialRadius(darkTheme = true)

    private fun mediaDefault(darkTheme: Boolean) {
        snapshot("empty_media_default", darkTheme = darkTheme) {
            Empty {
                EmptyHeader {
                    EmptyMedia(variant = EmptyMediaDefaults.Variant) { EmojiText("📭") }
                    EmptyTitle("No results found")
                    EmptyDescription("Try adjusting your search or filters.")
                }
            }
        }
    }

    @Test
    fun media_default_light() = mediaDefault(darkTheme = false)

    @Test
    fun media_default_dark() = mediaDefault(darkTheme = true)
}
