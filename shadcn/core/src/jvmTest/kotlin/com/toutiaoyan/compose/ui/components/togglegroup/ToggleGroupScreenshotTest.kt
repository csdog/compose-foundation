@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.togglegroup

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class ToggleGroupScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("toggle_group_states", darkTheme = darkTheme) {
            ToggleGroup(value = setOf("bold"), onValueChange = {}) {
                ToggleGroupItem("bold", "B")
                ToggleGroupItem("italic", "I")
                ToggleGroupItem("underline", "U")
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
