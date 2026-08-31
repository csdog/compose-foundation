import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.TargetConfigDsl
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.buildkonfig)
}

if (!project.hasProperty("buildkonfig.flavor")) {
    val resolvedFlavor =
        when {
            rootProject.hasProperty("development") -> "development"
            gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) } -> "product"
            else -> "development"
        }
    extensions.extraProperties["buildkonfig.flavor"] = resolvedFlavor
}

buildkonfig {
    packageName = "com.toutiaoyan.compose.ui"

    defaultConfigs {
        configsFromClientProperties("development.properties")
    }
    defaultConfigs("development") {
        configsFromClientProperties("development.properties")
    }
    defaultConfigs("product") {
        configsFromClientProperties("product.properties")
    }
}

fun TargetConfigDsl.configsFromClientProperties(file: String) {
    val properties =
        Properties().apply {
            val propertiesFile = layout.projectDirectory.file("config/client/$file").asFile
            load(propertiesFile.inputStream())
        }
    buildConfigField(STRING, "BACKEND_BASE_URL", properties.getProperty("BACKEND_BASE_URL") ?: "")
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    androidLibrary {
        namespace = "com.toutiaoyan.compose.ui.core"
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
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
