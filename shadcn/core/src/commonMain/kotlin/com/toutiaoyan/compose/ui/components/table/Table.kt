@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.table

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.style.contentPadding
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
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
    val scroll = rememberScrollState()
    BoxWithConstraints(
        modifier =
            modifier.fillMaxWidth().then(
                if (ariaLabel == null) Modifier else Modifier.semantics { contentDescription = ariaLabel },
            ).styleable(styleState, style),
    ) {
        val bounded = constraints.hasBoundedWidth
        Column(
            Modifier.then(if (bounded) Modifier.horizontalScroll(scroll) else Modifier)
                .width(IntrinsicSize.Max).widthIn(min = if (bounded) maxWidth else 0.dp),
        ) {
            TableStack(content)
        }
    }
}

private enum class TablePart : ParentDataModifier {
    Caption,
    HeaderDivider,
    RowDivider,
    ;

    override fun Density.modifyParentData(parentData: Any?): Any = this@TablePart
}

/** Caption-bottom and last-row rules apply regardless of declaration order. */
@Composable
private fun ColumnScope.TableStack(content: @Composable ColumnScope.() -> Unit) {
    Layout(content = { content() }) { children, constraints ->
        val ordered =
            children.filter { it.parentData != TablePart.Caption } +
                children.filter { it.parentData == TablePart.Caption }
        val lastContent =
            ordered.indexOfLast {
                it.parentData != TablePart.Caption && it.parentData != TablePart.RowDivider
            }
        val visible =
            ordered.filterIndexed {
                    index,
                    child,
                ->
                child.parentData != TablePart.RowDivider || index < lastContent
            }
        val placeables =
            visible.map {
                it.measure(constraints.copy(minWidth = 0, minHeight = 0, maxHeight = Constraints.Infinity))
            }
        val width = constraints.constrainWidth(placeables.maxOfOrNull { it.width } ?: 0)
        val height = constraints.constrainHeight(placeables.sumOf { it.height })
        layout(width, height) {
            var y = 0
            placeables.forEach {
                it.placeRelative(0, y)
                y += it.height
            }
        }
    }
}

@Composable
fun ColumnScope.TableHeaderRow(
    modifier: Modifier = Modifier,
    style: Style = Style,
    cells: @Composable RowScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interaction) }
    Row(
        modifier =
            modifier.hoverable(interaction).styleable(
                styleState,
                rememberTableRowStyle(selected = false) then style,
            ),
        verticalAlignment = Alignment.CenterVertically,
        content = cells,
    )
    Separator(Modifier.then(TablePart.HeaderDivider))
}

@Composable
fun ColumnScope.TableRow(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    isLast: Boolean = false,
    style: Style = Style,
    cells: @Composable RowScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interaction) }
    Row(
        modifier =
            modifier.semantics { this.selected = selected }.hoverable(interaction).styleable(
                styleState,
                rememberTableRowStyle(selected = selected) then style,
            ),
        verticalAlignment = Alignment.CenterVertically,
        content = cells,
    )
    if (!isLast) Separator(Modifier.then(TablePart.RowDivider))
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

/** Matches shadcn's `TableBody`, including automatic removal of the last row divider. */
@Composable
fun ColumnScope.TableBody(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth()) { TableStack(content) }
}

/** Matches shadcn's `TableFooter`: top border, muted/50 background and medium text. */
@Composable
fun ColumnScope.TableFooter(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(com.toutiaoyan.compose.ui.theme.currentTheme.colors.muted.copy(alpha = .5f)),
    ) {
        Separator()
        CompositionLocalProvider(LocalTableFooter provides true) { TableStack(content) }
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

private fun tableTypography(medium: Boolean = false): TextStyle =
    TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = if (medium) FontWeight.Medium else FontWeight.Normal,
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )

private fun tableAlignment(style: TextStyle): Alignment =
    when (style.textAlign) {
        TextAlign.End -> Alignment.CenterEnd
        TextAlign.Right -> AbsoluteAlignment.CenterRight
        TextAlign.Left -> AbsoluteAlignment.CenterLeft
        TextAlign.Center -> Alignment.Center
        else -> Alignment.CenterStart
    }

@Composable
fun RowScope.TableHeadCell(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier.styleable(
            styleState,
            Style {
                height(40.dp)
                contentPadding(horizontal = 8.dp, vertical = 0.dp)
            } then style,
        ),
        contentAlignment = tableAlignment(textStyle),
    ) {
        Text(maxLines = 1, textStyle = tableTypography(medium = true).merge(textStyle)) { text }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier.styleable(styleState, Style { contentPadding(8.dp) } then style),
        contentAlignment = tableAlignment(textStyle),
    ) {
        Text(
            maxLines = 1,
            textStyle =
                tableTypography(LocalTableFooter.current)
                    .merge(if (muted) TextColors.Muted else TextStyle.Default).merge(textStyle),
        ) { text }
    }
}

/** Supply `content = { ... }` for arbitrary cells; the trailing String-lambda overload stays unambiguous. */
@Composable
@JvmName("TableCellContent")
fun RowScope.TableCell(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    contentAlignment: Alignment = Alignment.CenterStart,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier.styleable(styleState, Style { contentPadding(8.dp) } then style),
        contentAlignment = contentAlignment,
    ) {
        content()
    }
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
    Box(
        modifier.then(TablePart.Caption).fillMaxWidth().padding(top = 16.dp).styleable(styleState, style),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            textStyle = tableTypography().merge(TextColors.Muted).merge(TextStyle(textAlign = TextAlign.Center)),
        ) { text }
    }
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
