@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.radiobutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class RadioButtonScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("radio_states", darkTheme = darkTheme) {
            RadioGroup {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RadioButton(selected = false, onClick = {})
                    RadioButton(selected = true, onClick = {})
                    RadioButton(selected = false, onClick = {}, enabled = false)
                    RadioButton(selected = true, onClick = {}, enabled = false)
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("radio_focused", focusTag = "rb", darkTheme = false) {
            RadioButton(selected = false, onClick = {}, modifier = Modifier.testTag("rb"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("radio_focused", focusTag = "rb", darkTheme = true) {
            RadioButton(selected = false, onClick = {}, modifier = Modifier.testTag("rb"))
        }
    }
}
