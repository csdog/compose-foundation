@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.combobox.Combobox
import com.toutiaoyan.compose.ui.components.combobox.ComboboxChip
import com.toutiaoyan.compose.ui.components.combobox.ComboboxChips
import com.toutiaoyan.compose.ui.components.combobox.ComboboxChipsInput
import com.toutiaoyan.compose.ui.components.combobox.ComboboxContent
import com.toutiaoyan.compose.ui.components.combobox.ComboboxEmpty
import com.toutiaoyan.compose.ui.components.combobox.ComboboxGroup
import com.toutiaoyan.compose.ui.components.combobox.ComboboxInput
import com.toutiaoyan.compose.ui.components.combobox.ComboboxItem
import com.toutiaoyan.compose.ui.components.combobox.ComboboxLabel
import com.toutiaoyan.compose.ui.components.combobox.ComboboxList
import com.toutiaoyan.compose.ui.components.combobox.ComboboxSeparator
import com.toutiaoyan.compose.ui.components.combobox.ComboboxTrigger
import com.toutiaoyan.compose.ui.components.combobox.ComboboxValue

val comboboxDoc =
    ComponentDoc(
        id = "combobox",
        title = "Combobox",
        description = "Autocomplete input with a list of suggestions.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.combobox.Combobox
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxContent
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxEmpty
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxInput
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxItem
            import com.toutiaoyan.compose.ui.components.combobox.ComboboxTrigger

            var framework by remember { mutableStateOf<String?>(null) }
            Combobox(
                value = framework,
                options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                onValueChange = { framework = it },
            ) {
                ComboboxTrigger()
                ComboboxContent()
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("value / values", "T? / List<T>", "required"),
                ApiProperty("options", "List<T>", "required"),
                ApiProperty("ComboboxInput.editable", "Boolean", "explicit editable overload"),
                ApiProperty("autoHighlight", "Boolean", "false"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("onClear", "callback?", "null"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(value = framework, options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"), onValueChange = { framework = it }, placeholder = "Select a framework") {
                            ComboboxInput(editable = true)
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options =
                                listOf(
                                    "Next.js",
                                    "SvelteKit",
                                    "Nuxt.js",
                                    "Remix",
                                    "Astro",
                                ),
                            onValueChange = {
                                framework = it
                            },
                            placeholder = "Select a framework",
                        ) {
                            ComboboxInput(editable = true)
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple",
                    code =
                        """
                        var selected by remember {
                            mutableStateOf(listOf("Next.js", "Remix"))
                        }
                        Combobox(
                            values = selected,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValuesChange = { selected = it },
                        ) {
                            ComboboxChips {
                                selected.forEach { item ->
                                    ComboboxChip(value = item, label = item)
                                }
                                ComboboxChipsInput(placeholder = "Add framework")
                            }
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var selected by remember {
                            mutableStateOf(listOf("Next.js", "Remix"))
                        }
                        Combobox(
                            values = selected,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValuesChange = { selected = it },
                        ) {
                            ComboboxChips {
                                selected.forEach { item ->
                                    ComboboxChip(value = item, label = item)
                                }
                                ComboboxChipsInput(placeholder = "Add framework")
                            }
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Clear button",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>("Next.js") }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            onClear = { framework = null },
                        ) {
                            ComboboxTrigger(showClear = true)
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>("Next.js") }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            onClear = { framework = null },
                        ) {
                            ComboboxTrigger(showClear = true)
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Without trigger",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(showTrigger = false)
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(showTrigger = false)
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom items",
                    code =
                        """
                        val frameworks = listOf("next" to "Next.js", "nuxt" to "Nuxt", "remix" to "Remix")
                        var selected by remember { mutableStateOf<Pair<String, String>?>(null) }
                        Combobox(
                            value = selected,
                            options = frameworks,
                            onValueChange = { selected = it },
                            itemToStringValue = { it.second },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        val frameworks = listOf("next" to "Next.js", "nuxt" to "Nuxt", "remix" to "Remix")
                        var selected by remember { mutableStateOf<Pair<String, String>?>(null) }
                        Combobox(
                            value = selected,
                            options = frameworks,
                            onValueChange = { selected = it },
                            itemToStringValue = { it.second },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Auto highlight",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            autoHighlight = true,
                            defaultQuery = "n",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            autoHighlight = true,
                            defaultQuery = "n",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultOpen = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultOpen = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultOpen = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultOpen = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default query",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "",
                            defaultOpen = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "",
                            defaultOpen = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom icon",
                    code =
                        """
                        // Combobox's trigger chevron is a plain glyph by default (this
                        // library takes no icon-library dependency) -- override the icon slot
                        // with a real vector from any icon set, e.g. Lucide.
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(icon = { DocIcon(Lucide.ChevronDown) })
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(icon = { DocIcon(Lucide.ChevronDown) })
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Groups",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup(heading = "Frontend") {
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                    ComboboxGroup(heading = "Backend") {
                                        ComboboxItem("Nuxt.js", "Nuxt.js")
                                        ComboboxItem("Remix", "Remix")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup(heading = "Frontend") {
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                    ComboboxGroup(heading = "Backend") {
                                        ComboboxItem("Nuxt.js", "Nuxt.js")
                                        ComboboxItem("Remix", "Remix")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit", disabled = true)
                                    ComboboxItem("Remix", "Remix")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit", disabled = true)
                                    ComboboxItem("Remix", "Remix")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            disabled = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit", disabled = false)
                                    ComboboxItem("Remix", "Remix")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            disabled = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit", disabled = false)
                                    ComboboxItem("Remix", "Remix")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Empty",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            invalid = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            invalid = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            invalid = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            invalid = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom filter",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            defaultQuery = "Nu",
                            filter = { query, value -> value.startsWith(query, ignoreCase = true) },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            defaultQuery = "Nu",
                            filter = { query, value -> value.startsWith(query, ignoreCase = true) },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "No filter",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                            shouldFilter = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                            shouldFilter = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default filter",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            shouldFilter = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            shouldFilter = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default should filter",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            shouldFilter = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            shouldFilter = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Keywords",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            keywords = { if (it == "Next.js") listOf("react") else emptyList() },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            keywords = { if (it == "Next.js") listOf("react") else emptyList() },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled trigger",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            disabled = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            disabled = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-modal",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            modal = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                            onValueChange = { framework = it },
                            modal = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Modal",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            modal = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            modal = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default modal",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            modal = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            modal = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Required",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            required = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            required = true,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default required",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            required = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                            required = false,
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Prevent item select",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit", onSelect = { true })
                                    ComboboxItem("Nuxt.js", "Nuxt.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit", onSelect = { true })
                                    ComboboxItem("Nuxt.js", "Nuxt.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom text value",
                    code =
                        """
                        var size by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = size,
                            options = listOf("sm", "md", "lg"),
                            onValueChange = { size = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("sm", "sm", textValue = "Small")
                                    ComboboxItem("md", "md", textValue = "Medium")
                                    ComboboxItem("lg", "lg", textValue = "Large")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var size by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = size,
                            options = listOf("sm", "md", "lg"),
                            onValueChange = { size = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("sm", "sm", textValue = "Small")
                                    ComboboxItem("md", "md", textValue = "Medium")
                                    ComboboxItem("lg", "lg", textValue = "Large")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Value",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger { ComboboxValue() }
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger { ComboboxValue() }
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Label",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup {
                                        ComboboxLabel("Frameworks")
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup {
                                        ComboboxLabel("Frameworks")
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxSeparator()
                                    ComboboxItem("SvelteKit", "SvelteKit")
                                    ComboboxItem("Remix", "Remix")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxSeparator()
                                    ComboboxItem("SvelteKit", "SvelteKit")
                                    ComboboxItem("Remix", "Remix")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Empty text",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                            emptyText = "No results found.",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxEmpty()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                            emptyText = "No results found.",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxEmpty()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default empty text",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                            emptyText = "No results found.",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxEmpty()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                            emptyText = "No results found.",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxEmpty()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Empty slot",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxEmpty()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                            defaultQuery = "xyzzy",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxEmpty()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Input",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Input icon",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(icon = { DocIcon(Lucide.Search) })
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(icon = { DocIcon(Lucide.Search) })
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default input icon",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(icon = null)
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(icon = null)
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Placeholder",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(placeholder = "Filter...")
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(placeholder = "Filter...")
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placeholder",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            placeholder = "Select...",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            placeholder = "Select...",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled input",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(disabled = true)
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput(disabled = true)
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Search placeholder",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            searchPlaceholder = "Search frameworks...",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            searchPlaceholder = "Search frameworks...",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default search placeholder",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            searchPlaceholder = "Search...",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit", "Remix"),
                            onValueChange = { framework = it },
                            searchPlaceholder = "Search...",
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "List",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup {
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup {
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group heading",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup(heading = "Frameworks") {
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxGroup(heading = "Frameworks") {
                                        ComboboxItem("Next.js", "Next.js")
                                        ComboboxItem("SvelteKit", "SvelteKit")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Icon",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default icon",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(accessibilityLabel = "Framework")
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(accessibilityLabel = "Framework")
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default accessibility label",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(accessibilityLabel = "")
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger(accessibilityLabel = "")
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                    ComboboxItem("SvelteKit", "SvelteKit")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent(dismissOnClickOutside = true) {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent(dismissOnClickOutside = true) {
                                ComboboxInput()
                                ComboboxList {
                                    ComboboxItem("Next.js", "Next.js")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent(sideOffset = 6.dp)
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent(sideOffset = 6.dp)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent(sideOffset = 6.dp)
                        }
                        """.trimIndent(),
                    preview = {
                        var framework by remember { mutableStateOf<String?>(null) }
                        Combobox(
                            value = framework,
                            options = listOf("Next.js", "SvelteKit"),
                            onValueChange = { framework = it },
                        ) {
                            ComboboxTrigger()
                            ComboboxContent(sideOffset = 6.dp)
                        }
                    },
                ),
            ),
    )
