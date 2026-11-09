@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.select.SelectGroup
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectLabel
import com.toutiaoyan.compose.ui.components.select.SelectSeparator
import com.toutiaoyan.compose.ui.components.select.SelectSize
import com.toutiaoyan.compose.ui.components.select.SelectScrollDownButton
import com.toutiaoyan.compose.ui.components.select.SelectScrollUpButton
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.select.SelectVariant
import com.toutiaoyan.compose.ui.components.select.selectState
import com.toutiaoyan.compose.ui.components.select.SelectValue

val selectDoc =
    ComponentDoc(
        id = "select",
        title = "Select",
        description = "Displays a list of options for the user to pick from—triggered by a button.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.select.Select
            import com.toutiaoyan.compose.ui.components.select.SelectContent
            import com.toutiaoyan.compose.ui.components.select.SelectItem
            import com.toutiaoyan.compose.ui.components.select.SelectTrigger

            var theme by remember { mutableStateOf<String?>(null) }
            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                SelectTrigger()
                SelectContent {
                    SelectItem("Light")
                    SelectItem("Dark")
                    SelectItem("System")
                }
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("value", "T?", "required"),
                ApiProperty("open", "Boolean?", "null"),
                ApiProperty("placeholder", "String", "Select an option"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("SelectTrigger.size", "SelectSize", "Default"),
                ApiProperty("SelectContent.alignItemWithTrigger", "Boolean?", "null"),
            ),
        afterApiSections =
            listOf(
                ComponentGuideSection(
                    title = "Base UI",
                    level = 3,
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "See the [Base UI Select]" +
                                    "(https://base-ui.com/react/components/select#api-reference) documentation.",
                            ),
                        ),
                ),
            ),
        examplesHeading = null,
        beforeCompositionMarkdown = "Use the following composition to build a `Select`:",
        compositionCode =
            """
            Select
            ├── SelectTrigger
            │   └── SelectValue
            └── SelectContent
                ├── SelectGroup
                │   ├── SelectLabel
                │   ├── SelectItem
                │   └── SelectItem
                ├── SelectSeparator
                └── SelectGroup
                    ├── SelectLabel
                    ├── SelectItem
                    └── SelectItem
            """.trimIndent(),
        examples =
            selectPageExamples(
                listOf(
                    ComponentExample(
                        title = "Default",
                        code =
                            """
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit") {
                                SelectTrigger(modifier = Modifier.width(192.dp))
                                SelectContent {
                                    SelectGroup(heading = "Fruits") {
                                        val controller = selectState()
                                        SelectItem<String?>(null, textValue = "Select a fruit", onSelect = { fruit = null; controller.setOpen(false); true })
                                        SelectItem("Apple")
                                        SelectItem("Banana")
                                        SelectItem("Blueberry")
                                        SelectItem("Grapes")
                                        SelectItem("Pineapple")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit") {
                                SelectTrigger(modifier = Modifier.width(192.dp))
                                SelectContent {
                                    SelectGroup(heading = "Fruits") {
                                        val controller = selectState()
                                        SelectItem<String?>(null, textValue = "Select a fruit", onSelect = {
                                            fruit = null
                                            controller.setOpen(false)
                                            true
                                        })
                                        SelectItem("Apple")
                                        SelectItem("Banana")
                                        SelectItem("Blueberry")
                                        SelectItem("Grapes")
                                        SelectItem("Pineapple")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Uncontrolled",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                defaultOpen = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                defaultOpen = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default open",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                defaultOpen = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                defaultOpen = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Compound",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup {
                                        SelectLabel("Appearance")
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                    }
                                    SelectSeparator()
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup {
                                        SelectLabel("Appearance")
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                    }
                                    SelectSeparator()
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Disabled",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System", disabled = true)
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System", disabled = true)
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default disabled",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                disabled = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System", disabled = false)
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                disabled = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System", disabled = false)
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Small",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(size = SelectSize.Sm)
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(size = SelectSize.Sm)
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default size",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(size = SelectSize.Default)
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(size = SelectSize.Default)
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Scroll buttons",
                        code =
                            """
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Fruit") {
                                SelectTrigger()
                                SelectContent {
                                    SelectScrollUpButton()
                                    SelectItem("Apple")
                                    SelectItem("Banana")
                                    SelectItem("Blueberry")
                                    SelectItem("Grapes")
                                    SelectItem("Pineapple")
                                    SelectScrollDownButton()
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Fruit") {
                                SelectTrigger()
                                SelectContent {
                                    SelectScrollUpButton()
                                    SelectItem("Apple")
                                    SelectItem("Banana")
                                    SelectItem("Blueberry")
                                    SelectItem("Grapes")
                                    SelectItem("Pineapple")
                                    SelectScrollDownButton()
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Invalid",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                invalid = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                invalid = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default invalid",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                invalid = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                invalid = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Required",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                required = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                required = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default required",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                required = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                required = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default placeholder",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Select an option",
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Select an option",
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Disabled select",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                disabled = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                disabled = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Non-modal",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                modal = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                modal = false,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Modal",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                modal = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                modal = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default modal",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                modal = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(
                                value = theme,
                                onValueChange = { theme = it },
                                placeholder = "Theme",
                                modal = true,
                            ) {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Group heading",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup(heading = "Theme") {
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                        SelectItem("System")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup(heading = "Theme") {
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                        SelectItem("System")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Prevent item select",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark", onSelect = { true })
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark", onSelect = { true })
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Custom text value",
                        code =
                            """
                            var size by remember { mutableStateOf<String?>(null) }
                            Select(value = size, onValueChange = { size = it }, placeholder = "Size") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("sm", textValue = "Small")
                                    SelectItem("md", textValue = "Medium")
                                    SelectItem("lg", textValue = "Large")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var size by remember { mutableStateOf<String?>(null) }
                            Select(value = size, onValueChange = { size = it }, placeholder = "Size") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("sm", textValue = "Small")
                                    SelectItem("md", textValue = "Medium")
                                    SelectItem("lg", textValue = "Large")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Value",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger { SelectValue() }
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger { SelectValue() }
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Separator",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectSeparator()
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectSeparator()
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Label",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup {
                                        SelectLabel("Appearance")
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                        SelectItem("System")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup {
                                        SelectLabel("Appearance")
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                        SelectItem("System")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Group",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup {
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                    }
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectGroup {
                                        SelectItem("Light")
                                        SelectItem("Dark")
                                    }
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Item",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Accessible label",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(accessibilityLabel = "Theme")
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(accessibilityLabel = "Theme")
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default accessibility label",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(accessibilityLabel = "")
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger(accessibilityLabel = "")
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Trigger",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Content",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default content variant",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(variant = SelectVariant.Default) {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(variant = SelectVariant.Default) {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default dismiss",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(dismissOnClickOutside = true) {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(dismissOnClickOutside = true) {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Side offset",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(sideOffset = 4.dp) {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(sideOffset = 4.dp) {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Default side offset",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(sideOffset = 4.dp) {
                                    SelectItem("Light")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent(sideOffset = 4.dp) {
                                    SelectItem("Light")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Viewport",
                        code =
                            """
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var theme by remember { mutableStateOf<String?>(null) }
                            Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Light")
                                    SelectItem("Dark")
                                    SelectItem("System")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Scroll up button",
                        code =
                            """
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Fruit") {
                                SelectTrigger()
                                SelectContent {
                                    SelectScrollUpButton()
                                    SelectItem("Apple")
                                    SelectItem("Banana")
                                    SelectItem("Pineapple")
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Fruit") {
                                SelectTrigger()
                                SelectContent {
                                    SelectScrollUpButton()
                                    SelectItem("Apple")
                                    SelectItem("Banana")
                                    SelectItem("Pineapple")
                                }
                            }
                        },
                    ),
                    ComponentExample(
                        title = "Scroll down button",
                        code =
                            """
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Fruit") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Apple")
                                    SelectItem("Banana")
                                    SelectItem("Pineapple")
                                    SelectScrollDownButton()
                                }
                            }
                            """.trimIndent(),
                        preview = {
                            var fruit by remember { mutableStateOf<String?>(null) }
                            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Fruit") {
                                SelectTrigger()
                                SelectContent {
                                    SelectItem("Apple")
                                    SelectItem("Banana")
                                    SelectItem("Pineapple")
                                    SelectScrollDownButton()
                                }
                            }
                        },
                    ),
                ),
            ),
    )
