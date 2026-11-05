@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.badge.Badge
import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroup
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupItem
import com.toutiaoyan.compose.ui.components.buttongroup.rememberButtonGroupOutlineStyle
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldOrientation
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputType
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupInput
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

// region exported code

private val inputRtlCode =
    """
    @Composable
    fun InputRtlExample() {
        var language by remember { mutableStateOf(1) } // 0 English, 1 Arabic, 2 Hebrew
        val inherited = currentTheme
        var apiKey by remember { mutableStateOf("") }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
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
                fontFamily = when (language) {
                    1 -> catalogArabicFontFamily()
                    2 -> catalogHebrewFontFamily()
                    else -> inherited.fontFamily
                },
            ) {
                val t = inputRtlTranslations[language]
                Field(Modifier.widthIn(max = 320.dp)) {
                    FieldLabel(t.apiKey)
                    Input(apiKey, { apiKey = it }, type = InputType.Password, placeholder = "sk-...")
                    FieldDescription(t.description)
                }
            }
        }
    }
    """.trimIndent()

// endregion

private val inputImports =
    """
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
    import com.toutiaoyan.compose.ui.components.badge.Badge
    import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
    import com.toutiaoyan.compose.ui.components.button.Button
    import com.toutiaoyan.compose.ui.components.button.ButtonVariant
    import com.toutiaoyan.compose.ui.components.buttongroup.*
    import com.toutiaoyan.compose.ui.components.field.*
    import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.input.Input
    import com.toutiaoyan.compose.ui.components.input.InputType
    import com.toutiaoyan.compose.ui.components.inputgroup.*
    import com.toutiaoyan.compose.ui.components.select.*
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.components.text.TextColors
    """.trimIndent()

private val inputCountries =
    listOf(
        "United States" to "us",
        "United Kingdom" to "uk",
        "Canada" to "ca",
    )

