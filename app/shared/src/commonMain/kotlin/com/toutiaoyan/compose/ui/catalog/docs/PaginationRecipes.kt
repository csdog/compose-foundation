@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldOrientation
import com.toutiaoyan.compose.ui.components.pagination.*
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.text.Text

// region shared
private val paginationTranslations =
    listOf(
        "Previous" to "Next",
        "السابق" to "التالي",
        "הקודם" to "הבא",
    )

private fun paginationNumber(
    page: Int,
    language: Int,
): String {
    if (language != 1) return page.toString()
    val digits = "٠١٢٣٤٥٦٧٨٩"
    return page.toString().map { digits[it.digitToInt()] }.joinToString("")
}

@Composable
private fun PaginationSourceRow(
    language: Int,
    modifier: Modifier = Modifier,
) {
    val (previous, next) = paginationTranslations[language]
    Pagination(modifier.testTag("pagination-recipe")) {
        PaginationContent {
            PaginationItem { PaginationPrevious(onClick = {}, text = previous) }
            (1..3).forEach { page ->
                PaginationItem {
                    PaginationLink(page, isActive = page == 2, onClick = {}) {
                        Text { paginationNumber(page, language) }
                    }
                }
            }
            PaginationItem { PaginationEllipsis() }
            PaginationItem { PaginationNext(onClick = {}, text = next) }
        }
    }
}
// endregion shared

// region default
@Composable
internal fun PaginationDefaultRecipe(modifier: Modifier = Modifier) {
    PaginationSourceRow(0, modifier)
}
// endregion default

// region simple
@Composable
internal fun PaginationSimpleRecipe(modifier: Modifier = Modifier) {
    Pagination(modifier.testTag("pagination-recipe")) {
        PaginationContent {
            (1..5).forEach { page ->
                PaginationItem { PaginationLink(page, isActive = page == 2, onClick = {}) }
            }
        }
    }
}
// endregion simple

// region icons
@Composable
internal fun PaginationIconsRecipe(modifier: Modifier = Modifier) {
    var rows by remember { mutableStateOf("25") }
    var rowsOpen by remember { mutableStateOf(false) }
    Row(
        modifier.testTag("pagination-icons-recipe"),
        horizontalArrangement =
            Arrangement.spacedBy(16.dp),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Field(modifier = Modifier.width(IntrinsicSize.Max), orientation = FieldOrientation.Horizontal) {
            FieldLabel("Rows per page", modifier = Modifier.clickable { rowsOpen = true })
            Select(value = rows, onValueChange = { rows = it }, open = rowsOpen, onOpenChange = { rowsOpen = it }) {
                SelectTrigger(
                    Modifier.width(80.dp).testTag("pagination-row-count"),
                    accessibilityLabel =
                        "Rows per page",
                )
                SelectContent {
                    listOf("10", "25", "50", "100").forEach { SelectItem(it) }
                }
            }
        }
        Pagination(Modifier.width(IntrinsicSize.Max)) {
            PaginationContent {
                PaginationItem { PaginationPrevious(onClick = {}) }
                PaginationItem { PaginationNext(onClick = {}) }
            }
        }
    }
}
// endregion icons

// region rtl
@Composable
internal fun PaginationRtlRecipe(
    language: Int = 1,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides
            if (language == 0) {
                LayoutDirection.Ltr
            } else {
                LayoutDirection.Rtl
            },
    ) {
        PaginationSourceRow(language, modifier)
    }
}
// endregion rtl

internal val paginationOfficialExamples =
    listOf(
        ComponentExample(
            title = "Default",
            code = paginationDefaultRecipeCode,
            preview = { PaginationDefaultRecipe() },
        ),
        ComponentExample(
            title = "Simple",
            descriptionMarkdown = "A simple pagination with only page numbers.",
            code = paginationSimpleRecipeCode,
            preview = { PaginationSimpleRecipe() },
        ),
        ComponentExample(
            title = "Icons Only",
            descriptionMarkdown =
                "Use just the previous and next buttons without page numbers. This is useful " +
                    "for data tables with a rows per page selector.",
            code = paginationIconsRecipeCode,
            preview = { PaginationIconsRecipe() },
        ),
        ComponentExample(
            title = "RTL",
            descriptionMarkdown = "To enable RTL support in shadcn/ui, see the RTL configuration guide.",
            code = paginationRtlRecipeCode,
            previewPadding = 0.dp,
            preview = {
            var language by remember { mutableIntStateOf(1) }
            RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                PaginationRtlRecipe(language)
            }
        }),
    )
