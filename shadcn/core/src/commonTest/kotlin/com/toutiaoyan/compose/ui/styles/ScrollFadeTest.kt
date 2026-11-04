package com.toutiaoyan.compose.ui.styles

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.test.*

class ScrollFadeTest {
    @Test fun leadingIsCrispAtStart() = assertFalse(shouldFadeLeadingEdge(0))

    @Test fun leadingAppearsAfterStart() = assertTrue(shouldFadeLeadingEdge(1))

    @Test fun trailingAppearsBeforeEnd() = assertTrue(shouldFadeTrailingEdge(0, 100))

    @Test fun trailingIsCrispAtEnd() = assertFalse(shouldFadeTrailingEdge(100, 100))

    @Test fun defaultUsesTwelvePercentOnSmallViewports() {
        assertEquals(24f, scrollFadeDepth(ScrollFadeSize.Default, 200f, Density(1f)))
    }

    @Test fun defaultIsCappedAtFortyOnTallViewports() {
        assertEquals(40f, scrollFadeDepth(ScrollFadeSize.Default, 1000f, Density(1f)))
        assertEquals(80f, scrollFadeDepth(ScrollFadeSize.Default, 2000f, Density(2f)))
    }

    @Test fun explicitLengthsAndPercentagesBypassDefaultCap() {
        assertEquals(96f, scrollFadeDepth(ScrollFadeSize.Length(96.dp), 192f, Density(1f)))
        assertEquals(150f, scrollFadeDepth(ScrollFadeSize.Fraction(.15f), 1000f, Density(1f)))
    }

    @Test fun revealUsesSymmetricCssEaseInOutOverNinetySixPixels() {
        assertEquals(0f, scrollFadeReveal(24f, 0, 96f))
        assertEquals(12f, scrollFadeReveal(24f, 48, 96f), .001f)
        assertEquals(24f, scrollFadeReveal(24f, 96, 96f))
        assertEquals(24f, scrollFadeReveal(24f, 200, 96f))
        assertEquals(24f, scrollFadeReveal(24f, 1, 0f))
        assertTrue(scrollFadeReveal(24f, 24, 96f) < 6f)
    }

    @Test fun noFadeLeavesAnOpaqueMask() {
        assertTrue(scrollFadeStops(200f, 0f, 0f).all { it.second.alpha == 1f })
    }

    @Test fun normalGradientHasTwoOpaqueStopsAndTransparentEnds() {
        val stops = scrollFadeStops(200f, 24f, 24f)
        assertEquals(listOf(0f, .12f, .88f, 1f), stops.map { it.first })
        assertEquals(listOf(0f, 1f, 1f, 0f), stops.map { it.second.alpha })
    }

    @Test fun overlappingFadesUseCssStopFixupInsteadOfMultiplyingTwoMasks() {
        val stops = scrollFadeStops(100f, 80f, 80f)
        assertEquals(listOf(0f, .8f, 1f), stops.map { it.first })
        assertEquals(1f, stops[1].second.alpha)
    }

    @Test fun oversizedLeadingFadePreservesTheVisiblePartOfItsRamp() {
        val stops = scrollFadeStops(100f, 200f, 0f)
        assertEquals(.5f, stops.last().second.alpha, 1f / 255)
    }

    @Test fun invalidSizesAreRejectedAndZeroExtentIsSafe() {
        assertFailsWith<IllegalArgumentException> { ScrollFadeSize.Length((-1).dp) }
        assertFailsWith<IllegalArgumentException> { ScrollFadeSize.Fraction(Float.NaN) }
        assertTrue(scrollFadeStops(0f, 24f, 24f).all { it.second.alpha == 1f })
    }
}
