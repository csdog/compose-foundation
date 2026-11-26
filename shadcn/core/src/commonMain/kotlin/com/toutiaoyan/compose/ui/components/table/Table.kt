@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.table

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import androidx.compose.ui.text.TextStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

private val LocalTableFooter = staticCompositionLocalOf { false }

/**
 * A simple data table shell, matching real shadcn/ui's `table.tsx` structure --
 * header row separated by a bottom border, body rows separated by hairlines, cells
 * left-aligned with consistent padding. Rows are plain [Row]s (`cells` receives a
 * [RowScope]) so callers can freely use `Modifier.weight` per column.
 * Use `colSpan` on heads or cells for HTML-style spanning across weighted columns.
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
    val viewportInteractions = remember { MutableInteractionSource() }
    val viewportStyleState = remember { MutableStyleState(viewportInteractions) }
    var viewportFocused by remember { mutableStateOf(false) }
    val keyStep = with(LocalDensity.current) { 40.dp.toPx() }
    val leadingDirection = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f
    BoxWithConstraints(
        modifier =
            modifier.fillMaxWidth().then(
                if (ariaLabel == null) Modifier else Modifier.semantics { contentDescription = ariaLabel },
            ).styleable(styleState, style),
    ) {
        val bounded = constraints.hasBoundedWidth
        val viewportModifier =
            if (bounded) {
                Modifier
                    .styleable(viewportStyleState, Style { focusRing(RectangleShape) })
                    .horizontalScroll(scroll)
                    .keyboardInputMode()
                    .onFocusChanged { viewportFocused = it.isFocused }
                    .onKeyEvent { event ->
                        if (!viewportFocused) return@onKeyEvent false
                        val delta =
                            event.tableScrollDelta(scroll, keyStep * leadingDirection) ?: return@onKeyEvent false
                        scroll.dispatchRawDelta(delta)
                        true
                    }
                    .focusable(enabled = scroll.maxValue > 0, interactionSource = viewportInteractions)
            } else {
                Modifier
            }
        Column(
            viewportModifier.width(IntrinsicSize.Max).widthIn(min = if (bounded) maxWidth else 0.dp),
        ) {
            TableStack(content)
        }
    }
}

private fun KeyEvent.tableScrollDelta(
    scroll: ScrollState,
    directionalStep: Float,
): Float? {
    val hasCommandModifier = isAltPressed || isCtrlPressed || isMetaPressed
    if (type != KeyEventType.KeyDown || hasCommandModifier) return null
    return when (key) {
        Key.DirectionLeft -> -directionalStep
        Key.DirectionRight -> directionalStep
        Key.MoveHome -> -scroll.value.toFloat()
        Key.MoveEnd -> (scroll.maxValue - scroll.value).toFloat()
        else -> null
    }
}

private enum class TablePart : ParentDataModifier {
    Caption,
    HeaderDivider,
    RowDivider,
    Footer,
    ;

    override fun Density.modifyParentData(parentData: Any?): Any = this@TablePart
}

/** Caption-bottom and last-row rules apply regardless of declaration order. */
@Composable
private fun ColumnScope.TableStack(content: @Composable ColumnScope.() -> Unit) {
    Layout(content = { content() }) { children, constraints ->
        val (captions, rows) = children.partition { it.parentData == TablePart.Caption }
        val lastRow =
            rows.indexOfLast { it.parentData != TablePart.RowDivider && it.parentData != TablePart.Footer }
        val visible =
            rows.filterIndexed { index, child ->
                child.parentData != TablePart.RowDivider || index < lastRow
            } + captions
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
        Modifier.then(TablePart.Footer).fillMaxWidth()
            .background(currentTheme.colors.muted.copy(alpha = TABLE_MUTED_BACKGROUND_ALPHA)),
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
    TableHead(text = text, modifier = modifier, style = style, colSpan = 1)
}

/** [colSpan] spans across equal-weight columns in the same row, overriding caller weight. */
@Composable
fun RowScope.TableHead(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    colSpan: Int,
) {
    TableHeadCell(text = text, modifier = modifier, style = style, colSpan = colSpan)
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
@JvmName("TableHeadLambdaColSpan")
@OverloadResolutionByLambdaReturnType
fun RowScope.TableHead(
    modifier: Modifier = Modifier,
    style: Style = Style,
    colSpan: Int,
    text: () -> String,
) {
    TableHead(text = text(), modifier = modifier, style = style, colSpan = colSpan)
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

/**
 * Maps a span to equal weighted columns. Prepend the weight because the first
 * parent-data modifier wins when a caller also supplies Modifier.weight.
 */
private fun RowScope.spanModifier(
    modifier: Modifier,
    colSpan: Int,
): Modifier {
    require(colSpan > 0) { "colSpan must be greater than 0" }
    return if (colSpan == 1) modifier else Modifier.weight(colSpan.toFloat()).then(modifier)
}

@Composable
fun RowScope.TableHeadCell(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
) {
    TableHeadCell(text, modifier, style, textStyle, colSpan = 1)
}

/** [colSpan] spans across equal-weight columns in the same row, overriding caller weight. */
@Composable
fun RowScope.TableHeadCell(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
    colSpan: Int,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        spanModifier(modifier, colSpan).styleable(
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
    TableCell(text, modifier, muted, style, textStyle, colSpan = 1)
}

/** [colSpan] spans across equal-weight columns in the same row, overriding caller weight. */
@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
    style: Style = Style,
    textStyle: TextStyle = TextStyle.Default,
    colSpan: Int,
) {
    TableCell(
        content = {
            Text(
                maxLines = 1,
                textStyle =
                    tableTypography(LocalTableFooter.current)
                        .merge(if (muted) TextColors.Muted else TextStyle.Default).merge(textStyle),
            ) { text }
        },
        modifier = modifier,
        style = style,
        contentAlignment = tableAlignment(textStyle),
        colSpan = colSpan,
    )
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
    TableCell(content, modifier, style, contentAlignment, colSpan = 1)
}

/** [colSpan] spans across equal-weight columns in the same row, overriding caller weight. */
@Composable
@JvmName("TableCellContentColSpan")
fun RowScope.TableCell(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    contentAlignment: Alignment = Alignment.CenterStart,
    colSpan: Int,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        spanModifier(modifier, colSpan).styleable(styleState, Style { contentPadding(8.dp) } then style),
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

@Composable
@JvmName("TableCellLambdaColSpan")
@OverloadResolutionByLambdaReturnType
fun RowScope.TableCell(
    modifier: Modifier = Modifier,
    muted: Boolean = false,
    style: Style = Style,
    colSpan: Int,
    text: () -> String,
) {
    TableCell(text = text(), modifier = modifier, muted = muted, style = style, colSpan = colSpan)
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
