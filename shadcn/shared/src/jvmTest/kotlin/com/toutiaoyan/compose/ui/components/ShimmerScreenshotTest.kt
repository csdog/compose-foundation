@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components

import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.styles.shimmer
import kotlin.test.Test

/**
 * `Modifier.shimmer()` drives an [androidx.compose.animation.core.InfiniteTransition],
 * whose real-clock frame position at capture time is otherwise uncontrolled -- a plain
 * [snapshot] call here caught the sweep off-screen more often than not, producing a golden
 * that was indistinguishable from plain unstyled text (confirmed by looking at it, not
 * assumed). Freezing [ScreenshotTest.composeRule]'s `mainClock` and advancing it to
 * exactly half the sweep's `durationMillis` guarantees the highlight band sits mid-text
 * every time, deterministically.
 */
class ShimmerScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(darkTheme = darkTheme) {
            Text(textStyle = TextColors.Muted, modifier = Modifier.shimmer()) { "Generating response…" }
        }
        composeRule.mainClock.advanceTimeBy(1000L)
        captureNamed("shimmer_states", darkTheme = darkTheme)
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)
}
