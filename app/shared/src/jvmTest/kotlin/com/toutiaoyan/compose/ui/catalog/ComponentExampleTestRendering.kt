package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.catalog.docs.ComponentExample

// Keep the external composable property access in one place. The Compose compiler
// otherwise remaps the same backing field for every test call site.
@Composable
internal fun ComponentExample.RenderPreview() {
    preview()
}
