import java.util.Base64
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.app.shared)
            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.kotlinx.browser)
        }
    }
}

val generatedCatalogFonts = layout.buildDirectory.dir("generated/catalogFonts/kotlin")
val shortcutFont = layout.projectDirectory.file("fonts/catalog-shortcut-symbols.ttf")
val generateCatalogShortcutFont = tasks.register("generateCatalogShortcutFont")

generateCatalogShortcutFont.configure {
    val sourceFile = shortcutFont.asFile
    val outputDirectory = generatedCatalogFonts.get().asFile
    inputs.file(sourceFile)
    outputs.dir(outputDirectory)
    doLast {
        val encoded =
            Base64.getEncoder().encodeToString(sourceFile.readBytes())
                .chunked(100)
                .joinToString(" +\n                        ") { "\"$it\"" }
        val output = outputDirectory.resolve("com/toutiaoyan/compose/ui/CatalogShortcutFont.kt")
        output.parentFile.mkdirs()
        output.writeText(
            """
            package com.toutiaoyan.compose.ui

            internal fun catalogShortcutFontBytes(): ByteArray =
                kotlin.io.encoding.Base64.decode(
                    $encoded,
                )
            """.trimIndent() + "\n",
        )
    }
}
kotlin.sourceSets.matching { it.name == "webMain" }.configureEach {
    kotlin.srcDir(generateCatalogShortcutFont)
}

// The baseline Compose plugin recognizes only module UI dependencies, not our Web composite.
afterEvaluate {
    tasks.named("unpackSkikoWasmRuntime") {
        setOnlyIf { true }
    }
}
kotlin.js().compilations.configureEach {
    binaries.configureEach {
        linkSyncTask.configure {
            dependsOn("unpackSkikoWasmRuntime")
            from.from(layout.buildDirectory.dir("compose/skiko-for-web-runtime"))
        }
    }
}
