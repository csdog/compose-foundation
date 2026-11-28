plugins {
    id("com.android.application") version "9.4.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.21"
}

android {
    namespace = "com.toutiaoyan.compose.ui.animation"
    compileSdk = 37
    defaultConfig {
        minSdk = 24
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.foundation:foundation-android:1.12.1")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.12.1")
    implementation("com.composables:compose-interaction-capabilities:1.1.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.12.1")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}
