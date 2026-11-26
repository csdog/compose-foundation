package com.toutiaoyan.compose.ui.catalog.docs

internal const val CatalogLibraryVersion = "0.3.37"
internal const val CatalogLibraryModule = "com.toutiaoyan.compose:ui"
internal val catalogInstallationCode =
    """
    // build.gradle.kts — commonMain.dependencies
    implementation("$CatalogLibraryModule:$CatalogLibraryVersion")
    """.trimIndent()

private const val REGISTRY_BASE_URL = "https://compose-foundation.devops.cn.toutiaoyan.com/r"
private const val SWIFT_PACKAGE_PATH = "Packages/ComposeFoundationSwiftUI"

internal data class SourceInstallation(
    val title: String,
    val description: String,
    val code: String,
)

internal fun sourceInstallations(componentId: String): List<SourceInstallation> {
    val items = if (componentId == "date-picker") listOf("calendar", "popover") else listOf(componentId)
    return listOf(
        SourceInstallation(
            title = "Compose Multiplatform",
            description = "Run from your project root and point the CLI at the module containing `src/commonMain`.",
            code = registryAddCommand(REGISTRY_BASE_URL, items, "path/to/composeApp"),
        ),
        SourceInstallation(
            title = "Jetpack Compose",
            description =
                "Point the CLI at a standard Android module with Compose enabled. Sources go into `src/main/kotlin`.",
            code = registryAddCommand("$REGISTRY_BASE_URL/android", items, "path/to/app"),
        ),
        SourceInstallation(
            title = "SwiftUI",
            description =
                "Add the resulting local package to your Xcode project " +
                    "and select the `ComposeFoundationSwiftUI` product.",
            code =
                "mkdir -p $SWIFT_PACKAGE_PATH\n" +
                    registryAddCommand("$REGISTRY_BASE_URL/swiftui", items, SWIFT_PACKAGE_PATH),
        ),
    )
}

private fun registryAddCommand(
    baseUrl: String,
    items: List<String>,
    directory: String,
): String =
    (listOf("npx shadcn@latest add") + items.map { "  $baseUrl/$it.json" } + "  --cwd $directory")
        .joinToString(" \\\n")
