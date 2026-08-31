@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.table

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.tailwind.core.twSpacing
import androidx.compose.ui.text.TextStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

private val LocalTableFooter = staticCompositionLocalOf { false }

/**
 * A simple data table shell, matching real shadcn/ui's `table.tsx` structure --
 * header row separated by a bottom border, body rows separated by hairlines, cells
 * left-aligned with consistent padding. Rows are plain [Row]s (`cells` receives a
 * [RowScope]) so callers can freely use `Modifier.weight` per column.
 *
 * Row chrome is Style-driven — override with [style] on header/body rows.
 *
 * Usage:
 * ```
 * Table {
 *     TableHeaderRow { TableHeadCell("Name", Modifier.weight(1f)); TableHeadCell("Status") }
 *     TableRow { TableCell("Invoice #1", Modifier.weight(1f)); TableCell("Paid") }
 * }
 * ```
 */
@Composable
fun Table(
    modifier: Modifier = Modifier,
    ariaLabel: String? = null,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (ariaLabel != null) {
                        Modifier.semantics { contentDescription = ariaLabel }
                    } else {
                        Modifier
                    },
                )
                .styleable(styleState, style),
        content = content,
    )
}

@Composable
fun ColumnScope.TableHeaderRow(
    modifier: Modifier = Modifier,
    style: Style = Style,
    cells: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier.styleable(
                styleState,
                rememberTableRowStyle(selected = false) then style,
            ),
        content = cells,
    )
    Separator()
}

@Composable
fun ColumnScope.TableRow(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    isLast: Boolean = false,
    style: Style = Style,
    cells: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val footer = LocalTableFooter.current
    Row(
        modifier =
            modifier.styleable(
                styleState,
                rememberTableRowStyle(selected = selected, footer = footer) then style,
            ),
        content = cells,
    )
    if (!isLast) Separator()
}

/** Alias for [TableHeaderRow] matching shadcn's `TableHeader`. */
@Composable
fun ColumnScope.TableHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    cells: @Composable RowScope.() -> Unit,
) {
    TableHeaderRow(modifier = modifier, style = style, cells = cells)
}

/** Matches shadcn's `TableBody`. Children stay in the parent [Table] column. */
@Composable
fun ColumnScope.TableBody(
    content: @Composable ColumnScope.() -> Unit,
) {
    content()
}

/** Matches shadcn's `TableFooter` (`bg-muted/50`). Children stay in the parent [Table] column. */
@Composable
fun ColumnScope.TableFooter(
    content: @Composable ColumnScope.() -> Unit,
) {
    CompositionLocalProvider(LocalTableFooter provides true) {
        content()
    }
}

/** Alias for [TableHeadCell] matching shadcn's `TableHead`. */
@Composable
fun RowScope.TableHead(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    TableHeadCell(text = text, modifier = modifier, style = style)
}

@Composable
@JvmName("TableHeadLambda")
@OverloadResolutionByLambdaReturnType
fun RowScope.TableHead(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    TableHead(text = text(), modifier = modifier, style = style)
}

@Composable
fun RowScope.TableHeadCell(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.LabelLarge, modifier =
            modifier
                .padding(horizontal = twSpacing.sm)
                .padding(end = 4.dp)
                .styleable(styleState, style)
    ) { text }
}

@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.BodyMedium,
        textStyle = if (muted) TextColors.Muted else TextStyle.Default,
        style = style,
        modifier =
            modifier
                .padding(horizontal = twSpacing.sm)
                .padding(end = 4.dp)
                .styleable(styleState, style)
    ) { text }
}

@Composable
@JvmName("TableCellLambda")
@OverloadResolutionByLambdaReturnType
fun RowScope.TableCell(
    modifier: Modifier = Modifier,
    muted: Boolean = false,
    style: Style = Style,
    text: () -> String,
) {
    TableCell(text = text(), modifier = modifier, muted = muted, style = style)
}

/** A caption below a [Table], matching real shadcn/ui's `TableCaption`. */
@Composable
fun ColumnScope.TableCaption(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        variant = TextVariant.BodySmall, textStyle = TextColors.Muted, style = style, modifier =
            modifier
                .fillMaxWidth()
                .padding(top = twSpacing.md)
                .styleable(styleState, style)
    ) { text }
}

@Composable
@JvmName("TableCaptionLambda")
@OverloadResolutionByLambdaReturnType
fun ColumnScope.TableCaption(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    TableCaption(text = text(), modifier = modifier, style = style)
}
