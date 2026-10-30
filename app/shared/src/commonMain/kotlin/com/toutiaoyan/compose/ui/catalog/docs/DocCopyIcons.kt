package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

// Tabler outline paths used by ui.shadcn.com's copy-button.
// MIT notice is bundled in composeResources/files/licenses/tabler-icons.txt.
internal val CopyCodeIcon =
    codeOutline(
        "CodeCopy",
        (
            "M7 7m0 2.667a2.667 2.667 0 0 1 2.667 -2.667h8.666a2.667 2.667 " +
                "0 0 1 2.667 2.667v8.666a2.667 2.667 0 0 1 -2.667 " +
                "2.667h-8.666a2.667 2.667 0 0 1 -2.667 -2.667z"
        ),
        "M4.012 16.737a2.005 2.005 0 0 1 -1.012 -1.737v-10c0 -1.1 .9 -2 2 -2h10c.75 0 1.158 .385 1.5 1",
    )
internal val CopiedCodeIcon = codeOutline("CodeCopied", "M5 12l5 5l10 -10")

private fun codeOutline(
    name: String,
    vararg paths: String,
): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        paths.forEach { path ->
            addPath(
                pathData = PathParser().parsePathString(path).toNodes(),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()
