/*
ISC License

Copyright (c) for portions of Lucide are held by Cole Bemis 2013-2022 as part of Feather (MIT). All other copyright (c) for Lucide are held by Lucide Contributors 2022.

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

*/

// Glyph paths adapted from com.composables:icons-lucide-cmp 2.2.1; no icon-set runtime dependency.

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.theme.currentTheme

private val ToastCircleCheckIcon by lazy {
    ImageVector.Builder(
        name = "circle-check",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(22f, 12f)
            arcTo(10f, 10f, 0f, false, true, 12f, 22f)
            arcTo(10f, 10f, 0f, false, true, 2f, 12f)
            arcTo(10f, 10f, 0f, false, true, 22f, 12f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(9f, 12f)
            lineToRelative(2f, 2f)
            lineToRelative(4f, -4f)
        }
    }.build()
}

private val ToastInfoIcon by lazy {
    ImageVector.Builder(
        name = "info",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(22f, 12f)
            arcTo(10f, 10f, 0f, false, true, 12f, 22f)
            arcTo(10f, 10f, 0f, false, true, 2f, 12f)
            arcTo(10f, 10f, 0f, false, true, 22f, 12f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(12f, 16f)
            verticalLineToRelative(-4f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(12f, 8f)
            horizontalLineToRelative(0.01f)
        }
    }.build()
}

private val ToastWarningIcon by lazy {
    ImageVector.Builder(
        name = "triangle-alert",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(21.73f, 18f)
            lineToRelative(-8f, -14f)
            arcToRelative(2f, 2f, 0f, false, false, -3.48f, 0f)
            lineToRelative(-8f, 14f)
            arcTo(2f, 2f, 0f, false, false, 4f, 21f)
            horizontalLineToRelative(16f)
            arcToRelative(2f, 2f, 0f, false, false, 1.73f, -3f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(12f, 9f)
            verticalLineToRelative(4f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(12f, 17f)
            horizontalLineToRelative(0.01f)
        }
    }.build()
}

private val ToastErrorIcon by lazy {
    ImageVector.Builder(
        name = "octagon-x",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(15f, 9f)
            lineToRelative(-6f, 6f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(2.586f, 16.726f)
            arcTo(2f, 2f, 0f, false, true, 2f, 15.312f)
            verticalLineTo(8.688f)
            arcToRelative(2f, 2f, 0f, false, true, 0.586f, -1.414f)
            lineToRelative(4.688f, -4.688f)
            arcTo(2f, 2f, 0f, false, true, 8.688f, 2f)
            horizontalLineToRelative(6.624f)
            arcToRelative(2f, 2f, 0f, false, true, 1.414f, 0.586f)
            lineToRelative(4.688f, 4.688f)
            arcTo(2f, 2f, 0f, false, true, 22f, 8.688f)
            verticalLineToRelative(6.624f)
            arcToRelative(2f, 2f, 0f, false, true, -0.586f, 1.414f)
            lineToRelative(-4.688f, 4.688f)
            arcToRelative(2f, 2f, 0f, false, true, -1.414f, 0.586f)
            horizontalLineTo(8.688f)
            arcToRelative(2f, 2f, 0f, false, true, -1.414f, -0.586f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(9f, 9f)
            lineToRelative(6f, 6f)
        }
    }.build()
}

internal val ToastXIcon by lazy {
    ImageVector.Builder(
        name = "x",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(18f, 6f)
            lineTo(6f, 18f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(6f, 6f)
            lineToRelative(12f, 12f)
        }
    }.build()
}

@Composable
internal fun ToastStatusIcon(variant: ToastVariant) {
    when (variant) {
        ToastVariant.Default -> Unit
        ToastVariant.Loading -> Box(Modifier.clearAndSetSemantics {}) { Spinner(Modifier.size(16.dp)) }
        else -> {
            val vector =
                when (variant) {
                    ToastVariant.Success -> ToastCircleCheckIcon
                    ToastVariant.Info -> ToastInfoIcon
                    ToastVariant.Warning -> ToastWarningIcon
                    ToastVariant.Error -> ToastErrorIcon
                }
            Icon(
                vector,
                Modifier.size(16.dp),
                tint =
                    if (variant == ToastVariant.Error) {
                        currentTheme.colors.destructive
                    } else {
                        currentTheme.colors.popoverForeground
                    },
            )
        }
    }
}