val inputDoc =
    ComponentDoc(
        id = "input",
        title = "Input",
        description =
            "A text input component for forms and user data entry with built-in styling and " +
                "accessibility features.",
        examplesHeading = null,
        usageImportsCode = inputImports,
        usageCode =
            """
            var text by remember { mutableStateOf("") }
            Input(text, { text = it }, placeholder = "Enter text")
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var apiKey by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("API Key")
                            Input(apiKey, { apiKey = it }, type = InputType.Password, placeholder = "sk-...")
                            FieldDescription("Your API key is encrypted and stored securely.")
                        }
                        """.trimIndent(),
                    preview = { InputDemoRecipe() },
                ),
                ComponentExample(
                    title = "Basic",
                    code =
                        """
                        var text by remember { mutableStateOf("") }
                        Input(text, { text = it }, Modifier.widthIn(max = 320.dp), placeholder = "Enter text")
                        """.trimIndent(),
                    preview = { InputBasicRecipe() },
                ),
                ComponentExample(
                    title = "Field",
                    descriptionMarkdown =
                        "Use `Field`, `FieldLabel`, and `FieldDescription` to create an input with a " +
                            "label and description.",
                    code =
                        """
                        var username by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Username")
                            Input(username, { username = it }, placeholder = "Enter your username")
                            FieldDescription("Choose a unique username for your account.")
                        }
                        """.trimIndent(),
                    preview = { InputFieldRecipe() },
                ),
                ComponentExample(
                    title = "Field Group",
                    descriptionMarkdown =
                        "Use `FieldGroup` to show multiple `Field` blocks and to build forms.",
                    code =
                        """
                        FieldGroup(Modifier.widthIn(max = 320.dp)) {
                            Field {
                                FieldLabel("Name")
                                Input(name, { name = it }, placeholder = "Jordan Lee")
                            }
                            Field {
                                FieldLabel("Email")
                                Input(email, { email = it }, type = InputType.Email, placeholder = "name@example.com")
                                FieldDescription("We'll send updates to this address.")
                            }
                            Field(orientation = FieldOrientation.Horizontal) {
                                Button("Reset", variant = ButtonVariant.Outline)
                                Button("Submit")
                            }
                        }
                        """.trimIndent(),
                    preview = { InputFieldGroupRecipe() },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown =
                        "Use the `disabled` prop to disable the input. To style the disabled state, " +
                            "disable the `Field` as well.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp), disabled = true) {
                            FieldLabel("Email")
                            Input(email, { email = it }, type = InputType.Email, placeholder = "Email", disabled = true)
                            FieldDescription("This field is currently disabled.")
                        }
                        """.trimIndent(),
                    preview = { InputDisabledRecipe() },
                ),
                ComponentExample(
                    title = "Invalid",
                    descriptionMarkdown =
                        "Use the `invalid` prop to mark the input as invalid. To style the invalid " +
                            "state, mark the `Field` invalid as well.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp), invalid = true) {
                            FieldLabel("Invalid Input")
                            Input(value, { value = it }, placeholder = "Error", invalid = true)
                            FieldDescription("This field contains validation errors.")
                        }
                        """.trimIndent(),
                    preview = { InputInvalidRecipe() },
                ),
                ComponentExample(
                    title = "File",
                    descriptionMarkdown =
                        "Use a file picker for file inputs. Compose has no browser-native " +
                            "`type=\"file\"` control, so this preview approximates the browser field.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Picture")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Button("Choose File", variant = ButtonVariant.Outline)
                                Text("No file chosen", textStyle = TextColors.Muted)
                            }
                            FieldDescription("Select a picture to upload.")
                        }
                        """.trimIndent(),
                    preview = { InputFileRecipe() },
                ),
                ComponentExample(
                    title = "Inline",
                    descriptionMarkdown =
                        "Use `Field` with `orientation = FieldOrientation.Horizontal` to create an " +
                            "inline input. Pair it with `Button` to create a search input with a button.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp), orientation = FieldOrientation.Horizontal) {
                            Input(query, { query = it }, Modifier.width(244.dp), type = InputType.Search, placeholder = "Search...")
                            Button("Search")
                        }
                        """.trimIndent(),
                    preview = { InputInlineRecipe() },
                ),
                ComponentExample(
                    title = "Grid",
                    descriptionMarkdown = "Use a grid layout to place multiple inputs side by side.",
                    previewPadding = 24.dp,
                    code =
                        """
                        FieldGroup(Modifier.widthIn(max = 384.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Field("First Name", Modifier.weight(1f)) {
                                    Input(first, { first = it }, placeholder = "Jordan")
                                }
                                Field("Last Name", Modifier.weight(1f)) {
                                    Input(last, { last = it }, placeholder = "Lee")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGridRecipe() },
                ),
                ComponentExample(
                    title = "Required",
                    descriptionMarkdown = "Use the `required` attribute to indicate required inputs.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Required Field", required = true)
                            Input(value, { value = it }, placeholder = "This field is required", required = true)
                            FieldDescription("This field must be filled out.")
                        }
                        """.trimIndent(),
                    preview = { InputRequiredRecipe() },
                ),
                ComponentExample(
                    title = "Badge",
                    descriptionMarkdown = "Use `Badge` in the label to highlight a recommended field.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FieldLabel("Webhook URL")
                                Box(Modifier.weight(1f))
                                Badge("Beta", variant = BadgeVariant.Secondary)
                            }
                            Input(value, { value = it }, type = InputType.Url, placeholder = "https://api.example.com/webhook")
                        }
                        """.trimIndent(),
                    preview = { InputBadgeRecipe() },
                ),
                ComponentExample(
                    title = "Input Group",
                    descriptionMarkdown =
                        "To add icons, text, or buttons inside an input, use the `InputGroup` component. " +
                            "See the Input Group component for more examples.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Website URL")
                            InputGroup(Modifier.fillMaxWidth()) {
                                InputGroupInput(value, { value = it }, placeholder = "example.com")
                                InputGroupAddon("https://")
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) { Icon(Lucide.Info) }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputGroupRecipe() },
                ),
                ComponentExample(
                    title = "Button Group",
                    descriptionMarkdown =
                        "To add buttons to an input, use the `ButtonGroup` component. See the Button " +
                            "Group component for more examples.",
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Search")
                            BoxWithConstraints(Modifier.fillMaxWidth()) {
                                ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                                    Input(query, { query = it }, Modifier.width(maxWidth - 67.dp), placeholder = "Type to search...")
                                    ButtonGroupItem("Search", onClick = {}, variant = ButtonVariant.Outline)
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputButtonGroupRecipe() },
                ),
                ComponentExample(
                    title = "Form",
                    descriptionMarkdown =
                        "A full form example with multiple inputs, a select, and a button.",
                    minPreviewHeight = 512.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        FieldGroup(Modifier.widthIn(max = 384.dp)) {
                            Field("Name") { Input(name, { name = it }, placeholder = "Evil Rabbit", required = true) }
                            Field("Email", description = "We'll never share your email with anyone.") {
                                Input(email, { email = it }, type = InputType.Email, placeholder = "john@example.com")
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Field("Phone", Modifier.weight(1f)) {
                                    Input(phone, { phone = it }, type = InputType.Tel, placeholder = "+1 (555) 123-4567")
                                }
                                Field("Country", Modifier.weight(1f)) {
                                    Select<String?>(value = country, onValueChange = { country = it }, placeholder = "United States") {
                                        SelectTrigger(Modifier.fillMaxWidth())
                                        SelectContent { countries.forEach { (label, value) -> SelectItem<String?>(value, textValue = label) } }
                                    }
                                }
                            }
                            Field("Address") { Input(address, { address = it }, placeholder = "123 Main St") }
                            Field(orientation = FieldOrientation.Horizontal) {
                                Button("Cancel", variant = ButtonVariant.Outline)
                                Button("Submit")
                            }
                        }
                        """.trimIndent(),
                    preview = { InputFormRecipe() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    minPreviewHeight = 288.dp,
                    previewPadding = 0.dp,
                    code = inputRtlCode,
                    preview = { InputRtlRecipe() },
                ),
            ),
    )

