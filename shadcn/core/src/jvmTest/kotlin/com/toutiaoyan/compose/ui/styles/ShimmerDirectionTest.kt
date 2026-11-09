@file:OptIn(
    androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class,
    androidx.compose.ui.test.ExperimentalTestApi::class,
)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.math.abs

@RunWith(Parameterized::class)
class ShimmerDirectionTest(private val overlay: Boolean, private val dark: Boolean) {
    private val motionScale =
        object : MotionDurationScale {
            override var scaleFactor by mutableFloatStateOf(1f)
        }

    @get:Rule
    val rule = createComposeRule(effectContext = motionScale)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "overlay={0},dark={1}")
        fun modes() = listOf(arrayOf(false, false), arrayOf(false, true), arrayOf(true, false), arrayOf(true, true))
    }

    private fun pixels(tag: String): List<Color> {
        val image = rule.onNodeWithTag(tag, useUnmergedTree = true).captureToImage().toPixelMap()
        return (0 until image.height).flatMap { y -> (0 until image.width).map { x -> image[x, y] } }
    }

    private fun show() {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            Theme(isDark = dark) {
                Column(Modifier.background(currentTheme.colors.background)) {
                    listOf(LayoutDirection.Ltr, LayoutDirection.Rtl).forEach { direction ->
                        listOf(false, true).forEach { reverse ->
                            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                                val tag = "$direction-$reverse"
                                Text(
                                    Modifier.width(240.dp).testTag(tag).then(
                                        if (overlay) {
                                            Modifier.shimmer(color = Color.Blue, reverse = reverse)
                                        } else {
                                            Modifier
                                        },
                                    ),
                                    textStyle =
                                        TextStyle(
                                            color = if (dark) Color.White else Color.Black,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center,
                                        ),
                                    style =
                                        if (overlay) {
                                            Style
                                        } else {
                                            rememberShimmerStyle(color = Color.Blue, reverse = reverse)
                                        },
                                ) { "MMMMMMMMMMMMMMMM" }
                            }
                        }
                    }
                    Text(
                        Modifier.width(240.dp).testTag("plain"),
                        textStyle =
                            TextStyle(
                                color = if (dark) Color.White else Color.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                            ),
                    ) { "MMMMMMMMMMMMMMMM" }
                }
            }
        }
        rule.mainClock.advanceTimeBy(64)
    }

    @Test
    fun reverseAndRtlBothSelectCssReverseWithoutCancellingEachOther() {
        show()
        val initial = pixels("Ltr-false")
        rule.mainClock.advanceTimeBy(512)
        assertNotEquals(initial, pixels("Ltr-false"), "Compare a moving intermediate frame")
        assertNotEquals(pixels("Ltr-false"), pixels("Ltr-true"))
        assertEquals(pixels("Ltr-true"), pixels("Rtl-false"))
        assertEquals(pixels("Rtl-false"), pixels("Rtl-true"), "Both CSS selectors assign animation-direction: reverse")
    }

    @Test
    fun hostDurationScaleZeroFinishesSweepAndRestoresNormalTextUntilMotionResumes() {
        show()
        rule.mainClock.advanceTimeBy(512)
        assertNotEquals(pixels("plain"), pixels("Ltr-false"))
        rule.runOnIdle { motionScale.scaleFactor = 0f }
        rule.mainClock.advanceTimeBy(64)
        val plain = pixels("plain")
        val completed = pixels("Ltr-false")
        assertEquals(plain.size, completed.size)
        assertTrue(
            plain.zip(completed).all { (expected, actual) ->
                // Offscreen glyph compositing can round an antialiased edge by one sRGB byte.
                abs(expected.red - actual.red) <= 1.1f / 255f &&
                    abs(expected.green - actual.green) <= 1.1f / 255f &&
                    abs(expected.blue - actual.blue) <= 1.1f / 255f &&
                    abs(expected.alpha - actual.alpha) <= 1.1f / 255f
            },
            "A host duration scale of zero must restore the plain text appearance",
        )
        val stopped = pixels("Ltr-false")
        rule.mainClock.advanceTimeBy(512)
        assertEquals(stopped, pixels("Ltr-false"))
        rule.runOnIdle { motionScale.scaleFactor = 1f }
        rule.mainClock.advanceTimeBy(576)
        assertNotEquals(stopped, pixels("Ltr-false"))
    }
}
