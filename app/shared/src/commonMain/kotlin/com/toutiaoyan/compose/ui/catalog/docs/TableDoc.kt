@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.table.Table
import com.toutiaoyan.compose.ui.components.table.TableBody
import com.toutiaoyan.compose.ui.components.table.TableCaption
import com.toutiaoyan.compose.ui.components.table.TableCell
import com.toutiaoyan.compose.ui.components.table.TableHead
import com.toutiaoyan.compose.ui.components.table.TableHeader
import com.toutiaoyan.compose.ui.components.table.TableRow

private val tableDataTableExample =
    ComponentExample(
        title = "Data Table",
        descriptionMarkdown =
            (
                "You can use the `Table` component to build more complex data tables. " +
                    "Combine it with a sorting/filtering/pagination library to create tables " +
                    "with those behaviors; Compose has no TanStack Table equivalent, so this " +
                    "library keeps `Table` as the presentational primitive."
            ),
        code = "",
        hideCode = true,
        minPreviewHeight = 160.dp,
        previewPadding = 24.dp,
        preview = {
            Table {
                TableCaption("Data Table guide")
                TableHeader {
                    TableHead("Approach", Modifier.weight(1f))
                    TableHead("Compose")
                }
                TableBody {
                    TableRow {
                        TableCell("Sorting / filtering / pagination", Modifier.weight(1f))
                        TableCell("Caller-owned")
                    }
                    TableRow(isLast = true) {
                        TableCell("Table primitive", Modifier.weight(1f))
                        TableCell("Built in")
                    }
                }
            }
        },
    )

private val tableApiSections =
    listOf(
        ComponentApiSection(
            "Table",
            (
                "Presentational table: the caller supplies the column layout " +
                    "(`Modifier.weight`) and row content. `ariaLabel` labels the table; " +
                    "`TableFooter` adds the translucent footer band. See the [Base UI Table " +
                    "API](https://ui.shadcn.com/docs/components/table#api-reference)."
            ),
            listOf(
                ApiProperty("ariaLabel", "String?", "null"),
                ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
            ),
        ),
        ComponentApiSection(
            "TableHeader / TableBody / TableRow / TableHead / TableCell",
            (
                "Header is 40dp tall with 8dp cell padding; body cells are 14sp/20sp with the " +
                    "official p-2. `TableRow(isLast)` drops the bottom border on the last row; " +
                    "hover/selected row states are Style overrides."
            ),
            listOf(
                ApiProperty("isLast", "Boolean", "false"),
                ApiProperty("modifier", "Modifier", "Default"),
            ),
        ),
        ComponentApiSection(
            "TableCaption / TableFooter",
            (
                "`TableCaption` is the muted caption below the table; `TableFooter` is the " +
                    "translucent band with a top border that holds totals or summary rows."
            ),
            listOf(
                ApiProperty("content", "@Composable RowScope.() -> Unit", "Required (footer row)"),
            ),
        ),
    )

val tableDoc =
    ComponentDoc(
        id = "table",
        title = "Table",
        description = "A responsive table component.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.table.*

            Table {
                TableHeader {
                    TableHead("Invoice", Modifier.weight(1f))
                    TableHead("Status")
                }
                TableBody {
                    TableRow(isLast = true) {
                        TableCell("INV001", Modifier.weight(1f))
                        TableCell("Paid")
                    }
                }
            }
            """.trimIndent(),
        examplesHeading = null,
        compositionCode =
            """
            Table
            ├── TableCaption
            ├── TableHeader
            │   └── TableRow
            │       ├── TableHead
            │       ├── TableHead
            │       ├── TableHead
            │       └── TableHead
            ├── TableBody
            │   ├── TableRow
            │   │   ├── TableCell
            │   │   ├── TableCell
            │   │   ├── TableCell
            │   │   └── TableCell
            │   └── TableRow
            └── TableFooter
            """.trimIndent(),
        apiSections = tableApiSections,
        examples =
            tableOfficialExamples.take(3) +
                listOf(tableDataTableExample) +
                tableOfficialExamples.drop(3) +
                listOf(
                    ComponentExample(
                        title = "Caption",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                                TableCaption("A list of your recent invoices.")
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                                TableCaption("A list of your recent invoices.")
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Accessible label",
                        code =
                            """
                            Table(ariaLabel = "Invoices") {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table(ariaLabel = "Invoices") {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default aria label",
                        code =
                            """
                            Table(ariaLabel = null) {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table(ariaLabel = null) {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Header",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Body",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Status")
                                }
                                TableBody {
                                    TableRow {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("Paid")
                                    }
                                    TableRow(isLast = true) {
                                        TableCell("INV002", Modifier.weight(1f))
                                        TableCell("Pending")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Status")
                                }
                                TableBody {
                                    TableRow {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("Paid")
                                    }
                                    TableRow(isLast = true) {
                                        TableCell("INV002", Modifier.weight(1f))
                                        TableCell("Pending")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Row",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Selected",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(selected = true, isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(selected = true, isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default selected",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(selected = false, isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(selected = false, isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Head",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Status")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("Paid")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Status")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("Paid")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Cell",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Muted",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00", muted = true)
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00", muted = true)
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default muted",
                        code =
                            """
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("${'$'}250.00", muted = false)
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            Table {
                                TableHeader {
                                    TableHead("Invoice", Modifier.weight(1f))
                                    TableHead("Amount")
                                }
                                TableBody {
                                    TableRow(isLast = true) {
                                        TableCell("INV001", Modifier.weight(1f))
                                        TableCell("$250.00", muted = false)
                                    }
                                }
                            }
                        },
                    ),
                ).map { it.copy(title = "Kotlin extension: " + it.title) },
    )
