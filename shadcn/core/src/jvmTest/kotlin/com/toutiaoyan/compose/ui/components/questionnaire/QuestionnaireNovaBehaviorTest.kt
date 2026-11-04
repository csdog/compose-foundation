package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class QuestionnaireNovaBehaviorTest : ScreenshotTest() {
    @Test fun radioAndCheckboxRowsExposeTheirActualSelectionState() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(modifier = Modifier.width(320.dp)) {
                QuestionnaireItem("q", multiple = true) {
                    QuestionnaireChoices {
                        QuestionnaireChoice("a", "First")
                        QuestionnaireChoice("b", "Second")
                    }
                }
            }
        }
        composeRule.onNodeWithText("First").assertIsOff().assertHeightIsEqualTo(44.dp).performClick()
        composeRule.onNodeWithText("First").assertIsOn()
        composeRule.onNodeWithText("Second").performClick()
        composeRule.onNodeWithText("First").assertIsOn()
        composeRule.onNodeWithText("Second").assertIsOn()
    }

    @Test fun enterOnlyConfirmsAnAlreadySelectedChoice() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(
                modifier = Modifier.width(320.dp),
                items =
                    listOf(
                        QuestionnaireItemDefinition(
                            "first",
                            required =
                            true,
                            choices =
                                listOf(QuestionnaireChoiceDefinition("a")),
                        ),
                        QuestionnaireItemDefinition("last", required = true),
                    ),
            ) {
                QuestionnaireProgress()
                QuestionnaireItem("first", required = true) {
                    QuestionnaireChoices { QuestionnaireChoice("a", "First answer") }
                }
                QuestionnaireItem("last", required = true) { QuestionnaireTitle("Last question") }
            }
        }
        val answer = composeRule.onNodeWithText("First answer")
        answer.requestFocus()
        answer.performKeyInput { pressKey(Key.Enter) }
        answer.assertIsNotSelected()
        answer.performKeyInput { pressKey(Key.Spacebar) }
        answer.assertIsSelected()
        answer.performKeyInput { pressKey(Key.Enter) }
        composeRule.onNodeWithText("Last question").assertIsDisplayed()
        composeRule.onNodeWithText("Question 2 of 2").assertIsDisplayed()
    }

    @Test fun progressCounterMatchesItsOneBasedRangeAndLiveValueText() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(
                items =
                    listOf(
                        QuestionnaireItemDefinition("one"),
                        QuestionnaireItemDefinition("two"),
                        QuestionnaireItemDefinition("three"),
                    ),
            ) { QuestionnaireProgress(Modifier.testTag("counter")) }
        }
        composeRule.onNodeWithTag("counter").assertHeightIsEqualTo(16.dp).assertTextEquals("Question 1 of 3")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ProgressBarRangeInfo,
                    ProgressBarRangeInfo(
                        1f,
                        1f..3f,
                        1,
                    ),
                ),
            )
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Question 1 of 3"))
    }

    @Test fun emptyCollectionDoesNotInventAQuestionOrNavigation() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire {
                QuestionnaireProgress(Modifier.testTag("counter"))
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireSkip()
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
        composeRule.onNodeWithTag("counter").assertTextEquals("")
        listOf("Previous", "Skip", "Next", "Submit").forEach { composeRule.onNodeWithText(it).assertDoesNotExist() }
    }

    @Test fun firstAndRequiredActionsAreHiddenAndRequiredFailureUsesSourceText() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(modifier = Modifier.width(320.dp)) {
                QuestionnaireItem("first", required = true) {
                    QuestionnaireChoices { QuestionnaireChoice("a", "First answer") }
                    QuestionnaireError()
                }
                QuestionnaireItem("last", required = true) { QuestionnaireTitle("Last question") }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireSkip()
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
        composeRule.onNodeWithText("Previous").assertDoesNotExist()
        composeRule.onNodeWithText("Skip").assertDoesNotExist()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Choose an answer to continue.").assertIsDisplayed()
        composeRule.onNodeWithText("Last question").assertDoesNotExist()
    }

    @Test fun initialCollectionShortcutsAndChangedShortcutModeRenderWithoutSelectingAnAnswer() {
        var mode by mutableStateOf(QuestionnaireShortcuts.Letters)
        val items = listOf(QuestionnaireItemDefinition("q", choices = listOf(QuestionnaireChoiceDefinition("a"))))
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(items = items, shortcuts = mode) {
                QuestionnaireItem("q") { QuestionnaireChoices { QuestionnaireChoice("a", "Answer") } }
            }
        }
        composeRule.onNodeWithText("A", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("Answer").assertIsNotSelected()
        composeRule.runOnIdle { mode = QuestionnaireShortcuts.Numbers }
        composeRule.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("A", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test fun acceptedAndExternalControlledItemChangesActuallyUpdateTheVisibleItem() {
        var item by mutableStateOf("first")
        val items =
            listOf(
                QuestionnaireItemDefinition("first", choices = listOf(QuestionnaireChoiceDefinition("a"))),
                QuestionnaireItemDefinition("last"),
            )
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(item = item, onItemChange = { item = it }, items = items) {
                QuestionnaireItem("first") { QuestionnaireChoices { QuestionnaireChoice("a", "Answer") } }
                QuestionnaireItem("last") { QuestionnaireTitle("Last question") }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireNext()
                }
            }
        }
        composeRule.onNodeWithText("Answer").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Last question").assertIsDisplayed()
        composeRule.runOnIdle { item = "first" }
        composeRule.onNodeWithText("Answer").assertIsDisplayed().assertIsSelected()
    }

    @Test fun choiceInsetsMatchTheCssBorderPaddingIndicatorAndGap() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(Modifier.width(320.dp)) {
                QuestionnaireItem("q") {
                    QuestionnaireChoices { QuestionnaireChoice("a", "Answer", Modifier.testTag("choice")) }
                }
            }
        }
        val row = composeRule.onNodeWithTag("choice").getUnclippedBoundsInRoot()
        val label = composeRule.onNodeWithText("Answer", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(39f, (label.left - row.left).value, 0.5f)
    }
}
