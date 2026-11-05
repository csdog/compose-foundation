@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.style.Style
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.FileCode
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.FileExclamationPoint
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Table
import com.composables.icons.lucide.X
import com.toutiaoyan.compose.ui.components.attachment.*
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import compose_foundation.app.shared.generated.resources.Res
import compose_foundation.app.shared.generated.resources.attachment_desk
import compose_foundation.app.shared.generated.resources.attachment_office
import compose_foundation.app.shared.generated.resources.attachment_workspace
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class AttachmentPhoto(
    val resource: DrawableResource,
    val name: String,
    val meta: String,
    val alt: String,
    val url: String,
)

private val attachmentPhotos =
    listOf(
        AttachmentPhoto(
            Res.drawable.attachment_workspace,
            "workspace.png",
            "PNG · 820 KB",
            "Workspace",
            "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=900&auto=format&fit=crop&q=80",
        ),
        AttachmentPhoto(
            Res.drawable.attachment_desk,
            "desk-reference.jpg",
            "JPG · 1.1 MB",
            "Desk",
            "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=900&auto=format&fit=crop&q=80",
        ),
        AttachmentPhoto(
            Res.drawable.attachment_office,
            "office-reference.jpg",
            "JPG · 940 KB",
            "Office",
            "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=900&auto=format&fit=crop&q=80",
        ),
    )

private val attachmentDefaultCode =
    """
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        data class Photo(val resource: DrawableResource, val name: String, val meta: String, val alt: String, val url: String)
        val images = listOf(
            Photo(Res.drawable.attachment_workspace, "workspace.png", "PNG · 820 KB", "Workspace",
                "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=900&auto=format&fit=crop&q=80"),
            Photo(Res.drawable.attachment_desk, "desk-reference.jpg", "JPG · 1.1 MB", "Desk",
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=900&auto=format&fit=crop&q=80"),
            Photo(Res.drawable.attachment_office, "office-reference.jpg", "JPG · 940 KB", "Office",
                "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=900&auto=format&fit=crop&q=80"),
        )
        AttachmentGroup(Modifier.fillMaxWidth()) {
            images.forEach { photo ->
                Attachment(orientation = AttachmentOrientation.Vertical) {
                    AttachmentMedia(AttachmentMediaVariant.Image) {
                        Image(painterResource(photo.resource), contentDescription = photo.alt,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                    AttachmentContent {
                        AttachmentTitle(photo.name)
                        AttachmentDescription(photo.meta)
                    }
                }
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Uploading, size = AttachmentSize.Default, actions = {
            AttachmentActions {
                AttachmentAction(onClick = {}, label = "Cancel upload") {
                    Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
            }
        }) {
            AttachmentMedia {
                Spinner()
            }
            AttachmentContent {
                AttachmentTitle("sales-dashboard.pdf")
                AttachmentDescription("Uploading · 64%")
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Done, size = AttachmentSize.Default, actions = {
            AttachmentActions {
                AttachmentAction(onClick = {}, label = "Remove message-renderer.tsx") {
                    Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
            }
        }) {
            AttachmentMedia {
                Image(Lucide.FileCode, contentDescription = null, modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("message-renderer.tsx")
                AttachmentDescription("TypeScript · 12 KB")
            }
        }
    }
    """.trimIndent()

