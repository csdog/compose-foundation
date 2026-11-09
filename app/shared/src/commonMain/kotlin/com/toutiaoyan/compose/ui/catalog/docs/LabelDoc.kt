@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
import com.toutiaoyan.compose.ui.components.textarea.Textarea

private val labelMonths = listOf("MM") + (1..12).map { it.toString().padStart(2, '0') }
private val labelYears = listOf("YYYY") + (2024..2029).map { it.toString() }

@Composable
private fun LabelCheckboxDemo(label: String = "Accept terms and conditions") {
    var checked by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Label(label, modifier = Modifier.clickable { checked = !checked })
    }
}

private val labelDemoCode =
    """
    var checked by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Label("Accept terms and conditions", modifier = Modifier.clickable { checked = !checked })
    }
    """.trimIndent()

@Composable
private fun LabelInFieldDemo() {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var month by remember { mutableStateOf<String?>(null) }
    var year by remember { mutableStateOf<String?>(null) }
    var comments by remember { mutableStateOf("") }
    var sameAddress by remember { mutableStateOf(true) }
    FieldGroup(Modifier.widthIn(max = 448.dp)) {
        FieldSet {
            FieldLegend("Payment Method")
            FieldDescription("All transactions are secure and encrypted")
            FieldGroup {
                Field {
                    FieldLabel("Name on Card")
                    Input(name, { name = it }, placeholder = "Evil Rabbit", required = true)
                }
                Field {
                    FieldLabel("Card Number")
                    Input(number, { number = it }, placeholder = "1234 5678 9012 3456", required = true)
                    FieldDescription("Enter your 16-digit card number")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Field(Modifier.weight(1f)) {
                        FieldLabel("Month")
                        Select<String?>(
                            value = month,
                            onValueChange = { month = it },
                            placeholder = "MM",
                        ) {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent {
                                SelectItem<String?>(null, textValue = "MM")
                                labelMonths.drop(1).forEach { SelectItem<String?>(it, textValue = it) }
                            }
                        }
                    }
                    Field(Modifier.weight(1f)) {
                        FieldLabel("Year")
                        Select<String?>(
                            value = year,
                            onValueChange = { year = it },
                            placeholder = "YYYY",
                        ) {
                            SelectTrigger(Modifier.fillMaxWidth())
                            SelectContent {
                                SelectItem<String?>(null, textValue = "YYYY")
                                labelYears.drop(1).forEach { SelectItem<String?>(it, textValue = it) }
                            }
                        }
                    }
                    Field(Modifier.weight(1f)) {
                        FieldLabel("CVV")
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
                    Checkbox(checked = sameAddress, onCheckedChange = { sameAddress = it })
                    FieldLabel("Same as shipping address", modifier = Modifier.clickable { sameAddress = !sameAddress })
                }
            }
        }
        FieldSet {
            FieldGroup {
                Field {
                    FieldLabel("Comments")
                    Textarea(comments, { comments = it }, placeholder = "Add any additional comments")
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
    var checked by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(
            language,
            listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
            { language = it },
        )
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Row(
                Modifier.testTag("label-rtl-body").fillMaxWidth().padding(40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = checked, onCheckedChange = { checked = it })
                Label(
                    when (language) {
                        0 -> "Accept terms and conditions"
                        1 -> "قبول الشروط والأحكام"
                        else -> "קבל תנאים והגבלות"
                    },
                    modifier = Modifier.clickable { checked = !checked },
                )
            }
        }
    }
}

val labelDoc =
    ComponentDoc(
        id = "label",
        title = "Label",
        description = "Renders an accessible label associated with controls.",
        introMarkdown =
            "For form fields, use the [Field](https://ui.shadcn.com/docs/components/base/field) component " +
                "which includes built-in label, description, and error handling.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.label.Label

            Label("Your email address")
            """.trimIndent(),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = labelDemoCode,
                    preview = { LabelCheckboxDemo() },
                ),
                ComponentExample(
                    title = "Label in Field",
                    descriptionMarkdown =
                        "For form fields, use the `Field` component which includes built-in `FieldLabel`, " +
                            "`FieldDescription`, and `FieldError` components.\n\n" +
                            "```kotlin\nField {\n    FieldLabel(\"Your email address\")\n" +
                            "    Input(value = \"\", onValueChange = {})\n}\n```",
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
                    minPreviewHeight = 288.dp,
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
                        "Use a click or focus modifier for control activation, as shown in the examples. " +
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
