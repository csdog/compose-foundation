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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QuestionnairePageTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(560.dp).verticalScroll(rememberScrollState())) {
                    questionnaireDoc.examples.single { it.title == title }.preview()
                }
            }
        }
        rule.waitForIdle()
    }

    @Test fun pageListsOfficialRecipesInOrder() {
        assertEquals(
            listOf(
                "Default",
                "Multiple Selection",
                "Freeform Answer",
                "Explicit Skip",
                "Shortcuts",
                "Custom Validation",
                "Controlled",
                "Resume",
                "Conditional Items",
                "Navigation State",
                "Custom Progress",
                "Animated Items",
                "Card",
                "Dialog",
                "Kotlin extension: Disabled",
                "Kotlin extension: Items collection",
                "Kotlin extension: Choice parts",
                "Kotlin extension: Reset",
            ),
            questionnaireDoc.examples.map { it.title },
        )
        assertEquals(
            "A multi-step questionnaire with single-choice, multiple-choice, freeform, " +
                "and skippable questions.",
            questionnaireDoc.description,
        )
        assertNull(questionnaireDoc.examplesHeading)
        assertFalse(questionnaireDoc.examples.first().showHeading)
        assertEquals(
            "Server Rendering",
            questionnaireDoc.beforeExamples.single().title,
        )
        assertEquals(
            listOf("Accessibility", "Unstyled", "API Reference"),
            questionnaireDoc.afterExamples.map { it.title },
        )
        assertTrue(questionnaireDoc.examples.all { it.title.startsWith("Kotlin extension: ") || !it.title.contains("Kotlin") })
    }

    @Test fun pageKeepsOfficialUsageCompositionAndGuideCopy() {
        assertTrue(questionnaireDoc.usageImportsCode!!.contains("components.questionnaire.*"))
        listOf(
            "QuestionnaireProgress",
            "QuestionnaireItem",
            "QuestionnaireTitle",
            "QuestionnaireDescription",
            "QuestionnaireChoices",
            "QuestionnaireInput",
            "QuestionnaireError",
            "QuestionnaireActions",
            "QuestionnairePrevious",
            "QuestionnaireSkip",
            "QuestionnaireNext",
            "QuestionnaireSubmit",
        ).forEach { assertTrue(questionnaireDoc.usageCode.contains(it), it) }
        val composition = questionnaireDoc.compositionCode!!
        listOf(
            "Questionnaire",
            "├── QuestionnaireProgress",
            "├── QuestionnaireItem",
            "│   ├── QuestionnaireTitle",
            "│   ├── QuestionnaireDescription",
            "│   ├── QuestionnaireChoices",
            "│   │   ├── QuestionnaireChoice",
            "│   │   └── QuestionnaireInput",
            "│   └── QuestionnaireError",
            "└── QuestionnaireActions",
        ).forEach { assertTrue(composition.contains(it), it) }
        val serverRendering = questionnaireDoc.beforeExamples.single()
        val serverText = (serverRendering.blocks.single() as com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Paragraph)
            .markdown
        assertTrue(serverText.contains("Pass `items` to server-render"))

        val accessibility = questionnaireDoc.afterExamples.first()
        assertEquals(3, accessibility.blocks.size)
        assertTrue(
            (accessibility.blocks[0] as com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Paragraph)
                .markdown.contains("renders a `fieldset`"),
        )
        val api = questionnaireDoc.afterExamples.last()
        assertEquals("API Reference", api.title)
        assertEquals(2, api.blocks.size)
    }

    @Test fun controlledRecipeTracksTheHostItem() {
        show("Controlled")
        rule.onNodeWithText("Checkpoint 1 of 3: Change scope", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Only the target component").assertIsDisplayed()
        rule.onNodeWithText("Only the target component").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Next").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Checkpoint 2 of 3: Verification", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Which verification level should it use?").assertIsDisplayed()
    }

    @Test fun customValidationReturnsToTheInvalidItem() {
        show("Custom Validation")
        rule.onNodeWithText("How much detail should the answer include?").assertIsDisplayed()
        rule.onNodeWithText("Concise summary").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Next").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Who will read the answer?").assertIsDisplayed()
        rule.onNodeWithText("Public audience").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Validate answers").performClick()
        rule.waitForIdle()
        rule.onNodeWithText(
            "Public answers need enough context. Choose a complete answer.",
        ).assertIsDisplayed()
        rule.onNodeWithText("How much detail should the answer include?").assertIsDisplayed()
    }

    @Test fun resumeStartsWithSavedAnswersAndResetRestoresThem() {
        show("Resume")
        // The saved draft resumes on the multi-select verification item with two checked answers.
        rule.onNodeWithText("How should the migration be verified?").assertIsDisplayed()
        rule.onNodeWithText("Run migration tests").assertIsOn()
        rule.onNodeWithText("Run the typecheck").assertIsOn()
        rule.onNodeWithText("Perform a manual smoke test").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Perform a manual smoke test").assertIsOn()
        rule.onNodeWithText("Reset changes").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Run migration tests").assertIsOn()
        rule.onNodeWithText("Run the typecheck").assertIsOn()
        rule.onNodeWithText("Perform a manual smoke test").assertIsOff()
    }

    @Test fun dialogRecipeOpensAndAdvancesThroughItems() {
        show("Dialog")
        rule.onNodeWithText("Open clarification").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Clarification").assertIsDisplayed()
        rule.onNodeWithText("Which files are in scope?").assertIsDisplayed()
        rule.onNodeWithText("Component only").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Next").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("How much verification is needed?").assertIsDisplayed()
    }
}
