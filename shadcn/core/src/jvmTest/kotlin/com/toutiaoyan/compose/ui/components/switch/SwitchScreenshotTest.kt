@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.switch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class SwitchScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("switch_states", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Switch(checked = false, onCheckedChange = {})
                Switch(checked = true, onCheckedChange = {})
                Switch(checked = false, onCheckedChange = {}, enabled = false)
                Switch(checked = true, onCheckedChange = {}, enabled = false)
            }
        }
    }

    private fun sizes(darkTheme: Boolean) {
        snapshot("switch_sizes", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Switch(checked = true, onCheckedChange = {})
                Switch(checked = true, onCheckedChange = {}, size = SwitchSize.Sm)
                Switch(checked = false, onCheckedChange = {}, size = SwitchSize.Sm)
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
        snapshotFocused("switch_focused", focusTag = "sw", darkTheme = false) {
            Switch(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("sw"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("switch_focused", focusTag = "sw", darkTheme = true) {
            Switch(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("sw"))
        }
    }
}
