@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import kotlin.test.Test

class LazyScrollAreaScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("lazy_scroll_area_states", darkTheme = darkTheme) {
            val state = rememberLazyListState()
            LazyScrollArea(state = state, type = ScrollType.Auto, modifier = Modifier.height(120.dp)) {
                LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
                    items(20) { index -> Text { "Row $index" } }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
