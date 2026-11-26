@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.emojitext.EmojiText
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text

class AttachmentScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("attachment_states", darkTheme = darkTheme) {
            AttachmentGroup(modifier = Modifier.height(56.dp)) {
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
        }
    }

    private fun vertical(darkTheme: Boolean) {
        snapshot("attachment_vertical", darkTheme = darkTheme) {
            AttachmentGroup {
                Attachment(orientation = AttachmentOrientation.Vertical) {
                    AttachmentMedia { EmojiText("🖼️") }
                    AttachmentContent {
                        AttachmentTitle("photo.png")
                        AttachmentDescription("1.1 MB")
                    }
                }
            }
        }
    }

    private fun composerUploadTray(darkTheme: Boolean) {
        snapshot("attachment_composer_upload_tray", darkTheme = darkTheme) {
            Column(
                modifier = Modifier.width(320.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AttachmentGroup {
                    Attachment(
                        orientation = AttachmentOrientation.Vertical,
                        actions = { AttachmentActions { RemoveButton() } },
                    ) {
                        AttachmentMedia { EmojiText("🏢") }
                        AttachmentContent {
                            AttachmentTitle("workspace.png")
                            AttachmentDescription("PNG · 820 KB")
                        }
                    }
                    Attachment(
                        orientation = AttachmentOrientation.Vertical,
                        actions = { AttachmentActions { RemoveButton() } },
                    ) {
                        AttachmentMedia { EmojiText("🌿") }
                        AttachmentContent {
                            AttachmentTitle("desk-reference-photo.jpg")
                            AttachmentDescription("JPG · 1.1 MB")
                        }
                    }
                    Attachment(
                        orientation = AttachmentOrientation.Vertical,
                        actions = { AttachmentActions { RemoveButton() } },
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
                    actions = { AttachmentActions { RemoveButton() } },
                ) {
                    AttachmentMedia { Spinner() }
                    AttachmentContent {
                        AttachmentTitle("sales-dashboard.pdf", state = AttachmentState.Uploading)
                        AttachmentDescription("Uploading · 64%")
                    }
                }
                Attachment(
                    actions = { AttachmentActions { RemoveButton() } },
                ) {
                    AttachmentMedia { Text { "</>" } }
                    AttachmentContent {
                        AttachmentTitle("message-renderer.tsx")
                        AttachmentDescription("TypeScript · 12 KB")
                    }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun vertical_light() = vertical(darkTheme = false)

    @Test
    fun vertical_dark() = vertical(darkTheme = true)

    @Test
    fun composer_upload_tray_light() = composerUploadTray(darkTheme = false)

    @Test
    fun composer_upload_tray_dark() = composerUploadTray(darkTheme = true)
}

@androidx.compose.runtime.Composable
private fun RemoveButton() {
    Button(onClick = {}, variant = ButtonVariant.Ghost, size = ButtonSize.Xs) {
        Text { "✕" }
    }
}
