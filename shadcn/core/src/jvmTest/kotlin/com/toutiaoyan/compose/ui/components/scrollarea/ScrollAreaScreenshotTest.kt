@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.text.Text

class ScrollAreaScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("scroll_area_states", darkTheme = darkTheme) {
            ScrollArea(type = ScrollType.Auto, modifier = Modifier.height(120.dp)) {
                Column {
                    repeat(10) { index -> Text { "Row $index" } }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
