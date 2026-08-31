@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Check
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.alert.Alert
import com.toutiaoyan.compose.ui.components.alert.AlertAction
import com.toutiaoyan.compose.ui.components.alert.AlertDescription
import com.toutiaoyan.compose.ui.components.alert.AlertTitle
import com.toutiaoyan.compose.ui.components.alert.AlertVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant

val alertDoc =
    ComponentDoc(
        id = "alert",
        title = "Alert",
        description = "A short, prominent callout for important information.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.alert.Alert

            Alert(title = "Heads up!", description = "You can add components to your app.")
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Alert(
                                title = "Success! Your changes have been saved.",
                                icon = { DocIcon(Lucide.Check) },
                            )
                            Alert(
                                title = "This Alert has a title and no icon.",
                                description = "No worries though, we've got you covered.",
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Alert(
                                title = "Success! Your changes have been saved.",
                                icon = { DocIcon(Lucide.Check) },
                            )
                            Alert(
                                title = "This Alert has a title and no icon.",
                                description = "No worries though, we've got you covered.",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        Alert(
                            variant = AlertVariant.Default,
                            title = "Heads up!",
                            description = "You can add components to your app.",
                        )
                        """.trimIndent(),
                    preview = {
                        Alert(
                            variant = AlertVariant.Default,
                            title = "Heads up!",
                            description = "You can add components to your app.",
                        )
                    },
                ),
                ComponentExample(
                    title = "Destructive",
                    code =
                        """
                        Alert(
                            variant = AlertVariant.Destructive,
                            title = "Error",
                            description = "Your session has expired. Please log in again.",
                        )
                        """.trimIndent(),
                    preview = {
                        Alert(
                            variant = AlertVariant.Destructive,
                            title = "Error",
                            description = "Your session has expired. Please log in again.",
                        )
                    },
                ),
                ComponentExample(
                    title = "Slots",
                    code =
                        """
                        Alert {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                        """.trimIndent(),
                    preview = {
                        Alert {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Alert {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                        """.trimIndent(),
                    preview = {
                        Alert {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Alert {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                        """.trimIndent(),
                    preview = {
                        Alert {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Action",
                    code =
                        """
                        Alert {
                            AlertTitle("Dark mode is now available")
                            AlertDescription("Enable it under your profile settings to get started.")
                            AlertAction {
                                Button("Enable", onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Alert {
                            AlertTitle("Dark mode is now available")
                            AlertDescription("Enable it under your profile settings to get started.")
                            AlertAction {
                                Button("Enable", onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline)
                            }
                        }
                    },
                ),
            ),
    )
