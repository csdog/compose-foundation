@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.accordion

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class AccordionScreenshotTest : ScreenshotTest() {
    private fun states(
        darkTheme: Boolean,
        value: Set<String>,
    ) {
        val suffix = if (value.isEmpty()) "_collapsed" else "_expanded"
        snapshot("accordion$suffix", darkTheme = darkTheme) {
            Accordion(value = value, onValueChange = {}) {
                AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
            }
        }
    }

    @Test
    fun collapsed_light() = states(darkTheme = false, value = emptySet())

    @Test
    fun collapsed_dark() = states(darkTheme = true, value = emptySet())

    @Test
    fun expanded_light() = states(darkTheme = false, value = setOf("item-1"))

    @Test
    fun expanded_dark() = states(darkTheme = true, value = setOf("item-1"))

    /**
     * Regression guard: the trigger's focus-ring `Style` block used to omit `shape(...)`,
     * so its `dropShadow` ring fell back to the Style API's default `RectangleShape`
     * (sharp corners) instead of following `TwRadius.md`, the rounding every other
     * focusable component in this library rings with (matches real shadcn's
     * `AccordionTrigger` `rounded-md` class).
     */
    private fun focused(darkTheme: Boolean) {
        snapshotFocused("accordion_trigger_focused", focusTag = "accordion-item-1", darkTheme = darkTheme) {
            Accordion {
                AccordionItem(
                    "item-1",
                    "Is it accessible?",
                    "Yes. It adheres to the WAI-ARIA design pattern.",
                    modifier = Modifier.testTag("accordion-item-1"),
                )
            }
        }
    }

    @Test
    fun trigger_focused_light() = focused(darkTheme = false)

    @Test
    fun trigger_focused_dark() = focused(darkTheme = true)
}
