@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import com.toutiaoyan.compose.ui.components.text.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

private val fruitItems = listOf("Apple", "Banana", "Blueberry", "Grapes", "Pineapple")

private val timeZoneGroups =
    listOf(
        "North America" to
            listOf(
                "Eastern Standard Time",
                "Central Standard Time",
                "Mountain Standard Time",
                "Pacific Standard Time",
                "Alaska Standard Time",
                "Hawaii Standard Time",
            ),
        "Europe / Africa" to
            listOf(
                "Greenwich Mean Time",
                "Central European Time",
                "Eastern European Time",
                "Western European Summer Time",
                "Central Africa Time",
                "East Africa Time",
            ),
        "Asia" to
            listOf(
                "Moscow Time",
                "India Standard Time",
                "China Standard Time",
                "Japan Standard Time",
                "Korea Standard Time",
                "Indonesia Central Standard Time",
            ),
        "Australia / Pacific" to
            listOf(
                "Australian Western Standard Time",
                "Australian Central Standard Time",
                "Australian Eastern Standard Time",
                "New Zealand Standard Time",
                "Fiji Time",
            ),
        "South America" to
            listOf(
                "Argentina Time",
                "Bolivia Time",
                "Brasilia Time",
                "Chile Standard Time",
            ),
    )

internal val selectAlignItemExample =
    ComponentExample(
        title = "Align Item With Trigger",
        descriptionMarkdown =
            (
                "Use `alignItemWithTrigger` on `SelectContent` to control whether the selected " +
                    "item aligns with the trigger. When `true` (default), the popup positions so " +
                    "the selected item appears over the trigger. When `false`, the popup aligns " +
                    "to the trigger edge."
            ),
        code =
            """
            var alignItem by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text { "Align Item" }
                    Switch(checked = alignItem, onCheckedChange = { alignItem = it })
                }
                var fruit by remember { mutableStateOf<String?>("Banana") }
                Select(value = fruit, onValueChange = { fruit = it }) {
                    SelectTrigger(Modifier.width(192.dp))
                    SelectContent(alignItemWithTrigger = alignItem) {
                        SelectGroup { fruitItems.forEach { SelectItem(it) } }
                    }
                }
            }
            """.trimIndent(),
        preview = {
            var alignItem by remember { mutableStateOf(true) }
            var fruit by remember { mutableStateOf<String?>("Banana") }
            Column(
                Modifier.widthIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text { "Align Item" }
                    Switch(checked = alignItem, onCheckedChange = { alignItem = it })
                }
                Select(value = fruit, onValueChange = { fruit = it }) {
                    SelectTrigger(Modifier.width(192.dp))
                    SelectContent(alignItemWithTrigger = alignItem) {
                        SelectGroup { fruitItems.forEach { SelectItem(it) } }
                    }
                }
            }
        },
    )

internal val selectGroupsExample =
    ComponentExample(
        title = "Groups",
        descriptionMarkdown = "Use `SelectGroup`, `SelectLabel`, and `SelectSeparator` to organize items.",
        code =
            """
            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit") {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    SelectGroup {
                        SelectLabel("Fruits")
                        SelectItem("Apple")
                        SelectItem("Banana")
                        SelectItem("Blueberry")
                    }
                    SelectSeparator()
                    SelectGroup {
                        SelectLabel("Vegetables")
                        SelectItem("Carrot")
                        SelectItem("Broccoli")
                        SelectItem("Spinach")
                    }
                }
            }
            """.trimIndent(),
        preview = {
            var fruit by remember { mutableStateOf<String?>(null) }
            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit") {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    SelectGroup {
                        SelectLabel("Fruits")
                        SelectItem("Apple")
                        SelectItem("Banana")
                        SelectItem("Blueberry")
                    }
                    SelectSeparator()
                    SelectGroup {
                        SelectLabel("Vegetables")
                        SelectItem("Carrot")
                        SelectItem("Broccoli")
                        SelectItem("Spinach")
                    }
                }
            }
        },
    )

internal val selectScrollableExample =
    ComponentExample(
        title = "Scrollable",
        descriptionMarkdown = "A select with many items that scrolls.",
        code =
            """
            Select(value = zone, onValueChange = { zone = it }, placeholder = "Select a timezone") {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    timeZoneGroups.forEachIndexed { index, (group, zones) ->
                        if (index > 0) SelectSeparator()
                        SelectGroup {
                            SelectLabel(group)
                            zones.forEach { SelectItem(it) }
                        }
                    }
                }
            }
            """.trimIndent(),
        preview = {
            var zone by remember { mutableStateOf<String?>(null) }
            Select(value = zone, onValueChange = { zone = it }, placeholder = "Select a timezone") {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    timeZoneGroups.forEachIndexed { index, (group, zones) ->
                        if (index > 0) SelectSeparator()
                        SelectGroup {
                            SelectLabel(group)
                            zones.forEach { SelectItem(it) }
                        }
                    }
                }
            }
        },
    )

