@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
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
import com.toutiaoyan.compose.ui.catalog.docs.componentDocsById
import com.toutiaoyan.compose.ui.catalog.docs.guidePagesById
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
    guidePagesById[componentId]?.let { guidePage ->
        guidePage.content()
        return
    }
    val doc = componentDocsById[componentId] ?: return
    val pageScroll = scrollState ?: rememberScrollState()
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
        if (location?.componentId != doc.id) return@LaunchedEffect
        if (location.fragment.isEmpty()) {
            scrollState.scrollTo(0)
        } else {
            val section =
                sections.firstOrNull { it.fragment == location.fragment || it.id == location.fragment }
                    ?: return@LaunchedEffect
            val target = anchors.awaitOffsetOf(section.id)
            if (location.animate) scrollState.animateScrollTo(target) else scrollState.scrollTo(target)
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
            anchors.offsetOf(id)?.let { target -> scope.launch { scrollState.animateScrollTo(target) } }
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
            val horizontalPadding = if (maxWidth >= 760.dp) 76.dp else 24.dp
            val showTableOfContents = maxWidth >= 1100.dp
            Row(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollState),
                ) {
                    Column(
                        modifier =
                            Modifier.widthIn(max = 792.dp).fillMaxWidth()
                                .onGloballyPositioned { anchors.content = it }
                                .padding(horizontal = horizontalPadding, vertical = 48.dp),
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
                            Text(variant = TextVariant.BodyLarge, textStyle = TextColors.Muted) { doc.description }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(variant = TextVariant.BodyLarge) { "Base UI" }
                                Box(
                                    Modifier.widthIn(
                                        min = 56.dp,
                                    ).height(2.dp).background(currentTheme.colors.foreground),
                                )
                            }
                            doc.examples.firstOrNull()?.let { example ->
                                PreviewCodeSection(
                                    code = example.code,
                                    modifier = Modifier.fillMaxWidth(),
                                    minPreviewHeight = example.minPreviewHeight,
                                    mobileMinPreviewHeight = example.mobileMinPreviewHeight,
                                    previewPadding = example.previewPadding,
                                    previewBackground = example.previewBackground,
                                    descriptionMarkdown = example.descriptionMarkdown,
                                    preview = example.preview,
                                )
                            }
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
                                CodeBlock(code = code, modifier = Modifier.fillMaxWidth(), language = null)
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
                                        PreviewCodeSection(
                                            code = example.code, title = example.title,
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
                                            descriptionMarkdown =
                                                example.descriptionMarkdown,
                                            headingAnchor =
                                                "example-$exampleIndex",
                                            preview =
                                                example.preview,
                                        )
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
    val mobile = LocalWindowInfo.current.containerSize.width < with(LocalDensity.current) { 640.dp.roundToPx() }
    val bodySize = if (mobile) 18.9f else 15f
    val lineHeight = bodySize * 1.75f
    Column(Modifier.fillMaxWidth()) {
        DocHeading(section.title, Modifier.docAnchor(anchors, id), level = section.level, anchorId = id)
        section.blocks.forEachIndexed { index, block ->
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
            }
        }
    }
}

internal fun docSections(doc: ComponentDoc): List<DocSection> =
    buildList {
        add(DocSection("installation", "Installation"))
        add(DocSection("usage", "Usage"))
        if (doc.compositionCode != null) add(DocSection("composition", "Composition"))
        doc.beforeExamples.forEachIndexed { index, section ->
            add(DocSection("before-guide-$index", section.title, nested = section.level == 3))
        }
        if (doc.examples.size > 1) {
            doc.examplesHeading?.let { add(DocSection("examples", it)) }
            doc.examples.drop(1).forEachIndexed { index, example ->
                add(DocSection("example-$index", example.title, nested = doc.examplesHeading != null))
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
    }.let { sections ->
        val used = mutableSetOf<String>()
        sections.map { section ->
            var fragment = section.fragment
            var suffix = 1
            while (!used.add(fragment)) fragment = "${section.fragment}-${suffix++}"
            section.copy(fragment = fragment)
        }
    }
