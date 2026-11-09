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
        isKotlinExtension = true,
        examplesHeading = null,
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.styles.scrollFade
            import com.toutiaoyan.compose.ui.styles.ScrollFadeEdge
            import com.toutiaoyan.compose.ui.styles.ScrollFadeSize
            import com.toutiaoyan.compose.ui.styles.ScrollFadeOrientation
            """.trimIndent(),
        usageCode =
            """
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
                    "Utility reference",
                    listOf(
                        ComponentGuideBlock.Table(
                            headers = listOf("Class", "Behavior", "Kotlin equivalent"),
                            rows =
                                listOf(
                                    listOf(
                                        "`scroll-fade`",
                                        "Both vertical edges",
                                        "`scrollFade(state)`",
                                    ),
                                    listOf(
                                        "`scroll-fade-y`",
                                        "Both vertical edges",
                                        "`orientation = ScrollFadeOrientation.Vertical`",
                                    ),
                                    listOf(
                                        "`scroll-fade-x`",
                                        "Both logical horizontal edges",
                                        "`orientation = ScrollFadeOrientation.Horizontal`",
                                    ),
                                    listOf(
                                        "`scroll-fade-t`",
                                        "Top edge",
                                        "`edge = ScrollFadeEdge.Top`",
                                    ),
                                    listOf(
                                        "`scroll-fade-b`",
                                        "Bottom edge",
                                        "`edge = ScrollFadeEdge.Bottom`",
                                    ),
                                    listOf(
                                        "`scroll-fade-l`",
                                        "Physical left edge",
                                        "`edge = ScrollFadeEdge.Left`",
                                    ),
                                    listOf(
                                        "`scroll-fade-r`",
                                        "Physical right edge",
                                        "`edge = ScrollFadeEdge.Right`",
                                    ),
                                    listOf(
                                        "`scroll-fade-s`",
                                        "Logical start edge, mirrors in RTL",
                                        "`edge = ScrollFadeEdge.Start`",
                                    ),
                                    listOf(
                                        "`scroll-fade-e`",
                                        "Logical end edge, mirrors in RTL",
                                        "`edge = ScrollFadeEdge.End`",
                                    ),
                                    listOf(
                                        "`scroll-fade-<number>`",
                                        "Spacing-scale depth (4px per unit)",
                                        "`size = ScrollFadeSize.Length(16.dp)` for `scroll-fade-4`",
                                    ),
                                    listOf(
                                        "`scroll-fade-[<value>]`",
                                        "Arbitrary length or percentage",
                                        "`ScrollFadeSize.Length(dp)` or `Fraction(0.15f)`",
                                    ),
                                    listOf(
                                        "`scroll-fade-{t,b,s,e}-<number>`",
                                        "Per-edge spacing-scale depth",
                                        "`leadingSize` / `trailingSize` with `Length(dp)`",
                                    ),
                                    listOf(
                                        "`scroll-fade-{t,b,s,e}-[<value>]`",
                                        "Per-edge arbitrary depth",
                                        "`leadingSize` / `trailingSize` with `Length` or `Fraction`",
                                    ),
                                    listOf(
                                        "`scroll-fade-none`",
                                        "Remove the mask",
                                        "`enabled = false`",
                                    ),
                                ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Kotlin extension: Scroll fade rules",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Place `scrollFade` before `verticalScroll` or " +
                                    "`horizontalScroll` so the mask measures the viewport, and " +
                                    "put the background and border on an outer wrapper. The " +
                                    "default depth is `min(12%, 40.dp)`, revealed over `96dp` " +
                                    "of scrolling."
                            ),
                        ),
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "`ScrollFadeEdge.Both` / `Top` / `Bottom` / `Left` / `Right` / " +
                                    "`Start` / `End` choose the faded edges.",
                                "`ScrollFadeEdge.Start` and `End` are logical inline edges that mirror in RTL.",
                                "`ScrollFadeSize.Default` / `Length(dp)` / `Fraction(0.15f)` set the " +
                                    "fade depth; per-edge sizes override it.",
                                "`enabled = false` removes the fade in any state or responsive branch.",
                                "`revealDistance` controls how far you scroll before the fade reaches full depth.",
                            ),
                        ),
                        ComponentGuideBlock.Paragraph(
                            (
                                "The fade is applied with a mask, so it dissolves the content " +
                                    "itself rather than overlaying a color. At rest the start " +
                                    "edge is crisp and the end edge fades; mid-scroll both edges " +
                                    "stay faded; at the end the trailing edge sharpens. If the " +
                                    "content does not overflow, no fade is shown."
                            ),
                        ),
                    ),
                ),
            ),
        beforeExampleSections =
            mapOf(
                "RTL" to
                    listOf(
                        ComponentGuideSection(
                            "Fallback",
                            listOf(
                                ComponentGuideBlock.Paragraph(
                                    (
                                        "The upstream CSS uses scroll-driven animations without JavaScript " +
                                            "or scroll listeners. Browsers without support use a static fade " +
                                            "on both edges, or on the selected edge for edge utilities. " +
                                            "Compose drives the scroll-aware mask from the `ScrollState` " +
                                            "position and eases in over `revealDistance`; no scroll " +
                                            "listeners are installed. Since the mask is applied to the " +
                                            "scroll container itself, a visible scrollbar fades with " +
                                            "the content at the edges. The upstream `no-scrollbar` utility " +
                                            "hides it entirely; Compose scrollbars are added separately."
                                    ),
                                ),
                            ),
                        ),
                    ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Kotlin extension: Rows",
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
