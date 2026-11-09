@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bold
import com.composables.icons.lucide.Italic
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Underline
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroup
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupDefaults
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupItem
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupOrientation

private val toggleGroupRtlLabels =
    listOf(
        listOf("List", "Grid", "Cards"),
        listOf("قائمة", "شبكة", "بطاقات"),
        listOf("רשימה", "רשת", "כרטיסים"),
    )

@Composable
private fun ToggleGroupIconDemo(
    value: Set<String>,
    onValueChange: (Set<String>) -> Unit,
    vertical: Boolean = false,
    size: ToggleSize = ToggleSize.Md,
    spacing: androidx.compose.ui.unit.Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
) {
    ToggleGroup(
        value = value,
        onValueChange = onValueChange,
        variant = ToggleVariant.Outline,
        size = size,
        spacing = spacing,
        disabled = disabled,
        orientation = if (vertical) ToggleGroupOrientation.Vertical else ToggleGroupOrientation.Horizontal,
        modifier = Modifier.testTag("toggle-group-recipe"),
    ) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = "Toggle strikethrough") { Icon(Lucide.Underline) }
    }
}

@Composable
private fun ToggleGroupTextDemo(
    values: List<String>,
    active: Set<String>,
    onValueChange: (Set<String>) -> Unit,
    size: ToggleSize = ToggleSize.Md,
    spacing: androidx.compose.ui.unit.Dp = ToggleGroupDefaults.Spacing,
) {
    ToggleGroup(
        value = active,
        onValueChange = onValueChange,
        variant = ToggleVariant.Outline,
        size = size,
        spacing = spacing,
        modifier = Modifier.testTag("toggle-group-recipe"),
    ) {
        values.forEach { value ->
            ToggleGroupItem(value.lowercase(), label = "Toggle ${value.lowercase()}") { Text { value } }
        }
    }
}

@Composable
private fun ToggleGroupFontWeightDemo() {
    var fontWeight by remember { mutableStateOf("normal") }
    Field {
        FieldLabel("Font Weight")
        ToggleGroup(
            value = fontWeight,
            onValueChange = { fontWeight = it },
            variant = ToggleVariant.Outline,
            spacing = 2.dp,
            size = ToggleSize.Lg,
            modifier = Modifier.testTag("toggle-group-font-weight"),
        ) {
            listOf(
                "light" to FontWeight.Light,
                "normal" to FontWeight.Normal,
                "medium" to FontWeight.Medium,
                "bold" to FontWeight.Bold,
            ).forEach { (value, weight) ->
                ToggleGroupItem(value, label = value.replaceFirstChar { it.uppercase() }) {
                    Column(
                        Modifier.size(64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, fontWeight = weight),
                        ) { "Aa" }
                        Text(textStyle = TextColors.Muted) { value.replaceFirstChar { it.uppercase() } }
                    }
                }
            }
        }
        FieldDescription("Use font-$fontWeight to set the font weight.")
    }
}

private val toggleGroupDefaultCode =
    """
    var value by remember { mutableStateOf(emptySet<String>()) }
    ToggleGroup(value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = "Toggle strikethrough") { Icon(Lucide.Underline) }
    }
    """.trimIndent()

private val toggleGroupOutlineCode =
    """
    var value by remember { mutableStateOf(setOf("all")) }
    ToggleGroup(value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
        ToggleGroupItem("all") { Text { "All" } }
        ToggleGroupItem("missed") { Text { "Missed" } }
    }
    """.trimIndent()

private val toggleGroupSizesCode =
    """
    ToggleGroup(defaultValue = setOf("top"), variant = ToggleVariant.Outline, size = ToggleSize.Sm) {
        listOf("top", "bottom", "left", "right").forEach { value ->
            ToggleGroupItem(value) { Text { value.replaceFirstChar { it.uppercase() } } }
        }
    }
    ToggleGroup(defaultValue = setOf("top"), variant = ToggleVariant.Outline) {
        listOf("top", "bottom", "left", "right").forEach { value ->
            ToggleGroupItem(value) { Text { value.replaceFirstChar { it.uppercase() } } }
        }
    }
    """.trimIndent()

private val toggleGroupSpacingCode =
    """
    ToggleGroup(defaultValue = setOf("top"), variant = ToggleVariant.Outline, size = ToggleSize.Sm, spacing = 2.dp) {
        listOf("top", "bottom", "left", "right").forEach { value ->
            ToggleGroupItem(value) { Text { value.replaceFirstChar { it.uppercase() } } }
        }
    }
    """.trimIndent()

private val toggleGroupVerticalCode =
    """
    var value by remember { mutableStateOf(setOf("bold", "italic")) }
    ToggleGroup(
        value = value,
        onValueChange = { value = it },
        type = ToggleGroupType.Multiple,
        orientation = ToggleGroupOrientation.Vertical,
        spacing = 4.dp,
    ) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = "Toggle underline") { Icon(Lucide.Underline) }
    }
    """.trimIndent()

private val toggleGroupDisabledCode =
    """
    ToggleGroup(defaultValue = setOf("bold"), disabled = true, variant = ToggleVariant.Outline) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = "Toggle strikethrough") { Icon(Lucide.Underline) }
    }
    """.trimIndent()

private val toggleGroupFontWeightCode =
    """
    var fontWeight by remember { mutableStateOf("normal") }
    Field {
        FieldLabel("Font Weight")
        ToggleGroup(value = fontWeight, onValueChange = { fontWeight = it },
            variant = ToggleVariant.Outline, spacing = 2.dp, size = ToggleSize.Lg) {
            listOf("light", "normal", "medium", "bold").forEach { value ->
                ToggleGroupItem(value, label = value.replaceFirstChar { it.uppercase() }) {
                    Column(Modifier.size(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(textStyle = TextStyle(fontSize = 24.sp, fontWeight = fontWeightOf(value))) { "Aa" }
                        Text(textStyle = TextColors.Muted) { value.replaceFirstChar { it.uppercase() } }
                    }
                }
            }
        }
        FieldDescription("Use font-${'$'}fontWeight to set the font weight.")
    }
    """.trimIndent()

