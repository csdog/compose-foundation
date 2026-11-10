package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.catalog.docs.componentDocsById
import com.toutiaoyan.compose.ui.navigation.RouteKey

/** Browser tab label for the same committed route used by catalog navigation. */
val RouteKey.documentTitle: String
    get() {
        val title =
            when (this) {
                is RouteKey.ComponentDetail -> componentDocsById[componentId]?.title
                RouteKey.Introduction -> "Introduction"
                RouteKey.Create -> "Theme"
            }
        return title?.let { "$it — Compose Foundation" } ?: "Compose Foundation — Components"
    }
