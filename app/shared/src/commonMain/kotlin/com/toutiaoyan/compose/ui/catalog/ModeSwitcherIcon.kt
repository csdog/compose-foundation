package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Half-circle theme glyph from
 * [mode-switcher.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/components/mode-switcher.tsx)
 * (circle + vertical diameter + filled right hemisphere).
 */
@Composable
fun ModeSwitcherIcon(modifier: Modifier = Modifier) {
    val color = currentTheme.colors.foreground
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 1.75.dp.toPx()
        val r = size.minDimension / 2f - strokeWidth
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(color = color, radius = r, center = c, style = Stroke(width = strokeWidth))
        drawLine(
            color = color,
            start = Offset(c.x, c.y - r),
            end = Offset(c.x, c.y + r),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(c.x - r, c.y - r),
            size = Size(r * 2f, r * 2f),
        )
    }
}
