@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.textarea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class TextareaScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("textarea_states", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Textarea(
                    value = "",
                    onValueChange = {},
                    placeholder = "Type here...",
                    modifier = Modifier.width(220.dp),
                )
                Textarea(
                    value = "Invalid",
                    onValueChange = {},
                    isError = true,
                    modifier = Modifier.width(220.dp),
                )
                Textarea(
                    value = "Disabled",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.width(220.dp),
                )
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("textarea_focused", focusTag = "ta", darkTheme = false) {
            Textarea(value = "", onValueChange = {}, modifier = Modifier.width(220.dp).testTag("ta"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("textarea_focused", focusTag = "ta", darkTheme = true) {
            Textarea(value = "", onValueChange = {}, modifier = Modifier.width(220.dp).testTag("ta"))
        }
    }
}
