package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.questionnaireDoc
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
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

    @Test fun previewsAlignToThePaddedStartOnPhonesAndEndOnDesktop() {
        var viewportWidth by mutableIntStateOf(390)
        val default = questionnaireDoc.examples.first()
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(viewportWidth, original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(viewportWidth, 800)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    PreviewCodeSection(
                        code = "",
                        modifier = Modifier.width((if (viewportWidth == 390) 340 else 638).dp).testTag("frame"),
                        minPreviewHeight = default.minPreviewHeight,
                        previewPadding = default.previewPadding,
                        previewVerticalAlignment = default.previewVerticalAlignment,
                        mobilePreviewPadding = default.mobilePreviewPadding,
                        mobilePreviewVerticalAlignment = default.mobilePreviewVerticalAlignment,
                        hideCode = true,
                    ) {
                        Box(Modifier.size(100.dp, 80.dp).testTag("content"))
                    }
                }
            }
        }
        val frame = rule.onNodeWithTag("frame")
        val content = rule.onNodeWithTag("content")
        assertEquals(17.dp, content.getUnclippedBoundsInRoot().top - frame.getUnclippedBoundsInRoot().top)
        rule.runOnIdle { viewportWidth = 1440 }
        assertEquals(33.dp, frame.getUnclippedBoundsInRoot().bottom - content.getUnclippedBoundsInRoot().bottom)
    }

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
            "Initial Composition",
            questionnaireDoc.beforeExamples.single().title,
        )
        assertEquals(
            listOf("Accessibility", "Unstyled", "API Reference"),
            questionnaireDoc.afterExamples.map { it.title },
        )
        assertTrue(
            questionnaireDoc.examples.all { it.title.startsWith("Kotlin extension: ") || !it.title.contains("Kotlin") },
        )
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
        val initialComposition = questionnaireDoc.beforeExamples.single()
        val initialText =
            (initialComposition.blocks.single() as com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Paragraph)
                .markdown
        assertTrue(initialText.contains("uses `items` to render the first state on the server"))
        assertTrue(initialText.contains("first composition"))

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

    @Test fun pageKeepsCompositionOwnershipAndMappedAccessibleUsage() {
        assertTrue(questionnaireDoc.compositionMarkdown!!.contains("close and cancellation behavior"))
        assertTrue(questionnaireDoc.beforeCompositionMarkdown!!.contains("Define the collection once"))
        assertTrue(questionnaireDoc.usageCode.contains("name = \"detail\""))
        assertTrue(questionnaireDoc.usageCode.contains("items.forEach { question ->"))
        assertTrue(questionnaireDoc.usageCode.contains("contentDescription = input.label"))
    }

    @Test fun customProgressIncludesFourSegmentsAndAdvancesItsCounter() {
        show("Custom Progress")
        repeat(4) { rule.onNodeWithTag("questionnaire-progress-segment-$it").assertHeightIsEqualTo(6.dp) }
        rule.onNodeWithText("Checkpoint 1 of 4").assertIsDisplayed()
        rule.onNodeWithText("Small patch").performClick()
        rule.onNodeWithText("Next").performClick()
        rule.onNodeWithText("Checkpoint 2 of 4").assertIsDisplayed()
        repeat(4) { rule.onNodeWithTag("questionnaire-progress-segment-$it").assertIsDisplayed() }
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
        rule.onNodeWithText("Complete answer").performClick()
        rule.onNodeWithText("Public answers need enough context. Choose a complete answer.").assertDoesNotExist()
        rule.onNodeWithText("Next").performClick()
        rule.onNodeWithText("Validate answers").performClick()
        rule.onNodeWithText("Agent response configured").assertIsDisplayed()
        rule.onNodeWithText("Detail: complete · Audience: public").assertIsDisplayed()
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
        rule.onNodeWithText("Saved answers restored").assertIsDisplayed()
    }

    @Test fun dialogRecipeOpensAndAdvancesThroughItems() {
        show("Dialog")
        rule.onNodeWithText("Open clarification").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Clarification").assertDoesNotExist()
        rule.onNodeWithText("Question 1 of 2").assertIsDisplayed()
        rule.onNodeWithText("Cancel").assertIsDisplayed()
        rule.onNodeWithText("Which files are in scope?").assertIsDisplayed()
        rule.onNodeWithText("Component only").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Next").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("How much verification is needed?").assertIsDisplayed()
        rule.onNodeWithText("Question 2 of 2").assertIsDisplayed()
        rule.onNodeWithText("Cancel").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("How much verification is needed?").assertDoesNotExist()
        rule.onNodeWithText("Open clarification").assertIsDisplayed().performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Which files are in scope?").assertIsDisplayed()
        rule.onNodeWithText("Question 1 of 2").assertIsDisplayed()
    }
}
