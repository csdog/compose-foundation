@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Test
import kotlin.test.*

class ScrollFadeInteractionTest : ScreenshotTest() {
    private lateinit var state: ScrollState
    private var compositions = 0
    private var contentExtent by mutableIntStateOf(600)

    private fun fixture(
        horizontal: Boolean = false,
        cappedBridge: Boolean = false,
        rtl: Boolean = false,
        edge: ScrollFadeEdge = ScrollFadeEdge.Both,
        enabled: Boolean = true,
        leading: ScrollFadeSize = ScrollFadeSize.Default,
        trailing: ScrollFadeSize = leading,
    ) {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                val current = rememberScrollState()
                SideEffect {
                    state = current
                    compositions++
                }
                val viewport = if (horizontal) Modifier.size(200.dp, 120.dp) else Modifier.size(120.dp, 200.dp)
                Box(viewport.background(Color.Blue)) {
                    val orientation =
                        if (horizontal) {
                            ScrollFadeOrientation.Horizontal
                        } else {
                            ScrollFadeOrientation.Vertical
                        }
                    val modifier =
                        viewport.testTag("mask").let {
                            if (cappedBridge) {
                                it.scrollFade(current, orientation, 40.dp, .12f, 96.dp)
                            } else {
                                it.scrollFade(
                                    current,
                                    edge = edge,
                                    leadingSize = leading,
                                    trailingSize = trailing,
                                    enabled = enabled,
                                    orientation = orientation,
                                )
                            }
                        }
                    if (horizontal) {
                        Row(modifier.horizontalScroll(current)) {
                            Box(Modifier.size(contentExtent.dp, 120.dp).background(Color.Red))
                        }
                    } else {
                        Column(modifier.verticalScroll(current)) {
                            Box(Modifier.size(120.dp, contentExtent.dp).background(Color.Red))
                        }
                    }
                }
            }
        }
        advance()
    }

    private fun advance() {
        composeRule.mainClock.advanceTimeBy(64)
        composeRule.waitForIdle()
    }

    private fun move(value: Int) {
        composeRule.runOnIdle { state.dispatchRawDelta((value - state.value).toFloat()) }
        advance()
    }

    private fun pixel(
        x: Int,
        y: Int,
    ) = composeRule.onNodeWithTag("mask").captureToImage().toPixelMap()[x, y]

    private fun red(
        x: Int,
        y: Int,
    ) {
        assertTrue(pixel(x, y).red > .95f, "Expected crisp red at$x,$y")
    }

    private fun blue(
        x: Int,
        y: Int,
    ) {
        assertTrue(pixel(x, y).blue > .9f, "Expected dissolved edge at$x,$y")
    }

    @Test fun defaultVerticalMaskTracksStartMiddleAndEndOverAnUnrelatedBackground() {
        fixture()
        red(60, 0)
        blue(60, 199)
        red(60, 100)
        move(200)
        blue(60, 0)
        blue(60, 199)
        red(60, 100)
        move(state.maxValue)
        blue(60, 0)
        red(60, 199)
    }

    @Test fun halfRevealIsHalfDepthRatherThanInstantFullDepth() {
        fixture()
        move(48)
        assertEquals(6.5f / 12f, pixel(60, 6).red, .04f)
    }

    @Test fun fixedAndAsymmetricDepthsOverrideTheDefaultPercentage() {
        fixture(leading = ScrollFadeSize.Length(8.dp), trailing = ScrollFadeSize.Length(32.dp))
        move(200)
        assertEquals(6.5f / 8f, pixel(60, 6).red, .04f)
        assertEquals(6.5f / 32f, pixel(60, 193).red, .04f)
    }

    @Test fun horizontalAndRtlMasksMirrorTheReadingDirection() {
        fixture(horizontal = true, rtl = true)
        blue(0, 60)
        red(199, 60)
        move(200)
        blue(0, 60)
        blue(199, 60)
        move(state.maxValue)
        red(0, 60)
        blue(199, 60)
    }

    @Test fun horizontalLtrStartsCrispOnTheLeft() {
        fixture(horizontal = true)
        red(0, 60)
        blue(199, 60)
    }

    @Test fun logicalStartInRtlOnlyRevealsTheRightEdgeAfterScrolling() {
        fixture(horizontal = true, rtl = true, edge = ScrollFadeEdge.Start)
        red(0, 60)
        red(199, 60)
        move(200)
        red(0, 60)
        blue(199, 60)
    }

    @Test fun physicalLeftRemainsOnTheLeftInRtl() {
        fixture(horizontal = true, rtl = true, edge = ScrollFadeEdge.Left)
        blue(0, 60)
        red(199, 60)
    }

    @Test fun topOnlyDoesNotFadeTheBottom() {
        fixture(edge = ScrollFadeEdge.Top)
        red(60, 0)
        red(60, 199)
        move(200)
        blue(60, 0)
        red(60, 199)
    }

    @Test fun bottomOnlyDoesNotFadeTheTop() {
        fixture(edge = ScrollFadeEdge.Bottom)
        move(200)
        red(60, 0)
        blue(60, 199)
    }

    @Test fun noneRetainsAllContentPixelsAndScrolling() {
        fixture(enabled = false)
        move(200)
        red(60, 0)
        red(60, 199)
        assertEquals(200, state.value)
    }

    @Test fun fittingContentDoesNotFadeAndShrinkingOverflowRemovesTheMask() {
        fixture()
        move(200)
        composeRule.runOnIdle { contentExtent = 200 }
        advance()
        assertEquals(0, state.maxValue)
        red(60, 0)
        red(60, 199)
    }

    @Test(timeout = 15000)
    fun realTouchScrollingUpdatesTheMaskWithoutComposingTheCaller() {
        fixture()
        val before = compositions
        composeRule.onNodeWithTag("mask").performTouchInput { swipeUp(durationMillis = 160) }
        advance()
        assertTrue(state.value > 96)
        blue(60, 0)
        assertEquals(before, compositions)
    }

    @Test fun attachmentCappedBridgeKeepsItsFractionAndRevealContract() {
        fixture(horizontal = true, cappedBridge = true)
        red(0, 60)
        blue(199, 60)
        move(48)
        assertEquals(6.5f / 12f, pixel(6, 60).red, .04f)
        move(state.maxValue)
        blue(0, 60)
        red(199, 60)
    }

    @Test fun overlappingRtlGradientKeepsCssLogicalStopOrder() {
        fixture(horizontal = true, rtl = true, leading = ScrollFadeSize.Length(128.dp))
        move(200)
        // CSS to-left stops: transparent0, black128, black72->128, transparent200.
        // The opaque peak therefore lies at physicalx72, notx128.
        assertTrue(pixel(72, 60).red > .95f, "RTL opaque stop must stay at logical128/physical72")
        assertTrue(pixel(128, 60).red < .65f)
    }
}
