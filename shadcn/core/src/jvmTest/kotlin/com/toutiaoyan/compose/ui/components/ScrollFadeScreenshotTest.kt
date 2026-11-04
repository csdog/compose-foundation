@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.ScrollState
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.styles.scrollFade
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.text.Text

class ScrollFadeScreenshotTest : ScreenshotTest() {
    /** Capture a measured midpoint with the alpha layer outside the scrolling content. */
    private fun states(darkTheme: Boolean) {
        lateinit var state: ScrollState
        setThemedContent(darkTheme = darkTheme) {
            val current = rememberScrollState()
            SideEffect { state = current }
            Column(Modifier.height(120.dp).scrollFade(current).verticalScroll(current)) {
                repeat(12) { index -> Text { "Row $index" } }
            }
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            assertTrue(state.maxValue > 0)
            state.dispatchRawDelta((state.maxValue / 2).toFloat())
            assertEquals(state.maxValue / 2, state.value)
        }
        composeRule.waitForIdle()
        captureNamed("scroll_fade_states", darkTheme)
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
