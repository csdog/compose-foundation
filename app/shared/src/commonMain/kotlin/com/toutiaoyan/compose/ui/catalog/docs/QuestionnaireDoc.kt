@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardAction
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardFooter
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
import com.toutiaoyan.compose.ui.components.questionnaire.Questionnaire
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireActions
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoice
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDescription
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
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireShortcuts
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireSkip
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireSubmit
import com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireTitle
import com.toutiaoyan.compose.ui.components.questionnaire.questionnaireState
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.toast.ToastState
import com.toutiaoyan.compose.ui.components.toast.rememberToastState
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectSize
import com.toutiaoyan.compose.ui.components.select.SelectTrigger

@Composable
private fun rememberQuestionnaireFeedback(): ToastState {
    val toast = rememberToastState()
    QuestionnaireSourceToast(toast)
    return toast
}

private fun Map<String, List<String>>.answer(
    name: String,
    fallback: String = "None",
): String = get(name).orEmpty().joinToString(", ").ifBlank { fallback }

private val questionnaireCollection =
    listOf(
        com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
            name = "direction",
            required = true,
            prompt = "What should we prototype next?",
            description = "Choose a direction or write your own.",
            choices =
                listOf(
                    com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDefinition(
                        "delegation",
                        "Delegation",
                        "Show how work moves to a specialist.",
                    ),
                    com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDefinition(
                        "questions",
                        "Question prompts",
                        "Show choices while the interface waits.",
                    ),
                    com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDefinition(
                        "both",
                        "Both together",
                    ),
                ),
            input =
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireInputDefinition(
                    "Another answer",
                    "Type another answer…",
                ),
        ),
        com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
            name = "detail",
            prompt = "How much detail should it include?",
            description = "Skip this if you are not sure yet.",
            choices =
                listOf(
                    com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDefinition(
                        "focused",
                        "Focused",
                    ),
                    com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireChoiceDefinition(
                        "complete",
                        "Complete flow",
                    ),
                ),
        ),
    )

private fun contentWidth() = Modifier.widthIn(max = 448.dp).fillMaxWidth()

@Composable
private fun MultipleSelectionPreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        onSubmit = { answers ->
            toast.show(
                "Context selected",
                description =
                    "Context: ${answers.answer("context")}",
            )
        },
    ) {
        QuestionnaireProgress()
        QuestionnaireItem("context", required = true, multiple = true) {
            QuestionnaireTitle("What context should the agent inspect?")
            QuestionnaireDescription("Select every source that may affect the implementation.")
            QuestionnaireChoices {
                QuestionnaireChoice("source", "Relevant source files")
                QuestionnaireChoice("tests", "Existing tests")
                QuestionnaireChoice("docs", "Architecture documentation")
                QuestionnaireChoice("history", "Recent commit history")
            }
            QuestionnaireError()
        }
        QuestionnaireActions { QuestionnaireSubmit("Share context") }
    }
}

@Composable
private fun FreeformAnswerPreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        onSubmit = { answers ->
            toast.show(
                "Approach selected",
                description =
                    "Approach: ${answers.answer("approach")}",
            )
        },
    ) {
        QuestionnaireProgress()
        QuestionnaireItem("approach", required = true) {
            QuestionnaireTitle("How should the agent approach this refactor?")
            QuestionnaireDescription("Choose a strategy or write a more specific instruction.")
            QuestionnaireChoices {
                QuestionnaireChoice("incremental", "Make the smallest safe change")
                QuestionnaireChoice("module", "Refactor one module at a time")
                QuestionnaireChoice("rewrite", "Replace the implementation completely")
                QuestionnaireInput(
                    placeholder = "Describe another approach…",
                    contentDescription = "Another refactoring approach",
                )
            }
            QuestionnaireError()
        }
        QuestionnaireActions { QuestionnaireSubmit("Use this approach") }
    }
}

