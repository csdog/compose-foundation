import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktechPublish)
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
            baseName = "TailwindCompose"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    androidLibrary {
        namespace = "com.toutiaoyan.compose.tailwind"
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
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    // Maven Central rejects unsigned artifacts. Skip signing for publishToMavenLocal
    // so local verification works without a GPG key (CI/release still signs).
    if (!gradle.startParameter.taskNames.any { it.contains("ToMavenLocal", ignoreCase = true) }) {
        signAllPublications()
    }

    coordinates(artifactId = "tailwind")

    pom {
        name = "compose-tailwind"
        description = "Vendored Tailwind-style Compose Multiplatform utilities " +
            "(TwColors, Oklch, Modifier/Style helpers) used by compose-foundation."
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
