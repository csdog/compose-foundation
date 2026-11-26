rootProject.name = "compose-foundation"
includeBuild("gradle/compose-web-ui")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":app:androidApp")
project(":app:androidApp").projectDir = file("shadcn/androidApp")
include(":app:desktopApp")
include(":app:shared")
include(":app:webApp")
include(":core")
include(":shadcn:core")
project(":shadcn:core").projectDir = file("shadcn/shared")
// Unique Gradle project name (not ":tailwind:core") — two leaf projects both named
// "core" make KMP/Gradle substitute one for the other and create a self-cycle.
include(":tailwind-core")
project(":tailwind-core").projectDir = file("tailwind/core")
