@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogViewportWidth
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldContent
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldError
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldLegend
import com.toutiaoyan.compose.ui.components.field.FieldLegendVariant
import com.toutiaoyan.compose.ui.components.field.FieldOrientation
import com.toutiaoyan.compose.ui.components.field.FieldSeparator
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.components.field.FieldTitle
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputType
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupItem
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupScope
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.slider.Slider
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

// region exported code

private val fieldDemoCode =
    """
    FieldGroup(Modifier.widthIn(max = 448.dp)) {
        FieldSet {
            FieldLegend("Payment Method")
            FieldDescription("All transactions are secure and encrypted")
            FieldGroup {
                Field("Name on Card") {
                    Input(name, { name = it }, placeholder = "Evil Rabbit", required = true)
                }
                Field("Card Number", description = "Enter your 16-digit card number") {
                    Input(number, { number = it }, placeholder = "1234 5678 9012 3456", required = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Field("Month", Modifier.weight(1f)) {
                        Select(value = month, onValueChange = { month = it }, placeholder = "MM") {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent { listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12").forEach { SelectItem(it) } }
                        }
                    }
                    Field("Year", Modifier.weight(1f)) {
                        Select(value = year, onValueChange = { year = it }, placeholder = "YYYY") {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent { (2024..2029).forEach { SelectItem(it.toString()) } }
                        }
                    }
                    Field("CVV", Modifier.weight(1f)) {
                        Input(cvv, { cvv = it }, placeholder = "123", required = true)
                    }
                }
            }
        }
        FieldSeparator()
        FieldSet {
            FieldLegend("Billing Address")
            FieldDescription("The billing address associated with your payment method")
            FieldGroup {
                Field(orientation = FieldOrientation.Horizontal) {
                    Checkbox(checked = sameAddress, onCheckedChange = { sameAddress = it }, label = "Same as shipping address")
                    Text("Same as shipping address", textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal))
                }
            }
        }
        FieldSet {
            FieldGroup {
                Field("Comments") {
                    Textarea(comments, { comments = it }, placeholder = "Add any additional comments")
                }
            }
        }
        Field(orientation = FieldOrientation.Horizontal) {
            Button("Submit", onClick = submit)
            Button("Cancel", variant = ButtonVariant.Outline, onClick = reset)
        }
    }
    """.trimIndent()

private val fieldCheckboxCode =
    """
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        FieldSet {
            FieldLegend("Show these items on the desktop", variant = FieldLegendVariant.Label)
            FieldDescription("Select the items you want to show on the desktop.")
            FieldGroup(spacing = 12.dp) {
                CheckboxField("Hard disks")
                CheckboxField("External disks")
                CheckboxField("CDs, DVDs, and iPods")
                CheckboxField("Connected servers")
            }
        }
        FieldSeparator()
        Field(orientation = FieldOrientation.Horizontal) {
            Checkbox(checked = sync, onCheckedChange = { sync = it })
            FieldContent {
                FieldLabel("Sync Desktop & Documents folders")
                FieldDescription("Your Desktop & Documents folders are being synced with iCloud Drive. You can access them from other devices.")
            }
        }
    }
    """.trimIndent()

private val fieldChoiceCardCode =
    """
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        FieldSet {
            FieldLegend("Compute Environment", variant = FieldLegendVariant.Label)
            FieldDescription("Select the compute environment for your cluster.")
            RadioGroup(value = environment, onValueChange = { environment = it }) {
                FieldLabel(selected = environment == "kubernetes", onClick = { environment = "kubernetes" }) {
                    Field(orientation = FieldOrientation.Horizontal) {
                        FieldContent {
                            FieldTitle("Kubernetes")
                            FieldDescription("Run GPU workloads on a K8s cluster.")
                        }
                        RadioGroupItem("kubernetes", label = "Kubernetes. Run GPU workloads on a K8s cluster.")
                    }
                }
                FieldLabel(selected = environment == "vm", onClick = { environment = "vm" }) {
                    Field(orientation = FieldOrientation.Horizontal) {
                        FieldContent {
                            FieldTitle("Virtual Machine")
                            FieldDescription("Access a cluster to run GPU workloads.")
                        }
                        RadioGroupItem("vm", label = "Virtual Machine. Access a cluster to run GPU workloads.")
                    }
                }
            }
        }
    }
    """.trimIndent()

private val fieldGroupCode =
    """
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        FieldSet {
            FieldLabel("Responses")
            FieldDescription("Get notified when ChatGPT responds to requests that take time, like research or image generation.")
            FieldGroup(dense = true) {
                Field(orientation = FieldOrientation.Horizontal) {
                    Checkbox(checked = true, onCheckedChange = {}, disabled = true)
                    FieldLabel("Push notifications")
                }
            }
        }
        FieldSeparator()
        FieldSet {
            FieldLabel("Tasks")
            FieldDescription("Get notified when tasks you've created have updates.")
            FieldGroup(dense = true) {
                Field(orientation = FieldOrientation.Horizontal) {
                    Checkbox(checked = checked, onCheckedChange = { checked = it })
                    FieldLabel("Push notifications")
                }
                Field(orientation = FieldOrientation.Horizontal) {
                    Checkbox(checked = email, onCheckedChange = { email = it })
                    FieldLabel("Email notifications")
                }
            }
        }
    }
    """.trimIndent()

