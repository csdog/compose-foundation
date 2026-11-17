package com.toutiaoyan.compose.ui.catalog.docs

internal val labelDemoCode =
    """
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
    private fun Modifier.activateLabel(onActivate: () -> Unit): Modifier {
        val action by rememberUpdatedState(onActivate)
        return pointerInput(Unit) { detectTapGestures { action() } }
    }
    """.trimIndent()

internal val labelInFieldCode =
    """
    private val labelMonths = (1..12).map { it.toString().padStart(2, '0') }
    private val labelYears = (2024..2029).map { it.toString() }

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
    private fun LabeledFieldControl(
        name: String,
        required: Boolean = false,
        content: @Composable (Modifier) -> Unit,
    ) {
        val focus = remember { FocusRequester() }
        FieldLabel(name, modifier = Modifier.activateLabel { focus.requestFocus() })
        content(
            Modifier.focusRequester(focus).semantics { contentDescription = if (required) "${'$'}name, required" else name },
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
                    accessibilityLabel = "${'$'}name, ${'$'}{value ?: placeholder}",
                )
                SelectContent {
                    SelectItem<String?>(null, textValue = placeholder)
                    options.forEach { SelectItem<String?>(it, textValue = it) }
                }
            }
        }
    }

    @Composable
    private fun Modifier.activateLabel(onActivate: () -> Unit): Modifier {
        val action by rememberUpdatedState(onActivate)
        return pointerInput(Unit) { detectTapGestures { action() } }
    }
    """.trimIndent()

internal val labelRtlCode =
    """
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
    private fun Modifier.activateLabel(onActivate: () -> Unit): Modifier {
        val action by rememberUpdatedState(onActivate)
        return pointerInput(Unit) { detectTapGestures { action() } }
    }
    """.trimIndent()

internal val labelUsageCode =
    """
    import androidx.compose.foundation.gestures.detectTapGestures
    import androidx.compose.foundation.layout.Column
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.focus.FocusRequester
    import androidx.compose.ui.focus.focusRequester
    import androidx.compose.ui.input.pointer.pointerInput
    import androidx.compose.ui.semantics.contentDescription
    import androidx.compose.ui.semantics.semantics
    import com.toutiaoyan.compose.ui.components.input.Input
    import com.toutiaoyan.compose.ui.components.label.Label

    @Composable
    fun EmailField(value: String, onValueChange: (String) -> Unit) {
        val name = "Your email address"
        val focus = remember { FocusRequester() }
        Column {
            Label(name, modifier = Modifier.pointerInput(Unit) { detectTapGestures { focus.requestFocus() } })
            Input(
                value,
                onValueChange,
                modifier = Modifier.focusRequester(focus).semantics { contentDescription = name },
            )
        }
    }
    """.trimIndent()
