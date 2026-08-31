// Hand-derived from ChevronRight (90° CCW) — keep in sync with that glyph.
package com.toutiaoyan.compose.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Matches Base AccordionTrigger collapsed icon (`ChevronDownIcon`). */
val ChevronDown: ImageVector by lazy {
    ImageVector.Builder(
        name = "ChevronDown",
        defaultWidth = 24f.dp,
        defaultHeight = 24f.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 8f)
            lineTo(12f, 18f)
            lineTo(4f, 8f)
            lineTo(12f, 11f)
            close()
        }
    }.build()
}
