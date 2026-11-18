@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.select.*

@Composable
internal fun SelectDefaultPreview() {
    var fruit by remember { mutableStateOf<String?>(null) }
    Select<String?>(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit") {
        SelectTrigger(modifier = Modifier.width(192.dp))
        SelectContent {
            SelectGroup(heading = "Fruits") {
                SelectItem(null, textValue = "Select a fruit")
                SelectItem("Apple")
                SelectItem("Banana")
                SelectItem("Blueberry")
                SelectItem("Grapes")
                SelectItem("Pineapple")
            }
        }
    }
}

@Composable
internal fun SelectUncontrolledPreview() {
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
}

@Composable
internal fun SelectDefaultOpenPreview() {
    var mounted by remember { mutableStateOf(false) }
    if (!mounted) {
        Button(onClick = { mounted = true }) { Text { "Show default-open select" } }
    } else {
        var theme by remember { mutableStateOf<String?>(null) }
        Select(
            value = theme,
            onValueChange = { theme = it },
            placeholder = "Theme",
            defaultOpen = true,
        ) {
            SelectTrigger()
            SelectContent {
                SelectItem("Light")
            }
        }
    }
}

@Composable
internal fun SelectCompoundPreview() {
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
}

@Composable
internal fun SelectDefaultDisabledPreview() {
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
}

@Composable
internal fun SelectSmallPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger(size = SelectSize.Sm)
        SelectContent {
            SelectItem("Light")
            SelectItem("Dark")
            SelectItem("System")
        }
    }
}

@Composable
internal fun SelectDefaultSizePreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger(size = SelectSize.Default)
        SelectContent {
            SelectItem("Light")
        }
    }
}

@Composable
internal fun SelectScrollButtonsPreview() {
    var option by remember { mutableStateOf("Option 20") }
    Select(value = option, onValueChange = { option = it }, placeholder = "Select an option") {
        SelectTrigger(Modifier.width(192.dp))
        SelectContent(style = Style { height(160.dp) }, alignItemWithTrigger = false) {
            SelectGroup { repeat(40) { SelectItem("Option $it") } }
        }
    }
}

@Composable
internal fun SelectDefaultInvalidPreview() {
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
}

@Composable
internal fun SelectRequiredPreview() {
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
}

@Composable
internal fun SelectDefaultRequiredPreview() {
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
}

@Composable
internal fun SelectDefaultPlaceholderPreview() {
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
}

@Composable
internal fun SelectDisabledSelectPreview() {
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
}

@Composable
internal fun SelectNonModalPreview() {
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
}

@Composable
internal fun SelectModalPreview() {
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
}

@Composable
internal fun SelectDefaultModalPreview() {
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
}

@Composable
internal fun SelectGroupHeadingPreview() {
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
}

@Composable
internal fun SelectPreventItemSelectPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent {
            SelectItem("Light")
            SelectItem("Dark", onSelect = { true })
            SelectItem("System")
        }
    }
}

@Composable
internal fun SelectCustomTextValuePreview() {
    var size by remember { mutableStateOf<String?>(null) }
    Select(value = size, onValueChange = { size = it }, placeholder = "Size") {
        SelectTrigger()
        SelectContent {
            SelectItem("sm", textValue = "Small")
            SelectItem("md", textValue = "Medium")
            SelectItem("lg", textValue = "Large")
        }
    }
}

@Composable
internal fun SelectValuePreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger { SelectValue() }
        SelectContent {
            SelectItem("Light")
            SelectItem("Dark")
            SelectItem("System")
        }
    }
}

@Composable
internal fun SelectSeparatorPreview() {
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
}

@Composable
internal fun SelectLabelPreview() {
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
}

@Composable
internal fun SelectGroupPreview() {
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
}

@Composable
internal fun SelectItemPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent {
            SelectItem("Light")
            SelectItem("Dark")
        }
    }
}

@Composable
internal fun SelectAccessibleLabelPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger(accessibilityLabel = "Theme")
        SelectContent {
            SelectItem("Light")
        }
    }
}

@Composable
internal fun SelectDefaultAccessibilityLabelPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger(accessibilityLabel = "")
        SelectContent {
            SelectItem("Light")
        }
    }
}

@Composable
internal fun SelectTriggerPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent {
            SelectItem("Light")
        }
    }
}

@Composable
internal fun SelectContentPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent {
            SelectItem("Light")
            SelectItem("Dark")
            SelectItem("System")
        }
    }
}

@Composable
internal fun SelectDefaultContentVariantPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent(variant = SelectVariant.Default) {
            SelectItem("Light")
            SelectItem("Dark")
            SelectItem("System")
        }
    }
}

@Composable
internal fun SelectDefaultDismissPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent(dismissOnClickOutside = true) {
            SelectItem("Light")
        }
    }
}

@Composable
internal fun SelectSideOffsetPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent(sideOffset = 4.dp) {
            SelectItem("Light")
        }
    }
}

@Composable
internal fun SelectDefaultSideOffsetPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent(sideOffset = 4.dp) {
            SelectItem("Light")
        }
    }
}

@Composable
internal fun SelectViewportPreview() {
    var theme by remember { mutableStateOf<String?>(null) }
    Select(value = theme, onValueChange = { theme = it }, placeholder = "Theme") {
        SelectTrigger()
        SelectContent {
            SelectItem("Light")
            SelectItem("Dark")
            SelectItem("System")
        }
    }
}

@Composable
internal fun SelectScrollUpButtonPreview() {
    var option by remember { mutableStateOf("Option 20") }
    Select(value = option, onValueChange = { option = it }, placeholder = "Select an option") {
        SelectTrigger(Modifier.width(192.dp))
        SelectContent(style = Style { height(160.dp) }, alignItemWithTrigger = false) {
            SelectGroup { repeat(40) { SelectItem("Option $it") } }
        }
    }
}

@Composable
internal fun SelectScrollDownButtonPreview() {
    var option by remember { mutableStateOf("Option 0") }
    Select(value = option, onValueChange = { option = it }, placeholder = "Select an option") {
        SelectTrigger(Modifier.width(192.dp))
        SelectContent(style = Style { height(160.dp) }, alignItemWithTrigger = false) {
            SelectGroup { repeat(40) { SelectItem("Option $it") } }
        }
    }
}
