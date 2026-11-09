package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.questionnaireDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class QuestionnaireFeedbackTest(
    private val recipe: String,
    private val actions: List<String>,
    private val message: String,
    private val summary: String,
) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun recipes() =
            listOf(
                arrayOf(
                    "Multiple Selection",
                    listOf("Relevant source files", "Existing tests", "Share context"),
                    "Context selected",
                    "Context: source, tests",
                ),
                arrayOf(
                    "Freeform Answer",
                    listOf("Make the smallest safe change", "Use this approach"),
                    "Approach selected",
                    "Approach: incremental",
                ),
                arrayOf(
                    "Explicit Skip",
                    listOf(
                        "New feature",
                        "Next",
                        "Skip",
                        "Run the test suite",
                        "Submit brief",
                    ),
                    "Agent brief submitted",
                    "Task: feature · Constraints: Skipped · Review: tests",
                ),
                arrayOf(
                    "Shortcuts",
                    listOf("Inspect the implementation", "Confirm action"),
                    "Next action selected",
                    "Action: inspect · Shortcuts: letters",
                ),
                arrayOf(
                    "Custom Validation",
                    listOf("Complete answer", "Next", "My team", "Validate answers"),
                    "Agent response configured",
                    "Detail: complete · Audience: team",
                ),
                arrayOf(
                    "Controlled",
                    listOf(
                        "Only the target component",
                        "Next",
                        "Targeted tests",
                        "Next",
                        "Concise summary",
                        "Save workflow",
                    ),
                    "Agent workflow configured",
                    "Scope: component · Verification: targeted · Output: summary",
                ),
                arrayOf(
                    "Resume",
                    listOf("Next", "Update draft"),
                    "Draft updated",
                    "Migration: incremental · Verification: tests, typecheck · " +
                        "Notes: Keep the existing public API stable.",
                ),
                arrayOf(
                    "Conditional Items",
                    listOf("Next", "Before writing files", "Save execution plan"),
                    "Execution plan saved",
                    "Runtime: local · Environment: Not applicable · Approval: writes",
                ),
                arrayOf(
                    "Navigation State",
                    listOf("Project files", "Next", "Tests", "Save permissions"),
                    "Permissions saved",
                    "Permission: files · Verification: tests",
                ),
                arrayOf(
                    "Custom Progress",
                    listOf(
                        "Small patch",
                        "Next",
                        "Single commit",
                        "Next",
                        "Targeted tests",
                        "Next",
                        "Patch only",
                        "Finish plan",
                    ),
                    "Pull request plan ready",
                    "Scope: small · Commits: single · Tests: targeted · Delivery: patch",
                ),
                arrayOf(
                    "Animated Items",
                    listOf(
                        "Implement the requested change",
                        "Next",
                        "Targeted checks",
                        "Next",
                        "Concise summary",
                        "Save workflow",
                    ),
                    "Agent workflow saved",
                    "Task: implement · Review: targeted · Delivery: summary",
                ),
                arrayOf(
                    "Card",
                    listOf("Fix the failing tests", "Next", "Summary only", "Create task"),
                    "Agent task created",
                    "Task: fix · Handoff: summary",
                ),
                arrayOf(
                    "Dialog",
                    listOf(
                        "Open clarification",
                        "Component only",
                        "Next",
                        "Targeted tests",
                        "Send answer",
                    ),
                    "Clarification sent",
                    "Scope: component · Verification: targeted",
                ),
            )
    }

    @Test fun submissionDisplaysTheOfficialFeedbackAndSelectedValues() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(560.dp).verticalScroll(rememberScrollState())) {
                    questionnaireDoc.examples.single { it.title == recipe }.preview()
                }
            }
        }
        actions.forEach { rule.onNodeWithText(it).performClick() }
        rule.onNodeWithText(message).assertIsDisplayed()
        rule.onNodeWithText(summary).assertIsDisplayed()
    }
}
