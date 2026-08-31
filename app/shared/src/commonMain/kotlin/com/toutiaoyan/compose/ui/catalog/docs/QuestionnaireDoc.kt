@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardFooter
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
import com.toutiaoyan.compose.ui.components.input.InputType
import com.toutiaoyan.compose.ui.components.questionnaire.Questionnaire
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireActions
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoice
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDescription
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceInput
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceLabel
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoices
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireDescription
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireError
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireInput
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItem
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemStatus
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireNext
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnairePrevious
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireProgress
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireReset
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireSkip
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireSubmit
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireShortcuts
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDefinition
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireInputDefinition
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireTitle
import com.toutiaoyan.compose.ui.components.questionnaire.questionnaireState

private val questionnaireCollection =
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

val questionnaireDoc =
    ComponentDoc(
        id = "questionnaire",
        title = "Questionnaire",
        description =
            "A multi-step questionnaire with single-choice, multiple-choice, freeform, " +
                "and skippable questions — matching real shadcn questionnaire.tsx.",
        usageCode =
            """
            Questionnaire(onSubmit = { answers -> }) {
                QuestionnaireProgress()
                QuestionnaireItem("direction", required = true) {
                    QuestionnaireTitle("What should the agent build next?")
                    QuestionnaireDescription("Choose a direction or describe another task.")
                    QuestionnaireChoices {
                        QuestionnaireChoice("timeline", "Tool call timeline")
                        QuestionnaireChoice("checkpoints", "Approval checkpoints")
                    }
                    QuestionnaireError()
                }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireNext()
                    QuestionnaireSubmit()
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Questionnaire(onSubmit = { }) {
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
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Questionnaire(disabled = true, onSubmit = { }) {
                            QuestionnaireProgress()
                            QuestionnaireItem("direction", required = true) {
                                QuestionnaireTitle("What should the agent build next?")
                                QuestionnaireChoices {
                                    QuestionnaireChoice("timeline", "Tool call timeline")
                                    QuestionnaireChoice("checkpoints", "Approval checkpoints")
                                }
                            }
                            QuestionnaireActions {
                                QuestionnairePrevious()
                                QuestionnaireNext()
                                QuestionnaireSubmit()
                            }
                        }
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
                ComponentExample(
                    title = "Multiple selection",
                    code =
                        """
                        QuestionnaireItem("context", multiple = true) {
                            QuestionnaireTitle("What context should the agent inspect?")
                            QuestionnaireChoices {
                                QuestionnaireChoice("source", "Relevant source files")
                                QuestionnaireChoice("tests", "Existing tests")
                                QuestionnaireChoice("docs", "Architecture documentation")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Questionnaire(modifier = Modifier.width(360.dp), defaultValue = "context") {
                            QuestionnaireProgress()
                            QuestionnaireItem("context", multiple = true) {
                                QuestionnaireTitle("What context should the agent inspect?")
                                QuestionnaireDescription("Select every source that may affect the implementation.")
                                QuestionnaireChoices {
                                    QuestionnaireChoice("source", "Relevant source files")
                                    QuestionnaireChoice("tests", "Existing tests")
                                    QuestionnaireChoice("docs", "Architecture documentation")
                                }
                            }
                            QuestionnaireActions {
                                QuestionnairePrevious()
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Freeform answer",
                    code =
                        """
                        QuestionnaireChoices {
                            QuestionnaireChoice("smallest", "Make the smallest safe change")
                            QuestionnaireInput(
                                placeholder = "Type another answer…",
                                contentDescription = "Another answer",
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Questionnaire(
                            modifier = Modifier.width(360.dp),
                            onSubmit = { },
                        ) {
                            QuestionnaireProgress()
                            QuestionnaireItem("approach") {
                                QuestionnaireTitle("How should the agent approach this refactor?")
                                QuestionnaireDescription("Choose a strategy or write a more specific instruction.")
                                QuestionnaireChoices {
                                    QuestionnaireChoice("smallest", "Make the smallest safe change")
                                    QuestionnaireChoice("module", "Refactor one module at a time")
                                    QuestionnaireInput(
                                        placeholder = "Type another answer…",
                                        contentDescription = "Another answer",
                                    )
                                }
                            }
                            QuestionnaireActions {
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Explicit skip",
                    code =
                        """
                        QuestionnaireItem("kind", required = false) {
                            QuestionnaireTitle("What kind of change is this?")
                            QuestionnaireChoices {
                                QuestionnaireChoice("feature", "New feature")
                                QuestionnaireChoice("fix", "Bug fix")
                            }
                        }
                        QuestionnaireActions {
                            QuestionnaireSkip()
                            QuestionnaireNext()
                        }
                        """.trimIndent(),
                    preview = {
                        Questionnaire(modifier = Modifier.width(360.dp)) {
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
                            QuestionnaireItem("done", required = true) {
                                QuestionnaireTitle("Ready to continue?")
                                QuestionnaireChoices {
                                    QuestionnaireChoice("yes", "Yes")
                                }
                                QuestionnaireError()
                            }
                            QuestionnaireActions {
                                QuestionnairePrevious()
                                QuestionnaireSkip()
                                QuestionnaireNext()
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shortcuts",
                    code =
                        """
                        Questionnaire(shortcuts = QuestionnaireShortcuts.Letters) {
                            QuestionnaireItem("review", required = true) {
                                QuestionnaireChoices {
                                    QuestionnaireChoice("api", "Public API")
                                    QuestionnaireChoice("tests", "Test coverage")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Questionnaire(
                            modifier = Modifier.width(360.dp),
                            shortcuts = QuestionnaireShortcuts.Letters,
                        ) {
                            QuestionnaireProgress()
                            QuestionnaireItem("review", required = true) {
                                QuestionnaireTitle("What should the agent review?")
                                QuestionnaireDescription("Use the displayed shortcut or click a choice.")
                                QuestionnaireChoices {
                                    QuestionnaireChoice("api", "Public API")
                                    QuestionnaireChoice("tests", "Test coverage")
                                    QuestionnaireChoice("docs", "Architecture docs")
                                }
                                QuestionnaireError()
                            }
                            QuestionnaireActions {
                                QuestionnaireNext()
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Conditional item",
                    code =
                        """
                        var run by remember { mutableStateOf<String?>(null) }
                        QuestionnaireChoice("cloud", "Cloud workspace",
                            checked = run == "cloud",
                            onCheckedChange = { if (it) run = "cloud" })
                        QuestionnaireItem("region", disabled = run != "cloud") { /* … */ }
                        """.trimIndent(),
                    preview = {
                        var run by remember { mutableStateOf<String?>(null) }
                        Questionnaire(modifier = Modifier.width(360.dp), defaultValue = "run") {
                            QuestionnaireProgress()
                            QuestionnaireItem("run", required = true) {
                                QuestionnaireTitle("Where should the agent run?")
                                QuestionnaireDescription("Cloud adds an environment question.")
                                QuestionnaireChoices {
                                    QuestionnaireChoice(
                                        "local",
                                        "Local workspace",
                                        checked = run == "local",
                                        onCheckedChange = { if (it) run = "local" },
                                    )
                                    QuestionnaireChoice(
                                        "cloud",
                                        "Cloud workspace",
                                        checked = run == "cloud",
                                        onCheckedChange = { if (it) run = "cloud" },
                                    )
                                }
                            }
                            QuestionnaireItem("region", disabled = run != "cloud") {
                                QuestionnaireTitle("Which cloud region?")
                                QuestionnaireChoices {
                                    QuestionnaireChoice("us", "US")
                                    QuestionnaireChoice("eu", "EU")
                                }
                            }
                            QuestionnaireActions {
                                QuestionnairePrevious()
                                QuestionnaireNext()
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Resume",
                    code =
                        """
                        Questionnaire(defaultItem = "detail") {
                            QuestionnaireItem("direction", required = true) { /* … */ }
                            QuestionnaireItem("detail") {
                                QuestionnaireChoice("complete", "Complete flow", defaultChecked = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
                ComponentExample(
                    title = "Custom validation",
                    code =
                        """
                        QuestionnaireItem(
                            "email",
                            required = true,
                            invalid = true,
                            requiredMessage = "Enter a company email.",
                            validate = { answers ->
                                answers.firstOrNull()?.takeIf { !it.endsWith("@acme.com") }
                                    ?.let { "Enter a company email." }
                            },
                        ) {
                            QuestionnaireInput(placeholder = "you@acme.com", type = InputType.Email)
                            QuestionnaireError()
                        }
                        """.trimIndent(),
                    preview = {
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
                                QuestionnaireInput(placeholder = "you@acme.com", type = InputType.Email)
                                QuestionnaireError()
                            }
                            QuestionnaireActions {
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Animated items",
                    code =
                        """
                        Questionnaire(animate = true, onSubmit = { }) {
                            QuestionnaireItem("direction", required = true) { /* … */ }
                        }
                        """.trimIndent(),
                    preview = {
                        Questionnaire(
                            modifier = Modifier.width(360.dp),
                            animate = true,
                            onSubmit = { },
                        ) {
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
                    },
                ),
                ComponentExample(
                    title = "Item status",
                    code =
                        """
                        QuestionnaireItem("direction", onStatusChange = { status -> }) { /* … */ }
                        val state = questionnaireState() // state.status
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
                ComponentExample(
                    title = "Custom progress",
                    code =
                        """
                        QuestionnaireProgress { state ->
                            "Checkpoint ${'$'}{state.current} of ${'$'}{state.total}"
                        }
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
                ComponentExample(
                    title = "Navigation state",
                    code =
                        """
                        val state = questionnaireState()
                        QuestionnaireNext(
                            enabled = state.status != QuestionnaireItemStatus.Unanswered,
                        )
                        """.trimIndent(),
                    preview = {
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
                                QuestionnaireNext(
                                    enabled = state.status != QuestionnaireItemStatus.Unanswered,
                                )
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Card",
                    code =
                        """
                        Questionnaire(onSubmit = { }) {
                            Card {
                                CardHeader {
                                    QuestionnaireTitle("What should the agent work on?")
                                    QuestionnaireDescription("Choose the task that should be handled next.")
                                }
                                CardContent {
                                    QuestionnaireProgress()
                                    QuestionnaireItem("task", required = true) { /* choices */ }
                                }
                                CardFooter {
                                    QuestionnaireNext()
                                    QuestionnaireSubmit()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
                ComponentExample(
                    title = "Dialog",
                    code =
                        """
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open clarification")
                            DialogContent {
                                DialogHeader(
                                    "Clarification",
                                    description = "Answer so the agent can continue. Close is host-owned.",
                                )
                                Questionnaire(onSubmit = { open = false }) {
                                    QuestionnaireProgress()
                                    QuestionnaireItem("task", required = true) { /* … */ }
                                    DialogFooter {
                                        QuestionnaireNext()
                                        QuestionnaireSubmit()
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Dialog(open = open, onOpenChange = { open = it }) {
                            DialogTrigger("Open clarification")
                            DialogContent {
                                DialogHeader(
                                    "Clarification",
                                    description = "Answer so the agent can continue. Close is host-owned.",
                                )
                                Questionnaire(onSubmit = { open = false }) {
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
                    },
                ),
                ComponentExample(
                    title = "Items collection",
                    code =
                        """
                        Questionnaire(items = items, onSubmit = { }) {
                            QuestionnaireProgress()
                            items.forEach { question ->
                                QuestionnaireItem(question.name, required = question.required) { /* … */ }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Questionnaire(
                            modifier = Modifier.width(360.dp),
                            items = questionnaireCollection,
                            onSubmit = { },
                        ) {
                            QuestionnaireProgress()
                            questionnaireCollection.forEach { question ->
                                QuestionnaireItem(
                                    question.name,
                                    required = question.required,
                                    multiple = question.multiple,
                                    disabled = question.disabled,
                                ) {
                                    if (question.prompt.isNotEmpty()) {
                                        QuestionnaireTitle(question.prompt)
                                    }
                                    question.description?.let { QuestionnaireDescription(it) }
                                    QuestionnaireChoices {
                                        question.choices.forEach { choice ->
                                            QuestionnaireChoice(
                                                choice.value,
                                                choice.label,
                                                description = choice.description,
                                                disabled = choice.disabled,
                                            )
                                        }
                                        question.input?.let { input ->
                                            QuestionnaireInput(placeholder = input.placeholder)
                                        }
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
                    },
                ),
                ComponentExample(
                    title = "Choice parts",
                    code =
                        """
                        QuestionnaireChoice("timeline") {
                            Row {
                                QuestionnaireChoiceInput()
                                Column {
                                    QuestionnaireChoiceLabel("Tool call timeline")
                                    QuestionnaireChoiceDescription("Show what the agent ran.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
                ComponentExample(
                    title = "Reset",
                    code =
                        """
                        Questionnaire(defaultItem = "detail") {
                            QuestionnaireChoice("complete", "Complete flow", defaultChecked = true)
                            QuestionnaireActions {
                                QuestionnaireReset()
                                QuestionnairePrevious()
                                QuestionnaireSubmit()
                            }
                        }
                        """.trimIndent(),
                    preview = {
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
                                    QuestionnaireChoice(
                                        "types",
                                        "Run the typecheck",
                                        defaultChecked = true,
                                    )
                                    QuestionnaireChoice("manual", "Perform a manual smoke test")
                                }
                            }
                            QuestionnaireActions {
                                QuestionnaireReset()
                                QuestionnairePrevious()
                                QuestionnaireSubmit()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Without Actions",
                    code =
                        """
                        Questionnaire {
                            QuestionnaireProgress()
                            QuestionnaireItem("kind") { /* … */ }
                            QuestionnairePrevious()
                            QuestionnaireSkip()
                            QuestionnaireNext()
                            QuestionnaireSubmit()
                        }
                        """.trimIndent(),
                    preview = {
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
                    },
                ),
            ),
    )
