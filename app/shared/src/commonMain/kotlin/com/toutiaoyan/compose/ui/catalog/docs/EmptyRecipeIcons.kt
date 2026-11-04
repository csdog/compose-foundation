package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.dp

// Tabler Icons v3.34.1 (MIT), see https://github.com/tabler/tabler-icons/blob/v3.34.1/LICENSE
internal val EmptyFolderCode =
    emptyTablerIcon(
        "folder-code",
        "M11 19h-6a2 2 0 0 1 -2 -2v-11a2 2 0 0 1 2 -2h4l3 3h7a2 2 0 0 1 2 2v4",
        "M20 21l2 -2l-2 -2",
        "M17 17l-2 2l2 2",
    )

internal val EmptyCloud =
    emptyTablerIcon(
        "cloud",
        (
            "M6.657 18c-2.572 0 -4.657 -2.007 -4.657 -4.483c0 -2.475 2.085 " +
                "-4.482 4.657 -4.482c.393 -1.762 1.794 -3.2 3.675 -3.773c1.88 " +
                "-.572 3.956 -.193 5.444 1c1.488 1.19 2.162 3.007 1.77 " +
                "4.769h.99c1.913 0 3.464 1.56 3.464 3.486c0 1.927 -1.551 3.487 " +
                "-3.465 3.487h-11.878"
        ),
    )

internal val EmptyBell =
    emptyTablerIcon(
        "bell",
        "M10 5a2 2 0 1 1 4 0a7 7 0 0 1 4 6v3a4 4 0 0 0 2 3h-16a4 4 0 0 0 2 -3v-3a7 7 0 0 1 4 -6",
        "M9 17v1a3 3 0 0 0 6 0v-1",
    )

private fun emptyTablerIcon(
    name: String,
    vararg paths: String,
): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        paths.forEach { data ->
            addPath(
                pathData = PathParser().parsePathString(data).toNodes(),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

internal val emptyMediaIconCode =
    """
    // Tabler Icons v3.34.1 (MIT), see https://github.com/tabler/tabler-icons/blob/v3.34.1/LICENSE
    private val EmptyFolderCode = emptyTablerIcon("folder-code",
        "M11 19h-6a2 2 0 0 1 -2 -2v-11a2 2 0 0 1 2 -2h4l3 3h7a2 2 0 0 1 2 2v4",
        "M20 21l2 -2l-2 -2",
        "M17 17l-2 2l2 2"
    )

    private val EmptyCloud = emptyTablerIcon("cloud",
        "M6.657 18c-2.572 0 -4.657 -2.007 -4.657 -4.483c0 -2.475 2.085 -4.482 4.657 -4.482c.393 -1.762 1.794 -3.2 3.675 -3.773c1.88 -.572 3.956 -.193 5.444 1c1.488 1.19 2.162 3.007 1.77 4.769h.99c1.913 0 3.464 1.56 3.464 3.486c0 1.927 -1.551 3.487 -3.465 3.487h-11.878"
    )

    private val EmptyBell = emptyTablerIcon("bell",
        "M10 5a2 2 0 1 1 4 0a7 7 0 0 1 4 6v3a4 4 0 0 0 2 3h-16a4 4 0 0 0 2 -3v-3a7 7 0 0 1 4 -6",
        "M9 17v1a3 3 0 0 0 6 0v-1"
    )

    private fun emptyTablerIcon(name: String, vararg paths: String): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            paths.forEach { data ->
                addPath(
                    pathData = PathParser().parsePathString(data).toNodes(),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build()
    """.trimIndent()