private val fieldRtlCode =
    """
    @Composable
    fun FieldRtlPaymentForm() {
        var language by remember { mutableStateOf(1) } // 0 English, 1 Arabic, 2 Hebrew
        val inherited = currentTheme
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
                val t = fieldRtlTranslations[language]
                FieldGroup(Modifier.widthIn(max = 448.dp)) {
                    FieldSet {
                        FieldLegend(t.paymentMethod)
                        FieldDescription(t.secureTransactions)
                        FieldGroup {
                            Field(t.nameOnCard) { Input(name, { name = it }, placeholder = "Evil Rabbit", required = true) }
                            Field(t.cardNumber, description = t.cardNumberDescription) {
                                Input(number, { number = it }, placeholder = "1234 5678 9012 3456", required = true)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Field(t.month, Modifier.weight(1f)) {
                                    Select(value = month, onValueChange = { month = it }, placeholder = t.monthPlaceholder) {
                                        SelectTrigger(Modifier.fillMaxWidth())
                                        SelectContent { t.months.drop(1).forEach { SelectItem(it) } }
                                    }
                                }
                                Field(t.year, Modifier.weight(1f)) {
                                    Select(value = year, onValueChange = { year = it }, placeholder = "YYYY") {
                                        SelectTrigger(Modifier.fillMaxWidth())
                                        SelectContent { (2024..2029).forEach { SelectItem(it.toString()) } }
                                    }
                                }
                                Field(t.cvv, Modifier.weight(1f)) { Input(cvv, { cvv = it }, placeholder = "123", required = true) }
                            }
                        }
                    }
                    FieldSeparator()
                    FieldSet {
                        FieldLegend(t.billingAddress)
                        FieldDescription(t.billingAddressDescription)
                        FieldGroup {
                            Field(orientation = FieldOrientation.Horizontal) {
                                Checkbox(checked = sameAddress, onCheckedChange = { sameAddress = it })
                                FieldLabel(t.sameAsShipping)
                            }
                        }
                    }
                    FieldSet {
                        FieldGroup {
                            Field(t.comments) { Textarea(comments, { comments = it }, placeholder = t.commentsPlaceholder) }
                        }
                    }
                    Field(orientation = FieldOrientation.Horizontal) {
                        Button(t.submit, onClick = submit)
                        Button(t.cancel, variant = ButtonVariant.Outline)
                    }
                }
            }
        }
    }
    """.trimIndent()

private val fieldResponsiveCode =
    """
    FieldGroup(Modifier.widthIn(max = 512.dp)) {
        FieldSet {
            FieldLegend("Profile")
            FieldDescription("Fill in your profile information.")
            FieldGroup {
                Field(orientation = FieldOrientation.Responsive) {
                    FieldContent {
                        FieldLabel("Name")
                        FieldDescription("Provide your full name for identification")
                    }
                    Input(name, { name = it }, placeholder = "Evil Rabbit", required = true, modifier = Modifier.width(192.dp))
                }
                Field(orientation = FieldOrientation.Responsive) {
                    Button("Submit", onClick = submit)
                    Button("Cancel", variant = ButtonVariant.Outline)
                }
            }
        }
    }
    """.trimIndent()

// endregion

private val fieldImports =
    """
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import com.toutiaoyan.compose.ui.components.button.Button
    import com.toutiaoyan.compose.ui.components.button.ButtonVariant
    import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
    import com.toutiaoyan.compose.ui.components.field.*
    import com.toutiaoyan.compose.ui.components.input.Input
    import com.toutiaoyan.compose.ui.components.input.InputType
    import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
    import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupItem
    import com.toutiaoyan.compose.ui.components.select.*
    import com.toutiaoyan.compose.ui.components.slider.Slider
    import com.toutiaoyan.compose.ui.components.switch.Switch
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.components.textarea.Textarea
    """.trimIndent()

private const val fieldDescriptionCopy = "All transactions are secure and encrypted"

