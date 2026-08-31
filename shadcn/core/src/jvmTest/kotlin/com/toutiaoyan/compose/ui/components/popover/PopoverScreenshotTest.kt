@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.input.Input
import kotlin.test.Test

class PopoverScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("popover_states", darkTheme = darkTheme) {
            Popover(open = true, onOpenChange = {}) {
                PopoverTrigger("Open popover")
                PopoverContent("Place content for the popover here.")
            }
        }
    }

    private fun dimensionsForm(darkTheme: Boolean) {
        snapshot("popover_dimensions_form", darkTheme = darkTheme) {
            Popover(open = true, onOpenChange = {}) {
                PopoverTrigger("Open popover")
                PopoverContent {
                    PopoverHeader("Dimensions", description = "Set the dimensions for the layer.")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldGroup {
                            Field("Width") {
                                Input(value = "100%", onValueChange = {})
                            }
                            Field("Height") {
                                Input(value = "25px", onValueChange = {})
                            }
                        }
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
    fun dimensions_form_light() = dimensionsForm(darkTheme = false)

    @Test
    fun dimensions_form_dark() = dimensionsForm(darkTheme = true)
}
