package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.unit.dp

private val traceExampleTitles =
    listOf(
        "Trace Line",
        "Status",
        "Description",
        "Collapsible",
        "Open State",
        "Link",
        "Dialog",
        "Sheet",
        "Group",
        "Sources",
        "Chain of Thought",
        "Commit",
        "Whole Turn",
        "Reasoning",
        "Tool",
    )

private val traceDescriptions =
    mapOf(
        "Trace Line" to
            (
                "Use a `TraceLine` on its own for a step with no detail to show. " +
                    "It reads as plain text."
            ),
        "Status" to
            (
                "Set `status` on `TraceLine` when a step is running, has failed or hasn't " +
                    "started yet.\n\n" +
                    "None is for work that is done (\"Ran pnpm install\"). Error is for work " +
                    "that failed; say so in the title (\"Failed to run pnpm lint\"). Running " +
                    "is for work in progress, written in the present tense (\"Running pnpm " +
                    "build\"). Pending is for work that has not started, such as a plan step " +
                    "(\"Run pnpm test\")."
            ),
        "Description" to
            (
                "Put secondary text such as a diff stat, a duration or a count in a " +
                    "`TraceDescription` after the `TraceTitle`. It does not truncate."
            ),
        "Collapsible" to
            (
                "Wrap the line in a `TraceCollapsible` to show more detail when it expands. " +
                    "Put the `TraceLine` inside `TraceCollapsibleTrigger` and the detail in " +
                    "`TraceCollapsibleContent`."
            ),
        "Open State" to
            (
                "Rows start closed. Use `defaultOpen` on `TraceCollapsible` to start open, or " +
                    "`open` with `onOpenChange` to control a row."
            ),
        "Link" to
            (
                "Wrap the `TraceLine` in an anchor. For a new tab, use an arrow-up-right icon " +
                    "and sr-only \"(opens in a new tab)\"."
            ),
        "Dialog" to
            (
                "Wrap the `TraceLine` in the dialog's trigger. The dialog owns its open state, " +
                    "Escape and focus return."
            ),
        "Sheet" to "The same shape as the dialog. Drawer and Popover work the same way.",
        "Group" to
            (
                "Put rows that sit next to each other in a `Trace`. It also styles any " +
                    "trigger or link that wraps a `TraceLine`."
            ),
        "Sources" to
            (
                "A collapsible row whose detail is a `Trace` of link rows.\n\n" +
                    "See Sources for more examples."
            ),
        "Chain of Thought" to
            (
                "A chain of thought is a collapsible row with step rows inside. Set the " +
                    "parent's status and title from its children.\n\n" +
                    "See Chain of Thought and Task for more examples."
            ),
        "Commit" to
            (
                "A commit is a collapsible row with a line for each changed file. Say what " +
                    "changed in the title, Added, Edited or Deleted, and put the line counts in " +
                    "the description. Each file opens its diff in a [Sheet](#sheet)."
            ),
        "Whole Turn" to
            (
                "Three levels: the turn, a summary, and the rows. Text between steps sits " +
                    "between two `Trace` lists."
            ),
        "Reasoning" to
            (
                "Render a `reasoning` part from the AI SDK. The row is a `TraceLine` until the " +
                    "first text arrives. See Open State.\n\n" +
                    "See Reasoning for more examples."
            ),
        "Tool" to
            (
                "Render tool parts from the AI SDK. Map the tool part as in Tool.\n\n" +
                    "See Tool for more examples."
            ),
    )

private val tracePreviews: Map<String, @androidx.compose.runtime.Composable () -> Unit> =
    mapOf(
        "Trace Line" to { TraceStaticPreview() },
        "Status" to { TraceStatusPreview() },
        "Description" to { TraceDescriptionPreview() },
        "Collapsible" to { TraceCollapsiblePreview() },
        "Open State" to { TraceOpenPreview() },
        "Link" to { TraceLinkPreview() },
        "Dialog" to { TraceDialogPreview() },
        "Sheet" to { TraceSheetPreview() },
        "Group" to { TraceGroupPreview() },
        "Sources" to { TraceSourcesPreview() },
        "Chain of Thought" to { TraceChainPreview() },
        "Commit" to { TraceCommitPreview() },
        "Whole Turn" to { TraceTurnPreview() },
        "Reasoning" to { TraceReasoningPreview() },
        "Tool" to { TraceToolPreview() },
    )

