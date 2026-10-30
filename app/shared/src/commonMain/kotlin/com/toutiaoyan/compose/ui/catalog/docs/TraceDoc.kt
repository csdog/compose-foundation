package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.trace.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.icons.Check

val traceDoc =
    ComponentDoc(
        id = "trace",
        title = "Trace",
        description = "Display a sequence of steps with collapsible details and status indicators.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.trace.*

            Trace {
                TraceLine {
                    TraceIcon { Icon(Check) }
                    TraceTitle("Searched the web")
                    TraceDescription("2.4s")
                }
                TraceCollapsible {
                    TraceCollapsibleTrigger { TraceLine { TraceTitle("Read 3 pages") } }
                    TraceCollapsibleContent { Text { "Source details" } }
                }
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("status", "TraceStatus?", "null"),
                ApiProperty("open", "Boolean?", "null"),
                ApiProperty("defaultOpen", "Boolean", "false"),
                ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                ApiProperty("enabled", "Boolean", "true"),
            ),
        examples =
            listOf(
                ComponentExample(
                    "Default",
                    """
                    Trace {
                        TraceLine {
                            TraceIcon { Icon(Check) }
                            TraceTitle("Searched the web")
                            TraceDescription("2.4s")
                        }
                        TraceCollapsible {
                            TraceCollapsibleTrigger { TraceLine { TraceTitle("Read 3 pages") } }
                            TraceCollapsibleContent { Text { "Reviewed documentation and examples." } }
                        }
                    }
                    """.trimIndent(),
                ) {
                    Trace(Modifier.widthIn(max = 384.dp)) {
                        TraceLine {
                            TraceIcon { Icon(Check) }
                            TraceTitle("Searched the web")
                            TraceDescription("2.4s")
                        }
                        TraceCollapsible {
                            TraceCollapsibleTrigger {
                                TraceLine {
                                    TraceIcon { Icon(Check) }
                                    TraceTitle("Read 3 pages")
                                }
                            }
                            TraceCollapsibleContent { Text { "Reviewed documentation and examples." } }
                        }
                    }
                },
                ComponentExample("Status", "TraceLine(status = TraceStatus.Running) { TraceTitle(\"Thinking\") }") {
                    Trace(Modifier.widthIn(max = 384.dp)) {
                        TraceLine(status = TraceStatus.Running) { TraceTitle("Thinking") }
                        TraceLine(status = TraceStatus.Pending) { TraceTitle("Waiting for results") }
                        TraceLine(status = TraceStatus.Error) {
                            TraceTitle("Unable to read page")
                            TraceDescription("Retry")
                        }
                    }
                },
                ComponentExample("Expanded", "TraceCollapsible(defaultOpen = true) { /* trigger and content */ }") {
                    Trace(Modifier.widthIn(max = 384.dp)) {
                        TraceCollapsible(defaultOpen = true) {
                            TraceCollapsibleTrigger {
                                TraceLine {
                                    TraceTitle("Read documentation")
                                    TraceDescription("1.2s")
                                }
                            }
                            TraceCollapsibleContent {
                                Text {
                                    (
                                        "Compared Base UI component source, spacing, and keyboard " +
                                            "behavior."
                                    )
                                }
                            }
                        }
                    }
                },
            ),
    )
