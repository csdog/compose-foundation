package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.staticCompositionLocalOf

/** Filename-only catalog action. Null result means the user cancelled. */
internal typealias FilePicker = (onResult: (String?) -> Unit) -> Unit

/** Null means this catalog target has no file chooser integration. */
internal expect fun platformFilePicker(): FilePicker?

internal val LocalFilePicker = staticCompositionLocalOf { platformFilePicker() }
