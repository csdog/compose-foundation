package com.toutiaoyan.compose.ui.utils.clipboard

import androidx.compose.ui.platform.ClipEntry

/** Platform wrapper around [ClipEntry]; Compose has no common plaintext factory yet. */
internal expect fun plainTextClipEntry(text: String): ClipEntry
