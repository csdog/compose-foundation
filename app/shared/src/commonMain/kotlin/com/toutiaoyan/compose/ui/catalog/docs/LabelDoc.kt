@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldOrientation
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldLegend
import com.toutiaoyan.compose.ui.components.field.FieldSeparator
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.textarea.Textarea

private val labelMonths = (1..12).map { it.toString().padStart(2, '0') }
private val labelYears = (2024..2029).map { it.toString() }

// The control owns semantics and keyboard focus. Its visible label forwards pointer activation.
@Composable
private fun Modifier.activateLabel(onActivate: () -> Unit): Modifier {
    val action by rememberUpdatedState(onActivate)
    return pointerInput(Unit) { detectTapGestures { action() } }
}

@Composable
private fun LabelCheckboxDemo(
    label: String = "Accept terms and conditions",
    modifier: Modifier = Modifier,
) {
    var checked by remember { mutableStateOf(false) }
    val checkboxFocus = remember { FocusRequester() }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier.focusRequester(checkboxFocus),
            label = label,
        )
        Label(
            label,
            modifier =
                Modifier.activateLabel {
                    checked = !checked
                    checkboxFocus.requestFocus()
                },
        )
    }
}

@Composable
private fun LabeledFieldControl(
    name: String,
    required: Boolean = false,
    content: @Composable (Modifier) -> Unit,
) {
    val focus = remember { FocusRequester() }
    FieldLabel(name, modifier = Modifier.activateLabel { focus.requestFocus() })
    content(
        Modifier.focusRequester(focus).semantics { contentDescription = if (required) "$name, required" else name },
    )
}

@Composable
private fun LabelExpiryField(
    name: String,
    placeholder: String,
    value: String?,
    onValueChange: (String?) -> Unit,
    options: List<String>,
    modifier: Modifier,
) {
    val focus = remember { FocusRequester() }
    var open by remember { mutableStateOf(false) }
    Field(modifier) {
        FieldLabel(
            name,
            modifier =
                Modifier.activateLabel {
                    focus.requestFocus()
                    open = true
                },
        )
        Select(
            value = value,
            onValueChange = onValueChange,
            open = open,
            onOpenChange = { open = it },
            placeholder = placeholder,
        ) {
            SelectTrigger(
                Modifier.fillMaxWidth().focusRequester(focus),
                accessibilityLabel = "$name, ${value ?: placeholder}",
            )
            SelectContent {
                SelectItem<String?>(null, textValue = placeholder)
                options.forEach { SelectItem<String?>(it, textValue = it) }
            }
        }
    }
}