internal val selectDisabledExample =
    ComponentExample(
        title = "Disabled",
        code =
            """
            Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit", disabled = true) {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    SelectGroup {
                        SelectItem("Apple")
                        SelectItem("Banana")
                        SelectItem("Grapes", disabled = true)
                        SelectItem("Pineapple")
                    }
                }
            }
            """.trimIndent(),
        preview = {
            var fruit by remember { mutableStateOf<String?>(null) }
            Select(
                value = fruit,
                onValueChange = { fruit = it },
                placeholder = "Select a fruit",
                disabled = true,
            ) {
                SelectTrigger(Modifier.width(192.dp))
                SelectContent {
                    SelectGroup {
                        SelectItem("Apple")
                        SelectItem("Banana")
                        SelectItem("Grapes", disabled = true)
                        SelectItem("Pineapple")
                    }
                }
            }
        },
    )

internal val selectInvalidExample =
    ComponentExample(
        title = "Invalid",
        descriptionMarkdown =
            (
                "Add the `data-invalid` attribute to the `Field` component and the `aria-invalid` " +
                    "attribute to the `SelectTrigger` component to show an error state. In Compose, " +
                    "`Field(invalid = true)` and `Select(invalid = true)` expose the same state."
            ),
        code =
            """
            Field(label = "Fruit", invalid = true) {
                Select(value = fruit, onValueChange = { fruit = it }, placeholder = "Select a fruit", invalid = true) {
                    SelectTrigger(Modifier.width(192.dp))
                    SelectContent { SelectItem("Apple") ; SelectItem("Banana") }
                }
            }
            """.trimIndent(),
        preview = {
            var fruit by remember { mutableStateOf<String?>(null) }
            Column(Modifier.widthIn(max = 320.dp)) {
                Field(label = "Fruit", invalid = true) {
                    Select(
                        value = fruit,
                        onValueChange = { fruit = it },
                        placeholder = "Select a fruit",
                        invalid = true,
                    ) {
                        SelectTrigger(Modifier.width(192.dp))
                        SelectContent {
                            SelectItem("Apple")
                            SelectItem("Banana")
                        }
                    }
                }
            }
        },
    )

private val selectRtlTranslations =
    listOf(
        listOf("Select a fruit", "Fruits", "Apple", "Banana", "Blueberry"),
        listOf("اختر فاكهة", "الفواكه", "تفاح", "موز", "عنب"),
        listOf("בחר פרי", "פירות", "תפוח", "בננה", "ענב"),
    )

internal val selectRtlExample =
    ComponentExample(
        title = "RTL",
        descriptionMarkdown =
            "To enable RTL support in shadcn/ui, see the " +
                "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
        previewPadding = 0.dp,
        code =
            """
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Theme(fontFamily = catalogArabicFontFamily()) {
                    var fruit by remember { mutableStateOf<String?>(null) }
                    Select(value = fruit, onValueChange = { fruit = it }, placeholder = "اختر فاكهة") {
                        SelectTrigger(Modifier.width(192.dp))
                        SelectContent {
                            SelectGroup {
                                SelectLabel("الفواكه")
                                SelectItem("تفاح")
                                SelectItem("موز")
                                SelectItem("عنب")
                            }
                        }
                    }
                }
            }
            """.trimIndent(),
        preview = { SelectRtlRecipe() },
    )

@Composable
private fun SelectRtlRecipe() {
    var language by remember { mutableIntStateOf(1) }
    val inherited = currentTheme
    RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
        val font =
            when (language) {
                1 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Theme(
                preset = inherited.activePreset,
                baseColor = inherited.activeBaseColor,
                accent = inherited.activeAccent,
                isDark = inherited.isDark,
                baseRadius = inherited.baseRadius,
                ring = inherited.ring,
                unitSpacing = LocalUnitSpacing.current,
                minTouchTarget = inherited.minTouchTarget,
                fontFamily = font,
            ) {
                val t = selectRtlTranslations[language]
                var fruit by remember(language) { mutableStateOf<String?>(null) }
                Select(value = fruit, onValueChange = { fruit = it }, placeholder = t[0]) {
                    SelectTrigger(Modifier.width(192.dp))
                    SelectContent {
                        SelectGroup {
                            SelectLabel(t[1])
                            SelectItem(t[2])
                            SelectItem(t[3])
                            SelectItem(t[4])
                        }
                    }
                }
            }
        }
    }
}

/** Official seven sections first, then the existing Compose-only examples. */
internal fun selectPageExamples(native: List<ComponentExample>): List<ComponentExample> {
    val byTitle = native.associateBy { it.title }
    val promoted = setOf("Default", "Disabled", "Invalid")
    return listOf(
        byTitle.getValue("Default"),
        selectAlignItemExample,
        selectGroupsExample,
        selectScrollableExample,
        selectDisabledExample,
        selectInvalidExample,
        selectRtlExample,
    ) + native
        .filterNot { it.title in promoted }
        .map { it.copy(title = "Kotlin extension: " + it.title) }
}
