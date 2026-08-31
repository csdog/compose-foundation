@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardFooter
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import kotlin.test.Test

class QuestionnaireScreenshotTest : ScreenshotTest() {
    private fun default(darkTheme: Boolean) {
        snapshot("questionnaire_default", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), onSubmit = { }) {
                QuestionnaireProgress()
                QuestionnaireItem("direction", required = true) {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireDescription("Choose a direction or describe another task.")
                    QuestionnaireChoices {
                        QuestionnaireChoice(
                            "timeline",
                            "Tool call timeline",
                            description = "Show what the agent ran and what came back.",
                        )
                        QuestionnaireChoice(
                            "checkpoints",
                            "Approval checkpoints",
                            description = "Ask before sensitive or destructive actions.",
                        )
                        QuestionnaireChoice(
                            "handoffs",
                            "Sub-agent handoffs",
                            description = "Make delegated work and results easier to follow.",
                        )
                    }
                    QuestionnaireError()
                }
                QuestionnaireItem("detail") {
                    QuestionnaireTitle("How much detail should it include?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("focused", "Focused")
                        QuestionnaireChoice("complete", "Complete flow")
                    }
                }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun default_light() = default(darkTheme = false)

    @Test
    fun default_dark() = default(darkTheme = true)

    private fun shortcuts(darkTheme: Boolean) {
        snapshot("questionnaire_shortcuts", darkTheme = darkTheme) {
            Questionnaire(
                modifier = Modifier.width(360.dp),
                shortcuts = QuestionnaireShortcuts.Letters,
            ) {
                QuestionnaireProgress()
                QuestionnaireItem("review", required = true) {
                    QuestionnaireTitle("What should the agent review?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("api", "Public API")
                        QuestionnaireChoice("tests", "Test coverage")
                        QuestionnaireChoice("docs", "Architecture docs")
                    }
                }
                QuestionnaireActions {
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun shortcuts_light() = shortcuts(darkTheme = false)

    @Test
    fun shortcuts_dark() = shortcuts(darkTheme = true)

    private fun resume(darkTheme: Boolean) {
        snapshot("questionnaire_resume", darkTheme = darkTheme) {
            Questionnaire(
                modifier = Modifier.width(360.dp),
                defaultItem = "detail",
            ) {
                QuestionnaireProgress()
                QuestionnaireItem("direction", required = true) {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("timeline", "Tool call timeline")
                        QuestionnaireChoice("checkpoints", "Approval checkpoints")
                    }
                    QuestionnaireError()
                }
                QuestionnaireItem("detail") {
                    QuestionnaireTitle("How much detail should it include?")
                    QuestionnaireDescription("Resumed on this question with a default answer.")
                    QuestionnaireChoices {
                        QuestionnaireChoice("focused", "Focused")
                        QuestionnaireChoice(
                            "complete",
                            "Complete flow",
                            defaultChecked = true,
                        )
                    }
                }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun resume_light() = resume(darkTheme = false)

    @Test
    fun resume_dark() = resume(darkTheme = true)

    private fun customValidation(darkTheme: Boolean) {
        snapshot("questionnaire_custom_validation", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), onSubmit = { }) {
                QuestionnaireProgress()
                QuestionnaireItem(
                    "email",
                    required = true,
                    invalid = true,
                    requiredMessage = "Enter a company email.",
                ) {
                    QuestionnaireTitle("Work email")
                    QuestionnaireDescription("Must be an @acme.com address.")
                    QuestionnaireInput(placeholder = "you@acme.com")
                    QuestionnaireError()
                }
                QuestionnaireActions {
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun custom_validation_light() = customValidation(darkTheme = false)

    @Test
    fun custom_validation_dark() = customValidation(darkTheme = true)

    private fun animate(darkTheme: Boolean) {
        snapshot("questionnaire_animate", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), animate = true, onSubmit = { }) {
                QuestionnaireProgress()
                QuestionnaireItem("direction", required = true) {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireDescription("Choose a direction or describe another task.")
                    QuestionnaireChoices {
                        QuestionnaireChoice("timeline", "Tool call timeline")
                        QuestionnaireChoice("checkpoints", "Approval checkpoints")
                        QuestionnaireChoice("handoffs", "Sub-agent handoffs")
                    }
                }
                QuestionnaireActions {
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun animate_light() = animate(darkTheme = false)

    @Test
    fun animate_dark() = animate(darkTheme = true)

    private fun itemStatus(darkTheme: Boolean) {
        snapshot("questionnaire_status", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), onSubmit = { }) {
                val state = questionnaireState()
                QuestionnaireProgress()
                QuestionnaireItem("direction") {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireDescription("Status: ${state.status.name.lowercase()}")
                    QuestionnaireChoices {
                        QuestionnaireChoice("timeline", "Tool call timeline")
                        QuestionnaireChoice("checkpoints", "Approval checkpoints")
                    }
                }
                QuestionnaireActions {
                    QuestionnaireSkip()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun status_light() = itemStatus(darkTheme = false)

    @Test
    fun status_dark() = itemStatus(darkTheme = true)

    private fun customProgress(darkTheme: Boolean) {
        snapshot("questionnaire_custom_progress", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), onSubmit = { }) {
                QuestionnaireProgress { state ->
                    "Checkpoint ${state.current} of ${state.total}"
                }
                QuestionnaireItem("scope", required = true) {
                    QuestionnaireTitle("How large is the change?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("patch", "Small patch")
                        QuestionnaireChoice("feature", "Feature-sized change")
                        QuestionnaireChoice("cross", "Cross-package change")
                    }
                }
                QuestionnaireItem("verify") {
                    QuestionnaireTitle("How should it be verified?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("tests", "Run tests")
                        QuestionnaireChoice("manual", "Manual smoke test")
                    }
                }
                QuestionnaireActions {
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun custom_progress_light() = customProgress(darkTheme = false)

    @Test
    fun custom_progress_dark() = customProgress(darkTheme = true)

    private fun navigationState(darkTheme: Boolean) {
        snapshot("questionnaire_navigation_state", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), onSubmit = { }) {
                val state = questionnaireState()
                QuestionnaireProgress()
                QuestionnaireItem("modify", required = true) {
                    QuestionnaireTitle("What may the agent modify?")
                    QuestionnaireDescription("Next is disabled until an answer is selected.")
                    QuestionnaireChoices {
                        QuestionnaireChoice("files", "Project files")
                        QuestionnaireChoice("tests", "Project files and tests")
                        QuestionnaireChoice("config", "Files, tests, and configuration")
                    }
                }
                QuestionnaireItem("verify") {
                    QuestionnaireTitle("How should it be verified?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("tests", "Run tests")
                        QuestionnaireChoice("manual", "Manual smoke test")
                    }
                }
                QuestionnaireActions {
                    QuestionnaireNext(enabled = state.status != QuestionnaireItemStatus.Unanswered)
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun navigation_state_light() = navigationState(darkTheme = false)

    @Test
    fun navigation_state_dark() = navigationState(darkTheme = true)

    private fun card(darkTheme: Boolean) {
        snapshot("questionnaire_card", darkTheme = darkTheme) {
            Questionnaire(onSubmit = { }) {
                Card(modifier = Modifier.width(360.dp)) {
                    CardHeader {
                        QuestionnaireTitle("What should the agent work on?")
                        QuestionnaireDescription("Choose the task that should be handled next.")
                    }
                    CardContent {
                        QuestionnaireProgress()
                        QuestionnaireItem("task", required = true) {
                            QuestionnaireChoices {
                                QuestionnaireChoice("tests", "Fix the failing tests")
                                QuestionnaireChoice("refactor", "Refactor the data layer")
                                QuestionnaireChoice("docs", "Update the integration guide")
                            }
                        }
                        QuestionnaireItem("verify") {
                            QuestionnaireChoices {
                                QuestionnaireChoice("tests", "Run tests")
                                QuestionnaireChoice("manual", "Manual smoke test")
                            }
                        }
                    }
                    CardFooter {
                        QuestionnaireNext()
                        QuestionnaireSubmit()
                    }
                }
            }
        }
    }

    @Test
    fun card_light() = card(darkTheme = false)

    @Test
    fun card_dark() = card(darkTheme = true)

    private fun dialog(darkTheme: Boolean) {
        snapshot("questionnaire_dialog", darkTheme = darkTheme) {
            Dialog(open = true, onOpenChange = {}) {
                DialogContent {
                    DialogHeader(
                        "Clarification",
                        description = "Answer so the agent can continue. Close is host-owned.",
                    )
                    Questionnaire(onSubmit = { }) {
                        QuestionnaireProgress()
                        QuestionnaireItem("task", required = true) {
                            QuestionnaireTitle("What should the agent work on?")
                            QuestionnaireChoices {
                                QuestionnaireChoice("tests", "Fix the failing tests")
                                QuestionnaireChoice("refactor", "Refactor the data layer")
                                QuestionnaireChoice("docs", "Update the integration guide")
                            }
                        }
                        QuestionnaireItem("verify") {
                            QuestionnaireChoices {
                                QuestionnaireChoice("tests", "Run tests")
                                QuestionnaireChoice("manual", "Manual smoke test")
                            }
                        }
                        DialogFooter {
                            QuestionnaireNext()
                            QuestionnaireSubmit()
                        }
                    }
                }
            }
        }
    }

    @Test
    fun dialog_light() = dialog(darkTheme = false)

    @Test
    fun dialog_dark() = dialog(darkTheme = true)

    private fun itemsCollection(darkTheme: Boolean) {
        val items =
            listOf(
                QuestionnaireItemDefinition(
                    name = "prototype",
                    required = true,
                    prompt = "What should we prototype next?",
                    description = "Choose a direction or write your own.",
                    choices =
                        listOf(
                            QuestionnaireChoiceDefinition(
                                "delegation",
                                "Delegation",
                                "Show how work moves to a specialist.",
                            ),
                            QuestionnaireChoiceDefinition(
                                "questions",
                                "Question prompts",
                                "Show choices while the interface waits.",
                            ),
                            QuestionnaireChoiceDefinition("both", "Both together"),
                        ),
                    input = QuestionnaireInputDefinition(placeholder = "Type another answer…"),
                ),
                QuestionnaireItemDefinition(
                    name = "detail",
                    prompt = "How much detail should it include?",
                    description = "Skip this if you are not sure yet.",
                    choices =
                        listOf(
                            QuestionnaireChoiceDefinition("focused", "Focused"),
                            QuestionnaireChoiceDefinition("complete", "Complete flow"),
                        ),
                ),
            )
        snapshot("questionnaire_items", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), items = items, onSubmit = { }) {
                QuestionnaireProgress()
                items.forEach { question ->
                    QuestionnaireItem(
                        question.name,
                        required = question.required,
                    ) {
                        QuestionnaireTitle(question.prompt)
                        question.description?.let { QuestionnaireDescription(it) }
                        QuestionnaireChoices {
                            question.choices.forEach { choice ->
                                QuestionnaireChoice(
                                    choice.value,
                                    choice.label,
                                    description = choice.description,
                                )
                            }
                            question.input?.let { QuestionnaireInput(placeholder = it.placeholder) }
                        }
                        QuestionnaireError()
                    }
                }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireSkip()
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun items_light() = itemsCollection(darkTheme = false)

    @Test
    fun items_dark() = itemsCollection(darkTheme = true)

    private fun choiceParts(darkTheme: Boolean) {
        snapshot("questionnaire_choice_parts", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), onSubmit = { }) {
                QuestionnaireProgress()
                QuestionnaireItem("direction", required = true) {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireDescription("Compose ChoiceInput, ChoiceLabel, and ChoiceDescription.")
                    QuestionnaireChoices {
                        QuestionnaireChoice("timeline") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                QuestionnaireChoiceInput()
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    QuestionnaireChoiceLabel("Tool call timeline")
                                    QuestionnaireChoiceDescription(
                                        "Show what the agent ran and what came back.",
                                    )
                                }
                            }
                        }
                        QuestionnaireChoice("checkpoints") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                QuestionnaireChoiceInput()
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    QuestionnaireChoiceLabel("Approval checkpoints")
                                    QuestionnaireChoiceDescription(
                                        "Ask before sensitive or destructive actions.",
                                    )
                                }
                            }
                        }
                    }
                }
                QuestionnaireActions {
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun choice_parts_light() = choiceParts(darkTheme = false)

    @Test
    fun choice_parts_dark() = choiceParts(darkTheme = true)

    private fun resetDraft(darkTheme: Boolean) {
        snapshot("questionnaire_reset", darkTheme = darkTheme) {
            Questionnaire(
                modifier = Modifier.width(360.dp),
                defaultItem = "detail",
                onSubmit = { },
            ) {
                QuestionnaireProgress()
                QuestionnaireItem("direction", required = true) {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("timeline", "Tool call timeline")
                        QuestionnaireChoice("checkpoints", "Approval checkpoints")
                    }
                }
                QuestionnaireItem("detail") {
                    QuestionnaireTitle("How should the migration be verified?")
                    QuestionnaireDescription("These checks were selected during the previous session.")
                    QuestionnaireChoices {
                        QuestionnaireChoice("tests", "Run migration tests")
                        QuestionnaireChoice("types", "Run the typecheck", defaultChecked = true)
                        QuestionnaireChoice("manual", "Perform a manual smoke test")
                    }
                }
                QuestionnaireActions {
                    QuestionnaireReset()
                    QuestionnairePrevious()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun reset_light() = resetDraft(darkTheme = false)

    @Test
    fun reset_dark() = resetDraft(darkTheme = true)

    private fun withoutActions(darkTheme: Boolean) {
        snapshot("questionnaire_without_actions", darkTheme = darkTheme) {
            Questionnaire(modifier = Modifier.width(360.dp), onSubmit = { }) {
                QuestionnaireProgress()
                QuestionnaireItem("kind") {
                    QuestionnaireTitle("What kind of change is this?")
                    QuestionnaireDescription("Skip this if you are not sure yet.")
                    QuestionnaireChoices {
                        QuestionnaireChoice("feature", "New feature")
                        QuestionnaireChoice("fix", "Bug fix")
                        QuestionnaireChoice("refactor", "Refactor")
                    }
                }
                QuestionnaireItem("verify") {
                    QuestionnaireTitle("How should it be verified?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("tests", "Run tests")
                        QuestionnaireChoice("manual", "Manual smoke test")
                    }
                }
                QuestionnairePrevious()
                QuestionnaireSkip()
                QuestionnaireNext()
                QuestionnaireSubmit()
            }
        }
    }

    @Test
    fun without_actions_light() = withoutActions(darkTheme = false)

    @Test
    fun without_actions_dark() = withoutActions(darkTheme = true)

    private fun disabled(darkTheme: Boolean) {
        snapshot("questionnaire_disabled", darkTheme = darkTheme) {
            Questionnaire(
                modifier = Modifier.width(360.dp),
                disabled = true,
                onSubmit = { },
            ) {
                QuestionnaireProgress()
                QuestionnaireItem("direction", required = true) {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireDescription("Choose a direction or describe another task.")
                    QuestionnaireChoices {
                        QuestionnaireChoice(
                            "timeline",
                            "Tool call timeline",
                            description = "Show what the agent ran and what came back.",
                        )
                        QuestionnaireChoice(
                            "checkpoints",
                            "Approval checkpoints",
                            description = "Ask before sensitive or destructive actions.",
                        )
                        QuestionnaireChoice(
                            "handoffs",
                            "Sub-agent handoffs",
                            description = "Make delegated work and results easier to follow.",
                        )
                    }
                    QuestionnaireError()
                }
                QuestionnaireItem("detail") {
                    QuestionnaireTitle("How much detail should it include?")
                    QuestionnaireChoices {
                        QuestionnaireChoice("focused", "Focused")
                        QuestionnaireChoice("complete", "Complete flow")
                    }
                }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test
    fun disabled_light() = disabled(darkTheme = false)

    @Test
    fun disabled_dark() = disabled(darkTheme = true)
}
