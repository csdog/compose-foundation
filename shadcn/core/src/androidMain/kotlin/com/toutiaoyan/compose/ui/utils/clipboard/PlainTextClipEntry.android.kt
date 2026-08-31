package com.toutiaoyan.compose.ui.utils.clipboard

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.toClipEntry

internal actual fun plainTextClipEntry(text: String): ClipEntry =
    ClipData.newPlainText("text", text).toClipEntry()
