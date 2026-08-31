@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
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
        description =
            "A stackable, auto-dismissing notification queue. Real shadcn/ui's sonner.tsx wraps the separate " +
                "`sonner` npm library; since there's no Compose Multiplatform equivalent, ToastState + " +
                "Toaster reimplement that queue directly.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            val toastState = rememberToastState()
            Button("Save", onClick = { toastState.success("Saved") })
            Toaster(state = toastState)
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        val toastState = rememberToastState()
                        Button(
                            "Show toast",
                            onClick = {
                                toastState.show(
                                    title = "Event created",
                                    description = "Monday, March 12 at 9:00 AM",
                                    variant = ToastVariant.Success,
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
                                        variant = ToastVariant.Success,
                                    )
                                },
                            )
                            Toaster(state = toastState)
                        }
                    },
                ),
                ComponentExample(
                    title = "Variants",
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
                    title = "With action",
                    code =
                        """
                        val toastState = rememberToastState()
                        Button(
                            "Show toast",
                            onClick = {
                                toastState.show(
                                    title = "Event created",
                                    description = "Monday, March 12 at 9:00 AM",
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
                                        description = "Monday, March 12 at 9:00 AM",
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
                    title = "Duration",
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
                    title = "Default duration",
                    code =
                        """
                        val toastState = rememberToastState()
                        Button(
                            "Show toast",
                            onClick = {
                                toastState.show(
                                    title = "Event created",
                                    duration = 4000L,
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
                                        duration = 4000L,
                                    )
                                },
                            )
                            Toaster(state = toastState)
                        }
                    },
                ),
                ComponentExample(
                    title = "Position",
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
                    title = "Default position",
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
                    title = "Close button",
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
                    title = "Default close button",
                    code =
                        """
                        val toastState = rememberToastState()
                        Button(
                            "Show toast",
                            onClick = { toastState.show(title = "Event created") },
                        )
                        Toaster(state = toastState, closeButton = false)
                        """.trimIndent(),
                    preview = {
                        val toastState = rememberToastState()
                        Column {
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, closeButton = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Rich colors",
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
                    title = "Default rich colors",
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
                    title = "Visible toasts",
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
                    title = "Default visible toasts",
                    code =
                        """
                        val toastState = rememberToastState()
                        Button("Show toast", onClick = { toastState.show(title = "Event created") })
                        Toaster(state = toastState, visibleToasts = Int.MAX_VALUE)
                        """.trimIndent(),
                    preview = {
                        val toastState = rememberToastState()
                        Column {
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, visibleToasts = Int.MAX_VALUE)
                        }
                    },
                ),
                ComponentExample(
                    title = "Offset",
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
                    title = "Default offset",
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
                    title = "Gap",
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
                    title = "Default gap",
                    code =
                        """
                        val toastState = rememberToastState()
                        Button("Show toast", onClick = { toastState.show(title = "Event created") })
                        Toaster(state = toastState, gap = twSpacing.sm)
                        """.trimIndent(),
                    preview = {
                        val toastState = rememberToastState()
                        Column {
                            Button(
                                "Show toast",
                                onClick = { toastState.show(title = "Event created") },
                            )
                            Toaster(state = toastState, gap = twSpacing.sm)
                        }
                    },
                ),
                ComponentExample(
                    title = "Invert",
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
                    title = "Default invert",
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
                    title = "Cancel action",
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
                    title = "Success helper",
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
                    title = "Forced light theme",
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
                    title = "Default theme",
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
                    title = "Toaster",
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
                    title = "Viewport",
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
                    title = "Title",
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
                    title = "Description",
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
                    title = "Action",
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
                    title = "Close",
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
                    title = "Close label",
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
                    title = "Default close label",
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
                    title = "Default container aria label",
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
                    title = "Accessible label",
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
            ),
    )
