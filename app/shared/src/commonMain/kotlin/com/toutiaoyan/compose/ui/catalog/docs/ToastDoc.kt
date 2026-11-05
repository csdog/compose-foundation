@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.toast.ToastPosition
import com.toutiaoyan.compose.ui.components.toast.ToastTheme
import com.toutiaoyan.compose.ui.components.toast.ToastVariant
import com.toutiaoyan.compose.ui.components.toast.Toaster
import com.toutiaoyan.compose.ui.components.toast.rememberToastState

val toastDoc =
    ComponentDoc(
        id = "toast",
        title = "Toast",
        description = "A succinct message that is displayed temporarily.",
        usageCode =
            """
            import androidx.compose.foundation.layout.*
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp
            import com.toutiaoyan.compose.ui.components.toast.*
            import com.toutiaoyan.compose.ui.components.button.Button

            val toastState = rememberToastState()
            Box(Modifier.fillMaxSize()) {
                Button("Save", onClick = { toastState.success("Saved") })
                Toaster(
                    state = toastState,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                )
            }
            """.trimIndent(),
        examplesHeading = null,
        examples =
            toastOfficialExamples +
                listOf(
                    ComponentExample(
                        title = "Kotlin extension: Variants",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Error", onClick = { toastState.show("Something went wrong", variant = ToastVariant.Error) })
                            Button("Warning", onClick = { toastState.show("Be careful", variant = ToastVariant.Warning) })
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Error",
                                    onClick = {
                                        toastState.show("Something went wrong", variant = ToastVariant.Error)
                                    },
                                )
                                Button(
                                    "Warning",
                                    onClick = {
                                        toastState.show("Be careful", variant = ToastVariant.Warning)
                                    },
                                )
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: explicit action close",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = {
                                    var id = -1L
                                    id = toastState.show(
                                        title = "Event created",
                                        description = "Monday, March 12 at 9:00 AM",
                                        actionLabel = "Undo",
                                        onAction = { toastState.close(id) },
                                    )
                                },
                            )
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = {
                                        var id = -1L
                                        id =
                                            toastState.show(
                                                title = "Event created",
                                                description = "Monday, March 12 at 9:00 AM",
                                                actionLabel = "Undo",
                                                onAction = { toastState.close(id) },
                                            )
                                    },
                                )
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Duration",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = {
                                    toastState.show(
                                        title = "Event created",
                                        description = "This stays for 8 seconds.",
                                        duration = 8000L,
                                    )
                                },
                            )
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = {
                                        toastState.show(
                                            title = "Event created",
                                            description = "This stays for 8 seconds.",
                                            duration = 8000L,
                                        )
                                    },
                                )
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default duration",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = {
                                    toastState.show(
                                        title = "Event created",
                                        duration = 5000L,
                                    )
                                },
                            )
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = {
                                        toastState.show(
                                            title = "Event created",
                                            duration = 5000L,
                                        )
                                    },
                                )
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Position",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, position = ToastPosition.TopEnd)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, position = ToastPosition.TopEnd)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default position",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, position = ToastPosition.BottomEnd)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, position = ToastPosition.BottomEnd)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Close button",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, closeButton = true)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, closeButton = true)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default close button",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, closeButton = true)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, closeButton = true)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Rich colors",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created", variant = ToastVariant.Success) },
                            )
                            Toaster(state = toastState, richColors = true)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = {
                                        toastState.show(title = "Event created", variant = ToastVariant.Success)
                                    },
                                )
                                Toaster(state = toastState, richColors = true)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default rich colors",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, richColors = false)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, richColors = false)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Visible toasts",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, visibleToasts = 3)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, visibleToasts = 3)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default visible toasts",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, visibleToasts = 3)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, visibleToasts = 3)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Offset",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, offset = 24.dp)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, offset = 24.dp)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default offset",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, offset = 0.dp)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, offset = 0.dp)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Gap",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, gap = 16.dp)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, gap = 16.dp)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default gap",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, gap = 12.dp)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, gap = 12.dp)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Invert",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, invert = true)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, invert = true)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default invert",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, invert = false)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = { toastState.show(title = "Event created") },
                                )
                                Toaster(state = toastState, invert = false)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Cancel action",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = {
                                toastState.show(
                                    title = "Event created",
                                    cancelLabel = "Cancel",
                                    onCancel = {},
                                )
                            })
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = {
                                        toastState.show(
                                            title = "Event created",
                                            cancelLabel = "Cancel",
                                            onCancel = {},
                                        )
                                    },
                                )
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Success helper",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Save", onClick = { toastState.success("Saved") })
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Save", onClick = { toastState.success("Saved") })
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Forced light theme",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Notify", onClick = { toastState.show("Event created") })
                            Toaster(state = toastState, theme = ToastTheme.Light)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Notify", onClick = { toastState.show("Event created") })
                                Toaster(state = toastState, theme = ToastTheme.Light)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default theme",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Notify", onClick = { toastState.show("Event created") })
                            Toaster(state = toastState, theme = ToastTheme.System)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Notify", onClick = { toastState.show("Event created") })
                                Toaster(state = toastState, theme = ToastTheme.System)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Toaster",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Viewport",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Title",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Description",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = {
                                    toastState.show(
                                        title = "Event created",
                                        description = "Monday, March 12 at 9:00 AM",
                                    )
                                },
                            )
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = {
                                        toastState.show(
                                            title = "Event created",
                                            description = "Monday, March 12 at 9:00 AM",
                                        )
                                    },
                                )
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Action",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button(
                                "Show toast",
                                onClick = {
                                    toastState.show(
                                        title = "Event created",
                                        actionLabel = "Undo",
                                        onAction = {},
                                    )
                                },
                            )
                            Toaster(state = toastState)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button(
                                    "Show toast",
                                    onClick = {
                                        toastState.show(
                                            title = "Event created",
                                            actionLabel = "Undo",
                                            onAction = {},
                                        )
                                    },
                                )
                                Toaster(state = toastState)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Close",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, closeButton = true)
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState, closeButton = true)
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Close label",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, closeButton = true, closeAriaLabel = "Close")
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState, closeButton = true, closeAriaLabel = "Close")
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default close label",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, closeButton = true, closeAriaLabel = "Close toast")
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState, closeButton = true, closeAriaLabel = "Close toast")
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Default container aria label",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, containerAriaLabel = "Notifications")
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState, containerAriaLabel = "Notifications")
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Kotlin extension: Accessible label",
                        code =
                            """
                            val toastState = rememberToastState()
                            Button("Show toast", onClick = { toastState.show(title = "Event created") })
                            Toaster(state = toastState, containerAriaLabel = "Notifications")
                            """.trimIndent(),
                        preview = {
                            val toastState = rememberToastState()
                            Column {
                                Button("Show toast", onClick = { toastState.show(title = "Event created") })
                                Toaster(state = toastState, containerAriaLabel = "Notifications")
                            }
                        },
                    ),
                ).let { all ->
                    all.filter {
                        it.title in
                            setOf(
                                "Kotlin extension: Position",
                                "Kotlin extension: Duration",
                                "Kotlin extension: Close button",
                            )
                    }
                },
    )
