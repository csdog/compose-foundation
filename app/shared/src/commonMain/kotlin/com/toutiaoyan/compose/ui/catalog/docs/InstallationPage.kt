@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.catalog.rememberGuideScrollState
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant

val installationPage =
    GuidePage(
        id = "installation",
        title = "Installation",
        content = { InstallationContent() },
    )

@Composable
private fun InstallationContent() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberGuideScrollState())
                .padding(twSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
    ) {
        Text(variant = TextVariant.TitleLarge) { "Installation" }
        RegistryInstallationExamples()
        AndroidModuleSetup()
        GuideHeading("Compose Multiplatform Maven dependency")
        GuideParagraph("Add the Maven Central dependency to your commonMain source set:")
        CodeBlock(
            code =
                """
                // gradle/libs.versions.toml
                [versions]
                shadcn-compose = "$CatalogLibraryVersion"

                [libraries]
                shadcn-compose = { module = "$CatalogLibraryModule", version.ref = "shadcn-compose" }
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
        CodeBlock(
            code =
                """
                // build.gradle.kts
                kotlin {
                    sourceSets {
                        commonMain.dependencies {
                            implementation(libs.shadcn.compose)
                        }
                    }
                }
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
        GuideHeading("Opt in to the Styles API")
        GuideParagraph(
            "The library is built on the experimental Compose Styles API. Any file that references " +
                "a component's `style` parameter needs an opt-in:",
        )
        CodeBlock(
            code =
                """
                @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RegistryInstallationExamples() {
    GuideParagraph(
        "Use shadcn CLI 4.23.0 or later to copy editable source into your project. These examples install Button.",
    )
    sourceInstallations("button").forEach { installation ->
        GuideHeading(installation.title)
        SourceInstallationCode(installation)
    }
    GuideParagraph(
        "SwiftUI supports iOS 14 and macOS 11 or later. It uses Swift 6 and inherits your app's native color scheme.",
    )
}

@Composable
private fun AndroidModuleSetup() {
    GuideHeading("Android module setup")
    GuideParagraph(
        "With AGP 9.4 and Kotlin Compose compiler 2.4.21, enable Compose in your Android module " +
            "and set compile SDK 37. Add these dependencies for the installed source:",
    )
    CodeBlock(
        code =
            """
            dependencies {
                implementation("androidx.compose.runtime:runtime-android:1.12.1")
                implementation("androidx.compose.foundation:foundation-android:1.12.1")
                implementation("androidx.compose.ui:ui-android:1.12.1")
                implementation("androidx.compose.ui:ui-tooling-preview-android:1.12.1")
                implementation("com.composables:compose-interaction-capabilities:1.1.0")
            }
            """.trimIndent(),
        modifier = Modifier.fillMaxWidth(),
    )
    GuideParagraph(
        "For Avatar image loading, also add Coil Compose, Coil SVG, " +
            "Coil's Ktor 3 network integration, and the Ktor OkHttp engine.",
    )
}

@Composable
internal fun SourceInstallationCode(installation: SourceInstallation) {
    DocParagraph(installation.description)
    CodeBlock(
        code = installation.code,
        modifier = Modifier.fillMaxWidth(),
        language = null,
    )
}
