@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.platform.testTag

import androidx.compose.foundation.background
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.onGloballyPositioned
import com.toutiaoyan.compose.ui.catalog.docs.ApiReference
import com.toutiaoyan.compose.ui.catalog.docs.catalogInstallationCode
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.catalog.docs.CodeBlock
import com.toutiaoyan.compose.ui.catalog.docs.DocHeading
import com.toutiaoyan.compose.ui.catalog.docs.DocParagraph
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.ComponentDoc
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideSection
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.DocIcon
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.catalog.docs.SourceInstallationCode
import com.toutiaoyan.compose.ui.catalog.docs.componentDocsById
import com.toutiaoyan.compose.ui.catalog.docs.guidePagesById
import com.toutiaoyan.compose.ui.catalog.docs.sourceInstallations
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.utils.clipboard.rememberCopyToClipboard

/** Dispatches a sidebar entry to a guide or a component demo. */
@Composable
fun ComponentDetailScreen(
    componentId: String,
    onNavigateComponent: (String) -> Unit = {},
    scrollState: ScrollState? = null,
) {
    val pageScroll = scrollState ?: rememberScrollState()
    guidePagesById[componentId]?.let { guidePage ->
        CompositionLocalProvider(LocalGuideScrollState provides pageScroll) { guidePage.content() }
        return
    }
    val doc = componentDocsById[componentId] ?: return
    key(doc.id) { ComponentDetailContent(doc, onNavigateComponent, pageScroll) }
}

