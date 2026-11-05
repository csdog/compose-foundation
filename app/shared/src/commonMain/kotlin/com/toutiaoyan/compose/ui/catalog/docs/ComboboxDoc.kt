@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.components.combobox.Combobox
import com.toutiaoyan.compose.ui.components.combobox.ComboboxChip
import com.toutiaoyan.compose.ui.components.combobox.ComboboxChips
import com.toutiaoyan.compose.ui.components.combobox.ComboboxChipsInput
import com.toutiaoyan.compose.ui.components.combobox.ComboboxContent
import com.toutiaoyan.compose.ui.components.combobox.ComboboxEmpty
import com.toutiaoyan.compose.ui.components.combobox.ComboboxGroup
import com.toutiaoyan.compose.ui.components.combobox.ComboboxInput
import com.toutiaoyan.compose.ui.components.combobox.ComboboxItem
import com.toutiaoyan.compose.ui.components.combobox.ComboboxList
import com.toutiaoyan.compose.ui.components.combobox.ComboboxSeparator
import com.toutiaoyan.compose.ui.components.combobox.ComboboxTrigger
import com.toutiaoyan.compose.ui.components.field.FieldLabel

/**
 * Current official page (revision 8a35366):
 * demo, Installation, Usage, Composition (Simple / With chips / With groups and
 * collection), Custom Items, Multiple Selection, then the recipes Basic, Multiple,
 * Clear Button, Groups, Custom Items, Invalid, Disabled, Auto Highlight, Popup,
 * Input Group, RTL, API Reference.
 *
 * The previous local page was a Base UI API dump (48 recipes such as "Default open",
 * "Default required" and "Default side offset"); this round replaces it with the
 * official page structure. Kotlin-only behavior that the official page demonstrates
 * through prose is mapped to the equivalent Compose API in the code blocks.
 */
