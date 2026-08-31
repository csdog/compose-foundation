@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twSpacing

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
                .verticalScroll(rememberScrollState())
                .padding(twSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
    ) {
        Text(variant = TextVariant.TitleLarge) { "Installation" }
        GuideParagraph("Add the Maven Central dependency to your commonMain source set:")
        CodeBlock(
            code =
                """
                // gradle/libs.versions.toml
                [versions]
                shadcn-compose = "0.1.0-SNAPSHOT"

                [libraries]
                shadcn-compose = { module = "com.toutiaoyan:shadcn-compose", version.ref = "shadcn-compose" }
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
                @file:OptIn(ExperimentalFoundationStyleApi::class)
                """.trimIndent(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
