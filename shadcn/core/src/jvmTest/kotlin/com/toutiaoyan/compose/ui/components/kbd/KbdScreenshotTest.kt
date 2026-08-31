@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.kbd

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class KbdScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("kbd_states", darkTheme = darkTheme) {
            KbdGroup {
                Kbd("Ctrl")
                Kbd("K")
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
