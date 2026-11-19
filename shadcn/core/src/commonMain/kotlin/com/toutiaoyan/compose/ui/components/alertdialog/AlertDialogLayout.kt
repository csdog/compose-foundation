package com.toutiaoyan.compose.ui.components.alertdialog

import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.dialog.compactDialogFooterMeasurePolicy

private val AlertDialogHeaderGap = 6.dp
private val AlertDialogMediaBottomMargin = 8.dp
private val AlertDialogMediaColumnGap = 16.dp
private val AlertDialogActionGap = 8.dp

internal enum class AlertDialogHeaderPart { Media }

internal fun alertDialogHeaderMeasurePolicy(centered: Boolean): MeasurePolicy =
    MeasurePolicy { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val mediaNode = measurables.firstOrNull { it.layoutId == AlertDialogHeaderPart.Media }
        val media = mediaNode?.measure(loose)
        val gap = AlertDialogHeaderGap.roundToPx()
        val mediaBottomMargin = AlertDialogMediaBottomMargin.roundToPx()
        val mediaColumn = if (media != null && !centered) media.width + AlertDialogMediaColumnGap.roundToPx() else 0
        val bodyWidth =
            if (constraints.hasBoundedWidth) {
                (constraints.maxWidth - mediaColumn).coerceAtLeast(
                    0,
                )
            } else {
                constraints.maxWidth
            }
        val body = measurables.filter { it !== mediaNode }.map { it.measure(loose.copy(maxWidth = bodyWidth)) }
        val width =
            constraints.constrainWidth(
                if (constraints.hasBoundedWidth) {
                    constraints.maxWidth
                } else {
                    maxOf((body.maxOfOrNull { it.width } ?: 0) + mediaColumn, media?.width ?: 0)
                },
            )
        val bodyHeight = body.sumOf { it.height } + gap * (body.size - 1).coerceAtLeast(0)
        val mediaHeight = if (media == null) 0 else media.height + mediaBottomMargin
        val bodyTop =
            if (centered && media != null) mediaHeight + (if (body.isNotEmpty()) gap else 0) else 0
        val height = maxOf(bodyTop + bodyHeight, mediaHeight)
        layout(width, constraints.constrainHeight(height)) {
            media?.placeRelative(if (centered) (width - media.width) / 2 else 0, 0)
            var y = bodyTop
            body.forEach { child ->
                child.placeRelative(if (centered) (width - child.width) / 2 else mediaColumn, y)
                y += child.height + gap
            }
        }
    }

internal fun alertDialogFooterMeasurePolicy(small: Boolean): MeasurePolicy =
    if (small) {
        compactAlertDialogGridMeasurePolicy()
    } else {
        compactDialogFooterMeasurePolicy(AlertDialogActionGap)
    }

private fun compactAlertDialogGridMeasurePolicy(): MeasurePolicy =
    MeasurePolicy { measurables, constraints ->
        val gap = AlertDialogActionGap.roundToPx()
        val width =
            if (constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                val widestAction = measurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) } ?: 0
                val naturalWidth = if (measurables.isNotEmpty()) widestAction.toLong() * 2 + gap else 0
                val preferredWidth = naturalWidth.coerceAtMost((Constraints.Infinity - 1).toLong()).toInt()
                Constraints.fitPrioritizingWidth(
                    minWidth = constraints.minWidth,
                    maxWidth = constraints.constrainWidth(preferredWidth),
                    minHeight = 0,
                    maxHeight = constraints.maxHeight,
                ).maxWidth
            }
        val cellWidth = ((width - gap) / 2).coerceAtLeast(0)
        val actionConstraints =
            Constraints.fitPrioritizingWidth(
                minWidth = cellWidth,
                maxWidth = cellWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight,
            )
        val actions = measurables.map { it.measure(actionConstraints) }
        val rows = actions.chunked(2)
        val rowHeights = rows.map { row -> row.maxOf { it.height } }
        val height = rowHeights.sum() + gap * (rows.size - 1).coerceAtLeast(0)
        layout(width, constraints.constrainHeight(height)) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { column, action -> action.placeRelative(column * (cellWidth + gap), y) }
                y += rowHeights[rowIndex] + gap
            }
        }
    }
