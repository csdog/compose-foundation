package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

// Current Lucide FileSearch outline from the live shadcn example. The installed
// icons-lucide-cmp2.2.1 contains the earlier, right-hand magnifier design.
// ISC/MIT notices: composeResources/files/licenses/lucide-icons.txt.
private val fileSearchPaths =
    listOf(
        "M14 2v4a2 2 0 0 0 2 2h4",
        "M4.268 21a2 2 0 0 0 1.727 1H18a2 2 0 0 0 2-2V7l-5-5H6a2 2 0 0 0-2 2v3",
        "m9 18-1.5-1.5",
        "M8 14a3 3 0 1 1-6 0a3 3 0 1 1 6 0Z",
    )

internal val AttachmentFileSearchIcon =
    ImageVector.Builder("FileSearch", 24.dp, 24.dp, 24f, 24f).apply {
        fileSearchPaths.forEach { path ->
            addPath(
                PathParser().parsePathString(path).toNodes(),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

private val fileSearchPathsCode = fileSearchPaths.joinToString("\n") { "                \"$it\"," }

/** The displayed recipe includes the real vector construction, rather than an old import. */
internal val attachmentPreviewIconCode =
    """
        |    val previewIcon = remember {
        |        ImageVector.Builder("FileSearch", 24.dp, 24.dp, 24f, 24f).apply {
        |            listOf(
        |$fileSearchPathsCode
        |            ).forEach { path ->
        |                addPath(PathParser().parsePathString(path).toNodes(), fill = null,
        |                    stroke = SolidColor(Color.Black), strokeLineWidth = 2f,
        |                    strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round)
        |            }
        |        }.build()
        |    }
    """.trimMargin().trimIndent()
