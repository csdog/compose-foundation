@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
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
        // 0 English, 1 Arabic, 2 Hebrew; supply a font covering the chosen language.
        var language by remember { mutableStateOf(1) }
        var apiKey by remember { mutableStateOf("") }
        val translations = listOf(
            "API Key" to "Your API key is encrypted and stored securely.",
            "مفتاح API" to "مفتاح API الخاص بك مشفر ومخزن بأمان.",
            "מפתח API" to "מפתח ה-API שלך מוצפן ונשמר בצורה מאובטחת.",
        )
        val (label, description) = translations[language]
        val focus = remember { FocusRequester() }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Field(Modifier.widthIn(max = 320.dp)) {
                FieldLabel(label, Modifier.focusProperties { canFocus = false }.clickable { focus.requestFocus() })
                Input(apiKey, { apiKey = it }, Modifier.focusRequester(focus).semantics { contentDescription = label }, type = InputType.Password, placeholder = "sk-...")
                FieldDescription(description)
            }
        }
    }
    """.trimIndent()

// endregion

private val inputImports =
    """
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.focus.FocusRequester
    import androidx.compose.ui.focus.focusProperties
    import androidx.compose.ui.focus.focusRequester
    import androidx.compose.ui.platform.LocalLayoutDirection
    import androidx.compose.ui.semantics.contentDescription
    import androidx.compose.ui.semantics.semantics
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.unit.LayoutDirection
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
                            val inputFocus0 = remember { FocusRequester() }
                            FieldLabel("API Key", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                            Input(apiKey, { apiKey = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "API Key" }, type = InputType.Password, placeholder = "sk-...")
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
                            val inputFocus0 = remember { FocusRequester() }
                            FieldLabel("Username", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                            Input(username, { username = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "Username" }, placeholder = "Enter your username")
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
                        var name by remember { mutableStateOf("") }
                        var email by remember { mutableStateOf("") }
                        FieldGroup(Modifier.widthIn(max = 320.dp)) {
                            Field {
                                val inputFocus0 = remember { FocusRequester() }
                                FieldLabel("Name", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                                Input(name, { name = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "Name" }, placeholder = "Jordan Lee")
                            }
                            Field {
                                val inputFocus1 = remember { FocusRequester() }
                                FieldLabel("Email", Modifier.focusProperties { canFocus = false }.clickable { inputFocus1.requestFocus() })
                                Input(email, { email = it }, Modifier.focusRequester(inputFocus1).semantics { contentDescription = "Email" }, type = InputType.Email, placeholder = "name@example.com")
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
                        var email by remember { mutableStateOf("") }
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
                        var value by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp), invalid = true) {
                            val inputFocus0 = remember { FocusRequester() }
                            FieldLabel("Invalid Input", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                            Input(value, { value = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "Invalid Input" }, placeholder = "Error", invalid = true)
                            FieldDescription("This field contains validation errors.")
                        }
                        """.trimIndent(),
                    preview = { InputInvalidRecipe() },
                ),
                ComponentExample(
                    title = "File",
                    descriptionMarkdown =
                        "Choose a file with the browser or desktop file chooser. This catalog preview " +
                            "shows its filename without reading or uploading the file. Android and iOS " +
                            "catalogs disable the action until a picker is connected.",
                    code =
                        """
                        // Supply the platform chooser; null result means cancelled.
                        @Composable
                        fun PictureInput(pickFile: (((String?) -> Unit) -> Unit)?) {
                            var filename by remember { mutableStateOf<String?>(null) }
                            val onResult by rememberUpdatedState<(String?) -> Unit>({ selected ->
                                if (selected != null) filename = selected
                            })
                            val focus = remember { FocusRequester() }
                            val choose = { pickFile?.invoke { onResult(it) }; Unit }
                            Field(Modifier.widthIn(max = 320.dp)) {
                                FieldLabel("Picture", Modifier.focusProperties { canFocus = false }.clickable(enabled = pickFile != null) { focus.requestFocus(); choose() }, disabled = pickFile == null)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Button("Choose File", enabled = pickFile != null, modifier = Modifier.focusRequester(focus), label = "Choose File: Picture", onClick = choose, variant = ButtonVariant.Outline)
                                    Text(filename ?: "No file chosen", modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis, textStyle = TextColors.Muted)
                                }
                                FieldDescription("Select a picture to upload.")
                                if (pickFile == null) FieldDescription("File selection is unavailable on this platform.")
                            }
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
                        var query by remember { mutableStateOf("") }
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
                        var first by remember { mutableStateOf("") }
                        var last by remember { mutableStateOf("") }
                        FieldGroup(Modifier.widthIn(max = 384.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Field(Modifier.weight(1f)) {
                                    val inputFocus0 = remember { FocusRequester() }
                                    FieldLabel("First Name", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                                    Input(first, { first = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "First Name" }, placeholder = "Jordan")
                                }
                                Field(Modifier.weight(1f)) {
                                    val inputFocus1 = remember { FocusRequester() }
                                    FieldLabel("Last Name", Modifier.focusProperties { canFocus = false }.clickable { inputFocus1.requestFocus() })
                                    Input(last, { last = it }, Modifier.focusRequester(inputFocus1).semantics { contentDescription = "Last Name" }, placeholder = "Lee")
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
                        var value by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            val inputFocus0 = remember { FocusRequester() }
                            FieldLabel("Required Field", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() }, required = true)
                            Input(value, { value = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "Required Field, required" }, placeholder = "This field is required", required = true)
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
                        var value by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            val inputFocus0 = remember { FocusRequester() }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FieldLabel("Webhook URL", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                                Box(Modifier.weight(1f))
                                Badge("Beta", variant = BadgeVariant.Secondary)
                            }
                            Input(value, { value = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "Webhook URL" }, type = InputType.Url, placeholder = "https://api.example.com/webhook")
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
                        var value by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            val inputFocus0 = remember { FocusRequester() }
                            FieldLabel("Website URL", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                            InputGroup(Modifier.fillMaxWidth()) {
                                InputGroupInput(value, { value = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "Website URL" }, placeholder = "example.com")
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
                        var query by remember { mutableStateOf("") }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            val inputFocus0 = remember { FocusRequester() }
                            FieldLabel("Search", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                            BoxWithConstraints(Modifier.fillMaxWidth()) {
                                ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                                    Input(query, { query = it }, Modifier.width((maxWidth - 67.dp).coerceAtLeast(120.dp)).focusRequester(inputFocus0).semantics { contentDescription = "Search" }, placeholder = "Type to search...")
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
                        var name by remember { mutableStateOf("") }
                        var email by remember { mutableStateOf("") }
                        var phone by remember { mutableStateOf("") }
                        var country by remember { mutableStateOf<String?>("us") }
                        var address by remember { mutableStateOf("") }
                        val countries = listOf("United States" to "us", "United Kingdom" to "uk", "Canada" to "ca")
                        FieldGroup(Modifier.widthIn(max = 384.dp)) {
                            Field {
                                val inputFocus0 = remember { FocusRequester() }
                                FieldLabel("Name", Modifier.focusProperties { canFocus = false }.clickable { inputFocus0.requestFocus() })
                                Input(name, { name = it }, Modifier.focusRequester(inputFocus0).semantics { contentDescription = "Name, required" }, placeholder = "Evil Rabbit", required = true)
                            }
                            Field {
                                val inputFocus1 = remember { FocusRequester() }
                                FieldLabel("Email", Modifier.focusProperties { canFocus = false }.clickable { inputFocus1.requestFocus() })
                                Input(email, { email = it }, Modifier.focusRequester(inputFocus1).semantics { contentDescription = "Email" }, type = InputType.Email, placeholder = "john@example.com")
                                FieldDescription("We'll never share your email with anyone.")
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Field(Modifier.weight(1f)) {
                                    val inputFocus2 = remember { FocusRequester() }
                                    FieldLabel("Phone", Modifier.focusProperties { canFocus = false }.clickable { inputFocus2.requestFocus() })
                                    Input(phone, { phone = it }, Modifier.focusRequester(inputFocus2).semantics { contentDescription = "Phone" }, type = InputType.Tel, placeholder = "+1 (555) 123-4567")
                                }
                                Field(Modifier.weight(1f)) {
                                    val inputFocus3 = remember { FocusRequester() }
                                    FieldLabel("Country", Modifier.focusProperties { canFocus = false }.clickable { inputFocus3.requestFocus() })
                                    Select<String?>(value = country, onValueChange = { country = it }, placeholder = "United States", label = { selected -> countries.firstOrNull { it.second == selected }?.first ?: "United States" }) {
                                        SelectTrigger(Modifier.fillMaxWidth().focusRequester(inputFocus3).semantics { contentDescription = "Country" })
                                        SelectContent { countries.forEach { (label, value) -> SelectItem<String?>(value, textValue = label) } }
                                    }
                                }
                            }
                            Field {
                                val inputFocus4 = remember { FocusRequester() }
                                FieldLabel("Address", Modifier.focusProperties { canFocus = false }.clickable { inputFocus4.requestFocus() })
                                Input(address, { address = it }, Modifier.focusRequester(inputFocus4).semantics { contentDescription = "Address" }, placeholder = "123 Main St")
                            }
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
                ComponentExample(
                    title = "Kotlin extension: Autofocus",
                    code =
                        """
                        var email by remember { mutableStateOf("") }
                        val focus = remember { FocusRequester() }
                        Field {
                            FieldLabel("Email", Modifier.focusProperties { canFocus = false }.clickable { focus.requestFocus() })
                            Input(value = email, onValueChange = { email = it }, modifier = Modifier.focusRequester(focus).semantics { contentDescription = "Email" }, placeholder = "m@example.com", autoFocus = true)
                        }
                        """.trimIndent(),
                    preview = {
                        AutofocusPreview {
                            var email by remember { mutableStateOf("") }
                            InputRecipeField("Email") { input ->
                                Input(
                                    value = email,
                                    onValueChange = { email = it },
                                    modifier = input,
                                    placeholder = "m@example.com",
                                    autoFocus = true,
                                )
                            }
                        }
                    },
                ),
            ),
    )

// region recipes

@Composable
private fun InputRecipeField(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    required: Boolean = false,
    showRequired: Boolean = required,
    invalid: Boolean = false,
    trailingLabel: (@Composable () -> Unit)? = null,
    content: @Composable (Modifier) -> Unit,
) {
    val focus = remember { FocusRequester() }
    Field(modifier, invalid = invalid) {
        val labelModifier = Modifier.focusProperties { canFocus = false }.clickable { focus.requestFocus() }
        if (trailingLabel == null) {
            FieldLabel(label, labelModifier, required = showRequired)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FieldLabel(label, labelModifier, required = showRequired)
                Box(Modifier.weight(1f))
                trailingLabel()
            }
        }
        content(
            Modifier.focusRequester(focus).semantics {
                contentDescription = if (required) "$label, required" else label
            },
        )
        if (description != null) FieldDescription(description)
    }
}

@Composable
private fun InputDemoRecipe() {
    var apiKey by remember { mutableStateOf("") }
    InputRecipeField(
        "API Key",
        Modifier.widthIn(max = 320.dp),
        "Your API key is encrypted and stored securely.",
    ) { input ->
        Input(apiKey, { apiKey = it }, input, type = InputType.Password, placeholder = "sk-...")
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
    InputRecipeField(
        "Username",
        Modifier.widthIn(max = 320.dp),
        "Choose a unique username for your account.",
    ) { input ->
        Input(username, { username = it }, input, placeholder = "Enter your username")
    }
}

@Composable
private fun InputFieldGroupRecipe() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        InputRecipeField("Name") { input ->
            Input(name, { name = it }, input, placeholder = "Jordan Lee")
        }
        InputRecipeField("Email", description = "We'll send updates to this address.") { input ->
            Input(email, { email = it }, input, type = InputType.Email, placeholder = "name@example.com")
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
    InputRecipeField(
        "Invalid Input",
        Modifier.widthIn(max = 320.dp),
        "This field contains validation errors.",
        invalid = true,
    ) { input ->
        Input(value, { value = it }, input, placeholder = "Error", invalid = true)
    }
}

@Composable
private fun InputFileRecipe() {
    val picker = LocalFilePicker.current
    var filename by remember { mutableStateOf<String?>(null) }
    val onResult by rememberUpdatedState<(String?) -> Unit>({ selected ->
        if (selected != null) filename = selected
    })
    val focus = remember { FocusRequester() }
    val choose = {
        picker?.invoke { onResult(it) }
        Unit
    }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel(
            "Picture",
            Modifier.focusProperties { canFocus = false }.clickable(enabled = picker != null) {
                focus.requestFocus()
                choose()
            },
            disabled = picker == null,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                "Choose File",
                enabled = picker != null,
                modifier = Modifier.focusRequester(focus),
                label = "Choose File: Picture",
                onClick = choose,
                variant = ButtonVariant.Outline,
            )
            Text(
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textStyle = TextColors.Muted,
            ) { filename ?: "No file chosen" }
        }
        FieldDescription("Select a picture to upload.")
        if (picker == null) FieldDescription("File selection is unavailable on this platform.")
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
            InputRecipeField("First Name", Modifier.weight(1f)) { input ->
                Input(first, { first = it }, input, placeholder = "Jordan")
            }
            InputRecipeField("Last Name", Modifier.weight(1f)) { input ->
                Input(last, { last = it }, input, placeholder = "Lee")
            }
        }
    }
}

@Composable
private fun InputRequiredRecipe() {
    var value by remember { mutableStateOf("") }
    InputRecipeField(
        "Required Field",
        Modifier.widthIn(max = 320.dp),
        "This field must be filled out.",
        required = true,
    ) { input ->
        Input(value, { value = it }, input, placeholder = "This field is required", required = true)
    }
}

@Composable
private fun InputBadgeRecipe() {
    var value by remember { mutableStateOf("") }
    InputRecipeField(
        "Webhook URL",
        Modifier.widthIn(max = 320.dp),
        trailingLabel = { Badge("Beta", variant = BadgeVariant.Secondary) },
    ) { input ->
        Input(value, { value = it }, input, type = InputType.Url, placeholder = "https://api.example.com/webhook")
    }
}

@Composable
private fun InputGroupRecipe() {
    var value by remember { mutableStateOf("") }
    InputRecipeField("Website URL", Modifier.widthIn(max = 320.dp)) { input ->
        InputGroup(Modifier.fillMaxWidth()) {
            InputGroupInput(value, { value = it }, input, placeholder = "example.com")
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
    InputRecipeField("Search", Modifier.widthIn(max = 320.dp)) { input ->
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
                Input(
                    query,
                    { query = it },
                    input.width((maxWidth - 67.dp).coerceAtLeast(120.dp)),
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
        InputRecipeField("Name", required = true, showRequired = false) { input ->
            Input(name, { name = it }, input, placeholder = "Evil Rabbit", required = true)
        }
        InputRecipeField("Email", description = "We'll never share your email with anyone.") { input ->
            Input(email, { email = it }, input, type = InputType.Email, placeholder = "john@example.com")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InputRecipeField("Phone", Modifier.weight(1f)) { input ->
                Input(phone, { phone = it }, input, type = InputType.Tel, placeholder = "+1 (555) 123-4567")
            }
            InputRecipeField("Country", Modifier.weight(1f)) { input ->
                Select<String?>(
                    value = country,
                    onValueChange = { country = it },
                    placeholder = "United States",
                    label = { value -> inputCountries.firstOrNull { it.second == value }?.first ?: "United States" },
                ) {
                    SelectTrigger(input.fillMaxWidth())
                    SelectContent {
                        inputCountries.forEach { (label, value) ->
                            SelectItem<String?>(value, textValue = label)
                        }
                    }
                }
            }
        }
        InputRecipeField("Address") { input ->
            Input(address, { address = it }, input, placeholder = "123 Main St")
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
                    InputRecipeField(t.apiKey, Modifier.widthIn(max = 320.dp), t.description) { input ->
                        Input(apiKey, { apiKey = it }, input, type = InputType.Password, placeholder = "sk-...")
                    }
                }
            }
        }
    }
}

// endregion
