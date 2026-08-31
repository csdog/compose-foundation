@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.select

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

/** The idle (closed) trigger only -- the open panel reuses the same row rendering already proven by Combobox/DropdownMenu. */
class SelectScreenshotTest : ScreenshotTest() {
    private fun closed(darkTheme: Boolean) {
        snapshot("select_closed", darkTheme = darkTheme) {
            Select(
                value = StylePreset.Vega,
                onValueChange = {},
                label = { it.label },
            ) {
                SelectTrigger { SelectValue() }
                SelectContent {
                    StylePreset.entries.forEach { SelectItem(it) }
                }
            }
        }
    }

    private fun placeholder(darkTheme: Boolean) {
        snapshot("select_placeholder", darkTheme = darkTheme) {
            Select<String>(
                value = null,
                onValueChange = {},
                placeholder = "Theme",
            ) {
                SelectTrigger { SelectValue() }
                SelectContent {
                    SelectItem("Light")
                    SelectItem("Dark")
                    SelectItem("System")
                }
            }
        }
    }

    @Test
    fun closed_light() = closed(darkTheme = false)

    @Test
    fun closed_dark() = closed(darkTheme = true)

    @Test
    fun placeholder_light() = placeholder(darkTheme = false)

    @Test
    fun placeholder_dark() = placeholder(darkTheme = true)

    private fun triggerFocused(darkTheme: Boolean) {
        snapshotFocused("select_trigger_focused", focusTag = "select-trigger", darkTheme = darkTheme) {
            Select(
                modifier = Modifier.testTag("select-trigger"),
                value = StylePreset.Vega,
                onValueChange = {},
                label = { it.label },
            ) {
                SelectTrigger { SelectValue() }
                SelectContent {
                    StylePreset.entries.forEach { SelectItem(it) }
                }
            }
        }
    }

    @Test
    fun trigger_focused_light() = triggerFocused(darkTheme = false)

    private fun itemAligned(darkTheme: Boolean) {
        snapshot("select_item_aligned", darkTheme = darkTheme) {
            Select(
                open = true,
                onOpenChange = {},
                value = "Dark",
                onValueChange = {},
            ) {
                SelectTrigger { SelectValue() }
                SelectContent(position = SelectContentDefaults.Position) {
                    SelectItem("Light")
                    SelectItem("Dark")
                    SelectItem("System")
                }
            }
        }
    }

    @Test
    fun item_aligned_light() = itemAligned(darkTheme = false)

    @Test
    fun item_aligned_dark() = itemAligned(darkTheme = true)
}
