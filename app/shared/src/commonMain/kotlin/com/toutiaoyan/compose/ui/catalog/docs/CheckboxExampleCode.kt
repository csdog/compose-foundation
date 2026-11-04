package com.toutiaoyan.compose.ui.catalog.docs

internal val checkboxImports =
    """
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.style.Style
    import androidx.compose.foundation.style.contentPadding
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.focus.FocusRequester
    import androidx.compose.ui.focus.focusProperties
    import androidx.compose.ui.focus.focusRequester
    import androidx.compose.ui.platform.LocalLayoutDirection
    import androidx.compose.ui.text.TextStyle
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.*
    import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
    import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
    import com.toutiaoyan.compose.ui.components.field.*
    import com.toutiaoyan.compose.ui.components.label.Label
    import com.toutiaoyan.compose.ui.components.table.*
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.theme.Theme
    import com.toutiaoyan.compose.ui.theme.currentTheme
    import androidx.compose.ui.text.font.FontFamily
    import compose_foundation.app.shared.generated.resources.*
    import org.jetbrains.compose.resources.Font
    """.trimIndent()

internal fun checkboxCode(name: String): String =
    when (name) {
        "default" ->
            """
            @Composable
            private fun CheckboxRecipeField(
                title: String,
                description: String? = null,
                defaultChecked: Boolean = false,
                disabled: Boolean = false,
                invalid: Boolean = false,
                plainLabel: Boolean = false,
                normalWeight: Boolean = false,
            ) {
                var checked by remember { mutableStateOf(defaultChecked) }
                val focus = remember { FocusRequester() }
                val labelModifier =
                    Modifier.focusProperties { canFocus = false }.clickable(enabled = !disabled) {
                        checked = !checked
                        focus.requestFocus()
                    }
                Field(orientation = FieldOrientation.Horizontal, disabled = disabled, invalid = invalid) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        disabled = disabled,
                        invalid = invalid,
                        label = title,
                        modifier = Modifier.focusRequester(focus),
                    )
                    if (description != null) {
                        FieldContent {
                            FieldLabel(title, labelModifier)
                            FieldDescription(description)
                        }
                    } else if (plainLabel) {
                        Label(title, labelModifier)
                    } else if (normalWeight) {
                        Text(
                            modifier = labelModifier,
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                        ) {
                            title
                        }
                    } else {
                        FieldLabel(title, labelModifier)
                    }
                }
            }

            @Composable
            private fun CheckboxNotificationCard(
                title: String,
                description: String,
            ) {
                var checked by remember { mutableStateOf(false) }
                val focus = remember { FocusRequester() }
                FieldLabel(selected = checked, onClick = {
                    checked = !checked
                    focus.requestFocus()
                }) {
                    Field(orientation = FieldOrientation.Horizontal) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            label = "${'$'}title. ${'$'}description",
                            modifier = Modifier.focusRequester(focus),
                        )
                        FieldContent {
                            FieldTitle(title)
                            FieldDescription(description)
                        }
                    }
                }
            }

            @Composable
            fun CheckboxDefaultRecipe(
                terms: String = "Accept terms and conditions",
                termsDescription: String = "By clicking this checkbox, you agree to the terms.",
                notifications: String = "Enable notifications",
                notificationsDescription: String = "You can enable or disable notifications at any time.",
            ) {
                FieldGroup(Modifier.widthIn(max = 384.dp)) {
                    CheckboxRecipeField(terms, plainLabel = true)
                    CheckboxRecipeField(terms, description = termsDescription, defaultChecked = true)
                    CheckboxRecipeField(notifications, disabled = true)
                    CheckboxNotificationCard(notifications, notificationsDescription)
                }
            }

            CheckboxDefaultRecipe()
            """.trimIndent()
        "invalid" ->
            """
            @Composable
            private fun CheckboxRecipeField(
                title: String,
                description: String? = null,
                defaultChecked: Boolean = false,
                disabled: Boolean = false,
                invalid: Boolean = false,
                plainLabel: Boolean = false,
                normalWeight: Boolean = false,
            ) {
                var checked by remember { mutableStateOf(defaultChecked) }
                val focus = remember { FocusRequester() }
                val labelModifier =
                    Modifier.focusProperties { canFocus = false }.clickable(enabled = !disabled) {
                        checked = !checked
                        focus.requestFocus()
                    }
                Field(orientation = FieldOrientation.Horizontal, disabled = disabled, invalid = invalid) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        disabled = disabled,
                        invalid = invalid,
                        label = title,
                        modifier = Modifier.focusRequester(focus),
                    )
                    if (description != null) {
                        FieldContent {
                            FieldLabel(title, labelModifier)
                            FieldDescription(description)
                        }
                    } else if (plainLabel) {
                        Label(title, labelModifier)
                    } else if (normalWeight) {
                        Text(
                            modifier = labelModifier,
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                        ) {
                            title
                        }
                    } else {
                        FieldLabel(title, labelModifier)
                    }
                }
            }

            @Composable
            fun CheckboxInvalidRecipe() {
                FieldGroup(Modifier.width(224.dp)) { CheckboxRecipeField("Accept terms and conditions", invalid = true) }
            }

            CheckboxInvalidRecipe()
            """.trimIndent()
        "basic" ->
            """
            @Composable
            private fun CheckboxRecipeField(
                title: String,
                description: String? = null,
                defaultChecked: Boolean = false,
                disabled: Boolean = false,
                invalid: Boolean = false,
                plainLabel: Boolean = false,
                normalWeight: Boolean = false,
            ) {
                var checked by remember { mutableStateOf(defaultChecked) }
                val focus = remember { FocusRequester() }
                val labelModifier =
                    Modifier.focusProperties { canFocus = false }.clickable(enabled = !disabled) {
                        checked = !checked
                        focus.requestFocus()
                    }
                Field(orientation = FieldOrientation.Horizontal, disabled = disabled, invalid = invalid) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        disabled = disabled,
                        invalid = invalid,
                        label = title,
                        modifier = Modifier.focusRequester(focus),
                    )
                    if (description != null) {
                        FieldContent {
                            FieldLabel(title, labelModifier)
                            FieldDescription(description)
                        }
                    } else if (plainLabel) {
                        Label(title, labelModifier)
                    } else if (normalWeight) {
                        Text(
                            modifier = labelModifier,
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                        ) {
                            title
                        }
                    } else {
                        FieldLabel(title, labelModifier)
                    }
                }
            }

            @Composable
            fun CheckboxBasicRecipe() {
                FieldGroup(Modifier.width(224.dp)) { CheckboxRecipeField("Accept terms and conditions") }
            }

            CheckboxBasicRecipe()
            """.trimIndent()
        "description" ->
            """
            @Composable
            private fun CheckboxRecipeField(
                title: String,
                description: String? = null,
                defaultChecked: Boolean = false,
                disabled: Boolean = false,
                invalid: Boolean = false,
                plainLabel: Boolean = false,
                normalWeight: Boolean = false,
            ) {
                var checked by remember { mutableStateOf(defaultChecked) }
                val focus = remember { FocusRequester() }
                val labelModifier =
                    Modifier.focusProperties { canFocus = false }.clickable(enabled = !disabled) {
                        checked = !checked
                        focus.requestFocus()
                    }
                Field(orientation = FieldOrientation.Horizontal, disabled = disabled, invalid = invalid) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        disabled = disabled,
                        invalid = invalid,
                        label = title,
                        modifier = Modifier.focusRequester(focus),
                    )
                    if (description != null) {
                        FieldContent {
                            FieldLabel(title, labelModifier)
                            FieldDescription(description)
                        }
                    } else if (plainLabel) {
                        Label(title, labelModifier)
                    } else if (normalWeight) {
                        Text(
                            modifier = labelModifier,
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                        ) {
                            title
                        }
                    } else {
                        FieldLabel(title, labelModifier)
                    }
                }
            }

            @Composable
            fun CheckboxDescriptionRecipe() {
                FieldGroup(Modifier.width(288.dp)) {
                    CheckboxRecipeField(
                        "Accept terms and conditions",
                        "By clicking this checkbox, you agree to the terms and conditions.",
                        defaultChecked = true,
                    )
                }
            }

            CheckboxDescriptionRecipe()
            """.trimIndent()
        "disabled" ->
            """
            @Composable
            private fun CheckboxRecipeField(
                title: String,
                description: String? = null,
                defaultChecked: Boolean = false,
                disabled: Boolean = false,
                invalid: Boolean = false,
                plainLabel: Boolean = false,
                normalWeight: Boolean = false,
            ) {
                var checked by remember { mutableStateOf(defaultChecked) }
                val focus = remember { FocusRequester() }
                val labelModifier =
                    Modifier.focusProperties { canFocus = false }.clickable(enabled = !disabled) {
                        checked = !checked
                        focus.requestFocus()
                    }
                Field(orientation = FieldOrientation.Horizontal, disabled = disabled, invalid = invalid) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        disabled = disabled,
                        invalid = invalid,
                        label = title,
                        modifier = Modifier.focusRequester(focus),
                    )
                    if (description != null) {
                        FieldContent {
                            FieldLabel(title, labelModifier)
                            FieldDescription(description)
                        }
                    } else if (plainLabel) {
                        Label(title, labelModifier)
                    } else if (normalWeight) {
                        Text(
                            modifier = labelModifier,
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                        ) {
                            title
                        }
                    } else {
                        FieldLabel(title, labelModifier)
                    }
                }
            }

            @Composable
            fun CheckboxDisabledRecipe() {
                FieldGroup(Modifier.width(224.dp)) { CheckboxRecipeField("Enable notifications", disabled = true) }
            }

            CheckboxDisabledRecipe()
            """.trimIndent()
        "group" ->
            """
            @Composable
            private fun CheckboxRecipeField(
                title: String,
                description: String? = null,
                defaultChecked: Boolean = false,
                disabled: Boolean = false,
                invalid: Boolean = false,
                plainLabel: Boolean = false,
                normalWeight: Boolean = false,
            ) {
                var checked by remember { mutableStateOf(defaultChecked) }
                val focus = remember { FocusRequester() }
                val labelModifier =
                    Modifier.focusProperties { canFocus = false }.clickable(enabled = !disabled) {
                        checked = !checked
                        focus.requestFocus()
                    }
                Field(orientation = FieldOrientation.Horizontal, disabled = disabled, invalid = invalid) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        disabled = disabled,
                        invalid = invalid,
                        label = title,
                        modifier = Modifier.focusRequester(focus),
                    )
                    if (description != null) {
                        FieldContent {
                            FieldLabel(title, labelModifier)
                            FieldDescription(description)
                        }
                    } else if (plainLabel) {
                        Label(title, labelModifier)
                    } else if (normalWeight) {
                        Text(
                            modifier = labelModifier,
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                        ) {
                            title
                        }
                    } else {
                        FieldLabel(title, labelModifier)
                    }
                }
            }

            @Composable
            fun CheckboxGroupRecipe() {
                FieldSet(
                    legend = "Show these items on the desktop:",
                    legendVariant = FieldLegendVariant.Label,
                    modifier = Modifier.width(IntrinsicSize.Max),
                ) {
                    FieldDescription("Select the items you want to show on the desktop.")
                    FieldGroup(spacing = 12.dp) {
                        listOf("Hard disks", "External disks", "CDs, DVDs, and iPods", "Connected servers").forEachIndexed {
                                index,
                                name,
                            ->
                            CheckboxRecipeField(name, defaultChecked = index < 2, normalWeight = true)
                        }
                    }
                }
            }

            CheckboxGroupRecipe()
            """.trimIndent()
        "table" ->
            """
            private data class CheckboxPerson(val name: String, val email: String, val role: String)

            private val checkboxPeople =
                listOf(
                    CheckboxPerson("Sarah Chen", "sarah.chen@example.com", "Admin"),
                    CheckboxPerson("Marcus Rodriguez", "marcus.rodriguez@example.com", "User"),
                    CheckboxPerson("Priya Patel", "priya.patel@example.com", "User"),
                    CheckboxPerson("David Kim", "david.kim@example.com", "Editor"),
                )

            @Composable
            fun CheckboxTableRecipe() {
                var selected by remember { mutableStateOf(setOf(0)) }
                val all = selected.size == checkboxPeople.size
                Table(ariaLabel = "People") {
                    TableHeaderRow {
                        TableCell(
                            content = {
                                Checkbox(checked = all, onCheckedChange = {
                                    selected = if (it) checkboxPeople.indices.toSet() else emptySet()
                                }, label = "Select all")
                            },
                            modifier = Modifier.width(32.dp),
                            style =
                                Style {
                                    height(40.dp)
                                    contentPadding(vertical = 0.dp, horizontal = 8.dp)
                                },
                        )
                        TableHeadCell("Name", Modifier.width(160.dp))
                        TableHeadCell("Email", Modifier.width(260.dp))
                        TableHeadCell("Role", Modifier.width(64.dp))
                    }
                    checkboxPeople.forEachIndexed { index, person ->
                        TableRow(selected = index in selected) {
                            TableCell(content = {
                                Checkbox(checked = index in selected, onCheckedChange = {
                                    selected = if (it) selected + index else selected - index
                                }, label = "Select " + person.name)
                            }, modifier = Modifier.width(32.dp))
                            TableCell(person.name, Modifier.width(160.dp), textStyle = TextStyle(fontWeight = FontWeight.Medium))
                            TableCell(person.email, Modifier.width(260.dp))
                            TableCell(person.role, Modifier.width(64.dp))
                        }
                    }
                }
            }

            CheckboxTableRecipe()
            """.trimIndent()
        "rtl" ->
            """
            @Composable
            private fun CheckboxRecipeField(
                title: String,
                description: String? = null,
                defaultChecked: Boolean = false,
                disabled: Boolean = false,
                invalid: Boolean = false,
                plainLabel: Boolean = false,
                normalWeight: Boolean = false,
            ) {
                var checked by remember { mutableStateOf(defaultChecked) }
                val focus = remember { FocusRequester() }
                val labelModifier =
                    Modifier.focusProperties { canFocus = false }.clickable(enabled = !disabled) {
                        checked = !checked
                        focus.requestFocus()
                    }
                Field(orientation = FieldOrientation.Horizontal, disabled = disabled, invalid = invalid) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        disabled = disabled,
                        invalid = invalid,
                        label = title,
                        modifier = Modifier.focusRequester(focus),
                    )
                    if (description != null) {
                        FieldContent {
                            FieldLabel(title, labelModifier)
                            FieldDescription(description)
                        }
                    } else if (plainLabel) {
                        Label(title, labelModifier)
                    } else if (normalWeight) {
                        Text(
                            modifier = labelModifier,
                            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.Normal),
                        ) {
                            title
                        }
                    } else {
                        FieldLabel(title, labelModifier)
                    }
                }
            }

            @Composable
            private fun CheckboxNotificationCard(
                title: String,
                description: String,
            ) {
                var checked by remember { mutableStateOf(false) }
                val focus = remember { FocusRequester() }
                FieldLabel(selected = checked, onClick = {
                    checked = !checked
                    focus.requestFocus()
                }) {
                    Field(orientation = FieldOrientation.Horizontal) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            label = "${'$'}title. ${'$'}description",
                            modifier = Modifier.focusRequester(focus),
                        )
                        FieldContent {
                            FieldTitle(title)
                            FieldDescription(description)
                        }
                    }
                }
            }

            @Composable
            fun CheckboxDefaultRecipe(
                terms: String = "Accept terms and conditions",
                termsDescription: String = "By clicking this checkbox, you agree to the terms.",
                notifications: String = "Enable notifications",
                notificationsDescription: String = "You can enable or disable notifications at any time.",
            ) {
                FieldGroup(Modifier.widthIn(max = 384.dp)) {
                    CheckboxRecipeField(terms, plainLabel = true)
                    CheckboxRecipeField(terms, description = termsDescription, defaultChecked = true)
                    CheckboxRecipeField(notifications, disabled = true)
                    CheckboxNotificationCard(notifications, notificationsDescription)
                }
            }

            private val checkboxTranslations =
                listOf(
                    listOf(
                        "Accept terms and conditions",
                        "By clicking this checkbox, you agree to the terms.",
                        "Enable notifications",
                        "You can enable or disable notifications at any time.",
                    ),
                    listOf(
                        "قبول الشروط والأحكام",
                        "بالنقر على هذا المربع، فإنك توافق على الشروط.",
                        "تفعيل الإشعارات",
                        "يمكنك تفعيل أو إلغاء تفعيل الإشعارات في أي وقت.",
                    ),
                    listOf(
                        "קבל תנאים והגבלות",
                        "על ידי לחיצה על תיבת הסימון הזו, אתה מסכים לתנאים.",
                        "הפעל התראות",
                        "אתה יכול להפעיל או להשבית התראות בכל עת.",
                    ),
                )

            @Composable
            fun CheckboxRtlRecipe(language: Int = 1) {
                val inherited = currentTheme
                Column(Modifier.fillMaxWidth()) {

                    // Pass 0 for English, 1 for Arabic, or 2 for Hebrew from your language picker.
                    Box(Modifier.fillMaxWidth().heightIn(min = 320.dp).padding(40.dp), contentAlignment = Alignment.Center) {
                        val font =
                            when (language) {
                                1 -> FontFamily(Font(Res.font.catalog_arabic_regular, FontWeight.Normal), Font(Res.font.catalog_arabic_medium, FontWeight.Medium))
                                2 -> FontFamily(Font(Res.font.catalog_hebrew_regular, FontWeight.Normal), Font(Res.font.catalog_hebrew_medium, FontWeight.Medium))
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
                                minTouchTarget = inherited.minTouchTarget, fontFamily = font,
                            ) {
                                val t = checkboxTranslations[language]
                                CheckboxDefaultRecipe(t[0], t[1], t[2], t[3])
                            }
                        }
                    }
                }
            }

            CheckboxRtlRecipe()
            """.trimIndent()
        else -> error("Unknown checkbox recipe: $name")
    }
