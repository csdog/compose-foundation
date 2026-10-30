@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroup
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupDefaults
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupItem
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupOrientation
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupType

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Bold
import com.composables.icons.lucide.Italic
import com.composables.icons.lucide.Underline
import com.toutiaoyan.compose.ui.components.icon.Icon

val toggleGroupDoc =
    ComponentDoc(
        id = "toggle-group",
        title = "Toggle Group",
        description = "A set of two-state buttons that can be toggled on or off.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroup
            import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupItem

            var value by remember { mutableStateOf(setOf("bold")) }
            ToggleGroup(value = value, onValueChange = { value = it }) {
                ToggleGroupItem("bold", "B")
                ToggleGroupItem("italic", "I")
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("value", "Set<String>?", "null"),
                ApiProperty("type", "ToggleGroupType", "Multiple"),
                ApiProperty("variant", "ToggleVariant", "Default"),
                ApiProperty("size", "ToggleSize", "Md"),
                ApiProperty("spacing", "Dp", "8.dp"),
                ApiProperty("orientation", "ToggleGroupOrientation", "Horizontal"),
                ApiProperty("disabled", "Boolean", "false"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var value by remember { mutableStateOf(emptySet<String>()) }
                        ToggleGroup(value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
                            ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
                            ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
                            ToggleGroupItem("underline", label = "Toggle strikethrough") { Icon(Lucide.Underline) }
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(emptySet<String>()) }
                        ToggleGroup(value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
                            ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
                            ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
                            ToggleGroupItem("underline", label = "Toggle strikethrough") { Icon(Lucide.Underline) }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Default,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Default,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Outline",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("italic")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Outline,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("italic")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Outline,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Single",
                    code =
                        """
                        var value by remember { mutableStateOf("bold") }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("bold") }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            type = ToggleGroupType.Multiple,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            type = ToggleGroupType.Multiple,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default type",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            type = ToggleGroupType.Multiple,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            type = ToggleGroupType.Multiple,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(value = value, onValueChange = { value = it }) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I", disabled = true)
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(value = value, onValueChange = { value = it }) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I", disabled = true)
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(value = value, onValueChange = { value = it }, disabled = false) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I", disabled = false)
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(value = value, onValueChange = { value = it }, disabled = false) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I", disabled = false)
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Sizes",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            size = ToggleSize.Sm,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            size = ToggleSize.Sm,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default size",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            size = ToggleSize.Md,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            size = ToggleSize.Md,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            orientation = ToggleGroupOrientation.Vertical,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            orientation = ToggleGroupOrientation.Vertical,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            orientation = ToggleGroupOrientation.Horizontal,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            orientation = ToggleGroupOrientation.Horizontal,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            orientation = ToggleGroupOrientation.Horizontal,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            orientation = ToggleGroupOrientation.Horizontal,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Spacing",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Outline,
                            spacing = ToggleGroupDefaults.Spacing,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Outline,
                            spacing = ToggleGroupDefaults.Spacing,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default spacing",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Outline,
                            spacing = 0.dp,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            value = value,
                            onValueChange = { value = it },
                            variant = ToggleVariant.Outline,
                            spacing = 0.dp,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        ToggleGroup(defaultValue = "bold") {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        ToggleGroup(defaultValue = "bold") {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled group",
                    code =
                        """
                        ToggleGroup(defaultValue = "bold", disabled = true) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        ToggleGroup(defaultValue = "bold", disabled = true) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        ToggleGroup(defaultValue = "bold") {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                        }
                        """.trimIndent(),
                    preview = {
                        ToggleGroup(defaultValue = "bold") {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                        }
                    },
                ),
            ),
    )
