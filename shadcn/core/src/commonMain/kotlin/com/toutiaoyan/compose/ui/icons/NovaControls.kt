package com.toutiaoyan.compose.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// The small control glyphs used by base-nova. Kept internal to avoid changing
// existing icon APIs or adding an external icon-set dependency to core.
private fun controlGlyph(
    name: String,
    nodes: PathBuilder.() -> Unit,
): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = nodes,
        )
    }.build()

internal val NovaChevronRight by lazy {
    controlGlyph("NovaChevronRight") {
        moveTo(9f, 18f)
        lineTo(15f, 12f)
        lineTo(9f, 6f)
    }
}
internal val NovaCheck by lazy {
    controlGlyph("NovaCheck") {
        moveTo(20f, 6f)
        lineTo(9f, 17f)
        lineTo(4f, 12f)
    }
}
internal val NovaX by lazy {
    controlGlyph("NovaX") {
        moveTo(18f, 6f)
        lineTo(6f, 18f)
        moveTo(6f, 6f)
        lineTo(18f, 18f)
    }
}
internal val NovaMinus by lazy {
    controlGlyph("NovaMinus") {
        moveTo(5f, 12f)
        lineTo(19f, 12f)
    }
}

internal val NovaSearch by lazy {
    controlGlyph("NovaSearch") {
        moveTo(21f, 21f)
        lineTo(16.65f, 16.65f)
        moveTo(19f, 11f)
        arcTo(8f, 8f, 0f, true, true, 3f, 11f)
        arcTo(8f, 8f, 0f, true, true, 19f, 11f)
    }
}

internal val NovaCommandKey by lazy {
    controlGlyph("NovaCommandKey") {
        moveTo(9f, 7f)
        curveTo(9f, 2f, 3f, 2f, 3f, 6f)
        curveTo(3f, 9f, 6f, 9f, 9f, 9f)
        lineTo(18f, 9f)
        curveTo(22f, 9f, 22f, 3f, 18f, 3f)
        curveTo(15f, 3f, 15f, 6f, 15f, 9f)
        lineTo(15f, 18f)
        curveTo(15f, 22f, 21f, 22f, 21f, 18f)
        curveTo(21f, 15f, 18f, 15f, 15f, 15f)
        lineTo(6f, 15f)
        curveTo(2f, 15f, 2f, 21f, 6f, 21f)
        curveTo(9f, 21f, 9f, 18f, 9f, 15f)
        close()
    }
}

// Lucide ellipsis: three stroked r=1 circles (the legacy filled glyph was smaller).
internal val NovaEllipsis by lazy {
    controlGlyph("NovaEllipsis") {
        for (x in listOf(5f, 12f, 19f)) {
            moveTo(x + 1f, 12f)
            arcTo(1f, 1f, 0f, true, true, x - 1f, 12f)
            arcTo(1f, 1f, 0f, true, true, x + 1f, 12f)
            close()
        }
    }
}