@Composable
private fun LabelInFieldDemo() {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var month by remember { mutableStateOf<String?>(null) }
    var year by remember { mutableStateOf<String?>(null) }
    var comments by remember { mutableStateOf("") }
    var sameAddress by remember { mutableStateOf(true) }
    val addressFocus = remember { FocusRequester() }
    val addressLabel = "Same as shipping address"
    FieldGroup(Modifier.widthIn(max = 448.dp)) {
        FieldSet {
            FieldLegend("Payment Method")
            FieldDescription("All transactions are secure and encrypted")
            FieldGroup {
                Field {
                    LabeledFieldControl("Name on Card", required = true) { modifier ->
                        Input(name, { name = it }, modifier, placeholder = "Evil Rabbit", required = true)
                    }
                }
                Field {
                    LabeledFieldControl("Card Number", required = true) { modifier ->
                        Input(number, { number = it }, modifier, placeholder = "1234 5678 9012 3456", required = true)
                    }
                    FieldDescription("Enter your 16-digit card number")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LabelExpiryField("Month", "MM", month, { month = it }, labelMonths, Modifier.weight(1f))
                    LabelExpiryField("Year", "YYYY", year, { year = it }, labelYears, Modifier.weight(1f))
                    Field(Modifier.weight(1f)) {
                        LabeledFieldControl("CVV", required = true) { modifier ->
                            Input(cvv, { cvv = it }, modifier, placeholder = "123", required = true)
                        }
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
                        modifier = Modifier.focusRequester(addressFocus),
                        label = addressLabel,
                    )
                    Text(
                        modifier =
                            Modifier.activateLabel {
                                sameAddress = !sameAddress
                                addressFocus.requestFocus()
                            },
                        textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                    ) { addressLabel }
                }
            }
        }
        FieldSet {
            FieldGroup {
                Field {
                    LabeledFieldControl("Comments") { modifier ->
                        Textarea(comments, { comments = it }, modifier, placeholder = "Add any additional comments")
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button("Submit")
            Button(
                "Cancel",
                variant = ButtonVariant.Outline,
                onClick = {
                    name = ""
                    number = ""
                    cvv = ""
                    month = null
                    year = null
                    comments = ""
                    sameAddress = true
                },
            )
        }
    }
}

@Composable
private fun LabelRtlDemo() {
    var language by remember { mutableStateOf(1) }
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(
            language,
            listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
            { language = it },
        )
        LabelRtlContent(language)
    }
}

@Composable
private fun LabelRtlContent(language: Int) {
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
    ) {
        Box(Modifier.fillMaxWidth().heightIn(min = 288.dp).padding(40.dp), contentAlignment = Alignment.Center) {
            LabelCheckboxDemo(
                label =
                    when (language) {
                        0 -> "Accept terms and conditions"
                        1 -> "قبول الشروط والأحكام"
                        else -> "קבל תנאים והגבלות"
                    },
            )
        }
    }
}

private val labelFieldGuide =
    ComponentGuideSection(
        title = "Label in Field",
        blocks =
            listOf(
                ComponentGuideBlock.Paragraph(
                    "For form fields, use the `Field` component which includes built-in `FieldLabel`, " +
                        "`FieldDescription`, and `FieldError` components.",
                ),
                ComponentGuideBlock.Code(
                    """
                    Field {
                        LabeledFieldControl("Your email address") { modifier ->
                            Input(value, onValueChange, modifier)
                        }
                    }
                    """.trimIndent(),
                ),
            ),
    )

val labelDoc =
    ComponentDoc(
        id = "label",
        title = "Label",
        description = "Renders an accessible label associated with controls.",
        introMarkdown =
            "For form fields, use the [Field](https://ui.shadcn.com/docs/components/base/field) component " +
                "which includes built-in label, description, and error handling.",
        usageCode = labelUsageCode,
        examplesHeading = null,
        beforeExampleSections = mapOf("Label in Field" to listOf(labelFieldGuide)),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = labelDemoCode,
                    preview = { LabelCheckboxDemo() },
                ),
                ComponentExample(
                    title = "Label in Field",
                    showHeading = false,
                    minPreviewHeight = 850.dp,
                    mobileMinPreviewHeight = 800.dp,
                    previewPadding = 24.dp,
                    code = labelInFieldCode,
                    preview = { LabelInFieldDemo() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    minPreviewHeight = 352.dp,
                    previewPadding = 0.dp,
                    code = labelRtlCode,
                    preview = { LabelRtlDemo() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Required",
                    code = """Label("Email", required = true)""",
                    preview = { Label("Email", required = true) },
                ),
                ComponentExample(
                    title = "Kotlin extension: Disabled",
                    code = """Label("Email", disabled = true)""",
                    preview = { Label("Email", disabled = true) },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Label",
                    "A passive text label; pair it with the control it names. " +
                        "Pass an accessible name to the control and forward pointer activation from " +
                        "the visible label. " +
                        "See the [Base UI Label API](https://base-ui.com/react/components/label#api-reference) " +
                        "for the upstream HTML API.",
                    listOf(
                        ApiProperty("text", "String", "Required"),
                        ApiProperty("required", "Boolean", "false"),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
            ),
    )