val comboboxDoc =
    ComponentDoc(
        id = "combobox",
        title = "Combobox",
        description = "Autocomplete input with a list of suggestions.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.combobox.Combobox
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxContent
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxEmpty
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxInput
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxItem
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxList
            """.trimIndent(),
        usageCode =
            """
            val frameworks = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro")

            var framework by remember { mutableStateOf<String?>(null) }
            Combobox(
                value = framework,
                options = frameworks,
                onValueChange = { framework = it },
            ) {
                ComboboxInput(editable = true, placeholder = "Select a framework")
                ComboboxContent {
                    ComboboxEmpty(text = "No items found.")
                    ComboboxList {
                        frameworks.forEach { item ->
                            ComboboxItem(value = item, label = item)
                        }
                    }
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            Simple

            Combobox
            ├── ComboboxInput
            └── ComboboxContent
                ├── ComboboxEmpty
                └── ComboboxList
                    ├── ComboboxItem
                    └── ComboboxItem

            With chips

            Combobox
            ├── ComboboxChips
            │   ├── ComboboxValue
            │   │   └── ComboboxChip
            │   └── ComboboxChipsInput
            └── ComboboxContent
                ├── ComboboxEmpty
                └── ComboboxList
                    ├── ComboboxItem
                    └── ComboboxItem

            With groups and collection

            Combobox
            ├── ComboboxInput
            └── ComboboxContent
                ├── ComboboxEmpty
                └── ComboboxList
                    ├── ComboboxGroup
                    │   ├── ComboboxLabel
                    │   └── ComboboxCollection
                    │       ├── ComboboxItem
                    │       └── ComboboxItem
                    ├── ComboboxSeparator
                    └── ComboboxGroup
                        ├── ComboboxLabel
                        └── ComboboxCollection
                            ├── ComboboxItem
                            └── ComboboxItem
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Custom Items",
                    listOf(
                        ComponentGuideBlock.Paragraph("Use `itemToStringValue` when your items are objects."),
                        ComponentGuideBlock.Code(
                            """
                            import com.toutiaoyan.compose.ui.components.combobox.Combobox
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxContent
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxEmpty
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxInput
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxItem
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxList

                            data class Country(val code: String, val label: String, val continent: String)

                            val countries = listOf(
                                Country("ar", "Argentina", "South America"),
                                Country("au", "Australia", "Oceania"),
                                Country("br", "Brazil", "South America"),
                                Country("ca", "Canada", "North America"),
                                Country("cn", "China", "Asia"),
                                Country("co", "Colombia", "South America"),
                                Country("eg", "Egypt", "Africa"),
                                Country("fr", "France", "Europe"),
                                Country("de", "Germany", "Europe"),
                                Country("it", "Italy", "Europe"),
                                Country("jp", "Japan", "Asia"),
                                Country("ke", "Kenya", "Africa"),
                                Country("mx", "Mexico", "North America"),
                                Country("nz", "New Zealand", "Oceania"),
                                Country("ng", "Nigeria", "Africa"),
                                Country("za", "South Africa", "Africa"),
                                Country("kr", "South Korea", "Asia"),
                                Country("gb", "United Kingdom", "Europe"),
                                Country("us", "United States", "North America"),
                            )

                            var country by remember { mutableStateOf<Country?>(null) }
                            Combobox(
                                value = country,
                                options = countries,
                                onValueChange = { country = it },
                                itemToStringValue = { it.label },
                            ) {
                                ComboboxInput(editable = true, placeholder = "Search countries...")
                                ComboboxContent {
                                    ComboboxEmpty(text = "No countries found.")
                                    ComboboxList {
                                        countries.forEach { item ->
                                            ComboboxItem(value = item, label = item.label) {
                                                Item(
                                                    title = item.label,
                                                    description = "${'$'}{item.continent} (${'$'}{item.code})",
                                                    size = ItemSize.Xs,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            """.trimIndent(),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Multiple Selection",
                    listOf(
                        ComponentGuideBlock.Paragraph("Use `multiple` with chips for multi-select behavior."),
                        ComponentGuideBlock.Code(
                            """
                            import com.toutiaoyan.compose.ui.components.combobox.Combobox
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxChip
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxChips
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxChipsInput
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxContent
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxEmpty
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxItem
                            import com.toutiaoyan.compose.ui.components.combobox.ComboboxList

                            val frameworks = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro")

                            var value by remember { mutableStateOf(listOf<String>()) }
                            Combobox(
                                values = value,
                                options = frameworks,
                                onValuesChange = { value = it },
                            ) {
                                ComboboxChips {
                                    value.forEach { item ->
                                        ComboboxChip(value = item, label = item)
                                    }
                                    ComboboxChipsInput(placeholder = "Add framework")
                                }
                                ComboboxContent {
                                    ComboboxEmpty(text = "No items found.")
                                    ComboboxList {
                                        frameworks.forEach { item ->
                                            ComboboxItem(value = item, label = item)
                                        }
                                    }
                                }
                            }
                            """.trimIndent(),
                        ),
                    ),
                ),
            ),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework")
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { ComboboxBasicPreview() },
                ),
                ComponentExample(
                    title = "Basic",
                    descriptionMarkdown = "A simple combobox with a list of frameworks.",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework")
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { ComboboxBasicPreview() },
                ),
                ComponentExample(
                    title = "Multiple",
                    descriptionMarkdown =
                        "A combobox with multiple selection using `multiple` and `ComboboxChips`.",
                    code =
                        """
                        var selected by remember { mutableStateOf(listOf("Next.js")) }
                        Combobox(
                            values = selected,
                            options = comboboxFrameworks,
                            onValuesChange = { selected = it },
                            autoHighlight = true,
                            modifier = Modifier.widthIn(max = 320.dp),
                        ) {
                            ComboboxChips {
                                selected.forEach { item ->
                                    ComboboxChip(value = item, label = item)
                                }
                                ComboboxChipsInput()
                            }
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var selected by remember { mutableStateOf(listOf("Next.js")) }
                        Combobox(
                            values = selected,
                            options = comboboxFrameworks,
                            onValuesChange = { selected = it },
                            autoHighlight = true,
                            modifier = Modifier.widthIn(max = 320.dp),
                        ) {
                            ComboboxChips {
                                selected.forEach { item ->
                                    ComboboxChip(value = item, label = item)
                                }
                                ComboboxChipsInput()
                            }
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Clear Button",
                    descriptionMarkdown = "Use the `showClear` prop to show a clear button.",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>("Next.js") }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            // Kotlin's single-select value is nullable, so the clear callback
                            // supplies the reset value that Base UI derives from `null`.
                            onClear = { framework = null },
                        ) {
                            ComboboxInput(
                                editable = true,
                                placeholder = "Select a framework",
                                showClear = true,
                            )
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>("Next.js") }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            onClear = { framework = null },
                        ) {
                            ComboboxInput(
                                editable = true,
                                placeholder = "Select a framework",
                                showClear = true,
                            )
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Groups",
                    descriptionMarkdown = "Use `ComboboxGroup` and `ComboboxSeparator` to group items.",
                    code =
                        """
                        var timezone by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = timezone,
                            options = comboboxTimezones.flatMap { it.second },
                            onValueChange = { timezone = it },
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a timezone")
                            ComboboxContent {
                                ComboboxEmpty(text = "No timezones found.")
                                ComboboxList {
                                    comboboxTimezones.forEachIndexed { index, group ->
                                        ComboboxGroup(heading = group.first) {
                                            group.second.forEach { item ->
                                                ComboboxItem(value = item, label = item)
                                            }
                                        }
                                        if (index < comboboxTimezones.lastIndex) ComboboxSeparator()
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var timezone by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = timezone,
                            options = comboboxTimezones.flatMap { it.second },
                            onValueChange = { timezone = it },
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a timezone")
                            ComboboxContent {
                                ComboboxEmpty(text = "No timezones found.")
                                ComboboxList {
                                    comboboxTimezones.forEachIndexed { index, group ->
                                        ComboboxGroup(heading = group.first) {
                                            group.second.forEach { item ->
                                                ComboboxItem(value = item, label = item)
                                            }
                                        }
                                        if (index < comboboxTimezones.lastIndex) ComboboxSeparator()
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom Items",
                    descriptionMarkdown = "You can render a custom component inside `ComboboxItem`.",
                    code =
                        """
                        var country by remember { mutableStateOf<ComboboxCountry?>(null) }
                        Combobox(
                            value = country,
                            options = comboboxCountries,
                            onValueChange = { country = it },
                            itemToStringValue = { it.label },
                        ) {
                            ComboboxInput(editable = true, placeholder = "Search countries...")
                            ComboboxContent {
                                ComboboxEmpty(text = "No countries found.")
                                ComboboxList {
                                    comboboxCountries.forEach { item ->
                                        ComboboxItem(value = item, label = item.label) {
                                            Item(
                                                title = item.label,
                                                description = "${'$'}{item.continent} (${'$'}{item.code})",
                                                size = ItemSize.Xs,
                                                style = Style { contentPadding(0.dp) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { ComboboxCustomItemsPreview() },
                ),
                ComponentExample(
                    title = "Invalid",
                    descriptionMarkdown = "Use the `aria-invalid` prop to make the combobox invalid.",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            invalid = true,
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework")
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            invalid = true,
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework")
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown = "Use the `disabled` prop to disable the combobox.",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            disabled = true,
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework", disabled = true)
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            disabled = true,
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework", disabled = true)
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Auto Highlight",
                    descriptionMarkdown =
                        "Use the `autoHighlight` prop to automatically highlight the first item on filter.",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            autoHighlight = true,
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework")
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = comboboxFrameworks,
                            onValueChange = { framework = it },
                            autoHighlight = true,
                        ) {
                            ComboboxInput(editable = true, placeholder = "Select a framework")
                            ComboboxContent {
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxFrameworks.forEach { item ->
                                        ComboboxItem(value = item, label = item)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Popup",
                    descriptionMarkdown =
                        "You can trigger the combobox from a button or any other component by " +
                            "using the `render` prop. Move the `ComboboxInput` inside the " +
                            "`ComboboxContent`.",
                    code =
                        """
                        var country by remember {
                            mutableStateOf(ComboboxCountry("", "Select country", ""))
                        }
                        Combobox(
                            value = country,
                            options = comboboxCountries,
                            onValueChange = { country = it },
                            itemToStringValue = { it.label },
                        ) {
                            ComboboxTrigger(modifier = Modifier.width(256.dp))
                            ComboboxContent(panelWidth = 256.dp) {
                                ComboboxInput(placeholder = "Search")
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxCountries.forEach { item ->
                                        ComboboxItem(value = item, label = item.label)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var country by remember {
                            mutableStateOf(ComboboxCountry("", "Select country", ""))
                        }
                        Combobox(
                            value = country,
                            options = comboboxCountries,
                            onValueChange = { country = it },
                            itemToStringValue = { it.label },
                        ) {
                            ComboboxTrigger(modifier = Modifier.width(256.dp))
                            ComboboxContent(panelWidth = 256.dp) {
                                ComboboxInput(placeholder = "Search")
                                ComboboxEmpty(text = "No items found.")
                                ComboboxList {
                                    comboboxCountries.forEach { item ->
                                        ComboboxItem(value = item, label = item.label)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Input Group",
                    descriptionMarkdown =
                        "You can add an addon to the combobox by using the " +
                            "`InputGroupAddon` component inside the `ComboboxInput`.",
                    code =
                        """
                        var timezone by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = timezone,
                            options = comboboxTimezones.flatMap { it.second },
                            onValueChange = { timezone = it },
                        ) {
                            ComboboxInput(
                                editable = true,
                                placeholder = "Select a timezone",
                                leading = { DocIcon(Lucide.Globe) },
                            )
                            ComboboxContent(
                                alignOffset = (-28).dp,
                                panelWidth = 240.dp,
                            ) {
                                ComboboxEmpty(text = "No timezones found.")
                                ComboboxList {
                                    comboboxTimezones.forEach { group ->
                                        ComboboxGroup(heading = group.first) {
                                            group.second.forEach { item ->
                                                ComboboxItem(value = item, label = item)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var timezone by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = timezone,
                            options = comboboxTimezones.flatMap { it.second },
                            onValueChange = { timezone = it },
                        ) {
                            ComboboxInput(
                                editable = true,
                                placeholder = "Select a timezone",
                                leading = { DocIcon(Lucide.Globe) },
                            )
                            ComboboxContent(
                                alignOffset = (-28).dp,
                                panelWidth = 240.dp,
                            ) {
                                ComboboxEmpty(text = "No timezones found.")
                                ComboboxList {
                                    comboboxTimezones.forEach { group ->
                                        ComboboxGroup(heading = group.first) {
                                            group.second.forEach { item ->
                                                ComboboxItem(value = item, label = item)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code =
                        """
                        val categories = listOf(
                            "technology", "design", "business", "marketing", "education", "health",
                        )
                        val categoryLabels = mapOf(
                            "technology" to "التكنولوجيا",
                            "design" to "التصميم",
                            "business" to "الأعمال",
                            "marketing" to "التسويق",
                            "education" to "التعليم",
                            "health" to "الصحة",
                        )

                        var selected by remember { mutableStateOf(listOf("technology")) }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Column(Modifier.widthIn(max = 320.dp)) {
                                FieldLabel("الفئات")
                                Combobox(
                                    values = selected,
                                    options = categories,
                                    onValuesChange = { selected = it },
                                    autoHighlight = true,
                                    itemToStringValue = { categoryLabels[it] ?: it },
                                ) {
                                    ComboboxChips {
                                        selected.forEach { item ->
                                            ComboboxChip(
                                                value = item,
                                                label = categoryLabels[item] ?: item,
                                            )
                                        }
                                        ComboboxChipsInput(placeholder = "أضف فئات")
                                    }
                                    ComboboxContent {
                                        ComboboxEmpty(text = "لم يتم العثور على فئات.")
                                        ComboboxList {
                                            categories.forEach { item ->
                                                ComboboxItem(
                                                    value = item,
                                                    label = categoryLabels[item] ?: item,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { ComboboxRtlPreview() },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Combobox",
                    "Autocomplete root. Holds single or multiple value state, the option " +
                        "list, filtering and open state. See the [Base UI](https://base-ui.com/" +
                        "react/components/combobox#api-reference) documentation for more " +
                        "information.",
                    listOf(
                        ApiProperty("value / values", "T? / List<T>", "Required for single / multiple"),
                        ApiProperty("options", "List<T>", "Required"),
                        ApiProperty("onValueChange / onValuesChange", "callback", "Required"),
                        ApiProperty("label / itemToStringValue", "(T) -> String", "toString()"),
                        ApiProperty("placeholder / searchPlaceholder", "String", "Select… / Search…"),
                        ApiProperty("emptyText", "String", "No results found."),
                        ApiProperty("open / defaultOpen", "Boolean? / Boolean", "null / false"),
                        ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                        ApiProperty("autoHighlight / shouldFilter", "Boolean", "false / true"),
                        ApiProperty("filter / keywords", "callback", "case-insensitive contains / emptyList"),
                        ApiProperty("invalid / required / disabled", "Boolean", "false"),
                        ApiProperty("modal / defaultQuery", "Boolean / String", "true / \"\""),
                        ApiProperty("onClear", "(() -> Unit)?", "null (single-select clear callback)"),
                    ),
                ),
                ComponentApiSection(
                    "ComboboxInput / ComboboxContent",
                    "The editable input group or an in-popup search field, plus the " +
                        "anchored list panel.",
                    listOf(
                        ApiProperty("ComboboxInput.editable", "Boolean", "required on the editable overload"),
                        ApiProperty("ComboboxInput.leading", "(@Composable () -> Unit)?", "null"),
                        ApiProperty("ComboboxInput.icon", "(@Composable () -> Unit)?", "null"),
                        ApiProperty("ComboboxInput.showTrigger / showClear", "Boolean", "true / false"),
                        ApiProperty("ComboboxContent.sideOffset / alignOffset", "Dp", "6.dp / 0.dp"),
                        ApiProperty("ComboboxContent.align / panelWidth", "PopupAlign / Dp?", "Start / null"),
                        ApiProperty("ComboboxContent.dismissOnClickOutside", "Boolean", "true"),
                        ApiProperty("ComboboxContent.modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "Chips, groups and items",
                    "Multi-select chips, grouped options and rows. `ComboboxCollection` is a " +
                        "name-parity passthrough because Compose callers already iterate.",
                    listOf(
                        ApiProperty("ComboboxChips / ComboboxChipsInput", "slot composables", "Required inside multiple"),
                        ApiProperty("ComboboxChip.showRemove", "Boolean", "true"),
                        ApiProperty("ComboboxGroup.heading", "String?", "null"),
                        ApiProperty("ComboboxItem.onSelect", "(() -> Boolean)?", "null; return true to prevent"),
                        ApiProperty("ComboboxItem.disabled / textValue", "Boolean / String?", "false / null"),
                        ApiProperty("ComboboxEmpty.text", "String?", "null → root emptyText"),
                    ),
                ),
            ),
    )

private data class ComboboxCountry(
    val code: String,
    val label: String,
    val continent: String,
)

private val comboboxFrameworks = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro")

private val comboboxCountries =
    listOf(
        ComboboxCountry("ar", "Argentina", "South America"),
        ComboboxCountry("au", "Australia", "Oceania"),
        ComboboxCountry("br", "Brazil", "South America"),
        ComboboxCountry("ca", "Canada", "North America"),
        ComboboxCountry("cn", "China", "Asia"),
        ComboboxCountry("co", "Colombia", "South America"),
        ComboboxCountry("eg", "Egypt", "Africa"),
        ComboboxCountry("fr", "France", "Europe"),
        ComboboxCountry("de", "Germany", "Europe"),
        ComboboxCountry("it", "Italy", "Europe"),
        ComboboxCountry("jp", "Japan", "Asia"),
        ComboboxCountry("ke", "Kenya", "Africa"),
        ComboboxCountry("mx", "Mexico", "North America"),
        ComboboxCountry("nz", "New Zealand", "Oceania"),
        ComboboxCountry("ng", "Nigeria", "Africa"),
        ComboboxCountry("za", "South Africa", "Africa"),
        ComboboxCountry("kr", "South Korea", "Asia"),
        ComboboxCountry("gb", "United Kingdom", "Europe"),
        ComboboxCountry("us", "United States", "North America"),
    )

private val comboboxTimezones =
    listOf(
        "Americas" to
            listOf(
                "(GMT-5) New York",
                "(GMT-8) Los Angeles",
                "(GMT-6) Chicago",
                "(GMT-5) Toronto",
                "(GMT-8) Vancouver",
                "(GMT-3) São Paulo",
            ),
        "Europe" to
            listOf(
                "(GMT+0) London",
                "(GMT+1) Paris",
                "(GMT+1) Berlin",
                "(GMT+1) Rome",
                "(GMT+1) Madrid",
                "(GMT+1) Amsterdam",
            ),
        "Asia/Pacific" to
            listOf(
                "(GMT+9) Tokyo",
                "(GMT+8) Shanghai",
                "(GMT+8) Singapore",
                "(GMT+4) Dubai",
                "(GMT+11) Sydney",
                "(GMT+9) Seoul",
            ),
    )

@Composable
private fun ComboboxBasicPreview() {
    var framework by remember { mutableStateOf<String?>(null) }
    Combobox(
        value = framework,
        options = comboboxFrameworks,
        onValueChange = { framework = it },
    ) {
        ComboboxInput(editable = true, placeholder = "Select a framework")
        ComboboxContent {
            ComboboxEmpty(text = "No items found.")
            ComboboxList {
                comboboxFrameworks.forEach { item ->
                    ComboboxItem(value = item, label = item)
                }
            }
        }
    }
}

@Composable
private fun ComboboxCustomItemsPreview() {
    var country by remember { mutableStateOf<ComboboxCountry?>(null) }
    Combobox(
        value = country,
        options = comboboxCountries,
        onValueChange = { country = it },
        itemToStringValue = { it.label },
    ) {
        ComboboxInput(editable = true, placeholder = "Search countries...")
        ComboboxContent {
            ComboboxEmpty(text = "No countries found.")
            ComboboxList {
                comboboxCountries.forEach { item ->
                    ComboboxItem(value = item, label = item.label) {
                        com.toutiaoyan.compose.ui.components.item.Item(
                            title = item.label,
                            description = "${item.continent} (${item.code})",
                            size = com.toutiaoyan.compose.ui.components.item.ItemSize.Xs,
                            style = Style { contentPadding(0.dp) },
                        )
                    }
                }
            }
        }
    }
}

private data class ComboboxRtlText(
    val language: String,
    val direction: LayoutDirection,
    val label: String,
    val placeholder: String,
    val empty: String,
    val categories: Map<String, String>,
)

private val comboboxRtlTexts =
    listOf(
        ComboboxRtlText(
            language = "Arabic (العربية)",
            direction = LayoutDirection.Rtl,
            label = "الفئات",
            placeholder = "أضف فئات",
            empty = "لم يتم العثور على فئات.",
            categories =
                mapOf(
                    "technology" to "التكنولوجيا",
                    "design" to "التصميم",
                    "business" to "الأعمال",
                    "marketing" to "التسويق",
                    "education" to "التعليم",
                    "health" to "الصحة",
                ),
        ),
        ComboboxRtlText(
            language = "English",
            direction = LayoutDirection.Ltr,
            label = "Categories",
            placeholder = "Add categories",
            empty = "No categories found.",
            categories =
                mapOf(
                    "technology" to "Technology",
                    "design" to "Design",
                    "business" to "Business",
                    "marketing" to "Marketing",
                    "education" to "Education",
                    "health" to "Health",
                ),
        ),
        ComboboxRtlText(
            language = "Hebrew (עברית)",
            direction = LayoutDirection.Rtl,
            label = "קטגוריות",
            placeholder = "הוסף קטגוריות",
            empty = "לא נמצאו קטגוריות.",
            categories =
                mapOf(
                    "technology" to "טכנולוגיה",
                    "design" to "עיצוב",
                    "business" to "עסקים",
                    "marketing" to "שיווק",
                    "education" to "חינוך",
                    "health" to "בריאות",
                ),
        ),
    )

@Composable
private fun ComboboxRtlPreview() {
    var language by remember { mutableStateOf(0) }
    val text = comboboxRtlTexts[language]
    val categories = listOf("technology", "design", "business", "marketing", "education", "health")
    var selected by remember { mutableStateOf(listOf("technology")) }
    RtlPreview(language, comboboxRtlTexts.map { it.language }, { language = it }) {
        CompositionLocalProvider(LocalLayoutDirection provides text.direction) {
            Column(
                Modifier.widthIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FieldLabel(text.label)
                Combobox(
                    values = selected,
                    options = categories,
                    onValuesChange = { selected = it },
                    autoHighlight = true,
                    itemToStringValue = { text.categories[it] ?: it },
                ) {
                    ComboboxChips {
                        selected.forEach { item ->
                            ComboboxChip(value = item, label = text.categories[item] ?: item)
                        }
                        ComboboxChipsInput(placeholder = text.placeholder)
                    }
                    ComboboxContent {
                        ComboboxEmpty(text = text.empty)
                        ComboboxList {
                            categories.forEach { item ->
                                ComboboxItem(
                                    value = item,
                                    label = text.categories[item] ?: item,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
