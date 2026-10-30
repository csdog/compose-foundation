@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.components.switch.SwitchSize
import com.toutiaoyan.compose.ui.components.text.Text

val switchDoc =
    ComponentDoc(
        id = "switch",
        title = "Switch",
        description = "An on/off control for a single setting, applied immediately.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.switch.Switch

            var enabled by remember { mutableStateOf(false) }
            Switch(checked = enabled, onCheckedChange = { enabled = it }, text = "Airplane Mode")
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("checked", "Boolean?", "null"),
                ApiProperty("defaultChecked", "Boolean", "false"),
                ApiProperty("size", "SwitchSize", "Default"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("disabled", "Boolean", "false"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var enabled by remember { mutableStateOf(false) }
                        Switch("Airplane Mode", checked = enabled, onCheckedChange = { enabled = it })
                        """.trimIndent(),
                    preview = {
                        var enabled by remember { mutableStateOf(false) }
                        Switch("Airplane Mode", checked = enabled, onCheckedChange = { enabled = it })
                    },
                ),
                ComponentExample(
                    title = "Sizes",
                    code =
                        """
                        Switch(checked = true, onCheckedChange = {})
                        Switch(checked = true, onCheckedChange = {}, size = SwitchSize.Sm)
                        """.trimIndent(),
                    preview = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Switch(checked = true, onCheckedChange = {})
                            Text { "Default" }
                            Switch(checked = true, onCheckedChange = {}, size = SwitchSize.Sm)
                            Text { "Sm" }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default size",
                    code =
                        """
                        var enabled by remember { mutableStateOf(false) }
                        Switch(checked = enabled, onCheckedChange = { enabled = it }, size = SwitchSize.Default)
                        """.trimIndent(),
                    preview = {
                        var enabled by remember { mutableStateOf(false) }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            size = SwitchSize.Default,
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = """Switch(checked = true, onCheckedChange = null, disabled = true)""",
                    preview = { Switch(checked = true, onCheckedChange = null, disabled = true) },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code = """Switch(checked = false, onCheckedChange = {}, disabled = false)""",
                    preview = { Switch(checked = false, onCheckedChange = {}, disabled = false) },
                ),
                ComponentExample(
                    title = "With label",
                    code =
                        """
                        var enabled by remember { mutableStateOf(true) }
                        Switch(checked = enabled, onCheckedChange = { enabled = it }, text = "Airplane Mode")
                        """.trimIndent(),
                    preview = {
                        var enabled by remember { mutableStateOf(true) }
                        Switch(checked = enabled, onCheckedChange = { enabled = it }, text = "Airplane Mode")
                    },
                ),
                ComponentExample(
                    title = "With description",
                    code =
                        """
                        var sharing by remember { mutableStateOf(true) }
                        Switch(
                            checked = sharing,
                            onCheckedChange = { sharing = it },
                            text = "Share across devices",
                            description = "Everyone signed in on this account can see this.",
                        )
                        """.trimIndent(),
                    preview = {
                        var sharing by remember { mutableStateOf(true) }
                        Switch(
                            checked = sharing,
                            onCheckedChange = { sharing = it },
                            text = "Share across devices",
                            description = "Everyone signed in on this account can see this.",
                        )
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    code =
                        """
                        var enabled by remember { mutableStateOf(false) }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            text = "Airplane Mode",
                            invalid = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var enabled by remember { mutableStateOf(false) }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            text = "Airplane Mode",
                            invalid = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        var enabled by remember { mutableStateOf(false) }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            text = "Airplane Mode",
                            invalid = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var enabled by remember { mutableStateOf(false) }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            text = "Airplane Mode",
                            invalid = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code = """Switch(text = "Airplane Mode", defaultChecked = true)""",
                    preview = { Switch(text = "Airplane Mode", defaultChecked = true) },
                ),
                ComponentExample(
                    title = "Default checked",
                    code = """Switch(text = "Airplane Mode", defaultChecked = false)""",
                    preview = { Switch(text = "Airplane Mode", defaultChecked = false) },
                ),
                ComponentExample(
                    title = "Required",
                    code = """Switch(text = "Airplane Mode", required = true)""",
                    preview = { Switch(text = "Airplane Mode", required = true) },
                ),
                ComponentExample(
                    title = "Default required",
                    code = """Switch(text = "Airplane Mode", required = false)""",
                    preview = { Switch(text = "Airplane Mode", required = false) },
                ),
                ComponentExample(
                    title = "Thumb",
                    code =
                        """
                        var enabled by remember { mutableStateOf(true) }
                        Switch(checked = enabled, onCheckedChange = { enabled = it }) {
                            
                        }
                        """.trimIndent(),
                    preview = {
                        var enabled by remember { mutableStateOf(true) }
                        Switch(checked = enabled, onCheckedChange = { enabled = it }) {
                        }
                    },
                ),
            ),
    )
