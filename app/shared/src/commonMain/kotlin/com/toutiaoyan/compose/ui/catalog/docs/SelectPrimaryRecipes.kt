@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.select.*

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
        "Europe & Africa" to
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
        "Australia & Pacific" to
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

private val selectRtlTranslations =
    listOf(
        listOf(
            "Select a fruit",
            "Fruits",
            "Apple",
            "Banana",
            "Blueberry",
            "Grapes",
            "Pineapple",
            "Vegetables",
            "Carrot",
            "Broccoli",
            "Spinach",
        ),
        listOf(
            "اختر فاكهة",
            "الفواكه",
            "تفاح",
            "موز",
            "توت أزرق",
            "عنب",
            "أناناس",
            "الخضروات",
            "جزر",
            "بروكلي",
            "سبانخ",
        ),
        listOf("בחר פרי", "פירות", "תפוח", "בננה", "אוכמניה", "ענבים", "אננס", "ירקות", "גזר", "ברוקולי", "תרד"),
    )

@Composable
internal fun SelectGroupsPreview() {
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
}

@Composable
internal fun SelectScrollablePreview() {
    var zone by remember { mutableStateOf<String?>(null) }
    Select(value = zone, onValueChange = { zone = it }, placeholder = "Select a timezone") {
        SelectTrigger(Modifier.width(256.dp))
        SelectContent {
            timeZoneGroups.forEach { (group, zones) ->
                SelectGroup {
                    SelectLabel(group)
                    zones.forEach { SelectItem(it) }
                }
            }
        }
    }
}

@Composable
internal fun SelectDisabledPreview() {
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
                SelectItem("Blueberry")
                SelectItem("Grapes", disabled = true)
                SelectItem("Pineapple")
            }
        }
    }
}

@Composable
internal fun SelectRtlContent(language: Int = 1) {
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
    ) {
        val t = selectRtlTranslations[language]
        var fruit by remember(language) { mutableStateOf<String?>(null) }
        Select(value = fruit, onValueChange = { fruit = it }, placeholder = t[0]) {
            SelectTrigger(Modifier.width(128.dp))
            SelectContent {
                SelectGroup {
                    SelectLabel(t[1])
                    SelectItem(t[2])
                    SelectItem(t[3])
                    SelectItem(t[4])
                    SelectItem(t[5])
                    SelectItem(t[6])
                }
                SelectSeparator()
                SelectGroup {
                    SelectLabel(t[7])
                    SelectItem(t[8])
                    SelectItem(t[9])
                    SelectItem(t[10])
                }
            }
        }
    }
}
