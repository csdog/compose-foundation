@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.contentPadding
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.toutiaoyan.compose.tailwind.core.TwShadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.utils.rememberTheme

/** Swatch shape for [ChartTooltipContent], matching shadcn `indicator`. */
enum class ChartIndicator { Dot, Line, Dashed }

@Composable
fun rememberChartLegendSwatchStyle(color: Color): Style =
    rememberTheme(color) {
        Style {
            width(8.dp)
            height(8.dp)
            shape(RoundedCornerShape(2.dp))
            background(color)
        }
    }

@Composable
fun rememberChartIndicatorStyle(
    color: Color,
    indicator: ChartIndicator,
): Style =
    rememberTheme(color, indicator) {
        when (indicator) {
            ChartIndicator.Dot ->
                Style {
                    width(10.dp)
                    height(10.dp)
                    shape(RoundedCornerShape(2.dp))
                    background(color)
                }
            ChartIndicator.Line ->
                Style {
                    width(4.dp)
                    height(8.dp)
                    shape(RoundedCornerShape(2.dp))
                    background(color)
                }
            ChartIndicator.Dashed ->
                Style {
                    width(8.dp)
                    height(8.dp)
                    shape(RoundedCornerShape(2.dp))
                    border(color)
                    background(Color.Transparent)
                }
        }
    }

@Composable
internal fun rememberChartTooltipStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(8.dp))
            background(colors.background)
            contentColor(colors.foreground)
            border(colors.border.copy(alpha = colors.border.alpha * .5f))
            contentPadding(horizontal = 10.dp, vertical = 6.dp)
            dropShadow(Shadow(radius = TwShadow.xl, color = Color.Black.copy(alpha = .15f)))
        }
    }

@Composable
internal fun ChartTooltipIndicator(
    color: Color,
    indicator: ChartIndicator,
) {
    when (indicator) {
        ChartIndicator.Dot ->
            Box(
                Modifier.styleable(
                    remember { MutableStyleState(MutableInteractionSource()) },
                    rememberChartIndicatorStyle(color, indicator),
                ),
            )
        ChartIndicator.Line ->
            Box(
                Modifier.width(4.dp).fillMaxHeight().styleable(
                    remember { MutableStyleState(MutableInteractionSource()) },
                    Style {
                        shape(RoundedCornerShape(2.dp))
                        background(color)
                    },
                ),
            )
        ChartIndicator.Dashed ->
            Canvas(Modifier.width(3.dp).fillMaxHeight()) {
                val dash = 3.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color,
                        Offset(
                            size.width / 2,
                            y,
                        ),
                        Offset(
                            size.width / 2,
                            (
                                y +
                                    dash
                            ).coerceAtMost(size.height),
                        ),
                        3.dp.toPx(),
                    )
                    y += dash * 2
                }
            }
    }
}
