import java.security.MessageDigest
import java.util.zip.ZipFile
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
plugins {
    kotlin("multiplatform") version "2.4.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20"
    id("org.jetbrains.kotlinx.atomicfu") version "0.28.0"
}
group = "com.toutiaoyan.compose.internal"
version = "1.13.0-beta01-invalid.1"
val upstreamSources by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}
dependencies {
    upstreamSources("org.jetbrains.compose.ui:ui-js:1.13.0-beta01:sources@jar")
    upstreamSources("org.jetbrains.compose.ui:ui-wasm-js:1.13.0-beta01:sources@jar")
}
// Pin the release source bytes and fail closed when the reviewed patch no longer applies.
abstract class GenerateUpstreamSources : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val archives: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val destination: DirectoryProperty

    @TaskAction
    fun generate() {
        fun sha(bytes: ByteArray) = MessageDigest.getInstance("SHA-256")
            .digest(bytes).joinToString("") { "%02x".format(it) }
        val expected = mapOf(
            "ui-js-1.13.0-beta01-sources.jar" to "0f1ecdc228eada93b714d4a7722ea45e87b7d147a5909186682045893fbd1aa6",
            "ui-wasm-js-1.13.0-beta01-sources.jar" to "d1e9bf04d22d39b04651bc274b5c0de059ace2a1a0e437e60993a49237facb33",
        )
        val counts = mapOf("commonMain" to 244, "skikoMain" to 86, "nonJvmMain" to 8,
            "webMain" to 52, "jsMain" to 3, "wasmJsMain" to 3)
        check(archives.files.map { it.name }.toSet() == expected.keys) { "Unexpected source archives" }
        val sources = sortedMapOf<String, ByteArray>()
        for (archive in archives.files.sortedBy { it.name }) {
            check(sha(archive.readBytes()) == expected.getValue(archive.name)) { "Source archive hash mismatch: ${archive.name}" }
            ZipFile(archive).use { zip ->
                for (entry in zip.entries().asSequence()) {
                    val path = entry.name
                    check(!path.startsWith("/") && '\\' !in path && path.split('/').none { it == ".." }) {
                        "Unsafe archive path: $path"
                    }
                    if (entry.isDirectory || !path.endsWith(".kt")) continue
                    check(path.substringBefore('/') in counts) { "Unexpected source set: $path" }
                    val bytes = zip.getInputStream(entry).use { it.readBytes() }
                    val previous = sources.put(path, bytes)
                    check(previous == null || previous.contentEquals(bytes)) { "Overlapping source mismatch: $path" }
                }
            }
        }
        check(counts.all { (set, count) -> sources.keys.count { it.startsWith("$set/") } == count } && sources.size == 396) {
            "Upstream source inventory changed"
        }
        val listener = "webMain/androidx/compose/ui/platform/accessibility/ComposeWebSemanticsListener.kt"
        val before = sources.getValue(listener)
        check(sha(before) == "898e32bb3b83f7a2695330aa3a647b40e2340db4b72692ea16bfd52924ec34f8") { "Listener preimage mismatch" }
        val old = "        if (SemanticsProperties.Selected in config) {"
        val replacement = """
            |        if (SemanticsProperties.Error in config) {
            |            htmlNode.setAttribute("aria-invalid", "true")
            |        } else {
            |            htmlNode.removeAttribute("aria-invalid")
            |        }
            |
            |        if (SemanticsProperties.Selected in config) {
        """.trimMargin()
        val text = before.toString(Charsets.UTF_8)
        check(text.indexOf(old) == text.lastIndexOf(old) && old in text) { "Patch context mismatch" }
        sources[listener] = text.replace(old, replacement).toByteArray(Charsets.UTF_8)
        check(sha(sources.getValue(listener)) == "543b90746ec4abfebb4f5141150ac51485d6115dbc095fcd1129c4203f0fdda3") { "Listener postimage mismatch" }
        val radioSelectedOld = """
            |        if (SemanticsProperties.Selected in config) {
            |            val selected = config[SemanticsProperties.Selected]
            |            htmlNode.setAttribute("aria-selected", selected.toString())
            |        } else {
            |            htmlNode.removeAttribute("aria-selected")
            |        }
            |
            |        val roleId = config.getRoleId()
            |
        """.trimMargin()
        val radioSelectedNew = """
            |        val roleId = config.getRoleId()
            |        if (SemanticsProperties.Selected in config && roleId != AriaRoleId.RadioButton) {
            |            val selected = config[SemanticsProperties.Selected]
            |            htmlNode.setAttribute("aria-selected", selected.toString())
            |        } else {
            |            htmlNode.removeAttribute("aria-selected")
            |        }
            |
            |
        """.trimMargin()
        val radioCheckedOld = """
            |        if (config.contains(SemanticsProperties.ToggleableState)) {
            |            val ariaChecked = config[SemanticsProperties.ToggleableState].toAriaChecked()
            |            htmlNode.setAttribute("aria-checked", ariaChecked)
            |        } else {
            |            htmlNode.removeAttribute("aria-checked")
            |        }
        """.trimMargin()
        val radioCheckedNew = """
            |        if (roleId == AriaRoleId.RadioButton && SemanticsProperties.Selected in config) {
            |            htmlNode.setAttribute("aria-checked", config[SemanticsProperties.Selected].toString())
            |        } else if (config.contains(SemanticsProperties.ToggleableState)) {
            |            val ariaChecked = config[SemanticsProperties.ToggleableState].toAriaChecked()
            |            htmlNode.setAttribute("aria-checked", ariaChecked)
            |        } else {
            |            htmlNode.removeAttribute("aria-checked")
            |        }
        """.trimMargin()
        val radioText = sources.getValue(listener).toString(Charsets.UTF_8)
        check(radioSelectedOld in radioText && radioText.indexOf(radioSelectedOld) == radioText.lastIndexOf(radioSelectedOld)) { "Radio selected patch context mismatch" }
        check(radioCheckedOld in radioText && radioText.indexOf(radioCheckedOld) == radioText.lastIndexOf(radioCheckedOld)) { "Radio checked patch context mismatch" }
        sources[listener] = radioText.replace(radioSelectedOld, radioSelectedNew)
            .replace(radioCheckedOld, radioCheckedNew).toByteArray(Charsets.UTF_8)
        check(sha(sources.getValue(listener)) == "2d3fc02987caa82bf87e7db98aaf9b38854f99e2d452b3b02454a8efcbb0817e") { "Radio listener postimage mismatch" }
        // A two-state button exposes pressed; checkboxes, switches, and radios use checked.
        val togglePressed = """
            |        val toggleState = config.getOrNull(SemanticsProperties.ToggleableState)
            |        val buttonPressed = toggleState?.takeIf { roleId == AriaRoleId.Button }
            |        if (buttonPressed != null) {
            |            htmlNode.setAttribute("aria-pressed", buttonPressed.toAriaChecked())
            |        } else {
            |            htmlNode.removeAttribute("aria-pressed")
            |        }
            |        if (roleId == AriaRoleId.RadioButton && SemanticsProperties.Selected in config) {
            |            htmlNode.setAttribute("aria-checked", config[SemanticsProperties.Selected].toString())
            |        } else if (toggleState != null && buttonPressed == null) {
            |            htmlNode.setAttribute("aria-checked", toggleState.toAriaChecked())
            |        } else {
            |            htmlNode.removeAttribute("aria-checked")
            |        }
        """.trimMargin()
        val toggleListener = sources.getValue(listener).toString(Charsets.UTF_8)
        check(toggleListener.indexOf(radioCheckedNew) == toggleListener.lastIndexOf(radioCheckedNew) && radioCheckedNew in toggleListener) {
            "Toggle pressed patch context mismatch"
        }
        sources[listener] = toggleListener.replace(radioCheckedNew, togglePressed).toByteArray(Charsets.UTF_8)
        check(sha(sources.getValue(listener)) == "754ab9699688c3f0bb9c7cabc2e709c99b901046cec8b02624506e14e7fba4a7") { "Toggle pressed postimage mismatch" }
        // Browser ContextMenu events must reach every Web Compose key handler as Key.Menu.
        val keyEvent = "webMain/androidx/compose/ui/input/key/KeyEvent.web.kt"
        val keyBefore = sources.getValue(keyEvent)
        check(sha(keyBefore) == "8c2906e3fb25c8dd805beb0efd12865190e71bac232d5d82b558279e6b31b104") { "Key event preimage mismatch" }
        val keyOld = "    \"Escape\" to Key.Escape,"
        val keyReplacement = "    \"Escape\" to Key.Escape,\n    \"ContextMenu\" to Key.Menu,"
        val keyText = keyBefore.toString(Charsets.UTF_8)
        check(keyText.indexOf(keyOld) == keyText.lastIndexOf(keyOld) && keyOld in keyText) { "Key event patch context mismatch" }
        sources[keyEvent] = keyText.replace(keyOld, keyReplacement).toByteArray(Charsets.UTF_8)
        check(sha(sources.getValue(keyEvent)) == "962340448a261961a17c452485ab6d0b2fa8b3643d356338150e09eb7fb3e6e4") { "Key event postimage mismatch" }
        val output = destination.get().asFile
        output.deleteRecursively()
        for ((path, bytes) in sources) {
            val file = output.resolve(path.substringBefore('/') + "/kotlin/" + path.substringAfter('/'))
            file.parentFile.mkdirs()
            file.writeBytes(bytes)
        }
        output.resolve("source-hashes.txt").writeText(sources.entries.joinToString("\n", postfix = "\n") { (path, bytes) -> "${sha(bytes)}  $path" })
    }
}

