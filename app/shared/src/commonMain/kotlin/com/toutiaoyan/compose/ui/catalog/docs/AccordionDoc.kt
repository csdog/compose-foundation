@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ChevronDown
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.toutiaoyan.compose.ui.components.accordion.Accordion
import com.toutiaoyan.compose.ui.components.accordion.AccordionContent
import com.toutiaoyan.compose.ui.components.accordion.AccordionItem
import com.toutiaoyan.compose.ui.components.accordion.AccordionOrientation
import com.toutiaoyan.compose.ui.components.accordion.AccordionTrigger
import com.toutiaoyan.compose.ui.components.accordion.AccordionType

val accordionDoc =
    ComponentDoc(
        id = "accordion",
        title = "Accordion",
        description = "A vertically-stacked set of collapsible sections.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.accordion.Accordion
            import com.toutiaoyan.compose.ui.components.accordion.AccordionContent
                        import com.toutiaoyan.compose.ui.components.accordion.AccordionItem
            import com.toutiaoyan.compose.ui.components.accordion.AccordionTrigger

            var value by remember { mutableStateOf(setOf("item-1")) }
            Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom icon",
                    code =
                        """
                        // Accordion's chevron is a plain glyph by default (this library
                        // takes no icon-library dependency) -- override the icon slot with a
                        // real vector from any icon set, e.g. Lucide. The slot
                        // receives isOpen so the override can drive its own rotation too.
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            value = value,
                            onValueChange = { value = it },
                            icon = { isOpen ->
                                val rotation by animateFloatAsState(if (isOpen) 180f else 0f)
                                DocIcon(Lucide.ChevronDown, modifier = Modifier.rotate(rotation))
                            },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            value = value,
                            onValueChange = { value = it },
                            icon = { isOpen ->
                                val rotation by animateFloatAsState(if (isOpen) 180f else 0f)
                                DocIcon(Lucide.ChevronDown, modifier = Modifier.rotate(rotation))
                            },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(type = AccordionType.Multiple, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(type = AccordionType.Multiple, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Single",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default type",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default multiple",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            multiple = null,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            multiple = null,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Not collapsible",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = false,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = false,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Collapsible",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default collapsible",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem(
                                "item-2",
                                "Is it disabled?",
                                "This item cannot be opened.",
                                disabled = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem(
                                "item-2",
                                "Is it disabled?",
                                "This item cannot be opened.",
                                disabled = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Accordion(defaultValue = "item-1") {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem(
                                "item-2",
                                "Is it styled?",
                                "Yes. It comes with default styles.",
                                disabled = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = "item-1") {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem(
                                "item-2",
                                "Is it styled?",
                                "Yes. It comes with default styles.",
                                disabled = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default value",
                    code =
                        """
                        Accordion(defaultValue = "item-1") {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = "item-1") {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Horizontal,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Horizontal,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "No loop",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = false,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = false,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Loop",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default loop",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled accordion",
                    code =
                        """
                        Accordion(type = AccordionType.Single, defaultValue = setOf("item-1"), disabled = true) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(
                            type = AccordionType.Single,
                            defaultValue = setOf("item-1"),
                            disabled = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled trigger",
                    code =
                        """
                        Accordion(type = AccordionType.Single, defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.")
                            }
                            AccordionItem("item-2") {
                                AccordionTrigger("Disabled?", disabled = true)
                                AccordionContent("This trigger cannot be opened.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(type = AccordionType.Single, defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.")
                            }
                            AccordionItem("item-2") {
                                AccordionTrigger("Disabled?", disabled = true)
                                AccordionContent("This trigger cannot be opened.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled value",
                    code =
                        """
                        var value by remember { mutableStateOf("item-1") }
                        Accordion(value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("item-1") }
                        Accordion(value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple flag",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(multiple = true, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(multiple = true, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Keep mounted",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Not keep mounted",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default keep mounted",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Force mount",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Not force mount",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default force mount",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                    },
                ),
            ),
    )
