@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.spinner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpinnerGeometryTest {
    @get:Rule val composeRule =
        createComposeRule(
            effectContext =
                object : MotionDurationScale {
                    override val scaleFactor = 0f
                },
        )

    @Test fun paintSilhouetteMatchesOfficialSvgAtPublicSizesAndCustomStyle() {
        var pixels by mutableStateOf(16)
        var dark by mutableStateOf(false)
        composeRule.setContent {
            Theme {
                val background = if (dark) Color(0xff0a0a0a) else Color.White
                Box(Modifier.background(background).padding(8.dp)) {
                    CompositionLocalProvider(LocalContentColor provides if (dark) Color.White else Color.Black) {
                        Spinner(
                            Modifier.testTag("spinner"),
                            size =
                                when (pixels) {
                                    12, 20 -> SpinnerSize.Sm
                                    24 -> SpinnerSize.Lg
                                    else -> SpinnerSize.Default
                                },
                            style =
                                if (pixels == 20) {
                                    Style {
                                        width(20.dp)
                                        height(20.dp)
                                    }
                                } else {
                                    Style
                                },
                        )
                    }
                }
            }
        }
        for (night in listOf(false, true)) {
            for (size in listOf(12, 16, 24, 20)) {
                composeRule.runOnIdle {
                    dark = night
                    pixels = size
                }
                assertPaintMatchesSvg(size, night)
            }
        }
    }

    private fun assertPaintMatchesSvg(
        size: Int,
        night: Boolean,
    ) {
        val node = composeRule.onNodeWithTag("spinner")
        val actual = node.captureToImage().toPixelMap()
        assertEquals(size, actual.width)
        assertEquals(size.toFloat(), node.fetchSemanticsNode().boundsInRoot.width, .1f)
        // Independent oracle: the official SVG rendered by Chromium without animation.
        val name = "/spinner/loader-circle-$size-${if (night) "dark" else "light"}.png"
        val reference = checkNotNull(javaClass.getResourceAsStream(name)).use { ImageIO.read(it) }
        val background = if (night) Color(0xff0a0a0a) else Color.White

        fun contrast(red: Float): Float =
            if (night) ((red - background.red) / (1f - background.red)).coerceIn(0f, 1f) else 1f - red

        val drawn = Array(size) { y -> FloatArray(size) { x -> contrast(actual[x, y].red) } }
        val expected =
            Array(size) { y -> FloatArray(size) { x -> contrast(((reference.getRGB(x, y) shr 16) and 255) / 255f) } }
        assertInkMatches(drawn, expected, size, night)

        for (i in 0 until size) {
            assertEquals(background, actual[i, 0], "Top edge must remain outside the ink")
            assertEquals(background, actual[0, i], "Left edge must remain outside the ink")
        }
    }

    private fun assertInkMatches(
        drawn: Array<FloatArray>,
        expected: Array<FloatArray>,
        size: Int,
        night: Boolean,
    ) {
        for (y in 0 until size) {
            for (x in 0 until size) {
                assertInkAt(drawn, expected, x, y, "$size, dark=$night")
            }
        }
        val area = drawn.sumOf { row -> row.sum().toDouble() }
        val expectedArea = expected.sumOf { row -> row.sum().toDouble() }
        // Quantized and continuous edge AA differ. Allow a 0.1px band around a ~4*size perimeter.
        assertTrue(
            abs(area - expectedArea) <= .4 * size,
            "Stroke coverage $area differs from SVG $expectedArea ($size, dark=$night)",
        )
    }

    private fun assertInkAt(
        drawn: Array<FloatArray>,
        expected: Array<FloatArray>,
        x: Int,
        y: Int,
        fixture: String,
    ) {
        if (drawn[y][x] > .1f) assertTrue(hasNearbyInk(expected, x, y), "Unexpected ink at $x,$y ($fixture)")
        if (expected[y][x] > .1f) assertTrue(hasNearbyInk(drawn, x, y), "Missing SVG silhouette at $x,$y ($fixture)")
    }

    private fun hasNearbyInk(
        ink: Array<FloatArray>,
        x: Int,
        y: Int,
    ): Boolean =
        (max(0, y - 1)..min(ink.lastIndex, y + 1)).any { yy ->
            (max(0, x - 1)..min(ink.lastIndex, x + 1)).any { xx -> ink[yy][xx] > .1f }
        }
}