val generatedSources = layout.buildDirectory.dir("generated/upstream")
val generateUpstreamSources by tasks.registering(GenerateUpstreamSources::class) {
    archives.from(upstreamSources)
    destination.set(generatedSources)
}
kotlin {
    js {
        browser()
        outputModuleName.set("compose-multiplatform-core-compose-ui-ui")
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        outputModuleName.set("compose-multiplatform-core-compose-ui-ui")
    }
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_2)
        languageVersion.set(KotlinVersion.KOTLIN_2_2)
        freeCompilerArgs.addAll("-Xir-module-name=org.jetbrains.compose.ui:ui", "-Xexpect-actual-classes", "-XXLanguage:+AllowAnyAsAnActualTypeForExpectInterface", "-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        optIn.addAll("androidx.compose.ui.ExperimentalComposeUiApi", "androidx.compose.ui.InternalComposeUiApi")
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                api("androidx.annotation:annotation:1.9.1")
                implementation("androidx.collection:collection:1.6.0")
                implementation("org.jetbrains.compose.runtime:runtime:1.13.0-beta01")
                api("androidx.compose.runtime:runtime-retain:1.13.0-beta01")
                api("org.jetbrains.compose.runtime:runtime-saveable:1.13.0-beta01")
                api("org.jetbrains.compose.ui:ui-geometry:1.13.0-beta01")
                api("org.jetbrains.compose.ui:ui-graphics:1.13.0-beta01")
                api("org.jetbrains.compose.ui:ui-text:1.13.0-beta01")
                api("org.jetbrains.compose.ui:ui-unit:1.13.0-beta01")
                api("org.jetbrains.compose.ui:ui-util:1.13.0-beta01")
                api("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.6")
                api("androidx.savedstate:savedstate-compose:1.4.0")
            }
        }
        val skikoMain by creating {
            dependsOn(commonMain)
            dependencies {
                api("org.jetbrains.compose.ui:ui-skiko:1.13.0-beta01")
                implementation("org.jetbrains.kotlinx:atomicfu:0.28.0")
                implementation("org.jetbrains.androidx.navigationevent:navigationevent-compose:1.1.0")
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
                implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.11.0")
            }
        }
        val nonJvmMain by creating {
            dependsOn(skikoMain)
            dependencies {
                implementation("org.jetbrains.compose.annotation-internal:annotation:1.10.0")
                implementation("org.jetbrains.compose.collection-internal:collection:1.10.0")
                implementation("org.jetbrains.compose.ui:ui-backhandler:1.13.0-beta01")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.9.6")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-savedstate:2.9.6")
            }
        }
        val webMain by creating {
            dependsOn(nonJvmMain)
            dependencies { api("org.jetbrains.kotlinx:kotlinx-browser:0.5.0") }
        }
        jsMain.get().dependsOn(webMain)
        wasmJsMain.get().apply {
            dependsOn(webMain)
            dependencies { implementation("org.jetbrains.skiko:skiko-wasm-js:0.156.0") }
        }
    }
    sourceSets.configureEach {
        kotlin.srcDir(generateUpstreamSources.map { generatedSources.get().dir("$name/kotlin") })
    }
}
composeCompiler { includeSourceInformation.set(false); includeTraceMarkers.set(false) }
