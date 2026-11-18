@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.slider

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.TouchInjectionScope
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SliderInteractionTest {
    @get:Rule
    val rule = createComposeRule()

    private val changes = mutableListOf<List<Float>>()
    private val commits = mutableListOf<List<Float>>()
    private val slider get() = rule.onNodeWithTag("slider")

    private fun showSlider(
        initial: List<Float>,
        step: Float,
        minStepsBetweenThumbs: Int = 0,
        acceptChange: (List<Float>) -> List<Float> = { it },
        preset: StylePreset = StylePreset.Nova,
    ) {
        rule.setContent {
            Theme(preset = preset, minTouchTarget = null) {
                var values by remember { mutableStateOf(initial) }
                Slider(
                    values = values,
                    onValuesChange = { requested: List<Float> ->
                        values = acceptChange(requested)
                        changes.add(requested)
                    },
                    onValuesCommit = { accepted: List<Float> -> commits.add(accepted) },
                    valueRange = 0f..100f,
                    step = step,
                    minStepsBetweenThumbs = minStepsBetweenThumbs,
                    modifier = Modifier.width(200.dp).testTag("slider"),
                )
            }
        }
    }

    private fun TouchInjectionScope.at(fraction: Float): Offset {
        // A 12.dp Nova thumb on the 200.dp control leaves a 6.dp inset at either end.
        val inset = width * 0.03f
        return Offset(inset + (width - 2 * inset) * fraction, center.y)
    }

    @Test
    fun nativeDiscreteCoarseMovementRetainsTenFineSteps() {
        showSlider(listOf(25f, 75f), step = 5f)
        slider.performTouchInput { click(at(0.25f)) }
        slider.performKeyInput { pressKey(Key.PageUp) }
        rule.runOnIdle { assertEquals(listOf(75f, 75f), changes.last()) }
        slider.performKeyInput { withKeyDown(Key.ShiftLeft) { pressKey(Key.DirectionLeft) } }
        rule.runOnIdle { assertEquals(listOf(25f, 75f), changes.last()) }
    }

    @Test
    fun directThumbGrabPreservesTheOffsetAndDoesNotJumpOnPress() {
        showSlider(listOf(25f, 60f), step = 1f)
        slider.performTouchInput {
            val offset = Offset(width * 0.02f, 0f)
            down(at(0.25f) + offset)
            moveTo(position = at(0.4f) + offset)
            up()
        }
        rule.runOnIdle { assertEquals(listOf(listOf(40f, 60f)), changes) }
    }

    @Test
    fun reversingTheSameDragLeavesThePushedThumbInPlace() {
        showSlider(listOf(25f, 50f), step = 5f)
        slider.performTouchInput {
            down(at(0.25f))
            moveTo(position = at(0.8f))
            moveTo(position = at(0.4f))
        }
        rule.waitForIdle()
        slider.performTouchInput { up() }
        rule.runOnIdle {
            assertEquals(listOf(listOf(80f, 80f), listOf(40f, 80f)), changes)
            assertEquals(listOf(listOf(40f, 80f)), commits)
        }
    }

    @Test
    fun pushingTwoThumbsReservesRoomForTheMinimumGapAtTheEndpoint() {
        showSlider(listOf(20f, 40f, 60f), step = 5f, minStepsBetweenThumbs = 2)
        slider.performTouchInput {
            down(at(0.2f))
            moveTo(position = at(0.97f))
        }
        rule.waitForIdle()
        slider.performTouchInput { up() }
        rule.runOnIdle {
            assertEquals(listOf(listOf(80f, 90f, 100f)), changes)
            assertEquals(changes, commits)
        }
    }

    @Test
    fun rejectedPointerUpdateCommitsTheControlledValues() {
        val initial = listOf(25f, 50f)
        showSlider(initial, step = 5f, acceptChange = { initial })
        slider.performTouchInput {
            down(at(0.25f))
            moveTo(position = at(0.8f))
            up()
        }
        rule.runOnIdle {
            assertEquals(listOf(listOf(80f, 80f)), changes)
            assertEquals(listOf(initial), commits)
        }
    }

    @Test
    fun translatedPointerUpdateCommitsTheControlledValues() {
        showSlider(listOf(25f, 50f), step = 5f, acceptChange = { listOf(it.first() - 10f, it.last()) })
        slider.performTouchInput {
            down(at(0.25f))
            moveTo(position = at(0.8f))
        }
        rule.waitForIdle()
        slider.performTouchInput { up() }
        rule.runOnIdle {
            assertEquals(listOf(listOf(80f, 80f)), changes)
            assertEquals(listOf(listOf(70f, 80f)), commits)
        }
    }

    @Test
    fun vegaKeepsItsThumbSizedCrossAxisWithoutAMinimumTouchTarget() {
        showSlider(listOf(50f), step = 0f, preset = StylePreset.Vega)
        slider.assertHeightIsEqualTo(16.dp)
    }
}
