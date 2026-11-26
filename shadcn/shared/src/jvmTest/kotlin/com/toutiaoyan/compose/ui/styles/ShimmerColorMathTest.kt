package com.toutiaoyan.compose.ui.styles

import androidx.compose.ui.graphics.Color
import kotlin.test.*

class ShimmerColorMathTest {
    @Test fun lightRelativeColorRetainsRgbAndScalesOriginalAlpha() {
        val base = Color.Red.copy(alpha = .5f)
        val peak = shimmerHighlight(base, dark = false)
        assertEquals(1f, peak.red)
        assertEquals(0f, peak.green)
        assertEquals(0f, peak.blue)
        assertEquals(.1f, peak.alpha, 1f / 255f)
    }

    @Test fun darkBlackUsesPointEightFloorAndRaisesAlpha() {
        val peak = shimmerHighlight(Color.Black.copy(alpha = .2f), dark = true)
        assertEquals(.8f, peak.shimmerLch().l, .003f)
        assertEquals(.6f, peak.alpha, 1f / 255f)
    }

    @Test fun darkLuminanceAtOneMapsToWhiteEvenForSaturatedBase() {
        val peak = shimmerHighlight(Color.Red, dark = true)
        assertEquals(Color.White, peak)
    }

    @Test fun fullyTransparentWhiteDoesNotLeakLuminanceIntoBlackMidpoint() {
        val mixed = shimmerOklchMidpoint(Color.Black, Color.White.copy(alpha = 0f))
        assertEquals(0f, mixed.red)
        assertEquals(0f, mixed.green)
        assertEquals(0f, mixed.blue)
        assertEquals(.5f, mixed.alpha, 1f / 255f)
    }

    @Test fun alphaPremultipliedOklchMidpointHasExpectedLuminance() {
        val mixed = shimmerOklchMidpoint(Color.Black, Color.White.copy(alpha = .25f))
        assertEquals(.2f, mixed.shimmerLch().l, .005f)
        assertEquals(.625f, mixed.alpha, 1f / 255f)
    }

    @Test fun shortestHueCrossesZeroInsteadOfOppositeGreenArc() {
        val first = ShimmerLch(.6f, .1f, 350f, 1f).shimmerColor()
        val second = ShimmerLch(.6f, .1f, 10f, 1f).shimmerColor()
        val hue = shimmerOklchMidpoint(first, second).shimmerLch().h
        assertTrue(minOf(hue, 360f - hue) < 2f, "hue=$hue")
    }

    @Test fun powerlessGrayHueBorrowsChromaticEndpointHue() {
        val blue = ShimmerLch(.6f, .1f, 250f, 1f).shimmerColor()
        val mixed = shimmerOklchMidpoint(Color.Gray, blue)
        assertEquals(250f, mixed.shimmerLch().h, 2f)
    }

    @Test fun sharedStyleCachesColorsSeparatelyAndKeepsExplicitPeakAlpha() {
        val peak = Color.Blue.copy(alpha = .5f)
        val cache = ShimmerColorCache(peak, dark = false)
        val red = cache.colors(Color.Red)
        val green = cache.colors(Color.Green)
        assertSame(red, cache.colors(Color.Red))
        assertSame(green, cache.colors(Color.Green))
        assertEquals(peak, red[red.lastIndex / 2])
        assertEquals(Color.Red, red.first())
        assertEquals(Color.Red, red.last())
    }
}
