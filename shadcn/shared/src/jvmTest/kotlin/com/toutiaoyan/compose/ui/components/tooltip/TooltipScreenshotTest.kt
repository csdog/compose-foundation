@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.tooltip

import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import kotlin.test.Test

/**
 * Captures the trigger only (not the hover-revealed tooltip bubble) -- a
 * `performMouseInput { moveTo(...) }` hover simulation was tried here and hung the
 * JVM test worker indefinitely (confirmed via `ps`: an actively CPU-spinning "Gradle
 * Test Executor" that had to be `kill -9`'d), unlike every other interaction used
 * elsewhere in this suite (`requestFocus()`, plain clicks). Documented gap, not
 * silently skipped.
 */
class TooltipScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("tooltip_trigger", darkTheme = darkTheme) {
            Tooltip {
                TooltipTrigger("Hover me")
                TooltipContent("Add to library")
            }
        }
    }

    @Test
    fun trigger_light() = states(darkTheme = false)

    @Test
    fun trigger_dark() = states(darkTheme = true)

    private fun arrow(darkTheme: Boolean) {
        snapshot("tooltip_arrow", darkTheme = darkTheme) {
            Tooltip(open = true) {
                TooltipTrigger("Hover me")
                TooltipContent(
                    "Add to library",
                    showArrow = true,
                    placement = PopupPlacement.Bottom,
                )
            }
        }
    }

    @Test
    fun arrow_light() = arrow(darkTheme = false)

    @Test
    fun arrow_dark() = arrow(darkTheme = true)

    private fun officialSideOffset(darkTheme: Boolean) {
        snapshot("tooltip_side_offset_4", darkTheme = darkTheme) {
            Tooltip(open = true) {
                TooltipTrigger("Hover me")
                TooltipContent(
                    "Add to library",
                    sideOffset = TooltipDefaults.SideOffset,
                    placement = PopupPlacement.Bottom,
                )
            }
        }
    }

    @Test
    fun side_offset_4_light() = officialSideOffset(darkTheme = false)

    @Test
    fun side_offset_4_dark() = officialSideOffset(darkTheme = true)
}
