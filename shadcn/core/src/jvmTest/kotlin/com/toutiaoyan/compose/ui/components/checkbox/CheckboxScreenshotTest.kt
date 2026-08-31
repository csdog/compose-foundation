@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class CheckboxScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("checkbox_states", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Checkbox(checked = false, onCheckedChange = {})
                Checkbox(checked = true, onCheckedChange = {})
                Checkbox(checked = false, indeterminate = true, onCheckedChange = {})
                Checkbox(checked = false, onCheckedChange = {}, enabled = false)
                Checkbox(checked = true, onCheckedChange = {}, enabled = false)
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("checkbox_focused", focusTag = "cb", darkTheme = false) {
            Checkbox(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("cb"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("checkbox_focused", focusTag = "cb", darkTheme = true) {
            Checkbox(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("cb"))
        }
    }
}
