package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.FilePen
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Terminal
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlock
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockContent
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockSize
import com.toutiaoyan.compose.ui.components.trace.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.icons.Check
import androidx.compose.ui.text.TextStyle

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
                ComponentExample(
                    "Description",
                    """
                    Trace {
                        TraceLine {
                            TraceIcon { Icon(Lucide.FilePen) }
                            TraceTitle("Edited components/settings/notification-preferences-form.tsx")
                            TraceDescription("+48 −12")
                        }
                        TraceLine {
                            TraceIcon { Icon(Lucide.Terminal) }
                            TraceTitle("Ran pnpm test")
                            TraceDescription("14s")
                        }
                    }
                    """.trimIndent(),
                ) {
                    Trace(Modifier.widthIn(max = 384.dp)) {
                        TraceLine {
                            TraceIcon { Icon(Lucide.FilePen) }
                            TraceTitle("Edited components/settings/notification-preferences-form.tsx")
                            TraceDescription("+48 −12")
                        }
                        TraceLine {
                            TraceIcon { Icon(Lucide.Terminal) }
                            TraceTitle("Ran pnpm test")
                            TraceDescription("14s")
                        }
                        TraceLine {
                            TraceIcon { Icon(Lucide.Search) }
                            TraceTitle("Searched for useChat")
                            TraceDescription("8 results")
                        }
                    }
                },
                ComponentExample(
                    "Collapsible",
                    """
                    TraceCollapsible {
                        TraceCollapsibleTrigger {
                            TraceLine {
                                TraceIcon { Icon(Lucide.Terminal) }
                                TraceTitle("Ran node --version")
                            }
                        }
                        TraceCollapsibleContent {
                            CodeBlock(size = CodeBlockSize.Sm) {
                                CodeBlockContent("${'$'} node --version\nv24.18.0", wrap = true)
                            }
                        }
                    }
                    """.trimIndent(),
                ) {
                    Column(Modifier.widthIn(max = 384.dp)) {
                        TraceCollapsible {
                            TraceCollapsibleTrigger {
                                TraceLine {
                                    TraceIcon { Icon(Lucide.Terminal) }
                                    TraceTitle("Ran node --version")
                                }
                            }
                            TraceCollapsibleContent {
                                CodeBlock(size = CodeBlockSize.Sm) {
                                    CodeBlockContent(
                                        "${'$'} node --version\nv24.18.0",
                                        wrap = true,
                                        textStyle = TextStyle(fontFamily = catalogMonoFontFamily()),
                                    )
                                }
                            }
                        }
                    }
                },
                ComponentExample(
                    "Open State",
                    """
                    var open by remember { mutableStateOf(false) }
                    Column {
                        TraceCollapsible(defaultOpen = true) { /* node output */ }
                        TraceCollapsible(open = open, onOpenChange = { open = it }) {
                            /* pnpm output */
                        }
                        Button(onClick = { open = !open }) {
                            Text(if (open) "Hide the pnpm output" else "Show the pnpm output")
                        }
                    }
                    """.trimIndent(),
                ) {
                    var open by remember { mutableStateOf(false) }
                    Column(
                        Modifier.widthIn(max = 384.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        TraceOutput(defaultOpen = true, title = "Ran node --version", output = "v24.18.0")
                        TraceOutput(
                            open = open,
                            onOpenChange = { open = it },
                            title = "Ran pnpm --version",
                            output = "10.33.4",
                        )
                        Button(
                            text = if (open) "Hide the pnpm output" else "Show the pnpm output",
                            onClick = { open = !open },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        )
                    }
                },
                ComponentExample(
                    "Group",
                    """
                    Trace {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Search) }
                            TraceTitle("Searched for minimumReleaseAge")
                        }
                        TraceLine {
                            TraceIcon { Icon(Lucide.FileText) }
                            TraceTitle("Read pnpm-workspace.yaml")
                        }
                        TraceLine {
                            TraceIcon { Icon(Lucide.Terminal) }
                            TraceTitle("Ran pnpm install")
                        }
                    }
                    """.trimIndent(),
                ) {
                    Trace(Modifier.widthIn(max = 384.dp)) {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Search) }
                            TraceTitle("Searched for minimumReleaseAge")
                        }
                        TraceLine {
                            TraceIcon { Icon(Lucide.FileText) }
                            TraceTitle("Read pnpm-workspace.yaml")
                        }
                        TraceLine {
                            TraceIcon { Icon(Lucide.Terminal) }
                            TraceTitle("Ran pnpm install")
                        }
                    }
                },
            ),
    )

@Composable
private fun TraceOutput(
    title: String,
    output: String,
    defaultOpen: Boolean = false,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
) {
    TraceCollapsible(
        defaultOpen = defaultOpen,
        open = open,
        onOpenChange = onOpenChange,
    ) {
        TraceCollapsibleTrigger {
            TraceLine {
                TraceIcon { Icon(Lucide.Terminal) }
                TraceTitle(title)
            }
        }
        TraceCollapsibleContent {
            CodeBlock(size = CodeBlockSize.Sm) {
                CodeBlockContent(
                    "${'$'} ${title.removePrefix("Ran ")}\n$output",
                    wrap = true,
                    textStyle = TextStyle(fontFamily = catalogMonoFontFamily()),
                )
            }
        }
    }
}
