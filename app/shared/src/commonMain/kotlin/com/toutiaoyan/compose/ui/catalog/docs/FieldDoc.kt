@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.toutiaoyan.compose.ui.components.field.FieldSet
import com.toutiaoyan.compose.ui.components.field.FieldSeparator
import com.toutiaoyan.compose.ui.components.field.FieldTitle
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputType
import com.toutiaoyan.compose.ui.components.textarea.Textarea

val fieldDoc =
    ComponentDoc(
        id = "field",
        title = "Field",
        description =
            "Combine labels, controls, and help text — matching real shadcn field.tsx " +
                "(FieldSet / FieldLegend / FieldGroup / Field / FieldContent / FieldLabel / " +
                "FieldTitle / FieldDescription / FieldError / FieldSeparator). No Form wrapper; " +
                "compose with your own hoisted state.",
        usageCode =
            """
            FieldGroup {
                Field("Email", description = "We'll never share your email.") {
                    Input(value = email, onValueChange = { email = it }, type = InputType.Email)
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Profile form",
                    code =
                        """
                        FieldGroup(modifier = Modifier.width(320.dp)) {
                            Field("Name", required = true) {
                                Input(value = name, onValueChange = { name = it }, placeholder = "Your name")
                            }
                            Field("Email", required = true, description = "We'll never share your email.") {
                                Input(value = email, onValueChange = { email = it }, type = InputType.Email)
                            }
                            Field("Bio", description = "Shown on your public profile.") {
                                Textarea(value = bio, onValueChange = { bio = it })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var name by remember { mutableStateOf("") }
                        var email by remember { mutableStateOf("") }
                        var bio by remember { mutableStateOf("") }
                        FieldGroup(modifier = Modifier.width(320.dp)) {
                            Field("Name", required = true) {
                                Input(
                                    value = name,
                                    onValueChange = { name = it },
                                    placeholder = "Your name",
                                )
                            }
                            Field("Email", required = true, description = "We'll never share your email.") {
                                Input(
                                    value = email,
                                    onValueChange = { email = it },
                                    type = InputType.Email,
                                    placeholder = "you@example.com",
                                )
                            }
                            Field("Bio", description = "Shown on your public profile.") {
                                Textarea(
                                    value = bio,
                                    onValueChange = { bio = it },
                                    placeholder = "Tell us about yourself",
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "FieldSet + Legend",
                    code =
                        """
                        FieldSet("Payment") {
                            Field("Card number") {
                                Input(value = card, onValueChange = { card = it })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var card by remember { mutableStateOf("") }
                        FieldSet("Payment", modifier = Modifier.width(320.dp)) {
                            Field("Card number") {
                                Input(value = card, onValueChange = { card = it }, placeholder = "•••• •••• •••• ••••")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal + Content",
                    code =
                        """
                        Field(orientation = FieldOrientation.Horizontal) {
                            FieldTitle(" deriv")
                            FieldContent {
                                Checkbox(checked = checked, onCheckedChange = { checked = it })
                                FieldDescription("You agree to our Terms of Service.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var checked by remember { mutableStateOf(false) }
                        Field(
                            orientation = FieldOrientation.Horizontal,
                            modifier = Modifier.width(320.dp),
                        ) {
                            FieldTitle("Terms")
                            FieldContent {
                                Checkbox(checked = checked, onCheckedChange = { checked = it })
                                FieldDescription("You agree to our Terms of Service.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        Field(orientation = FieldOrientation.Vertical) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field(
                            orientation = FieldOrientation.Vertical,
                            modifier = Modifier.width(320.dp),
                        ) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        Field(orientation = FieldOrientation.Vertical) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field(
                            orientation = FieldOrientation.Vertical,
                            modifier = Modifier.width(320.dp),
                        ) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        Field(orientation = FieldOrientation.Horizontal) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field(
                            orientation = FieldOrientation.Horizontal,
                            modifier = Modifier.width(320.dp),
                        ) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Responsive",
                    code =
                        """
                        Field(orientation = FieldOrientation.Responsive) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field(
                            orientation = FieldOrientation.Responsive,
                            modifier = Modifier.width(320.dp),
                        ) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    code =
                        """
                        Field("Username", invalid = true) {
                            Input(value = "jane", onValueChange = {}, invalid = true)
                            FieldError(errors = listOf("Username is taken", "Try another"))
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Username", invalid = true, modifier = Modifier.width(320.dp)) {
                            Input(value = "jane", onValueChange = {}, invalid = true)
                            FieldError(errors = listOf("Username is taken", "Try another"))
                        }
                    },
                ),
                ComponentExample(
                    title = "Default invalid",
                    code =
                        """
                        Field("Username", invalid = false) {
                            Input(value = "jane", onValueChange = {})
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Username", invalid = false, modifier = Modifier.width(320.dp)) {
                            Input(value = "jane", onValueChange = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Field("Email", disabled = true) {
                            Input(value = "jane@example.com", onValueChange = {})
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email", disabled = true, modifier = Modifier.width(320.dp)) {
                            Input(value = "jane@example.com", onValueChange = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Field("Email", disabled = false) {
                            Input(value = "jane@example.com", onValueChange = {})
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email", disabled = false, modifier = Modifier.width(320.dp)) {
                            Input(value = "jane@example.com", onValueChange = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Required",
                    code =
                        """
                        Field("Email", required = true) {
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email", required = true, modifier = Modifier.width(320.dp)) {
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default required",
                    code =
                        """
                        Field("Email", required = false) {
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Email", required = false, modifier = Modifier.width(320.dp)) {
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled fieldset",
                    code =
                        """
                        FieldSet("Account", disabled = true) {
                            Field("Email") {
                                Input(value = "jane@example.com", onValueChange = {})
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldSet("Account", disabled = true, modifier = Modifier.width(320.dp)) {
                            Field("Email") {
                                Input(value = "jane@example.com", onValueChange = {})
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        FieldGroup {
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                            FieldSeparator(label = "Or continue with")
                            Field("Username") {
                                Input(value = "", onValueChange = {}, placeholder = "jane")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldGroup(modifier = Modifier.width(320.dp)) {
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                            FieldSeparator(label = "Or continue with")
                            Field("Username") {
                                Input(value = "", onValueChange = {}, placeholder = "jane")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default separator",
                    code =
                        """
                        FieldGroup {
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                            FieldSeparator()
                            Field("Username") {
                                Input(value = "", onValueChange = {}, placeholder = "jane")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldGroup(modifier = Modifier.width(320.dp)) {
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                            FieldSeparator()
                            Field("Username") {
                                Input(value = "", onValueChange = {}, placeholder = "jane")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Legend",
                    code =
                        """
                        FieldSet {
                            FieldLegend("Payment")
                            Field("Card number") {
                                Input(value = "", onValueChange = {}, placeholder = "•••• •••• •••• ••••")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldSet(modifier = Modifier.width(320.dp)) {
                            FieldLegend("Payment")
                            Field("Card number") {
                                Input(value = "", onValueChange = {}, placeholder = "•••• •••• •••• ••••")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default legend variant",
                    code =
                        """
                        FieldSet {
                            FieldLegend("Payment", variant = FieldLegendVariant.Legend)
                            Field("Card number") {
                                Input(value = "", onValueChange = {}, placeholder = "•••• •••• •••• ••••")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldSet(modifier = Modifier.width(320.dp)) {
                            FieldLegend("Payment", variant = FieldLegendVariant.Legend)
                            Field("Card number") {
                                Input(value = "", onValueChange = {}, placeholder = "•••• •••• •••• ••••")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Error",
                    code =
                        """
                        Field("Username", invalid = true) {
                            Input(value = "jane", onValueChange = {}, invalid = true)
                            FieldError("Username is taken")
                        }
                        """.trimIndent(),
                    preview = {
                        Field("Username", invalid = true, modifier = Modifier.width(320.dp)) {
                            Input(value = "jane", onValueChange = {}, invalid = true)
                            FieldError("Username is taken")
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Field {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field(modifier = Modifier.width(320.dp)) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Field {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            FieldDescription("We'll never share your email.")
                        }
                        """.trimIndent(),
                    preview = {
                        Field(modifier = Modifier.width(320.dp)) {
                            FieldTitle("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            FieldDescription("We'll never share your email.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Label",
                    code =
                        """
                        Field {
                            FieldLabel("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                        """.trimIndent(),
                    preview = {
                        Field(modifier = Modifier.width(320.dp)) {
                            FieldLabel("Email")
                            Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Field {
                            FieldTitle("Email")
                            FieldContent {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Field(modifier = Modifier.width(320.dp)) {
                            FieldTitle("Email")
                            FieldContent {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        FieldGroup {
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldGroup(modifier = Modifier.width(320.dp)) {
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "FieldSet",
                    code =
                        """
                        FieldSet {
                            FieldLegend("Profile")
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        FieldSet(modifier = Modifier.width(320.dp)) {
                            FieldLegend("Profile")
                            Field("Email") {
                                Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                            }
                        }
                    },
                ),
            ),
    )
