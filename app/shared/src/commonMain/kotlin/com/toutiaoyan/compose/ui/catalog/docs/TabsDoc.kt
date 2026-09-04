@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.tabs.Tabs
import com.toutiaoyan.compose.ui.components.tabs.TabsActivationMode
import com.toutiaoyan.compose.ui.components.tabs.TabsContent
import com.toutiaoyan.compose.ui.components.tabs.TabsList
import com.toutiaoyan.compose.ui.components.tabs.TabsListVariant
import com.toutiaoyan.compose.ui.components.tabs.TabsOrientation
import com.toutiaoyan.compose.ui.components.tabs.TabsTrigger

val tabsDoc =
    ComponentDoc(
        id = "tabs",
        title = "Tabs",
        description = "A segmented control with a sliding indicator for switching between related views.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.tabs.Tabs
            import com.toutiaoyan.compose.ui.components.tabs.TabsContent
            import com.toutiaoyan.compose.ui.components.tabs.TabsList
            import com.toutiaoyan.compose.ui.components.tabs.TabsTrigger

            var tab by remember { mutableStateOf("account") }
            Tabs(value = tab, onValueChange = { tab = it }) {
                TabsList {
                    TabsTrigger(value = "account", label = "Account")
                    TabsTrigger(value = "password", label = "Password")
                }
                TabsContent("account", "Account settings")
                TabsContent("password", "Password settings")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var tab by remember { mutableStateOf("account") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList(contentDescription = "Account") {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        var tab by remember { mutableStateOf("account") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList(contentDescription = "Account") {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Line",
                    code =
                        """
                        var tab by remember { mutableStateOf("overview") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList(variant = TabsListVariant.Line) {
                                TabsTrigger(value = "overview", label = "Overview")
                                TabsTrigger(value = "analytics", label = "Analytics")
                                TabsTrigger(value = "reports", label = "Reports")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var tab by remember { mutableStateOf("overview") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList(variant = TabsListVariant.Line) {
                                TabsTrigger(value = "overview", label = "Overview")
                                TabsTrigger(value = "analytics", label = "Analytics")
                                TabsTrigger(value = "reports", label = "Reports")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var tab by remember { mutableStateOf("account") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password", disabled = true)
                            }
                            TabsContent("account", "Make changes to your account here.")
                        }
                        """.trimIndent(),
                    preview = {
                        var tab by remember { mutableStateOf("account") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password", disabled = true)
                            }
                            TabsContent("account", "Make changes to your account here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        var tab by remember { mutableStateOf("account") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password", disabled = false)
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        var tab by remember { mutableStateOf("account") }
                        Tabs(value = tab, onValueChange = { tab = it }) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password", disabled = false)
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        var tab by remember { mutableStateOf("account") }
                        Tabs(
                            value = tab,
                            onValueChange = { tab = it },
                            orientation = TabsOrientation.Vertical,
                        ) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        var tab by remember { mutableStateOf("account") }
                        Tabs(
                            value = tab,
                            onValueChange = { tab = it },
                            orientation = TabsOrientation.Vertical,
                        ) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        var tab by remember { mutableStateOf("account") }
                        Tabs(
                            value = tab,
                            onValueChange = { tab = it },
                            orientation = TabsOrientation.Horizontal,
                        ) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        var tab by remember { mutableStateOf("account") }
                        Tabs(
                            value = tab,
                            onValueChange = { tab = it },
                            orientation = TabsOrientation.Horizontal,
                        ) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        var tab by remember { mutableStateOf("account") }
                        Tabs(
                            value = tab,
                            onValueChange = { tab = it },
                            orientation = TabsOrientation.Horizontal,
                        ) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        var tab by remember { mutableStateOf("account") }
                        Tabs(
                            value = tab,
                            onValueChange = { tab = it },
                            orientation = TabsOrientation.Horizontal,
                        ) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default value",
                    code =
                        """
                        Tabs(defaultValue = "password") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "password") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Manual activation",
                    code =
                        """
                        Tabs(defaultValue = "account", activationMode = TabsActivationMode.Manual) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys move focus; Enter selects.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account", activationMode = TabsActivationMode.Manual) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys move focus; Enter selects.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Automatic activation",
                    code =
                        """
                        Tabs(defaultValue = "account", activationMode = TabsActivationMode.Automatic) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account", activationMode = TabsActivationMode.Automatic) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default activation",
                    code =
                        """
                        Tabs(defaultValue = "account", activationMode = TabsActivationMode.Automatic) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account", activationMode = TabsActivationMode.Automatic) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "No loop",
                    code =
                        """
                        Tabs(defaultValue = "account", loop = false) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys stop at the ends.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account", loop = false) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys stop at the ends.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Loop",
                    code =
                        """
                        Tabs(defaultValue = "account", loop = true) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys wrap around.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account", loop = true) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys wrap around.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default loop",
                    code =
                        """
                        Tabs(defaultValue = "account", loop = true) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys wrap around.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account", loop = true) {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Arrow keys wrap around.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Keep mounted",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", keepMounted = true)
                            TabsContent("password", "Change your password here.", keepMounted = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", keepMounted = true)
                            TabsContent("password", "Change your password here.", keepMounted = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Not keep mounted",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", keepMounted = false)
                            TabsContent("password", "Change your password here.", keepMounted = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", keepMounted = false)
                            TabsContent("password", "Change your password here.", keepMounted = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default keep mounted",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", keepMounted = false)
                            TabsContent("password", "Change your password here.", keepMounted = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", keepMounted = false)
                            TabsContent("password", "Change your password here.", keepMounted = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Force mount",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", forceMount = true)
                            TabsContent("password", "Change your password here.", forceMount = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", forceMount = true)
                            TabsContent("password", "Change your password here.", forceMount = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Not force mount",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", forceMount = false)
                            TabsContent("password", "Change your password here.", forceMount = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", forceMount = false)
                            TabsContent("password", "Change your password here.", forceMount = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default force mount",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", forceMount = false)
                            TabsContent("password", "Change your password here.", forceMount = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.", forceMount = false)
                            TabsContent("password", "Change your password here.", forceMount = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Indicator",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList() {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "List",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                            TabsContent("password", "Change your password here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                                TabsTrigger(value = "password", label = "Password")
                            }
                            TabsContent("account", "Make changes to your account here.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                            }
                            TabsContent("account", "Make changes to your account here.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tabs(defaultValue = "account") {
                            TabsList {
                                TabsTrigger(value = "account", label = "Account")
                            }
                            TabsContent("account", "Make changes to your account here.")
                        }
                    },
                ),
            ),
    )
