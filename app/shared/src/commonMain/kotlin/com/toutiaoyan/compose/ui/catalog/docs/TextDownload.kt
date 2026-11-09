package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.staticCompositionLocalOf

internal typealias TextDownloader = (value: String, filename: String) -> Unit

/** Catalog-only platform action. Null means this target has no file-download UI. */
internal expect fun platformTextDownloader(): TextDownloader?

internal val LocalTextDownloader = staticCompositionLocalOf { platformTextDownloader() }
