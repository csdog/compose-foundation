@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.empty.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.theme.LocalContentColor

val emptyDoc =
    ComponentDoc(
        id = "empty",
        title = "Empty",
        description = "Use the Empty component to display an empty state.",
        examplesHeading = null,
        usageImportsCode =
            """
            import androidx.compose.foundation.gestures.detectTapGestures
            import androidx.compose.foundation.hoverable
            import androidx.compose.foundation.interaction.*
            import androidx.compose.foundation.style.contentPadding
            import androidx.compose.foundation.style.rememberUpdatedStyleState
            import androidx.compose.foundation.text.selection.DisableSelection
            import androidx.compose.ui.semantics.*
            import androidx.compose.ui.focus.*
            import androidx.compose.ui.input.InputMode
            import androidx.compose.ui.input.key.onPreviewKeyEvent
            import androidx.compose.ui.input.pointer.*
            import androidx.compose.ui.platform.LocalInputModeManager
            import com.toutiaoyan.compose.ui.theme.Theme
            import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
            import org.jetbrains.compose.resources.Font
            import androidx.compose.ui.text.font.FontFamily
            import androidx.compose.ui.text.font.FontWeight
            import androidx.compose.ui.text.style.LineHeightStyle
            import androidx.compose.foundation.Image
            import androidx.compose.foundation.border
            import androidx.compose.foundation.layout.*
            import androidx.compose.foundation.shape.CircleShape
            import androidx.compose.foundation.style.Style
            import androidx.compose.runtime.*
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.draw.rotate
            import androidx.compose.ui.graphics.*
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.graphics.ColorFilter
            import androidx.compose.ui.graphics.ColorMatrix
            import androidx.compose.ui.graphics.vector.*
            import androidx.compose.ui.graphics.vector.ImageVector
            import androidx.compose.ui.platform.LocalDensity
            import androidx.compose.ui.platform.LocalLayoutDirection
            import androidx.compose.ui.platform.LocalUriHandler
            import androidx.compose.ui.platform.LocalWindowInfo
            import androidx.compose.ui.text.*
            import androidx.compose.ui.text.TextStyle
            import androidx.compose.ui.text.style.TextDecoration
            import androidx.compose.ui.unit.*
            import androidx.compose.ui.unit.dp
            import com.composables.icons.lucide.*
            import com.toutiaoyan.compose.ui.components.avatar.*
            import com.toutiaoyan.compose.ui.components.button.*
            import com.toutiaoyan.compose.ui.components.empty.*
            import com.toutiaoyan.compose.ui.components.inputgroup.*
            import com.toutiaoyan.compose.ui.components.kbd.Kbd
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.theme.LocalContentColor
            import com.toutiaoyan.compose.ui.theme.currentTheme
            import compose_foundation.app.shared.generated.resources.*
            import org.jetbrains.compose.resources.DrawableResource
            import org.jetbrains.compose.resources.painterResource
            """.trimIndent(),
        usageCode =
            """
            Empty {
                EmptyHeader {
                    EmptyTitle("No data")
                    EmptyDescription("No data found")
                }
                EmptyContent { Button("Add data", onClick = {}) }
            }
            """.trimIndent(),
        compositionCode =
            """
            Empty
            ├── EmptyHeader
            │   ├── EmptyMedia
            │   ├── EmptyTitle
            │   └── EmptyDescription
            └── EmptyContent
            """.trimIndent(),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Empty",
                    (
                        "The root owns centered layout and spacing. Nova defaults to " +
                            "no visible border; Outline adds a dashed border."
                    ),
                    listOf(
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("variant", "EmptyVariant", "Default"),
                        ApiProperty("spacing", "Dp", "16.dp"),
                        ApiProperty("padding / radius", "Dp?", "null (theme defaults)"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "EmptyHeader",
                    (
                        "Centers the media, title and description, with an 8dp gap and " +
                            "a default maximum width of 384dp."
                    ),
                    listOf(
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "EmptyMedia",
                    (
                        "Use Default for arbitrary content or Icon for the 32dp muted " +
                            "badge. Supply a 16dp vector in the icon slot."
                    ),
                    listOf(
                        ApiProperty("variant", "EmptyMediaVariant", "Default"),
                        ApiProperty("size", "Dp?", "null (32.dp for Icon)"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "EmptyTitle",
                    (
                        "The Nova default title uses 14/20 typography, medium weight " +
                            "and tight tracking."
                    ),
                    listOf(
                        ApiProperty("text", "String / () -> String", "Required"),
                        ApiProperty("variant", "TextVariant", "EmptyTitleDefaults.Official"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "EmptyDescription",
                    (
                        "Muted, centered description text. AnnotatedString preserves " +
                            "inline links and their interaction styles."
                    ),
                    listOf(
                        ApiProperty("text", "String / AnnotatedString / () -> String", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "EmptyContent",
                    (
                        "The action area fills available width up to 384dp, with 10dp " +
                            "vertical spacing. Compose a Row for horizontal actions."
                    ),
                    listOf(
                        ApiProperty("spacing", "Dp", "10.dp"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 0.dp,
                    code =
                        """
                        Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyRecipeGlyph(EmptyFolderCode) }
                                EmptyTitle("No Projects Yet")
                                EmptyDescription("You haven't created any projects yet. Get started by creating your first project.")
                            }
                            EmptyContent {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button("Create Project", onClick = {})
                                    Button("Import Project", onClick = {}, variant = ButtonVariant.Outline)
                                }
                            }
                            EmptyLearnMore("Learn More")
                        }

                        @Composable
                        private fun EmptyRecipeGlyph(image: ImageVector, tint: Color = Color.Unspecified) {
                            Image(image, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(if (tint.isSpecified) tint else LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground))
                        }
                        """.trimIndent() + "\n\n" + emptyMediaIconCode + "\n\n" + emptyLearnMoreCode,
                    preview = {
                        Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp)) {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyRecipeGlyph(EmptyFolderCode) }
                                EmptyTitle("No Projects Yet")
                                EmptyDescription(
                                    (
                                        "You haven't created any projects yet. Get started by creating " +
                                            "your first project."
                                    ),
                                )
                            }
                            EmptyContent {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button("Create Project", onClick = {})
                                    Button("Import Project", onClick = {}, variant = ButtonVariant.Outline)
                                }
                            }
                            EmptyLearnMore("Learn More")
                        }
                    },
                ),
                ComponentExample(
                    title = "Outline",
                    descriptionMarkdown = "Use a dashed border to create an outline empty state.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 0.dp,
                    code =
                        """
                        val inset = if (LocalWindowInfo.current.containerSize.width >= with(LocalDensity.current) { 768.dp.roundToPx() }) 40.dp else 24.dp
                        Box(Modifier.fillMaxWidth().heightIn(min = 384.dp).padding(inset), contentAlignment = Alignment.Center) {
                            Empty(variant = EmptyVariant.Outline) {
                                EmptyHeader {
                                    EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyRecipeGlyph(EmptyCloud) }
                                    EmptyTitle("Cloud Storage Empty")
                                    EmptyDescription("Upload files to your cloud storage to access them anywhere.")
                                }
                                EmptyContent { Button("Upload Files", onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm) }
                            }
                        }

                        @Composable
                        private fun EmptyRecipeGlyph(image: ImageVector, tint: Color = Color.Unspecified) {
                            Image(image, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(if (tint.isSpecified) tint else LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground))
                        }
                        """.trimIndent() + "\n\n" + emptyMediaIconCode,
                    preview = { EmptyOutlineRecipe() },
                ),
                ComponentExample(
                    title = "Background",
                    descriptionMarkdown = "Add a muted background to the empty state.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 0.dp,
                    code =
                        """
                        val muted = currentTheme.colors.muted.copy(alpha = 0.3f)
                        Empty(modifier = Modifier.fillMaxWidth().heightIn(min = 384.dp), style = Style { background(muted) }) {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyRecipeGlyph(EmptyBell) }
                                EmptyTitle("No Notifications")
                                EmptyDescription("You're all caught up. New notifications will appear here.", modifier = Modifier.widthIn(max = 320.dp))
                            }
                            EmptyContent {
                                Button(onClick = {}, variant = ButtonVariant.Outline) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        EmptyRecipeGlyph(Lucide.RefreshCcw)
                                        Text { "Refresh" }
                                    }
                                }
                            }
                        }

                        @Composable
                        private fun EmptyRecipeGlyph(image: ImageVector, tint: Color = Color.Unspecified) {
                            Image(image, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(if (tint.isSpecified) tint else LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground))
                        }
                        """.trimIndent() + "\n\n" + emptyMediaIconCode,
                    preview = {
                        val muted = currentTheme.colors.muted.copy(alpha = 0.3f)
                        Empty(
                            modifier =
                                Modifier.fillMaxWidth().heightIn(
                                    min =
                                        384.dp,
                                ),
                            style =
                                Style { background(muted) },
                        ) {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyRecipeGlyph(EmptyBell) }
                                EmptyTitle("No Notifications")
                                EmptyDescription(
                                    "You're all caught up. New notifications will appear here.",
                                    modifier =
                                        Modifier.widthIn(
                                            max =
                                                320.dp,
                                        ),
                                )
                            }
                            EmptyContent {
                                Button(onClick = {}, variant = ButtonVariant.Outline) {
                                    Row(
                                        horizontalArrangement =
                                            Arrangement.spacedBy(6.dp),
                                        verticalAlignment =
                                            Alignment.CenterVertically,
                                    ) {
                                        EmptyRecipeGlyph(Lucide.RefreshCcw)
                                        Text { "Refresh" }
                                    }
                                }
                            }
                        }
                    },
                ),
            ) + emptyAdditionalExamples,
    )

@Composable
private fun EmptyRecipeGlyph(
    image: ImageVector,
    tint: Color = Color.Unspecified,
) {
    Image(
        image,
        null,
        Modifier.size(16.dp),
        colorFilter =
            ColorFilter.tint(
                if (tint.isSpecified) {
                    tint
                } else {
                    LocalContentColor.current.takeIf { it.isSpecified }
                        ?: currentTheme.colors.foreground
                },
            ),
    )
}

@Composable
private fun EmptyOutlineRecipe() {
    val inset =
        if (LocalWindowInfo.current.containerSize.width >=
            with(LocalDensity.current) {
                768.dp.roundToPx()
            }
        ) {
            40.dp
        } else {
            24.dp
        }
    Box(Modifier.fillMaxWidth().heightIn(min = 384.dp).padding(inset), contentAlignment = Alignment.Center) {
        Empty(modifier = Modifier.testTag("empty-outline-root"), variant = EmptyVariant.Outline) {
            EmptyHeader {
                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyRecipeGlyph(EmptyCloud) }
                EmptyTitle("Cloud Storage Empty")
                EmptyDescription("Upload files to your cloud storage to access them anywhere.")
            }
            EmptyContent { Button("Upload Files", onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm) }
        }
    }
}
