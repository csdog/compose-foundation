package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Every bordered component in this library uses a constant 1.dp width -- only the color
 * varies per variant/state (real shadcn/ui's own `border` utility is likewise a fixed
 * 1px everywhere; only `border-<color>` changes per component). This is the one place
 * that width lives instead of being repeated at all 18+ call sites across this package.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
fun StyleScope.border(color: Color) {
    borderWidth(1.dp)
    borderColor(color)
}

/**
 * CSS `border-dashed` approximation (Compose Style has no dash stroke).
 * Dash/gap = 4.dp matches typical Tailwind 1px dashed rhythm at our density.
 */
fun Modifier.dashedBorder(
    color: Color,
    corner: Dp,
    width: Dp = 1.dp,
    dash: Dp = 4.dp,
    gap: Dp = 4.dp,
): Modifier =
    drawWithContent {
        drawContent()
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(corner.toPx()),
            style =
                Stroke(
                    width = width.toPx(),
                    pathEffect =
                        PathEffect.dashPathEffect(
                            floatArrayOf(dash.toPx(), gap.toPx()),
                            phase = 0f,
                        ),
                ),
        )
    }
