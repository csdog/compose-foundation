@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.then
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class SkeletonParityTest(private val dark: Boolean) : ScreenshotTest() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "dark={0}")
        fun modes() = listOf(arrayOf(false), arrayOf(true))
    }

    private fun pixels() = composeRule.onNodeWithTag("skeleton").captureToImage().toPixelMap()

    @Test fun chromeUsesMutedAndRoundedMdWithoutPulse() {
        var muted = Color.Unspecified
        var background = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            muted = currentTheme.colors.muted
            background = currentTheme.colors.background
            Skeleton(Modifier.size(100.dp, 20.dp).testTag("skeleton"), style = rememberSkeletonChrome())
        }
        val p = pixels()
        assertEquals(muted, p[50, 10])
        assertEquals(background, p[0, 0])
        composeRule.onNodeWithTag("skeleton").assertWidthIsEqualTo(100.dp).assertHeightIsEqualTo(20.dp)
    }

    @Test fun callerStyleCanKeepChromeAndOverrideCircle() {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Skeleton(
                Modifier.size(40.dp).testTag("skeleton"),
                style =
                    rememberSkeletonChrome() then
                        Style {
                            shape(CircleShape)
                            background(Color.Magenta)
                        },
            )
        }
        val p = pixels()
        assertEquals(Color.Magenta, p[20, 20])
        assertNotEquals(Color.Magenta, p[2, 2])
    }

    @Test fun explicitReplacementStyleOmitsDefaultChromeAndPulse() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Skeleton(Modifier.size(40.dp).testTag("skeleton"), style = Style { background(Color.Magenta) })
        }
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.waitForIdle()
        val p = pixels()
        assertEquals(Color.Magenta, p[20, 20])
        assertEquals(Color.Magenta, p[0, 0])
    }

    @Test fun pulseHasTwoSecondCycleAndHalfOpacityAtMidpoint() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Box(Modifier.background(Color.Black)) {
                Skeleton(
                    Modifier.size(40.dp).testTag("skeleton"),
                    style = Style { background(Color.White) } then rememberSkeletonPulse(),
                )
            }
        }
        composeRule.mainClock.advanceTimeByFrame()
        val start = composeRule.mainClock.currentTime

        fun sampleAt(
            offset: Long,
            alpha: Float,
        ) {
            composeRule.mainClock.advanceTimeBy(start + offset - composeRule.mainClock.currentTime)
            composeRule.waitForIdle()
            assertEquals(alpha, pixels()[20, 20].red, .025f, "opacity at $offset ms")
        }
        sampleAt(0, 1f)
        sampleAt(500, .75f)
        sampleAt(1000, .5f)
        sampleAt(1500, .75f)
        sampleAt(2000, 1f)
    }

    @Test fun pulseDoesNotRecomposeItsConsumerEveryFrame() {
        composeRule.mainClock.autoAdvance = false
        var compositions = 0
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            val style = rememberSkeletonStyle()
            SideEffect { compositions++ }
            Skeleton(Modifier.size(40.dp).testTag("skeleton"), style = style)
        }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        val initial = compositions
        repeat(12) {
            composeRule.mainClock.advanceTimeBy(64)
            composeRule.waitForIdle()
        }
        assertEquals(initial, compositions, "Pulse must update Style observation without recomposing its owner")
    }
}
