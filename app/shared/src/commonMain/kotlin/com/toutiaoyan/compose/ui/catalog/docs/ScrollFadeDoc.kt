@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.styles.scrollFade

val scrollFadeDoc =
    ComponentDoc(
        id = "scroll-fade",
        title = "scroll-fade",
        description =
            "Utilities for adding a fade effect to the edges of a scroll container.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.styles.scrollFade

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.height(160.dp).scrollFade(scrollState).verticalScroll(scrollState),
            ) {
                repeat(20) { Text { "Row ${'$'}it" } }
            }
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Usage",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Place `scrollFade` before `verticalScroll` or " +
                                    "`horizontalScroll` so the mask measures the viewport. "
                            ) +
                                (
                                    "Put the background and border on an outer wrapper. The " +
                                        "default depth is 12%, capped at 40.dp, "
                                ) +
                                (
                                    "revealed over 96.dp of scrolling. Use `ScrollFadeEdge`, " +
                                        "`ScrollFadeSize` or `enabled = false` "
                                ) +
                                "for single edges, custom lengths/percentages and no fade.",
                        ),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Rows (Kotlin)",
                    code =
                        """
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier.width(240.dp).height(160.dp)
                                .scrollFade(scrollState).verticalScroll(scrollState),
                        ) {
                            repeat(20) { index -> Text(modifier = Modifier.padding(vertical = 4.dp)) { "Row ${'$'}index" } }
                        }
                        """.trimIndent(),
                    preview = {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier =
                                Modifier
                                    .width(240.dp)
                                    .height(160.dp)
                                    .scrollFade(scrollState)
                                    .verticalScroll(scrollState),
                        ) {
                            repeat(20) { index ->
                                Text(modifier = Modifier.padding(vertical = 4.dp)) { "Row $index" }
                            }
                        }
                    },
                ),
            ),
    )
        .let { native -> native.copy(examples = scrollFadeOfficialExamples + native.examples) }
