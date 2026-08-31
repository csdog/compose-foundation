@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toggle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class ToggleScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("toggle_states", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(ToggleVariant.Default, ToggleVariant.Outline).forEach { variant ->
                    Toggle("Off", pressed = false, onPressedChange = {}, variant = variant)
                    Toggle("On", pressed = true, onPressedChange = {}, variant = variant)
                    Toggle(
                        "Disabled",
                        pressed = false,
                        onPressedChange = {},
                        variant = variant,
                        enabled = false,
                    )
                }
            }
        }
    }

    private fun sizes(darkTheme: Boolean) {
        snapshot("toggle_sizes", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Toggle("Sm", pressed = true, onPressedChange = {}, size = ToggleSize.Sm)
                Toggle("Md", pressed = true, onPressedChange = {}, size = ToggleSize.Md)
                Toggle("Lg", pressed = true, onPressedChange = {}, size = ToggleSize.Lg)
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun sizes_light() = sizes(darkTheme = false)

    @Test
    fun sizes_dark() = sizes(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("toggle_focused", focusTag = "tg", darkTheme = false) {
            Toggle(
                "Focus me",
                pressed = false,
                onPressedChange = {},
                modifier = Modifier.testTag("tg"),
            )
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("toggle_focused", focusTag = "tg", darkTheme = true) {
            Toggle(
                "Focus me",
                pressed = false,
                onPressedChange = {},
                modifier = Modifier.testTag("tg"),
            )
        }
    }
}
