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
import com.toutiaoyan.compose.ui.components.input.InputVariant
import com.toutiaoyan.compose.ui.components.textarea.Textarea

val textareaDoc =
    ComponentDoc(
        id = "textarea",
        title = "Textarea",
        description = "A multi-line text input, sharing the same border/focus spec as Input.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.field.Field
            import com.toutiaoyan.compose.ui.components.textarea.Textarea

            var bio by remember { mutableStateOf("") }
            Field("Bio") {
                Textarea(value = bio, onValueChange = { bio = it }, placeholder = "Tell us about yourself")
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("readOnly", "Boolean", "false"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("rows", "Int?", "null"),
                ApiProperty("minLines", "Int", "1"),
                ApiProperty("maxLines", "Int", "Int.MAX_VALUE"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Textarea(message, { message = it }, modifier = Modifier.widthIn(max = 320.dp), placeholder = "Type your message here.")
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            message,
                            { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                        )
                    },
                ),
                ComponentExample(
                    title = "Default variant",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                variant = InputVariant.Default,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                variant = InputVariant.Default,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Field("Bio") {
                            Textarea(
                                value = "Not editable",
                                onValueChange = {},
                                disabled = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Bio") {
                            Textarea(
                                value = "Not editable",
                                onValueChange = {},
                                disabled = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Field("Bio") {
                            Textarea(
                                value = "Hello",
                                onValueChange = {},
                                disabled = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Bio") {
                            Textarea(
                                value = "Hello",
                                onValueChange = {},
                                disabled = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    code =
                        """
                        Field("Bio", invalid = true) {
                            Textarea(
                                value = "Too short",
                                onValueChange = {},
                                invalid = true,
                            )
                            FieldError("Bio must be at least 20 characters.")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Bio", invalid = true) {
                            Textarea(
                                value = "Too short",
                                onValueChange = {},
                                invalid = true,
                            )
                            FieldError("Bio must be at least 20 characters.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                invalid = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                invalid = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Rows",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                rows = 6,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                rows = 6,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default rows",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                rows = null,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                rows = null,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Min lines",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                minLines = 3,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                minLines = 3,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default min lines",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                minLines = 1,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                minLines = 1,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Max lines",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                maxLines = 4,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                maxLines = 4,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default max lines",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                maxLines = Int.MAX_VALUE,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                maxLines = Int.MAX_VALUE,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Required",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio", required = true) {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                required = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio", required = true) {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                required = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default required",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                required = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Tell us about yourself",
                                required = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Max length",
                    code =
                        """
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Up to 120 characters",
                                maxLength = 120,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = "Up to 120 characters",
                                maxLength = 120,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default max length",
                    code =
                        """
                        var bio by remember { mutableStateOf("I like to build things.") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                maxLength = null,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("I like to build things.") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                maxLength = null,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Autofocus",
                    code =
                        """
                        Field("Bio") {
                            Textarea(
                                value = "",
                                onValueChange = {},
                                placeholder = "Tell us about yourself",
                                autoFocus = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        AutofocusPreview {
                            Field("Bio") {
                                Textarea(
                                    value = "",
                                    onValueChange = {},
                                    placeholder = "Tell us about yourself",
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
                        Field("Bio") {
                            Textarea(
                                value = "",
                                onValueChange = {},
                                placeholder = "Tell us about yourself",
                                autoFocus = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Bio") {
                            Textarea(
                                value = "",
                                onValueChange = {},
                                placeholder = "Tell us about yourself",
                                autoFocus = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Read only",
                    code =
                        """
                        Field("Bio") {
                            Textarea(
                                value = "I like to build things.",
                                onValueChange = {},
                                readOnly = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Bio") {
                            Textarea(
                                value = "I like to build things.",
                                onValueChange = {},
                                readOnly = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default read only",
                    code =
                        """
                        var bio by remember { mutableStateOf("I like to build things.") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                readOnly = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("I like to build things.") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                readOnly = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placeholder",
                    code =
                        """
                        var bio by remember { mutableStateOf("I like to build things.") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = null,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var bio by remember { mutableStateOf("I like to build things.") }
                        Field("Bio") {
                            Textarea(
                                value = bio,
                                onValueChange = { bio = it },
                                placeholder = null,
                            )
                        }
                    },
                ),
            ),
    )