val fieldDoc =
    ComponentDoc(
        id = "field",
        title = "Field",
        description = "Combine labels, controls, and help text to compose accessible form fields and grouped inputs.",
        examplesHeading = null,
        usageImportsCode = fieldImports,
        usageCode =
            """
            FieldSet {
                FieldLegend("Profile")
                FieldDescription("This appears on invoices and emails.")
                FieldGroup {
                    Field("Full name") {
                        Input(value = name, onValueChange = { name = it }, placeholder = "Evil Rabbit")
                    }
                    Field("Username", error = "Choose another username.") {
                        Input(value = username, onValueChange = { username = it }, invalid = true)
                    }
                    Field(orientation = FieldOrientation.Horizontal) {
                        Switch(checked = newsletter, onCheckedChange = { newsletter = it })
                        FieldLabel("Subscribe to the newsletter")
                    }
                }
            }
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Composition",
                    emptyList(),
                ),
                ComponentGuideSection(
                    "Field",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "A single control with label, helper text, and validation.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Field
                            ├── FieldLabel
                            ├── Input / Textarea / Switch / Select
                            ├── FieldDescription
                            └── FieldError
                            """.trimIndent(),
                            language = null,
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "FieldGroup",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Related fields in one group. Use `FieldSeparator` between sections when needed.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            FieldGroup
                            ├── Field
                            │   ├── FieldLabel
                            │   ├── Input / Textarea / Switch / Select
                            │   ├── FieldDescription
                            │   └── FieldError
                            ├── FieldSeparator
                            └── Field
                                ├── FieldLabel
                                └── Input / Textarea / Switch / Select
                            """.trimIndent(),
                            language = null,
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "FieldSet",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Semantic grouping with a legend and description, usually containing a `FieldGroup`.",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            FieldSet
                            ├── FieldLegend
                            ├── FieldDescription
                            └── FieldGroup
                                ├── Field
                                │   ├── FieldLabel
                                │   ├── Input / Textarea / Switch / Select
                                │   ├── FieldDescription
                                │   └── FieldError
                                └── Field
                                    ├── FieldLabel
                                    └── Input / Textarea / Switch / Select
                            """.trimIndent(),
                            language = null,
                        ),
                    ),
                    level = 3,
                ),
                ComponentGuideSection(
                    "Anatomy",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The `Field` family is designed for composing accessible forms. " +
                                "A typical field is structured as follows:",
                        ),
                        ComponentGuideBlock.Code(
                            """
                            <Field>
                              <FieldLabel htmlFor="input-id">Label</FieldLabel>
                              {/* Input, Select, Switch, etc. */}
                              <FieldDescription>Optional helper text.</FieldDescription>
                              <FieldError>Validation message.</FieldError>
                            </Field>
                            """.trimIndent(),
                            language = "text",
                        ),
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "`Field` is the core wrapper for a single field.",
                                "`FieldContent` is a flex column that groups label and description. " +
                                    "Not required if you have no description.",
                                "Wrap related fields with `FieldGroup`, and use `FieldSet` with " +
                                    "`FieldLegend` for semantic grouping.",
                            ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Form",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "See the [Form](https://ui.shadcn.com/docs/forms) documentation for building " +
                                "forms with the `Field` component and React Hook Form, Tanstack Form, or " +
                                "Formisch. Compose hoists form state to the caller instead of depending " +
                                "on a form library. In the form previews, Submit focuses the first empty " +
                                "required field. " +
                                "Once the required fields are filled, submitting resets the preview.",
                        ),
                    ),
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Validation and Errors",
                    listOf(
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "Add `invalid` to `Field` to switch the entire block into an error state.",
                                "Add `invalid` on the input itself for assistive technologies.",
                                "Render `FieldError` immediately after the control or inside " +
                                    "`FieldContent` to keep error messages aligned with the field.",
                            ),
                        ),
                        ComponentGuideBlock.Code(
                            """
                            Field("Email", invalid = true) {
                                Input(value = email, onValueChange = { email = it }, invalid = true)
                                FieldError("Enter a valid email address.")
                            }
                            """.trimIndent(),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Accessibility",
                    listOf(
                        ComponentGuideBlock.Bullets(
                            listOf(
                                "`FieldSet` and `FieldLegend` visually group related controls. " +
                                    "Compose does not emit native HTML fieldset or legend elements.",
                                "Provide accessible labels on the controls themselves. `FieldLabel` does not " +
                                    "automatically associate with another control; checkbox, radio, and switch " +
                                    "examples wire label activation explicitly.",
                                "Use `FieldSeparator` for visual section boundaries and provide meaningful " +
                                    "control labels independently.",
                            ),
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "FieldSet",
                    "Container that visually groups related controls with spacing presets.",
                    listOf(ApiProperty("modifier", "Modifier", "Modifier")),
                ),
                ComponentApiSection(
                    "FieldLegend",
                    "Legend element for a `FieldSet`. Switch to the `label` variant to align with label sizing.",
                    listOf(
                        ApiProperty("variant", "\"legend\" | \"label\"", "\"legend\""),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
                ComponentApiSection(
                    "FieldGroup",
                    "Layout wrapper that stacks `Field` components and enables container queries for " +
                        "responsive orientations.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("spacing", "Dp?", "null (20dp Nova)"),
                    ),
                ),
                ComponentApiSection(
                    "Field",
                    "The core wrapper for a single field. Provides orientation control, invalid state " +
                        "styling, and spacing.",
                    listOf(
                        ApiProperty("orientation", "\"vertical\" | \"horizontal\" | \"responsive\"", "\"vertical\""),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("invalid", "Boolean", "false"),
                    ),
                ),
                ComponentApiSection(
                    "FieldContent",
                    "Flex column that groups control and descriptions when the label sits beside the control. " +
                        "Not required if you have no description.",
                    listOf(ApiProperty("modifier", "Modifier", "Modifier")),
                ),
                ComponentApiSection(
                    "FieldLabel",
                    "Label styled for both direct inputs and nested `Field` children.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("required", "Boolean", "false"),
                    ),
                ),
                ComponentApiSection(
                    "FieldTitle",
                    "Renders a title with label styling inside `FieldContent`.",
                    listOf(ApiProperty("modifier", "Modifier", "Modifier")),
                ),
                ComponentApiSection(
                    "FieldDescription",
                    "Helper text slot with muted styling and normal line height.",
                    listOf(ApiProperty("modifier", "Modifier", "Modifier")),
                ),
                ComponentApiSection(
                    "FieldSeparator",
                    "Visual divider to separate sections inside a `FieldGroup`. Accepts optional inline content.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("label", "String?", "null"),
                    ),
                ),
                ComponentApiSection(
                    "FieldError",
                    "Accessible error container that accepts children or an `errors` array, " +
                        "matching react-hook-form-style lists.",
                    listOf(
                        ApiProperty("errors", "List<String>", "emptyList()"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    minPreviewHeight = 850.dp,
                    mobileMinPreviewHeight = 800.dp,
                    previewPadding = 24.dp,
                    previewVerticalAlignment = Alignment.CenterVertically,
                    code = fieldDemoCode,
                    preview = { FieldDemoRecipe() },
                ),
                ComponentExample(
                    title = "Input",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        FieldSet(Modifier.widthIn(max = 320.dp)) {
                            FieldGroup {
                                Field("Username") {
                                    Input(value = user, onValueChange = { user = it }, placeholder = "Max Leiter")
                                    FieldDescription("Choose a unique username for your account.")
                                }
                                Field("Password") {
                                    FieldDescription("Must be at least 8 characters long.")
                                    Input(value = password, onValueChange = { password = it },
                                        type = InputType.Password, placeholder = "••••••••")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { FieldInputRecipe() },
                ),
                ComponentExample(
                    title = "Textarea",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        FieldSet(Modifier.widthIn(max = 320.dp)) {
                            FieldGroup {
                                Field("Feedback") {
                                    Textarea(value = feedback, onValueChange = { feedback = it },
                                        placeholder = "Your feedback helps us improve...", rows = 4)
                                    FieldDescription("Share your thoughts about our service.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { FieldTextareaRecipe() },
                ),
                ComponentExample(
                    title = "Select",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Department")
                            Select(value = department, onValueChange = { department = it },
                                placeholder = "Choose department") {
                                SelectTrigger(Modifier.fillMaxWidth())
                                SelectContent { departments.forEach { SelectItem(it) } }
                            }
                            FieldDescription("Select your department or area of work.")
                        }
                        """.trimIndent(),
                    preview = { FieldSelectRecipe() },
                ),
                ComponentExample(
                    title = "Slider",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        var range by remember { mutableStateOf(200f..800f) }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldTitle("Price Range")
                            FieldDescription(
                                "Set your budget range (\${'$'}${'$'}{range.start.toInt()} - ${'$'}{range.endInclusive.toInt()}).",
                            )
                            Slider(
                                value = range,
                                onValueChange = { range = it },
                                modifier = Modifier.padding(top = 8.dp),
                                valueRange = 0f..1000f,
                                step = 10f,
                                label = "Price Range",
                            )
                        }
                        """.trimIndent(),
                    preview = { FieldSliderRecipe() },
                ),
                ComponentExample(
                    title = "Fieldset",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        FieldSet(Modifier.widthIn(max = 384.dp)) {
                            FieldLegend("Address Information")
                            FieldDescription("We need your address to deliver your order.")
                            FieldGroup {
                                Field("Street Address") {
                                    Input(value = street, onValueChange = { street = it }, placeholder = "123 Main St")
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Field("City", Modifier.weight(1f)) {
                                        Input(value = city, onValueChange = { city = it }, placeholder = "New York")
                                    }
                                    Field("Postal Code", Modifier.weight(1f)) {
                                        Input(value = zip, onValueChange = { zip = it }, placeholder = "90502")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { FieldFieldsetRecipe() },
                ),
                ComponentExample(
                    title = "Checkbox",
                    minPreviewHeight = 432.dp,
                    mobileMinPreviewHeight = 432.dp,
                    previewPadding = 40.dp,
                    code = fieldCheckboxCode,
                    preview = { FieldCheckboxRecipe() },
                ),
                ComponentExample(
                    title = "Radio",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        FieldSet(Modifier.widthIn(max = 320.dp)) {
                            FieldLegend("Subscription Plan", variant = FieldLegendVariant.Label)
                            FieldDescription("Yearly and lifetime plans offer significant savings.")
                            RadioGroup(value = plan, onValueChange = { plan = it }, contentDescription = "Subscription Plan") {
                                Field(orientation = FieldOrientation.Horizontal) {
                                    RadioGroupItem("monthly", label = "Monthly (${'$'}9.99/month)")
                                    FieldLabel("Monthly (${'$'}9.99/month)", Modifier.clickable { plan = "monthly" })
                                }
                                Field(orientation = FieldOrientation.Horizontal) {
                                    RadioGroupItem("yearly", label = "Yearly (${'$'}99.99/year)")
                                    FieldLabel("Yearly (${'$'}99.99/year)", Modifier.clickable { plan = "yearly" })
                                }
                                Field(orientation = FieldOrientation.Horizontal) {
                                    RadioGroupItem("lifetime", label = "Lifetime (${'$'}299.99)")
                                    FieldLabel("Lifetime (${'$'}299.99)", Modifier.clickable { plan = "lifetime" })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { FieldRadioRecipe() },
                ),
                ComponentExample(
                    title = "Switch",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code =
                        """
                        val label = "Multi-factor authentication"
                        Field(orientation = FieldOrientation.Horizontal, modifier = Modifier.width(IntrinsicSize.Max)) {
                            FieldLabel(label, Modifier.clickable { enabled = !enabled })
                            Switch(checked = enabled, onCheckedChange = { enabled = it }, label = label)
                        }
                        """.trimIndent(),
                    preview = { FieldSwitchRecipe() },
                ),
                ComponentExample(
                    title = "Choice Card",
                    descriptionMarkdown =
                        "Wrap `Field` components inside `FieldLabel` to create selectable field groups. " +
                            "This works with `RadioItem`, `Checkbox` and `Switch` components.",
                    minPreviewHeight = 288.dp,
                    previewPadding = 40.dp,
                    code = fieldChoiceCardCode,
                    preview = { FieldChoiceCardRecipe() },
                ),
                ComponentExample(
                    title = "Field Group",
                    descriptionMarkdown =
                        "Stack `Field` components with `FieldGroup`. Add `FieldSeparator` to divide them.",
                    minPreviewHeight = 384.dp,
                    mobileMinPreviewHeight = 384.dp,
                    previewPadding = 40.dp,
                    code = fieldGroupCode,
                    preview = { FieldGroupRecipe() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    minPreviewHeight = 640.dp,
                    mobileMinPreviewHeight = 640.dp,
                    previewPadding = 0.dp,
                    code = fieldRtlCode,
                    preview = { FieldRtlRecipe() },
                ),
                ComponentExample(
                    title = "Responsive Layout",
                    descriptionMarkdown =
                        "• Vertical fields: Default orientation stacks label, control, and helper " +
                            "text—ideal for mobile-first layouts.\n" +
                            "• Horizontal fields: Set `orientation = FieldOrientation.Horizontal` on " +
                            "`Field` to align the label and control side-by-side. Pair with `FieldContent` " +
                            "to keep descriptions aligned.\n" +
                            "• Responsive fields: Set `orientation = FieldOrientation.Responsive` for " +
                            "automatic column layouts inside container-aware parents. A `FieldGroup` switches " +
                            "orientations at the 448dp container breakpoint.",
                    minPreviewHeight = 500.dp,
                    mobileMinPreviewHeight = 650.dp,
                    previewPadding = 0.dp,
                    code = fieldResponsiveCode,
                    preview = { FieldResponsiveRecipe() },
                ),
            ),
    )

// region helpers

/** Real shadcn `FieldLabel className="font-normal"` — 14/19.25 regular, not a tab stop. */
@Composable
private fun FieldNormalLabel(
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Text(
        modifier =
            Modifier.then(
                if (onClick != null) {
                    Modifier.focusProperties { canFocus = false }.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            ),
        textStyle =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 19.25.sp,
                fontWeight = FontWeight.Normal,
            ),
    ) { text }
}

/** Muted 14/21 description body, used where the official copy mixes weights inline. */
@Composable
private fun fieldDescriptionStyle(): TextStyle =
    currentTheme.typography.bodyMedium.merge(TextColors.Muted).copy(
        lineHeight = 21.sp,
        lineHeightStyle =
            LineHeightStyle(
                LineHeightStyle.Alignment.Center,
                LineHeightStyle.Trim.None,
                LineHeightStyle.Mode.Tight,
            ),
    )

@Composable
private fun FieldCheckboxRow(
    text: String,
    defaultChecked: Boolean = false,
    disabled: Boolean = false,
) {
    var checked by remember { mutableStateOf(defaultChecked) }
    val focus = remember { FocusRequester() }
    Field(orientation = FieldOrientation.Horizontal) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            disabled = disabled,
            label = text,
            modifier = Modifier.focusRequester(focus),
        )
        FieldNormalLabel(
            text,
            onClick =
                if (disabled) {
                    null
                } else {
                    {
                        checked = !checked
                        focus.requestFocus()
                    }
                },
        )
    }
}

@Composable
private fun RadioGroupScope.FieldRadioRow(
    value: String,
    text: String,
    onSelect: () -> Unit,
) {
    val focus = remember { FocusRequester() }
    Field(orientation = FieldOrientation.Horizontal) {
        RadioGroupItem(value, modifier = Modifier.focusRequester(focus), label = text)
        FieldNormalLabel(
            text,
            onClick = {
                onSelect()
                focus.requestFocus()
            },
        )
    }
}

private val months = listOf("MM") + (1..12).map { it.toString().padStart(2, '0') }
private val years = listOf("YYYY") + (2024..2029).map { it.toString() }
private val departments =
    listOf(
        "Choose department",
        "Engineering",
        "Design",
        "Marketing",
        "Sales",
        "Customer Support",
        "Human Resources",
        "Finance",
        "Operations",
    )

private class RequiredPreviewText {
    var value by mutableStateOf("")
        private set
    var missing by mutableStateOf(false)
        private set
    private val focus = FocusRequester()
    private var focusRequest by mutableStateOf(0)

    fun edit(value: String) {
        this.value = value
        if (value.isNotEmpty()) missing = false
    }

    fun reset() {
        value = ""
        missing = false
    }

    fun reportMissing() {
        missing = true
        focusRequest++
    }

    @Composable
    fun Input(
        placeholder: String,
        modifier: Modifier = Modifier,
    ) {
        LaunchedEffect(focusRequest) {
            if (missing) focus.requestFocus()
        }
        Input(
            value = value,
            onValueChange = ::edit,
            modifier = modifier.focusRequester(focus),
            placeholder = placeholder,
            invalid = missing,
            required = true,
        )
    }
}

private fun submitFieldPreview(
    vararg inputs: RequiredPreviewText,
    onValid: () -> Unit,
) {
    val missing = inputs.firstOrNull { it.value.isEmpty() }
    if (missing == null) onValid() else missing.reportMissing()
}

private const val requiredFieldMessage = "Please fill out this field."

@Composable
private fun FieldDemoRecipe() {
    val name = remember { RequiredPreviewText() }
    val number = remember { RequiredPreviewText() }
    val cvv = remember { RequiredPreviewText() }
    var month by remember { mutableStateOf<String?>(null) }
    var year by remember { mutableStateOf<String?>(null) }
    var comments by remember { mutableStateOf("") }
    var sameAddress by remember { mutableStateOf(true) }
    val reset = {
        name.reset()
        number.reset()
        cvv.reset()
        month = null
        year = null
        comments = ""
        sameAddress = true
    }
    FieldGroup(Modifier.widthIn(max = 448.dp)) {
        FieldSet {
            FieldLegend("Payment Method")
            FieldDescription(fieldDescriptionCopy)
            FieldGroup {
                Field("Name on Card", error = requiredFieldMessage.takeIf { name.missing }) {
                    name.Input("Evil Rabbit")
                }
                Field(
                    "Card Number",
                    description = "Enter your 16-digit card number",
                    error = requiredFieldMessage.takeIf { number.missing },
                ) {
                    number.Input("1234 5678 9012 3456")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Field("Month", Modifier.weight(1f)) {
                        Select<String?>(
                            value = month,
                            onValueChange = { month = it },
                            placeholder = "MM",
                        ) {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent {
                                SelectItem<String?>(null, textValue = "MM")
                                months.drop(1).forEach { SelectItem<String?>(it, textValue = it) }
                            }
                        }
                    }
                    Field("Year", Modifier.weight(1f)) {
                        Select<String?>(
                            value = year,
                            onValueChange = { year = it },
                            placeholder = "YYYY",
                        ) {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent {
                                SelectItem<String?>(null, textValue = "YYYY")
                                years.drop(1).forEach { SelectItem<String?>(it, textValue = it) }
                            }
                        }
                    }
                    Field("CVV", Modifier.weight(1f), error = requiredFieldMessage.takeIf { cvv.missing }) {
                        cvv.Input("123")
                    }
                }
            }
        }
        FieldSeparator()
        FieldSet {
            FieldLegend("Billing Address")
            FieldDescription("The billing address associated with your payment method")
            FieldGroup {
                Field(orientation = FieldOrientation.Horizontal) {
                    Checkbox(
                        checked = sameAddress,
                        onCheckedChange = { sameAddress = it },
                        label = "Same as shipping address",
                    )
                    FieldNormalLabel(
                        "Same as shipping address",
                        onClick = { sameAddress = !sameAddress },
                    )
                }
            }
        }
        FieldSet {
            FieldGroup {
                Field("Comments") {
                    Textarea(comments, { comments = it }, placeholder = "Add any additional comments")
                }
            }
        }
        Field(orientation = FieldOrientation.Horizontal) {
            Button("Submit", onClick = { submitFieldPreview(name, number, cvv, onValid = reset) })
            Button("Cancel", variant = ButtonVariant.Outline, onClick = reset)
        }
    }
}

@Composable
private fun FieldInputRecipe() {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    FieldSet(Modifier.widthIn(max = 320.dp)) {
        FieldGroup {
            Field("Username") {
                Input(user, { user = it }, placeholder = "Max Leiter")
                FieldDescription("Choose a unique username for your account.")
            }
            Field("Password") {
                FieldDescription("Must be at least 8 characters long.")
                Input(password, { password = it }, type = InputType.Password, placeholder = "••••••••")
            }
        }
    }
}

@Composable
private fun FieldTextareaRecipe() {
    var feedback by remember { mutableStateOf("") }
    FieldSet(Modifier.widthIn(max = 320.dp)) {
        FieldGroup {
            Field("Feedback") {
                Textarea(
                    feedback,
                    { feedback = it },
                    placeholder = "Your feedback helps us improve...",
                    rows = 4,
                )
                FieldDescription("Share your thoughts about our service.")
            }
        }
    }
}

@Composable
private fun FieldSelectRecipe() {
    var department by remember { mutableStateOf<String?>(null) }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldLabel("Department")
        Select<String?>(
            value = department,
            onValueChange = { department = it },
            placeholder = "Choose department",
        ) {
            SelectTrigger(Modifier.fillMaxWidth())
            SelectContent {
                SelectItem<String?>(null, textValue = "Choose department")
                departments.drop(1).forEach { SelectItem<String?>(it, textValue = it) }
            }
        }
        FieldDescription("Select your department or area of work.")
    }
}

@Composable
private fun FieldSliderRecipe() {
    var range by remember { mutableStateOf(200f..800f) }
    Field(Modifier.widthIn(max = 320.dp)) {
        FieldTitle("Price Range")
        Text(
            textStyle = fieldDescriptionStyle(),
        ) {
            buildAnnotatedString {
                append("Set your budget range ($")
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(range.start.toInt().toString())
                }
                append(" - ")
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(range.endInclusive.toInt().toString())
                }
                append(").")
            }
        }
        Slider(
            value = range,
            onValueChange = { range = it },
            valueRange = 0f..1000f,
            step = 10f,
            label = "Price Range",
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun FieldFieldsetRecipe() {
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    FieldSet(Modifier.widthIn(max = 384.dp)) {
        FieldLegend("Address Information")
        FieldDescription("We need your address to deliver your order.")
        FieldGroup {
            Field("Street Address") {
                Input(street, { street = it }, placeholder = "123 Main St")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Field("City", Modifier.weight(1f)) {
                    Input(city, { city = it }, placeholder = "New York")
                }
                Field("Postal Code", Modifier.weight(1f)) {
                    Input(zip, { zip = it }, placeholder = "90502")
                }
            }
        }
    }
}

@Composable
private fun FieldCheckboxRecipe() {
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        FieldSet {
            FieldLegend("Show these items on the desktop", variant = FieldLegendVariant.Label)
            FieldDescription("Select the items you want to show on the desktop.")
            FieldGroup(spacing = 12.dp) {
                FieldCheckboxRow("Hard disks")
                FieldCheckboxRow("External disks")
                FieldCheckboxRow("CDs, DVDs, and iPods")
                FieldCheckboxRow("Connected servers")
            }
        }
        FieldSeparator()
        var sync by remember { mutableStateOf(true) }
        Field(orientation = FieldOrientation.Horizontal) {
            Checkbox(
                checked = sync,
                onCheckedChange = { sync = it },
                label = "Sync Desktop & Documents folders",
            )
            FieldContent {
                FieldNormalLabel("Sync Desktop & Documents folders", onClick = { sync = !sync })
                FieldDescription(
                    "Your Desktop & Documents folders are being synced with iCloud Drive. " +
                        "You can access them from other devices.",
                )
            }
        }
    }
}

@Composable
private fun FieldRadioRecipe() {
    var plan by remember { mutableStateOf("monthly") }
    FieldSet(Modifier.widthIn(max = 320.dp)) {
        FieldLegend("Subscription Plan", variant = FieldLegendVariant.Label)
        FieldDescription("Yearly and lifetime plans offer significant savings.")
        RadioGroup(
            value = plan,
            onValueChange = { plan = it },
            contentDescription = "Subscription Plan",
        ) {
            FieldRadioRow("monthly", "Monthly ($9.99/month)") { plan = "monthly" }
            FieldRadioRow("yearly", "Yearly ($99.99/year)") { plan = "yearly" }
            FieldRadioRow("lifetime", "Lifetime ($299.99)") { plan = "lifetime" }
        }
    }
}

@Composable
private fun FieldSwitchRecipe() {
    var enabled by remember { mutableStateOf(false) }
    val focus = remember { FocusRequester() }
    val label = "Multi-factor authentication"
    Field(orientation = FieldOrientation.Horizontal, modifier = Modifier.width(IntrinsicSize.Max)) {
        FieldLabel(
            label,
            Modifier.focusProperties { canFocus = false }.clickable {
                enabled = !enabled
                focus.requestFocus()
            },
        )
        Switch(
            checked = enabled,
            onCheckedChange = { enabled = it },
            modifier = Modifier.focusRequester(focus),
            label = label,
        )
    }
}

@Composable
private fun FieldChoiceCardRecipe() {
    var environment by remember { mutableStateOf("kubernetes") }
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        FieldSet {
            FieldLegend("Compute Environment", variant = FieldLegendVariant.Label)
            FieldDescription("Select the compute environment for your cluster.")
            RadioGroup(
                value = environment,
                onValueChange = { environment = it },
                contentDescription = "Compute Environment",
            ) {
                ChoiceCardOption(
                    value = "kubernetes",
                    title = "Kubernetes",
                    description = "Run GPU workloads on a K8s cluster.",
                    selected = environment == "kubernetes",
                    onSelect = { environment = "kubernetes" },
                )
                ChoiceCardOption(
                    value = "vm",
                    title = "Virtual Machine",
                    description = "Access a cluster to run GPU workloads.",
                    selected = environment == "vm",
                    onSelect = { environment = "vm" },
                )
            }
        }
    }
}

@Composable
private fun RadioGroupScope.ChoiceCardOption(
    value: String,
    title: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val focus = remember { FocusRequester() }
    FieldLabel(
        selected = selected,
        onClick = {
            onSelect()
            focus.requestFocus()
        },
    ) {
        Field(orientation = FieldOrientation.Horizontal) {
            FieldContent {
                FieldTitle(title)
                FieldDescription(description)
            }
            RadioGroupItem(value, modifier = Modifier.focusRequester(focus), label = "$title. $description")
        }
    }
}

@Composable
private fun FieldGroupRecipe() {
    FieldGroup(Modifier.widthIn(max = 320.dp)) {
        FieldSet {
            FieldLabel("Responses")
            FieldDescription(
                "Get notified when ChatGPT responds to requests that take time, like research " +
                    "or image generation.",
            )
            FieldGroup(dense = true) {
                FieldCheckboxRow("Push notifications", defaultChecked = true, disabled = true)
            }
        }
        FieldSeparator()
        FieldSet {
            FieldLabel("Tasks")
            Text(
                textStyle = fieldDescriptionStyle(),
            ) {
                buildAnnotatedString {
                    append("Get notified when tasks you've created have updates. ")
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Manage tasks")
                    }
                }
            }
            FieldGroup(dense = true) {
                FieldCheckboxRow("Push notifications")
                FieldCheckboxRow("Email notifications")
            }
        }
    }
}

@Composable
private fun FieldResponsiveRecipe() {
    val mobile = catalogViewportWidth() < 640.dp
    Box(
        Modifier.fillMaxWidth().padding(if (mobile) 24.dp else 40.dp),
        contentAlignment = Alignment.Center,
    ) {
        val name = remember { RequiredPreviewText() }
        FieldGroup(Modifier.widthIn(max = 512.dp)) {
            FieldSet {
                FieldLegend("Profile")
                FieldDescription("Fill in your profile information.")
                FieldGroup {
                    Field(orientation = FieldOrientation.Responsive, invalid = name.missing) {
                        FieldContent {
                            FieldLabel("Name")
                            FieldDescription("Provide your full name for identification")
                            if (name.missing) FieldError(requiredFieldMessage)
                        }
                        name.Input("Evil Rabbit", Modifier.width(192.dp))
                    }
                    Field(orientation = FieldOrientation.Responsive) {
                        Button("Submit", onClick = { submitFieldPreview(name, onValid = name::reset) })
                        Button("Cancel", variant = ButtonVariant.Outline)
                    }
                }
            }
        }
    }
}

// endregion helpers

// region RTL payment form

private class FieldRtlStrings(
    val paymentMethod: String,
    val secureTransactions: String,
    val nameOnCard: String,
    val cardNumber: String,
    val cardNumberDescription: String,
    val month: String,
    val year: String,
    val cvv: String,
    val monthPlaceholder: String,
    val billingAddress: String,
    val billingAddressDescription: String,
    val sameAsShipping: String,
    val comments: String,
    val commentsPlaceholder: String,
    val submit: String,
    val cancel: String,
    val requiredFieldMessage: String,
    val months: List<String>,
)

private val arabicDigitMap =
    mapOf(
        '0' to '٠',
        '1' to '١',
        '2' to '٢',
        '3' to '٣',
        '4' to '٤',
        '5' to '٥',
        '6' to '٦',
        '7' to '٧',
        '8' to '٨',
        '9' to '٩',
    )

private val fieldRtlTranslations =
    listOf(
        FieldRtlStrings(
            paymentMethod = "Payment Method",
            secureTransactions = fieldDescriptionCopy,
            nameOnCard = "Name on Card",
            cardNumber = "Card Number",
            cardNumberDescription = "Enter your 16-digit card number",
            month = "Month",
            year = "Year",
            cvv = "CVV",
            monthPlaceholder = "MM",
            billingAddress = "Billing Address",
            billingAddressDescription = "The billing address associated with your payment method",
            sameAsShipping = "Same as shipping address",
            comments = "Comments",
            commentsPlaceholder = "Add any additional comments",
            submit = "Submit",
            cancel = "Cancel",
            requiredFieldMessage = requiredFieldMessage,
            months = months,
        ),
        FieldRtlStrings(
            paymentMethod = "طريقة الدفع",
            secureTransactions = "جميع المعاملات آمنة ومشفرة",
            nameOnCard = "الاسم على البطاقة",
            cardNumber = "رقم البطاقة",
            cardNumberDescription = "أدخل رقم البطاقة المكون من 16 رقمًا",
            month = "الشهر",
            year = "السنة",
            cvv = "CVV",
            monthPlaceholder = "MM",
            billingAddress = "عنوان الفوترة",
            billingAddressDescription = "عنوان الفوترة المرتبط بطريقة الدفع الخاصة بك",
            sameAsShipping = "نفس عنوان الشحن",
            comments = "تعليقات",
            commentsPlaceholder = "أضف أي تعليقات إضافية",
            submit = "إرسال",
            cancel = "إلغاء",
            requiredFieldMessage = "يرجى ملء هذا الحقل.",
            months = listOf("ش.ش") + (1..12).map { arabicDigits(it.toString().padStart(2, '0')) },
        ),
        FieldRtlStrings(
            paymentMethod = "אמצעי תשלום",
            secureTransactions = "כל העסקאות מאובטחות ומוצפנות",
            nameOnCard = "שם על הכרטיס",
            cardNumber = "מספר כרטיס",
            cardNumberDescription = "הזן את מספר הכרטיס בן 16 הספרות שלך",
            month = "חודש",
            year = "שנה",
            cvv = "CVV",
            monthPlaceholder = "MM",
            billingAddress = "כתובת חיוב",
            billingAddressDescription = "כתובת החיוב המשויכת לאמצעי התשלום שלך",
            sameAsShipping = "זהה לכתובת המשלוח",
            comments = "הערות",
            commentsPlaceholder = "הוסף הערות נוספות",
            submit = "שלח",
            cancel = "בטל",
            requiredFieldMessage = "יש למלא את השדה הזה.",
            months = months,
        ),
    )

private fun arabicDigits(value: String): String = value.map { arabicDigitMap[it] ?: it }.joinToString("")

@Composable
private fun FieldRtlRecipe() {
    var language by remember { mutableStateOf(1) }
    val inherited = currentTheme
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(
            language,
            listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
            { language = it },
        )
        Box(
            Modifier.fillMaxWidth().heightIn(min = 577.dp).padding(40.dp),
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
                    FieldRtlPaymentForm(fieldRtlTranslations[language])
                }
            }
        }
    }
}

@Composable
private fun FieldRtlPaymentForm(t: FieldRtlStrings) {
    val name = remember { RequiredPreviewText() }
    val number = remember { RequiredPreviewText() }
    val cvv = remember { RequiredPreviewText() }
    var month by remember { mutableStateOf<String?>(null) }
    var year by remember { mutableStateOf<String?>(null) }
    var comments by remember { mutableStateOf("") }
    var sameAddress by remember { mutableStateOf(true) }
    val reset = {
        name.reset()
        number.reset()
        cvv.reset()
        month = null
        year = null
        comments = ""
        sameAddress = true
    }
    FieldGroup(Modifier.widthIn(max = 448.dp)) {
        FieldSet {
            FieldLegend(t.paymentMethod)
            FieldDescription(t.secureTransactions)
            FieldGroup {
                Field(t.nameOnCard, error = t.requiredFieldMessage.takeIf { name.missing }) {
                    name.Input("Evil Rabbit")
                }
                Field(
                    t.cardNumber,
                    description = t.cardNumberDescription,
                    error = t.requiredFieldMessage.takeIf { number.missing },
                ) {
                    number.Input("1234 5678 9012 3456")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Field(t.month, Modifier.weight(1f)) {
                        Select(
                            value = month,
                            onValueChange = { month = it },
                            placeholder = t.monthPlaceholder,
                        ) {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent { t.months.drop(1).forEach { SelectItem(it) } }
                        }
                    }
                    Field(t.year, Modifier.weight(1f)) {
                        Select(value = year, onValueChange = { year = it }, placeholder = "YYYY") {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent { (2024..2029).forEach { SelectItem(it.toString()) } }
                        }
                    }
                    Field(t.cvv, Modifier.weight(1f), error = t.requiredFieldMessage.takeIf { cvv.missing }) {
                        cvv.Input("123")
                    }
                }
            }
        }
        FieldSeparator()
        FieldSet {
            FieldLegend(t.billingAddress)
            FieldDescription(t.billingAddressDescription)
            FieldGroup {
                Field(orientation = FieldOrientation.Horizontal) {
                    Checkbox(
                        checked = sameAddress,
                        onCheckedChange = { sameAddress = it },
                        label = t.sameAsShipping,
                    )
                    FieldNormalLabel(t.sameAsShipping, onClick = { sameAddress = !sameAddress })
                }
            }
        }
        FieldSet {
            FieldGroup {
                Field(t.comments) {
                    Textarea(comments, { comments = it }, placeholder = t.commentsPlaceholder)
                }
            }
        }
        Field(orientation = FieldOrientation.Horizontal) {
            Button(t.submit, onClick = { submitFieldPreview(name, number, cvv, onValid = reset) })
            Button(t.cancel, variant = ButtonVariant.Outline, onClick = reset)
        }
    }
}

// endregion
