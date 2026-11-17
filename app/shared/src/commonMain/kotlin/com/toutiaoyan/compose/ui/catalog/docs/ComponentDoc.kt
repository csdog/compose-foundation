package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ComponentExample(
    val title: String,
    val code: String,
    val minPreviewHeight: Dp = 288.dp,
    val mobileMinPreviewHeight: Dp? = null,
    val previewPadding: Dp = 40.dp,
    val descriptionMarkdown: String? = null,
    val previewBackground: (@Composable () -> Color)? = null,
    val hideCode: Boolean = false,
    val previewHeight: Dp? = null,
    val previewVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    /** Steps keep their content inset on desktop and use the full column below640px. */
    val desktopPreviewInsetStart: Dp = 0.dp,
    val showHeading: Boolean = true,
    /** Nested step examples use level 3; ordinary recipes remain level 2. */
    val headingLevel: Int = 2,
    val mobilePreviewPadding: Dp? = null,
    val mobilePreviewVerticalAlignment: Alignment.Vertical? = null,
    val preview: @Composable () -> Unit,
)

data class ComponentDoc(
    val id: String,
    val title: String,
    val description: String,
    val usageCode: String,
    val examples: List<ComponentExample>,
    val apiProperties: List<ApiProperty> = emptyList(),
    val compositionCode: String? = null,
    val examplesHeading: String? = "Examples",
    val apiSections: List<ComponentApiSection> = emptyList(),
    val usageImportsCode: String? = null,
    val introductionSections: List<ComponentGuideSection> = emptyList(),
    val beforeExamples: List<ComponentGuideSection> = emptyList(),
    val beforeExampleSections: Map<String, List<ComponentGuideSection>> = emptyMap(),
    val afterExamples: List<ComponentGuideSection> = emptyList(),
    val afterApiSections: List<ComponentGuideSection> = emptyList(),
    /** Project-only components show a "Kotlin extension" label instead of the Base UI tab. */
    val isKotlinExtension: Boolean = false,
    /** Headingless prose following the primary preview, before Installation. */
    val introMarkdown: String? = null,
    /** Headingless guidance immediately after the Composition snippet. */
    val compositionMarkdown: String? = null,
    /** Headingless source guidance after a recipe's preview and code panel. */
    val afterExampleSections: Map<String, List<ComponentGuideBlock>> = emptyMap(),
    /** Headingless guidance immediately before the Composition snippet. */
    val beforeCompositionMarkdown: String? = null,
)

/** Prose between the component's recipes, with the same content used by Copy Page. */
data class ComponentGuideSection(
    val title: String,
    val blocks: List<ComponentGuideBlock>,
    val level: Int = 2,
)

sealed interface ComponentGuideBlock {
    data class Paragraph(val markdown: String) : ComponentGuideBlock

    data class Bullets(val items: List<String>) : ComponentGuideBlock

    data class Code(val code: String, val language: String? = "kotlin") : ComponentGuideBlock

    data class Steps(val items: List<Step>) : ComponentGuideBlock

    data class Table(val headers: List<String>, val rows: List<List<String>>) : ComponentGuideBlock

    data class Step(
        val title: String,
        val bodyMarkdown: String? = null,
        val code: String? = null,
        val language: String? = "kotlin",
    )
}

data class ComponentApiSection(
    val title: String,
    val descriptionMarkdown: String,
    val properties: List<ApiProperty>,
)
