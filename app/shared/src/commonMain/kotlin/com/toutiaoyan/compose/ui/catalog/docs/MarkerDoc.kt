@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.marker.*
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.toast.*
import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle

val markerDoc =
    ComponentDoc(
        id = "marker",
        title = "Marker",
        description = "Displays an inline status, system note, bordered row, or labeled separator in a conversation.",
        examplesHeading = null,
        usageImportsCode =
            """
            import androidx.compose.foundation.layout.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.semantics.*
            import androidx.compose.ui.text.TextStyle
            import androidx.compose.ui.unit.dp
            import com.composables.icons.lucide.*
            import com.toutiaoyan.compose.ui.components.icon.Icon
            import com.toutiaoyan.compose.ui.components.marker.*
            import com.toutiaoyan.compose.ui.components.spinner.Spinner
            import com.toutiaoyan.compose.ui.components.text.Text
            import com.toutiaoyan.compose.ui.components.toast.*
            import com.toutiaoyan.compose.ui.styles.rememberShimmerStyle
            """.trimIndent(),
        usageCode =
            """
            Marker() {
                MarkerIcon { Icon(Lucide.Search, contentDescription = null) }
                MarkerContent("Explored 4 files")
            }
            """.trimIndent(),
        compositionCode =
            """
            Marker
            ├── MarkerIcon
            └── MarkerContent
            """.trimIndent(),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Marker",
                    (
                        "A presentational row; use modifier semantics for status and " +
                            "clickable for actions."
                    ),
                    listOf(
                        ApiProperty("variant", "MarkerVariant", "Default"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("content", "@Composable RowScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "MarkerIcon",
                    "A decorative 16dp icon slot, hidden from accessibility in Nova.",
                    listOf(
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "MarkerContent",
                    (
                        "The visible marker label; also accepts rich content. " +
                            "MarkerLabel remains a Kotlin alias."
                    ),
                    listOf(
                        ApiProperty("text / content", "String / @Composable () -> Unit", "Required"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
            ),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Features",
                    listOf(
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "Inline, bordered row, and labeled separator variants.",
                                "Decorative icons are hidden from assistive technology.",
                                "Compose links and actions using the marker content and modifier.",
                                "Pair streaming status text with the shimmer utility.",
                                "Customize each part through Modifier and Style.",
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
                            "Use a polite live region for progress updates. A label should describe the state " +
                                "without relying on the spinner or decorative icon. Compose " +
                                "liveRegion and URL annotations " +
                                "are platform adaptations; they are not a native HTML role=status " +
                                "or anchor DOM element.",
                        ),
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "Separator and border variants are presentational; meaningful " +
                                    "section headings remain the caller's responsibility.",
                                "Keep icons decorative and put the message in MarkerContent.",
                                "Use a real link annotation for navigation, and a button role for " +
                                    "actions; test keyboard activation.",
                            ),
                        ),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(
                            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(32.dp),
                        ) {
                            Marker() {
                                MarkerIcon { Icon(Lucide.GitBranch, contentDescription = null) }
                                MarkerContent("Switched to a new branch")
                            }
                            Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                MarkerIcon { Spinner() }
                                MarkerContent { Text(style = rememberShimmerStyle()) { "Thinking..." } }
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Conversation compacted")
                            }
                            Marker() {
                                MarkerIcon { Icon(Lucide.Search, contentDescription = null) }
                                MarkerContent("Explored 4 files")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    384.dp,
                            ).fillMaxWidth().padding(
                                vertical =
                                    48.dp,
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(32.dp),
                        ) {
                            Marker {
                                MarkerIcon { Icon(Lucide.GitBranch, contentDescription = null) }
                                MarkerContent("Switched to a new branch")
                            }
                            Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                MarkerIcon { Spinner() }
                                MarkerContent { Text(style = rememberShimmerStyle()) { "Thinking..." } }
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Conversation compacted")
                            }
                            Marker {
                                MarkerIcon { Icon(Lucide.Search, contentDescription = null) }
                                MarkerContent("Explored 4 files")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Variants",
                    descriptionMarkdown =
                        "Use variant to switch between an inline marker, bordered row, and labeled separator.",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(
                            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(32.dp),
                        ) {
                            Marker() {
                                MarkerContent("A default marker for inline notes.")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("A separator marker")
                            }
                            Marker(variant = MarkerVariant.Border) {
                                MarkerContent("A border marker for row boundaries.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    384.dp,
                            ).fillMaxWidth().padding(
                                vertical =
                                    48.dp,
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(32.dp),
                        ) {
                            Marker {
                                MarkerContent("A default marker for inline notes.")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("A separator marker")
                            }
                            Marker(variant = MarkerVariant.Border) {
                                MarkerContent("A border marker for row boundaries.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Status",
                    descriptionMarkdown =
                        "Use a polite live region and a Spinner for streaming or in-progress status updates.",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(
                            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(32.dp),
                        ) {
                            Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                MarkerIcon { Spinner() }
                                MarkerContent("Compacting conversation")
                            }
                            Marker(
                                variant = MarkerVariant.Separator,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            ) {
                                MarkerIcon { Spinner() }
                                MarkerContent("Running tests")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    384.dp,
                            ).fillMaxWidth().padding(
                                vertical =
                                    48.dp,
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(32.dp),
                        ) {
                            Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                MarkerIcon { Spinner() }
                                MarkerContent("Compacting conversation")
                            }
                            Marker(
                                variant =
                                    MarkerVariant.Separator,
                                modifier =
                                    Modifier.semantics {
                                        liveRegion =
                                            LiveRegionMode.Polite
                                    },
                            ) {
                                MarkerIcon { Spinner() }
                                MarkerContent("Running tests")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Shimmer",
                    descriptionMarkdown =
                        "Apply rememberShimmerStyle to MarkerContent text for an animated streaming effect.",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(
                            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(32.dp),
                        ) {
                            Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                MarkerContent { Text(style = rememberShimmerStyle()) { "Thinking..." } }
                            }
                            Marker(
                                variant = MarkerVariant.Separator,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            ) {
                                MarkerContent { Text(style = rememberShimmerStyle()) { "Reading 4 files" } }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    384.dp,
                            ).fillMaxWidth().padding(
                                vertical =
                                    48.dp,
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(32.dp),
                        ) {
                            Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                MarkerContent { Text(style = rememberShimmerStyle()) { "Thinking..." } }
                            }
                            Marker(
                                variant =
                                    MarkerVariant.Separator,
                                modifier =
                                    Modifier.semantics {
                                        liveRegion =
                                            LiveRegionMode.Polite
                                    },
                            ) {
                                MarkerContent { Text(style = rememberShimmerStyle()) { "Reading 4 files" } }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    descriptionMarkdown =
                        "Use the separator variant for labeled dividers, such as dates or section breaks.",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(
                            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(32.dp),
                        ) {
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Today")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Worked for 42s")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Conversation compacted")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    384.dp,
                            ).fillMaxWidth().padding(
                                vertical =
                                    48.dp,
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(32.dp),
                        ) {
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Today")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Worked for 42s")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerContent("Conversation compacted")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Border",
                    descriptionMarkdown =
                        "Use the border variant to separate status rows while keeping their default alignment.",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(
                            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Marker(variant = MarkerVariant.Border) {
                                MarkerIcon { Icon(Lucide.GitBranch, contentDescription = null) }
                                MarkerContent("Switched to release-candidate")
                            }
                            Marker(variant = MarkerVariant.Border) {
                                MarkerIcon { Icon(Lucide.Search, contentDescription = null) }
                                MarkerContent("Reviewed 8 related files")
                            }
                            Marker(variant = MarkerVariant.Border) {
                                MarkerIcon { Icon(Lucide.FileText, contentDescription = null) }
                                MarkerContent("Opened implementation notes")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    384.dp,
                            ).fillMaxWidth().padding(
                                vertical =
                                    48.dp,
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp),
                        ) {
                            Marker(variant = MarkerVariant.Border) {
                                MarkerIcon { Icon(Lucide.GitBranch, contentDescription = null) }
                                MarkerContent("Switched to release-candidate")
                            }
                            Marker(variant = MarkerVariant.Border) {
                                MarkerIcon { Icon(Lucide.Search, contentDescription = null) }
                                MarkerContent("Reviewed 8 related files")
                            }
                            Marker(variant = MarkerVariant.Border) {
                                MarkerIcon { Icon(Lucide.FileText, contentDescription = null) }
                                MarkerContent("Opened implementation notes")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "With Icon",
                    descriptionMarkdown =
                        "Use MarkerIcon alongside the content, or stack the icon and content in a Column.",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Column(
                            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
                            verticalArrangement = Arrangement.spacedBy(48.dp),
                        ) {
                            Marker() {
                                MarkerIcon { Icon(Lucide.GitBranch, contentDescription = null) }
                                MarkerContent("Switched to a new branch")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerIcon { Icon(Lucide.Search, contentDescription = null) }
                                MarkerContent("Explored 4 files")
                            }
                            Marker {
                                Column(
                                    Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    MarkerIcon { Icon(Lucide.BookOpenCheck, contentDescription = null) }
                                    MarkerContent("Syncing completed")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max =
                                    384.dp,
                            ).fillMaxWidth().padding(
                                vertical =
                                    48.dp,
                            ),
                            verticalArrangement =
                                Arrangement.spacedBy(48.dp),
                        ) {
                            Marker {
                                MarkerIcon { Icon(Lucide.GitBranch, contentDescription = null) }
                                MarkerContent("Switched to a new branch")
                            }
                            Marker(variant = MarkerVariant.Separator) {
                                MarkerIcon { Icon(Lucide.Search, contentDescription = null) }
                                MarkerContent("Explored 4 files")
                            }
                            Marker {
                                Column(
                                    Modifier.fillMaxWidth(),
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally,
                                    verticalArrangement =
                                        Arrangement.spacedBy(8.dp),
                                ) {
                                    MarkerIcon { Icon(Lucide.BookOpenCheck, contentDescription = null) }
                                    MarkerContent("Syncing completed")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Links and Buttons",
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 40.dp,
                    descriptionMarkdown = "Render markers as links or buttons to make them interactive.",
                    code = markerActionCode,
                    preview = { MarkerActionsRecipe() },
                ),
            ),
    )
