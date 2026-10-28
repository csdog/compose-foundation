import org.jlleitschuh.gradle.ktlint.KtlintExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

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
    }

    dependencies {
        add("detektPlugins", rootProject.libs.detekt.formatting)
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
