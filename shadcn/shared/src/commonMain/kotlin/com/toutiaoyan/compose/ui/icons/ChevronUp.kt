// Hand-derived from ChevronRight (90° CW) — keep in sync with that glyph.
package com.toutiaoyan.compose.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Matches Base AccordionTrigger expanded icon (`ChevronUpIcon`). */
val ChevronUp: ImageVector by lazy {
    ImageVector.Builder(
        name = "ChevronUp",
        defaultWidth = 24f.dp,
        defaultHeight = 24f.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 16f)
            lineTo(12f, 6f)
            lineTo(20f, 16f)
            lineTo(12f, 13f)
            close()
        }
    }.build()
}
