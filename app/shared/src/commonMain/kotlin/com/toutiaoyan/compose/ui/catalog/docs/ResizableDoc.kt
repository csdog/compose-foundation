@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.resizable.ResizableDefaults
import com.toutiaoyan.compose.ui.components.resizable.ResizableHandle
import com.toutiaoyan.compose.ui.components.resizable.ResizablePanel
import com.toutiaoyan.compose.ui.components.resizable.ResizablePanelGroup
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.currentTheme

val resizableDoc =
    ComponentDoc(
        id = "resizable",
        title = "Resizable",
        description = "Two panes divided by a draggable handle.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.resizable.ResizablePanelGroup
            import com.toutiaoyan.compose.ui.components.resizable.ResizableHandle
            import com.toutiaoyan.compose.ui.components.resizable.ResizablePanel

            ResizablePanelGroup { first, second, _ ->
                ResizablePanel(first, "One")
                ResizableHandle()
                ResizablePanel(second, "Two")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Sidebar and content",
                    code =
                        """
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(160.dp),
                            initialFraction = 0.35f,
                        ) { first, second, _ ->
                            Box(
                                first.background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) { Text(textStyle = TextStyles.Muted) { "Sidebar" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Content" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(160.dp),
                            initialFraction = 0.35f,
                        ) { first, second, _ ->
                            Box(
                                first.background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) { Text(textStyle = TextColors.Muted) { "Sidebar" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Content" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        ResizablePanelGroup(
                            direction = Orientation.Vertical,
                            modifier = Modifier.width(320.dp).height(200.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(
                                first.background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) { Text(textStyle = TextStyles.Muted) { "Header" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Content" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            direction = Orientation.Vertical,
                            modifier = Modifier.width(320.dp).height(200.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(
                                first.background(currentTheme.colors.muted),
                                contentAlignment = Alignment.Center,
                            ) { Text(textStyle = TextColors.Muted) { "Header" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Content" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        ResizablePanelGroup(
                            orientation = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            orientation = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        ResizablePanelGroup(
                            orientation = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            orientation = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default fraction",
                    code =
                        """
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.5f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.5f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Direction",
                    code =
                        """
                        ResizablePanelGroup(
                            direction = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            direction = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default direction",
                    code =
                        """
                        ResizablePanelGroup(
                            direction = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            direction = Orientation.Horizontal,
                            modifier = Modifier.width(320.dp).height(120.dp),
                            initialFraction = 0.4f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Without handle",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(withHandle = ResizableDefaults.WithHandle)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(withHandle = ResizableDefaults.WithHandle)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "With handle",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(withHandle = true)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(withHandle = true)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default min fraction",
                    code =
                        """
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(120.dp),
                            minFraction = 0.15f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(120.dp),
                            minFraction = 0.15f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default max fraction",
                    code =
                        """
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(120.dp),
                            maxFraction = 0.85f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(
                            modifier = Modifier.width(320.dp).height(120.dp),
                            maxFraction = 0.85f,
                        ) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default handle",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(withHandle = true)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(withHandle = true)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(disabled = true)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(disabled = true)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(disabled = false)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle(disabled = false)
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Handle",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            Box(first, contentAlignment = Alignment.Center) { Text { "One" } }
                            ResizableHandle()
                            Box(second, contentAlignment = Alignment.Center) { Text { "Two" } }
                        }
                    },
                ),
                ComponentExample(
                    title = "Panel",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            ResizablePanel(modifier = first, text = "One")
                            ResizableHandle()
                            ResizablePanel(modifier = second, text = "Two")
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            ResizablePanel(modifier = first, text = "One")
                            ResizableHandle()
                            ResizablePanel(modifier = second, text = "Two")
                        }
                    },
                ),
                ComponentExample(
                    title = "Panel group",
                    code =
                        """
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            ResizablePanel(modifier = first, text = "One")
                            ResizableHandle()
                            ResizablePanel(modifier = second, text = "Two")
                        }
                        """.trimIndent(),
                    preview = {
                        ResizablePanelGroup(modifier = Modifier.width(320.dp).height(120.dp)) { first, second, _ ->
                            ResizablePanel(modifier = first, text = "One")
                            ResizableHandle()
                            ResizablePanel(modifier = second, text = "Two")
                        }
                    },
                ),
            ),
    )