private val attachmentInteractiveImageCode =
    """
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        data class Photo(val resource: DrawableResource, val name: String, val meta: String, val alt: String, val url: String)
        val images = listOf(
            Photo(Res.drawable.attachment_workspace, "workspace.png", "PNG · 820 KB", "Workspace",
                "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=900&auto=format&fit=crop&q=80"),
            Photo(Res.drawable.attachment_desk, "desk-reference.jpg", "JPG · 1.1 MB", "Desk",
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=900&auto=format&fit=crop&q=80"),
            Photo(Res.drawable.attachment_office, "office-reference.jpg", "JPG · 940 KB", "Office",
                "https://images.unsplash.com/photo-1497366811353-6870744d04b2?w=900&auto=format&fit=crop&q=80"),
        )
        val uri = LocalUriHandler.current
        AttachmentGroup(Modifier.fillMaxWidth()) {
            images.forEach { photo ->
                Attachment(orientation = AttachmentOrientation.Vertical,
                    onClick = { uri.openUri(photo.url) },
                    modifier = Modifier.semantics { contentDescription = "Open ${'$'}{photo.name}" },
                    actions = {
                        AttachmentActions {
                            AttachmentAction(onClick = {}, label = "Remove ${'$'}{photo.name}") {
                                Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                                    colorFilter = ColorFilter.tint(LocalContentColor.current))
                            }
                        }
                    }) {
                    AttachmentMedia(AttachmentMediaVariant.Image) {
                        Image(painterResource(photo.resource), contentDescription = photo.alt,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                    AttachmentContent {
                        AttachmentTitle(photo.name)
                        AttachmentDescription(photo.meta)
                    }
                }
            }
        }
    }
    """.trimIndent()

private val attachmentStatesCode =
    """
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Idle, size = AttachmentSize.Default, actions = {
            AttachmentActions {
                AttachmentAction(onClick = {}, label = "Remove selected-file.pdf") {
                    Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
            }
        }) {
            AttachmentMedia {
                Image(Lucide.Clock, contentDescription = null, modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("selected-file.pdf")
                AttachmentDescription("Ready to upload")
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Uploading, size = AttachmentSize.Default, actions = {
            AttachmentActions {
                AttachmentAction(onClick = {}, label = "Cancel upload") {
                    Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
            }
        }) {
            AttachmentMedia {
                Spinner()
            }
            AttachmentContent {
                AttachmentTitle("design-system.zip")
                AttachmentDescription("Uploading · 64%")
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Processing, size = AttachmentSize.Default, actions = {
            AttachmentActions {
                AttachmentAction(onClick = {}, label = "Remove market-research.pdf") {
                    Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
            }
        }) {
            AttachmentMedia {
                Image(Lucide.FileText, contentDescription = null, modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("market-research.pdf")
                AttachmentDescription("Processing document")
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Error, size = AttachmentSize.Default, actions = {
            AttachmentActions {
                AttachmentAction(onClick = {}, label = "Retry upload") {
                    Image(Lucide.RefreshCw, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
                AttachmentAction(onClick = {}, label = "Remove financial-model.xlsx") {
                    Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
            }
        }) {
            AttachmentMedia {
                Image(Lucide.FileExclamationPoint, contentDescription = null, modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("financial-model.xlsx")
                AttachmentDescription("Upload failed. Try again.")
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Done, size = AttachmentSize.Default, actions = {
            AttachmentActions {
                AttachmentAction(onClick = {}, label = "Remove uploaded-report.pdf") {
                    Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
            }
        }) {
            AttachmentMedia {
                Image(Lucide.Check, contentDescription = null, modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("uploaded-report.pdf")
                AttachmentDescription("Uploaded · 1.8 MB")
            }
        }
    }
    """.trimIndent()

private val attachmentSizesCode =
    """
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Done, size = AttachmentSize.Default) {
            AttachmentMedia {
                Image(Lucide.FileText, contentDescription = null, modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("Default attachment")
                AttachmentDescription("PDF · 2.4 MB")
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Done, size = AttachmentSize.Sm) {
            AttachmentMedia {
                Image(Lucide.FileText, contentDescription = null, modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("Small attachment")
                AttachmentDescription("PDF · 2.4 MB")
            }
        }
        Attachment(modifier = Modifier.fillMaxWidth(), state = AttachmentState.Done, size = AttachmentSize.Xs) {
            AttachmentMedia {
                Image(Lucide.FileText, contentDescription = null, modifier = Modifier.size(14.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current))
            }
            AttachmentContent {
                AttachmentTitle("Extra small attachment")
            }
        }
    }
    """.trimIndent()

