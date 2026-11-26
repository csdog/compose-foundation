@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.runtime.remember
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class ToastScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("toast_states", darkTheme = darkTheme) {
            val state =
                remember {
                    ToastState().apply {
                        show(
                            title = "Event created",
                            description = "Monday, March 12 at 9:00 AM",
                            variant = ToastVariant.Success,
                        )
                    }
                }
            Toaster(state = state)
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
