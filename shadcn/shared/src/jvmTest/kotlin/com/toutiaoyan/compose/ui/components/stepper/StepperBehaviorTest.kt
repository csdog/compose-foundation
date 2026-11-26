@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.stepper

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
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

    @Composable
    private fun StepperScope.ReorderableSteps(order: MutableState<List<String>>) {
        order.value.forEach { title -> key(title) { StepperItem(title) } }
    }

    @Test
    fun independentlyReorderedKeyedDeclarationsFollowTheirCurrentOrder() {
        val order = mutableStateOf(listOf("First", "Second"))
        setThemedContent {
            Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) { ReorderableSteps(order) }
        }

        fun firstComesBeforeSecond(): Boolean =
            composeRule.onNodeWithText("First").getUnclippedBoundsInRoot().left <
                composeRule.onNodeWithText("Second").getUnclippedBoundsInRoot().left
        assertTrue(firstComesBeforeSecond())
        composeRule.runOnIdle { order.value = order.value.reversed() }
        assertTrue(!firstComesBeforeSecond(), "The visible steps must follow their reordered declarations")
    }

    @Test
    fun indicatorExposesProgressAndDisabledState() {
        setThemedContent {
            Stepper(currentStep = 1, modifier = Modifier.width(320.dp), showLabels = false) {
                StepperItem("First")
                StepperItem("Current")
                StepperItem("Unavailable", disabled = true)
            }
        }
        composeRule.onNodeWithText("1")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Completed"))
        composeRule.onNodeWithText("2")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Current"))
        composeRule.onNodeWithText("3")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Upcoming"))
            .assertIsNotEnabled()
    }

    @Test
    fun labeledStepsCanAdvanceAndRetreat() {
        val current = mutableStateOf(0)
        setThemedContent {
            Stepper(currentStep = current.value, modifier = Modifier.width(320.dp)) {
                StepperItem("Details")
                StepperItem("Review")
                StepperItem("Done")
            }
        }

        for (step in listOf(1, 2, 1, 0)) {
            composeRule.runOnIdle { current.value = step }
            composeRule.onNodeWithText((step + 1).toString())
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Current"))
            composeRule.onNodeWithText("Review").assertIsDisplayed()
        }
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
