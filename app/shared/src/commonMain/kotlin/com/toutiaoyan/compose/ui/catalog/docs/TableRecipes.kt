@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Ellipsis
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.dropdownmenu.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.table.*

// region shared
private data class TableInvoice(val status: Int, val method: Int, val amount: String)

private val tableInvoices =
    listOf(
        TableInvoice(0, 0, "$250.00"),
        TableInvoice(1, 1, "$150.00"),
        TableInvoice(2, 2, "$350.00"),
        TableInvoice(0, 0, "$450.00"),
        TableInvoice(0, 1, "$550.00"),
        TableInvoice(1, 2, "$200.00"),
        TableInvoice(2, 0, "$300.00"),
    )
private val tableTranslations =
    listOf(
        listOf(
            "A list of your recent invoices.", "Invoice", "Status", "Method", "Amount", "Paid", "Pending", "Unpaid",
            "Credit Card", "PayPal", "Bank Transfer", "Total",
        ),
        listOf(
            "قائمة بفواتيرك الأخيرة.", "الفاتورة", "الحالة", "الطريقة", "المبلغ", "مدفوع", "قيد الانتظار", "غير مدفوع",
            "بطاقة ائتمانية", "PayPal", "تحويل بنكي", "المجموع",
        ),
        listOf(
            "רשימת החשבוניות האחרונות שלך.", "חשבונית", "סטטוס", "שיטה", "סכום", "שולם", "ממתין", "לא שולם",
            "כרטיס אשראי", "PayPal", "העברה בנקאית", "סה\"כ",
        ),
    )

@Composable
private fun TableInvoicesRecipe(
    rows: Int = 7,
    language: Int = 0,
) {
    val labels = tableTranslations[language]
    Table(Modifier.testTag("table-example"), ariaLabel = labels[0]) {
        TableCaption(labels[0], Modifier.testTag("table-caption"))
        TableHeader {
            TableHeadCell(labels[1], Modifier.width(100.dp))
            TableHeadCell(labels[2], Modifier.weight(1f))
            TableHeadCell(labels[3], Modifier.weight(1f))
            TableHeadCell(labels[4], Modifier.width(100.dp), textStyle = TextStyle(textAlign = TextAlign.Right))
        }
        TableBody {
            tableInvoices.take(rows).forEachIndexed { index, invoice ->
                TableRow(Modifier.testTag("table-row-$index")) {
                    TableCell(
                        "INV00${index + 1}",
                        Modifier.width(100.dp),
                        textStyle = TextStyle(fontWeight = FontWeight.Medium),
                    )
                    TableCell(labels[5 + invoice.status], Modifier.weight(1f))
                    TableCell(labels[8 + invoice.method], Modifier.weight(1f))
                    TableCell(
                        invoice.amount,
                        Modifier.width(100.dp),
                        textStyle = TextStyle(textAlign = TextAlign.Right),
                    )
                }
            }
        }
        TableFooter {
            TableRow(Modifier.testTag("table-total")) {
                TableCell(labels[11], Modifier.weight(1f))
                TableCell("$2,500.00", Modifier.width(100.dp), textStyle = TextStyle(textAlign = TextAlign.Right))
            }
        }
    }
}
// endregion shared

// region default
@Composable
internal fun TableDefaultRecipe() {
    TableInvoicesRecipe()
}
// endregion default

// region footer
@Composable
internal fun TableFooterRecipe() {
    TableInvoicesRecipe(rows = 3)
}
// endregion footer

// region actions
@Composable
internal fun TableActionsRecipe() {
    val products = listOf("Wireless Mouse" to "$29.99", "Mechanical Keyboard" to "$129.99", "USB-C Hub" to "$49.99")
    Table(Modifier.testTag("table-example")) {
        TableHeader {
            TableHeadCell("Product", Modifier.weight(1f))
            TableHeadCell("Price", Modifier.width(100.dp))
            TableHeadCell("Actions", Modifier.width(80.dp), textStyle = TextStyle(textAlign = TextAlign.Right))
        }
        TableBody {
            products.forEachIndexed { index, (product, price) ->
                TableRow(Modifier.testTag("table-row-$index")) {
                    TableCell(product, Modifier.weight(1f), textStyle = TextStyle(fontWeight = FontWeight.Medium))
                    TableCell(price, Modifier.width(100.dp))
                    TableCell(modifier = Modifier.width(80.dp), contentAlignment = Alignment.CenterEnd, content = {
                        DropdownMenu {
                            DropdownMenuTrigger {
                                Button(
                                    onClick = toggle,
                                    variant = ButtonVariant.Ghost,
                                    size = ButtonSize.Icon,
                                    modifier = Modifier.size(32.dp).testTag("table-menu-$index"),
                                    label = "Open menu",
                                ) { Icon(Lucide.Ellipsis, modifier = Modifier.size(16.dp)) }
                            }
                            DropdownMenuContent(align = PopupAlign.End) {
                                DropdownMenuItem("Edit", onClick = {})
                                DropdownMenuItem("Duplicate", onClick = {})
                                DropdownMenuSeparator()
                                DropdownMenuItem("Delete", onClick = {}, variant = DropdownMenuItemVariant.Destructive)
                            }
                        }
                    })
                }
            }
        }
    }
}
// endregion actions

// region rtl
@Composable
internal fun TableRtlRecipe(language: Int = 1) {
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
    ) {
        TableInvoicesRecipe(language = language)
    }
}
// endregion rtl

internal val tableOfficialExamples =
    listOf(
        ComponentExample("Default", code = tableDefaultRecipeCode, preview = { TableDefaultRecipe() }),
        ComponentExample(
            "Footer",
            code = tableFooterRecipeCode,
            descriptionMarkdown = "Use the `TableFooter` component to add a footer to the table.",
            preview = { TableFooterRecipe() },
        ),
        ComponentExample(
            "Actions",
            code = tableActionsRecipeCode,
            descriptionMarkdown = "A table showing actions for each row using a `DropdownMenu` component.",
            preview = { TableActionsRecipe() },
        ),
        ComponentExample(
            "RTL",
            code = tableRtlRecipeCode,
            descriptionMarkdown =
                (
                    "To enable RTL support in shadcn/ui, see the " +
                        "[RTL configuration guide](https://ui.shadcn.com/docs/rtl)."
                ),
            previewPadding = 0.dp,
            preview = {
                var language by remember { mutableIntStateOf(1) }
                RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                    TableRtlRecipe(language)
                }
            },
        ),
    )
