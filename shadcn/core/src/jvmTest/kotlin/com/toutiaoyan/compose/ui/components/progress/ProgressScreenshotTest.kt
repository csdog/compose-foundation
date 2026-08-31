@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class ProgressScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("progress_states", darkTheme = darkTheme) {
            Progress(value = 0.6f, modifier = Modifier.width(240.dp))
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun label(darkTheme: Boolean) {
        snapshot("progress_label", darkTheme = darkTheme) {
            Progress(value = 0.56f, modifier = Modifier.width(240.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProgressLabel("Upload progress")
                    ProgressValue()
                }
                ProgressTrack()
            }
        }
    }

    @Test
    fun label_light() = label(darkTheme = false)

    @Test
    fun label_dark() = label(darkTheme = true)
}
