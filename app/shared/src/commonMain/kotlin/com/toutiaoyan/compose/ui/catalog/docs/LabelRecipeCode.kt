package com.toutiaoyan.compose.ui.catalog.docs

internal val labelInFieldCode =
    """
    val labelMonths = listOf("MM") + (1..12).map { it.toString().padStart(2, '0') }
    val labelYears = listOf("YYYY") + (2024..2029).map { it.toString() }
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
    """.trimIndent()

internal val labelRtlCode =
    """
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
    """.trimIndent()
