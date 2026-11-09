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
        introMarkdown =
            "The `Marker` component displays inline conversation markers such as status updates, " +
                "system notes, bordered rows, and labeled separators. Compose it with " +
                "[Message](/message) in a conversation thread.",
        beforeCompositionMarkdown = "Use the following composition to build a marker:",
        afterExampleSections =
            mapOf(
                "Variants" to
                    listOf(
                        ComponentGuideBlock.Table(
                            headers = listOf("Variant", "Description"),
                            rows =
                                listOf(
                                    listOf("`Default`", "An inline marker for status, notes, and actions."),
                                    listOf("`Border`", "A default marker with a bottom border under the row."),
                                    listOf("`Separator`", "A centered label with divider lines on each side."),
                                ),
                        ),
                    ),
            ),
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
                                "Pair streaming status text with the [shimmer](/shimmer) utility.",
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
                            "`Marker` is presentational by default. Choose semantics based on the marker's purpose.",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Status and Progress",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Use a polite live region for streaming or progress updates. This is the Compose " +
                                "adaptation of HTML `role=status`; it does not create a native HTML status element.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Marker(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                                MarkerIcon { Spinner() }
                                MarkerContent("Compacting conversation")
                            }
                            """.trimIndent(),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Labeled Separators",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "A labeled separator needs no separator role. Its lines are decorative and its text " +
                                "is ordinary content. An HTML separator role hides its child content from " +
                                "assistive technology; keep meaningful labels exposed.",
                        ),
                        ComponentGuideBlock.Code("Marker(\"Today\", variant = MarkerVariant.Separator)"),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Bordered Markers",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The bottom border is decorative. Choose a live region, interactive action, or " +
                                "ordinary content based on the marker's purpose, just as for the default variant.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Marker(variant = MarkerVariant.Border) {
                                MarkerIcon { Icon(Lucide.FileText, contentDescription = null) }
                                MarkerContent("Opened implementation notes")
                            }
                            """.trimIndent(),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Decorative Icons",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "In Nova, `MarkerIcon` hides its decorative content from assistive technology. " +
                                "Put the meaning in `MarkerContent`. For an icon-only marker, provide an " +
                                "accessible name on the root so it is not announced as empty.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Marker(modifier = Modifier.semantics { contentDescription = "Synced" }) {
                                MarkerIcon { Icon(Lucide.Check, contentDescription = null) }
                            }
                            """.trimIndent(),
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Interactive Markers",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Compose actions with `Modifier.clickable(role = Role.Button)` and navigation with " +
                                "`LinkAnnotation.Url`, as in Links and Buttons. The visible marker text supplies " +
                                "the accessible name. Test keyboard activation. A URL annotation is a platform " +
                                "adaptation and does not create a native anchor DOM element.",
                        ),
                    ),
                    level = 3,
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
                        "Use `variant` to switch between an inline marker, bordered row, and labeled separator.",
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
                        "Use `Modifier.semantics { liveRegion = LiveRegionMode.Polite }` and a `Spinner` " +
                            "for streaming or in-progress markers so updates are announced.",
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
                        "Add the shimmer utility to `MarkerContent` for an animated streaming-text " +
                            "effect. The catalog applies it with `rememberShimmerStyle()`.",
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
                        "Use the `separator` variant for labeled dividers, such as dates or section " +
                            "breaks, in a conversation. A " +
                            "[Checkpoint](https://ui.shadcn.com/docs/ai-elements/base/checkpoint) " +
                            "is a separator with a button that restores the thread to that point.",
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
                        "Use the `border` variant for status rows that should keep the default marker " +
                            "alignment while separating the next row.",
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
                        "Use `MarkerIcon` to render an icon alongside the content. Use a column to " +
                            "stack the icon above the content.",
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
                    descriptionMarkdown =
                        "Compose a marker link with `LinkAnnotation.Url`, or an action with " +
                            "`Modifier.clickable(role = Role.Button)`.",
                    code = markerActionCode,
                    preview = { MarkerActionsRecipe() },
                ),
            ),
    )
