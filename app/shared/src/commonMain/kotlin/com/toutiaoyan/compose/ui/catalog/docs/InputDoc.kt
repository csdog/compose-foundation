@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldError
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputType
import com.toutiaoyan.compose.ui.components.input.InputVariant

val inputDoc =
    ComponentDoc(
        id = "input",
        title = "Input",
        description = (
            "A text input component for forms and user data entry with " +
                "built-in styling and accessibility features."
        ),
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.field.Field
            import com.toutiaoyan.compose.ui.components.input.Input
            import com.toutiaoyan.compose.ui.components.input.InputType

            var email by remember { mutableStateOf("") }
            Field("Email") {
                Input(
                    value = email,
                    onValueChange = { email = it },
                    type = InputType.Email,
                    placeholder = "you@example.com",
                )
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("type", "InputType", "Text"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("readOnly", "Boolean", "false"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("maxLength", "Int?", "null"),
                ApiProperty("autoFocus", "Boolean", "false"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var apiKey by remember { mutableStateOf("") }
                        Field("API Key", description = "Your API key is encrypted and stored securely.", modifier = Modifier.widthIn(max = 320.dp)) {
                            Input(apiKey, { apiKey = it }, placeholder = "sk-...", type = InputType.Password)
                        }
                        """.trimIndent(),
                    preview = {
                        var apiKey by remember { mutableStateOf("") }
                        Field(
                            "API Key",
                            description = "Your API key is encrypted and stored securely.",
                            modifier = Modifier.widthIn(max = 320.dp),
                        ) {
                            Input(apiKey, { apiKey = it }, placeholder = "sk-...", type = InputType.Password)
                        }
                    },
                ),
                ComponentExample(
                    title = "Filled",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, variant = InputVariant.Filled, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                variant = InputVariant.Filled,
                                placeholder = "you@example.com",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Ghost",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, variant = InputVariant.Ghost, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                variant = InputVariant.Ghost,
                                placeholder = "you@example.com",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, variant = InputVariant.Default, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                variant = InputVariant.Default,
                                placeholder = "you@example.com",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Password",
                    code =
                        """
                        var password by remember { mutableStateOf("") }
                        Field("Password") {
                            Input(
                                value = password,
                                onValueChange = { password = it },
                                type = InputType.Password,
                                placeholder = "••••••••",
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var password by remember { mutableStateOf("") }
                        Field("Password") {
                            Input(
                                value = password,
                                onValueChange = { password = it },
                                type = InputType.Password,
                                placeholder = "••••••••",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Text type",
                    code =
                        """
                        var name by remember { mutableStateOf("") }
                        Field("Name") {
                            Input(
                                value = name,
                                onValueChange = { name = it },
                                type = InputType.Text,
                                placeholder = "Your name",
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var name by remember { mutableStateOf("") }
                        Field("Name") {
                            Input(
                                value = name,
                                onValueChange = { name = it },
                                type = InputType.Text,
                                placeholder = "Your name",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default type",
                    code =
                        """
                        var name by remember { mutableStateOf("") }
                        Field("Name") {
                            Input(
                                value = name,
                                onValueChange = { name = it },
                                type = InputType.Text,
                                placeholder = "Your name",
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var name by remember { mutableStateOf("") }
                        Field("Name") {
                            Input(
                                value = name,
                                onValueChange = { name = it },
                                type = InputType.Text,
                                placeholder = "Your name",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Error",
                    code =
                        """
                        Field("Email") {
                            Input(
                                value = "bad-email",
                                onValueChange = {},
                                type = InputType.Email,
                                invalid = true,
                            )
                            FieldError("Please enter a valid email address")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "bad-email",
                                onValueChange = {},
                                type = InputType.Email,
                                invalid = true,
                            )
                            FieldError("Please enter a valid email address")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                invalid = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                invalid = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                disabled = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                disabled = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                disabled = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                disabled = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Read only",
                    code =
                        """
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                readOnly = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                readOnly = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default read only",
                    code =
                        """
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                readOnly = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "you@example.com",
                                onValueChange = {},
                                type = InputType.Email,
                                readOnly = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Search",
                    code =
                        """
                        Field("Search") {
                            Input(value = "", onValueChange = {}, type = InputType.Search, placeholder = "Search…")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Search") {
                            Input(
                                value = "",
                                onValueChange = {},
                                type = InputType.Search,
                                placeholder = "Search…",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Telephone",
                    code =
                        """
                        Field("Phone") {
                            Input(value = "", onValueChange = {}, type = InputType.Tel, placeholder = "+1 555 000 0000")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Phone") {
                            Input(
                                value = "",
                                onValueChange = {},
                                type = InputType.Tel,
                                placeholder = "+1 555 000 0000",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "URL",
                    code =
                        """
                        Field("Website") {
                            Input(value = "", onValueChange = {}, type = InputType.Url, placeholder = "https://")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Website") {
                            Input(
                                value = "",
                                onValueChange = {},
                                type = InputType.Url,
                                placeholder = "https://",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Number",
                    code =
                        """
                        Field("Amount") {
                            Input(value = "", onValueChange = {}, type = InputType.Number, placeholder = "0")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Amount") {
                            Input(
                                value = "",
                                onValueChange = {},
                                type = InputType.Number,
                                placeholder = "0",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Decimal",
                    code =
                        """
                        Field("Price") {
                            Input(value = "", onValueChange = {}, type = InputType.Decimal, placeholder = "0.00")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Price") {
                            Input(
                                value = "",
                                onValueChange = {},
                                type = InputType.Decimal,
                                placeholder = "0.00",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Email",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, type = InputType.Email, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                type = InputType.Email,
                                placeholder = "you@example.com",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Required",
                    code =
                        """
                        Field("Email", required = true) {
                            Input(value = "", onValueChange = {}, placeholder = "m@example.com", required = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email", required = true) {
                            Input(
                                value = "",
                                onValueChange = {},
                                placeholder = "m@example.com",
                                required = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default required",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, placeholder = "m@example.com", required = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                placeholder = "m@example.com",
                                required = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Max length",
                    code =
                        """
                        var value by remember { mutableStateOf("") }
                        Field("Username") {
                            Input(value = value, onValueChange = { value = it }, maxLength = 12, placeholder = "Up to 12 characters")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("") }
                        Field("Username") {
                            Input(
                                value = value,
                                onValueChange = { value = it },
                                maxLength = 12,
                                placeholder = "Up to 12 characters",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default max length",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, placeholder = "m@example.com", maxLength = null)
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                placeholder = "m@example.com",
                                maxLength = null,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Autofocus",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, placeholder = "m@example.com", autoFocus = true)
                        }
                        """.trimIndent(),
                    preview = {
                        AutofocusPreview {
                            Field("Email") {
                                Input(
                                    value = "",
                                    onValueChange = {},
                                    placeholder = "m@example.com",
                                    autoFocus = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default autofocus",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, placeholder = "m@example.com", autoFocus = false)
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                placeholder = "m@example.com",
                                autoFocus = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placeholder",
                    code =
                        """
                        Field("Email") {
                            Input(value = "jane@example.com", onValueChange = {}, placeholder = null)
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "jane@example.com",
                                onValueChange = {},
                                placeholder = null,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default min lines",
                    code =
                        """
                        Field("Email") {
                            Input(value = "", onValueChange = {}, placeholder = "m@example.com", minLines = 1)
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email") {
                            Input(
                                value = "",
                                onValueChange = {},
                                placeholder = "m@example.com",
                                minLines = 1,
                            )
                        }
                    },
                ),
            ),
    )
