@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.stepper

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import kotlin.test.assertTrue

class StepperBehaviorTest : ScreenshotTest() {
    @Composable
    private fun StepperScope.ChangingStep(title: MutableState<String>) {
        StepperItem(title.value)
    }

    @Composable
    private fun StepperScope.OptionalStep(visible: MutableState<Boolean>) {
        if (visible.value) StepperItem("Optional step")
    }

    @Test
    fun independentlyInsertedAndRemovedDeclarationUpdatesRenderedSteps() {
        val visible = mutableStateOf(false)
        setThemedContent {
            Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                StepperItem("First")
                OptionalStep(visible)
                StepperItem("Last")
            }
        }
        composeRule.onNodeWithText("Optional step").assertDoesNotExist()
        composeRule.runOnIdle { visible.value = true }
        composeRule.onNodeWithText("Optional step").assertIsDisplayed()
        val first = composeRule.onNodeWithText("First").getUnclippedBoundsInRoot()
        val optional = composeRule.onNodeWithText("Optional step").getUnclippedBoundsInRoot()
        val last = composeRule.onNodeWithText("Last").getUnclippedBoundsInRoot()
        assertTrue(first.left < optional.left && optional.left < last.left)
        composeRule.runOnIdle { visible.value = false }
        composeRule.onNodeWithText("Optional step").assertDoesNotExist()
        composeRule.onNodeWithText("First").assertIsDisplayed()
        composeRule.onNodeWithText("Last").assertIsDisplayed()
    }

    @Test
    fun independentlyRecomposedDeclarationUpdatesItsRenderedTitle() {
        val title = mutableStateOf("Initial title")
        setThemedContent {
            Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                ChangingStep(title)
                StepperItem("Review")
            }
        }
        composeRule.onNodeWithText("Initial title").assertIsDisplayed()
        composeRule.runOnIdle { title.value = "Updated title" }
        composeRule.onNodeWithText("Updated title").assertIsDisplayed()
        composeRule.onNodeWithText("Initial title").assertDoesNotExist()
    }
}