@Composable
private fun ExplicitSkipPreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        items =
            listOf(
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition("task", required = true),
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition("constraints"),
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                    "review",
                    required = true,
                ),
            ),
        onSubmit = { answers ->
            toast.show(
                "Agent brief submitted",
                description =
                    "Task: ${answers.answer("task")} · " +
                        "Constraints: ${answers.answer("constraints", "Skipped")} · " +
                        "Review: ${answers.answer("review")}",
            )
        },
    ) {
        QuestionnaireProgress()
        QuestionnaireItem("task", required = true) {
            QuestionnaireTitle("What kind of change is this?")
            QuestionnaireDescription("Choose the category that best describes the work.")
            QuestionnaireChoices {
                QuestionnaireChoice("feature", "New feature")
                QuestionnaireChoice("fix", "Bug fix")
                QuestionnaireChoice("refactor", "Refactor")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("constraints") {
            QuestionnaireTitle("Are there any implementation constraints?")
            QuestionnaireDescription("Answer if needed, or intentionally skip this question.")
            QuestionnaireChoices {
                QuestionnaireChoice("no-dependencies", "Do not add dependencies")
                QuestionnaireChoice("no-migrations", "Do not change the database")
                QuestionnaireChoice("preserve-api", "Preserve the public API")
                QuestionnaireInput(
                    placeholder = "Describe another constraint…",
                    contentDescription = "Another implementation constraint",
                )
            }
        }
        QuestionnaireItem("review", required = true) {
            QuestionnaireTitle("How should the work be reviewed?")
            QuestionnaireDescription("Choose the checks the agent should complete before handoff.")
            QuestionnaireChoices {
                QuestionnaireChoice("tests", "Run the test suite")
                QuestionnaireChoice("diff", "Review the final diff")
                QuestionnaireChoice("both", "Tests and diff review")
            }
            QuestionnaireError()
        }
        QuestionnaireActions {
            QuestionnairePrevious()
            QuestionnaireSkip()
            QuestionnaireNext("Next")
            QuestionnaireSubmit("Submit brief")
        }
    }
}

@Composable
private fun ShortcutsPreview() {
    val labels = listOf("No shortcuts", "Letters", "Numbers")
    val modes =
        listOf(
            QuestionnaireShortcuts.None,
            QuestionnaireShortcuts.Letters,
            QuestionnaireShortcuts.Numbers,
        )
    var modeIndex by remember { mutableStateOf(1) }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Select(value = labels[modeIndex], onValueChange = { label -> modeIndex = labels.indexOf(label) }) {
            SelectTrigger(Modifier.width(160.dp), size = SelectSize.Sm, accessibilityLabel = "Shortcut style")
            SelectContent { labels.forEach { SelectItem(it) } }
        }
        Spacer(Modifier.height(8.dp))
        QuestionnaireListShortcuts(modes[modeIndex])
    }
}

@Composable
private fun QuestionnaireListShortcuts(shortcuts: QuestionnaireShortcuts) {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        items =
            listOf(
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                    "action",
                    required = true,
                ),
            ),
        shortcuts = shortcuts,
        onSubmit = { answers ->
            toast.show(
                "Next action selected",
                description =
                    "Action: ${answers.answer("action")} · " +
                        "Shortcuts: ${shortcuts.name.lowercase()}",
            )
        },
    ) {
        QuestionnaireItem("action", required = true) {
            QuestionnaireTitle("What should the agent do next?")
            QuestionnaireDescription("Use the displayed shortcut or navigate with the keyboard.")
            QuestionnaireChoices {
                QuestionnaireChoice("inspect", "Inspect the implementation")
                QuestionnaireChoice("tests", "Run the relevant tests")
                QuestionnaireChoice("patch", "Prepare the patch")
            }
            QuestionnaireError()
        }
        QuestionnaireActions { QuestionnaireSubmit("Confirm action") }
    }
}

@Composable
private fun CustomValidationPreview() {
    val toast = rememberQuestionnaireFeedback()
    var item by remember { mutableStateOf("detail") }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val definitions =
        listOf(
            com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition("detail", required = true),
            com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition("audience", required = true),
        )
    Questionnaire(
        modifier = contentWidth(),
        item = item,
        onItemChange = { item = it },
        items = definitions,
        onSubmit = { answers ->
            val detail = answers["detail"]?.firstOrNull()
            val audience = answers["audience"]?.firstOrNull()
            val next =
                if (audience == "public" && detail == "summary") {
                    mapOf("detail" to "Public answers need enough context. Choose a complete answer.")
                } else {
                    emptyMap()
                }
            errors = next
            if (next.isNotEmpty()) {
                item = "detail"
            } else {
                toast.show(
                    "Agent response configured",
                    description = "Detail: ${answers.answer("detail")} · Audience: ${answers.answer("audience")}",
                )
            }
        },
    ) {
        Card(Modifier.fillMaxWidth()) {
            QuestionnaireItem(
                "detail",
                required = true,
                invalid = errors.containsKey("detail"),
            ) {
                CardHeader {
                    QuestionnaireTitle("How much detail should the answer include?")
                    QuestionnaireDescription("Choose the response depth.")
                    CardAction {
                        QuestionnaireProgress { state -> "${state.current} / ${state.total}" }
                    }
                }
                CardContent {
                    QuestionnaireChoices {
                        QuestionnaireChoice(
                            "summary",
                            "Concise summary",
                            onCheckedChange = { if (it) errors = errors - "detail" },
                        )
                        QuestionnaireChoice(
                            "complete",
                            "Complete answer",
                            onCheckedChange = { if (it) errors = errors - "detail" },
                        )
                    }
                    QuestionnaireError(text = errors["detail"])
                }
            }
            QuestionnaireItem(
                "audience",
                required = true,
                invalid = errors.containsKey("audience"),
            ) {
                CardHeader {
                    QuestionnaireTitle("Who will read the answer?")
                    QuestionnaireDescription("Public answers require complete context.")
                    CardAction {
                        QuestionnaireProgress { state -> "${state.current} / ${state.total}" }
                    }
                }
                CardContent {
                    QuestionnaireChoices {
                        QuestionnaireChoice(
                            "team",
                            "My team",
                            onCheckedChange = { if (it) errors = errors - "audience" },
                        )
                        QuestionnaireChoice(
                            "public",
                            "Public audience",
                            onCheckedChange = { if (it) errors = errors - "audience" },
                        )
                    }
                    QuestionnaireError(text = errors["audience"])
                }
            }
            CardFooter {
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireNext("Next")
                    QuestionnaireSubmit("Validate answers")
                }
            }
        }
    }
}

