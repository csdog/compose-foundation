import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add(
            "-opt-in=androidx.compose.foundation.style.ExperimentalFoundationStyleApi",
        )
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    android {
        namespace = "com.toutiaoyan.compose.ui.app.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.navigationevent.compose)
        }
        commonMain.dependencies {
            api(projects.core)
            api(projects.shadcn.core)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.androidx.navigationevent)
            // Catalog *examples* only (e.g. Date Picker's trigger icon) -- :shadcn:core
            // itself takes no icon-set dependency, matching every other icon spot
            // there (plain text glyphs). Never add this to :shadcn:core's dependencies.
            // Composables Lucide (com.composables:icons-lucide-cmp).
            implementation(libs.composables.icons.lucide)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.uiTestJunit4)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.espresso.core)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
