package com.toutiaoyan.compose.ui.components.spinner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.modifiers.animateSpin
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpinnerMotionTest {
    @get:Rule val rule =
        createComposeRule(
            effectContext =
                object : MotionDurationScale {
                    override val scaleFactor = 1f
                },
        )

    @Test fun spinnerKeepsStableAccessibilityBoundsDuringRotation() {
        rule.mainClock.autoAdvance = false
        rule.setContent { Theme { Spinner() } }
        rule.mainClock.advanceTimeByFrame()
        val spinner = rule.onNodeWithContentDescription("Loading")
        spinner.assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo,
                ProgressBarRangeInfo.Indeterminate,
            ),
        )
        val initialBounds = spinner.fetchSemanticsNode().boundsInRoot
        rule.mainClock.advanceTimeBy(125)
        assertEquals(initialBounds, spinner.fetchSemanticsNode().boundsInRoot)
    }

    @Test fun spinDrawsNewFramesWithoutRecomposingItsCaller() {
        var compositions = 0
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val rotation = Modifier.animateSpin()
            SideEffect { compositions++ }
            Box(Modifier.size(64.dp).background(Color.White).testTag("frame"), contentAlignment = Alignment.Center) {
                Canvas(rotation.size(32.dp)) {
                    drawLine(Color.Black, Offset(16f, 2f), Offset(16f, 12f), 3f)
                }
            }
        }
        rule.mainClock.advanceTimeByFrame()
        val baseline = compositions
        val before = rule.onNodeWithTag("frame").captureToImage().toPixelMap()
        rule.mainClock.advanceTimeBy(256)
        rule.waitForIdle()
        val after = rule.onNodeWithTag("frame").captureToImage().toPixelMap()
        val changed = (0 until 64).sumOf { y -> (0 until 64).count { x -> before[x, y] != after[x, y] } }
        assertTrue(changed > 10, "Rotation must keep drawing new frames")
        assertTrue(compositions <= baseline + 1, "Animation recomposed caller ${compositions - baseline} times")
    }
}
