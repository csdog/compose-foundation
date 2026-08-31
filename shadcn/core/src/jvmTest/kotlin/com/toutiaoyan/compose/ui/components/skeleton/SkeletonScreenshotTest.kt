@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class SkeletonScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("skeleton_states", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Skeleton(modifier = Modifier.size(width = 250.dp, height = 20.dp))
                Skeleton(modifier = Modifier.size(width = 200.dp, height = 20.dp))
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