@Composable
internal fun ComponentDetailContent(
    doc: ComponentDoc,
    onNavigateComponent: (String) -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
) {
    val pageText = componentPageMarkdown(doc)
    val anchors = remember(doc.id) { DocAnchors() }
    val scope = rememberCoroutineScope()
    val sections = remember(doc) { docSections(doc) }
    val navigation = LocalDocNavigation.current
    val location = navigation?.location
    LaunchedEffect(location, doc.id, anchors, scrollState) {
        if (location?.componentId != doc.id || location.restoreScroll) return@LaunchedEffect
        if (location.fragment.isEmpty()) {
            scrollState.scrollTo(0)
        } else {
            val section =
                sections.firstOrNull { it.fragment == location.fragment || it.id == location.fragment }
                    ?: sections.firstOrNull { it.legacyFragment == location.fragment }
                    ?: return@LaunchedEffect
            scrollState.followDocumentAnchor(anchors, section.id, animated = location.animate)
        }
    }
    val activeSection by remember(anchors, sections, scrollState) {
        derivedStateOf {
            val offset = scrollState.value + 24
            sections.lastOrNull { (anchors.offsetOf(it.id) ?: Int.MAX_VALUE) <= offset }?.id.orEmpty()
        }
    }

    fun scrollToSection(id: String) {
        val section = sections.firstOrNull { it.id == id } ?: return
        if (navigation != null) {
            navigation.navigate(section.fragment)
        } else {
            anchors.offsetOf(id)?.let { target ->
                scope.launch { scrollState.seekDocumentAnchor(target, animated = true) }
            }
        }
    }
    val copy = rememberCopyToClipboard(pageText)
    val docs = componentDocsById.values.sortedBy { it.title }
    val index = docs.indexOf(doc)
    CompositionLocalProvider(
        LocalDocHeadingLinks provides
            sections.associate { section ->
                section.id to DocHeadingLink(section.fragment) { scrollToSection(section.id) }
            },
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val showPageNavigation = maxWidth >= 600.dp
            val horizontalPadding = if (catalogViewportWidth() >= 768.dp) 0.dp else 16.dp
            val showTableOfContents = catalogViewportWidth() >= 1280.dp
            Row(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier =
                            Modifier.widthIn(max = 640.dp).fillMaxWidth()
                                .onGloballyPositioned { anchors.content = it }
                                .padding(
                                    horizontal = horizontalPadding,
                                    // Official page: 48px top/bottom on desktop, 24px on phones
                                    // (measured h1 top 112 at 1440 vs 80 at 390).
                                    vertical = if (catalogViewportWidth() < 640.dp) 24.dp else 48.dp,
                                ).testTag("doc-column"),
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    variant = TextVariant.DisplayMedium,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    textStyle =
                                        currentTheme.typography.displayMedium.copy(
                                            fontSize = 30.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                ) { doc.title }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        text =
                                            if (copy.justCopied) {
                                                "Copied!"
                                            } else if (!showPageNavigation) {
                                                "Copy"
                                            } else {
                                                "Copy Page"
                                            },
                                        onClick = copy.copy,
                                        variant = ButtonVariant.Secondary,
                                        size = ButtonSize.Sm,
                                    )
                                    if (showPageNavigation) {
                                        docs.getOrNull(index - 1)?.let { previous ->
                                            Button(
                                                onClick = {
                                                    onNavigateComponent(previous.id)
                                                },
                                                variant =
                                                    ButtonVariant.Secondary,
                                                size =
                                                    ButtonSize.IconSm,
                                                label =
                                                    "Previous: ${previous.title}",
                                            ) {
                                                DocIcon(Lucide.ArrowLeft)
                                            }
                                        }
                                        docs.getOrNull(index + 1)?.let { next ->
                                            Button(
                                                onClick = {
                                                    onNavigateComponent(next.id)
                                                },
                                                variant =
                                                    ButtonVariant.Secondary,
                                                size =
                                                    ButtonSize.IconSm,
                                                label =
                                                    "Next: ${next.title}",
                                            ) {
                                                DocIcon(Lucide.ArrowRight)
                                            }
                                        }
                                    }
                                }
                            }
                            val descriptionViewport = catalogViewportWidth()
                            Text(
                                variant = TextVariant.BodyLarge,
                                modifier =
                                    if (descriptionViewport >= 768.dp) {
                                        Modifier.fillMaxWidth(0.8f)
                                    } else {
                                        Modifier.fillMaxWidth()
                                    },
                                textStyle =
                                    TextColors.Muted.copy(
                                        fontSize = if (descriptionViewport < 640.dp) 16.8.sp else 16.sp,
                                        lineHeight = if (descriptionViewport < 640.dp) 25.2.sp else 24.sp,
                                        lineBreak = LineBreak.Heading,
                                    ),
                            ) { doc.description }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                            if (doc.isKotlinExtension) {
                                Text(variant = TextVariant.BodyLarge) { "Kotlin extension" }
                            }
                            doc.examples.firstOrNull()?.let { example ->
                                PreviewCodeSection(
                                    code = example.code,
                                    modifier = Modifier.fillMaxWidth(),
                                    minPreviewHeight = example.minPreviewHeight,
                                    mobileMinPreviewHeight = example.mobileMinPreviewHeight,
                                    previewPadding = example.previewPadding,
                                    previewBackground = example.previewBackground,
                                    hideCode = example.hideCode,
                                    previewHeight = example.previewHeight,
                                    previewVerticalAlignment = example.previewVerticalAlignment,
                                    mobilePreviewPadding = example.mobilePreviewPadding,
                                    mobilePreviewVerticalAlignment = example.mobilePreviewVerticalAlignment,
                                    desktopPreviewInsetStart = example.desktopPreviewInsetStart,
                                    descriptionMarkdown = example.descriptionMarkdown,
                                    preview = example.preview,
                                )
                                doc.afterExampleSections[example.title]?.let { GuideBlocks(it) }
                            }
                        }
                        doc.introMarkdown?.let { DocParagraph(it) }
                        doc.introductionSections.forEachIndexed { index, section ->
                            GuideSection(section, anchors, "introduction-$index")
                        }
                        Section(
                            "Installation",
                            Modifier.docAnchor(anchors, "installation"),
                            spacing = 17.5.dp,
                            anchorId = "installation",
                        ) {
                            CodeBlock(
                                code = catalogInstallationCode,
                                modifier = Modifier.fillMaxWidth(),
                                title = "build.gradle.kts",
                            )
                            sourceInstallations(doc.id).forEach { installation ->
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(variant = TextVariant.TitleSmall) { installation.title }
                                    SourceInstallationCode(installation)
                                }
                            }
                            if (doc.installationBlocks.isNotEmpty()) GuideBlocks(doc.installationBlocks)
                        }
                        Section("Usage", Modifier.docAnchor(anchors, "usage"), spacing = 17.5.dp, anchorId = "usage") {
                            doc.usageImportsCode?.let {
                                CodeBlock(
                                    code = it,
                                    modifier = Modifier.fillMaxWidth(),
                                    lineNumbers = true,
                                )
                            }
                            CodeBlock(code = doc.usageCode, modifier = Modifier.fillMaxWidth(), lineNumbers = true)
                        }
                        doc.compositionCode?.let { code ->
                            Section(
                                "Composition",
                                Modifier.docAnchor(anchors, "composition"),
                                spacing = 17.5.dp,
                                anchorId = "composition",
                            ) {
                                doc.beforeCompositionMarkdown?.let { DocParagraph(it) }
                                CodeBlock(code = code, modifier = Modifier.fillMaxWidth(), language = null)
                                doc.compositionMarkdown?.let { DocParagraph(it) }
                            }
                        }
                        doc.beforeExamples.forEachIndexed { index, section ->
                            GuideSection(section, anchors, "before-guide-$index")
                        }
                        val additionalExamples = doc.examples.drop(1)
                        if (additionalExamples.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                doc.examplesHeading?.let {
                                    DocHeading(
                                        it,
                                        Modifier.docAnchor(anchors, "examples"),
                                        anchorId = "examples",
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(48.dp)) {
                                    additionalExamples.forEachIndexed { exampleIndex, example ->
                                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                            doc.beforeExampleSections[example.title]?.forEachIndexed {
                                                    guideIndex,
                                                    section,
                                                ->
                                                GuideSection(
                                                    section,
                                                    anchors,
                                                    "before-example-$exampleIndex-$guideIndex",
                                                )
                                            }
                                            PreviewCodeSection(
                                                code = example.code,
                                                title = example.headingTitle.takeIf { example.showHeading },
                                                headingLevel = example.headingLevel,
                                                modifier =
                                                    Modifier.fillMaxWidth().docAnchor(
                                                        anchors,
                                                        "example-$exampleIndex",
                                                    ),
                                                minPreviewHeight =
                                                    example.minPreviewHeight,
                                                mobileMinPreviewHeight =
                                                    example.mobileMinPreviewHeight,
                                                previewPadding =
                                                    example.previewPadding,
                                                previewBackground =
                                                    example.previewBackground,
                                                hideCode = example.hideCode,
                                                previewHeight = example.previewHeight,
                                                previewVerticalAlignment = example.previewVerticalAlignment,
                                                mobilePreviewPadding = example.mobilePreviewPadding,
                                                mobilePreviewVerticalAlignment = example.mobilePreviewVerticalAlignment,
                                                desktopPreviewInsetStart = example.desktopPreviewInsetStart,
                                                descriptionMarkdown =
                                                    example.descriptionMarkdown,
                                                headingAnchor =
                                                    "example-$exampleIndex",
                                                preview =
                                                    example.preview,
                                            )
                                            doc.afterExampleSections[example.title]?.let { GuideBlocks(it) }
                                        }
                                    }
                                }
                            }
                        }
                        doc.afterExamples.forEachIndexed { index, section ->
                            GuideSection(section, anchors, "after-guide-$index")
                        }
                        if (doc.apiProperties.isNotEmpty() || doc.apiSections.isNotEmpty()) {
                            Section(
                                "API Reference",
                                Modifier.docAnchor(anchors, "api-reference"),
                                anchorId = "api-reference",
                            ) {
                                if (doc.apiProperties.isNotEmpty()) ApiReference(doc.apiProperties)
                                doc.apiSections.forEachIndexed { index, section ->
                                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        DocHeading(
                                            section.title,
                                            Modifier.docAnchor(anchors, "api-$index"),
                                            level = 3,
                                            anchorId = "api-$index",
                                        )
                                        DocParagraph(section.descriptionMarkdown)
                                        ApiReference(section.properties)
                                    }
                                }
                            }
                        }
                        doc.afterApiSections.forEachIndexed { index, section ->
                            GuideSection(section, anchors, "after-api-guide-$index")
                        }
                    }
                }
                if (showTableOfContents) {
                    DocTableOfContents(sections, activeSection, ::scrollToSection)
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    spacing: Dp = 24.dp,
    anchorId: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing)) {
        DocHeading(title, anchorId = anchorId)
        content()
    }
}

