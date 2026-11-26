@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.then
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import kotlin.test.*

class ShimmerCurrentColorTest : ScreenshotTest() {
    private fun assertPainted(color: Color) {
        val image = composeRule.onNodeWithTag("text", useUnmergedTree = true).captureToImage().toPixelMap()
        assertTrue(
            (0 until image.width).any { x ->
                (0 until image.height).any { y ->
                    val p = image[x, y]
                    kotlin.math.abs(p.red - color.red) < .04f && kotlin.math.abs(p.green - color.green) < .04f &&
                        kotlin.math.abs(p.blue - color.blue) < .04f
                }
            },
            "Expected currentColor=$color",
        )
    }

    private fun start() {
        composeRule.mainClock.advanceTimeBy(64)
    }

    @Test fun localContentColorIsCurrentColor() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            CompositionLocalProvider(LocalContentColor provides Color.Green) {
                Text(Modifier.testTag("text"), style = rememberShimmerStyle()) { "Generating response…" }
            }
        }
        start()
        assertPainted(Color.Green)
    }

    @Test fun ancestorStyleColorIsCurrentColor() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            Box(Modifier.styleable(style = Style { contentColor(Color.Blue) })) {
                Text(Modifier.testTag("text"), style = rememberShimmerStyle()) { "Generating response…" }
            }
        }
        start()
        assertPainted(Color.Blue)
    }

    @Test fun styleShimmerRetainsItsPrecedingOwnColor() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                style =
                    Style { contentColor(Color.Red) }.shimmer(),
            ) { "Generating response…" }
        }
        start()
        assertPainted(Color.Red)
    }

    @Test fun nearerExplicitTextColorOverridesOwnStyleShimmerColor() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                textStyle = TextStyle(color = Color.Green),
                style = Style { contentColor(Color.Red) }.shimmer(),
            ) { "Generating response…" }
        }
        start()
        assertPainted(Color.Green)
    }

    @Test fun explicitBaseParameterStillOverridesTextCurrentColor() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                textStyle = TextStyle(color = Color.Blue),
                style = rememberShimmerStyle(baseColor = Color.Red),
            ) { "Generating response…" }
        }
        start()
        assertPainted(Color.Red)
    }

    @Test fun ancestorColorChangeRepaintsWithoutRecomposingTextCaller() {
        composeRule.mainClock.autoAdvance = false
        var tint by mutableStateOf(Color.Red)
        var compositions = 0
        setThemedContent {
            Box(Modifier.styleable(style = Style { contentColor(tint) })) {
                SideEffect { compositions++ }
                Text(Modifier.testTag("text"), style = rememberShimmerStyle()) { "Generating response…" }
            }
        }
        start()
        assertPainted(Color.Red)
        var baseline = 0
        composeRule.runOnIdle {
            baseline = compositions
            tint = Color.Blue
        }
        composeRule.waitForIdle()
        assertPainted(Color.Blue)
        composeRule.runOnIdle { assertEquals(baseline, compositions) }
    }

    @Test fun precedingOwnStyleColorCanChangeWhileAnimationClockIsPaused() {
        composeRule.mainClock.autoAdvance = false
        var tint by mutableStateOf(Color.Red)
        setThemedContent {
            Text(Modifier.testTag("text"), style = Style { contentColor(tint) }.shimmer()) { "Generating response…" }
        }
        start()
        assertPainted(Color.Red)
        composeRule.runOnIdle { tint = Color.Green }
        composeRule.waitForIdle()
        assertPainted(Color.Green)
    }

    @Test fun rawThenChainAlsoRetainsItsPrecedingOwnColor() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                style = Style { contentColor(Color.Red) } then rememberShimmerStyle(),
            ) { "Generating response…" }
        }
        start()
        assertPainted(Color.Red)
    }
}
