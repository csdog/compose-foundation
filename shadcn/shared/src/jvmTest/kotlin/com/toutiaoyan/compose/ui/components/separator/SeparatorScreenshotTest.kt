@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.separator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.text.Text

class SeparatorScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("separator_states", darkTheme = darkTheme) {
            // Arrangement.spacedBy, not a bare Row: Separator has no built-in
            // margin (matching real shadcn/ui), so without spacing the surrounding
            // text renders flush against the separator line -- see SeparatorDoc.kt's
            // "with spacing" examples for the same fix applied to the catalog demo.
            Row(modifier = Modifier.height(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text { "Blog" }
                Separator(orientation = SeparatorOrientation.Vertical)
                Text { "Docs" }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