@Composable
private fun GuideSection(
    section: ComponentGuideSection,
    anchors: DocAnchors,
    id: String,
) {
    Column(Modifier.fillMaxWidth()) {
        DocHeading(section.title, Modifier.docAnchor(anchors, id), level = section.level, anchorId = id)
        GuideBlocks(section.blocks)
    }
}

@Composable
internal fun GuideBlocks(blocks: List<ComponentGuideBlock>) {
    val mobile = catalogViewportWidth() < 640.dp
    val bodySize = if (mobile) 18.9f else 15f
    val lineHeight = bodySize * 1.75f
    Column(Modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            val gap =
                when {
                    block is ComponentGuideBlock.Code -> 17.5f
                    block is ComponentGuideBlock.Bullets || index > 0 -> bodySize * 1.25f
                    else -> bodySize
                }
            Spacer(Modifier.height(gap.dp))
            when (block) {
                is ComponentGuideBlock.Paragraph -> DocParagraph(block.markdown)
                is ComponentGuideBlock.Code ->
                    CodeBlock(
                        block.code,
                        Modifier.fillMaxWidth(),
                        language = block.language,
                        lineNumbers = true,
                    )
                is ComponentGuideBlock.Bullets ->
                    Column(
                        Modifier.fillMaxWidth().semantics { collectionInfo = CollectionInfo(block.items.size, 1) },
                        verticalArrangement = Arrangement.spacedBy((bodySize * .5f).dp),
                    ) {
                        block.items.forEach { item ->
                            Row(Modifier.fillMaxWidth()) {
                                // Prose list: 1.5em list inset + .4em item padding.
                                // The marker stays on the first line when an item wraps.
                                Box(
                                    Modifier.width((bodySize * 1.9f).dp).height(lineHeight.dp).clearAndSetSemantics {},
                                ) {
                                    Box(
                                        Modifier.offset(
                                            x = (bodySize * (5f / 6f - .15f)).dp,
                                            y = (lineHeight / 2f - bodySize * .15f).dp,
                                        )
                                            .size(
                                                (bodySize * .3f).dp,
                                            ).background(currentTheme.colors.mutedForeground, CircleShape),
                                    )
                                }
                                Column(Modifier.weight(1f)) { DocParagraph(item) }
                            }
                        }
                    }
                is ComponentGuideBlock.Table ->
                    Column(
                        Modifier.fillMaxWidth().semantics {
                            collectionInfo = CollectionInfo(block.rows.size + 1, block.headers.size)
                        },
                    ) {
                        (listOf(block.headers) + block.rows).forEachIndexed { rowIndex, cells ->
                            Row(Modifier.fillMaxWidth()) {
                                cells.forEach { cell ->
                                    Box(
                                        Modifier.weight(1f).padding(8.dp).then(
                                            if (rowIndex == 0) Modifier.semantics { heading() } else Modifier,
                                        ),
                                    ) { DocParagraph(cell) }
                                }
                            }
                            Box(Modifier.fillMaxWidth().height(1.dp).background(currentTheme.colors.border))
                        }
                    }
                is ComponentGuideBlock.Steps ->
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        block.items.forEachIndexed { index, step ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Box(
                                    Modifier.size(20.dp).background(currentTheme.colors.muted, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        textStyle = TextStyle(fontSize = 12.sp, lineHeight = 12.sp),
                                    ) { "${index + 1}" }
                                }
                                Column(
                                    Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Text(
                                        textStyle =
                                            TextStyle(
                                                fontSize = bodySize.sp,
                                                lineHeight = lineHeight.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                    ) { step.title }
                                    step.bodyMarkdown?.let { DocParagraph(it) }
                                    step.code?.let {
                                        CodeBlock(
                                            it,
                                            Modifier.fillMaxWidth(),
                                            language = step.language,
                                            lineNumbers = true,
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }
}

internal fun docSections(doc: ComponentDoc): List<DocSection> =
    buildList {
        doc.introductionSections.forEachIndexed { index, section ->
            add(DocSection("introduction-$index", section.title, nested = section.level == 3))
        }
        add(DocSection("installation", "Installation"))
        add(DocSection("usage", "Usage"))
        if (doc.compositionCode != null) add(DocSection("composition", "Composition"))
        doc.beforeExamples.forEachIndexed { index, section ->
            add(DocSection("before-guide-$index", section.title, nested = section.level == 3))
        }
        if (doc.examples.size > 1) {
            doc.examplesHeading?.let { add(DocSection("examples", it)) }
            doc.examples.drop(1).forEachIndexed { index, example ->
                doc.beforeExampleSections[example.title]?.forEachIndexed {
                        guideIndex,
                        section,
                    ->
                    add(
                        DocSection(
                            "before-example-$index-$guideIndex",
                            section.title,
                            nested = section.level == 3,
                        ),
                    )
                }
                if (example.showHeading) {
                    val title = example.headingTitle
                    add(
                        DocSection(
                            "example-$index",
                            title,
                            nested = doc.examplesHeading != null || example.headingLevel > 2,
                            legacyFragment = example.title.takeIf { it != title }?.let(::docFragment),
                        ),
                    )
                }
            }
        }
        doc.afterExamples.forEachIndexed { index, section ->
            add(DocSection("after-guide-$index", section.title, nested = section.level == 3))
        }
        if (doc.apiProperties.isNotEmpty() || doc.apiSections.isNotEmpty()) {
            add(DocSection("api-reference", "API Reference"))
            doc.apiSections.forEachIndexed {
                    index,
                    section,
                ->
                add(DocSection("api-$index", section.title, nested = true))
            }
        }
        doc.afterApiSections.forEachIndexed { index, section ->
            add(DocSection("after-api-guide-$index", section.title, nested = section.level == 3))
        }
    }.let { sections ->
        val used = mutableSetOf<String>()
        sections.map { section ->
            var fragment = section.fragment
            var suffix = 1
            while (!used.add(fragment)) fragment = "${section.fragment}-${suffix++}"
            section.copy(fragment = fragment)
        }
    }
