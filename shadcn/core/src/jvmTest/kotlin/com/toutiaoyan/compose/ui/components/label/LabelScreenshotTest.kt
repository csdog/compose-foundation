@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.label

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class LabelScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("label_states", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Label(text = "Default label")
                Label(text = "Required label", required = true)
                Label(text = "Disabled label", disabled = true)
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