private val toggleGroupRtlCode =
    """
    var language by remember { mutableStateOf(1) }
    var value by remember { mutableStateOf(setOf("list")) }
    CompositionLocalProvider(LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl) {
        ToggleGroup(value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
            listOf("list", "grid", "cards").forEach { key ->
                ToggleGroupItem(key, label = key) { Text { translatedLabel(language, key) } }
            }
        }
    }
    """.trimIndent()

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
        compositionCode =
            """
            ToggleGroup
            ├── ToggleGroupItem
            └── ToggleGroupItem
            """.trimIndent(),
        examplesHeading = null,
        apiSections =
            listOf(
                ComponentApiSection(
                    "ToggleGroup",
                    "Connected (spacing 0) or spaced two-state buttons; single or multiple selection.",
                    listOf(
                        ApiProperty("value / defaultValue", "Set<String>? / Set<String>", "null / emptySet()"),
                        ApiProperty("type", "ToggleGroupType", "Multiple"),
                        ApiProperty("variant / size", "ToggleVariant / ToggleSize", "Default / Md"),
                        ApiProperty("spacing", "Dp", "8.dp (2 in CSS units)"),
                        ApiProperty("orientation / disabled", "ToggleGroupOrientation / Boolean", "Horizontal / false"),
                    ),
                ),
                ComponentApiSection(
                    "ToggleGroupItem",
                    "One toggle inside the group; the root owns the selection state.",
                    listOf(
                        ApiProperty("value", "String", "Required"),
                        ApiProperty("label / content", "String / @Composable () -> Unit", "value / Required"),
                        ApiProperty("enabled / disabled", "Boolean", "true / false"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = toggleGroupDefaultCode,
                    preview = {
                        var value by remember { mutableStateOf(emptySet<String>()) }
                        ToggleGroupIconDemo(value, { value = it })
                    },
                ),
                ComponentExample(
                    title = "Outline",
                    code = toggleGroupOutlineCode,
                    preview = {
                        var value by remember { mutableStateOf(setOf("all")) }
                        ToggleGroupTextDemo(listOf("All", "Missed"), value, { value = it })
                    },
                ),
                ComponentExample(
                    title = "Size",
                    descriptionMarkdown = "Use the `size` prop to change the size of the toggle group.",
                    code = toggleGroupSizesCode,
                    preview = {
                        var small by remember { mutableStateOf(setOf("top")) }
                        var medium by remember { mutableStateOf(setOf("top")) }
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            ToggleGroupTextDemo(
                                listOf("Top", "Bottom", "Left", "Right"),
                                small,
                                { small = it },
                                size = ToggleSize.Sm,
                            )
                            ToggleGroupTextDemo(
                                listOf("Top", "Bottom", "Left", "Right"),
                                medium,
                                { medium = it },
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Spacing",
                    descriptionMarkdown = "Use `spacing` to add spacing between toggle group items.",
                    code = toggleGroupSpacingCode,
                    preview = {
                        var value by remember { mutableStateOf(setOf("top")) }
                        ToggleGroupTextDemo(
                            listOf("Top", "Bottom", "Left", "Right"),
                            value,
                            { value = it },
                            size = ToggleSize.Sm,
                            spacing = 2.dp,
                        )
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    descriptionMarkdown = "Use `orientation=\"vertical\"` for vertical toggle groups.",
                    code = toggleGroupVerticalCode,
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold", "italic")) }
                        ToggleGroupIconDemo(value, { value = it }, vertical = true, spacing = 4.dp)
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = toggleGroupDisabledCode,
                    preview = {
                        ToggleGroupIconDemo(setOf("bold"), {}, disabled = true)
                    },
                ),
                ComponentExample(
                    title = "Custom",
                    descriptionMarkdown = "A custom toggle group example.",
                    code = toggleGroupFontWeightCode,
                    preview = { ToggleGroupFontWeightDemo() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown = "To enable RTL support in shadcn/ui, see the RTL configuration guide.",
                    previewPadding = 0.dp,
                    code = toggleGroupRtlCode,
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        var value by remember { mutableStateOf(setOf("list")) }
                        Column(Modifier.fillMaxWidth()) {
                            RtlPreviewToolbar(
                                language,
                                listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
                                { language = it },
                            )
                            CompositionLocalProvider(
                                LocalLayoutDirection provides
                                    if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
                            ) {
                                Column(
                                    Modifier.fillMaxWidth().padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    ToggleGroup(
                                        value = value,
                                        onValueChange = { value = it },
                                        variant = ToggleVariant.Outline,
                                    ) {
                                        listOf("List", "Grid", "Cards").forEachIndexed { index, english ->
                                            val label = toggleGroupRtlLabels[language][index]
                                            ToggleGroupItem(english.lowercase(), label = label) {
                                                Text { label }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Single select",
                    code =
                        """
                        var value by remember { mutableStateOf("italic") }
                        ToggleGroup(value = value, onValueChange = { value = it }) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        // Clicking the active item deselects it; at most one item is active.
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("italic") }
                        ToggleGroup(value = value, onValueChange = { value = it }) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Connected",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(value = value, onValueChange = { value = it }, spacing = 0.dp) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(value = value, onValueChange = { value = it }, spacing = 0.dp) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
            ),
    )
