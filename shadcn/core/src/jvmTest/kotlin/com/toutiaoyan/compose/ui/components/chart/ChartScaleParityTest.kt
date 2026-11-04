package com.toutiaoyan.compose.ui.components.chart

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChartScaleParityTest {
    @Test fun interactiveSourceDataUsesTheObservedSixHundredMaximum() {
        val domain = chartNiceDomain(listOf(222f, 454f))
        assertEquals(0.0, domain.minimum, 1e-9)
        assertEquals(600.0, domain.maximum, 1e-9)
        assertEquals(81.4, 222 / domain.maximum * 220, 1e-9)
    }

    @Test fun firstChartRoundsToThreeHundredTwenty() {
        assertEquals(320.0, chartNiceDomain(listOf(73f, 305f)).maximum, 1e-9)
    }

    @Test fun signedAndZeroDomainsKeepFiveTicksAndACrossingBaseline() {
        val signed = chartNiceDomain(listOf(-100f, 305f))
        assertTrue(signed.minimum <= -100)
        assertTrue(signed.maximum >= 305)
        assertEquals(ChartDomain(0.0, 4.0), chartNiceDomain(listOf(0f, Float.NaN)))
    }

    @Test fun smallAndLargeFiniteValuesProduceFinitePositiveSpans() {
        for (values in listOf(listOf(.0003f), listOf(Float.MAX_VALUE), listOf(-Float.MAX_VALUE, Float.MAX_VALUE))) {
            val domain = chartNiceDomain(values)
            assertTrue(domain.minimum.isFinite() && domain.maximum.isFinite())
            assertTrue(domain.maximum > domain.minimum)
        }
    }
}
