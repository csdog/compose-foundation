@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.catalog.docs.Breadcrumb
import com.toutiaoyan.compose.ui.catalog.docs.CodeBlock
import com.toutiaoyan.compose.ui.catalog.docs.ComponentDoc
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.catalog.docs.componentDocsById
import com.toutiaoyan.compose.ui.catalog.docs.guidePagesById
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twSpacing

/** Dispatches a sidebar entry id to either a Getting Started guide page or a component demo. */
@Composable
fun ComponentDetailScreen(componentId: String) {
    guidePagesById[componentId]?.let { guidePage ->
        guidePage.content()
        return
    }
    val doc = componentDocsById[componentId] ?: return
    ComponentDetailContent(doc)
}

@Composable
private fun ComponentDetailContent(doc: ComponentDoc) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(twSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xl2),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            Breadcrumb("Components", doc.title)
            Text(variant = TextVariant.TitleLarge) { doc.title }
            Text(variant = TextVariant.BodyLarge, textStyle = TextColors.Muted) { doc.description }
        }

        Section(title = "Preview & Code") {
            PreviewCodeSection(
                code = doc.examples.first().code,
                title = doc.examples.first().title,
                modifier = Modifier.fillMaxWidth(),
                preview = doc.examples.first().preview,
            )
        }

        Section(title = "Usage") {
            CodeBlock(code = doc.usageCode, modifier = Modifier.fillMaxWidth())
        }

        // doc.examples.first() is already shown above under "Preview & Code" -- re-showing
        // it here too was displaying the exact same preview+code a second time on every
        // single component page (a third time, near-identically, counting "Usage"), most
        // visibly on the many components with only one example, where "Examples" rendered
        // nothing but a word-for-word repeat of "Preview & Code". Only the *remaining*
        // examples belong here, and only when there's more than one to show.
        val additionalExamples = doc.examples.drop(1)
        if (additionalExamples.isNotEmpty()) {
            Section(title = "Examples") {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xl)) {
                    additionalExamples.forEach { example ->
                        PreviewCodeSection(
                            code = example.code,
                            title = example.title,
                            modifier = Modifier.fillMaxWidth(),
                            preview = example.preview,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(twSpacing.md)) {
        Text(variant = TextVariant.TitleMedium) { title }
        content()
    }
}
