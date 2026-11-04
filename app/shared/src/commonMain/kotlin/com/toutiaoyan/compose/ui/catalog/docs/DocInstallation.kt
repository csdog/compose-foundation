package com.toutiaoyan.compose.ui.catalog.docs

internal const val CatalogLibraryVersion = "0.3.36"
internal const val CatalogLibraryModule = "com.toutiaoyan.compose:ui"
internal val catalogInstallationCode =
    """
    // build.gradle.kts — commonMain.dependencies
    implementation("$CatalogLibraryModule:$CatalogLibraryVersion")
    """.trimIndent()