private val attachmentGroupCode =
    """
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AttachmentGroup(Modifier.fillMaxWidth()) {
            Attachment(modifier = Modifier.width(256.dp), state = AttachmentState.Done, size = AttachmentSize.Default, actions = {
                AttachmentActions {
                    AttachmentAction(onClick = {}, label = "Remove briefing-notes.pdf") {
                        Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current))
                    }
                }
            }) {
                AttachmentMedia {
                    Image(Lucide.FileText, contentDescription = null, modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
                AttachmentContent {
                    AttachmentTitle("briefing-notes.pdf")
                    AttachmentDescription("PDF · 1.4 MB")
                }
            }
            Attachment(modifier = Modifier.width(256.dp), state = AttachmentState.Done, size = AttachmentSize.Default, actions = {
                AttachmentActions {
                    AttachmentAction(onClick = {}, label = "Remove workspace.png") {
                        Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current))
                    }
                }
            }) {
                AttachmentMedia(AttachmentMediaVariant.Image) {
                    Image(painterResource(Res.drawable.attachment_workspace), contentDescription = "Workspace",
                        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
                AttachmentContent {
                    AttachmentTitle("workspace.png")
                    AttachmentDescription("PNG · 820 KB")
                }
            }
            Attachment(modifier = Modifier.width(256.dp), state = AttachmentState.Done, size = AttachmentSize.Default, actions = {
                AttachmentActions {
                    AttachmentAction(onClick = {}, label = "Remove customers.csv") {
                        Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current))
                    }
                }
            }) {
                AttachmentMedia {
                    Image(Lucide.Table, contentDescription = null, modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
                AttachmentContent {
                    AttachmentTitle("customers.csv")
                    AttachmentDescription("CSV · 18 KB")
                }
            }
            Attachment(modifier = Modifier.width(256.dp), state = AttachmentState.Done, size = AttachmentSize.Default, actions = {
                AttachmentActions {
                    AttachmentAction(onClick = {}, label = "Remove renderer.tsx") {
                        Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current))
                    }
                }
            }) {
                AttachmentMedia {
                    Image(Lucide.FileCode, contentDescription = null, modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(LocalContentColor.current))
                }
                AttachmentContent {
                    AttachmentTitle("renderer.tsx")
                    AttachmentDescription("TSX · 12 KB")
                }
            }
        }
    }
    """.trimIndent()

private val attachmentPreviewIconIndentedCode = attachmentPreviewIconCode.prependIndent("        ")

private val attachmentTriggerCode =
    """
        |    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
        |        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        |$attachmentPreviewIconIndentedCode
        |        var open by remember { mutableStateOf(false) }
        |        Dialog(open = open, onOpenChange = { open = it }) {
        |            Attachment(onClick = { open = true },
        |                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Preview research-summary.pdf" },
        |                actions = {
        |                    AttachmentActions {
        |                        AttachmentAction(onClick = {}, label = "Copy link") {
        |                            Image(Lucide.Copy, contentDescription = null, modifier = Modifier.size(12.dp),
        |                                colorFilter = ColorFilter.tint(LocalContentColor.current))
        |                        }
        |                        AttachmentAction(onClick = {}, label = "Remove research-summary.pdf") {
        |                            Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
        |                                colorFilter = ColorFilter.tint(LocalContentColor.current))
        |                        }
        |                    }
        |                },
        |            ) {
        |                AttachmentMedia {
        |                    Image(previewIcon, contentDescription = null, modifier = Modifier.size(16.dp),
        |                        colorFilter = ColorFilter.tint(LocalContentColor.current))
        |                }
        |                AttachmentContent {
        |                    AttachmentTitle("research-summary.pdf")
        |                    AttachmentDescription("Open preview dialog")
        |                }
        |            }
        |            DialogContent("research-summary.pdf", description =
        |                "The attachment trigger fills the card and opens the dialog, while the actions stay independently clickable above it.", style = Style { width(448.dp) }) { }
        |        }
        |    }
    """.trimMargin().trimIndent()

