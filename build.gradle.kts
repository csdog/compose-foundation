import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.vanniktechPublish) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.buildkonfig) apply false
}

// No build-logic/convention-plugin module exists in this project, so Ktlint
// and Detekt are wired here uniformly instead of per-module convention plugins.
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<KtlintExtension> {
        android.set(false)
        outputToConsole.set(true)
        filter {
            exclude("**/generated/**")
            exclude("**/build/**")
            exclude("**/buildkonfig/**")
            exclude("**/*BuildKonfig.kt")
        }
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<DetektExtension> {
        config.setFrom(rootProject.file("detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        baseline = file("detekt-baseline.xml")
        // Scan checked-in source sets even when their targets are disabled on this host.
        source.setFrom(fileTree("src") {
            include("**/*.kt")
            exclude("**/generated/**", "**/build/**")
        })
    }

    tasks.withType<AbstractTestTask>().configureEach {
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = false
        }
    }

    if (System.getenv("CI") != "true") {
        listOf("ktlintCheck", "ktlintFormat").forEach { name ->
            tasks.named(name).configure { dependsOn(rootProject.tasks.named("installGitHooks")) }
        }
    }
}

/** Copy all hooks from .git-hooks/ to .git/hooks/ (idempotent; ktlint tasks depend on this locally). */
tasks.register<Exec>("installGitHooks") {
    group = "setup"
    description = "Install all hooks from .git-hooks/ into .git/hooks/"
    workingDir = layout.projectDirectory.asFile
    commandLine("sh", file("scripts/install-git-hooks.sh").absolutePath)
    isIgnoreExitValue = false
}

// Keep the patched UI and its compatible runtime train on Kotlin Web targets only.
val webComposeGroups = setOf(
    "org.jetbrains.compose.animation",
    "org.jetbrains.compose.foundation",
    "org.jetbrains.compose.runtime",
    "org.jetbrains.compose.ui",
)

subprojects {
    configurations.configureEach {
        val configuration = this
        // Compose's shared JS/Wasm asset configuration has no target attributes.
        // It feeds only Web processResources/linkSync (Compose 1.12.2 configureWebApplication.kt).
        if (name == "COMPOSE_SKIKO_JS_WASM_RUNTIME") {
            attributes.attribute(
                org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.attribute,
                org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.js,
            )
            attributes.attribute(
                org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE,
                objects.named(org.gradle.api.attributes.Usage::class.java, "skiko-runtime"),
            )
        }
        resolutionStrategy.eachDependency {
            val platform = configuration.attributes.getAttribute(
                org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.attribute,
            )
            val wasmTarget = configuration.attributes.getAttribute(
                org.jetbrains.kotlin.gradle.targets.js.KotlinWasmTargetAttribute.wasmTargetAttribute,
            )
            val isWeb = platform == org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.js ||
                (platform == org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.wasm && wasmTarget == org.jetbrains.kotlin.gradle.targets.js.KotlinWasmTargetAttribute.js)
            if (isWeb) {
                when {
                    requested.group == "org.jetbrains.compose.ui" && requested.name in setOf("ui", "ui-js", "ui-wasm-js") ->
                        useTarget("com.toutiaoyan.compose.internal:compose-web-ui-invalid:1.13.0-beta01-invalid.1")
                    requested.group == "org.jetbrains.skiko" && requested.name == "skiko-js-wasm-runtime" ->
                        useTarget("org.jetbrains.skiko:skiko:0.156.0")
                    requested.group in webComposeGroups ||
                        (requested.group == "org.jetbrains.compose.components" &&
                            requested.name in setOf("components-resources", "components-resources-js", "components-resources-wasmJs")) ->
                        useVersion("1.13.0-beta01")
                    requested.group == "org.jetbrains.skiko" &&
                        requested.name in setOf("skiko", "skiko-js", "skiko-wasm-js") ->
                        useVersion("0.156.0")
                }
            }
        }
    }
}

// Compose 1.12.2 does not recognize our substituted Web UI dependency when
// deciding whether to unpack the Skiko runtime. Both the catalog and the Web
// test bundles need the same runtime assets.
val webUiProjects = setOf(":app:webApp", ":app:shared", ":shadcn:core", ":tailwind-core")
subprojects {
    if (path in webUiProjects) {
        val skikoRuntime = layout.buildDirectory.dir("compose/skiko-for-web-runtime")
        val webpackRules = rootProject.file("app/webApp/webpack.config.d")
        gradle.projectsEvaluated {
            tasks.named("unpackSkikoWasmRuntime") {
                setOnlyIf { true }
            }
        }
        afterEvaluate {
            val multiplatform = extensions.getByType<KotlinMultiplatformExtension>()
            val js = multiplatform.js()
            js.compilations.configureEach {
                binaries.configureEach {
                    linkSyncTask.configure {
                        dependsOn("unpackSkikoWasmRuntime")
                        from.from(skikoRuntime)
                    }
                }
            }
            @OptIn(ExperimentalWasmDsl::class)
            val wasm = multiplatform.wasmJs()
            listOf(js, wasm).forEach { target ->
                target.browser {
                    testTask {
                        useKarma {
                            useChromeHeadless()
                            webpackConfig.configDirectory = webpackRules
                        }
                    }
                }
            }
        }
    }
}
