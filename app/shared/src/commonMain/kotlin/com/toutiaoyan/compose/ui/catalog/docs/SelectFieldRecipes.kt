@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.field.*
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.switch.Switch

// recipe:align
@Composable
internal fun SelectAlignItemPreview() {
    val fruits = listOf("Apple", "Banana", "Blueberry", "Grapes", "Pineapple")
    var alignItem by remember { mutableStateOf(true) }
    var fruit by remember { mutableStateOf<String?>("Banana") }
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        Field(orientation = FieldOrientation.Horizontal) {
            FieldContent {
                FieldLabel(
                    "Align Item",
                    modifier = Modifier.focusProperties { canFocus = false }.clickable { alignItem = !alignItem },
                )
                FieldDescription("Toggle to align the item with the trigger.")
            }
            Switch(checked = alignItem, onCheckedChange = { alignItem = it }, label = "Align Item")
        }
        Field {
            Select<String?>(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit") {
                SelectTrigger(Modifier.fillMaxWidth())
                SelectContent(alignItemWithTrigger = alignItem) {
                    SelectGroup {
                        SelectItem(null, textValue = "Select a fruit")
                        fruits.forEach { SelectItem(it) }
                    }
                }
            }
        }
    }
}

// recipe:invalid
@Composable
internal fun SelectInvalidPreview() {
    var fruit by remember { mutableStateOf<String?>(null) }
    val trigger = remember { FocusRequester() }
    Field(Modifier.width(192.dp), invalid = true) {
        FieldLabel(
            "Fruit",
            modifier = Modifier.focusProperties { canFocus = false }.clickable { trigger.requestFocus() },
        )
        Select<String?>(
            value = fruit,
            onValueChange = { fruit = it },
            placeholder = "Select a fruit",
            invalid = true,
        ) {
            SelectTrigger(Modifier.fillMaxWidth().focusRequester(trigger), accessibilityLabel = "Fruit")
            SelectContent {
                SelectGroup {
                    SelectItem(null, textValue = "Select a fruit")
                    SelectItem("Apple")
                    SelectItem("Banana")
                    SelectItem("Blueberry")
                }
            }
        }
        FieldError("Please select a fruit.")
    }
}