// region recipes

@Composable
private fun InputDemoRecipe() {
    var apiKey by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel("API Key")
        Input(apiKey, { apiKey = it }, type = InputType.Password, placeholder = "sk-...")
        FieldDescription("Your API key is encrypted and stored securely.")
    }
}

@Composable
private fun InputBasicRecipe() {
    var text by remember { mutableStateOf("") }
    Input(text, { text = it }, Modifier.widthIn(max = 320.dp), placeholder = "Enter text")
}

@Composable
private fun InputFieldRecipe() {
    var username by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel("Username")
        Input(username, { username = it }, placeholder = "Enter your username")
        FieldDescription("Choose a unique username for your account.")
    }
}

@Composable
private fun InputFieldGroupRecipe() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        Field {
            FieldLabel("Name")
            Input(name, { name = it }, placeholder = "Jordan Lee")
        }
        Field {
            FieldLabel("Email")
            Input(email, { email = it }, type = InputType.Email, placeholder = "name@example.com")
            FieldDescription("We'll send updates to this address.")
        }
        Field(orientation = FieldOrientation.Horizontal) {
            Button("Reset", variant = ButtonVariant.Outline)
            Button("Submit")
        }
    }
}

@Composable
private fun InputDisabledRecipe() {
    var email by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp), disabled = true) {
        FieldLabel("Email")
        Input(email, { email = it }, type = InputType.Email, placeholder = "Email", disabled = true)
        FieldDescription("This field is currently disabled.")
    }
}

@Composable
private fun InputInvalidRecipe() {
    var value by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp), invalid = true) {
        FieldLabel("Invalid Input")
        Input(value, { value = it }, placeholder = "Error", invalid = true)
        FieldDescription("This field contains validation errors.")
    }
}

@Composable
private fun InputFileRecipe() {
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel("Picture")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button("Choose File", variant = ButtonVariant.Outline)
            Text(textStyle = TextColors.Muted) { "No file chosen" }
        }
        FieldDescription("Select a picture to upload.")
    }
}

@Composable
private fun InputInlineRecipe() {
    var query by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp), orientation = FieldOrientation.Horizontal) {
        // HTML's `w-full min-w-0` input shrinks to the width left by the button.
        Input(query, { query = it }, Modifier.width(244.dp), type = InputType.Search, placeholder = "Search...")
        Button("Search")
    }
}

@Composable
private fun InputGridRecipe() {
    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    FieldGroup(Modifier.widthIn(max = 384.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Field("First Name", Modifier.weight(1f)) {
                Input(first, { first = it }, placeholder = "Jordan")
            }
            Field("Last Name", Modifier.weight(1f)) {
                Input(last, { last = it }, placeholder = "Lee")
            }
        }
    }
}

