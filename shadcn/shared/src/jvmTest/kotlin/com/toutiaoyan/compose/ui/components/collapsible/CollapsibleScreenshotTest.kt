@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.collapsible

import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import kotlin.test.Test

class CollapsibleScreenshotTest : ScreenshotTest() {
    private fun states(
        darkTheme: Boolean,
        open: Boolean,
    ) {
        val suffix = if (open) "_expanded" else "_collapsed"
        snapshot("collapsible$suffix", darkTheme = darkTheme) {
            Collapsible(
                open = open,
                onOpenChange = {},
            ) {
                CollapsibleTrigger(if (open) "Hide details" else "Show details", variant = ButtonVariant.Outline)
                CollapsibleContent("@peduarte starred 3 repositories")
            }
        }
    }

    @Test
    fun collapsed_light() = states(darkTheme = false, open = false)

    @Test
    fun collapsed_dark() = states(darkTheme = true, open = false)

    @Test
    fun expanded_light() = states(darkTheme = false, open = true)

    @Test
    fun expanded_dark() = states(darkTheme = true, open = true)
}
