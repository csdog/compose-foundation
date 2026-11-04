@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.progress.Progress
import com.toutiaoyan.compose.ui.components.progress.ProgressIndicator
import com.toutiaoyan.compose.ui.components.progress.ProgressLabel
import com.toutiaoyan.compose.ui.components.progress.ProgressTrack
import com.toutiaoyan.compose.ui.components.progress.ProgressValue
import com.toutiaoyan.compose.ui.components.text.Text

val progressDoc =
    ComponentDoc(
        id = "progress",
        title = "Progress",
        description =
            "Displays an indicator showing the completion progress of a task, typically displayed as a progress bar.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.progress.Progress

            Progress(value = 33f, max = 100f)
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Static (Kotlin)",
                    code = "Progress(value = 0.6f, modifier = Modifier.width(240.dp))",
                    preview = { Progress(value = 0.6f, modifier = Modifier.width(240.dp)) },
                ),
                ComponentExample(
                    title = "Default max",
                    code = "Progress(value = 0.6f, max = 1f, modifier = Modifier.width(240.dp))",
                    preview = { Progress(value = 0.6f, max = 1f, modifier = Modifier.width(240.dp)) },
                ),
                ComponentExample(
                    title = "Empty",
                    code = "Progress(value = 0f, modifier = Modifier.width(240.dp))",
                    preview = { Progress(value = 0f, modifier = Modifier.width(240.dp)) },
                ),
                ComponentExample(
                    title = "Complete",
                    code = "Progress(value = 1f, modifier = Modifier.width(240.dp))",
                    preview = { Progress(value = 1f, modifier = Modifier.width(240.dp)) },
                ),
                ComponentExample(
                    title = "Label and value",
                    code =
                        """
                        Progress(value = 0.56f, modifier = Modifier.width(240.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ProgressLabel("Upload progress")
                                ProgressValue()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Progress(value = 0.56f, modifier = Modifier.width(240.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ProgressLabel("Upload progress")
                                ProgressValue()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "With label",
                    code =
                        """
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text { "Uploading 60%" }
                            Progress(value = 0.6f, modifier = Modifier.width(240.dp))
                        }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text { "Uploading 60%" }
                            Progress(value = 0.6f, modifier = Modifier.width(240.dp))
                        }
                    },
                ),
                ComponentExample(
                    title = "Indeterminate",
                    code = "Progress(value = 0f, indeterminate = true, modifier = Modifier.width(240.dp))",
                    preview = {
                        Progress(value = 0f, indeterminate = true, modifier = Modifier.width(240.dp))
                    },
                ),
                ComponentExample(
                    title = "Default indeterminate",
                    code = "Progress(value = 0.6f, indeterminate = false, modifier = Modifier.width(240.dp))",
                    preview = {
                        Progress(value = 0.6f, indeterminate = false, modifier = Modifier.width(240.dp))
                    },
                ),
                ComponentExample(
                    title = "Max",
                    code = "Progress(value = 60f, max = 100f, modifier = Modifier.width(240.dp))",
                    preview = {
                        Progress(value = 60f, max = 100f, modifier = Modifier.width(240.dp))
                    },
                ),
                ComponentExample(
                    title = "Value label",
                    code =
                        """
                        Progress(
                            value = 0.6f,
                            modifier = Modifier.width(240.dp),
                            getValueLabel = { "${'$'}{(it * 100).toInt()} percent" },
                        )
                        """.trimIndent(),
                    preview = {
                        Progress(
                            value = 0.6f,
                            modifier = Modifier.width(240.dp),
                            getValueLabel = { "${(it * 100).toInt()} percent" },
                        )
                    },
                ),
                ComponentExample(
                    title = "Default getValueLabel",
                    code = "Progress(value = 0.6f, getValueLabel = null, modifier = Modifier.width(240.dp))",
                    preview = {
                        Progress(value = 0.6f, getValueLabel = null, modifier = Modifier.width(240.dp))
                    },
                ),
                ComponentExample(
                    title = "Indicator",
                    code =
                        """
                        ProgressTrack(modifier = Modifier.width(240.dp)) {
                            ProgressIndicator(value = 0.6f)
                        }
                        """.trimIndent(),
                    preview = {
                        ProgressTrack(modifier = Modifier.width(240.dp)) {
                            ProgressIndicator(value = 0.6f)
                        }
                    },
                ),
            ),
    )
        .let { native -> native.copy(examples = progressOfficialExamples + native.examples) }
