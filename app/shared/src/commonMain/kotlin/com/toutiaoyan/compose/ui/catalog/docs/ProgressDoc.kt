@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

val progressDoc =
    ComponentDoc(
        id = "progress",
        title = "Progress",
        description =
            "Displays an indicator showing the completion progress of a task, typically displayed as a progress bar.",
        usageImportsCode =
            "import androidx.compose.foundation.layout.*\n" +
                "import androidx.compose.ui.Modifier\n" +
                "import androidx.compose.ui.unit.dp\n" +
                "import com.toutiaoyan.compose.ui.components.progress.*",
        usageCode = "Progress(value = 33f, max = 100f)",
        examplesHeading = null,
        compositionCode =
            """
            Progress
            ├── ProgressLabel
            ├── ProgressValue
            └── ProgressTrack
                └── ProgressIndicator
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "With label and value",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Use `ProgressLabel` and `ProgressValue` to add a label and value display.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Progress(value = 56f, max = 100f, modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                                ProgressLabel("Upload progress")
                                ProgressValue()
                            }
                            """.trimIndent(),
                        ),
                    ),
                    level = 3,
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Kotlin conventions and accessibility",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Kotlin uses a fraction by default: Progress(0.6f) is 60%. " +
                                "The official recipes pass max = 100f explicitly. Use indeterminate = true " +
                                "for unknown progress; ProgressValue then omits the percentage. " +
                                "Compose exposes a normalized progress range and hides the duplicate value text. " +
                                "Native HTML naming, min/null/format/render props and screen-reader behavior " +
                                "are not equivalent APIs.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Progress(value = 0f, indeterminate = true)
                            Progress(value = 0.6f, getValueLabel = { "${'$'}{(it * 100).toInt()} percent" })
                            ProgressTrack(Modifier.width(240.dp)) { ProgressIndicator(value = 0.6f) }
                            """.trimIndent(),
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Progress",
                    "A controlled progress root that appends its track after the optional content. " +
                        "See the [Base UI Progress](https://base-ui.com/react/components/progress#api-reference) " +
                        "documentation for the original props.",
                    listOf(
                        ApiProperty("value", "Float", "required"),
                        ApiProperty("max", "Float", "1f"),
                        ApiProperty("indeterminate", "Boolean", "false"),
                        ApiProperty("getValueLabel", "((Float) -> String)?", "null"),
                        ApiProperty("modifier / style", "Modifier / Style", "default"),
                        ApiProperty("content", "(@Composable () -> Unit)?", "null"),
                    ),
                ),
                ComponentApiSection(
                    "ProgressLabel / ProgressValue",
                    "Label text and optional formatted value read the nearest Progress. " +
                        "Value is decorative to accessibility.",
                    listOf(
                        ApiProperty("ProgressLabel.text", "String", "required"),
                        ApiProperty("ProgressValue.text", "String?", "null (percentage)"),
                        ApiProperty("modifier / style", "Modifier / Style", "default"),
                    ),
                ),
                ComponentApiSection(
                    "ProgressTrack / ProgressIndicator",
                    "Use a standalone track and indicator when supplying your own track layout.",
                    listOf(
                        ApiProperty("ProgressTrack.content", "@Composable BoxScope.() -> Unit", "ProgressIndicator()"),
                        ApiProperty("ProgressIndicator.value", "Float", "nearest Progress fraction"),
                        ApiProperty("ProgressIndicator.indeterminate", "Boolean", "nearest Progress or false"),
                    ),
                ),
            ),
        examples = progressOfficialExamples,
    )
