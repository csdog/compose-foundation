@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldOrientation
import com.toutiaoyan.compose.ui.components.label.Label

val labelDoc =
    ComponentDoc(
        id = "label",
        title = "Label",
        description = "A form-field label, optionally marked required.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.label.Label

            Label("Email", required = true)
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = """Label("Email")""",
                    preview = { Label("Email") },
                ),
                ComponentExample(
                    title = "Required",
                    code = """Label("Email", required = true)""",
                    preview = { Label("Email", required = true) },
                ),
                ComponentExample(
                    title = "Default required",
                    code = """Label("Email", required = false)""",
                    preview = { Label("Email", required = false) },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = """Label("Email", disabled = true)""",
                    preview = { Label("Email", disabled = true) },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code = """Label("Email", disabled = false)""",
                    preview = { Label("Email", disabled = false) },
                ),
                ComponentExample(
                    title = "With a checkbox",
                    code =
                        """
                        var checked by remember { mutableStateOf(false) }
                        Field(orientation = FieldOrientation.Horizontal) {
                            Checkbox(checked = checked, onCheckedChange = { checked = it })
                            Label("Accept terms and conditions")
                        }
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        Field(orientation = FieldOrientation.Horizontal) {
                            Checkbox(checked = checked, onCheckedChange = { checked = it })
                            Label("Accept terms and conditions")
                        }
                    },
                ),
            ),
    )
