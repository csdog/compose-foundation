@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroupItem
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class FieldScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("field_states", darkTheme = darkTheme) {
            FieldGroup {
                Field {
                    FieldLabel("Email", required = true)
                    Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                    FieldDescription("We'll never share your email.")
                }
                FieldSeparator(label = "OR")
                Field {
                    FieldLabel("Username")
                    Input(value = "jane", onValueChange = {})
                    FieldError("Username is already taken.")
                }
            }
        }
    }

    private fun profileForm(darkTheme: Boolean) {
        snapshot("field_profile_form", darkTheme = darkTheme) {
            FieldGroup(modifier = Modifier.width(320.dp)) {
                Field {
                    FieldLabel("Name", required = true)
                    Input(value = "", onValueChange = {}, placeholder = "Your name")
                }
                Field {
                    FieldLabel("Email", required = true)
                    Input(value = "", onValueChange = {}, placeholder = "you@example.com")
                    FieldDescription("We'll never share your email.")
                }
                Field {
                    FieldLabel("Bio")
                    Textarea(value = "", onValueChange = {}, placeholder = "Tell us about yourself")
                    FieldDescription("Shown on your public profile.")
                }
            }
        }
    }

    private fun novaFieldSets(darkTheme: Boolean) {
        snapshot("field_nova_fieldsets", darkTheme = darkTheme, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                FieldSet(legend = "Address Information") {
                    FieldDescription("We need your address to deliver your order.")
                    Field {
                        FieldLabel("Street Address")
                        Input(value = "", onValueChange = {}, placeholder = "123 Main St")
                    }
                }
                FieldSet(legend = "Subscription Plan", legendVariant = FieldLegendVariant.Label) {
                    FieldDescription("Yearly and lifetime plans offer significant savings.")
                    RadioGroup(value = "monthly", onValueChange = {}) {
                        RadioGroupItem(value = "monthly", text = "Monthly")
                        RadioGroupItem(value = "yearly", text = "Yearly")
                    }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun profile_form_light() = profileForm(darkTheme = false)

    @Test
    fun profile_form_dark() = profileForm(darkTheme = true)

    @Test
    fun nova_fieldsets_light() = novaFieldSets(darkTheme = false)

    @Test
    fun nova_fieldsets_dark() = novaFieldSets(darkTheme = true)
}
