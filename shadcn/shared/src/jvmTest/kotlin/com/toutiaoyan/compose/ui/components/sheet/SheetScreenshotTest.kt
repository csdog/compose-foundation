@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.input.Input
import kotlin.test.Test

class SheetScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("sheet_states", darkTheme = darkTheme) {
            Sheet(open = true, onOpenChange = {}) {
                SheetContent(side = SheetSide.End) {
                    SheetHeader(
                        "Edit profile",
                        description = "Make changes to your profile here. Click save when you're done.",
                    )
                }
            }
        }
    }

    private fun editProfile(darkTheme: Boolean) {
        snapshot("sheet_edit_profile", darkTheme = darkTheme) {
            Sheet(open = true, onOpenChange = {}) {
                SheetContent(side = SheetSide.End) {
                    SheetHeader(
                        "Edit profile",
                        description = "Make changes to your profile here. Click save when you're done.",
                    )
                    FieldGroup(modifier = Modifier.padding(top = 16.dp)) {
                        Field("Name") {
                            Input(value = "Pedro Duarte", onValueChange = {})
                        }
                        Field("Username") {
                            Input(value = "@peduarte", onValueChange = {})
                        }
                    }
                    SheetFooter(modifier = Modifier.padding(top = 16.dp)) {
                        Button("Save changes")
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
    fun edit_profile_light() = editProfile(darkTheme = false)

    @Test
    fun edit_profile_dark() = editProfile(darkTheme = true)

    private fun closeButton(darkTheme: Boolean) {
        snapshot("sheet_close_button", darkTheme = darkTheme) {
            Sheet(open = true, onOpenChange = {}) {
                SheetContent(
                    side = SheetSide.End,
                    showCloseButton = SheetDefaults.ShowCloseButton,
                ) {
                    SheetHeader(
                        "Edit profile",
                        description = "Make changes to your profile here. Click save when you're done.",
                    )
                }
            }
        }
    }

    @Test
    fun close_button_light() = closeButton(darkTheme = false)

    @Test
    fun close_button_dark() = closeButton(darkTheme = true)
}
