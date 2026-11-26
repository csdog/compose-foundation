# Install Compose Foundation source with shadcn

For native SwiftUI components, see the [SwiftUI setup](../iosApp/README.md).

The root registry copies Kotlin files into an existing Compose Multiplatform module,
including supporting packages and platform implementations. It keeps the
original Kotlin packages and source-set names. Run the command at the module
root where `src/commonMain` belongs. A KMP module with only an Android target
also works.

For an Android-only Jetpack Compose module, use the Android registry:

```sh
npx shadcn@latest add \
  https://compose-foundation.devops.cn.toutiaoyan.com/r/android/button.json \
  --cwd path/to/app
```

The Android catalog at `/r/android/registry.json` offers the same items and
installs Kotlin into `src/main/kotlin`. Configure the Android application
or library module's Compose plugin and dependencies as follows. The full
catalog was verified with AGP 9.4, the Kotlin 2.4.21 Compose compiler plugin,
and compile SDK 37.

```kotlin
android {
    buildFeatures { compose = true }
}

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }
}

dependencies {
    implementation("androidx.compose.runtime:runtime-android:1.12.1")
    implementation("androidx.compose.foundation:foundation-android:1.12.1")
    implementation("androidx.compose.ui:ui-android:1.12.1")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.12.1")
    implementation("com.composables:compose-interaction-capabilities:1.1.0")
}
```

If the installed sources include `AvatarImage.kt`, add Coil Compose, Coil SVG,
Coil's Ktor 3 network integration, and Ktor's Android OkHttp engine. The versions
and coordinates are in the [version catalog](../../gradle/libs.versions.toml).

## Install a component

Use shadcn CLI 4.23.0 or later. The catalog site serves registry files under
`/r/` when built with registry support.

```sh
npx shadcn@latest add \
  https://compose-foundation.devops.cn.toutiaoyan.com/r/button.json \
  --cwd path/to/composeApp
```

The site's `/r/registry.json` lists available item names. You can pass several
item URLs to one `add` command. Each item contains its Kotlin source dependencies,
so installing `button` also installs the theme it uses. Components that share
source files install them to the same paths. The CLI prompts before replacing
locally edited files.

```kotlin
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset

@Composable
fun SaveAction(onSave: () -> Unit) {
    Theme(preset = StylePreset.Nova) {
        Button("Save", onClick = onSave)
    }
}
```

## Set up the consumer module

Use the Kotlin and Compose versions in the [version catalog](../../gradle/libs.versions.toml)
as the compatibility baseline. Keep Compose Runtime, Foundation, and UI in
`commonMain`, along with UI Tooling Preview for the bundled composable previews
and the interaction capabilities dependency used by `Theme`.
Opt into Compose's experimental Foundation Styles API in the consuming module.
The following example uses the Compose Multiplatform Gradle plugin.

```kotlin
kotlin {
    compilerOptions {
        optIn.add("androidx.compose.foundation.style.ExperimentalFoundationStyleApi")
    }
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.12.2")
            implementation("org.jetbrains.compose.foundation:foundation:1.12.2")
            implementation("org.jetbrains.compose.ui:ui:1.12.2")
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.12.2")
            implementation("com.composables:compose-interaction-capabilities:1.1.0")
        }
    }
}
```

If the installed sources include `AvatarImage.kt`, add Coil Compose, Coil SVG,
Coil's Ktor 3 network integration, and a Ktor client engine for each enabled
target. Use the versions and target dependencies in
[`shadcn/shared/build.gradle.kts`](../shared/build.gradle.kts) and the version
catalog. For JS and Wasm, configure the same `webMain` hierarchy and browser
dependency as that module so installed web implementations are compiled.

Source installation and the `com.toutiaoyan.compose:ui` and
`com.toutiaoyan.compose:tailwind` Maven artifacts provide the same Kotlin
packages. Use source installation or the artifacts in a consuming module.

## Build and try the registry locally

From the compose-foundation repository root, run:

```sh
python3 shadcn/registry/build.py --output build/registry-preview/r
python3 -m http.server 8765 --bind 127.0.0.1 --directory build/registry-preview
```

In another terminal, install from the local server:

```sh
npx shadcn@latest add http://127.0.0.1:8765/r/button.json --cwd path/to/composeApp
```

The generator reads the library's main source sets and follows dependencies
between Kotlin packages. The catalog build publishes the generated JSON
alongside the existing site. Source packages and the generator are maintained
in Git, with JSON generated at build time.
