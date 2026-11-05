@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
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
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

val textareaDoc =
    ComponentDoc(
        id = "textarea",
        title = "Textarea",
        description = "Displays a form textarea or a component that looks like a textarea.",
        examplesHeading = null,
        usageImportsCode =
            "import com.toutiaoyan.compose.ui.components.textarea.Textarea\n" +
                "import com.toutiaoyan.compose.ui.components.field.Field",
        usageCode =
            """
            var message by remember { mutableStateOf("") }
            Textarea(
                value = message,
                onValueChange = { message = it },
                placeholder = "Type your message here.",
            )
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("value", "String", "Required"),
                ApiProperty("onValueChange", "(String) -> Unit", "Required"),
                ApiProperty("placeholder", "String?", "null"),
                ApiProperty("disabled", "Boolean", "false"),
                ApiProperty("readOnly", "Boolean", "false"),
                ApiProperty("invalid", "Boolean", "false"),
                ApiProperty("required", "Boolean", "false"),
                ApiProperty("maxLength", "Int?", "null"),
                ApiProperty("rows", "Int?", "null"),
                ApiProperty("minLines", "Int", "1"),
                ApiProperty("maxLines", "Int", "Int.MAX_VALUE"),
                ApiProperty("autoFocus", "Boolean", "false"),
                ApiProperty("variant", "InputVariant", "Default"),
                ApiProperty("style", "Style", "Style"),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    showHeading = false,
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                        )
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                        )
                    },
                ),
                ComponentExample(
                    title = "Field",
                    descriptionMarkdown =
                        "Use `Field`, `FieldLabel`, and `FieldDescription` to create a textarea " +
                            "with a label and description.",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Message")
                            FieldDescription("Enter your message below.")
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Message")
                            FieldDescription("Enter your message below.")
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown =
                        "Use the `disabled` prop to disable the textarea. Pass `disabled = true` to " +
                            "`Field` so the label follows the disabled state.",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp), disabled = true) {
                            FieldLabel("Message")
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                                disabled = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp), disabled = true) {
                            FieldLabel("Message")
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                                disabled = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    descriptionMarkdown =
                        "Mark the textarea with `invalid = true` and the wrapping `Field` with " +
                            "`invalid = true` so the label and description become destructive.",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp), invalid = true) {
                            FieldLabel("Message")
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                                invalid = true,
                            )
                            FieldDescription("Please enter a valid message.")
                        }
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp), invalid = true) {
                            FieldLabel("Message")
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                                invalid = true,
                            )
                            FieldDescription("Please enter a valid message.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Button",
                    descriptionMarkdown = "Pair with `Button` to create a textarea with a submit button.",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Column(
                            Modifier.widthIn(max = 320.dp).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                            )
                            Button("Send message", onClick = {}, modifier = Modifier.fillMaxWidth())
                        }
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Column(
                            Modifier.widthIn(max = 320.dp).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = "Type your message here.",
                            )
                            Button("Send message", onClick = {}, modifier = Modifier.fillMaxWidth())
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    code =
                        """
                        var language by remember { mutableStateOf(0) }
                        var feedback by remember { mutableStateOf("") }
                        RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
                            CompositionLocalProvider(
                                LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
                            ) {
                                Field(Modifier.widthIn(max = 320.dp)) {
                                    FieldLabel(label)
                                    Textarea(
                                        value = feedback,
                                        onValueChange = { feedback = it },
                                        placeholder = placeholder,
                                        rows = 4,
                                    )
                                    FieldDescription(description)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { TextareaRtlPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Rows",
                    descriptionMarkdown = "`rows` pins the visible line count instead of relying on content.",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                            rows = 4,
                        )
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                            rows = 4,
                        )
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Min and max lines",
                    descriptionMarkdown = "`minLines` / `maxLines` grow the field with its content inside a range.",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                            minLines = 2,
                            maxLines = 6,
                        )
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                            minLines = 2,
                            maxLines = 6,
                        )
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Max length",
                    descriptionMarkdown = "`maxLength` truncates typed and pasted input at the limit.",
                    code =
                        """
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                            maxLength = 120,
                        )
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("") }
                        Textarea(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.widthIn(max = 320.dp),
                            placeholder = "Type your message here.",
                            maxLength = 120,
                        )
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Autofocus",
                    descriptionMarkdown =
                        "`autoFocus = true` requests focus once. The catalog wraps it in an explicit " +
                            "try action so navigating to the page does not steal focus.",
                    code =
                        """
                        AutofocusPreview {
                            var message by remember { mutableStateOf("") }
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                modifier = Modifier.widthIn(max = 320.dp),
                                placeholder = "Type your message here.",
                                autoFocus = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        AutofocusPreview {
                            var message by remember { mutableStateOf("") }
                            Textarea(
                                value = message,
                                onValueChange = { message = it },
                                modifier = Modifier.widthIn(max = 320.dp),
                                placeholder = "Type your message here.",
                                autoFocus = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Read only",
                    descriptionMarkdown = "`readOnly = true` keeps the text selectable but not editable.",
                    code =
                        """
                        Textarea(
                            value = "This message cannot be edited.",
                            onValueChange = {},
                            modifier = Modifier.widthIn(max = 320.dp),
                            readOnly = true,
                        )
                        """.trimIndent(),
                    preview = {
                        Textarea(
                            value = "This message cannot be edited.",
                            onValueChange = {},
                            modifier = Modifier.widthIn(max = 320.dp),
                            readOnly = true,
                        )
                    },
                ),
            ),
    )

private val textareaRtlLabels = listOf("Arabic (العربية)", "English", "Hebrew (עברית)")

private class TextareaRtlCopy(
    val label: String,
    val placeholder: String,
    val description: String,
)

private val textareaRtlCopy =
    listOf(
        TextareaRtlCopy(
            label = "التعليقات",
            placeholder = "تعليقاتك تساعدنا على التحسين...",
            description = "شاركنا أفكارك حول خدمتنا.",
        ),
        TextareaRtlCopy(
            label = "Feedback",
            placeholder = "Your feedback helps us improve...",
            description = "Share your thoughts about our service.",
        ),
        TextareaRtlCopy(
            label = "משוב",
            placeholder = "המשוב שלך עוזר לנו להשתפר...",
            description = "שתף את מחשבותיך על השירות שלנו.",
        ),
    )

@Composable
private fun TextareaRtlPreview() {
    var language by remember { mutableStateOf(0) }
    RtlPreview(language, textareaRtlLabels, { language = it }) {
        val inherited = currentTheme
        val fontFamily =
            when (language) {
                0 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
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
                fontFamily = fontFamily,
            ) {
                var feedback by remember { mutableStateOf("") }
                val copy = textareaRtlCopy[language]
                Field(Modifier.widthIn(max = 320.dp)) {
                    FieldLabel(copy.label)
                    Textarea(
                        value = feedback,
                        onValueChange = { feedback = it },
                        placeholder = copy.placeholder,
                        rows = 4,
                    )
                    FieldDescription(copy.description)
                }
            }
        }
    }
}
