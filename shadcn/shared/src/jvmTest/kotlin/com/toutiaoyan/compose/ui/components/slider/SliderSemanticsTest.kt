@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.slider

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.SemanticsMatcher
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SliderSemanticsTest {
    @get:Rule
    val rule = createComposeRule()

    private fun assertSteps(
        range: ClosedFloatingPointRange<Float>,
        step: Float,
        expected: Int,
    ) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Slider(
                    values = listOf(range.start, range.endInclusive),
                    onValuesChange = {},
                    valueRange = range,
                    step = step,
                )
            }
        }
        val ranges =
            rule.onAllNodes(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo),
                useUnmergedTree = true,
            ).fetchSemanticsNodes().map { it.config[SemanticsProperties.ProgressBarRangeInfo] }
        assertTrue(ranges.size >= 3, "Root and both focusable thumbs must expose range information")
        ranges.forEach {
            assertEquals(expected, it.steps)
            if (step > 0f && range.start < range.endInclusive) {
                // Same increment calculation used by Android's accessibility scroll action.
                val accessibleIncrement = (it.range.endInclusive - it.range.start) / (it.steps + 1)
                assertEquals(step, accessibleIncrement, 0.0001f)
            }
        }
    }

    @Test
    fun integerStepsDescribeIntermediateValuesOnRootAndThumbs() = assertSteps(0f..100f, 5f, 19)

    @Test
    fun fractionalStepsDescribeIntermediateValuesOnRootAndThumbs() = assertSteps(0f..1f, 0.1f, 9)

    @Test
    fun continuousSliderRemainsContinuousOnRootAndThumbs() = assertSteps(0f..1f, 0f, 0)

    @Test
    fun degenerateRangeDoesNotAdvertiseDiscreteValues() = assertSteps(4f..4f, 1f, 0)
}
