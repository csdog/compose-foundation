@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardAction
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardDefaults
import com.toutiaoyan.compose.ui.components.card.CardDescription
import com.toutiaoyan.compose.ui.components.card.CardFooter
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardSize
import com.toutiaoyan.compose.ui.components.card.CardTitle
import com.toutiaoyan.compose.ui.components.card.CardVariant
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.input.Input

val cardDoc =
    ComponentDoc(
        id = "card",
        title = "Card",
        description =
            "Displays content in a bordered container. Compose with CardHeader / CardTitle / " +
                "CardDescription / CardContent / CardFooter.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.card.Card
            import com.toutiaoyan.compose.ui.components.card.CardContent
            import com.toutiaoyan.compose.ui.components.card.CardDescription
            import com.toutiaoyan.compose.ui.components.card.CardHeader
            import com.toutiaoyan.compose.ui.components.card.CardTitle

            Card {
                CardHeader("Account", description = "Manage your settings")
                CardContent("Card body content")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Compound",
                    code =
                        """
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader("Account", description = "Manage your settings")
                            CardContent("Card body content")
                            CardFooter {
                                Button("Save", onClick = {}, size = ButtonSize.Sm)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader("Account", description = "Manage your settings")
                            CardContent("Card body content")
                            CardFooter {
                                Button("Save", onClick = {}, size = ButtonSize.Sm)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Create an account",
                    code =
                        """
                        var email by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader(
                                "Create an account",
                                description = "Enter your email below to create your account",
                            )
                            CardContent {
                                FieldGroup {
                                    Field("Email", required = true) {
                                        Input(
                                            value = email,
                                            onValueChange = { email = it },
                                            placeholder = "m@example.com",
                                        )
                                    }
                                    Field("Password", required = true) {
                                        Input(value = password, onValueChange = { password = it })
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Button("Create account", onClick = {}, modifier = Modifier.fillMaxWidth())
                                Button(
                                    "Sign up with Google",
                                    onClick = {},
                                    variant = ButtonVariant.Outline,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var email by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader(
                                "Create an account",
                                description = "Enter your email below to create your account",
                            )
                            CardContent {
                                FieldGroup {
                                    Field("Email", required = true) {
                                        Input(
                                            value = email,
                                            onValueChange = { email = it },
                                            placeholder = "m@example.com",
                                        )
                                    }
                                    Field("Password", required = true) {
                                        Input(value = password, onValueChange = { password = it })
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Button("Create account", onClick = {}, modifier = Modifier.fillMaxWidth())
                                Button(
                                    "Sign up with Google",
                                    onClick = {},
                                    variant = ButtonVariant.Outline,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Filled",
                    code =
                        """
                        Card(variant = CardVariant.Filled) {
                            Text { "Filled card" }
                        }
                        """.trimIndent(),
                    preview = { Card(variant = CardVariant.Filled) { Text { "Filled card" } } },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        Card(variant = CardVariant.Default) {
                            Text { "Default card" }
                        }
                        """.trimIndent(),
                    preview = { Card(variant = CardVariant.Default) { Text { "Default card" } } },
                ),
                ComponentExample(
                    title = "Default size",
                    code =
                        """
                        Card(size = CardSize.Default) {
                            CardHeader("Default card", description = "Default padding.")
                        }
                        """.trimIndent(),
                    preview = {
                        Card(size = CardSize.Default) {
                            CardHeader("Default card", description = "Default padding.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Size",
                    code =
                        """
                        Card(size = CardSize.Sm) {
                            CardHeader("Small card", description = "Tighter section gap than the default size.")
                        }
                        """.trimIndent(),
                    preview = {
                        Card(size = CardSize.Sm) {
                            CardHeader("Small card", description = "Tighter section gap than the default size.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Spacing",
                    code =
                        """
                        Card(spacing = 32.dp, modifier = Modifier.width(320.dp)) {
                            CardHeader("Terms of Service", description = "Official --card-spacing overlay.")
                            CardContent("Gap and inset share one token when spacing is set.")
                        }
                        """.trimIndent(),
                    preview = {
                        Card(spacing = 32.dp, modifier = Modifier.width(320.dp)) {
                            CardHeader("Terms of Service", description = "Official --card-spacing overlay.")
                            CardContent("Gap and inset share one token when spacing is set.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Small spacing",
                    code =
                        """
                        Card(spacing = CardDefaults.SmSpacing, modifier = Modifier.width(320.dp)) {
                            CardHeader("Small Card", description = "Official size=sm --card-spacing (12dp).")
                            CardContent("Use this when you want compact inset, not only a tighter gap.")
                        }
                        """.trimIndent(),
                    preview = {
                        Card(spacing = CardDefaults.SmSpacing, modifier = Modifier.width(320.dp)) {
                            CardHeader("Small Card", description = "Official size=sm --card-spacing (12dp).")
                            CardContent("Use this when you want compact inset, not only a tighter gap.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Action",
                    code =
                        """
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                                CardAction {
                                    Button("Edit", onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline)
                                }
                            }
                            CardContent("Card body content")
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                                CardAction {
                                    Button("Edit", onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline)
                                }
                            }
                            CardContent("Card body content")
                        }
                    },
                ),
                ComponentExample(
                    title = "Footer",
                    code =
                        """
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                            CardContent("Card body content")
                            CardFooter {
                                Button("Cancel", onClick = {}, variant = ButtonVariant.Outline)
                                Button("Save", onClick = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                            CardContent("Card body content")
                            CardFooter {
                                Button("Cancel", onClick = {}, variant = ButtonVariant.Outline)
                                Button("Save", onClick = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                            CardContent("Card body content")
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                            CardContent("Card body content")
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.width(320.dp)) {
                            CardHeader {
                                CardTitle("Account")
                                CardDescription("Manage your settings")
                            }
                        }
                    },
                ),
            ),
    )
