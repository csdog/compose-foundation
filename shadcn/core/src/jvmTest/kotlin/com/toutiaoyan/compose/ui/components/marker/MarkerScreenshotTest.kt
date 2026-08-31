@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.marker

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText

class MarkerScreenshotTest : ScreenshotTest() {
    private fun default(darkTheme: Boolean) {
        snapshot("marker_default", darkTheme = darkTheme) {
            Marker("Today")
        }
    }

    private fun separator(darkTheme: Boolean) {
        snapshot("marker_separator", darkTheme = darkTheme) {
            Marker("March 3", variant = MarkerVariant.Separator)
        }
    }

    private fun border(darkTheme: Boolean) {
        snapshot("marker_border", darkTheme = darkTheme) {
            Marker(variant = MarkerVariant.Border) {
                MarkerIcon { EmojiText("📌") }
                MarkerContent("Pinned messages")
            }
        }
    }

    @Test
    fun default_light() = default(darkTheme = false)

    @Test
    fun default_dark() = default(darkTheme = true)

    @Test
    fun separator_light() = separator(darkTheme = false)

    @Test
    fun separator_dark() = separator(darkTheme = true)

    @Test
    fun border_light() = border(darkTheme = false)
}
