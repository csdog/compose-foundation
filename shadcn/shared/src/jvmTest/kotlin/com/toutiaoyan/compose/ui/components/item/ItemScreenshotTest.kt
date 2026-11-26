@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.item

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText

class ItemScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("item_states", darkTheme = darkTheme) {
            ItemGroup {
                Item(variant = ItemVariant.Outline) {
                    ItemMedia(variant = ItemMediaVariant.Icon) { EmojiText("📦") }
                    ItemContent {
                        ItemTitle("Order #1234")
                        ItemDescription("Shipped on March 12")
                    }
                    ItemActions("Track")
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun extraSmall(darkTheme: Boolean) {
        snapshot("item_xs", darkTheme = darkTheme) {
            Item(variant = ItemVariant.Outline, size = ItemSize.Xs) {
                ItemContent {
                    ItemTitle("Extra small item")
                    ItemDescription("Official size=xs.")
                }
            }
        }
    }

    @Test
    fun xs_light() = extraSmall(darkTheme = false)

    @Test
    fun xs_dark() = extraSmall(darkTheme = true)
}
