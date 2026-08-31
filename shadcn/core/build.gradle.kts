import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.roborazzi)
}

roborazzi {
    outputDir = project.file("src/jvmTest/snapshots")
}

group = project.property("GROUP") as String
version = project.property("VERSION_NAME") as String

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
            baseName = "ShadcnCompose"
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
        namespace = "com.toutiaoyan.compose.ui"
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
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            // No Material dependency: this library is fully custom, built on
            // the Compose Styles API (@ExperimentalStylesApi) instead.
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.composables.compose.interaction.capabilities)
            // Tailwind utilities live in :tailwind-core (api so callers can use TwColors etc.)
            api(projects.tailwindCore)
            // AvatarImage: Coil3 (raster + SVG) over Ktor on all targets.
            api(libs.coil.compose)
            api(libs.coil.svg)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.espresso.core)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js.wasm.js)
        }
        jvmTest.dependencies {
            // Robolectric-less JVM/Desktop screenshot testing: `roborazzi` and
            // `roborazzi-junit-rule` only publish as Android .aar (Robolectric-based),
            // which doesn't resolve for this module's plain jvm() target -- only
            // `roborazzi-compose-desktop` publishes a real desktop/jvm artifact.
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.uiTestJunit4)
            implementation(libs.roborazzi.compose.desktop)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

mavenPublishing {
    // Central Portal publish target, with automatic release: once uploaded and
    // validated, the deployment publishes on its own with no manual "release" click
    // in the Central Portal UI. Needs real Sonatype Central Portal credentials --
    // ORG_GRADLE_PROJECT_mavenCentralUsername/mavenCentralPassword (env vars or
    // ~/.gradle/gradle.properties, never committed here) -- `publish` fails without
    // them. `publishToMavenLocal` works with no credentials and is the way to verify
    // this wiring locally.
    publishToMavenCentral(automaticRelease = true)

    // Maven Central rejects unsigned artifacts. Resolves a signing key the same way
    // icons-lucide-cmp do -- either the standard `signing.keyId` /
    // `signing.password` / `signing.secretKeyRingFile` Gradle properties (keyring
    // file) or the in-memory equivalents (ORG_GRADLE_PROJECT_signingInMemoryKey /
    // signingInMemoryKeyPassword / signingInMemoryKeyId, preferred for CI since no
    // keyring file needs to exist on disk). No key material lives in this repo.
    // Skip signing for publishToMavenLocal so local POM/artifact checks work without GPG.
    if (!gradle.startParameter.taskNames.any { it.contains("ToMavenLocal", ignoreCase = true) }) {
        signAllPublications()
    }

    coordinates(artifactId = "ui")

    pom {
        name = "compose-ui"
        description = "A shadcn/ui-inspired component library for Compose Multiplatform -- " +
                "token-based theming and sealed variant systems built on the Compose Styles API, " +
                "no Material dependency."
        url = "https://gitlab.devops.toutiaoyan.com/web-foundation/compose-foundation"
        inceptionYear = "2026"

        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }

        developers {
            developer {
                id = "toutiaoyan"
                name = "Toutiaoyan"
                url = "https://gitlab.devops.toutiaoyan.com/web-foundation"
            }
        }

        scm {
            url = "https://gitlab.devops.toutiaoyan.com/web-foundation/compose-foundation"
            connection = "scm:git:git://gitlab.devops.toutiaoyan.com/web-foundation/compose-foundation.git"
            developerConnection =
                "scm:git:ssh://git@gitlab.devops.toutiaoyan.com:2222/web-foundation/compose-foundation.git"
        }
    }
}
