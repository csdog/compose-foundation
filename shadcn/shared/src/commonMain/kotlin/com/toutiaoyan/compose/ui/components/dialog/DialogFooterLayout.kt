package com.toutiaoyan.compose.ui.components.dialog

import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth

internal fun compactDialogFooterMeasurePolicy(actionSpacing: Dp): MeasurePolicy =
    MeasurePolicy { measurables, constraints ->
        val targetWidth =
            if (constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                val widestAction = measurables.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) } ?: 0
                constraints.constrainWidth(widestAction.coerceAtMost(Constraints.Infinity - 1))
            }
        val actionConstraints =
            Constraints.fitPrioritizingWidth(
                minWidth = targetWidth,
                maxWidth = targetWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight,
            )
        val children = measurables.map { it.measure(actionConstraints) }
        val gap = actionSpacing.roundToPx()
        val height = children.sumOf { it.height } + gap * (children.size - 1).coerceAtLeast(0)
        layout(actionConstraints.minWidth, constraints.constrainHeight(height)) {
            var y = 0
            children.asReversed().forEach { child ->
                child.placeRelative(0, y)
                y += child.height + gap
            }
        }
    }
