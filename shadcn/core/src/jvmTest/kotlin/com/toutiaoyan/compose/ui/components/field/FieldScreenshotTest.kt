@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.components.input.Input

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

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun profile_form_light() = profileForm(darkTheme = false)

    @Test
    fun profile_form_dark() = profileForm(darkTheme = true)
}
