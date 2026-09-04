@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.collapsible.Collapsible
import com.toutiaoyan.compose.ui.components.collapsible.CollapsibleContent
import com.toutiaoyan.compose.ui.components.collapsible.CollapsibleTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant

val collapsibleDoc =
    ComponentDoc(
        id = "collapsible",
        title = "Collapsible",
        description = "An interactive component which expands/collapses a panel.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.collapsible.Collapsible
            import com.toutiaoyan.compose.ui.components.collapsible.CollapsibleContent
            import com.toutiaoyan.compose.ui.components.collapsible.CollapsibleTrigger

            var open by remember { mutableStateOf(false) }
            Collapsible(open = open, onOpenChange = { open = it }) {
                CollapsibleTrigger(if (open) "Hide details" else "Show details", variant = ButtonVariant.Outline)
                CollapsibleContent("@peduarte starred 3 repositories")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Collapsible(open = open, onOpenChange = { open = it }) {
                            CollapsibleTrigger(if (open) "Hide details" else "Show details", variant = ButtonVariant.Outline)
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Collapsible(open = open, onOpenChange = { open = it }) {
                            CollapsibleTrigger(
                                if (open) "Hide details" else "Show details",
                                variant = ButtonVariant.Outline,
                            )
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                    },
                ),
                ComponentExample(
                    title = "Always-visible siblings",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Collapsible(open = open, onOpenChange = { open = it }) {
                            Row {
                                Text(variant = TextVariant.LabelLarge) {
                                    "@peduarte starred 3 repositories"
                                }
                                CollapsibleTrigger(
                                    if (open) "Hide" else "Show",
                                    variant = ButtonVariant.Ghost,
                                    size = ButtonSize.Sm,
                                )
                            }
                            Text { "@radix-ui/primitives" }
                            CollapsibleContent {
                                Text { "@radix-ui/colors" }
                                Text { "@stitches/react" }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Collapsible(open = open, onOpenChange = { open = it }) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    variant = TextVariant.LabelLarge,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    "@peduarte starred 3 repositories"
                                }
                                CollapsibleTrigger(
                                    if (open) "Hide" else "Show",
                                    variant = ButtonVariant.Ghost,
                                    size = ButtonSize.Sm,
                                )
                            }
                            Text { "@radix-ui/primitives" }
                            CollapsibleContent {
                                Text { "@radix-ui/colors" }
                                Text { "@stitches/react" }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        Collapsible("Hide details", "@peduarte starred 3 repositories", defaultOpen = true)
                        """.trimIndent(),
                    preview = {
                        Collapsible("Hide details", "@peduarte starred 3 repositories", defaultOpen = true)
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Collapsible("Hide details", "@peduarte starred 3 repositories", disabled = true)
                        """.trimIndent(),
                    preview = {
                        Collapsible("Hide details", "@peduarte starred 3 repositories", disabled = true)
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Collapsible("Hide details", "@peduarte starred 3 repositories", disabled = false)
                        """.trimIndent(),
                    preview = {
                        Collapsible("Hide details", "@peduarte starred 3 repositories", disabled = false)
                    },
                ),
                ComponentExample(
                    title = "Keep mounted content",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Not keep mounted",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default keep mounted",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Force mount",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Not force mount",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default force mount",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = false)
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Collapsible {
                            CollapsibleTrigger(
                                if (open) "Hide details" else "Show details",
                                variant = ButtonVariant.Outline,
                            )
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible {
                            CollapsibleTrigger(
                                if (open) "Hide details" else "Show details",
                                variant = ButtonVariant.Outline,
                            )
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Collapsible {
                            CollapsibleTrigger("Show details", variant = ButtonVariant.Outline)
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible {
                            CollapsibleTrigger("Show details", variant = ButtonVariant.Outline)
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Collapsible {
                            CollapsibleTrigger("Show details", variant = ButtonVariant.Outline)
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible {
                            CollapsibleTrigger("Show details", variant = ButtonVariant.Outline)
                            CollapsibleContent("@peduarte starred 3 repositories")
                        }
                    },
                ),
            ),
    )
