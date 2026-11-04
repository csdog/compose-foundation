@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.table.Table
import com.toutiaoyan.compose.ui.components.table.TableBody
import com.toutiaoyan.compose.ui.components.table.TableCaption
import com.toutiaoyan.compose.ui.components.table.TableCell
import com.toutiaoyan.compose.ui.components.table.TableHead
import com.toutiaoyan.compose.ui.components.table.TableHeader
import com.toutiaoyan.compose.ui.components.table.TableRow

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
        examples =
            tableOfficialExamples +
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
                ),
    )
