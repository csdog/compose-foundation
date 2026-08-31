@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.table.Table
import com.toutiaoyan.compose.ui.components.table.TableBody
import com.toutiaoyan.compose.ui.components.table.TableCaption
import com.toutiaoyan.compose.ui.components.table.TableCell
import com.toutiaoyan.compose.ui.components.table.TableFooter
import com.toutiaoyan.compose.ui.components.table.TableHead
import com.toutiaoyan.compose.ui.components.table.TableHeader
import com.toutiaoyan.compose.ui.components.table.TableRow

private data class InvoiceRow(val id: String, val status: String, val method: String, val amount: String)

private val sampleInvoices =
    listOf(
        InvoiceRow("INV001", "Paid", "Credit Card", "$250.00"),
        InvoiceRow("INV002", "Pending", "PayPal", "$150.00"),
        InvoiceRow("INV003", "Unpaid", "Bank Transfer", "$350.00"),
    )

val tableDoc =
    ComponentDoc(
        id = "table",
        title = "Table",
        description = "A responsive data table with a bordered header and hairline row separators.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.table.Table

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
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Table {
                            TableCaption("A list of your recent invoices.")
                            TableHeader {
                                TableHead("Invoice", Modifier.weight(1f))
                                TableHead("Status", Modifier.weight(1f))
                                TableHead("Method", Modifier.weight(1f))
                                TableHead("Amount")
                            }
                            TableBody {
                                sampleInvoices.forEachIndexed { index, invoice ->
                                    TableRow(isLast = index == sampleInvoices.lastIndex) {
                                        TableCell(invoice.id, Modifier.weight(1f))
                                        TableCell(invoice.status, Modifier.weight(1f), muted = true)
                                        TableCell(invoice.method, Modifier.weight(1f), muted = true)
                                        TableCell(invoice.amount)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Table {
                            TableCaption("A list of your recent invoices.")
                            TableHeader {
                                TableHead("Invoice", Modifier.weight(1f))
                                TableHead("Status", Modifier.weight(1f))
                                TableHead("Method", Modifier.weight(1f))
                                TableHead("Amount")
                            }
                            TableBody {
                                sampleInvoices.forEachIndexed { index, invoice ->
                                    TableRow(isLast = index == sampleInvoices.lastIndex) {
                                        TableCell(invoice.id, Modifier.weight(1f))
                                        TableCell(invoice.status, Modifier.weight(1f), muted = true)
                                        TableCell(invoice.method, Modifier.weight(1f), muted = true)
                                        TableCell(invoice.amount)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Table {
                            TableHeader {
                                TableHead("Invoice", Modifier.weight(1f))
                                TableHead("Amount")
                            }
                            TableBody {
                                TableRow {
                                    TableCell("INV001", Modifier.weight(1f))
                                    TableCell("${'$'}250.00")
                                }
                                TableRow(isLast = true) {
                                    TableCell("INV002", Modifier.weight(1f))
                                    TableCell("${'$'}150.00")
                                }
                            }
                            TableFooter {
                                TableRow(isLast = true) {
                                    TableCell("Total", Modifier.weight(1f))
                                    TableCell("${'$'}400.00")
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
                                TableRow {
                                    TableCell("INV001", Modifier.weight(1f))
                                    TableCell("$250.00")
                                }
                                TableRow(isLast = true) {
                                    TableCell("INV002", Modifier.weight(1f))
                                    TableCell("$150.00")
                                }
                            }
                            TableFooter {
                                TableRow(isLast = true) {
                                    TableCell("Total", Modifier.weight(1f))
                                    TableCell("$400.00")
                                }
                            }
                        }
                    },
                ),
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
