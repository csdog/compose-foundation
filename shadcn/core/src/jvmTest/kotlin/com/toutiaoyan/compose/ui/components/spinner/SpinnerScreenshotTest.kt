@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.spinner

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class SpinnerScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("spinner_states", darkTheme = darkTheme) {
            Spinner()
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
