package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.staticCompositionLocalOf

/** Optional browser bridge; native catalogs keep local page scrolling. */
data class DocLocation(
    val componentId: String?,
    val fragment: String,
    val revision: Int = 0,
    val animate: Boolean = false,
)

interface DocNavigation {
    val location: DocLocation

    fun navigate(fragment: String)
}

val LocalDocNavigation = staticCompositionLocalOf<DocNavigation?> { null }

internal data class DocHeadingLink(val fragment: String, val onClick: () -> Unit)

internal val LocalDocHeadingLinks = staticCompositionLocalOf<Map<String, DocHeadingLink>> { emptyMap() }

internal fun docFragment(title: String): String =
    title.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty { "section" }