val attachmentDoc =
    ComponentDoc(
        id = "attachment",
        title = "Attachment",
        description = "Displays a file or image attachment with media, metadata, upload state, and actions.",
        examplesHeading = null,
        usageImportsCode =
            """
            import androidx.compose.foundation.Image
            import androidx.compose.foundation.layout.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.*
            import androidx.compose.ui.graphics.vector.ImageVector
            import androidx.compose.ui.graphics.vector.PathParser
            import androidx.compose.ui.platform.LocalUriHandler
            import androidx.compose.ui.semantics.contentDescription
            import androidx.compose.ui.semantics.semantics
            import androidx.compose.ui.unit.dp
            import com.toutiaoyan.compose.ui.components.attachment.*
            import com.toutiaoyan.compose.ui.components.dialog.*
            import com.toutiaoyan.compose.ui.theme.LocalContentColor
            import com.composables.icons.lucide.*
            """.trimIndent(),
        usageCode =
            """
            Attachment(actions = {
                AttachmentActions {
                    AttachmentAction(onClick = {}, label = "Remove sales-dashboard.pdf") {
                        Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(LocalContentColor.current))
                    }
                }
            }) {
                AttachmentMedia { Image(Lucide.FileText, contentDescription = null) }
                AttachmentContent {
                    AttachmentTitle("sales-dashboard.pdf")
                    AttachmentDescription("PDF · 2.4 MB")
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            AttachmentGroup
            └── Attachment
                ├── AttachmentMedia
                ├── AttachmentContent
                │   ├── AttachmentTitle
                │   └── AttachmentDescription
                └── AttachmentActions (actions parameter or child)
                    └── AttachmentAction

            Attachment(onClick = …) activates the whole card.
            BoxScope.AttachmentTrigger is available for custom overlays.
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Features",
                    listOf(
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "Icon and image previews through `AttachmentMedia`.",
                                (
                                    "Five upload states: `Idle`, `Uploading`, `Processing`, " +
                                        "`Error`, and `Done`, with a title shimmer while work is in " +
                                        "progress."
                                ),
                                "Three sizes, with horizontal and vertical layouts.",
                                (
                                    "`onClick` opens a link or dialog for the whole card, while " +
                                        "actions remain separately clickable."
                                ),
                                "A horizontally scrolling `AttachmentGroup` with snapping and an edge fade.",
                                "Caller styling through `modifier` and `style` on the component parts.",
                            ),
                        ),
                    ),
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Accessibility",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "`AttachmentAction` uses a Compose `Button`. Interactive cards " +
                                    "and `AttachmentTrigger` expose button semantics. Give each " +
                                    "interactive target a meaningful name, and keep the file name " +
                                    "and status available as text."
                            ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Label icon-only actions",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Set `label` to describe both the action and the file it " +
                                    "affects. Decorative icons do not need a separate content " +
                                    "description."
                            ),
                        ),
                        ComponentGuideBlock.Code(
                            """
                            AttachmentAction(onClick = {}, label = "Remove sales-dashboard.pdf") {
                                Image(Lucide.X, contentDescription = null, modifier = Modifier.size(12.dp),
                                    colorFilter = ColorFilter.tint(LocalContentColor.current))
                            }
                            """.trimIndent(),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Label the trigger",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "For the standard card, name the `onClick` target with " +
                                    "`modifier.semantics`. Inside a composable, use your URI " +
                                    "handler to open a file or image."
                            ),
                        ),
                        ComponentGuideBlock.Code(
                            """
                            val uri = LocalUriHandler.current
                            val url = "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=900&auto=format&fit=crop&q=80"
                            Attachment(
                                modifier = Modifier.semantics { contentDescription = "Open workspace.png" },
                                onClick = { uri.openUri(url) },
                            ) {
                                AttachmentContent("workspace.png", "PNG · 820 KB")
                            }
                            """.trimIndent(),
                        ),
                        ComponentGuideBlock.Paragraph(
                            (
                                "Actions and the interactive card remain separate focus " +
                                    "targets. A custom `BoxScope.AttachmentTrigger` takes its own " +
                                    "`label` and fills its parent at zIndex 10; place separate " +
                                    "action siblings above it, for example at zIndex 20."
                            ),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Keyboard scrolling",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "`AttachmentGroup` is keyboard-focusable in Compose. Give it a " +
                                    "content description. Arrow keys scroll the row; Tab reaches " +
                                    "interactive cards and their actions, including items outside " +
                                    "the current view."
                            ),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Meaning beyond color",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "An `Error` attachment uses destructive colors. Include the " +
                                    "failure reason in `AttachmentDescription` so color is not the " +
                                    "only signal."
                            ),
                        ),
                    ),
                    level = 3,
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Attachment",
                    "The root file or image attachment container.",
                    listOf(
                        ApiProperty("state", "AttachmentState", "Done"),
                        ApiProperty("size", "AttachmentSize", "Default"),
                        ApiProperty("orientation", "AttachmentOrientation", "Horizontal"),
                        ApiProperty("onClick", "(() -> Unit)?", "null"),
                        ApiProperty("actions", "(@Composable AttachmentScope.() -> Unit)?", "null"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable AttachmentScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentScope",
                    (
                        "The receiver of content and actions. Stored slot lambdas use " +
                            "@Composable AttachmentScope.() -> Unit. Wrap a stored " +
                            "unscoped lambda in { oldContent() }, or extract it as an " +
                            "AttachmentScope extension when it contains variant-aware " +
                            "parts."
                    ),
                    listOf(
                        ApiProperty("size", "AttachmentSize", "Root size"),
                        ApiProperty("orientation", "AttachmentOrientation", "Root orientation"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentMedia",
                    (
                        "An icon or cropped image. Image media uses 60% opacity while " +
                            "uploading, processing or in error. The original icon overload " +
                            "is retained. Images and resource loading belong to the caller."
                    ),
                    listOf(
                        ApiProperty("variant", "AttachmentMediaVariant", "Icon (original overload)"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentContent",
                    "The flexible metadata column. The convenience overload accepts a title and optional description.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required (slot overload)"),
                        ApiProperty("title", "String", "Required (convenience overload)"),
                        ApiProperty("description", "String?", "null (convenience overload)"),
                        ApiProperty("state", "AttachmentState", "Inherited (convenience overload)"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentTitle",
                    (
                        "A single-line filename. Uploading and processing animate the " +
                            "inherited title; an explicit state overrides it. Both String " +
                            "and lambda forms are supported."
                    ),
                    listOf(
                        ApiProperty("text", "String / () -> String", "Required"),
                        ApiProperty("state", "AttachmentState", "Inherited / Done outside Attachment"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentDescription",
                    (
                        "File metadata or status. Keep a textual failure reason so " +
                            "error is conveyed beyond color; isError can override the root " +
                            "state."
                    ),
                    listOf(
                        ApiProperty("text", "String / () -> String", "Required"),
                        ApiProperty("isError", "Boolean", "Inherited from root state"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentActions",
                    (
                        "A trailing action group. In AttachmentScope, vertical actions " +
                            "have gap-1 and are overlaid at the physical top right, " +
                            "including RTL."
                    ),
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentAction",
                    "A button. Label icon-only actions with their purpose and target.",
                    listOf(
                        ApiProperty(
                            "onClick",
                            "() -> Unit",
                            "Required",
                        ),
                        ApiProperty("label", "String?", "null"), ApiProperty("enabled", "Boolean", "true"),
                        ApiProperty(
                            "disabled",
                            "Boolean",
                            "false",
                        ),
                        ApiProperty(
                            "variant",
                            "ButtonVariant",
                            "Ghost",
                        ),
                        ApiProperty("size", "ButtonSize", "IconXs"),
                        ApiProperty(
                            "modifier",
                            "Modifier",
                            "Modifier",
                        ),
                        ApiProperty(
                            "style",
                            "Style",
                            "Style",
                        ),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentTrigger",
                    (
                        "A BoxScope overlay for custom layouts, filling its parent's " +
                            "bounds at zIndex 10. Set label and place independent action " +
                            "siblings above it (for example zIndex 20). Both enabled = " +
                            "false and disabled = true block pointer and keyboard " +
                            "activation. For the standard component, prefer " +
                            "Attachment(onClick = …); the custom Box overlay does not " +
                            "automatically wire the surrounding Attachment's hover style."
                    ),
                    listOf(
                        ApiProperty("onClick", "() -> Unit", "Required"),
                        ApiProperty("label", "String?", "null"),
                        ApiProperty("enabled", "Boolean", "true"),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
                ComponentApiSection(
                    "AttachmentGroup",
                    (
                        "A horizontally scrolling, snapping row with an edge fade. " +
                            "Arrow keys scroll focused groups or actions in the current " +
                            "layout direction; oversized cards remain scrollable within " +
                            "their snap area."
                    ),
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable RowScope.() -> Unit", "Required"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    "Default",
                    code =
                    attachmentDefaultCode,
                    minPreviewHeight =
                        0.dp,
                    previewPadding =
                        40.dp,
                    previewBackground =
                        {
                            attachmentPreviewBackground()
                        },
                    preview = {
                        AttachmentExampleFrame {
                            AttachmentPhotoGroup()
                            FileAttachment(
                                "sales-dashboard.pdf",
                                "Uploading · 64%",
                                state = AttachmentState.Uploading,
                                actions = { AttachmentActions { AttachmentExampleAction("Cancel upload") } },
                            )
                            FileAttachment(
                                "message-renderer.tsx",
                                "TypeScript · 12 KB",
                                icon = Lucide.FileCode,
                                actions = {
                                    AttachmentActions {
                                        AttachmentExampleAction(
                                            "Remove message-renderer.tsx",
                                        )
                                    }
                                },
                            )
                        }
                    },
                ),
                ComponentExample(
                    "Image",
                    descriptionMarkdown = (
                        "Set `variant = AttachmentMediaVariant.Image` on the media and " +
                            "use `orientation = AttachmentOrientation.Vertical`. Supply " +
                            "your own image resources."
                    ),
                    code = attachmentInteractiveImageCode,
                    minPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    previewBackground = {
                        attachmentPreviewBackground()
                    },
                    preview = { AttachmentExampleFrame { AttachmentPhotoGroup(interactive = true) } },
                ),
                ComponentExample(
                    "States",
                    descriptionMarkdown = (
                        "Set `state` to reflect the upload lifecycle. Uploading and " +
                            "processing shimmer the title, and error switches to a " +
                            "destructive treatment."
                    ),
                    code = attachmentStatesCode,
                    minPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    previewBackground = {
                        attachmentPreviewBackground()
                    },
                    preview = {
                        AttachmentExampleFrame(gap = 8.dp) {
                            FileAttachment(
                                "selected-file.pdf",
                                "Ready to upload",
                                Lucide.Clock,
                                AttachmentState.Idle,
                                actions = { AttachmentActions { AttachmentExampleAction("Remove selected-file.pdf") } },
                            )
                            FileAttachment(
                                "design-system.zip",
                                "Uploading · 64%",
                                state = AttachmentState.Uploading,
                                actions = { AttachmentActions { AttachmentExampleAction("Cancel upload") } },
                            )
                            FileAttachment(
                                "market-research.pdf",
                                "Processing document",
                                Lucide.FileText,
                                AttachmentState.Processing,
                                actions = {
                                    AttachmentActions {
                                        AttachmentExampleAction(
                                            "Remove market-research.pdf",
                                        )
                                    }
                                },
                            )
                            FileAttachment(
                                "financial-model.xlsx",
                                "Upload failed. Try again.",
                                Lucide.FileExclamationPoint,
                                AttachmentState.Error,
                                actions = {
                                    AttachmentActions {
                                        AttachmentExampleAction("Retry upload", Lucide.RefreshCw)
                                        AttachmentExampleAction("Remove financial-model.xlsx")
                                    }
                                },
                            )
                            FileAttachment(
                                "uploaded-report.pdf",
                                "Uploaded · 1.8 MB",
                                Lucide.Check,
                                actions = {
                                    AttachmentActions {
                                        AttachmentExampleAction(
                                            "Remove uploaded-report.pdf",
                                        )
                                    }
                                },
                            )
                        }
                    },
                ),
                ComponentExample(
                    "Sizes",
                    descriptionMarkdown = "Use `size` to switch between Default, Sm, and Xs.",
                    code = attachmentSizesCode,
                    minPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    previewBackground = {
                        attachmentPreviewBackground()
                    },
                    preview = {
                        AttachmentExampleFrame {
                            FileAttachment("Default attachment", "PDF · 2.4 MB")
                            FileAttachment("Small attachment", "PDF · 2.4 MB", size = AttachmentSize.Sm)
                            FileAttachment("Extra small attachment", null, size = AttachmentSize.Xs)
                        }
                    },
                ),
                ComponentExample(
                    "Group",
                    descriptionMarkdown = (
                        "Wrap attachments in `AttachmentGroup` to lay them out in a " +
                            "horizontally scrollable, snapping row with an edge fade."
                    ),
                    code = attachmentGroupCode,
                    minPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    previewBackground = {
                        attachmentPreviewBackground()
                    },
                    preview = {
                        AttachmentExampleFrame {
                            AttachmentGroup(Modifier.fillMaxWidth()) {
                                FileAttachment(
                                    "briefing-notes.pdf",
                                    "PDF · 1.4 MB",
                                    modifier = Modifier.width(256.dp),
                                    actions = {
                                        AttachmentActions {
                                            AttachmentExampleAction(
                                                "Remove briefing-notes.pdf",
                                            )
                                        }
                                    },
                                )
                                val photo = attachmentPhotos.first()
                                Attachment(Modifier.width(256.dp), actions = {
                                    AttachmentActions { AttachmentExampleAction("Remove ${photo.name}") }
                                }) {
                                    AttachmentMedia(AttachmentMediaVariant.Image) { AttachmentPhotoImage(photo) }
                                    AttachmentContent {
                                        AttachmentTitle(photo.name)
                                        AttachmentDescription(photo.meta)
                                    }
                                }
                                FileAttachment(
                                    "customers.csv",
                                    "CSV · 18 KB",
                                    Lucide.Table,
                                    modifier = Modifier.width(256.dp),
                                    actions = { AttachmentActions { AttachmentExampleAction("Remove customers.csv") } },
                                )
                                FileAttachment(
                                    "renderer.tsx",
                                    "TSX · 12 KB",
                                    Lucide.FileCode,
                                    modifier = Modifier.width(256.dp),
                                    actions = { AttachmentActions { AttachmentExampleAction("Remove renderer.tsx") } },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    "Trigger",
                    descriptionMarkdown = (
                        "Add an `AttachmentTrigger` to make the whole card open a link " +
                            "or dialog. It fills the card behind the actions, so the " +
                            "actions stay independently clickable."
                    ),
                    code = attachmentTriggerCode,
                    minPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    previewBackground = {
                        attachmentPreviewBackground()
                    },
                    preview = { AttachmentExampleFrame { AttachmentTriggerExample() } },
                ),
            ),
    )

@Composable
private fun attachmentPreviewBackground(): Color =
    if (currentTheme.isDark) currentTheme.colors.background else Color(0xFFF8F8F8)

@Composable
private fun AttachmentExampleFrame(
    gap: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
        Column(
            Modifier.widthIn(max = 384.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(gap),
            content = content,
        )
    }
}

@Composable
private fun FileAttachment(
    title: String,
    description: String?,
    icon: ImageVector = Lucide.FileText,
    state: AttachmentState = AttachmentState.Done,
    size: AttachmentSize = AttachmentSize.Default,
    modifier: Modifier = Modifier.fillMaxWidth(),
    actions: (@Composable AttachmentScope.() -> Unit)? = null,
) {
    Attachment(modifier, state = state, size = size, actions = actions) {
        AttachmentMedia {
            if (state == AttachmentState.Uploading) {
                Spinner()
            } else {
                AttachmentGlyph(icon, if (size == AttachmentSize.Xs) 14.dp else 16.dp)
            }
        }
        AttachmentContent {
            AttachmentTitle(title)
            if (description != null) AttachmentDescription(description)
        }
    }
}

@Composable
private fun AttachmentPhotoGroup(interactive: Boolean = false) {
    val uri = LocalUriHandler.current
    AttachmentGroup(Modifier.fillMaxWidth()) {
        attachmentPhotos.forEach { photo ->
            Attachment(
                modifier =
                    if (interactive) {
                        Modifier.semantics {
                            contentDescription =
                                "Open ${photo.name}"
                        }
                    } else {
                        Modifier
                    },
                orientation = AttachmentOrientation.Vertical,
                onClick = if (interactive) ({ uri.openUri(photo.url) }) else null,
                actions =
                    if (interactive) {
                        (
                            {
                                AttachmentActions {
                                    AttachmentExampleAction(
                                        "Remove ${photo.name}",
                                    )
                                }
                            }
                        )
                    } else {
                        null
                    },
            ) {
                AttachmentMedia(AttachmentMediaVariant.Image) { AttachmentPhotoImage(photo) }
                AttachmentContent {
                    AttachmentTitle(photo.name)
                    AttachmentDescription(photo.meta)
                }
            }
        }
    }
}

@Composable
private fun AttachmentPhotoImage(photo: AttachmentPhoto) {
    Image(
        painterResource(photo.resource),
        contentDescription = photo.alt,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun AttachmentGlyph(
    icon: ImageVector,
    size: Dp? = null,
) {
    Icon(icon, modifier = if (size != null) Modifier.size(size) else Modifier)
}

@Composable
private fun AttachmentExampleAction(
    label: String,
    icon: ImageVector = Lucide.X,
) {
    AttachmentAction(onClick = {}, label = label) { AttachmentGlyph(icon) }
}

@Composable
private fun AttachmentTriggerExample() {
    var open by remember { mutableStateOf(false) }
    Dialog(open = open, onOpenChange = { open = it }) {
        Attachment(
            Modifier.fillMaxWidth().semantics { contentDescription = "Preview research-summary.pdf" },
            onClick = { open = true },
            actions = {
                AttachmentActions {
                    AttachmentExampleAction("Copy link", Lucide.Copy)
                    AttachmentExampleAction("Remove research-summary.pdf")
                }
            },
        ) {
            AttachmentMedia { AttachmentGlyph(AttachmentFileSearchIcon) }
            AttachmentContent {
                AttachmentTitle("research-summary.pdf")
                AttachmentDescription("Open preview dialog")
            }
        }
        DialogContent(
            "research-summary.pdf",
            description =
                (
                    "The attachment trigger fills the card and opens the dialog, " +
                        "while the actions stay independently clickable above it."
                ),
            style = Style { width(448.dp) },
        ) { }
    }
}
