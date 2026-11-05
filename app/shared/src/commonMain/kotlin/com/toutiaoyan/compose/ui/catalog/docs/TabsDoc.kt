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
import com.toutiaoyan.compose.ui.components.tabs.TabsOrientation
import com.toutiaoyan.compose.ui.components.tabs.TabsTrigger

private val tabsApiSections =
    listOf(
        ComponentApiSection(
            "Tabs",
            (
                "Controlled (`value` + `onValueChange`) or uncontrolled (`defaultValue`). " +
                    "Activation modes follow Base UI (`activationMode = Manual` keeps arrow " +
                    "keys from activating a panel until Enter/Space). `orientation` switches " +
                    "horizontal/vertical keyboard navigation. See the [Base UI Tabs API]" +
                    "(https://base-ui.com/react/components/tabs#api-reference)."
            ),
            listOf(
                ApiProperty("value / onValueChange", "String? / ((String) -> Unit)?", "null"),
                ApiProperty("defaultValue", "String?", "First tab"),
                ApiProperty("orientation", "TabsOrientation", "Horizontal"),
                ApiProperty("activationMode", "TabsActivationMode", "Automatic"),
            ),
        ),
        ComponentApiSection(
            "TabsList / TabsTrigger",
            (
                "`TabsList` owns the shared chrome (default pill/line variants, 150ms " +
                    "transition). `TabsTrigger` is the single Tab entry; only the selected " +
                    "trigger is focusable in the default mode, and arrows move within the list."
            ),
            listOf(
                ApiProperty("variant", "TabsVariant", "TabsVariant.Default"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("icon", "@Composable (() -> Unit)?", "null"),
            ),
        ),
        ComponentApiSection(
            "TabsContent",
            (
                "Panel content. Hidden panels stay composed when `keepMounted`/`forceMount` " +
                    "is requested; otherwise they leave composition and lose focus."
            ),
            listOf(
                ApiProperty("value", "String", "Required"),
                ApiProperty("keepMounted", "Boolean", "false"),
                ApiProperty("forceMount", "Boolean", "false"),
            ),
        ),
    )

val tabsDoc =
    ComponentDoc(
        id = "tabs",
        title = "Tabs",
        description = "A set of layered sections of content, known as tab panels, displayed one at a time.",
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
        examplesHeading = null,
        compositionCode =
            """
            Tabs
            ├── TabsList
            │   ├── TabsTrigger
            │   └── TabsTrigger
            ├── TabsContent
            └── TabsContent
            """.trimIndent(),
        apiSections = tabsApiSections,
        examples =
            tabsOfficialExamples +
                listOf(
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
                ).map { it.copy(title = "Kotlin extension: " + it.title) },
    )
