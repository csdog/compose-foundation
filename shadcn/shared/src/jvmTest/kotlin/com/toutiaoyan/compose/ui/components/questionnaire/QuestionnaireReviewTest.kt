package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.theme.Theme
import kotlin.test.*

class QuestionnaireReviewTest : ScreenshotTest() {
    @Test fun inputReceivesAndClearsItemValidationError() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire {
                QuestionnaireItem("q", required = true) {
                    QuestionnaireInput(Modifier.testTag("input"))
                    QuestionnaireError()
                }
                QuestionnaireActions { QuestionnaireSubmit() }
            }
        }
        composeRule.onNodeWithText("Submit").performClick()
        composeRule.onNodeWithTag("input").assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Error),
        )
        composeRule.onNodeWithTag("input").performTextInput("Valid answer")
        composeRule.onNodeWithTag("input").assert(
            SemanticsMatcher.keyNotDefined(SemanticsProperties.Error),
        )
    }

    @Test fun inheritedDisabledStateBlocksKeyboardAndUpdatesOnRecomposition() {
        var disabled by mutableStateOf(true)
        var submitted = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalFieldSetDisabled provides disabled) {
                Questionnaire(Modifier.testTag("root"), onSubmit = { submitted++ }) {
                    QuestionnaireItem("q", required = true) {
                        QuestionnaireChoice("a", "Answer", defaultChecked = true)
                    }
                }
            }
        }
        val root = composeRule.onNodeWithTag("root")
        root.requestFocus()
        root.performKeyInput {
            keyDown(Key.CtrlLeft)
            pressKey(Key.Enter)
            keyUp(Key.CtrlLeft)
        }
        composeRule.runOnIdle {
            assertEquals(0, submitted)
            disabled = false
        }
        root.requestFocus()
        root.performKeyInput {
            keyDown(Key.CtrlLeft)
            pressKey(Key.Enter)
            keyUp(Key.CtrlLeft)
        }
        composeRule.runOnIdle { assertEquals(1, submitted) }
    }

    @Test fun controlledChoiceShortcutsAndArrowNavigationNotifyTheHost() {
        var answer by mutableStateOf("a")
        var accept = true
        val changes = mutableListOf<String>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(Modifier.testTag("root"), shortcuts = QuestionnaireShortcuts.Letters) {
                QuestionnaireItem("q") {
                    QuestionnaireChoices {
                        listOf("a", "b").forEach { value ->
                            QuestionnaireChoice(value, value, checked = answer == value, onCheckedChange = {
                                if (it) {
                                    if (accept) answer = value
                                    changes += value
                                }
                            })
                        }
                    }
                }
            }
        }
        composeRule.onNodeWithTag("root").requestFocus()
        composeRule.onNodeWithTag("root").performKeyInput { pressKey(Key.B) }
        composeRule.onNodeWithText("b").assertIsSelected().assertIsFocused()
        composeRule.onNodeWithText("b").performKeyInput { pressKey(Key.DirectionUp) }
        composeRule.onNodeWithText("a").assertIsSelected()
        composeRule.runOnIdle {
            assertEquals(listOf("b", "a"), changes)
            accept = false
        }
        composeRule.onNodeWithTag("root").requestFocus()
        composeRule.onNodeWithTag("root").performKeyInput { pressKey(Key.B) }
        composeRule.onNodeWithText("a").assertIsSelected()
        composeRule.onNodeWithText("b").assertIsNotSelected()
        composeRule.runOnIdle { assertEquals(listOf("b", "a", "b"), changes) }
    }

    @Test fun changingMultipleToSingleRetainsOnlyFirstAnswerInCollectionOrder() {
        val controller = QuestionnaireController(null, null, {})

        fun items(multiple: Boolean) =
            listOf(
                QuestionnaireItemDefinition(
                    "q",
                    multiple = multiple,
                    choices = listOf(QuestionnaireChoiceDefinition("a"), QuestionnaireChoiceDefinition("b")),
                ),
            )
        controller.applyItems(items(true))
        controller.selectChoice("q", "b")
        controller.selectChoice("q", "a")
        controller.setInput("q", "Draft")
        controller.applyItems(items(false))
        assertEquals(listOf("a"), controller.answers()["q"])
        assertEquals("Draft", controller.inputs["q"])
    }

    @Test fun nextFocusesInvalidAnswerThenSuccessfulNavigationFocusesItem() {
        var item by mutableStateOf("one")
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire(item = item, onItemChange = { item = it }) {
                QuestionnaireItem("one", Modifier.testTag("first"), required = true) {
                    QuestionnaireChoice("a", "Answer")
                    QuestionnaireError()
                }
                QuestionnaireItem("two", Modifier.testTag("second")) { QuestionnaireTitle("Second question") }
                QuestionnaireActions { QuestionnaireNext() }
            }
        }
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Answer").assertIsFocused().performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithTag("second").assertIsFocused()
        composeRule.runOnIdle { item = "one" }
        composeRule.onNodeWithTag("first").assertIsFocused()
    }

    @Test fun resumeSubmitsUnvisitedDefaultsAndOmitsDisabledAnswers() {
        var submitted: Map<String, List<String>>? = null
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Questionnaire(Modifier.testTag("resume"), defaultItem = "last", onSubmit = { submitted = it }) {
                    QuestionnaireItem("first", required = true) {
                        QuestionnaireChoice("saved", "Saved first answer", defaultChecked = true)
                    }
                    QuestionnaireItem("disabled", disabled = true) {
                        QuestionnaireChoice("excluded", "Disabled answer", defaultChecked = true)
                    }
                    QuestionnaireItem("last", required = true) {
                        QuestionnaireInput(Modifier.testTag("active-input"), defaultValue = "Saved note")
                    }
                    QuestionnaireActions { QuestionnaireSubmit() }
                }
            }
        }
        composeRule.onNodeWithText("Saved first answer").assertDoesNotExist()
        composeRule.onNodeWithText("Disabled answer").assertDoesNotExist()
        composeRule.onNodeWithTag("resume").assertHeightIsEqualTo(80.dp).requestFocus()
        composeRule.onNodeWithTag("resume").performKeyInput { pressKey(Key.DirectionDown) }
        composeRule.onNodeWithTag("active-input").assertIsFocused()
        composeRule.onNodeWithText("Submit").performClick()
        composeRule.runOnIdle {
            assertEquals(mapOf("first" to listOf("saved"), "last" to listOf("Saved note")), submitted)
        }
    }

    @Test fun resetRestoresADefaultAfterTheOnlyAnswerWasDeselected() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Questionnaire {
                QuestionnaireItem("q", multiple = true) {
                    QuestionnaireChoice("saved", "Saved choice", defaultChecked = true)
                }
                QuestionnaireActions { QuestionnaireReset() }
            }
        }
        composeRule.onNodeWithText("Saved choice").assertIsOn().performClick()
        composeRule.onNodeWithText("Saved choice").assertIsOff()
        composeRule.onNodeWithText("Reset changes").performClick()
        composeRule.onNodeWithText("Saved choice").assertIsOn()
    }
}
