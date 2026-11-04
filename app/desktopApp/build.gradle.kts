import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

dependencies {
    implementation(projects.app.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
    testImplementation(libs.kotlin.testJunit)
}

compose.desktop {
    application {
        mainClass = "com.toutiaoyan.compose.ui.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.toutiaoyan.compose.ui"
            packageVersion = "1.0.0"
            windows { iconFile.set(project.file("icons/app.ico")) }
            linux { iconFile.set(project.file("src/main/resources/icons/app.png")) }
            macOS {
                iconFile.set(project.file("icons/app.icns"))
                bundleID = "com.toutiaoyan.compose.ui"
                infoPlist {
                    extraKeysRawXml =
                        """
                        <key>CFBundleURLTypes</key>
                        <array>
                            <dict>
                                <key>CFBundleTypeRole</key>
                                <string>Editor</string>
                                <key>CFBundleURLName</key>
                                <string>com.toutiaoyan.compose.ui</string>
                                <key>CFBundleURLSchemes</key>
                                <array>
                                    <string>shadcncompose</string>
                                </array>
                            </dict>
                        </array>
                        """.trimIndent()
                }
            }
        }
    }
}
