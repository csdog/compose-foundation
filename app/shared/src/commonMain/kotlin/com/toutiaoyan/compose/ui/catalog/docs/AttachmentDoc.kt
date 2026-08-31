@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.attachment.Attachment
import com.toutiaoyan.compose.ui.components.attachment.AttachmentAction
import com.toutiaoyan.compose.ui.components.attachment.AttachmentActions
import com.toutiaoyan.compose.ui.components.attachment.AttachmentContent
import com.toutiaoyan.compose.ui.components.attachment.AttachmentDescription
import com.toutiaoyan.compose.ui.components.attachment.AttachmentGroup
import com.toutiaoyan.compose.ui.components.attachment.AttachmentMedia
import com.toutiaoyan.compose.ui.components.attachment.AttachmentOrientation
import com.toutiaoyan.compose.ui.components.attachment.AttachmentState
import com.toutiaoyan.compose.ui.components.attachment.AttachmentTitle
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

val attachmentDoc =
    ComponentDoc(
        id = "attachment",
        title = "Attachment",
        description = "A file-attachment chip for a chat composer's upload tray, with upload/processing/error states.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            AttachmentGroup {
                Attachment(state = AttachmentState.Done) {
                    AttachmentMedia { EmojiText("📄") }
                    AttachmentContent {
                        AttachmentTitle("report.pdf")
                        AttachmentDescription("2.4 MB")
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "States",
                    code =
                        """
                        AttachmentGroup {
                            Attachment(state = AttachmentState.Done) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("report.pdf")
                                    AttachmentDescription("2.4 MB")
                                }
                            }
                            Attachment(state = AttachmentState.Uploading) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("uploading.zip", state = AttachmentState.Uploading)
                                    AttachmentDescription("Uploading…")
                                }
                            }
                            Attachment(state = AttachmentState.Error) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("failed.csv")
                                    AttachmentDescription("Upload failed", isError = true)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AttachmentGroup {
                            Attachment(state = AttachmentState.Done) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("report.pdf")
                                    AttachmentDescription("2.4 MB")
                                }
                            }
                            Attachment(state = AttachmentState.Uploading) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("uploading.zip", state = AttachmentState.Uploading)
                                    AttachmentDescription("Uploading…")
                                }
                            }
                            Attachment(state = AttachmentState.Error) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("failed.csv")
                                    AttachmentDescription("Upload failed", isError = true)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default error",
                    code =
                        """
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                                AttachmentDescription("2.4 MB", isError = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                                AttachmentDescription("2.4 MB", isError = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Composer upload tray",
                    code =
                        """
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            AttachmentGroup {
                                Attachment(
                                    orientation = AttachmentOrientation.Vertical,
                                    actions = { AttachmentActions { AttachmentRemoveButton() } },
                                ) {
                                    AttachmentMedia { EmojiText("🏢") }
                                    AttachmentContent {
                                        AttachmentTitle("workspace.png")
                                        AttachmentDescription("PNG · 820 KB")
                                    }
                                }
                                Attachment(
                                    orientation = AttachmentOrientation.Vertical,
                                    actions = { AttachmentActions { AttachmentRemoveButton() } },
                                ) {
                                    AttachmentMedia { EmojiText("🌿") }
                                    AttachmentContent {
                                        AttachmentTitle("desk-reference-photo.jpg")
                                        AttachmentDescription("JPG · 1.1 MB")
                                    }
                                }
                                Attachment(
                                    orientation = AttachmentOrientation.Vertical,
                                    actions = { AttachmentActions { AttachmentRemoveButton() } },
                                ) {
                                    AttachmentMedia { EmojiText("🪑") }
                                    AttachmentContent {
                                        AttachmentTitle("office-reference.jpg")
                                        AttachmentDescription("JPG · 940 KB")
                                    }
                                }
                            }
                            Attachment(
                                state = AttachmentState.Uploading,
                                actions = { AttachmentActions { AttachmentRemoveButton() } },
                            ) {
                                AttachmentMedia { Spinner() }
                                AttachmentContent {
                                    AttachmentTitle("sales-dashboard.pdf", state = AttachmentState.Uploading)
                                    AttachmentDescription("Uploading · 64%")
                                }
                            }
                            Attachment(
                                actions = { AttachmentActions { AttachmentRemoveButton() } },
                            ) {
                                AttachmentMedia { Text { "</>" } }
                                AttachmentContent {
                                    AttachmentTitle("message-renderer.tsx")
                                    AttachmentDescription("TypeScript · 12 KB")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            modifier = Modifier.width(320.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            AttachmentGroup {
                                Attachment(
                                    orientation = AttachmentOrientation.Vertical,
                                    actions = { AttachmentActions { AttachmentRemoveButton() } },
                                ) {
                                    AttachmentMedia { EmojiText("🏢") }
                                    AttachmentContent {
                                        AttachmentTitle("workspace.png")
                                        AttachmentDescription("PNG · 820 KB")
                                    }
                                }
                                Attachment(
                                    orientation = AttachmentOrientation.Vertical,
                                    actions = { AttachmentActions { AttachmentRemoveButton() } },
                                ) {
                                    AttachmentMedia { EmojiText("🌿") }
                                    AttachmentContent {
                                        AttachmentTitle("desk-reference-photo.jpg")
                                        AttachmentDescription("JPG · 1.1 MB")
                                    }
                                }
                                Attachment(
                                    orientation = AttachmentOrientation.Vertical,
                                    actions = { AttachmentActions { AttachmentRemoveButton() } },
                                ) {
                                    AttachmentMedia { EmojiText("🪑") }
                                    AttachmentContent {
                                        AttachmentTitle("office-reference.jpg")
                                        AttachmentDescription("JPG · 940 KB")
                                    }
                                }
                            }
                            Attachment(
                                state = AttachmentState.Uploading,
                                actions = { AttachmentActions { AttachmentRemoveButton() } },
                            ) {
                                AttachmentMedia { Spinner() }
                                AttachmentContent {
                                    AttachmentTitle(
                                        "sales-dashboard.pdf",
                                        state = AttachmentState.Uploading,
                                    )
                                    AttachmentDescription("Uploading · 64%")
                                }
                            }
                            Attachment(
                                actions = { AttachmentActions { AttachmentRemoveButton() } },
                            ) {
                                AttachmentMedia { Text { "</>" } }
                                AttachmentContent {
                                    AttachmentTitle("message-renderer.tsx")
                                    AttachmentDescription("TypeScript · 12 KB")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                                AttachmentDescription("2.4 MB")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                                AttachmentDescription("2.4 MB")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Media",
                    code =
                        """
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Attachment(state = AttachmentState.Done) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Attachment(state = AttachmentState.Done) {
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                                AttachmentDescription("2.4 MB")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Attachment(state = AttachmentState.Done) {
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                                AttachmentDescription("2.4 MB")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        AttachmentGroup {
                            Attachment(state = AttachmentState.Done) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("report.pdf")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        AttachmentGroup {
                            Attachment(state = AttachmentState.Done) {
                                AttachmentMedia { EmojiText("📄") }
                                AttachmentContent {
                                    AttachmentTitle("report.pdf")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Actions",
                    code =
                        """
                        Attachment(actions = { AttachmentActions { AttachmentRemoveButton() } }) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Attachment(actions = { AttachmentActions { AttachmentRemoveButton() } }) {
                            AttachmentMedia { EmojiText("📄") }
                            AttachmentContent {
                                AttachmentTitle("report.pdf")
                            }
                        }
                    },
                ),
            ),
    )

@Composable
private fun AttachmentRemoveButton() {
    AttachmentAction(onClick = {}) {
        Image(
            imageVector = Lucide.X,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            colorFilter = ColorFilter.tint(currentTheme.colors.foreground),
        )
    }
}
