@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.scrollarea.LazyScrollArea
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollArea
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollAreaOrientation
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollDir
import com.toutiaoyan.compose.ui.components.scrollarea.ScrollType
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text

val scrollAreaDoc =
    ComponentDoc(
        id = "scroll-area",
        title = "Scroll Area",
        description =
            "Augments native scroll functionality for custom, cross-browser styling.",
        examplesHeading = null,
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.scrollarea.ScrollArea
            import com.toutiaoyan.compose.ui.components.scrollarea.LazyScrollArea
            import com.toutiaoyan.compose.ui.components.scrollarea.ScrollType
            import com.toutiaoyan.compose.ui.components.scrollarea.ScrollDir
            """.trimIndent(),
        compositionCode =
            """
            ScrollArea
            └── ScrollBar
            """.trimIndent(),
        usageCode =
            """
            // Default: Auto (show when content overflows)
            ScrollArea(modifier = Modifier.height(200.dp)) {
                Column { repeat(20) { Text { "Item ${'$'}it" } } }
            }

            ScrollArea(type = ScrollType.Always, dir = ScrollDir.Rtl, modifier = Modifier.height(200.dp)) {
                Column { repeat(20) { Text { "Item ${'$'}it" } } }
            }

            val state = rememberLazyListState()
            LazyScrollArea(state = state, type = ScrollType.Auto, modifier = Modifier.height(200.dp)) {
                LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
                    items(100) { Text { "Item ${'$'}it" } }
                }
            }
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Kotlin extension: Compose customization",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Use `style` for the root and `viewportStyle` for the scrolling viewport. " +
                                (
                                    "When setting a custom root shape, set the same shape on " +
                                        "`viewportStyle` for its focus ring. "
                                ) +
                                (
                                    "The default visibility is `ScrollType.Auto`; pass " +
                                        "`ScrollType.Hover()` for hover-only controls."
                                ),
                        ),
                    ),
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "API Reference",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "See the [Base UI Scroll Area](" +
                                "https://base-ui.com/react/components/scroll-area#api-reference) " +
                                "documentation.",
                        ),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Hover (Kotlin)",
                    code =
                        """
                        ScrollArea(type = ScrollType.Hover(), modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(type = ScrollType.Hover(), modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default type",
                    code =
                        """
                        ScrollArea(type = ScrollType.Hover(), modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(type = ScrollType.Hover(), modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Always",
                    code =
                        """
                        ScrollArea(type = ScrollType.Always, modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(type = ScrollType.Always, modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Auto",
                    code =
                        """
                        ScrollArea(type = ScrollType.Auto, modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(type = ScrollType.Auto, modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Scroll (custom hide delay)",
                    code =
                        """
                        ScrollArea(
                            type = ScrollType.Scroll(),
                            scrollHideDelay = 1200,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(
                            type = ScrollType.Scroll(),
                            scrollHideDelay = 1200,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default hide delay",
                    code =
                        """
                        ScrollArea(
                            type = ScrollType.Hover(),
                            scrollHideDelay = 600,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(
                            type = ScrollType.Hover(),
                            scrollHideDelay = 600,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Rtl",
                    code =
                        """
                        ScrollArea(
                            type = ScrollType.Auto,
                            dir = ScrollDir.Rtl,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(
                            type = ScrollType.Auto,
                            dir = ScrollDir.Rtl,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dir",
                    code =
                        """
                        ScrollArea(
                            type = ScrollType.Auto,
                            dir = null,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(
                            type = ScrollType.Auto,
                            dir = null,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Lazy list",
                    code =
                        """
                        val state = rememberLazyListState()
                        LazyScrollArea(
                            state = state,
                            type = ScrollType.Auto,
                            modifier = Modifier.height(160.dp),
                        ) {
                            LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
                                items(40) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Row ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        val state = rememberLazyListState()
                        LazyScrollArea(
                            state = state,
                            type = ScrollType.Auto,
                            modifier = Modifier.height(160.dp),
                        ) {
                            LazyColumn(state = state, modifier = Modifier.fillMaxSize()) {
                                items(40) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Row $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal (Kotlin)",
                    code =
                        """
                        ScrollArea(
                            orientation = ScrollAreaOrientation.Horizontal,
                            type = ScrollType.Always,
                            modifier = Modifier.height(48.dp),
                        ) {
                            Row {
                                repeat(12) { index ->
                                    Text(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                                        "Tag ${'$'}index"
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(
                            orientation = ScrollAreaOrientation.Horizontal,
                            type = ScrollType.Always,
                            modifier = Modifier.height(48.dp),
                        ) {
                            Row {
                                repeat(12) { index ->
                                    Text(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                                        "Tag $index"
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        ScrollArea(
                            orientation = ScrollAreaOrientation.Vertical,
                            type = ScrollType.Always,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(
                            orientation = ScrollAreaOrientation.Vertical,
                            type = ScrollType.Always,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        ScrollArea(
                            orientation = ScrollAreaOrientation.Vertical,
                            type = ScrollType.Always,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(
                            orientation = ScrollAreaOrientation.Vertical,
                            type = ScrollType.Always,
                            modifier = Modifier.height(160.dp),
                        ) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "ScrollBar",
                    code =
                        """
                        ScrollArea(type = ScrollType.Always, modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag ${'$'}index" }
                                    Separator()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        ScrollArea(type = ScrollType.Always, modifier = Modifier.height(160.dp)) {
                            Column {
                                repeat(20) { index ->
                                    Text(modifier = Modifier.padding(vertical = 6.dp)) { "Tag $index" }
                                    Separator()
                                }
                            }
                        }
                    },
                ),
            ),
    )
        .let { native ->
            native.copy(
                examples = scrollAreaOfficialExamples + scrollAreaExtensionExamples(native.examples),
            )
        }


/**
 * Kotlin-only recipes: the Base UI page does not document these type/dir/lazy variants
 * and the remaining legacy list duplicated the three official previews.
 */
private fun scrollAreaExtensionExamples(native: List<ComponentExample>): List<ComponentExample> =
    native.filter {
        it.title in
            setOf(
                "Hover (Kotlin)",
                "Always",
                "Scroll (custom hide delay)",
                "Lazy list",
            )
    }.map {
        it.copy(
            title =
                when (it.title) {
                    "Hover (Kotlin)" -> "Kotlin extension: Hover-only scrollbar"
                    "Always" -> "Kotlin extension: Always-visible scrollbar"
                    "Scroll (custom hide delay)" -> "Kotlin extension: Custom hide delay"
                    else -> "Kotlin extension: Lazy list"
                },
        )
    }

