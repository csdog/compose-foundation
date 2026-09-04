@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupItem
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupOrientation

val radioGroupDoc =
    ComponentDoc(
        id = "radio-group",
        title = "Radio Group",
        description = "A set of mutually exclusive options. Combine RadioGroupItem inside RadioGroup.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
            import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupItem

            var choice by remember { mutableStateOf("a") }
            RadioGroup(value = choice, onValueChange = { choice = it }, contentDescription = "Plan") {
                RadioGroupItem("a", "Option A")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            contentDescription = "Plan",
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            contentDescription = "Plan",
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B", disabled = true)
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B", disabled = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }, disabled = false) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B", disabled = false)
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }, disabled = false) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B", disabled = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            orientation = RadioGroupOrientation.Horizontal,
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                            RadioGroupItem("c", "Option C")
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            orientation = RadioGroupOrientation.Horizontal,
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                            RadioGroupItem("c", "Option C")
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            orientation = RadioGroupOrientation.Vertical,
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            orientation = RadioGroupOrientation.Vertical,
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            orientation = RadioGroupOrientation.Vertical,
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(
                            value = choice,
                            onValueChange = { choice = it },
                            orientation = RadioGroupOrientation.Vertical,
                        ) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "With description",
                    code =
                        """
                        var choice by remember { mutableStateOf("comfortable") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem(
                                "default",
                                "Default",
                                description = "The default spacing for the app.",
                            )
                            RadioGroupItem(
                                "comfortable",
                                "Comfortable",
                                description = "A little extra room between items.",
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("comfortable") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem(
                                "default",
                                "Default",
                                description = "The default spacing for the app.",
                            )
                            RadioGroupItem(
                                "comfortable",
                                "Comfortable",
                                description = "A little extra room between items.",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A", invalid = true)
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A", invalid = true)
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A", invalid = false)
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A", invalid = false)
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        RadioGroup(defaultValue = "a") {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        RadioGroup(defaultValue = "a") {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default value",
                    code =
                        """
                        RadioGroup(defaultValue = "a") {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        RadioGroup(defaultValue = "a") {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled group",
                    code =
                        """
                        RadioGroup(defaultValue = "a", disabled = true) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        RadioGroup(defaultValue = "a", disabled = true) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Required",
                    code =
                        """
                        RadioGroup(defaultValue = "a", required = true) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        RadioGroup(defaultValue = "a", required = true) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default required",
                    code =
                        """
                        RadioGroup(defaultValue = "a", required = false) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        RadioGroup(defaultValue = "a", required = false) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                        """.trimIndent(),
                    preview = {
                        var choice by remember { mutableStateOf("a") }
                        RadioGroup(value = choice, onValueChange = { choice = it }) {
                            RadioGroupItem("a", "Option A")
                            RadioGroupItem("b", "Option B")
                        }
                    },
                ),
            ),
    )
