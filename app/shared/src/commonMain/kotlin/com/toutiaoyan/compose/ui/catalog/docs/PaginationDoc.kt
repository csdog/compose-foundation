@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.pagination.Pagination
import com.toutiaoyan.compose.ui.components.pagination.PaginationContent
import com.toutiaoyan.compose.ui.components.pagination.PaginationEllipsis
import com.toutiaoyan.compose.ui.components.pagination.PaginationItem
import com.toutiaoyan.compose.ui.components.pagination.PaginationFirst
import com.toutiaoyan.compose.ui.components.pagination.PaginationLast
import com.toutiaoyan.compose.ui.components.pagination.PaginationLink
import com.toutiaoyan.compose.ui.components.pagination.PaginationNext
import com.toutiaoyan.compose.ui.components.pagination.PaginationPrevious

val paginationDoc =
    ComponentDoc(
        id = "pagination",
        title = "Pagination",
        description = "Pagination with page navigation, next and previous links.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.pagination.Pagination
            import com.toutiaoyan.compose.ui.components.pagination.PaginationContent
            import com.toutiaoyan.compose.ui.components.pagination.PaginationEllipsis
            import com.toutiaoyan.compose.ui.components.pagination.PaginationItem
            import com.toutiaoyan.compose.ui.components.pagination.PaginationLink
            import com.toutiaoyan.compose.ui.components.pagination.PaginationNext
            import com.toutiaoyan.compose.ui.components.pagination.PaginationPrevious

            Pagination {
                PaginationContent {
                    PaginationItem { PaginationPrevious(onClick = {}) }
                    PaginationItem { PaginationLink(1, onClick = {}) }
                    PaginationItem { PaginationLink(2, isActive = true, onClick = {}) }
                    PaginationItem { PaginationLink(3, onClick = {}) }
                    PaginationItem { PaginationEllipsis() }
                    PaginationItem { PaginationNext(onClick = {}) }
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            Pagination
            └── PaginationContent
                ├── PaginationItem → PaginationPrevious
                ├── PaginationItem → PaginationLink
                ├── PaginationItem → PaginationEllipsis
                └── PaginationItem → PaginationNext
            """.trimIndent(),
        examplesHeading = null,
        apiSections =
            listOf(
                ComponentApiSection(
                    "Pagination",
                    (
                        "Presentational root; optional controlled page/windowing are " +
                            "Kotlin extensions."
                    ),
                    listOf(
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                        ApiProperty("ariaLabel", "String", "pagination"),
                        ApiProperty("page / onPageChange / pageCount", "Int? / callback? / Int?", "null"),
                        ApiProperty("siblingCount / showEdges", "Int / Boolean", "1 / false"),
                    ),
                ),
                ComponentApiSection(
                    "PaginationLink",
                    (
                        "Numeric label or caller-supplied content. Active links use " +
                            "Outline, other links Ghost."
                    ),
                    listOf(
                        ApiProperty("page", "Int", "Required"),
                        ApiProperty("isActive / onClick", "Boolean? / callback?", "null (controlled context)"),
                        ApiProperty("enabled / disabled", "Boolean", "true / false"),
                        ApiProperty("size / style", "ButtonSize / Style", "Icon / Style"),
                        ApiProperty("content", "Composable slot", "Optional overload for localized labels"),
                    ),
                ),
                ComponentApiSection(
                    "PaginationPrevious / PaginationNext",
                    (
                        "Nova labels show from640dp, arrows mirror in RTL. Text=null " +
                            "uses default Previous/Next; empty text requests icon-only " +
                            "content."
                    ),
                    listOf(
                        ApiProperty("onClick / enabled", "callback? / Boolean?", "null (controlled context)"),
                        ApiProperty("text", "String?", "null (Nova default label; legacy no label)"),
                        ApiProperty("style / ariaLabel", "Style / String?", "Default / English navigation label"),
                    ),
                ),
                ComponentApiSection(
                    "PaginationContent / PaginationItem / PaginationEllipsis",
                    (
                        "Content arranges items; Nova ellipsis is decorative and " +
                            "hidden from accessibility."
                    ),
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style (Content)", "Style", "Style"),
                    ),
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Navigation callbacks (Kotlin)",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "The official examples use placeholder links and a literal " +
                                    "active page2. Native examples keep that selection; provide " +
                                    "onClick callbacks or the controlled page context for real " +
                                    "navigation. Kotlin callbacks replace HTML href/render " +
                                    "integration; native Button semantics are not HTML anchors."
                            ),
                        ),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Controlled (Kotlin)",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationLink(page = 3)
                                PaginationEllipsis()
                                PaginationLink(page = 10)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationLink(page = 3)
                                PaginationEllipsis()
                                PaginationLink(page = 10)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Simple (Kotlin)",
                    code =
                        """
                        var page by remember { mutableStateOf(2) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 3) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationLink(page = 3)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(2) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 3) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationLink(page = 3)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icons only",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "With labels",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious(text = "Previous")
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationEllipsis()
                                PaginationLink(page = 10)
                                PaginationNext(text = "Next")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious(text = "Previous")
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationEllipsis()
                                PaginationLink(page = 10)
                                PaginationNext(text = "Next")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "First and last",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationFirst()
                                PaginationPrevious()
                                PaginationLink(page = 5)
                                PaginationNext()
                                PaginationLast()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationFirst()
                                PaginationPrevious()
                                PaginationLink(page = 5)
                                PaginationNext()
                                PaginationLast()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Sibling count",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            siblingCount = 2,
                        )
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            siblingCount = 2,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default sibling count",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            siblingCount = 1,
                        )
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            siblingCount = 1,
                        )
                    },
                ),
                ComponentExample(
                    title = "Show edges",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            showEdges = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            showEdges = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Hide edges",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            showEdges = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            showEdges = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default show edges",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            showEdges = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            showEdges = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious(disabled = true)
                                PaginationLink(page = 5)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious(disabled = true)
                                PaginationLink(page = 5)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious(disabled = false)
                                PaginationLink(page = 5)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious(disabled = false)
                                PaginationLink(page = 5)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            ariaLabel = "Search results",
                        ) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            ariaLabel = "Search results",
                        ) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default aria label",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            ariaLabel = "pagination",
                        ) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(
                            page = page,
                            onPageChange = { page = it },
                            pageCount = 10,
                            ariaLabel = "pagination",
                        ) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Ellipsis",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationEllipsis()
                                PaginationLink(page = 10)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationEllipsis()
                                PaginationLink(page = 10)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Previous",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationPrevious()
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Next",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Link",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "First",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationFirst()
                                PaginationLink(page = 5)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationFirst()
                                PaginationLink(page = 5)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Last",
                    code =
                        """
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 5)
                                PaginationLast()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(5) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 5)
                                PaginationLast()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationNext()
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationLink(page = 1)
                                PaginationLink(page = 2)
                                PaginationNext()
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationItem { PaginationLink(page = 1) }
                                PaginationItem { PaginationLink(page = 2) }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var page by remember { mutableStateOf(1) }
                        Pagination(page = page, onPageChange = { page = it }, pageCount = 10) {
                            PaginationContent {
                                PaginationItem { PaginationLink(page = 1) }
                                PaginationItem { PaginationLink(page = 2) }
                            }
                        }
                    },
                ),
            ),
    ).let { native ->
        val curated = listOf("Controlled (Kotlin)", "First and last", "Ellipsis")
        native.copy(
            examples = paginationOfficialExamples +
                native.examples.filter { it.title in curated }
                    .map { it.copy(title = "Kotlin extension: " + it.title.removeSuffix(" (Kotlin)")) },
        )
    }