private val traceCodes =
    mapOf(
        "Trace Line" to traceStaticCode,
        "Status" to traceStatusCode,
        "Description" to traceDescriptionCode,
        "Collapsible" to traceCollapsibleCode,
        "Open State" to traceOpenCode,
        "Link" to traceLinkCode,
        "Dialog" to traceDialogCode,
        "Sheet" to traceSheetCode,
        "Group" to traceGroupCode,
        "Sources" to traceSourcesCode,
        "Chain of Thought" to traceChainCode,
        "Commit" to traceCommitCode,
        "Whole Turn" to traceTurnCode,
        "Reasoning" to traceReasoningCode,
        "Tool" to traceToolCode,
    )

val traceDoc =
    ComponentDoc(
        id = "trace",
        title = "Trace",
        description = "The building block for reasoning, tool calls, sources and more.",
        examplesHeading = null,
        usageImportsCode = traceUsageImports,
        usageCode = traceUsageCode,
        compositionCode = traceCompositionCode,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Built with Trace",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "`Trace` is a list of lines. Each line has an icon, a title, and an " +
                                    "optional description and status. A line can expand to show more " +
                                    "detail, link to a page, or open a dialog or sheet."
                            ),
                        ),
                        ComponentGuideBlock.Paragraph(
                            (
                                "Every line has the same shape, so reasoning, tool calls, " +
                                    "sources and plans built from `Trace` look and behave the same."
                            ),
                        ),
                        ComponentGuideBlock.Paragraph(
                            (
                                "Use `Trace` for the steps an agent takes, like calling a tool, " +
                                    "running a subagent or editing a file. Use Message for its " +
                                    "reply, and Marker for notes and dividers between turns."
                            ),
                        ),
                        ComponentGuideBlock.Paragraph(
                            (
                                "Every pattern below is a composition of `Trace` parts. " +
                                    "See AI Elements for examples and code."
                            ),
                        ),
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "Chain of Thought — A summary that expands into the steps behind it.",
                                "Plan — The steps the agent plans to take, and how far it has got.",
                                "Queue — The agent's todo list, split into what is left and what is done.",
                                "Reasoning — The agent's thinking as it streams.",
                                "Sources — The links the agent used for a reply.",
                                "Stack Trace — A failed tool call and its stack frames.",
                                "Subagent — A subagent run, with its tool calls and result.",
                                "Task — The steps the agent takes, as it takes them.",
                                "Terminal — A command the agent runs, with its output as it streams.",
                                "Test Results — A test run by the agent, with its suites and failures.",
                                "Tool — A tool call the agent makes, from input to output.",
                            ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Features",
                    listOf(
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "One row for reasoning, tool calls, sources, plans and tasks",
                                "Rows that expand, link out, or open a dialog, sheet, drawer or popover",
                                "`running`, `error` and `pending` states, with a shimmer while running",
                                "Nested rows, so a summary expands into steps and a step into its output",
                                "Works with reasoning, tool and source parts from the AI SDK",
                                "Keyboard and screen reader support from the trigger that wraps each row",
                                "Customizable styling through `modifier` on every part",
                            ),
                        ),
                    ),
                ),
            ),
        examples =
            buildList {
                add(
                    ComponentExample(
                        title = "Default",
                        code = traceDemoCode,
                        minPreviewHeight = 0.dp,
                        previewPadding = 40.dp,
                        preview = { TraceDemoPreview() },
                    ),
                )
                traceExampleTitles.forEach { title ->
                    add(
                        ComponentExample(
                            title = title,
                            code = traceCodes.getValue(title),
                            minPreviewHeight = 0.dp,
                            previewPadding = 40.dp,
                            descriptionMarkdown = traceDescriptions.getValue(title),
                            preview = tracePreviews.getValue(title),
                        ),
                    )
                }
            },
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Accessibility",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "`Trace` adds no roles of its own. Each line gets its semantics " +
                                    "from the element that wraps it."
                            ),
                        ),
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "A `TraceLine` on its own is a span. It is read as text and is not a tab stop.",
                                "Wrap it in a `TraceCollapsibleTrigger` and it becomes a button with " +
                                    "`aria-expanded`. Wrap it in a link, or in the trigger for a " +
                                    "dialog or sheet, and it takes the keyboard and focus behavior " +
                                    "of that element.",
                                "`TraceIcon` is hidden from assistive tech, so `TraceTitle` is the " +
                                    "accessible name. Write it as a sentence that says what " +
                                    "happened, like \"Edited trace.tsx\", not just \"trace.tsx\".",
                                "When a link opens a new tab, add \"(opens in a new tab)\" to the " +
                                    "title as sr-only text (`contentDescription` on `TraceLink`).",
                                "Status is shown with color and motion, so put it in the title too. " +
                                    "Write \"Running pnpm build\" while it runs and \"Failed to run " +
                                    "pnpm build\" when it errors.",
                                "The shimmer on a running title stops when the reader prefers " +
                                    "reduced motion. The title still says what is happening.",
                                "`TraceCollapsibleContent` mutes its text. If you put a block with " +
                                    "a muted background inside it, give the block foreground text " +
                                    "so it keeps its contrast. `CodeBlock` sets its own text color.",
                            ),
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Trace",
                    "A list of rows. It also styles any trigger or link that wraps a `TraceLine`.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "TraceLine",
                    (
                        "One line: an icon and a title. It is not a button or a tab stop. " +
                            "Wrap it in a trigger or a link to make it do something."
                    ),
                    listOf(
                        ApiProperty(
                            "status",
                            "TraceStatus?",
                            "null",
                        ),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable TraceLineScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "TraceIcon",
                    (
                        "A decorative icon slot, hidden from assistive tech. Before the title " +
                            "it names the action; after it, it is the indicator."
                    ),
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "TraceTitle",
                    "One sentence on one line. It truncates and shimmers while the line is running.",
                    listOf(
                        ApiProperty("text", "String", "Required"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
                ComponentApiSection(
                    "TraceDescription",
                    (
                        "Secondary text after the title, such as a count or a duration. It " +
                            "does not truncate. `mono` is a Kotlin extension that matches the " +
                            "official `className=\"font-mono\"` for diff stats."
                    ),
                    listOf(
                        ApiProperty("text", "String", "Required"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("mono", "Boolean", "false"),
                    ),
                ),
                ComponentApiSection(
                    "TraceCollapsible",
                    (
                        "A row that expands. It accepts the props of `Collapsible`: `open` " +
                            "with `onOpenChange` controls the row, `defaultOpen` starts it open, " +
                            "and `enabled` is a Kotlin extension."
                    ),
                    listOf(
                        ApiProperty("open", "Boolean?", "null"),
                        ApiProperty("defaultOpen", "Boolean", "false"),
                        ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                        ApiProperty("enabled", "Boolean", "true"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable CollapsibleScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "TraceCollapsibleTrigger",
                    (
                        "The button that opens and closes the row. Put a `TraceLine` inside it. " +
                            "It adds a chevron after the line."
                    ),
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "TraceCollapsibleContent",
                    (
                        "The detail of a row that expands. `keepMounted` keeps the detail " +
                            "mounted while closed, so nested rows keep their open state."
                    ),
                    listOf(
                        ApiProperty("keepMounted", "Boolean", "false"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "TraceLink",
                    (
                        "Kotlin extension: the anchor that wraps a `TraceLine` on the web. " +
                            "Renders a hoverable, focusable row that opens `href` through " +
                            "`LocalUriHandler`."
                    ),
                    listOf(
                        ApiProperty("href", "String", "Required"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("contentDescription", "String?", "null"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
            ),
    )