@Composable
private fun InputRequiredRecipe() {
    var value by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel("Required Field", required = true)
        Input(value, { value = it }, placeholder = "This field is required", required = true)
        FieldDescription("This field must be filled out.")
    }
}

@Composable
private fun InputBadgeRecipe() {
    var value by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FieldLabel("Webhook URL")
            Box(Modifier.weight(1f))
            Badge("Beta", variant = BadgeVariant.Secondary)
        }
        Input(value, { value = it }, type = InputType.Url, placeholder = "https://api.example.com/webhook")
    }
}

@Composable
private fun InputGroupRecipe() {
    var value by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel("Website URL")
        InputGroup(Modifier.fillMaxWidth()) {
            InputGroupInput(value, { value = it }, placeholder = "example.com")
            InputGroupAddon("https://")
            InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                Icon(Lucide.Info)
            }
        }
    }
}

@Composable
private fun InputButtonGroupRecipe() {
    var query by remember { mutableStateOf("") }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel("Search")
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                Input(
                    query,
                    { query = it },
                    Modifier.width((maxWidth - 67.dp).coerceAtLeast(120.dp)),
                    placeholder = "Type to search...",
                )
                ButtonGroupItem("Search", onClick = {}, variant = ButtonVariant.Outline)
            }
        }
    }
}

@Composable
private fun InputFormRecipe() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var country by remember { mutableStateOf<String?>("us") }
    var address by remember { mutableStateOf("") }
    FieldGroup(Modifier.widthIn(max = 384.dp)) {
        Field("Name") {
            Input(name, { name = it }, placeholder = "Evil Rabbit", required = true)
        }
        Field("Email", description = "We'll never share your email with anyone.") {
            Input(email, { email = it }, type = InputType.Email, placeholder = "john@example.com")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Field("Phone", Modifier.weight(1f)) {
                Input(phone, { phone = it }, type = InputType.Tel, placeholder = "+1 (555) 123-4567")
            }
            Field("Country", Modifier.weight(1f)) {
                Select<String?>(
                    value = country,
                    onValueChange = { country = it },
                    placeholder = "United States",
                    label = { value -> inputCountries.firstOrNull { it.second == value }?.first ?: "United States" },
                ) {
                    SelectTrigger(Modifier.fillMaxWidth())
                    SelectContent {
                        inputCountries.forEach { (label, value) ->
                            SelectItem<String?>(value, textValue = label)
                        }
                    }
                }
            }
        }
        Field("Address") {
            Input(address, { address = it }, placeholder = "123 Main St")
        }
        Field(orientation = FieldOrientation.Horizontal) {
            Button("Cancel", variant = ButtonVariant.Outline)
            Button("Submit")
        }
    }
}

private class InputRtlStrings(
    val apiKey: String,
    val description: String,
)

private val inputRtlTranslations =
    listOf(
        InputRtlStrings("API Key", "Your API key is encrypted and stored securely."),
        InputRtlStrings("مفتاح API", "مفتاح API الخاص بك مشفر ومخزن بأمان."),
        InputRtlStrings("מפתח API", "מפתח ה-API שלך מוצפן ונשמר בצורה מאובטחת."),
    )

@Composable
private fun InputRtlRecipe() {
    var language by remember { mutableStateOf(1) }
    var apiKey by remember { mutableStateOf("") }
    val inherited = currentTheme
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(
            language,
            listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
            { language = it },
        )
        Box(
            Modifier.fillMaxWidth().heightIn(min = 225.dp).padding(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            val font =
                when (language) {
                    1 -> catalogArabicFontFamily()
                    2 -> catalogHebrewFontFamily()
                    else -> inherited.fontFamily
                }
            CompositionLocalProvider(
                LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
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
                    fontFamily = font,
                ) {
                    val t = inputRtlTranslations[language]
                    Field(Modifier.widthIn(max = 320.dp)) {
                        FieldLabel(t.apiKey)
                        Input(apiKey, { apiKey = it }, type = InputType.Password, placeholder = "sk-...")
                        FieldDescription(t.description)
                    }
                }
            }
        }
    }
}

// endregion


