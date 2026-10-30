@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldOrientation

val checkboxDoc =
    ComponentDoc(
        id = "checkbox",
        title = "Checkbox",
        description = "A tri-state input control: unchecked, checked, or indeterminate.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.checkbox.Checkbox

            var checked by remember { mutableStateOf(false) }
            Checkbox(checked = checked, onCheckedChange = { checked = it }, text = "Accept terms")
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("checked", "Boolean?", "null"),
                ApiProperty("defaultChecked", "Boolean", "false"),
                ApiProperty("indeterminate", "Boolean", "false"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("disabled", "Boolean", "false"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        Checkbox("Accept terms and conditions", checked = checked, onCheckedChange = { checked = it })
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        Checkbox("Accept terms and conditions", checked = checked, onCheckedChange = { checked = it })
                    },
                ),
                ComponentExample(
                    title = "With label",
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        Checkbox(checked = checked, onCheckedChange = { checked = it }, text = "Accept terms")
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        Checkbox(checked = checked, onCheckedChange = { checked = it }, text = "Accept terms")
                    },
                ),
                ComponentExample(
                    title = "With text",
                    code =
                        """
                        var checked by remember { mutableStateOf(true) }
                        Field(orientation = FieldOrientation.Horizontal) {
                            Checkbox(checked = checked, onCheckedChange = { checked = it })
                            Column {
                                FieldLabel("Accept terms and conditions")
                                FieldDescription(
                                    "By clicking this checkbox, you agree to the terms and conditions.",
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(true) }
                        Field(orientation = FieldOrientation.Horizontal) {
                            Checkbox(checked = checked, onCheckedChange = { checked = it })
                            Column {
                                FieldLabel("Accept terms and conditions")
                                FieldDescription(
                                    "By clicking this checkbox, you agree to the terms and conditions.",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Indeterminate",
                    code = """Checkbox(checked = false, indeterminate = true, onCheckedChange = {})""",
                    preview = { Checkbox(checked = false, indeterminate = true, onCheckedChange = {}) },
                ),
                ComponentExample(
                    title = "Default indeterminate",
                    code = """Checkbox(checked = false, indeterminate = false, onCheckedChange = {})""",
                    preview = { Checkbox(checked = false, indeterminate = false, onCheckedChange = {}) },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = """Checkbox(checked = true, onCheckedChange = null, disabled = true)""",
                    preview = { Checkbox(checked = true, onCheckedChange = null, disabled = true) },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code = """Checkbox(checked = false, onCheckedChange = {}, disabled = false)""",
                    preview = { Checkbox(checked = false, onCheckedChange = {}, disabled = false) },
                ),
                ComponentExample(
                    title = "Invalid",
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            text = "Accept terms and conditions",
                            invalid = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            text = "Accept terms and conditions",
                            invalid = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            text = "Accept terms and conditions",
                            invalid = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            text = "Accept terms and conditions",
                            invalid = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "With description",
                    code =
                        """
                        var checked by remember { mutableStateOf(true) }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            text = "Accept terms and conditions",
                            description = "By clicking this checkbox, you agree to the terms.",
                        )
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(true) }
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            text = "Accept terms and conditions",
                            description = "By clicking this checkbox, you agree to the terms.",
                        )
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code = """Checkbox(text = "Accept terms", defaultChecked = true)""",
                    preview = { Checkbox(text = "Accept terms", defaultChecked = true) },
                ),
                ComponentExample(
                    title = "Default checked",
                    code = """Checkbox(text = "Accept terms", defaultChecked = false)""",
                    preview = { Checkbox(text = "Accept terms", defaultChecked = false) },
                ),
                ComponentExample(
                    title = "Required",
                    code = """Checkbox(text = "Accept terms", required = true)""",
                    preview = { Checkbox(text = "Accept terms", required = true) },
                ),
                ComponentExample(
                    title = "Default required",
                    code = """Checkbox(text = "Accept terms", required = false)""",
                    preview = { Checkbox(text = "Accept terms", required = false) },
                ),
            ),
    )
