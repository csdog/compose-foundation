@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.hovercard

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

/** Captures the trigger only -- see [com.toutiaoyan.compose.ui.components.tooltip.TooltipScreenshotTest]'s doc comment for why hover simulation isn't used. */
class HoverCardScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("hover_card_trigger", darkTheme = darkTheme) {
            HoverCard {
                HoverCardTrigger("@shadcn")
                HoverCardContent("The React Framework – created and maintained by @vercel.")
            }
        }
    }

    @Test
    fun trigger_light() = states(darkTheme = false)

    @Test
    fun trigger_dark() = states(darkTheme = true)
}
