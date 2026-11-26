@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.stepper

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class StepperScreenshotTest : ScreenshotTest() {
    @Composable
    private fun StepperScope.DemoSteps() {
        StepperItem("Details", "Enter the required details")
        StepperItem("Review", "Confirm your information")
        StepperItem("Done", "All set")
    }

    /** currentStep = 1 shows all three states at once: completed, active, upcoming. */
    private fun midProgress(darkTheme: Boolean) {
        snapshot("stepper_mid_progress", darkTheme = darkTheme) {
            Stepper(currentStep = 1, modifier = Modifier.width(320.dp)) { DemoSteps() }
        }
    }

    private fun compact(darkTheme: Boolean) {
        snapshot("stepper_compact", darkTheme = darkTheme) {
            Stepper(currentStep = 1, showLabels = false, modifier = Modifier.width(320.dp)) { DemoSteps() }
        }
    }

    private fun vertical(darkTheme: Boolean) {
        snapshot("stepper_vertical", darkTheme = darkTheme) {
            Stepper(
                currentStep = 1,
                orientation = StepperOrientation.Vertical,
                modifier = Modifier.width(320.dp),
            ) { DemoSteps() }
        }
    }

    @Test
    fun mid_progress_light() = midProgress(darkTheme = false)

    @Test
    fun mid_progress_dark() = midProgress(darkTheme = true)

    @Test
    fun compact_light() = compact(darkTheme = false)

    @Test
    fun compact_dark() = compact(darkTheme = true)

    @Test
    fun vertical_light() = vertical(darkTheme = false)

    @Test
    fun vertical_dark() = vertical(darkTheme = true)
}