private class Checkpoint(val name: String, val label: String)

private val checkpoints =
    listOf(
        Checkpoint("scope", "Change scope"),
        Checkpoint("checks", "Verification"),
        Checkpoint("output", "Final output"),
    )

@Composable
private fun ControlledPreview() {
    val toast = rememberQuestionnaireFeedback()
    var item by remember { mutableStateOf("scope") }
    val label = checkpoints.firstOrNull { it.name == item }?.label ?: item
    Questionnaire(
        modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth(),
        item = item,
        onItemChange = { item = it },
        items =
            checkpoints.map {
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                    it.name,
                    required = true,
                )
            },
        onSubmit = { answers ->
            toast.show(
                "Agent workflow configured",
                description =
                    "Scope: ${answers.answer("scope")} · " +
                        "Verification: ${answers.answer("checks")} · " +
                        "Output: ${answers.answer("output")}",
            )
        },
    ) {
        QuestionnaireProgress { state -> "Checkpoint ${state.current} of ${state.total}: $label" }
        QuestionnaireItem("scope", required = true) {
            QuestionnaireTitle("What may the agent change?")
            QuestionnaireDescription("The host stores the active checkpoint while Questionnaire navigates.")
            QuestionnaireChoices {
                QuestionnaireChoice("component", "Only the target component")
                QuestionnaireChoice("tests", "Component and related tests")
                QuestionnaireChoice("feature", "The complete feature area")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("checks", required = true) {
            QuestionnaireTitle("Which verification level should it use?")
            QuestionnaireChoices {
                QuestionnaireChoice("targeted", "Targeted tests")
                QuestionnaireChoice("package", "Package tests and typecheck")
                QuestionnaireChoice("full", "Full workspace verification")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("output", required = true) {
            QuestionnaireTitle("What should the agent return when finished?")
            QuestionnaireChoices {
                QuestionnaireChoice("summary", "Concise summary")
                QuestionnaireChoice("diff", "Summary with changed files")
                QuestionnaireChoice("handoff", "Detailed implementation handoff")
            }
            QuestionnaireError()
        }
        QuestionnaireActions {
            QuestionnairePrevious()
            QuestionnaireNext("Next")
            QuestionnaireSubmit("Save workflow")
        }
    }
}

@Composable
private fun ResumePreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        items =
            listOf(
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                    "change",
                    required = true,
                ),
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                    "verification",
                    required = true,
                    multiple = true,
                ),
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition("notes"),
            ),
        defaultItem = "verification",
        onSubmit = { answers ->
            toast.show(
                "Draft updated",
                description =
                    "Migration: ${answers.answer("change")} · " +
                        "Verification: ${answers.answer("verification")} · " +
                        "Notes: ${answers.answer("notes")}",
            )
        },
    ) {
        QuestionnaireProgress()
        QuestionnaireItem("change", required = true) {
            QuestionnaireTitle("What kind of migration is this?")
            QuestionnaireDescription("This answer was saved during the previous session.")
            QuestionnaireChoices {
                QuestionnaireChoice("incremental", "Incremental migration", defaultChecked = true)
                QuestionnaireChoice("cutover", "Single cutover")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("verification", required = true, multiple = true) {
            QuestionnaireTitle("How should the migration be verified?")
            QuestionnaireDescription("These checks were selected during the previous session.")
            QuestionnaireChoices {
                QuestionnaireChoice("tests", "Run migration tests", defaultChecked = true)
                QuestionnaireChoice("typecheck", "Run the typecheck", defaultChecked = true)
                QuestionnaireChoice("manual", "Perform a manual smoke test")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("notes") {
            QuestionnaireTitle("Anything else the agent should remember?")
            QuestionnaireDescription("This note was saved with the draft.")
            QuestionnaireInput(
                placeholder = "Additional notes",
                contentDescription = "Saved migration note",
                defaultValue = "Keep the existing public API stable.",
            )
        }
        QuestionnaireActions {
            val state = questionnaireState()
            Button(
                text = "Reset changes",
                modifier =
                    Modifier.heightIn(
                        min =
                            if (LocalWindowInfo.current.containerSize.width <
                                with(LocalDensity.current) { 640.dp.roundToPx() }
                            ) {
                                44.dp
                            } else {
                                0.dp
                            },
                    ),
                variant = ButtonVariant.Outline,
                onClick = {
                    state.reset()
                    toast.show("Saved answers restored")
                },
            )
            QuestionnairePrevious()
            QuestionnaireNext("Next")
            QuestionnaireSubmit("Update draft")
        }
    }
}

@Composable
private fun ConditionalItemsPreview() {
    val toast = rememberQuestionnaireFeedback()
    var runtime by remember { mutableStateOf("local") }
    Questionnaire(
        modifier = contentWidth(),
        defaultItem = "runtime",
        onSubmit = { answers ->
            toast.show(
                "Execution plan saved",
                description =
                    "Runtime: ${answers.answer("runtime")} · " +
                        "Environment: ${answers.answer("environment", "Not applicable")} · " +
                        "Approval: ${answers.answer("approval")}",
            )
        },
    ) {
        QuestionnaireProgress()
        QuestionnaireItem("runtime", required = true) {
            QuestionnaireTitle("Where should the agent run?")
            QuestionnaireDescription("Cloud runs add an environment question to this flow.")
            QuestionnaireChoices {
                QuestionnaireChoice(
                    "local",
                    "Local workspace",
                    checked = runtime == "local",
                    onCheckedChange = { if (it) runtime = "local" },
                )
                QuestionnaireChoice(
                    "cloud",
                    "Cloud workspace",
                    checked = runtime == "cloud",
                    onCheckedChange = { if (it) runtime = "cloud" },
                )
            }
            QuestionnaireError()
        }
        QuestionnaireItem("environment", required = true, disabled = runtime != "cloud") {
            QuestionnaireTitle("Which cloud environment should it use?")
            QuestionnaireChoices {
                QuestionnaireChoice("preview", "Preview")
                QuestionnaireChoice("staging", "Staging")
                QuestionnaireChoice("isolated", "Isolated sandbox")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("approval", required = true) {
            QuestionnaireTitle("When should the agent request approval?")
            QuestionnaireChoices {
                QuestionnaireChoice("writes", "Before writing files")
                QuestionnaireChoice("commands", "Before running commands")
                QuestionnaireChoice("sensitive", "Only for sensitive actions")
            }
            QuestionnaireError()
        }
        QuestionnaireActions {
            QuestionnairePrevious()
            QuestionnaireNext("Next")
            QuestionnaireSubmit("Save execution plan")
        }
    }
}

@Composable
private fun NavigationStatePreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        items =
            listOf(
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                    "permission",
                    required = true,
                ),
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                    "verification",
                    required = true,
                ),
            ),
        onSubmit = { answers ->
            toast.show(
                "Permissions saved",
                description =
                    "Permission: ${answers.answer("permission")} · " +
                        "Verification: ${answers.answer("verification")}",
            )
        },
    ) {
        val state = questionnaireState()
        QuestionnaireProgress()
        QuestionnaireItem("permission", required = true) {
            QuestionnaireTitle("What may the agent modify?")
            QuestionnaireDescription("Next is intentionally disabled until an answer is selected.")
            QuestionnaireChoices {
                QuestionnaireChoice("files", "Project files")
                QuestionnaireChoice("tests", "Project files and tests")
                QuestionnaireChoice("config", "Files, tests, and configuration")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("verification", required = true) {
            QuestionnaireTitle("What must pass before completion?")
            QuestionnaireChoices {
                QuestionnaireChoice("tests", "Tests")
                QuestionnaireChoice("types", "Tests and types")
                QuestionnaireChoice("all", "Tests, types, and visual QA")
            }
            QuestionnaireError()
        }
        QuestionnaireActions {
            QuestionnairePrevious()
            QuestionnaireNext(
                enabled = state.status != QuestionnaireItemStatus.Unanswered,
                text = "Next",
            )
            QuestionnaireSubmit(
                enabled = state.status != QuestionnaireItemStatus.Unanswered,
                text = "Save permissions",
            )
        }
    }
}

@Composable
private fun CustomProgressPreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        items =
            listOf("scope", "strategy", "tests", "delivery").map {
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(it, required = true)
            },
        defaultItem = "scope",
        onSubmit = { answers ->
            toast.show(
                "Pull request plan ready",
                description =
                    "Scope: ${answers.answer("scope")} · " +
                        "Commits: ${answers.answer("strategy")} · " +
                        "Tests: ${answers.answer("tests")} · " +
                        "Delivery: ${answers.answer("delivery")}",
            )
        },
    ) {
        val progress = questionnaireState()
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(progress.count) { index ->
                    Box(
                        Modifier.weight(1f).height(6.dp)
                            .background(
                                if (index <= progress.index) currentTheme.colors.primary else currentTheme.colors.muted,
                                RoundedCornerShape(50),
                            ).testTag("questionnaire-progress-segment-$index"),
                    )
                }
            }
            QuestionnaireProgress { state -> "Checkpoint ${state.current} of ${state.total}" }
        }
        QuestionnaireItem("scope", required = true) {
            QuestionnaireTitle("How large is the change?")
            QuestionnaireChoices {
                QuestionnaireChoice("small", "Small patch")
                QuestionnaireChoice("medium", "Feature-sized change")
                QuestionnaireChoice("large", "Cross-package change")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("strategy", required = true) {
            QuestionnaireTitle("How should commits be organized?")
            QuestionnaireChoices {
                QuestionnaireChoice("single", "Single commit")
                QuestionnaireChoice("logical", "Logical commits")
                QuestionnaireChoice("squash", "Squash before review")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("tests", required = true) {
            QuestionnaireTitle("Which tests should run?")
            QuestionnaireChoices {
                QuestionnaireChoice("targeted", "Targeted tests")
                QuestionnaireChoice("package", "Package suite")
                QuestionnaireChoice("workspace", "Full workspace")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("delivery", required = true) {
            QuestionnaireTitle("How should the work be delivered?")
            QuestionnaireChoices {
                QuestionnaireChoice("patch", "Patch only")
                QuestionnaireChoice("commit", "Committed locally")
                QuestionnaireChoice("branch", "Push a review branch")
            }
            QuestionnaireError()
        }
        QuestionnaireActions {
            QuestionnairePrevious()
            QuestionnaireNext("Next")
            QuestionnaireSubmit("Finish plan")
        }
    }
}

@Composable
private fun AnimatedItemsPreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        defaultItem = "task",
        animate = true,
        items =
            listOf("task", "review", "delivery").map {
                com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(it, required = true)
            },
        onSubmit = { answers ->
            toast.show(
                "Agent workflow saved",
                description =
                    "Task: ${answers.answer("task")} · " +
                        "Review: ${answers.answer("review")} · " +
                        "Delivery: ${answers.answer("delivery")}",
            )
        },
    ) {
        QuestionnaireProgress()
        QuestionnaireItem("task", required = true) {
            QuestionnaireTitle("What should the agent do?")
            QuestionnaireDescription("Choose the task for this run.")
            QuestionnaireChoices {
                QuestionnaireChoice("implement", "Implement the requested change")
                QuestionnaireChoice("debug", "Debug the current behavior")
                QuestionnaireChoice("review", "Review the implementation")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("review", required = true) {
            QuestionnaireTitle("How should the work be reviewed?")
            QuestionnaireDescription("Select the verification depth.")
            QuestionnaireChoices {
                QuestionnaireChoice("targeted", "Targeted checks")
                QuestionnaireChoice("complete", "Complete test suite")
                QuestionnaireChoice("manual", "Tests and manual QA")
            }
            QuestionnaireError()
        }
        QuestionnaireItem("delivery", required = true) {
            QuestionnaireTitle("How should the result be delivered?")
            QuestionnaireDescription("Choose the final handoff format.")
            QuestionnaireChoices {
                QuestionnaireChoice("summary", "Concise summary")
                QuestionnaireChoice("diff", "Summary and changed files")
                QuestionnaireChoice("handoff", "Detailed review handoff")
            }
            QuestionnaireError()
        }
        QuestionnaireActions {
            QuestionnairePrevious()
            QuestionnaireNext("Next")
            QuestionnaireSubmit("Save workflow")
        }
    }
}

@Composable
private fun CardPreview() {
    val toast = rememberQuestionnaireFeedback()
    Questionnaire(
        modifier = contentWidth(),
        defaultItem = "task",
        shortcuts = QuestionnaireShortcuts.Numbers,
        onSubmit = { answers ->
            toast.show(
                "Agent task created",
                description =
                    "Task: ${answers.answer("task")} · " +
                        "Handoff: ${answers.answer("output")}",
            )
        },
    ) {
        Card(Modifier.fillMaxWidth()) {
            QuestionnaireItem("task", required = true) {
                CardHeader {
                    QuestionnaireTitle("What should the agent work on?")
                    QuestionnaireDescription("Choose the task that should be handled next.")
                    CardAction { QuestionnaireProgress() }
                }
                CardContent {
                    QuestionnaireChoices {
                        QuestionnaireChoice("fix", "Fix the failing tests")
                        QuestionnaireChoice("refactor", "Refactor the data layer")
                        QuestionnaireChoice("docs", "Update the integration guide")
                    }
                    QuestionnaireError()
                }
            }
            QuestionnaireItem("output", required = true) {
                CardHeader {
                    QuestionnaireTitle("What should the final handoff include?")
                    QuestionnaireDescription("Pick the level of detail needed for review.")
                    CardAction { QuestionnaireProgress() }
                }
                CardContent {
                    QuestionnaireChoices {
                        QuestionnaireChoice("summary", "Summary only")
                        QuestionnaireChoice("files", "Summary and changed files")
                        QuestionnaireChoice("review", "Full review handoff")
                    }
                    QuestionnaireError()
                }
            }
            CardFooter {
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireNext("Next")
                    QuestionnaireSubmit("Create task")
                }
            }
        }
    }
}

@Composable
private fun DialogPreview() {
    val toast = rememberQuestionnaireFeedback()
    var open by remember { mutableStateOf(false) }
    Dialog(open = open, onOpenChange = { open = it }) {
        DialogTrigger("Open clarification")
        DialogContent {
            Questionnaire(
                modifier = Modifier.fillMaxWidth(),
                defaultItem = "scope",
                items =
                    listOf("scope", "tests").map {
                        com.toutiaoyan.compose.ui.components.questionnaire.QuestionnaireItemDefinition(
                            it,
                            required = true,
                        )
                    },
                onSubmit = { answers ->
                    toast.show(
                        "Clarification sent",
                        description =
                            "Scope: ${answers.answer("scope")} · " +
                                "Verification: ${answers.answer("tests")}",
                    )
                    open = false
                },
            ) {
                QuestionnaireItem("scope", required = true) {
                    DialogHeader {
                        QuestionnaireProgress()
                        QuestionnaireTitle("Which files are in scope?")
                        QuestionnaireDescription("Choose how broadly the agent can update the workspace.")
                    }
                    QuestionnaireChoices {
                        QuestionnaireChoice("component", "Component only")
                        QuestionnaireChoice("feature", "Complete feature directory")
                        QuestionnaireChoice("workspace", "Any related workspace file")
                    }
                    QuestionnaireError()
                }
                QuestionnaireItem("tests", required = true) {
                    DialogHeader {
                        QuestionnaireProgress()
                        QuestionnaireTitle("How much verification is needed?")
                        QuestionnaireDescription("Choose the checks the agent should run before handoff.")
                    }
                    QuestionnaireChoices {
                        QuestionnaireChoice("targeted", "Targeted tests")
                        QuestionnaireChoice("package", "Package tests")
                        QuestionnaireChoice("full", "Full workspace verification")
                    }
                    QuestionnaireError()
                }
                DialogFooter {
                    Button(text = "Cancel", onClick = { open = false }, variant = ButtonVariant.Outline)
                    QuestionnaireActions(Modifier.weight(1f)) {
                        QuestionnairePrevious()
                        QuestionnaireNext("Next")
                        QuestionnaireSubmit("Send answer")
                    }
                }
            }
        }
    }
}

@Composable
private fun DisabledExtensionPreview() {
    Questionnaire(modifier = contentWidth(), disabled = true, onSubmit = { }) {
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
}

@Composable
private fun ItemsCollectionExtensionPreview() {
    Questionnaire(
        modifier = contentWidth(),
        items = questionnaireCollection,
        onSubmit = { },
    ) {
        QuestionnaireProgress()
        questionnaireCollection.forEach { question ->
            QuestionnaireItem(question.name, required = question.required, multiple = question.multiple) {
                if (question.prompt.isNotEmpty()) QuestionnaireTitle(question.prompt)
                question.description?.let { QuestionnaireDescription(it) }
                QuestionnaireChoices {
                    question.choices.forEach { choice ->
                        QuestionnaireChoice(choice.value, choice.label, description = choice.description)
                    }
                    question.input?.let { input ->
                        QuestionnaireInput(contentDescription = input.label, placeholder = input.placeholder)
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
}

@Composable
private fun ChoicePartsExtensionPreview() {
    Questionnaire(modifier = contentWidth(), onSubmit = { }) {
        QuestionnaireProgress()
        QuestionnaireItem("direction", required = true) {
            QuestionnaireTitle("What should the agent build next?")
            QuestionnaireDescription("Compose ChoiceLabel and ChoiceDescription for rich rows.")
            QuestionnaireChoices {
                QuestionnaireChoice("timeline") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            QuestionnaireChoiceLabel("Tool call timeline")
                            QuestionnaireChoiceDescription("Show what the agent ran and what came back.")
                        }
                    }
                }
                QuestionnaireChoice("checkpoints") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            QuestionnaireChoiceLabel("Approval checkpoints")
                            QuestionnaireChoiceDescription("Ask before sensitive or destructive actions.")
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

@Composable
private fun ResetExtensionPreview() {
    Questionnaire(modifier = contentWidth(), defaultItem = "detail", onSubmit = { }) {
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

val questionnaireDoc =
    ComponentDoc(
        id = "questionnaire",
        title = "Questionnaire",
        description =
            "A multi-step questionnaire with single-choice, multiple-choice, freeform, " +
                "and skippable questions.",
        usageImportsCode =
            "import com.toutiaoyan.compose.ui.components.questionnaire.*",
        usageCode =
            """
            val items = listOf(
                QuestionnaireItemDefinition(
                    name = "direction",
                    required = true,
                    prompt = "What should we prototype next?",
                    description = "Choose a direction or write your own.",
                    choices = listOf(
                        QuestionnaireChoiceDefinition(
                            "delegation", "Delegation", "Show how work moves to a specialist.",
                        ),
                        QuestionnaireChoiceDefinition(
                            "questions", "Question prompts", "Show choices while the interface waits.",
                        ),
                        QuestionnaireChoiceDefinition("both", "Both together"),
                    ),
                    input = QuestionnaireInputDefinition("Another answer", "Type another answer…"),
                ),
                QuestionnaireItemDefinition(
                    name = "detail",
                    prompt = "How much detail should it include?",
                    description = "Skip this if you are not sure yet.",
                    choices = listOf(
                        QuestionnaireChoiceDefinition("focused", "Focused"),
                        QuestionnaireChoiceDefinition("complete", "Complete flow"),
                    ),
                ),
            )

            Questionnaire(items = items, onSubmit = { answers -> }) {
                QuestionnaireProgress()
                items.forEach { question ->
                    QuestionnaireItem(question.name, required = question.required) {
                        QuestionnaireTitle(question.prompt)
                        question.description?.let { QuestionnaireDescription(it) }
                        QuestionnaireChoices {
                            question.choices.forEach { choice ->
                                QuestionnaireChoice(choice.value, choice.label, description = choice.description)
                            }
                            question.input?.let { input ->
                                QuestionnaireInput(contentDescription = input.label, placeholder = input.placeholder)
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
            """.trimIndent(),
        compositionCode =
            """
            Questionnaire
            ├── QuestionnaireProgress
            ├── QuestionnaireItem
            │   ├── QuestionnaireTitle
            │   ├── QuestionnaireDescription
            │   ├── QuestionnaireChoices
            │   │   ├── QuestionnaireChoice
            │   │   └── QuestionnaireInput
            │   └── QuestionnaireError
            └── QuestionnaireActions
                ├── QuestionnairePrevious
                ├── QuestionnaireSkip
                ├── QuestionnaireNext
                └── QuestionnaireSubmit
            """.trimIndent(),
        beforeCompositionMarkdown =
            "Define the collection once and pass it to `Questionnaire` so the initial composition can resolve " +
                "progress, actions, and shortcuts. Use the same collection to compose the item parts.",
        compositionMarkdown =
            "Questionnaire owns the ordered items, active item, answer state, validation, progress, and navigation. " +
                "The containing page, card, dialog, or drawer owns close and cancellation behavior, persistence, " +
                "transport, and branching.",
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Initial Composition",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The upstream [headless Questionnaire]" +
                                "(https://ui.shadcn.com/docs/react/questionnaire) uses `items` to render the first " +
                                "state on the server. In Compose, the collection makes the active item, progress, " +
                                "actions, and answer shortcuts available on the first composition.",
                        ),
                    ),
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Accessibility",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "In the official web component, `QuestionnaireItem` renders a `fieldset`, and " +
                                "`QuestionnaireTitle` renders its `legend`. Compose names the active focus target " +
                                "with the question title, description and any validation error. The visible title " +
                                "is a heading, and invalid answer controls expose an error state.",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "Fixed choices expose radio or checkbox roles and their selection state. Progress is " +
                                "a named progressbar. Navigation uses buttons. Inactive items and " +
                                "actions are hidden and inert. Successful navigation focuses the newly active " +
                                "item. Failed validation focuses an available answer control.",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "Always give `QuestionnaireInput` an accessible name with a visible label, " +
                                "`contentDescription`, or a labelled field. A placeholder is not a label. See the " +
                                "[Questionnaire accessibility guide]" +
                                "(https://ui.shadcn.com/docs/react/questionnaire#accessibility) " +
                                "for labeling custom compositions and the complete keyboard behavior.",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Unstyled",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The behavior in the official `Questionnaire` comes from the `@shadcn/react` package. " +
                                "To use it directly with your own markup and styles, see " +
                                "[Questionnaire](https://ui.shadcn.com/docs/react/questionnaire) under " +
                                "`@shadcn/react`. This Compose port implements the styled parts with the same " +
                                "behavior in Kotlin, without that package.",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "API Reference",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The props, data attributes, and render states for every part are documented on the " +
                                "[@shadcn/react Questionnaire]" +
                                "(https://ui.shadcn.com/docs/react/questionnaire#api-reference) " +
                                "page. The styled components inherit the corresponding unstyled props. " +
                                "Navigation components also accept Button `size` and `variant` props, and " +
                                "`QuestionnaireActions` is a styled-only layout helper.",
                        ),
                        ComponentGuideBlock.Paragraph(
                            "Kotlin API summary: `Questionnaire` takes `item`/`onItemChange` for controlled " +
                                "navigation and `defaultItem` for the initial step, `items` for progress, " +
                                "shortcuts and validation, `shortcuts`, `animate`, `disabled` and `onSubmit` " +
                                "(answers keyed by item name). `QuestionnaireItem` takes `required`, `multiple`, " +
                                "`requiredMessage`, `disabled`, `invalid`, `validate` and `onStatusChange`.",
                        ),
                    ),
                ),
            ),
        examples =
            questionnaireOfficialExamples +
                listOf(
                    ComponentExample(
                        title = "Multiple Selection",
                        minPreviewHeight = 420.dp,
                        descriptionMarkdown =
                            "Use `multiple` for an item that accepts more than one fixed answer.",
                        code = questionnaireMultipleSelectionRecipeCode,
                        preview = { MultipleSelectionPreview() },
                    ),
                    ComponentExample(
                        title = "Freeform Answer",
                        minPreviewHeight = 420.dp,
                        descriptionMarkdown =
                            "Compose `QuestionnaireInput` with fixed choices when the user can provide another " +
                                "answer.",
                        code = questionnaireFreeformAnswerRecipeCode,
                        preview = { FreeformAnswerPreview() },
                    ),
                    ComponentExample(
                        title = "Explicit Skip",
                        minPreviewHeight = 520.dp,
                        descriptionMarkdown =
                            "Add `QuestionnaireSkip` when an optional item may be intentionally left unanswered.",
                        code = questionnaireExplicitSkipRecipeCode,
                        preview = { ExplicitSkipPreview() },
                    ),
                    ComponentExample(
                        title = "Shortcuts",
                        minPreviewHeight = 480.dp,
                        descriptionMarkdown = "Assign a letter or number key to each answer with `shortcuts`.",
                        code = questionnaireShortcutsRecipeCode,
                        preview = { ShortcutsPreview() },
                    ),
                    ComponentExample(
                        title = "Custom Validation",
                        minPreviewHeight = 520.dp,
                        descriptionMarkdown =
                            "Combine controlled navigation with an external schema to return to an invalid item " +
                                "and present its error.",
                        code = questionnaireCustomValidationRecipeCode,
                        preview = { CustomValidationPreview() },
                    ),
                    ComponentExample(
                        title = "Controlled",
                        minPreviewHeight = 520.dp,
                        descriptionMarkdown =
                            "Control the active item from host state, such as returning to an " +
                                "invalid step.",
                        code = questionnaireControlledRecipeCode,
                        preview = { ControlledPreview() },
                    ),
                    ComponentExample(
                        title = "Resume",
                        minPreviewHeight = 520.dp,
                        descriptionMarkdown =
                            "Restore a saved active item and default answers, then reset changes back to that " +
                                "saved state.",
                        code = questionnaireResumeRecipeCode,
                        preview = { ResumePreview() },
                    ),
                    ComponentExample(
                        title = "Conditional Items",
                        minPreviewHeight = 520.dp,
                        descriptionMarkdown = "Disable items that do not apply to the user's earlier answers.",
                        code = questionnaireConditionalItemsRecipeCode,
                        preview = { ConditionalItemsPreview() },
                    ),
                    ComponentExample(
                        title = "Navigation State",
                        minPreviewHeight = 480.dp,
                        descriptionMarkdown =
                            "Read item status to opt into disabled navigation and custom action styling.",
                        code = questionnaireNavigationStateRecipeCode,
                        preview = { NavigationStatePreview() },
                    ),
                    ComponentExample(
                        title = "Custom Progress",
                        minPreviewHeight = 520.dp,
                        descriptionMarkdown = "Use the Progress render state to build a custom progress indicator.",
                        code = questionnaireCustomProgressRecipeCode,
                        preview = { CustomProgressPreview() },
                    ),
                    ComponentExample(
                        title = "Animated Items",
                        minPreviewHeight = 520.dp,
                        descriptionMarkdown =
                            "Animate the active item while keeping progress and navigation stationary.",
                        code = questionnaireAnimatedItemsRecipeCode,
                        preview = { AnimatedItemsPreview() },
                    ),
                    ComponentExample(
                        title = "Card",
                        minPreviewHeight = 560.dp,
                        descriptionMarkdown =
                            "Compose Questionnaire with Card slots while keeping the question title and " +
                                "description semantic.",
                        code = questionnaireCardRecipeCode,
                        preview = { CardPreview() },
                    ),
                    ComponentExample(
                        title = "Dialog",
                        minPreviewHeight = 320.dp,
                        descriptionMarkdown =
                            "Compose Questionnaire inside a Dialog while keeping cancellation and dismissal " +
                                "host-owned.",
                        code = questionnaireDialogRecipeCode,
                        preview = { DialogPreview() },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Disabled",
                        descriptionMarkdown =
                            "`disabled = true` renders the whole questionnaire inert. The official page has no " +
                                "disabled recipe.",
                        code = "Questionnaire(disabled = true, items = items) { /* same parts */ }",
                        preview = { DisabledExtensionPreview() },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Items collection",
                        descriptionMarkdown =
                            "One `items` list drives progress, shortcuts and validation; the same parts are " +
                                "mapped from the definitions.",
                        code =
                            """
                            Questionnaire(items = items) {
                                QuestionnaireProgress()
                                items.forEach { question ->
                                    QuestionnaireItem(question.name, required = question.required) { /* … */ }
                                }
                                QuestionnaireActions { /* … */ }
                            }
                            """.trimIndent(),
                        preview = { ItemsCollectionExtensionPreview() },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Choice parts",
                        descriptionMarkdown =
                            "Compose `QuestionnaireChoiceLabel` and `QuestionnaireChoiceDescription` for rich " +
                                "choice rows.",
                        code =
                            """
                            QuestionnaireChoice("timeline") {
                                Row {
                                    Column {
                                        QuestionnaireChoiceLabel("Tool call timeline")
                                        QuestionnaireChoiceDescription("Show what the agent ran.")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = { ChoicePartsExtensionPreview() },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Reset",
                        descriptionMarkdown =
                            "`QuestionnaireReset` restores the default answers in a saved draft.",
                        code =
                            """
                            QuestionnaireActions {
                                QuestionnaireReset()
                                QuestionnairePrevious()
                                QuestionnaireSubmit()
                            }
                            """.trimIndent(),
                        preview = { ResetExtensionPreview() },
                    ),
                ),
    ).let { doc ->
        doc.copy(
            examples =
                doc.examples.map { example ->
                    if (example.title.startsWith("Kotlin extension:")) {
                        example
                    } else {
                        example.copy(
                            previewPadding = 32.dp,
                            mobilePreviewPadding = 16.dp,
                            previewVerticalAlignment = Alignment.Bottom,
                            mobilePreviewVerticalAlignment = Alignment.Top,
                        )
                    }
                },
        )
    }
