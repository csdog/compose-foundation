@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.chart

import androidx.compose.foundation.shape.CircleShape
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
fun rememberChartLegendSwatchStyle(color: Color): Style = rememberChartIndicatorStyle(color, ChartIndicator.Dot)

@Composable
fun rememberChartIndicatorStyle(
    color: Color,
    indicator: ChartIndicator,
): Style =
    rememberTheme(color, indicator) {
        when (indicator) {
            ChartIndicator.Dot ->
                Style {
                    width(8.dp)
                    height(8.dp)
                    shape(CircleShape)
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
